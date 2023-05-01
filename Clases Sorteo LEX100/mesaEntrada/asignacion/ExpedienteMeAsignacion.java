package com.base100.lex100.mesaEntrada.asignacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.Identity;
import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.expand.seam.framework.entity.LookupEntitySelectionListener;
import com.base100.lex100.Lex100Exception;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.currentdate.CurrentDateManager;
import com.base100.lex100.component.enumeration.CompetenciaEnumeration;
import com.base100.lex100.component.enumeration.MateriaEnumeration;
import com.base100.lex100.component.enumeration.NaturalezaSubexpedienteEnumeration;
import com.base100.lex100.component.enumeration.ObjetoJuicioEnumeration;
import com.base100.lex100.component.enumeration.OficinaDestinoEnumeration;
import com.base100.lex100.component.enumeration.StatusStorteoEnumeration;
import com.base100.lex100.component.enumeration.TemaEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionLex100Enumeration;
import com.base100.lex100.component.enumeration.TipoCambioAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoCausaEnumeration;
import com.base100.lex100.component.enumeration.TipoConexidadSolicitadaEnumeration;
import com.base100.lex100.component.enumeration.TipoDocumentoIdentidadEnumeration;
import com.base100.lex100.component.enumeration.TipoGiroEnumeration;
import com.base100.lex100.component.enumeration.TipoInformacionEnumeration;
import com.base100.lex100.component.enumeration.TipoInstanciaEnumeration;
import com.base100.lex100.component.enumeration.TipoIntervinienteEnumeration;
import com.base100.lex100.component.enumeration.TipoLetradoEnumeration;
import com.base100.lex100.component.enumeration.TipoPresentanteRecursoEnumeration;
import com.base100.lex100.component.enumeration.TipoProcesoEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration.Type;
import com.base100.lex100.component.enumeration.TipoSalaResolucionRecursoEnumeration;
import com.base100.lex100.component.enumeration.TipoSubexpedienteEnumeration;
import com.base100.lex100.component.enumeration.TipoVerificacionMesaEnumeration;
import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.controller.entity.expediente.ExpedienteList;
import com.base100.lex100.controller.entity.expediente.ExpedienteListState;
import com.base100.lex100.controller.entity.expediente.ExpedienteSearch;
import com.base100.lex100.controller.entity.objetoJuicio.ObjetoJuicioSearch;
import com.base100.lex100.controller.entity.oficinaAsignacionExp.OficinaAsignacionExpSearch;
import com.base100.lex100.controller.entity.recursoExp.RecursoExpHome;
import com.base100.lex100.controller.entity.tipoBolillero.TipoBolilleroSearch;
import com.base100.lex100.controller.entity.tipoDocumentoIdentidad.TipoDocumentoIdentidadSearch;
import com.base100.lex100.controller.entity.tipoInstancia.TipoInstanciaSearch;
import com.base100.lex100.controller.entity.tipoLetrado.TipoLetradoSearch;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.ConexidadDeclarada;
import com.base100.lex100.entity.DelitoExpediente;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.ExpedienteEspecial;
import com.base100.lex100.entity.Interviniente;
import com.base100.lex100.entity.IntervinienteExp;
import com.base100.lex100.entity.Letrado;
import com.base100.lex100.entity.LetradoDomicilio;
import com.base100.lex100.entity.LetradoIntervinienteExp;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.Perito;
import com.base100.lex100.entity.PeritoExp;
import com.base100.lex100.entity.PresentanteRecursoExp;
import com.base100.lex100.entity.RecursoExp;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.Tema;
import com.base100.lex100.entity.TipoCambioAsignacion;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoInterviniente;
import com.base100.lex100.entity.TipoLetrado;
import com.base100.lex100.entity.TipoPresentanteRecurso;
import com.base100.lex100.entity.TipoProceso;
import com.base100.lex100.entity.TipoRecurso;
import com.base100.lex100.entity.TipoSubexpediente;
import com.base100.lex100.manager.accion.AccionCambioAsignacion;
import com.base100.lex100.manager.accion.impl.AccionException;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.configuracionMateria.ConfiguracionMateriaManager;
import com.base100.lex100.manager.cuaderno.CuadernoManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.manager.expediente.OficinaAsignacionExpManager;
import com.base100.lex100.manager.oficina.OficinaManager;
import com.base100.lex100.manager.recursoExp.RecursoExpManager;
import com.base100.lex100.mesaEntrada.ExpedienteMeAbstract;
import com.base100.lex100.mesaEntrada.conexidad.ConexidadManager;
import com.base100.lex100.mesaEntrada.conexidad.FamiliaManager;
import com.base100.lex100.mesaEntrada.ingreso.CreateExpedienteAction;
import com.base100.lex100.mesaEntrada.ingreso.EditExpedienteAction;
import com.base100.lex100.mesaEntrada.ingreso.ExpedienteMeAdd;
import com.base100.lex100.mesaEntrada.ingreso.IntervinienteEdit;
import com.base100.lex100.mesaEntrada.ingreso.RecursoEdit;
import com.base100.lex100.mesaEntrada.sorteo.MesaSorteo;
import com.base100.lex100.mesaEntrada.sorteo.RubrosInfo;
import com.base100.lex100.mesaEntrada.sorteo.SorteoParametros;


@Name("expedienteMeAsignacion")
@Scope(ScopeType.CONVERSATION)
public class ExpedienteMeAsignacion extends ExpedienteMeAbstract
{
	private static final String TIPOS_RECURSO_MATERIA_LIST_STMT = "from TipoRecurso tr where materia in (:materiaList) and status <> -1";
	//private static final String WHERE_RECURSO_QUEJA_LIST_STMT= "and codigo in ('RJU', 'ADE')";
	private static final String WHERE_RECURSO_TIPO_INGRESO_SALA= "and tipoSala = :tipoSala";
	private static final String TIPOS_RECURSO_MATERIA_STMT = "from TipoRecurso where materia = :materia and status = 0";
	private static final String AND_CODIGO_ALPHA= "and codigo = :codigo";
	private static final String CODIGO_ALPHA_SALTO_INSTANCIA = "RSI";
	private static final String CODIGO_ALPHA_PRESENTACION_VARIA_RETARDO = "PVR";
	
	@In(create=true)
	ExpedienteHome expedienteHome;
	@In(create=true)
	ExpedienteList expedienteList;
	@In(create=true)
	ExpedienteSearch expedienteSearch;
	@In(create=true)
	OficinaAsignacionExpManager oficinaAsignacionExpManager;
	@In(create=true)
	private OficinaDestinoEnumeration oficinaDestinoEnumeration;
	@In(create=true)
	private ObjetoJuicioEnumeration objetoJuicioEnumeration;
	@In(create = true)
	private TemaEnumeration temaEnumeration;
	@In(create = true)
	private ObjetoJuicioSearch objetoJuicioSearch;

	@In(create=true)
	private MesaSorteo mesaSorteo;
	
	@In(create=true)
	private EntityManager entityManager;
	
	@In(create=true)
	private CurrentDateManager currentDateManager;

	private TipoGiroEnumeration.Type tipoGiro;

    private Integer numeroExpediente;
    private Integer anioExpediente;
    private String abreviaturaCamaraExpediente;
	
	/** ATOS COMERCIAL numeroSubExpediente*/
    private Integer numeroSubExpediente;
    /** ATOS COMERCIAL numeroSubExpediente*/
	
    private Expediente expediente;
    private List<Expediente> familiaExpedientes;

    private List<OficinaAsignacionExp> radicacionesActuales;

    
    private boolean defensorOficial;
    private Integer fojas;
    private Integer cuerpos;
    
    
    private Integer hechos;
    
    private List<TipoRecurso> tiposRecurso;    
    private RecursoExp recurso;
    
    
    private RecursoEdit recursoEdit;
    private String observaciones;
  //  private String[] filtroNaturaleza;
    private TipoSorteo tipoSorteo;
  //
	private Sorteo sorteo;
	private Competencia competencia;
    private List<Competencia> competenciasTo = null;
    private List<Competencia> competenciasPlenario = null;

    private boolean radicacionPrevia = false;
    private boolean conexidadPorTema = false;
	private Oficina oficinaDestino;
	private boolean verTodasOficinasDestino = false;
	
    private String[] filtroNaturalezaPrincipal ={
    		(String)NaturalezaSubexpedienteEnumeration.Type.principal.getValue(),
    		};
    private String[] filtroNaturalezaPrincipalIncidenteLegajo ={
    		(String)NaturalezaSubexpedienteEnumeration.Type.principal.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.incidente.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.legajo.getValue()
    		};
    private String[] filtroNaturalezaPrincipalIncidenteLegajoQueja ={
    		(String)NaturalezaSubexpedienteEnumeration.Type.principal.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.incidente.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.legajo.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.recursoQueja.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue(),
    		};
    private String[] filtroNaturalezaPlenario ={
    		(String)NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue()
    		};
    private String[] filtroNaturalezaTribunalOral ={
    		(String)NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue()
    		};
    
    private String[] filtroNaturalezaJuzgadoEjecucion ={
    		(String)NaturalezaSubexpedienteEnumeration.Type.legajoEjecucion.getValue()
    		};
    
    private String[] filtroNaturalezaPrincipalTribunalOralPlenario ={
    		(String)NaturalezaSubexpedienteEnumeration.Type.principal.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue(),
    		};
    private String[] filtroNaturalezaTribunalOralPlenario ={
    		(String)NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue(),
    		};
    private String[] filtroNaturalezaCasacion ={
    		(String)NaturalezaSubexpedienteEnumeration.Type.principal.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.legajo.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.incidente.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.legajoEjecucion.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.recursoQueja.getValue(),
    		};
    private String[] filtroNaturalezaCorte ={
    		(String)NaturalezaSubexpedienteEnumeration.Type.principal.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.legajo.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.incidente.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.cuaderno.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.subexpediente.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.legajoEjecucion.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.recursoQueja.getValue(),
    		};
    private String[] filtroNaturalezaRecursoQuejaCorte ={
    		(String)NaturalezaSubexpedienteEnumeration.Type.principal.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.legajo.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.incidente.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.cuaderno.getValue(),
    		(String)NaturalezaSubexpedienteEnumeration.Type.recursoQueja.getValue(),
    		};
    private String[] filtroNaturalezaMediacion ={
    		(String)NaturalezaSubexpedienteEnumeration.Type.mediacion.getValue(),
    		};
	private boolean competenciaSeleccionadaPorCompetenciaDeMesa =false;

	private boolean necesitaSorteo = false;
	private boolean hasDiversidad = false; 
    
    private ObjetoJuicio objetoJuicioNuevo = null;
    private TipoProceso tipoProcesoNuevo = null;
	private Tema tema;
	private List<Tema> temas;

	private ConexidadDeclarada conexidadDeclarada;

	private boolean enviadoASorteo;
	
	private List<RecursoExp>  listaRecursosParaCirculacion = null;
	private Materia materiaCorteSuprema;
	private Oficina secretariaHonorariosCorte;

	private boolean disableOficinaDestino;
	
	private boolean sorteando;
	private boolean forzeRefresh;
	private Boolean conexidadAsincrona;
	
	private boolean derechosHumanos = false;
	
	@Create
	public void init() {
		recursoEdit = new RecursoEdit();
		Oficina oficinaActual = OficinaManager.instance().getOficinaActual();
		if(oficinaActual != null && oficinaActual.getCompetencia() != null){
			setCompetencia(oficinaActual.getCompetencia());
			competenciaSeleccionadaPorCompetenciaDeMesa  = true;
		}
	    setNumeroExpediente(null);
	    setAnioExpediente(null);
	    setAbreviaturaCamaraExpediente(null);
	    setExpediente(null);
	    familiaExpedientes = null;
	    oficinaDestino = null;
		temas = null;
		enviadoASorteo = false;
		listaRecursosParaCirculacion = null;
		resetRadicacionesActuales();		
		resetRadicacionesFamilia(true);
		this.sorteando=false;
		this.forzeRefresh=false;
		this.conexidadAsincrona = null;
    }
    
	public Integer getNumeroExpediente() {
		if(this.numeroExpediente == null){
			this.numeroExpediente = (getExpediente() != null)? getExpediente().getNumero(): null;
		}
		return this.numeroExpediente;
	}

	public void setNumeroExpediente(Integer numeroExpediente) {
		this.numeroExpediente = numeroExpediente;
	}

//	public void onChangeNumeroExpediente(ValueChangeEvent event) {
//		setNumeroExpediente((Integer)(event.getNewValue()));
//		updateExpediente();
//	}
//	
	public Integer getAnioExpediente() {
		if(this.anioExpediente == null){
			this.anioExpediente = (getExpediente() != null)? getExpediente().getAnio(): null;
		}
		return this.anioExpediente;
	}

	public void setAnioExpediente(Integer anioExpediente) {
		this.anioExpediente = anioExpediente;
	}
	
	/** ATOS COMERCIAL numeroSubExpediente*/
	public Integer getNumeroSubExpediente() {
		return numeroSubExpediente;
	}

	public void setNumeroSubExpediente(Integer numeroSubExpediente) {
		this.numeroSubExpediente = numeroSubExpediente;
	}
	/** ATOS COMERCIAL numeroSubExpediente*/

//	public void onChangeAnioExpediente(ValueChangeEvent event) {
//		setAnioExpediente((Integer)(event.getNewValue()));
//		updateExpediente();
//	}
//	
	public void onChangeAnioExpedienteQueja(ValueChangeEvent event) {
		setAnioExpediente((Integer)(event.getNewValue()));
	}

	public String getCodigoSubexpediente() {
		String codigoSubexpediente = null;
		if (getExpediente() != null) {
			codigoSubexpediente = getExpediente().getCodigoSubexpediente();
			if (Strings.isEmpty(codigoSubexpediente) && (getExpediente().getNumeroSubexpediente() != null) && getExpediente().getNumeroSubexpediente() > 0) {
				codigoSubexpediente = getExpediente().getNumeroSubexpediente().toString();
			}
		}
		return codigoSubexpediente;
	}
	
	public void updateExpediente() {
//		Expediente selected = ExpedienteManager.findExpediente(getEntityManager(), this.numeroExpediente, this.anioExpediente, getNaturalezaRequerida(), false);
		Expediente selected = null;
		if ((this.numeroExpediente != null) && (this.anioExpediente != null)) {
			familiaExpedientes = findExpedientes();
			if (familiaExpedientes.size() == 1) {
				selected = familiaExpedientes.get(0);
			}
		} else {
			familiaExpedientes = null;
		}
		setExpediente(selected);
	}
	
	private List<Expediente> findExpedientes() {
		expedienteSearch.init();
		addExpedienteFilters();
		expedienteSearch.updateFilters();
		return getExpedienteList().getAllResults();
	}
	
	public Expediente getExpediente() {
		return expediente;
	}

	
	
	public void setExpediente(Expediente expediente) {
		if ((expediente != null) && !expediente.equals(this.expediente)){ 
			expediente.reset();
			setFojas(expediente.getFojas());
			setCuerpos(expediente.getCuerpos());
			if (expediente.getExpedienteEspecial() != null) {
				setHechos(expediente.getExpedienteEspecial().getHechos());
				getRecursoEdit().setDetenidos(expediente.getExpedienteEspecial().isDetenidos());
				getRecursoEdit().setRegimenProcesal(expediente.getExpedienteEspecial().getRegimenProcesal());
				setDefensorOficial(Boolean.TRUE.equals(expediente.getExpedienteEspecial().isDefensorOficial()));
			}
			ExpedienteHome expedienteHome = (ExpedienteHome)Component.getInstance(ExpedienteHome.class, true);
			expedienteHome.setId(expediente.getId());
			this.expediente = expediente;
			checkPrevioSorteoExpediente(true);
			if(!competenciaSeleccionadaPorCompetenciaDeMesa && ExpedienteManager.getCompetencia(expediente) != null){
				setCompetencia(ExpedienteManager.getCompetencia(expediente));
			}
			resetRadicacionesActuales();		
			resetRadicacionesFamilia(true);
			if (isTipoSorteoAnySala()) {
				recursoEdit.recalculateExpedienteSala(expediente, false);
			} else if (isTipoSorteoAnySalaCasacion(tipoSorteo)) {
				recursoEdit.recalculateExpedienteCasacion(expediente, false);
			} else if (isTipoSorteoAnyCorte(tipoSorteo)) {
				recursoEdit.recalculateExpedienteCorte(expediente, false);
				initSecretariaCorte();
			}
			initRecursoInfo();
			initOficinasExcluidas();
			// para quejas
			CuadernoManager.instance().setExpedienteActual(expediente);
			initAltaIntervinientes(expediente, true);
			if (getTiposRecurso().size() == 1) {
				getRecursoEdit().setTipoRecurso(getTiposRecurso().get(0));
			}
			
			if (expediente.getObjetoJuicioPrincipal() != null) {
				setTema(expediente.getObjetoJuicioPrincipal().getTema());
				if ((expediente.getObjetoJuicioPrincipal().getTipoInstancia() == null) || TipoInstanciaEnumeration.isPrimeraInstancia(expediente.getObjetoJuicioPrincipal().getTipoInstancia())){
					setObjetoJuicioNuevo(expediente.getObjetoJuicioPrincipal());
				}
			} else {
				setTema(null);
			}
			initFiltroObjetosJuicio();

			if (showConexidadDeclarada()) {
				setConexidadDeclarada(new ConexidadDeclarada());
			}

		}
		this.expediente = expediente;
		
	}

	
	private void initSecretariaCorte() {
		setDisableOficinaDestino(false);
		if (oficinaDestino == null) {
			if (isSorteoRecursoHonorariosCorte() || isTipoRecursoHonorariosCorte()) {
				setOficinaDestino(OficinaManager.instance().getSecretariaHonorariosCorte());
			} else if (expediente.getRadicacionCorteSuprema() != null) {
				if (expediente.getRadicacionCorteSuprema().getOficinaConSecretaria() == null ||
					!expediente.getRadicacionCorteSuprema().getOficinaConSecretaria().equals(OficinaManager.instance().getSecretariaHonorariosCorte())) {
					setOficinaDestino(expediente.getRadicacionCorteSuprema().getOficinaConSecretaria());
					setDisableOficinaDestino(true);
				}
			} else {
				for (Oficina secretaria: getOficinasDestino()) {
					if (secretaria.getMateriaList().size() > 0 &&  OficinaManager.instance().hasMateria(secretaria, expediente.getMateria())) {
						setOficinaDestino(secretaria);
						break;
					}
				}
			}
		}
	}

