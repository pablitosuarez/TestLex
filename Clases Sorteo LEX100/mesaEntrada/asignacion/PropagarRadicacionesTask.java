package com.base100.lex100.mesaEntrada.asignacion;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;

import com.base100.lex100.component.admin.AbstractIsolatedTransactional;
import com.base100.lex100.component.datasource.DynamicEntityManagerFactory;
import com.base100.lex100.component.enumeration.StatusProcesoSegundoPlanoEnumeration;
import com.base100.lex100.component.enumeration.StatusStorteoEnumeration;
import com.base100.lex100.component.enumeration.TipoProcesoSegundoPlanoEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.ProcesoSegundoPlanoExp;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.mesaEntrada.segundoPlano.SegundoPlano;
import com.base100.lex100.mesaEntrada.segundoPlano.SegundoPlanoManager;
import com.base100.lex100.mesaEntrada.sorteo.SorteoManager;

/**
 * Task que realiza la propagacion de las radicaciones de un expediente a toda su Familia
 * Utiliza EntityManager nuevo por medio de la herencia de {@link AbstractIsolatedTransactional} y {@link DynamicEntityManagerFactory}.
 * Se aisla entonces de todo proceso que este ejecutando el lex100 en la parte visual o el usuario.
 * 
 * 1- Agrega nuevo registro en tabla de {@link ProcesoSegundoPlanoExp}
 * 2- Cambia status sorteo de cada expediente de la familia informando que se esta ejecutando tarea en segundo plano
 * 3- Realiza la copia de las radicaciones a cada hijo
 * 4- Cambia status sorteo de cada expediente de la familia informando que se finalizo el proceso en segundo plano
 * 5- Finaliza el registro de {@link ProcesoSegundoPlanoExp} con fecha y status correspondiente al fin de la tarea
 * 
 * @author Sergio Forastieri
 */
public class PropagarRadicacionesTask extends AbstractIsolatedTransactional implements SegundoPlano {
	private static final long serialVersionUID = -5406407191498407562L;
	
	public static final String COMMAND_NAME = "propagarRadicacionesTask";
	private String description = "Este proceso actualiza las radicaciones en los expedientes que acompañan a la causa";
	private Integer totalDeExpedientesFamilia = 0;
	private Integer numeroExpedienteActual = 0;
	
	private Expediente expediente;
	private TipoRadicacionEnumeration.Type tipoRadicacion;
	private OficinaAsignacionExp oficinaAsignacionExp;
	private boolean isReasignacion;
	
	private ExpedienteManager expedienteManager;
	private SegundoPlanoManager segundoPlanoManager;
	private ProcesoSegundoPlanoExp procesoSegundoPlanoExp;
	
	public PropagarRadicacionesTask(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion, OficinaAsignacionExp oae, boolean isReasignacion){
		super(COMMAND_NAME);
		this.expediente = expediente;
		this.tipoRadicacion = tipoRadicacion;
		this.oficinaAsignacionExp = oae;
		this.isReasignacion = isReasignacion;
		this.description = "[" + calculateNumeracionUnica(expediente) + "] - " + description;
	}
	
	/* (non-Javadoc)
	 * @see com.base100.lex100.component.admin.AdminTask#run()
	 */
	@Override
	protected void run() throws Exception {
		try{
			initTransaction(null);
			doPersist(true); //fuerza commit y clear del entityManager isolated para comenzar sin datos
			List<Expediente> expedientesPropagados = new ArrayList<Expediente>();
			
			//se busca la familia del expediente sobre el cual realizar el copiado de las radicaciones
			expediente = solveExpediente(expediente);
			List<Expediente> familiaExpediente = ExpedienteManager.getFamiliaDirectaExpediente(getEntityManager(), expediente);
			totalDeExpedientesFamilia = familiaExpediente.size();
			if(familiaExpediente.size()>0){
				//se crea nuevo registro en tabla de procesos en segundo plano
				procesoSegundoPlanoExp = this.createProcesoSegundoPlano(expediente, TipoProcesoSegundoPlanoEnumeration.Type.copiaRadicaciones);
				//se cambia el status de sorteo a segundo plano para cada expediente
				this.updateAndCommitStatusSorteo(familiaExpediente, StatusStorteoEnumeration.Type.segundoPlano);
				
				for(Expediente item: familiaExpediente){
					if(!getEntityManager().contains(oficinaAsignacionExp))
						oficinaAsignacionExp = getEntityManager().find(OficinaAsignacionExp.class, oficinaAsignacionExp.getId());
					
					//se controla null pointer exception para aquellos casos que no tiene que realizar propagacion
					if(oficinaAsignacionExp!=null){
						//importante: se vuelve a buscar el item por el desatatcheo que se hace en el entityManager con el clean al hacer doPersist
						item = solveExpediente(item);
						getLog().info("Comienzo propagacion radicacion para el expediente con ID " +item.getId()+ " y clave " + item.getClave());
						numeroExpedienteActual++;
						
						if(getExpedienteManager().isAnySubexpedienteNaturaleza(item)){
							//se busca la radicacion del expediente segun un tipo de radicacion. Si existe y es distinta la reemplaza (propaga)
							OficinaAsignacionExp oficinaAsignacionExpAnterior = getExpedienteManager().findRadicacionExpediente(getEntityManager(), item, tipoRadicacion);
							if ((oficinaAsignacionExpAnterior == null) || (isReasignacion && ((oficinaAsignacionExpAnterior.getOficina() == null) || oficinaAsignacionExpAnterior.getOficina().getId() != oficinaAsignacionExp.getOficina().getId()))){
								getLog().info("Propagando radicacion del principal para el expediente con ID " +item.getId()+ " y clave " + item.getClave());
								getExpedienteManager().setRadicacionExpedienteFromOther(getEntityManager(), 
										item,
										oficinaAsignacionExp,
										tipoRadicacion);
								//se agrega a la lista de expedientes procesados para luego hacer el refresh
								expedientesPropagados.add(item);
							}
							transactionCount++;
						}
						getEntityManager().flush();
						doPersist(false);
					}
				}
				//termina de hacer commit de todos los cambios
				doPersist(true);
				//se cambia el status de sorteo a sorteado OK para cada expediente
				this.updateAndCommitStatusSorteo(familiaExpediente, StatusStorteoEnumeration.Type.sorteadoOk);
				//se cambia el status del proceso en segundo plano
				this.finalizarProcesoSegundoPlano(procesoSegundoPlanoExp);
			}
		}catch(Exception e){
			getLog().error("Error al ejecutar propagacion en segundo plano de radicaciones del expediente con ID: " + ((expediente!=null) ? expediente.getId().toString() : "desconocido"));
			e.printStackTrace();
			//lanzo nuevamente la excepcion para que llegue a AdminTask y finalice la tarea con error
			throw new Exception();
		}finally{
			if(getEntityManager().isOpen()){
				getEntityManager().clear();
				getEntityManager().close();
			}
		}
	}
	
