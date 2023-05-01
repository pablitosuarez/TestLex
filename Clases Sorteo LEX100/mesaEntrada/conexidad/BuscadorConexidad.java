package com.base100.lex100.mesaEntrada.conexidad;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.framework.Controller;
import org.jboss.seam.util.Strings;

import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.currentdate.CurrentDateManager;
import com.base100.lex100.component.enumeration.EstadoExpedienteEnumeration;
import com.base100.lex100.component.enumeration.EstadoIntervinienteEnumeration;
import com.base100.lex100.component.enumeration.TipoFinalizacionEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.estadoexpediente.EstadoExpedientePredefinedEnumeration;
import com.base100.lex100.controller.entity.parametro.ParametroSearch;
import com.base100.lex100.controller.entity.sigla.SiglaSearch;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.EstadoExpediente;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.IntervinienteExp;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Parametro;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.configuracionMateria.ConfiguracionMateriaManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.mesaEntrada.sorteo.Tracer;

public class BuscadorConexidad extends Controller {

	private static String EXPEDIENTE_TEMP_TABLE_1 = "TempExpedientesIds1";
	private static String EXPEDIENTE_TEMP_TABLE_2 = "TempExpedientesIds2";
	private static String EXPEDIENTE_INTERVINIENTE_IDS = "TempExpedienteIntervinienteIds";
	private static String EXPEDIENTE_INTERV_TEMP_CNX_TABLE = "TempExpedientesIntervCnx";

	private static final String DESDE_FECHA_PARAMETER = "desdeFecha";
	private static final String HASTA_FECHA_PARAMETER = "hastaFecha";
	
	private static final int MAX_ITEMS_IN_CLAUSE = 999;
	
	public EntityManager getEntityManager() {
		return (EntityManager) getComponentInstance("entityManager");
	}

	// NO SE USA
	public List<IntervinienteExp> buscaConexidadPor1Interviniente(String nombre, ActorDemandado actorDemandado, Date desdeFecha, List<String> codigoObjetoJuicioIncluidosList) {
		
		ListaIntervinientes listaIntervinientes = new ListaIntervinientes(actorDemandado);
		listaIntervinientes.addNombre(nombre);
				
		String intervinienteEntityAlias = "intervinienteExp";	
		String stmt = "select "+intervinienteEntityAlias+" from IntervinienteExp "+intervinienteEntityAlias;
		
		List<String> conditions = new ArrayList<String>(); 
		
		addIntervinienteConditions(null, SessionState.instance().getGlobalCamara(), null, listaIntervinientes, desdeFecha, null, null, null, intervinienteEntityAlias, conditions, false, true, true);
		
		addObjetoJuicioInCondition(codigoObjetoJuicioIncluidosList,	intervinienteEntityAlias+".expediente", conditions);
		stmt += " where "+createWhereClause(conditions);
		
		Query query = getEntityManager().createQuery(stmt);

		setParametersIntervinientes(null, query, listaIntervinientes);
		setParameterDespuesDeFecha(null, query, desdeFecha);
		setParameterObjetoJuicioIn(null, query, codigoObjetoJuicioIncluidosList);
		return query.getResultList();
	}

	private void setParametersIntervinientes(Tracer tracer, Query query, ListaIntervinientes listaIntervinientes) {
		if(listaIntervinientes!=null){
			if(listaIntervinientes.getNombres() != null){
				setParameter(tracer, query, listaIntervinientes.getParametroNombre(), listaIntervinientes.getNombres());
			}
			if(listaIntervinientes.getCuitCuils() != null){
				setParameter(tracer, query, listaIntervinientes.getParametroCuit(), listaIntervinientes.getCuitCuils());
			}
			if(listaIntervinientes.getDnis() != null){
				setParameter(tracer, query, listaIntervinientes.getParametroDni(), listaIntervinientes.getDnis());
			}
		}
	}

