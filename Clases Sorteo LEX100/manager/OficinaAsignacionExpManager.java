package com.base100.lex100.manager.expediente;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.util.Strings;

import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.enumeration.TipoAsignacionOficinaEnumeration;
import com.base100.lex100.component.enumeration.TipoCambioAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoOficinaEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.controller.entity.asesoria.AsesoriaSearch;
import com.base100.lex100.controller.entity.defensoria.DefensoriaSearch;
import com.base100.lex100.controller.entity.fiscalia.FiscaliaSearch;
import com.base100.lex100.entity.Asesoria;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.CambioAsignacionExp;
import com.base100.lex100.entity.CodigoPostal;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.Defensoria;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.Fiscalia;
import com.base100.lex100.entity.Mediador;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.Provincia;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.ZonaNotificacion;
import com.base100.lex100.manager.AbstractManager;
import com.base100.lex100.manager.oficina.OficinaManager;
import com.base100.lex100.manager.ordenCirculacion.OrdenCirculacionManager;

@Name("oficinaAsignacionExpManager")
public class OficinaAsignacionExpManager extends AbstractManager {

	public static OficinaAsignacionExpManager instance() {
		return (OficinaAsignacionExpManager) Component.getInstance(OficinaAsignacionExpManager.class, true);
	}
	
	public Oficina getSalaApelacion(Expediente expediente) {
		Oficina result = null;
		result = getOficinaRadicacion(expediente, TipoRadicacionEnumeration.Type.sala);
		if (result == null) {
			for (OficinaAsignacionExp asignacionExp: expediente.getOficinaAsignacionExpList()) {
				if (OficinaManager.instance().isApelaciones(asignacionExp.getOficina())) {
					result = asignacionExp.getOficina();
					break;
				}
			}
		}
		return result;
	}
	
	public Oficina getOficinaRadicacion(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		Oficina result = null;
		OficinaAsignacionExp asignacionExp = getRadicacion(expediente, tipoRadicacion);
		if (asignacionExp != null) {
			result = asignacionExp.getOficina();
		}
		return result;
	}

	public OficinaAsignacionExp getRadicacion(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		OficinaAsignacionExp result = null;
		for (OficinaAsignacionExp asignacionExp: expediente.getOficinaAsignacionExpList()) {
			if (tipoRadicacion.getValue().equals(asignacionExp.getTipoRadicacion())) {
				result = asignacionExp;
				break;
			}
		}
		return result;
	}

	/**
	 * Realiza refresh de radicaciones de un {@link Expediente}
	 * Se agrega find de la radicacion para estar seguros que existe en BD por posible remove realizado por otro thread
	 * @param expediente {@link Expediente}
	 * @throws Exception ante cualquier tipo de excepcion existente
	 */
	public void refreshRadicacionExpediente(Expediente expediente) throws Exception {
		List<OficinaAsignacionExp> oael = expediente.getOficinaAsignacionExpList();
		for (OficinaAsignacionExp asignacionExp : oael) {
			if(getEntityManager().contains(asignacionExp)){
				getLog().info("Realizando Refresh de radicacion con ID_OFICINA_ASIGNACION_EXP: " + asignacionExp.getId() + " y expediente: " + expediente.getClave());
				getEntityManager().refresh(asignacionExp);
			} else {
				getLog().info("No existe radicacion en entityManager, se DESCARTA el refresh de radicacion con ID_OFICINA_ASIGNACION_EXP: " + asignacionExp.getId() + " y expediente: " + expediente.getClave());
			}
		}
	}
	
	public List<OficinaAsignacionExp> getOficinasAsignacionOrdenadas(Expediente expediente) {
		List<OficinaAsignacionExp> oficinaAsignacionList = new ArrayList<OficinaAsignacionExp>();
		if(expediente != null && !getEntityManager().contains(expediente)){
			expediente = getEntityManager().find(Expediente.class, expediente.getId());
		}
		boolean isManaged = expediente!= null && getEntityManager().contains(expediente);
		if (expediente != null && isManaged) {
			if (expediente.getOficinaAsignacionExpList().size() > 0) {
				oficinaAsignacionList = ordenar(expediente.getOficinaAsignacionExpList());
			} else if(expediente.getOficinaInicial() != null){	// Para los expediente antiguos que no tienen OficinaAsignacion
				OficinaAsignacionExp oficinaAsignacionExp = new OficinaAsignacionExp();
				oficinaAsignacionExp.setExpediente(expediente);
				oficinaAsignacionExp.setOficina(expediente.getOficinaInicial());
				oficinaAsignacionList.add(oficinaAsignacionExp);
			}
		}
		return oficinaAsignacionList;
	}