	public String lookupExpediente(String toPage, String returnPage) {
		if (tipoSorteo == null) {
			ExpedienteManager.instance().setDefaultListaExpedientesFilter();
		} else {
			expedienteSearch.init();
		}
//		setNumeroExpediente(null);
//		setAnioExpediente(null);
		setExpediente(null);

		addExpedienteFilters();
		expedienteSearch.updateFilters();
		expedienteSearch.setShowClearSearchButton(false);
		expedienteHome.initializeLookup(expedienteSearch, new LookupEntitySelectionListener<Expediente>(returnPage, Expediente.class) {
			public void updateLookup(Expediente entity) {
				ExpedienteMeAsignacion asignacion = (ExpedienteMeAsignacion) getComponentInstance(ExpedienteMeAsignacion.class);
				asignacion.setExpediente(entity);
				asignacion.setNumeroExpediente(null);
				asignacion.setAnioExpediente(null);
				asignacion.setAbreviaturaCamaraExpediente(null);
//				if(isNaturalezaRecurso(entity)){
//					setTipoRecurso(entity.getExpedienteEspecial().getTipoRecurso());
//					RecursoExp recursoExp = getRecursoExp(entity);
//					if(recursoExp != null){
//						setRegimenProcesal(recursoExp.getRegimenProcesal());
//						setObservaciones(recursoExp.getDescripcion());
//						setFechaPresentacion(recursoExp.getFechaPresentacion());
//					}
//				}
				
			}

			
		});
		return toPage;
	}
    
	public void doSelectExpediente(Expediente expediente) {
		setExpediente(expediente);
	}

//	protected RecursoExp getRecursoExp(Expediente instance) {
//		List<RecursoExp> list = RecursoExpList.instance().getRecursoExpListByExpediente(instance);
//		return list.size() > 0 ? list.get(0) : null;
//	}

	

	private List<String> getNaturalezaRequerida() {
		List<String> list = null;
		if (isPenal() && getFiltroNaturaleza() != null) { // OJO tambien para Penal Ordinario??
			list = new ArrayList<String>();
			for(String naturaleza: getFiltroNaturaleza()){
				list.add(naturaleza);
			}
		}
		return list;
	}
	
	private Boolean filterByExpedientesEntramite(){
//		if(isNoPenal() || isSorteoJuzgado()) {
//			return null;
//		}
//		return true;
		return null;
	}
	
	private void addExpedienteFilters() {
		expedienteSearch.initForMediacion(false); // @No hay asignaciones para extes de mediacion?

		expedienteSearch.setUseFilterByNumero(false);
		//expedienteSearch.setNumero(getNumeroExpediente());
		if (getNumeroExpediente() != null) {
			expedienteSearch.setCodigoBarras(getNumeroExpediente().toString());
		} else {
			expedienteSearch.setNumero(null);
		}
		expedienteSearch.setAnio(getAnioExpediente());
		
		/** ATOS COMERCIAL numeroSubExpediente*/
		expedienteSearch.setNumeroSubExpediente(getNumeroSubExpediente());
		/** ATOS COMERCIAL numeroSubExpediente*/
		
		if (expedienteSearch.getCodigoBarras() != null || expedienteSearch.getAnio() != null || expedienteSearch.getNumero() != null){
			ExpedienteListState.instance().setShowList(true);
		}

		expedienteSearch.setSoloEnTramite(filterByExpedientesEntramite());
		expedienteSearch.setTipoVerificadoMesa((String)TipoVerificacionMesaEnumeration.Type.validados.getValue());

		if ((getTipoSorteo() != null) && !isRecursoQueja() && !isRecursoQuejaCasacion() && !isRecursoQuejaCorte() && !isSorteoLegajoEjecucion() && !isSorteoLegajoEjecucionCasacion() && !isRecursoHonorariosCorte()) {
			expedienteSearch.setOcultarPases(true);
			expedienteSearch.setOcultarEnRemision(true);
		}
		List<String> naturalezaList = getNaturalezaRequerida();
		
		expedienteSearch.setMostrarTodosExpedientes(isAmbitoSorteoCamara() || isAmbitoSorteoTodasCamaras() || isCorteSuprema());
		expedienteSearch.setMostrarTodasCamaras(isAmbitoSorteoTodasCamaras() || isCorteSuprema());
		
		expedienteSearch.setNaturalezaSubexpedienteList(null);
		if (tipoSorteo != null) {
			expedienteSearch.setExcluirAdministrativos(true);
		} else {
			expedienteSearch.setSoloEnTramite(true);
		}
		if (naturalezaList != null && naturalezaList.size() > 0) {
			expedienteSearch.disableNaturalezaFilter();

			if(!isAmbitoSorteoCamara()){
				expedienteSearch.setAmbitoCamaraDisabled(true);
				expedienteSearch.setMostrarTodosExpedientes(false);
			}
			if(naturalezaList.size() > 1){
				expedienteSearch.setNaturalezaSubexpediente(null);
				expedienteSearch.setNaturalezaSubexpedienteList(naturalezaList);
			}else{
				expedienteSearch.setNaturalezaSubexpedienteList(null);
				expedienteSearch.setNaturalezaSubexpediente(naturalezaList.get(0));				
				
			}
		}
		if (isSoloPenales()) {
			expedienteSearch.setMateriaList(MateriaEnumeration.instance().getMateriasPenales());
		}
		if ((tipoSorteo != null) && isTipoRadicacionJuzgado()) {
			expedienteSearch.setSinTipoRadicacion((String)TipoRadicacionEnumeration.Type.juzgado.getValue());
		}
		if (!Strings.isEmpty(abreviaturaCamaraExpediente)) {
			expedienteSearch.setAbreviaturaCamara(abreviaturaCamaraExpediente);
		}
		
	}
	
	private boolean isSoloPenales() {
		return isSorteoSalaCasacion() || isSorteoRecursoQuejaCasacion() || isSorteoLegajoEjecucionCasacion();
	}

	public void resetRadicacionesActuales() {
		this.radicacionesActuales = null;
	}
	
	public List<OficinaAsignacionExp> getRadicacionesActuales() {
		if(radicacionesActuales == null) {
			radicacionesActuales = OficinaAsignacionExpManager.instance().getOficinasAsignacionOrdenadas(getExpediente());
		}
		return radicacionesActuales;
	}
	       
	public TipoRadicacionEnumeration.Type getTipoRadicacion() {
		if(tipoSorteo == TipoSorteo.juzgado || tipoSorteo == TipoSorteo.fiscaliaJuzgado){
			return TipoRadicacionEnumeration.Type.juzgado;
		}else if(tipoSorteo == TipoSorteo.juzgadoSentencia){
			return TipoRadicacionEnumeration.Type.juzgadoSentencia;
		}else if(tipoSorteo == TipoSorteo.juzgadoPlenario){
			return TipoRadicacionEnumeration.Type.juzgadoPlenario;
		}else if(tipoSorteo == TipoSorteo.juzgadoMediacion){
			return TipoRadicacionEnumeration.Type.juzgado;
		}else if(tipoSorteo == TipoSorteo.sala || 
				tipoSorteo == TipoSorteo.recursoQueja ||
				tipoSorteo == TipoSorteo.recursoLegajoEjecucion){
			return TipoRadicacionEnumeration.Type.sala;
		}else if(tipoSorteo == TipoSorteo.salaEstudio) {
			return TipoRadicacionEnumeration.Type.salaEstudio;
		}else if(tipoSorteo == TipoSorteo.salaRecursoExtraordinario){
			return TipoRadicacionEnumeration.Type.sala;
		}else if(tipoSorteo == TipoSorteo.tribunalOral || tipoSorteo == TipoSorteo.fiscaliaTribunalOral){
			return TipoRadicacionEnumeration.Type.tribunalOral;
		}else if(tipoSorteo == TipoSorteo.juzgadoEjecucion){
			return TipoRadicacionEnumeration.Type.juzgadoEjecucion;		
		}else if(tipoSorteo == TipoSorteo.salaCasacion || tipoSorteo == TipoSorteo.recursoQuejaCasacion || tipoSorteo == TipoSorteo.recursoLegajoEjecucionCasacion){			
			return TipoRadicacionEnumeration.Type.salaCasacion;
		}else if(tipoSorteo == TipoSorteo.secretariaCorte || tipoSorteo == TipoSorteo.recursoQuejaCorte || tipoSorteo == TipoSorteo.recursoSaltoInstanciaCorte || tipoSorteo == TipoSorteo.recursoHonorariosCorte || tipoSorteo == TipoSorteo.recursoPresentacionesVarias) {
			return TipoRadicacionEnumeration.Type.corteSuprema;
		}else if(tipoSorteo == TipoSorteo.ordenCirculacionSala){
			return TipoRadicacionEnumeration.Type.sala;
		}else{
			return TipoRadicacionEnumeration.Type.juzgado;
		}
	}
	
	public String getTipoRadicacionValue() {
		TipoRadicacionEnumeration.Type tipoRadicacion = getTipoRadicacion();
		if (tipoRadicacion != null) {
			return (String)tipoRadicacion.getValue();
		}
		return null;
	}
	
	private TipoRecurso getTipoRecurso() {
		TipoRecurso tipoRecurso = null;
		if(tipoSorteo == TipoSorteo.recursoQueja || tipoSorteo == TipoSorteo.recursoLegajoEjecucion || tipoSorteo == TipoSorteo.recursoQuejaCasacion || tipoSorteo == TipoSorteo.recursoLegajoEjecucionCasacion || tipoSorteo == TipoSorteo.recursoQuejaCorte || tipoSorteo == TipoSorteo.recursoSaltoInstanciaCorte || tipoSorteo == TipoSorteo.recursoPresentacionesVarias || tipoSorteo == TipoSorteo.recursoHonorariosCorte ){
			tipoRecurso = getRecursoEdit().getTipoRecurso();
		}else if(isTipoSorteoAnySala(tipoSorteo) || isTipoSorteoAnySalaCasacion(tipoSorteo) || isTipoSorteoAnyCorte(tipoSorteo)){
			if (getExpediente() != null) {
				RecursoExp recurso = recursoEdit.getRecursoSorteo();
				if(recurso == null){
					if (isTipoSorteoAnySalaCasacion(tipoSorteo)) {
						recurso = getExpediente().getUltimoRecursoCasacionPteAsignar();
					} else {
						recurso = getExpediente().getUltimoRecursoSalaPteAsignar();
					}
				}
				if ((recurso != null) && (recurso.getTipoRecurso() != null)){
					tipoRecurso = recurso.getTipoRecurso();
				}
			}
		}
		return tipoRecurso;
	}

	private String getCodigoTipoCambioAsignacion() {
		if(tipoSorteo == TipoSorteo.juzgado){
			return  TipoCambioAsignacionEnumeration.SORTEO_JUZGADO;
		}else if (tipoSorteo == TipoSorteo.sala || tipoSorteo == TipoSorteo.salaCasacion || tipoSorteo == TipoSorteo.recursoQueja || tipoSorteo == TipoSorteo.recursoQuejaCasacion || tipoSorteo == TipoSorteo.secretariaCorte || tipoSorteo == TipoSorteo.recursoQuejaCorte || tipoSorteo == TipoSorteo.recursoSaltoInstanciaCorte || tipoSorteo == TipoSorteo.recursoPresentacionesVarias || tipoSorteo == TipoSorteo.recursoHonorariosCorte){
			TipoRecurso tipoRecurso = getTipoRecurso();
			if (tipoRecurso != null){
				return tipoRecurso.getCodigo();
			}
		}else if(tipoSorteo == TipoSorteo.juzgadoSentencia){
			return TipoCambioAsignacionEnumeration.RESORTEO_AUTOMATICO_JUZGADO_SENTENCIA;
		}else if (tipoSorteo == TipoSorteo.salaEstudio){
			return TipoCambioAsignacionEnumeration.SORTEO_SALA_ESTUDIO;
		}else if(tipoSorteo == TipoSorteo.juzgadoPlenario){
			return TipoCambioAsignacionEnumeration.SORTEO_JUZGADO_PLENARIO;
		}else if(tipoSorteo == TipoSorteo.tribunalOral){
			return TipoCambioAsignacionEnumeration.ELEVACION_A_TRIBUNAL_ORAL;
		}else if(tipoSorteo == TipoSorteo.perito){
			return TipoCambioAsignacionEnumeration.SORTEO_PERITO;
		}
		return null;
	}

	private String getDescripcionCambioAsignacion() {
		if (isTipoSorteoAnySala(tipoSorteo) || isTipoSorteoAnySalaCasacion(tipoSorteo)) {				
			TipoRecurso tipoRecurso = getTipoRecurso();
			if (tipoRecurso != null){
				return tipoRecurso.getDescripcion();
			}
		} else {
			Integer tipoInstanciaCodigo = TipoRadicacionEnumeration.calculateCodigoTipoInstanciaByTipoRadicacion(getTipoRadicacion());
			TipoInstancia tipoInstancia = TipoInstanciaSearch.findByCodigo(getEntityManager(), tipoInstanciaCodigo);
			TipoCambioAsignacion tipoCambioAsignacion = TipoCambioAsignacionEnumeration.getItemByCodigo(getEntityManager(), getCodigoTipoCambioAsignacion(), tipoInstancia, getExpediente().getMateria());
			if (tipoCambioAsignacion != null) {
				return tipoCambioAsignacion.getDescripcion();
			}
		}
		return null;
	}

	private boolean isTipoRecursoHonorariosCorte() {
		return getRecursoEdit() != null && getRecursoEdit().getRecursoSorteo() != null && getRecursoEdit().getRecursoSorteo().getTipoRecurso() != null &&
			   getRecursoEdit().getRecursoSorteo().getTipoRecurso().getTipoSala().equals(TipoSalaResolucionRecursoEnumeration.Type.recurso_honorarios_corte_suprema.getValue());
	}
	
	public List<Oficina> getOficinasDestino() {
		boolean incluirSecretarias = isVerTodasOficinasDestino();

		if (isTipoRadicacionCorteSuprema() && isTipoRecursoHonorariosCorte()) {
			List<Oficina> result = new ArrayList<Oficina>();
			result.add(OficinaManager.instance().getSecretariaHonorariosCorte());
			return result;
		} else if(!isVerTodasOficinasDestino() && this.sorteo != null && !isRadicacionPrevia()){
			return getOficinas(this.sorteo);
		}else if (isTipoRadicacionTribunalOral() || isTipoRadicacionAnySala() || isTipoRadicacionAnyJuzgado() || isTipoRadicacionSalaCasacion() || isTipoRadicacionCorteSuprema()) {
			return oficinaDestinoEnumeration.getOficinasPorRadicacion(getTipoRadicacion(), incluirSecretarias);
		}else{
			return new ArrayList<Oficina>();
		}
	}

	public boolean isTipoRadicacionSala() {
		return TipoRadicacionEnumeration.Type.sala == getTipoRadicacion();
	}
	
	public boolean isTipoRadicacionAnySala() {
		return TipoRadicacionEnumeration.isAnySala(getTipoRadicacion());			
	}
	
	public boolean isTipoRadicacionAnyJuzgado() {
		return TipoRadicacionEnumeration.isAnyJuzgado(getTipoRadicacion());			
	}
	
