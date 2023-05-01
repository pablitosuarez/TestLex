package com.base100.lex100.mesaEntrada.sorteo;

import groovy.transform.Synchronized;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.Strings;

import com.base100.lex100.Lex100Exception;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.currentdate.CurrentDateManager;
import com.base100.lex100.component.enumeration.CamaraEnumeration;
import com.base100.lex100.component.enumeration.MateriaEnumeration;
import com.base100.lex100.component.enumeration.RegimenProcesalEnumeration;
import com.base100.lex100.component.enumeration.TipoCausaEnumeration;
import com.base100.lex100.component.enumeration.TipoGiroEnumeration;
import com.base100.lex100.component.enumeration.TipoInstanciaEnumeration;
import com.base100.lex100.component.enumeration.TipoOficinaEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration.Type;
import com.base100.lex100.component.enumeration.TurnoSorteoEnumeration;
import com.base100.lex100.component.misc.DateUtil;
import com.base100.lex100.controller.entity.camara.CamaraSearch;
import com.base100.lex100.controller.entity.sorteo.SorteoHome;
import com.base100.lex100.controller.entity.sorteoAud.SorteoAudSearch;
import com.base100.lex100.controller.entity.tipoInstancia.TipoInstanciaSearch;
import com.base100.lex100.controller.entity.tipoOficina.TipoOficinaSearch;
import com.base100.lex100.controller.entity.turnoOficina.TurnoOficinaSearch;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.DelitoExpediente;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.ExpedienteEspecial;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.OficinaCargaExp;
import com.base100.lex100.entity.OficinaSorteo;
import com.base100.lex100.entity.RecursoExp;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.SorteoAud;
import com.base100.lex100.entity.TipoBolillero;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoOficina;
import com.base100.lex100.entity.TipoRecurso;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.configuracionMateria.ConfiguracionMateriaManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.mesaEntrada.asignacion.TipoSalaEstudio;
import com.base100.lex100.mesaEntrada.asignacion.TipoSorteo;
import com.base100.lex100.mesaEntrada.conexidad.ConexidadManager;
import com.base100.lex100.mesaEntrada.ingreso.ScreenController;

@Name("mesaSorteo")
@Scope(ScopeType.CONVERSATION)
public class MesaSorteo extends ScreenController{
	public static final String ANY_RUBRO = "*ANYRUBRO";
	public static final String RUBRO_DERECHOS_HUMANOS = "M";
	public static final String RUBRO_LEYES_ESPECIALES = "L";
	
	private static final String ACTUALIZACION_TURNOS_OPERACION = "ACT_TURNOS";
	@Logger
	private Log log;

	 // flags CON DETENIDOS, >5CPOS, >8HECHOS, COD DEFENSOR OFICIAL
    private static String[] rubrosTO={
    		// flags, rubro
    		"1001", "A",
    		"1011", "B",
    		"1101", "C",
    		"1111", "D",
    		"0001", "E",
    		"0011", "F",
    		"0101", "G",
    		"0111", "H",
    		"1000", "I",
    		"1010", "J",
    		"1100", "K",
    		"1110", "L",
    		"0000", "M",
    		"0010", "N",
    		"0100", "O",
    		"0110", "P"
    };
    
//	private Map<String, List<OficinaCargaExp>> OficinaCargaExpPorRubroMap = new HashMap<String, List<OficinaCargaExp>>();
	

	
	public void sorteaJuzgado(List<Expediente> list){
		int i = 0;
		for(Expediente expediente: list){
			boolean ret = sorteaJuzgado(expediente);
			if(!ret){
				break;
			}
			i++;
			log.info("num exp sorteados: "+i);
		}
	}
	
	public boolean sorteaJuzgado(Expediente expediente){
		boolean ret = false;
		if(expediente.getObjetoJuicioList().size()>0){
			log.info("inicio sorteo expediente "+expediente.getClave());
			BolaSorteo bola;
			try {
				Camara camara = SessionState.instance().getGlobalCamara();
				if(camara == null){
					throw new SorteoException("No se ha seleccionado Camara para el sorteo");
				}
				bola = runSorteo(null, null, null, camara, expediente.getObjetoJuicioPrincipal());
				log.info("resultado sorteo: " + bola.getOficina());
				// TODO.............................
				ret = true;
			} catch (SorteoException e) {
				errorSorteo(e);
			}
		}else{
			log.error("Sorteo error, expediente sin Objeto de juicio");
			errorMsg("mesaSorteo.error.emptyObjetoJuicio.");
		}
		return ret;
	}

	
	
//	public BolaSorteo sorteaJuzgado(String codigoSorteo, Camara camara, ObjetoJuicio objetoJuicio){		
//		try {
//			return runSorteo(codigoSorteo, camara, objetoJuicio);
//		} catch (SorteoException e) {
//			errorSorteo(e);
//		}
//		return null;
//	}
	
	private void errorSorteo(SorteoException e) {
		log.error(e.getMessage());
		addStatusMessage(Severity.ERROR, e.getMessage());
	}
	

	public Sorteador createSorteador(Sorteo sorteo, TipoBolillero tipoBolillero, Date aFecha, Camara camara, Materia materia, String rubro, Integer filtroOficinaId){
		EntityManager entityManager = getEntityManager();
		if(sorteo != null){
			sorteo = entityManager.find(Sorteo.class, sorteo.getId());
		}
		if(tipoBolillero != null){
			tipoBolillero = entityManager.find(TipoBolillero.class, tipoBolillero.getId());
		}
		String name = camara.getAbreviatura()+":"+materia.getAbreviatura()+":("+rubro+")";
		if(filtroOficinaId != null){
			name += " of("+filtroOficinaId+")";
		}
		if(tipoBolillero != null){
			name += ":" +tipoBolillero.getCodigo();
		}
		if(sorteo != null){
			name += ":" +sorteo.getCodigo()+ ": rango " + sorteo.getRangoMinimos();
			if(sorteo.getMinimoNumeroBolas() > 1){
				name += " : mínimo "+sorteo.getMinimoNumeroBolas();
			}
		}
		List<BolaSorteo> bolas = createBolas(sorteo, tipoBolillero, aFecha, camara, materia, rubro, filtroOficinaId);		
		Bolillero bolillero = new Bolillero(name, rubro, bolas, sorteo.getRangoMinimos(), sorteo.getMinimoNumeroBolas(), filtroOficinaId != null);
		Sorteador sorteador = new Sorteador(bolillero);
		return sorteador;
	}
	
	
	private BolaSorteo runSorteo(Sorteo sorteo, TipoBolillero tipoBolillero, Date aFecha, Camara camara, ObjetoJuicio objetoJuicio) throws SorteoException{
		Sorteador sorteador = createSorteador(sorteo, tipoBolillero, aFecha, camara, objetoJuicio.getTipoProceso().getMateria(), objetoJuicio.getRubroLetter(), null);
		BolaSorteo bola = sorteador.sorteo(null);// FIXME objetoJuicio.getPeso());
		actualizaCompensacion(bola);
		return bola;
	}

	
	private void actualizaCompensacion(BolaSorteo bola) throws SorteoException {
		if(bola.getOficinaCargaExp().getId() != null){
			OficinaCargaExp oficinaCargaExp =bola.getOficinaCargaExp();
			oficinaCargaExp.setAuditableUsuario("SORTEO");
			getEntityManager().flush();
		}else{
			throw new SorteoException("Compensacion de oficina id = <null>");
		}
		
	}

	private List<BolaSorteo> createBolas(Sorteo sorteo, TipoBolillero tipoBolillero, Date aFecha, Camara camara, Materia materia, String rubroLetter, Integer filtroOficinaId) {
		List<OficinaCargaExp> list  = null;
		
		if(filtroOficinaId != null){
			list = getJuzgadosCargaExpPorOficinaLikeRubro( sorteo, tipoBolillero, aFecha, camara, materia, rubroLetter, filtroOficinaId);
		}else{
			list = getJuzgadosCargaExpPorRubro( sorteo, tipoBolillero, aFecha, camara, materia, rubroLetter);
		}
		
		List<BolaSorteo> bolas = new ArrayList<BolaSorteo>();
		EntityManager entityManager = getEntityManager();
		for(OficinaCargaExp oficinaCargaExp: list){
			oficinaCargaExp = entityManager.find(OficinaCargaExp.class, oficinaCargaExp.getId());
			entityManager.refresh(oficinaCargaExp);
			BolaSorteo bolaSorteo = creaBola(oficinaCargaExp);
			bolas.add(bolaSorteo);
		}
		return bolas;
	}


	private BolaSorteo creaBola(OficinaCargaExp oficinaCargaExp) {
		BolaSorteo bolaSorteo = new BolaSorteo(oficinaCargaExp);
		return bolaSorteo;
	}

