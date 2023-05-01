package com.base100.lex100.mesaEntrada.conexidad;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.framework.Controller;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.UserTransaction;
import org.jboss.seam.util.Strings;

import com.base100.lex100.Lex100Exception;
import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.enumeration.AsignaConexidadEnumeration;
import com.base100.lex100.component.enumeration.EstadoIntervinienteEnumeration;
import com.base100.lex100.component.enumeration.MateriaEnumeration;
import com.base100.lex100.component.enumeration.NaturalezaSubexpedienteEnumeration;
import com.base100.lex100.component.enumeration.ParametroPredefinidoEnumeration;
import com.base100.lex100.component.enumeration.StatusStorteoEnumeration;
import com.base100.lex100.component.enumeration.TipoCambioAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoCausaEnumeration;
import com.base100.lex100.component.enumeration.TipoComparacionEnumeration;
import com.base100.lex100.component.enumeration.TipoConexidadEnumeration;
import com.base100.lex100.component.enumeration.TipoDocumentoIdentidadEnumeration;
import com.base100.lex100.component.enumeration.TipoFinalizacionEnumeration;
import com.base100.lex100.component.enumeration.TipoInformacionEnumeration;
import com.base100.lex100.component.enumeration.TipoInstanciaEnumeration;
import com.base100.lex100.component.enumeration.TipoIntervinienteEnumeration;
import com.base100.lex100.component.enumeration.TipoOficinaEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.misc.DateUtil;
import com.base100.lex100.component.validator.CuilValidator;
import com.base100.lex100.controller.entity.actuacionExp.ActuacionExpSearch;
import com.base100.lex100.controller.entity.criterioCnx.CriterioCnxSearch;
import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.controller.entity.oficina.OficinaSearch;
import com.base100.lex100.controller.entity.oficinaAsignacionExp.OficinaAsignacionExpSearch;
import com.base100.lex100.controller.entity.sigla.SiglaSearch;
import com.base100.lex100.controller.entity.tipoOficina.TipoOficinaSearch;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.CondicionCnx;
import com.base100.lex100.entity.Conexidad;
import com.base100.lex100.entity.ConexidadDeclarada;
import com.base100.lex100.entity.ConexidadLog;
import com.base100.lex100.entity.CriterioCnx;
import com.base100.lex100.entity.EstadoExpediente;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.Interviniente;
import com.base100.lex100.entity.IntervinienteExp;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.Parametro;
import com.base100.lex100.entity.ParametroExpediente;
import com.base100.lex100.entity.Sigla;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.Tema;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.TipoDocumentoIdentidad;
import com.base100.lex100.entity.TipoInformacion;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoOficina;
import com.base100.lex100.manager.actuacionExp.ActuacionExpManager;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.configuracionMateria.ConfiguracionMateriaManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.manager.expediente.OficinaAsignacionExpManager;
import com.base100.lex100.manager.oficina.OficinaManager;
import com.base100.lex100.mesaEntrada.ingreso.CreateExpedienteAction;
import com.base100.lex100.mesaEntrada.ingreso.ExpedienteMeAdd;
import com.base100.lex100.mesaEntrada.sorteo.MesaSorteo;
import com.base100.lex100.mesaEntrada.sorteo.SorteoExpedienteEntry;
import com.base100.lex100.mesaEntrada.sorteo.SorteoManager;
import com.base100.lex100.mesaEntrada.sorteo.SorteoOperation;
import com.base100.lex100.mesaEntrada.sorteo.SorteoParametros;
import com.base100.lex100.mesaEntrada.sorteo.Tracer;

@Name("conexidadManager")
public class ConexidadManager extends Controller {

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	protected static final boolean PRIORIDAD_ANTIGUOS = true;

	private static boolean useAntecenteSearch = false;// true;

	static public final String CONEXIDAD_STMT = "FROM Conexidad conexidad WHERE "
			+ "conexidad.expedienteOrigen = :expedienteOrigen and conexidad.expedienteRelacionado = :expedienteRelacionado and status = 0";

	public static final Integer ACTOR = TipoIntervinienteEnumeration.ACTOR_NUMERO_ORDEN;
	public static final Integer DEMANDADO = TipoIntervinienteEnumeration.DEMANDADO_NUMERO_ORDEN;

	private Date today;

	@In(create = true)
	private AntecedenteSearch antecedenteSearch;
	@In(create = true)
	private AntecedenteList antecedenteList;

	@In(create = true)
	private SessionState sessionState;
	@In(create = true)
	private ParametroPredefinidoEnumeration parametroPredefinidoEnumeration;

	private List<Conexidad> conexidadResult = new ArrayList<Conexidad>();

	private static Log log = Logging.getLog(ConexidadManager.class);
	
	private Tracer tracer;
	private Boolean auditoriaActiva;

	public static EntityManager getEntityManager() {
		return (EntityManager) Component.getInstance("entityManager");
	}

	public static ConexidadManager instance() {
		return (ConexidadManager) Component.getInstance(ConexidadManager.class, true);
	}

	@Transactional
	@TransactionTimeout(1000)
	public List<Conexidad> buscaConexidadPorCriterios(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion,
			Expediente expedienteConexoDeclarado, List<ParametroExpediente> parametrosExpediente,
			boolean pararSiEncontrado, boolean updateConexidad, boolean paraAsignar, boolean ignoreCheckDobleInicio) throws Lex100Exception {

		List<String> naturalezas = new ArrayList<String>();

		if (TipoRadicacionEnumeration.Type.tribunalOral.equals(tipoRadicacion)) {
			naturalezas.add((String) NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue());
		} else if (TipoRadicacionEnumeration.Type.salaCasacion.equals(tipoRadicacion)) {
		} else if (TipoRadicacionEnumeration.Type.corteSuprema.equals(tipoRadicacion)) {
		} else {
			naturalezas.add((String) NaturalezaSubexpedienteEnumeration.Type.principal.getValue());
		}

		if (TipoCausaEnumeration.isMediacion(expediente.getTipoCausa())) {
			naturalezas.add((String) NaturalezaSubexpedienteEnumeration.Type.mediacion.getValue());
		}
		if (naturalezas.size() == 0) {
			naturalezas = null;
		}

		conexidadResult = buscaConexidadPorCriterios(expediente, tipoRadicacion, expedienteConexoDeclarado,
				parametrosExpediente, naturalezas, pararSiEncontrado, updateConexidad, paraAsignar, ignoreCheckDobleInicio);

		return conexidadResult;
	}

	@Transactional 
	@TransactionTimeout(200000)
	public List<Conexidad> buscaConexidadPorCriterios(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion,
			Expediente expedienteConexoDeclarado, List<ParametroExpediente> parametrosExpediente,
			List<String> naturalezas, boolean pararSiEncontrado, boolean updateConexidad, 
			boolean paraAsignar, boolean ignoreCheckDobleInicio) throws Lex100Exception {

		startTracer();
		
		getEntityManager().joinTransaction();

		conexidadResult = new ArrayList<Conexidad>();
		if (updateConexidad) {
			deleteConexidad(expediente);
			conexidadResult = new ArrayList<Conexidad>(expediente.getConexidadList());
		}
		Collection<CriterioCnx> criterios = getCriterios(expediente.getTipoCausa(),
				expediente.getObjetoJuicioPrincipal(), ExpedienteManager.getMateria(expediente), expediente.isVoluntario(), ignoreCheckDobleInicio);

		Boolean soloEnTramite = null;
		String conTipoRadicacion = null;
		if ((tipoRadicacion != null) && !TipoRadicacionEnumeration.isAnyJuzgado(tipoRadicacion)) {
			conTipoRadicacion = tipoRadicacion.getValue().toString();
		}
	
		for (CriterioCnx criterio : criterios) {
			getTracer().notify("Criterio", criterio.getNombre());
			getLog().info("Buscando conexidad por criterio [" + criterio.getNombre() + "] para el expediente: " + expediente.getClave());
			
			List<Expediente> antecedentes;
			if (useAntecenteSearch) { // Se puede elegir entre dos
				// implementaciones (se supone que es
				// más rápida la segunda)
				// Búsqueda de antecedentes mediante el controlador
				// antecedenteSearch
				antecedentes = buscaAntecedentesPorCriterio(expediente, criterio, parametrosExpediente, naturalezas,
						soloEnTramite, conTipoRadicacion);
			} else {
				// Búsqueda de antecedentes mediante BuscadorConexidad
				antecedentes = buscaAntecedentes(expediente, criterio, parametrosExpediente, naturalezas, soloEnTramite, conTipoRadicacion);
			}
			if ((antecedentes != null) && (antecedentes.size() > 0)) {
				boolean validosParaAsignar = addConexidad(expediente, expedienteConexoDeclarado, antecedentes, criterio, updateConexidad, pararSiEncontrado && paraAsignar, tipoRadicacion);
				if (pararSiEncontrado && (!paraAsignar || validosParaAsignar)){
					break;
				}
			} else {
				getTracer().notify("Fin Criterio", "0 expedientes encontrados");
			}
			getTracer().traceLn();
			getLog().info("Fin busqueda de conexidad por criterio [" + criterio.getNombre() + "] para el expediente: " + expediente.getClave());
		}
		ordenaConexidad(conexidadResult, expediente, tipoRadicacion);
		addActuacionConexidad(tipoRadicacion, expediente);
	
		if(criterios.size() > 0)
			addConexidadLog(getEntityManager(), SessionState.instance().getGlobalCamara(), expediente, tipoRadicacion);

		return conexidadResult;
	}

	private void addActuacionConexidad(TipoRadicacionEnumeration.Type tipoRadicacion, Expediente expediente) {
		getLog().info("Agregando actuacion de conexidad para el expediente: " + expediente.getClave());
		String codigoTipoInformacion = null;
		if (TipoRadicacionEnumeration.Type.tribunalOral.equals(tipoRadicacion)) {
			codigoTipoInformacion = TipoInformacionEnumeration.CONEXIDAD_TO;
		} else if (TipoRadicacionEnumeration.isAnySala(tipoRadicacion) || TipoRadicacionEnumeration.isRadicacionSalaCasacion(tipoRadicacion)) {
			codigoTipoInformacion = TipoInformacionEnumeration.CONEXIDAD_SALA;
		} else {
			codigoTipoInformacion = TipoInformacionEnumeration.CONEXIDAD_JUZGADO;
		}
		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(
				ActuacionExpManager.class, true);

		if (!Strings.isEmpty(codigoTipoInformacion)) {
			TipoInformacion tipoInformacion = TipoInformacionEnumeration.getItemByCodigo(getEntityManager(),
					codigoTipoInformacion, expediente.getMateria());
			

			List<Conexidad> conexidadList = expediente.getConexidadList();
			StringBuffer descripcion = new StringBuffer();
			
			int found = getResultadoConexidad(tipoRadicacion, conexidadList, descripcion, true);
			if (found > 0) {
				String detalle = interpolate(getMessages().get("conexidad.found"), found);
				actuacionExpManager.addInformacionExpediente(expediente, null, tipoInformacion, descripcion.toString(),
						null, null, SessionState.instance().getUsername(), detalle);
			}
		}
		getLog().info("Fin agregado de actuacion de conexidad para el expediente: " + expediente.getClave());
	}

	private int getResultadoConexidad(TipoRadicacionEnumeration.Type tipoRadicacion, List<Conexidad> conexidadList,
			StringBuffer descripcion, boolean soloConRadicacion) {

		int found = 0;
		
		for (Conexidad conexidad : conexidadList) {
			OficinaAsignacionExp oficinaAsignacionExp = null;
			Oficina oficina = null;
			
			oficinaAsignacionExp = OficinaAsignacionExpSearch.findByTipoRadicacion(getEntityManager(), conexidad.getExpedienteRelacionado(), (String)tipoRadicacion.getValue());
			if (oficinaAsignacionExp != null) {
				oficina = oficinaAsignacionExp.getOficinaConSecretaria();
			}
			if (!soloConRadicacion || (oficina != null)) {
				String descripcionOficina = "";
				if(oficina != null) {
					descripcionOficina = oficina.getDescripcion();
				}
				Tracer.addInfo(descripcion, conexidad.getExpedienteRelacionado().getIdxAnaliticoFirst(), descripcionOficina);
				Tracer.addInfo(descripcion, null, conexidad.getCriteriosName());
				Tracer.addInfo(descripcion, null, conexidad.getCoincidencias());
				found++;
			}
		}
		return found;
	}

	// Devuelve el expediente conexo con más prioridad
	private Conexidad ordenaConexidad(List<Conexidad> conexidadList, Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		getLog().info("Ordenando conexidad para el expediente: " + expediente.getClave());
		Conexidad conexidadPrioritaria = null;
		if (conexidadList.size() > 0) {
			ordenarConexidad(conexidadList, expediente, tipoRadicacion);
			int orden = 0;
			for (Conexidad conexidad : conexidadList) {
				conexidad.setOrden(++orden);
			}
			conexidadPrioritaria = conexidadList.get(0);
			getEntityManager().flush();
		}
		getLog().info("Fin orden de conexidad para el expediente: " + expediente.getClave());
		return conexidadPrioritaria;
	}

	/* */

	public boolean hasConexidadDeclarada(Expediente expediente) {
		return ExpedienteManager.hasConexidadDeclarada(expediente);
	}

	public Oficina findOficinaConexidadDeclarada(Expediente expediente, TipoInstancia tipoInstancia) {
		Oficina oficina = null;
		ConexidadDeclarada conexidadDeclarada = expediente.getConexidadDeclarada();
		if (conexidadDeclarada != null) {
			if (conexidadDeclarada.getOficina() != null) {
				oficina = conexidadDeclarada.getOficina();
			} else if (conexidadDeclarada.getJuzgado() != null) {
				TipoOficina tipoOficina = TipoOficinaSearch.findByCodigo(getEntityManager(), TipoInstanciaEnumeration
						.isApelaciones(tipoInstancia) ? TipoOficinaEnumeration.TIPO_OFICINA_SALA_APELACIONES
						: TipoOficinaEnumeration.TIPO_OFICINA_JUZGADO);
				oficina = OficinaSearch.findByNumero(getEntityManager(),
						String.valueOf(conexidadDeclarada.getJuzgado()), sessionState.getGlobalCamara(), tipoInstancia,
						ExpedienteManager.getCompetencia(expediente), tipoOficina);
				if ((oficina != null) && (conexidadDeclarada.getSecretaria() != null)) {
					TipoOficina tipoSecretaria = TipoOficinaSearch.findByCodigo(getEntityManager(),
							TipoOficinaEnumeration.TIPO_OFICINA_SECRETARIA_JUZGADO);
					oficina = OficinaSearch.findByNumeroYSuperior(getEntityManager(),
							String.valueOf(conexidadDeclarada.getJuzgado()), oficina, sessionState.getGlobalCamara(),
							tipoInstancia, ExpedienteManager.getCompetencia(expediente), tipoSecretaria);
				}
			}
		}
		return oficina;
	}

	@SuppressWarnings("unchecked")
	public void solveExpedienteConexidadDeclarada(ConexidadDeclarada conexidadDeclarada) {
		if (conexidadDeclarada != null) {
			Expediente expedienteConexo = null;
			if (!conexidadDeclarada.isAnteriorAlSistema()) {
				Integer numero = conexidadDeclarada.getNumeroExpedienteConexo();
				Integer anio = conexidadDeclarada.getAnioExpedienteConexo();
				String abreviaturaCamara = conexidadDeclarada.getAbreviaturaCamaraExpedienteConexo();
				if (numero != null && anio != null) {
					if (!sameExpediente(numero, anio, abreviaturaCamara, conexidadDeclarada.getExpedienteConexo())) {
						String stmt = "from Expediente where numero = :numero and anio = :anio and tipoSubexpediente.naturalezaSubexpediente = :naturaleza and tipoProceso is not null  and verificacionMesa in ('M', 'N') and status = 0"
						+ "and (exists (select 1 from OficinaAsignacionExp oa where oa.expediente = expediente and oa.oficina.camara.id = #{sessionState.globalCamara.definedId}) ) ";
						
						if(!Strings.isEmpty(abreviaturaCamara)){
							stmt +=" and camara.abreviatura = :abreviaturaCamara";
						}

						List<Expediente> expedienteList;
						Query query = getEntityManager().createQuery(stmt);
						
						query.setParameter("numero", conexidadDeclarada.getNumeroExpedienteConexo());
						query.setParameter("anio", conexidadDeclarada.getAnioExpedienteConexo());
						query.setParameter("naturaleza",	NaturalezaSubexpedienteEnumeration.Type.principal.getValue().toString());
						if(!Strings.isEmpty(abreviaturaCamara)){
							query.setParameter("abreviaturaCamara", abreviaturaCamara);	
						}
								
						expedienteList  = query.getResultList();

						if (expedienteList.size() > 0) {
							expedienteConexo = expedienteList.get(0);
						}
					} else {
						expedienteConexo = conexidadDeclarada.getExpedienteConexo();
					}
				}
			}
			conexidadDeclarada.setExpedienteConexo(expedienteConexo);
		}
	}