	public boolean isTipoSorteoAnySala(TipoSorteo tipo) {
		return (tipoSorteo == TipoSorteo.sala || 
				tipoSorteo == TipoSorteo.recursoQueja ||
				tipoSorteo == TipoSorteo.recursoLegajoEjecucion ||
				tipoSorteo == TipoSorteo.salaEstudio ||
				tipoSorteo == TipoSorteo.salaRecursoExtraordinario
				);
	}
	
	public boolean isTipoSorteoAnySalaCasacion(TipoSorteo tipo) {
		return (tipoSorteo == TipoSorteo.salaCasacion || 
				tipoSorteo == TipoSorteo.recursoQuejaCasacion ||
				tipoSorteo == TipoSorteo.recursoLegajoEjecucionCasacion
				);
	}
	
	public boolean isTipoSorteoAnyCorte(TipoSorteo tipo) {
		return (tipoSorteo == TipoSorteo.secretariaCorte || 
				tipoSorteo == TipoSorteo.recursoQuejaCorte ||
				tipoSorteo == TipoSorteo.recursoSaltoInstanciaCorte ||
				tipoSorteo == TipoSorteo.recursoHonorariosCorte || 
				tipoSorteo == TipoSorteo.recursoPresentacionesVarias
				);
	}
	
	public boolean isTipoSorteoAnySala(){
		return isTipoSorteoAnySala(tipoSorteo);
	}
	
	public boolean isTipoRadicacionTribunalOral() {
		return TipoRadicacionEnumeration.isRadicacionTribunalOral(getTipoRadicacion());
	}

	public boolean isTipoRadicacionPlenario() {
		return TipoRadicacionEnumeration.isRadicacionJuzgadoPlenario(getTipoRadicacion());
	}

	public boolean isTipoRadicacionSentencia() {
		return TipoRadicacionEnumeration.isRadicacionJuzgadoSentencia(getTipoRadicacion());
	}

	public boolean isTipoRadicacionSalaCasacion() {
		return TipoRadicacionEnumeration.isRadicacionSalaCasacion(getTipoRadicacion());
	}

	public boolean isTipoRadicacionCorteSuprema() {
		return TipoRadicacionEnumeration.isRadicacionCorteSuprema(getTipoRadicacion());
	}
	
	public String getTitle() {
		return getTitleBySorteo(tipoSorteo);
	}
	
	public String getTitleBySorteo(TipoSorteo tipoSorteo) {
		if (tipoSorteo != null) {
			switch (tipoSorteo) {
			case juzgado:
				return getMessages().get("expedienteMeAsignacion.asignacion.juzgado");
			case juzgadoSentencia:
				return getMessages().get("expedienteMeAsignacion.asignacion.juzgadoSentencia");
			case sala:
				return getMessages().get("expedienteMeAsignacion.asignacion.sala");
			case recursoQueja:
				return getMessages().get("recursoQueja.tittle");
			case recursoLegajoEjecucion:
				return getMessages().get("recursoLegajoEjecucion.title");
			case recursoLegajoEjecucionCasacion:
				return getMessages().get("recursoLegajoEjecucion.title");
			case juzgadoPlenario:
				return getMessages().get("expedienteMeAsignacion.asignacion.juzgadoPlenario");
			case tribunalOral:
				return getMessages().get("expedienteMeAsignacion.asignacion.tribunalOral");
			case salaCasacion:
				return getMessages().get("expedienteMeAsignacion.asignacion.salaCasacion");
			case recursoQuejaCasacion:
				return getMessages().get("expedienteMeAsignacion.asignacion.recursoQuejaCasacion");
			case secretariaCorte:
				return getMessages().get("expedienteMeAsignacion.asignacion.secretariaCorte");
			case recursoQuejaCorte:
				return getMessages().get("expedienteMeAsignacion.asignacion.recursoQuejaCorte");
			case recursoSaltoInstanciaCorte:
				return getMessages().get("expedienteMeAsignacion.asignacion.recursoSaltoInstanciaCorte");
			case recursoPresentacionesVarias:
				return getMessages().get("expedienteMeAsignacion.asignacion.pvaRetardoJusticia");
			case recursoHonorariosCorte:
				return getMessages().get("expedienteMeAsignacion.asignacion.recursoHonorariosCorte");
			case salaEstudio:
				return getMessages().get("expedienteMeAsignacion.asignacion.salaEstudio");
			case perito:
				return getMessages().get("expedienteMeAsignacion.asignacion.Perito");
			case juzgadoEjecucion:
				return getMessages().get("expedienteMeAsignacion.asignacion.juzgadoEjecucion");				
			default:
				break;
			}
		}
		return "";
	}
    
	public String getTitleByTipoSalaEstudio(TipoSalaEstudio tipoSalaEstudio){
		if (tipoSalaEstudio != null) {
			switch (tipoSalaEstudio) {
			case salaEstudio:
				return getMessages().get("expedienteMeAsignacion.tipoSorteoSalaEstudio.salaEstudio");
			case salaEstudioRecusacionConCausa:
				return getMessages().get("expedienteMeAsignacion.tipoSorteoSalaEstudio.salaEstudioRecusacionConCausa");
			case salaPronunciamientoSentencia:
				return getMessages().get("expedienteMeAsignacion.tipoSorteoSalaEstudio.salaPronunciamientoSentencia");
			default:
				break;
			}
		}
		return "";		
	}
	
	public TipoGiroEnumeration.Type getTipoGiro(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		if (this.tipoGiro == null) {
			if (TipoRadicacionEnumeration.isAnySala(tipoRadicacion)) {
				this.tipoGiro = ConfiguracionMateriaManager.instance().getTipoGiroAsignacionSala(SessionState.instance().getGlobalCamara(), expediente.getMateria());
			} else {
				this.tipoGiro = ConfiguracionMateriaManager.instance().getTipoGiroAsignacion(SessionState.instance().getGlobalCamara(), expediente.getMateria());
			}
		}
		return this.tipoGiro;
	}

	public void setTipoGiro(TipoGiroEnumeration.Type tipoGiro) {
		this.tipoGiro = tipoGiro;
	}

	public String doSave1(){
		return doSave1(null);
	}
	
	public String doSave1(String page){
		boolean error = false;
		
		if ((getExpediente() == null)  || (showCamaraInput()  && !getExpediente().getCamara().getAbreviatura().equals(getAbreviaturaCamaraExpediente())) 
			|| !getExpediente().getNumero().equals(getNumeroExpediente()) || !getExpediente().getAnio().equals(getAnioExpediente())){
			updateExpediente();
		}
		
		if (getExpediente() == null) {
			if(getFamiliaExpedientes() == null || getFamiliaExpedientes().size() == 0){
				errorMsg("expedienteMeAsignacion.error.expedienteRequerido");
				error = true;
			}
		} else if( (ExpedienteManager.isPase(getExpediente()) || ExpedienteManager.isRemision(getExpediente())) && (!isRecursoQueja() && !isSorteoLegajoEjecucion() && !isRecursoQuejaCasacion() && !isRecursoQuejaCorte() && !isRecursoSaltoInstanciaCorte() && !isRecursoPresentacionesVarias())) {
			errorMsg("expedienteMeAsignacion.error.expedienteEnPase");
			error = true;
		} else if(isSorteoLegajoEjecucion() && !ExpedienteManager.instance().isPenal(getExpediente())) {
			errorMsg("expedienteMeAsignacion.error.expedientePenalRequerido");
			error = true;
		} else if (isSorteoSalaEstudio()) {
			Oficina radicacionSalaActual = ExpedienteManager.instance().getOficinaByRadicacion(getExpediente(), TipoRadicacionEnumeration.Type.sala);
			if (radicacionSalaActual == null) {
				errorMsg("expedienteMeAsignacion.error.radicacionSalaRequerida");
				error = true;
			}
		}else if (isSorteoAnySala() && !ExpedienteManager.instance().isOficinaActual(expediente)) {
			errorMsg("expedienteMeAsignacion.error.expedienteNoEnOficinaActual");
			error = true;
		} else {
			if(isSorteoJuzgado()) {
				Oficina oficinaActual = ExpedienteManager.instance().getOficinaByRadicacion(getExpediente(), getTipoRadicacion());
				if (oficinaActual != null) {
					errorMsg(Interpolator.instance().interpolate(getMessages().get("expedienteMeAsignacion.error.radicacionActual"), getMessages().get(getTipoRadicacion().getLabel())));
					error = true;
				}
			}
			if((isRadicacionPrevia() || isSorteoSecretariaCorte() || isSorteoRecursoQuejaCorte() || isSorteoRecursoSaltoInstanciaCorte() || isSorteoRecursoPresentacionesVarias() || isSorteoRecursoHonorariosCorte()) && getOficinaDestino() == null) {
				errorMsg("expedienteMeAsignacion.error.oficinaRequerida");
				error = true;
			}
		}
		if(!error && isMostrarSelectorObjetoJuicio() && getObjetoJuicioNuevo() == null) {
			errorMsg("expedienteMeAsignacion.error.objetoJuicioRequerido");
			error = true;			
		}
		if(!error && showCompetencias()){
			if(competencia == null){
				errorMsg("expedienteMeAdd.error.noCompetenciaDefined");
				error = true;
			}
		}
		if(!error && showConexidadDeclarada()){
			try {
				checkConexidadDeclarada();
			} catch (EntityOperationException e) {
				error = true;
			}
		}
		if(!error && isSorteoRecursoHonorariosCorte()){
			if (getExpediente().getRadicacionCorteSuprema() == null) {
				errorMsg("expedienteMeAdd.error.noCorteSuprema");
				error = true;
			}
		}

		return error ? null : page;
	}
    
	private void initAltaIntervinientes(Expediente expedienteActual, boolean initClear) {
		ExpedienteMeAdd expedienteMeAdd = ExpedienteMeAdd.instance();
		if(initClear){
			expedienteMeAdd.init();
		}
		if(expedienteActual.getTipoCausa() != null){
			expedienteMeAdd.setTipoCausaEspecial(expedienteActual.getTipoCausa());
		}else{
			expedienteMeAdd.setTipoCausaNormal(null);
		}
		expedienteMeAdd.setObjetoJuicio(expedienteActual.getObjetoJuicio());
		expedienteMeAdd.setIngresoUrgente(true);
	}
	
	private boolean hayNuevosIntervinientes(){
		try {
			ExpedienteMeAdd.instance().getIntervinienteEditList().onAcceptLine();
			return CreateExpedienteAction.instance().countIntervinientes(ExpedienteMeAdd.instance().getIntervinienteEditList()) > 0;		
		} catch (EntityOperationException e) {
			return false;
		}
	}

	
	private String doSaveRecurso(String page){
		boolean error = false;
		
		if(showTipoRecurso() && getRecursoEdit().getTipoRecurso() == null){
			errorMsg("expedienteMeAsignacion.error.tipoRecursoRequerido");
			error = true;			
		}
		if(getRecursoEdit().getFechaPresentacion()==null){
			errorMsg("expedienteMeAsignacion.error.fechaPresentacionRequerido");
			error = true;
		}
		if(getRecursoEdit().getFechaPresentacion().after(currentDateManager.getCurrentDate())) {
			errorMsg("expedienteMeAsignacion.error.fechaPresentacionPosterior");
			error = true;
		}
		
		Oficina oficinaSorteadora =  OficinaManager.instance().getOficinaSorteadoraOAsignadora();
		if(!OficinaManager.instance().isMesaDeEntrada(oficinaSorteadora)){
			errorMsg("expedienteMeAdd.error.ingresoQuejaPorOficinaInvalida");
			error = true;			
		}
		
		if(!checkPresentantesRecurso()) {
			errorMsg("expedienteMeAdd.error.obligatorioEspecificarPresentantes");
			error = true;			
		}
		
		/** ATOS - RECURSO QUEJA */
		 
		elminiarLetradosSeleccionadosRedundantes(RecursoExpManager.instance().getIntervinientesSeleccionados(), RecursoExpManager.instance().getLetradoIntervinientesSeleccionados());
		if(CamaraManager.instance().isIntevinientesEnRecursoQuejaRequerido() && 
					RecursoExpManager.instance().countIntevinientesSeleccionados() == 0 
					&& RecursoExpManager.instance().countLetradoIntervinientesSeleccionados() == 0  
					&& RecursoExpManager.instance().countPeritosSeleccionados() == 0  
					&& !hayNuevosIntervinientes()){
			errorMsg("recursoExp.intervinientesSeleccionados.required");
			error = true;

		}
		if(CamaraManager.instance().isIntevinientesEnRecursoQuejaRequerido() && 
				RecursoExpManager.instance().countIntevinientesSeleccionados() > 0 && RecursoExpManager.instance().countLetradoIntervinientesSeleccionados() > 0  
				&& !hayNuevosIntervinientes()){
			errorMsg("recursoExp.intervinientesSeleccionados.letradosExclusivos");
			error = true;
		}
		
		if(CamaraManager.instance().isIntevinientesEnRecursoQuejaRequerido() && 
				RecursoExpManager.instance().countIntevinientesSeleccionados() == 0 && RecursoExpManager.instance().countLetradoIntervinientesSeleccionados() > 1  
				&& !hayNuevosIntervinientes()){
			errorMsg("recursoExp.intervinientesSeleccionados.maximoNroLetrados");
			error = true;

		}
		
		/** ATOS - RECURSO QUEJA */
		
		return error ? null : page;		
	}
	
	private boolean checkPresentantesRecurso() {
		if (getRecursoEdit().getTipoRecurso() != null && getRecursoEdit().getTipoRecurso().isPresentanteObligatorio()) {
			int count = RecursoExpManager.instance().getPresentanteRecursoExpList().size();
			if (count == 0) {
				for (IntervinienteEdit intervinienteEdit: ExpedienteMeAdd.instance().getIntervinienteEditList().getIntervinientesValidos()) {
					TipoPresentanteRecurso tipoPresentanteRecurso = TipoPresentanteRecursoEnumeration.instance().getTipoPresentante(intervinienteEdit.getTipoInterviniente().getDescripcion());
					if (tipoPresentanteRecurso != null) {
						count++;
					}
				}
			}
			return count > 0;
		} else {
			return true;
		}
	}

	/** ATOS - RECURSO QUEJA */
	private void elminiarLetradosSeleccionadosRedundantes(
			Map<IntervinienteExp, Boolean> intervinientesSeleccionados,
			Map<LetradoIntervinienteExp, Boolean> letradoIntervinientesSeleccionados) {
		if(letradoIntervinientesSeleccionados.size() > 0 ){
			List<LetradoIntervinienteExp> letradosImplicitos = new ArrayList<LetradoIntervinienteExp>(); 
			for(IntervinienteExp intervinienteExp: intervinientesSeleccionados.keySet()) {
				Boolean value = intervinientesSeleccionados.get(intervinienteExp);
				if (value != null && value) {
					letradosImplicitos.addAll(intervinienteExp.getLetradoIntervinienteExpList());
				}
			}
			 
			for(LetradoIntervinienteExp letradoIntervinienteExp: letradosImplicitos) {
				letradoIntervinientesSeleccionados.remove(letradoIntervinienteExp);
			}
		}
		
	}
	/** ATOS - RECURSO QUEJA */

	
	public boolean isEnviadoASorteo() {
		return enviadoASorteo;
	}

	public void setEnviadoASorteo(boolean enviadoASorteo) {
		this.enviadoASorteo = enviadoASorteo;
	}

	public String doSaveFin(int currentPage, String page){
		if (isEnviadoASorteo()) {
			return null;
		}
		boolean error = false;
		error = doSave1("ok") == null;

		if(!error && showTipoRecurso() && getRecursoEdit().getTipoRecurso() == null){
			errorMsg("expedienteMeAsignacion.error.tipoRecursoRequerido");
			error = true;
		}
		if(!error){
			try {
				setEnviadoASorteo(true);

				checkCambioObjetoJuicio();
				checkCambioFojasRecurso();
				Oficina oficinaAsignadora = OficinaManager.instance().getOficinaSorteadoraOAsignadora();
				asignarOficina(oficinaAsignadora);
			} catch (Lex100Exception e) {
				setEnviadoASorteo(false);
				errorMsg("expedienteMeAsignacion.error.asignarExpediente", e.getMessage());
				error = true;
			}
		}
		return error ? null : page;
	}

	private void checkCambioFojasRecurso() {
		if(isSorteoSala() && recursoEdit.getRecursoSorteo() != null){
			recursoEdit.copyFojasToRecurso(recursoEdit.getRecursoSorteo());
			getEntityManager().flush();
		}
	}

	private void checkCambioObjetoJuicio() {
		if (isMostrarSelectorObjetoJuicio()) {
			ObjetoJuicio objetoJuicioAnterior = getExpediente().getObjetoJuicioPrincipal();
			getExpediente().setObjetoJuicioPrincipal(getEntityManager().find(ObjetoJuicio.class, getObjetoJuicioNuevo().getId()));
			getExpediente().setTipoProceso(tipoProcesoNuevo);
			String objetoJuicioChanged = EditExpedienteAction.objetoJuicioChanged(objetoJuicioAnterior, getExpediente().getDescripcionObjetoJuicio(), getExpediente());
			if(objetoJuicioChanged != null) {
				ExpedienteManager.instance().actualizarCaratula(getExpediente(), TipoInformacionEnumeration.CAMBIO_OBJETO_JUICIO, objetoJuicioChanged);
			}
		}
	}