	public List<IntervinienteExp> buscaConexidadPorActorYDemandado(ListaIntervinientes	actores,
			ListaIntervinientes demandados,
			Map<String, String> parametroMap,
			Date desdeFecha,
			List<String> codigoObjetoJuicioIncluidosList) {
		
		String intervinienteEntityAlias = "intervinienteExp";
		String expedienteColumnExpression = intervinienteEntityAlias+".expediente";
		
		String stmt = "select "+intervinienteEntityAlias+" from IntervinienteExp "+intervinienteEntityAlias;
		List<String> conditions = new ArrayList<String>(); 
			 
		if(actores.hasAnyInfo() && demandados.hasAnyInfo()){
			addIntervinienteConditions(null, SessionState.instance().getGlobalCamara(), null, actores, desdeFecha, null, null, null, intervinienteEntityAlias, conditions, false, true, true);
			conditions.add(getConditionExpedienteHasInterviniente(null, SessionState.instance().getGlobalCamara(), null, demandados, desdeFecha, null, null, null, expedienteColumnExpression, false));
		}else{
			if(actores.hasAnyInfo()){
				addIntervinienteConditions(null, SessionState.instance().getGlobalCamara(), null, actores, desdeFecha, null, null, null, intervinienteEntityAlias, conditions, false, true, true);			
			}else if(demandados.hasAnyInfo()){
				addIntervinienteConditions(null, SessionState.instance().getGlobalCamara(), null, demandados, desdeFecha, null, null, null, intervinienteEntityAlias, conditions, false, true, true);							
			}
		}

		addParametrosConditions(parametroMap, expedienteColumnExpression, conditions);
		
		addObjetoJuicioInCondition(codigoObjetoJuicioIncluidosList, expedienteColumnExpression, conditions);
		stmt += " where "+createWhereClause(conditions);
		
		Query query = getEntityManager().createQuery(stmt);
		
		setParametersIntervinientes(null, query, actores);
		setParametersIntervinientes(null, query, demandados);		
		setParameterDespuesDeFecha(null, query, desdeFecha);		
		setParameterObjetoJuicioIn(null, query, codigoObjetoJuicioIncluidosList);
		setParameterParametros(null, query, parametroMap);
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Expediente> buscaConexidadGenerica(Tracer tracer, Expediente expediente, 
			ListaIntervinientes	actores,
			ListaIntervinientes demandados,
			Map<String, String> parametroMap,
			List<String> naturalezas,
			Date desdeFecha,
			Integer desdeAnioExpediente,
			Boolean soloIniciados,
			Boolean soloEnTramite,
			Boolean ambitoVoluntarios,
			boolean ambitoGlobal,
			List<String> codigoObjetoJuicioIncluidosList,
			List<String> codigoObjetoJuicioExcluidosList,
			List<String> codigoTipoCausaIncluidasList,
			List<Integer> idTemasIncluidosList,
			String dependenciaOrigen,
			String codigoRelacionado,
			String conTipoRadicacion,
			boolean origenEnCamara,
			boolean radicacionEnCamara) {
		
		boolean conexidadOptimizada = ConfiguracionMateriaManager.instance().isConexidadOptimizada(SessionState.instance().getGlobalCamara());
		boolean conexidadBuscaAnteriores = ConfiguracionMateriaManager.instance().isConexidadBuscaAnteriores(SessionState.instance().getGlobalCamara(), expediente.getMateria());

		Date hastaFecha = null;
		if (conexidadBuscaAnteriores) {
			hastaFecha = expediente.getFechaSorteoCarga();
		}
		
		boolean doSearch = false;
		String stmt = "from Expediente expediente"; 
		String expedienteColumnExpression = "expediente";
		boolean isPenal = ExpedienteManager.isPenal(expediente);
		Camara camara = null;
		Camara camaraRadicacion = null;
		Camara camaraOriginal = null;
		if (!ambitoGlobal) {
			camara = SessionState.instance().getGlobalCamara();
		}
		if (radicacionEnCamara){
			camaraRadicacion = SessionState.instance().getGlobalCamara();
		}
		if(origenEnCamara) {
			camaraOriginal = SessionState.instance().getGlobalCamara();
		}
		List<String> conditions = new ArrayList<String>(); 
		
		List<Integer> idExpedienteIncluidosList = null;
		String expedientesIncluidosTempTable = null;

		if (camara != null) {
			conditions.add("expediente.camaraActual.id = " +  camara.getId());
		}
		if (camaraOriginal != null) {
			conditions.add("expediente.camara.id = " +  camaraOriginal.getId());
		}

		if (naturalezas == null) {
			conditions.add("expediente.naturaleza <> 'L'");
		}
		if(CamaraManager.isCamaraActualMultiMateria()) {
			if(expediente.getMateria() != null) {
				conditions.add("expediente.materia.id = " + expediente.getMateria().getId());
			}
		}
		conditions.add("expediente.status <> -1");
		/* OJO de momento se quita. esto hace que el ORACLE se eternice (optimiza por esta cond. y lanza un producto cartesiano!)
		conditions.add("expediente.numero >= 0");
		conditions.add("expediente.anio >= 0");
		*/
		
		conditions.add("expediente.verificacionMesa in ('M','N')");

		List<Integer> validIdEstadoList = new ArrayList<Integer>();
		List<Integer> invalidIdEstadoList = new ArrayList<Integer>();

		if(soloIniciados != null) {
			EstadoExpediente estadoSinIniciar = getEstadoSinIniciar(getEntityManager(), expediente.getMateria());
			if (estadoSinIniciar != null) {
				if(Boolean.TRUE.equals(soloIniciados)) {			// SOlo iniciados
					invalidIdEstadoList.add(estadoSinIniciar.getId());
				} else if(Boolean.FALSE.equals(soloIniciados)) {	// Solo no iniciados
					validIdEstadoList.add(estadoSinIniciar.getId());
				}
			}
		}

		if(Boolean.TRUE.equals(soloEnTramite)) {
			invalidIdEstadoList.addAll(getIdEstadosFinalizacion(getEntityManager(), expediente.getMateria()));
		}
		if(Boolean.FALSE.equals(soloEnTramite)) {
			validIdEstadoList.addAll(getIdEstadosFinalizacion(getEntityManager(), expediente.getMateria()));
		}

		if(ambitoVoluntarios != null) {
			conditions.add("expediente.voluntario = " + (Boolean.TRUE.equals(ambitoVoluntarios) ? "1": "0"));
		}

		if((conTipoRadicacion == null) && !TipoRadicacionEnumeration.isAnySala(conTipoRadicacion) ){
			if(desdeAnioExpediente != null) {
				conditions.add("expediente.anio >= " + desdeAnioExpediente.toString());
			}
	
			if(desdeFecha != null) {
				conditions.add("expediente.fechaSorteoCarga >= :" + DESDE_FECHA_PARAMETER);
			}
		}
		if(hastaFecha != null) {
			conditions.add("expediente.fechaSorteoCarga <= :" + HASTA_FECHA_PARAMETER);
		}

		if(hastaFecha != null) {
			conditions.add("expediente.fechaSorteoCarga <= :" + HASTA_FECHA_PARAMETER);
		}

		Integer actualExpedienteId = null;
		if (expediente != null) {
			Expediente expedienteBase = ExpedienteManager.getExpedienteBase(getEntityManager(), expediente);
			if (expedienteBase != null) {
				actualExpedienteId = expedienteBase.getId();
				conditions.add(" expediente.id <> " + actualExpedienteId);
			}
		}
		
		if(conTipoRadicacion != null) {
			if(TipoRadicacionEnumeration.isAnySala(conTipoRadicacion) ){
				if((desdeAnioExpediente != null) || (desdeFecha != null)) {
					desdeFecha = calculaDesdeFecha(desdeAnioExpediente, desdeFecha);
					desdeAnioExpediente = null;
					conditions.add("exists (select 1 from OficinaAsignacionExp oa where expediente = oa.expediente and oa.tipoRadicacion = '"+conTipoRadicacion+"' and fechaAsignacion >=  :" + DESDE_FECHA_PARAMETER +")");
				}
			} else {
				conditions.add("exists (select 1 from OficinaAsignacionExp oa where expediente = oa.expediente and oa.tipoRadicacion = '"+conTipoRadicacion+"')");
			}
		}

		if(camaraRadicacion != null) {
			conditions.add("exists (select 1 from OficinaAsignacionExp oa where expediente = oa.expediente and oa.oficina.camara.id = " +  camaraRadicacion.getId()+")");
		}

		boolean cancelar = false;
		if (conexidadOptimizada) {
			if(actores.hasAnyInfo() || demandados.hasAnyInfo()){
				//Busqueda de intervinientes por identidad de nombre en la camara de la sesion
				expedientesIncluidosTempTable = getExpedientesIncluidosTempTable(tracer, actualExpedienteId, camara, camaraOriginal, actores, demandados, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, expedienteColumnExpression, isPenal);
				if (expedientesIncluidosTempTable != null ) {
					doSearch = true;
					conditions.add(expedienteColumnExpression + ".id in (select temp.id from " + expedientesIncluidosTempTable + " temp)");
				} else {
					cancelar = true;
				}
			}
		} else {
			if(CamaraManager.isCamaraCivilNacional() && actores.hasAnyInfo() && demandados.hasAnyInfo()){
				doSearch = true;
				conditions.add(getConditionExpedienteHasIntervinientes(actualExpedienteId, camara, camaraOriginal, actores, demandados, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, expedienteColumnExpression, isPenal));
			} else {
				if(actores.hasAnyInfo()){
					doSearch = true;
					conditions.add(getConditionExpedienteHasInterviniente(actualExpedienteId, camara, camaraOriginal, actores, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, expedienteColumnExpression, isPenal));
				}
		
				if(demandados.hasAnyInfo()){
					doSearch = true;
					conditions.add(getConditionExpedienteHasInterviniente(actualExpedienteId, camara, camaraOriginal, demandados, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, expedienteColumnExpression, isPenal));
				}
			}
		}
		if (!cancelar) {
			if(parametroMap != null && parametroMap.size() > 0){
				doSearch = true;
				addParametrosConditions(parametroMap, "expediente", conditions);
			}
			
			if (!Strings.isEmpty(dependenciaOrigen)) {
				doSearch = true;
				conditions.add("expediente.expedienteEspecial.dependenciaOrigen = '" + dependenciaOrigen + "'");
			}
			if (!Strings.isEmpty(codigoRelacionado)) {
				doSearch = true;
				conditions.add("expediente.expedienteEspecial.codigoRelacionado = '" + codigoRelacionado + "'");
			}
		}
		List<Expediente> list;
		if(doSearch && !cancelar){
			addIdExpedientesInCondition(idExpedienteIncluidosList, expedienteColumnExpression, conditions);

			addEstadosInCondition(validIdEstadoList, expedienteColumnExpression, conditions);
			addEstadosNotInCondition(invalidIdEstadoList, expedienteColumnExpression, conditions);
			addNaturalezaInCondition(naturalezas, expedienteColumnExpression, conditions);
			addTipoCausaInCondition(codigoTipoCausaIncluidasList, expedienteColumnExpression, conditions);
			addTemasInCondition(idTemasIncluidosList, expedienteColumnExpression, conditions);
			addObjetoJuicioInCondition(codigoObjetoJuicioIncluidosList, expedienteColumnExpression, conditions);
			addObjetoJuicioNotInCondition(codigoObjetoJuicioExcluidosList, expedienteColumnExpression, conditions);
			stmt += " where "+createWhereClause(conditions);
			
			Query query = getEntityManager().createQuery(stmt);

			if(tracer != null) {
				tracer.notify("Sentencia HQL", stmt);
			}
			
			setParameterEstadosIn(tracer, query, validIdEstadoList);
			setParameterEstadosNotIn(tracer, query, invalidIdEstadoList);
			if (!conexidadOptimizada) {
				setParametersIntervinientes(tracer, query, actores);
				setParametersIntervinientes(tracer, query, demandados);		
			}
			setParameterIdExpedienteIn(tracer, query, idExpedienteIncluidosList);

			setParameterDespuesDeFecha(tracer, query, desdeFecha);
			setParameterAntesDeFecha(tracer, query, hastaFecha);
			setParameterNaturalezaIn(tracer, query, naturalezas);
			setParameterTipoCausaIn(tracer, query, codigoTipoCausaIncluidasList);
			setParameterTemaIn(tracer, query, idTemasIncluidosList);
			setParameterObjetoJuicioIn(tracer, query, codigoObjetoJuicioIncluidosList);
			setParameterObjetoJuicioNotIn(tracer, query, codigoObjetoJuicioExcluidosList);
			setParameterParametros(tracer, query, parametroMap);
			query.setMaxResults(50);
		
//			Logger sqlLogger = Logger.getLogger("org.hibernate.SQL");
//			Level actualLevel = sqlLogger.getLevel();
//			sqlLogger.setLevel(Level.TRACE);

			list = query.getResultList();
			
//			sqlLogger.setLevel(actualLevel);

		}else{
			list = new ArrayList<Expediente>();
		}
		return list;
	}

	private Date calculaDesdeFecha(Integer desdeAnio, Date desdeFecha) {
		Date fecha = null;
		if (desdeAnio != null) {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(CurrentDateManager.instance().getCurrentDateWithoutTime());
			gc.set(GregorianCalendar.YEAR, desdeAnio);
			fecha = gc.getTime();
		}
		if (desdeFecha != null) {
			if(fecha != null) {
				desdeFecha = desdeFecha.after(fecha) ? desdeFecha: fecha;	// toma la mas reciente
			}
		} else {
			desdeFecha = fecha;
		}
		return desdeFecha;
	}

	private void addParametrosConditions(Map<String, String> parametroMap, String expedienteColumnExpression, List<String> conditions) {
		int idx = 1;
		for(String parametroName: parametroMap.keySet()) {
			StringBuffer sb = new StringBuffer();
			sb.append("select parametroExpediente.expedienteIngreso.expediente from ParametroExpediente parametroExpediente where ");			
			sb.append("(parametro.name = '");
			sb.append(parametroName);
			sb.append("' and valor in (:"+parametroName);
//			sb.append(idx++);
			sb.append("))");		
			
			conditions.add(expedienteColumnExpression+" in ("+sb.toString()+")");
		}
	}
	
	private void setParameterParametros(Tracer tracer, Query query, Map<String, String> parametroMap) {
		if(parametroMap != null && parametroMap.size() > 0){
			for(String parametroName: parametroMap.keySet()) {
				boolean multiple = false;
				Parametro parametro = ParametroSearch.findByName(getEntityManager(), SessionState.instance().getGlobalCamara(), parametroName);
				if ((parametro != null) && (parametro.getMultiple() != null)){
					multiple = parametro.getMultiple();
				}
				List<String> valoresList;
				if(multiple) {
					valoresList = Configuration.getCommaSeparatedAsNotNullList(parametroMap.get(parametroName));
				} else{
					valoresList = new ArrayList<String>();
					valoresList.add(parametroMap.get(parametroName));
				}
				setParameter(tracer, query, parametroName, valoresList);				
			}
		}
	}

	private void setParameterIdExpedienteIn(Tracer tracer, Query query, List<Integer> idExpedienteIncluidosList) {
		if(idExpedienteIncluidosList != null){
			if(idExpedienteIncluidosList.size() > 1){
				setParameter(tracer, query, "idExpedienteIncluidosList", idExpedienteIncluidosList);
			}
		}
	}

	private void setParameterNaturalezaIn(Tracer tracer, Query query,
			List<String> naturalezaList) {
		if(naturalezaList != null){
			if(naturalezaList.size() > 1){
				setParameter(tracer, query, "naturalezaList", naturalezaList);
			}
		}
	}

	private void setParameterTipoCausaIn(Tracer tracer, Query query,
			List<String> codigoTipoCausaIncluidasList) {
		if(codigoTipoCausaIncluidasList != null){
			if(codigoTipoCausaIncluidasList.size() > 1){
				setParameter(tracer, query, "tiposCausaIncluidas", codigoTipoCausaIncluidasList);
			}
		}
	}

	private void setParameterTemaIn(Tracer tracer, Query query,
			List<Integer> idTemasIncluidosList) {
		if(idTemasIncluidosList != null){
			if(idTemasIncluidosList.size() > 1){
				setParameter(tracer, query, "temasIncluidosList", idTemasIncluidosList);
			}
		}
	}

	private void setParameterObjetoJuicioIn(Tracer tracer, Query query,
			List<String> codigoObjetoJuicioIncluidosList) {
		if(codigoObjetoJuicioIncluidosList != null){
			if(codigoObjetoJuicioIncluidosList.size() > 1){
				setParameter(tracer, query, "objetosIncluidos", codigoObjetoJuicioIncluidosList);
			}
		}
	}

	private void setParameterObjetoJuicioNotIn(Tracer tracer, Query query,
			List<String> codigoObjetoJuicioExcluidosList) {
		if(codigoObjetoJuicioExcluidosList != null){
			if(codigoObjetoJuicioExcluidosList.size() > 1){
				setParameter(tracer, query, "objetosExcluidos", codigoObjetoJuicioExcluidosList);
			}
		}
	}

	private void setParameterEstadosIn(Tracer tracer, Query query,
			List<Integer> idEstadosValidosList) {
		if(idEstadosValidosList != null){
			if(idEstadosValidosList.size() > 1){
				setParameter(tracer, query, "idEstadosValidosList", idEstadosValidosList);
			}
		}
	}
	
	private void setParameterEstadosNotIn(Tracer tracer, Query query,
			List<Integer> idEstadosNovalidosList) {
		if(idEstadosNovalidosList != null){
			if(idEstadosNovalidosList.size() > 1){
				setParameter(tracer, query, "idEstadosNovalidosList", idEstadosNovalidosList);
			}
		}
	}
	
	private void setParameterDespuesDeFecha(Tracer tracer, Query query,
			Date desdeFecha) {
		if(desdeFecha != null){
			setParameter(tracer, query, DESDE_FECHA_PARAMETER, desdeFecha);
		}
	}

	private void setParameterAntesDeFecha(Tracer tracer, Query query,
			Date hastaFecha) {
		if(hastaFecha != null){
			setParameter(tracer, query, HASTA_FECHA_PARAMETER, hastaFecha);
		}
	}

	private void addNaturalezaInCondition(List<String> naturalezaList,
			String expedienteColumnExpression, List<String> conditions) {
		
		if ((naturalezaList != null) && (naturalezaList.size() > 0)) {
			if(naturalezaList.size() > 1){
				conditions.add(expedienteColumnExpression+".naturaleza in (:naturalezaList)");
			}else{
				conditions.add(expedienteColumnExpression+".naturaleza = '"+naturalezaList.get(0)+"'");
			}
		}
	}
	
	private void addTipoCausaInCondition(List<String> codigoTipoCausaList,
			String expedienteColumnExpression, List<String> conditions) {
		
		if ((codigoTipoCausaList != null) && (codigoTipoCausaList.size() > 0)) {
			if(codigoTipoCausaList.size() > 1){
				conditions.add(expedienteColumnExpression+".tipoCausa.codigo in (:tiposCausaIncluidas)");
			}else{
				conditions.add(expedienteColumnExpression+".tipoCausa.codigo = '"+codigoTipoCausaList.get(0)+"'");
			}
		}
	}
	
	private void addTemasInCondition(List<Integer> idTemaList,
			String expedienteColumnExpression, List<String> conditions) {
		
		if ((idTemaList != null) && (idTemaList.size() > 0)) {
			if(idTemaList.size() > 1){
				conditions.add(expedienteColumnExpression+".objetoJuicio.tema.id in (:temasIncluidosList)");
			}else{
				conditions.add(expedienteColumnExpression+".objetoJuicio.tema.id = '"+idTemaList.get(0)+"'");
			}
		}
	}
	
	private void addObjetoJuicioInCondition(
			List<String> codigoObjetoJuicioList,
			String expedienteColumnExpression, List<String> conditions) {
		
		if ((codigoObjetoJuicioList != null) && (codigoObjetoJuicioList.size() > 0)) {
			if(codigoObjetoJuicioList.size() > 1){
				conditions.add(expedienteColumnExpression+".objetoJuicio.codigo in (:objetosIncluidos)");
			}else{
				conditions.add(expedienteColumnExpression+".objetoJuicio.codigo = '"+codigoObjetoJuicioList.get(0)+"'");
			}
		}
	}
	
	private void addObjetoJuicioNotInCondition(
			List<String> codigoObjetoJuicioList,
			String expedienteColumnExpression, List<String> conditions) {
		
		if ((codigoObjetoJuicioList != null) && (codigoObjetoJuicioList.size() > 0)) {
			if(codigoObjetoJuicioList.size() > 1){
				conditions.add(expedienteColumnExpression+".objetoJuicio.codigo not in (:objetosExcluidos)");
			}else{
				conditions.add(expedienteColumnExpression+".objetoJuicio.codigo <> '"+codigoObjetoJuicioList.get(0)+"'");
			}
		}
	}

	private void addIdExpedientesInCondition(List<Integer> idExpedienteIncluidosList,
			String expedienteColumnExpression, List<String> conditions) {
		if ((idExpedienteIncluidosList != null) && (idExpedienteIncluidosList.size() > 0)) {
			if(idExpedienteIncluidosList.size() > 1){
				conditions.add(expedienteColumnExpression+".id in (:idExpedienteIncluidosList)");
			} else{
				conditions.add(expedienteColumnExpression+".id = "+idExpedienteIncluidosList.get(0));
			}
		}
	}

	private void addEstadosInCondition(
			List<Integer> idEstadosValidosList,
			String expedienteColumnExpression, List<String> conditions) {
		
		if ((idEstadosValidosList != null) && (idEstadosValidosList.size() > 0)) {
			if(idEstadosValidosList.size() > 1){
				conditions.add(expedienteColumnExpression+".estadoExpediente.id in (:idEstadosValidosList)");
			}else{
				conditions.add(expedienteColumnExpression+".estadoExpediente.id = "+idEstadosValidosList.get(0));
			}
		}
	}
	
	private void addEstadosNotInCondition(
			List<Integer> idEstadosNovalidosList,
			String expedienteColumnExpression, List<String> conditions) {
		
		if ((idEstadosNovalidosList != null) && (idEstadosNovalidosList.size() > 0)) {
			if(idEstadosNovalidosList.size() > 1){
				conditions.add(expedienteColumnExpression+".estadoExpediente.id not in (:idEstadosNovalidosList)");
			}else{
				conditions.add(expedienteColumnExpression+".estadoExpediente.id <> "+idEstadosNovalidosList.get(0));
			}
		}
	}
	
	public List<IntervinienteExp> buscaConexidadPorActorYDemandado(			
			String nombreActor, 
			String nombreDemandado, 
			Map<String, String> parametroMap,
			Date desdeFecha,
			List<String> codigoObjetoJuicioList) {
		
		ListaIntervinientes actores = new ListaIntervinientes(ActorDemandado.ACTOR);
		ListaIntervinientes demandados = new ListaIntervinientes(ActorDemandado.DEMANDADO);
		
		if(nombreActor != null){
			actores.addNombre(nombreActor);
		}
		if(nombreDemandado != null){
			demandados.addNombre(nombreDemandado);
		}
		
		return buscaConexidadPorActorYDemandado(actores, demandados, parametroMap, desdeFecha, codigoObjetoJuicioList);
	}

	public static List<String> parseObjetoJuicioExpression(String objetosJuicioExpression, String currentObjetoJuicio) {
		List<String> list = null;
		if(objetosJuicioExpression != null){
			list = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(objetosJuicioExpression, ",");
			while(st.hasMoreTokens()){
				String token = st.nextToken();
				token = token.trim();
				if(token.equals("$=")){
					list.add(currentObjetoJuicio);
				}else if((token.startsWith("'") && token.endsWith("'")) || (token.startsWith("\"") && token.endsWith("\""))){
					list.add(token.substring(1, token.length()-1));
				}else{
					list.add(token);
				}
			}
		}
		return list;
	}

	private String createIntervinientesCombQueryStmt(Integer actualExpedienteId, Camara camara, Camara camaraOriginal, ListaIntervinientes actores, ListaIntervinientes demandados, Date desdeFecha, Integer desdeAnioExpediente, List<String> naturalezas, String conTipoRadicacion, String intervinienteEntityAlias, boolean isPenal) {		
		List<String> conditions = new ArrayList<String>(); 
		String stmt;
		
		if(hayNoFrecuentes(actores)) {
			addIntervinienteConditions(actualExpedienteId, camara, camaraOriginal, demandados,	desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, intervinienteEntityAlias, conditions, isPenal, true, true);
			conditions.add(intervinienteEntityAlias+".expediente in (select iexp2.expediente "+createIntervinienteQueryStmt(actualExpedienteId, camara, camaraOriginal, actores, desdeFecha, desdeAnioExpediente, naturalezas, null, "iexp2", isPenal)+")");
			stmt = "from IntervinienteExp "+intervinienteEntityAlias+" where "+createWhereClause(conditions);
		} else {
			addIntervinienteConditions(actualExpedienteId, camara, camaraOriginal, actores,	desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, intervinienteEntityAlias, conditions, isPenal, true, true);
			conditions.add(intervinienteEntityAlias+".expediente in (select iexp2.expediente "+createIntervinienteQueryStmt(actualExpedienteId, camara, camaraOriginal, demandados, desdeFecha, desdeAnioExpediente, naturalezas, null, "iexp2", isPenal)+")");
			stmt = "from IntervinienteExp "+intervinienteEntityAlias+" where "+createWhereClause(conditions);
		}
		return stmt;
	}

	private String createIntervinienteQueryStmt(Integer actualExpedienteId, Camara camara, Camara camaraOriginal, ListaIntervinientes intervinientes, 
			Date desdeFecha, Integer desdeAnioExpediente, List<String> naturalezas, String conTipoRadicacion, String intervinienteEntityAlias, boolean isPenal) {		
		List<String> conditions = new ArrayList<String>(); 
			
		addIntervinienteConditions(actualExpedienteId, camara, camaraOriginal, intervinientes,	desdeFecha,	desdeAnioExpediente, naturalezas, conTipoRadicacion, intervinienteEntityAlias, conditions, isPenal, true, true);
		String stmt = "from IntervinienteExp "+intervinienteEntityAlias+" where "+createWhereClause(conditions);
		return stmt;
	}
	
	/**
	 * Se obtienen seccion de sentencia SQL con filtros de expediente para la busqueda de conexidad
	 * @param actualExpedienteId * @param camara * @param camaraOriginal * @param desdeFecha * @param desdeAnioExpediente
	 * @param naturalezas * @param conTipoRadicacion * @param intervinienteEntityAlias * @param isPenal
	 * @return {@link String} con la seccion from y where de la consulta SQL
	 */
	private String createExpedienteQueryStmt(Integer actualExpedienteId, Camara camara, Camara camaraOriginal, Date desdeFecha,
			Integer desdeAnioExpediente, List<String> naturalezas, String conTipoRadicacion, String intervinienteEntityAlias, boolean isPenal) {		
		List<String> conditions = new ArrayList<String>(); 
			
		addExpedienteConditionWithoutInterviniente(actualExpedienteId, camara, camaraOriginal, desdeFecha,	desdeAnioExpediente, naturalezas, conTipoRadicacion,
								intervinienteEntityAlias, conditions, isPenal, true);
		String stmt = "from Expediente "+intervinienteEntityAlias+" where "+createWhereClause(conditions);
		return stmt;
	}

	private boolean addIntervinienteConditions(Integer actualExpedienteId, Camara camara, Camara camaraOriginal, ListaIntervinientes intervinientes, 
			Date desdeFecha, Integer desdeAnioExpediente, List<String> naturalezas, String conTipoRadicacion, 
			String intervinienteEntityAlias, List<String> conditions, boolean isPenal, boolean filtroExpediente, boolean agregarFiltrosNombreCuitDni) {
		boolean ok = false;
		//agrega condiciones del interviniente
		String cond = getConditionInterviniente(intervinientes, intervinienteEntityAlias, isPenal, agregarFiltrosNombreCuitDni, agregarFiltrosNombreCuitDni);
		if(!Strings.isEmpty(cond)){
			conditions.add(cond);
			ok = true;
		}
		//agrega condiciones del expediente
		ok = addExpedienteCondition(actualExpedienteId, camara, camaraOriginal, desdeFecha, desdeAnioExpediente, 
				naturalezas, conTipoRadicacion, intervinienteEntityAlias, conditions, isPenal, filtroExpediente);
		return ok;
	}
	
	private boolean addExpedienteConditionWithoutInterviniente(Integer actualExpedienteId, Camara camara, Camara camaraOriginal,
			Date desdeFecha, Integer desdeAnioExpediente, List<String> naturalezas, String conTipoRadicacion, 
			String expedienteEntityAlias, List<String> conditions, boolean isPenal, boolean filtroVerificacionMesa){
		
		boolean ok = false;
		if((conTipoRadicacion == null) && !TipoRadicacionEnumeration.isAnySala(conTipoRadicacion) ){
			if(desdeFecha != null){
				conditions.add(expedienteEntityAlias + ".fechaSorteoCarga >= :" + DESDE_FECHA_PARAMETER);
				ok = true;
			}
			if (desdeAnioExpediente != null) {
				conditions.add(expedienteEntityAlias + ".anio >= " + desdeAnioExpediente.toString());
			}
		}
		if (camara != null) {
			conditions.add(expedienteEntityAlias + ".camaraActual.id = " + camara.getId());
		}
		if (camaraOriginal != null) {
			conditions.add(expedienteEntityAlias + ".camara.id = " + camaraOriginal.getId());
		}
		if(filtroVerificacionMesa) {
			conditions.add(expedienteEntityAlias + ".verificacionMesa in ('M','N')");
		}
		if (actualExpedienteId != null) {
			conditions.add(expedienteEntityAlias + ".id <> " + actualExpedienteId);
		}
		if(naturalezas != null) {
			addNaturalezaInCondition(naturalezas, expedienteEntityAlias, conditions);
		}
		if(conTipoRadicacion != null) {
			if(TipoRadicacionEnumeration.isAnySala(conTipoRadicacion) ){
				if((desdeAnioExpediente != null) || (desdeFecha != null)) {
					desdeFecha = calculaDesdeFecha(desdeAnioExpediente, desdeFecha);
					conditions.add(expedienteEntityAlias + " in (select oa.expediente from OficinaAsignacionExp oa where oa.tipoRadicacion = '"+conTipoRadicacion+"' and fechaAsignacion >=  :" + DESDE_FECHA_PARAMETER +")");
				}
			} else {
				conditions.add(expedienteEntityAlias + " in (select oa.expediente from OficinaAsignacionExp oa where oa.tipoRadicacion = '"+conTipoRadicacion+"')");
			}
		}
		return ok;
	}
	
	private boolean addExpedienteCondition(Integer actualExpedienteId, Camara camara, Camara camaraOriginal,
			Date desdeFecha, Integer desdeAnioExpediente, List<String> naturalezas, String conTipoRadicacion, 
			String intervinienteEntityAlias, List<String> conditions, boolean isPenal, boolean filtroVerificacionMesa){
		
		boolean ok = false;
		if((conTipoRadicacion == null) && !TipoRadicacionEnumeration.isAnySala(conTipoRadicacion) ){
			if(desdeFecha != null){
				conditions.add(intervinienteEntityAlias + ".expediente.fechaSorteoCarga >= :" + DESDE_FECHA_PARAMETER);
				ok = true;
			}
			if (desdeAnioExpediente != null) {
				conditions.add(intervinienteEntityAlias + ".expediente.anio >= " + desdeAnioExpediente.toString());
			}
		}
		if (camara != null) {
			conditions.add(intervinienteEntityAlias + ".expediente.camaraActual.id = " + camara.getId());
		}
		if (camaraOriginal != null) {
			conditions.add(intervinienteEntityAlias + ".expediente.camara.id = " + camaraOriginal.getId());
		}
		if(filtroVerificacionMesa) {
			conditions.add(intervinienteEntityAlias + ".expediente.verificacionMesa in ('M','N')");
		}
		if (actualExpedienteId != null) {
			conditions.add(intervinienteEntityAlias + ".expediente.id <> " + actualExpedienteId);
		}
		if(naturalezas != null) {
			addNaturalezaInCondition(naturalezas, intervinienteEntityAlias + ".expediente", conditions);
		}
		if(conTipoRadicacion != null) {
			if(TipoRadicacionEnumeration.isAnySala(conTipoRadicacion) ){
				if((desdeAnioExpediente != null) || (desdeFecha != null)) {
					desdeFecha = calculaDesdeFecha(desdeAnioExpediente, desdeFecha);
					conditions.add(intervinienteEntityAlias + ".expediente in (select oa.expediente from OficinaAsignacionExp oa where oa.tipoRadicacion = '"+conTipoRadicacion+"' and fechaAsignacion >=  :" + DESDE_FECHA_PARAMETER +")");
				}
			} else {
				conditions.add(intervinienteEntityAlias + ".expediente in (select oa.expediente from OficinaAsignacionExp oa where oa.tipoRadicacion = '"+conTipoRadicacion+"')");
			}
		}
		return ok;
	}
	

	private String createWhereClause(List<String> conditions) {
		StringBuilder sb = new StringBuilder();
		for(String cond: conditions){
			if(sb.length() > 0){
				sb.append(" and ");
			}
			sb.append(cond);
		}
		return sb.toString();
	}

	/**
	 * @param intervinientes
	 * @param intervinienteEntityAlias
	 * @return
	 */
	private StringBuffer addNombreCuitDniConditions(ListaIntervinientes intervinientes, String intervinienteEntityAlias, boolean intervinienteTable){
		StringBuffer buffer = new StringBuffer();
		Integer cantClauses = 0;
		
		if(intervinientes.hasMultipleInfo())
			buffer.append("( ");
		
		if(intervinientes.hasNombres()){
			buffer.append(getNombresRestriction(intervinientes, intervinienteEntityAlias, intervinienteTable));
			cantClauses++;
		}
		if(intervinientes.hasCuitCuils()){
			if(cantClauses>0) buffer.append(" or ");
			buffer.append(getCuitCuilRestriction(intervinientes, intervinienteEntityAlias, intervinienteTable));
			cantClauses++;
		}
		if(intervinientes.hasDnis()){
			if(cantClauses>0) buffer.append(" or ");
			buffer.append(getDniRestriction(intervinientes, intervinienteEntityAlias, intervinienteTable));
			cantClauses++;
		}
		
		if(intervinientes.hasMultipleInfo())
			buffer.append(" )");
		
		return buffer;
	}
	
	private StringBuffer addNativeNombreCuitDniConditions(ListaIntervinientes intervinientes){
		StringBuffer buffer = new StringBuffer();
		Integer cantClauses = 0;
		
		if(intervinientes.hasMultipleInfo())
			buffer.append("( ");
		
		if(intervinientes.hasNombres()){
			buffer.append(getNativeNombresRestriction(intervinientes));
			cantClauses++;
		}
		if(intervinientes.hasCuitCuils()){
			if(cantClauses>0) buffer.append(" or ");
			buffer.append(getNativeCuitCuilRestriction(intervinientes));
			cantClauses++;
		}
		if(intervinientes.hasDnis()){
			if(cantClauses>0) buffer.append(" or ");
			buffer.append(getNativeDniRestriction(intervinientes));
			cantClauses++;
		}
		
		if(intervinientes.hasMultipleInfo())
			buffer.append(" )");
		
		return buffer;
	}
	
	private String getConditionInterviniente(ListaIntervinientes intervinientes, String intervinienteEntityAlias, boolean isPenal, 
											boolean agregarFiltrosNombreCuitDni, boolean intervinienteTable) {
		StringBuffer sb = new StringBuffer();
		
		if(agregarFiltrosNombreCuitDni)
			sb.append(addNombreCuitDniConditions(intervinientes, intervinienteEntityAlias, intervinienteTable));
		
		if(sb.length()>0)
			sb.append(" and ");
		
		if(intervinientes.getTipo().getOrder() != null){
			sb.append(intervinienteEntityAlias+".tipoInterviniente.numeroOrden = ");
			sb.append(intervinientes.getTipo().getOrder());
		} else {
			sb.append(intervinienteEntityAlias+".tipoInterviniente.numeroOrden in (1, 2)");
		}
		sb.append(" and ").append(intervinienteEntityAlias+".status <> -1");
		if (isPenal) {
			sb.append(" and ").append(intervinienteEntityAlias+".identidadReservada = 0");
			sb.append(" and (");
			sb.append(intervinienteEntityAlias+".estadoInterviniente is null or ");
			sb.append(intervinienteEntityAlias+".estadoInterviniente in (select ei from EstadoInterviniente ei where ei.codigo <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "'))");
		}
		return sb.toString();
	}

	private String getNombresRestriction(ListaIntervinientes intervinientes, String intervinienteEntityAlias, boolean intervinienteTable){
		String parametro = intervinientes.getParametroNombre();
		return getRestriction(intervinientes, intervinienteEntityAlias, "nombre", parametro, intervinientes.getNombres(), intervinienteTable);
	}

	private String getCuitCuilRestriction(ListaIntervinientes intervinientes, String intervinienteEntityAlias, boolean intervinienteTable){
		String parametro = intervinientes.getParametroCuit();
		return getRestriction(intervinientes, intervinienteEntityAlias, "numeroDocId", parametro, intervinientes.getCuitCuils(), intervinienteTable);
	}
	
	private String getDniRestriction(ListaIntervinientes intervinientes, String intervinienteEntityAlias, boolean intervinienteTable){
		String parametro = intervinientes.getParametroDni();
		return getRestriction(intervinientes, intervinienteEntityAlias, "dni", parametro, intervinientes.getDnis(), intervinienteTable);
	}

	private String getRestriction(ListaIntervinientes intervinientes, String intervinienteEntityAlias, 
								String intervinienteColumn, String parametro, List<String> values, boolean intervinienteTable){
		StringBuffer sb = new StringBuffer(); 
		sb.append(intervinienteEntityAlias+".");
		if(intervinienteTable)
			sb.append("interviniente.");
		sb.append(intervinienteColumn);
		if(values.size() == 1){
			sb.append(" = :"+parametro);
		}else{
			sb.append(" in (:"+parametro+")");
		}
		return sb.toString();
	}

	private String getConditionExpedienteHasIntervinientes(Integer actualExpedienteId, Camara camara, Camara camaraOriginal, ListaIntervinientes actores, ListaIntervinientes demandados, Date desdeFecha, Integer desdeAnioExpediente, List<String> naturalezas, String conTipoRadicacion, String expedienteColumnExpression, boolean isPenal) {
		
		String condStmt = createIntervinientesCombQueryStmt(actualExpedienteId, camara, camaraOriginal, actores, demandados, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, "iexp", isPenal);
		
		return expedienteColumnExpression+" in (select "+"iexp.expediente "+condStmt+")";		
	}

	private String getConditionExpedienteHasInterviniente(Integer actualExpedienteId, Camara camara, Camara camaraOriginal, ListaIntervinientes intervinientes, Date desdeFecha, Integer desdeAnioExpediente, List<String> naturalezas, String conTipoRadicacion, String expedienteColumnExpression, boolean isPenal) {
		
		String condStmt = createIntervinienteQueryStmt(actualExpedienteId, camara, camaraOriginal, intervinientes, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, "iexp", isPenal);
		
		return expedienteColumnExpression+" in (select "+"iexp.expediente "+condStmt+")";		
	}

	private static Date getFechaDesdeHace(int anios, int meses, int dias) {
		GregorianCalendar gc = new GregorianCalendar();
		
		gc.setTime(new Date());
		
		if(anios > 0){
			gc.add(GregorianCalendar.YEAR, -anios);
		}
		if(meses > 0){
			gc.add(GregorianCalendar.MONTH, -meses);
		}
		if(dias > 0){
			gc.add(GregorianCalendar.DAY_OF_YEAR, -dias);
		}
		return gc.getTime();
	}

	@SuppressWarnings("unchecked")
	public static List<Integer> getIdEstadosFinalizacion(EntityManager entityManager, Materia materia) {
		return entityManager.createQuery("select id from EstadoExpediente where (materia is null or materia =:materia) and status <> -1 and tipoFinalizacion = :tipoFinalizacion").
				setParameter("materia",materia).
				setParameter("tipoFinalizacion", TipoFinalizacionEnumeration.Type.finalizacion_expediente.getValue()).
				getResultList();
	}

	public static  EstadoExpediente getEstadoSinIniciar(EntityManager entityManager, Materia materia) {
		return EstadoExpedienteEnumeration.getEstadoExpediente(entityManager, (String)EstadoExpedientePredefinedEnumeration.Type.$SI.getValue(), materia);
	}

	private Integer cantidadFrecuentes(ListaIntervinientes intervinientes) {
		Integer cantidadFrecuentes = 0;
		if ((intervinientes.getNombres() == null) || (intervinientes.getNombres().size() == 0))
			return cantidadFrecuentes;
		
		Camara camara = SessionState.instance().getGlobalCamara();
		for (String nombre : intervinientes.getNombres()) {
			if(SiglaSearch.isFrecuente(getEntityManager(), nombre, camara)) {
				cantidadFrecuentes++;
			}
		}
		return cantidadFrecuentes;
	}

	private boolean hayNoFrecuentes(ListaIntervinientes intervinientes) {
		if ((intervinientes.getNombres() == null) || (intervinientes.getNombres().size() == 0)) {
			return true;
		}
		Camara camara = SessionState.instance().getGlobalCamara();
		for (String nombre : intervinientes.getNombres()) {
			if(!SiglaSearch.isFrecuente(getEntityManager(), nombre, camara)) {
				return true;
			}
		}
		return false;
	}

	private void setParameter(Tracer tracer, Query query, String nombre, Object valor) {
		query.setParameter(nombre, valor);
		if (tracer != null) {
			tracer.notify(nombre, valor.toString());
		}
	}

	@SuppressWarnings("unused")
	private void setParameter(Tracer tracer, Query query, String nombre, List<Object> valores) {
		query.setParameter(nombre, valores);
		if (tracer != null) {
			tracer.notify(nombre, toString(valores));
		}
	}

	private String toString(List<Object> valores) {
		StringBuffer result = new StringBuffer(); 
		for(Object valor:valores) {
			if(result.length() > 0) {
				result.append(", ");
			}
			result.append(valor.toString());
		}
		return null;
	}

	private int executeQuery(Tracer tracer, String SQL, ListaIntervinientes intervinientes, List<String> naturalezaList, Date desdeFecha, String entityDescriptionName) {
		Query query;
		query = getEntityManager().createQuery(SQL);
		if(tracer != null) {
			tracer.notify("Sentencia HQL", SQL);
		}
		setParametersIntervinientes(tracer, query, intervinientes);
		setParameterNaturalezaIn(tracer, query, naturalezaList);
		setParameterDespuesDeFecha(tracer, query, desdeFecha);
		int rows = query.executeUpdate();
		
		tracer.notify("", rows + " " + entityDescriptionName);

		return rows;
	}
	
	private int executeQuery(Tracer tracer, String SQL, ListaIntervinientes intervinientes, List<String> naturalezaList, Date desdeFecha) {
		return executeQuery(tracer, SQL, intervinientes, naturalezaList, desdeFecha, "expedientes");
	}

	private int executeNativeQuery(Tracer tracer, String SQL, ListaIntervinientes intervinientes, List<String> naturalezaList, Date desdeFecha, String entityDescriptionName) {
		Query query;
		query = getEntityManager().createNativeQuery(SQL);
		if(tracer != null) {
			tracer.notify("Sentencia SQL-Nativa", SQL);
		}
		setParametersIntervinientes(tracer, query, intervinientes);
		setParameterNaturalezaIn(tracer, query, naturalezaList);
		setParameterDespuesDeFecha(tracer, query, desdeFecha);
		int rows = query.executeUpdate();
		
		tracer.notify("", rows + " " + entityDescriptionName);
		
		return rows;
	}
	
	/**
	 * Realiza el borrado de datos de las tablas temporales utilizadas para las busquedas de conexidad
	 */
	private void cleanTempTables(){
		Query query;
		
		query = getEntityManager().createQuery("delete from " + EXPEDIENTE_INTERVINIENTE_IDS);
		query.executeUpdate();
		query = getEntityManager().createQuery("delete from " + EXPEDIENTE_INTERV_TEMP_CNX_TABLE);
		query.executeUpdate();
		query = getEntityManager().createQuery("delete from " + EXPEDIENTE_TEMP_TABLE_1);
		query.executeUpdate();
		query = getEntityManager().createQuery("delete from " + EXPEDIENTE_TEMP_TABLE_2);
		query.executeUpdate();
	}
	
	/**
	 * @param tracer * @param actualExpedienteId * @param camara * @param camaraOriginal * @param actores * @param demandados * @param desdeFecha
	 * @param desdeAnioExpediente * @param naturalezas * @param conTipoRadicacion * @param expedienteColumnExpression * @param isPenal
	 * @return
	 */
	private String getExpedientesIncluidosTempTable(Tracer tracer, Integer actualExpedienteId, Camara camara, Camara camaraOriginal, ListaIntervinientes actores, ListaIntervinientes demandados, Date desdeFecha,
			Integer desdeAnioExpediente, List<String> naturalezas, String conTipoRadicacion, String expedienteColumnExpression, boolean isPenal) {
		/*
		 * IMPORTANTE !!
		 * Se utilizan tablas temporales por la performance a la BD teniendo en cuenta la cantidad de registro de la tabla Interviente
		 * que tarda mucho en hacer el JOIN con IntervinienteExp y Expediente para poder obtener las causas deseadas.
		 * Para ello se crea primero una tabla temporal con los intervinientes segun el nombre (no se aplica entonces JOIN alguno)
		 * y luego se obtienen los expedientes a partir de esa tabla temporal
		 * (aplicando los JOINS, pero en este caso solo con los datos encontrados en la tabla temporal anterior)
		 */
		
		this.cleanTempTables();
		int rows = 0;
		if(actores.hasAnyInfo() && demandados.hasAnyInfo()){
			//Se busca la cantidad de frecuentes de actores y de demandados para buscar por el que tenga menos
			Integer cantidadActoresFrecuentes = cantidadFrecuentes(actores);
			Integer cantidadDemandadosFrecuentes = cantidadFrecuentes(demandados);
			
			boolean hayMasActoresFrecuentes = (cantidadActoresFrecuentes>cantidadDemandadosFrecuentes);
			if (cantidadActoresFrecuentes==0 && cantidadDemandadosFrecuentes==0)
				hayMasActoresFrecuentes = actores.isHayPersonaJuridica();
			
			//en caso que sea la misma cantidad de frecuentes se verifica que los actores no sean mas de 1000 por error en IN clause de Oracle
			if (cantidadActoresFrecuentes==cantidadDemandadosFrecuentes && !hayMasActoresFrecuentes)
				hayMasActoresFrecuentes = actores.hasTooMuchValues();
				
			ListaIntervinientes intervinientes1 = hayMasActoresFrecuentes ? demandados: actores;
			ListaIntervinientes intervinientes2 = hayMasActoresFrecuentes ? actores: demandados;
			
			Integer numeroOrden = intervinientes1.getTipo().getOrder();
			String labelParameter = intervinientes1.getTipo().getLabel();
			//inserta en tabla temporal todos los expedientes de los intervinientes seleccionados segun su numero de orden
			System.out.println("[TEMP 1] Buscando tabla temporal TEMP_EXP_INTERVINIENTE_IDS de expedientes por nombre para partes: " + intervinientes1.getTipo() + " y expediente con ID: " + actualExpedienteId);
			rows = insertIntoTempTableIntervinienteByNombreYDNI(tracer, intervinientes1, labelParameter, numeroOrden);
			if(rows>0) {
				//inserta en tabla temporal de expedientes 1 a partir de la tabla temporal anterior
				System.out.println("[TEMP 2] Buscando tabla temporal de EXPEDIENTES_IDS1 para partes: " + intervinientes1.getTipo() + " y expediente con ID: " + actualExpedienteId);
				rows = insertIntoTempExpedientesIds(tracer, EXPEDIENTE_TEMP_TABLE_1, EXPEDIENTE_INTERVINIENTE_IDS, numeroOrden,
						actualExpedienteId, camara, camaraOriginal, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, isPenal);
				if (rows > 0){
					//se buscan todas las partes de los expedientes anteriormente encontrados para buscar coincidencias con el otro tipo de parte
					System.out.println("[TEMP 3] Buscando tabla temporal de nombre de intervinientes de todos los expedientes encontrados en EXPEDIENTES_IDS1 para partes: " + intervinientes1.getTipo() + " y expediente con ID: " + actualExpedienteId);
					rows = insertIntoExpIntervTempTableConexidadPartes(tracer, EXPEDIENTE_INTERV_TEMP_CNX_TABLE, EXPEDIENTE_TEMP_TABLE_1, actualExpedienteId);
					if(rows > 0)
						//en caso que haya coincidencia en alguna de las partes anteriores inserta en tabla temporal 2 de expedientes que son los conexos
						System.out.println("[TEMP 4] Buscando tabla temporal de EXPEDIENTES_IDS2 para partes: " + intervinientes1.getTipo() + " y expediente con ID: " + actualExpedienteId);
						rows = insertIntoTempTableByInterviniente(tracer, EXPEDIENTE_INTERV_TEMP_CNX_TABLE, EXPEDIENTE_TEMP_TABLE_2, actualExpedienteId, camara, camaraOriginal, intervinientes2, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, isPenal);
				}
			}
		} else if(actores.hasAnyInfo()){
			ActorDemandado tipo = actores.getTipo();
			Integer numeroOrden = tipo.getOrder();
			String labelParameter = tipo.getLabel();
			//inserta en tabla temporal todos los expedientes de los intervinientes actores seleccionados segun su numero de orden
			System.out.println("[TEMP 5] Buscando tabla temporal TEMP_EXP_INTERVINIENTE_IDS de expedientes por nombre para partes: " + tipo + " y expediente con ID: " + actualExpedienteId);
			rows = insertIntoTempTableIntervinienteByNombreYDNI(tracer, actores, labelParameter, numeroOrden);
			if(rows>0)
				//si existen resultados insertar en la tabla temporal de expedientes
				System.out.println("[TEMP 6] Buscando tabla temporal de id de expedientes 2 para partes: " + tipo + " y expediente con ID: " + actualExpedienteId);
				rows = insertIntoTempExpedientesIds(tracer, EXPEDIENTE_TEMP_TABLE_2, EXPEDIENTE_INTERVINIENTE_IDS, numeroOrden,
						actualExpedienteId, camara, camaraOriginal, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, isPenal);
		} else if(demandados.hasAnyInfo()) {
			ActorDemandado tipo = demandados.getTipo();
			Integer numeroOrden = tipo.getOrder();
			String labelParameter = tipo.getLabel();
			//inserta en tabla temporal todos los expedientes de los intervinientes demandados seleccionados segun su numero de orden
			System.out.println("[TEMP 7] Buscando tabla temporal TEMP_EXP_INTERVINIENTE_IDS de expedientes por nombre para partes: " + tipo + " y expediente con ID: " + actualExpedienteId);
			rows = insertIntoTempTableIntervinienteByNombreYDNI(tracer, demandados, labelParameter, numeroOrden);
			if(rows>0)
				//si existen resultados insertar en la tabla temporal de expedientes
				System.out.println("[TEMP 8] Buscando tabla temporal de id de expedientes 2 para partes: " + tipo + " y expediente con ID: " + actualExpedienteId);
				rows = insertIntoTempExpedientesIds(tracer, EXPEDIENTE_TEMP_TABLE_2, EXPEDIENTE_INTERVINIENTE_IDS, numeroOrden,
						actualExpedienteId, camara, camaraOriginal, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, isPenal);
		}
		if(rows > 0)
			return EXPEDIENTE_TEMP_TABLE_2;
		return null;
	}
	
	/**
	 * @param tracer * @param tempTable * @param tempTableInterviniente * @param numeroOrden * @param actualExpedienteId * @param camara
	 * @param camaraOriginal * @param desdeFecha * @param desdeAnioExpediente * @param naturalezas * @param conTipoRadicacion * @param isPenal
	 * @return
	 */
	private int insertIntoTempExpedientesIds(Tracer tracer, String tempTable, String tempTableInterviniente, Integer numeroOrden, Integer actualExpedienteId,
			Camara camara, Camara camaraOriginal, Date desdeFecha, Integer desdeAnioExpediente, List<String> naturalezas, String conTipoRadicacion, boolean isPenal) {
		String aliasTable = "e";
		
		String condStmt = createExpedienteQueryStmt(actualExpedienteId, camara, camaraOriginal, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, aliasTable, isPenal);
		condStmt += " AND "+aliasTable+".id IN (select temp.id from " + tempTableInterviniente + " temp)";
		
		String SQL = "insert into " + tempTable + " (id) select "+aliasTable+".id "+condStmt;
		SQL += " AND " + aliasTable + ".status <> -1";
		SQL += " GROUP BY e.id";
		
		return executeQuery(tracer, SQL, null, naturalezas, desdeFecha);
	}
	
	/**
	 * Busca los nombres de todos los intervinientes a partir de una tabla de ID de expedientes
	 * @param tracer * @param tempTable * @param inTempTable * @param actualExpedienteId
	 * @return cantidad de registros encontrados e insertados en tabla temporal
	 */
	private int insertIntoExpIntervTempTableConexidadPartes(Tracer tracer, String tempTable, String inTempTable, Integer actualExpedienteId) {
		String aliasTable = "iexp";
		String SQL;
			SQL = "INSERT INTO " + tempTable + " (id,nombre,numeroDocId,dni) "
				+ "SELECT "+aliasTable+".expediente.id, "+aliasTable+".interviniente.nombre, "+aliasTable+".interviniente.numeroDocId, "+aliasTable+".interviniente.dni "
				+ "FROM IntervinienteExp "+aliasTable+" "
				+ "WHERE iexp.expediente IN (SELECT temp.id FROM " + inTempTable + " temp WHERE temp.id <> "+actualExpedienteId+") ";
			
		return executeQuery(tracer, SQL, null, null, null, "intervinientes");
	}
	
	/**
	 * @param tracer * @param inTempTable * @param tempTable * @param actualExpedienteId * @param camara * @param camaraOriginal
	 * @param intervinientes * @param desdeFecha * @param desdeAnioExpediente * @param naturalezas * @param conTipoRadicacion * @param isPenal
	 * @return
	 */
	private int insertIntoTempTableByInterviniente(Tracer tracer, String inTempTable, String tempTable, Integer actualExpedienteId,
			Camara camara, Camara camaraOriginal, ListaIntervinientes intervinientes, Date desdeFecha, Integer desdeAnioExpediente, 
			List<String> naturalezas, String conTipoRadicacion, boolean isPenal) {
		int rows = 0;
		String SQL;
		String condStmt;
		if(intervinientes.hasAnyInfo()) {
			if (inTempTable != null) {
				List<String> conditions = new ArrayList<String>(); 
				addTempNombreIntervinienteCondition(intervinientes, conditions, inTempTable);
				naturalezas = null;
				desdeFecha = null;
				addIntervinienteConditions(actualExpedienteId, camara, camaraOriginal, intervinientes, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, 
										"iexp", conditions, isPenal, true, false);
				condStmt = "from IntervinienteExp iexp where " + createWhereClause(conditions);
				SQL = "insert into " + tempTable + " (id) select iexp.expediente.id "+condStmt + " group by iexp.expediente.id";
			} else {
				condStmt = createIntervinienteQueryStmt(actualExpedienteId, camara, camaraOriginal, intervinientes, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, "iexp", isPenal);
				SQL = "insert into " + tempTable + " (id) select iexp.expediente.id "+condStmt + " group by iexp.expediente.id";
			}
			
			List<String> nombres = intervinientes.getNombres();
			//si tiene mas de 999 nombres hay que ejecutar mas de una vez el INSERT porque Hibernate rompe con IN de mas de 999 elementos
			if(nombres!=null && nombres.size()>MAX_ITEMS_IN_CLAUSE)
				rows = getRowsFromSubList(tracer, SQL, intervinientes, naturalezas, desdeFecha, "expedientes", false);
			else
				rows = executeQuery(tracer, SQL, intervinientes, naturalezas, desdeFecha);
		}
		return rows;
	}
	
	/**
	 * Ejecuta query nativa sobre una sublista de maximo 1000 items
	 * @param tracer
	 * @param SQL
	 * @param intervinientes
	 * @param naturalezas
	 * @param desdeFecha
	 * @param entityDescriptionName
	 * @return
	 */
	private int getRowsFromSubList(Tracer tracer, String SQL, ListaIntervinientes intervinientes, List<String> naturalezas, Date desdeFecha, String entityDescriptionName, boolean isNative){
		int rows = 0;
		List<String> nombres = intervinientes.getNombres();
		
		Integer size = nombres.size();
		Integer iteraciones = (size / MAX_ITEMS_IN_CLAUSE) + 1;
		Integer maxActual = 0;
		for(Integer i=0;i<iteraciones;i++){
			List<String> subNombres = new ArrayList<String>();
			
			Integer next = ((i+1)==iteraciones) ? size : (maxActual+MAX_ITEMS_IN_CLAUSE);
			subNombres = nombres.subList(maxActual, next);
			maxActual = next;
			
			//se crea nueva lista de intervinientes con la sublista de nombres de maximo 999 elementos
			ListaIntervinientes newListaIntervinientes = new ListaIntervinientes(intervinientes.getTipo(),subNombres,
					intervinientes.getCuitCuils(),intervinientes.getDnis(),
					intervinientes.isHayPersonaJuridica());
			
			if(isNative)
				rows += executeNativeQuery(tracer, SQL, newListaIntervinientes, naturalezas, desdeFecha, entityDescriptionName);
			else
				rows += executeQuery(tracer, SQL, newListaIntervinientes, naturalezas, desdeFecha, entityDescriptionName);
		}
		return rows;
	}
	
	/**
	 * Busqueda en segunda tabla temporal a partir de otra
	 * @param intervinientes
	 * @param conditions
	 * @param inTempTable
	 */
	private void addTempNombreIntervinienteCondition(ListaIntervinientes intervinientes, List<String> conditions, String inTempTable){
		StringBuffer sb = new StringBuffer();
		String aliasTempTable = "t";
		sb.append(addNombreCuitDniConditions(intervinientes, aliasTempTable, false));
		
		String tempClause = "iexp.expediente.id in (select "+aliasTempTable+".id from " + inTempTable + " " + aliasTempTable + " ";
		if(!sb.toString().isEmpty())
			tempClause += "WHERE " + sb.toString();
		tempClause += ") ";
		
		conditions.add(tempClause);
	}
	
	/**
	 * Inserta intervinientes en tabla temporal de Oracle y guarda en el trace la cantidad insertada
	 * @param tracer * @param inTempTable * @param tempTable * @param actualExpedienteId * @param camara
	 * @param camaraOriginal * @param intervinientes * @param desdeFecha * @param desdeAnioExpediente
	 * @param naturalezas * @param conTipoRadicacion * @param isPenal
	 * @return cantidad de registros insertados
	 */
	private int insertIntoTempTableIntervinienteByNombreYDNI(Tracer tracer, ListaIntervinientes intervinientes, String labelParameter, Integer numeroOrden) {
		int rows = 0;
		String tempTableAlias = "interviniente";
		String tempTable = "TEMP_EXP_INTERVINIENTE_IDS";
		
		if(intervinientes.hasAnyInfo()) {
			//crea la query del INSERT en tabla temporal
			String query = doCreateNativeQueryTempTableIntervinienteByNombreYDNI(tempTable, intervinientes, tempTableAlias, numeroOrden);
			
			if(query!=null){
				List<String> nombres = intervinientes.getNombres();
				//en caso de ser mas de 1000 nombres Hibernate no permite hacer IN en la query, entonces dividimos la ejecuccion
				if(nombres!=null && nombres.size()>MAX_ITEMS_IN_CLAUSE)
					rows = getRowsFromSubList(tracer, query, intervinientes, null, null, "expedientes", true);
				else
					rows = executeNativeQuery(tracer, query, intervinientes, null, null, "expedientes");
			}
		}
		return rows;
	}
	
	/**
	 * Crea la query para hacer insert en tabla temporal para busqueda de intervinientes por nombre y dni
	 * @param tempTable
	 * @param intervinientes
	 * @param tempTableAlias
	 * @return
	 */
	private String doCreateNativeQueryTempTableIntervinienteByNombreYDNI(String tempTable, ListaIntervinientes intervinientes, String tempTableAlias, Integer numeroOrden) {
		String SQL = null;
		if(intervinientes.hasAnyInfo()){
			String tableWithName = "int_nombre";
			
			SQL = "INSERT INTO " + tempTable + " (id_expediente) ";
			
			//bloque WITH de Oracle
			SQL+= "WITH "+tableWithName+" AS "
			+ "(SELECT " + tempTableAlias + ".id_interviniente AS idInterviniente "
			+ " FROM Interviniente "+tempTableAlias
			+ " WHERE ";
			
			StringBuffer stringBuffer = addNativeNombreCuitDniConditions(intervinientes);
			if(stringBuffer.length()>0){
				stringBuffer.append(" AND ");
				SQL += stringBuffer.toString();
			}
			SQL+=tempTableAlias+".status = 0) ";
			
			//select siguiente al bloque with de Oracle
			SQL += "SELECT iexp.id_expediente "
				 + "FROM interviniente_exp iexp, "+tableWithName+" "
				 + "WHERE "+tableWithName+".idInterviniente = iexp.id_interviniente "
				 	+ "AND iexp.status<>-1 ";
			if(numeroOrden!=null)
				SQL += "AND iexp.numero_orden = "+numeroOrden;
		}
		return SQL;
	}

//	private int insertIntoTempTableByIntervinienteNative(Tracer tracer, String inTempTable, String tempTable, Integer actualExpedienteId, Camara camara, Camara camaraOriginal, ListaIntervinientes intervinientes, Date desdeFecha, Integer desdeAnioExpediente, List<String> naturalezas, String conTipoRadicacion, boolean isPenal) {
//		int rows = 0;
//		String SQL;
//		String condStmt;
//		if(intervinientes.hasAnyInfo()) {
//			if (inTempTable != null) {
//				List<String> conditions = new ArrayList<String>(); 
//				conditions.add("intervinienteExp.ID_EXPEDIENTE in (select t.ID_EXPEDIENTE from " + inTempTable + " t)");
//				naturalezas = null;
//				addIntervinienteNativeConditions(null, null, null, intervinientes, null, null, null, null, "intervinienteExp", conditions, isPenal, false);
//				condStmt = "from INTERVINIENTE_EXP intervinienteExp, TIPO_INTERVINIENTE tipoInterviniente, INTERVINIENTE interviniente where " + createWhereClause(conditions);
//				SQL = "insert into " + tempTable + " select /*+ORDERED */ intervinienteExp.ID_EXPEDIENTE "+condStmt + " group by intervinienteExp.ID_EXPEDIENTE";
//			} else {
//				condStmt = createIntervinienteNativeQueryStmt(actualExpedienteId, camara, camaraOriginal, intervinientes, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, "intervinienteExp", isPenal);
//				SQL = "insert into " + tempTable + " (id) select intervinienteExp.ID_EXPEDIENTE "+condStmt + " group by intervinienteExp.ID_EXPEDIENTE";
//			}
//			rows = executeNativeQuery(tracer, SQL, intervinientes, naturalezas, desdeFecha);
//		}
//		return rows;
//	}

//	@SuppressWarnings("unchecked")
//	private List<Integer> getNativeIdExpedientesIncluidosList(Tracer tracer, ListaIntervinientes actores, ListaIntervinientes demandados, Date desdeFecha, Integer desdeAnioExpediente, List<String> naturalezas, String expedienteColumnExpression,
//			boolean isPenal) {
//
//		
//		Query query = getEntityManager().createNativeQuery("DELETE FROM TEMP_EXPEDIENTES_IDS");
//		query.executeUpdate();
//		
//		String SQL;
//		String condStmt;
//		if(actores.hasAnyInfo()){
//			condStmt = createIntervinienteNativeQueryStmt(actores, desdeFecha, desdeAnioExpediente,naturalezas, isPenal);
//			SQL = "INSERT INTO TEMP_EXPEDIENTES_IDS SELECT DISTINCT intervinienteExp.ID_EXPEDIENTE "+condStmt;
//			executeNativeQuery(tracer, SQL, actores);
//
//			if(demandados.hasAnyInfo()) {
//				condStmt = createIntervinienteNativeQueryStmt(demandados, desdeFecha, desdeAnioExpediente,naturalezas, isPenal);
//				SQL = "DELETE FROM EXPEDIENTES_IDS_IN WHERE ID_EXPEDIENTE NOT IN SELECT intervinienteExp.ID_EXPEDIENTE "+condStmt;
//				executeNativeQuery(tracer, SQL, demandados);
//			}
//		} else if(demandados.hasAnyInfo()) {
//			condStmt = createIntervinienteNativeQueryStmt(demandados, desdeFecha, desdeAnioExpediente,naturalezas, isPenal);
//			SQL = "INSERT INTO EXPEDIENTES_IDS_IN SELECT DISTINCT intervinienteExp.ID_EXPEDIENTE "+condStmt;
//			executeNativeQuery(tracer, SQL, demandados);
//		}
//		
//		return getEntityManager().createNativeQuery("SELECT ID FROM EXPEDIENTES_IDS_IN").getResultList();
//	}
//
	private String createIntervinienteNativeQueryStmt(Integer actualExpedienteId, Camara camara, Camara camaraOriginal, ListaIntervinientes intervinientes, Date desdeFecha, Integer desdeAnioExpediente, List<String> naturalezas, String conTipoRadicacion, String intervinienteEntityAlias, boolean isPenal) {		
		List<String> conditions = new ArrayList<String>(); 
			
		addIntervinienteNativeConditions(actualExpedienteId, camara, camaraOriginal, intervinientes, desdeFecha, desdeAnioExpediente, naturalezas, conTipoRadicacion, intervinienteEntityAlias, conditions, isPenal, true);

		StringBuffer stmt = new StringBuffer();
		stmt.append("FROM INTERVINIENTE_EXP intervinienteExp, EXPEDIENTE expediente, INTERVINIENTE interviniente, TIPO_INTERVINIENTE tipointerviniente WHERE ");
		stmt.append("intervinienteExp.ID_EXPEDIENTE = expediente.ID_EXPEDIENTE");
		if(conditions.size() > 0) {
			stmt.append( " AND ");
		}
		stmt.append(createWhereClause(conditions));
		return stmt.toString();
	}
	
	private boolean addIntervinienteNativeConditions(Integer actualExpedienteId, Camara camara, Camara camaraOriginal, ListaIntervinientes intervinientes, Date desdeFecha, Integer desdeAnioExpediente, List<String> naturalezas, String conTipoRadicacion, String intervinienteEntityAlias, List<String> conditions, boolean isPenal, boolean filtroExpediente) {
		boolean ok = false;
		String cond = getNativeConditionInterviniente(intervinientes, isPenal);
		if(!Strings.isEmpty(cond)){
			conditions.add(cond);
			ok = true;
		}
		if(desdeFecha != null){
			conditions.add("expediente.fechaSorteoCarga >= :" + DESDE_FECHA_PARAMETER);
			ok = true;
		}
		if (camara != null) {
			conditions.add("expediente.ID_CAMARA_ACTUAL = " + camara.getId().toString());
		}
		if (camaraOriginal != null) {
			conditions.add("expediente.ID_CAMARA = " + camaraOriginal.getId().toString());
		}
		if(filtroExpediente) {
			conditions.add("expediente.VERIFICACION_MESA IN ('M','N')");
		}
		if (desdeAnioExpediente != null) {
			conditions.add("expediente.ANIO_EXPEDIENTE >= " + desdeAnioExpediente.toString());
		}
		if ((naturalezas != null) && (naturalezas.size() > 0)) {
			if(naturalezas.size() > 1){
				conditions.add("expediente.NATURALEZA_EXPEDIENTE IN (" + Configuration.getCommaSeparatedString(naturalezas)+")");
			}else{
				conditions.add("expediente.NATURALEZA_EXPEDIENTE = '"+naturalezas.get(0)+"'");
			}
		}
		if(conTipoRadicacion != null) {
			conditions.add(intervinienteEntityAlias + ".ID_EXPEDIENTE in (select oa.ID_EXPEDIENTE from OFICINA_ASIGNACION_EXP oa where oa.TIPO_RADICACION = '"+conTipoRadicacion+"')");
		}

		return ok;
	}

	private String getNativeConditionInterviniente(ListaIntervinientes intervinientes, boolean isPenal) {
		
		StringBuffer sb = new StringBuffer(); 

		sb.append("intervinienteExp.STATUS <> -1");
		
		sb.append(" AND intervinienteExp.ID_TIPO_INTERVINIENTE=tipointerviniente.ID_TIPO_INTERVINIENTE");

		if(intervinientes.getTipo().getOrder() != null){
			sb.append(" AND ");
			sb.append("tipoInterviniente.NUMERO_ORDEN = ");
			sb.append(intervinientes.getTipo().getOrder());
		}
		if (isPenal) {
			sb.append(" AND intervinienteExp.IDENTIDAD_RESERVADA = 0");
			sb.append(" AND (intervinienteExp.ID_ESTADO_INTERVINIENTE IS NULL OR ");
			sb.append("intervinienteExp.ID_ESTADO_INTERVINIENTE IN (SELECT ei.ID_ESTADO_INTERVINIENTE FROM ESTADO_INTERVINIENTE ei WHERE ei.CODIGO_ESTADO_INTERVINIENTE <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "'))");
		}

		sb.append(" AND interviniente.ID_INTERVINIENTE = intervinienteExp.ID_INTERVINIENTE");
		sb.append(" AND ");
		boolean addOrNexus = false;
		if(intervinientes.hasMultipleInfo()){
			sb.append("( ");
		}

		if(intervinientes.hasNombres()){
			sb.append(getNativeNombresRestriction(intervinientes));
			addOrNexus = true;
		}
		if(intervinientes.hasCuitCuils()){
			if(addOrNexus){
				sb.append(" or ");
			}
			sb.append(getNativeCuitCuilRestriction(intervinientes));
			addOrNexus = true;
		}
		if(intervinientes.hasDnis()){
			if(addOrNexus){
				sb.append(" or ");
			}
			sb.append(getNativeDniRestriction(intervinientes));
			addOrNexus = true;
		}
		if(intervinientes.hasMultipleInfo()){
			sb.append(" )");
		}
		
		return sb.toString();
	}

	private String getNativeNombresRestriction(ListaIntervinientes intervinientes){
		String parametro = intervinientes.getParametroNombre();
		return getNativeRestriction(intervinientes, "NOMBRE", parametro, intervinientes.getNombres());
	}

	private String getNativeCuitCuilRestriction(ListaIntervinientes intervinientes){
		String parametro = intervinientes.getParametroCuit();
		return getNativeRestriction(intervinientes, "NUMERO_DOC_ID", parametro, intervinientes.getCuitCuils());
	}
	
	private String getNativeDniRestriction(ListaIntervinientes intervinientes){
		String parametro = intervinientes.getParametroDni();
		return getNativeRestriction(intervinientes, "DNI", parametro, intervinientes.getDnis());
	}

	private String getNativeRestriction(ListaIntervinientes intervinientes, String intervinienteColumn, String parametro, List<String> values){
		StringBuffer sb = new StringBuffer(); 
		sb.append("interviniente."+intervinienteColumn);
		if(values.size() == 1){
			sb.append(" = :"+parametro);
		}else{
			sb.append(" in (:"+parametro+")");
		}
		return sb.toString();
	}
}