	private boolean sameExpediente(Integer numero, Integer anio, String abreviaturaCamara, Expediente expediente) {
		boolean ret = true;
		if(expediente == null || numero == null || anio == null){
			ret = false;
		}else if(!numero.equals(expediente.getNumero()) || !anio.equals(expediente.getAnio())) {
			ret = false;
		}else if(abreviaturaCamara != null && !abreviaturaCamara.equals(expediente.getCamara().getAbreviatura())){
			ret = false;
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<Expediente> getExpedientesConexidadDeclaradaInversa(Expediente expediente) {
		List<Expediente> expedienteConexidadDeclaradaInversaList = new ArrayList<Expediente>();
		if (expediente != null) {
			// expedienteConexidadDeclaradaInversaList =
			// (List<Expediente>)getEntityManager().createQuery("select e.expedienteIngreso.expediente from ConexidadDeclarada e where e.expedienteIngreso.expediente.camara = :camara and e.numeroExpedienteConexo = :numeroExpediente and e.anioExpedienteConexo = :anioExpediente and e.expedienteIngreso.expediente.status<>-1").
			// setParameter("camara", expediente.getCamara()).
			// setParameter("numeroExpediente", expediente.getNumero()).
			// setParameter("anioExpediente", expediente.getAnio()).
			expedienteConexidadDeclaradaInversaList = (List<Expediente>) getEntityManager()
					.createQuery(
							"select conexidadDeclarada.expedienteIngreso.expediente from ConexidadDeclarada conexidadDeclarada where conexidadDeclarada.expedienteConexo = :expedienteConexo and conexidadDeclarada.expedienteIngreso.expediente.status<>-1")
					.setParameter("expedienteConexo", expediente).getResultList();
		}
		return expedienteConexidadDeclaradaInversaList;
	}

	public Expediente findExpedienteConexidadPrincipal(Expediente expediente) {
		Expediente expedienteConexidadPrincipal = null;
		if (expediente != null) {
			try {
				expedienteConexidadPrincipal = (Expediente) getEntityManager()
						.createQuery(
								"select e.expedienteRelacionado from Conexidad e where e.expedienteOrigen.id = :id and e.expedienteRelacionado is not null and e.principal = :principal")
						.setParameter("id", expediente.getId()).setParameter("principal", Boolean.TRUE)
						.getSingleResult();
			} catch (Exception e) {
				expedienteConexidadPrincipal = null;
			}
		}
		return expedienteConexidadPrincipal;
	}

	@SuppressWarnings("unchecked")
	public List<Expediente> findExpedientesConexidadPrincipalInversa(Expediente expediente) {
		List<Expediente> expedientesConexidadPrincipalInversaList = null;
		if (expediente != null) {
			expedientesConexidadPrincipalInversaList = (List<Expediente>) getEntityManager()
					.createQuery(
							"select e.expedienteOrigen from Conexidad e where e.expedienteRelacionado.id = :id and e.expedienteOrigen is not null and e.principal = :principal")
					.setParameter("id", expediente.getId()).setParameter("principal", Boolean.TRUE).getResultList();
		}
		return expedientesConexidadPrincipalInversaList;
	}

	public Boolean getSoloIniciados(Camara camara, Materia materia, CriterioCnx criterio) {
		Boolean soloIniciados = criterio.getExpedientesIniciados();
		
		if (soloIniciados == null) {
			if (Boolean.TRUE.equals(ConfiguracionMateriaManager.instance().isConexidadExpedientesIniciados(
					sessionState.getGlobalCamara(), materia))) {
				soloIniciados = true;
			}
		}
		return soloIniciados;
	}
	
	public Boolean getSoloEnTramite(Camara camara, Materia materia, CriterioCnx criterio) {
		Boolean soloEnTramite = criterio.getExpedientesEnTramite();
		
		if (soloEnTramite == null) {
			if (Boolean.TRUE.equals(ConfiguracionMateriaManager.instance().isConexidadExpedientesEnTramite(
					sessionState.getGlobalCamara(), materia))) {
				soloEnTramite = true;
			}
		}
		return soloEnTramite;
	}

	// Búsqueda de antecedentes mediante BuscadorConexidad
	private List<Expediente> buscaAntecedentes(Expediente expediente, CriterioCnx criterio,
			List<ParametroExpediente> parametrosExpediente, List<String> naturalezas, Boolean soloEnTramite, String conTipoRadicacion) {

		antecedenteSearch.clear();
		antecedenteList.setEjbql(null);

		ObjetoJuicio objetoJuicioPrincipal = expediente.getObjetoJuicioPrincipal();

		List<Expediente> result = null;
		Date fromFechaIngreso = null;
		Integer fromAnioExpediente = null;
		Boolean soloIniciados = null;
		
		soloIniciados = getSoloIniciados(sessionState.getGlobalCamara(), getMateria(objetoJuicioPrincipal), criterio);

		if (soloEnTramite == null) {
			soloEnTramite = getSoloEnTramite(sessionState.getGlobalCamara(), getMateria(objetoJuicioPrincipal), criterio);
		}

		fromAnioExpediente = getAnioDesde(objetoJuicioPrincipal, criterio);
		fromFechaIngreso = getFechaDesde(objetoJuicioPrincipal, criterio);

		List<String> codigoObjetoJuicioIncluidosList = toCodigoList(getListaObjetosIncluidos(objetoJuicioPrincipal,
				criterio));
		List<String> codigoObjetoJuicioExcluidosList = toCodigoList(getListaObjetosExcluidos(objetoJuicioPrincipal,
				criterio));
		List<String> codigoTipoCausaIncluidasList = criterio.getCodigosTipoCausaList();

//		List<String> codigoTemasIncluidosList = criterio.getCodigosTemaList();
		List<Integer> idTemasIncluidosList = getIdTemasIncluidosByCodigo(sessionState.getGlobalCamara(), criterio.getCodigosTemaList());

		ListaIntervinientes actores = new ListaIntervinientes(ActorDemandado.ACTOR);
		ListaIntervinientes demandados = new ListaIntervinientes(ActorDemandado.DEMANDADO);

		boolean filtered = false;
		for (CondicionCnx condicion : criterio.getCondicionCnxList()) {
			if(!condicion.isLogicalDeleted()) {
				filtered = filtrarPorCondicion(condicion, expediente, parametrosExpediente);
				if (!filtered) { // El expediente no tiene datos para establecer los
					// filtros
					break;
				}
			}
		}
		
		boolean origenEnCamara = criterio.isAmbitoGlobal() && CamaraManager.isCamaraActualSeguridadSocial();
		boolean radicacionEnCamara = false;
		
		if (filtered && antecedenteSearch.isFiltered()) {
			fillFiltroIntervinientes(expediente, actores, demandados);
			BuscadorConexidad buscadorConexidad = new BuscadorConexidad();
			result = buscadorConexidad.buscaConexidadGenerica(getTracer(), expediente, actores, demandados, antecedenteSearch.getParametro(),
					naturalezas, fromFechaIngreso, fromAnioExpediente, soloIniciados, soloEnTramite, criterio.getAmbitoVoluntarios(), criterio.isAmbitoGlobal(), codigoObjetoJuicioIncluidosList,
					codigoObjetoJuicioExcluidosList, codigoTipoCausaIncluidasList, idTemasIncluidosList,
					antecedenteSearch.getDependenciaOrigen(), antecedenteSearch.getCodigoRelacionado(), conTipoRadicacion, origenEnCamara, radicacionEnCamara);
		} else {
			getTracer().trace("", "No aplica");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<Integer> getIdTemasIncluidosByCodigo(Camara camara, List<String> codigosTemaList) {
		if ((codigosTemaList != null) && (camara != null)){
			List<Integer> ids = getEntityManager().createQuery("select id from Tema where codigo in (:codigos) and camara = :camara").
				setParameter("codigos",codigosTemaList).
				setParameter("camara", camara).
				getResultList();
			if (ids.size() > 0) {
				List<Integer> temasHijos = getIdsSubtemasByIdTemaPadre(camara, ids);
				if (temasHijos != null) {
					ids.addAll(temasHijos);
				}
			}
			return ids;
		} 
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Integer> getIdsSubtemasByIdTemaPadre(Camara camara, List<Integer> idsTemaPadreList) {
		if ((camara != null) && (idsTemaPadreList != null) && idsTemaPadreList.size() > 0){
			List<Integer> ids = getEntityManager().createQuery("select id from Tema where temaPadre.id in (:ids) and camara = :camara").
				setParameter("ids",idsTemaPadreList).
				setParameter("camara", camara).
				getResultList();
			if (ids.size() > 0) {
				ids.addAll(getIdsSubtemasByIdTemaPadre(camara, ids));
			}
			return ids;
		} 
		return null;
	}

	private List<Expediente> getExpedientes(List<IntervinienteExp> intervinientes) {
		Set<Expediente> expedientes = new HashSet<Expediente>();
		for (IntervinienteExp intervinienteExp : intervinientes) {
			expedientes.add(intervinienteExp.getExpediente());
		}
		return new ArrayList<Expediente>(expedientes);
	}

	private void fillFiltroIntervinientes(Expediente expediente, ListaIntervinientes actores,
			ListaIntervinientes demandados) {

		if (antecedenteSearch.getTipoComparacionPartes() != null) {
			List<String> actorCuitList = null;
			List<String> actorDniList = null;
			List<String> actorNombreList = null;
			boolean actorHayPersonaJuridica = false;
			List<String> demandadoCuitList = null;
			List<String> demandadoDniList = null;
			List<String> demandadoNombreList = null;
			boolean demandadoHayPersonaJuridica = false;
			
			boolean excluirPersonasJuridicas = antecedenteSearch.isExcluirPersonasJuridicas();

			if (!DEMANDADO.equals(antecedenteSearch.getTipoFiltroPartes())) { 
				Integer tipoBusca = getTipoBusca(ACTOR, antecedenteSearch.getTipoComparacionPartes());
				actorCuitList = getCuitParteList(expediente, ACTOR, tipoBusca, excluirPersonasJuridicas);
				actorDniList = getDniParteList(expediente, ACTOR, tipoBusca, excluirPersonasJuridicas);
				actorNombreList = getNombreParteList(expediente, ACTOR, tipoBusca, excluirPersonasJuridicas);
				actorHayPersonaJuridica = hayPersonaJuridica(expediente, ACTOR, tipoBusca, excluirPersonasJuridicas);
			}
			if (!ACTOR.equals(antecedenteSearch.getTipoFiltroPartes())) { 
				Integer tipoBusca = getTipoBusca(DEMANDADO, antecedenteSearch.getTipoComparacionPartes());
				demandadoCuitList = getCuitParteList(expediente, DEMANDADO, tipoBusca, excluirPersonasJuridicas);
				demandadoDniList = getDniParteList(expediente, DEMANDADO, tipoBusca, excluirPersonasJuridicas);
				demandadoNombreList = getNombreParteList(expediente, DEMANDADO, tipoBusca, excluirPersonasJuridicas);
				demandadoHayPersonaJuridica = hayPersonaJuridica(expediente, DEMANDADO, tipoBusca, excluirPersonasJuridicas);
			}
			if (TipoComparacionEnumeration.Type.indistinta.getValue().equals(
					antecedenteSearch.getTipoComparacionPartes())) {
        		// actores
				if (!DEMANDADO.equals(antecedenteSearch.getTipoFiltroPartes())) {
					actores.setTipo(ActorDemandado.ACTOR);
					actores.setCuitCuils(actorCuitList);
					actores.addCuitCuils(demandadoCuitList);
					actores.setDnis(actorDniList);
					actores.addDnis(demandadoDniList);
					actores.setNombres(actorNombreList);
					actores.addNombres(demandadoNombreList);
					actores.setHayPersonaJuridica(actorHayPersonaJuridica || demandadoHayPersonaJuridica); 
				}
				// demandados
				if (!ACTOR.equals(antecedenteSearch.getTipoFiltroPartes())) {
					demandados.setTipo(ActorDemandado.DEMANDADO);
					demandados.setCuitCuils(demandadoCuitList);
					demandados.addCuitCuils(actorCuitList);
					demandados.setDnis(demandadoDniList);
					demandados.addDnis(actorDniList);
					demandados.setNombres(demandadoNombreList);
					demandados.addNombres(actorNombreList);
					demandados.setHayPersonaJuridica(actorHayPersonaJuridica || demandadoHayPersonaJuridica); 
				}
			} else if (TipoComparacionEnumeration.Type.cruzada.getValue().equals(
					antecedenteSearch.getTipoComparacionPartes())) {
				// actores
				if (!DEMANDADO.equals(antecedenteSearch.getTipoFiltroPartes())) {
					demandados.setCuitCuils(actorCuitList);
					demandados.setDnis(actorDniList);
					demandados.setNombres(actorNombreList);
					demandados.setHayPersonaJuridica(actorHayPersonaJuridica);
				}
				// demandados
				if (!ACTOR.equals(antecedenteSearch.getTipoFiltroPartes())) {
					actores.setCuitCuils(demandadoCuitList);
					actores.setDnis(demandadoDniList);
					actores.setNombres(demandadoNombreList);
					actores.setHayPersonaJuridica(demandadoHayPersonaJuridica);
				}
			} else if (TipoComparacionEnumeration.Type.paralela.getValue().equals(
					antecedenteSearch.getTipoComparacionPartes())) {
				// actores
				if (!DEMANDADO.equals(antecedenteSearch.getTipoFiltroPartes())) {
					actores.setCuitCuils(actorCuitList);
					actores.setDnis(actorDniList);
					actores.setNombres(actorNombreList);
					actores.setHayPersonaJuridica(actorHayPersonaJuridica);
				}
				// demandados
				if (!ACTOR.equals(antecedenteSearch.getTipoFiltroPartes())) {
					demandados.setCuitCuils(demandadoCuitList);
					demandados.setDnis(demandadoDniList);
					demandados.setNombres(demandadoNombreList);
					demandados.setHayPersonaJuridica(demandadoHayPersonaJuridica);
				}
			}
		} else {
			// actores
			if (antecedenteSearch.getCuitParte1List() != null) {
				actores.setTipo(ActorDemandado.CUALQUIERA);
				actores.setCuitCuils(antecedenteSearch.getCuitParte1List());
			} else {
				actores.setCuitCuils(antecedenteSearch.getCuitActorList());
			}
			if (antecedenteSearch.getDniParte1List() != null) {
				actores.setTipo(ActorDemandado.CUALQUIERA);
				actores.setDnis(antecedenteSearch.getDniParte1List());
			} else {
				actores.setDnis(antecedenteSearch.getDniActorList());
			}
			if (antecedenteSearch.getNombreParte1List() != null) {
				actores.setTipo(ActorDemandado.CUALQUIERA);
				actores.setNombres(antecedenteSearch.getNombreParte1List());
			} else {
				actores.setNombres(antecedenteSearch.getNombreActorList());
			}
			// demandados
			if (antecedenteSearch.getCuitParte2List() != null) {
				demandados.setTipo(ActorDemandado.CUALQUIERA);
				demandados.setCuitCuils(antecedenteSearch.getCuitParte2List());
			} else {
				demandados.setCuitCuils(antecedenteSearch.getCuitDemandadoList());
			}
			if (antecedenteSearch.getDniParte2List() != null) {
				demandados.setTipo(ActorDemandado.CUALQUIERA);
				demandados.setDnis(antecedenteSearch.getDniParte2List());
			} else {
				demandados.setDnis(antecedenteSearch.getDniDemandadoList());
			}
			if (antecedenteSearch.getNombreParte2List() != null) {
				demandados.setTipo(ActorDemandado.CUALQUIERA);
				demandados.setNombres(antecedenteSearch.getNombreParte2List());
			} else {
				demandados.setNombres(antecedenteSearch.getNombreDemandadoList());
			}
		}
	}

	// Búsqueda de antecedentes mediante el controlador antecedenteSearch
	private List<Expediente> buscaAntecedentesPorCriterio(Expediente expediente, CriterioCnx criterio,
			List<ParametroExpediente> parametrosExpediente, List<String> naturalezas, Boolean soloEnTramite, String conTipoRadicacion) {
		ObjetoJuicio objetoJuicioPrincipal = expediente.getObjetoJuicioPrincipal();

		List<Expediente> antecedentes = new ArrayList<Expediente>();
		antecedenteSearch.clear();
		antecedenteList.setEjbql(null);
		antecedenteSearch.setExpedienteOrigen(expediente);
		antecedenteSearch.setExpedienteBase(ExpedienteManager.getExpedienteBase(getEntityManager(), expediente));
		antecedenteSearch.setObjetoJuicioPrincipal(objetoJuicioPrincipal);
		if (expediente.getMateria().getGrupo() != null) {
			antecedenteSearch.setMateriaGrupo(expediente.getMateria().getGrupo());
		}
		antecedenteSearch.setConTipoRadicacion(conTipoRadicacion);

		Boolean soloIniciados = getSoloIniciados(sessionState.getGlobalCamara(), getMateria(objetoJuicioPrincipal), criterio);
		antecedenteSearch.setSoloIniciados(soloIniciados);
		
		if (soloEnTramite == null) {
			soloEnTramite = getSoloEnTramite(sessionState.getGlobalCamara(), getMateria(objetoJuicioPrincipal), criterio);
		}
		antecedenteSearch.setSoloEnTramite(soloEnTramite);

		filtrarPorNaturalezas(naturalezas);
		filtrarPorTipoCausa(expediente.getTipoCausa(), criterio);
		filtrarPorFechas(objetoJuicioPrincipal, criterio);
		filtrarPorAmbito(objetoJuicioPrincipal, criterio);
		boolean filtered = false;
		for (CondicionCnx condicion : criterio.getCondicionCnxList()) {
			if(!condicion.isLogicalDeleted()) {
				filtered = filtrarPorCondicion(condicion, expediente, parametrosExpediente);
				if (!filtered) { // El expediente no tiene datos para establecer los
					// filtros
					break;
				}
			}
		}
		if (filtered && antecedenteSearch.isFiltered()) {
			getLog().error("Busqueda de antecedentes por criterio: " + criterio.getNombre());
			antecedentes = antecedenteList.getAllResults();
			getLog().error(
					"Busqueda de antecedentes por criterio: " + criterio.getNombre() + ". Encontrados : "
							+ antecedentes.size());
		}
		return antecedentes;
	}

	private void ordenarConexidad(List<Conexidad> conexidadList, Expediente expediente, final TipoRadicacionEnumeration.Type tipoRadicacion) {

		final Map<Integer, Integer> expedientesPorOficinaMap = new HashMap<Integer, Integer>();
		final boolean prioridadNumExpedientes = expediente.isLitisConsorcio() && 
			ConfiguracionMateriaManager.instance().isConexidadPrioridadNumExpedientesLitisConsorcio(CamaraManager.getCamaraActual(), null);

		final boolean prioridadNumCoincidencias = ConfiguracionMateriaManager.instance().isConexidadPrioridadNumCoincidencias(CamaraManager.getCamaraActual(), null);

		if (conexidadList.size() > 0) {
			for (Conexidad conexidad : conexidadList) {
				conexidad.setNumeroCriterios(conexidad.getCriterioCnxList().size());
				for (CriterioCnx criterio : conexidad.getCriterioCnxList()) {
					if ((criterio.getPrioridad() != null)
							&& (criterio.getPrioridad().compareTo(conexidad.getPrioridad()) < 0)) {
						conexidad.setPrioridad(criterio.getPrioridad());
					}
				}
				if (prioridadNumExpedientes) {
					Oficina oficina = ExpedienteManager.getOficinaByRadicacion(getEntityManager(), conexidad.getExpedienteRelacionado(),  (String)tipoRadicacion.getValue());
					if (oficina != null) {
						Integer numExpedientes = expedientesPorOficinaMap.get(oficina.getId());
						if (numExpedientes == null) {
							numExpedientes = 0;
						}
						numExpedientes++;
						expedientesPorOficinaMap.put(oficina.getId(), numExpedientes);
					}
				}
			}
			actualizarCoincidencias(conexidadList);

			Collections.sort(conexidadList, new Comparator<Conexidad>() {
				@Override
				public int compare(Conexidad c1, Conexidad c2) {
					int ret = 0;
					Boolean isC1Principal = Boolean.TRUE.equals(c1.getPrincipal());
					Boolean isC2Principal = Boolean.TRUE.equals(c2.getPrincipal());
					ret = isC2Principal.compareTo(isC1Principal);
					if ((ret == 0) && 
							CreateExpedienteAction.isConexidadDenunciadaPrioritaria(c1.getExpedienteOrigen())) {
						Boolean isC1Denunciada = TipoConexidadEnumeration.Type.denunciada.getValue().equals(c1.getTipo());
						Boolean isC2Denunciada = TipoConexidadEnumeration.Type.denunciada.getValue().equals(c2.getTipo());
						ret = isC2Denunciada.compareTo(isC1Denunciada);
					}
					if (ret == 0) {
						Boolean c1Asigna = isAsigna(c1) && isValidoParaAsignar(c1, tipoRadicacion);
						Boolean c2Asigna = isAsigna(c2) && isValidoParaAsignar(c2, tipoRadicacion);
						ret = c2Asigna.compareTo(c1Asigna);
					}
					if (ret == 0) {
						Boolean isC1NaturalezaPrincipal = isNaturalezaPrincipal(c1.getExpedienteRelacionado());
						Boolean isC2NaturalezaPrincipal = isNaturalezaPrincipal(c2.getExpedienteRelacionado());
						ret = isC2NaturalezaPrincipal.compareTo(isC1NaturalezaPrincipal);
					}
					if ((ret == 0)
							&& ConfiguracionMateriaManager.instance().isConexidadPrioridadIniciados(
									c1.getExpedienteOrigen().getCamara(), c1.getExpedienteOrigen().getMateria())) {
						Boolean isC1Iniciado = isIniciado(c1.getExpedienteRelacionado());
						Boolean isC2Iniciado = isIniciado(c2.getExpedienteRelacionado());
						ret = isC2Iniciado.compareTo(isC1Iniciado);
					}
					if ((ret == 0) && prioridadNumExpedientes) {
						Oficina oficina1 = ExpedienteManager.getOficinaByRadicacion(getEntityManager(), c1.getExpedienteRelacionado(), (String)tipoRadicacion.getValue());
						Oficina oficina2 = ExpedienteManager.getOficinaByRadicacion(getEntityManager(), c2.getExpedienteRelacionado(), (String)tipoRadicacion.getValue());
						Integer numExpedientes1 = (oficina1 != null)? expedientesPorOficinaMap.get(oficina1.getId()) : 0;
						Integer numExpedientes2 = (oficina2 != null)? expedientesPorOficinaMap.get(oficina2.getId()) : 0;
						ret = numExpedientes2.compareTo(numExpedientes1);
					}
					if (ret == 0) {
						ret = c1.getPrioridad().compareTo(c2.getPrioridad()); // Mayor prioridad si el valor es menor
						if ((ret == 0) && prioridadNumCoincidencias) {
							ret = c2.getNumeroCriterios().compareTo(c1.getNumeroCriterios());
							if (ret == 0) {
								if (ret == 0) {
									Boolean actoryDemandado1 = c1.isCoincidenciaActor() && c1.isCoincidenciaDemandado();
									Boolean actoryDemandado2 = c2.isCoincidenciaActor() && c2.isCoincidenciaDemandado();
									ret = actoryDemandado2.compareTo(actoryDemandado1);
								}
								if (ret == 0) {
									ret = c2.getNumeroCoincidencias().compareTo(c1.getNumeroCoincidencias());
									if (ret == 0) {
										if(ExpedienteManager.isPenal(c1.getExpedienteOrigen()) ) {
											ret = c2.getCUITOcurrences().compareTo(c1.getCUITOcurrences());
										} else {
											ret = c2.getCUITDNIOcurrences().compareTo(c1.getCUITDNIOcurrences());
										}
									}
								}
							}
						}
						if (ret == 0) {
							Date fecha1 = getFechaExpediente(c1.getExpedienteRelacionado());
							Date fecha2 = getFechaExpediente(c2.getExpedienteRelacionado());
							if (fecha1 != null && fecha2 != null) {
								if (ConfiguracionMateriaManager.instance()
										.isConexidadPrioridadAntiguos(
												c1.getExpedienteOrigen().getCamara(),
												c1.getExpedienteOrigen().getMateria())) {
									ret = fecha1.compareTo(fecha2);
								} else {
									ret = fecha2.compareTo(fecha1);
								}
							}
							if (ret == 0) {
								if (ConfiguracionMateriaManager.instance()
										.isConexidadPrioridadAntiguos(
												c1.getExpedienteOrigen().getCamara(),
												c1.getExpedienteOrigen().getMateria())) {
									ret = c1.getExpedienteRelacionado().getAnio().compareTo(c2.getExpedienteRelacionado().getAnio());
								} else {
									ret = c2.getExpedienteRelacionado().getAnio().compareTo(c1.getExpedienteRelacionado().getAnio());
								}
								if (ret == 0) {
									if (ConfiguracionMateriaManager.instance()
											.isConexidadPrioridadAntiguos(
													c1.getExpedienteOrigen().getCamara(),
													c1.getExpedienteOrigen().getMateria())) {
										ret = c1.getExpedienteRelacionado().getNumero().compareTo(c2.getExpedienteRelacionado().getNumero());
									} else {
										ret = c2.getExpedienteRelacionado().getNumero().compareTo(c1.getExpedienteRelacionado().getNumero());
									}
								}
							}
						}
					}
					return ret;
				}
			});
		}
	}

	public boolean isNaturalezaPrincipal(Expediente expediente) {
		return NaturalezaSubexpedienteEnumeration.Type.principal.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}
	
	public boolean isAsigna(Conexidad conexidad) {
		boolean asigna = false;
		for (CriterioCnx criterio : conexidad.getCriterioCnxList()) {
			if (criterio.isAsigna()) {
				asigna = true;
			}
		}
		if (conexidad.getExpedienteOrigen().getObjetoJuicio().getAsignaConexidad() != null) {
			if (AsignaConexidadEnumeration.Type.si.getValue().equals(
					conexidad.getExpedienteOrigen().getObjetoJuicio().getAsignaConexidad())) {
				asigna = true;
			} else if (AsignaConexidadEnumeration.Type.no.getValue().equals(
					conexidad.getExpedienteOrigen().getObjetoJuicio().getAsignaConexidad())) {
				asigna = false;
			} else if (AsignaConexidadEnumeration.Type.siTodos.getValue().equals(
					conexidad.getExpedienteOrigen().getObjetoJuicio().getAsignaConexidad())) {
				Collection<CriterioCnx> criterios = ConexidadManager.instance().getCriterios(
						conexidad.getExpedienteOrigen().getTipoCausa(),
						conexidad.getExpedienteOrigen().getObjetoJuicioPrincipal(),
						ExpedienteManager.getMateria(conexidad.getExpedienteOrigen()),
						conexidad.getExpedienteOrigen().isVoluntario(), true);
				if ((criterios != null) && (conexidad.getCriterioCnxList().size() >= criterios.size())) {
					asigna = true;
				}
			}
		}
		return asigna;
	}

	protected boolean areEquals(Expediente expediente1, Expediente expediente2) {
		return (expediente1 != null) && (expediente2 != null)
				&& expediente1.getNumero().equals(expediente2.getNumero())
				&& expediente1.getAnio().equals(expediente2.getAnio());
	}

	protected boolean isIniciado(Expediente expediente) {
		return ExpedienteManager.instance().isIniciado(expediente);
	}

	protected Date getFechaExpediente(Expediente expediente) {
		Date fecha = null;
		if (isIniciado(expediente)) {
			fecha = expediente.getFechaInicio();
		}
		if (fecha == null) {
			fecha = expediente.getFechaSorteoCarga();
		}
		if (fecha == null) {
			fecha = expediente.getFechaIngreso();
		}
		return fecha;
	}

	public Integer getPrioridad(CriterioCnx criterio) {
		if (criterio.getPrioridad() == null) {
			return Conexidad.MAX_PRIORIDAD_VALUE;
		}
		return criterio.getPrioridad();
	}

	private void ordenarCriterios(List<CriterioCnx> criterioList) {
		if (criterioList.size() > 0) {
			Collections.sort(criterioList, new Comparator<CriterioCnx>() {
				@Override
				public int compare(CriterioCnx c1, CriterioCnx c2) {
					int ret = 0;

					ret = getPrioridad(c1).compareTo(getPrioridad(c2)); // Mayor
					// prioridad
					// si el
					// valor
					// es
					// menor
					return ret;
				}
			});
		}
	}

	public List<CriterioCnx> getCriterios(TipoCausa tipoCausa, ObjetoJuicio objetoJuicioPrincipal, Materia materia, boolean voluntario, boolean ignoreCheckDobleInicio) {

		Map<String, CriterioCnx> criteriosMap = new HashMap<String, CriterioCnx>();


		if (tipoCausa != null && tipoCausa.isBuscaConexidad()) {
			addCriterios(criteriosMap, tipoCausa.getCriterioCnxList(), voluntario, ignoreCheckDobleInicio);
		}
		if (criteriosMap.size() == 0) { // Si hay criterios a nivel de Tipo de
			// Causa NO se consideran los definidos a nivel de OJ, Tema o Materia
			if (tipoCausa == null || tipoCausa.isBuscaConexidad()) {
				if (objetoJuicioPrincipal != null && objetoJuicioPrincipal.isBuscaConexidad()) {
					addCriterios(criteriosMap, objetoJuicioPrincipal.getCriterioCnxList(), voluntario, ignoreCheckDobleInicio);
					addCriterios(criteriosMap, objetoJuicioPrincipal.getTema(), voluntario, ignoreCheckDobleInicio);
					addCriterios(criteriosMap, materia, voluntario, ignoreCheckDobleInicio);
	
					// if (criteriosMap.isEmpty()) {
					// addCriteriosPorDefecto(criteriosMap, objetoJuicioPrincipal);
					// }
				}
			}
			// Criterios de la Camara (Casacion)
			addCriterios(criteriosMap, voluntario, ignoreCheckDobleInicio);

		}
		List<CriterioCnx> criterioList = new ArrayList<CriterioCnx>(criteriosMap.values());
		ordenarCriterios(criterioList);
		return criterioList;
	}

	public List<CriterioCnx> getCriteriosDobleInicio(TipoCausa tipoCausa, ObjetoJuicio objetoJuicioPrincipal,
			Materia materia, boolean voluntario) {
		List<CriterioCnx> criteriosDobleInicio = new ArrayList<CriterioCnx>();
		for (CriterioCnx criterio : getCriterios(tipoCausa, objetoJuicioPrincipal, materia, voluntario, false)) {
			if (criterio.isDobleInicio()) {
				criteriosDobleInicio.add(criterio);
			}
		}
		return criteriosDobleInicio;
	}

	public Integer getAnioDesde(ObjetoJuicio objetoJuicio, CriterioCnx criterio) {
		Integer aniosAntiguedad = null;
//		Se pasa a getFechaDesde() para que cuente años desde la fecha actual		
//		aniosAntiguedad = criterio.getAniosAntiguedad();
//		if (aniosAntiguedad == null) {
			aniosAntiguedad = ConfiguracionMateriaManager.instance().getConexidadAniosAntiguedad(
				sessionState.getGlobalCamara(), getMateria(objetoJuicio));
//		}
		if (aniosAntiguedad != null) {
			return DateUtil.getCurrentYear() - aniosAntiguedad;
		}
		return null;
	}

	public Date getFechaDesde(ObjetoJuicio objetoJuicio, CriterioCnx criterio) {
		Date desdeFecha = null;
		
		String buscaConexidadDesde = null;
		if (objetoJuicio != null) {
			buscaConexidadDesde = objetoJuicio.getBuscaConexidadDesde();
		}
		if (Strings.isEmpty(buscaConexidadDesde)) {
	
			if(criterio.getDesdeFecha() != null) {
				desdeFecha = criterio.getDesdeFecha();
			} else if(criterio.getAniosAntiguedad() != null) {
				buscaConexidadDesde = criterio.getAniosAntiguedad().toString() + "A";
			} else {
				buscaConexidadDesde = ConfiguracionMateriaManager.instance().getConexidadBuscaDesde(
					sessionState.getGlobalCamara(), getMateria(objetoJuicio));
			}
		}
		if (!Strings.isEmpty(buscaConexidadDesde)) {
			desdeFecha = getFechaAnterior(getToday(), buscaConexidadDesde);
		}

		return desdeFecha;
	}

	public Date getFechaHasta(ObjetoJuicio objetoJuicio) {
		String buscaConexidadHasta = null;
		if (objetoJuicio != null) {
			objetoJuicio.getBuscaConexidadHasta();
		}
		if (Strings.isEmpty(buscaConexidadHasta)) {
			buscaConexidadHasta = ConfiguracionMateriaManager.instance().getConexidadBuscaHasta(
					sessionState.getGlobalCamara(), getMateria(objetoJuicio));
		}
		if (!Strings.isEmpty(buscaConexidadHasta)) {
			return getFechaAnterior(getToday(), buscaConexidadHasta);
		}
		return null;
	}

	private void addCriterios(Map<String, CriterioCnx> criteriosMap, Boolean voluntario, boolean ignoreCheckDobleInicio) {
		List<CriterioCnx> criteriosCamara = CriterioCnxSearch.findByCamara(getEntityManager(), sessionState.getGlobalCamara());
		for (CriterioCnx criterio : criteriosCamara) {
			if (!criteriosMap.containsKey(criterio.getNombre())  && (criterio.getMateria() == null) && (criterio.getTema() == null)
					&& (criterio.getObjetoJuicio() == null) && (criterio.getStatus() == 0)) {
				if(!ignoreCheckDobleInicio || !criterio.isDobleInicio()) {
					if ((criterio.getJuicioVoluntario() == null)
							|| criterio.getJuicioVoluntario().equals(voluntario)) {
						criteriosMap.put(criterio.getNombre(), criterio);
					}
				}
			}
		}
	}

	private void addCriterios(Map<String, CriterioCnx> criteriosMap, Materia materia, Boolean voluntario, boolean ignoreCheckDobleInicio) {
		if (materia != null) {
			for (CriterioCnx criterio : materia.getCriterioCnxList()) {
				if ((criterio.getCamara() == null) || criterio.getCamara().equals(sessionState.getGlobalCamara())) {
					if (!criteriosMap.containsKey(criterio.getNombre()) && (criterio.getTema() == null)
							&& (criterio.getObjetoJuicio() == null) && (criterio.getStatus() == 0)) {
						if(!ignoreCheckDobleInicio || !criterio.isDobleInicio()) {
							if ((criterio.getJuicioVoluntario() == null)
									|| criterio.getJuicioVoluntario().equals(voluntario)) {
								criteriosMap.put(criterio.getNombre(), criterio);
							}
						}
					}
				}
			}
		}
	}

	private void addCriterios(Map<String, CriterioCnx> criteriosMap, Tema tema, Boolean voluntario, boolean ignoreCheckDobleInicio) {
		if (tema != null) {
			for (CriterioCnx criterio : tema.getCriterioCnxList()) {
				if ((criterio.getCamara() == null) || criterio.getCamara().equals(sessionState.getGlobalCamara())) {
					if (!criteriosMap.containsKey(criterio.getNombre()) && (criterio.getObjetoJuicio() == null)
							&& (criterio.getStatus() == 0)) {
						if(!ignoreCheckDobleInicio || !criterio.isDobleInicio()) {
							if ((criterio.getJuicioVoluntario() == null)
									|| criterio.getJuicioVoluntario().equals(voluntario)) {
								criteriosMap.put(criterio.getNombre(), criterio);
							}
						}
					}
				}
			}
			addCriterios(criteriosMap, tema.getTemaPadre(), voluntario, ignoreCheckDobleInicio);
		}
	}

	private void addCriterios(Map<String, CriterioCnx> criteriosMap, List<CriterioCnx> criterios, Boolean voluntario, boolean ignoreCheckDobleInicio) {

		for (CriterioCnx criterio : criterios) {
			if ((criterio.getCamara() == null) || criterio.getCamara().equals(sessionState.getGlobalCamara())) {
				if (!criteriosMap.containsKey(criterio.getNombre()) && criterio.getStatus() == 0) {
					if(!ignoreCheckDobleInicio || !criterio.isDobleInicio()) {
						if ((criterio.getJuicioVoluntario() == null) || criterio.getJuicioVoluntario().equals(voluntario)) {
							criteriosMap.put(criterio.getNombre(), criterio);
						}
					}
				}
			}
		}
	}

	private void filtrarPorFechas(ObjetoJuicio objetoJuicioPrincipal, CriterioCnx criterio) {
		antecedenteSearch.setFromFechaIngreso(getFechaDesde(objetoJuicioPrincipal, criterio));
		antecedenteSearch.setToFechaIngreso(getFechaHasta(objetoJuicioPrincipal));
	}

	// Calcula la fecha anterior a la especificada en un un numero de años,
	// meses o dias
	private static Date getFechaAnterior(Date date, String intervalo) {
		return ConfiguracionMateriaManager.getFechaAnterior(date, intervalo);
	}

	private Date getToday() {
		if (today == null) {
			today = DateUtil.getToday();
		}
		return today;
	}


	private List<String> toCodigoList(List<ObjetoJuicio> listaObjetos) {
		if (listaObjetos != null) {
			List<String> codigos = new ArrayList<String>();
			for (ObjetoJuicio objetoJuicio : listaObjetos) {
				codigos.add(objetoJuicio.getCodigo());
			}
			return codigos;
		}
		return null;
	}

	private String toString(List<String> lista) {
		if (lista != null) {
			StringBuffer result = new StringBuffer();
			for (String valor : lista) {
				if (result.length() > 0) {
					result.append(",");
				}
				result.append(valor);
			}
			return result.toString();
		}
		return null;
	}

	public boolean isConexidadFamiliaExtendida(Expediente expediente) {
		return ConfiguracionMateriaManager.instance().isConexidadFamiliaExtendida(SessionState.instance().getGlobalCamara(),
				expediente.getMateria());
	}

	public List<Expediente> getFamiliaExpediente(EntityManager entityManager, Expediente expediente) {
		return ExpedienteManager.getFamiliaExpediente(entityManager, expediente,
				isConexidadFamiliaExtendida(expediente));
	}

	public void addConexidadDeclarada(Expediente expediente, Expediente expedienteConexoDeclarado) {
		Conexidad conexidad = new Conexidad(expediente, expedienteConexoDeclarado);
		conexidad.setTipo((String) TipoConexidadEnumeration.Type.denunciada.getValue());
		conexidad.setPrincipal(false);
		getEntityManager().persist(conexidad);
		getEntityManager().flush();
		getEntityManager().refresh(expediente);
	}

	private boolean addConexidad(Expediente expediente, Expediente expedienteConexoDeclarado,
			List<Expediente> antecedentes, CriterioCnx criterio, boolean updateConexidad, boolean checkValidosParaAsignar, TipoRadicacionEnumeration.Type tipoRadicacion) {

//		boolean condicionIndistintaPartes = false;
//		for (CondicionCnx condicion : criterio.getCondicionCnxList()) {
//			if (isCondicionIndistintaPartes(condicion)) {
//				condicionIndistintaPartes = true;
//				break;
//			}
//		}
		int deshechados = 0;
		boolean validoParaAsignarFound = false;
		for (Expediente conexo : antecedentes) {
			if (OficinaAsignacionExpManager.instance().hasAnyRadicacionEnCamara(CamaraManager.getCamaraActual(), conexo)) {
				if( validarIntervinientesConDocumento(criterio, expediente, conexo) && validadFinalizacion(criterio, conexo) ) {
					Conexidad conexidad = findConexidad(expediente, conexo);
					if (conexidad == null) {
						conexidad = new Conexidad(expediente, conexo);
						if (areEquals(conexo, expedienteConexoDeclarado)) {
							conexidad.setTipo((String) TipoConexidadEnumeration.Type.denunciada.getValue());
						} else {
							conexidad.setTipo((String) TipoConexidadEnumeration.Type.automatica.getValue());
						}
						conexidad.setPrincipal(false);
						if (updateConexidad) {
							getEntityManager().persist(conexidad);
						}
						conexidadResult.add(conexidad);
					}
					if ((criterio != null) && (criterio.getId() != null) && !conexidad.getCriterioCnxList().contains(criterio)) {
						if (updateConexidad) {
							conexidad.getCriterioCnxList().add(criterio);
						}
					}
					if (checkValidosParaAsignar && !validoParaAsignarFound) {
						validoParaAsignarFound = ConexidadManager.instance().isAsigna(conexidad) && isValidoParaAsignar(conexidad, tipoRadicacion);
					}
				} else {
					deshechados++;
				}
			} else {
				deshechados++;
			}
		}
		if (deshechados > 0) {
			getTracer().notify("Fin Criterio", ((antecedentes != null)? antecedentes.size() : "0") + " expedientes encontrados, " + deshechados + " expedientes deshechados");
		} else {
			getTracer().notify("Fin Criterio", ((antecedentes != null)? antecedentes.size() : "0") + " expedientes encontrados");
		}
		getEntityManager().flush();
		getEntityManager().refresh(expediente);
		
		return validoParaAsignarFound;
	}
	
	private boolean validadFinalizacion(CriterioCnx criterio, Expediente expediente) {
		return ((criterio.getTiempoFinalizacion() == null) || !isFueraPlazoFinalizacion(criterio, expediente)) &&
			(!Boolean.TRUE.equals(criterio.getExpedientesEnTramite()) || !isFinalizado(expediente));
	}

	private boolean isConexidadDesecharPorDistintoDocumento() {
		return ConfiguracionMateriaManager.instance().isConexidadDesecharPorDistintoDocumento(CamaraManager.getCamaraActual(), null);
	}

	// Valida si los intervinientes encontrados con el mismo nombre que tengan documento, coinciden en el documento
	// Se Desecharan las conexidades encontradas en las que coindida el nombre pero no el documento (si son validas si el documento es nulo en cualquiera de los dos)
	private boolean validarIntervinientesConDocumento(CriterioCnx criterio,
			Expediente expediente, Expediente conexo) {

		boolean ok = true;
		
		if (isConexidadDesecharPorDistintoDocumento()) {
			for (CondicionCnx condicion: criterio.getCondicionCnxList()) {
				if(!condicion.isLogicalDeleted()) {
					if (parametroPredefinidoEnumeration.isActor(condicion.getParametro()) || 
						parametroPredefinidoEnumeration.isPartes(condicion.getParametro())){
						ok = validarIntervinientesConDocumento(expediente, conexo, ACTOR);
						if (!ok) {
							break;
						}
					}
					if (parametroPredefinidoEnumeration.isDemandado(condicion.getParametro()) ||
						parametroPredefinidoEnumeration.isPartes(condicion.getParametro())){
						ok = validarIntervinientesConDocumento(expediente, conexo, DEMANDADO);
						if (!ok) {
							break;
						}
					}
				}
			}
		}
		return ok;
	}

	private boolean validarIntervinientesConDocumento(Expediente expediente, Expediente conexo, Integer tipo) {
		boolean found = false;
		for (IntervinienteExp intervinienteExp : getIntervinientes(expediente)) {
			if (!intervinienteExp.isLogicalDeleted() && (((tipo == null) || tipo.equals(intervinienteExp.getTipoInterviniente().getNumeroOrden())))) {
				Interviniente interviniente= intervinienteExp.getInterviniente();
				if (findIntervinienteConDocumento(interviniente, conexo)) {
					found = true;
					break;
				}
			}
		}
		return found;
	}


	private boolean findIntervinienteConDocumento(Interviniente interviniente,
			Expediente conexo) {

		for (IntervinienteExp intervinienteExp : getIntervinientes(conexo)) {
			if(!intervinienteExp.isLogicalDeleted()) {
				Interviniente intervinienteConexo = intervinienteExp.getInterviniente();
				if (interviniente.getNombre().equals(intervinienteConexo.getNombre())) {
					if ((interviniente.getNumeroDocId() == null) || (intervinienteConexo.getNumeroDocId() == null) || !validaDocumento(intervinienteConexo.getNumeroDocId(), intervinienteConexo.getTipoDocumentoIdentidad())){
						return true;
					} else if (((interviniente.getNumeroDocId() != null) && interviniente.getNumeroDocId().equals(intervinienteConexo.getNumeroDocId()))){
						return true;
					}
					if (compareDNIs(interviniente.getDni(), intervinienteConexo.getDni(), false)){	// la comparacion es positiva si alguno es nulo porque coinciden los nombres
						return true;
					}
				} else if (((interviniente.getNumeroDocId() != null) && interviniente.getNumeroDocId().equals(intervinienteConexo.getNumeroDocId())) ||
						compareDNIs(interviniente.getDni(), intervinienteConexo.getDni(), true)){
					return true;
				}
			}
		}
		return false;
	}

	private boolean validaDocumento(String documento, TipoDocumentoIdentidad tipoDocumentoIdentidad) {
		if(tipoDocumentoIdentidad != null && !Strings.isEmpty(documento)){
			if (TipoDocumentoIdentidadEnumeration.isCuitCuil(tipoDocumentoIdentidad) && !CuilValidator.validateCUIL(documento)){
				return false;
			}
		}
		return true;
	}

	private boolean compareDNIs(String dni1, String dni2, boolean notNullRequired) {
		String formatedDni1 = CuilValidator.formatDNI(dni1);
		String formatedDni2 = CuilValidator.formatDNI(dni2);
		if ((dni1 == null) || (dni2 == null) ) {
			return !notNullRequired;	// Devuelve si la comparacion es positiva si alguno es nulo
		} else {
			return formatedDni1.equals(formatedDni2);
		}
	}
	
	public static boolean isValidoParaAsignar(Conexidad conexidad, TipoRadicacionEnumeration.Type tipoRadicacion) {
		Expediente expediente = conexidad.getExpedienteOrigen();
		Expediente expedienteConexo = conexidad.getExpedienteRelacionado();
		OficinaAsignacionExp oficinaAsignacion = OficinaAsignacionExpSearch.findByTipoRadicacion(getEntityManager(), expedienteConexo, (String)tipoRadicacion.getValue());
		Oficina oficina = null;
		if (oficinaAsignacion != null) {
			oficina = oficinaAsignacion.getOficinaConSecretaria();
			if(oficina != null) {
				if(CamaraManager.getCamaraActual().equals(oficina.getCamara())) {
					for(CriterioCnx criterio: conexidad.getCriterioCnxList()) {
						if(criterio.isAsigna() && (criterio.getTiempoFinalizacion() != null) &&	isFueraPlazoFinalizacion(criterio, expedienteConexo)){
							return false;
						}
					}
					if (ExpedienteManager.isPenal(expediente) || (ExpedienteManager.instance().isCivil(expediente))) {
						if ((ExpedienteManager.getCompetencia(expediente) == null) || (oficina.getCompetencia() == null) || (ExpedienteManager.getCompetencia(expediente).equals(oficina.getCompetencia()))){
							if (ExpedienteManager.instance().isCivil(expediente) ) {
								return true;
							}
							return (expedienteConexo.isEnTramite() && equalRubro(expediente, expedienteConexo));
						}
					} else if ((ExpedienteManager.isComercial(expediente))) {
						return OficinaManager.instance().admiteExpediente(oficina, expediente, tipoRadicacion);
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static boolean isFueraPlazoFinalizacion(CriterioCnx criterio, Expediente expediente) {
		Date minimaFechaFinalizacion = getMinimaFechaFinalizacion (criterio);
		if ((minimaFechaFinalizacion != null) && isFinalizado(expediente) && (expediente.getFechaEstado() != null) && minimaFechaFinalizacion.after(expediente.getFechaEstado()) ){
			return true;
		}
		return false;
	}
	
	private static Date getMinimaFechaFinalizacion (CriterioCnx criterio) {
		Date fechaFinalizacion = null;
		if(criterio.isAsigna() && (criterio.getTiempoFinalizacion() != null)) {
			fechaFinalizacion = getFechaAnterior(DateUtil.getCurrentDate(), criterio.getTiempoFinalizacion());
		}
		return fechaFinalizacion;
	}
	
	private static boolean isFinalizado(Expediente expediente) {
		boolean finalizado = (expediente.getEstadoExpediente() != null) && (TipoFinalizacionEnumeration.Type.finalizacion_expediente.getValue().equals(expediente.getEstadoExpediente().getTipoFinalizacion()));
		if (!finalizado && CamaraManager.isCamaraActualComercial() || CamaraManager.isCamaraActualSeguridadSocial()) {
			// Se mira si ha tenido un estado anterior de finalizacion
			finalizado = ActuacionExpSearch.findLastByEstadoFinalizacion(getEntityManager(), expediente) != null;
		}
		return finalizado;
	}

	private static boolean equalRubro(Expediente expediente, Expediente expedienteConexo) {
		if ((expediente.getTipoCausa() != null) && (expediente.getTipoCausa().getRubro() != null) && (expedienteConexo.getTipoCausa() != null)) {
			return expediente.getTipoCausa().getRubro().equals(expedienteConexo.getTipoCausa().getRubro());
		}
		return false;
	}

	private Conexidad findConexidad(Expediente expediente, Expediente conexo) {
		Query query = getEntityManager().createQuery(CONEXIDAD_STMT);
		Conexidad conexidad = null;
		try {
			query.setParameter("expedienteOrigen", expediente);
			query.setParameter("expedienteRelacionado", conexo);
			conexidad = (Conexidad) query.getSingleResult();
		} catch (Exception e) {
		}
		return conexidad;
	}

	public void deleteConexidad(Expediente expediente) {
		for (Conexidad conexidad : expediente.getConexidadList()) {
			getEntityManager().remove(conexidad); // Se borra la conexidad
			// informativa
		}
		expediente.setConexidadList(new ArrayList<Conexidad>(0));
		getEntityManager().flush();
		getEntityManager().refresh(expediente);
	}

	private void deleteConexidadInformativa(Expediente expediente) {
		for (Conexidad conexidad : expediente.getConexidadList()) {
			if (!Boolean.TRUE.equals(conexidad.getPrincipal())
					&& !TipoConexidadEnumeration.Type.denunciada.getValue().equals(conexidad.getTipo())) {
				getEntityManager().remove(conexidad); // Se borra la conexidad
				// informativa
			}
		}
		expediente.setConexidadList(new ArrayList<Conexidad>(0));
		getEntityManager().flush();
		getEntityManager().refresh(expediente);
	}

	private List<ObjetoJuicio> getListaObjetosExcluidos(ObjetoJuicio objetoJuicioPrincipal, CriterioCnx criterio) {
		List<ObjetoJuicio> objetosExcluidos = null;
		if (criterio.getObjetoJuicioList().size() == 0) {
			if (Boolean.TRUE.equals(objetoJuicioPrincipal.isExcluirObjetosJuicio())
					&& (objetoJuicioPrincipal.getObjetoJuicioList().size() > 0)) {
				objetosExcluidos = objetoJuicioPrincipal.getObjetoJuicioList();
			}
		} else {
			if (Boolean.TRUE.equals(criterio.getExcluirObjetosJuicio())) {
				objetosExcluidos = criterio.getObjetoJuicioList();
			}
		}
		return objetosExcluidos;
	}

	private List<ObjetoJuicio> getListaObjetosIncluidos(ObjetoJuicio objetoJuicioPrincipal, CriterioCnx criterio) {
		List<ObjetoJuicio> objetosIncluidos = null;

		if (Boolean.TRUE.equals(criterio.getIgualObjetoJuicio())) {
			objetosIncluidos = new ArrayList<ObjetoJuicio>();
			objetosIncluidos.add(objetoJuicioPrincipal);
		} else {
			if (criterio.getObjetoJuicioList().size() == 0) {
				if (!Boolean.TRUE.equals(objetoJuicioPrincipal.isExcluirObjetosJuicio())
						&& (objetoJuicioPrincipal.getObjetoJuicioList().size() > 0)) {
					objetosIncluidos = objetoJuicioPrincipal.getObjetoJuicioList();
				}
			} else {
				if (!Boolean.TRUE.equals(criterio.getExcluirObjetosJuicio())) {
					objetosIncluidos = criterio.getObjetoJuicioList();
				}
			}
		}
		return objetosIncluidos;
	}

	private void filtrarPorNaturalezas(List<String> naturalezas) {
		if ((naturalezas != null) && naturalezas.size() > 0) {
			if (naturalezas.size() == 1) {
				antecedenteSearch.setNaturalezaSubexpediente(naturalezas.get(0));
			} else {
				antecedenteSearch.setNaturalezaSubexpedienteList(naturalezas);
			}
		}
	}

	private void filtrarPorTipoCausa(TipoCausa tipoCausa, CriterioCnx criterio) {
		List<String> codigoTipoCausaIncluidasList = criterio.getCodigosTipoCausaList();

		if ((codigoTipoCausaIncluidasList != null) && codigoTipoCausaIncluidasList.size() > 0) {
			antecedenteSearch.setCodigoTiposCausaIncluidos(codigoTipoCausaIncluidasList);
		}
	}

	private void filtrarPorAmbito(ObjetoJuicio objetoJuicioPrincipal, CriterioCnx criterio) {
		List<ObjetoJuicio> objetosExcluidos = getListaObjetosExcluidos(objetoJuicioPrincipal, criterio);
		List<ObjetoJuicio> objetosIncluidos = getListaObjetosIncluidos(objetoJuicioPrincipal, criterio);

		if ((objetosExcluidos != null) && objetosExcluidos.size() > 0) {
			antecedenteSearch.setObjetosJuicioExcluidos(objetosExcluidos);
		}
		if ((objetosIncluidos != null) && objetosIncluidos.size() > 0) {
			antecedenteSearch.setObjetosJuicioIncluidos(objetosIncluidos);
		}

//		List<String> codigoTemasIncluidosList = criterio.getCodigosTemaList();
		List<Integer> idTemasIncluidosList = getIdTemasIncluidosByCodigo(sessionState.getGlobalCamara(), criterio.getCodigosTemaList());

		if ((idTemasIncluidosList != null) && idTemasIncluidosList.size() > 0) {
			antecedenteSearch.setIdTemasIncluidos(idTemasIncluidosList);
		}
		
		if (criterio.getAmbitoVoluntarios() != null) {
			antecedenteSearch.setVoluntario(criterio.getAmbitoVoluntarios());
		}
		if (!criterio.isAmbitoGlobal()) {
			antecedenteSearch.setCamara(SessionState.instance().getGlobalCamara());
		}
		if (criterio.isAmbitoGlobal() && CamaraManager.isCamaraActualSeguridadSocial()) {
			antecedenteSearch.setCamaraOriginal(SessionState.instance().getGlobalCamara());
//			antecedenteSearch.setCamaraRadicacion(SessionState.instance().getGlobalCamara());
		}

	}

	private boolean isEmpty(List<String> list) {
		return (list == null) || (list.size() == 0);
	}

	private boolean filtrarPorCondicion(CondicionCnx condicion, Expediente expediente,
			List<ParametroExpediente> parametrosExpediente) {
		boolean filtered = false;
		
		boolean excluirPersonasJuridicas = condicion.getCriterioCnx().isExcluirPersonasJuridicas();
		boolean excluirFrecuentes = ConfiguracionMateriaManager.instance().isConexidadNoAplicaSiFrecuentes(SessionState.instance().getGlobalCamara(), expediente.getMateria()); 
			
		if (excluirPersonasJuridicas) {
			antecedenteSearch.setExcluirPersonasJuridicas (true); 
		}
		
		if (isCondicionPorCualquierIdentificacionPartes(condicion)) { // Busqueda indistinta por nombre, dni o cuil
			if (parametroPredefinidoEnumeration.isDemandado(condicion.getParametro())) {
				if (hasParteNoFrecuente(expediente, DEMANDADO, getTipoBusca(DEMANDADO, condicion.getTipoComparacion()), excluirPersonasJuridicas, excluirFrecuentes)) {
					antecedenteSearch.setTipoFiltroPartes(DEMANDADO);
					filtered = true;
				}
			} else if (parametroPredefinidoEnumeration.isActor(condicion.getParametro())) {
				if (hasParteNoFrecuente(expediente, ACTOR, getTipoBusca(ACTOR, condicion.getTipoComparacion()), excluirPersonasJuridicas, excluirFrecuentes)) {
					antecedenteSearch.setTipoFiltroPartes(ACTOR);
					filtered = true;
				}
			} else if (hasParte(expediente, ACTOR, getTipoBusca(ACTOR, condicion.getTipoComparacion()), excluirPersonasJuridicas) && 
					   hasParte(expediente, DEMANDADO, getTipoBusca(DEMANDADO, condicion.getTipoComparacion()), excluirPersonasJuridicas) && 
					   (
						hasParteNoFrecuente(expediente, ACTOR, getTipoBusca(ACTOR, condicion.getTipoComparacion()), excluirPersonasJuridicas, excluirFrecuentes) 
						||
						hasParteNoFrecuente(expediente, DEMANDADO, getTipoBusca(DEMANDADO, condicion.getTipoComparacion()), excluirPersonasJuridicas, excluirFrecuentes)
					   )) {
				antecedenteSearch.setTipoFiltroPartes(null);
				filtered = true;
			}
			antecedenteSearch.setTipoComparacionPartes(condicion.getTipoComparacion());
		} else if (isCondicionPorIdentificacionUnicaPartes(condicion)) {
			List<String> actores = getIdentidadPartes(condicion, expediente, ACTOR, excluirPersonasJuridicas);
			List<String> demandados = getIdentidadPartes(condicion, expediente, DEMANDADO, excluirPersonasJuridicas);
			if (!isEmpty(actores) && !isEmpty(demandados)) {
				filtered = true;
				if (parametroPredefinidoEnumeration.isCuitPartes(condicion.getParametro())) {
					if (TipoComparacionEnumeration.Type.indistinta.getValue().equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setCuitParte1List(actores);
						antecedenteSearch.setCuitParte2List(demandados);
					} else if (TipoComparacionEnumeration.Type.cruzada.getValue()
							.equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setCuitActorList(demandados);
						antecedenteSearch.setCuitDemandadoList(actores);
					} else if (TipoComparacionEnumeration.Type.paralela.getValue().equals(
							condicion.getTipoComparacion())) {
						antecedenteSearch.setCuitActorList(actores);
						antecedenteSearch.setCuitDemandadoList(demandados);
					}
				} else if (parametroPredefinidoEnumeration.isDniPartes(condicion.getParametro())) {
					if (TipoComparacionEnumeration.Type.indistinta.getValue().equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setDniParte1List(actores);
						antecedenteSearch.setDniParte2List(demandados);
					} else if (TipoComparacionEnumeration.Type.cruzada.getValue()
							.equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setDniActorList(demandados);
						antecedenteSearch.setDniDemandadoList(actores);
					} else if (TipoComparacionEnumeration.Type.paralela.getValue().equals(
							condicion.getTipoComparacion())) {
						antecedenteSearch.setDniActorList(actores);
						antecedenteSearch.setDniDemandadoList(demandados);
					}
				} else if (parametroPredefinidoEnumeration.isNombrePartes(condicion.getParametro())) {
					if (TipoComparacionEnumeration.Type.indistinta.getValue().equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setNombreParte1List(actores);
						antecedenteSearch.setNombreParte2List(demandados);
					} else if (TipoComparacionEnumeration.Type.cruzada.getValue()
							.equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setNombreActorList(demandados);
						antecedenteSearch.setNombreDemandadoList(actores);
					} else if (TipoComparacionEnumeration.Type.paralela.getValue().equals(
							condicion.getTipoComparacion())) {
						antecedenteSearch.setNombreActorList(actores);
						antecedenteSearch.setNombreDemandadoList(demandados);
					}
				}
			}
		} else if (isCondicionPorIdentificacionUnicaActor(condicion)) {
			List<String> actores = getIdentidadPartes(condicion, expediente, ACTOR, excluirPersonasJuridicas);
			if (!isEmpty(actores)) {
				filtered = true;
				if (parametroPredefinidoEnumeration.isCuitActor(condicion.getParametro())) {
					if (TipoComparacionEnumeration.Type.indistinta.getValue().equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setCuitParte1List(actores);
					} else if (TipoComparacionEnumeration.Type.cruzada.getValue()
							.equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setCuitDemandadoList(actores);
					} else if (TipoComparacionEnumeration.Type.paralela.getValue().equals(
							condicion.getTipoComparacion())) {
						antecedenteSearch.setCuitActorList(actores);
					}
				} else if (parametroPredefinidoEnumeration.isDniActor(condicion.getParametro())) {
					if (TipoComparacionEnumeration.Type.indistinta.getValue().equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setDniParte1List(actores);
					} else if (TipoComparacionEnumeration.Type.cruzada.getValue()
							.equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setDniDemandadoList(actores);
					} else if (TipoComparacionEnumeration.Type.paralela.getValue().equals(
							condicion.getTipoComparacion())) {
						antecedenteSearch.setDniActorList(actores);
					}
				} else if (parametroPredefinidoEnumeration.isNombreActor(condicion.getParametro())) {
					if (TipoComparacionEnumeration.Type.indistinta.getValue().equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setNombreParte1List(actores);
					} else if (TipoComparacionEnumeration.Type.cruzada.getValue()
							.equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setNombreDemandadoList(actores);
					} else if (TipoComparacionEnumeration.Type.paralela.getValue().equals(
							condicion.getTipoComparacion())) {
						antecedenteSearch.setNombreActorList(actores);
					}
				}
			}
		} else if (isCondicionPorIdentificacionUnicaDemandado(condicion)) {
			List<String> demandados = getIdentidadPartes(condicion, expediente, DEMANDADO, excluirPersonasJuridicas);
			if (!isEmpty(demandados)) {
				filtered = true;
				if (parametroPredefinidoEnumeration.isCuitDemandado(condicion.getParametro())) {
					if (TipoComparacionEnumeration.Type.indistinta.getValue().equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setCuitParte2List(demandados);
					} else if (TipoComparacionEnumeration.Type.cruzada.getValue()
							.equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setCuitActorList(demandados);
					} else if (TipoComparacionEnumeration.Type.paralela.getValue().equals(
							condicion.getTipoComparacion())) {
						antecedenteSearch.setCuitDemandadoList(demandados);
					}
				} else if (parametroPredefinidoEnumeration.isDniDemandado(condicion.getParametro())) {
					if (TipoComparacionEnumeration.Type.indistinta.getValue().equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setDniParte2List(demandados);
					} else if (TipoComparacionEnumeration.Type.cruzada.getValue()
							.equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setDniActorList(demandados);
					} else if (TipoComparacionEnumeration.Type.paralela.getValue().equals(
							condicion.getTipoComparacion())) {
						antecedenteSearch.setDniDemandadoList(demandados);
					}
				} else if (parametroPredefinidoEnumeration.isNombreDemandado(condicion.getParametro())) {
					if (TipoComparacionEnumeration.Type.indistinta.getValue().equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setNombreParte2List(demandados);
					} else if (TipoComparacionEnumeration.Type.cruzada.getValue()
							.equals(condicion.getTipoComparacion())) {
						antecedenteSearch.setNombreActorList(demandados);
					} else if (TipoComparacionEnumeration.Type.paralela.getValue().equals(
							condicion.getTipoComparacion())) {
						antecedenteSearch.setNombreDemandadoList(demandados);
					}
				}
			}
		} else if (parametroPredefinidoEnumeration.isOrigen(condicion.getParametro())) {
			if (expediente.getExpedienteEspecial() != null) {
				String organismoOrigen = expediente.getExpedienteEspecial().getDependenciaOrigen();
				String numeroExpedienteOrigen = expediente.getExpedienteEspecial().getCodigoRelacionado();
				if (!Strings.isEmpty(organismoOrigen) && !Strings.isEmpty(numeroExpedienteOrigen)) {
					filtered = true;
					antecedenteSearch.setDependenciaOrigen(organismoOrigen);
					antecedenteSearch.setCodigoRelacionado(numeroExpedienteOrigen);
				}
			}
		} else if (parametroPredefinidoEnumeration.isNumeroExpedienteOrigen(condicion.getParametro())) {
			// Solo comprueba el numero de expediente origen
			if (expediente.getExpedienteEspecial() != null) {
				String numeroExpedienteOrigen = expediente.getExpedienteEspecial().getCodigoRelacionado();
				if (!Strings.isEmpty(numeroExpedienteOrigen)) {
					filtered = true;
					antecedenteSearch.setCodigoRelacionado(numeroExpedienteOrigen);
				}
			}
		} else { // parametro del expediente
			String values = findParametroValues(condicion.getParametro(), parametrosExpediente);
			if (values != null) {
				filtered = true;
				antecedenteSearch.setValorParametro(condicion.getParametro(), values);
			}
		}
		return filtered;
	}

	public static Integer getTipoBusca(Integer tipoParte, String tipoComparacion) {
		Integer tipoBusca = null;
		if(TipoComparacionEnumeration.Type.paralela.getValue().equals(tipoComparacion)){
			tipoBusca = tipoParte;
		} else if(TipoComparacionEnumeration.Type.cruzada.getValue().equals(tipoComparacion)){
			tipoBusca = (ACTOR == tipoParte)? DEMANDADO: ACTOR;
		}
		return tipoBusca;
	}
	
	public List<String> getIdentidadPartes(CondicionCnx condicion, Expediente expediente, Integer tipoParte, boolean excluirPersonasJuridicas) {
		List<String> identidadPartes = null;
		Integer tipoBusca = getTipoBusca(tipoParte, condicion.getTipoComparacion());
		if (parametroPredefinidoEnumeration.isCuitPartes(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isCuitActor(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isCuitDemandado(condicion.getParametro())) {
			identidadPartes = getCuitParteList(expediente, tipoParte, tipoBusca, excluirPersonasJuridicas);
			List<String> dniPartes= getDniParteList(expediente, tipoParte, tipoBusca, excluirPersonasJuridicas);
			if (dniPartes != null) {
				if (identidadPartes != null) {
					for(String dni: dniPartes) {
						if(!identidadPartes.contains(dni)) {
							identidadPartes.add(dni);
						}
					}
				} else{
					identidadPartes = dniPartes;
				}
			}
		} else if (parametroPredefinidoEnumeration.isDniPartes(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isDniActor(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isDniDemandado(condicion.getParametro())) {
			identidadPartes = getDniParteList(expediente, tipoParte, tipoBusca, excluirPersonasJuridicas);
		} else if (parametroPredefinidoEnumeration.isNombrePartes(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isNombreActor(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isNombreDemandado(condicion.getParametro())) {
			identidadPartes = getNombreParteList(expediente, tipoParte, tipoBusca, excluirPersonasJuridicas);
		}
		return identidadPartes;
	}

//	private boolean isCondicionIndistintaPartes(CondicionCnx condicion) {
//		return (TipoComparacionEnumeration.Type.indistinta.getValue().equals(condicion.getTipoComparacion()))
//			&&
//			(
//			parametroPredefinidoEnumeration.isPartes(condicion.getParametro())
//			|| parametroPredefinidoEnumeration.isNombrePartes(condicion.getParametro())
//			|| parametroPredefinidoEnumeration.isCuitPartes(condicion.getParametro())
//			|| parametroPredefinidoEnumeration.isDniPartes(condicion.getParametro())
//			);
//	}

	private boolean isCondicionPorCualquierIdentificacionPartes(CondicionCnx condicion) {
		return parametroPredefinidoEnumeration.isPartes(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isDemandado(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isActor(condicion.getParametro());
	}

	private boolean isCondicionPorIdentificacionUnicaPartes(CondicionCnx condicion) {
		return parametroPredefinidoEnumeration.isCuitPartes(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isDniPartes(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isNombrePartes(condicion.getParametro());
	}

	private boolean isCondicionPorIdentificacionUnicaActor(CondicionCnx condicion) {
		return parametroPredefinidoEnumeration.isCuitActor(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isDniActor(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isNombreActor(condicion.getParametro());
	}

	private boolean isCondicionPorIdentificacionUnicaDemandado(CondicionCnx condicion) {
		return parametroPredefinidoEnumeration.isCuitDemandado(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isDniDemandado(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isNombreDemandado(condicion.getParametro());
	}

	private boolean isCondicionPorOrigen(CondicionCnx condicion) {
		return parametroPredefinidoEnumeration.isOrigen(condicion.getParametro())
				|| parametroPredefinidoEnumeration.isNumeroExpedienteOrigen(condicion.getParametro());
	}

	private String findParametroValues(String parametro, List<ParametroExpediente> parametrosExpediente) {
		List<String> valueList = findParametroValueList(parametro, parametrosExpediente);
		if ((valueList != null) && (valueList.size() > 0)){
			return Configuration.getCommaSeparatedString(valueList);
		}
		return null;
	}

	private List<String> findParametroValueList(String nombre, List<ParametroExpediente> parametrosExpediente) {
		List<String> valueList = new ArrayList<String>();
		if (parametrosExpediente != null) {
			for (ParametroExpediente parametroExpediente : parametrosExpediente) {
				if (!parametroExpediente.isLogicalDeleted()) {
					if (nombre.equals(parametroExpediente.getParametro().getName()) && !Strings.isEmpty(parametroExpediente.getValor())) {
						valueList.add(parametroExpediente.getValor());
					}
				}
			}
		}
		return valueList;
	}

	private ParametroExpediente findParametro(String nombre, List<ParametroExpediente> parametrosExpediente) {
		if (parametrosExpediente != null) {
			for (ParametroExpediente parametroExpediente : parametrosExpediente) {
				if (nombre.equals(parametroExpediente.getParametro().getName())) {
					return parametroExpediente;
				}
			}
		}
		return null;
	}

	private List<ParametroExpediente> findParametros(String nombre, List<ParametroExpediente> parametrosExpediente) {
		List<ParametroExpediente> result = new ArrayList<ParametroExpediente>();
		if (parametrosExpediente != null) {
			for (ParametroExpediente parametroExpediente : parametrosExpediente) {
				if (nombre.equals(parametroExpediente.getParametro().getName())) {
					result.add(parametroExpediente);
				}
			}
		}
		return result;
	}

	private void addCriteriosPorDefecto(Map<String, CriterioCnx> criteriosMap, ObjetoJuicio objetoJuicio) {
		CriterioCnx criterioPorDefecto = new CriterioCnx();
		criterioPorDefecto.setObjetoJuicio(objetoJuicio);
		criterioPorDefecto.setExcluirObjetosJuicio(false);
		criterioPorDefecto.setNombre("Partes");
		List<CondicionCnx> condicionCnxList = new ArrayList<CondicionCnx>();
		CondicionCnx condicionCnx = new CondicionCnx();
		condicionCnx.setParametro((String) ParametroPredefinidoEnumeration.Type.partes.getValue());
		condicionCnx.setTipoComparacion((String) TipoComparacionEnumeration.Type.paralela.getValue());
		condicionCnxList.add(condicionCnx);
		criterioPorDefecto.setCondicionCnxList(condicionCnxList);

		criteriosMap.put(criterioPorDefecto.getNombre(), criterioPorDefecto);

		// criterioPorDefecto = new CriterioCnx();
		// criterioPorDefecto.setObjetoJuicio(objetoJuicio);
		// criterioPorDefecto.setExcluirObjetosJuicio(false);
		// criterioPorDefecto.setNombre("Demandado");
		// condicionCnxList = new ArrayList<CondicionCnx>();
		// condicionCnx = new CondicionCnx();
		// condicionCnx.setParametro((String)ParametroPredefinidoEnumeration.Type.demandado.getValue());
		// condicionCnx.setTipoComparacion((String)TipoComparacionEnumeration.Type.paralela.getValue());
		// condicionCnxList.add(condicionCnx);
		// criterioPorDefecto.setCondicionCnxList(condicionCnxList);
		//
		// criteriosMap.put(criterioPorDefecto.getNombre(), criterioPorDefecto);
	}

	private static List<IntervinienteExp> getIntervinientes(Expediente expediente) {
		if ((expediente.getIntervinienteExpList().size() == 0) && !ExpedienteManager.isExpedienteBase(expediente)) {
			Expediente expedienteBase = ExpedienteManager.getExpedienteBase(getEntityManager(), expediente);
			if (expedienteBase != null) {
				return expedienteBase.getIntervinienteExpList();
			}
		}
		return expediente.getIntervinienteExpList();
	}

	public static boolean isSobreseido(IntervinienteExp parte) {
		if (parte.getEstadoInterviniente() != null) {
			return EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO.equals(parte.getEstadoInterviniente().getCodigo());
		}
		return false;
	}

//	public static boolean isExcluirConexidad(IntervinienteExp parte, Integer tipoBusca, boolean excluirPersonasJuridicas) {
//		
//	}
	
	public static boolean isExcluirConexidad(IntervinienteExp parte, Integer tipoBusca, boolean excluirPersonasJuridicas, Camara camara) {	// No participa en la busqueda de conexidad
		if (TipoIntervinienteEnumeration.isNN(parte.getTipoInterviniente())
				|| TipoIntervinienteEnumeration.NN_NOMBRE.equals(parte.getInterviniente().getNombre())) {
			return true;
		}
		if (parte.getInterviniente() != null) {
			if(excluirPersonasJuridicas && parte.getInterviniente().isPersonaJuridica()) {
				return true;
			}
			EntityManager entityManager = (EntityManager) Component.getInstance("entityManager");
			Sigla sigla = SiglaSearch.findByDescripcion(entityManager, parte.getInterviniente().getNombre(), camara);
			if(sigla == null) {
				sigla = SiglaSearch.findByNombreAlternativo(entityManager, parte.getInterviniente().getNombre(), camara);
			}
			if (sigla != null) {
				return sigla.isExcluirConexidad() ||
						(((tipoBusca == null) || (ACTOR == tipoBusca)) && sigla.isExcluirConexidadActor()) ||
						(((tipoBusca == null) || (DEMANDADO == tipoBusca)) && sigla.isExcluirConexidadDemandado())
						;
			}
		}
		return false;
	}

	public static boolean hasParte(Expediente expediente, Integer tipo, Integer tipoBusca, boolean excluirPersonasJuridicas) {
		Camara camara = SessionState.instance().getGlobalCamara();
		for (IntervinienteExp parte : getIntervinientes(expediente)) {
			if (!parte.isLogicalDeleted() && !isExcluirConexidad(parte, tipoBusca, excluirPersonasJuridicas, camara) && !parte.isIdentidadReservada()
					&& !isSobreseido(parte) && (parte.getTipoInterviniente() != null)
					&& (parte.getTipoInterviniente().getNumeroOrden() != null)
					&& ((tipo == null) || tipo.equals(parte.getTipoInterviniente().getNumeroOrden()))) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasParteNoFrecuente(Expediente expediente, Integer tipo, Integer tipoBusca, boolean excluirPersonasJuridicas, boolean excluirFrecuentes) {
		Camara camara = SessionState.instance().getGlobalCamara();
		for (IntervinienteExp parte : getIntervinientes(expediente)) {
			if (!parte.isLogicalDeleted() && (parte.getTipoInterviniente() != null)
					&& (parte.getTipoInterviniente().getNumeroOrden() != null)
					&& !isExcluirConexidad(parte, tipoBusca, excluirPersonasJuridicas, camara) && !parte.isIdentidadReservada()
					&& !isSobreseido(parte) 
					&& ((tipo == null) || tipo.equals(parte.getTipoInterviniente().getNumeroOrden()))) {
				EntityManager entityManager = (EntityManager) Component.getInstance("entityManager");
				if(!excluirFrecuentes || !SiglaSearch.isFrecuente(entityManager, parte.getInterviniente().getNombre(), camara)) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<String> getCuitParteList(Expediente expediente, Integer tipo, Integer tipoBusca, boolean excluirPersonasJuridicas) {
		List<String> cuits = null;
		Camara camara = SessionState.instance().getGlobalCamara();
		for (IntervinienteExp parte : getIntervinientes(expediente)) {
			if (!parte.isLogicalDeleted() && !isExcluirConexidad(parte, tipoBusca, excluirPersonasJuridicas, camara) && !parte.isIdentidadReservada()
					&& !isSobreseido(parte)) {
				if ((parte.getTipoInterviniente() != null)
						&& ((tipo == null) || tipo.equals(parte.getTipoInterviniente().getNumeroOrden()))) {
//					if (TipoDocumentoIdentidadEnumeration.isCuitCuil(parte.getInterviniente()		// Se busca cualquier tipo de documento
//							.getTipoDocumentoIdentidad())) {
						String cuit = parte.getInterviniente().getNumeroDocId();
						if (!Strings.isEmpty(cuit)) {
							if (cuits == null) {
								cuits = new ArrayList<String>();
							}
							Sigla sigla = null;
							if (TipoDocumentoIdentidadEnumeration.isCuitCuil(parte.getInterviniente()
									.getTipoDocumentoIdentidad())) {
								sigla = SiglaSearch.findByCuit(getEntityManager(), cuit);
							}
							if (sigla != null) {
								cuits.addAll(SiglaSearch.getCuitsGrupoEmpresario(getEntityManager(), sigla));
							} else {
								cuits.add(cuit);
							}
						}
//					}
				}
			}
		}
		return cuits;
	}

	public static List<String> getDniParteList(Expediente expediente, Integer tipo, Integer tipoBusca, boolean excluirPersonasJuridicas) {
		List<String> dnis = null;
		Camara camara = SessionState.instance().getGlobalCamara();
		for (IntervinienteExp parte : getIntervinientes(expediente)) {
			if (!parte.isLogicalDeleted() && !isExcluirConexidad(parte, tipoBusca, excluirPersonasJuridicas, camara) && !parte.isIdentidadReservada()
					&& !isSobreseido(parte)) {
				if ((!parte.getInterviniente().isPersonaJuridica()) && (parte.getTipoInterviniente() != null)
						&& ((tipo == null) || tipo.equals(parte.getTipoInterviniente().getNumeroOrden()))) {
					String dni = CuilValidator.formatDNI(parte.getInterviniente().getDni());
					if (Strings.isEmpty(dni) &&  TipoDocumentoIdentidadEnumeration.isDni(parte.getInterviniente().getTipoDocumentoIdentidad())) {
						dni = parte.getInterviniente().getNumeroDocId();
					}
					if (!Strings.isEmpty(dni) && CuilValidator.validateDNI(dni)) {
						if (dnis == null) {
							dnis = new ArrayList<String>();
						}
						dnis.add(dni);
					}
				}
			}
		}
		return dnis;
	}

	public static List<String> getNombreParteList(Expediente expediente, Integer tipo, Integer tipoBusca, boolean excluirPersonasJuridicas) {
		List<String> nombres = null;
		Camara camara = SessionState.instance().getGlobalCamara();
		for (IntervinienteExp parte : getIntervinientes(expediente)) {
			if (!parte.isLogicalDeleted() && !isExcluirConexidad(parte, tipoBusca, excluirPersonasJuridicas, camara) && !parte.isIdentidadReservada()
					&& !isSobreseido(parte)) {
				if ((parte.getTipoInterviniente() != null)
						&& ((tipo == null) || tipo.equals(parte.getTipoInterviniente().getNumeroOrden()))) {
					String nombre = parte.getInterviniente().getNombre();
					if (!Strings.isEmpty(nombre)) {
						if (nombres == null) {
							nombres = new ArrayList<String>();
						}
						nombres.add(nombre);
					}
				}
			}
		}
		return nombres;
	}

	public static boolean hayPersonaJuridica(Expediente expediente, Integer tipo, Integer tipoBusca, boolean excluirPersonasJuridicas) {
		Camara camara = SessionState.instance().getGlobalCamara();
		for (IntervinienteExp parte : getIntervinientes(expediente)) {
			if (!parte.isLogicalDeleted() && !isExcluirConexidad(parte, tipoBusca, excluirPersonasJuridicas, camara) && !parte.isIdentidadReservada()
					&& !isSobreseido(parte)) {
				if ((parte.getTipoInterviniente() != null)
						&& ((tipo == null) || tipo.equals(parte.getTipoInterviniente().getNumeroOrden()))) {
					if(parte.getInterviniente().isPersonaJuridica()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public List<Conexidad> getResult() {
		return conexidadResult;
	}

	public void setResult(List<Conexidad> result) {
		this.conexidadResult = result;
	}

	private void actualizarCoincidencias(List<Conexidad> conexidadList) {
		for (Conexidad conexidad : conexidadList) {
			actualizarCoincidencias(conexidad);
		}
	}

	private void actualizarCoincidencias(Conexidad conexidad) {
		StringBuffer coincidencias = new StringBuffer();
		int numeroCoincidencias = 0;
		boolean demandadosDone = false;
		boolean actoresDone = false;
		for (CriterioCnx criterio : conexidad.getCriterioCnxList()) {
			for (CondicionCnx condicion : criterio.getCondicionCnxList()) {
				if(!condicion.isLogicalDeleted()) {
					if (parametroPredefinidoEnumeration.isDemandado(condicion.getParametro())
							|| parametroPredefinidoEnumeration.isCuitDemandado(condicion.getParametro())
							|| parametroPredefinidoEnumeration.isDniDemandado(condicion.getParametro())
							|| parametroPredefinidoEnumeration.isNombreDemandado(condicion.getParametro())) {
						if (!demandadosDone) {
							numeroCoincidencias += addDatosComunesInterviniente(coincidencias, conexidad, DEMANDADO);
							demandadosDone = true;
						}
					} else if (parametroPredefinidoEnumeration.isActor(condicion.getParametro())
							|| parametroPredefinidoEnumeration.isCuitActor(condicion.getParametro())
							|| parametroPredefinidoEnumeration.isDniActor(condicion.getParametro())
							|| parametroPredefinidoEnumeration.isNombreActor(condicion.getParametro())) {
						if (!actoresDone) {
							numeroCoincidencias += addDatosComunesInterviniente(coincidencias, conexidad, ACTOR);
							actoresDone = true;
						}
					} else if (parametroPredefinidoEnumeration.isPartes(condicion.getParametro())
							|| parametroPredefinidoEnumeration.isCuitPartes(condicion.getParametro())
							|| parametroPredefinidoEnumeration.isDniPartes(condicion.getParametro())
							|| parametroPredefinidoEnumeration.isNombrePartes(condicion.getParametro())) {
	
						if (!demandadosDone) {
							numeroCoincidencias += addDatosComunesInterviniente(coincidencias, conexidad, DEMANDADO);
							demandadosDone = true;
						}
						if (!actoresDone) {
							numeroCoincidencias += addDatosComunesInterviniente(coincidencias, conexidad, ACTOR);
							actoresDone = true;
						}
	
					} else if (parametroPredefinidoEnumeration.isOrigen(condicion.getParametro())
							&& (conexidad.getExpedienteOrigen().getExpedienteEspecial() != null)) {
						numeroCoincidencias++;
						if (coincidencias.length() > 0) {
							coincidencias.append(", ");
						}
						coincidencias.append(condicion.getParametro());
						coincidencias.append(": ").append(
								conexidad.getExpedienteOrigen().getExpedienteEspecial().getDependenciaOrigen());
						coincidencias.append(" [")
								.append(conexidad.getExpedienteOrigen().getExpedienteEspecial().getCodigoRelacionado())
								.append("]");
	
					} else if (parametroPredefinidoEnumeration.isNumeroExpedienteOrigen(condicion.getParametro())
							&& (conexidad.getExpedienteOrigen().getExpedienteEspecial() != null)) {
						numeroCoincidencias++;
						if (coincidencias.length() > 0) {
							coincidencias.append(", ");
						}
						coincidencias.append(condicion.getParametro());
						coincidencias.append(": [")
								.append(conexidad.getExpedienteOrigen().getExpedienteEspecial().getCodigoRelacionado())
								.append("]");
					} else {
						numeroCoincidencias += addParametrosComunes(coincidencias,
								conexidad.getExpedienteOrigen(), conexidad.getExpedienteRelacionado(), condicion.getParametro());
					}
				}
			}
		}
		conexidad.setCoincidencias(coincidencias.toString());
		conexidad.setNumeroCoincidencias(numeroCoincidencias);
	}

	private int addParametrosComunes(StringBuffer coincidencias, Expediente expedienteOrigen,
			Expediente expedienteRelacionado, String nombreParametro) {

		int numeroCoincidencias = 0;

		List<ParametroExpediente> origenParametroExpedienteList = findParametros(nombreParametro, expedienteOrigen.getParametroExpedienteList());
		List<String> relacionadoValueList = findParametroValueList(nombreParametro, expedienteRelacionado.getParametroExpedienteList());
		if(origenParametroExpedienteList != null && origenParametroExpedienteList.size() > 0){
			
			StringBuffer coincidenciaParametro = new StringBuffer();
			coincidenciaParametro.append(origenParametroExpedienteList.get(0).getParametro().getLabel());
			coincidenciaParametro.append(" [");
			for(ParametroExpediente parametroExpediente:origenParametroExpedienteList) {
				if (relacionadoValueList.contains(parametroExpediente.getValor())){
					if (numeroCoincidencias > 0) {
						coincidencias.append(',');
					}
					numeroCoincidencias++;
					coincidenciaParametro.append(parametroExpediente.getValor());
				}
			}
			coincidenciaParametro.append("]");
			
			if (!coincidencias.toString().contains(coincidenciaParametro)) {
				if (coincidencias.length() > 0) {
					coincidencias.append(", ");
				}
				coincidencias.append(coincidenciaParametro);
			}
		}
		return numeroCoincidencias;
	}

	private int addDatosComunesInterviniente(StringBuffer coincidencias, Conexidad conexidad, Integer tipo) {
		Expediente expedienteOrigen = conexidad.getExpedienteOrigen();
		int numeroCoincidencias = 0;
		for (IntervinienteExp intervinienteExp : getIntervinientes(expedienteOrigen)) {
			if (!intervinienteExp.isLogicalDeleted() && ((tipo == null) || tipo.equals(intervinienteExp.getTipoInterviniente().getNumeroOrden()))) {
				numeroCoincidencias += addDatosComunesInterviniente(coincidencias, intervinienteExp, conexidad);
			}
		}
		return numeroCoincidencias;
	}

	private int addDatosComunesInterviniente(StringBuffer coincidencias, IntervinienteExp intervinienteOrigen, Conexidad conexidad) {
		Expediente expedienteRelacionado = conexidad.getExpedienteRelacionado();
		StringBuffer datosComunes = new StringBuffer();
		Interviniente interviniente = intervinienteOrigen.getInterviniente();
		int numeroCoincidencias = 0;
		for (IntervinienteExp intervinienteExp : getIntervinientes(expedienteRelacionado)) {
			if (!intervinienteExp.isLogicalDeleted() && !intervinienteExp.isIdentidadReservada()) {
				Interviniente intervinienteRelacionado = intervinienteExp.getInterviniente();
				StringBuffer appendBuffer = new StringBuffer();
				if (interviniente.getNombre() != null
						&& interviniente.getNombre().equals(intervinienteRelacionado.getNombre())) {
					appendBuffer.append(interviniente.getNombre());
					if (mismaNaturaleza(intervinienteOrigen, intervinienteExp)) {
						conexidad.addTipoCoincidenciaParte(intervinienteExp.getTipoInterviniente().getNumeroOrden());
					}
					numeroCoincidencias++;
				}
				if (!Strings.isEmpty(interviniente.getNumeroDocId())
						&& interviniente.getNumeroDocId().equals(intervinienteRelacionado.getNumeroDocId())) {
					appendBuffer.append(" [");
					if ((interviniente.getTipoDocumentoIdentidad() != null)
							&& (interviniente.getTipoDocumentoIdentidad().getCodigo() != null)) {
						if (TipoDocumentoIdentidadEnumeration.isCuitCuil(interviniente.getTipoDocumentoIdentidad())) {
							appendBuffer.append(TipoDocumentoIdentidadEnumeration.CUIT_CODE).append(": ");
						} else if (TipoDocumentoIdentidadEnumeration.isDni(interviniente.getTipoDocumentoIdentidad())) {
							appendBuffer.append(TipoDocumentoIdentidadEnumeration.DNI_CODE).append(": ");
						} else {
							appendBuffer.append(interviniente.getTipoDocumentoIdentidad().getCodigo()).append(": ");
						}
					}
					appendBuffer.append(interviniente.getNumeroDocId());
					appendBuffer.append("]");
					if (mismaNaturaleza(intervinienteOrigen, intervinienteExp)) {
						conexidad.addTipoCoincidenciaParte(intervinienteExp.getTipoInterviniente().getNumeroOrden());
					}
					numeroCoincidencias++;
					// Añadir coincidencias por cuit de las empresas del grupo
				} else if (!Strings.isEmpty(interviniente.getDni())
						&& interviniente.getDni().equals(intervinienteRelacionado.getDni())) {
					appendBuffer.append(" [");
					appendBuffer.append(TipoDocumentoIdentidadEnumeration.DNI_CODE).append(": ");
					appendBuffer.append(interviniente.getDni());
					appendBuffer.append("]");
					if (mismaNaturaleza(intervinienteOrigen, intervinienteExp)) {
						conexidad.addTipoCoincidenciaParte(intervinienteExp.getTipoInterviniente().getNumeroOrden());
					}
					numeroCoincidencias++;
				}
				if (appendBuffer.length() > 0) {
					if (datosComunes.length() > 0) {
						datosComunes.append(", ");
					}
					datosComunes.append(intervinienteExp.getTipoInterviniente().getDescripcion()).append(" ")
							.append(appendBuffer);
				}
			}
		}
		if (datosComunes.length() > 0) {
			if (coincidencias.length() > 0) {
				coincidencias.append(", ");
			}
			coincidencias.append(datosComunes);
		}
		return numeroCoincidencias;
	}

	private boolean mismaNaturaleza(IntervinienteExp intervinienteExp1, IntervinienteExp intervinienteExp2) {
		return (intervinienteExp1.isActor() && intervinienteExp2.isActor())
				||
				(intervinienteExp1.isDemandado() && intervinienteExp2.isDemandado());
	}
			
	private Materia getMateria(ObjetoJuicio objetoJuicio) {
		if (objetoJuicio != null) {
			if (objetoJuicio.getMateria() != null) {
				return objetoJuicio.getMateria();
			} else if (objetoJuicio.getTipoProceso() != null) {
				return objetoJuicio.getTipoProceso().getMateria();
			}
		}
		return null;
	}

	public String getDescripcionTipo(Conexidad conexidad) {
		if (TipoConexidadEnumeration.Type.denunciada.getValue().equals(conexidad.getTipo())) {
			return getMessages().get("conexidad.denunciada");
		} else {
			if (Boolean.TRUE.equals(conexidad.getPrincipal())) {
				return getMessages().get("conexidad.detectada");
			} else {
				return getMessages().get("conexidad.informativa");
			}
		}
	}

	public static List<OficinaAsignacionExp> getRadicacionesFamiliaPrevia(EntityManager entityManager,
			Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion, Camara camara) {
		Map<Integer, OficinaAsignacionExp> oficinaAsignacionMap = new HashMap<Integer, OficinaAsignacionExp>();
		OficinaAsignacionExp radicacionSelected = null;

		if (expediente != null) {
			if (MateriaEnumeration.isAnyPenal(expediente.getMateria())) {
				Expediente expedientePrincipal = ExpedienteManager.getExpedientePrincipal(entityManager, expediente);
				radicacionSelected = ExpedienteManager.instance().findRadicacionExpediente(expedientePrincipal,
						tipoRadicacion);
				if (radicacionSelected != null) {
					if (radicacionSelected.getOficina() != null
							&& radicacionSelected.getOficina().getCamara().equals(camara)) {
						oficinaAsignacionMap.put(radicacionSelected.getOficina().getId(), radicacionSelected);
					} else {
						radicacionSelected = null;
					}
				}
			}

			if (radicacionSelected == null) {
				List<Expediente> familiaExpediente = ConexidadManager.instance().getFamiliaExpediente(entityManager,
						expediente);
				OficinaAsignacionExp radicacionActual = ExpedienteManager.instance().findRadicacionExpediente(expediente,
						tipoRadicacion);
				for (Expediente expedienteConexo : familiaExpediente) {
					OficinaAsignacionExp radicacion = ExpedienteManager.instance().findRadicacionExpediente(
							expedienteConexo, tipoRadicacion);
					if (radicacion != null && radicacion.getOficina() != null
							&& radicacion.getOficina().getCamara().equals(camara)) {
						Date nuevaFechaAsignacion = radicacion.getFechaAsignacion();
						OficinaAsignacionExp oficinaAsignacionExp = oficinaAsignacionMap.get(radicacion.getOficina()
								.getId());
						if (oficinaAsignacionExp != null) {
							if (!oficinaAsignacionExp.equals(radicacionActual)) {
								if (nuevaFechaAsignacion == null || radicacion.equals(radicacionActual)
										|| nuevaFechaAsignacion.before(oficinaAsignacionExp.getFechaAsignacion())) {
									oficinaAsignacionMap.put(radicacion.getOficina().getId(), radicacion);
								}
							}
						} else {
							oficinaAsignacionMap.put(radicacion.getOficina().getId(), radicacion);
						}
					}
				}
			}
		}
		return new ArrayList<OficinaAsignacionExp>(oficinaAsignacionMap.values());
	}

	// Busca dobles inicios (solo tiene en cuenta parametros del expediente, y
	// los parametros predefinidos de "origen" pero NO de partes)
	@Transactional
	public CriterioCnx buscaDobleInicio(ExpedienteMeAdd expedienteEdit) {
		getEntityManager().joinTransaction();

		Collection<CriterioCnx> criterios = getCriteriosDobleInicio(expedienteEdit.getTipoCausa(),
				expedienteEdit.getObjetoJuicio(), expedienteEdit.getMateria(), (expedienteEdit.findIntervinienteDemandado() == null));

		for (CriterioCnx criterio : criterios) {
			List<Expediente> antecedentes;
			antecedentes = buscaAntecentesPorParametros(expedienteEdit, criterio, true);
			if ((antecedentes != null) && (antecedentes.size() > 0)) {
				for(Expediente antecedente: antecedentes) {
					if ((!expedienteEdit.isEditMode()
							|| !antecedente.getId().equals(ExpedienteHome.instance().getInstance().getId()))
						&&
						(!Boolean.TRUE.equals(criterio.getExpedientesEnTramite()) || !isFinalizado(antecedente))
					) {
						expedienteEdit.setExpedienteDobleInicio(antecedente);
						return criterio;
					}
				}
			}
		}
		return null;
	}

	private List<Expediente> buscaAntecentesPorParametros(ExpedienteMeAdd expedienteEdit, CriterioCnx criterio,
			Boolean soloEnTramite) {
		ObjetoJuicio objetoJuicioPrincipal = expedienteEdit.getObjetoJuicio();

		List<Expediente> antecedentes = new ArrayList<Expediente>();
		antecedenteSearch.clear();
		antecedenteList.setEjbql(null);
		antecedenteSearch.setObjetoJuicioPrincipal(objetoJuicioPrincipal);
		if (expedienteEdit.getMateria().getGrupo() != null) {
			antecedenteSearch.setMateriaGrupo(expedienteEdit.getMateria().getGrupo());
		}

		antecedenteSearch.setNaturalezaSubexpediente(NaturalezaSubexpedienteEnumeration.Type.principal.getValue()
				.toString());

		Boolean soloIniciados = getSoloIniciados(sessionState.getGlobalCamara(), getMateria(objetoJuicioPrincipal), criterio);
		antecedenteSearch.setSoloIniciados(soloIniciados);
		
		if (soloEnTramite == null) {
			soloEnTramite = getSoloEnTramite(sessionState.getGlobalCamara(), getMateria(objetoJuicioPrincipal), criterio);
		}
		antecedenteSearch.setSoloEnTramite(soloEnTramite);

		List<Integer> validIdEstadoList = new ArrayList<Integer>();
		List<Integer> invalidIdEstadoList = new ArrayList<Integer>();
		if(soloIniciados != null) {
			EstadoExpediente estadoSinIniciar = BuscadorConexidad.getEstadoSinIniciar(getEntityManager(), expedienteEdit.getMateria());
			if (estadoSinIniciar != null) {
				if(Boolean.TRUE.equals(soloIniciados)) {			// SOlo iniciados
					invalidIdEstadoList.add(estadoSinIniciar.getId());
				} else if(Boolean.FALSE.equals(soloIniciados)) {	// Solo no iniciados
					validIdEstadoList.add(estadoSinIniciar.getId());
				}
			}
		}
		if(Boolean.TRUE.equals(soloEnTramite)) {
			invalidIdEstadoList.addAll(BuscadorConexidad.getIdEstadosFinalizacion(getEntityManager(), expedienteEdit.getMateria()));
		}
		if(Boolean.FALSE.equals(soloEnTramite)) {
			validIdEstadoList.addAll(BuscadorConexidad.getIdEstadosFinalizacion(getEntityManager(), expedienteEdit.getMateria()));
		}

		if (validIdEstadoList.size() > 0) {
			antecedenteSearch.setEstadosValidosIds(validIdEstadoList);
		}
		if (invalidIdEstadoList.size() > 0) {
			antecedenteSearch.setEstadosNoValidosIds(invalidIdEstadoList);
		}

		
		antecedenteSearch.setFromAnioExpediente(getAnioDesde(objetoJuicioPrincipal, criterio));

		filtrarPorTipoCausa(expedienteEdit.getTipoCausa(), criterio);
		filtrarPorFechas(objetoJuicioPrincipal, criterio);
		filtrarPorAmbito(objetoJuicioPrincipal, criterio);
		boolean filtered = false;
		for (CondicionCnx condicion : criterio.getCondicionCnxList()) {
			if(!condicion.isLogicalDeleted()) {
				filtered = filtrarPorParametros(condicion, expedienteEdit);
				filtered |= filtrarPorOrigen(condicion, expedienteEdit);
				if (!filtered) { // El expediente no tiene datos para establecer los
					// filtros
					break;
				}
			}
		}
		if (filtered && antecedenteSearch.isFiltered()) {
			antecedentes = antecedenteList.getAllResults();
		}
		return antecedentes;
	}

	private boolean filtrarPorParametros(CondicionCnx condicion, ExpedienteMeAdd expedienteEdit) {
		boolean filtered = false;
		for (Parametro parametro : expedienteEdit.getParametroList()) {
			if (condicion.getParametro().equals(parametro.getName())) {
				String stringValues = expedienteEdit.getParametroValue(parametro);
				if (!Strings.isEmpty(stringValues)) {
					filtered = true;
					antecedenteSearch.setValorParametro(condicion.getParametro(), stringValues);
				}
			}
		}
		return filtered;
	}

	private boolean filtrarPorOrigen(CondicionCnx condicion, ExpedienteMeAdd expedienteEdit) {
		boolean filtered = false;
		if (parametroPredefinidoEnumeration.isOrigen(condicion.getParametro())) {
			if (expedienteEdit.getExpedienteEspecialEdit() != null) {
				String organismoOrigen = (expedienteEdit.getExpedienteEspecialEdit().getDependencia() != null) ? expedienteEdit
						.getExpedienteEspecialEdit().getDependencia().getDescripcion()
						: null;
				String numeroExpedienteOrigen = expedienteEdit.getExpedienteEspecialEdit().getNumero();
				if (!Strings.isEmpty(organismoOrigen) && !Strings.isEmpty(numeroExpedienteOrigen)) {
					filtered = true;
					antecedenteSearch.setDependenciaOrigen(organismoOrigen);
					antecedenteSearch.setCodigoRelacionado(numeroExpedienteOrigen);
				}
			}
		} else if (parametroPredefinidoEnumeration.isNumeroExpedienteOrigen(condicion.getParametro())) {
			// Solo comprueba el numero de expediente origen
			if (expedienteEdit.getExpedienteEspecialEdit() != null) {
				String numeroExpedienteOrigen = expedienteEdit.getExpedienteEspecialEdit().getNumero();
				if (!Strings.isEmpty(numeroExpedienteOrigen)) {
					filtered = true;
					antecedenteSearch.setCodigoRelacionado(numeroExpedienteOrigen);
				}
			}
		}
		return filtered;
	}

	public static boolean isConexidadInformativa(TipoRadicacionEnumeration.Type tipoRadicacion, Camara camara, Materia materia) {
		if(TipoRadicacionEnumeration.Type.tribunalOral.equals(tipoRadicacion)) {
			return ConfiguracionMateriaManager.instance().isConexidadInformativaTribunalOral(camara, materia);
		} else if(TipoRadicacionEnumeration.isAnySala(tipoRadicacion)) {
			return ConfiguracionMateriaManager.instance().isConexidadInformativaSegundaInstancia(camara, materia);
		} else if(TipoRadicacionEnumeration.isRadicacionSalaCasacion(tipoRadicacion)) {
			return ConfiguracionMateriaManager.instance().isConexidadInformativaCasacion(camara, materia);
		}
		return  ConfiguracionMateriaManager.instance().isConexidadInformativa(camara, materia);
	}
	
	public static boolean isBuscaConexidad(TipoRadicacionEnumeration.Type tipoRadicacion, Camara camara, Materia materia) {
		return isBuscaConexidad(tipoRadicacion, camara, materia, false) ;
	}

	public static boolean isBuscaConexidad(TipoRadicacionEnumeration.Type tipoRadicacion, Camara camara, Materia materia, boolean ingreso) {
		if (TipoRadicacionEnumeration.isRadicacionJuzgado(tipoRadicacion)) {
			return ConfiguracionMateriaManager.instance().isConexidadPrimeraInstancia(camara, materia);
		} else if (TipoRadicacionEnumeration.isRadicacionSala(tipoRadicacion)) {
			return ingreso? ConfiguracionMateriaManager.instance().isConexidadSegundaInstancia(camara, materia):ConfiguracionMateriaManager.instance().isConexidadSorteoSala(camara, materia) ;
		} else if (TipoRadicacionEnumeration.isRadicacionTribunalOral(tipoRadicacion)) {
			return ConfiguracionMateriaManager.instance().isConexidadTribunalOral(camara, materia);
		} else if (TipoRadicacionEnumeration.isRadicacionSalaCasacion(tipoRadicacion)) {
			return ConfiguracionMateriaManager.instance().isConexidadCasacion(camara, materia);
		}
		return false;
	}

	public void runBusquedaConexidad(SorteoParametros sorteoParametros) {
		
		// commit transaction
		 UserTransaction transaction = Transaction.instance();
		 try{
		     if (transaction.isActive()) {
		    	 transaction.commit();
		     }
		     transaction.begin();
		     getEntityManager().joinTransaction();
		    //
		     start(sorteoParametros);		
		 } catch (Exception e) {
				throw new RuntimeException(e);
		}
	}

	public void start(final SorteoParametros sorteoParametros) {
		String clave = sorteoParametros.getExpediente().getIdxAnaliticoFirst();
		
		SorteoManager.setStatusSorteoExpediente(getEntityManager(), sorteoParametros.getExpediente(), (Integer)StatusStorteoEnumeration.Type.sorteando.getValue());

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Lifecycle.beginCall();
				Manager.instance().initializeTemporaryConversation();
				Conversation.instance().begin();
				try {
					processBuscaConexidadEntry(sorteoParametros);
				} finally {
				}
				Conversation.instance().end();
				try {
					if(Transaction.instance().isActive()){
						Transaction.instance().commit();
					}
				} catch (Exception e) {
					log.error("Error cerrando conversacion", e);
				}
				Lifecycle.endCall();
			}
		});
		thread.setName("Buscador Conexidad: "+clave);
		thread.start();
	}
		
	public boolean processBuscaConexidadEntry(SorteoParametros sorteoParametros) {
		SorteoExpedienteEntry entry = new SorteoExpedienteEntry(sorteoParametros); 
		boolean done = false;
		boolean asignada = false;
		try {
			beginTransaction();
		
			entry =	new SorteoExpedienteEntry(entry, getEntityManager());
			Sorteo sorteo = sorteoParametros.getSorteo() == null ? null : getEntityManager().find(Sorteo.class, sorteoParametros.getSorteo().getId());
					
			Expediente expediente = entry.getExpediente(getEntityManager());
			String codigoTipoCambioAsignacion = TipoCambioAsignacionEnumeration.CONEXIDAD_DETECTADA;
			String descripcionCambioAsignacion = CreateExpedienteAction.getDescripcionCambioAsignacion(getEntityManager(), codigoTipoCambioAsignacion, entry.getTipoRadicacion(), expediente.getMateria()); 
			try {
				SessionState.instance().setGlobalCamara(entry.getCamara());
				if(entry.getUsuario() != null){
					SessionState.instance().setUsername(entry.getUsuario());
				}
				/** ATOS OVD */
				asignada = CreateExpedienteAction.buscaConexidadPorCriterios(getEntityManager(), 
								expediente,
								entry.getRecursoExp(),
								entry.getTipoRadicacion(), 
								codigoTipoCambioAsignacion, 
								descripcionCambioAsignacion,
								entry.getTipoGiro(),
								CreateExpedienteAction.puedeCompensarPorConexidadAutomatica(expediente, entry.getTipoRadicacion()),
								sorteoParametros.isDescompensarAnterior(),
								sorteo,
								entry.getRubro(),
								entry.getSiguienteSorteoEncadenado(),
								entry.isAsignaPorConexidad(),
								entry.getOficinaSorteo(),
								true,
								false,
								entry.getFojasPase(),sorteoParametros.getAnioOVD(), sorteoParametros.getLegajoOVD(), sorteoParametros.getCaratulaOVD());
				/** ATOS OVD */
				sorteoParametros.setBuscaConexidad(false);
				
				if (!asignada) {
					if (TipoCausaEnumeration.isProvenienteMediacion(expediente.getTipoCausa()) && CreateExpedienteAction.isAsignaJuzgadoMediacion(expediente.getTipoCausa())){
						asignada = CreateExpedienteAction.asignarJuzgadoMediacion(getEntityManager(),
								expediente, 
								sorteoParametros.getOficinaSorteo(),
								sorteoParametros.getSorteo(),
								sorteoParametros.getRubro(),
								sorteoParametros.getTipoGiro(),
								null,
								sorteoParametros.getFojasPase());
					}
				}

				if (asignada) {
					if (entry.isSorteaMediador()){
						OficinaAsignacionExp radicacion = ExpedienteManager.instance().findRadicacionExpediente(expediente, entry.getTipoRadicacion());
						if ((radicacion != null) && radicacion.getMediador() == null) { 
							sorteoParametros.setSorteoOperation(SorteoOperation.sorteoMediador);
							MesaSorteo.instance().enviarAColaDeSorteo(sorteoParametros);
						}
					}
				} else if(sorteoParametros.isSortea()){
					MesaSorteo.instance().enviarAColaDeSorteo(sorteoParametros);
				}
				done = true;
			} catch (Lex100Exception e) {
				log.error("Error buscando conexidad", e);
				done = false;
			}
			
		} catch (Exception e) {				
			log.error("Error buscando conexidad", e);
			done = false;
		} finally {
			endTransaction(entry, asignada, done);
		}			

		return done;
	}

	public void beginTransaction() throws SystemException, RollbackException,
		HeuristicMixedException, HeuristicRollbackException, NotSupportedException {
		if(Transaction.instance().isActive()){
			Transaction.instance().commit();
		}
		Transaction.instance().setTransactionTimeout(10800);
		Transaction.instance().begin();
		getEntityManager().joinTransaction();
	}


	private boolean endTransaction(SorteoExpedienteEntry entry, boolean asigned, boolean done) {
		boolean finished = false;
		try {
			if(!done){				
				Transaction.instance().rollback();
				Transaction.instance().begin();
				getEntityManager().joinTransaction();
				getTracer().trace("Error buscando Conexidad");
				addConexidadLog(getEntityManager(), entry.getCamara(), entry.getExpediente(getEntityManager()), entry.getTipoRadicacion());
			}
			Transaction.instance().commit();
			Transaction.instance().begin();
			getEntityManager().joinTransaction();

			if(done) {
				if(asigned || !entry.isSortea()) {
					SorteoManager.setStatusSorteoExpediente(getEntityManager(), entry.getExpediente(getEntityManager()), (Integer)StatusStorteoEnumeration.Type.sorteadoOk.getValue());
				}
			} else {
				SorteoManager.setStatusSorteoExpediente(getEntityManager(), entry.getExpediente(getEntityManager()), (Integer)StatusStorteoEnumeration.Type.sorteadoError.getValue());
			}
			finished = true;
		}catch (Exception e) {
			String err = "Error actualizando transaccion";				
			log.error(err, e);
			done = false;
		} finally{
			if(!finished){
				try{
					done = false;
					Transaction.instance().rollback();
				} catch (Exception e) {
				}
			}
		}
		return done;
	}

	private boolean isAuditoriaActiva() {
		if (this.auditoriaActiva == null) {
			this.auditoriaActiva = ConfiguracionMateriaManager.instance().isConexidadAuditoria(SessionState.instance().getGlobalCamara());
		}
		return this.auditoriaActiva;
	}
	
	private void addConexidadLog(EntityManager entityManager, Camara camara, Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		if (isAuditoriaActiva()) {
			String resultado = null;
			
			StringBuffer descripcion = new StringBuffer();
			if (getResultadoConexidad(tipoRadicacion, expediente.getConexidadList(), descripcion, false) > 0) {
				resultado = descripcion.toString();
			}

			Date startDate = getTracer().getStartDate();
			Date endDate = new Date();
			Long intervalo = endDate.getTime() - startDate.getTime();

			Long intervaloMinimo = ConfiguracionMateriaManager.instance().getConexidadAuditoriaIntervaloMinimo(camara);

			if ((intervaloMinimo == null) || intervalo > intervaloMinimo) {
				ConexidadLog conexidadLog = new ConexidadLog();
				conexidadLog.setCamara(SessionState.instance().getGlobalCamara());
				conexidadLog.setExpediente(expediente);
				conexidadLog.setFechaComienzo(startDate);
				conexidadLog.setFechaFin(endDate);
				conexidadLog.setIntervalo(intervalo);
				if (tipoRadicacion != null) {
					conexidadLog.setTipoRadicacion(tipoRadicacion.getValue().toString());
				}
				conexidadLog.setInformacionConexidad(getTracer().getInformacion());
				conexidadLog.setResultado(resultado);
				
				entityManager.persist(conexidadLog);
				entityManager.flush();
			}
		}
	}

	private void startTracer() {
		getTracer().reset();
		getTracer().setSimpleDateFormat(simpleDateFormat);
	}

	public Tracer getTracer() {
		if (this.tracer == null) {
			this.tracer = new Tracer(log);
		}
		return tracer;
	}

	public void setTracer(Tracer tracer) {
		this.tracer = tracer;
	}

}