	@SuppressWarnings("unchecked")
	@Synchronized
	private List<OficinaCargaExp> getJuzgadosCargaExpPorOficinaLikeRubro(Sorteo sorteo, TipoBolillero tipoBolillero, Date aFecha, Camara camara, Materia materia, String rubro, Integer filtroOficinaId){
		List<OficinaCargaExp> list;
		Query query = getEntityManager().createQuery(
				"from OficinaCargaExp oficinaCargaExp where oficina.camara = :camara and oficina.id = :oficinaId"
				+ " and rubro like '" + rubro +"#%'"
				+ " and sorteo = :sorteo and tipoBolillero = :tipoBolillero and status <> -1");
		query.setParameter("camara", getEntityManager().find(Camara.class, camara.getId()));
		query.setParameter("oficinaId", filtroOficinaId);
		query.setParameter("tipoBolillero", tipoBolillero);
		query.setParameter("sorteo", sorteo);
		list = query.getResultList();
		return list;
	}

	
	@SuppressWarnings("unchecked")
	@Synchronized
	private List<OficinaCargaExp> getJuzgadosCargaExpPorRubro(Sorteo sorteo, TipoBolillero tipoBolillero, Date aFecha, Camara camara, Materia materia, String rubro){
		String key = rubro;
		if(sorteo != null){
			key += "-"+sorteo.getId();
		}
		List<Integer> restrictIdsOficinas = null;
		if(sorteo != null){
			restrictIdsOficinas = getIdsOficinasSorteo(sorteo, aFecha);
		}
		List<OficinaCargaExp> list = null;
		//List<OficinaCargaExp> list = OficinaCargaExpPorRubroMap.get(key);
		if(list == null){
			list = getOficinaCargaExpListByRubro(camara, sorteo, tipoBolillero, rubro);
			if(restrictIdsOficinas != null){
				list = removeOficinasFromList( list, restrictIdsOficinas);
			}
			//OficinaCargaExpPorRubroMap.put(key, list);
		}
		return list;		
	}

