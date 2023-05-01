package com.base100.lex100.mesaEntrada.segundoPlano;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.base100.lex100.component.enumeration.StatusProcesoSegundoPlanoEnumeration;
import com.base100.lex100.component.enumeration.TipoProcesoSegundoPlanoEnumeration;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.ProcesoSegundoPlanoExp;
import com.base100.lex100.manager.AbstractManager;

/**
 * Manejador de acciones y metodos de manipulacion para las tareas en segundo plano de los expedientes
 * 
 * @author Sergio Forastieri
 */
@Name("segundoPlanoManager")
@Scope(ScopeType.CONVERSATION)
public class SegundoPlanoManager extends AbstractManager {
	private static final long serialVersionUID = -6419220327598163270L;

	private ProcesoSegundoPlanoExp procesoSegundoPlano;
	
	@Create
	public void init(){
		this.procesoSegundoPlano = null;
	}
	
	/**
	 * Realiza la busqueda de procesos en segundo plano por los distintos posibles parametros
	 * @param em {@link EntityManager}
	 * @param expediente {@link Expediente}
	 * @param statusProceso status del proceso
	 * @param tipo tipo de proceso en segundo plano
	 * @return {@link ProcesoSegundoPlanoExp} o <code>null</code> en caso que no exista
	 */
	@SuppressWarnings("unchecked")
	public ProcesoSegundoPlanoExp findActive(EntityManager em, Expediente expediente, StatusProcesoSegundoPlanoEnumeration.Type statusProceso, TipoProcesoSegundoPlanoEnumeration.Type tipo){
		String SQL = "from ProcesoSegundoPlanoExp p where p.expediente = :expediente AND status = 0 ";
		if(statusProceso!=null) SQL += "AND statusProceso = :statusProceso ";
		if(tipo!=null) SQL += "AND tipo = :tipo ";
		
		SQL += "ORDER BY fechaCreacion asc ";
		
		Query query = em.createQuery(SQL);
		query.setParameter("expediente", expediente);
		if(statusProceso!=null) query.setParameter("statusProceso", (Integer)statusProceso.getValue());
		if(tipo!=null) query.setParameter("tipo", (Integer)tipo.getValue());
		
		List<ProcesoSegundoPlanoExp> pspeList = query.getResultList();
		//se guarda en atributo de la clase para poder utilizarlo luego y no estar constantemente haciendo la query
		procesoSegundoPlano = (pspeList.size()>0) ? pspeList.get(0) : null;
		
		return getProcesoSegundoPlanoExp();
	}
	
	/**
	 * Busca el primer proceso en segundo plano pendiente para un expediente. Utiliza {@link EntityManager} general
	 * @param expediente {@link Expediente} sobre el cual se busca los procesos en segundo plano pendientes
	 * @return {@link ProcesoSegundoPlanoExp} o <code>null</code> en caso que no tenga ninguno
	 */
	public ProcesoSegundoPlanoExp findPendingActive(Expediente expediente){
		return findActive(getEntityManager(), expediente, StatusProcesoSegundoPlanoEnumeration.Type.pendiente, null);
	}
	
	/**
	 * Crea un nuevo {@link ProcesoSegundoPlanoExp} para un determinado {@link Expediente}
	 * @param expediente {@link Expediente} sobre el cual se crea el proceso en segundo plano
	 * @param tipo tipo de proceso en segundo plano
	 * @return nuevo {@link ProcesoSegundoPlanoExp} creado
	 */
	public ProcesoSegundoPlanoExp createProcesoSegundoPlano(Expediente expediente, TipoProcesoSegundoPlanoEnumeration.Type tipo){
		return new ProcesoSegundoPlanoExp(expediente, tipo);
	}
	
	/**
	 * Actualiza status y fecha de finalizacion de un proceso en segundo plano
	 * @param proceso {@link ProcesoSegundoPlanoExp}
	 * @return {@link ProcesoSegundoPlanoExp} con status modificado
	 */
	public ProcesoSegundoPlanoExp finalizarProceso(ProcesoSegundoPlanoExp proceso){
		proceso.setStatusProceso((Integer) StatusProcesoSegundoPlanoEnumeration.Type.finalizado.getValue());
		proceso.setFechaFinalizacion(new Date());
		
		return proceso;
	}
	
	public ProcesoSegundoPlanoExp getProcesoSegundoPlanoExp(){
		return this.procesoSegundoPlano;
	}
}
