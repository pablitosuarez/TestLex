package com.base100.lex100.mesaEntrada.ingreso;

import groovy.transform.Synchronized;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.expand.seam.framework.entity.LookupEntitySelectionListener;
import com.base100.lex100.Lex100Exception;
import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.converter.UpperCaseFilteredConverter;
import com.base100.lex100.component.datasheet.DataSheetEditorManager;
import com.base100.lex100.component.datasheet.IDataSheetProperty;
import com.base100.lex100.component.datasheet.PropertyValueManager;
import com.base100.lex100.component.datasheet.controller.dataSheetProperty.DataSheetPropertyUtils;
import com.base100.lex100.component.enumeration.CodigoTipoOficinaEnumeration;
import com.base100.lex100.component.enumeration.CompetenciaEnumeration;
import com.base100.lex100.component.enumeration.DelitoEnumeration;
import com.base100.lex100.component.enumeration.DependenciaContendienteEnumeration;
import com.base100.lex100.component.enumeration.DependenciaEnumeration;
import com.base100.lex100.component.enumeration.DependenciaOrigenEnumeration;
import com.base100.lex100.component.enumeration.EstadoPoderEnumeration;
import com.base100.lex100.component.enumeration.GrupoCompetenciaEnumeration;
import com.base100.lex100.component.enumeration.MateriaEnumeration;
import com.base100.lex100.component.enumeration.NaturalezaParteEnumeration;
import com.base100.lex100.component.enumeration.NaturalezaSubexpedienteEnumeration;
import com.base100.lex100.component.enumeration.ObjetoJuicioEnumeration;
import com.base100.lex100.component.enumeration.OficinaDestinoEnumeration;
import com.base100.lex100.component.enumeration.OficinaEnumeration;
import com.base100.lex100.component.enumeration.ParametroConfiguracionMateriaEnumeration;
import com.base100.lex100.component.enumeration.ParametroConfiguracionOficinaEnumeration;
import com.base100.lex100.component.enumeration.ParametroEnumeration;
import com.base100.lex100.component.enumeration.StatusStorteoEnumeration;
import com.base100.lex100.component.enumeration.TemaEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionLex100Enumeration;
import com.base100.lex100.component.enumeration.TipoCambioAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoCausaEnumeration;
import com.base100.lex100.component.enumeration.TipoConexidadSolicitadaEnumeration;
import com.base100.lex100.component.enumeration.TipoDatoEnumeration;
import com.base100.lex100.component.enumeration.TipoGiroEnumeration;
import com.base100.lex100.component.enumeration.TipoInstanciaEnumeration;
import com.base100.lex100.component.enumeration.TipoIntervinienteEnumeration;
import com.base100.lex100.component.enumeration.TipoProcesoEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.enumeration.TipoSubexpedienteEnumeration;
import com.base100.lex100.component.enumeration.TipoTestigoPoderEnumeration;
import com.base100.lex100.component.enumeration.TipoVinculadoEnumeration;
import com.base100.lex100.component.enumeration.VFLetradoEnumeration;
import com.base100.lex100.component.misc.DateUtil;
import com.base100.lex100.component.validator.CuilValidator;
import com.base100.lex100.controller.entity.VFLetrado.VFLetradoSearch;
import com.base100.lex100.controller.entity.delito.DelitoHome;
import com.base100.lex100.controller.entity.delito.DelitoSearch;
import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.controller.entity.expediente.ExpedienteSearch;
import com.base100.lex100.controller.entity.interviniente.IntervinienteSearch;
import com.base100.lex100.controller.entity.letrado.LetradoSearch;
import com.base100.lex100.controller.entity.materia.MateriaSearch;
import com.base100.lex100.controller.entity.mediador.MediadorSearch;
import com.base100.lex100.controller.entity.moneda.MonedaSearch;
import com.base100.lex100.controller.entity.objetoJuicio.ObjetoJuicioSearch;
import com.base100.lex100.controller.entity.oficinaAsignacionExp.OficinaAsignacionExpSearch;
import com.base100.lex100.controller.entity.poder.PoderHome;
import com.base100.lex100.controller.entity.poder.PoderSearch;
import com.base100.lex100.controller.entity.recursoExp.RecursoExpList;
import com.base100.lex100.controller.entity.sigla.SiglaSearch;
import com.base100.lex100.controller.entity.tasaExp.TasaExpList;
import com.base100.lex100.controller.entity.tipoCausa.TipoCausaSearch;
import com.base100.lex100.controller.entity.tipoGrupoDependencia.TipoGrupoDependenciaSearch;
import com.base100.lex100.controller.entity.tipoInstancia.TipoInstanciaSearch;
import com.base100.lex100.entity.AbogadoPoder;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.Conexidad;
import com.base100.lex100.entity.ConexidadDeclarada;
import com.base100.lex100.entity.CriterioCnx;
import com.base100.lex100.entity.Delito;
import com.base100.lex100.entity.DemandaPoder;
import com.base100.lex100.entity.DemandadoPoder;
import com.base100.lex100.entity.Dependencia;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.Fiscalia;
import com.base100.lex100.entity.Interviniente;
import com.base100.lex100.entity.IntervinienteExp;
import com.base100.lex100.entity.Letrado;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Moneda;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.OficinaSorteo;
import com.base100.lex100.entity.Parametro;
import com.base100.lex100.entity.Poder;
import com.base100.lex100.entity.RecursoExp;
import com.base100.lex100.entity.Sigla;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.Tema;
import com.base100.lex100.entity.TestigoPoder;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.TipoGrupoDependencia;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoInterviniente;
import com.base100.lex100.entity.TipoProceso;
import com.base100.lex100.entity.TipoRecurso;
import com.base100.lex100.entity.TipoSubexpediente;
import com.base100.lex100.entity.VFLetrado;
import com.base100.lex100.entity.VinculadosExp;
import com.base100.lex100.manager.afip.AfipManager;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.configuracionMateria.ConfiguracionMateriaManager;
import com.base100.lex100.manager.configuracionOficina.ConfiguracionOficinaManager;
import com.base100.lex100.manager.cuaderno.CuadernoManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.manager.expediente.OficinaAsignacionExpManager;
import com.base100.lex100.manager.oficina.OficinaManager;
import com.base100.lex100.manager.poder.PoderManager;
import com.base100.lex100.manager.vinculadosExp.VinculadosExpManager;
import com.base100.lex100.manager.web.WebManager;
import com.base100.lex100.mesaEntrada.conexidad.ConexidadManager;
import com.base100.lex100.mesaEntrada.recurso.TipoRecursoFinderMe;
import com.base100.lex100.mesaEntrada.sorteo.SorteoParametros;

/**
 * @author Sergio
 *
 */