	public List<OficinaAsignacionExp> getRadicaciones(Expediente expediente) {
		List<OficinaAsignacionExp> oficinaAsignacionList = new ArrayList<OficinaAsignacionExp>();
		if(expediente != null && !getEntityManager().contains(expediente)){
			expediente = getEntityManager().find(Expediente.class, expediente.getId());
		}
		boolean isManaged = expediente!= null && getEntityManager().contains(expediente);
		if (expediente != null && isManaged) {
			oficinaAsignacionList.addAll(expediente.getOficinaAsignacionExpList());
		}
		Collections.sort(oficinaAsignacionList, new Comparator<OficinaAsignacionExp>() {
			private final String[] ORDEN_TIPO_RADICACION = {
				TipoRadicacionEnumeration.Type.juzgado.getValue().toString(),
				TipoRadicacionEnumeration.Type.juzgadoMediacion.getValue().toString(),
				TipoRadicacionEnumeration.Type.sala.getValue().toString(),
				TipoRadicacionEnumeration.Type.salaEstudio.getValue().toString(),
				TipoRadicacionEnumeration.Type.vocalias.getValue().toString(),
				TipoRadicacionEnumeration.Type.juzgadoPlenario.getValue().toString(),
				TipoRadicacionEnumeration.Type.juzgadoSentencia.getValue().toString(),
				TipoRadicacionEnumeration.Type.tribunalOral.getValue().toString(),
				TipoRadicacionEnumeration.Type.salaCasacion.getValue().toString(),
				TipoRadicacionEnumeration.Type.vocaliasCasacion.getValue().toString(),
				TipoRadicacionEnumeration.Type.vocaliasEstudioCasacion.getValue().toString(),
				TipoRadicacionEnumeration.Type.corteSuprema.getValue().toString()
			};
			@Override
			public int compare(OficinaAsignacionExp o1, OficinaAsignacionExp o2) {
				if (o1.getTipoRadicacion() != null && o2.getTipoRadicacion() != null) {
					Integer i1 = Arrays.binarySearch(ORDEN_TIPO_RADICACION, o1.getTipoRadicacion());
					Integer i2 = Arrays.binarySearch(ORDEN_TIPO_RADICACION, o2.getTipoRadicacion());
					return i1.compareTo(i2);
				}
				return o1.hashCode()-o1.hashCode();
			}
		});
		return oficinaAsignacionList;
	}

	public List<Oficina> getOficinasRadicacion(Expediente expediente) {
		List<Oficina> oficinas = new ArrayList<Oficina>();
		if (expediente != null) {
			if (expediente.getOficinaAsignacionExpList() != null && expediente.getOficinaAsignacionExpList().size() > 0) {
				List<OficinaAsignacionExp> oficinaAsignacionList = ordenar(expediente.getOficinaAsignacionExpList());
				for (OficinaAsignacionExp oficinaAsignacion: oficinaAsignacionList) {
					oficinas.add(oficinaAsignacion.getOficina());
				}
			} else {	// Para los expediente antiguos que no tienen OficinaAsignacion
				Oficina oficinaInicial = expediente.getOficinaInicial();
				if(oficinaInicial != null){
					oficinas.add(oficinaInicial);
				}
			}
		}
		return oficinas;
	}