	public String doSaveRecurso(int currentPage, String page){
		boolean error = false;
		error = doSave1("ok") == null;
		if(!error){
			error = doSaveRecurso("ok") == null;
		}
		if(!error){
			try {				
				doCrearRecurso();
			} catch (Lex100Exception e) {
				errorMsg("expedienteMeAsignacion.error.asignarExpediente", e.getMessage());
				error = true;
			}
		}
		return error ? null : page;		
	}
	
	private void doCrearRecurso() throws Lex100Exception{
		Expediente expedienteAsignar = getExpediente();
		expedienteAsignar = createRecurso(getExpediente());	
		if(expedienteAsignar == null){
			throw new Lex100Exception("Imposible crear recurso");
		} else {
			getEntityManager().flush();
		}
		setExpediente(expedienteAsignar);
		//
		Oficina oficinaAsignadora = OficinaManager.instance().getOficinaSorteadoraOAsignadora();
		asignarOficina(oficinaAsignadora);
	}
	
	public RecursoExp createRecurso(Expediente expediente, TipoRecurso tipoRecurso, Date fechaPresentacion, boolean detenidos, Integer regimenProcesal, String observaciones, 
			String resolucionApelacion,
			String tipoResolucionApelada, String tipoSentencia,
			Integer fojas, Integer cuerpos, Integer paquetes, Integer agregados, String comentarios, boolean urgente){
		RecursoExpHome recursoExpHome = (RecursoExpHome) getComponentInstance(RecursoExpHome.class);
        recursoExpHome.setLimpiarIntervinientesSeleccionados(false);
        recursoExpHome.createNewInstance();
		RecursoExp recursoExp = recursoExpHome.getInstance();
		recursoExp.setFechaResolucionElevacion(fechaPresentacion);
		recursoExp.setTipoRecurso(tipoRecurso);
		recursoExp.setDetenidos(detenidos);
		recursoExp.setRegimenProcesal(regimenProcesal);
		recursoExp.setDescripcion(observaciones);
		recursoExp.setResolucionApelacion(resolucionApelacion);
		recursoExp.setTipoResolucionApelada(tipoResolucionApelada);
		recursoExp.setTipoSentencia(tipoSentencia);
		recursoExp.setExpediente(expediente);
		
		recursoExp.setFojas(fojas);
		recursoExp.setCuerpos(cuerpos);
		recursoExp.setPaquetes(paquetes);
		recursoExp.setAgregados(agregados);
		recursoExp.setComentarios(comentarios);
		recursoExp.setUrgente(urgente);
		
		recursoExpHome.setSaltarControlIntervinientesRequeridos(hayNuevosIntervinientes());
		String ret = recursoExpHome.doAdd("ok");
        recursoExpHome.setLimpiarIntervinientesSeleccionados(true);
		if(ret == null){
			recursoExpHome.cancel();
			return null;
		}else{
			getEntityManager().flush();
			return recursoExpHome.getInstance();
		}
	}
	
	private Expediente createRecurso(Expediente expedienteOrigen) {
		CuadernoManager cuadernoManager = CuadernoManager.instance();
		CuadernoManager.instance().doCrearRecursoQueja();
		
		Expediente ret = null;		

		TipoSubexpediente tipoSubexpediente = null;
		if(isSorteoRecursoQueja() || isSorteoRecursoQuejaCasacion() || isSorteoRecursoQuejaCorte() ) {
			tipoSubexpediente = getTipoSubexpedienteParaRecursoQueja(expedienteOrigen.getMateria());
		} else if (isSorteoRecursoSaltoInstanciaCorte()) {
			tipoSubexpediente = TipoSubexpedienteEnumeration.calculateTipoSubexpediente(entityManager, NaturalezaSubexpedienteEnumeration.Type.recursoSaltoInstancia, getMateriaCorteSuprema());
		} else if (isSorteoRecursoPresentacionesVarias()) {
			tipoSubexpediente = TipoSubexpedienteEnumeration.calculateTipoSubexpediente(entityManager, NaturalezaSubexpedienteEnumeration.Type.presentacionesVariasRetardo, getMateriaCorteSuprema());
		}else if (isSorteoRecursoHonorariosCorte()) {
			tipoSubexpediente = TipoSubexpedienteEnumeration.calculateTipoSubexpediente(getEntityManager(), NaturalezaSubexpedienteEnumeration.Type.honorarios, getMateriaCorteSuprema());
		} else {
			tipoSubexpediente = getTipoSubexpedienteParaRecursoApelacion(expedienteOrigen.getMateria(), TipoSubexpedienteEnumeration.CODIGO_LEGAJO_EJECUCION_PENAL);
		}
		if(tipoSubexpediente == null){
			getStatusMessages().addFromResourceBundle(Severity.ERROR, "expedienteMeAsignacion.error.tipoSubExpedienteQueja.notExist");
		}else{
			cuadernoManager.getCuadernoActual().setTipoSubexpediente(tipoSubexpediente);
	
			cuadernoManager.setPermitirCuadernoSinContenido(true);
			cuadernoManager.setAbreviaturaSubexpediente("");
			
			copyIntervinientesSeleccionados();
			copyPeritosSeleccionados();
			String done = cuadernoManager.generarCuaderno("done", true, true, true);
			if (done != null) {
				expedienteHome.setId(cuadernoManager.getCuadernoActual().getId());
				cuadernoManager.getCuadernoActual().setVerificacionMesa("N");

				actualizarIntervinientesNuevos();
				
				createRecurso(expedienteHome.getInstance(), getRecursoEdit().getTipoRecurso(), getRecursoEdit().getFechaPresentacion(), getRecursoEdit().isDetenidos(), getRecursoEdit().getRegimenProcesal(), getRecursoEdit().getObservaciones(), 
						getRecursoEdit().getResolucionApelacion(),
						getRecursoEdit().getTipoResolucionApelada(),
						getRecursoEdit().getTipoSentencia(),
						getRecursoEdit().getFojas(), getRecursoEdit().getCuerpos(), getRecursoEdit().getPaquetes(), getRecursoEdit().getAgregados(), getRecursoEdit().getComentarios(), getRecursoEdit().isUrgente());
				if (expedienteHome.getInstance().getExpedienteEspecial() == null) {
					ExpedienteEspecial expedienteEspecial = new ExpedienteEspecial();
					expedienteEspecial.setExpediente(expedienteHome.getInstance());
					getEntityManager().persist(expedienteEspecial);
					expedienteHome.getInstance().setExpedienteEspecial(expedienteEspecial);
					getEntityManager().flush();
				}
				expedienteHome.getInstance().getExpedienteEspecial().setDetenidosFlag(getRecursoEdit().isDetenidos());
				expedienteHome.getInstance().getExpedienteEspecial().setRegimenProcesal(getRecursoEdit().getRegimenProcesal());
				if (expedienteHome.getInstance().getExpedienteIngreso() != null) {
					expedienteHome.getInstance().getExpedienteIngreso().setUrgente(getRecursoEdit().isUrgente());
				}
				getEntityManager().flush();
				ret = expedienteHome.getInstance();
			}
		}		
		return ret;
	}

	
	private void actualizarIntervinientesNuevos() {
		Expediente expediente = expedienteHome.getInstance();
		getEntityManager().refresh(expediente);
		RecursoExpManager recursoExpManager = RecursoExpManager.instance();
		for (IntervinienteEdit intervinienteEdit: ExpedienteMeAdd.instance().getIntervinienteEditList().getIntervinientesValidos()) {
			if (intervinienteEdit.getIntervinienteExp() == null) {
				continue;
			}
			IntervinienteExp intervinienteExp = intervinienteEdit.getIntervinienteExp(); 
			Boolean seleccionado =recursoExpManager.getIntervinientesSeleccionados().get(intervinienteExp);
			if (seleccionado == null || seleccionado == false) {
				recursoExpManager.getIntervinientesSeleccionados().put(intervinienteExp, true);
			}
			if (intervinienteExp.getTipoInterviniente() != null && intervinienteExp.getTipoInterviniente().getDescripcion() != null) {
				TipoPresentanteRecurso tipoPresentanteRecurso = TipoPresentanteRecursoEnumeration.instance().getTipoPresentante(intervinienteExp.getTipoInterviniente().getDescripcion());
				if (tipoPresentanteRecurso != null) {
					PresentanteRecursoExp presentanteRecursoExp = new PresentanteRecursoExp();
					presentanteRecursoExp.setIntervinienteExp(intervinienteExp);
					presentanteRecursoExp.setTipoPresentanteRecurso(tipoPresentanteRecurso);
					recursoExpManager.getPresentanteRecursoExpList().add(presentanteRecursoExp);
					recursoExpManager.getPresentantesSeleccionados().put(intervinienteExp, true);
				}
			}
		}
	}

	private Materia getMateriaCorteSuprema() {
		if (this.materiaCorteSuprema == null) {
			this.materiaCorteSuprema = (Materia) getEntityManager().createQuery("from Materia where abreviatura = '"+MateriaEnumeration.CODIGO_CORTE_SUPREMA+"' and status <> -1")
				.getSingleResult();
		}
		return materiaCorteSuprema;
	}

/** ATOS - RECURSO QUEJA **/
	
	private List<IntervinienteExp> copyIntervinientesSeleccionados() {
		List<IntervinienteExp> intervinientesAgregados = new ArrayList<IntervinienteExp>(); 
		if(RecursoExpManager.instance().countIntevinientesSeleccionados() > 0) {
			CuadernoManager.instance().setIntervinientesSeleccionados(RecursoExpManager.instance().getIntervinientesSeleccionados());
			
		} else if(RecursoExpManager.instance().countLetradoIntervinientesSeleccionados() > 0){
			Map<IntervinienteExp, Boolean> intervinientesSeleccionados  = new HashMap<IntervinienteExp, Boolean>();
			//Solo debe haber uno seleccionado
			Map<LetradoIntervinienteExp, Boolean> letradosIntervinientes = RecursoExpManager.instance().getLetradoIntervinientesSeleccionados();
			for (LetradoIntervinienteExp letradoIntervinienteExp : letradosIntervinientes.keySet()) {
				Boolean value = letradosIntervinientes.get(letradoIntervinienteExp);
				if (value != null && value) {
					Interviniente interviniente = createInterviniente(letradoIntervinienteExp.getLetrado());
					IntervinienteExp intervinienteExp = createIntervinienteExp(letradoIntervinienteExp, interviniente);

					LetradoIntervinienteExp newLetradoIntervinienteExp = new LetradoIntervinienteExp();
					newLetradoIntervinienteExp.setIntervinienteExp(intervinienteExp);
					newLetradoIntervinienteExp.setCodigoPostal(letradoIntervinienteExp.getCodigoPostal());
					newLetradoIntervinienteExp.setCorreoElectronico(letradoIntervinienteExp.getCorreoElectronico());
					newLetradoIntervinienteExp.setDomicilio(letradoIntervinienteExp.getDomicilio());
					newLetradoIntervinienteExp.setFax(letradoIntervinienteExp.getFax());
					newLetradoIntervinienteExp.setLetrado(letradoIntervinienteExp.getLetrado());
					newLetradoIntervinienteExp.setLocalidad(letradoIntervinienteExp.getLocalidad());
					newLetradoIntervinienteExp.setProvincia(letradoIntervinienteExp.getProvincia());
					newLetradoIntervinienteExp.setTelefono(letradoIntervinienteExp.getTelefono());
					newLetradoIntervinienteExp.setTipoAsignacionInterviniente(letradoIntervinienteExp.getTipoAsignacionInterviniente());
					newLetradoIntervinienteExp.setZonaMandamiento(letradoIntervinienteExp.getZonaMandamiento());
					newLetradoIntervinienteExp.setZonaNotificacion(letradoIntervinienteExp.getZonaNotificacion());
					
					TipoLetrado tipoLetrado = TipoLetradoSearch.findByCodigo(getEntityManager(), TipoLetradoEnumeration.LETRADO_EN_CAUSA_PROPIA, letradoIntervinienteExp.getTipoLetrado().getMateria() );
					
					newLetradoIntervinienteExp.setTipoLetrado(tipoLetrado);
					intervinienteExp.getLetradoIntervinienteExpList().add(newLetradoIntervinienteExp);
					intervinientesSeleccionados.put(intervinienteExp, true);
					intervinientesAgregados.add(intervinienteExp);
				}
			}
			 
			CuadernoManager.instance().setIntervinientesNuevos(intervinientesAgregados);
			CuadernoManager.instance().setIntervinientesSeleccionados(intervinientesSeleccionados);
		}
		return intervinientesAgregados;
	}


	private void copyPeritosSeleccionados() {		
		List<IntervinienteExp> intervinientesNuevos = CuadernoManager.instance().getIntervinientesNuevos();
		
		List<PeritoExp> peritosAgregados = new ArrayList<PeritoExp>(); 
		if(RecursoExpManager.instance().countPeritosSeleccionados() > 0) {
			Map<PeritoExp, Boolean> peritosSeleccionados  = new HashMap<PeritoExp, Boolean>();
			Map<PeritoExp, Boolean> peritos = RecursoExpManager.instance().getPeritosSeleccionados();
			for (PeritoExp peritoExp : peritos.keySet()) {
				Boolean value = peritos.get(peritoExp);
				if (value != null && value) {
					Interviniente interviniente = createInterviniente(peritoExp);
					IntervinienteExp newIntervinienteExp = new IntervinienteExp();
					newIntervinienteExp.setInterviniente(interviniente);
					newIntervinienteExp.setTipoInterviniente(getDefaultTipoIntervinienteActor());
					
					newIntervinienteExp.setCodigoPostal(interviniente.getCodigoPostal());
					newIntervinienteExp.setCorreoElectronico(interviniente.getCorreoElectronico());
					newIntervinienteExp.setDomicilio(interviniente.getDomicilio());
					newIntervinienteExp.setNumeroDomicilio(interviniente.getNumeroDomicilio());
					newIntervinienteExp.setDetalleDomicilio(interviniente.getDetalleDomicilio());
					newIntervinienteExp.setFax(interviniente.getFax());
					newIntervinienteExp.setLocalidad(interviniente.getLocalidad());
					newIntervinienteExp.setProvincia(interviniente.getProvincia());
					newIntervinienteExp.setTelefono(interviniente.getTelefono());
					newIntervinienteExp.setZonaMandamiento(interviniente.getZonaMandamiento());
					newIntervinienteExp.setZonaNotificacion(interviniente.getZonaNotificacion());
					
					boolean ret = CuadernoManager.instance().addIntervinienteSeleccionado(newIntervinienteExp);
					if(ret){
						intervinientesNuevos.add(newIntervinienteExp);
					}
				}
			}
		}
	}

	
	private IntervinienteExp createIntervinienteExp(LetradoIntervinienteExp letradoIntervinienteExp, 
			Interviniente interviniente) {
		
		IntervinienteExp oldIntervinienteExp = letradoIntervinienteExp.getIntervinienteExp();
		IntervinienteExp intervinienteExp = new IntervinienteExp();
		
		intervinienteExp.setDomicilio(letradoIntervinienteExp.getDomicilio());
		intervinienteExp.setLocalidad(letradoIntervinienteExp.getLocalidad());
		intervinienteExp.setTelefono(letradoIntervinienteExp.getTelefono());
		intervinienteExp.setFax(letradoIntervinienteExp.getFax());
		intervinienteExp.setCodCodigoPostal(letradoIntervinienteExp.getCodCodigoPostal());
		intervinienteExp.setProvincia(letradoIntervinienteExp.getProvincia());
		intervinienteExp.setCodZonaMandamiento(letradoIntervinienteExp.getCodZonaMandamiento());
		intervinienteExp.setCodZonaNotificacion(letradoIntervinienteExp.getCodZonaNotificacion());
		intervinienteExp.setZonaMandamiento(letradoIntervinienteExp.getZonaMandamiento());
		intervinienteExp.setZonaNotificacion(letradoIntervinienteExp.getZonaNotificacion());
		intervinienteExp.setNumeroDomicilio(letradoIntervinienteExp.getNumeroDomicilio());
		intervinienteExp.setDetalleDomicilio(letradoIntervinienteExp.getDetalleDomicilio());
		intervinienteExp.setCorreoElectronico(letradoIntervinienteExp.getCorreoElectronico());
		intervinienteExp.setCodigoPostal(letradoIntervinienteExp.getCodigoPostal());
		intervinienteExp.setEstadoInterviniente(null);
		intervinienteExp.setTipoAsignacionInterviniente(null);
		
		//Copio datos del expediente anterior, asi se copian en los intervinientes no letrados
		intervinienteExp.setDependencia(oldIntervinienteExp.getDependencia());
		intervinienteExp.setExpediente(oldIntervinienteExp.getExpediente());
		intervinienteExp.setEstadoExpediente(oldIntervinienteExp.getEstadoExpediente());
		intervinienteExp.setExpedienteLIP(oldIntervinienteExp.getExpedienteLIP());
		intervinienteExp.setTipoInterviniente(getTipoIntervinienteLetradoEnCausaPropia(SessionState.instance().getGlobalCamara(), oldIntervinienteExp.getExpediente().getMateria()));
		
		intervinienteExp.setInterviniente(interviniente);
		return intervinienteExp;
	}