@Name("expedienteMeAdd")
@Scope(ScopeType.CONVERSATION)
public class ExpedienteMeAdd extends ScreenController implements
		IEditListController {
	private static final String TIPOS_SUBEXPEDIENTE_STMT = "from TipoSubexpediente where materia in (:materiaList) and codigo <> 0 and status <> -1";

	private static final SimpleDateFormat defaultParameterDateFormat = new SimpleDateFormat(
			"dd/MM/yyyy");
	private static final SimpleDateFormat defaultParameterDateTimeFormat = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm");

	private static final Integer MIN_ANIO_CONEXIDAD_SALA_EJECUCION_PREVISIONAL = 1996;

	private final int PAGE_1 = 1;
	private final int PAGE_2 = 2;
	private final int PAGE_ESPECIAL = 3;
	private final int PAGE_INGRESO_DIFERIDO = 4;
	private final int PAGE_RADICACION_PREVIA = 5;

	// OVD
	private Integer legajoOVD;
	private Integer anioLegajoOVD;
		
	@In(create = true)
	private ObjetoJuicioSearch objetoJuicioSearch;
	@In(create = true)
	private DelitoSearch delitoSearch;
	@In(create = true)
	private LetradoSearch letradoSearch;
	@In(create = true)
	private VFLetradoSearch vfLetradoSearch;
	@In(create = true)
	private IntervinienteSearch intervinienteSearch;
	@In(create = true)
	private TipoCausaSearch tipoCausaSearch;
	@In(create = true)
	private MonedaSearch monedaSearch;
	@In(create = true)
	ExpedienteHome expedienteHome;
	@In(create = true)
	ExpedienteSearch expedienteSearch;
	@In(create = true)
	DataSheetEditorManager dataSheetEditorManager;
	@In(create = true)
	OficinaManager oficinaManager;
	@In(create = true)
	OficinaAsignacionExpManager oficinaAsignacionExpManager;
	@In(create = true)
	private TipoIntervinienteEnumeration tipoIntervinienteEnumeration;
	@In(create = true)
	private DependenciaOrigenEnumeration dependenciaOrigenEnumeration;
	@In(create = true)
	private DependenciaContendienteEnumeration dependenciaContendienteEnumeration;
	@In(create = true)
	private ObjetoJuicioEnumeration objetoJuicioEnumeration;
	@In(create = true)
	private DelitoEnumeration delitoEnumeration;
	@In(create = true)
	private TipoRecursoFinderMe tipoRecursoFinderMe;
	@In(create = true)
	private TemaEnumeration temaEnumeration;

	//
	private Camara camara;
	private Materia materia;
	private Materia filtroMateriaObjetoJuicio;
	private Competencia competencia;
	private Tema tema;

	private ExpedienteEspecialEdit expedienteEspecialEdit;
	private ObjetoJuicioEdit objetoJuicioEdit;
	private DelitoEditList delitoEditList;
	private RecursoEdit recursoEdit;

	private Integer numeroExpedientePrincipal;
	private Integer anioExpedientePrincipal;
	private Expediente expedientePrincipal;

	private Integer numeroExpedienteMediacion;
	private Integer anioExpedienteMediacion;
	private Expediente expedienteMediacion;
	private Oficina expedienteMediacionOficinaRadicacion;

	private IngresoDiferidoEdit ingresoDiferido;
	private RadicacionPreviaEdit radicacionPrevia;

	private Integer numeroPoder;
	private Integer anioPoder;
	private Poder poder;

	private Integer numeroSeclo;
	private Integer anioSeclo;

	private TipoProceso tipoProceso;
	private MontoJuicioEdit montoJuicioEdit;
	private FojasEdit fojasEdit;
	//
	private TipoSubexpediente tipoSubexpediente;

	private LetradoEditList letradoEditList;
	private ObjetoJuicioEditList objetoJuicioEditList;
	private IntervinienteEditList intervinienteEditList;
	private ConexidadDeclarada conexidadDeclarada;
	private ConexidadDeclarada conexidadDeclaradaSegundaInstancia;

	//
	private OtrosDatosEdit otrosDatos;
	private MinisterioPublicoEdit ministerioPublicoEdit;
	//
	// private TasaEdit tasaEdit;
	private List<IDataSheetProperty> parametrosAsPropertyList;
	private String parametrosChanged;
	private Integer lastObjetoJuicioId;
	private Expediente expedienteResultado;
	private Expediente expedienteConexo;
	private Expediente expedienteDobleInicio;

	private List<Materia> materiasNoPenal = null;
	private List<TipoCausa> tiposCausaEspecial;
	private List<Tema> temas;
	private TipoCausa tipoCausaNormal;
	private boolean tipoCausaNormalSearched = false;
	private List<Competencia> competencias;
	private List<Competencia> competenciasPenales;
	private List<Competencia> competenciasCiviles;

	private List<TipoSubexpediente> tiposSubexpediente;
	private List<TipoInterviniente> tiposInterviniente;
	private TipoGrupoDependencia tipoGrupoDependencia;

	private List<Oficina> oficinasConexidadDeclarada;
	private List<Oficina> oficinasSorteo;

	private boolean verTodasOficinasDestino;

	Boolean flagIsPenal;
	boolean enviadoASorteo;

	private TipoGiroEnumeration.Type tipoGiro;

	private boolean editMode = false;
	private boolean verifyMode = false;

	private boolean showDenunciaModal;

	private SorteoParametros sorteoParametros;

	private boolean ingresoAnteriorSistema = false;
	private boolean ingresoWeb = false;

	private ObjetoJuicio objetoJuicioTipoCausa;
	private List<String> rubrosExcluidosList;

	private boolean medidasCautelares = false;
	private boolean ingresoUrgente = false;
	private boolean relajarValidacion = false;
	private boolean estadoEsParte = false;
	private boolean derechosHumanos = false;
	private boolean corrupcion = false;

	private Boolean showTemaRadioButtonSelector;
	private Boolean confirmacionIngresoSimplificada;

	private Date fechaComunicacion = null;
	private boolean ignoreCheckDobleInicio = false;
	private boolean modoRapido;

	private String expedienteInfoValor;
	private String comentarios;

	private Date fechaIngresoExpediente = null;

	private Expediente currentEditExpediente;

	private String warning;
	private boolean showWarningModalPanel = false;
	private String warningModalPage = null;
	private boolean omitirBusquedaConexidad = false;

	private Boolean objetoJuicioEditable = null;
	private TipoRadicacionEnumeration.Type tipoRadicacion = null;

	private boolean iniciarEnIngresoTurno = true;

	private boolean visibleAddPoderDialog = false;
	
	/** ATOS COMERCIAL */
	private Integer idCausa = null; // expedientes Web

	public Integer getIdCausa() {
		return idCausa;
	}

	public void setIdCausa(Integer idCausa) {
		this.idCausa = idCausa;
	}

	/** ATOS COMERCIAL */
	private Oficina oficinaActual;
	private Camara globalCamara;
	private Boolean inMesaDeEntrada;
	/** ATOS COMERCIAL */

	private boolean forzeRefresh;

	public ExpedienteMeAdd() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Create
	public void init() {
		expedienteEspecialEdit = new ExpedienteEspecialEdit();
		intervinienteEditList = new IntervinienteEditList(true, this);
		objetoJuicioEdit = new ObjetoJuicioEdit();
		delitoEditList = new DelitoEditList(true);
		montoJuicioEdit = new MontoJuicioEdit();
		recursoEdit = new RecursoEdit();
		fojasEdit = new FojasEdit();
		conexidadDeclarada = new ConexidadDeclarada();
		conexidadDeclaradaSegundaInstancia = new ConexidadDeclarada();
		tipoProceso = null;
		fechaComunicacion = null;
		ignoreCheckDobleInicio = false;
		expedienteDobleInicio = null;
		// tasaEdit = new TasaEdit();
		otrosDatos = new OtrosDatosEdit();
		ministerioPublicoEdit = new MinisterioPublicoEdit();
		enviadoASorteo = false;
		competencia = OficinaManager.instance().getOficinaActual() != null ? OficinaManager
				.instance().getOficinaActual().getCompetencia()
				: null;
		letradoEditList = new LetradoEditList(true, isPermitirAltaLetrado());
		objetoJuicioEditList = new ObjetoJuicioEditList(true);
		parametrosAsPropertyList = null;
		dataSheetEditorManager.resetDataSheets();
		setTema(selectDefaultTema());

		if (ConfiguracionMateriaManager.instance().isModoRapidoIngreso(
				SessionState.instance().getGlobalCamara())) {
			cambiarModoRapido(true);
		}
		omitirBusquedaConexidad = false;

		medidasCautelares = false;
		ingresoUrgente = false;
		relajarValidacion = false;
		estadoEsParte = false;
		derechosHumanos = false;
//		setCorrupcion(false);
		setIniciarEnIngresoTurno(true);
		this.forzeRefresh = false;
	}

	private Tema findTemaByCodigo(String codigo) {
		Tema ret = null;
		Set<Materia> materiaSet = SessionState.instance().getGlobalMateriaSet();
		if (!materiaSet.isEmpty()) {
			List<Tema> temas = getEntityManager()
					.createQuery(
							"from Tema where materia in (:materias) and codigo = :codigo and status <> -1 order by id")
					.setParameter("materias", materiaSet)
					.setParameter("codigo", codigo).getResultList();
			if (temas.size() > 0) {
				ret = temas.get(0);
			}
		}
		return ret;
	}

	private Tema selectDefaultTema() {
		Oficina oficina = SessionState.instance().getGlobalOficina();
		if (oficina != null) {
			String submateria = ConfiguracionOficinaManager.instance()
					.getSubmateriaDefectoMesa(oficina);
			if (Strings.isEmpty(submateria)) {
				oficina = CamaraManager.getOficinaPrincipal(oficina);
				submateria = ConfiguracionOficinaManager.instance()
						.getSubmateriaDefectoMesa(oficina);
			}
			Set<Materia> materiaSet = SessionState.instance()
					.getGlobalMateriaSet();
			if (!materiaSet.isEmpty() && !Strings.isEmpty(submateria)) {
				List<Tema> temas = getEntityManager()
						.createQuery(
								"from Tema where materia in (:materias) and codigo = :codigo and status <> -1 order by id")
						.setParameter("materias", materiaSet)
						.setParameter("codigo", submateria).getResultList();
				if (temas.size() > 0) {
					return temas.get(0);
				}
			}
		}
		return null;
	}

	public EntityManager getEntityManager() {
		return expedienteHome.getEntityManager();
	}

	public Camara getCamara() {
		if (camara == null) {
			camara = SessionState.instance().getGlobalCamara();
			if (camara != null) {
				camara = getEntityManager().find(Camara.class, camara.getId());
			}
		}
		return camara;
	}

	public boolean showFiltroMateriaObjetoJuicio() {
		return showObjetoJuicio() && CamaraManager.isCamaraActualCorteSuprema();
	}

	private void initFiltroMateriaObjetoJuicio() {
		if (showObjetoJuicio()) {
			Materia materia1 = getMateria();
			if (materia1 != null && !isCamaraCorteSuprema()) {
				filtroMateriaObjetoJuicio = materia1;
			} else {
				filtroMateriaObjetoJuicio = null;
			}
		} else {
			ObjetoJuicio oj = getObjetoJuicioTipoCausa(getTipoCausa());
			if (oj != null) {
				filtroMateriaObjetoJuicio = oj.getMateria();
			}
		}
	}

	public Materia getFiltroMateriaObjetoJuicio() {
		if (filtroMateriaObjetoJuicio == null) {
			initFiltroMateriaObjetoJuicio();
		}
		return filtroMateriaObjetoJuicio;
	}

	public void setFiltroMateriaObjetoJuicio(Materia filtroMateriaObjetoJuicio) {
		this.filtroMateriaObjetoJuicio = filtroMateriaObjetoJuicio;
		objetoJuicioEnumeration.setMateria(filtroMateriaObjetoJuicio);
	}

	public void onChangeFiltroMateriaObjetoJuicio(ValueChangeEvent event) {
		clearObjetoJuicio();
	}
	
	public Materia getMateria() {
		if (materia == null) {
			Set<Materia> materias = SessionState.instance()
					.getGlobalMateriaSet();
			if (materias.size() == 1) {
				for (Materia m : materias) {
					setMateria(getEntityManager()
							.find(Materia.class, m.getId()));
					break;
				}
			}
			if (materia == null) {
				ObjetoJuicio objetoJuicio = getObjetoJuicio();
				if (objetoJuicio != null) {
					setMateria(objetoJuicio.getMateria());
				}
			}
			if (materia == null) {
				TipoCausa tipoCausa = expedienteEspecialEdit.getTipoCausa();
				if (tipoCausa != null) {
					setMateria(tipoCausa.getMateria());
				}
			}
		}
		return materia;
	}

	public void setMateria(Materia materia) {
		if (!equals(this.materia, materia)) {
			temas = null;
			tipoCausaNormal = null;
			tiposCausaEspecial = null;
			flagIsPenal = null;
			competencias = null;
			objetoJuicioEnumeration.setMateria(materia);
		}
		this.materia = materia;
	}

	public List<Materia> getMateriasNoPenal() {
		if (materiasNoPenal == null) {
			materiasNoPenal = new ArrayList<Materia>();
			Camara camara = SessionState.instance().getGlobalCamara();
			if (camara != null) {
				if (CamaraManager.isCamaraActualCorteSuprema()) {
					MateriaEnumeration materiaEnumeration = MateriaEnumeration
							.instance();
					addMateria(
							materiasNoPenal,
							materiaEnumeration
									.getMateria(MateriaEnumeration.CODIGO_CIVIL));
					addMateria(materiasNoPenal,
							materiaEnumeration
									.getMateria(MateriaEnumeration.CODIGO_CAF));
					addMateria(materiasNoPenal,
							materiaEnumeration
									.getMateria(MateriaEnumeration.CODIGO_CYCF));
					addMateria(
							materiasNoPenal,
							materiaEnumeration
									.getMateria(MateriaEnumeration.CODIGO_SEGURIDAD_SOCIAL));
					addMateria(
							materiasNoPenal,
							materiaEnumeration
									.getMateria(MateriaEnumeration.CODIGO_LABORAL));
					addMateria(
							materiasNoPenal,
							materiaEnumeration
									.getMateria(MateriaEnumeration.CODIGO_ELECTORAL_CIVIL));
					addMateria(
							materiasNoPenal,
							materiaEnumeration
									.getMateria(MateriaEnumeration.CODIGO_COMER));
					addMateria(
							materiasNoPenal,
							materiaEnumeration
									.getMateria(MateriaEnumeration.CODIGO_CORTE_SUPREMA));
				} else {
					Set<Materia> materias = SessionState.instance()
							.getGlobalMateriaSet();
					for (Materia materia : materias) {
						setMateria(getEntityManager().find(Materia.class,
								materia.getId()));
						if (!MateriaEnumeration.isAbreviaturaPenal(getMateria()
								.getAbreviatura())) {
							addMateria(materiasNoPenal, getMateria());
						}
					}
				}
			}
		}
		return materiasNoPenal;
	}

	private void addMateria(List<Materia> list, Materia materia) {
		if (list != null && materia != null) {
			list.add(materia);
		}
	}

	public void selectMateriaCorteSuprema() { // Selecciona la primera materia
												// Penal federal para Corte
		setMateria(MateriaEnumeration.instance().getMateria(
				MateriaEnumeration.CODIGO_CORTE_SUPREMA));
	}

	public void selectMateriaPenalFederal() { // Selecciona la primera materia
												// Penal federal para Corte
		setMateria(MateriaEnumeration.instance().getMateria(
				MateriaEnumeration.CODIGO_PENAL));
	}

	public void selectMateriaPenal() { // Selecciona la primera materia Penal de
										// la oficina/camara. Necesario para
										// oficinas multiMateria
		Set<Materia> materias = SessionState.instance().getGlobalMateriaSet();
		for (Materia materia1 : materias) {
			materia1 = getEntityManager().find(Materia.class, materia1.getId());
			if (MateriaEnumeration
					.isAbreviaturaPenal(materia1.getAbreviatura())) {
				setMateria(materia1);
				break;
			}
		}
	}

	public void selectMateriaCivil() { // Selecciona la primera materia Civil de
										// la oficina/camara. Necesario para
										// oficinas multiMateria
		Set<Materia> materias = SessionState.instance().getGlobalMateriaSet();
		for (Materia materia1 : materias) {
			materia1 = getEntityManager().find(Materia.class, materia1.getId());
			if (MateriaEnumeration.isAbreviaturaNoPenal(materia1
					.getAbreviatura())) {
				setMateria(materia1);
				break;
			}
		}
	}

	public TipoCausa getTipoCausaIncompetenciaPrimera() {
		TipoCausa tipoCausa = findTipoCausaFromCodigo(TipoCausaEnumeration.CODIGO_IN1);

		if (tipoCausa == null) {
			tipoCausa = findTipoCausaFromCodigo(TipoCausaEnumeration.CODIGO_COM);
		}
		return tipoCausa;
	}

	public TipoCausa getTipoCausaIncompetenciaSegunda() {
		TipoCausa tipoCausa = findTipoCausaFromCodigo(TipoCausaEnumeration.CODIGO_IN2);

		if (tipoCausa == null) {
			tipoCausa = findTipoCausaFromCodigo(TipoCausaEnumeration.CODIGO_COS);
		}
		return tipoCausa;
	}

	public void selectIncompetenciaPrimera() {
		selectMateriaCivil();
		TipoCausa tipoCausa = getTipoCausaIncompetenciaPrimera();

		if (tipoCausa != null) {
			selectTipoCausa(tipoCausa.getId());
		} else {
			errorMsg("expedienteMeAdd.error.tipoCausaIncompetenciaPrimera.notFound");
		}
	}

	public void selectIncompetenciaSegunda() {
		selectMateriaCivil();
		TipoCausa tipoCausa = getTipoCausaIncompetenciaSegunda();

		if (tipoCausa != null) {
			selectTipoCausa(tipoCausa.getId());
		} else {
			errorMsg("expedienteMeAdd.error.tipoCausaIncompetenciaSegunda.notFound");
		}
	}

	public void selectExpedienteMediacion() {
		selectMateriaCivil();
		TipoCausa tipoCausa = findTipoCausaFromCodigo(TipoCausaEnumeration.CODIGO_EMD);
		if (tipoCausa != null) {
			selectTipoCausa(tipoCausa.getId());
		} else {
			errorMsg("expedienteMeAdd.error.tipoCausaMediacion.notFound");
		}
	}

	public TipoCausa findTipoCausaFromCodigo(String codigo) {
		List<TipoCausa> list = getEntityManager()
				.createQuery(
						"from TipoCausa where camara = :camara and codigo = :codigo and status <> -1")
				.setParameter("codigo", codigo)
				.setParameter("camara", getCamara()).getResultList();
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public void ingresoAnteriorSistemaPenal() {
		selectMateriaPenal();
		setIngresoAnteriorSistema(true);
	}

	public void ingresoAnteriorSistemaCivil() {
		selectMateriaCivil();
		setIngresoAnteriorSistema(true);
	}

	public void ingresoNormalCivil() {
		selectMateriaCivil();
		setTipoCausaEspecial(null);

	}

	public void cambiarModoRapido(boolean activar) {
		intervinienteEditList.cambiarModoRapido(activar);
	}

	public boolean isModoRapido() {
		return intervinienteEditList.isModoRapido();
	}

	public String doCopiaExpediente(String pageAdd0, String pageAdd1,
			String pageAddEspecial, String pageAddEspecial0) {
		setEnviadoASorteo(false);
		setIgnoreCheckDobleInicio(false);
		conexidadDeclarada = new ConexidadDeclarada(conexidadDeclarada);
		conexidadDeclaradaSegundaInstancia = new ConexidadDeclarada(conexidadDeclaradaSegundaInstancia);
		setExpedienteConexo(null);
		getExpedienteEspecialEdit().setExpedientesConexosPorNumeroOrigen(null);
		getExpedienteEspecialEdit().setUltimoNumeroCodigo(null);
		expedienteResultado = null;
		if (isCamaraCivilNacional() && !isMediacion()) {
			setObjetoJuicio(getObjetoJuicioTipoCausa(getTipoCausa()));
		}
		resetPoder();
		resetSeclo();
		return redirect(getTipoCausa(), pageAdd0, pageAdd1, pageAddEspecial,
				pageAddEspecial0);
	}

	public String doNewExpedienteCivil(String pageAdd0, String pageAdd1,
			String pageAddEspecial, String pageAddEspecial0) {
		Integer idTipoCausa = SessionState.instance()
				.getUltimoIdTipoCausaCreado();
		TipoCausa tipoCausa = null;
		if (idTipoCausa == null) {
			selectMateriaCivil();
		} else {
			selectTipoCausa(idTipoCausa);
			tipoCausa = getEntityManager().find(TipoCausa.class, idTipoCausa);
		}
		setIngresoAnteriorSistema(SessionState.instance()
				.isUltimoIngresoAnteriorSistema());
		expedienteResultado = null;
		return redirect(tipoCausa, pageAdd0, pageAdd1, pageAddEspecial,
				pageAddEspecial0);
	}

	private String redirect(TipoCausa tipoCausa, String pageAdd0,
			String pageAdd1, String pageAddEspecial, String pageAddEspecial0) {
		String returnPage = "";
		if (startAtPage0(tipoCausa)) {
			returnPage = pageAdd0;
		} else if (startAtPage1(tipoCausa)) {
			returnPage = pageAdd1;
		} else if (startAtPageEspecial(tipoCausa)) {
			returnPage = pageAddEspecial;
		} else if (startAtPageEspecial0(tipoCausa)) {
			returnPage = pageAddEspecial0;
		}
		return returnPage;
	}

	private boolean equals(Materia materia1, Materia materia2) {
		return materia1 == null ? materia2 == null : materia1.equals(materia2);
	}

	private boolean equals(Object s1, Object s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}

	public void setMateriaByCodigo(Integer materiaCodigo) {
		if (materiaCodigo != null) {
			setMateria(MateriaSearch.findByCodigo(getEntityManager(),
					materiaCodigo));
		}
		setMateria(null);
	}

	public Competencia getCompetencia() {
		if (competencia == null) {
			if (getObjetoJuicio() != null
					&& getObjetoJuicio().getCompetencia() != null) {
				competencia = getObjetoJuicio().getCompetencia();
			} else {
				competencia = getCompetenciaOficinaActual();
			}
		}
		return competencia;
	}

	public void setCompetencia(Competencia competencia) {
		this.competencia = competencia;
	}

	public Competencia getCompetenciaOficinaActual() {
		return oficinaManager.getCompetencia(oficinaManager.getOficinaActual()!=null?oficinaManager.getOficinaActual():oficinaActual);
	}

	public String getTitle() {
		if (isEditMode()) {
			return getMessages().get("expedienteMeUpdate.title");
		} else {
			if (isIngresoAnteriorSistema()) {
				return getMessages().get(
						"expedienteMeAdd.anteriorSistema.title");
			} else {
				return getMessages().get("expedienteMeAdd.title");
			}
		}
	}

	public String getNormalTitle() {
		if (isEditMode()) {
			return getMessages().get("expedienteMeUpdate.normal.title");
		} else {
			if (isIngresoAnteriorSistema()) {
				return getMessages().get(
						"expedienteMeAdd.anteriorSistema.normal.title");
			} else {
				return getMessages().get("expedienteMeAdd.normal.title");
			}
		}
	}

	public String getIngresoDiferidoTitle() {
		if (isIngresoAnteriorSistema()) {
			return getMessages().get("expedienteMeAdd.anteriorSistema.title");
		} else {
			return getMessages().get("expedienteMe.ingresoDiferido.title");
		}
	}

	public boolean isPenal() {
		if (flagIsPenal == null) {
			flagIsPenal = MateriaEnumeration.isAnyPenal(getMateria());
			// OficinaManager.instance().isPenal(SessionState.instance().getGlobalOficina());
		}
		return flagIsPenal;
	}

	public static ExpedienteMeAdd instance() {
		return (ExpedienteMeAdd) Component.getInstance(ExpedienteMeAdd.class,
				true);
	}

	private void initFiltroDelitos(TipoCausa tipoCausa) {
		// Search
		if (tipoCausa != null) {
			delitoSearch.setTipoInstancia(tipoCausa.getTipoInstancia());// selecciona
																		// la
																		// instancia
																		// de
																		// los
																		// delitos
			delitoEnumeration.setTipoInstancia(tipoCausa.getTipoInstancia());// selecciona
																				// la
																				// instancia
																				// de
																				// los
																				// delitos

			delitoSearch.setAbreviaturaTipoInstancia(tipoCausa
					.getAbreviaturaTipoInstanciaOJ());
			delitoEnumeration.setAbreviaturaTipoInstancia(tipoCausa
					.getAbreviaturaTipoInstanciaOJ());

			delitoSearch.setCodigosList(tipoCausa.getCodigoObjetoJuicioList());
			delitoEnumeration.setCodigosList(tipoCausa
					.getCodigoObjetoJuicioList());

			delitoSearch.updateFilters();
		}
	}

	public List<String> getRubrosExcluidosList() {
		if (rubrosExcluidosList == null) {
			String rubrosExcluidos = getParametroConfiguracionOficina(
					OficinaManager.instance().getOficinaActual(),
					ParametroConfiguracionOficinaEnumeration.Type.rubrosExluidosIngreso
							.getName(), null);
			if (!Strings.isEmpty(rubrosExcluidos)) {
				rubrosExcluidosList = Configuration
						.getCommaSeparatedAsList(rubrosExcluidos);
			}
		}
		return rubrosExcluidosList;
	}

	private void initFiltroObjetosJuicio(TipoCausa tipoCausa) {
		// Search
		Camara camaraFilter = null;
		if(CamaraManager.isCamaraActualCorteSuprema()){
			camaraFilter = null;
		}else{
			camaraFilter = CamaraManager.getCamaraActual();
		}

		objetoJuicioSearch.setCamara(camaraFilter);
		
		objetoJuicioSearch.setMateria(getFiltroMateriaObjetoJuicio());
		objetoJuicioSearch
				.setShowMateriaFilter(getFiltroMateriaObjetoJuicio() == null) ;

		objetoJuicioEnumeration.setMateria(getFiltroMateriaObjetoJuicio());

		objetoJuicioSearch.setRubrosExcluidosList(getRubrosExcluidosList());
		objetoJuicioEnumeration
				.setRubrosExcluidosList(getRubrosExcluidosList());

		if (tipoCausa != null) {
			objetoJuicioSearch.setTipoInstancia(tipoCausa.getTipoInstancia());// selecciona
																				// la
																				// instancia
																				// de
																				// los
																				// OJ
			objetoJuicioEnumeration.setTipoInstancia(tipoCausa
					.getTipoInstancia());// selecciona
											// la
											// instancia
											// de
											// los
											// OJ

			objetoJuicioSearch.setAbreviaturaTipoInstancia(tipoCausa
					.getAbreviaturaTipoInstanciaOJ());
			objetoJuicioEnumeration.setAbreviaturaTipoInstancia(tipoCausa
					.getAbreviaturaTipoInstanciaOJ());

			objetoJuicioSearch.setCodigosList(tipoCausa
					.getCodigoObjetoJuicioList());
			objetoJuicioEnumeration.setCodigosList(tipoCausa
					.getCodigoObjetoJuicioList());

		} else {
			objetoJuicioSearch.setTipoInstancia(getPrimeraInstancia());
			objetoJuicioEnumeration.setTipoInstancia(getPrimeraInstancia());
		}
		if (editMode && (getCurrentEditExpediente() != null) && (getCurrentEditExpediente().getObjetoJuicioPrincipal() != null) && (getCurrentEditExpediente().getObjetoJuicioPrincipal().getStatus() != 0)) {
			objetoJuicioEnumeration.setCurrentObjetoJuicio(getCurrentEditExpediente().getObjetoJuicioPrincipal());
		}
		objetoJuicioSearch.updateFilters();
	}

	// ObjetoJuicio

	public void lookupObjetoJuicio(String returnPage) {
		initFiltroObjetosJuicio(getTipoCausa());
		expedienteHome.initializeLookup(objetoJuicioSearch,
				new LookupEntitySelectionListener<ObjetoJuicio>(returnPage,
						ObjetoJuicio.class) {
					public void updateLookup(ObjetoJuicio entity) {
						setObjetoJuicio(entity);
						if (entity != null) {
							setTema(entity.getTema());
						}
					}
				});
	}

	public String lookupObjetoJuicio(String page, String returnPage) {
		lookupObjetoJuicio(returnPage);
		return page;
	}

	public ObjetoJuicio getObjetoJuicio() {
		return objetoJuicioEdit.getObjetoJuicio();
	}

	public void setObjetoJuicio(ObjetoJuicio objetoJuicio) {
		if (!isCamaraCorteSuprema()) {
			setTipoProceso(null);
		}
		if (showIngresoUrgente() && (objetoJuicio != null) && !objetoJuicio.equals(objetoJuicioEdit.getObjetoJuicio())) {
			resetIngresoUrgente(objetoJuicio);
		}
		setTiposInterviniente(null); // resetea los tipos de intervinientes
		oficinasConexidadDeclarada = null;
		oficinasSorteo = null;
		objetoJuicioEdit.setObjetoJuicio(objetoJuicio);
		if ((objetoJuicio != null) && (objetoJuicio.getCompetencia() != null)) {
			setCompetencia(objetoJuicio.getCompetencia());
		}
		if ((objetoJuicio != null) && isObjetoJuicioVoluntario()
				&& intervinienteEditList != null
				&& intervinienteEditList.getTmpInstance() != null) {
			intervinienteEditList.getTmpInstance().setPersonaJuridica(false);
		}
	}

	public void clearObjetoJuicio() {
		objetoJuicioEdit.clearObjetoJuicio();
		objetoJuicioEnumeration.setSelected(null);
	}

	public String getDescripcionObjetoJuicio() {
		return objetoJuicioEdit.getDescripcionObjetoJuicio();
	}

	public void setDescripcionObjetoJuicio(String descripcionObjetoJuicio) {
		if(descripcionObjetoJuicio==null || descripcionObjetoJuicio.trim().isEmpty())
			objetoJuicioEdit.setDescripcionObjetoJuicio(null);
		else
			objetoJuicioEdit.setDescripcionObjetoJuicio(descripcionObjetoJuicio);
	}

	public void clearDelito() {
		delitoEditList.getTmpInstance().clearDelito();
	}

	private boolean isDelitoSelect(Delito delito) {
		if (equals(delito.getLey(), 11179) && !equals(delito.getArticulo(), 0)
				|| !equals(delito.getLey(), 11179)
				&& !equals(delito.getTitulo(), 99)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean equals(Integer value1, Integer value2) {
		return value1 == null ? value2 == null : value1.equals(value2);
	}

	public String lookupAddToDelitoListMultiple(String page, String returnPage) {
		DelitoHome delitoHome = (DelitoHome) Component.getInstance(
				DelitoHome.class, true);

		initFiltroDelitos(getTipoCausa());

		delitoHome.initializeLookup(delitoSearch,
				new LookupEntitySelectionListener<Delito>(returnPage,
						Delito.class, true) {
					public void updateLookup(Delito entity) {
						if (isDelitoSelect(entity)
								&& !delitoEditList.contains(entity)) {
							delitoEditList.getTmpInstance().setDelito(entity);
							try {
								delitoEditList.onAcceptLine();
							} catch (EntityOperationException e) {
							}
						}
					}

					public void finishSelection() {
						super.finishSelection();
					}
				});
		return page;
	}

	// Letrado

	public String lookupLetrado(String toPage, String returnPage) {
		expedienteHome.initializeLookup(letradoSearch,
				new LookupEntitySelectionListener<Letrado>(returnPage,
						Letrado.class) {
					public void updateLookup(Letrado entity) {
						letradoEditList.getTmpInstance().setLetrado(entity);
					}
				});
		return toPage;
	}

	public void clearLetrado() {
		letradoEditList.getTmpInstance().clearLetrado();
	}

	private Integer toInteger(String s) {
		try {
			return Integer.valueOf(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Long toLong(String s) {
		try {
			return Long.valueOf(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public String lookupVFletrado(LetradoEdit instance, String toPage,
			String returnPage) {
		expedienteHome.initializeLookup(vfLetradoSearch,
				new LookupEntitySelectionListener<VFLetrado>(returnPage,
						VFLetrado.class) {
					public void updateLookup(VFLetrado entity) {
						letradoEditList.getTmpInstance().setVfletrado(entity);
					}
				});
		// vfLetradoSearch.setIdTomo(toInteger(instance.getTomo()));
		// vfLetradoSearch.setIdFolio(toInteger(instance.getFolio()));
		// vfLetradoSearch.getFilter().setIdTomo(toInteger(instance.getTomo()));
		// vfLetradoSearch.getFilter().setIdFolio(toInteger(instance.getFolio()));
		// if(instance.getDocumento() != null){
		// if(TipoDocumentoLetradoEnumeration.isMatricula(instance.getTipoDocumento())){
		// vfLetradoSearch.setIdMatricula(toInteger(instance.getDocumento()));
		// vfLetradoSearch.getFilter().setIdMatricula(toInteger(instance.getDocumento()));
		// }else
		// if(TipoDocumentoLetradoEnumeration.isCuitCuil(instance.getTipoDocumento())){
		// vfLetradoSearch.setIdCuil(toLong(instance.getDocumento()));
		// vfLetradoSearch.getFilter().setIdMatricula(toInteger(instance.getDocumento()));
		// }
		// }
		vfLetradoSearch.setSoloLetradosHabilitados(true);
		return toPage;
	}

	// Denunciante / Imputado / OtroInterviniente

	public String lookupInterviniente(final String type, String toPage,
			String returnPage) {
		expedienteHome.initializeLookup(intervinienteSearch,
				new LookupEntitySelectionListener<Interviniente>(returnPage,
						Interviniente.class) {
					public void updateLookup(Interviniente entity) {
						intervinienteEditList.getTmpInstance()
								.setInterviniente(entity);
					}
				});
		return toPage;
	}

	public void clearIntervinienteActor(String type) {
		intervinienteEditList.getTmpInstance().clearInterviniente();
	}

	// Actor

	public String lookupInterviniente(String toPage, String returnPage) {
		expedienteHome.initializeLookup(intervinienteSearch,
				new LookupEntitySelectionListener<Interviniente>(returnPage,
						Interviniente.class) {
					public void updateLookup(Interviniente entity) {
						intervinienteEditList.getTmpInstance()
								.setInterviniente(entity);
					}
				});
		return toPage;
	}

	public void clearInterviniente() {
		intervinienteEditList.getTmpInstance().clearInterviniente();
	}

	// // Demandado

	// // Otro

	// Tipo Expediente Especial

	public void lookupTipoCausa(String returnPage) {
		tipoCausaSearch.clear();
		tipoCausaSearch.setMateria(getMateria());
		tipoCausaSearch.setEspecial(true);
		expedienteHome.initializeLookup(tipoCausaSearch,
				new LookupEntitySelectionListener<TipoCausa>(returnPage,
						TipoCausa.class) {
					public void updateLookup(TipoCausa entity) {
						setTipoCausaEspecial(entity);
					}
				});
	}

	public String lookupTipoCausa(String page, String returnPage) {
		lookupTipoCausa(returnPage);
		return page;
	}

	public void clearTipoCausa() {
		setTipoCausaEspecial(null);
	}

	public void selectTipoCausa(Integer idTipoCausa) {
		this.tipoRadicacion = null;
		setTipoCausaEspecial(getEntityManager().find(TipoCausa.class,
				idTipoCausa));
	}

	public void setTipoCausaEspecial(TipoCausa tipoCausa) {
		setTiposInterviniente(null); // resetea los tipos de intervinientes
		setTipoGrupoDependencia(null);// resetea el grupo de dependencias origen
		objetoJuicioTipoCausa = null;
		oficinasConexidadDeclarada = null;
		oficinasSorteo = null;
		expedienteEspecialEdit.setTipoCausa(tipoCausa);
		// ITO (Incompetencia TO)
		if (tipoCausa != null) {
			ObjetoJuicio objetoJuicioTipoCausa = getObjetoJuicioTipoCausa(tipoCausa);
			if (objetoJuicioTipoCausa != null) {
				setObjetoJuicio(objetoJuicioTipoCausa);
			} else {
				initFiltroDelitos(tipoCausa);
			}
		}
		initFiltroObjetosJuicio(tipoCausa);
		//
	}

	private TipoInstancia getPrimeraInstancia() {
		return TipoInstanciaSearch
				.findByAbreviatura(
						getEntityManager(),
						TipoInstanciaEnumeration.TIPO_INSTANCIA_PRIMERA_INSTANCIA_ABREVIATURA);
	}

	private TipoInstancia getSegundaInstancia() {
		return TipoInstanciaSearch
				.findByAbreviatura(
						getEntityManager(),
						TipoInstanciaEnumeration.TIPO_INSTANCIA_APELACIONES_ABREVIATURA);
	}

	// Moneda

	public void lookupMoneda(String returnPage) {
		expedienteHome.initializeLookup(monedaSearch,
				new LookupEntitySelectionListener<Moneda>(returnPage,
						Moneda.class) {
					public void updateLookup(Moneda entity) {
						montoJuicioEdit.setMoneda(entity);
					}
				});
	}

	public void clearMoneda() {
		montoJuicioEdit.setMoneda(null);
	}

	public ObjetoJuicioEdit getObjetoJuicioEdit() {
		return objetoJuicioEdit;
	}

	public LetradoEditList getLetradoEditList() {
		return letradoEditList;
	}

	public void setLetradoEditList(LetradoEditList letradoEditList) {
		this.letradoEditList = letradoEditList;
	}

	public ObjetoJuicioEditList getObjetoJuicioEditList() {
		return objetoJuicioEditList;
	}

	public void setObjetoJuicioEditList(
			ObjetoJuicioEditList objetoJuicioEditList) {
		this.objetoJuicioEditList = objetoJuicioEditList;
	}

	public ConexidadDeclarada getConexidadDeclarada() {
		return conexidadDeclarada;
	}

	public ConexidadDeclarada getConexidadDeclaradaSegundaInstancia() {
		return conexidadDeclaradaSegundaInstancia;
	}

	public TipoProceso getTipoProceso() {
		if (this.tipoProceso == null && !isPenal()
				&& CamaraManager.isCamaraActualCorteSuprema()) {
			seleccionaTipoProcesoCorteSuprema();
		}
		if (this.tipoProceso == null) {
			if (getObjetoJuicioEdit().getObjetoJuicio() != null) {
				if (getObjetoJuicioEdit().getObjetoJuicio().getTipoProceso() != null) {
					return getObjetoJuicioEdit().getObjetoJuicio()
							.getTipoProceso();
				}
			}
		}
		return tipoProceso;
	}

	private TipoProceso getTipoProcesoCorteSuprema() {
		return TipoProcesoEnumeration.instance().getTipoProcesoCorteSuprema();
	}

	public List<TipoProceso> getTiposProceso(){
		return TipoProcesoEnumeration.instance().getItemsByMateria(getMateria());
	}

	private void seleccionaTipoProcesoCorteSuprema() {
		if(MateriaEnumeration.isJuiciosOriginarios(getMateria())) {
			setTipoProceso(TipoProcesoEnumeration.instance().getDefaultTipoProceso(getMateria()));
		} else {
			setTipoProceso(getTipoProcesoCorteSuprema());
		}
	}

	public void setTipoProceso(TipoProceso tipoProceso) {
		this.tipoProceso = tipoProceso;
	}

	public ExpedienteEspecialEdit getExpedienteEspecialEdit() {
		return expedienteEspecialEdit;
	}

	public MontoJuicioEdit getMontoJuicioEdit() {
		return montoJuicioEdit;
	}

	public void setMontoJuicioEdit(MontoJuicioEdit montoJuicioEdit) {
		this.montoJuicioEdit = montoJuicioEdit;
	}

	public OtrosDatosEdit getOtrosDatos() {
		return otrosDatos;
	}

	public MinisterioPublicoEdit getMinisterioPublicoEdit() {
		return ministerioPublicoEdit;
	}

	public String doCancel(String page) {
		return page;
	}

	// public String doSaveFromMediacion() {
	// return doSaveFromMediacion(null);
	// }
	//
	// public String doSaveFromMediacion(String page) {
	// boolean error = false;
	// // try {
	// // //expedienteEspecialEdit.checkAccept();
	// // } catch (EntityOperationException e) {
	// // error = true;
	// // }
	// return error ? null : page;
	// }

	public String doSaveEspecial0() {
		return doSaveEspecial0(null);
	}

	public String doSaveEspecial0(String page) {
		boolean error = false;

		if (ConfiguracionMateriaManager.instance().isNumeroExpedienteDividido(
				CamaraManager.getCamaraActual())) {
			StringBuffer sb = new StringBuffer();
			if (expedienteEspecialEdit.getNumeroConcat() != null
					&& expedienteEspecialEdit.getAnioConcat() != null) {
				sb.append(expedienteEspecialEdit.getNumeroConcat());
				sb.append("/");
				sb.append(expedienteEspecialEdit.getAnioConcat());
				expedienteEspecialEdit.setNumero(sb.toString());
			} else {
				if (expedienteEspecialEdit.getNumeroConcat() != null) {
					sb.append(expedienteEspecialEdit.getNumeroConcat());
					expedienteEspecialEdit.setNumero(sb.toString());
				} else if (expedienteEspecialEdit.getAnioConcat() != null) {
					sb.append("/");
					sb.append(expedienteEspecialEdit.getAnioConcat());
					expedienteEspecialEdit.setNumero(sb.toString());
				}
			}
		}

		try {
			expedienteEspecialEdit.checkAccept();
		} catch (EntityOperationException e) {
			error = true;
		}
		if (expedienteEspecialEdit.isErrorTipoCausa()) {
			error = true;
		}
		try {
			if (showMinisteriosPublicos()) {
				ministerioPublicoEdit.checkAccept();
				if (ministerioPublicoEdit.isError()) {
					error = true;
				} else {
					createAbogadoMinisterio();
					if (CamaraManager.isCamaraCivilNacional()) {
						Tema tema = findTemaByCodigo(TemaEnumeration.CODIGO_TEMA_FAMILIA);
						if (tema != null) {
							setTema(tema);
						}
					}
				}
			}
		} catch (EntityOperationException e) {
			error = true;
		}

		if (showOrigen() && !isDependenciaOpcional(getTipoCausa())
				&& expedienteEspecialEdit.getDependencia() == null
				&& expedienteEspecialEdit.getOtraDependencia() == null) {
			String errKey = "expedienteEspecial.error.dependenciaOrigenRequerida";
			expedienteEspecialEdit.addErrorField("dependenciaOrigen");
			errorMsg(errKey);
			error = true;
		}
		if (showFechaHecho()) {
			if ((expedienteEspecialEdit.getFechaHecho() != null)
					&& (expedienteEspecialEdit.getFechaHecho()
							.after(new Date()))) {
				String errKey = "expedienteEspecial.error.fechaInvalida";
				expedienteEspecialEdit.addErrorField("fechaHecho");
				errorMsg(errKey);
				error = true;
			}
		}
		if (showOrigen() && showFechaInformacion()) {
			if (expedienteEspecialEdit.getFechaInformacion() == null) {
				if (isRequiredFechaInformacion()) {
					String errKey = "expedienteEspecial.error.fechaInformacionRequerida";
					expedienteEspecialEdit.addErrorField("fechaInformacion");
					errorMsg(errKey);
					error = true;
				}
			} else {
				if (expedienteEspecialEdit.getFechaInformacion().after(
						new Date())) {
					String errKey = "expedienteEspecial.error.fechaInvalida";
					expedienteEspecialEdit.addErrorField("fechaInformacion");
					errorMsg(errKey);
					error = true;
				}
			}
		}
		if (showOrigen()) {
			if (Strings.isEmpty(expedienteEspecialEdit.getNumero())) {
				String errKey = "expedienteEspecial.error.numeroRequerido";
				expedienteEspecialEdit.addErrorField("numero");
				errorMsg(errKey);
				error = true;
			}
		}
		if (!isEditMode() && showCoincidenciasPorNumeroOrigen()) {
			if (!error && showOrigen()) {
				error = calcularConexidadOrigen();
			}
			if (!error && getExpedienteConexo() != null) {
				conexidadDeclarada
						.setNumeroExpedienteConexo(getExpedienteConexo()
								.getNumero());
				conexidadDeclarada
						.setAnioExpedienteConexo(getExpedienteConexo()
								.getAnio());
			} else {
				resetConexidadDeclarada();
			}
		}
		return error ? null : page;
	}

	public boolean createAbogadoMinisterio() {
		boolean error = false;

		if (findAbogadoPrincipal() == null) {
			LetradoEdit letradoEdit = letradoEditList.getTmpInstance();
			letradoEdit.setTipoDocumento(null);
			letradoEdit
					.setTipoLetrado(LetradoEditList.TIPO_LETRADO_DEFENSOR_OFICIAL);
			letradoEdit.setNombre(UpperCaseFilteredConverter
					.filterValue(ministerioPublicoEdit.getNombreMinisterio()));
			VFLetradoEnumeration vfLetradoEnumeration = VFLetradoEnumeration
					.instance();
			vfLetradoEnumeration.setUserInput(letradoEdit.getNombre());
			try {
				letradoEditList.onAcceptLine();
			} catch (EntityOperationException e) {
				error = true;
			}
		}
		return !error;
	}

	private boolean calcularConexidadOrigen() {
		if (expedienteEspecialEdit.getUltimoNumeroCodigo() == null
				|| !expedienteEspecialEdit.getUltimoNumeroCodigo().equals(
						expedienteEspecialEdit.getNumeroCodigo())
				|| expedienteEspecialEdit.getUltimoNumero() == null
				|| !expedienteEspecialEdit.getUltimoNumero().equals(
						expedienteEspecialEdit.getNumero())) {

			List<Expediente> expedientesConexos = getEntityManager()
					.createQuery(
							"from Expediente e "
									+ "where e.expedienteEspecial.tipoCodigoRelacionado = :tipoCodigoRelacionado "
									+ "and e.expedienteEspecial.codigoRelacionado = :codigoRelacionado "
									+ "and e.camaraActual = :camara and e.naturaleza = 'P' and e.status <> -1 and (e.verificacionMesa = 'M' or e.verificacionMesa = 'N')"
									+ "order by e.anio, e.numero ")
					.setParameter("tipoCodigoRelacionado",
							expedienteEspecialEdit.getNumeroCodigo())
					.setParameter("codigoRelacionado",
							expedienteEspecialEdit.getNumero())
					.setParameter("camara",
							SessionState.instance().getGlobalCamara())
					.getResultList();

			expedienteEspecialEdit
					.setExpedientesConexosPorNumeroOrigen(expedientesConexos);
			expedienteEspecialEdit.setUltimoNumero(expedienteEspecialEdit
					.getNumero());
			expedienteEspecialEdit.setUltimoNumeroCodigo(expedienteEspecialEdit
					.getNumeroCodigo());
			ExpedienteHome.instance().setCerrarConversacion(false);
			if (expedientesConexos.size() > 0) {
				getStatusMessages().addFromResourceBundle(Severity.WARN,
						"expedienteEspecial.expedienteConexoOrigenList.found");
				return true;
			}
		}
		return false;
	}

	public String doSaveEspecial() {
		return doSaveEspecial(null);
	}

	public String doSaveEspecial(String page) {
		boolean error = false;

		if (getMateria() == null) {
			String errKey = "expedienteMeAdd.error.materia";
			errorMsg(errKey);
			return null;
		}

		try {
			expedienteEspecialEdit.checkAccept();
		} catch (EntityOperationException e) {
			error = true;
		}
		if (expedienteEspecialEdit.isErrorTipoCausa()) {
			error = true;
		}
		if (showOrigen() && !isDependenciaOpcional(getTipoCausa())
				&& showDependenciaOrigen()
				&& expedienteEspecialEdit.getDependencia() == null
				&& expedienteEspecialEdit.getOtraDependencia() == null) {
			String errKey = "expedienteEspecial.error.dependenciaOrigenRequerida";
			expedienteEspecialEdit.addErrorField("dependenciaOrigen");
			errorMsg(errKey);
			error = true;
		}
		if (showFechaHecho()) {
			if ((expedienteEspecialEdit.getFechaHecho() != null)
					&& (expedienteEspecialEdit.getFechaHecho()
							.after(new Date()))) {
				String errKey = "expedienteEspecial.error.fechaInvalida";
				expedienteEspecialEdit.addErrorField("fechaHecho");
				errorMsg(errKey);
				error = true;
			}
		}
		if (showOrigen() && showFechaInformacion()) {
			if (expedienteEspecialEdit.getFechaInformacion() == null) {
				if (isRequiredFechaInformacion()) {
					String errKey = "expedienteEspecial.error.fechaInformacionRequerida";
					expedienteEspecialEdit.addErrorField("fechaInformacion");
					errorMsg(errKey);
					error = true;
				}
			} else {
				if (expedienteEspecialEdit.getFechaInformacion().after(
						new Date())) {
					String errKey = "expedienteEspecial.error.fechaInvalida";
					expedienteEspecialEdit.addErrorField("fechaInformacion");
					errorMsg(errKey);
					error = true;
				}
			}
		}
		if (showJuezExortante()) {
			if (Strings.isEmpty(expedienteEspecialEdit.getJuezExhortante())) {
				String errKey = "expedienteEspecial.error.juezExhortanteRequerido";
				expedienteEspecialEdit.addErrorField("juezExhortante");
				errorMsg(errKey);
				error = true;
			}
		}
		if (showOrigen() && isRequiredNumeroExpedienteOrigen()) {
			if (Strings.isEmpty(expedienteEspecialEdit.getNumero())) {
				String errKey = "expedienteEspecial.error.numeroRequerido";
				expedienteEspecialEdit.addErrorField("numero");
				errorMsg(errKey);
				error = true;
			}
		}
		if (showNumeroCodigo() && isRequiredNumeroCodigo()) {
			if (Strings.isEmpty(expedienteEspecialEdit.getNumeroCodigo())) {
				String errKey = "expedienteEspecial.error.numeroCodigoRequerido";
				expedienteEspecialEdit.addErrorField("numeroCodigo");
				errorMsg(errKey);
				error = true;
			}
		}
		if (showCaratulaOrigen() && isRequiredCaratulaOrigen()
				&& Strings.isEmpty(expedienteEspecialEdit.getCaratulaOrigen())) {
			String errKey = "expedienteEspecial.error.caratulaOrigenRequerida";
			expedienteEspecialEdit.addErrorField("caratulaOrigen");
			errorMsg(errKey);
			error = true;
		}
		if (showHechos() && expedienteEspecialEdit.getHechos() == null) {
			String errKey = "expedienteEspecial.error.hechosRequerido";
			expedienteEspecialEdit.addErrorField("expedienteEspecialHechos");
			errorMsg(errKey);
			error = true;
		}
		if (isCuerposRequired() && fojasEdit.getCuerpos() == null) {
			String errKey = "expedienteEspecial.error.cuerposRequerido";
			expedienteEspecialEdit.addErrorField("expedienteEspecialCuerpos");
			errorMsg(errKey);
			error = true;
		}
		if (MateriaEnumeration.isNoPenal(getMateria())
				&& getObjetoJuicio() == null) {
			if (getObjetoJuicio() == null) {
				setObjetoJuicio(getObjetoJuicioTipoCausa(getTipoCausa()));
			}
			if (getObjetoJuicio() == null && showObjetoJuicio()) {
				String errKey = "expedienteEspecial.error.noObjetoJuicio";
				errorMsg(errKey);
				error = true;
			}
		}
		if (error && isEditMode() && expedienteHome.getInstance().isMigrado()) {
			error=false;
		}
		return error ? null : page;
	}

	public boolean isDependenciaOpcional() {
		return isDependenciaOpcional(getTipoCausa());
	}

	private boolean isDependenciaOpcional(TipoCausa tipoCausa) {
		return TipoCausaEnumeration.isExequatur(tipoCausa)
				|| TipoCausaEnumeration.isSeclo(tipoCausa)
				|| TipoCausaEnumeration.isJuiciosOriginarios(tipoCausa)
				|| TipoCausaEnumeration.isPresentacionesVarias(tipoCausa)
				|| TipoCausaEnumeration.isPresentacionesVariasConRetardoJusticia(tipoCausa)
				|| TipoCausaEnumeration.isExpedientesOriginariosPenales(tipoCausa);
	}

	private ObjetoJuicio getObjetoJuicioTipoCausa(TipoCausa tipoCausa) {
		if (objetoJuicioTipoCausa == null) {
			if ((tipoCausa != null)
					&& !Strings.isEmpty(tipoCausa.getCodigoObjetoJuicio())) {
				if (!tipoCausa.getCodigoObjetoJuicio().contains("[")
						&& !tipoCausa.getCodigoObjetoJuicio().contains(",")) {
					Materia materia1 = getMateria();
					if (materia1 == null) {
						materia1 = tipoCausa.getMateria();
					}
					objetoJuicioTipoCausa = ObjetoJuicioSearch.findByCodigo(
							getEntityManager(),
							tipoCausa.getCodigoObjetoJuicio(), materia1);
				}
			}
			if (objetoJuicioTipoCausa == null && isPenal()
					&& isCamaraCorteSuprema()) {
				objetoJuicioTipoCausa = ObjetoJuicioSearch.findByCodigo(
						getEntityManager(), "1", getMateria());
			}
		}
		return objetoJuicioTipoCausa;
	}

	private boolean sinObjetoJuicioTipoCausa(TipoCausa tipoCausa) {
		return tipoCausa != null
				&& "NO".equals(tipoCausa.getAbreviaturaTipoInstanciaOJ());
	}

	public String doSave0() {
		return doSave0(null);
	}

	public String doSave0(String page) {
		boolean error = false;
		if (TipoCausaEnumeration.isProvenienteMediacion(getTipoCausa())) {
			if ((getExpedienteMediacion() == null)
					|| !getExpedienteMediacion().getNumero().equals(
							getNumeroExpedienteMediacion())
					|| !getExpedienteMediacion().getAnio().equals(
							getAnioExpedienteMediacion())) {
				updateExpedienteMediacion();
			}

			if (getExpedienteMediacion() != null) {
				TipoCausa tipoCausa = expedienteEspecialEdit.getTipoCausa();
				copiaExpedienteDeMediacion();
				expedienteEspecialEdit.setTipoCausa(tipoCausa);
				resetTiposIntervinientesAtorDemandadoPorDefecto();
			} else {
				errorMsg("expedienteMeAdd.error.noExpedienteMediacion");
				error = true;
			}
		}
		if (ConfiguracionMateriaManager.instance().isCamaraUsaPoderes(
				getCamara())) {
			if ((getPoder() == null)
					|| !getPoder().getNumero().equals(getNumeroPoder())
					|| !getPoder().getAnio().equals(getAnioPoder())) {
				error = !updatePoder();
			} else {
				error = !checkPoder(getPoder());
			}
			if (!error && (getPoder() != null) && (page != null)) {
				copiaDatosPoder();
			}
		}

		return error ? null : page;
	}

	public void addPoder() {
		boolean error = false;
		if ((getPoder() == null)
				|| !getPoder().getNumero().equals(getNumeroPoder())
				|| !getPoder().getAnio().equals(getAnioPoder())) {
			error = !updatePoder();
		} else {
			error = !checkPoder(getPoder());
		}
		if (!error && (getPoder() != null)) {
			addPoderToEdit(getPoder(), this);
			setVisibleAddPoderDialog(false);
		}
	}

	private boolean checkPoder(Poder poder) {
		boolean ok = true;
		boolean permiteAltaExpedientePoderNoValido = ConfiguracionMateriaManager.instance().isPermitirAltaExpedientePoderNoValido(SessionState.instance().getGlobalCamara());
		
		if (poder != null) {
			DemandaPoder demandaPoder = PoderManager.findDemandaPoderByPoder(
					getEntityManager(), getPoder());
			if ((demandaPoder != null)
					&& (!isEditMode()
							|| !demandaPoder.getNumeroDemanda().equals(
									getCurrentEditExpediente().getNumero()) || !demandaPoder
							.getAnioDemanda().equals(
									getCurrentEditExpediente().getAnio()))) {
				if (permiteAltaExpedientePoderNoValido) {
					warnMsg("expedienteMeAdd.error.poderUsed", getPoder()
							.getNumero().toString(), poder.getAnio().toString(),
							demandaPoder.getNumeroDemanda().toString(),
							demandaPoder.getAnioDemanda().toString());
				} else {
					errorMsg("expedienteMeAdd.error.poderUsed", getPoder()
							.getNumero().toString(), poder.getAnio().toString(),
							demandaPoder.getNumeroDemanda().toString(),
							demandaPoder.getAnioDemanda().toString());
					ok = false;
				}
			} else if (ok
					&& (!isEditMode() || ((demandaPoder != null) && (!demandaPoder
							.getNumeroDemanda().equals(
									getCurrentEditExpediente().getNumero()) || !demandaPoder
							.getAnioDemanda().equals(
									getCurrentEditExpediente().getAnio()))))) {
				if (!EstadoPoderEnumeration.Type.ALTA.getValue().equals(
						poder.getAbm())) {
					if (permiteAltaExpedientePoderNoValido) {
						warnMsg(
								"expedienteMeAdd.error.poderEstadoNoValido",
								EstadoPoderEnumeration.instance().getLabel(
										poder.getAbm()));
					} else {
						errorMsg(
							"expedienteMeAdd.error.poderEstadoNoValido",
							EstadoPoderEnumeration.instance().getLabel(
									poder.getAbm()));
						ok = false;
					}
				}
			}
		} else {
			if ((getNumeroPoder() != null) && (getAnioPoder() != null)) {
				if (permiteAltaExpedientePoderNoValido) {
					if (WebManager.findPoderWeb(getEntityManager(), getNumeroPoder(), getAnioPoder())) {
						warnMsg("expedienteMeAdd.error.poderSinValidar");
					} else{
						warnMsg("expedienteMeAdd.error.poderNotFound");
					}
				} else {
					errorMsg("expedienteMeAdd.error.poderNotFound");
					ok = false;
				}
			}
		}
		return ok;
	}

	private void copiaExpedienteDeMediacion() {
		Expediente expedienteMediacion = getExpedienteMediacion();
		EditExpedienteAction.instance().copyExpedienteToEdit(
				expedienteMediacion, this, true);
		for (IntervinienteEdit intervinienteEdit : getIntervinienteEditList()
				.getList()) {
			if (!intervinienteEdit.isEmpty()) {
				intervinienteEdit.setNew(true);
			}
		}
		this.getIntervinienteEditList().setInAddExpedienteMode(true);
	}

	private void resetTiposIntervinientesAtorDemandadoPorDefecto() {
		if (getTiposInterviniente() != null) {
			TipoInterviniente defaultActor = getDefaultInterviniente(
					getTiposInterviniente(), true, false, "ACTOR", "ACTOR/ES");
			TipoInterviniente defaultDemandado = getDefaultInterviniente(
					getTiposInterviniente(), false, true, "DEMANDADO",
					"DEMANDADO/S");
			for (IntervinienteEdit item : getIntervinienteEditList().getList()) {
				if (item.isActor() && defaultActor != null) {
					item.setTipoInterviniente(defaultActor);
				} else if (item.isDemandado() && defaultDemandado != null) {
					item.setTipoInterviniente(defaultDemandado);
				}
			}
		}
	}

	public TipoInterviniente getDefaultInterviniente(
			List<TipoInterviniente> list, boolean isActor, boolean isDemandado,
			String defaultName1, String defaultName2) {
		TipoInterviniente ret = null;
		for (TipoInterviniente item : list) {
			if ((isActor == item.isActor())
					&& (isDemandado == item.isDemandado())) {
				if (defaultName1 != null
						&& defaultName1.equals(item.getDescripcion())) {
					ret = item;
				}
				if (ret == null && defaultName2 != null
						&& defaultName2.equals(item.getDescripcion())) {
					ret = item;
				}
			}
		}
		return ret;
	}

	public String doSave1() {
		return doSave1(null);
	}

	/** ATOS COMERCIAL */
	public String doSave0Web(String page) {
		boolean error = false;
		if (TipoCausaEnumeration.isProvenienteMediacion(getTipoCausa())) {
			if ((getExpedienteMediacion() == null)
					|| !getExpedienteMediacion().getNumero().equals(
							getNumeroExpedienteMediacion())
					|| !getExpedienteMediacion().getAnio().equals(
							getAnioExpedienteMediacion())) {
				updateExpedienteMediacionWeb();
			}

			if (getExpedienteMediacion() != null) {
				TipoCausa tipoCausa = expedienteEspecialEdit.getTipoCausa();
				copiaExpedienteDeMediacion();
				expedienteEspecialEdit.setTipoCausa(tipoCausa);
				resetTiposIntervinientesAtorDemandadoPorDefecto();
			} else {
				errorMsg("expedienteMeAdd.error.noExpedienteMediacion");
				error = true;
			}
		}
		return error ? null : page;
	}

	/** ATOS COMERCIAL */
	public String doSave1(String page) {
		boolean error = false;
		if (!checkMateriaObligatoria()) {
			error = true;
			return null;
		}

		try {
			if (isPenal()) {
				delitoEditList.onAcceptLine();
			} else {
				if (isMediacion()) {
					doSaveLineMultipleObjetojuicio();
				}
				if (showObjetoJuicio() && !oficinaManager.isCorteSuprema(oficinaManager.getOficinaActual())) {
					objetoJuicioEdit.checkAccept();
				}
				if (showLetrados()) {
					letradoEditList.onAcceptLine();
				}
			}
		} catch (EntityOperationException e) {
			error = true;
		}

		if (getPoder() != null) {
			ObjetoJuicio objetoJuicioPoder = poder.getObjetoJuicio();
			if ((objetoJuicioPoder != null) && (getObjetoJuicio() != null) && !getObjetoJuicio().getCodigo().equals(objetoJuicioPoder.getCodigo())){
				warnMsg("expedienteMeAdd.error.poderObjetoJuicioDiferente", getObjetoJuicio().getCodigo(), objetoJuicioPoder.getCodigo());
			}
		}
		if (!intervinienteEditList.verify(true)) {
			error = true;
		}

		if (!checkDelitos()) {
			error = true;
		}
		if (isVinculado() && isPenal()) {
			if (getExpedientePrincipal() == null) {
				errorMsg("expedienteMeAdd.error.noExpedienteOrigen");
				error = true;
			}
		}
		if (isPenal() && !checkCompetenciaObligatoria()) {
			error = true;
		}
		if (!error && !checkIntervinientes()) {
			error = true;
		}

		/**
		 * ATOS COMERCIAL Presunto fallido juicios universales: direccion
		 * obligatoria
		 */
		if (isComercial()) {
			if (!error && !checkDireccionPresuntoFallido()) {
				error = true;
			}

			if (!isOrganismoExternoComercial() && !error && !checkCuilActores()) {
				error = true;
			}
		}
		/** ATOS COMERCIAL */

		if (!error && page != null && !checkLetradoObligatorio()) {
			error = true;
		}

		if (isPenal()) {
			if (!error && !updateReservaIdentidad()) {
				errorMsg("expedienteMeAdd.error.updateReservaIdentidad");
				error = true;
			}
		}
		Oficina oficinaSorteadora = OficinaManager.instance()
				.getOficinaSorteadoraOAsignadora();
		Oficina oficinaPrincipal = CamaraManager
				.getOficinaPrincipal(oficinaSorteadora);
		if (!isEditMode()
				&& !isIngresoAnteriorSistema()
				&& !OficinaManager.instance()
						.isMesaDeEntrada(oficinaSorteadora)
				&& !OficinaManager.instance().isJuzgado(oficinaPrincipal)
				&& !OficinaManager.instance().isTribunalOral(oficinaPrincipal)) {
			errorMsg("expedienteMeAdd.error.ingresoPorOficinaInvalida");
			error = true;
		}

		if (!isEditMode() && !Strings.isEmpty(page)
				&& !Strings.isEmpty(getWarning())) {
			error = true;
			showWarningModalPanel(page);
		}
		actualizaParametros();
		return error ? null : page;
	}

	private void actualizaParametros() {
		if (!isIngresoWeb() || isEditMode()) {
			// Inicializa el valor del parametro domicilioDemandado con los datos
			// del domicilio del primer interviniente de tipo demandado
			// y el parametro Obra Social con la sigla del actor
			if (getParametrosAsPropertyList() != null) {
				for (IDataSheetProperty property : getParametrosAsPropertyList()) {
					if (ParametroEnumeration.DOMICILIO_DEMANDADO_NAME
							.equals(property.getName())) {
						/** ATOS COMERCIAL */
						String domicilioDemandado = null;
						for(Parametro parametro : getParametroList()){
							domicilioDemandado = getParametroValue(parametro);
						}

						for (IntervinienteEdit item : getIntervinienteEditList()
								.getList()) {
							if (item.isDemandado()) {
								domicilioDemandado = item.getDomicilioCompleto();
								break;
							}
						}
						DataSheetEditorManager
								.instance()
								.getInstanceProperties()
								.put(property.getDataSheetPropertyId(),
										domicilioDemandado);
					} else if ("$siglaActor".equals(DataSheetPropertyUtils.getParameter(property, DataSheetPropertyUtils.INIT_DATA_PARAMETER))) {
						String siglaActor = null;
						if (isEditMode()) {
							siglaActor = getParametroValue(property);
						}
						Camara camara = SessionState.instance().getGlobalCamara();
						for (IntervinienteEdit item : getIntervinienteEditList().getList()) {
							if (item.isActor() && item.isPersonaJuridica()) {
								if(item.getSigla() != null) {
									siglaActor = item.getSigla().getCodigo();
								} else {
									Sigla sigla = SiglaSearch.findByDescripcion(getEntityManager(), item.getApellidos(), camara);
									if (sigla != null) {
										siglaActor = sigla.getCodigo();
									}
								}
								break;
							}
						}
						DataSheetEditorManager
						.instance()
						.getInstanceProperties()
						.put(property.getDataSheetPropertyId(),	siglaActor);
					}
				}
			}
		}
	}

	private void showWarningModalPanel(String page) {
		setShowWarningModalPanel(true);
		setWarningModalPage(page);
	}

	private boolean updateReservaIdentidad() {
		boolean ret = true;
		boolean drogas_Ley_23737 = false;
		boolean trata_de_personas_Ley_26364_Ley_11179_arts_145_bis_y_ter = false;

		for (DelitoEdit delitoEdit : getDelitoEditList().getList()) {
			if (delitoEdit.isDrogas_Ley_23737()) {
				drogas_Ley_23737 = true;
			} else if (delitoEdit
					.isTrata_de_personas_Ley_26364_Ley_11179_arts_145_bis_y_ter()) {
				trata_de_personas_Ley_26364_Ley_11179_arts_145_bis_y_ter = true;
			}
		}

		for (IntervinienteEdit intervinienteEdit : getIntervinienteEditList()
				.getList()) {
			if (intervinienteEdit.isDenunciante()) {
				if (drogas_Ley_23737) {
					intervinienteEdit.setIdentidadReservada(true);
				}
			} else if (intervinienteEdit.isVictima()
					&& trata_de_personas_Ley_26364_Ley_11179_arts_145_bis_y_ter) {
				intervinienteEdit.setIdentidadReservada(true);
			}
		}
		try {
			getIntervinienteEditList().checkAccept();
		} catch (EntityOperationException e) {
			ret = true;
		}
		return ret;
	}

	private boolean checkDelitos() {
		boolean error = false;
		if (showDelitos()) {
			if (getDelitoEditList().size() == 0) {
				errorMsg("expedienteMeAdd.error.noDelitoDefined");
				error = true;
			}
		}
		if (!getDelitoEditList().checkConcurso()) {
			String errKey = "delito.error.enConcursoError";
			errorMsg(errKey);
			error = true;

		}
		return !error;
	}

	private boolean checkLetradoObligatorio() {
		boolean error = false;

		boolean required = !isPenal() && !isEditMode() && !isRelajarValidacion();
		if (!isMediacion()
				&& !ConfiguracionMateriaManager.instance()
						.isLetradoObligatorio(getCamara())) {
			required = false;
		}

		if (CamaraManager.isCamaraActualCOM() && isOrganismoExternoComercial() || 
				(isEspecial() && TipoCausaEnumeration.isRecursoDirectoACamara(getTipoCausa()))) {
			required = false;
		}

		if (CamaraManager.isCamaraActualCorteSuprema()
				&& TipoCausaEnumeration.isPresentacionesVarias(getTipoCausa())) {
			required = false;
		}

		if (CamaraManager.isCamaraActualSeguridadSocial()
				&& isObjetoJuicioRetiroInvalidezArt49()) {
			required = false;
		}

		if (required) {
			if (!TipoCausaEnumeration.isExhorto(getTipoCausa())) {
				ObjetoJuicio objetoJuicio = getObjetoJuicioEdit()
						.getObjetoJuicio();
				if ((findIntervinienteActor() != null)
						&& (findLetrado() == null) && (objetoJuicio != null)
						&& !objetoJuicio.isVoluntario()) {
					errorMsg("expedienteMeAdd.error.noLetradoDefined");
					error = true;
				}
			}
		}
		return !error;
	}

	private boolean checkMateriaObligatoria() {
		boolean error = false;
		if (getMateria() == null) {
			errorMsg("expedienteMeAdd.error.noMateriaDefined");
			error = true;
		}
		return !error;
	}

	private boolean checkCompetenciaObligatoria() {
		boolean error = false;
		if (showCompetencias() && getCompetencia() == null && !isIngresoASala()) {
			errorMsg("expedienteMeAdd.error.noCompetenciaDefined");
			error = true;
		}
		return !error;
	}

	private boolean checkIntervinientes() {
		boolean ok = true;

		if (showIntervinientes()) {
			ok &= checkIntervinientesObligatorios();
			ok &= checkTipoIntervinientes();
		}
		return ok;
	}

	/**
	 * ATOS COMERCIAL Presunto fallido juicios universales: direccion
	 * obligatoria
	 */
	private boolean checkDireccionPresuntoFallido() {
		boolean error = false;
		for (IntervinienteEdit inter : intervinienteEditList.getList()) {
			if (!inter.isEmpty() && inter.getTipoInterviniente() != null) {
				if (inter.isPresuntoFallido() && isJuicioUniversal()) {
					if (StringUtils.isBlank(inter.getDomicilio())
							|| StringUtils.isBlank(inter.getNumeroDomicilio())) {
						errorMsg("presuntoFallido.error.domicilio", inter
								.getTipoInterviniente().getDescripcion());
						error = true;
					}
				}
			}
		}
		return !error;
	}

	private boolean checkCuilActores() {
		boolean error = false;
		
		/* ATOS COMERCIAL Esta validacion no la toma en cuenta si viene de un Ingreso Anterior al Sistema */
		if (!isIngresoAnteriorSistema() || !isCamaraComercial()) {
			for (IntervinienteEdit inter : intervinienteEditList.getList()) {
				if (!inter.isEmpty() && inter.getTipoInterviniente() != null) {
					if (inter.isActor()) {
						if (inter.getTipoDocumento() == null) {
							errorMsg("actores.error.tipoDocumento", inter
									.getTipoInterviniente().getDescripcion());
							error = true;
						}
					}
				}
			}
		}
		return !error;
	}

	/** ATOS COMERCIAL */

	private boolean checkTipoIntervinientes() {
		boolean error = false;
		for (IntervinienteEdit intervinienteEdit : intervinienteEditList
				.getList()) {
			if (!intervinienteEdit.isEmpty()
					&& intervinienteEdit.getTipoInterviniente() != null) {
				if (intervinienteEdit.getTipoInterviniente().isDemandado()
						&& !intervinienteEdit.getTipoInterviniente().isTipoElectoral()
						&& isObjetoJuicioVoluntario() && !isMediacion()) {
					errorMsg(
							"actorDemandado.error.tipoIntervinienteIncorrecto.juicioVoluntario",
							intervinienteEdit.getTipoInterviniente()
									.getDescripcion());
					error = true;
				}

				/** ATOS COMERCIAL Preguntar a Luis */
				// if(intervinienteEdit.isPresuntoFallido() &&
				// !intervinienteEdit.isPersonaJuridica()){
				// errorMsg(
				// "actorDemandado.error.tipoIntervinientePresuntoFallido",
				// intervinienteEdit.getTipoInterviniente()
				// .getDescripcion());
				// error = true;
				// }
				/** ATOS COMERCIAL */

			}
		}
		return !error;
	}

	private boolean checkIntervinientesObligatorios() {
		boolean error = false;
		String tipos = getTiposIntervinientesObligatorios();
		if (tipos != null) {
			StringTokenizer tokenizer = new StringTokenizer(tipos, ",");
			while (tokenizer.hasMoreTokens()) {
				String tipo = tokenizer.nextToken().trim();

				if ("<*>".equals(tipo)) {
					if (!hasIntervinientes(true)) {
						errorMsg("expedienteMeAdd.error.noIntervinienteDefined");
						error = true;
					}
				} else if ("<A>".equals(tipo)) {
					if (findIntervinienteActor(true) == null) {
						String label = isMediacion() ? "naturalezaParteEnumeration.actor.mediacion"
								: NaturalezaParteEnumeration.Type.actor
										.getLabel();
						errorMsg(
								"expedienteMeAdd.error.noIntervinienteNaturalezaDefined",
								getMessages().get(label));
						error = true;
					}
				} else if ("<D>".equals(tipo)) {
					if (findIntervinienteDemandado(true) == null) {
						String label = isMediacion() ? "naturalezaParteEnumeration.demandado.mediacion"
								: NaturalezaParteEnumeration.Type.demandado
										.getLabel();
						errorMsg(
								"expedienteMeAdd.error.noIntervinienteNaturalezaDefined",
								getMessages().get(label));
						error = true;
					}
				} else {
					if (findIntervinienteByType(tipo, true) == null) {
						errorMsg(
								"expedienteMeAdd.error.noIntervinienteTypeDefined",
								tipo);
						error = true;
					}
				}
			}
		}
		setWarning(null);
		if (!error && isCamaraCivilNacional() && !isObjetoJuicioVoluntario()
				&& (findIntervinienteDemandado(true) == null)) {
			getStatusMessages().addFromResourceBundle(Severity.WARN,
					"expedienteMeAdd.warn.noDemandadoEnJuicioContradictorio");
			setWarning(getMessages().get(
					"expedienteMeAdd.warn.noDemandadoEnJuicioContradictorio"));
		}
		return !error;
	}

	public LetradoEdit findAbogadoPrincipal() {
		for (LetradoEdit letradoEdit : letradoEditList.getList()) {
			if (!letradoEdit.isEmpty()) {
				return letradoEdit;
			}
		}
		return null;
	}

	private boolean hasIntervinientes(boolean saleEnCaratula) {
		int count = 0;
		for (IntervinienteEdit intervinienteEdit : intervinienteEditList
				.getList()) {
			if (!intervinienteEdit.isEmpty()) {
				if (!(saleEnCaratula && intervinienteEdit.isNoSaleEnCaratula())) {
					count++;
				}
			}
		}
		return count > 0;
	}

	public String getDescripcionTipoInterviniente(
			TipoInterviniente tipoInterviniente) {
		if (tipoInterviniente != null) {
			boolean voluntario = isObjetoJuicioVoluntario();
			if (!voluntario && isEditMode()) {
				Expediente expediente = expedienteHome.getInstance();
				voluntario = expediente.isVoluntario();
			}
			if (CamaraManager.isCamaraCivilNacional()
					&& tipoInterviniente.isActor() && voluntario
					&& !isMediacion()) {
				return TipoIntervinienteEnumeration.INTERVINIENTES_STR_CAUSANTE;
			} else {
				String descripcion = tipoInterviniente.getDescripcion();
				if (tipoInterviniente.isTipoElectoral()) {
					descripcion = descripcion.concat(getAclaracionTipoIntervinienteElectoral(tipoInterviniente));
				}
				return descripcion;
			}
		}
		return null;
	}

	private String getAclaracionTipoIntervinienteElectoral(TipoInterviniente tipo) {
		String descripcion = "";
		if (tipo != null){
			if ((tipo.getNumeroOrden().equals(TipoIntervinienteEnumeration.ACTOR_NUMERO_ORDEN))
				&& (!tipo.getDescripcion().equals(TipoIntervinienteEnumeration.INTERVINIENTES_STR_ACTOR))){
				descripcion = " (ACTOR)";
			}
			else if ((tipo.getNumeroOrden().equals(TipoIntervinienteEnumeration.DEMANDADO_NUMERO_ORDEN))
					&& (!tipo.getDescripcion().equals(TipoIntervinienteEnumeration.INTERVINIENTES_STR_DEMANDADO))){
				descripcion = " (DEMANDADO)";
			}
		}
		return descripcion;
	}
	
	private IntervinienteEdit findIntervinienteActor(boolean saleEnCaratula) {
		IntervinienteEdit intervinienteEdit = findIntervinienteActor();
		if (saleEnCaratula && intervinienteSaleEnCaratula(intervinienteEdit)) {
			intervinienteEdit = null;
		}
		return intervinienteEdit;
	}

	public IntervinienteEdit findIntervinienteActor() {
		for (IntervinienteEdit intervinienteEdit : intervinienteEditList
				.getList()) {
			if (!intervinienteEdit.isEmpty()
					&& intervinienteEdit.getTipoInterviniente() != null) {
				if (intervinienteEdit.getTipoInterviniente().isActor()) {
					return intervinienteEdit;
				}
			}
		}
		return null;
	}

	public IntervinienteEdit findIntervinienteDemandado(boolean saleEnCaratula) {
		IntervinienteEdit intervinienteEdit = findIntervinienteDemandado();
		if (saleEnCaratula && intervinienteSaleEnCaratula(intervinienteEdit)) {
			intervinienteEdit = null;
		}
		return intervinienteEdit;
	}

	public IntervinienteEdit findIntervinienteDemandado() {
		for (IntervinienteEdit intervinienteEdit : intervinienteEditList
				.getList()) {
			if (!intervinienteEdit.isEmpty()
					&& intervinienteEdit.getTipoInterviniente() != null) {
				if (intervinienteEdit.getTipoInterviniente().isDemandado()) {
					return intervinienteEdit;
				}
			}
		}
		return null;
	}

	private IntervinienteEdit findIntervinienteByType(String typeName,
			boolean saleEnCaratula) {
		IntervinienteEdit intervinienteEdit = findIntervinienteByType(typeName);
		if (saleEnCaratula && intervinienteSaleEnCaratula(intervinienteEdit)) {
			intervinienteEdit = null;
		}
		return intervinienteEdit;
	}

	private IntervinienteEdit findIntervinienteByType(String typeName) {
		for (IntervinienteEdit intervinienteEdit : intervinienteEditList
				.getList()) {
			if (!intervinienteEdit.isEmpty()
					&& intervinienteEdit.getTipoInterviniente() != null) {
				String type = intervinienteEdit.getTipoInterviniente()
						.getDescripcion();
				if (typeName.equals(type)) {
					return intervinienteEdit;
				}
			}
		}
		return null;
	}

	private boolean intervinienteSaleEnCaratula(
			IntervinienteEdit intervinienteEdit) {
		return (intervinienteEdit != null
				&& intervinienteEdit.isNoSaleEnCaratula() ? true : false);
	}

	// return a comma, separated tipoIntervientente, <*> = ANY, <A>=Actor,
	// <D>=Demandado
	private String getTiposIntervinientesObligatorios() {

		/** ATOS COMERCIAL */
		if(isComercial()){
			if(isObjetoJucioBeneficioLitigarSinGastos()){
				return "<A>";
			}
			if(isTemaQuiebra()) {
		
				if(isObjetoJuicioPropiasQuiebras())
					return TipoIntervinienteEnumeration.INTERVINIENTE_STR_PRESUNTO_FALLIDO;
				else{
					if (isObjetoJuicioConcursosPreventivos())
						return TipoIntervinienteEnumeration.INTERVINIENTE_STR_CONCURSADO;
					if (isObjetoJuicioQuiebras())
						return TipoIntervinienteEnumeration.INTERVINIENTE_STR_FALLIDO;
					return TipoIntervinienteEnumeration.INTERVINIENTE_STR_PRESUNTO_FALLIDO + "," + TipoIntervinienteEnumeration.INTERVINIENTE_STR_PETICIONANTE;
				}
			}
			if (comercialSoloActorRequerido()) return "<A>";
			
			if (isAmparo() || TipoCausaEnumeration.isExhortoCom(getTipoCausa())) {
				return "<A>";
			}
			
			return "<A>,<D>";
		}
		/** ATOS COMERCIAL */

		TipoCausa tipoCausa = getTipoCausa();
		if (isCamaraCorteSuprema() || isCamaraCivilNacional() || isCCF()
				|| isCamaraElectoral()) {
			String ret = "<*>";
			if (isMediacion()) {
				ret = "<A>,<D>";
			}
			return ret;
		} else if (!isPenal()) {
			String ret = "<A>";
						if (getObjetoJuicio() != null && !getObjetoJuicio().isVoluntario()
					&& !TipoCausaEnumeration.isExhorto(tipoCausa)) {
				if (isObjetoJuicioAFIP()) {
					ret = "<D>";
				} else if ( !isDemandadoOpcional()
						&&
						((!isIngresoASala() && !isIngresoACorteSuprema())
						|| TipoCausaEnumeration.isRecursoDirecto(tipoCausa)
						|| (isIngresoACorteSuprema()
								&& !TipoCausaEnumeration
										.isPresentacionesVarias(tipoCausa) && !TipoCausaEnumeration
								.isPresentacionesVariasConRetardoJusticia(tipoCausa))) ){
					ret = "<A>,<D>";
				}
			}
			return ret;
		} else {
			if (TipoCausaEnumeration.isQuerella(tipoCausa)) {
				return "QUERELLANTE";
			} else if (showIntervinientes()) {
				return "<*>";
			} else {
				return null;
			}
		}
	}

	private boolean isDemandadoOpcional() {
		String codigoObjetoJuicio = (getObjetoJuicio() != null) ? getObjetoJuicio().getCodigo(): null;
		return isSeguridadSocial() && (
				ObjetoJuicioEnumeration.BENEFICIO_DE_LITIGAR_SIN_GASTOS.equals(codigoObjetoJuicio) ||
				ObjetoJuicioEnumeration.INFORMACION_SUMARIA.equals(codigoObjetoJuicio) ||
				ObjetoJuicioEnumeration.HOMOLOGACION_DE_CONVENIO_LEY_23660.equals(codigoObjetoJuicio)
			);
	}

	private boolean comercialSoloActorRequerido() {
		ObjetoJuicio objetoJuicio = getObjetoJuicio();

		for (String s : ObjetoJuicioEnumeration.SUMARISIMO_COMERCIAL_LIST)
			if (s.equals(objetoJuicio.getCodigo()))
				return true;

		for (String s : ObjetoJuicioEnumeration.CANCELACION_COMERCIAL_LIST)
			if (s.equals(objetoJuicio.getCodigo()))
				return true;

		for (String s : ObjetoJuicioEnumeration.ORDINARIO_COMERCIAL_LIST)
			if (s.equals(objetoJuicio.getCodigo()))
				return true;

		for (String s : ObjetoJuicioEnumeration.MEDIDAS_CAUTELARES_COMERCIAL_LIST)
			if (s.equals(objetoJuicio.getCodigo()))
				return true;

		for (String s : ObjetoJuicioEnumeration.DILIGENCIAS_COMERCIAL_LIST)
			if (s.equals(objetoJuicio.getCodigo()))
				return true;

		if (isMedidasPrecautoriasComercial()) {
			return true;
		}
		if (isAmparo() || TipoCausaEnumeration.isExhortoCom(getTipoCausa())) {
			return true;
		}
		if(isObjetoJucioBeneficioLitigarSinGastos()){
			return true;
		}
		return false;
	}

	private boolean isCamaraSeguridadSocial() {
		return CamaraManager.isCamaraSeguridadSocial(SessionState.instance()
				.getGlobalCamara());
	}

	private boolean isCamaraElectoral() {
		return CamaraManager.isCamaraElectoral(SessionState.instance()
				.getGlobalCamara());
	}

	public boolean isObjetoJuicioVoluntario() {
		return (getObjetoJuicio() != null && getObjetoJuicio().isVoluntario());
	}

	public boolean isObjetoJuicioFamilia() {
		return (getObjetoJuicio() != null && TemaEnumeration.instance()
				.isFamilia(getObjetoJuicio().getTema()));
	}

	private LetradoEdit findLetrado() {
		for (LetradoEdit letradoEdit : letradoEditList.getList()) {
			if (!letradoEdit.isEmpty()) {
				return letradoEdit;
			}
		}
		return null;
	}

	public String doSave2() {
		return doSave2(null);
	}

	public String doSave2(String page) {
		boolean error = false;
		try {
			checkParametros();
			checkMontoJuicio();
			checkConexidadDeclarada();
			checkConexidadDeclaradaSegundaInstancia();
			// checkOrigenMediacion();
			checkTratamientoIncidental();
			checkTipoRecurso();

		} catch (EntityOperationException e) {
			error = true;
		}
		if (!isPenal() && !checkCompetenciaObligatoria()) {
			error = true;
		}

		if (isCuerposRequired() && fojasEdit.getCuerpos() == null) {
			String errKey = "expedienteEspecial.error.cuerposRequerido";
			expedienteEspecialEdit.addErrorField("expedienteEspecialCuerpos");
			errorMsg(errKey);
			error = true;
		}
		if (isMontoJuicioRequired()) {
			if (montoJuicioEdit.getMoneda() == null) {
				String errKey = "expedienteMe.error.monedaRequerida";
				expedienteEspecialEdit.addErrorField("moneda");
				errorMsg(errKey);
				error = true;
			}
			if (montoJuicioEdit.getCantidad() == null) {
				String errKey = "expedienteMe.error.cantidadRequerida";
				expedienteEspecialEdit.addErrorField("cantidad");
				errorMsg(errKey);
				error = true;
			}
		}
		/** OVD */
		if (getAnioLegajoOVD()!=null && getAnioLegajoOVD().toString().length()<4){
			String errKey ="expedienteMe.error.cantidadDigitoAnioLegajoOVD";
			expedienteEspecialEdit.addErrorField("cantidad");
			errorMsg(errKey);
			error=true;
		}/** OVD */

		return error ? null : page;
	}

	public String doSaveRadicacionPrevia() {
		doSaveIngresoRadicacionPrevia(null);
		return null;
	}

	/** ATOS COMERCIAL */
	@Synchronized
	public String doSaveFinWeb(int currentPage, String page) {
		if (isEnviadoASorteo()) {
			return null;
		}
		boolean error = false;
		if (!error) {
			try {
				setEnviadoASorteo(true);
				CreateExpedienteAction.instance()
						.crearExpedienteYAsignarOficinaWeb(this);
			} catch (Lex100Exception e) {
				setEnviadoASorteo(false);
				errorMsg("expedienteEspecial.error.createExpediente",
						e.getMessage());
				error = true;
			}
		}
		return error ? null : page;
	}

	@Synchronized
	public void doSaveFinConMed(){
		//TipoCausa tipoCausa = expedienteEspecialEdit.getTipoCausa();
		//copiaExpedienteDeMediacion();
	//	expedienteEspecialEdit.setTipoCausa(tipoCausa);
		resetTiposIntervinientesAtorDemandadoPorDefecto();
		doSaveFinWeb(0, "/webCustom/mesaEntrada/expediente/add/expedienteMeAddFin.xhtml");
	}

	/** ATOS COMERCIAL */

	/** ATOS COMERCIAL */

	@Synchronized
	public String doSaveFin(int currentPage, String page) {
		if (isEnviadoASorteo()) {
			return null;
		}
		boolean error = false;
		switch (currentPage) {
		case PAGE_1:
			error = doSave1("ok") == null;
			break;
		case PAGE_2:
			error = doSave2("ok") == null;
			break;
		case PAGE_ESPECIAL:
			error = doSaveEspecial("ok") == null;
			break;
		case PAGE_INGRESO_DIFERIDO:
			error = doSaveIngresoDiferido("ok") == null;
			break;
		case PAGE_RADICACION_PREVIA:
			error = doSaveIngresoRadicacionPrevia("ok") == null;
			break;
		}
		if (!error) {
			error = doCheckDobleInicio();
		}
		if (!error) {
			try {
				setEnviadoASorteo(true);
				if (isEditMode()) {
					EditExpedienteAction.instance().updateExpediente(this);
				} else {
					CreateExpedienteAction.instance()
							.crearExpedienteYAsignarOficina(this);
					SessionState.instance().setUltimoIngresoInfo(
							(getTipoCausa() != null ? getTipoCausa().getId()
									: null), isIngresoAnteriorSistema());
				}
				expedienteResultado = expedienteHome.getInstance();
			} catch (Lex100Exception e) {
				setEnviadoASorteo(false);
				errorMsg("expedienteEspecial.error.createExpediente",
						e.getMessage());
				error = true;
			}
		}
		return error ? null : page;
	}

	@Synchronized
	public String doSaveAll(int currentPage, String page) {
		boolean error = false;
		switch (currentPage) {
		case PAGE_1:
			if (!error && hasPage2()) {
				error = doSave2("ok") == null;
			}
			if (!error && hasPageEspecial()) {
				error = doSaveEspecial("ok") == null;
			}
			if (!error) {
				error = doSave1(!Strings.isEmpty(page) ? page : "ok") == null;
			}
			break;
		case PAGE_2:
			error = doSave2("ok") == null;
			if (!error && hasPageEspecial()) {
				error = doSaveEspecial("ok") == null;
			}
			break;
		}
		if (!error) {
			return doSaveFin(0, page);
		}
		return null;
	}

	public boolean isExpedienteAsignado() {
		// return !isSorteandoExpediente();
		return !isSorteandoExpediente()
				&& (getExpedienteResultado() != null)
				&& (oficinaAsignacionExpManager.getRadicacion(
						getExpedienteResultado(), getTipoRadicacion()) != null);
	}

	public boolean isSorteandoExpediente() {
		Expediente expedienteSorteo = getExpedienteResultado();
		if (isTratamientoIncidental() && !isSorteaIncidenteEnTratamientoIncidental())
			expedienteSorteo = getExpedienteResultado().getExpedienteOrigen();
		
		if (expedienteSorteo != null) {
			Boolean sorteando = (expedienteSorteo.getStatusSorteo()!=null)
									&& StatusStorteoEnumeration.Type.sorteando.getValue().equals(expedienteSorteo.getStatusSorteo());
			if (sorteando)
				this.forzeRefresh = true;
			return sorteando;
		}
		return false;
	}

	public boolean isSorteaIncidenteEnTratamientoIncidental() {
		return TipoRadicacionEnumeration.Type.sala == getTipoRadicacion(); // Si
																			// es
																			// sala
																			// se
																			// sortea
																			// el
																			// incidente,
																			// si
																			// no,
																			// se
																			// sortea
																			// el
																			// principal
	}

	public boolean isSorteadoOkExpediente() {
		Expediente expedienteSorteo = getExpedienteResultado();
		if (isTratamientoIncidental()
				&& !isSorteaIncidenteEnTratamientoIncidental()) {
			expedienteSorteo = getExpedienteResultado().getExpedienteOrigen();
		}
		return (expedienteSorteo.getStatusSorteo()==null) 
					|| StatusStorteoEnumeration.Type.sorteadoOk.getValue().equals(expedienteSorteo.getStatusSorteo());
	}

	public boolean isSorteadoErrorExpediente() {
		Expediente expedienteSorteo = getExpedienteResultado();
		if (isTratamientoIncidental()
				&& !isSorteaIncidenteEnTratamientoIncidental()) {
			expedienteSorteo = getExpedienteResultado().getExpedienteOrigen();
		}
		return StatusStorteoEnumeration.Type.sorteadoError.getValue().equals(
				expedienteSorteo.getStatusSorteo());
	}

	public void refreshExpedienteResultado() {
		if (isTratamientoIncidental() && (expedienteResultado != null))
			refreshExpediente(expedienteResultado.getExpedienteOrigen());
		refreshExpediente(expedienteResultado);
		this.forzeRefresh = false;
	}

	public void refreshExpediente(Expediente expediente) {
		if (expediente != null) {
			if (!getEntityManager().contains(expediente)) {
				getEntityManager().find(Expediente.class, expediente.getId());
			}
			getEntityManager().refresh(expediente);

			RecursoExp recursoExp = getRecursoExp(expediente);
			if (recursoExp != null) {
				if (!getEntityManager().contains(recursoExp)) {
					recursoExp = getEntityManager().find(RecursoExp.class,
							recursoExp.getId());
				}
				getEntityManager().refresh(recursoExp);
			}

			if (expediente.getExpedienteIngreso() != null) {
				getEntityManager().refresh(expediente.getExpedienteIngreso());
			}
			try{
				getLog().info("Realizando refresh de radicaciones por INGRESO del expediente: " + expediente.getClave());
				oficinaAsignacionExpManager.refreshRadicacionExpediente(expediente);
			}catch(Exception e){
				getLog().warn("No se pudo realizar el refresh del expediente en su asignacion");
				e.printStackTrace();
			}
		}
	}

	public void ingresarTasa() {
		refreshExpedienteResultado();
		TasaExpList tasaExpList = (TasaExpList) Component.getInstance(
				TasaExpList.class, true);
		tasaExpList.onAdd(); // /web/tasaExp/tasaExpEdit.xhtml
	}

	protected RecursoExp getRecursoExp(Expediente instance) {
		List<RecursoExp> list = RecursoExpList.instance()
				.getRecursoExpListByExpediente(instance);
		return list.size() > 0 ? list.get(0) : null;
	}

	public void doImprimirCaratula() {
		expedienteHome.setVisibleDialog("imprimirCaratula", true);
	}

	public Expediente getExpedienteResultado() {
		return expedienteResultado;
	}

	public List<Parametro> getParametroList() {
		if (getObjetoJuicioEdit().getObjetoJuicio() != null) {
			ObjetoJuicio objetoJuicio = (ObjetoJuicio) expedienteHome
					.getEntityManager().find(ObjetoJuicio.class,
							getObjetoJuicioEdit().getObjetoJuicio().getId());
			if (objetoJuicio != null) {
				return objetoJuicio.getParametroList();
			}
		}
		return null;
	}

	public List<IDataSheetProperty> getParametrosAsPropertyList() {
		if (showParametros()) {
			if (parametrosAsPropertyList == null
					|| lastObjetoJuicioId == null
					|| !lastObjetoJuicioId.equals(getObjetoJuicioEdit()
							.getObjetoJuicio().getId())) {

				lastObjetoJuicioId = getObjetoJuicioEdit().getObjetoJuicio()
						.getId();
				parametrosAsPropertyList = new ArrayList<IDataSheetProperty>();
				for (Parametro parametro : getParametroList()) {
					parametrosAsPropertyList
							.add(new ParametroToDataSheetPropertyAdapter(
									parametro));
				}

			}
		} else {
			parametrosAsPropertyList = null;
		}
		return parametrosAsPropertyList;
	}

	public String getParametroValue(IDataSheetProperty property) {
		Object value = dataSheetEditorManager.getPropertyValue(property);
		String str = dataSheetEditorManager.getAsString(property, value, true);
		if (str != null) {
			str = str.trim();
		}
		return str;
	}

	public String getParametroValue(Parametro parametro) {
		return getParametroValue(new ParametroToDataSheetPropertyAdapter(
				parametro));
	}

	// public List<ParametroExpediente> getParametrosYValores() {
	// List<ParametroExpediente> list = null;
	// if (this.getParametroList() != null) {
	// for (Parametro parametro : this.getParametroList()) {
	// String stringValue = this.getParametroValue(parametro);
	// if (!Strings.isEmpty(stringValue)) {
	// ParametroExpediente parametroExpediente = new ParametroExpediente();
	// parametroExpediente.setParametro(parametro);
	// parametroExpediente.setValor(stringValue);
	// parametroExpediente.setIndice(0);
	// if (list == null) {
	// list = new ArrayList<ParametroExpediente>();
	// }
	// list.add(parametroExpediente);
	// }
	// }
	// }
	// return list;
	// }

	private void checkParametros() throws EntityOperationException {
		// TODO. chequear validez de los parametros
		if (showParametros()) {
			for (IDataSheetProperty property : getParametrosAsPropertyList()) {
				Parametro parametro = ((ParametroToDataSheetPropertyAdapter) property)
						.getParametro();
				String stringValue = null;
				/** ATOS - Comercial */
				if (ParametroEnumeration.DOMICILIO_DEMANDADO_NAME.equals(property.getName())) {
					Object value = dataSheetEditorManager.getPropertyValue(property);
					dataSheetEditorManager.setPropertyValue(property,dataSheetEditorManager.getAsString(property, value, true).trim());
				}
				/** ATOS - Comercial */
				try {
					stringValue = getParametroValue(property);
				} catch (Exception e) {
					String errKey = "expedienteMe.error.parametro.erroneo";
					errorMsg(errKey, parametro.getLabel());
					throw new EntityOperationException();
				}
				if (Strings.isEmpty(stringValue)
						&& DataSheetPropertyUtils.isRequired(property)) {
					String errKey = "expedienteMe.error.parametro.requerido";
					errorMsg(errKey, parametro.getLabel());
					throw new EntityOperationException();
				}
				if (Boolean.TRUE.equals(parametro.getMultiple())) {
					checkParametroMultiple(parametro, stringValue);
				} else {
					checkParametroValue(parametro, stringValue);
				}
			}
		}
	}

	private void checkParametroValue(Parametro parametro, String stringValue)
			throws EntityOperationException {
		if (TipoDatoEnumeration.Type.CUIL_CUIT_CDI.getValue().equals(
				parametro.getType())
				&& (!CamaraManager.isCamaraActualCOM() || !isOrganismoExternoComercial())) {
			if (!CuilValidator.isValidFormat(stringValue)) {
				String errKey = "parametro.input.cuitCuil.error.format";
				errorMsg(errKey, parametro.getLabel(), stringValue);
				throw new EntityOperationException();
			}
		}
	}

	private void checkParametroMultiple(Parametro parametro, String stringValue)
			throws EntityOperationException {
		List<String> valueList = Configuration
				.getCommaSeparatedAsNotNullList(stringValue);
		Map<String, Object> valueMap = new HashMap<String, Object>();
		for (String value : valueList) {
			if (valueMap.get(value) != null) {
				String errKey = "expedienteMe.error.parametroMultiple.duplicado";
				errorMsg(errKey, parametro.getLabel(), value);
				throw new EntityOperationException();
			}
			try {
				Object obj = PropertyValueManager.getAsObject((String) value,
						parametro.getType(), null);
				valueMap.put(value, obj);
			} catch (Exception e) {
				String errKey = "expedienteMe.error.parametroMultiple.erroneo";
				errorMsg(
						errKey,
						parametro.getLabel(),
						value,
						TipoDatoEnumeration.instance().getLabel(
								parametro.getType()));
				throw new EntityOperationException();
			}
		}
	}

	private boolean doCheckDobleInicio() {
		if (!isIgnoreCheckDobleInicio()) {
			setExpedienteDobleInicio(null);
			CriterioCnx criterioDobleInicio = ConexidadManager.instance()
					.buscaDobleInicio(this);
			if (criterioDobleInicio != null) {
				String errKey = "expedienteMe.error.dobleInicio";
				String numeroAnio = (getExpedienteDobleInicio() != null) ? getExpedienteDobleInicio().getNumeroAnio(): "";
				errorMsg(errKey, criterioDobleInicio.getNombre(), numeroAnio);
				return true;
			}
		}
		return false;
	}

	private void checkMontoJuicio() throws EntityOperationException {
		if (showMontoJuicio()) {
			montoJuicioEdit.checkAccept();
		}
	}

	public boolean isMontoJuicioRequired() {
		return showMontoJuicio() && isCOM()
				&& (isObjetoJuicioQuiebras() || isObjetoJuicioPedidosQuiebra());
	}

	private void checkTratamientoIncidental() throws EntityOperationException {
		if (showTratamientoIncidental() && isTratamientoIncidental()) {
			if (getExpedienteEspecialEdit().getTipoSubexpediente() == null) {
				errorMsg("expedienteMeAdd.error.noTipoSubexpediente");
				throw new EntityOperationException();
			}
			if (!getIntervinienteEditList().isAnyIntervinienteSelected()) {
				errorMsg("expedienteMeAdd.error.noIntervinientesSelected");
				throw new EntityOperationException();
			}
		}
	}

	public boolean isTratamientoIncidental() {
		return (getExpedienteEspecialEdit() != null)
				&& getExpedienteEspecialEdit().isTratamientoIncidental();
	}

	private void checkTipoRecurso() throws EntityOperationException {
		if (showTipoRecurso()) {
			if (getRecursoEdit().getTipoRecurso() == null
					&& !isIngresoAnteriorSistema()) {
				errorMsg("expedienteMeAdd.error.noTipoRecurso");
				throw new EntityOperationException();
			}
			if (getRecursoEdit().getFechaPresentacion() != null
					&& esFechaFuturo(getRecursoEdit().getFechaPresentacion())) {
				errorMsg("expedienteMeAdd.error.fechaPresentacionNoFuturo");
				throw new EntityOperationException();
			}
		}
	}

	private boolean esFechaFuturo(Date date) {
		return !date.before(DateUtil.addDays(new Date(), 1));
	}

	public void updateExpedienteMediacion() {
		Expediente expediente = ExpedienteManager.findExpedienteMediacion(
				getEntityManager(), this.numeroExpedienteMediacion,
				this.anioExpedienteMediacion);
		setExpedienteMediacion(expediente);
	}

	public void resetExpedienteMediacion() {
		setExpedienteMediacion(null);
	}

	private void checkConexidadDeclaradaSegundaInstancia() throws EntityOperationException {
		if (showConexidadDeclaradaSegundaInstancia() && !isIngresoWeb()) {
			boolean updated = false;

			// Si ha habido cambios se actualiza
			if (((conexidadDeclaradaSegundaInstancia.getExpedienteConexo() != null) && ((!conexidadDeclaradaSegundaInstancia
					.getExpedienteConexo().getNumero()
					.equals(conexidadDeclaradaSegundaInstancia.getNumeroExpedienteConexo()) || !conexidadDeclaradaSegundaInstancia
					.getAnioExpedienteConexo().equals(
							conexidadDeclaradaSegundaInstancia.getAnioExpedienteConexo())) || conexidadDeclaradaSegundaInstancia
					.isAnteriorAlSistema()))
					|| (!conexidadDeclaradaSegundaInstancia.isAnteriorAlSistema()
							&& (conexidadDeclaradaSegundaInstancia.getExpedienteConexo() == null)
							&& (conexidadDeclaradaSegundaInstancia.getAnioExpedienteConexo() != null) && (conexidadDeclaradaSegundaInstancia
							.getNumeroExpedienteConexo() != null))) {
				updated = true;
				updateConexidadDeclaradaSegundaInstancia();
			}

			if (!conexidadDeclaradaSegundaInstancia.isEmpty()) {
				if ((conexidadDeclaradaSegundaInstancia.isAnteriorAlSistema() && (conexidadDeclaradaSegundaInstancia
						.getOficina() == null))
						|| (conexidadDeclaradaSegundaInstancia.getAnioExpedienteConexo() == null)
						|| (conexidadDeclaradaSegundaInstancia.getNumeroExpedienteConexo() == null)) {
					errorMsg("expedienteMe.error.conexidadDeclarada");
					throw new EntityOperationException();
				}

				if (!conexidadDeclaradaSegundaInstancia.isAnteriorAlSistema()) {
					if (conexidadDeclaradaSegundaInstancia.getExpedienteConexo() == null) {
						String errKey = "conexidad.conexidadDeclarada.expediente.notFound";
						errorMsg(
								errKey,
								String.valueOf(conexidadDeclaradaSegundaInstancia
										.getNumeroExpedienteConexo())
										+ "/"
										+ String.valueOf(conexidadDeclaradaSegundaInstancia
												.getAnioExpedienteConexo()));
						throw new EntityOperationException();
					} else  {
						if (conexidadDeclaradaSegundaInstancia.getOficina() == null) {
							String errKey = "conexidad.conexidadDeclarada.oficina.notExists";
							errorMsg(
									errKey,
									Messages.instance().get(
											TipoRadicacionEnumeration.Type.sala.getLabel()));
							throw new EntityOperationException();
						}
					}
				}
				if (!isAnioConexidadDeclaradaSegundaInstanciaValido(conexidadDeclaradaSegundaInstancia.getAnioExpedienteConexo()))  {
					errorMsg("expedienteMe.error.conexidadDeclaradaSegundaInstanciaAnioNoValido");
					throw new EntityOperationException();
				}
			} else if (isConexidadDeclaradaSegundaInstanciaRequerida()) {
				errorMsg("expedienteMe.error.conexidadDeclaradaSegundaInstanciaRequerida");
				throw new EntityOperationException();
			}
			if (updated) {
				getStatusMessages().addFromResourceBundle(Severity.WARN,
						"conexidad.conexidadDeclaradaSegundaInstancia.comprobarDatos");
				throw new EntityOperationException();
			}
		}
	}

	private boolean isConexidadDeclaradaSegundaInstanciaRequerida() {
		return showConexidadDeclaradaSegundaInstancia() && isSeguridadSocial() && ObjetoJuicioEnumeration.EJECUCION_PREVISIONAL.equals(getObjetoJuicio().getCodigo());
	}

	private boolean isAnioConexidadDeclaradaSegundaInstanciaValido(Integer anio) {
		if (showConexidadDeclaradaSegundaInstancia() && isSeguridadSocial() && ObjetoJuicioEnumeration.EJECUCION_PREVISIONAL.equals(getObjetoJuicio().getCodigo())) {
			return (anio != null) && (anio < MIN_ANIO_CONEXIDAD_SALA_EJECUCION_PREVISIONAL);
		}
		return true;
	}

	private void checkConexidadDeclarada() throws EntityOperationException {
		if (showConexidadDeclarada() && !isIngresoWeb()) {
			boolean updated = false;

			// Si ha habido cambios se actualiza
			if (((conexidadDeclarada.getExpedienteConexo() != null) && ((!conexidadDeclarada
					.getExpedienteConexo().getNumero()
					.equals(conexidadDeclarada.getNumeroExpedienteConexo()) || !conexidadDeclarada
					.getAnioExpedienteConexo().equals(
							conexidadDeclarada.getAnioExpedienteConexo())) || conexidadDeclarada
					.isAnteriorAlSistema()))
					|| (!conexidadDeclarada.isAnteriorAlSistema()
							&& (conexidadDeclarada.getExpedienteConexo() == null)
							&& (conexidadDeclarada.getAnioExpedienteConexo() != null) && (conexidadDeclarada
							.getNumeroExpedienteConexo() != null))) {
				updated = true;
				updateConexidadDeclarada();
			}

			if (!conexidadDeclarada.isEmpty()) {
				if ((conexidadDeclarada.isAnteriorAlSistema() && (conexidadDeclarada
						.getOficina() == null))
						|| (conexidadDeclarada.getAnioExpedienteConexo() == null)
						|| (conexidadDeclarada.getNumeroExpedienteConexo() == null)) {
					errorMsg("expedienteMe.error.conexidadDeclarada");
					throw new EntityOperationException();
				}

				if (!conexidadDeclarada.isAnteriorAlSistema()) {
					if (conexidadDeclarada.getExpedienteConexo() == null) {
						String errKey = "conexidad.conexidadDeclarada.expediente.notFound";
						errorMsg(
								errKey,
								String.valueOf(conexidadDeclarada
										.getNumeroExpedienteConexo())
										+ "/"
										+ String.valueOf(conexidadDeclarada
												.getAnioExpedienteConexo()));
						throw new EntityOperationException();
					} else if (!TipoCausaEnumeration
							.isRecursoDirecto(getTipoCausa())) {
						if (conexidadDeclarada.getOficina() != null) {
							if (!getOficinasConexidadDeclarada().contains(
									conexidadDeclarada.getOficina())) {
								errorMsg(
										"expedienteMe.error.oficinaConexidadDeclarada",
										Messages.instance().get(
												getTipoRadicacion().getLabel()));
								throw new EntityOperationException();
							}
						} else {
							String errKey = "conexidad.conexidadDeclarada.oficina.notExists";
							errorMsg(
									errKey,
									Messages.instance().get(
											getTipoRadicacion().getLabel()));
							throw new EntityOperationException();
						}
					}
					checkFechaConexidadDeclarada(conexidadDeclarada);
					checkPartesConexidadDeclarada(conexidadDeclarada);
					checkObjetoJuicioConexidadDeclarada(conexidadDeclarada);
				}
			}
			if (updated) {
				getStatusMessages().addFromResourceBundle(Severity.WARN,
						"conexidad.conexidadDeclarada.comprobarDatos");
				throw new EntityOperationException();
			}
		}
	}

	private void checkObjetoJuicioConexidadDeclarada(
			ConexidadDeclarada conexidadDeclarada)
			throws EntityOperationException {
		if (conexidadDeclarada.isSolicitadaPorMesa()) {
			checkObjetoJuicioConexidadSolicitadaMesa(conexidadDeclarada);
		} else if (conexidadDeclarada.isPrevencion()
				|| ConfiguracionMateriaManager.instance()
						.isConexidadDeclaradaVerificaObjetoJuicio(getCamara(),
								getMateria())) {
			checkCoincidenciaOJConexidadDeclarada(conexidadDeclarada);
		}
	}

	private boolean objetoJuicioVerificaPartesConexidadDeclarada() {
		return !isCamaraLaboral() || 
			(!isObjetoJuicioExtensiónResponsabilidad() && 
			 !isObjetoJuicioEjecucionHonorarios() && 
			 !isObjetoJuicioEjecucionSentencia() && 
			 !isObjetoJuicioRevisionCosaJuzgada());
	}

	private void checkPartesConexidadDeclarada(
			ConexidadDeclarada conexidadDeclarada)
			throws EntityOperationException {
		if (!conexidadDeclarada.isSolicitadaPorMesa() && objetoJuicioVerificaPartesConexidadDeclarada()) {
			if (conexidadDeclarada.isPrevencion()
					|| ConfiguracionMateriaManager.instance()
							.isConexidadDeclaradaVerificaPartes(getCamara(),
									getMateria())) {
				checkCoincidenciaPartesConexidadDeclarada(conexidadDeclarada, conexidadDeclarada.isDeclarada());
			}
		}
	}

	private void checkObjetoJuicioConexidadSolicitadaMesa(
			ConexidadDeclarada conexidadDeclarada)
			throws EntityOperationException {
		String codigoObjetoJuicioConexo = ((conexidadDeclarada
				.getExpedienteConexo() != null) && (conexidadDeclarada
				.getExpedienteConexo().getObjetoJuicioPrincipal() != null)) ? conexidadDeclarada
				.getExpedienteConexo().getObjetoJuicioPrincipal().getCodigo()
				: null;
		ObjetoJuicio objetoJuicio = ObjetoJuicioSearch.findByCodigo(
				getEntityManager(), codigoObjetoJuicioConexo, getMateria());
		if ((objetoJuicio == null) || !objetoJuicio.isConexidadSolicitadaMesa()) {
			getStatusMessages().addFromResourceBundle(Severity.ERROR,
					"conexidad.conexidadDeclarada.objetoJuicio.noValido",
					codigoObjetoJuicioConexo);
			throw new EntityOperationException();
		}
	}

	private void checkCoincidenciaOJConexidadDeclarada(
			ConexidadDeclarada conexidadDeclarada)
			throws EntityOperationException {

		String codigoObjetoJuicio = (getObjetoJuicio() != null) ? getObjetoJuicio()
				.getCodigo() : null;
		String codigoObjetoJuicioConexo = ((conexidadDeclarada
				.getExpedienteConexo() != null) && (conexidadDeclarada
				.getExpedienteConexo().getObjetoJuicioPrincipal() != null)) ? conexidadDeclarada
				.getExpedienteConexo().getObjetoJuicioPrincipal().getCodigo()
				: null;
		if (!equals(codigoObjetoJuicio, codigoObjetoJuicioConexo)) {
			getStatusMessages().addFromResourceBundle(Severity.ERROR,
					"conexidad.conexidadDeclarada.sinCoincidenciaObjetoJuicio");
			throw new EntityOperationException();
		}
	}

	private void checkCoincidenciaPartesConexidadDeclarada(
			ConexidadDeclarada conexidadDeclarada, boolean admiteCruzada)
			throws EntityOperationException {
		
		Conexidad conexidad = new Conexidad();
		boolean found = false;
		int numeroCoincidenciasActor = 0;
		int numeroCoincidenciasDemandado = 0;
		int numeroCoincidenciasActorX = 0;
		int numeroCoincidenciasDemandadoX = 0;
		for (IntervinienteEdit intervinienteEdit : getIntervinienteEditList()
				.getList()) {
			for (IntervinienteExp intervinienteExp : conexidadDeclarada
					.getExpedienteConexo().getIntervinienteExpList()) {
				if (!intervinienteExp.isLogicalDeleted()) {
					Interviniente intervinienteRelacionado = intervinienteExp
							.getInterviniente();
					if (intervinienteEdit.getNombreCompleto() != null
							&& intervinienteEdit.getNombreCompleto().equals(
									intervinienteRelacionado.getNombre())) {
						if (intervinienteEdit.isActor()) {
							if (intervinienteExp.isActor()) {
								numeroCoincidenciasActor++;
							} else if ( intervinienteExp.isDemandado()) {
								numeroCoincidenciasActorX++;
							}
						} else if (intervinienteEdit.isDemandado()) {
							if(intervinienteExp.isDemandado()) {
								numeroCoincidenciasDemandado++;
							} else if (intervinienteExp.isActor()) {
								numeroCoincidenciasDemandadoX++;
							}
						}
					}
					if (!Strings.isEmpty(intervinienteEdit.getNumero())
							&& intervinienteEdit.getNumero().equals(
									intervinienteRelacionado.getNumeroDocId())) {
						if (intervinienteEdit.isActor()) {
							if(intervinienteExp.isActor()) {
								numeroCoincidenciasActor++;
							} else if (intervinienteExp.isDemandado()) {
								numeroCoincidenciasActorX++;
							}
						} else if (intervinienteEdit.isDemandado()) {
							if(intervinienteExp.isDemandado()) {
								numeroCoincidenciasDemandado++;
							} else if(intervinienteExp.isActor()) {
								numeroCoincidenciasDemandadoX++;
							}
						}
						// Añadir coincidencias por cuit de las empresas del
						// grupo
					} else if (!Strings.isEmpty(intervinienteRelacionado
							.getDni())
							&& intervinienteRelacionado.getDni().equals(
									IntervinienteEditList
											.getDNI(intervinienteEdit))) {
						if (intervinienteEdit.isActor()) {
							if (intervinienteExp.isActor()) {
								numeroCoincidenciasActor++;
							} else if (intervinienteExp.isDemandado()) {
								numeroCoincidenciasActorX++;
							}
						} else if (intervinienteEdit.isDemandado()) {
							if (intervinienteExp.isDemandado()) {
								numeroCoincidenciasDemandado++;
							} else if(intervinienteExp.isActor()) {
								numeroCoincidenciasDemandadoX++;
							}
						}
					}
				}
			}
			if (numeroCoincidenciasActor > 0
					&& numeroCoincidenciasDemandado > 0) {
				found = true;
			}
			if (!found && admiteCruzada && numeroCoincidenciasActorX > 0
					&& numeroCoincidenciasDemandadoX > 0) {
				found = true;
			}
			if (found) {
				break;
			}
		}
		if (!found) {
			getStatusMessages().addFromResourceBundle(Severity.ERROR,
					"conexidad.conexidadDeclarada.sinCoincidenciaPartes");
			throw new EntityOperationException();
		}
	}

	private boolean objetoJuicioVerificaFechaConexidadDeclarada() {
		return !isCamaraLaboral() || 
			(!isObjetoJuicioExtensiónResponsabilidad() && 
			 !isObjetoJuicioEjecucionHonorarios() && 
			 !isObjetoJuicioEjecucionSentencia() && 
			 !isObjetoJuicioRevisionCosaJuzgada());
	}

	private void checkFechaConexidadDeclarada(
			ConexidadDeclarada conexidadDeclarada)
			throws EntityOperationException {
		boolean ret = true;

		Date minimaFecha = null;

		if (objetoJuicioVerificaFechaConexidadDeclarada()) {
			String plazoConexidadDeclarada = null;
			if (conexidadDeclarada.isSolicitadaPorMesa()) {
				plazoConexidadDeclarada = ConfiguracionMateriaManager.instance()
						.getConexidadSolicitadaMesaPlazo(getCamara(), getMateria());
			} else if (conexidadDeclarada.isPrevencion()) {
				plazoConexidadDeclarada = ConfiguracionMateriaManager.instance()
						.getPrevencionDeclaradaPlazo(getCamara(), getMateria());
			} else {
				plazoConexidadDeclarada = ConfiguracionMateriaManager.instance()
						.getConexidadDeclaradaPlazo(getCamara(), getMateria());
			}
			if (!Strings.isEmpty(plazoConexidadDeclarada)) {
				minimaFecha = ConfiguracionMateriaManager.getFechaAnterior(
						DateUtil.getToday(), plazoConexidadDeclarada);
			}
		}
		if (minimaFecha != null) {
			Date fechaExpediente;
			if (isIngresoASala()) {
				fechaExpediente = ExpedienteManager.instance()
						.getFechaAsignacionSala(
								conexidadDeclarada.getExpedienteConexo());
			} else {
				fechaExpediente = getFechaExpediente(conexidadDeclarada
						.getExpedienteConexo());
			}
			if ((fechaExpediente != null) && minimaFecha.after(fechaExpediente)) {
				errorMsg("expedienteMe.error.fechaConexidadDeclarada",
						defaultParameterDateFormat.format(minimaFecha));
				throw new EntityOperationException();
			}
		}
	}

	protected Date getFechaExpediente(Expediente expediente) {
		Date fecha = null;
		fecha = expediente.getFechaSorteoCarga();
		if (fecha == null) {
			fecha = expediente.getFechaIngreso();
		}
		if ((fecha == null) && ExpedienteManager.instance().isIniciado(expediente)) {
			fecha = expediente.getFechaInicio();
		}
		return fecha;
	}

	public void onChangeNumeroExpedienteConexo(ValueChangeEvent event) {
		if (event != null) {
			if (!equals(event.getNewValue(),
					conexidadDeclarada.getNumeroExpedienteConexo())) {
				conexidadDeclarada.setNumeroExpedienteConexo((Integer) event
						.getNewValue());
				updateConexidadDeclarada();
			}
		}
	}

	public void onChangeAnioExpedienteConexo(ValueChangeEvent event) {
		if (event != null && event.getNewValue() != null) {
			if (!equals(event.getNewValue(),
					conexidadDeclarada.getAnioExpedienteConexo())) {
				conexidadDeclarada.setAnioExpedienteConexo((Integer) event
						.getNewValue());
				updateConexidadDeclarada();
			}
		}
	}

	public void updateConexidadDeclarada() {
		if (!conexidadDeclarada.isAnteriorAlSistema()) {
			Oficina oficina = null;
			ConexidadManager.instance().solveExpedienteConexidadDeclarada(
					conexidadDeclarada);
			if (conexidadDeclarada.getExpedienteConexo() != null) {
				OficinaAsignacionExp radicacion = ExpedienteManager.instance()
						.findRadicacionExpediente(
								conexidadDeclarada.getExpedienteConexo(),
								getTipoRadicacion());
				if (radicacion != null) {
					if (getOficinasConexidadDeclarada().contains(
							radicacion.getOficina())) {
						oficina = radicacion.getOficina();
					} else if (getOficinasConexidadDeclarada().contains(
							radicacion.getSecretaria())) {
						oficina = radicacion.getSecretaria();
					} else {
						oficina = radicacion.getOficina();
					}
				}
			}
			conexidadDeclarada.setOficina(oficina);
		} else {
			conexidadDeclarada.setExpedienteConexo(null);
		}
	}

	public boolean isUseConexidadSolicitadaMesa() { // Conexidad por Tema
		if (getObjetoJuicio() != null) {
			return getObjetoJuicio().isConexidadSolicitadaMesa();
		}
		return false;
	}

	public List<SelectItem> getTiposConexidadSolicitada() {
		return TipoConexidadSolicitadaEnumeration.instance()
				.getTiposConexidadSolicitada(isUseConexidadSolicitadaMesa());
	}

	public boolean hayTiposConexidadSolicitada() {
		return getTiposConexidadSolicitada().size() > 1;
	}

	public void updateAndCheckConexidadDeclarada() {
		updateConexidadDeclarada();
		try {
			checkConexidadDeclarada();
		} catch (EntityOperationException e) {
		}
	}

	public void resetConexidadDeclarada() {
		conexidadDeclarada.setNumeroExpedienteConexo(null);
		conexidadDeclarada.setAnioExpedienteConexo(null);
		conexidadDeclarada.setAbreviaturaCamaraExpedienteConexo(null);
		conexidadDeclarada.setOficina(null);
		conexidadDeclarada.setExpedienteConexo(null);
	}

	public void resetConexidadDeclarada2() {
		conexidadDeclaradaSegundaInstancia.setNumeroExpedienteConexo(null);
		conexidadDeclaradaSegundaInstancia.setAnioExpedienteConexo(null);
		conexidadDeclaradaSegundaInstancia.setAbreviaturaCamaraExpedienteConexo(null);
		conexidadDeclaradaSegundaInstancia.setOficina(null);
		conexidadDeclaradaSegundaInstancia.setExpedienteConexo(null);
	}

	public void updateAndCheckConexidadDeclarada2() {
		updateConexidadDeclaradaSegundaInstancia();
		try {
			checkConexidadDeclaradaSegundaInstancia();
		} catch (EntityOperationException e) {
		}
	}

	public void updateConexidadDeclaradaSegundaInstancia() {
		Oficina oficina = null;
		ConexidadManager.instance().solveExpedienteConexidadDeclarada(
				conexidadDeclaradaSegundaInstancia);
		if (conexidadDeclaradaSegundaInstancia.getExpedienteConexo() != null) {
			OficinaAsignacionExp radicacion = ExpedienteManager.instance()
					.findRadicacionExpediente(
							conexidadDeclaradaSegundaInstancia.getExpedienteConexo(),
							TipoRadicacionEnumeration.Type.sala);
			if (radicacion != null) {
				oficina = radicacion.getOficina();
			}
		}
		conexidadDeclaradaSegundaInstancia.setOficina(oficina);
	}
	
	public void updateAndCheckConexidadDeclaradaSegundaInstancia() {
		updateConexidadDeclaradaSegundaInstancia();
		try {
			checkConexidadDeclaradaSegundaInstancia();
		} catch (EntityOperationException e) {
		}
	}

	public void resetConexidadDeclaradaSegundaInstancia() {
		conexidadDeclaradaSegundaInstancia.setNumeroExpedienteConexo(null);
		conexidadDeclaradaSegundaInstancia.setAnioExpedienteConexo(null);
		conexidadDeclaradaSegundaInstancia.setAbreviaturaCamaraExpedienteConexo(null);
		conexidadDeclaradaSegundaInstancia.setOficina(null);
		conexidadDeclaradaSegundaInstancia.setExpedienteConexo(null);
	}

	public boolean isVinculado() {
		return isVinculado(getTipoCausa());
	}

	public boolean isVinculado(TipoCausa tipoCausa) {
		return tipoCausa != null && tipoCausa.isVinculado();
	}

	public boolean showSelectorPoder() {
		return ConfiguracionMateriaManager.instance().isCamaraUsaPoderes(
				getCamara());
	}

	public boolean showVinculadoMediacion() {
		return showVinculadoMediacion(getTipoCausa());
	}

	public boolean showVinculadoMediacion(TipoCausa tipoCausa) {
		return tipoCausa != null
				&& TipoCausaEnumeration.isProvenienteMediacion(tipoCausa);
	}

	public boolean showMinisteriosPublicos() {
		return showMinisteriosPublicos(getTipoCausa());
	}

	public boolean showMinisteriosPublicos(TipoCausa tipoCausa) {
		return tipoCausa != null
				&& TipoCausaEnumeration.isMinisteriosPublicos(tipoCausa);
	}

	public boolean isIngresoAJuzgado() {
		return TipoRadicacionEnumeration
				.isRadicacionJuzgado(getTipoRadicacion());
	}

	public boolean isIngresoASala() {
		return TipoRadicacionEnumeration.isRadicacionSala(getTipoRadicacion());
	}

	public boolean isIngresoASala(TipoRadicacionEnumeration.Type tipoRadicacion) {
		return TipoRadicacionEnumeration.isRadicacionSala(tipoRadicacion);
	}

	public boolean isIngresoATO() {
		return TipoRadicacionEnumeration
				.isRadicacionTribunalOral(getTipoRadicacion());
	}

	public Integer getNumeroExpedientePrincipal() {
		if (this.numeroExpedientePrincipal == null) {
			this.numeroExpedientePrincipal = (getExpedientePrincipal() != null) ? getExpedientePrincipal()
					.getNumero() : null;
		}
		return this.numeroExpedientePrincipal;
	}

	public void setNumeroExpedientePrincipal(Integer numeroExpedientePrincipal) {
		this.numeroExpedientePrincipal = numeroExpedientePrincipal;
	}

	public void onChangeNumeroExpedientePrincipal(ValueChangeEvent event) {
		setNumeroExpedientePrincipal((Integer) (event.getNewValue()));
		updateExpedientePrincipal();
	}

	public Integer getAnioExpedientePrincipal() {
		if (this.anioExpedientePrincipal == null) {
			this.anioExpedientePrincipal = (getExpedientePrincipal() != null) ? getExpedientePrincipal()
					.getAnio() : null;
		}
		return this.anioExpedientePrincipal;
	}

	public void setAnioExpedientePrincipal(Integer anioExpedientePrincipal) {
		this.anioExpedientePrincipal = anioExpedientePrincipal;
	}

	public void onChangeAnioExpedientePrincipal(ValueChangeEvent event) {
		setAnioExpedientePrincipal((Integer) (event.getNewValue()));
		updateExpedientePrincipal();
	}

	private void updateExpedientePrincipal() {
		Expediente expediente = ExpedienteManager.findExpedienteIniciado(
				getEntityManager(), this.numeroExpedientePrincipal,
				this.anioExpedientePrincipal, true);
		setExpedientePrincipal(expediente);
	}

	public Expediente getExpedientePrincipal() {
		return expedientePrincipal;
	}

	public void setExpedientePrincipal(Expediente expedientePrincipal) {
		this.expedientePrincipal = expedientePrincipal;
	}

	public DelitoEditList getDelitoEditList() {
		return delitoEditList;
	}

	public void setDelitoEditList(DelitoEditList delitoEditList) {
		this.delitoEditList = delitoEditList;
	}

	public FojasEdit getFojasEdit() {
		return fojasEdit;
	}

	public void setFojasEdit(FojasEdit fojasEdit) {
		this.fojasEdit = fojasEdit;
	}

	public String getNumeroExpedienteOrigenLabel() {
		TipoCausa tipoCausa = getTipoCausa();
		return getNumeroExpedienteOrigenLabel(tipoCausa);
	}

	public String getNumeroExpedienteOrigenLabel(TipoCausa tipoCausa) {
		if ((tipoCausa != null)
				&& MateriaEnumeration.isAnyPenal(tipoCausa.getMateria())) {
			if (TipoCausaEnumeration.isExhorto(tipoCausa)) {
				return getMessages().get(
						"expedienteEspecial.numeroExpediente.penal");
			} else {
				return getMessages().get(
						"expedienteEspecial.numeroSumario.penal");
			}
		}
		return getMessages().get("expedienteEspecial.numeroExpediente.origen");
	}

	public String getFechaInformacionLabel() {
		TipoCausa tipoCausa = getTipoCausa();
		return getFechaInformacionLabel(tipoCausa);
	}

	public String getFechaInformacionLabel(TipoCausa tipoCausa) {
		if ((tipoCausa != null)
				&& MateriaEnumeration.isAnyPenal(tipoCausa.getMateria())) {
			return getMessages().get(
					"expedienteEspecial.fechaInformacion.penal");
		} else {
			return getMessages().get("expedienteEspecial.fechaInformacion");
		}
	}

	public String getAddPage(boolean especial, String pageEspecial,
			String pageNormal) {
		if (especial) {
			return pageEspecial;
		} else {
			return pageNormal;
		}
	}

	//

	public boolean startAtPage0(TipoCausa tipoCausa) {
		return hasPage0(tipoCausa) && !hasPageEspecial0(tipoCausa);
	}

	public boolean startAtPage1(TipoCausa tipoCausa) {
		return !startAtPage0(tipoCausa) && !hasPage0(tipoCausa)
				&& hasPage1(tipoCausa) && !hasPageEspecial0(tipoCausa);
	}

	public boolean startAtPageEspecial(TipoCausa tipoCausa) {
		return !startAtPage0(tipoCausa) && !startAtPage1(tipoCausa)
				&& !startAtPageEspecial0(tipoCausa)
				&& hasPageEspecial(tipoCausa);
	}

	public boolean startAtPageEspecial0(TipoCausa tipoCausa) {
		return !startAtPage0(tipoCausa) && !startAtPage1(tipoCausa)
				&& hasPageEspecial0(tipoCausa);
	}

	public boolean isEspecial() {
		return (getTipoCausa() != null) && getTipoCausa().isEspecial();
	}

	public boolean hasPageEspecial() {
		return hasPageEspecial(getTipoCausa());
	}

	public boolean hasPageEspecial(TipoCausa tipoCausa) {
		return (showOrigen(tipoCausa) //|| showFojasCuerposEtc(tipoCausa)
				|| showMinisteriosPublicos(tipoCausa)
				|| showDeclaracionHecho(tipoCausa) || showFechaHecho(tipoCausa) || showFojas(tipoCausa))
				&& !hasPageEspecial0(tipoCausa);
	}

	public boolean hasPageEspecial0() {
		return hasPageEspecial0(getTipoCausa());
	}

	public boolean hasPageEspecial0(TipoCausa tipoCausa) {
		return TipoCausaEnumeration.isRecursoDirecto(tipoCausa)
				|| TipoCausaEnumeration
						.isIncompetenciaExternaNoInformatizada(tipoCausa)
				|| (TipoCausaEnumeration.isMinisteriosPublicos(tipoCausa) && !isEditMode());
	}

	public boolean hasPage0() {
		return hasPage0(getTipoCausa());
	}

	public boolean hasPage0(TipoCausa tipoCausa) {
		return showPoder(tipoCausa)
				|| TipoCausaEnumeration.isProvenienteMediacion(tipoCausa);
	}

	public boolean hasPage1() {
		return hasPage1(getTipoCausa());
	}

	public boolean hasPage1(TipoCausa tipoCausa) {
		return showIntervinientes(tipoCausa) || showDelitos(tipoCausa)
				|| showObjetoJuicio(tipoCausa) || isVinculado(tipoCausa)
				|| showCompetenciasPenales();
	}

	public boolean hasPage2() {
		if ((TipoCausaEnumeration.isExhorto(getTipoCausa()) /*
															 * ||
															 * isObjetoJuicioVoluntario
															 * ()
															 */)
				&& !showTipoRecurso()) {
			return false;
		} else {
			return showParametros() || showConexidadDeclarada()
					|| showExpedientenInfo() || showMontoJuicio()
					|| showCompetenciasCiviles() || showTipoRecurso();
		}
	}

	public boolean showSeclo() {
		return showSeclo(getTipoCausa());
	}

	public boolean showSeclo(TipoCausa tipoCausa) {
		return (tipoCausa == null)
				&& ConfiguracionMateriaManager.instance().isCamaraUsaSeclo(
						getCamara());
	}

	public boolean showPoder() {
		return showPoder(getTipoCausa());
	}

	public boolean showPoder(TipoCausa tipoCausa) {
		return ((tipoCausa == null)
				&& ConfiguracionMateriaManager.instance().isCamaraUsaPoderes(
						getCamara()) && !isIngresoAnteriorSistema() && !hasPageEspecial0(tipoCausa));
	}

	public boolean showTipoProceso() {
		return showTipoProceso(getTipoCausa());
	}

	private boolean showTipoProceso(TipoCausa tipoCausa) {
		return TipoCausaEnumeration.isJuiciosOriginarios(tipoCausa) && !MateriaEnumeration.isAnyPenal(tipoCausa.getMateria());
	}

	public boolean showCoincidenciasPorNumeroOrigen() {
		return ConfiguracionMateriaManager.instance().isMostrarCoincidenciasPorNumeroOrigen(getCamara(),getMateria());
	}
	
	public boolean showOrigen() {
		return showOrigen(getTipoCausa());
	}

	public static boolean showOrigen(TipoCausa tipoCausa) {
		boolean show = false;
		if (tipoCausa != null) {
			if (MateriaEnumeration.isAnyPenal(tipoCausa.getMateria())) {
				if (TipoCausaEnumeration.isExhorto(tipoCausa)
						|| TipoCausaEnumeration.isCambioAsignacion(tipoCausa)
						|| TipoCausaEnumeration
								.isMedidasPrecautorias(tipoCausa)
						|| TipoCausaEnumeration.isExtradicion(tipoCausa)
						|| TipoCausaEnumeration.isOtrosOrganismos(tipoCausa)
						|| TipoCausaEnumeration.isPrevencion(tipoCausa)
						|| TipoCausaEnumeration.isTestimonio(tipoCausa)
						|| TipoCausaEnumeration.isIncompetenciaTO(tipoCausa) // ITO
																				// (Incompetencia
																				// TO)
						|| TipoCausaEnumeration.isInhibitoria(tipoCausa)
						|| TipoCausaEnumeration.isRecursoDirecto(tipoCausa)
						|| TipoCausaEnumeration.isNN(tipoCausa)
						|| (TipoCausaEnumeration.isDenuncia(tipoCausa) && isJuzgadoOSecretariaPenalOrdinario())) {
					show = true;
				}
			} else if (tipoCausa.isEspecial()) {
				if (TipoCausaEnumeration.isProvenienteMediacion(tipoCausa)
						|| TipoCausaEnumeration
								.isMinisteriosPublicos(tipoCausa)
						|| TipoCausaEnumeration.isSeclo(tipoCausa)) {
					show = false;
				} else {
					show = true;
				}
			}
		}
		return show;
	}

	private static boolean isJuzgadoOSecretariaPenalOrdinario() {
		Oficina oficinaActual = SessionState.instance().getGlobalOficina();
		return CamaraManager.isCamaraPenalOrdinario(CamaraManager
				.getCamaraActual())
				&& (OficinaManager.instance()
						.isSecretariaJuzgado(oficinaActual) || OficinaManager
						.instance().isJuzgado(oficinaActual));
	}

	public boolean showFechaInformacion() {
		return showFechaInformacion(getTipoCausa());
	}

	public static boolean showFechaInformacion(TipoCausa tipoCausa) {
		return showOrigen(tipoCausa)
				&& !TipoCausaEnumeration.isSeclo(tipoCausa);
	}

	public boolean showNumeroCodigo() {
		return showNumeroCodigo(getTipoCausa());
	}

	public boolean showNumeroCodigo(TipoCausa tipoCausa) {
		boolean show = showOrigen() && CamaraManager.isCamaraActualCivilCABA();
		return show;
	}

	public boolean showDependenciaOrigen() {
		return showDependenciaOrigen(getTipoCausa());
	}

	public boolean showDependenciaOrigen(TipoCausa tipoCausa) {
		return (tipoCausa != null)
				&& !(TipoCausaEnumeration.isIniciadoPorELJuzgado(tipoCausa)
						|| TipoCausaEnumeration.isReconstruccion(tipoCausa) || TipoCausaEnumeration
						.isSeclo(tipoCausa));
	}

	public boolean isRequiredNumeroCodigo() {
		return isRequiredNumeroCodigo(getTipoCausa());
	}

	public boolean isRequiredNumeroCodigo(TipoCausa tipoCausa) {
		return showNumeroCodigo()
				&& (tipoCausa != null)
				&& !(TipoCausaEnumeration.isIniciadoPorELJuzgado(tipoCausa) || TipoCausaEnumeration
						.isReconstruccion(tipoCausa));
	}

	public boolean isRequiredCaratulaOrigen() {
		return isRequiredCaratulaOrigen(getTipoCausa());
	}

	public boolean isRequiredCaratulaOrigen(TipoCausa tipoCausa) {
		return showCaratulaOrigen()
				&& (tipoCausa != null)
				&& 
					(TipoCausaEnumeration.isExhorto(tipoCausa)
					|| 
					TipoCausaEnumeration.isInhibitoria(tipoCausa));
	}

	public boolean isRequiredNumeroExpedienteOrigen() {
		return isRequiredNumeroExpedienteOrigen(getTipoCausa());
	}

	public boolean isRequiredNumeroExpedienteOrigen(TipoCausa tipoCausa) {
		return showOrigen() && (tipoCausa != null)
				&& !(TipoCausaEnumeration.isPresentacionesVarias(tipoCausa)) && !(TipoCausaEnumeration.isJuiciosOriginarios(tipoCausa)) && !(TipoCausaEnumeration.isExpedientesOriginariosPenales(tipoCausa));
	}

	public boolean isRequiredFechaInformacion() {
		return isRequiredFechaInformacion(getTipoCausa());
	}

	public boolean isRequiredFechaInformacion(TipoCausa tipoCausa) {
		return showOrigen() && (tipoCausa != null)
				&& !(TipoCausaEnumeration.isPresentacionesVarias(tipoCausa)) && !(TipoCausaEnumeration.isJuiciosOriginarios(tipoCausa)) && !(TipoCausaEnumeration.isExpedientesOriginariosPenales(tipoCausa));
	}

	public boolean isShowOficinaRadicacionPrevia() {
		return isShowOficinaRadicacionPrevia(getTipoCausa());
	}

	public boolean isShowOficinaRadicacionPrevia(TipoCausa tipoCausa) {
		return (TipoCausaEnumeration.isIniciadoPorELJuzgado(tipoCausa) || TipoCausaEnumeration
				.isReconstruccion(tipoCausa) || 
				MateriaEnumeration.isJuiciosOriginarios(getMateria()));
	}

	public boolean showJuezExortante() {
		TipoCausa tipoCausa = getTipoCausa();
		return showJuezExortante(tipoCausa);
	}

	public static boolean showJuezExortante(TipoCausa tipoCausa) {
		if (tipoCausa == null)
			return false;
		boolean show = false;
		if (tipoCausa != null) {
			if (TipoCausaEnumeration.isExhorto(tipoCausa)) {
				show = true;
			}
		}
		return show;
	}

	public boolean showDependenciaContendiente() {
		TipoCausa tipoCausa = getTipoCausa();
		return showDependenciaContendiente(tipoCausa);
	}

	public static boolean showDependenciaContendiente(TipoCausa tipoCausa) {
		if (tipoCausa == null)
			return false;
		boolean show = false;
		if (tipoCausa != null) {
			if (TipoCausaEnumeration.isCompetenciaCorteSuprema(tipoCausa)) {
				show = true;
			}
		}
		return show;
	}

	public boolean showCaratulaOrigen() {
		TipoCausa tipoCausa = getTipoCausa();
		return showCaratulaOrigen(tipoCausa);
	}

	public static boolean showCaratulaOrigen(TipoCausa tipoCausa) {
		if (tipoCausa == null)
			return false;
		boolean show = false;
		if (tipoCausa != null) {
			if (TipoCausaEnumeration.isExhorto(tipoCausa)
					|| TipoCausaEnumeration.isInhibitoria(tipoCausa)
					|| TipoCausaEnumeration.isCompetenciaCorteSuprema(tipoCausa))
			{
				show = true;
			}
		}
		return show;
	}

	public boolean showDelitos() {
		return showDelitos(getTipoCausa());
	}

	public boolean showDelitos(TipoCausa tipoCausa) {
		if (tipoCausa == null)
			return false;
		boolean show = false;
		if (tipoCausa != null && MateriaEnumeration.isAnyPenal(getMateria())) {
			show = true;
			if (TipoCausaEnumeration.isAmparo(tipoCausa)
					|| TipoCausaEnumeration.isHabeasData(tipoCausa)
					|| TipoCausaEnumeration.isExencionPrision(tipoCausa)
					|| TipoCausaEnumeration.isMedidasPrecautorias(tipoCausa)
					|| TipoCausaEnumeration.isExtradicion(tipoCausa)
					|| TipoCausaEnumeration.isElevacionCamara(tipoCausa)
					|| TipoCausaEnumeration.isIncidente(tipoCausa)
					|| TipoCausaEnumeration.isExhorto(tipoCausa)
					|| TipoCausaEnumeration.isHabeasCorpus(tipoCausa)
					|| TipoCausaEnumeration.isInhibitoria(tipoCausa)) {
				show = false;
			}
		}
		return show;
	}

	public boolean showTema() {
		return showObjetoJuicio()
				&& ConfiguracionMateriaManager.instance()
						.isSelectorSubmateria(
								SessionState.instance().getGlobalCamara(),
								getMateria())
				&& ((getTipoCausa() == null) || Strings.isEmpty(getTipoCausa()
						.getCodigoObjetoJuicio()));
	}

	public boolean isDisableTipoInterviniente() {
		return !isMediacion() && showObjetoJuicio()
				&& getObjetoJuicio() == null;
	}

	public boolean showObjetoJuicio() {
		return showObjetoJuicio(getTipoCausa());
	}

	public boolean showObjetoJuicio(TipoCausa tipoCausa) {
		boolean show = true;
		if (MateriaEnumeration.isAnyPenal(getMateria())) {
			show = false;
		} else {
			if (sinObjetoJuicioTipoCausa(tipoCausa)
					|| getObjetoJuicioTipoCausa(tipoCausa) != null) {
				show = false;
			}
		}
		return show;
	}

	public boolean showMostrarIniciales() {
		return isCamaraCorteSuprema();
	}

	public boolean showNoSaleEnCaratula() {
		return showNoSaleEnCaratula(getTipoCausa());
	}

	private boolean showNoSaleEnCaratula(TipoCausa tipoCausa) {
		return !isMediacion() && !isCamaraSeguridadSocial();
	}

	public boolean showIntervinientes() {
		return showIntervinientes(getTipoCausa());
	}

	public boolean showIntervinientes(TipoCausa tipoCausa) {
		boolean show = true;

		if (isPenal()) {
			if (TipoCausaEnumeration.isIncidente(tipoCausa)
					|| TipoCausaEnumeration.isExhorto(tipoCausa)) {
				show = false;
			}
		} else {
			if (TipoCausaEnumeration.isExhorto(tipoCausa)
					&& !CamaraManager.isCamaraActualCivilCABA()
					&& !CamaraManager.isCamaraActualCorteSuprema()) {
				// if (TipoCausaEnumeration.isExhorto(tipoCausa) &&
				// (CamaraManager.isCamaraActualLaboral() ||
				// !CamaraManager.isCamaraActualCivilCABA())) {
				show = false;
			}
		}
		return show;
	}

	public boolean showLetrados() {
		boolean show;
		if (!isPenal()
				&& (!isEditMode() || !isExpedienteIniciado() || isCamaraLaboral())) {
			show = showIntervinientes();
		} else {
			show = false;
		}
		return show;
	}

	public boolean showParametros() {
		// en Corte Suprema se meten OJ de cualquier materia pero no se piden
		// parametros
		if (CamaraManager.isCamaraActualCorteSuprema()) {
			return false;
		}
		return (!isMediacion() && (getParametroList() != null) && (getParametroList()
				.size() > 0));
	}

	public boolean showConexidadDeclaradaSegundaInstancia() {
		return showConexidadDeclaradaSegundaInstancia(getTipoCausa());
	}

	public boolean showConexidadDeclaradaSegundaInstancia(TipoCausa tipoCausa) {
		if (isEditMode() || isIngresoAnteriorSistema() || isMediacion()) {
			return false;
		}
		return isIngresoAJuzgado() && isCamaraSeguridadSocial() && 
				(
				ObjetoJuicioEnumeration.EJECUCION_PREVISIONAL.equals(getObjetoJuicio().getCodigo()) ||
				ObjetoJuicioEnumeration.EJECUCION_DE_HONORARIOS_ASTREINTES.equals(getObjetoJuicio().getCodigo()) ||
				ObjetoJuicioEnumeration.EJECUCION_DE_HONORARIOS_ASTREINTES_SEC_2.equals(getObjetoJuicio().getCodigo())
				);
	}

	public boolean showConexidadDeclarada() {
		return ExpedienteManager.showConexidadDeclarada(camara)
				&& showConexidadDeclarada(getTipoCausa());
	}

	public boolean showVinculadoAExpediente() {
		return !isEditMode() && CamaraManager.isCamaraActualCorteSuprema();
	}

	// ITO (Incompetencia TO)
	public boolean showConexidadDeclarada(TipoCausa tipoCausa) {
		// if (isEditMode() || isIngresoAnteriorSistema()) {
		if (isIngresoAnteriorSistema() || isMediacion()) {
			return false;
		}
		return ExpedienteManager.isUseConexidadDeclarada(tipoCausa);
	}

	//
	public boolean showMediacion() {
		boolean show = false;
		TipoCausa tipoCausa = getTipoCausa();
		if (!MateriaEnumeration.isAnyPenal(getMateria())) {
			if (TipoCausaEnumeration.isProvenienteMediacion(tipoCausa)) {
				show = true;
			}
		}
		return show;
	}

	public boolean showMontoJuicio() {
		boolean show = false;
		if (MateriaEnumeration.isAnyPenal(getMateria())
				|| isCamaraCorteSuprema()) {
			show = false;
		} else if (isIngresoAJuzgado() || isMediacion()) {
			show = true;
		}
		return show;
	}

	public boolean showAseDefFis() {
		boolean show = false;
		if (MateriaEnumeration.isAnyPenal(getMateria())) {
			show = false;
		} else if (MateriaEnumeration.isCivil(getMateria())) {
			show = true;
		}
		return show;
	}

	public boolean showOtrosDatosSS() {
		boolean show = false;
		if (isSeguridadSocial()) {
			show = true;
		}
		return show;
	}
	
	public boolean showManifestacionDe() {
		boolean show = false;
		if (MateriaEnumeration.isAnyPenal(getMateria())) {
			show = false;
		} else if (MateriaEnumeration.isCivil(getMateria())) {
			show = true;
		}
		return show;
	}

	public boolean showTasaJusticia() {
		boolean show = false;
		if (MateriaEnumeration.isAnyPenal(getMateria())) {
			show = false;
		} else {
			// show = true;
			show = false; // de momento hasta determinar donde se usa
		}
		return show;
	}

	public boolean showOtrosDatos() {
		return showAseDefFis() && showManifestacionDe() && showTasaJusticia() || showOtrosDatosSS();
	}

	public boolean showExpedientenInfo() {
		return showExpedientenInfo(getTipoCausa());
	}

	public boolean showExpedientenInfo(TipoCausa tipoCausa) {
		boolean show = true;
		if (tipoCausa != null) {
			if (MateriaEnumeration.isAnyPenal(tipoCausa.getMateria())) {
				show = false;
			}
		}
		return show;
	}

	public boolean showFojas() {
		TipoCausa tipoCausa = getTipoCausa();
		return showFojas(tipoCausa);
	}

	public boolean showFojas(TipoCausa tipoCausa) {
		boolean show = false;
		if ((tipoCausa != null)
				&& TipoCausaEnumeration.isDenuncia(tipoCausa)
				// || TipoCausaEnumeration.isQuerella(tipoCausa)
				|| TipoCausaEnumeration.isAmparo(tipoCausa)
				|| TipoCausaEnumeration.isHabeasData(tipoCausa)
				|| TipoCausaEnumeration.isExencionPrision(tipoCausa)
				|| TipoCausaEnumeration.isElevacionCamara(tipoCausa)
				|| TipoCausaEnumeration.isIncidente(tipoCausa)) {
			show = true;
		}
		return show;
	}

	public boolean showCuerpos() {
		boolean show = false;
		TipoCausa tipoCausa = getTipoCausa();
		if (tipoCausa != null
				&& TipoCausaEnumeration.isElevacionCamara(tipoCausa)
				|| TipoCausaEnumeration.isIncidente(tipoCausa)) {
			show = true;
		}
		return show;
	}

	public boolean showDetenidos() {
		boolean show = false;
		TipoCausa tipoCausa = getTipoCausa();
		if (tipoCausa != null
				&& MateriaEnumeration.isAnyPenal(tipoCausa.getMateria())) {
			if (TipoCausaEnumeration.isDenuncia(tipoCausa)
					|| TipoCausaEnumeration.isQuerella(tipoCausa)
					|| TipoCausaEnumeration.isCambioAsignacion(tipoCausa)
					|| TipoCausaEnumeration.isMedidasPrecautorias(tipoCausa)
					|| TipoCausaEnumeration.isExtradicion(tipoCausa)
					|| TipoCausaEnumeration.isTestimonio(tipoCausa)
					|| TipoCausaEnumeration.isIncompetenciaTO(tipoCausa) // ITO
																			// (Incompetencia
																			// TO)
			) {
				show = true;
			}
		}
		return show;
	}

	public boolean isPedirFojas() {
		return ConfiguracionMateriaManager.instance().isPedirFojas(getCamara(),
				getMateria());
	}

	public boolean showFojasCuerposEtc() {
		TipoCausa tipoCausa = getTipoCausa();
		return showFojasCuerposEtc(tipoCausa);
	}

	public boolean showFojasCuerposEtc(TipoCausa tipoCausa) {
		boolean show = false;
		if (isIngresoAnteriorSistema() || isPedirFojas()) {
			show = true;
		} else {
			if (tipoCausa != null) {
				if (TipoCausaEnumeration.isExhorto(tipoCausa)) {
					show = true;
				} else if (MateriaEnumeration
						.isAnyPenal(tipoCausa.getMateria())) {
					if (TipoCausaEnumeration.isCambioAsignacion(tipoCausa)
							|| TipoCausaEnumeration
									.isMedidasPrecautorias(tipoCausa)
							|| TipoCausaEnumeration.isExtradicion(tipoCausa)
							|| TipoCausaEnumeration
									.isOtrosOrganismos(tipoCausa)
							|| TipoCausaEnumeration.isPrevencion(tipoCausa)
							|| TipoCausaEnumeration.isTestimonio(tipoCausa)
							|| TipoCausaEnumeration
									.isIncompetenciaTO(tipoCausa) // ITO
																	// (Incompetencia
																	// TO)
							|| TipoCausaEnumeration.isRecursoDirecto(tipoCausa)
							|| TipoCausaEnumeration.isNN(tipoCausa)
							|| (TipoCausaEnumeration.isDenuncia(tipoCausa) && isJuzgadoOSecretariaPenalOrdinario())) {
						show = true;
					}
				} else if (tipoCausa.isEspecial()) {
					if (TipoCausaEnumeration.isProvenienteMediacion(tipoCausa)) {
						show = false;
					} else {
						show = true;
					}
				}
			}
		}
		return show;
	}

	// ITO (Incompetencia TO)
	public boolean isCuerposRequired() {
		TipoCausa tipoCausa = getTipoCausa();
		return isCuerposRequired(tipoCausa);
	}

	public boolean isCuerposRequired(TipoCausa tipoCausa) {
		boolean show = false;
		if (tipoCausa != null) {
			if (MateriaEnumeration.isAnyPenal(tipoCausa.getMateria())) {
				if (TipoCausaEnumeration.isIncompetenciaTO(tipoCausa)) {
					show = true;
				}
			}
		}
		return show;
	}

	public boolean showHechos() {
		TipoCausa tipoCausa = getTipoCausa();
		return showHechos(tipoCausa);
	}

	public boolean showHechos(TipoCausa tipoCausa) {
		if (tipoCausa == null)
			return false;
		boolean show = false;
		if (tipoCausa != null
				&& MateriaEnumeration.isAnyPenal(tipoCausa.getMateria())) {
			if (isIngresoATO()) {
				show = true;
			}
		}
		return show;
	}

	public boolean showDefensorOficial() {
		TipoCausa tipoCausa = getTipoCausa();
		return showDefensorOficial(tipoCausa);
	}

	public boolean showDefensorOficial(TipoCausa tipoCausa) {
		if (tipoCausa == null)
			return false;
		boolean show = false;
		if (tipoCausa != null
				&& MateriaEnumeration.isAnyPenal(tipoCausa.getMateria())) {
			if (isIngresoATO()) {
				show = true;
			}
		}
		return show;
	}

	//

	public boolean showDeclaracionHecho() {
		TipoCausa tipoCausa = getTipoCausa();
		return showDeclaracionHecho(tipoCausa);
	}

	public boolean showDeclaracionHecho(TipoCausa tipoCausa) {
		if (tipoCausa == null)
			return false;
		boolean show = false;
		if (tipoCausa != null
				&& MateriaEnumeration.isAnyPenal(tipoCausa.getMateria())) {
			if (TipoCausaEnumeration.isDenuncia(tipoCausa)) {
				show = true;
			}
		}
		return show;
	}

	public boolean showCompetenciasCiviles() {
		return showCompetenciasCiviles(isEditMode(), getTipoCausa(),
				getTipoRadicacion(), getObjetoJuicio());
	}

	public boolean showCompetenciasCiviles(boolean isEditMode,
			TipoCausa tipoCausa, TipoRadicacionEnumeration.Type tipoRadicacion,
			ObjetoJuicio objetoJuicio) {
		boolean show = false;
		if (!isIngresoASala(tipoRadicacion)) {
			if (!isTipoCausaMultiCompetencia(tipoCausa)) {
				if (!MateriaEnumeration.isAnyPenal(getMateria())) {
					if ((objetoJuicio == null)
							|| objetoJuicio.getCompetencia() == null) {
						if (getCompetenciasCiviles().size() > 1) {
							show = true;
						}
					}
				}
			}
		}
		return show;
	}

	public boolean showCompetenciasPenales() {
		return showCompetenciasPenales(isEditMode(), getTipoCausa(),
				getTipoRadicacion());
	}

	public boolean showCompetenciasPenales(boolean isEditMode,
			TipoCausa tipoCausa, TipoRadicacionEnumeration.Type tipoRadicacion) {
		return !isIngresoASala(tipoRadicacion) && !isEditMode
				&& !isTipoCausaMultiCompetencia(tipoCausa)
				&& getCompetenciasPenales().size() > 1;
	}

	private boolean isTipoCausaMultiCompetencia(TipoCausa tipoCausa) {
		boolean ret = false;
		if (tipoCausa != null && isPenalOrdinario()) {
			ret = TipoCausaEnumeration.isHabeasData(tipoCausa)
					|| TipoCausaEnumeration.isAmparo(tipoCausa);
		}
		return ret;
	}

	public boolean showCompetencias() {
		if (isPenal()) {
			return showCompetenciasPenales(isEditMode(), getTipoCausa(),
					getTipoRadicacion());
		} else {
			return showCompetenciasCiviles(isEditMode(), getTipoCausa(),
					getTipoRadicacion(), getObjetoJuicio());
		}
	}

	@Deprecated
	public boolean showEfectos() {
		boolean show = false;
		TipoCausa tipoCausa = getTipoCausa();
		if (tipoCausa != null) {
			if (TipoCausaEnumeration.isExhorto(tipoCausa)) {
				show = true;
			} else if (MateriaEnumeration.isAnyPenal(tipoCausa.getMateria())) {
				if (TipoCausaEnumeration.isCambioAsignacion(tipoCausa)
						|| TipoCausaEnumeration
								.isMedidasPrecautorias(tipoCausa)
						|| TipoCausaEnumeration.isExtradicion(tipoCausa)
						|| TipoCausaEnumeration.isOtrosOrganismos(tipoCausa)
						|| TipoCausaEnumeration.isPrevencion(tipoCausa)
						|| TipoCausaEnumeration.isTestimonio(tipoCausa)) {
					show = true;
				}
			}
		}
		return show;
	}

	public boolean showTipoGiro() {
		return false;
	}

	// Fecha del hecho. La que se tendrá en cuenta para asignar oficina por
	// turno
	public boolean showFechaHecho() {
		TipoCausa tipoCausa = getTipoCausa();
		return showFechaHecho(tipoCausa);
	}

	public boolean showFechaHecho(TipoCausa tipoCausa) {
		if (tipoCausa == null)
			return false;
		boolean ret = !showOrigen()
				&& ConfiguracionMateriaManager.instance().isPedirFechaHecho(
						getCamara(), tipoCausa.getMateria());

		return ret;
	}

	public static boolean isPrevencion(TipoCausa tipoCausa) {
		if (tipoCausa == null)
			return false;
		boolean ret = false;
		if (tipoCausa != null
				&& MateriaEnumeration.isAnyPenal(tipoCausa.getMateria())) {
			ret = TipoCausaEnumeration.isPrevencion(tipoCausa);
		}
		return ret;
	}

	public static boolean isExterna(TipoCausa tipoCausa) {
		if (tipoCausa == null) {
			return false;
		}
		boolean ret = false;
		return ret;
	}

	public boolean isPrevencion() {
		return isPrevencion(getTipoCausa());
	}

	public boolean isExterna() {
		return isExterna(getTipoCausa());
	}

	public boolean showAnySpecialGroup() {
		return true;
	}

	public List<Dependencia> dependenciaOrigenAutocomplete(Object patternObject) {
		dependenciaOrigenEnumeration
				.setTipoGrupoDependencia(getTipoGrupoDependencia());
		if (getTipoGrupoDependencia() == null) {
			dependenciaOrigenEnumeration
					.setEjbQlExtraCondition(getDependenciaCondition());
			dependenciaOrigenEnumeration
					.setTipo(isExterna() ? DependenciaEnumeration.TIPO_ORGANISMO_EXTERNO
							: null);
		}
		return dependenciaOrigenEnumeration.getItems(patternObject.toString());
	}

	public List<Dependencia> dependenciaContendienteAutocomplete(
			Object patternObject) {
		dependenciaContendienteEnumeration
				.setTipoGrupoDependencia(getTipoGrupoDependencia());
		if (getTipoGrupoDependencia() == null) {
			dependenciaContendienteEnumeration
					.setEjbQlExtraCondition(getDependenciaCondition());
			dependenciaContendienteEnumeration
					.setTipo(isExterna() ? DependenciaEnumeration.TIPO_ORGANISMO_EXTERNO
							: null);
		}
		return dependenciaContendienteEnumeration.getItems(patternObject.toString());
	}

	private String getDependenciaCondition() {
		if (isPrevencion()) {
			return "(codigo like '70%' or" + " codigo like '71%' or"
					+ " codigo like '72%' or" + " codigo like '73%' or"
					+ " codigo like '74%' or" + " codigo like '760%' or"
					+ " codigo like '086%' or" + " codigo like '014100%' or"
					+ " codigo like '01509900')";
		} else {
			return null;
		}
	}

	public Expediente getExpedienteConexo() {
		return expedienteConexo;
	}

	public void setExpedienteConexo(Expediente expedienteConexo) {
		this.expedienteConexo = expedienteConexo;
	}

	public IntervinienteEditList getIntervinienteEditList() {
		return intervinienteEditList;
	}

	public void setIntervinienteEditList(
			IntervinienteEditList intervinienteEditList) {
		this.intervinienteEditList = intervinienteEditList;
	}

	public List<Tema> getTemas() {
		if (temas == null && getMateria() != null) {
			temas = temaEnumeration.getItemsByMateria(getMateria());
		}
		return temas;
	}

	public Tema getTema() {
		return tema;
	}

	public void setTema(Tema tema) {
		this.tema = tema;
		ObjetoJuicioEnumeration.instance().setTema(tema);
		if (!isMediacion()) {
			if ((getObjetoJuicio() != null) && (tema != null)
					&& !tema.equals(getObjetoJuicio().getTema())) {
				setObjetoJuicio(null);
				objetoJuicioEditList = new ObjetoJuicioEditList(true);
			}
		}
	}

	public List<TipoCausa> getTiposCausaEspecial() {
		Oficina oficinaActual = OficinaManager.instance().getOficinaActual();
		if ((tiposCausaEspecial == null)) {
			TipoCausaEnumeration.instance().setEspecialFilter(true);

			if (isIngresoAnteriorSistema()) {
				Set<String> abreviaturasTipoInstanciaSet = new HashSet<String>();
				if (OficinaManager.instance().inJuzgado()
						|| OficinaManager.instance().inSalaDeCamara()
						|| OficinaManager.instance().inTribunalOral()) {
					abreviaturasTipoInstanciaSet
							.add(TipoInstanciaEnumeration.TIPO_INSTANCIA_PRIMERA_INSTANCIA_ABREVIATURA);
				}
				if (OficinaManager.instance().inSalaDeCamara()) {
					abreviaturasTipoInstanciaSet
							.add(TipoInstanciaEnumeration.TIPO_INSTANCIA_APELACIONES_ABREVIATURA);
				}
				if (OficinaManager.instance().inTribunalOral()) {
					abreviaturasTipoInstanciaSet
							.add(TipoInstanciaEnumeration.TIPO_INSTANCIA_TRIBUNAL_ORAL_ABREVIATURA);
				}
				if (abreviaturasTipoInstanciaSet.size() > 0) {
					TipoCausaEnumeration.instance()
							.setAbreviaturasTipoInstanciaFilter(
									abreviaturasTipoInstanciaSet);
				}
				tiposCausaEspecial = TipoCausaEnumeration.instance()
						.getByMateriaAllResults(getMateria());
			} else {
				Set<String> abreviaturasTipoInstanciaSet = new HashSet<String>(
						SessionState.instance()
								.getAbreviaturasTipoInstanciaIngresa());
				TipoCausaEnumeration.instance()
						.setAbreviaturasTipoInstanciaFilter(
								abreviaturasTipoInstanciaSet);
/**/
				if(isCamaraCorteSuprema()) {
					Set<Materia> materiaList = new HashSet<Materia>();
					if (getMateria() != null) {
						materiaList.add(getMateria());
					}
					materiaList.add(MateriaEnumeration.instance().getMateriaCorteSuprema());
					tiposCausaEspecial = TipoCausaEnumeration.instance()
					.getByMateriasAllResults(materiaList);
					if ((tiposCausaEspecial == null || tiposCausaEspecial.size() == 0)) {
						tiposCausaEspecial = TipoCausaEnumeration.instance()
								.getByMateriaAllResults(null);
					}
				} else {
					tiposCausaEspecial = TipoCausaEnumeration.instance()
					.getByMateriaAllResults(getMateria());
				}
/*			
				tiposCausaEspecial = TipoCausaEnumeration.instance()
						.getByMateriaAllResults(getMateria());

				if ((tiposCausaEspecial == null || tiposCausaEspecial.size() == 0)
						&& isCamaraCorteSuprema()) {
					tiposCausaEspecial = TipoCausaEnumeration.instance()
							.getByMateriaAllResults(null);
				}
*/
				if ((tiposCausaEspecial != null)
						&& tiposCausaEspecial.size() > 0) {
					boolean inJuzgado = OficinaManager.instance().inJuzgado();
					boolean intTribunalOral = OficinaManager.instance()
							.inTribunalOral();
					String tiposCausa = null;
					if (oficinaActual != null) {
						tiposCausa = getParametroConfiguracionOficina(
								oficinaActual,
								ParametroConfiguracionOficinaEnumeration.Type.tiposCausaIngresa
										.getName(), null);
					}
					if (tiposCausa == null && inJuzgado) {
						tiposCausa = getParametroConfiguracionMateria(
								ParametroConfiguracionMateriaEnumeration.Type.tiposCausaIngresaJuzgado
										.getName(), null);
					}
					if (tiposCausa == null && intTribunalOral) {
						tiposCausa = getParametroConfiguracionMateria(
								ParametroConfiguracionMateriaEnumeration.Type.tiposCausaIngresaTribunalOral
										.getName(), null);
					}
					if (tiposCausa != null) {
						List<String> codigos = Configuration
								.getCommaSeparatedAsList(tiposCausa);
						List<TipoCausa> permitidos = new ArrayList<TipoCausa>();
						for (TipoCausa tipoCausa : tiposCausaEspecial) {
							if (codigos.contains(tipoCausa.getCodigo())) {
								permitidos.add(tipoCausa);
							}
						}
						tiposCausaEspecial = permitidos;
					}
					if (tiposCausa == null) { // solo elimino no permitidos si
												// no son predefinidos por
												// parametro de conf.{
						removeNotAllowedTipoCausa(tiposCausaEspecial);
					}
					removeIncompetencias(tiposCausaEspecial);
				}
			}
		}
		return tiposCausaEspecial;
	}

	private void removeIncompetencias(List<TipoCausa> list) {
		ListIterator<TipoCausa> iterator = list.listIterator();
		while (iterator.hasNext()) {
			TipoCausa tipoCausa = iterator.next();
			if (CamaraManager.isCamaraActualCivilCABA()
					&& (TipoCausaEnumeration
							.isIncompetenciaExternaNoInformatizada(tipoCausa) || TipoCausaEnumeration
							.isIncompetenciaExternaInformatizada(tipoCausa))) {
				iterator.remove();
			}
		}
	}

	private void removeNotAllowedTipoCausa(List<TipoCausa> list) {
		ListIterator<TipoCausa> iterator = list.listIterator();
		while (iterator.hasNext()) {
			TipoCausa tipoCausa = iterator.next();
			if (tipoCausa.getTipoInstancia() != null
					&& !SessionState.instance().hasPermissionForTipoInstancia(
							tipoCausa.getTipoInstancia())) {
				iterator.remove();
			}
		}
	}

	public String getParametroConfiguracionMateria(String parametro,
			String defaultValue) {
		return ConfiguracionMateriaManager.instance().getProperty(getCamara(),
				getMateria(), parametro, defaultValue);
	}

	public String getParametroConfiguracionOficina(Oficina oficina,
			String parametro, String defaultValue) {
		return ConfiguracionOficinaManager.instance().getProperty(oficina,
				parametro, defaultValue);
	}

	public boolean getBooleanParametroConfiguracionMateria(String parametro,
			boolean defaultValue) {
		return ConfiguracionMateriaManager.instance().getBooleanProperty(
				getCamara(), getMateria(), parametro, defaultValue);
	}

	public List<Competencia> getCompetencias() {
		if (competencias == null) {
			if (getCamara() != null) {
				competencias = CompetenciaEnumeration.instance()
						.getAllResults();
			}
		}
		return competencias;
	}

	public List<Competencia> getCompetenciasPenales() {
		if (competenciasPenales == null) {
			competenciasPenales = new ArrayList<Competencia>();
			for (Competencia competencia : getCompetencias()) {
				if (competencia.getGrupo() == null
						|| competencia.getGrupo().equals(
								GrupoCompetenciaEnumeration.Type.penal
										.getValue().toString())) {
					competenciasPenales.add(competencia);
				}
			}
		}
		return competenciasPenales;
	}

	public List<Competencia> getCompetenciasCiviles() {
		if (competenciasCiviles == null) {
			competenciasCiviles = new ArrayList<Competencia>();
			for (Competencia competencia : getCompetencias()) {
				if (competencia.getGrupo() == null
						|| competencia.getGrupo().equals(
								GrupoCompetenciaEnumeration.Type.civil
										.getValue().toString())) {
					competenciasCiviles.add(competencia);
				}
			}
		}
		return competenciasCiviles;
	}

	public TipoCausa getTipoCausaNormal() {
		if ((tipoCausaNormal == null) && !(tipoCausaNormalSearched)) {
			tipoCausaNormal = TipoCausaSearch.findByNaturalKey(
					getEntityManager(), TipoCausaEnumeration.CODIGO_NOR,
					getMateria());
			tipoCausaNormalSearched = true;
		}
		return tipoCausaNormal;
	}

	public TipoCausa getTipoCausa() {
		TipoCausa tipoCausa = expedienteEspecialEdit.getTipoCausa();
		if (tipoCausa == null) {
			return getTipoCausaNormal();
		}
		return tipoCausa;
	}

	public void setTipoCausaNormal(TipoCausa tipoCausaNormal) {
		this.tipoCausaNormal = tipoCausaNormal;
	}

	public String getDescripcionTipoCausa() {
		TipoCausa tipoCausa = getTipoCausa();
		if (tipoCausa != null) {
			return tipoCausa.getDescripcion();
		}
		return TipoCausaEnumeration.DESCRIPCION_NOR;
	}

	public String lookupExpedienteOrigen(String toPage, String returnPage) {
		expedienteSearch.init();
		expedienteSearch.setOcultarNoIniciados(true);
		expedienteSearch.setMostrarTodosExpedientes(true);
		expedienteSearch.updateFilters();
		expedienteHome.initializeLookup(expedienteSearch,
				new LookupEntitySelectionListener<Expediente>(returnPage,
						Expediente.class) {
					public void updateLookup(Expediente entity) {
						setExpedientePrincipal(entity);
						numeroExpedientePrincipal = null;
						anioExpedientePrincipal = null;
					}
				});
		return toPage;
	}

	// public String lookupExpedienteConexo(String toPage, String returnPage) {
	// expedienteSearch.init();
	// expedienteSearch.setOcultarNoIniciados(true);
	// expedienteSearch.setMostrarTodosExpedientes(true);
	// expedienteSearch.updateFilters();
	// ExpedienteList.instance().setEjbql(null);
	// expedienteSearch.setShowClearSearchButton(false);
	// expedienteHome.initializeLookup(expedienteSearch, new
	// LookupEntitySelectionListener<Expediente>(returnPage, Expediente.class) {
	// public void updateLookup(Expediente entity) {
	//
	// }
	// });e
	// return toPage;
	// }

	public List<TipoInterviniente> getTiposIntervinienteWith(TipoInterviniente tipo) {
		List<TipoInterviniente> tipos = getTiposInterviniente();
		if ((tipo != null) && !tipos.contains(tipo)) {
			List<TipoInterviniente> tiposWithCurrent = new ArrayList<TipoInterviniente>(getTiposInterviniente());
			tiposWithCurrent.add(tipo);
			return tiposWithCurrent;
		}
		return tipos;
	}
	
	public List<TipoInterviniente> getTiposInterviniente() {
		if (tiposInterviniente == null) {
			tipoIntervinienteEnumeration.reset();
			if (!CamaraManager.isCamaraActualCorteSuprema() && (isPenal() || isMediacion())) {
				tipoIntervinienteEnumeration.setTipoProceso(null);
				if ((getTipoCausa() != null) && getTipoCausa().getStatus() == 0) {
					tipoIntervinienteEnumeration.setMateria(null);
					tipoIntervinienteEnumeration.setTipoCausa(getTipoCausa());
				} else {
					tipoIntervinienteEnumeration.setMateria(getMateria());
					tipoIntervinienteEnumeration.setTipoCausa(null);
					tipoIntervinienteEnumeration.setIgnoreTipoCausa(true);
				}
				tiposInterviniente = tipoIntervinienteEnumeration.getItems();
			} else {
				TipoProceso tipoProceso = getTipoProceso();
				if (CamaraManager.isCamaraActualCorteSuprema()) {
					tipoProceso = getTipoProcesoCorteSuprema();
				}
				if (tipoProceso == null) {
					tipoProceso = TipoProcesoEnumeration.instance()
							.getDefaultTipoProceso(getMateria());
				}
				tipoIntervinienteEnumeration.setTipoProceso(tipoProceso);
				tipoIntervinienteEnumeration.setMateria(null);
				tipoIntervinienteEnumeration.setTipoCausa(null);
				tipoIntervinienteEnumeration.setIgnoreTipoCausa(true);
				if (CamaraManager.isCamaraActualCorteSuprema()) {
					tiposInterviniente = tipoIntervinienteEnumeration.getItems();
				} else {
					tiposInterviniente = tipoIntervinienteEnumeration.items(TipoIntervinienteEnumeration.ACTOR_ORDEN);
					if (!isObjetoJuicioVoluntario() || isCamaraElectoral()) {
						tiposInterviniente.addAll(tipoIntervinienteEnumeration
								.items(TipoIntervinienteEnumeration.DEMANDADO_ORDEN));
					}
				}
			}
		}
		return tiposInterviniente;
	}

	// private void setDefaultTipoIntervinente() {
	// for (IntervinienteEdit intervinienteEdit :
	// intervinienteEditList.getList()) {
	// if (intervinienteEdit.getTipoInterviniente() == null) {
	// intervinienteEdit.setTipoInterviniente(getTipoIntervinienteActor());
	// }
	// }
	// }

	public void setTiposInterviniente(List<TipoInterviniente> tiposInterviniente) {
		this.tiposInterviniente = tiposInterviniente;
	}

	public TipoGrupoDependencia getTipoGrupoDependencia() {
		if (tipoGrupoDependencia == null) {
			if ((getTipoCausa() != null)
					&& !Strings.isEmpty(getTipoCausa()
							.getGrupoDependenciaOrigen())) {
				tipoGrupoDependencia = TipoGrupoDependenciaSearch.findByCodigo(
						getEntityManager(), getTipoCausa()
								.getGrupoDependenciaOrigen());
			}
		}
		return tipoGrupoDependencia;
	}

	private void setTipoGrupoDependencia(
			TipoGrupoDependencia tipoGrupoDependencia) {
		this.tipoGrupoDependencia = tipoGrupoDependencia;
	}

	public TipoRadicacionEnumeration.Type getTipoRadicacion() {
		if (tipoRadicacion == null) {
			tipoRadicacion = TipoRadicacionEnumeration.Type.juzgado;
			if (getTipoCausa() != null) {
				if (TipoInstanciaEnumeration.isApelaciones(getTipoCausa()
						.getTipoInstancia())) {
					tipoRadicacion = TipoRadicacionEnumeration.Type.sala;
				} else if (TipoInstanciaEnumeration
						.isTribunalOral(getTipoCausa().getTipoInstancia())) {
					tipoRadicacion = TipoRadicacionEnumeration.Type.tribunalOral;
				} else if (TipoCausaEnumeration.isMediacion(getTipoCausa())) {
					tipoRadicacion = TipoRadicacionEnumeration.Type.juzgado;
				} else if (TipoInstanciaEnumeration
						.isCorteSuprema(getTipoCausa().getTipoInstancia())) {
					tipoRadicacion = TipoRadicacionEnumeration.Type.corteSuprema;
				} else {
					tipoRadicacion = TipoRadicacionEnumeration.Type.juzgado;
				}
			}
		}
		if (tipoRadicacion == null) {
			tipoRadicacion = TipoRadicacionEnumeration.Type.juzgado;
		}
		if (isIngresoAnteriorSistema()
				&& TipoRadicacionEnumeration
						.isRadicacionJuzgado(tipoRadicacion)) {
			if (OficinaManager.instance().inTribunalOral()) {
				tipoRadicacion = TipoRadicacionEnumeration.Type.tribunalOral;
			} else if (OficinaManager.instance().inSalaDeCamara()) {
				tipoRadicacion = TipoRadicacionEnumeration.Type.sala;
			}
		}
		return tipoRadicacion;
	}

	public void setTipoRadicacion(TipoRadicacionEnumeration.Type tipoRadicacion) {
		this.tipoRadicacion = tipoRadicacion;
	}

	public String getTipoRadicacionValue() {
		TipoRadicacionEnumeration.Type tipoRadicacion = getTipoRadicacion();
		if (tipoRadicacion != null) {
			return (String) tipoRadicacion.getValue();
		}
		return null;
	}

	//
	public TipoGiroEnumeration.Type getTipoGiro() {
		if (isMediacion()) {
			this.tipoGiro = null;
		} else {
			if (this.tipoGiro == null) {
				this.tipoGiro = ConfiguracionMateriaManager.instance()
						.getTipoGiroAsignacion(getTipoRadicacion(),
								getCamara(), getMateria());
			}
		}
		return this.tipoGiro;
	}

	public void setTipoGiro(TipoGiroEnumeration.Type tipoGiro) {
		this.tipoGiro = tipoGiro;
	}

	public OficinaAsignacionExp getRadicacion() {
		return oficinaAsignacionExpManager.getRadicacion(
				getExpedienteResultado(), getTipoRadicacion());
	}

	public Oficina getOficinaRadicacion() {
		Oficina oficina = oficinaAsignacionExpManager.getOficinaRadicacion(
				getExpedienteResultado(), getTipoRadicacion());
		return oficina;
	}

	/**
	 * Obtiene la radicacion actual de un expediente yendo a la base de datos
	 * @return {@link OficinaAsignacionExp} o <code>null</code> en caso que no exista la radicacion para el tipo correspondiente
	 */
	public OficinaAsignacionExp getRadicacionActual() {
		if (getExpedienteResultado() != null && getTipoRadicacion() != null)
			return OficinaAsignacionExpSearch.findByTipoRadicacion(getEntityManager(), getExpedienteResultado(), (String)getTipoRadicacion().getValue());
		return null;
	}
	
	/**
	 * Obtiene la descripcion de una radicacion segun la nueva asignada y guardada en variable de este objeto
	 * @return descripcion de la radicacion nueva o <code>null</code> caso contrario
	 */
	public String getDescripcionOficinaRadicacion() {
		OficinaAsignacionExp oficinaRadicacion = oficinaAsignacionExpManager.getRadicacion(getExpedienteResultado(), getTipoRadicacion());
		if (oficinaRadicacion != null) {
			return oficinaRadicacion.getDescripcionOficinaRadicacion();
		} else {
			return null;
		}
	}
	
	/**
	 * Obtiene la descripcion de una radicacion segun {@link OficinaAsignacionExp}
	 * @param oficinaAsignacionExp radicacion
	 * @return descripcion o <code>null</code> caso que no posea
	 */
	public String getDescripcionRadicacion(OficinaAsignacionExp oficinaAsignacionExp){
		String ret = null;
		if (oficinaAsignacionExp != null)
			ret = oficinaAsignacionExp.getDescripcionOficinaRadicacion();
		return ret;
	}
	
	public String getDescripcionFiscaliaAsignada() {
		Integer codigoFiscalia = null;
		OficinaAsignacionExp asignacionExp = getRadicacion();

		if (asignacionExp != null) {
			codigoFiscalia = asignacionExp.getFiscalia();
		}
		String ret = null;
		if (codigoFiscalia != null) {
			ret = "Nro. " + codigoFiscalia;
			Fiscalia fiscalia = OficinaAsignacionExpManager
					.getFiscaliaAsignada(asignacionExp);
			if (fiscalia != null && fiscalia.getDescripcion() != null) {
				ret += ", " + fiscalia.getDescripcion();
			}
		}
		return ret;
	}

	public String getDescripcionMediadorAsignado() {
		OficinaAsignacionExp asignacionExp = oficinaAsignacionExpManager
				.getRadicacion(getExpedienteResultado(),
						TipoRadicacionEnumeration.Type.juzgado);

		String ret = null;
		if (asignacionExp != null && asignacionExp.getMediador() != null) {
			ret = asignacionExp.getMediador().getMatriculaYNombre();
		}
		return ret;
	}

	public boolean isVerifyMode() {
		return verifyMode;
	}

	public void setVerifyMode(boolean verifyMode) {
		this.verifyMode = verifyMode;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
		this.objetoJuicioEditable = null;
	}

	public boolean isEnviadoASorteo() {
		return enviadoASorteo;
	}

	public void setEnviadoASorteo(boolean enviadoASorteo) {
		this.enviadoASorteo = enviadoASorteo;
	}

	public RadicacionPreviaEdit getRadicacionPreviaEdit() {
		if (radicacionPrevia == null) {
			radicacionPrevia = new RadicacionPreviaEdit(isMediacion());
		}
		return radicacionPrevia;
	}

	public void setRadicacionPreviaEdit(RadicacionPreviaEdit radicacionPrevia) {
		this.radicacionPrevia = radicacionPrevia;
	}

	public IngresoDiferidoEdit getIngresoDiferidoEdit() {
		if (ingresoDiferido == null) {
			ingresoDiferido = new IngresoDiferidoEdit();
		}
		return ingresoDiferido;
	}

	public void setIngresoDiferidoEdit(IngresoDiferidoEdit ingresoDiferido) {
		this.ingresoDiferido = ingresoDiferido;
	}

	public void onShowIngresoRadicacionPrevia(String returnPage) {
		MediadorSearch.instance().setCompetencia(getCompetencia());
		getRadicacionPreviaEdit().setReturnPage(returnPage);
//		List<Oficina> oficinas = getOficinasDestinoRadicacionPrevia();
//		if ((oficinas != null) && oficinas.size() == 1) {
//			getRadicacionPreviaEdit().setOficinaDestino(oficinas.get(0));
//		} else {
//			getRadicacionPreviaEdit().setOficinaDestino(null);
//		}
	}

	public void onShowIngresoDiferido(String returnPage) {
		getIngresoDiferidoEdit().setReturnPage(returnPage);

		Oficina oficinaActual = OficinaManager.instance().getOficinaActual();
		getEntityManager().refresh(oficinaActual);
		if ((oficinaActual != null)
				&& !OficinaManager.instance().inAlgunaMesaDeEntrada()
				&& isOficinaActualCompatibleIngreso()) {
			getIngresoDiferidoEdit().setShowSelectorDeOficinas(false);
			getIngresoDiferidoEdit().setOficinaDestino(oficinaActual);
		} else {
			getIngresoDiferidoEdit().setShowSelectorDeOficinas(true);
			getIngresoDiferidoEdit().setOficinaDestino(null);
			if (OficinaManager.instance().inJuzgado()) {
				getIngresoDiferidoEdit().setJuzgado(oficinaActual);
			} else if (OficinaManager.instance().inSalaDeCamara()) {
				getIngresoDiferidoEdit().setSala(oficinaActual);
			} else if (OficinaManager.instance().inTribunalOral()) {
				getIngresoDiferidoEdit().setTribunalOral(oficinaActual);
			}
		}
	}

	private boolean isOficinaActualCompatibleIngreso() {
		return (isIngresoAJuzgado() && OficinaManager.instance().inJuzgado())
				|| (isIngresoASala() && OficinaManager.instance()
						.inSalaDeCamara())
				|| (isIngresoATO() && OficinaManager.instance()
						.inTribunalOral());
	}

	public boolean isAsignaPorSorteo() {
		return !CamaraManager.isCamaraActualCorteSuprema();
	}

	public boolean isIngresoDiferido() {
		return !getIngresoDiferidoEdit().isError();
	}

	public String doCancelIngresoDiferido() {
		String page = getIngresoDiferidoEdit().getReturnPage();
		setIngresoDiferidoEdit(null);
		return page;
	}

	public String doSaveIngresoDiferido() {
		return doSaveIngresoDiferido(null);
	}

	public boolean isIngresoDiferidoConOffset() {
		return isIngresoAnteriorSistema()
				|| ConfiguracionMateriaManager.instance()
						.isIngresoDiferidoConOffset(getCamara(), getMateria());
	}

	public String doSaveIngresoDiferido(String page) {
		boolean error = false;

		try {
			String fechaArranqueSistema = getParametroConfiguracionMateria(
					ParametroConfiguracionMateriaEnumeration.Type.fechaArranqueSistema
							.getName(), null);
			getIngresoDiferidoEdit().checkAccept(fechaArranqueSistema);
		} catch (EntityOperationException e) {
			error = true;
		}
		if (!error) {
			Integer numeroExpediente = getIngresoDiferidoEdit()
					.getNumeroExpediente();
			if (isIngresoDiferidoConOffset() && numeroExpediente != null) {
				Integer offset = getOffsetAnterioresAlSistema(getIngresoDiferidoEdit());
				if (offset == null) {
					String errKey = "expedienteMe.ingresoDiferido.error.offsetRequerido";
					errorMsg(errKey);
					error = true;
				} else {
					if (numeroExpediente <= 999999) {
						numeroExpediente += (offset * 1000000);
					}
					getIngresoDiferidoEdit()
							.setNumeroExpedienteAnteriorAlSistema(
									numeroExpediente);
				}
			}
			if (!error) {
				List<Expediente> expedienteList = ExpedienteManager
						.findExpedientes(getEntityManager(), numeroExpediente,
								getIngresoDiferidoEdit().getAnioExpediente());
				if (expedienteList != null && expedienteList.size() > 0) {
					String errKey = "expedienteMe.ingresoDiferido.error.expedienteYaExistente";
					errorMsg(errKey);
					error = true;
				}
			}
		}
		getIngresoDiferidoEdit().setError(error);
		return error ? null : page;
	}

	private Integer getOffsetAnterioresAlSistema(
			IngresoDiferidoEdit ingresoDiferidoEdit) {
		Oficina oficina;
		if (isIngresoAnteriorSistema()
				&& OficinaManager.instance().inAlgunaMesaDeEntrada()) {
			oficina = ingresoDiferidoEdit.getOficinaDestino();
		} else {
			oficina = OficinaManager.instance().getOficinaActual();
		}
		Integer offset = null;
		if ((oficina != null) && (oficina.getOffsetAnteriorSistema() != null)) {
			offset = oficina.getOffsetAnteriorSistema();
			if ((offset < 0) || (offset > 999)) {
				offset = null;
			}
		}
		return offset;
	}

	public List<Oficina> getOficinasDestinoRadicacionPrevia() {
		List<Oficina> oficinas = null;
		if (isVerTodasOficinasDestino()) {
			oficinas = getOficinasDestino(getTipoRadicacion(), null, false);
		} else {
			oficinas = getOficinasSorteo();
		}
		if ((oficinas != null) && oficinas.size() == 1) {
			getRadicacionPreviaEdit().setOficinaDestino(oficinas.get(0));
		} else {
			getRadicacionPreviaEdit().setOficinaDestino(null);
		}
		return oficinas;
	}

	public List<Oficina> getOficinasDestinoIngresoDiferido() {
		boolean verTodas = isVerTodasOficinasDestino()
				|| isIngresoAnteriorSistema();
		if (verTodas) {
			return getOficinasDestino(getTipoRadicacion(), getCompetencia(),
					isIngresoAnteriorSistema());
		} else {
			return getOficinasSorteo();
		}
	}

	public List<Oficina> getOficinasDestino(
			TipoRadicacionEnumeration.Type tipoRadicacion,
			Competencia competencia, boolean conOffset) {

		boolean incluirSecretarias = !ConfiguracionMateriaManager.instance()
				.isSecretariaUnica(SessionState.instance().getGlobalCamara());

		List<Oficina> oficinas = getOficinasPorRadicacion(tipoRadicacion,
				competencia, incluirSecretarias);
		if (conOffset) {
			oficinas = soloOficinasConOffsetAnteriorAlSistema(oficinas);
		}
		return oficinas;
	}

	public List<Oficina> getOficinasPorRadicacion(
			TipoRadicacionEnumeration.Type tipoRadicacion,
			Competencia competencia, boolean incluirSecretarias) {
		OficinaDestinoEnumeration oficinaDestinoEnumeration = (OficinaDestinoEnumeration) Component
				.getInstance(OficinaDestinoEnumeration.class, true);
		String zona = null;
		if (!isIngresoAnteriorSistema()) {
			zona = OficinaManager.instance().getOficinaActual()
					.getZonaOficina();
		}
		Integer tipoOficina = null;
		Integer tipoInstancia = null;

		List<Oficina> oficinas = null;
		switch (tipoRadicacion) {
		case sala:
			tipoOficina = (Integer) CodigoTipoOficinaEnumeration.Type.salaApelacion
					.getValue();
			tipoInstancia = TipoInstanciaEnumeration.TIPO_INSTANCIA_APELACIONES;
			break;
		case tribunalOral:
			tipoOficina = (Integer) CodigoTipoOficinaEnumeration.Type.tribunalOral
					.getValue();
			tipoInstancia = TipoInstanciaEnumeration.TIPO_INSTANCIA_TRIBUNAL_ORAL;
			break;
		case juzgado:
		case juzgadoPlenario:
		case juzgadoMediacion:
			tipoOficina = (Integer) CodigoTipoOficinaEnumeration.Type.juzgado
					.getValue();
			tipoInstancia = TipoInstanciaEnumeration.TIPO_INSTANCIA_PRIMERA_INSTANCIA;
			break;
		}
		oficinas = oficinaDestinoEnumeration.getOficinasByTipo(tipoOficina,
				getMateria(), competencia, zona);
		if (incluirSecretarias) {
			oficinas.addAll(oficinaDestinoEnumeration.getOficinasByTipo(
					(Integer) CodigoTipoOficinaEnumeration.Type.secretaria
							.getValue(), tipoInstancia, getMateria(),
					competencia, zona));
		}
		return oficinas;
	}

	private List<Oficina> soloOficinasConOffsetAnteriorAlSistema(
			List<Oficina> oficinas) {
		if (oficinas != null) {
			List<Oficina> oficinasConOffset = new ArrayList<Oficina>();
			for (Oficina oficina : oficinas) {
				if (oficina.getOffsetAnteriorSistema() != null) {
					oficinasConOffset.add(oficina);
				}
			}
			return oficinasConOffset;
		}
		return null;
	}

	public boolean isRadicacionPrevia() {
		return !getRadicacionPreviaEdit().isError();
	}

	public boolean isRadicacionCorteSuprema() {
		return TipoRadicacionEnumeration
				.isRadicacionCorteSuprema(getTipoRadicacion());
	}

	public boolean isIngresoACorteSuprema() {
		return CamaraManager.isCamaraActualCorteSuprema();
	}

	public String doCancelIngresoRadicacionPrevia() {
		String page = getRadicacionPreviaEdit().getReturnPage();
		setRadicacionPreviaEdit(null);
		return page;
	}

	public String doSaveIngresoRadicacionPrevia(String page) {
		boolean error = false;

		try {
			getRadicacionPreviaEdit().checkAccept(
					objetoJuicioEdit.getObjetoJuicio());
		} catch (EntityOperationException e) {
			error = true;
		}
		getRadicacionPreviaEdit().setError(error);
		return error ? null : page;
	}

	public List<Oficina> getJuzgados() {
		return getOficinasPorRadicacion(TipoRadicacionEnumeration.Type.juzgado,
				getCompetencia(), true);
	}

	public List<Oficina> getSalas() {
		return getOficinasPorRadicacion(TipoRadicacionEnumeration.Type.sala,
				getCompetencia(), true);
	}

	public List<Oficina> getTribunalesOrales() {
		return getOficinasPorRadicacion(
				TipoRadicacionEnumeration.Type.tribunalOral, getCompetencia(),
				true);
	}

	public boolean isShowDenunciaModal() {
		return showDenunciaModal;
	}

	public void setShowDenunciaModal(boolean showDenunciaModal) {
		this.showDenunciaModal = showDenunciaModal;
	}

	public void doShowDenuncia() {
		showDenunciaModal = true;
	}

	public void doHideDenuncia() {
		showDenunciaModal = false;
	}

	public void doSaveDenuncia() {
		try {
			EditExpedienteAction.instance().updateDenunciaExpediente(this);
		} catch (Lex100Exception e) {
			errorMsg("expedienteEspecial.error.updateDenunciaExpediente",
					e.getMessage());
		}
		doHideDenuncia();
	}

	public boolean showTipoRecurso() {
		boolean ret = false;
		if (!isEditMode()) {
			TipoCausa tipoCausa = getTipoCausa();
			if (isIngresoASala()
					|| ((tipoCausa != null) && TipoInstanciaEnumeration
							.isApelaciones(tipoCausa.getTipoInstancia()))
					|| ((tipoCausa != null) && TipoInstanciaEnumeration
							.isCorteSuprema(tipoCausa.getTipoInstancia()))
					|| ((tipoCausa != null) && !Strings.isEmpty(tipoCausa.getCodigoTipoRecurso()))) {
				List<TipoRecurso> tiposRecurso = getAvailableTiposRecurso();
				if (tiposRecurso != null) {
					ret = tiposRecurso.size() > 1;
					if (tiposRecurso.size() == 1) {
						getRecursoEdit().setTipoRecurso(tiposRecurso.get(0));
					}
					if (isRadicacionCorteSuprema()) {
						ret = tiposRecurso.size() > 0;
					}
				}
			}
		}
		return ret;
	}

	public boolean showResolucionApelada() {
		return CamaraManager.isCamaraActualCorteSuprema();
	}
	
	public List<TipoRecurso> getAvailableTiposRecurso() {
		TipoCausa tipoCausa = getTipoCausa();
		ObjetoJuicio objetoJuicio = getObjetoJuicioEdit().getObjetoJuicio();
		Materia materia;
		if(TipoRadicacionEnumeration.Type.corteSuprema == getTipoRadicacion() ) {
			materia = MateriaEnumeration.instance().getMateriaCorteSuprema();
		} else if (tipoCausa != null){
			 materia = tipoCausa.getMateria();
		} else {
			materia = getMateria();
		}
		return tipoRecursoFinderMe.getTiposRecurso(tipoCausa,
				materia, objetoJuicio);
	}

	public RecursoEdit getRecursoEdit() {
		return recursoEdit;
	}

	public void setRecursoEdit(RecursoEdit recursoEdit) {
		this.recursoEdit = recursoEdit;
	}

	public List<TipoSubexpediente> getTiposSubexpediente() {
		CuadernoManager.instance().setNaturalezaSubexpediente(
				(String) NaturalezaSubexpedienteEnumeration.Type.incidente
						.getValue());
		return TipoSubexpedienteEnumeration.instance().getCuadernoItems();
	}

	@Synchronized
	public void doSortear() {
		if (getSorteoParametros() != null) {
			try {
				getSorteoParametros().setBuscaConexidad(false);
				CreateExpedienteAction.instance().sortear(this,
						getSorteoParametros());
			} catch (Lex100Exception e) {
				errorMsg("expedienteMeAdd.error.sorteo", e.getMessage());
			}
		}
	}

	@Synchronized
	public void doConexidadSelection(Conexidad conexidad) {
		if (getSorteoParametros() != null) {
			try {

				CreateExpedienteAction
						.asignaRadicacionPorConexidad(
								getEntityManager(),
								getSorteoParametros().getTipoRadicacion(),
								getSorteoParametros().getExpediente(),
								getSorteoParametros().getRecursoExp(),
								conexidad,
								TipoAsignacionLex100Enumeration.Type.conexidadSeleccionada,
								TipoCambioAsignacionEnumeration.CONEXIDAD_DETECTADA,
								CreateExpedienteAction
										.getDescripcionCambioAsignacion(
												getEntityManager(),
												TipoCambioAsignacionEnumeration.CONEXIDAD_DETECTADA,
												getSorteoParametros()
														.getTipoRadicacion(),
												getSorteoParametros()
														.getExpediente()
														.getMateria()),
								getSorteoParametros().getTipoGiro(),
								CreateExpedienteAction
										.puedeCompensarPorConexidadAutomatica(
												getSorteoParametros()
														.getExpediente(),
												getSorteoParametros()
														.getTipoRadicacion()),
								getSorteoParametros().isDescompensarAnterior(),
								getSorteoParametros().getSorteo(),
								getSorteoParametros().getRubro(), true,
								getSorteoParametros().getOficinaSorteo(),
								getSorteoParametros().getFojasPase(), null, null, null);

				refreshExpedienteResultado();

			} catch (Lex100Exception e) {
				setEnviadoASorteo(false);
				errorMsg("expedienteMeAdd.error.asignacion", e.getMessage());
			}
		}
	}

	public void setSorteoParametros(SorteoParametros sorteoParametros) {
		this.sorteoParametros = sorteoParametros;
	}

	public SorteoParametros getSorteoParametros() {
		return sorteoParametros;
	}

	public boolean isSeleccionaConexidad() {
		return !isExpedienteAsignado()
				&& !isSorteandoExpediente()
				&& (getExpedienteResultado().getObjetoJuicio() != null)
				&& (getExpedienteResultado().getObjetoJuicio()
						.isSeleccionaConexidad());
	}

	public boolean isIngresoAnteriorSistema() {
		return ingresoAnteriorSistema;
	}

	public void setIngresoAnteriorSistema(boolean ingresoAnteriorSistema) {
		this.ingresoAnteriorSistema = ingresoAnteriorSistema;
	}

	public List<Oficina> getOficinasSorteo() { // Las oficinas integrantes del
												// sorteo esten o no de turno.
		if (oficinasSorteo == null) {
			Sorteo sorteo;
			try {
				sorteo = CreateExpedienteAction.instance().buscaSorteo(this);
				if (sorteo != null) {
					oficinasSorteo = new ArrayList<Oficina>();
					for (OficinaSorteo item : sorteo.getOficinaSorteoList()) {
						if (item.getStatus() != -1) {
							if (!item.getOficina().isInhabilitada()) {
								oficinasSorteo.add(item.getOficina());
							}
						}
					}
				}
			} catch (Lex100Exception e) {
			}
		}
		return oficinasSorteo;
	}

	public List<Oficina> getOficinasConexidadDeclarada() { // Las oficinas
															// integrantes del
															// sorteo esten o no
															// de turno. Para
															// mostrar las
															// posibles oficinas
															// de conexidad
															// declarada
		if (oficinasConexidadDeclarada == null) {
			if (!isIngresoAnteriorSistema()
					&& isConexidadDeclaradaLimitadaSorteo()) {
				oficinasConexidadDeclarada = getOficinasSorteo();
			}
		}
		if (oficinasConexidadDeclarada == null) {
			if (isIngresoASala()) {
				return OficinaEnumeration.instance().getSegundaInstanciaItems(
						null);
			} else if (isIngresoACorteSuprema()) {
				return OficinaEnumeration.instance().getSecretariaItems();
			} else {
				return OficinaEnumeration.instance().getPrimeraInstanciaItems(
						null);
			}
		}
		return oficinasConexidadDeclarada;
	}

	public boolean crearIntervinienteNNSiNoHayDemandado() {
		boolean error = false;
		if (findIntervinienteActor() != null) {
			if (findIntervinienteDemandado() == null) {
				TipoInterviniente tipoInterviniente = tipoIntervinienteEnumeration
						.getNNItem();
				if (tipoInterviniente != null) {
					IntervinienteEdit intervinienteEdit = intervinienteEditList
							.getNewInstance();
					intervinienteEdit
							.setApellidos(TipoIntervinienteEnumeration.NN_NOMBRE);
					intervinienteEdit.setNombre(null);
					intervinienteEdit.setTipoInterviniente(tipoInterviniente);
					try {
						intervinienteEditList.onAcceptLine();
					} catch (EntityOperationException e) {
						error = true;
					}
				}
			}
		}
		return !error;
	}

	public boolean crearIntervinienteAFIPSiNoHayActor() {
		boolean error = false;
		if (findIntervinienteActor() == null) {
			TipoInterviniente tipoInterviniente = getTipoIntervinienteActor();
			if (tipoInterviniente != null) {
				IntervinienteEdit intervinienteEdit = intervinienteEditList
						.getNewInstance();
				intervinienteEdit.setPersonaJuridica(true);
				intervinienteEdit.setApellidos(ConfiguracionMateriaManager
						.instance().getRazonSocialAFIP(getCamara(), null));
				intervinienteEdit.setNombre(null);
				intervinienteEdit.setTipoInterviniente(tipoInterviniente);
				try {
					intervinienteEditList.onAcceptLine();
				} catch (EntityOperationException e) {
					error = true;
				}
			}
		}
		return !error;
	}

	public TipoInterviniente getTipoIntervinienteActor() {
		for (TipoInterviniente tipoInterviniente : getTiposInterviniente()) {
			if (tipoInterviniente.isActor()) {
				return tipoInterviniente;
			}
		}
		return null;
	}

	public TipoInterviniente getTipoIntervinienteDemandado() {
		for (TipoInterviniente tipoInterviniente : getTiposInterviniente()) {
			if (tipoInterviniente.isDemandado()) {
				return tipoInterviniente;
			}
		}
		return null;
	}

	public boolean isConexidadDeclaradaLimitadaSorteo() {
		boolean defaultValue = !CamaraManager.isCamaraActualCorteSuprema();
		return getBooleanParametroConfiguracionMateria(
				ParametroConfiguracionMateriaEnumeration.Type.conexidadDenunciadaLimitadaSorteo
						.getName(), defaultValue);
	}

	public boolean isPermitirAltaLetrado() {
		return getBooleanParametroConfiguracionMateria(
				ParametroConfiguracionMateriaEnumeration.Type.permitirAltaLetrado
						.getName(), false);
	}

	/** ATOS COMERCIAL */
	public void updateExpedienteMediacionWeb() {
		Expediente expediente = ExpedienteManager.findExpedienteMediacion(
				getEntityManager(), this.numeroExpedienteMediacion,
				this.anioExpedienteMediacion);
		setExpedienteMediacion(expediente);
		this.expedienteMediacionOficinaRadicacion = getExpedienteMediacionOficinaRadicacion();
	}

	public boolean isInputDomicilio() {
		return !isCamaraCivilNacional()
				|| isUseDomicilioDenunciadoConstituido()
				|| (isCOM() && isTemaQuiebra());
	}

	public boolean isUseDomicilioDenunciadoConstituido() {
		return isCAF() || isSeguridadSocial() || (isCOM() && !isTemaQuiebra());
	}

	/** ATOS COMERCIAL */

	public boolean isUseApellidoMaterno() {
		return isCAF();
	}

	public boolean isUseIniciales() {
		return ConfiguracionMateriaManager.instance()
		.isUsaInicialesEnCaratula(
				SessionState.instance().getGlobalCamara());
	}

	public boolean isUseNumeroIGJ() {
		return isCAF() && !isObjetoJuicioAFIP();
	}

	public boolean isUseNumeroExpedienteAdministrativo() {
		return isSeguridadSocial();
	}
	public boolean isUseLeyAplicable() {
		return isSeguridadSocial();
	}
	public boolean isUseNumeroBeneficio() {
		return isSeguridadSocial();
	}

	public boolean isRequiredNumeroExpedienteAdministrativo() {
		return false;	// isSeguridadSocial() && (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.REAJUSTES_VARIOS.equals(getObjetoJuicio().getCodigo());
	}
	public boolean isRequiredLeyAplicable() {
		return false;	// isSeguridadSocial() && (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.REAJUSTES_VARIOS.equals(getObjetoJuicio().getCodigo());
	}
	public boolean isRequiredNumeroBeneficio() {
		return false;	// isSeguridadSocial() && (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.REAJUSTES_VARIOS.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isUsaIntervinientesFrecuentes() {
		return ConfiguracionMateriaManager.instance()
				.isUsaIntervinientesFrecuentes(
						SessionState.instance().getGlobalCamara());
	}

	public boolean isDocumentoIdentidadRequerido(
			IntervinienteEdit intervinienteEdit) {
		if (intervinienteEdit.isActor() && isCamaraSeguridadSocial() ) {
			return true;
		}
		return ((intervinienteEdit.isActor() && isObjetoJuicioVoluntario()
				&& !isCamaraCivilNacional() && !isCamaraElectoral() && (!isCamaraLaboral() || !isObjetoJuicioAccionAclaratoria())) || (isCAF() && !isObjetoJuicioAFIP()));
	}

	public boolean isObjetoJuicioAFIP() {
		return (getObjetoJuicio() != null)
				&& AfipManager.getObjetosJuicioAfip().contains(
						getObjetoJuicio().getCodigo());
	}

	public boolean isObjetoJuicioEjecucionFiscalMinTrab() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.EJECUCION_FISCAL_MINISTERIO_DE_TRABAJO.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isDomicilioRequerido(IntervinienteEdit intervinienteEdit) {
		return (intervinienteEdit.isActor() && (isCAF() || (isSeguridadSocial() && !isIngresoWeb() && !isObjetoJuicioEjecucionFiscalMinTrab())) && !isObjetoJuicioAFIP())
				|| (isComercial() && intervinienteEdit.isPresuntoFallido() && isJuicioUniversal());
	}

	//No se usa
	public boolean isDomicilioDemandadoRequerido() {
		return isComercial();
	}

	public boolean isSeguridadSocial() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCamaraSeguridadSocial(SessionState.instance()
						.getGlobalCamara());
	}

	public boolean isCAF() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCamaraCAF(SessionState.instance()
						.getGlobalCamara());
	}

	public boolean isPenalOrdinario() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCamaraPenalOrdinario(SessionState.instance()
						.getGlobalCamara());
	}

	public boolean isCCF() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCamaraCCF(SessionState.instance()
						.getGlobalCamara());
	}

	public boolean isCamaraCivilNacional() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCamaraCivilNacional();
	}

	public boolean isCamaraLaboral() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCamaraLaboral(SessionState.instance()
						.getGlobalCamara());
	}

	public boolean isCamaraComercial() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCamaraComercial(SessionState.instance()
						.getGlobalCamara());
	}

	public boolean isCIVILCABA() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCamaraActualCivilCABA();
	}

	public boolean isCamaraCorteSuprema() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCamaraActualCorteSuprema();
	}

	public boolean isObjetoJuicioEditable() {
		if (objetoJuicioEditable == null) {
			objetoJuicioEditable = true;
			if (isEditMode()) {
				objetoJuicioEditable = getIdentity().hasPermission(
						"Expediente", "cambioObjetoJuicio");
				if (objetoJuicioEditable) {
					if (isMediacion()) {
						List<VinculadosExp> vinculados = VinculadosExpManager
								.instance()
								.findByTipo(
										(String) TipoVinculadoEnumeration.Type.mediacion
												.getValue(),
										expedienteHome.getInstance());
						for (VinculadosExp vinculado : vinculados) {
							if ((vinculado.getExpedienteRelacionado() != null)
									&& isProvenienteMediacion(vinculado
											.getExpedienteRelacionado()
											.getTipoCausa())) {
								objetoJuicioEditable = false;
								break;
							}
						}
					}
				}
			}
		}
		return objetoJuicioEditable;
	}

	public boolean showResolucionAdministrativa() {
		return isCamaraSeguridadSocial();
	}

	public boolean showAdjuntaCopia() {
		return isCamaraSeguridadSocial();
	}

	public boolean showMedidasCautelares() {
		return SessionState.instance().getGlobalCamara() != null
				&& (CamaraManager.isCamaraActualMultiMateria() || isCamaraCivilNacional() || isCamaraSeguridadSocial())
				&& !isMediacion();
	}

	public boolean showTratamientoIncidental() {
		return ConfiguracionMateriaManager.instance()
				.isAdmiteTratamientoIncidental(
						SessionState.instance().getGlobalCamara())
				&& !isMediacion() && !isEditMode();
	}

	public boolean showRelajarValidacion() {
		return isCAF();
	}

	public boolean showIngresoUrgente() {
		return isSeguridadSocial();
	}

	public boolean isMediacion() {
		return TipoCausaEnumeration.isMediacion(getTipoCausa());
	}

	public boolean isMediacion(TipoCausa tipoCausa) {
		return TipoCausaEnumeration.isMediacion(tipoCausa);
	}

	public boolean isProvenienteMediacion() {
		return TipoCausaEnumeration.isProvenienteMediacion(getTipoCausa());
	}

	public boolean isProvenienteMediacion(TipoCausa tipoCausa) {
		return TipoCausaEnumeration.isProvenienteMediacion(tipoCausa);
	}

	public boolean showEstadoEsParte() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCamaraCCF(camara) && !isMediacion();
	}

	public boolean isMedidasCautelares() {
		return medidasCautelares;
	}

	public void setMedidasCautelares(boolean medidasCautelares) {
		if (showIngresoUrgente() && (medidasCautelares != this.medidasCautelares)) {
			this.medidasCautelares = medidasCautelares;
			resetIngresoUrgente(getObjetoJuicio());
		}
		this.medidasCautelares = medidasCautelares;
	}

	public Date getFechaComunicacion() {
		return fechaComunicacion;
	}

	public void setFechaComunicacion(Date fechaComunicacion) {
		this.fechaComunicacion = fechaComunicacion;
	}

	public boolean isIgnoreCheckDobleInicio() {
		return ignoreCheckDobleInicio;
	}

	public void setIgnoreCheckDobleInicio(boolean ignoreCheckDobleInicio) {
		this.ignoreCheckDobleInicio = ignoreCheckDobleInicio;
	}

	public String getExpedienteInfoValor() {
		return expedienteInfoValor;
	}

	public void setExpedienteInfoValor(String expedienteInfoValor) {
		if (expedienteInfoValor != null && !expedienteInfoValor.isEmpty()
				&& expedienteInfoValor.trim().length() > 512) {
			expedienteInfoValor = expedienteInfoValor.trim().substring(0, 511);
		}
		this.expedienteInfoValor = expedienteInfoValor;
	}

	public boolean isRelajarValidacion() {
		return relajarValidacion;
	}
	
	public void setRelajarValidacion(boolean relajarValidacion) {
		this.relajarValidacion = relajarValidacion;
	}

	public boolean isIngresoUrgente() {
		return ingresoUrgente;
	}

	public void setIngresoUrgente(boolean ingresoUrgente) {
		this.ingresoUrgente = ingresoUrgente;
	}

	private void resetIngresoUrgente(ObjetoJuicio objetoJuicio) {
		setIngresoUrgente(objetoJuicioEnumeration.isUrgente(objetoJuicio) || isMedidasCautelares());
	}

	public Date getFechaIngresoExpediente() {
		return fechaIngresoExpediente;
	}

	public void setFechaIngresoExpediente(Date fechaCreacionExpediente) {
		this.fechaIngresoExpediente = fechaCreacionExpediente;
	}

	public String getParametrosChanged() {
		return parametrosChanged;
	}

	public void setParametrosChanged(String parametrosChanged) {
		this.parametrosChanged = parametrosChanged;
	}

	public boolean isEstadoEsParte() {
		return estadoEsParte;
	}

	public void setEstadoEsParte(boolean estadoEsParte) {
		this.estadoEsParte = estadoEsParte;
	}

	public boolean showDerechosHumanos() {
		return ConfiguracionMateriaManager.instance().isAdmiteDerechosHumanos(
				SessionState.instance().getGlobalCamara());
	}
	
	public boolean showCorrupcion() {
		return ConfiguracionMateriaManager.instance().isAdmiteCorrupcion(
				SessionState.instance().getGlobalCamara());
	}

	public boolean isDerechosHumanos() {
		return derechosHumanos;
	}

	public void setDerechosHumanos(boolean derechosHumanos) {
		this.derechosHumanos = derechosHumanos;
	}

	public Integer getNumeroExpedienteMediacion() {
		return numeroExpedienteMediacion;
	}

	public void setNumeroExpedienteMediacion(Integer numeroExpedienteMediacion) {
		this.numeroExpedienteMediacion = numeroExpedienteMediacion;
	}

	public Integer getAnioExpedienteMediacion() {
		return anioExpedienteMediacion;
	}

	public void setAnioExpedienteMediacion(Integer anioExpedienteMediacion) {
		this.anioExpedienteMediacion = anioExpedienteMediacion;
	}

	public Expediente getExpedienteMediacion() {
		return expedienteMediacion;
	}

	public void setExpedienteMediacion(Expediente expedienteMediacion) {
		this.expedienteMediacion = expedienteMediacion;
		this.expedienteMediacionOficinaRadicacion = null;
	}

	public Oficina getExpedienteMediacionOficinaRadicacion() {
		if (expedienteMediacionOficinaRadicacion == null
				&& expedienteMediacion != null) {
			OficinaAsignacionExp oae = oficinaAsignacionExpManager
					.getRadicacion(expedienteMediacion,
							TipoRadicacionEnumeration.Type.juzgado);
			if (oae != null) {
				expedienteMediacionOficinaRadicacion = oae
						.getOficinaConSecretaria();
			}
		}
		return expedienteMediacionOficinaRadicacion;
	}

	public void setExpedienteMediacionOficinaRadicacion(
			Oficina expedienteMediacionOficinaRadicacion) {
		this.expedienteMediacionOficinaRadicacion = expedienteMediacionOficinaRadicacion;
	}

	public void selectExpedienteConexo(Expediente expediente) {
		setExpedienteConexo(expediente);
	}

	public void unselectExpedienteConexo(Expediente expediente) {
		setExpedienteConexo(null);
	}

	public boolean isVerTodasOficinasDestino() {
		return verTodasOficinasDestino;
	}

	public void setVerTodasOficinasDestino(boolean verTodasOficinasDestino) {
		this.verTodasOficinasDestino = verTodasOficinasDestino;
	}

	public boolean showVerTodasOficinasDestino() {
		// @TODO revisar si hay mas condiciones restrictivas
		return !CamaraManager.isCamaraActualCorteSuprema();
	}

	private List<SelectItem> tipoLetradoSelectItems;
	
	/** OVD */
	private String caratulaOVD;

	public void setCaratulaOVD(String caratulaOVD) {
		this.caratulaOVD = caratulaOVD;
	}
	/** OVD */
	public List<SelectItem> getTipoLetradoSelectItems() {
		if (tipoLetradoSelectItems == null) {
			tipoLetradoSelectItems = new ArrayList<SelectItem>();
			tipoLetradoSelectItems.add(new SelectItem(
					LetradoEditList.TIPO_LETRADO_PATROCINANTE, "P"));
			tipoLetradoSelectItems.add(new SelectItem(
					LetradoEditList.TIPO_LETRADO_APODERADO, "A"));
			tipoLetradoSelectItems.add(new SelectItem(
					LetradoEditList.TIPO_LETRADO_DERECHO_PROPIO, "D"));
			if (isOficinaElectoral()) {
				tipoLetradoSelectItems
						.add(new SelectItem(
								LetradoEditList.TIPO_LETRADO_APODERADO_PARTIDO_POLITICO,
								"E"));
			}

			tipoLetradoSelectItems.add(new SelectItem(
					LetradoEditList.TIPO_LETRADO_GESTOR_FISCAL, "G"));
			if (!CamaraManager.camaraTieneDefensorias()) {
				tipoLetradoSelectItems.add(new SelectItem(
						LetradoEditList.TIPO_LETRADO_DEFENSOR_OFICIAL, "O"));
			}
			tipoLetradoSelectItems.add(new SelectItem(
					LetradoEditList.TIPO_LETRADO_ADHOC, "H"));
			
			if (CamaraManager.isCamaraActualCorteSuprema()) {
				tipoLetradoSelectItems.add(new SelectItem(LetradoEditList.TIPO_LETRADO_VALIDADOS_AJENA_JURISDICCION, "V"));
			}
		}
		return tipoLetradoSelectItems;
	}

	public String getTipoLetradoLabel(String value) {
		for (SelectItem item : getTipoLetradoSelectItems()) {
			if (item.getValue() != null && item.getValue().equals(value)) {
				return item.getLabel();
			}
		}
		return value;
	}

	private boolean isOficinaElectoral() {
		return OficinaManager.instance().isElectoral(
				oficinaManager.instance().getOficinaActual());
	}

	private boolean isOficinaCorteSuprema() {
		return OficinaManager.instance().isCorteSuprema(
				oficinaManager.instance().getOficinaActual());
	}

	public String getTipoLetradoToolTip() {
		Camara camara = SessionState.instance().getGlobalCamara();
		String ret = null;
		if (isOficinaCorteSuprema()) {
			ret = getMessages().get(
					"abogado.tipoLetrado.tipo.tooltip.corte");
		}
		if (isOficinaElectoral()) {
			ret = getMessages().get(
					"abogado.tipoLetrado.tipo.tooltip.electoral");
		}
		if (ret == null && camara != null
				&& CamaraManager.isCamaraActualCivilCABA(camara)) {
			ret = getMessages().get("abogado.tipoLetrado.tipo.tooltip.CIV");
		}
		if (ret == null) {
			ret = getMessages().get("abogado.tipoLetrado.tipo.tooltip");
		}
		return ret;
	}

	public void doSaveLineMultipleObjetojuicio() {
		objetoJuicioEditList.doSaveLine();
		int cont = 0;
		for (int i = 0; i < objetoJuicioEditList.getList().size(); i++) {
			ObjetoJuicioEdit objetoJuicioEdit = objetoJuicioEditList.getList()
					.get(i);
			if (!objetoJuicioEdit.isEmpty()) {
				cont++;
			}
		}
		if (cont > 0) {
			setObjetoJuicio(objetoJuicioEditList.getList().get(0)
					.getObjetoJuicio());
		}
	}

	public void onDeleteLineMultipleObjetojuicio(
			ObjetoJuicioEdit objetoJuicioEdit) {
		objetoJuicioEditList.onDeleteLine(objetoJuicioEdit);
		if (objetoJuicioEdit.getCodigo().equals(getObjetoJuicio().getCodigo())) {
			setObjetoJuicio(objetoJuicioEditList.getList().get(0)
					.getObjetoJuicio());
		}
	}

	public void lookupObjetoMultipleJuicio(String returnPage,
			final ObjetoJuicioEdit objetoJuicioEdit) {
		initFiltroObjetosJuicio(getTipoCausa());
		expedienteHome.initializeLookup(objetoJuicioSearch,
				new LookupEntitySelectionListener<ObjetoJuicio>(returnPage,
						ObjetoJuicio.class) {
					public void updateLookup(ObjetoJuicio entity) {
						objetoJuicioEdit.setObjetoJuicio(entity);
						if (entity != null) {
							setTema(entity.getTema());
						}
					}
				});
	}

	public String lookupObjetoMultipleJuicio(String page, String returnPage,
			ObjetoJuicioEdit objetoJuicioEdit) {
		lookupObjetoMultipleJuicio(returnPage, objetoJuicioEdit);
		return page;
	}

	public boolean isShowTemaRadioButtonSelector() {
		if (showTemaRadioButtonSelector == null) {
			showTemaRadioButtonSelector = ConfiguracionMateriaManager
					.instance().isUseRadioButtonSubmateriaSelector(
							CamaraManager.getCamaraActual());
		}
		return showTemaRadioButtonSelector;
	}

	public boolean isReciboSorteoSala(Expediente instance) {
		boolean isVisible = false;
		if (instance.getCamara() != null) {
			isVisible = ConfiguracionMateriaManager.instance()
					.isVisibleReciboSorteoSala(instance.getCamara());
		}
		return isVisible;
	}

	public boolean isConfirmacionIngresoSimplificada() {
		if (confirmacionIngresoSimplificada == null) {
			confirmacionIngresoSimplificada = ConfiguracionOficinaManager
					.instance().isConfirmacionIngresoSimplificada(
							SessionState.instance().getGlobalOficina());
		}
		return confirmacionIngresoSimplificada;
	}

	public Expediente getCurrentEditExpediente() {
		return currentEditExpediente;
	}

	public void setCurrentEditExpediente(Expediente currentEditExpediente) {
		this.currentEditExpediente = currentEditExpediente;
	}

	private boolean isExpedienteIniciado() {
		boolean ret = false;
		Expediente expediente = getCurrentEditExpediente();
		if (expediente != null) {
			ret = ExpedienteManager.instance().isIniciado(expediente);
		}
		return ret;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		if (comentarios != null && !comentarios.isEmpty()
				&& comentarios.trim().length() > 200) {
			comentarios = comentarios.trim().substring(0, 200);
		}
		this.comentarios = comentarios;
	}

	public boolean isShowWarningModalPanel() {
		return showWarningModalPanel;
	}

	public void setShowWarningModalPanel(boolean showWarningModalPanel) {
		this.showWarningModalPanel = showWarningModalPanel;
	}

	public void hideWarningModalPanel() {
		setShowWarningModalPanel(false);
	}

	public String getWarningModalPage() {
		return warningModalPage;
	}

	public void setWarningModalPage(String warningModalPage) {
		this.warningModalPage = warningModalPage;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public String getWarning() {
		return this.warning;
	}

	public boolean isOmitirBusquedaConexidad() {
		return omitirBusquedaConexidad;
	}

	public void setOmitirBusquedaConexidad(boolean omitirBusquedaConexidad) {
		this.omitirBusquedaConexidad = omitirBusquedaConexidad;
	}

	/** ATOS COMERCIAL */
	public boolean isCOM() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCamaraCOM(SessionState.instance()
						.getGlobalCamara());
	}

	public Boolean isComercial() {
		return MateriaEnumeration.isComercial(getMateria());
	}

	private boolean isTemaQuiebra() {
		return getObjetoJuicio() != null
				&& TemaEnumeration.isQuiebra(getObjetoJuicio().getTema());
	}

	private boolean isObjetoJuicioPropiasQuiebras() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.PROPIAS_QUIEBRAS_COMERCIAL
				.equals(getObjetoJuicio().getCodigo());
	}

	private boolean isObjetoJucioBeneficioLitigarSinGastos(){
		return ObjetoJuicioEnumeration.BENEFICIO_DE_LITIGAR_SIN_GASTOS_COMERCIAL.equals(getObjetoJuicio().getCodigo());
	}

	private boolean isObjetoJuicioConcursosPreventivos() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.CONCURSOS_PREVENTIVOS_COMERCIAL
				.equals(getObjetoJuicio().getCodigo());
	}

	private boolean isObjetoJuicioIncidente() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.INCIDENTE_COMERCIAL
				.equals(getObjetoJuicio().getCodigo());
	}

	private boolean isObjetoJuicioIncidenteCalificacionConsulta() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.INC_CALIFICACION_CONDUCTA_COMERCIAL
				.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isObjetoJuicioQuiebras() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.QUIEBRAS_COMERCIAL
				.equals(getObjetoJuicio().getCodigo());
	}

	private boolean isObjetoJuicioAcuerdosPreConcursales() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.ACUERDOS_PRE_CONCURSALES_COMERCIAL
				.equals(getObjetoJuicio().getCodigo());
	}

	private boolean isObjetoJuicioAcuerdoPreventivoExtrajudicial() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.ACUERDO_PREVENTIVO_EXTRAJUDICIAL_COMERCIAL
				.equals(getObjetoJuicio().getCodigo());
	}

	private boolean isObjetoJuicioPedidoQuiebraArt66() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.PEDIDO_QUIEBRA_ART_66_BIS_COMERCIAL
				.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isObjetoJuicioPedidosQuiebra() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.PEDIDOS_QUIEBRA_COMERCIAL
				.equals(getObjetoJuicio().getCodigo());
	}

	private boolean isObjetoJuicioPropiaQuiebraArt66() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.PROPIA_QUIEBRA_ART_66_BIS_COMERCIAL
				.equals(getObjetoJuicio().getCodigo());
	}

	private boolean isObjetoJuicioAccionAclaratoria() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.ACCION_DECLARATORIA
				.equals(getObjetoJuicio().getCodigo());
	}

	private boolean isObjetoJuicioADeterminar(ObjetoJuicio objetoJuicio) {
		return (objetoJuicio != null) && ObjetoJuicioEnumeration.A_DETERMINAR
				.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isObjetoJuicioADeterminar() {
		return isObjetoJuicioADeterminar(getObjetoJuicio());
	}

	private boolean isJuicioUniversal() {
		if (isObjetoJuicioIncidente()
				|| isObjetoJuicioIncidenteCalificacionConsulta()
				|| isObjetoJuicioQuiebras()
				|| isObjetoJuicioAcuerdosPreConcursales()
				|| isObjetoJuicioPropiasQuiebras()
				|| isObjetoJuicioAcuerdoPreventivoExtrajudicial()
				|| isObjetoJuicioPedidoQuiebraArt66()
				|| isObjetoJuicioPedidosQuiebra()
				|| isObjetoJuicioConcursosPreventivos()
				|| isObjetoJuicioPropiaQuiebraArt66()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isOrganismoExternoComercial() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.ORGANISMOS_EXTERNOS_COMERCIAL
				.equals(getObjetoJuicio().getCodigo());
	}

	private boolean isMedidasPrecautoriasComercial() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.MEDIDA_PRECAUTORIA_COMERCIAL
				.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isComercialMediacion() {
		return CamaraManager.isCamaraActualCOM() && isMediacion();
	}

	private boolean isAmparo() {
		return ObjetoJuicioEnumeration.AMPARO.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isShowOmitirBusquedaConexidad() {
		return isEditMode() && !isCOM();
	}

	public void setOficinaActual(Oficina oficinaActual) {
		this.oficinaActual = oficinaActual;
	}
	
	public Oficina getOficinaActual() {
		return oficinaActual;
	}

	public void setGlobalCamara(Camara globalCamara) {
		this.globalCamara = globalCamara;
	}
	
	public Camara getGlobalCamara() {
		return globalCamara;
	}
	
	public void setInMesaDeEntrada(Boolean inMesaDeEntrada) {
		this.inMesaDeEntrada = inMesaDeEntrada;
	}
	
	public Boolean getInMesaDeEntrada() {
		return inMesaDeEntrada;
	}
	/** ATOS COMERCIAL */

	public boolean isObjetoJuicioExtensiónResponsabilidad() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.EXTENSION_RESPONSABILIDAD
				.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isObjetoJuicioEjecucionHonorarios() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.EJECUCION_HONORARIOS
				.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isObjetoJuicioEjecucionSentencia() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.EJECUCION_SENTENCIA
				.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isObjetoJuicioRevisionCosaJuzgada() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.REVISION_COSA_JUZGADA
				.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isObjetoJuicioRetiroInvalidezArt49() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.RETIRO_INVALIDEZ_ART_49
				.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isIniciarEnIngresoTurno() {
		return iniciarEnIngresoTurno;
	}

	public void setIniciarEnIngresoTurno(boolean iniciarEnIngresoTurno) {
		this.iniciarEnIngresoTurno = iniciarEnIngresoTurno;
	}

	public Integer getNumeroPoder() {
		return numeroPoder;
	}

	public void setNumeroPoder(Integer numeroPoder) {
		this.numeroPoder = numeroPoder;
	}

	public Integer getAnioPoder() {
		return anioPoder;
	}

	public void setAnioPoder(Integer anioPoder) {
		this.anioPoder = anioPoder;
	}

	public Poder getPoder() {
		return poder;
	}

	public void setPoder(Poder poder) {
		this.poder = poder;
	}

	public boolean updatePoder() {
		Poder poder = PoderManager.findPoder(getEntityManager(),
				this.numeroPoder, this.anioPoder);
		setPoder(poder);
		return checkPoder(getPoder());
	}

	public void resetPoder() {
		setNumeroPoder(null);
		setAnioPoder(null);
		setPoder(null);
	}

	private void copiaDatosPoder() {
		if (getPoder() != null) {
			copyPoderToEdit(getPoder(), this, true);
			// for(IntervinienteEdit intervinienteEdit:
			// getIntervinienteEditList().getList()){
			// if(!intervinienteEdit.isEmpty()){
			// intervinienteEdit.setNew(true);
			// }
			// }
			this.getIntervinienteEditList().setInAddExpedienteMode(true);
			doSave1();
		}
	}

	public void copyPoderToEdit(Poder poder, ExpedienteMeAdd expedienteEdit,
			boolean b) {
		TipoCausa tipoCausa = getTipoCausa();
		expedienteEdit.init();
		setTipoCausaEspecial(tipoCausa);
		ObjetoJuicio objetoJuicio = poder.getObjetoJuicio();
		if (objetoJuicio != null) {
			expedienteEdit.setObjetoJuicio(objetoJuicio);
			ObjetoJuicioEnumeration.instance().setSelected(objetoJuicio);
		}
		expedienteEdit.setTema(null);

		TipoInterviniente defaultActor = getDefaultInterviniente(
				getTiposInterviniente(), true, false, "ACTOR", "ACTOR/ES");
		TipoInterviniente defaultDemandado = getDefaultInterviniente(
				getTiposInterviniente(), false, true, "DEMANDADO",
				"DEMANDADO/S");
		TipoInterviniente defaultTestigo = getDefaultInterviniente(
				tipoIntervinienteEnumeration.getItems(),
				false, false, "TESTIGO",
				"TESTIGO/S");

		IntervinienteEditList intervinienteEditList = new IntervinienteEditList(
				true, expedienteEdit);

		List<IntervinienteEdit> listaIntervinientes = new ArrayList<IntervinienteEdit>();
		IntervinienteEdit intervinienteEdit = new IntervinienteEdit();
		intervinienteEdit.copyFielsFromPoder(poder);
		intervinienteEdit.setTipoInterviniente(defaultActor);
		intervinienteEdit.setNew(true);
		listaIntervinientes.add(intervinienteEdit);
		IntervinienteEdit actorEdit = intervinienteEdit;

		for (DemandadoPoder demandado : poder.getDemandadoPoderList()) {
			intervinienteEdit = new IntervinienteEdit();
			intervinienteEdit.copyFielsFromDemandadoPoder(demandado);
			intervinienteEdit.setTipoInterviniente(defaultDemandado);
			intervinienteEdit.setNew(true);
			listaIntervinientes.add(intervinienteEdit);
		}
		for (TestigoPoder testigo : poder.getTestigoPoderList()) {
			intervinienteEdit = new IntervinienteEdit();
			intervinienteEdit.copyFielsFromTestigoPoder(testigo);
			
			TipoTestigoPoderEnumeration.Type tipo = (TipoTestigoPoderEnumeration.Type)TipoTestigoPoderEnumeration.instance().getType(testigo.getTipo());
			TipoInterviniente tipoIntervinienteTestigo = null;
			if ((tipo != null) && !Strings.isEmpty(tipo.getNombreTipoInterviniente())){
				tipoIntervinienteTestigo = getDefaultInterviniente(
						tipoIntervinienteEnumeration.getItems(),
						false, false, tipo.getNombreTipoInterviniente(), null);
			}
			if (tipoIntervinienteTestigo == null) {
				tipoIntervinienteTestigo = defaultTestigo;
			}
			intervinienteEdit.setTipoInterviniente(tipoIntervinienteTestigo);
			intervinienteEdit.setNew(true);
			listaIntervinientes.add(intervinienteEdit);
		}
		intervinienteEditList.setList(listaIntervinientes);
		expedienteEdit.setIntervinienteEditList(intervinienteEditList);
		intervinienteEditList.checkAutoAdd();
		intervinienteEditList.setInAddExpedienteMode(true);
		
		intervinienteEditList.onEditLine(actorEdit);

		LetradoEditList letradoEditList = new LetradoEditList(true, true);
		letradoEditList.setLetradosDelDemandado(false);
		List<LetradoEdit> listaLetrados = new ArrayList<LetradoEdit>();
		LetradoEdit letradoEdit;
		for (AbogadoPoder letradoPoder : poder.getAbogadoPoderList()) {
			letradoEdit = new LetradoEdit();
			VFLetrado vfletrado = VFLetradoEnumeration.instance()
					.findVFLetradoCapitalByTomoFolio(letradoPoder.getTomoAbogado(),
							letradoPoder.getFolioAbogado());
			letradoEdit.setVfletrado(vfletrado);
			letradoEdit.setTipoLetrado("A");
			letradoEdit.setCuil(letradoPoder.getCuilAbogado());
			letradoEdit.setEmail(letradoPoder.getEmailAbogado());
			letradoEdit.setNew(true);
			letradoEdit.setPoder(poder);
			listaLetrados.add(letradoEdit);
		}

		letradoEditList.setList(listaLetrados);
		expedienteEdit.setLetradoEditList(letradoEditList);
		letradoEditList.checkAutoAdd();
	}

	public void addPoderToEdit(Poder poder, ExpedienteMeAdd expedienteEdit) {
		TipoInterviniente defaultActor = getDefaultInterviniente(
				getTiposInterviniente(), true, false, "ACTOR", "ACTOR/ES");
		TipoInterviniente defaultDemandado = getDefaultInterviniente(
				getTiposInterviniente(), false, true, "DEMANDADO",
				"DEMANDADO/S");

		intervinienteEditList.cancelEditing();
		intervinienteEditList.deleteEmptyLines();
		List<IntervinienteEdit> listaIntervinientes = intervinienteEditList
				.getList();

		IntervinienteEdit intervinienteEdit = intervinienteEditList
				.findIntervinienteEdit(poder);
		if (intervinienteEdit == null) {
			intervinienteEdit = new IntervinienteEdit();
			intervinienteEdit.copyFielsFromPoder(poder);
			intervinienteEdit.setTipoInterviniente(defaultActor);
			intervinienteEdit.setNew(true);
			listaIntervinientes.add(intervinienteEdit);
		} else {
			intervinienteEdit.copyFielsFromPoder(poder);
		}
		for (DemandadoPoder demandado : poder.getDemandadoPoderList()) {
			intervinienteEdit = intervinienteEditList
					.findIntervinienteEdit(demandado);
			if (intervinienteEdit == null) {
				intervinienteEdit = new IntervinienteEdit();
				intervinienteEdit.copyFielsFromDemandadoPoder(demandado);
				intervinienteEdit.setTipoInterviniente(defaultDemandado);
				intervinienteEdit.setNew(true);
				listaIntervinientes.add(intervinienteEdit);
			} else {
				intervinienteEdit.copyFielsFromDemandadoPoder(demandado);
			}
		}
		intervinienteEditList.addNewInstanceToList();
		intervinienteEditList.cancelEditing();
		intervinienteEditList.checkAutoAdd();

		letradoEditList.cancelEditing();
		letradoEditList.deleteEmptyLines();
		letradoEditList.setLetradosDelDemandado(false);
		List<LetradoEdit> listaLetrados = letradoEditList.getList();
		LetradoEdit letradoEdit;
		for (AbogadoPoder letradoPoder : poder.getAbogadoPoderList()) {
			if (letradoEditList.findLetradoEdit(letradoPoder) == null) {
				letradoEdit = new LetradoEdit();
				VFLetrado vfletrado = VFLetradoEnumeration.instance()
						.findVFLetradoCapitalByTomoFolio(
								letradoPoder.getTomoAbogado(),
								letradoPoder.getFolioAbogado());
				letradoEdit.setVfletrado(vfletrado);
				letradoEdit.setTipoLetrado("A");
				letradoEdit.setCuil(letradoPoder.getCuilAbogado());
				letradoEdit.setEmail(letradoPoder.getEmailAbogado());
				letradoEdit.setNew(true);
				letradoEdit.setPoder(poder);
				listaLetrados.add(letradoEdit);
			}
		}
		letradoEditList.addNewInstanceToList();
		letradoEditList.cancelEditing();
		letradoEditList.checkAutoAdd();
	}

	public String lookupPoder(String toPage, String returnPage) {
		lookupPoder(returnPage);
		return toPage;
	}

	public void lookupPoder(String returnPage) {
		PoderHome.instance().initializeLookup(
				PoderSearch.instance(),
				new LookupEntitySelectionListener<Poder>(returnPage,
						Poder.class) {
					public void updateLookup(Poder entity) {
						ExpedienteMeAdd.instance().setPoder(entity);
						ExpedienteMeAdd.instance().setNumeroPoder(
								entity.getNumero());
						ExpedienteMeAdd.instance().setAnioPoder(
								entity.getAnio());
						ExpedienteMeAdd.instance().checkPoder(entity);
					}
				});
	}

	public void resetSeclo() {
		setNumeroSeclo(null);
		setAnioSeclo(null);
	}

	public Integer getNumeroSeclo() {
		return numeroSeclo;
	}

	public void setNumeroSeclo(Integer numeroSeclo) {
		this.numeroSeclo = numeroSeclo;
	}

	public Integer getAnioSeclo() {
		return anioSeclo;
	}

	public void setAnioSeclo(Integer anioSeclo) {
		this.anioSeclo = anioSeclo;
	}

	public boolean isVisibleAddPoderDialog() {
		return visibleAddPoderDialog;
	}

	public String onAddPoder() {
		resetPoder();
		setVisibleAddPoderDialog(true);
		return "";
	}

	public void setVisibleAddPoderDialog(boolean visibleAddPoderDialog) {
		this.visibleAddPoderDialog = visibleAddPoderDialog;
	}

	public boolean isNumeroExpedienteDividido() {
		return ConfiguracionMateriaManager.instance()
				.isNumeroExpedienteDividido(CamaraManager.getCamaraActual());
	}

	public boolean puedeCambiarObjetoJuicio() {
		return getIdentity().hasPermission("Expediente", "cambioObjetoJuicio")
				&& (OficinaManager.instance().inAlgunaMesaDeEntrada() || !ConfiguracionMateriaManager
						.instance().isSoloMesaCambiaObjetoJuicio(getCamara()));
	}

	public void initFiltroMateriaObjetoJuicioForEditing(Expediente expediente) {
		Materia materia = getMateria();
		if (CamaraManager.isCamaraActualCorteSuprema()) {
			ObjetoJuicio objetoJuicio = expediente.getObjetoJuicio();
			if (objetoJuicio != null) {
				materia = objetoJuicio.getMateria();
			}
		}
		setFiltroMateriaObjetoJuicio(materia);
	}

	public boolean showConexidad() {
		return ConexidadManager.isBuscaConexidad(getTipoRadicacion(), CamaraManager.getCamaraActual(), null, true) && 
		(getExpedienteResultado() != null) && (getExpedienteResultado().getId() != null) && !isConfirmacionIngresoSimplificada() && 
		ExpedienteManager.isPrincipal(getExpedienteResultado()) &&
		((getExpedienteResultado().getTipoCausa() == null) || getExpedienteResultado().getTipoCausa().isBuscaConexidad());
	}

	public boolean isIngresoWeb() {
		return ingresoWeb;
	}

	public void setIngresoWeb(boolean ingresoWeb) {
		this.ingresoWeb = ingresoWeb;
	}

	public boolean isPermiteDescripcionObjetoJuicio() {
		return (ConfiguracionMateriaManager.instance().isPermiteDescripcionObjetoJuicio(SessionState.instance().getGlobalCamara())) || (objetoJuicioEdit.getDescripcionObjetoJuicio() != null);
	}
	
	@Override
	public boolean isAddNotificacion() {
		return isIngresoWeb();
	}

	public void onChangePersonaJuridica(ValueChangeEvent event) {
		intervinienteEditList.getTmpInstance().personaJuridicaChanged((Boolean)(event.getNewValue()));
	}

	public Expediente getExpedienteDobleInicio() {
		return expedienteDobleInicio;
	}
	
	public boolean isPideZona() {
		return ConfiguracionMateriaManager.instance().isPedirZonaEnIngreso(SessionState.instance().getGlobalCamara());
	}
	/** OVD */
	
	public void setExpedienteDobleInicio(Expediente expedienteDobleInicio) {
		this.expedienteDobleInicio = expedienteDobleInicio;
	}
	public void setAnioLegajoOVD(Integer anioLegajo) {
		this.anioLegajoOVD = anioLegajo;
	}
	public void setLegajoOVD(Integer legajoOVD) {
		this.legajoOVD = legajoOVD;
	}

	public Integer getLegajoOVD() {
		return legajoOVD;
	}

	public Integer getAnioLegajoOVD() {
		return anioLegajoOVD;
	}
	public String getCaratulaOVD() {
		return caratulaOVD;
	}
	
	public boolean isOVD(){
		return ConfiguracionMateriaManager.instance().isOVD(getCamara());
	}

	public boolean isCompletoOVD() {
		return getAnioLegajoOVD()!=null && getLegajoOVD()!=null;
	}

/** OVD */

	public void setCorrupcion(boolean corrupcion) {
		this.corrupcion = corrupcion;
	}

	public boolean isCorrupcion() {
		return corrupcion;
	}
	
	public boolean isForzeRefresh() {
		return forzeRefresh;
	}
}