	private List<OficinaAsignacionExp> ordenar(List<OficinaAsignacionExp> oficinaAsignacionList) {
		List<OficinaAsignacionExp> oficinaAsignacionOrderedList = new ArrayList<OficinaAsignacionExp>(oficinaAsignacionList);
		Collections.sort(oficinaAsignacionOrderedList, new Comparator<OficinaAsignacionExp>() {
			@Override
			public int compare(OficinaAsignacionExp o1, OficinaAsignacionExp o2) {
				TipoRadicacionEnumeration tipoRadicacionEnumeration = (TipoRadicacionEnumeration) Component.getInstance(TipoRadicacionEnumeration.class, true);
				TipoRadicacionEnumeration.Type tipoRadicacion1 = (TipoRadicacionEnumeration.Type)tipoRadicacionEnumeration.getType(o1.getTipoRadicacion());
				TipoRadicacionEnumeration.Type tipoRadicacion2 = (TipoRadicacionEnumeration.Type)tipoRadicacionEnumeration.getType(o2.getTipoRadicacion());
				if ((tipoRadicacion1 != null) && (tipoRadicacion2 != null)) {
					return (tipoRadicacion1.getOrden() < tipoRadicacion2.getOrden() ? -1 : (tipoRadicacion1.getOrden()==tipoRadicacion2.getOrden() ? 0 : 1));
				} else {
					return 0;
				}
			}
		});
		return oficinaAsignacionOrderedList;
	}

	public String getDescripcion(OficinaAsignacionExp oficinaAsignacionExp){
		Oficina oficina = oficinaAsignacionExp.getOficinaConSecretaria();
		if(oficina != null){
			if(TipoOficinaEnumeration.isMediador(oficina)){
				return oficina.getDescripcionMediadorYMatricula();
			}else{
				return oficina.getDescripcion();
			}
		}else{
			return null;
		}
	}
	
	public String getOrdenCirculacionDetalle(OficinaAsignacionExp oficinaAsignacionExp){
		
		if (oficinaAsignacionExp.getCodigoOrdenCirculacion() != null){
			String ordenCirculacion = OrdenCirculacionManager.instance().findOrdenCirculacionOficina(oficinaAsignacionExp.getOficina().getCamara(),oficinaAsignacionExp.getOficina(),oficinaAsignacionExp.getCodigoOrdenCirculacion());
			if(ordenCirculacion!=null){
				String result =ordenCirculacion.replace(",", "-");
				return " - ("+result+")";
			}
		}
		return "";
	}
	
	public String getMediador(OficinaAsignacionExp oficinaAsignacionExp){
		Mediador mediador = oficinaAsignacionExp.getMediador();		
		if(mediador != null){
			return mediador.getMatriculaYNombre();
		}else{
			return null;
		}
	}

	public String getDescripcionCompletaNumero (OficinaAsignacionExp oficinaAsignacionExp){
		Oficina oficina = oficinaAsignacionExp.getOficinaConSecretaria();
		String message = "";
		if(oficina != null){
			 message = oficina.getDescripcion();
			if (oficinaAsignacionExp.getFiscalia()!=null) {
				message += " - "+getMessages().get("expediente.fiscalian")+" "+oficinaAsignacionExp.getFiscalia();
			}
			if (oficinaAsignacionExp.getDefensoria()!=null) {
				message += " - "+getMessages().get("expediente.defensorian")+" "+oficinaAsignacionExp.getDefensoria();
			}
			if (!Strings.isEmpty(oficinaAsignacionExp.getInstructor())) {
				message += " - "+getMessages().get("expediente.instructorn")+" "+oficinaAsignacionExp.getInstructor();
			}
		}
		return message;
	}
	
	public String getDescripcionOficinaFiscalia (OficinaAsignacionExp oficinaAsignacionExp){
		Oficina oficina = oficinaAsignacionExp.getOficinaConSecretaria();
		String message = "";
		if(oficina != null){
			 message = oficina.getDescripcion();
			if (oficinaAsignacionExp.getFiscalia()!=null) {
				message += " - "+getMessages().get("expediente.fiscalian")+" "+oficinaAsignacionExp.getFiscalia();
			}
		}
		return message;
	}

	private static String getAbreviatura(OficinaAsignacionExp oficinaAsignacionExp){
		return (oficinaAsignacionExp.getOficina() != null
					&& oficinaAsignacionExp.getOficina().getTipoInstancia() != null) ?
							oficinaAsignacionExp.getOficina().getTipoInstancia().getAbreviatura() : null;
	}
	