	private Interviniente createInterviniente(Letrado letrado) {
		Interviniente interviniente = new Interviniente();
				
		
		interviniente.setNombre("");
		interviniente.setApellidos(letrado.getNombre());
		interviniente.setNombrePersona("");
		interviniente.setApellidoMaterno("");

		if(letrado.getCuitCuil() != null){
			interviniente.setNumeroDocId(letrado.getCuitCuil());
			interviniente.setTipoDocumentoIdentidad(TipoDocumentoIdentidadSearch.findByCodigo(getEntityManager(), TipoDocumentoIdentidadEnumeration.CUIL_CODE));
			 
		} else {
			interviniente.setNumeroDocId(null);
			interviniente.setTipoDocumentoIdentidad(null);
		}
		interviniente.setNacionalidad(null);
		interviniente.setSexo(null);
		 
		interviniente.setNumeroCBU(letrado.getNumeroCBU());
		 
		LetradoDomicilio domicilio = letrado.getLetradoDomicilio();
		if (domicilio != null) {
			interviniente.setDomicilio(domicilio.getDomicilio());
			interviniente.setNumeroDomicilio("");
			interviniente.setDetalleDomicilio("");
			interviniente.setLocalidad(domicilio.getLocalidad());
			interviniente.setCorreoElectronico(domicilio.getCorreoElectronico());
			interviniente.setTelefono(domicilio.getTelefono());
			interviniente.setFax(domicilio.getFax());
			interviniente.setPersonaJuridica(false);
			interviniente.setCodigoPostal(domicilio.getCodigoPostal());
			interviniente.setProvincia(domicilio.getProvincia());
			interviniente.setZonaMandamiento(domicilio.getZonaMandamiento());
			interviniente.setZonaNotificacion(domicilio.getZonaNotificacion());
			interviniente.setCodZonaNotificacion(domicilio.getCodZonaNotificacion());
			interviniente.setCodZonaMandamiento(domicilio.getCodZonaMandamiento());
		}
		getEntityManager().persist(interviniente);
		getEntityManager().flush();
		return interviniente;
	}

	private Interviniente createInterviniente(PeritoExp peritoExp) {
		String nombre = "?";
		Perito perito = null;
		if(peritoExp.getPeritoProfesion() != null){
			perito = peritoExp.getPeritoProfesion().getPerito();
		}
		if(perito != null){
			nombre = perito.getNombre();
		}
		
		Interviniente interviniente = new Interviniente();
				
		
		interviniente.setNombre("");
		interviniente.setApellidos("");
		interviniente.setNombrePersona("");
		interviniente.setApellidoMaterno("");

		if(perito != null){
			interviniente.setApellidos(nombre);
			if(perito.getCuitCuil() != null){
				interviniente.setNumeroDocId(perito.getCuitCuil());
				interviniente.setTipoDocumentoIdentidad(TipoDocumentoIdentidadSearch.findByCodigo(getEntityManager(), TipoDocumentoIdentidadEnumeration.CUIL_CODE));
			}else{			
				interviniente.setNumeroDocId(null);
				interviniente.setTipoDocumentoIdentidad(null);
			}
			interviniente.setNumeroCBU(perito.getNumeroCBU());
		   	interviniente.setDomicilio(perito.getDomicilio());
		   	interviniente.setNumeroDomicilio("");
		   	interviniente.setDetalleDomicilio("");
		   	interviniente.setLocalidad(perito.getLocalidad());
		   	interviniente.setCorreoElectronico(perito.getCorreoElectronico());
		   	interviniente.setTelefono(perito.getTelefono());
		   	interviniente.setFax(perito.getFax());
		   	interviniente.setPersonaJuridica(false);
			interviniente.setCodigoPostal(perito.getCodigoPostal());
		   	interviniente.setProvincia(perito.getProvincia());
			interviniente.setZonaMandamiento(perito.getZonaMandamiento());
		   	interviniente.setZonaNotificacion(perito.getZonaNotificacion());
			interviniente.setCodZonaNotificacion(perito.getCodZonaNotificacion());
		   	interviniente.setCodZonaMandamiento(perito.getCodZonaMandamiento());
		}
		
		getEntityManager().persist(interviniente);
		getEntityManager().flush();
		return interviniente;
	}

	
	public TipoInterviniente getTipoIntervinienteLetradoEnCausaPropia(Camara camara, Materia materia) {
		Integer idTipoInterviniente = ConfiguracionMateriaManager.instance().getIdTipoIntervinienteLetradoEnCausaPropia(camara, materia);
		if(idTipoInterviniente != null){
			return getEntityManager().find(TipoInterviniente.class, idTipoInterviniente);
		} else {
			return getDefaultTipoIntervinienteActor();
		}
	}
	
	/** ATOS - RECURSO QUEJA **/

	private TipoInterviniente getDefaultTipoIntervinienteActor(){
		List<TipoInterviniente> list = TipoIntervinienteEnumeration.instance().items(TipoIntervinienteEnumeration.ACTOR_ORDEN);
		TipoInterviniente ret = null;	
		for(TipoInterviniente item: list){
			if("ACTOR".equals(item.getDescripcion())){
				ret = item;
			}
			if(ret == null && "ACTOR/ES".equals(item.getDescripcion())){
				ret = item;
			}				
		}
		if (ret == null) {
			if (list.size() > 0) {
				ret = list.get(0);
			}
		}
		return ret;
	}
	
	private Map<IntervinienteExp, Boolean> copiaSeleccionados(Map<IntervinienteExp, Boolean> map) {
		Map<IntervinienteExp, Boolean> copia = new HashMap<IntervinienteExp, Boolean>();
		if(map != null){
			for(IntervinienteExp intervinienteExp: map.keySet()) {
				map.put(intervinienteExp, map.get(intervinienteExp));
			}			
		}
		return copia;
	}

	// Busca un tipoSubexpediente con naturaleza Recurso de Queja
	private TipoSubexpediente getTipoSubexpedienteParaRecursoQueja(Materia materia) {
		TipoSubexpediente tipoSubexpediente = TipoSubexpedienteEnumeration.calculateTipoSubexpediente(getEntityManager(), NaturalezaSubexpedienteEnumeration.Type.recursoQueja, materia);
		if(tipoSubexpediente == null){
			tipoSubexpediente = TipoSubexpedienteEnumeration.calculateTipoSubexpediente(getEntityManager(), NaturalezaSubexpedienteEnumeration.Type.incidente, materia);
		}
		if(tipoSubexpediente == null){
			tipoSubexpediente = TipoSubexpedienteEnumeration.calculateTipoSubexpediente(getEntityManager(), NaturalezaSubexpedienteEnumeration.Type.subexpediente, materia);
		}
		return tipoSubexpediente;
	}

	// Busca un tipoSubexpediente con natruraleza Recurso de Apelacion
	private TipoSubexpediente getTipoSubexpedienteParaRecursoApelacion(Materia materia, int codigo) {
		TipoSubexpediente tipoSubexpediente = TipoSubexpedienteEnumeration.calculateTipoSubexpediente(getEntityManager(), codigo, materia);
		if(tipoSubexpediente == null){
			tipoSubexpediente = TipoSubexpedienteEnumeration.calculateTipoSubexpediente(getEntityManager(), NaturalezaSubexpedienteEnumeration.Type.legajo, materia);
		}
		return tipoSubexpediente;
	}
		
	public boolean isActiveAsignarTribunalOral(Expediente expediente){
		if(Identity.instance().hasPermission("MesaEntrada", "sorteoDirectoTribunalOral") && ExpedienteManager.instance().isEditable()){
			this.expediente = expediente;
			setTipoSorteo(TipoSorteo.tribunalOral);
			return checkPrevioSorteoExpediente(false);
		}else{
			return false;
		}	
	}
	
	public String asignarTribunalOral(Expediente expediente, String page){
		boolean error = false; 
		setTipoSorteo(TipoSorteo.tribunalOral);
		if(isPuedeSortearExpediente()){
			try {
				Oficina oficinaActual = OficinaManager.instance().getOficinaActual();
				asignarOficina(oficinaActual);
			} catch (Lex100Exception e) {
				errorMsg("expedienteMeAsignacion.error.asignarExpediente", e.getMessage());
				error = true;
			}
		}else{
			error = true;
		}
		
		return error ? null : page;
		
	}

	public Sorteo getSorteoSinRubro(Oficina oficinaSorteadora, Competencia competencia) throws Lex100Exception {
		if (sorteo == null) {
			RubrosInfo rubrosInfo = calculaRubrosAsignacion(expediente, getTipoRadicacion());
			sorteo = mesaSorteo.buscaSorteo(expediente, getTipoRadicacion(), oficinaSorteadora, false, competencia, rubrosInfo.getRubroAsignacion(), rubrosInfo.getRubro(), true);
		}
		return sorteo;
	}
	
	public void setSorteo(Sorteo sorteo) {
		this.sorteo = sorteo;
	}

	public void onChangeCompetencia(ValueChangeEvent event) {
		setSorteo(null);
		if (event.getNewValue() != null) {
			setCompetencia((Competencia)event.getNewValue());
		}
		initOficinasExcluidas();
	}
	
	public void onChangeTipoRecurso(ValueChangeEvent event) {
		setSorteo(null);
	}
	
	public void asignarOficina(Oficina oficinaSorteadora) throws Lex100Exception{

		getExpediente().setStatusSorteo(null);

		RubrosInfo rubrosInfo = calculaRubrosAsignacion(expediente, getTipoRadicacion());
		Sorteo sorteo = getSorteoSinRubro(oficinaSorteadora, getCompetencia());
		
		setNecesitaSorteo(false);
		if(isRadicacionPrevia() && getOficinaDestino() != null) {
		 	TipoAsignacionEnumeration.Type tipoAsignacion = TipoAsignacionEnumeration.getTipoAsignacionRadicacionPrevia(getTipoRadicacion());
		 	try {
				new AccionCambioAsignacion().doAccion(SessionState.instance().getUsername(),getExpediente(),
						recursoEdit.getRecursoSorteo(),
						oficinaDestino,
						getTipoRadicacion(),
						tipoAsignacion,
						TipoAsignacionLex100Enumeration.Type.radicacionPrevia,
						SessionState.instance().getGlobalOficina(),
						getTipoGiro(getExpediente(), getTipoRadicacion()),
						puedeCompensarPorRadicacionPreviaAsignadaAMano(getTipoRadicacion()),
						sorteo,
						rubrosInfo.getRubro(),
						getCodigoTipoCambioAsignacion(),
						getDescripcionCambioAsignacion(),
						getObservaciones(),
						null,
						null);
			} catch (AccionException e) {
				throw new Lex100Exception(e);
			}
		} else if((isSorteoSecretariaCorte() || isSorteoRecursoQuejaCorte() || isSorteoRecursoSaltoInstanciaCorte() || isSorteoRecursoPresentacionesVarias() || isSorteoRecursoHonorariosCorte()) && getOficinaDestino() != null) {
		 	TipoAsignacionEnumeration.Type tipoAsignacion = TipoAsignacionEnumeration.getTipoAsignacionManual(getTipoRadicacion());
		 	try {
				new AccionCambioAsignacion().doAccion(SessionState.instance().getUsername(),getExpediente(),
						recursoEdit.getRecursoSorteo(),
						oficinaDestino,
						getTipoRadicacion(),
						tipoAsignacion,
						TipoAsignacionLex100Enumeration.Type.manual,
						SessionState.instance().getGlobalOficina(),
						getTipoGiro(getExpediente(), getTipoRadicacion()),
						puedeCompensarPorRadicacionPreviaAsignadaAMano(getTipoRadicacion()),
						sorteo,
						rubrosInfo.getRubro(),
						getCodigoTipoCambioAsignacion(),
						getDescripcionCambioAsignacion(),
						getObservaciones(),
						null,
						null);
			} catch (AccionException e) {
				throw new Lex100Exception(e);
			}
		} else if(isConexidadPorTema() && !getConexidadDeclarada().isEmpty()) {
		 	TipoAsignacionEnumeration.Type tipoAsignacion = TipoAsignacionEnumeration.getTipoAsignacionConexidadDenunciada(getTipoRadicacion(), getConexidadDeclarada());
		 	try {
				new AccionCambioAsignacion().doAccion(SessionState.instance().getUsername(),getExpediente(),
						recursoEdit.getRecursoSorteo(),
						getConexidadDeclarada().getOficina(),
						getTipoRadicacion(),
						tipoAsignacion,
						TipoAsignacionLex100Enumeration.Type.conexidadDenunciada,
						SessionState.instance().getGlobalOficina(),
						getTipoGiro(getExpediente(), getTipoRadicacion()),
						true,
						sorteo,
						rubrosInfo.getRubro(),
						getCodigoTipoCambioAsignacion(),
						getDescripcionCambioAsignacion(),
						getObservaciones(),
						null,
						null);
				/**ATOS OVD */ 
				CreateExpedienteAction.addVinculadoConexo(getEntityManager(), getTipoRadicacion(), getExpediente(), getConexidadDeclarada().getExpedienteConexo(), tipoAsignacion, null, null, null);
				/**ATOS OVD */
			} catch (AccionException e) {
				throw new Lex100Exception(e);
			}
 		} else {
			// Conexidad, Sorteo
			boolean found = false;
			if (isBuscaConexidadFamilia()){			
				found = buscaConexidadPorFamilia(getExpediente(), recursoEdit.getRecursoSorteo(), getTipoRadicacion(), getCodigoTipoCambioAsignacion(), getDescripcionCambioAsignacion(), sorteo, rubrosInfo.getRubro(), null);
				if(found){
					getExpediente().setStatusSorteo((Integer)StatusStorteoEnumeration.Type.sorteadoOk.getValue());
					getEntityManager().flush();
				}
			}
			boolean busquedaConexidadAsincrona = isConexidadAsincrona();
			boolean isConexidadInformativa = ConexidadManager.isConexidadInformativa(getTipoRadicacion(), SessionState.instance().getGlobalCamara(), expediente.getMateria());
			boolean asignaPorConexidad = !found && isBuscaConexidad() && !isConexidadInformativa;	// && !ExpedienteManager.instance().isIniciado(getExpediente());
			if (!busquedaConexidadAsincrona) {
				if (asignaPorConexidad) { // Intenta asignar por conexidad
					found = CreateExpedienteAction.buscaConexidadPorCriterios(getEntityManager(), 
							getExpediente(),
							recursoEdit.getRecursoSorteo(),
							getTipoRadicacion(), 
							TipoCambioAsignacionEnumeration.CONEXIDAD_DETECTADA, 
							CreateExpedienteAction.getDescripcionCambioAsignacion(getEntityManager(), TipoCambioAsignacionEnumeration.CONEXIDAD_DETECTADA, getTipoRadicacion(), expediente.getMateria()),
							getTipoGiro(getExpediente(), getTipoRadicacion()),
							CreateExpedienteAction.puedeCompensarPorConexidadAutomatica(expediente, getTipoRadicacion()),
							false,
							sorteo,
							rubrosInfo.getRubro(),
							null,
							true,
							oficinaSorteadora,
							false,
							false,
							null, null, null, null);
				} else 	if (isBuscaConexidad()) {
					boolean pararSiEncontrado = isConexidadPararSiEncontrada(getExpediente());
					ConexidadManager.instance().buscaConexidadPorCriterios(getExpediente(), getTipoRadicacion(), null, getExpediente().getParametroExpedienteList(), pararSiEncontrado, true, false, false);
				}
			}
			if (!found || (busquedaConexidadAsincrona && isBuscaConexidad())) {
				setNecesitaSorteo(!found);

				List<Integer> excludeOficinasIds =  getExcludeOficinaIds(getExpediente());
				SorteoParametros sorteoParametros = mesaSorteo.createSorteoParametros(sorteo, 
						TipoBolilleroSearch.getTipoBolilleroAsignacion(entityManager),
						oficinaSorteadora, 
						getExpediente(),
						recursoEdit.getRecursoSorteo(),
						getTipoRadicacion(), 
						getCodigoTipoCambioAsignacion(), 
						getDescripcionCambioAsignacion(), 
						getTipoGiro(getExpediente(), getTipoRadicacion()), 
						excludeOficinasIds, 
						rubrosInfo.getRubro(),
						null,
						false,
						getObservaciones(),
						true);
//						getObservaciones(excludeOficinasIds));
				if(busquedaConexidadAsincrona) {
					if(isBuscaConexidad()) {
						ConexidadManager.instance().deleteConexidad(expediente);
					}
					sorteoParametros.setBuscaConexidad(isBuscaConexidad());
					sorteoParametros.setAsignaPorConexidad(asignaPorConexidad);
				}
				sorteoParametros.setSortea(!found);
				
				mesaSorteo.enviarAColaDeSorteo(sorteoParametros);
			}
		}
	}
	

//	private String getObservaciones(List<Integer> excludeOficinasIds) {
//		StringBuffer observaciones = null;
//		if (excludeOficinasIds != null) {
//			for(Integer idOficina: excludeOficinasIds) {
//				Oficina oficina = getEntityManager().find(Oficina.class, idOficina);
//				if(oficina != null) {
//					if (observaciones == null) {
//						observaciones = new StringBuffer();
//						observaciones.append(getMessages().get("expedienteMeAsignacion.oficinasExcluidas"));
//					} else {
//						observaciones.append(", ");
//					}
//					observaciones.append(oficina.getCodigoNumerico());
//				}
//			}
//		}
//		return (observaciones != null)? observaciones.toString(): null;
//	}