	/**
	 * Actualiza el status de un proceso en segundo plano
	 * @param procesoSegundoPlanoExp {@link ProcesoSegundoPlanoExp}
	 * @param statusProceso {@link StatusProcesoSegundoPlanoEnumeration} nuevo
	 */
	public void finalizarProcesoSegundoPlano(ProcesoSegundoPlanoExp procesoSegundoPlanoExp){
		if(procesoSegundoPlanoExp!=null){
			initTransaction(null);
			if(!getEntityManager().contains(procesoSegundoPlanoExp))
				procesoSegundoPlanoExp = getEntityManager().find(ProcesoSegundoPlanoExp.class, procesoSegundoPlanoExp.getId());
			
			procesoSegundoPlanoExp = getSegundoPlanoManager().finalizarProceso(procesoSegundoPlanoExp);
			getEntityManager().flush();
			
			doPersist(true);
		} else {
			getLog().warn("No se pudo actualizar el flag de segundo plano para el proceso en segundo plano del expediente: " + expediente.getClave() + " y ID: " + expediente.getId());
		}
	}
	
	/**
	 * Crea y persiste objeto en base de datos por medio de Commit para agregar un proceso en segundo plano
	 * @param expediente {@link Expediente} sobre el que se agrega el proceso en segundo plano
	 * @param tipo tipo de proceso en segundo plano segun {@link TipoProcesoSegundoPlanoEnumeration}
	 */
	public ProcesoSegundoPlanoExp createProcesoSegundoPlano(Expediente expediente, TipoProcesoSegundoPlanoEnumeration.Type tipo){
		initTransaction(null);
		
		ProcesoSegundoPlanoExp pspe = getSegundoPlanoManager().createProcesoSegundoPlano(expediente, tipo);
		getEntityManager().persist(pspe);
		
		doPersist(true);
		
		return pspe;
	}
	
	/**
	 * Cambia el status de sorteo de un expediente
	 * @param expediente {@link Expediente} a modificar el status de sorteo
	 * @param statusSorteo status de sorteo
	 */
	private void updateAndCommitStatusSorteo(List<Expediente> expedientes, StatusStorteoEnumeration.Type statusSorteo) throws Exception {
		try{
			initTransaction(null);
			for (Expediente expediente : expedientes) {
				if(getExpedienteManager().isAnySubexpedienteNaturaleza(expediente))
					SorteoManager.setStatusSorteoExpediente(getEntityManager(), solveExpediente(expediente), (Integer)statusSorteo.getValue());
			}
			doPersist(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error al hacer UPDATE y COMMIT del status de sorteo en segundo plano para el status: " + statusSorteo.getValue().toString());
		}
	}
	
	/**
	 * @param expediente
	 * @return
	 */
	private Expediente solveExpediente(Expediente expediente){
		if(!(getEntityManager().contains(expediente)))
			return getEntityManager().find(Expediente.class, expediente.getId());
		return expediente;
	}
	
	/**
	 * Obtiene componente {@link ExpedienteManager} del contexto
	 * @return {@link ExpedienteManager}
	 */
	private ExpedienteManager getExpedienteManager(){
		if(expedienteManager == null)
			expedienteManager = (ExpedienteManager) Component.getInstance(ExpedienteManager.class, true);
		return expedienteManager;
	}
	
	private SegundoPlanoManager getSegundoPlanoManager(){
		if(segundoPlanoManager == null)
			segundoPlanoManager = (SegundoPlanoManager)Component.getInstance(SegundoPlanoManager.class, true);
		return segundoPlanoManager;
	}
	
	@Override
	public String getInfo() {
		String info = "Inicializando";
		if(totalDeExpedientesFamilia>0)
			info = "Procesado " + getNumeroExpedienteActual() + " de " + getTotalDeExpedientesFamilia();
		return info;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getTotalDeExpedientesFamilia() {
		return totalDeExpedientesFamilia;
	}

	public Integer getNumeroExpedienteActual() {
		return numeroExpedienteActual;
	}

}