	public static List<Fiscalia> getFiscalias(OficinaAsignacionExp oficinaAsignacionExp) {
		return FiscaliaSearch.instance().findFiscalias(SessionState.instance().getGlobalCamara(), getAbreviatura(oficinaAsignacionExp), oficinaAsignacionExp.getFechaAsignacion());
	}
	
	private Fiscalia fiscaliaDepdencenciaAux = null;
	
	public void setearFiscaliaDepdencenciaAux(OficinaAsignacionExp oficinaAsignacionExp){
		if(oficinaAsignacionExp == null){
			return;
		}
		
		if(fiscaliaDepdencenciaAux == null
				|| (oficinaAsignacionExp != null && oficinaAsignacionExp.getFiscalia() != null && fiscaliaDepdencenciaAux != null)) {/*&& !oficinaAsignacionExp.getFiscalia().equals(fiscaliaDepdencenciaAux.getCodigo())){*/
			fiscaliaDepdencenciaAux = getFiscaliaAsignada(oficinaAsignacionExp);
		}
	}
	
	public Fiscalia getDependenciaFiscalia(OficinaAsignacionExp oficinaAsignacionExp){
		this.setearFiscaliaDepdencenciaAux(oficinaAsignacionExp);
		return fiscaliaDepdencenciaAux;
	}
	
	public String getDescripcionDependenciaFiscalia(OficinaAsignacionExp oficinaAsignacionExp){
		fiscaliaDepdencenciaAux = null;
		this.setearFiscaliaDepdencenciaAux(oficinaAsignacionExp);
		return (fiscaliaDepdencenciaAux != null && fiscaliaDepdencenciaAux.getDependencia() != null) ? fiscaliaDepdencenciaAux.getDependencia().getDescripcion().trim() : "";
	}
	
	private Fiscalia fiscalFiscaliaAux = null;
	
	public void setearFiscalFiscaliaAux(OficinaAsignacionExp oficinaAsignacionExp){
		if(fiscalFiscaliaAux == null && oficinaAsignacionExp != null){
			if(oficinaAsignacionExp.getFiscalFiscalia() != null){
				fiscalFiscaliaAux = oficinaAsignacionExp.getFiscalFiscalia();
				return;
			}

			if(oficinaAsignacionExp.getDependenciaFiscalia() != null){
				oficinaAsignacionExp.setFiscalFiscalia(oficinaAsignacionExp.getDependenciaFiscalia());
				fiscalFiscaliaAux = oficinaAsignacionExp.getDependenciaFiscalia();
				return;
			}
			
			if(oficinaAsignacionExp.getFiscalia() != null){
				fiscalFiscaliaAux = getFiscalFiscaliaAsignada(oficinaAsignacionExp);
				return;
			}
		}
	}
	
	public String getDescripcionFiscalFiscalia(OficinaAsignacionExp oficinaAsignacionExp){
		fiscalFiscaliaAux = null;
		this.setearFiscalFiscaliaAux(oficinaAsignacionExp);
		return (fiscalFiscaliaAux != null && fiscalFiscaliaAux.getDescripcion() != null) ? fiscalFiscaliaAux.getDescripcion().trim() : "";
	}
	
	public String getCuifDependenciaFiscalia(OficinaAsignacionExp oficinaAsignacionExp){
		this.setearFiscaliaDepdencenciaAux(oficinaAsignacionExp);
		return (fiscaliaDepdencenciaAux != null) ? fiscaliaDepdencenciaAux.getCuif() : "";
	}
	