	public boolean isConexidadPararSiEncontrada(Expediente expediente) {
		return ConfiguracionMateriaManager.instance().isConexidadPararSiEncontrada(SessionState.instance().getGlobalCamara(), expediente.getMateria());
	}

	public boolean isBuscaConexidad() {
		return ConexidadManager.isBuscaConexidad(getTipoRadicacion(), SessionState.instance().getGlobalCamara(), getExpediente().getMateria());
	}

	public boolean isBuscaConexidadFamilia(){
		return isTipoRadicacionSala() || isTipoRadicacionTribunalOral() || isTipoRadicacionSalaCasacion();
	}

	public void initOficinasExcluidas() {
		resetOficinasExcluidas();
		try {
			if (getExpediente() != null) {
				if (isSorteoJuzgadoSentencia() || isSorteoJuzgadoPlenario()) {	// excluye juzgado instruccion
					excluirOficina(getSorteoSinRubro(SessionState.instance().getGlobalOficina(), getCompetencia()), getJuzgadoRadicacion(getExpediente()));
				} else if (isSorteoAnySala() && !isSorteoSala()) {	// excluye sala origen
					excluirOficina(getSorteoSinRubro(SessionState.instance().getGlobalOficina(), getCompetencia()), getSalaRadicacion(getExpediente()));
				}
			}
		} catch (Lex100Exception e) {
		}
	}

	public void refreshExpedienteResultado() {
		List<Expediente> familia = FamiliaManager.getFamiliaDirectaExpediente(expedienteHome.getEntityManager(), getExpediente());
		for (Expediente expediente:familia) {
			refreshExpedienteResultado(expediente);
		}
		expedienteList.doSelection(getExpediente(), null);
		this.forzeRefresh = false;
	}

	public void refreshExpedienteResultado(Expediente expediente) {
		if(expediente != null) {
			RecursoExp recursoExp = recursoEdit.getRecursoSorteo();
			if(!getEntityManager().contains(expediente)){
				expediente = getEntityManager().find(Expediente.class, expediente.getId());
			}
			getEntityManager().refresh(expediente);
			if(recursoExp != null) {
				if(!getEntityManager().contains(recursoExp)){
					recursoExp = getEntityManager().find(RecursoExp.class, recursoExp.getId());
				}
				getEntityManager().refresh(recursoExp);
			}
			expediente.reset();
		}
//		getEntityManager().refresh(expediente.getExpedienteIngreso());
		try{
			getLog().info("Realizando refresh de radicaciones por ASIGNACION del expediente: " + expediente.getClave());
			oficinaAsignacionExpManager.refreshRadicacionExpediente(expediente);
		}catch(Exception e){
			getLog().warn("No se pudo realizar el refresh del expediente en su asignacion");
			e.printStackTrace();
		}
	}
	
	public boolean isPenal() {
		if (getExpediente() != null) {
			return MateriaEnumeration.isAnyPenal(getExpediente().getMateria());
		} else {
			return OficinaManager.instance().isPenal(SessionState.instance().getGlobalOficina());
		}
	}

	public boolean isNoPenal() {
		if (getExpediente() != null) {
			return MateriaEnumeration.isNoPenal(getExpediente().getMateria());
		} else {
			return OficinaManager.instance().isNoPenal(SessionState.instance().getGlobalOficina());
		}
	}

	public boolean showTipoGiro() {
		return false;
	}
	
	public boolean showTipoRecurso() {
		return (tipoSorteo == TipoSorteo.recursoQueja || tipoSorteo == TipoSorteo.recursoQuejaCasacion || tipoSorteo == TipoSorteo.recursoLegajoEjecucionCasacion || tipoSorteo == TipoSorteo.recursoQuejaCorte || tipoSorteo == TipoSorteo.recursoSaltoInstanciaCorte || tipoSorteo == TipoSorteo.recursoPresentacionesVarias || tipoSorteo == TipoSorteo.recursoHonorariosCorte);
	}
	