	private List<OficinaCargaExp> removeOficinasFromList(List<OficinaCargaExp> list, List<Integer> restrictIdsOficinas) {
		List<OficinaCargaExp> result = new ArrayList<OficinaCargaExp>();
		for(OficinaCargaExp item: list){
			if(restrictIdsOficinas.contains(item.getOficina().getId())){
				result.add(item);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<OficinaCargaExp> getOficinaCargaExpListByRubro(Camara camara,
			Sorteo sorteo, TipoBolillero tipoBolillero, String rubro) {
		List<OficinaCargaExp> list;
        Query query;
        if (sorteo != null) { // No se buca por cámara porque hay sorteos (RECONQUISTA) que sortean entre oficinas de distintas camaras
            query = getEntityManager().createQuery("from OficinaCargaExp oficinaCargaExp where rubro = :rubro and sorteo = :sorteo and tipoBolillero = :tipoBolillero and status <> -1");
            query.setParameter("sorteo", sorteo);
    		query.setParameter("tipoBolillero", tipoBolillero);
            query.setParameter("rubro", rubro);
        } else {	// nunca debería pasa por aqui
            query = getEntityManager().createQuery("from OficinaCargaExp oficinaCargaExp where oficina.camara = :camara and rubro = :rubro and sorteo = :sorteo and tipoBolillero = :tipoBolillero and status <> -1");
            query.setParameter("camara", getEntityManager().find(Camara.class, camara.getId()));
    		query.setParameter("tipoBolillero", tipoBolillero);
            query.setParameter("sorteo", sorteo);
            query.setParameter("rubro", rubro);
        }
		list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	private List<OficinaCargaExp> getOficinaCargaExpListByRubroAndOficina(Camara camara,
			Sorteo sorteoOpcional, TipoBolillero tipoBolillero, String rubro, Integer oficinaId) {
		List<OficinaCargaExp> list;
		
		String stmt = "from OficinaCargaExp oficinaCargaExp where oficina.camara = :camara"+
			" and tipoBolillero = :tipoBolillero" +
			" and rubro = :rubro"+
			" and oficina.id = :oficinaId"+
			" and status <> -1";
		if(sorteoOpcional != null){
			stmt +=	" and sorteo = :sorteo";
		}
		Query query = getEntityManager().createQuery(stmt);
		query.setParameter("camara", getEntityManager().find(Camara.class, camara.getId()));
		query.setParameter("rubro", rubro);
		query.setParameter("oficinaId", oficinaId);
		query.setParameter("tipoBolillero", tipoBolillero);
		if(sorteoOpcional != null){
			query.setParameter("sorteo", sorteoOpcional);
		}
		list = query.getResultList();
		return list;
	}
	
	public List<Oficina> getOficinasSorteoActivas(Sorteo sorteo, Date aFecha) {
		List<Oficina> oficinasActivas = new ArrayList<Oficina>();

		if (aFecha == null) {
			aFecha = CurrentDateManager.instance().getCurrentDate();
		}
		aFecha = DateUtil.clearTime(aFecha);

		if (TurnoSorteoEnumeration.isConTurnoContinuo(sorteo.getConTurno())) {
			for(OficinaSorteo oficinaSorteo: sorteo.getOficinaSorteoList()){
				if (oficinaSorteo.getStatus() != -1) {
					if (TurnoOficinaSearch.isOficinaDeTurno(getEntityManager(), sorteo, oficinaSorteo.getOficina(), aFecha)){
						oficinasActivas.add(oficinaSorteo.getOficina());
					}
				}
			}
		} else {
			boolean isSorteoSinTurnos = TurnoSorteoEnumeration.Type.sinTurno.getValue().equals(sorteo.getConTurno());

			if(TurnoSorteoEnumeration.isConTurnoAutomatico(sorteo.getConTurno())){
				actualizarTurnoAutomatico(sorteo, aFecha, true);
			}
			
			for(OficinaSorteo oficinaSorteo: sorteo.getOficinaSorteoList()){
				if (oficinaSorteo.getStatus() != -1) {
					if(isSorteoSinTurnos || oficinaSorteo.isTurno()){
						oficinasActivas.add(oficinaSorteo.getOficina());
					}
				}
			}
		}
		return oficinasActivas;
	}

	private List<Integer> getIdsOficinasSorteo(Sorteo sorteo, Date aFecha) {
		List<Integer> idOficinas = new ArrayList<Integer>();
		List<Oficina> oficinasActivas = getOficinasSorteoActivas(sorteo, aFecha);

		for(Oficina item: oficinasActivas){
			idOficinas.add(item.getId());
		}
		return idOficinas;
	}

	public boolean actualizarTurnoAutomatico(Sorteo sorteo, Date aFecha, boolean checkLastTime) {
		Boolean changed = false;
		if(TurnoSorteoEnumeration.isConTurnoAutomatico(sorteo.getConTurno())){
			if (aFecha == null) {
				aFecha = CurrentDateManager.instance().getCurrentDate();
			}
			aFecha = DateUtil.clearTime(aFecha);
			if (checkLastTime) {
				Date ultimaActualizacion = getFechaUltimaActualizacionTurnos(sorteo);
				if (ultimaActualizacion != null) {
					ultimaActualizacion = DateUtil.clearTime(ultimaActualizacion);
					if (aFecha.before(DateUtil.addDays(ultimaActualizacion, 1))) {
						return changed; 
					}
				}
			}
			List<OficinaSorteo> oficinasQueMantienenElTurno = new ArrayList<OficinaSorteo>();
			List<OficinaSorteo> oficinasNuevasEnElTurno = new ArrayList<OficinaSorteo>();
			for(OficinaSorteo oficinaSorteo: sorteo.getOficinaSorteoList()){
				if (TurnoOficinaSearch.isOficinaDeTurno(getEntityManager(), sorteo, oficinaSorteo.getOficina(), aFecha)){
					if(oficinaSorteo.isTurno()) {
						oficinasQueMantienenElTurno.add(oficinaSorteo);
					} else {
						oficinasNuevasEnElTurno.add(oficinaSorteo);
						oficinaSorteo.setTurno(true);
						changed = true;
					}
				} else {
					if(oficinaSorteo.isTurno()) {
						oficinaSorteo.setTurno(false);
						changed = true;
					}
				}
			}
			actualizaActualizacionTurnos(sorteo, aFecha);
			getEntityManager().flush();
			if (oficinasNuevasEnElTurno.size() > 0) {
				SorteoHome.instance().meteOficinaEnTurno(sorteo, oficinasNuevasEnElTurno, oficinasQueMantienenElTurno);
				getEntityManager().flush();
			}
		}
		return changed;
	}

	private void actualizaActualizacionTurnos(Sorteo sorteo, Date fecha) {
		SorteoAud sorteoAud = new SorteoAud(sorteo);
		String usuario = SessionState.instance().getUsername();
		if (usuario == null) {
			usuario = "Sorteador";
		}
		sorteoAud.setUsuario(usuario);
		sorteoAud.setFechaModificacion(fecha);
		sorteoAud.setDescripcionModificacion(ACTUALIZACION_TURNOS_OPERACION);
		getEntityManager().persist(sorteoAud);
	}

	private Date getFechaUltimaActualizacionTurnos(Sorteo sorteo) {
		SorteoAud sorteoAud = SorteoAudSearch.findBySorteoOperacion(getEntityManager(), sorteo, ACTUALIZACION_TURNOS_OPERACION);
		if (sorteoAud != null) {
			return sorteoAud.getFechaModificacion();
		}
		return null;
	}
	
	public static MesaSorteo instance(){
		return (MesaSorteo) Component.getInstance(MesaSorteo.class, true);
	}
	
	public List<Sorteo> findSorteosByTipoRadicacion(Camara camara, String codigoSorteo, String tipoSorteo, TipoRadicacionEnumeration.Type tipoRadicacion, Competencia competencia, Oficina oficinaSorteadora, boolean asignacionDirecta){
		TipoInstancia tipoInstancia = null;
		TipoOficina tipoOficina = null;
		TipoOficina tipoOficinaAlternativa = null;
		List<Sorteo> ret = null;
		boolean err = false;
		if(tipoRadicacion == TipoRadicacionEnumeration.Type.juzgado || tipoRadicacion == TipoRadicacionEnumeration.Type.juzgadoPlenario  || tipoRadicacion == TipoRadicacionEnumeration.Type.juzgadoSentencia || tipoRadicacion == TipoRadicacionEnumeration.Type.juzgadoMediacion){
			tipoInstancia = getPrimeraInstancia();
			tipoOficina = getTipoOficinaJuzgado();
			tipoOficinaAlternativa = getTipoOficinaSecretariaJuzgado(); // prueba tambien secretarias
		}else if(TipoRadicacionEnumeration.isAnySala(tipoRadicacion)){
			tipoInstancia = getSegundaInstancia();
			tipoOficina = getTipoOficinaSala();			
			tipoOficinaAlternativa = getTipoOficinaSecretariaJuzgado(); // prueba tambien secretarias
		}else if(tipoRadicacion == TipoRadicacionEnumeration.Type.tribunalOral){
			tipoInstancia = getTribunalOralInstancia();
			tipoOficina = getTipoOficinaTribunalOral();			
		}else if(tipoRadicacion == TipoRadicacionEnumeration.Type.juzgadoEjecucion){
			tipoInstancia = getJuzgadoEjecucionInstancia();
			tipoOficina = getTipoOficinaJuzgadoEjecucion();			
		}else if(tipoRadicacion == TipoRadicacionEnumeration.Type.salaCasacion){
			tipoInstancia = getCasacion();
			tipoOficina = getTipoOficinaSala();			
		}else if(tipoRadicacion == TipoRadicacionEnumeration.Type.corteSuprema){
			tipoInstancia = getCorteSuprema();
			tipoOficina = getTipoOficinaSala();			
			tipoOficinaAlternativa = getTipoOficinaSecretariaJuzgado(); // prueba tambien secretarias
		}else{
			err = true;
		}
		if(!err){
			ret = findSorteosByAnyFilter(tipoRadicacion.getValue().toString(), codigoSorteo, tipoSorteo, camara, tipoInstancia, tipoOficina, tipoOficinaAlternativa, competencia, oficinaSorteadora, asignacionDirecta);
		}
		if(ret == null || ret.size() == 0){
			if(tipoRadicacion == TipoRadicacionEnumeration.Type.juzgadoPlenario){ // 2 intento : plenario -> usa sorteo de juzgado
				return findSorteosByTipoRadicacion(camara, codigoSorteo, tipoSorteo, TipoRadicacionEnumeration.Type.juzgado, competencia, oficinaSorteadora, asignacionDirecta);
			}
			
		}
		return ret;
	}
	
	public Sorteo findSorteoEncadenado(Camara camara, TipoRadicacionEnumeration.Type tipoRadicacion, Competencia competencia, Oficina oficinaSorteadora, int codigoTipoOficina){
		return findSorteoByTipoOficinaAndTipoRadicacion(camara, tipoRadicacion, competencia, oficinaSorteadora, codigoTipoOficina);
	}
	
	// por compatibilidad con ExpedienteMeReasignacion
	public Sorteo findSorteoFiscaliaByTipoRadicacion(Camara camara, TipoRadicacionEnumeration.Type tipoRadicacion, Competencia competencia, Oficina oficinaSorteadora){
		return findSorteoByTipoOficinaAndTipoRadicacion(camara, tipoRadicacion, competencia, oficinaSorteadora, TipoOficinaEnumeration.TIPO_OFICINA_FISCALIA); 
	}
	
	public Sorteo findSorteoByTipoOficinaAndTipoRadicacion(Camara camara, TipoRadicacionEnumeration.Type tipoRadicacion, Competencia competencia, Oficina oficinaSorteadora, int codigoTipoOficina){
		Sorteo ret = null;
		if(camaraHasSorteoFiscalias(camara, tipoRadicacion)){
			TipoInstancia tipoInstancia = null;
			TipoOficina tipoOficina = null;
//			TipoOficina tipoOficinaAlternativa = null;
			boolean err = false;
			tipoOficina = getTipoOficinaByCodigo(codigoTipoOficina);
			if(tipoRadicacion == TipoRadicacionEnumeration.Type.juzgado){
				tipoInstancia = getPrimeraInstancia();
			}else if(tipoRadicacion == TipoRadicacionEnumeration.Type.sala){
				tipoInstancia = getSegundaInstancia();
			}else if(tipoRadicacion == TipoRadicacionEnumeration.Type.tribunalOral){
				tipoInstancia = getTribunalOralInstancia();
			}else{
				err = true;
			}
			if(!err){
				List<Sorteo> list = findSorteosByAnyFilter(tipoRadicacion.getValue().toString(), null, null, camara, tipoInstancia, tipoOficina, null, competencia, oficinaSorteadora, false);
				if(list != null && list.size() > 0){
					ret = list.get(0);
				}
			}
		}
		return ret;		
	}
	
	private boolean camaraHasSorteoMediacion(Camara camara) {
		return ConfiguracionMateriaManager.instance().isCamaraUsaMediacion(camara);
	}

	private boolean camaraHasSorteoFiscalias(Camara camara,	Type tipoRadicacion) {
		boolean defautValue = CamaraManager.isCamaraPenalFederal(camara) || CamaraManager.isCamaraPenalEconomico(camara); // @FIXME hasta tener bien la configuracion de las camaras
		return  ConfiguracionMateriaManager.instance().isSorteaFiscaliasODefensorias(camara, defautValue);
	}

	private TipoInstancia getPrimeraInstancia() {
		return TipoInstanciaSearch.findByAbreviatura(getEntityManager(), TipoInstanciaEnumeration.TIPO_INSTANCIA_PRIMERA_INSTANCIA_ABREVIATURA);
	}

	private TipoInstancia getSegundaInstancia() {
		return TipoInstanciaSearch.findByAbreviatura(getEntityManager(), TipoInstanciaEnumeration.TIPO_INSTANCIA_APELACIONES_ABREVIATURA);
	}

	private TipoInstancia getCasacion() {
		return TipoInstanciaSearch.findByAbreviatura(getEntityManager(), TipoInstanciaEnumeration.TIPO_INSTANCIA_CASACION_ABREVIATURA);
	}
	
	private TipoInstancia getCorteSuprema() {
		return TipoInstanciaSearch.findByAbreviatura(getEntityManager(), TipoInstanciaEnumeration.TIPO_INSTANCIA_CORTESUPREMA_ABREVIATURA);
	}
	
	private TipoInstancia getTribunalOralInstancia() {
		return TipoInstanciaSearch.findByAbreviatura(getEntityManager(), TipoInstanciaEnumeration.TIPO_INSTANCIA_TRIBUNAL_ORAL_ABREVIATURA);
	}
	
	private TipoInstancia getJuzgadoEjecucionInstancia() {
		return TipoInstanciaSearch.findByAbreviatura(getEntityManager(), TipoInstanciaEnumeration.TIPO_INSTANCIA_EJECUCION_ABREVIATURA);
	}
	
	private TipoOficina getTipoOficinaJuzgado() {
		return TipoOficinaSearch.findByCodigo(getEntityManager(), TipoOficinaEnumeration.TIPO_OFICINA_JUZGADO);
	}

	private TipoOficina getTipoOficinaSecretariaJuzgado() {
		return TipoOficinaSearch.findByCodigo(getEntityManager(), TipoOficinaEnumeration.TIPO_OFICINA_SECRETARIA_JUZGADO);
	}

	private TipoOficina getTipoOficinaSala() {
		return TipoOficinaSearch.findByCodigo(getEntityManager(), TipoOficinaEnumeration.TIPO_OFICINA_SALA_APELACIONES);
	}

	private TipoOficina getTipoOficinaTribunalOral() {
		return TipoOficinaSearch.findByCodigo(getEntityManager(), TipoOficinaEnumeration.TIPO_OFICINA_TRIBUNAL_ORAL);
	}
	
	private TipoOficina getTipoOficinaJuzgadoEjecucion() {
		return TipoOficinaSearch.findByCodigo(getEntityManager(), TipoOficinaEnumeration.TIPO_OFICINA_JUZGADO);
	}
	private TipoOficina getTipoOficinaVocalia() {
		return TipoOficinaSearch.findByCodigo(getEntityManager(), TipoOficinaEnumeration.TIPO_OFICINA_VOCALIA);
	}
	
	private TipoOficina getTipoOficinaFiscalia() {
		return TipoOficinaSearch.findByCodigo(getEntityManager(), TipoOficinaEnumeration.TIPO_OFICINA_FISCALIA);
	}
	
	private TipoOficina getTipoOficinaMediacion() {
		return TipoOficinaSearch.findByCodigo(getEntityManager(), TipoOficinaEnumeration.TIPO_OFICINA_MEDIACION);
	}

	private TipoOficina getTipoOficinaByCodigo(int codigo) {
		return TipoOficinaSearch.findByCodigo(getEntityManager(), codigo);
	}

	public List<Sorteo> findSorteosByAnyFilter(String tipoRadicacion, String codigoSorteo, String tipoSorteo, Camara camara,
			TipoInstancia tipoInstancia,
			TipoOficina tipoOficina, TipoOficina tipoOficinaAlternativa, 
			Competencia competencia,
			Oficina oficinaSorteadora, boolean asignacionDirecta) {
		
		List<Sorteo> ret = null;
		StringBuffer sb = new StringBuffer();
		Oficina oficinaPrincipal = null;
		
		if(tipoSorteo == null){
			addCond(sb, "tipoSorteo is null");
		}else{
			addCond(sb, "tipoSorteo = :tipoSorteo");
		}
		if(oficinaSorteadora != null){
			oficinaPrincipal = CamaraManager.instance().getOficinaPrincipal(oficinaSorteadora);
		}
		
		if(tipoRadicacion != null){
			addCond(sb, "(tipoRadicacion = :tipoRadicacion or tipoRadicacion is null)");
		}
		if(codigoSorteo != null){
			addCond(sb, "codigo = :codigoSorteo");
		}
		if(camara != null){
			addCond(sb, "camara.id = :camaraId");
		}
		if(tipoInstancia != null){
			addCond(sb, "tipoInstancia.id = :tipoInstanciaId");
		}
		if(tipoOficina != null){
			if(tipoOficinaAlternativa == null){
				addCond(sb, "tipoOficina.id = :tipoOficinaId");
			}else{
				addCond(sb, "(tipoOficina.id = :tipoOficinaId or tipoOficina.id = :tipoOficinaAlternativaId)");
			}
		}
		if(competencia != null){
			addCond(sb, "(competencia.id = :competenciaId or competencia is null)");
		}
		
		if(oficinaSorteadora != null){
			if(!asignacionDirecta){
				String cond = getCondZonaSorteo(oficinaSorteadora);
				if(cond != null){
					addCond(sb,"("+cond+")");
				}
			}else if(asignacionDirecta && oficinaSorteadora.getTipoInstancia() != null){
				String cond = getContainsOficinaCond(oficinaSorteadora, oficinaPrincipal);
				if(cond != null){
					addCond(sb,cond);
				}
			}
		}
		if(sb.length() != 0){ // Sin condiciones -> null
			Query query = getEntityManager().createQuery("from Sorteo sorteoRecord where status <> -1 and "+sb.toString());
			if(tipoSorteo != null){
				query.setParameter("tipoSorteo", tipoSorteo);
			}
			if(tipoRadicacion != null){
				query.setParameter("tipoRadicacion", tipoRadicacion);
			}
			if(codigoSorteo != null){
				query.setParameter("codigoSorteo", codigoSorteo);
			}
			if(camara != null){
				query.setParameter("camaraId", camara.getId());
			}
			if(tipoInstancia != null){
				query.setParameter("tipoInstanciaId", tipoInstancia.getId());
			}
			if(tipoOficina != null){
				query.setParameter("tipoOficinaId", tipoOficina.getId());
				if(tipoOficinaAlternativa != null){
					query.setParameter("tipoOficinaAlternativaId", tipoOficinaAlternativa.getId());
				}
			}
			if(competencia != null){
				query.setParameter("competenciaId", competencia.getId());
			}
			
			if(oficinaSorteadora != null){
				if(!asignacionDirecta){
					setParametersZonaSorteo(query, oficinaSorteadora);
				}else if(asignacionDirecta && oficinaSorteadora.getTipoInstancia() != null){
					setParametersContainsOficina(query, oficinaSorteadora, oficinaPrincipal);
				}
			}
			
			ret = query.getResultList();
		}
		return ret;
	}

	
	public List<Sorteo> findSorteosByOficina(Oficina oficina){		
		List<Sorteo> ret = null;		
		Query query = getEntityManager().createQuery(
				"from Sorteo sorteo where status <> -1 "+
				"and 0 < (select count(*) from OficinaSorteo oficinaSorteo where status <> -1 and oficinaSorteo.sorteo.id = sorteo.id and oficina = :oficina)");
		query.setParameter("oficina", oficina);
		ret = query.getResultList();
		return ret;
	}
	
	private void setParametersContainsOficina(Query query,	Oficina oficinaSorteadora, Oficina oficinaPrincipal) {
		query.setParameter("oficina", oficinaSorteadora);	
		if(oficinaPrincipal != null && oficinaPrincipal.getId() != oficinaSorteadora.getId()){
			query.setParameter("oficinaPrincipal", oficinaPrincipal);
		}		
	}

	private String getContainsOficinaCond(Oficina oficinaSorteadora, Oficina oficinaPrincipal) {
		if(oficinaPrincipal.getId() != oficinaSorteadora.getId()){
			return "0 < (select count(*) from OficinaSorteo where oficina in (:oficina, :oficinaPrincipal) and sorteo = sorteoRecord)";
		}else{
			return "0 < (select count(*) from OficinaSorteo where oficina = :oficina and sorteo = sorteoRecord)";
		}
	}
	
	

	private void setParametersZonaSorteo(Query query, Oficina oficina) {
		String zona = oficina.getZonaOficina();
		String codigo = oficina.getCodigo();
		if(!Strings.isEmpty(zona)){
			query.setParameter("zona", zona);
		}
		if(!Strings.isEmpty(codigo)){
			query.setParameter("codigoOficina", codigo);			
		}
	}

	private String getCondZonaSorteo(Oficina oficina) {
		String zona = oficina.getZonaOficina();
		String codigo = oficina.getCodigo();
		if(!Strings.isEmpty(zona) && !Strings.isEmpty(codigo)){
			return "codigoMesa = :zona or codigoMesa = :codigoOficina or codigoMesa is null";
		}else if(!Strings.isEmpty(zona)){
			return "codigoMesa = :zona or codigoMesa is null";
		}else if(!Strings.isEmpty(codigo)){
			return "codigoMesa = :codigoOficina or codigoMesa is null";
		}else{
			return null;
		}
	}


	private void addCond(StringBuffer sb, String cond) {
		if(sb.length() > 0){
			sb.append(" and ");
		}
		sb.append(cond);
	}
	
	private EntityManager getEntityManager() {
		return (EntityManager) Component.getInstance("entityManager");
	}

//	public void enviarAColaSeSorteo(Sorteo sorteo, Oficina oficinaSorteadora, Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion, boolean reasignacion, String codigoTipoCamboAsignacion, TipoGiroEnumeration.Type tipoGiro, List<Integer> excludeOficinasIds, boolean useRubro2, String rubroCalculado, String observaciones) {	
//		Camara camara = SessionState.instance().getGlobalCamara();
//		
//		SorteoParametros sorteoParametros = new SorteoParametros(SorteoOperation.sorteo, camara, expediente.getMateria(), sorteo, tipoRadicacion, tipoGiro, oficinaSorteadora, codigoTipoCamboAsignacion, observaciones, expediente);		
//		sorteoParametros.setExcludeOficinasIds(excludeOficinasIds);
//		sorteoParametros.setUseRubro2(useRubro2);
//		sorteoParametros.setRubroCalculado(rubroCalculado);
//		sorteoParametros.setUsuario(SessionState.instance().getUsername());
//		
//		SorteoManager.instance().runTransaction(sorteoParametros);
//	}

	
	public SorteoParametros createSorteoParametros(Sorteo sorteo, 
			TipoBolillero tipoBolillero,
			Oficina oficinaSorteadora, 
			Expediente expediente, 
			RecursoExp recursoExp,
			TipoRadicacionEnumeration.Type tipoRadicacion, 
			String codigoTipoCambioAsignacion, 
			String descripcionCambioAsignacion,
			TipoGiroEnumeration.Type tipoGiro, 
			List<Integer> excludeOficinasIds, 
			String rubro,
			Date fechaTurnosActivos,
			boolean descompensarAnterior,
			String observaciones,
			boolean addSorteosEncadenados){
		
		Camara camara = SessionState.instance().getGlobalCamara();

		SorteoParametros sorteoParametros = new SorteoParametros(SorteoOperation.sorteo,
				camara, 
				expediente.getMateria(), 
				sorteo,
				tipoBolillero,
				rubro,
				tipoRadicacion, 
				tipoGiro, 
				oficinaSorteadora, 
				codigoTipoCambioAsignacion, 
				descripcionCambioAsignacion, 
				observaciones,
				fechaTurnosActivos,
				descompensarAnterior,
				expediente);		
		sorteoParametros.setExcludeOficinasIds(excludeOficinasIds);
		sorteoParametros.setUsuario(SessionState.instance().getUsername());
		sorteoParametros.setRecursoExp(recursoExp);

		sorteoParametros.setRemisionPorUsuario(SessionState.instance().isRemisionesPorUsuario());

		if(addSorteosEncadenados && isExpedienteMediacion(sorteoParametros.getExpediente())){
			sorteoParametros.setSorteaMediador(true);
		}
		
		if(addSorteosEncadenados) {
			SorteoParametros siguienteSorteoParametros = findSorteosEncadenado(sorteoParametros);
			if(siguienteSorteoParametros != null){
				sorteoParametros.setSiguienteSorteoEncadenado(siguienteSorteoParametros);
			}
		}
		return sorteoParametros;
	}

	public SorteoParametros createSorteoParametros(SorteoParametros fromSorteo, boolean isSorteoSecundario){		
		return new SorteoParametros(fromSorteo, isSorteoSecundario);
	}

	public void enviarAColaDeSorteo(SorteoParametros sorteoParametros){
		if (sorteoParametros.isBuscaConexidad()) {
			ConexidadManager.instance().runBusquedaConexidad(sorteoParametros);
		} else {
			SorteoManager.instance().runTransaction(sorteoParametros);
		}
	}
		

	private SorteoParametros findSorteosEncadenado(SorteoParametros sorteoParametros) {
		SorteoParametros ret = null;
		SorteoParametros first = null;
		
		ret = findSorteoEncadenado(sorteoParametros, TipoOficinaEnumeration.TIPO_OFICINA_DEFENSORIA);
		if(ret != null){
			if(first != null){
				ret.setSiguienteSorteoEncadenado(first);
			}
			first = ret;
		}

		ret = findSorteoEncadenado(sorteoParametros, TipoOficinaEnumeration.TIPO_OFICINA_FISCALIA);
		if(ret != null){
			if(first != null){
				ret.setSiguienteSorteoEncadenado(first);
			}
			first = ret;
		}
		
		return ret;

	}
	
	private SorteoParametros findSorteoEncadenado(SorteoParametros sorteoParametros, Integer codigoTipoOficina) {
		SorteoParametros ret = null;
		Sorteo sorteo = findSorteoEncadenado(
				sorteoParametros.getCamara(), 
				sorteoParametros.getTipoRadicacion(),
				ExpedienteManager.getCompetencia(sorteoParametros.getExpediente()), 
				sorteoParametros.getOficinaSorteo(),
				codigoTipoOficina);
		if(sorteo != null){
			ret = createSorteoParametros(sorteoParametros, true);
			ret.setSorteo(sorteo);
		}
		return ret;
	}

	private boolean isExpedienteMediacion(Expediente expediente) {
		return TipoCausaEnumeration.isMediacion(expediente.getTipoCausa());
	}


/*	public void enviarAColaSeSorteo(Sorteo sorteo, 
			Oficina oficinaSorteadora, 
			Expediente expediente, 
			TipoRadicacionEnumeration.Type tipoRadicacion, 
			boolean reasignacion, 
			String codigoTipoCamboAsignacion, 
			TipoGiroEnumeration.Type tipoGiro, 
			List<Integer> excludeOficinasIds, 
			boolean useRubro2, 
			String rubroCalculado, 
			String observaciones) {	
		Camara camara = SessionState.instance().getGlobalCamara();
//		Competencia competencia = getCompetencia(expediente);
		
//		Sorteo sorteo = tryFindSorteo(tipoRadicacion, camara, competencia, oficinaSorteadora);
		
		SorteoParametros sorteoParametros = new SorteoParametros(SorteoOperation.sorteo, camara, expediente.getMateria(), sorteo, tipoRadicacion, tipoGiro, oficinaSorteadora, codigoTipoCamboAsignacion, observaciones, expediente);		
		sorteoParametros.setExcludeOficinasIds(excludeOficinasIds);
		sorteoParametros.setUseRubro2(useRubro2);
		sorteoParametros.setRubroCalculado(rubroCalculado);
		sorteoParametros.setUsuario(SessionState.instance().getUsername());
		
		// sorteo encadenado
		Competencia competencia = getCompetencia(expediente);
		Sorteo sorteoEncadenado = findSorteoEncadenadoByTipoRadicacion(camara, tipoRadicacion, competencia, reasignacion, oficinaSorteadora);
		if(sorteoEncadenado != null){
			SorteoParametros siguienteSorteoParametros = new SorteoParametros(SorteoOperation.sorteo, camara, expediente.getMateria(), sorteoEncadenado, tipoRadicacion, tipoGiro, oficinaSorteadora, codigoTipoCamboAsignacion, observaciones, expediente);
			sorteoParametros.setSiguienteSorteoEncadenado(siguienteSorteoParametros);
		}
		
		SorteoManager.instance().runTransaction(sorteoParametros);
	}
*/
			
	// version antigua
//	public Sorteo tryFindSorteo(TipoRadicacionEnumeration.Type tipoRadicacion, Camara camara, Competencia competencia, Oficina oficinaSorteadora) {
//		Sorteo ret = null;
//		List<Sorteo> list = findSorteosByTipoRadicacion(camara, tipoRadicacion, competencia, oficinaSorteadora);
//		if(list != null && list.size() > 0){
//			ret = list.get(0);
//		}
//		return ret;
//	}

	private String getRubro(Expediente expediente) {
		if(expediente.getTipoCausa() != null && expediente.getTipoCausa().getRubro() != null){
			return expediente.getTipoCausa().getRubro();
		}else{
			return expediente.getObjetoJuicioPrincipal().getRubroLetter();
		}	
	}
	
	private String getRubroAsignacion(Expediente expediente) {
		if(expediente.getTipoCausa() != null && expediente.getTipoCausa().getRubroAsignacion() != null){
			return expediente.getTipoCausa().getRubroAsignacion();
		}else{
			return expediente.getObjetoJuicioPrincipal().getRubroAsignacion();
		}	
	}
	
	public Sorteo buscaSorteo(Expediente expediente, 
			TipoRadicacionEnumeration.Type tipoRadicacion, 
			Oficina oficinaSorteadora,
			boolean asignacionDirecta,
			Competencia competencia,
			String rubroAsignacion,
			String rubro,
			boolean required
			) throws Lex100Exception {
		//Materia materia = expediente.getMateria();
		// la materia del expediente no condiciona su sorteo, cuando sortea puede cambiar de materia y de competencia
		Materia materia = null; 
		String grupo = null;
		Sorteo sorteo = buscaSorteo(oficinaSorteadora, asignacionDirecta, tipoRadicacion, grupo, materia, competencia, rubroAsignacion, rubro, false);
		if(sorteo == null){ // intenta con materia
			materia = expediente.getMateria(); 
			grupo = materia.getGrupo();
			sorteo = buscaSorteo(oficinaSorteadora, asignacionDirecta, tipoRadicacion, grupo, materia, competencia, rubroAsignacion, rubro, required);
	}
		return sorteo;
	}
	
	public Sorteo buscaSorteoOrdenCirculacion(Oficina oficinaSorteadora, TipoRadicacionEnumeration.Type tipoRadicacion) throws Lex100Exception{
		return buscaSorteo(null, TipoSorteo.ordenCirculacionSala.toString(), oficinaSorteadora, false, tipoRadicacion, null, null, null, null, null, false, false);
	}


	public Sorteo buscaSorteo(String codigoSorteo, String tipoSorteo, Oficina oficinaSorteadora,
			boolean asignacionDirecta,
			TipoRadicacionEnumeration.Type tipoRadicacion,
			String grupoMateria,
			Materia materia,
			Competencia competencia,
			String rubroAsignacion,
			String rubro,
			boolean required,
			boolean rubroRequired) throws Lex100Exception{
		
		Camara camara = SessionState.instance().getGlobalCamara();
		if(rubro == null && rubroRequired){
			errorMsg("expedienteMeAsignacion.error.sorteoNotMatch");
			throw new Lex100Exception("Error sorteo no encontrado para el expediente");			
		}
		
		List<Sorteo> sorteos = buscaSorteosValidos(camara, codigoSorteo, tipoSorteo, oficinaSorteadora, asignacionDirecta, tipoRadicacion, grupoMateria, materia, competencia, rubroAsignacion, rubro, true);
		
		if(sorteos != null && sorteos.size() == 1){
			return sorteos.get(0);
		}else if(!required){
			return null;
		}else if(sorteos == null || sorteos.size() == 0){
			errorMsg("expedienteMeAsignacion.error.sorteoNotMatch");
			throw new Lex100Exception("Error sorteo no encontrado para el expediente");
		}else{
			errorMsg("expedienteMeAsignacion.error.sorteoAmbiguo"); 	
			throw new Lex100Exception("Error sorteo ambiguo para el expediente");
		}
	}
	
	public Sorteo buscaSorteo(Oficina oficinaSorteadora,
			boolean asignacionDirecta,
			TipoRadicacionEnumeration.Type tipoRadicacion,
			String grupoMateria,
			Materia materia,
			Competencia competencia,
			String rubroAsignacion,
			String rubro,
			boolean required) throws Lex100Exception{
		
		Camara camara = SessionState.instance().getGlobalCamara();
		
		if(TipoRadicacionEnumeration.Type.perito.equals(tipoRadicacion)) {
			camara = CamaraSearch.findByAbreviatura(getEntityManager(), CamaraManager.ABREVIATURA_CAMARA_SEGURIDAD_SOCIAL);
		}
		
		if(rubro == null && required){
			errorMsg("expedienteMeAsignacion.error.sorteoNotMatch");
			throw new Lex100Exception("Error sorteo no encontrado para el expediente");			
		}
		if(ANY_RUBRO.equals(rubro)){
			rubro = null;
		}
		if(ANY_RUBRO.equals(rubroAsignacion)){
			rubroAsignacion = null;
		}
		List<Sorteo> sorteos = buscaSorteosValidos(camara, null, null, oficinaSorteadora, asignacionDirecta, tipoRadicacion, grupoMateria, materia, competencia, rubroAsignacion, rubro, true);
		
		if(sorteos != null && sorteos.size() == 1){
			return sorteos.get(0);
		}else if(!required){
			return null;
		}else if(sorteos == null || sorteos.size() == 0){
			errorMsg("expedienteMeAsignacion.error.sorteoNotMatch");
			throw new Lex100Exception("Error sorteo no encontrado para el expediente");
		}else{
			errorMsg("expedienteMeAsignacion.error.sorteoAmbiguo"); 	
			throw new Lex100Exception("Error sorteo ambiguo para el expediente");
		}
	}
	

	public List<Sorteo> buscaSorteosValidos(
			Camara camara,
			String codigoSorteo,
			String tipoSorteo,
			Oficina oficinaSorteadora, boolean asignacionDirecta,
			TipoRadicacionEnumeration.Type tipoRadicacion,
			String grupoMateria,
			Materia materia,
			Competencia competencia,
			String rubroAsgnacion,
			String rubro,
			boolean seleccionaMayorPrioridad) {
		
		Sorteo ret = null;
		
		List<Sorteo> list = findSorteosByTipoRadicacion(camara, codigoSorteo, tipoSorteo, tipoRadicacion, competencia, oficinaSorteadora, asignacionDirecta);
		list = seleccionaSorteosValido(list, codigoSorteo, grupoMateria, materia, competencia, rubroAsgnacion, rubro, seleccionaMayorPrioridad);
		return list;
	}
	

	private List<Sorteo> seleccionaSorteosValido(List<Sorteo> list, String codigoSorteo, String grupoMateria, Materia materia,
			Competencia competencia, String rubroAsgnacion, String rubro, boolean seleccionaMayorPrioridad) {
		
		if(list != null && list.size() > 0){
			int prioridadAterior = 0;
			List<Sorteo> validos = new ArrayList<Sorteo>();
			for(Sorteo sorteo: list){
				if(codigoSorteo != null && !codigoSorteo.equals(sorteo.getCodigo())){
					continue;
				}
				if(grupoMateria != null && sorteo.getGrupoMateria() != null && !sorteo.getGrupoMateria().equals(grupoMateria)){
					continue; // no vale
				}
				if(materia != null && sorteo.getMateria() != null && !sorteo.getMateria().getId().equals(materia.getId())){
					continue; // no vale
				}
				if(competencia != null && sorteo.getCompetencia() != null && competencia != null && !sorteo.getCompetencia().getId().equals(competencia.getId())){
					continue; // no vale
				}
				if(rubroAsgnacion != null && !Strings.isEmpty(sorteo.getRubrosAsignacion()) && !containsRubro(sorteo.getRubrosAsignacion(), rubroAsgnacion)){
					continue; // no vale
				}
				if(rubro != null && !Strings.isEmpty(sorteo.getRubros()) && !containsRubro(sorteo.getRubros(), rubro)){
					continue; // no vale
				}
				if(seleccionaMayorPrioridad){
					int prioridad = calculaPrioridad(sorteo);
					if(prioridad >= prioridadAterior){
						if(prioridad > prioridadAterior){
							validos.clear();
							prioridadAterior = prioridad;
						}
						validos.add(sorteo);
					}
				}else{
					validos.add(sorteo);
				}
			}
			list = validos;
		}
		return list;
	}

	private int calculaPrioridad(Sorteo sorteo) {
		int prioridad = 0;
		if(sorteo.getGrupoMateria() != null){
			prioridad += 10;
		}
		if(sorteo.getMateria() != null){
			prioridad += 100;
		}
		if(sorteo.getCompetencia() != null){
			prioridad += 1000;
		}
		if(sorteo.getCodigoMesa() != null){
			prioridad += 5000;
		}
		if(!Strings.isEmpty(sorteo.getRubrosAsignacion())){
			prioridad += 10000;
		}
		return prioridad;
		
	}

	private boolean containsRubro(String rubrosList, String rubro) {
		String[] rubros = rubrosList.split(",");
		for(String item: rubros){
			if(item != null && item.trim().equals(rubro)){
				return true;
			}
		}
		return false;
	}


	public static RubrosInfo calculaRubroSalaByTipoRadicacion(TipoRadicacionEnumeration.Type tipoRadicacion){
		String rubro = null;
		if(TipoRadicacionEnumeration.Type.salaEstudio == tipoRadicacion){
			rubro = "SE";
		}		
		if(rubro != null){
			RubrosInfo rubrosInfo = new RubrosInfo();
			rubrosInfo.setRubro(rubro);
			return rubrosInfo;			
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public RubrosInfo calculaRubrosSala(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion, TipoSalaEstudio tipoSalaEstudio, boolean reasignacion){
		int regimenProcesal = 0;
		RubrosInfo ret = calculaRubroSalaByTipoRadicacion(tipoRadicacion);
		if(ret != null){
			return ret;
		}else{
			if(MateriaEnumeration.isAnyPenal(expediente.getMateria()) && (expediente.getExpedienteEspecial() != null)) {
				regimenProcesal = expediente.getExpedienteEspecial().getRegimenProcesal();
			}
			boolean detenidos = false;
		
			if(MateriaEnumeration.isAnyPenal(expediente.getMateria())) {
				if(expediente.getExpedienteEspecial() != null){
					detenidos = Boolean.TRUE.equals(expediente.getExpedienteEspecial().isDetenidos());
				}
			}
			return calculaRubrosSala(expediente, detenidos, regimenProcesal, reasignacion);
		}
	}

	@SuppressWarnings("unchecked")
	public RubrosInfo calculaRubrosSala(Expediente expediente, boolean detenidos, int regimenProcesal, boolean reasignacion){
		
		TipoRecurso tipoRecurso = null;
		
		RecursoExp recursoExp = null;
		if(reasignacion){
			recursoExp = expediente.getUltimoRecursoSalaAsignadoPteResolver();
		}
		if(recursoExp == null){
			recursoExp = expediente.getUltimoRecursoSalaPteAsignar();
		}		
		tipoRecurso = recursoExp == null ? null : recursoExp.getTipoRecurso();
		
		RubrosInfo result;
		if(MateriaEnumeration.isAnyPenal(expediente.getMateria())){
			result = calculaRubrosSalaPenal(tipoRecurso, detenidos, regimenProcesal);
			if (ConfiguracionMateriaManager.instance().isAsignaDerechosHumanos(expediente.getOficinaActual().getCamara()) && 
					expediente.getExpedienteEspecial() != null && expediente.getExpedienteEspecial().isDerechosHumanos()) {
				result.setRubro(RUBRO_DERECHOS_HUMANOS);
				result.setRubroAsignacion(null);
			} else if (ConfiguracionMateriaManager.instance().isAdmiteLeyesEspeciales(expediente.getOficinaActual().getCamara()) &&
					ExpedienteManager.instance().isPenal(expediente)) {
				if (expediente.getTipoCausa() != null && MesaSorteo.RUBRO_LEYES_ESPECIALES.equals(expediente.getTipoCausa().getRubro())) {
					result.setRubro(expediente.getTipoCausa().getRubro());
				} else {
					List<Integer> leyesEspecialesList =  ConfiguracionMateriaManager.instance().getLeyesEspeciales(expediente.getOficinaActual().getCamara());
					for (DelitoExpediente delitoExpediente : expediente.getDelitoExpedienteList()) {
						if (delitoExpediente.getDelito() != null && leyesEspecialesList.contains(delitoExpediente.getDelito().getLey())) {
							result.setRubro(RUBRO_LEYES_ESPECIALES);
							result.setRubroAsignacion(null);
						}
					}
				}
			}
		}else{
			result = calculaRubrosSalaCivil(expediente, tipoRecurso);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public RubrosInfo calculaRubrosSalaCasacion(Expediente expediente, boolean reasignacion){
		
		TipoRecurso tipoRecurso = null;
		
		RecursoExp recursoExp = null;
		if(reasignacion){
			recursoExp = expediente.getUltimoRecursoCasacionAsignadoPteResolver();
		}
		if(recursoExp == null){
			recursoExp = expediente.getUltimoRecursoCasacionPteAsignar();
		}		
		tipoRecurso = recursoExp == null ? null : recursoExp.getTipoRecurso();
		
		RubrosInfo result = calculaRubrosSalaPenal(tipoRecurso, false, 0);
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public RubrosInfo calculaRubrosCorteSuprema(Expediente expediente, boolean reasignacion){
		
		TipoRecurso tipoRecurso = null;
		
		RecursoExp recursoExp = null;
		if(reasignacion){
			recursoExp = expediente.getUltimoRecursoCorteAsignadoPteResolver();
		}
		if(recursoExp == null){
			recursoExp = expediente.getUltimoRecursoCortePteAsignar();
		}		
		tipoRecurso = recursoExp == null ? null : recursoExp.getTipoRecurso();
		
		RubrosInfo result;
		if (ExpedienteManager.instance().isPenal(expediente)) {
			result = calculaRubrosSalaPenal(tipoRecurso, false, 0);
		} else {
			result = calculaRubrosSalaCivil(tipoRecurso);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public RubrosInfo calculaRubrosSalaPenal(TipoRecurso tipoRecurso, boolean detenidos, int regimenProcesal){
		RubrosInfo rubrosInfo = new RubrosInfo();
		String rubro = null;
		String rubroAsignacion = null;

		boolean useRubro2 = false;
		
		useRubro2 = !detenidos;
		if(RegimenProcesalEnumeration.Type.ley_2372.getValue().equals(regimenProcesal)) {
			rubro = "D";
		}
		if ((rubro == null) && (tipoRecurso != null)){
			rubro = useRubro2 && (tipoRecurso.getRubro2() != null) ? tipoRecurso.getRubro2() : tipoRecurso.getRubro();
			rubroAsignacion = tipoRecurso.getRubroAsignacion();
		}
		rubrosInfo.setRubro(rubro);
		rubrosInfo.setRubroAsignacion(rubroAsignacion);
		
		if(rubrosInfo.getRubro() == null){
			if (!useRubro2) {
				rubrosInfo.setRubro("A");
			} else {
				rubrosInfo.setRubro("B");
			}
		}

		return rubrosInfo;
	}

	public RubrosInfo calculaRubrosSalaCivil(TipoRecurso tipoRecurso){
		RubrosInfo rubrosInfo = new RubrosInfo();
		String rubro = null;
		String rubroAsignacion = null;

		boolean useRubro2 = false;
		
		if ((rubro == null) && (tipoRecurso != null)){
			rubro = useRubro2 && (tipoRecurso.getRubro2() != null) ? tipoRecurso.getRubro2() : tipoRecurso.getRubro();
			rubroAsignacion = tipoRecurso.getRubroAsignacion();
		}
		rubrosInfo.setRubro(rubro);
		rubrosInfo.setRubroAsignacion(rubroAsignacion);
		
		return rubrosInfo;		
	}
	
	public RubrosInfo calculaRubrosSalaCivil(Expediente expediente, TipoRecurso tipoRecurso){
		RubrosInfo rubrosInfo = calculaRubrosSalaCivil(tipoRecurso);
		if (rubrosInfo.getRubro() == null){ // Rubro Libre -> se usa el del OJ.
			if(expediente.getObjetoJuicioPrincipal() != null){
				String rubro = null;
				rubro = expediente.getObjetoJuicioPrincipal().getRubroLetter2();
				if (!Strings.isEmpty(rubro)) {
					rubrosInfo = new RubrosInfo();
					rubrosInfo.setRubro(rubro);
				}
			}
			if (rubrosInfo.getRubro() == null){ // Rubro Libre -> se usa el del OJ.
				rubrosInfo = calculaRubrosJuzgado(expediente);
			}
		}
		
		return rubrosInfo;
	}

	public RubrosInfo calculaRubrosTO(Expediente expediente) {
		ExpedienteEspecial expedienteEspecial = expediente.getExpedienteEspecial();
		boolean isDetenidos = false;
		Integer hechos = null;
		Integer cuerpos = expediente.getCuerpos();
		boolean isDefensorOficial = false;
		if(expedienteEspecial != null){
			isDetenidos = Boolean.TRUE.equals(expedienteEspecial.isDetenidos());
			hechos = expedienteEspecial.getHechos();
			isDefensorOficial = expedienteEspecial.isDefensorOficial();
		}
		return calculaRubrosTO(isDetenidos, hechos, cuerpos, isDefensorOficial);
	}

	public RubrosInfo calculaRubrosTO(boolean isDetenidos, Integer hechos, Integer cuerpos, boolean isDefensorOficial) {
		RubrosInfo rubrosInfo = new RubrosInfo();
		String rubro = null;

		StringBuffer sb = new StringBuffer();
		sb.append(isDetenidos ? "1" : "0");
		sb.append(cuerpos != null && cuerpos > 5 ? "1" : "0");
		sb.append(hechos != null && hechos > 8 ? "1" : "0");
		sb.append(isDefensorOficial ? "1" : "0");
		String key = sb.toString();
		rubro = "A";
		for(int i=0; i<rubrosTO.length; i+=2){
			if(rubrosTO[i].equals(key)){
				rubro = rubrosTO[i+1];
				break;
			}
		}
		rubrosInfo.setRubro(rubro);
		return rubrosInfo;
	}
	
	
	public RubrosInfo calculaRubrosJuzgadoEjecucion(boolean isDetenidos, Integer hechos, Integer cuerpos, boolean isDefensorOficial) {
		RubrosInfo rubrosInfo = new RubrosInfo();
		String rubro = null;

		//TODO calculo de rubro para sorteo juzgados de ejecucion penal 

		rubrosInfo.setRubro(rubro);
		return rubrosInfo;
	}
	
	private RubrosInfo calculaRubrosPlenario(Expediente expediente) {
		RubrosInfo rubrosInfo = new RubrosInfo();

		rubrosInfo.setRubro("C");
		
		if(expediente.getTipoCausa() != null && expediente.getTipoCausa().getRubroAsignacion() != null){
			rubrosInfo.setRubroAsignacion(expediente.getTipoCausa().getRubroAsignacion());
		}else if (expediente.getObjetoJuicio() != null){
			rubrosInfo.setRubroAsignacion(expediente.getObjetoJuicio().getRubroAsignacion());
		}
		return rubrosInfo;
	}

	public RubrosInfo calculaRubrosJuzgado(Expediente expediente) {
		if (isExpedienteMediacion(expediente)) {
			return calculaRubrosJuzgadoMediacion(expediente);
		} else {
			return calculaRubrosJuzgadoExpedienteJudicial(expediente);
		}		
	}
	
	public RubrosInfo calculaRubrosJuzgadoExpedienteJudicial(Expediente expediente) {
		RubrosInfo rubrosInfo = new RubrosInfo();
		String rubro = null;
		String rubroAsignacion = null;
		if(expediente.getTipoCausa() != null && expediente.getTipoCausa().getRubro() != null){
			rubro = expediente.getTipoCausa().getRubro();
		}else if(expediente.getObjetoJuicioPrincipal() != null){
			rubro = expediente.getObjetoJuicioPrincipal().getRubroLetter();
		}
		if(expediente.getTipoCausa() != null && expediente.getTipoCausa().getRubroAsignacion() != null){
			rubroAsignacion = expediente.getTipoCausa().getRubroAsignacion();
		}else if(expediente.getObjetoJuicioPrincipal() != null){
			rubroAsignacion = expediente.getObjetoJuicioPrincipal().getRubroAsignacion();
		}
		if (ConfiguracionMateriaManager.instance().isAsignaDerechosHumanos(expediente.getOficinaActual().getCamara()) && 
				expediente.getExpedienteEspecial() != null && expediente.getExpedienteEspecial().isDerechosHumanos()) {
			rubro = RUBRO_DERECHOS_HUMANOS;
			rubroAsignacion = null;
		} else if (ConfiguracionMateriaManager.instance().isAdmiteLeyesEspeciales(expediente.getOficinaActual().getCamara()) &&
				ExpedienteManager.instance().isPenal(expediente)) {
			List<Integer> leyesEspecialesList =  ConfiguracionMateriaManager.instance().getLeyesEspeciales(expediente.getOficinaActual().getCamara());
			if (expediente.getTipoCausa() != null && MesaSorteo.RUBRO_LEYES_ESPECIALES.equals(expediente.getTipoCausa().getRubro())) {
				rubro = expediente.getTipoCausa().getRubro();
				rubroAsignacion = null;
			} else {
				for (DelitoExpediente delitoExpediente : expediente.getDelitoExpedienteList()) {
					if (delitoExpediente.getDelito() != null && leyesEspecialesList.contains(delitoExpediente.getDelito().getLey())) {
						rubro = RUBRO_LEYES_ESPECIALES;
						rubroAsignacion = null;
					}
				}
			}
		}
		
		rubrosInfo.setRubro(rubro);
		rubrosInfo.setRubroAsignacion(rubroAsignacion);
		return rubrosInfo;
	}
	
	public RubrosInfo calculaRubrosJuzgadoSentencia(Expediente expediente) {
		return calculaRubrosJuzgado(expediente);
	}
	
	public RubrosInfo calculaRubrosAsignacion(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion, TipoSalaEstudio tipoSalaEstudio, boolean reasignacion){
		RubrosInfo rubrosInfo = new RubrosInfo();

		if(TipoRadicacionEnumeration.Type.juzgadoPlenario == tipoRadicacion) {
			rubrosInfo = calculaRubrosPlenario(expediente);
		}else if(TipoRadicacionEnumeration.Type.tribunalOral == tipoRadicacion) {
			rubrosInfo = calculaRubrosTO(expediente);
		} else if(TipoRadicacionEnumeration.isAnySala(tipoRadicacion)){ 
			rubrosInfo = calculaRubrosSala(expediente, tipoRadicacion, tipoSalaEstudio, reasignacion);
		}else if(TipoRadicacionEnumeration.Type.salaCasacion == tipoRadicacion) {
			rubrosInfo = calculaRubrosSalaCasacion(expediente, reasignacion);
		}else if(TipoRadicacionEnumeration.Type.corteSuprema == tipoRadicacion) {
			rubrosInfo = calculaRubrosCorteSuprema(expediente, reasignacion);
		}else if(TipoRadicacionEnumeration.Type.juzgadoSentencia == tipoRadicacion) {
			rubrosInfo = calculaRubrosJuzgadoSentencia(expediente);
		} else if(TipoRadicacionEnumeration.Type.juzgado == tipoRadicacion){
			rubrosInfo = calculaRubrosJuzgado(expediente);
		}
		return rubrosInfo;
	}

	public static RubrosInfo calculaRubrosJuzgadoMediacion(Expediente expediente) {
		RubrosInfo rubrosInfo = new RubrosInfo();
		rubrosInfo.setRubro("JMED");	
		return rubrosInfo;
	}
	

	public List<BolaSorteo> findBolasByOficinaId(Sorteo sorteo, TipoBolillero tipoBolillero, Camara camara, String rubro, Integer oficinaId) {
		List<OficinaCargaExp> list = getOficinaCargaExpListByRubroAndOficina(camara, sorteo, tipoBolillero, rubro, oficinaId);		
		List<BolaSorteo> bolas = new ArrayList<BolaSorteo>();
		EntityManager entityManager = getEntityManager();
		for(OficinaCargaExp oficinaCargaExp: list){
			oficinaCargaExp = entityManager.find(OficinaCargaExp.class, oficinaCargaExp.getId());
			entityManager.refresh(oficinaCargaExp);
			BolaSorteo bolaSorteo = creaBola(oficinaCargaExp);
			bolas.add(bolaSorteo);
		}
		return bolas;
	}

	public Sorteo buscaSorteoByOficina(Oficina oficina, String rubro, Type tipoRadicacion) {
		List<Sorteo> sorteos = findSorteosByOficina(oficina);
		Sorteo ret = null;
		if(sorteos.size() == 1){
			ret = sorteos.get(0);
		}else if(sorteos.size() > 0){
			ret = seleccionaMejorSorteo(sorteos, rubro, tipoRadicacion);
		}
		return ret;
	}

	private Sorteo seleccionaMejorSorteo(List<Sorteo> sorteos, String rubro, Type tipoRadicacion) {
		Sorteo ret = null;
		for(Sorteo sorteo: sorteos){
			if(sorteoContainRubro(sorteo, rubro)){
				ret = sorteo;
				break;
			}
		}
		if(ret == null){
			ret = sorteos.get(0);
		}
		return ret;
	}

	private boolean sorteoContainRubro(Sorteo sorteo, String rubro) {
		List<String> rubros = SorteoHome.getRubrosAsList(sorteo.getRubros());
		if(rubros != null){
			return rubros.contains(rubro);
		}else{
			return false;
		}
	}

	public Sorteo findSorteoVocaliasSala(Camara camara, Type tipoRadicacion,
			Competencia competencia, boolean reasignacion, Oficina oficinaSorteadora) {
		Sorteo ret = null;
		List<Sorteo> list = findSorteosByAnyFilter(tipoRadicacion.getValue().toString(), null, null, camara, getSegundaInstancia(), getTipoOficinaVocalia(), null, null, oficinaSorteadora, false);
		if(list != null && list.size() > 0){
			ret = list.get(0);
		}
		return ret;		
	}
	
	public Sorteo findSorteoVocaliasSalaCasacion(Camara camara, Type tipoRadicacion,
			Competencia competencia, boolean reasignacion, Oficina oficinaSorteadora) {
		Sorteo ret = null;
		String codigoTipoRadicacion = tipoRadicacion.getValue().toString();
		List<Sorteo> list = findSorteosByAnyFilter(codigoTipoRadicacion, null, null, camara, getCasacion(), getTipoOficinaVocalia(), null, null, oficinaSorteadora, false);
		if(list != null && list.size() > 0){
			if (!Strings.isEmpty(codigoTipoRadicacion)) {
				for (Sorteo sorteo : list) {
					if (codigoTipoRadicacion.equals(sorteo.getTipoRadicacion())) {
						ret = sorteo;
						break;
					}
				}
			}
			if (ret == null) {
				ret = list.get(0);
			}
		}
		return ret;		
	}
	
	/** ATOS COMERCIAL */
	public SorteoParametros createSorteoParametrosWeb(Sorteo sorteo, 
			TipoBolillero tipoBolillero,
			Oficina oficinaSorteadora, 
			Expediente expediente, 
			RecursoExp recursoExp,
			TipoRadicacionEnumeration.Type tipoRadicacion, 
			String codigoTipoCambioAsignacion, 
			String descripcionCambioAsignacion,
			TipoGiroEnumeration.Type tipoGiro, 
			List<Integer> excludeOficinasIds, 
			String rubro,
			Date fechaTurnosActivos,
			boolean descompensarAnterior,
			String observaciones,
			boolean addSorteosEncadenados,
			Integer idCausa){
		
		Camara camara = SessionState.instance().getGlobalCamara();
		
		SorteoParametros sorteoParametros = new SorteoParametros(SorteoOperation.sorteo,
				camara, 
				expediente.getMateria(), 
				sorteo,
				tipoBolillero,
				rubro,
				tipoRadicacion, 
				tipoGiro, 
				oficinaSorteadora, 
				codigoTipoCambioAsignacion, 
				descripcionCambioAsignacion, 
				observaciones,
				fechaTurnosActivos,
				descompensarAnterior,
				expediente);		
		sorteoParametros.setExcludeOficinasIds(excludeOficinasIds);
		sorteoParametros.setUsuario(SessionState.instance().getUsername());
		sorteoParametros.setRecursoExp(recursoExp);
		sorteoParametros.setRemisionPorUsuario(SessionState.instance().isRemisionesPorUsuario());
		sorteoParametros.setIdCausa(idCausa);
		
		if(addSorteosEncadenados && isExpedienteMediacion(sorteoParametros.getExpediente())){
			sorteoParametros.setSorteaMediador(true);
		}
		
		if(addSorteosEncadenados) {
			SorteoParametros siguienteSorteoParametros = findSorteosEncadenado(sorteoParametros);
			if(siguienteSorteoParametros != null){
				sorteoParametros.setSiguienteSorteoEncadenado(siguienteSorteoParametros);
			}
		}
		return sorteoParametros;
	}
	/** ATOS COMERCIAL */
	
}