	public String getDomicilioDependenciaFiscalia(OficinaAsignacionExp oficinaAsignacionExp){
		this.setearFiscaliaDepdencenciaAux(oficinaAsignacionExp);
		return (fiscaliaDepdencenciaAux != null && fiscaliaDepdencenciaAux.getDependencia() != null) ? fiscaliaDepdencenciaAux.getDependencia().getDomicilio() : "";
	}
	public String getLocalidadDependenciaFiscalia(OficinaAsignacionExp oficinaAsignacionExp){
		this.setearFiscaliaDepdencenciaAux(oficinaAsignacionExp);
		return (fiscaliaDepdencenciaAux != null && fiscaliaDepdencenciaAux.getDependencia() != null) ? fiscaliaDepdencenciaAux.getDependencia().getLocalidad() : null;
	}
	public Provincia getProvinciaDependenciaFiscalia(OficinaAsignacionExp oficinaAsignacionExp){
		this.setearFiscaliaDepdencenciaAux(oficinaAsignacionExp);
		return (fiscaliaDepdencenciaAux != null && fiscaliaDepdencenciaAux.getDependencia() != null) ? fiscaliaDepdencenciaAux.getDependencia().getProvincia() : null;
	}
	public CodigoPostal getCodigoPostalDependenciaFiscalia(OficinaAsignacionExp oficinaAsignacionExp){
		this.setearFiscaliaDepdencenciaAux(oficinaAsignacionExp);
		return (fiscaliaDepdencenciaAux != null && fiscaliaDepdencenciaAux.getDependencia() != null) ? fiscaliaDepdencenciaAux.getDependencia().getCodigoPostal() : null;
	}
	public ZonaNotificacion getZonaNotificacionDependenciaFiscalia(OficinaAsignacionExp oficinaAsignacionExp){
		this.setearFiscaliaDepdencenciaAux(oficinaAsignacionExp);
		return (fiscaliaDepdencenciaAux != null && fiscaliaDepdencenciaAux.getDependencia() != null) ? fiscaliaDepdencenciaAux.getDependencia().getZonaNotificacion() : null;
	}
		
	public static Fiscalia getFiscaliaAsignada(OficinaAsignacionExp oficinaAsignacionExp) {
		if(oficinaAsignacionExp != null && oficinaAsignacionExp.getDependenciaFiscalia() == null){
			oficinaAsignacionExp.setDependenciaFiscalia(getFiscaliaAsignada(oficinaAsignacionExp, false));
			return oficinaAsignacionExp.getDependenciaFiscalia();
		}

		return oficinaAsignacionExp.getDependenciaFiscalia();
	}
	
	public static Fiscalia getFiscalFiscaliaAsignada(OficinaAsignacionExp oficinaAsignacionExp){
		if(oficinaAsignacionExp != null && oficinaAsignacionExp.getFiscalFiscalia() == null){
			oficinaAsignacionExp.setFiscalFiscalia(getFiscaliaAsignada(oficinaAsignacionExp, false));
			return oficinaAsignacionExp.getFiscalFiscalia();
		}
		
		return oficinaAsignacionExp.getFiscalFiscalia();
	}
	