	public boolean showResolucionApelada() {
		return CamaraManager.isCamaraActualCorteSuprema();
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoRecurso> getTiposRecursoQueja() {
		if (getExpediente() != null) {
			Query query = getEntityManager().createQuery(TIPOS_RECURSO_MATERIA_STMT+" "+WHERE_RECURSO_TIPO_INGRESO_SALA).
			setParameter("materia", getExpediente().getMateria()).
			setParameter("tipoSala", TipoSalaResolucionRecursoEnumeration.Type.recurso_queja.getValue())
				;
			return query.getResultList();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<TipoRecurso> getTiposRecursoHonorariosCorte() {
		if (getExpediente() != null) {
			Query query = getEntityManager().createQuery(TIPOS_RECURSO_MATERIA_STMT+" "+WHERE_RECURSO_TIPO_INGRESO_SALA).
			setParameter("materia", MateriaEnumeration.instance().getMateriaCorteSuprema()).
			setParameter("tipoSala", TipoSalaResolucionRecursoEnumeration.Type.recurso_honorarios_corte_suprema.getValue().toString());
			return query.getResultList();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoRecurso> getTiposRecursoQuejaCorte() {
		if (getExpediente() != null) {
			Query query = getEntityManager().createQuery(TIPOS_RECURSO_MATERIA_STMT+" "+WHERE_RECURSO_TIPO_INGRESO_SALA).
			setParameter("materia", MateriaEnumeration.instance().getMateriaCorteSuprema()).
			setParameter("tipoSala", TipoSalaResolucionRecursoEnumeration.Type.recurso_queja_corte_suprema.getValue().toString());
			return query.getResultList();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoRecurso> getTiposRecursoSaltoInstanciaCorte() {
		if (getExpediente() != null) {
			Query query = entityManager.createQuery(TIPOS_RECURSO_MATERIA_STMT+" "+WHERE_RECURSO_TIPO_INGRESO_SALA+" "+AND_CODIGO_ALPHA).
			setParameter("materia", MateriaEnumeration.instance().getMateriaCorteSuprema()).
			setParameter("tipoSala", TipoSalaResolucionRecursoEnumeration.Type.recurso_salto_instancia_corte_suprema.getValue().toString()).
			setParameter("codigo", CODIGO_ALPHA_SALTO_INSTANCIA);
			return query.getResultList();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoRecurso> getTiposRecursoPresentacionesVariasRetardo() {
		if (getExpediente() != null) {
			Query query = entityManager.createQuery(TIPOS_RECURSO_MATERIA_STMT+" "+WHERE_RECURSO_TIPO_INGRESO_SALA+" "+AND_CODIGO_ALPHA).
			setParameter("materia", MateriaEnumeration.instance().getMateriaCorteSuprema()).
			setParameter("tipoSala", TipoSalaResolucionRecursoEnumeration.Type.presentaciones_varias_con_retardo_de_justicia.getValue().toString()).
			setParameter("codigo", CODIGO_ALPHA_PRESENTACION_VARIA_RETARDO);
			return query.getResultList();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoRecurso> getTiposRecursoQuejaCasacion() {
		if (getExpediente() != null) {
			Query query = getEntityManager().createQuery(TIPOS_RECURSO_MATERIA_STMT+" "+WHERE_RECURSO_TIPO_INGRESO_SALA).
			setParameter("materia", getExpediente().getMateria()).
			setParameter("tipoSala", TipoSalaResolucionRecursoEnumeration.Type.recurso_queja_casacion.getValue())
				;
			return query.getResultList();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoRecurso> getTiposRecursoSala() {
		if (getExpediente() != null) {
			Query query = getEntityManager().createQuery(TIPOS_RECURSO_MATERIA_STMT+" "+WHERE_RECURSO_TIPO_INGRESO_SALA).
				setParameter("materia", getExpediente().getMateria()).
				setParameter("tipoSala", TipoSalaResolucionRecursoEnumeration.Type.recurso_sala.getValue())
				;
			return query.getResultList();
		}
		return null;
	}

	public List<TipoRecurso> getTiposRecurso() {
		if (tiposRecurso == null) {
			if(tipoSorteo == TipoSorteo.recursoQueja) {
				tiposRecurso = getTiposRecursoQueja();
			} else if(tipoSorteo == TipoSorteo.recursoQuejaCasacion) {
				tiposRecurso = getTiposRecursoQuejaCasacion();
			} else if(tipoSorteo == TipoSorteo.recursoLegajoEjecucionCasacion) {
				tiposRecurso = getTiposRecursoLegajoEjecucionCasacion();
			} else if(tipoSorteo == TipoSorteo.recursoQuejaCorte) {
				tiposRecurso = getTiposRecursoQuejaCorte();
			} else if(tipoSorteo == TipoSorteo.recursoSaltoInstanciaCorte) {
				tiposRecurso = getTiposRecursoSaltoInstanciaCorte();
			} else if(tipoSorteo == TipoSorteo.recursoPresentacionesVarias) {
				tiposRecurso = getTiposRecursoPresentacionesVariasRetardo();
			} else if(tipoSorteo == TipoSorteo.recursoHonorariosCorte) {
				tiposRecurso = getTiposRecursoHonorariosCorte();
			} else {
				tiposRecurso = getTiposRecursoSala();
			}
		}
		return tiposRecurso;
	}

	private List<TipoRecurso> getTiposRecursoLegajoEjecucionCasacion() {
		if (getExpediente() != null) {
			Query query = getEntityManager().createQuery(TIPOS_RECURSO_MATERIA_STMT+" "+WHERE_RECURSO_TIPO_INGRESO_SALA).
			setParameter("materia", getExpediente().getMateria()).
			setParameter("tipoSala", TipoSalaResolucionRecursoEnumeration.Type.recurso_casacion.getValue())
				;
			return query.getResultList();
		}
		return null;
	}

	public void setTiposRecurso(List<TipoRecurso> tiposRecurso) {
		this.tiposRecurso = tiposRecurso;
	}

	public static ExpedienteMeAsignacion instance(){
		return (ExpedienteMeAsignacion) Component.getInstance(ExpedienteMeAsignacion.class, true);
	}

	public void setTipoSorteoJuzgado() {
		tipoSorteo = TipoSorteo.juzgado;
	}
	
	public void setTipoSorteoPerito() {
		tipoSorteo = TipoSorteo.perito;
	}
	
	public void setTipoSorteoJuzgadoMediacion() {
		tipoSorteo = TipoSorteo.juzgadoMediacion;
	}

	public void setTipoSorteoJuzgadoSentencia() {
		tipoSorteo = TipoSorteo.juzgadoSentencia;
	}
	
	public void setTipoSorteoSala() {
		tipoSorteo = TipoSorteo.sala;
	}

	public void setTipoSorteoSalaCasacion() {
		tipoSorteo = TipoSorteo.salaCasacion;
	}

	public void setTipoSorteoSecretariaCorte() {
		tipoSorteo = TipoSorteo.secretariaCorte;
	}
	
	public void setTipoSorteoSalaEstudio() {		
		tipoSorteo = TipoSorteo.salaEstudio;
	}
	
	public void setTipoSorteoSalaRecursoExtraordinario(){
		tipoSorteo = TipoSorteo.salaRecursoExtraordinario;		
	}

	public void setTipoSorteoJuzgadoPlenario() {
		tipoSorteo = TipoSorteo.juzgadoPlenario;
	}

	public void setTipoSorteoTribunalOral() {
		tipoSorteo = TipoSorteo.tribunalOral;
	}
	
	public void setTipoSorteoEjecucionPenal() {
		tipoSorteo = TipoSorteo.juzgadoEjecucion;
	}

	public void setTipoSorteoRecursoQueja() {
		tipoSorteo = TipoSorteo.recursoQueja;
	}

	public void setTipoSorteoRecursoQuejaCasacion() {
		tipoSorteo = TipoSorteo.recursoQuejaCasacion;
	}
	
	public void setTipoSorteoRecursoQuejaCorte() {
		tipoSorteo = TipoSorteo.recursoQuejaCorte;
	}

	public void setTipoSorteoRecursoSaltoInstanciaCorte() {
		tipoSorteo = TipoSorteo.recursoSaltoInstanciaCorte;
	}

	public void setTipoSorteoPVR() {
		tipoSorteo = TipoSorteo.recursoPresentacionesVarias;
	}
	
	public void setTipoSorteoRecursoHonorariosCorte() {
		tipoSorteo = TipoSorteo.recursoHonorariosCorte;
	}
	
	public void setTipoSorteoRecursoLegajoEjecucion() {
		tipoSorteo = TipoSorteo.recursoLegajoEjecucion;
	}
	
	public void setTipoSorteoRecursoLegajoEjecucionCasacion() {
		tipoSorteo = TipoSorteo.recursoLegajoEjecucionCasacion;
	}
	
	public boolean isAmbitoSorteoTodasCamaras() {
		return tipoSorteo == TipoSorteo.recursoQuejaCasacion || 
			tipoSorteo == TipoSorteo.recursoLegajoEjecucionCasacion ||
			tipoSorteo == TipoSorteo.recursoQuejaCorte ||
			tipoSorteo == TipoSorteo.recursoSaltoInstanciaCorte ||
			tipoSorteo == TipoSorteo.recursoPresentacionesVarias ||
			tipoSorteo == TipoSorteo.recursoHonorariosCorte;
	}
	
	
	public boolean isAmbitoSorteoCamara() {
		if((tipoSorteo == null) || (tipoSorteo == TipoSorteo.recursoQueja) || (tipoSorteo == TipoSorteo.recursoLegajoEjecucion)){
			return true;
		}else{
			return false;
		}
	}
	
	private ExpedienteList getExpedienteList() {
		return expedienteList;
	}

//	public void setAmbitoSorteoCamara(boolean ambitoSorteoCamara) {
//		this.ambitoSorteoCamara = ambitoSorteoCamara;
//	}

	public String[] getFiltroNaturaleza() {
		if(tipoSorteo == TipoSorteo.juzgado){
			return filtroNaturalezaPrincipalIncidenteLegajo;
		}else if(isTipoSorteoAnySala(tipoSorteo)){
			return filtroNaturalezaPrincipalIncidenteLegajoQueja;
		}else if(tipoSorteo == TipoSorteo.juzgadoPlenario){
			return filtroNaturalezaPlenario;
		}else if(tipoSorteo == TipoSorteo.juzgadoMediacion){
			return filtroNaturalezaMediacion;
		}else if(tipoSorteo == TipoSorteo.tribunalOral){
			return filtroNaturalezaTribunalOral;
		}else if(tipoSorteo == TipoSorteo.salaCasacion){			
			return filtroNaturalezaCasacion;
		}else if(tipoSorteo == TipoSorteo.recursoQueja){
			return filtroNaturalezaPrincipal;
		}else if(tipoSorteo == TipoSorteo.recursoQuejaCasacion || tipoSorteo == TipoSorteo.recursoLegajoEjecucionCasacion){
			return filtroNaturalezaPrincipalTribunalOralPlenario;
		}else if(tipoSorteo == TipoSorteo.recursoLegajoEjecucion){
			return filtroNaturalezaPrincipal;
		}else if(tipoSorteo == TipoSorteo.secretariaCorte){
			return filtroNaturalezaCorte;
		}else if(tipoSorteo == TipoSorteo.recursoQuejaCorte || tipoSorteo == TipoSorteo.recursoSaltoInstanciaCorte || tipoSorteo == TipoSorteo.recursoPresentacionesVarias){	
			return filtroNaturalezaRecursoQuejaCorte;
		}else if(tipoSorteo == TipoSorteo.perito){	
			return filtroNaturalezaPrincipalIncidenteLegajo;
		}else if(tipoSorteo == TipoSorteo.juzgadoEjecucion){	
			return filtroNaturalezaJuzgadoEjecucion;
		}
		else{
			return filtroNaturalezaPrincipalIncidenteLegajo;
		}
	}

//	public void setFiltroNaturaleza(String[] filtroNaturaleza) {
//		this.filtroNaturaleza = filtroNaturaleza;
//	}

	public TipoSorteo getTipoSorteo() {
		return tipoSorteo;
	}

	public void setTipoSorteo(TipoSorteo tipoSorteo) {
		this.tipoSorteo = tipoSorteo;
	}

	public boolean isSorteoLegajoEjecucion(){
		return TipoSorteo.recursoLegajoEjecucion == getTipoSorteo();
	}

	public boolean isSorteoLegajoEjecucionCasacion(){
		return TipoSorteo.recursoLegajoEjecucionCasacion == getTipoSorteo();
	}
	
	public boolean isSorteoRecursoQueja(){
		return TipoSorteo.recursoQueja == getTipoSorteo();
	}

	public boolean isSorteoRecursoQuejaCorte(){
		return TipoSorteo.recursoQuejaCorte == getTipoSorteo();
	}

	public boolean isSorteoRecursoSaltoInstanciaCorte(){
		return TipoSorteo.recursoSaltoInstanciaCorte == getTipoSorteo();
	}

	public boolean isSorteoRecursoPresentacionesVarias(){
		return TipoSorteo.recursoPresentacionesVarias == getTipoSorteo();
	}
	
	public boolean isSorteoRecursoHonorariosCorte(){
		return TipoSorteo.recursoHonorariosCorte == getTipoSorteo();
	}
	
	public boolean isSorteoJuzgado(){
		return TipoSorteo.juzgado == getTipoSorteo();
	}
	
	public boolean isSorteoPerito(){
		return TipoSorteo.perito == getTipoSorteo();
	}
	
	public boolean isSorteoJuzgadoMediacion(){
		return TipoSorteo.juzgadoMediacion == getTipoSorteo();
	}

	public boolean isSorteoJuzgadoSentencia(){
		return TipoSorteo.juzgadoSentencia == getTipoSorteo();
	}
	
	public boolean isSorteoJuzgadoPlenario(){
		return TipoSorteo.juzgadoPlenario == getTipoSorteo();
	}

	public boolean isSorteoSala(){
		return TipoSorteo.sala == getTipoSorteo();
	}
	
	public boolean isSorteoSalaEstudio(){
		return TipoSorteo.salaEstudio == getTipoSorteo();
	}
	
	public boolean isSorteoAnySala(){
		return TipoSorteo.sala == getTipoSorteo() ||
		TipoSorteo.salaEstudio == getTipoSorteo()||
		TipoSorteo.salaRecursoExtraordinario == getTipoSorteo() ||
		TipoSorteo.salaCasacion == getTipoSorteo() ||
		TipoSorteo.secretariaCorte == getTipoSorteo() 
		;
	}
	
	public boolean isSorteoOrdenCirculacion(){
		return TipoSorteo.ordenCirculacionSala == getTipoSorteo();
	}
	
	public boolean isSorteoNecesitaRecursoSala(){
		return isSorteoSala();
	}

	public boolean isSorteoNecesitaRecursoCasacion(){
		return (isSorteoSalaCasacion());
	}
	
	public boolean isSorteoNecesitaRecursoCorteSuprema(){
		return (isSorteoSecretariaCorte());
	}
	
	public boolean isSorteoSalaRecursoExtraordinario(){
		return TipoSorteo.salaRecursoExtraordinario == getTipoSorteo();
	}

	public boolean isRecursoQueja(){
		return TipoSorteo.recursoQueja == getTipoSorteo();
	}

	public boolean isRecursoQuejaCasacion(){
		return TipoSorteo.recursoQuejaCasacion == getTipoSorteo();
	}
	
	public boolean isRecursoQuejaCorte(){
		return TipoSorteo.recursoQuejaCorte == getTipoSorteo();
	}

	public boolean isRecursoSaltoInstanciaCorte(){
		return TipoSorteo.recursoSaltoInstanciaCorte == getTipoSorteo();
	}

	public boolean isRecursoPresentacionesVarias(){
		return TipoSorteo.recursoPresentacionesVarias == getTipoSorteo();
	}
	
	public boolean isRecursoHonorariosCorte(){
		return TipoSorteo.recursoHonorariosCorte == getTipoSorteo();
	}
	
	public boolean isSorteoTribunalOral(){
		return TipoSorteo.tribunalOral == getTipoSorteo();
	}
	
	public boolean isSorteoJuzgadoEjecucion(){
		return TipoSorteo.juzgadoEjecucion == getTipoSorteo();
	}

	public boolean isSorteoSalaCasacion(){
		return TipoSorteo.salaCasacion == getTipoSorteo();
	}

	public boolean isSorteoSecretariaCorte(){
		return TipoSorteo.secretariaCorte == getTipoSorteo();
	}
	
	public boolean isSorteoRecursoQuejaCasacion(){
		return TipoSorteo.recursoQuejaCasacion == getTipoSorteo();
	}
	
	public Integer getFojas() {
		return fojas;
	}

	public void setFojas(Integer fojas) {
		this.fojas = fojas;
	}

	public Integer getCuerpos() {
		return cuerpos;
	}

	public void setCuerpos(Integer cuerpos) {
		this.cuerpos = cuerpos;
	}

	public boolean isDefensorOficial() {
		return defensorOficial;
	}

	public void setDefensorOficial(boolean defensorOficial) {
		this.defensorOficial = defensorOficial;
	}

	public Integer getHechos() {
		return hechos;
	}

	public void setHechos(Integer hechos) {
		this.hechos = hechos;
	}

	public void setRecurso(RecursoExp recurso) {
		this.recurso = recurso;
	}

	public RecursoExp getRecurso() {
		return recurso;
	}

	public List<Expediente> getFamiliaExpedientes() {
		return familiaExpedientes;
	}

	public void setFamiliaExpedientes(List<Expediente> familiaExpedientes) {
		this.familiaExpedientes = familiaExpedientes;
	}

	private boolean checkPrevioSorteoExpediente(boolean addErrors){
		boolean ret = false;
		String errMsg  = null;
		if(expediente == null){
			//errMsg = "expedienteMeAsignacion.error.expedienteRequerido";	
		}else if(isSorteoNecesitaRecursoSala()){
//			if(expediente.getRadicacionSala() != null){
//				errMsg = "expedienteMeAsignacion.error.expedienteYaTieneRadicacionSala"; 
//			} else
			if(!tieneRecursoSalaAbierto(expediente)){
				errMsg = "expedienteMeAsignacion.error.expedienteNoTieneRecursoAbierto"; 
			}else{
				ret = true;
			}
		}else if(isSorteoNecesitaRecursoCasacion()){
			if(!tieneRecursoCasacionAbierto(expediente)){
				errMsg = "expedienteMeAsignacion.error.expedienteNoTieneRecursoAbierto"; 
			}else{
				ret = true;
			}
		}else if(isSorteoNecesitaRecursoCorteSuprema()){
			if(!tieneRecursoCorteSupremaAbierto(expediente)){
				errMsg = "expedienteMeAsignacion.error.expedienteNoTieneRecursoCorteAbierto"; 
			}else{
				ret = true;
			}
		}else if(isSorteoTribunalOral()){
//			if(expediente.getRadicacionTribunalOral() != null){
//				errMsg = "expedienteMeAsignacion.error.expedienteYaTieneRadicacionTO"; 
//			} else
			if(!isNaturalezaTribunalOral(expediente)){

				errMsg = "expedienteMeAsignacion.error.expedienteNoEsElevacionTO";
				ret = true;
			}else if(expediente.getRadicacionTribunalOral() != null){
				errMsg = "expedienteMeAsignacion.error.expedienteYaTieneRadicacionTO"; 
			}else{
				ret = true;
			}
		}else if(isSorteoJuzgadoEjecucion()){
//			if(expediente.getRadicacionTribunalOral() != null){
//			errMsg = "expedienteMeAsignacion.error.expedienteYaTieneRadicacionTO"; 
//		} else
		if(!isNaturalezaLegajoEjecucion(expediente)){
				errMsg = "expedienteMeAsignacion.error.expedienteNoEsLegajoDeEjecucion";
				ret = true;
			}else if(expediente.getRadicacionJuzgadoEjecucion() != null){
				errMsg = "expedienteMeAsignacion.error.expedienteYaTieneRadicacionJuzgadoEjecucion"; 
			}else{
				ret = true;
			}
		}else if(isSorteoJuzgadoPlenario()){
//			if(expediente.getRadicacionJuzgadoPlenario() != null){
//				errMsg = "expedienteMeAsignacion.error.expedienteYaTieneRadicacionPL"; 
//			} else
			if(!isNaturalezaJuzgadoPlenario(expediente)){
				errorMsg("expedienteMeAsignacion.error.expedienteNoEsElevacionPL");
			}else{
				ret = true;
			}
		}else if(isSorteoJuzgado()){
			ret = true;
		}else if(isSorteoJuzgadoSentencia()){
			ret = true;
		}else if(isSorteoRecursoQuejaCasacion()){
			ret = true;
		}else if(isSorteoLegajoEjecucionCasacion()){
			ret = true;
		}else if(isSorteoRecursoQueja()){
			ret = true;
		}else if(isSorteoLegajoEjecucion()){
			ret = true;
		}else if(isSorteoRecursoQuejaCorte()){
			ret = true;
		}else if(isSorteoRecursoSaltoInstanciaCorte() || isSorteoRecursoPresentacionesVarias()){
			ret = true;
		}else if(isSorteoRecursoHonorariosCorte()){
			ret = true;
		}else if(isSorteoSalaRecursoExtraordinario()){
			if(expediente.getRadicacionSala() == null){
				errorMsg("expedienteMeAsignacion.error.sorteoRecursoExtraordinarioNecesitaRadicacionSala");
			}else{
				ret = true;
			}
		}else if(isSorteoAnySala()){
			ret = true;
		}else if(isSorteoOrdenCirculacion()){
			ret = true;
		}else if (tipoSorteo != null){
			errorMsg("Error tipo de sorteo no soportado");
		}
		if(!ret){
			if(addErrors && errMsg != null){
				errorMsg(errMsg);
			}
		}
		return ret;
	}

	private boolean isCompensaSalaRadicacionPrevia() {
		return ConfiguracionMateriaManager.instance().isCompensaSalaRadicacionPrevia(CamaraManager.getCamaraActual(), getMateria());
	}
	
	private boolean puedeCompensarPorRadicacionPreviaAsignadaAMano(TipoRadicacionEnumeration.Type tipoRadicacion) {
		return ((TipoRadicacionEnumeration.Type.sala == tipoRadicacion) && (isCompensaSalaRadicacionPrevia() || !isRadicacionPrevia())) || TipoRadicacionEnumeration.Type.tribunalOral == tipoRadicacion ;
	}

	public boolean puedeCompensarPorConexidadFamilia(TipoRadicacionEnumeration.Type tipoRadicacion) {
		boolean compensaSalaRadicacionPrevia = isCompensaSalaRadicacionPrevia() || (getRadicacionActual() == null);
		return ((TipoRadicacionEnumeration.Type.sala == tipoRadicacion) && compensaSalaRadicacionPrevia) || TipoRadicacionEnumeration.Type.tribunalOral == tipoRadicacion ;
	}
	
	public boolean isPuedeSortearExpediente() {
		return checkPrevioSorteoExpediente(false);
	}
	
	
	private boolean tieneRecursoSalaAbierto(Expediente expediente) {
		return expediente.isTieneRecursosSalaPteAsignar();
	}

	private boolean tieneRecursoCasacionAbierto(Expediente expediente) {
		return expediente.isTieneRecursosCasacionPteAsignar();
	}
	
	private boolean tieneRecursoCorteSupremaAbierto(Expediente expediente) {
		return expediente.isTieneRecursosCortePteAsignar();
	}
	
	public boolean isNaturalezaJuzgadoPlenario(Expediente instance) {
		return NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue().equals(instance.getTipoSubexpediente().getNaturalezaSubexpediente());
	}

	public boolean isNaturalezaTribunalOral(Expediente instance) {
		return NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue().equals(instance.getTipoSubexpediente().getNaturalezaSubexpediente());
	}

	public boolean isNaturalezaLegajoEjecucion(Expediente instance) {
		return NaturalezaSubexpedienteEnumeration.Type.legajoEjecucion.getValue().equals(instance.getTipoSubexpediente().getNaturalezaSubexpediente());
	}
	
	public String getDescripcionRadicacion(OficinaAsignacionExp oficinaAsignacionExp){
		String ret = null;
		if (oficinaAsignacionExp != null) {
			ret = oficinaAsignacionExp.getDescripcionOficinaRadicacion();
			if(oficinaAsignacionExp.getFiscalia() != null){
				ret += ", Fiscalía Nº "+oficinaAsignacionExp.getFiscalia();
			}
			if(oficinaAsignacionExp.getDefensoria() != null){
				ret += ", Defensoría Nº "+oficinaAsignacionExp.getDefensoria();
			}
			if(oficinaAsignacionExp.getCodigoOrdenCirculacion() != null){
				ret += oficinaAsignacionExpManager.getOrdenCirculacionDetalle(oficinaAsignacionExp);
			}
		}
		return ret;
	}
	
	public String getDescripcionOficinaRadicacion(){
		return getDescripcionRadicacion(getRadicacionActual());
	}

	public OficinaAsignacionExp getRadicacionActual() {
		if (expediente != null && getTipoRadicacion() != null) {
			return OficinaAsignacionExpSearch.findByTipoRadicacion(expedienteHome.getEntityManager(), expediente, (String)getTipoRadicacion().getValue());
		}
		return null;
	}

	public RecursoEdit getRecursoEdit() {
		return recursoEdit;
	}

	public void setRecursoEdit(RecursoEdit recursoEdit) {
		this.recursoEdit = recursoEdit;
	}
	
	public boolean isSorteadoOkExpediente() {
		return getExpediente().getStatusSorteo() == null
				|| StatusStorteoEnumeration.Type.sorteadoOk.getValue().equals(
						getExpediente().getStatusSorteo());
	}

	public boolean isSorteadoErrorExpediente() {
		return StatusStorteoEnumeration.Type.sorteadoError.getValue().equals(
				getExpediente().getStatusSorteo());
	}

	///////////////////////////////
	private RubrosInfo calculaRubrosSala(Expediente expediente, Type tipoRadicacion){
		RubrosInfo rubrosInfo = MesaSorteo.calculaRubroSalaByTipoRadicacion(tipoRadicacion);
		if(rubrosInfo == null){
			if( MateriaEnumeration.isAnyPenal(expediente.getMateria())){
				rubrosInfo = mesaSorteo.calculaRubrosSalaPenal(getTipoRecurso(), getRecursoEdit().isDetenidos(), getRecursoEdit().getRegimenProcesal());
				if (ConfiguracionMateriaManager.instance().isAsignaDerechosHumanos(expediente.getOficinaActual().getCamara()) && 
						expediente.getExpedienteEspecial() != null && expediente.getExpedienteEspecial().isDerechosHumanos()) {
					rubrosInfo.setRubro(MesaSorteo.RUBRO_DERECHOS_HUMANOS);
					rubrosInfo.setRubroAsignacion(null);
				} else if (ConfiguracionMateriaManager.instance().isAdmiteLeyesEspeciales(expediente.getOficinaActual().getCamara()) &&
						ExpedienteManager.isPenal(expediente)) {
					if (expediente.getTipoCausa() != null && MesaSorteo.RUBRO_LEYES_ESPECIALES.equals(expediente.getTipoCausa().getRubro())) {
						rubrosInfo.setRubro(expediente.getTipoCausa().getRubro());
					} else {
						List<Integer> leyesEspecialesList =  ConfiguracionMateriaManager.instance().getLeyesEspeciales(expediente.getOficinaActual().getCamara());
						for (DelitoExpediente delitoExpediente : expediente.getDelitoExpedienteList()) {
							if (delitoExpediente.getDelito() != null && leyesEspecialesList.contains(delitoExpediente.getDelito().getLey())) {
								rubrosInfo.setRubro(MesaSorteo.RUBRO_LEYES_ESPECIALES);
								rubrosInfo.setRubroAsignacion(null);
							}
						}
					}
				}
			}else{			
				rubrosInfo = mesaSorteo.calculaRubrosSalaCivil(expediente, getTipoRecurso());
			}		
		} 
		return  rubrosInfo;
	}
	
	private RubrosInfo calculaRubrosTO() {
		return mesaSorteo.calculaRubrosTO(getRecursoEdit().isDetenidos(), getHechos(), getCuerpos(), isDefensorOficial());
	}
	
	private RubrosInfo calculaRubrosJuzgadoEjecucion() {
		return mesaSorteo.calculaRubrosJuzgadoEjecucion(getRecursoEdit().isDetenidos(), getHechos(), getCuerpos(), isDefensorOficial());
	}
	private RubrosInfo calculaRubrosJuzgado(Expediente expediente) {
		return mesaSorteo.calculaRubrosJuzgado(expediente);
	}

	private RubrosInfo calculaRubrosJuzgadoSentencia(Expediente expediente) {
		return mesaSorteo.calculaRubrosJuzgadoSentencia(expediente);
	}

	public RubrosInfo calculaRubrosAsignacion(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion){
		RubrosInfo rubrosInfo = new RubrosInfo();
		
		if(TipoRadicacionEnumeration.Type.juzgadoPlenario == tipoRadicacion) {
			rubrosInfo.setRubro("C");
		}else if(TipoRadicacionEnumeration.Type.tribunalOral == tipoRadicacion) {
			rubrosInfo = calculaRubrosTO();
		}else if(TipoRadicacionEnumeration.Type.juzgadoEjecucion == tipoRadicacion) {
			rubrosInfo = calculaRubrosJuzgadoEjecucion();
		} else if(TipoRadicacionEnumeration.isAnySala(tipoRadicacion) || 
			TipoRadicacionEnumeration.isRadicacionSalaCasacion(tipoRadicacion)) {			
			rubrosInfo = calculaRubrosSala(expediente, tipoRadicacion);
		} else if(TipoRadicacionEnumeration.Type.juzgadoSentencia == tipoRadicacion){
			rubrosInfo = calculaRubrosJuzgadoSentencia(expediente);
		}else if(TipoRadicacionEnumeration.Type.perito == tipoRadicacion) {
			rubrosInfo.setRubro("P");
		} else if(TipoRadicacionEnumeration.Type.juzgado == tipoRadicacion){
			if (isExpedienteMediacion(expediente)) {
				rubrosInfo = MesaSorteo.calculaRubrosJuzgadoMediacion(expediente);
			} else{
				rubrosInfo = calculaRubrosJuzgado(expediente);
			}
		}
		return rubrosInfo;
	}

	private boolean isExpedienteMediacion(Expediente expediente) {
		return TipoCausaEnumeration.isMediacion(expediente.getTipoCausa());
	}

	public boolean isShowSelectorDeOficinas() {
		return isSorteoJuzgado() || isSorteoJuzgadoSentencia() || isSorteoAnySala() || isSorteoRecursoQueja() || isSorteoTribunalOral() || isSorteoJuzgadoPlenario() || isSorteoSalaCasacion() || isSorteoLegajoEjecucionCasacion() || isSorteoRecursoQuejaCasacion() || isSorteoRecursoQuejaCorte() || isSorteoRecursoSaltoInstanciaCorte() || isSorteoRecursoPresentacionesVarias() ;
	}

	public boolean isRadicacionPrevia() {
		return radicacionPrevia;
	}

	public void setRadicacionPrevia(boolean radicacionPrevia) {
		this.radicacionPrevia = radicacionPrevia;
		if (radicacionPrevia) {
			setConexidadPorTema(false);
		}
	}

	public boolean isAsignacionPorRadicacionPreviaPermitida(){
		if(expediente != null){
			return ConfiguracionMateriaManager.instance().isAsignacionPorRadicacionPrevia(SessionState.instance().getGlobalCamara(), expediente.getMateria());
		} 
		return false;
	}

	public boolean isExcluirOficinas(){
		return isSorteoJuzgadoSentencia() || isSorteoJuzgadoPlenario() || isSorteoSalaEstudio();
	}
	
	public Oficina getOficinaDestino() {
		return oficinaDestino;
	}

	public void setOficinaDestino(Oficina oficinaDestino) {
		this.oficinaDestino = oficinaDestino;
	}

	public boolean showCompetencias(){
		return ExpedienteMeAdd.instance().showCompetencias() && (isSorteoJuzgadoPlenario() || isSorteoJuzgado() || isSorteoTribunalOral());
	}
	
	public boolean showObjervaciones(){
		return isSorteoSalaEstudio();
	}

	public Competencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Competencia competencia) {
		this.competencia = competencia;
	}

	public List<Competencia> getCompetenciasSorteo() {
		if(isSorteoTribunalOral()){
			if(competenciasTo == null){
				List<Competencia> competencias = ExpedienteMeAdd.instance().getCompetenciasPenales();
				competenciasTo = new ArrayList<Competencia>();
				for(Competencia competencia:competencias){
					if(!CompetenciaEnumeration.COMPETENCIA_CORRECCIONAL.equals(competencia.getCodigo())){
						competenciasTo.add(competencia);
					}
				}
			}
			return competenciasTo;
		}else if(isSorteoJuzgadoPlenario()){
				if(competenciasPlenario == null){
					List<Competencia> competencias = ExpedienteMeAdd.instance().getCompetenciasPenales();
					competenciasPlenario = new ArrayList<Competencia>();
					for(Competencia competencia:competencias){
						if(!CompetenciaEnumeration.COMPETENCIA_CRIMINAL.equals(competencia.getCodigo())){
							competenciasPlenario.add(competencia);
						}
					}
				}
				return competenciasPlenario;
		}else{
			return ExpedienteMeAdd.instance().getCompetencias();
		}
	}

	public boolean isNecesitaSorteo() {
		return necesitaSorteo;
	}

	public void setNecesitaSorteo(boolean necesitaSorteo) {
		this.necesitaSorteo = necesitaSorteo;
	}
	
	@Override
	public EntityManager getEntityManager() {
		return (EntityManager) Component.getInstance("entityManager");
	}

	public boolean isVerTodasOficinasDestino() {
		return verTodasOficinasDestino;
	}

	public void setVerTodasOficinasDestino(boolean verTodasOficinasDestino) {
		this.verTodasOficinasDestino = verTodasOficinasDestino;
	}
	
	public boolean isMostrarSelectorObjetoJuicio() {
		return isNoPenal() && isSorteoJuzgado();
	}

	@Override
	public Materia getMateria() {
		if (getExpediente() != null) {
			return getExpediente().getMateria();
		}
		return null;
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
		if ((getObjetoJuicioNuevo() != null) && (tema != null)
				&& !tema.equals(getObjetoJuicioNuevo().getTema())) {
			setObjetoJuicioNuevo(null);
		}
	}
	
	public void lookupObjetoJuicio(String returnPage) {
		initFiltroObjetosJuicio();
		expedienteHome.initializeLookup(objetoJuicioSearch,
				new LookupEntitySelectionListener<ObjetoJuicio>(returnPage,
						ObjetoJuicio.class) {
					public void updateLookup(ObjetoJuicio entity) {
						setObjetoJuicioNuevo(entity);
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
	
	
	private void initFiltroObjetosJuicio() {
		// Search
		objetoJuicioSearch.setMateria(getMateria());
		objetoJuicioSearch.setShowMateriaFilter(getMateria() == null);
		objetoJuicioSearch.setTema(getTema());

		objetoJuicioEnumeration.setMateria(getMateria());
		objetoJuicioEnumeration.setCodigosList(null);
		
		objetoJuicioSearch.setTipoInstancia(getPrimeraInstancia());
		objetoJuicioEnumeration.setTipoInstancia(getPrimeraInstancia());

		objetoJuicioSearch.setAbreviaturaTipoInstancia(null);
		objetoJuicioEnumeration.setAbreviaturaTipoInstancia(null);

		objetoJuicioSearch.updateFilters();
	}
	
	private TipoInstancia getPrimeraInstancia() {
		return TipoInstanciaSearch
				.findByAbreviatura(
						getEntityManager(),
						TipoInstanciaEnumeration.TIPO_INSTANCIA_PRIMERA_INSTANCIA_ABREVIATURA);
	}

	public ObjetoJuicio getObjetoJuicioNuevo() {
		return objetoJuicioNuevo;
	}

	public void setObjetoJuicioNuevo(ObjetoJuicio objetoJuicioNuevo) {
		this.objetoJuicioNuevo = objetoJuicioNuevo;
	}
	
	
	public boolean isUnSoloRecursoParaCirculacion(Expediente expediente){
		List<RecursoExp> list = getListaRecursosParaCirculacion(expediente);
		return list != null && list.size() == 1;
	}

	public RecursoExp getUnSoloRecursoParaCirculacion(Expediente expediente){
		List<RecursoExp> list = getListaRecursosParaCirculacion(expediente);
		if(list != null && list.size() == 1){
			return list.get(0);
		}else{
			return null;
		}
	}
	

	public boolean isMultiplesRecursosASeleccionar(){
		List<RecursoExp> list = getListaRecursosASeleccionar();
		return list != null && list.size() > 1;
	}

	public boolean isMultiplesRecursosParaCirculacion(Expediente expediente){
		if(CamaraManager.isCamaraActualCorteSuprema()){
			return isMultiplesRecursosCorteSortadosAbiertos(expediente);
		}else{
			return isMultiplesRecursosSalaSortadosAbiertos(expediente);
		}
	}
	
	public List<RecursoExp>  getListaRecursosParaCirculacion(Expediente expediente){
		if(listaRecursosParaCirculacion == null){
			List<RecursoExp> recursosSorteados = null;
			if(CamaraManager.isCamaraActualCorteSuprema()){
				recursosSorteados = getListaRecursosCorteSorteadosAbiertos(expediente);
			}else{
				recursosSorteados = getListaRecursosSalaSorteadosAbiertos(expediente);
			}
			List<RecursoExp> otros = getListaRecursosParaCirculacionExpediente(expediente);
			if(recursosSorteados.size() > 0 && otros.size() > 0){
				listaRecursosParaCirculacion = new ArrayList<RecursoExp>(recursosSorteados);
				listaRecursosParaCirculacion.addAll(otros);
			}else{
				listaRecursosParaCirculacion = recursosSorteados.size() > 0 ? recursosSorteados : otros;
			}
		}
		return listaRecursosParaCirculacion;
	}
	
	public boolean isMultiplesRecursosSalaSortadosAbiertos(Expediente expediente){
		List<RecursoExp> list = recursoEdit.getListaRecursosSalaASeleccionar(expediente, true);
		return list != null && list.size() > 1;
	}
	
	
	public List<RecursoExp>  getListaRecursosParaCirculacionExpediente(Expediente expediente){
		return recursoEdit.getListaRecursosParaCirculacionExpediente(expediente);
	}

	public List<RecursoExp>  getListaRecursosSalaSorteadosAbiertos(Expediente expediente){
		return recursoEdit.getListaRecursosSalaASeleccionar(expediente, true);
	}

	public boolean isMultiplesRecursosCorteSortadosAbiertos(Expediente expediente){
		List<RecursoExp> list = recursoEdit.getListaRecursosCorteASeleccionar(expediente, true);
		return list != null && list.size() > 1;
	}

	public List<RecursoExp>  getListaRecursosCorteSorteadosAbiertos(Expediente expediente){
		return recursoEdit.getListaRecursosCorteASeleccionar(expediente, true);
	}

	public List<RecursoExp> getListaRecursosASeleccionar(){
		if(isSorteoSala()){
			return recursoEdit.getListaRecursosSalaASeleccionar(getExpediente(), false);
		} else if(isSorteoSalaCasacion()){
			return recursoEdit.getListaRecursosCasacionASeleccionar(getExpediente(), false);
		} else if(isSorteoSecretariaCorte()){
			return recursoEdit.getListaRecursosCorteASeleccionar(getExpediente(), false);
		} else {
			return null;
		}
	}

	public boolean showCamaraInput() {
		return (TipoSorteo.recursoQuejaCasacion.equals(tipoSorteo) ||  
		TipoSorteo.recursoLegajoEjecucionCasacion.equals(tipoSorteo) || 
		TipoSorteo.recursoQuejaCorte.equals(tipoSorteo) || 
		TipoSorteo.recursoSaltoInstanciaCorte.equals(tipoSorteo) || 
		TipoSorteo.recursoPresentacionesVarias.equals(tipoSorteo) ||
		TipoSorteo.recursoHonorariosCorte.equals(tipoSorteo)) ||
		getExpediente() != null;
	}

	public boolean showDetenidos() {
		return !TipoSorteo.recursoQuejaCasacion.equals(tipoSorteo) &&  
		!TipoSorteo.recursoLegajoEjecucionCasacion.equals(tipoSorteo) && 
		!TipoSorteo.salaCasacion.equals(tipoSorteo) &&
		!TipoSorteo.secretariaCorte.equals(tipoSorteo) &&
		!TipoSorteo.recursoQuejaCorte.equals(tipoSorteo) &&
		!TipoSorteo.recursoSaltoInstanciaCorte.equals(tipoSorteo) &&
		!TipoSorteo.recursoPresentacionesVarias.equals(tipoSorteo) &&
		isPenal() &&
		getExpediente() != null;
	}

	public String getAbreviaturaCamaraExpediente() {
		if (this.expediente != null) {
			return this.expediente.getCamara().getAbreviatura();
		}
		return abreviaturaCamaraExpediente;
	}

	public void setAbreviaturaCamaraExpediente(String abreviaturaCamaraExpediente) {
		this.abreviaturaCamaraExpediente = abreviaturaCamaraExpediente;
		if (this.abreviaturaCamaraExpediente != null) {
			this.abreviaturaCamaraExpediente = this.abreviaturaCamaraExpediente.toUpperCase();
		}
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public boolean showTema() {
		return ConfiguracionMateriaManager.instance().isSelectorSubmateria(SessionState.instance().getGlobalCamara(),getMateria());
	}

// ConexidadDeclarada
	
	public List<Oficina> getOficinasConexidadDeclarada() { 
		return getOficinasDestino();
	}

	@Override
	public ObjetoJuicio getObjetoJuicio() {
		if (getExpediente() != null && getExpediente().getObjetoJuicioPrincipal() != null){
			return getExpediente().getObjetoJuicioPrincipal();
		}
		return null;
	}
	
	public boolean showConexidadDeclarada() {
		return isUseConexidadSolicitadaMesa();
	}
	
	public boolean isUseConexidadSolicitadaMesa() {
		if (getObjetoJuicio() != null){
			return getObjetoJuicio().isConexidadSolicitadaMesa();
		}
		return false;
	}

	public boolean isConexidadPorTema() {
		return conexidadPorTema;
	}

	public void setConexidadPorTema(boolean conexidadPorTema) {
		this.conexidadPorTema = conexidadPorTema;
		if (conexidadPorTema) {
			getConexidadDeclarada().setTipoAsignacionConexo((String)TipoConexidadSolicitadaEnumeration.Type.solicitadaPorMesa.getValue());
			setRadicacionPrevia(false);
		} else {
			resetConexidadDeclarada();
		}
	}
	
	@Override
	public boolean hayTiposConexidadSolicitada() {
		return false; //Se elige ConexidadPorTema (solicitada por Mesa) de forma automática
	}

	public boolean isDisableOficinaDestino() {
		return disableOficinaDestino;
	}

	public void setDisableOficinaDestino(boolean disableOficinaDestino) {
		this.disableOficinaDestino = disableOficinaDestino;
	}

	public boolean isSorteandoExpediente() {
		if (getExpediente() != null) {
			if (!Boolean.valueOf(this.sorteando).equals(StatusStorteoEnumeration.Type.sorteando.getValue().equals(
					getExpediente().getStatusSorteo()))) {
				this.forzeRefresh = true;
			}
			this.sorteando = StatusStorteoEnumeration.Type.sorteando.getValue().equals(
				getExpediente().getStatusSorteo());
			return this.sorteando;
		}
		return false;
	}

	public void setTipoProcesoNuevo(TipoProceso tipoProcesoNuevo) {
		this.tipoProcesoNuevo = tipoProcesoNuevo;
	}

	public TipoProceso getTipoProcesoNuevo() {
		if (tipoProcesoNuevo==null)
			return getExpediente().getTipoProceso();
		return tipoProcesoNuevo;
	}
	
	public List<TipoProceso> getTiposProceso(){
		return TipoProcesoEnumeration.instance().getItemsByMateria(getMateria());
	}
	
	public void initRecursoInfo() {
//		oficinaDestino = null;
		if (isTipoSorteoAnyCorte(tipoSorteo)) {
			initSecretariaCorte();
		}
		if (getExpediente() != null) {
			getRecursoEdit().setUrgente(objetoJuicioEnumeration.isUrgente(getExpediente().getObjetoJuicio()));
		}
	}
	
	public boolean isForzeRefresh() {
		return forzeRefresh;
	}

	public boolean isConexidadAsincrona() {
		if (conexidadAsincrona == null) {
			conexidadAsincrona = ConfiguracionMateriaManager.instance().isConexidadAsincrona(SessionState.instance().getGlobalCamara());
		}
		return conexidadAsincrona;
	}

	public void setConexidadAsincrona(Boolean conexidadAsincrona) {
		this.conexidadAsincrona = conexidadAsincrona;
	}

	public boolean isDerechosHumanos() {
		return derechosHumanos;
	}

	public void setDerechosHumanos(boolean derechosHumanos) {
		this.derechosHumanos = derechosHumanos;
	}	
	
}
