package com.base100.lex100.mesaEntrada.loader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.persistence.EntityManager;

import jxl.read.biff.BiffException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.Controller;
import org.jboss.seam.international.StatusMessage.Severity;

import com.base100.lex100.component.misc.Downloader;
import com.base100.lex100.manager.ordenCirculacion.OrdenCirculacionLoader;

@Name("importMesaEntrada")
public class ImportMesaEntrada extends Controller{

	public static final String SORTEO = "sorteo";
	public static final String TURNO_OFICINA = "turnoOficina";
	public static final String TIPO_CAUSA = "tipoCausa";
	public static final String OBJETO_JUICIO = "objetoJuicio";
	public static final String TIPO_PROCESO = "tipoProceso";
	public static final String TIPO_INTERVINIENTE = "tipoInterviniente";
	public static final String COMPETENCIA = "competencia";
	public static final String TIPO_RECURSO = "tipoRecurso";
	public static final String TIPO_SUBEXPEDIENTE = "tipoSubexpediente";
	public static final String ESTADOS_PROCESALES = "estadosProcesales";
	public static final String TEMA = "tema";
	public static final String CRITERIO = "criterio";
	public static final String RUBRO = "rubro";
	public static final String TURNO_MINISTERIOS = "turnoMinisterios";
	public static final String BOLILLERO = "bolillero";
	public static final String CONFIGURACION = "configuracion";
	public static final String MATERIA = "materia";
	public static final String TIPO_LETRADO = "tipoLetrado";
	public static final String FISCALIA = "fiscalia";
	public static final String DEFENSORIA = "defensoria";
	public static final String ASESORIA = "asesoria";
	public static final String DEPENDENCIA = "dependencia";
	public static final String DELITO = "delito";
	public static final String SIGLA = "sigla";
	public static final String CENTRO_INTERNAMIENTO = "centroInternamiento";
	public static final String REGLA_TIPO_REMESA = "reglaTipoRemesa";
	public static final String MOTIVO_PASE = "motivoPase";
	public static final String ORDEN_CIRCULACION = "ordenCirculacion";
	
	@In
	private EntityManager entityManager;
	@In(create=true)
	private Downloader downloader;
	
	private byte[] importData;

	private String forzeCamara;

	public byte[] getImportData() {
		return importData;
	}

	public void setImportData(byte[] importData) {
		this.importData = importData;
	} 
	
	

	@Transactional
	public void importarExcel(String type) {
		
		if (importData != null) {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(importData);
			try {
				AbstractConfigurationLoader loader = null;
				if(SORTEO.equals(type)){
					loader = new SorteoLoader(entityManager, getStatusMessages());
				}else if(TURNO_OFICINA.equals(type)){
					loader = new TurnoOficinaLoader(entityManager, getStatusMessages());
				}else if(TIPO_CAUSA.equals(type)){
					loader = new TipoCausaLoader(entityManager, getStatusMessages());
				}else if(OBJETO_JUICIO.equals(type)){
					loader = new ObjetoJuicioLoader(entityManager, getStatusMessages());
				}else if(TIPO_PROCESO.equals(type)){
					loader = new TipoProcesoLoader(entityManager, getStatusMessages());
				}else if(TIPO_INTERVINIENTE.equals(type)){
					loader = new TipoIntervinienteLoader(entityManager, getStatusMessages());
				}else if(COMPETENCIA.equals(type)){
					loader = new CompetenciaLoader(entityManager, getStatusMessages());
				}else if(TIPO_RECURSO.equals(type)){
					loader = new TipoRecursoLoader(entityManager, getStatusMessages());
				}else if(TIPO_SUBEXPEDIENTE.equals(type)){
					loader = new TipoSubexpedienteLoader(entityManager, getStatusMessages());
				}else if(ESTADOS_PROCESALES.equals(type)){
					loader = new EstadoExpedienteLoader(entityManager, getStatusMessages());
				}else if(TEMA.equals(type)){
					loader = new TemaLoader(entityManager, getStatusMessages());
				}else if(CRITERIO.equals(type)){
					loader = new CriterioLoader(entityManager, getStatusMessages());
				}else if(RUBRO.equals(type)){
					loader = new RubroLoader(entityManager, getStatusMessages());
				}else if(TURNO_MINISTERIOS.equals(type)){
					loader = new TurnoMinisteriosLoader(entityManager, getStatusMessages());
				}else if(BOLILLERO.equals(type)){
					loader = new BolilleroLoader(entityManager, getStatusMessages());
				}else if(CONFIGURACION.equals(type)){
					loader = new ConfiguracionLoader(entityManager, getStatusMessages());
				}else if(MATERIA.equals(type)){
					loader = new MateriaLoader(entityManager, getStatusMessages());
				}else if(TIPO_LETRADO.equals(type)){
					loader = new TipoLetradoLoader(entityManager, getStatusMessages());
				}else if(FISCALIA.equals(type)){
					loader = new FiscaliaLoader(entityManager, getStatusMessages());
				}else if(DEFENSORIA.equals(type)){
					loader = new DefensoriaLoader(entityManager, getStatusMessages());
				}else if(ASESORIA.equals(type)){
					loader = new AsesoriaLoader(entityManager, getStatusMessages());
				}else if(DEPENDENCIA.equals(type)){
					loader = new DependenciaLoader(entityManager, getStatusMessages());
				}else if(DELITO.equals(type)){
					loader = new DelitoLoader(entityManager, getStatusMessages());
				}else if(SIGLA.equals(type)){
					loader = new SiglaLoader(entityManager, getStatusMessages());
				}else if(CENTRO_INTERNAMIENTO.equals(type)){
					loader = new CentroInternamientoLoader(entityManager, getStatusMessages());
				}else if(REGLA_TIPO_REMESA.equals(type)){
					loader = new ReglaTipoRemesaLoader(entityManager, getStatusMessages());
				}else if(MOTIVO_PASE.equals(type)){
					loader = new MotivoPaseLoader(entityManager, getStatusMessages());
				}else if(ORDEN_CIRCULACION.equals(type)){
					loader = new OrdenCirculacionLoader(entityManager, getStatusMessages());
				}else{
					String msg = "Error tipo de cargador desconocido "+type;
					getLog().error(msg);
					getStatusMessages().add(Severity.ERROR, msg);					
				}
				
				if(loader != null){
					loader.setForzeCamara(forzeCamara);
					loader.load(inputStream);
				}
			} catch (BiffException e) {
				String msg = "Error cargando fichero de configuracion";
				getLog().error(msg, e);
				getStatusMessages().add(Severity.ERROR, msg);
			} catch (IOException e) {
				String msg = "Error cargando fichero de configuracion";
				getLog().error(msg, e);
				getStatusMessages().add(Severity.ERROR, msg);
			} catch (LoaderException e) {
				String msg = "Error cargando fichero de configuracion";
				getLog().error(msg, e);
				getStatusMessages().add(Severity.ERROR, msg);
			} finally {
				getStatusMessages().add(Severity.INFO, "Proceso finalizado");
			}
		} else {
			getStatusMessages().add(Severity.ERROR, "No se ha especificado fichero de datos a importar");
		}		
	}

	public String getForzeCamara() {
		return forzeCamara;
	}

	public void setForzeCamara(String forzeCamara) {
		this.forzeCamara = forzeCamara;
	}
}