	public static Fiscalia getFiscaliaAsignada(OficinaAsignacionExp oficinaAsignacionExp, boolean actual) {
		
		//Si tiene asignado id_fiscalia retorna la asignada
		//Sino la deduce con datos de codigo,instancia,competencia y camara
		if(oficinaAsignacionExp.getDependenciaFiscalia() != null) return oficinaAsignacionExp.getDependenciaFiscalia();

		Fiscalia ret = null;
		Integer codigoFiscalia = null;
		Date fechaAsignacion = null;
		if (oficinaAsignacionExp != null) {
			codigoFiscalia = oficinaAsignacionExp.getFiscalia();
			fechaAsignacion = oficinaAsignacionExp.getFechaAsignacion();
		}
		if (codigoFiscalia != null) {
			String tipoInstanciaAbreviatura = null;
			Competencia competencia = null;
			if (oficinaAsignacionExp.getOficina() != null) {
				TipoInstancia tipoInstancia = oficinaAsignacionExp.getOficina()
						.getTipoInstancia();
				if (tipoInstancia != null) {
					tipoInstanciaAbreviatura = tipoInstancia.getAbreviatura();
				}
				competencia = OficinaManager.instance().getCompetencia(
						oficinaAsignacionExp.getOficina());
			}
			if (actual) {
				fechaAsignacion = null;
			}
			//Fiscalia fiscalia = FiscaliaSearch.instance().findByCodigo(
			//		codigoFiscalia, SessionState.instance().getGlobalCamara(), tipoInstanciaAbreviatura, competencia, fechaAsignacion, oficinaAsignacionExp.getOficina().getZonaOficina());
			Fiscalia fiscalia = FiscaliaSearch.instance().findByCodigo(
					codigoFiscalia, oficinaAsignacionExp.getOficina().getCamara(), tipoInstanciaAbreviatura, competencia, fechaAsignacion, oficinaAsignacionExp.getOficina().getZonaOficina());
			
			if (fiscalia != null) {
				ret = fiscalia;
			}
		}
		return ret;
	}
	
	
	public static Defensoria getDefensoriaAsignada(OficinaAsignacionExp oficinaAsignacionExp) {
		Defensoria ret = null;
		Integer codigoDefensoria = null;
		Date fechaAsignacion = null;
		if (oficinaAsignacionExp != null) {
			codigoDefensoria = oficinaAsignacionExp.getDefensoria();
			fechaAsignacion = oficinaAsignacionExp.getFechaAsignacion();
		}
		if (codigoDefensoria != null) {
			String tipoInstanciaAbreviatura = null;
			Competencia competencia = null;
			if (oficinaAsignacionExp.getOficina() != null) {
				TipoInstancia tipoInstancia = oficinaAsignacionExp.getOficina()
						.getTipoInstancia();
				if (tipoInstancia != null) {
					tipoInstanciaAbreviatura = tipoInstancia.getAbreviatura();
				}
				competencia = OficinaManager.instance().getCompetencia(
						oficinaAsignacionExp.getOficina());
			}
			Defensoria defensoria = DefensoriaSearch.instance().findByCodigo(
					codigoDefensoria, SessionState.instance().getGlobalCamara(), tipoInstanciaAbreviatura, competencia, fechaAsignacion, oficinaAsignacionExp.getOficina().getZonaOficina());
			if (defensoria != null) {
				ret = defensoria;
			}
		}
		return ret;
	}
	
	
	public static Asesoria getAsesoriaAsignada(OficinaAsignacionExp oficinaAsignacionExp) {
		Asesoria ret = null;
		Integer codigoAsesoria = null;
		Date fechaAsignacion = null;
		if (oficinaAsignacionExp != null) {
			codigoAsesoria = oficinaAsignacionExp.getAsesoria();
			fechaAsignacion = oficinaAsignacionExp.getFechaAsignacion();
		}
		if (codigoAsesoria != null) {
			String tipoInstanciaAbreviatura = null;
			Competencia competencia = null;
			if (oficinaAsignacionExp.getOficina() != null) {
				TipoInstancia tipoInstancia = oficinaAsignacionExp.getOficina()
						.getTipoInstancia();
				if (tipoInstancia != null) {
					tipoInstanciaAbreviatura = tipoInstancia.getAbreviatura();
				}
				competencia = OficinaManager.instance().getCompetencia(
						oficinaAsignacionExp.getOficina());
			}
			Asesoria asesoria = AsesoriaSearch.instance().findByCodigo(
					codigoAsesoria, SessionState.instance().getGlobalCamara(), tipoInstanciaAbreviatura, competencia, fechaAsignacion, oficinaAsignacionExp.getOficina().getZonaOficina());
			if (asesoria != null) {
				ret = asesoria;
			}
		}
		return ret;
	}	
	
	
	
	
	public boolean isRecusada(Expediente expediente, Oficina vocaliaSala, String tipoRadicacion) {
		List<CambioAsignacionExp> cambioAsignacionExpList = 
			getEntityManager().createQuery("from CambioAsignacionExp where status = 0 and expediente = :expediente and tipoRadicacion = :tipoRadicacion and oficina = :oficina order by fechaAsignacion desc")
				.setParameter("expediente", expediente)
				.setParameter("oficina", vocaliaSala)
				.setParameter("tipoRadicacion", tipoRadicacion)
				.getResultList();
		for (CambioAsignacionExp cambioAsignacionExp : cambioAsignacionExpList) {
			if (isRecusacionVocalia(cambioAsignacionExp)) {
				return true;
			} else if (isDesestimacionIntegracion(cambioAsignacionExp)) {
				return false;
			}
		}
		return false;
	}
	
	public boolean isRecusacionVocalia(CambioAsignacionExp cambioAsignacionExp) {
		return TipoCambioAsignacionEnumeration.RECUSACION_EXCUSACION_VOCAL.equals(cambioAsignacionExp.getCodigoTipoCambioAsignacion()) || 
 			   TipoCambioAsignacionEnumeration.RECUSACION_EXCUSACION_VOCAL_ESTUDIO.equals(cambioAsignacionExp.getCodigoTipoCambioAsignacion());
	}
	public boolean isDesestimacionIntegracion(CambioAsignacionExp cambioAsignacionExp) {
		return TipoCambioAsignacionEnumeration.DESESTIMACION_INTEGRACION.equals(cambioAsignacionExp.getCodigoTipoCambioAsignacion());
	}
	
	public boolean hasAnyRadicacionEnCamara(Camara camara, Expediente expediente) {
		List<OficinaAsignacionExp> radicacionesActuales = OficinaAsignacionExpManager.instance().getOficinasAsignacionOrdenadas(expediente);
		for (OficinaAsignacionExp radicacionActual: radicacionesActuales) {
			if (radicacionActual.getOficina().getCamara().equals(camara)) {
				return true;
			}
		}
		return false;
	}
	
	public List<OficinaAsignacionExp> getOficinaTipoRadicacionSalaJuzgado(Expediente expediente){
		List<OficinaAsignacionExp> oficinasAsigList = 
			getEntityManager().createQuery("from OficinaAsignacionExp where expediente = :expediente and tipoRadicacion in ('S','J') order by fechaAsignacion desc")
				.setParameter("expediente", expediente)
				.getResultList();
		
		return oficinasAsigList;
	}
	
	public List<OficinaAsignacionExp> filtrarOficinaAsignacionSinFiscalia(List<OficinaAsignacionExp> asignaciones){
		List<OficinaAsignacionExp> rta = new ArrayList<OficinaAsignacionExp>();
		for(OficinaAsignacionExp oficinaAsignacionExp : asignaciones){
			if(oficinaAsignacionExp.getFiscalia() != null){
				rta.add(oficinaAsignacionExp);
			}
		}
		return rta;
	}
	
	/**
	 * @param oficinaAsignacionExp
	 * @param oficinaAsignacionExpBase
	 * @return
	 */
	public static OficinaAsignacionExp copyAsignacionFromOther(OficinaAsignacionExp oficinaAsignacionExp, OficinaAsignacionExp oficinaAsignacionExpBase){
		Integer fiscalia = oficinaAsignacionExpBase.getFiscalia();
		Integer defensoria = oficinaAsignacionExpBase.getDefensoria();
		Integer asesoria = oficinaAsignacionExpBase.getAsesoria();
		Oficina oficina = oficinaAsignacionExpBase.getOficina();
//		Fiscalia fiscaliaFiscal = oficinaAsignacionExpBase.getFiscalFiscalia();
//		Fiscalia dependenciaFiscalia = oficinaAsignacionExpBase.getDependenciaFiscalia();
		
		Oficina oficinaAnterior = oficinaAsignacionExp.getOficina();
		if (!oficina.equals(oficinaAnterior)) oficinaAsignacionExp.setCodigoOrdenCirculacion(null);
		
		oficinaAnterior = oficinaAsignacionExp.getOficinaConSecretaria();
		oficinaAsignacionExp.setOperador(oficinaAsignacionExpBase.getOperador());
		oficinaAsignacionExp.setOficinaAsignadora(oficinaAsignacionExpBase.getOficinaAsignadora());
		oficinaAsignacionExp.setTipoAsignacion(oficinaAsignacionExpBase.getTipoAsignacion());
		oficinaAsignacionExp.setTipoAsignacionLex100(oficinaAsignacionExpBase.getTipoAsignacionLex100());
		oficinaAsignacionExp.setFechaAsignacion(oficinaAsignacionExpBase.getFechaAsignacion());
		if (fiscalia != null) oficinaAsignacionExp.setFiscalia(fiscalia);
		if (defensoria != null) oficinaAsignacionExp.setDefensoria(defensoria);
		if (asesoria != null) oficinaAsignacionExp.setAsesoria(asesoria);
		oficinaAsignacionExp.setOficina(oficina);
		oficinaAsignacionExp.setSecretaria(oficinaAsignacionExpBase.getSecretaria());
		oficinaAsignacionExp.setInstructor(null);
		oficinaAsignacionExp.setRubro(oficinaAsignacionExpBase.getRubro());
		oficinaAsignacionExp.setComentarios(oficinaAsignacionExpBase.getComentarios());
		oficinaAsignacionExp.setCompensado(false);
		oficinaAsignacionExp.setCodigoTipoCambioAsignacion(oficinaAsignacionExpBase.getCodigoTipoCambioAsignacion());
		oficinaAsignacionExp.setOficinaAnterior(oficinaAsignacionExpBase.getOficinaAnterior());
		oficinaAsignacionExp.setTipoAsignacionOficina(TipoAsignacionOficinaEnumeration.Type.reasignacion.getValue().toString());
		
		//TODO Ver si es necesario que se impacte el mismo fiscal y la misma dependencia (en los cuerpos no principales)
		
//		if (fiscaliaFiscal != null) oficinaAsignacionExp.setFiscalFiscalia(fiscaliaFiscal);
//		if (dependenciaFiscalia != null) oficinaAsignacionExp.setDependenciaFiscalia(dependenciaFiscalia);
		
		return oficinaAsignacionExp;
	}
	
	/**
	 * @param usuario * @param oficinaSorteo * @param expediente * @param tipoRadicacion
	 * @param tipoAsignacion * @param tipoAsignacionLex100 * @param tipoAsignacionOficina
	 * @param fechaAsignacion * @param oficina * @param secretaria * @param fiscalia
	 * @param defensoria * @param asesoria * @param rubro * @param observaciones
	 * @param codigoTipoCambioAsignacion * @param oficinaAnterior
	 * @return
	 */
	public static OficinaAsignacionExp createNewAsignacion(String usuario, Oficina oficinaSorteo, Expediente expediente,
			TipoRadicacionEnumeration.Type tipoRadicacion, String tipoAsignacion, String tipoAsignacionLex100,
			TipoAsignacionOficinaEnumeration.Type tipoAsignacionOficina, Date fechaAsignacion, Oficina oficina, Oficina secretaria, Integer fiscalia,
			Integer defensoria, Integer asesoria, String rubro, String observaciones, String codigoTipoCambioAsignacion, Oficina oficinaAnterior){
		OficinaAsignacionExp oficinaAsignacionExp = new OficinaAsignacionExp();
		
		oficinaAsignacionExp.setOperador(usuario);
		oficinaAsignacionExp.setOficinaAsignadora(oficinaSorteo);
		oficinaAsignacionExp.setExpediente(expediente);
		oficinaAsignacionExp.setTipoAsignacionOficina((String)tipoAsignacionOficina.getValue());
		oficinaAsignacionExp.setTipoRadicacion((String)tipoRadicacion.getValue());
		oficinaAsignacionExp.setTipoAsignacion(tipoAsignacion);
		oficinaAsignacionExp.setTipoAsignacionLex100(tipoAsignacionLex100);
		oficinaAsignacionExp.setFechaAsignacion(fechaAsignacion);
		oficinaAsignacionExp.setOficina(oficina);
		oficinaAsignacionExp.setSecretaria(secretaria);
		oficinaAsignacionExp.setFiscalia(fiscalia);
		oficinaAsignacionExp.setDefensoria(defensoria);
		oficinaAsignacionExp.setAsesoria(asesoria);
		oficinaAsignacionExp.setRubro(rubro);
		oficinaAsignacionExp.setComentarios(observaciones);
		oficinaAsignacionExp.setCompensado(false);
		oficinaAsignacionExp.setCodigoTipoCambioAsignacion(codigoTipoCambioAsignacion);
		oficinaAsignacionExp.setOficinaAnterior(oficinaAnterior);
		
		return oficinaAsignacionExp;
	}
}
