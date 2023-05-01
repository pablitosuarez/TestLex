package com.base100.lex100.mesaEntrada.reasignacion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.security.Identity;
import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.component.enumeration.ILabeledEnum;
import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.expand.seam.framework.entity.LookupEntitySelectionListener;
import com.base100.lex100.Lex100Exception;
import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.currentdate.CurrentDateManager;
import com.base100.lex100.component.datasheet.DataSheetEditorManager;
import com.base100.lex100.component.datasheet.IDataSheetProperty;
import com.base100.lex100.component.enumeration.CategoriaTipoAsignacionEnumeration;
import com.base100.lex100.component.enumeration.CodigoTipoOficinaEnumeration;
import com.base100.lex100.component.enumeration.CompetenciaEnumeration;
import com.base100.lex100.component.enumeration.DestinoAsignacionEnumeration;
import com.base100.lex100.component.enumeration.GrupoMateriaEnumeration;
import com.base100.lex100.component.enumeration.MateriaEnumeration;
import com.base100.lex100.component.enumeration.NaturalezaSubexpedienteEnumeration;
import com.base100.lex100.component.enumeration.ObjetoJuicioEnumeration;
import com.base100.lex100.component.enumeration.OficinaDestinoEnumeration;
import com.base100.lex100.component.enumeration.OficinaDestinoOtrasCamarasEnumeration;
import com.base100.lex100.component.enumeration.StatusStorteoEnumeration;
import com.base100.lex100.component.enumeration.TemaEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionLex100Enumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionOficinaEnumeration;
import com.base100.lex100.component.enumeration.TipoBolilleroEnumeration;
import com.base100.lex100.component.enumeration.TipoCambioAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoCausaEnumeration;
import com.base100.lex100.component.enumeration.TipoGiroEnumeration;
import com.base100.lex100.component.enumeration.TipoIncompetenciaInternaEnumeration;
import com.base100.lex100.component.enumeration.TipoInformacionEnumeration;
import com.base100.lex100.component.enumeration.TipoInstanciaEnumeration;
import com.base100.lex100.component.enumeration.TipoProcesoEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration.Type;
import com.base100.lex100.component.enumeration.TipoReasignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoReasignacionTribunalOralEnumeration;
import com.base100.lex100.component.enumeration.TipoRecursoEnumeration;
import com.base100.lex100.component.enumeration.TipoVerificacionMesaEnumeration;
import com.base100.lex100.component.enumeration.TipoVinculadoEnumeration;
import com.base100.lex100.controller.entity.cambioAsignacionExp.CambioAsignacionExpSearch;
import com.base100.lex100.controller.entity.conexidad.ConexidadList;
import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.controller.entity.expediente.ExpedienteList;
import com.base100.lex100.controller.entity.expediente.ExpedienteListState;
import com.base100.lex100.controller.entity.expediente.ExpedienteSearch;
import com.base100.lex100.controller.entity.mediador.MediadorSearch;
import com.base100.lex100.controller.entity.objetoJuicio.ObjetoJuicioSearch;
import com.base100.lex100.controller.entity.oficinaAsignacionExp.OficinaAsignacionExpSearch;
import com.base100.lex100.controller.entity.parametro.ParametroSearch;
import com.base100.lex100.controller.entity.parametroExpediente.ParametroExpedienteSearch;
import com.base100.lex100.controller.entity.sorteo.SorteoHome;
import com.base100.lex100.controller.entity.tipoBolillero.TipoBolilleroSearch;
import com.base100.lex100.controller.entity.tipoInstancia.TipoInstanciaSearch;
import com.base100.lex100.controller.entity.vinculadosExp.VinculadosConexosExpList;
import com.base100.lex100.controller.entity.vinculadosExp.VinculadosExpList;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.CambioAsignacionExp;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.ConexidadDeclarada;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Mediador;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.OficinaCargaExp;
import com.base100.lex100.entity.OficinaSorteo;
import com.base100.lex100.entity.Parametro;
import com.base100.lex100.entity.PersonalOficina;
import com.base100.lex100.entity.RecursoExp;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.Tema;
import com.base100.lex100.entity.TipoAsignacion;
import com.base100.lex100.entity.TipoBolillero;
import com.base100.lex100.entity.TipoCambioAsignacion;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.TipoInformacion;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoProceso;
import com.base100.lex100.entity.TipoRecurso;
import com.base100.lex100.entity.VinculadosExp;
import com.base100.lex100.manager.AbstractManager;
import com.base100.lex100.manager.accion.AccionCambioAsignacion;
import com.base100.lex100.manager.accion.impl.AccionException;
import com.base100.lex100.manager.actuacionExp.ActuacionExpManager;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.configuracionMateria.ConfiguracionMateriaManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.manager.expediente.OficinaAsignacionExpManager;
import com.base100.lex100.manager.mediador.SorteadorMediador;
import com.base100.lex100.manager.oficina.OficinaManager;
import com.base100.lex100.manager.personalOficina.PersonalOficinaManager;
import com.base100.lex100.mesaEntrada.ExpedienteMeAbstract;
import com.base100.lex100.mesaEntrada.asignacion.TipoSalaEstudio;
import com.base100.lex100.mesaEntrada.asignacion.TipoSorteo;
import com.base100.lex100.mesaEntrada.conexidad.ConexidadManager;
import com.base100.lex100.mesaEntrada.conexidad.FamiliaManager;
import com.base100.lex100.mesaEntrada.ingreso.CreateExpedienteAction;
import com.base100.lex100.mesaEntrada.ingreso.EditExpedienteAction;
import com.base100.lex100.mesaEntrada.ingreso.ExpedienteEspecialEdit;
import com.base100.lex100.mesaEntrada.ingreso.ExpedienteMeAdd;
import com.base100.lex100.mesaEntrada.ingreso.FojasEdit;
import com.base100.lex100.mesaEntrada.ingreso.RecursoEdit;
import com.base100.lex100.mesaEntrada.sorteo.MesaSorteo;
import com.base100.lex100.mesaEntrada.sorteo.RubrosInfo;
import com.base100.lex100.mesaEntrada.sorteo.SorteoOperation;
import com.base100.lex100.mesaEntrada.sorteo.SorteoParametros;


@Name("expedienteMeReasignacion")
@Scope(ScopeType.CONVERSATION)
public class ExpedienteMeReasignacion extends ExpedienteMeAbstract
{
	private final int PAGE_1 = 1;
	private final int PAGE_2 = 2;

	private final static String NOT_FOUND = "not_found";
	
	@In(create=true)
	ExpedienteHome expedienteHome;
	@In(create=true)
	ExpedienteSearch expedienteSearch;
	@In(create=true)
	ExpedienteList expedienteList;
	@In(create=true)
	SorteoHome sorteoHome;
	@In(create=true)
	private MesaSorteo mesaSorteo;
	@In(create=true)
	private ObjetoJuicioEnumeration objetoJuicioEnumeration;
	@In(create = true)
	private TemaEnumeration temaEnumeration;
	@In(create = true)
	private ObjetoJuicioSearch objetoJuicioSearch;
	
	private Sorteo sorteo;
	private List<Oficina> oficinasSorteo;
	private boolean competenciaSeleccionadaPorCompetenciaDeMesa;
	
	@In(create=true)
	private OficinaAsignacionExpManager oficinaAsignacionExpManager;
	@In(create=true)
	private OficinaDestinoEnumeration oficinaDestinoEnumeration;
	@In(create=true)
	private OficinaDestinoOtrasCamarasEnumeration oficinaDestinoOtrasCamarasEnumeration;
    //
	private TipoRadicacionEnumeration.Type tipoRadicacion = TipoRadicacionEnumeration.Type.juzgado;

	private TipoGiroEnumeration.Type tipoGiro;

    // Reasignaciones
    private ReasignacionEdit reasignacionEdit;    
    private Oficina oficinaAnterior;
    private Integer fiscaliaAnterior;
    private String rubroFiscaliaAnterior;
    private OficinaAsignacionExp radicacionAnterior;
    private TipoAsignacion tipoAsignacion;
    private Mediador mediadorAnterior;
    private Mediador mediadorNuevo;
    
	private boolean errorExpediente;
    private List<Expediente> familiaExpedientes;
	
    private RecursoEdit recursoEdit;
    private FojasEdit fojasEdit;

    private TipoSorteo tipoSorteo;
    private TipoSalaEstudio tipoSalaEstudio;
    
    private List<Competencia> competenciasTo = null;
    private List<Competencia> competenciasPlenario = null;
    
    
    private boolean forceCompensacion = false;
    
    private boolean incompetenciaCambioOJ = false;
    private ObjetoJuicio objetoJuicioNuevo = null;
    private TipoProceso tipoProcesoNuevo = null;
    private String descripcionObjetoJuicio;
    private ILabeledEnum[] tiposIncompetenciaInterna = null;
    
	private Tema tema;
	private List<Tema> temas;
	private List<OficinaAsignacionExp> radicacionesActuales;
	
	private List<Oficina> vocaliasSalaActuales;
	private Oficina vocaliaSeleccionada;
	
	private Map<Oficina, String> titularVocaliaMap = new HashMap<Oficina, String>();
	private String destinoAsignacion = DestinoAsignacionEnumeration.Type.camaraActual.getValue().toString();
	private boolean nuevoVocal = false;
	private boolean integracionTotal = false;
    
	ConexidadDeclarada conexidadDeclaradaAnterior = null;
	/**ATOS COMERCIAL*/
	private boolean omitirSorteoComercialHabilitado;
	/**ATOS COMERCIAL*/
	public ExpedienteMeReasignacion() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Create
	public void init(){
		reasignacionEdit = new ReasignacionEdit(getTipoRadicacion());
		recursoEdit = new RecursoEdit();
		fojasEdit = new FojasEdit();
		if(!OficinaManager.instance().inAlgunaMesaDeEntrada() || !usuarioPuedeSortear()){
			if(!isNoPenal()){
				if(isSorteoAnySala()){
					reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.oficioSalaPenal);
				}else if(isSorteoJuzgadoPlenario()){
					reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.oficioPlenario);
				}else{
					reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.oficioJuzgadoPenal);
				}
				reasignacionEdit.setTipoReasignacionTribulaOral(TipoReasignacionTribunalOralEnumeration.Type.oficio);
			}else{	
//				if(isSorteoAnySala()){
//					reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.oficioSalaCivil);					
//				}else{
//					reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.oficioJuzgadoCivil);
//				}
			}
		}else{
			Oficina oficinaActual = OficinaManager.instance().getOficinaActual();
			if(oficinaActual.getCompetencia() != null){
				reasignacionEdit.setCompetencia(oficinaActual.getCompetencia());
				competenciaSeleccionadaPorCompetenciaDeMesa = true;
			}
		}
		forceCompensacion = false;
		temas = null;
		destinoAsignacion = DestinoAsignacionEnumeration.Type.camaraActual.getValue().toString();
	}	


	public void recalculateTipoAsignacionPorDefecto(){
		if(OficinaManager.instance().inAlgunaMesaDeEntrada() && usuarioPuedeSortear()){
			if(!isPenal()){
				//probar sin nada
				// reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.sorteo);
			}
		}		
	}

	public static ExpedienteMeReasignacion instance() {
		return (ExpedienteMeReasignacion) Component.getInstance(ExpedienteMeReasignacion.class, true);
		
	}

	public boolean isExpedienteAsignado(){
		// FIXME; como saber si ya ha sorteado-> ? flag en expedienteIngreso = pendienteSorteo?
		return true;
	}
	
	
	public String getLabelOficinaAnteriorRadicacion(){
		if(isTipoSorteoMediador()){
			return "Mediador Anterior";
		}else{
			return "Oficina Anterior";
		}
	}
	
	public String getDescripcionOficinaAnteriorRadicacion(){
		String ret = null;
		if(isTipoSorteoMediador()){
			if(mediadorAnterior != null){
				ret = mediadorAnterior.getMatriculaYNombre();
			}
		}else{
			if(oficinaAnterior != null){
				ret = oficinaAnterior.getDescripcion();
				if(fiscaliaAnterior != null){
					ret += ", Fiscalia Nº " + fiscaliaAnterior;
				}
			}
		}
		return ret;
	}

	public String getLabelOficinaActualRadicacion(){
		if(isTipoSorteoMediador()){
			return "Mediador Actual";
		}else{
			return "Oficina Actual";
		}
	}

	private Mediador getMediador(Expediente expediente) {
		OficinaAsignacionExp radicacion = getRadicacionActual();
		if(radicacion != null && radicacion.getMediador() != null){
			return radicacion.getMediador();
		}
		return null;
	}
	
	public String getDescripcionOficinaActualRadicacion(){
		String ret = null;
		OficinaAsignacionExp radicacion = getRadicacionActual();
		if(isTipoSorteoMediador()){
			if(radicacion != null && radicacion.getMediador() != null)
				ret =  radicacion.getMediador().getMatriculaYNombre();
		}else{
			ret = getDescripcionRadicacion(radicacion);
			if (ret != null)
				ret = TipoRadicacionEnumeration.instance().getLabel(getTipoRadicacion().getValue()) + " - " + ret;
		}
		return (ret != null)? ret: "";
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
		}
		return ret;
	}
	
	public String getDescripcionOficinaRadicacion(){
		return getDescripcionRadicacion(getRadicacionActual());
	}

	public OficinaAsignacionExp getRadicacionActual() {
		if (reasignacionEdit.getExpediente() != null && getTipoRadicacion() != null)
			return OficinaAsignacionExpSearch.findByTipoRadicacion(expedienteHome.getEntityManager(), reasignacionEdit.getExpediente(), (String)getTipoRadicacion().getValue());
		return null;
	}
	
	public OficinaAsignacionExp getRadicacionActualSalaEstudio() {
		if (reasignacionEdit.getExpediente() != null) {
			return OficinaAsignacionExpSearch.findByTipoRadicacion(expedienteHome.getEntityManager(), reasignacionEdit.getExpediente(), TipoRadicacionEnumeration.Type.salaEstudio.getValue().toString());
		}
		return null;
	}

	
	public ReasignacionEdit getReasignacionEdit() {
		return reasignacionEdit;
	}

	public void setReasignacionEdit(ReasignacionEdit reasignacionEdit) {
		this.reasignacionEdit = reasignacionEdit;
	}

//	public void onChangeNumeroExpediente(ValueChangeEvent event) {
//		reasignacionEdit.setNumeroExpediente((Integer)(event.getNewValue()));
//		updateExpediente(expedienteHome.getEntityManager());
//	}
//	
//	public void onChangeAnioExpediente(ValueChangeEvent event) {
//		reasignacionEdit.setAnioExpediente((Integer)(event.getNewValue()));
//		updateExpediente(expedienteHome.getEntityManager());
//	}
	
	public String getCodigoSubexpediente() {
		String codigoSubexpediente = null;
		if (reasignacionEdit.getExpediente() != null) {
			codigoSubexpediente = reasignacionEdit.getExpediente().getCodigoSubexpediente();
			if (Strings.isEmpty(codigoSubexpediente) && (reasignacionEdit.getExpediente().getNumeroSubexpediente() != null) && reasignacionEdit.getExpediente().getNumeroSubexpediente() > 0) {
				codigoSubexpediente = reasignacionEdit.getExpediente().getNumeroSubexpediente().toString();
			}
		}
		return codigoSubexpediente;
	}
	
	public void updateExpediente() {
		Expediente selected = null;
		if (reasignacionEdit.numeroyAnioNotNull()) {
			familiaExpedientes = findExpedientes();
			if (familiaExpedientes.size() == 1) {
				selected = familiaExpedientes.get(0);
			}
		} else {
			familiaExpedientes = null;
		}
		if (selected != null) {
			doSelectExpediente(selected);
		} else {
			reasignacionEdit.setExpediente(null);
		}
	}
	
	private List<Expediente> findExpedientes() {
		expedienteSearch.init();
		if(isTipoSorteoMediador() || isTipoSorteoJuzgadoMediacion()){
			expedienteSearch.initForMediacion(true);
		}else{
			expedienteSearch.initForMediacion(false);
		}
		addExpedienteFilters();
		expedienteSearch.updateFilters();
		return expedienteList.getAllResults();
	}

	public String lookupExpediente(String toPage, String returnPage) {
		expedienteSearch.init();
//		reasignacionEdit.setNumeroExpediente(null);
//		reasignacionEdit.setAnioExpediente(null);
		reasignacionEdit.setExpediente(null);

		addExpedienteFilters();
		expedienteSearch.updateFilters();
		expedienteList.setEjbql(null);
		expedienteSearch.setShowClearSearchButton(false);
		expedienteHome.initializeLookup(expedienteSearch, new LookupEntitySelectionListener<Expediente>(returnPage, Expediente.class) {
			public void updateLookup(Expediente entity) {
				ExpedienteMeReasignacion.instance().doSelectExpediente(entity); //toma el bean de la conv. 
				
			}
		});
		return toPage;
	}
	
	private void initDatosIncompetenciaInterna() {
		if (reasignacionEdit.getExpediente() != null) {
			List<CambioAsignacionExp> cambioAsignacionList =
				getEntityManager().createQuery("from CambioAsignacionExp where expediente = :expediente "+
						"and tipoRadicacion = 'J' order by fechaAsignacion desc")
				.setParameter("expediente", reasignacionEdit.getExpediente())
				.getResultList();
			CambioAsignacionExp radicacionJuzgadoOrigen = null;
			OficinaAsignacionExp radicacionJuzgadoActual = reasignacionEdit.getExpediente().getRadicacionJuzgado();
			Oficina juzgadoActual = null;
			Oficina juzgadoOrigen = null;
			if (radicacionJuzgadoActual != null) {
				if (radicacionJuzgadoActual.getOficinaAnterior() != null) {
					juzgadoOrigen = radicacionJuzgadoActual.getOficinaAnterior();
				} else {
					juzgadoActual = radicacionJuzgadoActual.getOficina();
				}
			}
			if (juzgadoOrigen == null) {
				for (CambioAsignacionExp cambioAsignacion: cambioAsignacionList) {
					if (!cambioAsignacion.getOficina().equals(juzgadoActual)) {
						juzgadoOrigen = cambioAsignacion.getOficina();
						break;
					}
				}
			}
			if ((juzgadoOrigen != null) && !juzgadoOrigen.getCamara().equals(SessionState.instance().getGlobalCamara())) {
				juzgadoOrigen = null;
			}

			TipoIncompetenciaInternaEnumeration tipoIncompetenciaInternaEnumeration =
				(TipoIncompetenciaInternaEnumeration) getComponentInstance(TipoIncompetenciaInternaEnumeration.class);
			reasignacionEdit.setOficinaDestino(juzgadoOrigen);
			if (juzgadoOrigen != null) {
				reasignacionEdit.setTipoIncompetenciaInterna(TipoIncompetenciaInternaEnumeration.Type.juzgadoOrigen);
				tiposIncompetenciaInterna = tipoIncompetenciaInternaEnumeration.getEnumValues();
			} else {
				reasignacionEdit.setTipoIncompetenciaInterna(TipoIncompetenciaInternaEnumeration.Type.sorteo);
				tiposIncompetenciaInterna = tipoIncompetenciaInternaEnumeration.getInicialEnumValues();
			}
		}
	}

	public void doSelectExpediente(Expediente expediente) {
		reasignacionEdit.setNumeroExpediente(null);
		reasignacionEdit.setAnioExpediente(null);
		reasignacionEdit.setExpediente(expediente);
		updateMateria(expediente);
		setTema(null);
		radicacionesActuales = null;
		
		if (expediente != null && !isTipoSorteoCambioTipoCausa()) {
			reasignacionEdit.setTipoCausa(expediente.getTipoCausa());
		} else {
			reasignacionEdit.setTipoCausa(null);
		}
		
		if(isSorteoNecesitaRecurso() && !isTieneRecursosPendientes(expediente)){
			errorMsg("expedienteMeReasignacion.error.expedienteNoTieneRecursoAbierto"); 
		} else if (isTipoSorteoIncompetenciaExternaInformatizada() && isTipoRadicacionSala() && !isPermitirIncompetenciaSegundaConRecursoAbierto() && isTieneRecursosPendientes(expediente)) {
			errorMsg("expedienteMeReasignacion.error.expedienteTieneRecursoAbierto"); 
		}
		prepareSelectorObjetoJuicioTipoCausa(expediente);

		resetRadicacionesFamilia(true);
		if(expediente != null && !isTipoSorteoCambioTipoCausa()){
			if(!competenciaSeleccionadaPorCompetenciaDeMesa){ // prevalece la de la mesa
				if(expediente != null){
					reasignacionEdit.setCompetencia(ExpedienteManager.getCompetencia(expediente));
				}
			}
			if(!isTipoSorteoMediador()){
				if(haSeleccionadoTipoReasignacion() && reasignacionEdit.isNecesitaSorteo()){
					initOficinasExcluidas();
				} else {
					resetOficinasExcluidas();
				}
			}
		} else {
			reasignacionEdit.setCompetencia(null);
		}
		TipoReasignacionEnumeration.instance().reset();
		recalculateTipoAsignacionPorDefecto();
		if (reasignacionEdit.getTipoReasignacion() == TipoReasignacionEnumeration.Type.incompetencia)  {
			initDatosIncompetenciaInterna();
		} else if (isTipoSorteoDesafectacionSalaEstudio()) {
			initDatosDesafectacionSalaEstudio();
		} else if (isTipoSorteoCambioTipoCausa()) {
			initDatosCambioTipoCausa();
		} else if (isTipoSorteoIntegracionSala() || isTipoRadicacionVocaliasCasacion() || isTipoRadicacionVocaliasEstudioCasacion()) {
			initDatosIntegracionSala();
		} else if (isTipoSorteoFinJuzgadoSentencia()) {
			initDatosFinJuzgadoSentencia();
		} else if (isTipoSorteoIncompetenciaExternaInformatizada()) {
			initDatosIncompetenciaExternaInformatizada();
		}
		if (isTipoRadicacionAnySala()) {
			recursoEdit.recalculateExpedienteSala(expediente, soloRecursosAsignados());
		} else if (isTipoRadicacionSalaCasacion()) {
			recursoEdit.recalculateExpedienteCasacion(expediente, true);
		} else if (isTipoRadicacionCorteSuprema()) {
			recursoEdit.recalculateExpedienteCorte(expediente, true);
			try {
				getSorteoSinRubro(SessionState.instance().getGlobalOficina(), getCompetencia(), false, false);
			} catch (Lex100Exception e) {
			}
			if (expediente.getRadicacionCorteSuprema() != null) {
				getReasignacionEdit().setOficinaDestino(expediente.getRadicacionCorteSuprema().getOficinaConSecretaria());
			}
		}
		if(isTipoSorteoMediador()){
			MediadorSearch.instance().setCompetencia(ExpedienteManager.getCompetencia(expediente));
		}
		
	}

	private void prepareSelectorObjetoJuicioTipoCausa(Expediente expediente) {
		if (expediente != null) {
			if((isTipoSorteoIncompetenciaExternaInformatizada() && isTipoRadicacionJuzgado())|| isTipoSorteoRecuperacionIncompetencia()){
				// Busca OJ similar
//				ObjetoJuicio objetoJuicioAnterior = expediente.getObjetoJuicioPrincipal();
//				Camara camaraActual = SessionState.instance().getGlobalCamara();
//				if(objetoJuicioAnterior != null && camaraActual != null) {
//					if (!camaraActual.equals(objetoJuicioAnterior.getCamara())){
//						ObjetoJuicio candidatoEnNuevaCamara = objetoJuicioEnumeration.getItemByCodigo(objetoJuicioAnterior.getCodigo());
//						if(candidatoEnNuevaCamara != null){
//							setObjetoJuicioNuevo(candidatoEnNuevaCamara);
//						}
//					} else {
//						//setObjetoJuicioNuevo(objetoJuicioAnterior);
//					}
//				}
				// Selecciona tipo causa 
				List<TipoCausa> list = getTipoCausaList();
				if(list.size() == 1){
					reasignacionEdit.setTipoCausa(list.get(0));
				}
			}
			if(isMostrarSelectorObjetoJuicio()){
				initFiltroObjetosJuicio(reasignacionEdit.getTipoCausa(), reasignacionEdit.getMateria());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void initDatosIntegracionSala() {
		if (getExpediente() != null) {
			if (isTipoRadicacionVocaliasCasacion()) {
				this.vocaliasSalaActuales = getExpediente().getVocaliasCasacion();
				this.reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.integracionSalaCasacionVocal);
			} else if (isTipoRadicacionVocaliasEstudioCasacion()) {
				this.vocaliasSalaActuales = getExpediente().getVocaliasEstudioCasacion();
				this.reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.integracionSalaCasacionVocalEstudio);
			} else {
				this.vocaliasSalaActuales = getExpediente().getVocaliasSala();
				this.reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.integracionSalaVocal);
			}
		} else {
			this.vocaliasSalaActuales = new ArrayList<Oficina>();
		}
//		this.vocaliasSalaActuales = agregarOpcionNuevoVocal(this.vocaliasSalaActuales); 
		this.titularVocaliaMap.clear();
		this.vocaliaSeleccionada = null;
		if (isTipoRadicacionVocaliasCasacion()) {
			this.reasignacionEdit.setTipoCambioAsignacion(TipoCambioAsignacionEnumeration.getItemByCodigo(getEntityManager(), TipoCambioAsignacionEnumeration.INTEGRACION_CASACION_VOCAL, getInstanciaCasacion()));
		} else if (isTipoRadicacionVocaliasEstudioCasacion()) {
			this.reasignacionEdit.setTipoCambioAsignacion(TipoCambioAsignacionEnumeration.getItemByCodigo(getEntityManager(), TipoCambioAsignacionEnumeration.INTEGRACION_CASACION_VOCAL_ESTUDIO, getInstanciaCasacion()));
		} else {
			this.reasignacionEdit.setTipoCambioAsignacion(TipoCambioAsignacionEnumeration.getItemByCodigo(getEntityManager(), TipoCambioAsignacionEnumeration.INTEGRACION_SALA_VOCAL, getInstanciaApelaciones()));
		}
		try {
			/*******ATOS COMERCIAL*******/
			//Fecha: 26-06-2014
			//Desarrollador:  Luis Suarez
			String sql = "from OficinaSorteo where sorteo = :sorteo and status<> -1 order by ";
			if(CamaraManager.isCamaraActualCOM())
				sql += "oficina";
			else	
				sql += "orden";
			
			Sorteo sorteoSeleccionado = getSorteoSinRubro(null, reasignacionEdit.getCompetencia());
			List<OficinaSorteo> oficinaSorteoList =
				getEntityManager().createQuery(sql)
					.setParameter("sorteo", sorteoSeleccionado)
					.getResultList();
			/*******ATOS COMERCIAL*******/
			List<Oficina> vocaliasSorteoList = new ArrayList<Oficina>();
			Map<Oficina, Boolean> vocaliasSelectionMap = new HashMap<Oficina, Boolean>();
			Map<Oficina, Boolean> vocaliasInhibidasSelectionMap = new HashMap<Oficina, Boolean>();
			Map<Oficina, Boolean> vocaliasRecusadasSelectionMap = new HashMap<Oficina, Boolean>();
			PersonalOficinaManager personalOficinaManager = PersonalOficinaManager.instance();
			for (OficinaSorteo oficinaSorteo : oficinaSorteoList) {
				PersonalOficina juez = personalOficinaManager.getJuez(oficinaSorteo.getOficina());
				if (juez != null) {
					titularVocaliaMap.put(oficinaSorteo.getOficina(), juez.getApellidosNombre());
				} else {
					titularVocaliaMap.put(oficinaSorteo.getOficina(), NOT_FOUND);
				}
				if (!vocaliasSalaActuales.contains(oficinaSorteo.getOficina())) {
					vocaliasSorteoList.add(oficinaSorteo.getOficina());
					List<OficinaCargaExp> bolilleroList = getEntityManager()
						.createQuery("from OficinaCargaExp where sorteo = :sorteo and tipoBolillero.codigo = :codigoTipoBolillero and oficina = :oficina and status <> -1")
						.setParameter("sorteo", sorteoSeleccionado)
						.setParameter("codigoTipoBolillero", TipoBolilleroEnumeration.ASIGNACION)
						.setParameter("oficina", oficinaSorteo.getOficina())
						.getResultList();
					if (bolilleroList.size() > 0 && bolilleroList.get(0).isInhibido()) {
						vocaliasInhibidasSelectionMap.put(oficinaSorteo.getOficina(), true);
						vocaliasSelectionMap.put(oficinaSorteo.getOficina(), true);
					}
					if (OficinaAsignacionExpManager.instance().isRecusada(getExpediente(), oficinaSorteo.getOficina(), getTipoRadicacion().getValue().toString())) {
						vocaliasRecusadasSelectionMap.put(oficinaSorteo.getOficina(), true);
						vocaliasSelectionMap.put(oficinaSorteo.getOficina(), true);
					}
				}
			}
			
			this.reasignacionEdit.setVocaliasSalaSorteo(vocaliasSorteoList);
			this.reasignacionEdit.setVocaliasSalaSorteoSelectionMap(vocaliasSelectionMap);
			this.reasignacionEdit.setVocaliasSalaSorteoInhibidasMap(vocaliasInhibidasSelectionMap);
			this.reasignacionEdit.setVocaliasSalaSorteoRecusadasMap(vocaliasRecusadasSelectionMap);
			setNuevoVocal(false);
			setIntegracionTotal(false);
		} catch (Lex100Exception e) {
			getStatusMessages().addFromResourceBundle("expedienteMeReasignacion.error.buscandoSorteo");
			getLog().error("Error buscando sorteo", e);
		} 
		}

	private void initDatosCambioTipoCausa() {
		if(reasignacionEdit.getExpediente() != null) {
			setObjetoJuicioNuevo(reasignacionEdit.getExpediente().getObjetoJuicioPrincipal());
			ObjetoJuicioEnumeration.instance().setSelected(objetoJuicioNuevo);
		}
		if(getParametrosAsPropertyList() != null) {
			for(IDataSheetProperty property: getParametrosAsPropertyList()) {
				Parametro parametro = ParametroSearch.findByName(getEntityManager(), getObjetoJuicio().getCamara(), property.getName());
				if (parametro != null) {
					List<String> valueList = ParametroExpedienteSearch.getParametroValues(getEntityManager(), parametro, reasignacionEdit.getExpediente().getExpedienteIngreso());
					String values = Configuration.getCommaSeparatedString(valueList);
					if (values != null) {
						DataSheetEditorManager.instance().getInstanceProperties().put(property.getDataSheetPropertyId(), values);
					}
				}
			}
		}
	}

	private void updateMateria(Expediente expediente) {
		if (expediente != null) {
			Materia materia = null;
			if (CamaraManager.isCamaraActualMultiMateria()) {
				if (ExpedienteManager.isPenal(expediente)) {
					materia = getMateriaPenal();
				} else {
					materia = getMateriaCivil();
				}
			} 
			if(materia == null) {
				if (CamaraManager.getCamaraActual().getMateriaList().size() > 0) {
					materia = CamaraManager.getCamaraActual().getMateriaList().get(0);
				}
			}
			reasignacionEdit.setMateria(materia);
		}
	}
	
	
	public Materia getMateriaPenal() { // Devuelve la primera materia Penal de la oficina/camara. Necesario para
		// oficinas multiMateria
		Set<Materia> materias = SessionState.instance().getGlobalMateriaSet();
		for (Materia materia : materias) {
			materia = getEntityManager().find(Materia.class, materia.getId());
			if (MateriaEnumeration.isAbreviaturaPenal(materia.getAbreviatura())) {
				return materia;
			}
		}
		return null;
	}

	public Materia getMateriaCivil() { // Devuelve la primera materia Civil de la oficina/camara. Necesario para
		// oficinas multiMateria
		Set<Materia> materias = SessionState.instance().getGlobalMateriaSet();
		for (Materia materia : materias) {
			materia = getEntityManager().find(Materia.class, materia.getId());
			if (MateriaEnumeration.isAbreviaturaNoPenal(materia.getAbreviatura())) {
				return materia;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void initDatosDesafectacionSalaEstudio() {
		if (reasignacionEdit.getExpediente() != null) {
			List<CambioAsignacionExp> cambioAsignacionList =
				getEntityManager().createQuery("from CambioAsignacionExp where expediente = :expediente "+
						"and tipoRadicacion = 'S' and status != -1 order by fechaAsignacion desc")
				.setParameter("expediente", reasignacionEdit.getExpediente())
				.getResultList();
			OficinaAsignacionExp radicacionSalaActual = reasignacionEdit.getExpediente().getRadicacionSala();
			Oficina salaOrigen = null;
			if (radicacionSalaActual != null) {
				salaOrigen = radicacionSalaActual.getOficina();
			} 
			if (salaOrigen == null) {
				for (CambioAsignacionExp cambioAsignacion: cambioAsignacionList) {
					salaOrigen = cambioAsignacion.getOficina();
					break;
				}
			}
			reasignacionEdit.setOficinaDestino(salaOrigen);
			reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.desafectacionSalaEstudio);
			reasignacionEdit.setCategoriaTipoAsignacion(CategoriaTipoAsignacionEnumeration.Type.desafectacion_de_Sala_de_Estudio.getValue().toString());
		}
	}	
	
	@SuppressWarnings("unchecked")
	private void initDatosFinJuzgadoSentencia() {
		if (reasignacionEdit.getExpediente() != null) {
			List<CambioAsignacionExp> cambioAsignacionList =
				getEntityManager().createQuery("from CambioAsignacionExp where expediente = :expediente "+
						"and tipoRadicacion = 'JS' and status != -1 order by fechaAsignacion desc")
				.setParameter("expediente", reasignacionEdit.getExpediente())
				.getResultList();
			OficinaAsignacionExp radicacionJuzgadoActual = reasignacionEdit.getExpediente().getRadicacionJuzgado();
			Oficina oficinaOrigen = null;
			if (radicacionJuzgadoActual != null) {
				oficinaOrigen = radicacionJuzgadoActual.getOficinaConSecretaria();
			}
			if (oficinaOrigen == null) {
				for (CambioAsignacionExp cambioAsignacion : cambioAsignacionList) {
					oficinaOrigen = cambioAsignacion.getOficinaConSecretaria();
					break;
				}
			}
			reasignacionEdit.setOficinaDestino(oficinaOrigen);
			reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.finJuzgadoSentencia);
			reasignacionEdit.setCategoriaTipoAsignacion(CategoriaTipoAsignacionEnumeration.Type.fin_juzgado_sentecia.getValue().toString());
		}
	}

	private Boolean filterByExpedientesEntramite(){
//		if(!isNoPenal() && isSorteoSala()) {
//			return true;
//		}
		return null;
	}
	
	private void addExpedienteFilters() {
		boolean isMediacion = isTipoSorteoMediador() || isTipoSorteoJuzgadoMediacion();
		expedienteSearch.initForMediacion(isMediacion);

		expedienteSearch.setUseFilterByNumero(false);
		expedienteSearch.setNaturalezaSubexpedienteComb(null);
		//expedienteSearch.setNumero(reasignacionEdit.getNumeroExpediente());
		if (reasignacionEdit.getNumeroExpediente() != null) {
			expedienteSearch.setCodigoBarras(reasignacionEdit.getNumeroExpediente().toString());
		} else {
			expedienteSearch.setNumero(null);
		}
		expedienteSearch.setAnio(reasignacionEdit.getAnioExpediente());
		if (expedienteSearch.getCodigoBarras() != null || expedienteSearch.getAnio() != null || expedienteSearch.getNumero() != null){
			ExpedienteListState.instance().setShowList(true);
		}
		expedienteSearch.setSoloEnTramite(filterByExpedientesEntramite());
		expedienteSearch.setTipoVerificadoMesa((String)TipoVerificacionMesaEnumeration.Type.validados.getValue());
		
		expedienteSearch.setExcluirAdministrativos(true);
		if (isTipoRadicacionSala() ) {
			expedienteSearch.setNaturalezaSubexpediente(null);
		}else if(isTipoRadicacionPlenario()){
			expedienteSearch.setNaturalezaSubexpediente(NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue().toString());
		}else if(isTipoRadicacionTribunalOral()){
			expedienteSearch.setNaturalezaSubexpediente(NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue().toString());
		}
		if (isTipoSorteoIncompetenciaExternaInformatizada()) {
			expedienteSearch.setSoloOtrasCamaras(true);
			if(isTipoRadicacionJuzgado()) {
				expedienteSearch.setNaturalezaSubexpediente(NaturalezaSubexpedienteEnumeration.Type.principal.getValue().toString());
			}
		}
//		if (isTipoSorteoRecuperacionIncompetencia()) {
//			expedienteSearch.setCodigosTipoCausa(TipoCausaEnumeration.getCodigosIncompetenciaExternaInformatizada());
//		}
		expedienteSearch.setAmbitoCamaraDisabled(!isMediacion);
		expedienteSearch.setMostrarTodosExpedientes(isMediacion);

		expedienteSearch.setOcultarPases(!isMediacion);
		expedienteSearch.setOcultarEnRemision(!isMediacion);
	}

	public boolean isDestinoSecretariaEspecial() {
		return (TipoReasignacionEnumeration.Type.secretariaEspecial.equals(reasignacionEdit.getTipoReasignacion()));
	}
	
	public List<Oficina> getOficinasSorteo() {
		List<Oficina> ret = null;
		try {
			if(oficinasSorteo == null){
				Camara camara = SessionState.instance().getGlobalCamara();			
				if(ConfiguracionMateriaManager.instance().isReasignacionOficinasPorRubroYJurisdiccion(camara, reasignacionEdit.getMateria())){
					Oficina filtroOficinaSorteadora = SessionState.instance().getGlobalOficina();			
					calculaOficinasDestinoPorRubro(filtroOficinaSorteadora, reasignacionEdit.getCompetencia());
				}else if(ConfiguracionMateriaManager.instance().isReasignacionOficinasPorRubro(camara, reasignacionEdit.getMateria())){
					// permito cambio de asignacion a otras jurisdiciones
					calculaOficinasDestinoPorRubro(null, reasignacionEdit.getCompetencia());				
				}
			}
			ret = oficinasSorteo;
		} catch (Lex100Exception e) {						
		}
		if(ret == null){ // Expedientes con parametros erroneos de migracion
			ret = getOficinasDestino();
		}
		return ret;
	}

	public List<Oficina> getOficinasDestino() {
		boolean verTodasLasOficinas = reasignacionEdit.isVerTodasOficinasDestino();
		
		Integer tipoOficina = null;
		Integer tipoInstancia = null;

		if (isSorteoFiscaliaTribunalOral()) {
			return oficinaDestinoEnumeration.getFiscaliasTribunalOral();
		}else if (isTipoRadicacionSalaCasacion() ) {
			tipoOficina = (Integer) CodigoTipoOficinaEnumeration.Type.salaApelacion.getValue();
			tipoInstancia = TipoInstanciaEnumeration.TIPO_INSTANCIA_CASACION;
		}else if (isTipoRadicacionCorteSuprema() ) {
			tipoOficina = (Integer) CodigoTipoOficinaEnumeration.Type.secretaria.getValue();
			tipoInstancia = TipoInstanciaEnumeration.TIPO_INSTANCIA_CORTESUPREMA;
		}else if (isTipoRadicacionAnySala() ) {
			if (isDestinoSecretariaEspecial()){
				return oficinaDestinoEnumeration.getSecretariasEspeciales();
			} else {
				tipoOficina = (Integer) CodigoTipoOficinaEnumeration.Type.salaApelacion.getValue();
				tipoInstancia = TipoInstanciaEnumeration.TIPO_INSTANCIA_APELACIONES;
			}
		} else	if (isTipoRadicacionTribunalOral() ) {
			tipoOficina = (Integer) CodigoTipoOficinaEnumeration.Type.tribunalOral.getValue();
			tipoInstancia = TipoInstanciaEnumeration.TIPO_INSTANCIA_TRIBUNAL_ORAL;
		} else {
			tipoOficina = (Integer) CodigoTipoOficinaEnumeration.Type.juzgado.getValue();
			tipoInstancia = TipoInstanciaEnumeration.TIPO_INSTANCIA_PRIMERA_INSTANCIA;
		}
		
		List<Oficina> oficinas = null;
		
		// Si tiene identificaco el sorteo correspondiente y no ha marcado "ver Todas" se ofrecen como oficinas  las del sorteo
		if((tipoInstancia == TipoInstanciaEnumeration.TIPO_INSTANCIA_PRIMERA_INSTANCIA || tipoInstancia == TipoInstanciaEnumeration.TIPO_INSTANCIA_CORTESUPREMA) 
				&& this.sorteo != null && !verTodasLasOficinas){
			oficinas = getOficinas(sorteo);
		}else{
			oficinas = oficinaDestinoEnumeration.getOficinasByTipo(tipoOficina, null, getCompetencia(), null);
			if ((oficinas.size() == 0) || verTodasLasOficinas) {
				oficinas.addAll(oficinaDestinoEnumeration.getOficinasByTipo(
						(Integer) CodigoTipoOficinaEnumeration.Type.secretaria.getValue(),
						tipoInstancia, null, getCompetencia(), null));
			}
		}
		return oficinas;
	}
	
	
	
	public List<Oficina> autocompleteOtrasOficinas(Object patternObject) {
		intiOtrasOficinasEnumeration();
		return oficinaDestinoOtrasCamarasEnumeration.autocomplete(patternObject.toString());
	}

	
	private void intiOtrasOficinasEnumeration(){
		Integer tipoOficina = null;
		Integer tipoInstancia = null;
		
		if (isSorteoFiscaliaTribunalOral()) {
			tipoOficina = (Integer)CodigoTipoOficinaEnumeration.Type.fiscalia.getValue();
			tipoInstancia = (TipoInstanciaEnumeration.TIPO_INSTANCIA_TRIBUNAL_ORAL);
		}else if (isTipoRadicacionSalaCasacion() ) {
			tipoOficina = (Integer) CodigoTipoOficinaEnumeration.Type.salaApelacion.getValue();
			tipoInstancia = TipoInstanciaEnumeration.TIPO_INSTANCIA_CASACION;
		}else if (isTipoRadicacionCorteSuprema() ) {
			tipoOficina = (Integer) CodigoTipoOficinaEnumeration.Type.secretaria.getValue();
			tipoInstancia = TipoInstanciaEnumeration.TIPO_INSTANCIA_CORTESUPREMA;
		}else if (isTipoRadicacionAnySala() ) {
			if (isDestinoSecretariaEspecial()){
				tipoOficina =(Integer)CodigoTipoOficinaEnumeration.Type.secretariaEspecial.getValue();
				tipoInstancia = null;
			} else {
				tipoOficina = (Integer) CodigoTipoOficinaEnumeration.Type.salaApelacion.getValue();
				tipoInstancia = TipoInstanciaEnumeration.TIPO_INSTANCIA_APELACIONES;
			}
		} else	if (isTipoRadicacionTribunalOral() ) {
			tipoOficina = (Integer) CodigoTipoOficinaEnumeration.Type.tribunalOral.getValue();
			tipoInstancia = TipoInstanciaEnumeration.TIPO_INSTANCIA_TRIBUNAL_ORAL;
		} else {
			tipoOficina = (Integer) CodigoTipoOficinaEnumeration.Type.juzgado.getValue();
			tipoInstancia = TipoInstanciaEnumeration.TIPO_INSTANCIA_PRIMERA_INSTANCIA;
		}

		List<Integer> tiposOficinaList = new ArrayList<Integer>();
		tiposOficinaList.add(tipoOficina);
		tiposOficinaList.add((Integer) CodigoTipoOficinaEnumeration.Type.secretaria.getValue());
		oficinaDestinoOtrasCamarasEnumeration.setTiposOficina(tiposOficinaList);
		oficinaDestinoOtrasCamarasEnumeration.setTipoInstancia(tipoInstancia);
		
		if (ExpedienteManager.isPenal(getExpediente())) {
			oficinaDestinoOtrasCamarasEnumeration.setGrupoMateria((String)GrupoMateriaEnumeration.Type.penal.getValue());
		}

	}
			
	public boolean isReasignacionPorBusquedaConexidad(){
		return TipoReasignacionEnumeration.Type.busquedaConexidad.equals(reasignacionEdit.getTipoReasignacion());
	}
	
	public boolean showOmitirSorteo(){
		return isReasignacionPorBusquedaConexidad() && ! isTipoSorteoIncompetenciaExternaInformatizada();
	}
	
	public boolean showExcluirOficinaOrigen(){
		boolean ret = false;
		
		if (reasignacionEdit.isNecesitaSorteo() && !isReasignacionPorBusquedaConexidad()) {
			OficinaAsignacionExp radicacion = getRadicacionActual();
			if (radicacion != null && radicacion.getOficina() != null) {
				Camara camara = SessionState.instance().getGlobalCamara();
				if (camara != null) {
					ret = camara.getId().equals(radicacion.getOficina().getCamara().getId());
				}
			}
		}
		return ret;
	}

	public boolean isExcluirOficinas(){
		return reasignacionEdit.getExpediente() != null && reasignacionEdit.isNecesitaSorteo() && !isTipoSorteoMediador() && !isReasignacionPorBusquedaConexidad();
	}
	
	public Competencia getCompetencia() {
		return reasignacionEdit.getCompetencia();
	}

	public String doSaveFin(int currentPage, String page){
		boolean error = false;
		switch(currentPage)  {
			case PAGE_1:
				error = doSave1("ok") == null;
				break;
			case PAGE_2:
				break;
		}
		if(!error){
			try {			
				checkCambioFojasRecurso();
				checkVocaliasExcluidas();
				checkVocaliaEliminar();
				reasignarOficina(null);
			} catch (Lex100Exception e) {
				errorMsg("expedienteMeReasignacion.error.asignacion", e.getMessage());
				error = true;
			}
		}
		return error ? null : page;
	}

	private void checkVocaliasExcluidas() throws Lex100Exception {
		if (isTipoRadicacionVocalias() || isTipoRadicacionVocaliasCasacion()) {
			boolean found = false;
			for (Oficina vocalia: getReasignacionEdit().getVocaliasSalaSorteo()) {
				found = !getVocaliasExcluidasIds().contains(vocalia.getId());
				if (found) {
					break;
				}
			}
			if (!found) {
				errorMsg("expedienteMeReasignacion.error.noVocalias");
				throw new Lex100Exception();
			}
		}
	}
	
	private void checkVocaliaEliminar() throws Lex100Exception {
		if (isTipoSorteoIntegracionSala() || isTipoSorteoIntegracionSalaCasacion() || isTipoSorteoIntegracionSalaEstudioCasacion()) {
			if (vocaliaSeleccionada == null && !nuevoVocal && !integracionTotal) {
				errorMsg("expedienteMeReasignacion.error.noVocaliaSeleccionada");
				throw new Lex100Exception();
			}
		}
	}

	private void checkCambioFojasRecurso() {
		if(showFojasRecurso() && recursoEdit.getRecursoSorteo() != null){
			recursoEdit.copyFojasToRecurso(recursoEdit.getRecursoSorteo());
			getEntityManager().flush();
		}
	}

	public String doIncompetencia(int currentPage, String page){
		boolean error = false;
		switch(currentPage)  {
			case PAGE_1:
				error = doSave1("ok") == null;
				break;
			case PAGE_2:
				break;
		}
		if(!error){
			try {				 
				String rubroAnterior = null;
				if(isIncompetenciaCambioOJ()){
					ObjetoJuicio objetoJuicioAnterior = doCambioObjetoJuicio(true);
					rubroAnterior = objetoJuicioAnterior != null ? objetoJuicioAnterior.getRubroLetter() : null;
					if ((objetoJuicioNuevo != null) && (objetoJuicioNuevo.getCompetencia() != null)) {
						reasignacionEdit.setCompetencia(objetoJuicioNuevo.getCompetencia());
					}

				}
				reasignarOficina(rubroAnterior);
			} catch (Lex100Exception e) {
				errorMsg("expedienteMeReasignacion.error.asignacion", e.getMessage());
				error = true;
			}
		}
		return error ? null : page;
	}
	
	private RubrosInfo calculateRubrosReasignacion(){
		String rubro = null;
		String rubroAsignacion = null;
		if (isTipoSorteoIntegracionSala() || isTipoRadicacionVocaliasCasacion()) {
			rubro = "VOC";
		} 
		// tiene proridad el rubro de la causa/objeto juicio actual / tipo recurso o en Plenario siempre C (Por ejemplo cuando. se cambia OJ y se resortea)
		if(TipoRadicacionEnumeration.isAnyJuzgado(getTipoRadicacion())
				|| TipoRadicacionEnumeration.isAnySala(getTipoRadicacion())
				|| TipoRadicacionEnumeration.isRadicacionTribunalOral(getTipoRadicacion())
				|| TipoRadicacionEnumeration.isRadicacionSalaCasacion(getTipoRadicacion())
				|| TipoRadicacionEnumeration.isRadicacionCorteSuprema(getTipoRadicacion())
				){
			rubro = mesaSorteo.calculaRubrosAsignacion(reasignacionEdit.getExpediente(), getTipoRadicacion(), null, true).getRubro();
		}
		// si no ha encontrado rubro actual lo toma de la radicacion anterior si la hay
		if(rubro == null){
			if (radicacionAnterior == null && reasignacionEdit.getExpediente() != null){
				radicacionAnterior = findRadicacionExpediente(reasignacionEdit.getExpediente(), getTipoRadicacion());
			}
			if (radicacionAnterior != null) {
				Expediente expediente = reasignacionEdit.getExpediente();
				rubro = radicacionAnterior.getRubro();
			}
		}
		RubrosInfo info = null;
		if(rubro == null){ // expediente migrado o creado erroneamente
			info = mesaSorteo.calculaRubrosAsignacion(reasignacionEdit.getExpediente(), getTipoRadicacion(), null, true);
		}else{
			info = new RubrosInfo();
			info.setRubroAsignacion(rubroAsignacion);
			info.setRubro(rubro);			
		}
		if(info!=null && info.getRubro() == null){
			info.setRubro("A"); // para casos excepcionales que el expediente venga sin rubro
		}
		return info;
	}

	@Transactional
	private void reasignarOficina(String rubroAnterior) throws Lex100Exception{
		
		if (isDestinoSecretariaEspecial()){
			reasignacionSecretariaEspecial();
			return;
		} else if (isTipoSorteoCambioTipoCausa()) {
			cambioTipoCausa();
			return;
		} else if (isTipoSorteoIncompetenciaExternaInformatizada()) {
			incompetenciaExterna();
			borrarRadicacionesOtraCamara(reasignacionEdit.getExpediente(), TipoRadicacionEnumeration.Type.juzgado, false, true);
			if(isTipoRadicacionSala() && (!isPermitirIncompetenciaSegundaConRecursoAbierto() || (recursoEdit.getRecursoSorteo() == null))) {	// Si no tiene recurso no se sortea sala
				return;
			}
			ConexidadManager.instance().deleteConexidad(reasignacionEdit.getExpediente());
		} else if (isTipoSorteoRecuperacionIncompetencia()) {
			incompetenciaExterna();
			ConexidadManager.instance().deleteConexidad(reasignacionEdit.getExpediente());
			restaurarRadicacionesCamaraActual(reasignacionEdit.getExpediente(), true);
			return;
		} else if (isTipoSorteoMediador()){
			if(reasignacionEdit.isAsignacionManualMediador()){
				reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.manual);
			}else{
				reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.sorteo);
			}
			if(reasignacionEdit.getCompetencia() == null){				
				reasignacionEdit.setCompetencia(ExpedienteManager.getCompetencia(reasignacionEdit.getExpediente()));
			}
			radicacionAnterior = OficinaAsignacionExpSearch.findByTipoRadicacion(getEntityManager(), reasignacionEdit.getExpediente(), (String)getTipoRadicacion().getValue());
		}

		if (CamaraManager.isPenal(SessionState.instance().getGlobalCamara())) {
			Materia materiaPenal = getMateriaPenal();
			if (ExpedienteManager.isPenal(reasignacionEdit.getExpediente()) && !reasignacionEdit.getExpediente().getMateria().equals(materiaPenal)) {
				ObjetoJuicio oj = ObjetoJuicioSearch.findFirst(getEntityManager(), new ArrayList<Materia>(Arrays.asList(materiaPenal)));
				if (oj != null) {	//(Se modifica el Objeto de Juicio para que se apliquen los criterios de conexidad de la nueva camara)
					reasignacionEdit.getExpediente().setObjetoJuicioPrincipal(ObjetoJuicioSearch.findFirst(getEntityManager(), new ArrayList<Materia>(Arrays.asList(materiaPenal))));
				}
			}
		}
		
		if (isTipoSorteoIntegracionSala() || isTipoSorteoIntegracionSalaCasacion() || isTipoSorteoIntegracionSalaEstudioCasacion()) {
			recusarExcusarVocaliaSeleccionada();
		} else {
			oficinaAnterior = ExpedienteManager.instance().getOficinaByRadicacion(reasignacionEdit.getExpediente(), getTipoRadicacion());
			if (radicacionAnterior != null) {
				fiscaliaAnterior = radicacionAnterior.getFiscalia();
				rubroFiscaliaAnterior = radicacionAnterior.getRubro();
				mediadorAnterior = radicacionAnterior.getMediador();
			}
		}
		
		RubrosInfo info = calculateRubrosReasignacion();
		reasignacionEdit.getExpediente().setStatusSorteo(null);
		Oficina oficinaSorteadora = SessionState.instance().getGlobalOficina();
		
		boolean enviadoSorteo=false;
		TipoBolillero bolilleroExtra = null;
		if (reasignacionEdit.getTipoReasignacion()!=null && !Strings.isEmpty(reasignacionEdit.getTipoReasignacion().getTipoBolilleroExtra())) {
			bolilleroExtra = TipoBolilleroSearch.findByCodigo(getEntityManager(), reasignacionEdit.getTipoReasignacion().getTipoBolilleroExtra());
		}

		if(reasignacionEdit.isNecesitaSorteo() && !reasignacionEdit.isFijarRadicacion() ){
			Sorteo sorteoSeleccionado = getSorteoSinRubro(oficinaSorteadora, reasignacionEdit.getCompetencia(), CamaraManager.isCamaraActualCOM());
			ajustaRubroAlSorteo(info, sorteoSeleccionado);
			
			boolean found = false;
			if (isBuscaConexidadFamilia() && !reasignacionEdit.getTipoReasignacion().isSorteoPuro()  && !reasignacionEdit.getTipoReasignacion().isBuscaConexidad()){			
				found = buscaConexidadPorFamilia(getExpediente(), null, getTipoRadicacion(), getCodigoTipoCambioAsignacion(), getDescripcionCambioAsignacion(), sorteo, info.getRubro(), showFojasPase()? getFojasEdit(): null);
				if(found){
					getExpediente().setStatusSorteo((Integer)StatusStorteoEnumeration.Type.sorteadoOk.getValue());
				}
			}
			if (!found) {
				List<Integer> excludeOficinasIds = getExcludeOficinaIds(reasignacionEdit.getExpediente());
				SorteoParametros sorteoParametros = mesaSorteo.createSorteoParametros(
						sorteoSeleccionado,
						(bolilleroExtra != null) ? bolilleroExtra: TipoBolilleroSearch.getTipoBolilleroAsignacion(getEntityManager()),
						oficinaSorteadora, 
						reasignacionEdit.getExpediente(),
						recursoEdit.getRecursoSorteo(),
						getTipoRadicacion(), 
						getCodigoTipoCambioAsignacion(), 
						getDescripcionCambioAsignacion(),
						getTipoGiro(reasignacionEdit.getExpediente(), getTipoRadicacion()), 
						excludeOficinasIds, 
						info.getRubro(), 
						null,
						true,
						reasignacionEdit.getObservaciones(),
						false);
				if(showFojasPase()) {
					sorteoParametros.setFojasPase(fojasEdit);
				}
				// puede venir de una incompetencia con cambio de OJ. 
				if(rubroAnterior != null){
					sorteoParametros.setRubroAnterior(rubroAnterior);
				}
				boolean asignada = false;
				if(showConexidadDeclarada()) {
					CreateExpedienteAction.updateConexidadDeclaradaToExpediente(getEntityManager(), reasignacionEdit.getExpediente().getExpedienteIngreso(), getConexidadDeclarada());
					if (EditExpedienteAction.isConexidadDeclaradaChanged(conexidadDeclaradaAnterior, reasignacionEdit.getExpediente())) {
						EditExpedienteAction.conexidadDeclaradaChanged(getEntityManager(), reasignacionEdit.getExpediente(), conexidadDeclaradaAnterior, sorteoParametros.getTipoRadicacion());
					}
					boolean conexidadDenunciadaPrioritaria = CreateExpedienteAction.isConexidadDenunciadaPrioritaria(reasignacionEdit.getExpediente());
					
					if(conexidadDenunciadaPrioritaria && (reasignacionEdit.getExpediente().getConexidadDeclarada() != null)) {
						asignada =	CreateExpedienteAction.buscaConexidadDenunciada(getEntityManager(), 
								reasignacionEdit.getExpediente(),
								sorteoParametros.getRecursoExp(),
								sorteoParametros.getTipoRadicacion(),
								TipoCambioAsignacionEnumeration.CONEXIDAD_SOLICITADA, 
								CreateExpedienteAction.getDescripcionCambioAsignacion(getEntityManager(), TipoCambioAsignacionEnumeration.CONEXIDAD_SOLICITADA, getTipoRadicacion(), reasignacionEdit.getMateria()),
								sorteoParametros.getTipoGiro(), 
								sorteoParametros.getSorteo(), 
								sorteoParametros.getRubro(), 
								true,	// asigna
								sorteoParametros.getOficinaSorteo(),
								false,
								false,
								showFojasPase()? getFojasEdit(): null);
					}
				}
				if (!asignada) {
					if(isTipoSorteoMediador()){
						sorteoParametros.setSorteoOperation(SorteoOperation.sorteoMediador);
					} else {
						if (isReasignacionPorBusquedaConexidad() || isTipoSorteoIncompetenciaExternaInformatizada()) {
							sorteoParametros.setBuscaConexidad(true);
							sorteoParametros.setAsignaPorConexidad(true);
							sorteoParametros.setSortea(!reasignacionEdit.isOmitirSorteo());
						} else {
							sorteoParametros.setBuscaConexidad(isBuscaConexidad());
						}
					}
					mesaSorteo.enviarAColaDeSorteo(sorteoParametros);
					enviadoSorteo = true;
				} else if (isBuscaConexidad()){
					ConexidadManager.instance().buscaConexidadPorCriterios(reasignacionEdit.getExpediente(), getTipoRadicacion(), null,  reasignacionEdit.getExpediente().getParametroExpedienteList(), false, true, false, false);
				}
			}
		} else if (reasignacionEdit.getOficinaDestino() != null || reasignacionEdit.getMediadorDestino() != null) {
			try {
				Date FECHA_ASIGNACION_EN_CURSO = null; //null -> fecha en curso
				Oficina oficinaDestino = reasignacionEdit.getOficinaDestino();
			 	Competencia competenciaDestino = oficinaDestino != null && oficinaDestino.getCompetencia() != null ? oficinaDestino.getCompetencia() : reasignacionEdit.getCompetencia();
			 	if(competenciaDestino == null){
			 		competenciaDestino = reasignacionEdit.getExpediente().getCompetencia();
			 	}
			 	
			 	Sorteo sorteoSeleccionado = null;
			 	if (oficinaDestino != null && oficinaDestino.getCamara().equals(SessionState.instance().getGlobalCamara()) ) {
				 	sorteoSeleccionado = getSorteoSinRubro(oficinaSorteadora, competenciaDestino, false, isSorteoJuzgado()? false: true);
				 	if (sorteoSeleccionado == null) {
				 		sorteoSeleccionado = mesaSorteo.buscaSorteo(reasignacionEdit.getExpediente(), 
								getTipoRadicacion(), 
								oficinaDestino, true,
								competenciaDestino,
								MesaSorteo.ANY_RUBRO, 
								MesaSorteo.ANY_RUBRO, true);
				 	}
			 	}
				
				if (isSorteoFiscaliaTribunalOral()){
					ajustaRubroAlSorteo(info, sorteoSeleccionado);
					cambiaFiscalia(reasignacionEdit.getExpediente(), oficinaDestino, sorteoSeleccionado, fiscaliaAnterior, rubroFiscaliaAnterior, info.getRubro(), true);
				} else if (isTipoSorteoDesafectacionSalaEstudio() || isTipoSorteoFinJuzgadoSentencia()) {					
					OficinaAsignacionExp oficinaAsignacionExp = getRadicacionActual();
					if(oficinaAsignacionExp != null){						
						String codigoCambioAsignacion = reasignacionEdit.getTipoAsignacionCodificada() != null ? reasignacionEdit.getTipoAsignacionCodificada().getCodigo() : null;
						if(isTipoSorteoDesafectacionSalaEstudio()){ 
							ActuacionExpManager.instance().addActuacionFinSalaEstudo(reasignacionEdit.getExpediente(), oficinaAsignacionExp, codigoCambioAsignacion, reasignacionEdit.getObservaciones());
						}else{
							ActuacionExpManager.instance().addActuacionFinJuzgadoSentencia(reasignacionEdit.getExpediente(), oficinaAsignacionExp, codigoCambioAsignacion, reasignacionEdit.getObservaciones());
						}
						getEntityManager().remove(oficinaAsignacionExp);
						getEntityManager().flush();
						//reasignacionEdit.setOficinaDestino(null);
					}
						
					/*
					new AccionCancelarRadicacion().doAccion(getRadicacionActual(), reasignacionEdit.getTipoAsignacionCodificada(), reasignacionEdit.getObservaciones(), false);
					if (reasignacionEdit.getOficinaDestino() != null) {
						new AccionPase().doAccion(reasignacionEdit.getExpediente(), 
								null, 
								reasignacionEdit.getExpediente().getFojas(), 
								1,  
								reasignacionEdit.getExpediente().getCuerpos(), 
								reasignacionEdit.getExpediente().getAgregados(), 
								false, 
								(String)TipoPaseEnumeration.Type.giro.getValue(), 
								reasignacionEdit.getExpediente().getComentarios(), 
								oficinaDestino, 
								null, 
								CurrentDateManager.instance().getCurrentDate(), 
								getTipoGiro(reasignacionEdit.getExpediente(), TipoRadicacionEnumeration.Type.sala), 
								reasignacionEdit.getExpediente().getOficinaActual(), 
								null);
					}
					*/
				}else if (isTipoSorteoMediador()){
					Mediador mediador = reasignacionEdit.getMediadorDestino();
					if(mediador != null){
						ExpedienteManager.instance().cambiarMediador(reasignacionEdit.getExpediente(), oficinaSorteadora, getTipoRadicacion(), mediador, reasignacionEdit.getExpediente().getIdxAnaliticoFirst());
						SorteadorMediador.incrementaCargaMediador(getEntityManager(), mediador, reasignacionEdit.getExpediente(), SessionState.instance().getUsername(), null);
					}
				} else {
				 	TipoAsignacionEnumeration.Type tipoAsignacion = TipoAsignacionEnumeration.getTipoAsignacionManual(getTipoRadicacion());
					ajustaRubroAlSorteo(info, sorteoSeleccionado);
					
					new AccionCambioAsignacion().doAccion(SessionState.instance().getUsername(), 
							reasignacionEdit.getExpediente(), 
							recursoEdit.getRecursoSorteo(),
							oficinaDestino,
							getTipoRadicacion(),
							tipoAsignacion,
							TipoAsignacionLex100Enumeration.Type.manual,
							SessionState.instance().getGlobalOficina(),
							getTipoGiro(reasignacionEdit.getExpediente(), getTipoRadicacion()),
							puedeCompensarSinSorteo(getTipoRadicacion()),
							sorteoSeleccionado,
							info.getRubro(),
							getCodigoTipoCambioAsignacion(),
							getDescripcionCambioAsignacion(),
							reasignacionEdit.getObservaciones(),
							FECHA_ASIGNACION_EN_CURSO,
							fojasEdit);
				}
				if (bolilleroExtra != null) {
					ExpedienteManager.instance().enviarCompensacion(sorteoSeleccionado, bolilleroExtra, reasignacionEdit.getExpediente(), recursoEdit.getRecursoSorteo(), tipoRadicacion, oficinaAnterior, oficinaDestino, rubroAnterior, info.getRubro(), true, false, SessionState.instance().getUsername(), getCodigoTipoCambioAsignacion(), getDescripcionCambioAsignacion(), false);
				}
			} catch (AccionException e) {
				throw new Lex100Exception(e);
			}
		}	
	}

	public boolean isBuscaConexidad() {
		return isReasignacionPorBusquedaConexidad() || 
			( !CamaraManager.getCamaraActual().equals(reasignacionEdit.getExpediente().getCamara()) 
				&&
				ConexidadManager.isBuscaConexidad(getTipoRadicacion(), SessionState.instance().getGlobalCamara(), getExpediente().getMateria()));
	}

//	private void resorteaMediador(ReasignacionEdit reasignacionEdit) {
//		mediadorAnterior = null;
//		OficinaAsignacionExp oficinaAsignacionExp = findRadicacionExpediente(reasignacionEdit.getExpediente(), reasignacionEdit.getTipoRadicacion());
//		if(oficinaAsignacionExp != null){
//			mediadorAnterior = reasignacionEdit.isExcluirOficinaOrigen() ?   oficinaAsignacionExp.getMediador() : null;			
//			mediadorNuevo = SorteadorMediador.sorteaMediador(getEntityManager(), CamaraManager.getCamaraActual(), oficinaAsignacionExp.getOficina().getCompetencia(), mediadorAnterior, reasignacionEdit.getExpediente(), SessionState.instance().getUsername(), null);
//			if(mediadorNuevo != null){
//				ExpedienteManager.instance().cambiarMediador(reasignacionEdit.getExpediente(), SessionState.instance().getGlobalOficina(), reasignacionEdit.getTipoRadicacion(), mediadorNuevo);
//				if(mediadorAnterior != null && mediadorAnterior.equals(mediadorNuevo)){
//					SorteadorMediador.decrementaCargaMediador(getEntityManager(), mediadorAnterior, reasignacionEdit.getExpediente(), SessionState.instance().getUsername(), null);
//				}
//			}
//		}
//		
//	}

	private void recusarExcusarVocaliaSeleccionada() {
		this.oficinaAnterior = this.vocaliaSeleccionada;
		List<Oficina> vocalias;
		List<OficinaAsignacionExp> radicacionesVocalia;
		List<Expediente> expedienteList = new ArrayList<Expediente>();
		OficinaAsignacionExp radicacion;
		if (isTipoSorteoIntegracionSalaCasacion()) {
			vocalias = getExpediente().getVocaliasCasacion();
			radicacion = getExpediente().getRadicacionCasacion();
			radicacionesVocalia = getExpediente().getRadicacionesVocaliaCasacion();
		} else if (isTipoSorteoIntegracionSalaEstudioCasacion()) {
			vocalias = getExpediente().getVocaliasEstudioCasacion();
			radicacion = getExpediente().getRadicacionCasacion();
			radicacionesVocalia = getExpediente().getRadicacionesVocaliaEstudioCasacion();
		} else {
			vocalias = getExpediente().getVocaliasSala();
			radicacion = getExpediente().getRadicacionSala();
			radicacionesVocalia = getExpediente().getRadicacionesVocalia();
		}
		
		expedienteList.add(getExpediente());
		if(!(ExpedienteManager.isExpedienteBase(getExpediente()))){
			expedienteList.add(ExpedienteManager.getExpedienteBase(getEntityManager(), getExpediente()));
		}
		
		if (radicacionesVocalia.size() == 0) {
			
			for(Expediente expediente : expedienteList){	
				System.out.println(expediente.getIdxAnaliticoFirst());
				for (Oficina vocalia: vocalias) {
					System.out.println("-- "+vocalia.getDescripcion());					
					OficinaAsignacionExp oficinaAsignacionExp = new OficinaAsignacionExp(TipoAsignacionOficinaEnumeration.Type.asignacion.getValue().toString(), 
							radicacion.getFechaAsignacion(), expediente);
					oficinaAsignacionExp.setOficina(vocalia);
					oficinaAsignacionExp.setCodigoTipoCambioAsignacion(radicacion.getCodigoTipoCambioAsignacion());
					oficinaAsignacionExp.setOficinaAsignadora(radicacion.getOficinaAsignadora());
					if (isTipoSorteoIntegracionSalaEstudioCasacion()) {
						oficinaAsignacionExp.setRubro("VOE");
					} else {
						oficinaAsignacionExp.setRubro("VOC");
					}
					oficinaAsignacionExp.setTipoAsignacion(radicacion.getTipoAsignacion());
					oficinaAsignacionExp.setTipoAsignacionLex100(radicacion.getTipoAsignacionLex100());
					oficinaAsignacionExp.setTipoAsignacionOficina(radicacion.getTipoAsignacionOficina());
					oficinaAsignacionExp.setTipoRadicacion(getTipoRadicacion().getValue().toString());
					oficinaAsignacionExp.setOperador(radicacion.getOperador());
					getEntityManager().persist(oficinaAsignacionExp);
				}
			}
				getEntityManager().flush();
				getExpediente().setRadicacionesVocalia(null);
				getExpediente().setRadicacionesVocaliaCasacion(null);
				getExpediente().setRadicacionesVocaliaEstudioCasacion(null);
			}
		TipoCambioAsignacion tipoAsignacionRecusacionVocal;
		if (isTipoRadicacionVocaliasCasacion()) {
			tipoAsignacionRecusacionVocal = TipoCambioAsignacionEnumeration.getItemByCodigo(getEntityManager(), TipoCambioAsignacionEnumeration.RECUSACION_EXCUSACION_VOCAL, getInstanciaCasacion(), reasignacionEdit.getMateria());
		} else if (isTipoRadicacionVocaliasEstudioCasacion()) {
			tipoAsignacionRecusacionVocal = TipoCambioAsignacionEnumeration.getItemByCodigo(getEntityManager(), TipoCambioAsignacionEnumeration.RECUSACION_EXCUSACION_VOCAL_ESTUDIO, getInstanciaCasacion(), reasignacionEdit.getMateria());
		} else {
			tipoAsignacionRecusacionVocal = TipoCambioAsignacionEnumeration.getItemByCodigo(getEntityManager(), TipoCambioAsignacionEnumeration.RECUSACION_EXCUSACION_VOCAL, getInstanciaApelaciones(), reasignacionEdit.getMateria());
		}
		String codigo = tipoAsignacionRecusacionVocal.getCodigo();
		String descripcion = tipoAsignacionRecusacionVocal.getDescripcion();
		
		radicacionesVocalia.clear();
		//System.out.println(radicacionesVocalia.size());
		for(Expediente expediente: expedienteList){
			if (isTipoSorteoIntegracionSalaCasacion()) {
				radicacionesVocalia.addAll(expediente.getRadicacionesVocaliaCasacion());
			} else if (isTipoSorteoIntegracionSalaEstudioCasacion()) {
				radicacionesVocalia.addAll(expediente.getRadicacionesVocaliaCasacion());
			} else {
				radicacionesVocalia.addAll(expediente.getRadicacionesVocaliaCasacion());
			}
		}
		for (OficinaAsignacionExp radicacionVocalia: radicacionesVocalia) {
			if (radicacionVocalia.getOficina().equals(this.vocaliaSeleccionada) || this.integracionTotal) {
				System.out.println("eliminacion vocalia de expediente: "+radicacionVocalia.getExpediente().getIdxAnaliticoFirst());
				getEntityManager().remove(radicacionVocalia);
				CambioAsignacionExp cambioAsignacionExp = new CambioAsignacionExp(codigo, radicacionVocalia, null);
				PersonalOficina vocal = PersonalOficinaManager.instance().getJuez(radicacionVocalia.getOficina());
				String detalleRecusacion = CamaraManager.getOficinaPrincipal(radicacionVocalia.getOficina()).getNumero()+" "+radicacionVocalia.getOficina().getNumero();
				if (vocal != null) {
					detalleRecusacion += " "+vocal.getApellidosNombre();
				} else {
					detalleRecusacion += " (vacante)";
				}
				cambioAsignacionExp.setComentarios(descripcion+": "+detalleRecusacion);
				cambioAsignacionExp.setFechaAsignacion(CurrentDateManager.instance().getCurrentDate());
				cambioAsignacionExp.setOficinaAsignadora(SessionState.instance().getGlobalOficina());
				cambioAsignacionExp.setOperador(SessionState.instance().getUsername());
				if (isTipoRadicacionVocaliasEstudioCasacion()) {
					cambioAsignacionExp.setRubro("VOE");
				} else {
					cambioAsignacionExp.setRubro("VOC");
				}
				cambioAsignacionExp.setTipoAsignacion(TipoAsignacionEnumeration.TYPE_MANUAL);
				cambioAsignacionExp.setTipoAsignacionLex100(TipoAsignacionLex100Enumeration.Type.manual.getValue().toString());
				cambioAsignacionExp.setTipoRadicacion(getTipoRadicacion().getValue().toString());
				getEntityManager().persist(cambioAsignacionExp);
				ActuacionExpManager.instance().addActuacionExcusacionIntegracionSalaExpediente(getExpediente(), radicacionVocalia, null, descripcion+": "+detalleRecusacion, codigo);
				getEntityManager().flush();

				getEntityManager().refresh(getExpediente());
			}
		}
	}

	private void borrarRadicacionesOtraCamara(Expediente expediente, TipoRadicacionEnumeration.Type excluirTipoRadicacion, boolean eliminarHistorico, boolean propagar) throws Lex100Exception {
		Camara camara = CamaraManager.getCamaraActual();
		
		List<OficinaAsignacionExp >radicaciones = OficinaAsignacionExpManager.instance().getRadicaciones(expediente);
		for (OficinaAsignacionExp radicacion: radicaciones) {
			if (((excluirTipoRadicacion == null) || !excluirTipoRadicacion.equals(TipoRadicacionEnumeration.instance().getType(radicacion.getTipoRadicacion()))) && !radicacion.getOficina().getCamara().equals(camara)) {
				ExpedienteManager.instance().deleteOficinaAsignacionExp(radicacion, null, null, null, eliminarHistorico, true, false, false);
			}
		}
		if (propagar) {
			if(ExpedienteManager.isExpedienteBase(expediente)){
				List<Expediente> familiaExpediente = ExpedienteManager.getFamiliaDirectaExpediente(getEntityManager(), expediente);
				for(Expediente subexpediente: familiaExpediente){
					if(ExpedienteManager.instance().isAnySubexpedienteNaturaleza(subexpediente)){
						borrarRadicacionesOtraCamara(subexpediente, null, eliminarHistorico, false);	
					}
				}
			}
		}

	}

	private void restaurarRadicacionesCamaraActual(Expediente expediente, boolean propagar) {
		Camara camara = CamaraManager.getCamaraActual();
		for(TipoRadicacionEnumeration.Type tipoRadicacion: TipoRadicacionEnumeration.Type.values()){
			if (TipoRadicacionEnumeration.isAnyJuzgado(tipoRadicacion) ||
				TipoRadicacionEnumeration.isAnySala(tipoRadicacion) ||
				TipoRadicacionEnumeration.isRadicacionTribunalOral(tipoRadicacion) ||
				TipoRadicacionEnumeration.isRadicacionJuzgadoPlenario(tipoRadicacion)) {
				OficinaAsignacionExp radicacion =  findRadicacionExpediente(expediente, tipoRadicacion);
				if(radicacion != null) {
					CambioAsignacionExp cambioAsignacionExp = CambioAsignacionExpSearch.findLastByTipoRadicacionAndCamara(getEntityManager(), expediente, camara, (String)tipoRadicacion.getValue());
					if(cambioAsignacionExp != null) {
						radicacion.updateFrom(cambioAsignacionExp, false);
					} else {
						getEntityManager().remove(radicacion);
					}
				} else {
					CambioAsignacionExp cambioAsignacionExp = CambioAsignacionExpSearch.findLastByTipoRadicacionAndCamara(getEntityManager(), expediente, camara, (String)tipoRadicacion.getValue());
					if(cambioAsignacionExp != null) {
						OficinaAsignacionExp radicacionCamara = new OficinaAsignacionExp(cambioAsignacionExp);
						getEntityManager().persist(radicacionCamara);
					}
				}
			}
		}
		getEntityManager().flush();
		if (propagar) {
			if(ExpedienteManager.isExpedienteBase(expediente)){
				for (OficinaAsignacionExp oficinaAsignacionExp: OficinaAsignacionExpManager.instance().getRadicaciones(getExpediente())) {
					ExpedienteManager.instance().copiaRadicacionDesdePrincipal(getTipoRadicacion(), oficinaAsignacionExp, expediente, true);
				}
			}
		}
	}

	private void copiaObjetoJuicioDesdePrincipal(Expediente expediente, boolean crearActuacion) throws Lex100Exception {
		if(ExpedienteManager.isExpedienteBase(expediente)){
			List<Expediente> familiaExpediente = ExpedienteManager.getFamiliaDirectaExpediente(getEntityManager(), expediente);
			for(Expediente subexpediente: familiaExpediente){
				if(ExpedienteManager.instance().isAnySubexpedienteNaturaleza(subexpediente)){
					cambiarObjetoJuicioYTipoProceso(subexpediente, expediente.getObjetoJuicioPrincipal(), false, false, false);	
				}
			}
		}
	}

	private void cambiarObjetoJuicioYTipoProceso(Expediente expediente, ObjetoJuicio nuevoObjetoJuicio, boolean compensar, boolean crearActuacion, boolean propagar) throws Lex100Exception {
		ObjetoJuicio objetoJuicioAnterior = expediente.getObjetoJuicioPrincipal();
		if (nuevoObjetoJuicio != null && !equals(nuevoObjetoJuicio, objetoJuicioAnterior)) {
			expediente.setObjetoJuicioPrincipal(expedienteHome.getEntityManager().find(ObjetoJuicio.class, nuevoObjetoJuicio.getId()));
			String objetoJuicioChanged = EditExpedienteAction.objetoJuicioChanged(objetoJuicioAnterior, expediente.getDescripcionObjetoJuicio(), expediente);
			if(objetoJuicioChanged != null) {
				ExpedienteManager.instance().actualizarCaratula(expediente, crearActuacion ? TipoInformacionEnumeration.CAMBIO_OBJETO_JUICIO: null, crearActuacion? objetoJuicioChanged: null);
			}
			if (compensar) {
				RubrosInfo rubrosInfo = MesaSorteo.instance().calculaRubrosAsignacion(expediente, TipoRadicacionEnumeration.Type.juzgado, null, false);
				ExpedienteManager.instance().compensarOficina(expediente, null, TipoRadicacionEnumeration.Type.juzgado, rubrosInfo, TipoInformacionEnumeration.CAMBIO_OBJETO_JUICIO, objetoJuicioChanged);
			}
			if (propagar) {
				if(ExpedienteManager.isExpedienteBase(expediente)){
					copiaObjetoJuicioDesdePrincipal(expediente, crearActuacion);
				}
			}
			sorteo = null;
		}
		if (tipoProcesoNuevo!=null)	{
			TipoProceso tipoProcesoAnterior = expediente.getTipoProceso();
			cambiarTipoProceso(expediente, tipoProcesoNuevo);
			if (EditExpedienteAction.tipoProcesoChanged(tipoProcesoAnterior, tipoProcesoNuevo))
				ActuacionExpManager.instance().addActuacionTipoProcesoExpediente(expediente, null, tipoProcesoNuevo);
		}
	}
	
	/**
	 * Cambiar el tipo de proceso si es que se ha seleccionado otro
	 * @param expediente {@link Expediente}
	 * @param nuevoTipoProceso {@link TipoProceso} nuevo seleccionado
	 */
	private void cambiarTipoProceso(Expediente expediente, TipoProceso nuevoTipoProceso) {
		expediente.setTipoProceso(nuevoTipoProceso);
		
	}
	
	private void cambioTipoCausa() throws Lex100Exception {
		Expediente expediente = reasignacionEdit.getExpediente();
		reasignacionEdit.actualizarDatosExpediente();
		if (expediente.getExpedienteEspecial() != null && !getEntityManager().contains(expediente.getExpedienteEspecial())) {
			getEntityManager().persist(expediente.getExpedienteEspecial());
		}
		cambiarObjetoJuicioYTipoProceso(expediente, objetoJuicioNuevo, false, true, false);
		
		ExpedienteManager.instance().cambiarTipoCausa(expediente, null, reasignacionEdit.getTipoCausa(), true);

		updateParametros();
	}

	private void updateParametros() {
		if (showParametros()) {
			Expediente expediente = reasignacionEdit.getExpediente();
			createOrUpdateParametrosExpediente(expediente.getExpedienteIngreso());
			if(getParametrosChanged() != null) {
				ActuacionExpManager.instance().addActuacionCambioParametrosConexidad(expediente, getParametrosChanged());
			}
		}
	}
	
	private void incompetenciaExterna() throws Lex100Exception {
		Expediente expediente = reasignacionEdit.getExpediente();
		reasignacionEdit.actualizarDatosExpediente();
		if (expediente.getExpedienteEspecial() != null && !getEntityManager().contains(expediente.getExpedienteEspecial())) {
			getEntityManager().persist(expediente.getExpedienteEspecial());
		}
		
		if(isTipoSorteoIncompetenciaExternaInformatizada() && isTipoRadicacionSala()) {
			TipoInformacion tipoInformacion = TipoInformacionEnumeration.getItemByCodigo(getEntityManager(), TipoInformacionEnumeration.EXCUSACION_VOCALES, reasignacionEdit.getMateria());
			String descripcion = reasignacionEdit.getObservaciones();
			ActuacionExpManager.instance().addInformacionExpediente(expediente, null, tipoInformacion, descripcion, null, null, SessionState.instance().getUsername(), CamaraManager.getCamaraActual().getAbreviatura());
		}
		if(!equals(reasignacionEdit.getTipoCausa(), expediente.getTipoCausa())) {
			expediente.setTipoCausa(reasignacionEdit.getTipoCausa());
			ExpedienteManager.resetOrigen(expediente);
			ActuacionExpManager.instance().addActuacionTipoCausaExpediente(expediente, null, reasignacionEdit.getTipoCausa());
		}
		cambiarObjetoJuicioYTipoProceso(expediente, objetoJuicioNuevo, false, true, true);

		if(isPermitirIncompetenciaSegundaConRecursoAbierto()) {
			if(recursoEdit.getRecursoSorteo() != null) {
				if(recursoEdit.getTipoRecurso() != null){
					TipoRecurso tipoRecursoAnterior = recursoEdit.getRecursoSorteo().getTipoRecurso();
					recursoEdit.getRecursoSorteo().setTipoRecurso(recursoEdit.getTipoRecurso());
					ActuacionExpManager.instance().addActuacionCambioTipoRecurso(recursoEdit.getRecursoSorteo().getExpediente(), null, recursoEdit.getRecursoSorteo(), tipoRecursoAnterior);
				}
			}
		}
		updateParametros();
		getEntityManager().flush();
	}

	private void ajustaRubroAlSorteo(RubrosInfo info, Sorteo sorteo) {
		if(sorteo != null){
			List<String> rubros  = SorteoHome.getRubrosAsList(sorteo.getRubros());
			if(rubros != null && rubros.size()>0 && (info.getRubro() == null || !rubros.contains(info.getRubro()))){
				info.setRubro(rubros.get(0));
			}	
		}
	}

	private void reasignacionSecretariaEspecial()	throws Lex100Exception {
		Date FECHA_ASIGNACION_EN_CURSO = null; //null -> fecha en curso
		Oficina oficinaDestino = reasignacionEdit.getOficinaDestino();
		RubrosInfo info = calculateRubrosReasignacion();
		if(oficinaDestino != null){
			TipoAsignacionEnumeration.Type tipoAsignacionNone = null;
			TipoRadicacionEnumeration.Type tipoRadicacionNone = null;
			boolean compensarFalse = false; 
			try{
				new AccionCambioAsignacion().cambioAsignacionOficinaEspecial(SessionState.instance().getUsername(), reasignacionEdit.getExpediente(),
						oficinaDestino,
						tipoRadicacionNone,
						tipoAsignacionNone,
						SessionState.instance().getGlobalOficina(),
						getTipoGiro(reasignacionEdit.getExpediente(), tipoRadicacionNone),
						compensarFalse,
						info.getRubro(),	
						getCodigoTipoCambioAsignacion(),
						getDescripcionCambioAsignacion(),
						reasignacionEdit.getObservaciones(),
						FECHA_ASIGNACION_EN_CURSO,
						fojasEdit);
			} catch (AccionException e) {
				throw new Lex100Exception(e);
			}				
		}
	}
	
	private boolean puedeCompensarSinSorteo(TipoRadicacionEnumeration.Type tipoRadicacion) {
		if (isForceCompensacion()) {
			return true;
		} else {
			return true; // faltara meter un parametro para los casos que no compense.
		}
	}
	
	private void cambiaFiscalia(Expediente expediente, Oficina nuevaFiscalia, Sorteo sorteo, Integer numeroFiscaliaAnterior, String rubroAnterior, String rubro, boolean compensarOficinas){
		if(getTipoRadicacion() != null && (nuevaFiscalia != null) && !Strings.isEmpty(nuevaFiscalia.getNumero())){
			ExpedienteManager expedienteManager =  ExpedienteManager.instance();
			try {
				expedienteManager.cambiarFiscalia(expediente, getTipoRadicacion(), Integer.parseInt(nuevaFiscalia.getNumero()));
				if(compensarOficinas && sorteo != null){
					Oficina fiscaliaAnterior = OficinaManager.instance().findFiscalia(numeroFiscaliaAnterior, getTipoRadicacion());
					expedienteManager.enviarCompensacion(sorteo, TipoBolilleroSearch.getTipoBolilleroAsignacion(getEntityManager()), expediente, null, getTipoRadicacion(), fiscaliaAnterior, nuevaFiscalia, rubroAnterior, rubro, null, true, SessionState.instance().getUsername(), null, null, false);					
				}
			} catch (NumberFormatException e) {
			}
		}		
	}
	


	private String getCodigoTipoCambioAsignacion() {
		String codigoTipoCambioAsignacion = null;
		if (reasignacionEdit.getTipoCambioAsignacion() != null) {
			codigoTipoCambioAsignacion = reasignacionEdit.getTipoCambioAsignacion().getCodigo(); // Codigo seleccionado por el usuario, faltaría añadir control en la pantalla, ahora no se usa
		} else {
			if(isTipoRadicacionTribunalOral()){
				codigoTipoCambioAsignacion = reasignacionEdit.getTipoReasignacionTribulaOral().getValue().toString();
			} else if(isTipoRadicacionVocalias()){
				codigoTipoCambioAsignacion = TipoReasignacionEnumeration.Type.integracionSalaVocal.getValue().toString();
			} else if(isTipoRadicacionVocaliasCasacion()){
				codigoTipoCambioAsignacion = TipoReasignacionEnumeration.Type.integracionSalaCasacionVocal.getValue().toString();
			} else if(isTipoSorteoIncompetenciaExternaInformatizada() && isTipoRadicacionSala() && (getRecursoEdit().getTipoRecurso() != null)) {
				codigoTipoCambioAsignacion = getRecursoEdit().getTipoRecurso().getCodigo();
			} else if(isTipoSorteoIncompetenciaExternaInformatizada() && isTipoRadicacionJuzgado()) {
				if(reasignacionEdit.isOmitirSorteoComercial()) {
					codigoTipoCambioAsignacion = TipoCambioAsignacionEnumeration.ASIGNACION_MANUAL;
				} else {
					codigoTipoCambioAsignacion = TipoCambioAsignacionEnumeration.SORTEO_JUZGADO;
				}
			}else{ // Sala, Juzgado y JuzgadoPlenario
				//isTipoRadicacionSala()
				if(reasignacionEdit.getTipoReasignacion() != null) {
				codigoTipoCambioAsignacion = reasignacionEdit.getTipoReasignacion().getValue().toString();
				String codigoIncluyendoSalaOrigen = reasignacionEdit.getTipoReasignacion().getTipoReasignacionSiIncluyeSalaOrigen();
				String codigoExlusionMultiple = reasignacionEdit.getTipoReasignacion().getTipoReasignacionExlusionMultiple();
				if(!reasignacionEdit.isExcluirOficinaOrigen()){
					if(codigoIncluyendoSalaOrigen != null){
						codigoTipoCambioAsignacion = codigoIncluyendoSalaOrigen;
					}
					if(getExcludeOficinas() != null && getExcludeOficinas().size() > 0 && codigoExlusionMultiple != null) {
						codigoTipoCambioAsignacion = codigoExlusionMultiple;						
					}
				}
				}
			}
		}
		return codigoTipoCambioAsignacion;
	}

	private String getDescripcionCambioAsignacion() {
		String descripcion = null;
		
		if(isTipoSorteoIncompetenciaExternaInformatizada() && isTipoRadicacionSala() && (getRecursoEdit().getTipoRecurso() != null)) {
			descripcion = getRecursoEdit().getTipoRecurso().getDescripcion();
		} else {
			Integer tipoInstanciaCodigo = TipoRadicacionEnumeration.calculateCodigoTipoInstanciaByTipoRadicacion(getTipoRadicacion());
			TipoInstancia tipoInstancia = TipoInstanciaSearch.findByCodigo(expedienteHome.getEntityManager(), tipoInstanciaCodigo);
			TipoCambioAsignacion tipoCambioAsignacion = TipoCambioAsignacionEnumeration.getItemByCodigo(expedienteHome.getEntityManager(), getCodigoTipoCambioAsignacion(), tipoInstancia, reasignacionEdit.getMateria());
			if (tipoCambioAsignacion != null) {
				descripcion =  tipoCambioAsignacion.getDescripcion();
			}
		}
		return descripcion;
	}

	public boolean isBuscaConexidadFamilia(){
		boolean isPenal = reasignacionEdit.getExpediente() != null && ExpedienteManager.instance().isPenal(reasignacionEdit.getExpediente());

		return !isSorteoRecursoExtraordinario() && !isPenal; // OJO revisar si otros sorteos tampoco buscan conexidad de familia
	}

	public boolean puedeCompensarPorConexidadFamilia(TipoRadicacionEnumeration.Type tipoRadicacion) {
		return true;
	}
	
	private OficinaSorteo findOficinaSorteo(Sorteo sorteo, Oficina oficina) {
		for(OficinaSorteo oficinaSorteo:sorteo.getOficinaSorteoList()){
			if(!oficinaSorteo.isLogicalDeleted()){
				if (oficinaSorteo.getOficina().getId().equals(oficina.getId())){
					return oficinaSorteo;
				}
			}
		}			
		return null;
	}

	public void initOficinasExcluidas() {
		resetOficinasExcluidas();
		try {
			getSorteoSinRubro(SessionState.instance().getGlobalOficina(), getCompetencia());
			initOficinasExcluidas( getSorteoSinRubro(SessionState.instance().getGlobalOficina(), getCompetencia()));
		} catch (Lex100Exception e) {
		}
	}
	
	public void initOficinasExcluidas(Sorteo sorteo) {
		resetOficinasExcluidas();
		if (getExpediente() != null) {
			if (isSorteoJuzgadoPlenario() || isSorteoJuzgadoSentencia()) {	// excluye juzgado instruccion
				excluirOficina(sorteo, getJuzgadoRadicacion(getExpediente()));
			}
			if (isSorteoAnySala() && !isSorteoSala() && !isSorteoRecursoExtraordinario()) {	// excluye sala origen
				excluirOficina(sorteo, getSalaRadicacion(getExpediente()));
			}
			if (isSorteoJuzgado() || isSorteoJuzgadoSentencia() || isSorteoAnySala()) {	//excluye excusaciones, recusaciones....
				List<CambioAsignacionExp> radicacionesAnteriores = CambioAsignacionExpSearch.findByTipoRadicacion(getEntityManager(), getExpediente(), getCamara(), (String)getTipoRadicacion().getValue());
				for(CambioAsignacionExp radicacion: radicacionesAnteriores) {
					if(TipoCambioAsignacionEnumeration.isTipoCambioExcluir(radicacion.getCodigoTipoCambioAsignacion())){
						if(radicacion.getOficinaAnterior() != null) {
							excluirOficina(sorteo, radicacion.getOficinaAnterior());
						}
					}
				}
			}
		}
	}
	
	protected List<Integer> getExcludeOficinaIds(Expediente expediente) {
		if (isReasignacionPorBusquedaConexidad()) {
			return null;
		}
		if (isTipoSorteoIntegracionSala() || isTipoSorteoIntegracionSalaCasacion() || isTipoSorteoIntegracionSalaEstudioCasacion() ) {
			return getVocaliasExcluidasIds();
		}
		
		List<Integer> excludeOficinasIds = super.getExcludeOficinaIds(expediente);

		Oficina oficinaOrigen = null;
		if(reasignacionEdit.isExcluirOficinaOrigen()){
			if(isSorteoFiscaliaTribunalOral()){
				oficinaOrigen = getFiscaliaRadicacion(getTipoRadicacion(), expediente);
			} else if(isTipoSorteoMediador()){
				Mediador mediador = getMediador(expediente);
				if(mediador != null) {
					excludeOficinasIds = new ArrayList<Integer>();
					excludeOficinasIds.add(mediador.getIdMediador());
				}
			} else {
				oficinaOrigen = getOficinaRadicacion(getTipoRadicacion(), expediente);
			}
			if(oficinaOrigen != null){
				if(excludeOficinasIds == null){
					excludeOficinasIds = new ArrayList<Integer>();
				}
				List<OficinaSorteo> oficinasSorteoExcluidas = getOficinasSorteoExcluidas(sorteo, oficinaOrigen);
				for(OficinaSorteo oficinasSorteo: oficinasSorteoExcluidas) {
					if ((oficinasSorteo.getOficina() != null) && !excludeOficinasIds.contains(oficinasSorteo.getOficina().getId())) {
						excludeOficinasIds.add(oficinasSorteo.getOficina().getId());
					}
				}
			}
		}
		return excludeOficinasIds;
	}

	private List<Integer> getVocaliasExcluidasIds() {
		List<Integer> oficinasExcluidasIdList = new ArrayList<Integer>();
		for(Oficina vocalia: reasignacionEdit.getVocaliasSalaSorteoSelectionMap().keySet()) {
			Boolean value = reasignacionEdit.getVocaliasSalaSorteoSelectionMap().get(vocalia);
			if (value != null && value) {
				oficinasExcluidasIdList.add(vocalia.getId());
			}
		}
		for (Oficina vocalia: vocaliasSalaActuales) {
			oficinasExcluidasIdList.add(vocalia.getId());
		}
		return oficinasExcluidasIdList;
	}

	private Oficina getFiscaliaRadicacion(TipoRadicacionEnumeration.Type tipoRadicacion, Expediente expediente) {
		ExpedienteManager expedienteManager = ExpedienteManager.instance();
		return expedienteManager.getFiscaliaRadicacionExpediente(expediente, tipoRadicacion);
	}
	
	private Oficina getOficinaRadicacion(TipoRadicacionEnumeration.Type tipoRadicacion, Expediente expediente) {
		ExpedienteManager expedienteManager = ExpedienteManager.instance();
		return expedienteManager.getOficinaByRadicacion(expediente, tipoRadicacion);
	}
	
	private OficinaAsignacionExp findRadicacionExpediente(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		ExpedienteManager expedienteManager = ExpedienteManager.instance();
		return expedienteManager.findRadicacionExpediente(expediente, tipoRadicacion);
	}

	public String getTipoRadicacionValue() {
		TipoRadicacionEnumeration.Type tipoRadicacion = getTipoRadicacion();
		if (tipoRadicacion != null) {
			return (String)tipoRadicacion.getValue();
		}
		return null;
	}
	
	public Type getTipoRadicacion() {
		return this.tipoRadicacion;
	}

	public void setTipoRadicacion(TipoRadicacionEnumeration.Type tipoRadicacion) {
		this.reasignacionEdit.setTipoRadicacion(tipoRadicacion);
		this.tipoRadicacion = tipoRadicacion;
	}

	public void setTipoRadicacionJuzgado() {
		setTipoRadicacion(TipoRadicacionEnumeration.Type.juzgado);
	}

	public void setTipoRadicacionSala() {
		setTipoRadicacion(TipoRadicacionEnumeration.Type.sala);
	}
	
	public void setTipoRadicacionPlenario() {
		setTipoRadicacion(TipoRadicacionEnumeration.Type.juzgadoPlenario);
	}
	
	public void setTipoRadicacionTribunalOral() {
		setTipoRadicacion(TipoRadicacionEnumeration.Type.tribunalOral);
	}
	

	public boolean isTipoRadicacionSala() {
		return TipoRadicacionEnumeration.Type.sala == getTipoRadicacion();
	}
	
	public boolean isTipoRadicacionPlenario() {
		return TipoRadicacionEnumeration.Type.juzgadoPlenario == getTipoRadicacion();
	}
	
	public boolean isTipoRadicacionJuzgadoSentencia() {
		return TipoRadicacionEnumeration.Type.juzgadoSentencia == getTipoRadicacion();
	}
	
	public boolean isTipoRadicacionTribunalOral() {
		return TipoRadicacionEnumeration.Type.tribunalOral == getTipoRadicacion();
	}

	public boolean isTipoRadicacionVocalias() {
		return TipoRadicacionEnumeration.Type.vocalias == getTipoRadicacion();
	}

	public boolean isTipoRadicacionVocaliasCasacion() {
		return TipoRadicacionEnumeration.Type.vocaliasCasacion == getTipoRadicacion();
	}

	public boolean isTipoRadicacionVocaliasEstudioCasacion() {
		return TipoRadicacionEnumeration.Type.vocaliasEstudioCasacion == getTipoRadicacion();
	}
	
	public String getTitle() {
		if(isSorteoFiscaliaTribunalOral()){
			return getMessages().get("expedienteMeReasignacion.fiscaliaTribunalOral");
		}
		return getTitleBySorteo(tipoSorteo);
	}
	
	public String getTitleBySorteo(TipoSorteo tipoSorteo) {
		if (tipoSorteo != null) {
			switch (tipoSorteo) {
			case juzgado:
				return getMessages().get("expedienteMeReasignacion.juzgado");
			case juzgadoSentencia:
				return getMessages().get("expedienteMeReasignacion.juzgadoSentencia");
			case sala:
				return getMessages().get("expedienteMeReasignacion.sala");
			case tribunalOral:
				return getMessages().get("expedienteMeReasignacion.tribunalOral");
			case juzgadoPlenario:
				return getMessages().get("expedienteMeReasignacion.juzgadoPlenario");
			case salaCasacion:
				return getMessages().get("expedienteMeReasignacion.salaCasacion");
			case juzgadoMediacion:
				return getMessages().get("expedienteMeReasignacion.juzgadoMediacion");
			case cambioTipoCausa:
				return getMessages().get("expedienteMeReasignacion.cambioTipoCausa");
			case salaEstudio:
				return getMessages().get("expedienteMeReasignacion.salaEstudio");
			case salaRecursoExtraordinario:
				return getMessages().get("expedienteMeReasignacion.salaRecursoExtraordinario");
			case desafectacionSalaEstudio:
				return getMessages().get("expedienteMeReasignacion.desafectacionSalaEstudio");
			case incompetenciaExternaInformatizada:
				return getMessages().get("expedienteMeReasignacion.incompetenciaExternaInformatizada");
			case recuperacionIncompetencia:
				return getMessages().get("expedienteMeReasignacion.recuperacionIncompetencia");
			case integracionSala:
				return getMessages().get("expedienteMeReasignacion.integracionSala");
			case integracionSalaCasacion:
				return getMessages().get("expedienteMeReasignacion.integracionSalaCasacion");
			case integracionSalaEstudioCasacion:
				return getMessages().get("expedienteMeReasignacion.integracionSalaEstudioCasacion");
			case mediador:
				return getMessages().get("expedienteMeReasignacion.mediador");
			case finJuzgadoSentencia:
				return getMessages().get("expedienteMeReasignacion.finJuzgadoSentencia");	
			case secretariaCorte:
				return getMessages().get("expedienteMeReasignacion.secretariaCorte");
			default:
				break;
			}
		}
		return "";
	}
	
	
	public String getIcon() {
		if(isSorteoFiscaliaTribunalOral()){
			return "fiscalia";
		}
		switch(getTipoRadicacion()) {
			case sala:
			case salaEstudio:
			case salaCasacion:
			case corteSuprema:
				return "asignacionSala";
			case tribunalOral:
			case juzgadoMediacion:
			case juzgadoPlenario:
			case juzgadoSentencia:
			case juzgado:
			default:
				return "asignacionJuzgado";
		}
	}

	public TipoGiroEnumeration.Type getTipoGiro(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		if (this.tipoGiro == null) {
			if (TipoRadicacionEnumeration.isAnySala(tipoRadicacion)) {
				this.tipoGiro = ConfiguracionMateriaManager.instance().getTipoGiroAsignacionSala(SessionState.instance().getGlobalCamara(), reasignacionEdit.getMateria());
			} else if (TipoRadicacionEnumeration.isRadicacionVocalias(tipoRadicacion) || TipoRadicacionEnumeration.isRadicacionVocaliasCasacion(tipoRadicacion)) {
				return null;
			} else {
				this.tipoGiro = ConfiguracionMateriaManager.instance().getTipoGiroAsignacion(SessionState.instance().getGlobalCamara(), reasignacionEdit.getMateria());
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
		errorExpediente = false;
		try {
			if ((reasignacionEdit.getExpediente() == null)  || !reasignacionEdit.getExpediente().getNumero().equals(reasignacionEdit.getNumeroExpediente()) || !reasignacionEdit.getExpediente().getAnio().equals(reasignacionEdit.getAnioExpediente())){
				updateExpediente();
				checkExpediente();
				return null;
			}
			checkExpediente();
			checkDatosReasignacion();
			checkConexidadDeclarada();
			checkCambioUnico();
			checkParametros();

		} catch (EntityOperationException e) {
			error = true;
			errorExpediente = true;
		}
		return error ? null : page;
	}

	private void checkExpediente() throws EntityOperationException {
		String errKey = null;

		if (reasignacionEdit.getExpediente() == null) {
			if(getFamiliaExpedientes() == null || getFamiliaExpedientes().size() == 0){
				errKey = "expedienteMeAsignacion.error.expedienteRequerido";
			}
		} else if(ExpedienteManager.isPase(reasignacionEdit.getExpediente()) || ExpedienteManager.isRemision(reasignacionEdit.getExpediente())) {
			errKey = "expedienteMeReasignacion.error.expedienteEnPase";
		}
		if (errKey != null) {
			errorMsg(errKey);
			throw new EntityOperationException();
		}
	}

	private void checkDatosReasignacion() throws EntityOperationException {
		String errKey = null;

		if(isTipoSorteoCambioTipoCausa()) {
			errKey = checkCambioTipoCausa();
		} else if(isTipoSorteoIncompetenciaExternaInformatizada()) {
			errKey = checkIncompetenciaExternaInformatizada();
		} else if(isTipoSorteoRecuperacionIncompetencia()) {
			errKey = checkIncompetenciaExternaInformatizada();
		} else if(isTipoSorteoIntegracionSala()) {
			errKey = checkIntegracionSala();
		} else if(isTipoSorteoIntegracionSalaCasacion() || isTipoSorteoIntegracionSalaEstudioCasacion()) {
			errKey = checkIntegracionSalaCasacion();
		} else if(isTipoSorteoMediador()){
			if(!reasignacionEdit.isSortearMediador() && reasignacionEdit.getMediadorDestino() == null){
				errKey = "expedienteMeReasignacion.error.seleccioneMediador";
			}
		} else if(CamaraManager.isCamaraActualCivilCABA() && !isTipoSorteoIncompetenciaExternaInformatizada() && !hasAnyRadicacionEnCamara(reasignacionEdit.getExpediente()) && !CamaraManager.getCamaraActual().equals(reasignacionEdit.getExpediente().getCamara()) ){
			errKey = "expedienteMeReasignacion.error.expedienteOtraCamara";
		} else {
			radicacionAnterior = findRadicacionExpediente(reasignacionEdit.getExpediente(), getTipoRadicacion());
			if(radicacionAnterior != null && !tengoPermisoCambiarRadicacion(radicacionAnterior)){
				errKey="expedienteMeReasignacion.error.radicacionDiferente";
			}else	if (radicacionAnterior == null || radicacionAnterior.getOficina() == null && !isDestinoSecretariaEspecial()) {
				if(!isTipoRadicacionJuzgado()){
					errKey = Interpolator.instance().interpolate(getMessages().get("expedienteMeReasignacion.error.noRadicacionActual"), getMessages().get(getTipoRadicacion().getLabel()));
				}
			
			}else if (necesitaTipoReasignacion() && !haSeleccionadoTipoReasignacion()) {
				errKey = "expedienteMeReasignacion.error.tipo.required";
			}else if (reasignacionEdit.showSelectorDeOficinas() || isDestinoSecretariaEspecial()) {
				if (isDestinoSecretariaEspecial()) {
					List<Oficina> oficinasDestino = getOficinasDestino();
					if (oficinasDestino.size() > 0) {
						reasignacionEdit.setOficinaDestino(oficinasDestino.get(0));
					}
				} 
				if(reasignacionEdit.getOficinaDestino() == null) {
					errKey = "expedienteMeReasignacion.error.oficinaDestino.required";
				} else if (reasignacionEdit.getOficinaDestino().equals(radicacionAnterior.getOficina())) {
					errKey = "expedienteMeReasignacion.error.oficinaDestino.equalOrigen";
				}
			}
			
			if(errKey ==null){
				if(isResorteoSalaOrigenDesdeSalaDeEstudio() && getRadicacionActualSalaEstudio() != null){
					errKey = "expedienteMeReasignacion.error.debeFinalizarSalaEstudioPreviamente";
				}					
			}
				
		}
		if (errKey != null) {
			errorMsg(errKey);
			throw new EntityOperationException();
		}
	}

	private boolean isResorteoSalaOrigenDesdeSalaDeEstudio() {
		boolean ret = false;
		if(isTipoRadicacionSala()){
			String codigo = getCodigoTipoCambioAsignacion();
			ret = TipoCambioAsignacionEnumeration.SALA_INL.equals(codigo) || TipoCambioAsignacionEnumeration.SALA_REC.equals(codigo);
		}
		return ret;
	}

	private boolean hasAnyRadicacionEnCamara(Expediente expediente) {
		List<OficinaAsignacionExp> listaRadicacionesActuales = getRadicacionesActuales();
		if (listaRadicacionesActuales.size() == 0) {
			return true;
		}
		Camara camaraActual = CamaraManager.getCamaraActual();
		for (OficinaAsignacionExp radicacionActual: listaRadicacionesActuales) {
			if (radicacionActual.getOficina().getCamara().equals(camaraActual)) {
				return true;
			}
		}
		return false;
	}

	private boolean necesitaTipoReasignacion() {
		return !isTipoSorteoMediador() && !isTipoSorteoRecuperacionIncompetencia();
	}

	private String checkIntegracionSala() {
		if (reasignacionEdit.getExpediente().getRadicacionSala() == null) {
			return "expedienteMeReasignacion.error.radicacionSalaObligatoria";
		}
		return null;
	}

	private String checkIntegracionSalaCasacion() {
		if (reasignacionEdit.getExpediente().getRadicacionCasacion() == null) {
			return "expedienteMeReasignacion.error.radicacionCasacionObligatoria";
		}
		return null;
	}
	
	private String checkIncompetenciaExternaInformatizada() {
		String errKey = null;
		if(isPermitirIncompetenciaSegundaConRecursoAbierto()) {
			errKey = checkCambioTipoRecurso();
		} else if(isTieneRecursosPendientes(reasignacionEdit.getExpediente())) {
			errKey="expedienteMeReasignacion.error.expedienteTieneRecursoAbierto";
		}
		if (errKey == null) {
			if(isTipoRadicacionJuzgado()) {
				if (objetoJuicioNuevo == null) {
					errKey = "expedienteMeReasignacion.error.objetoJuicio.required";
				} 
			}
		}
		return errKey;
	}

	private String checkCambioTipoRecurso() {
		if(isSorteoNecesitaRecurso() && getRecursoEdit().getRecursoSorteo() == null){
			return "expedienteMeReasignacion.error.recursoRequerido"; 
		}
		if(isMostrarSelectorTipoRecurso() && getRecursoEdit().getTipoRecurso() == null){
			return "expedienteMeReasignacion.error.tipoRecursoRequerido";
		}
		return null;
	}
	
	private String checkCambioTipoCausa() {
		String errKey = null;
		TipoCausa tipoCausa = reasignacionEdit.getTipoCausa();
		if (getExpediente() != null && !CamaraManager.isPenal(CamaraManager.getCamaraActual())
			&& ExpedienteManager.instance().isPenal(getExpediente())) {
			return "expedienteMeReasignacion.error.cambioTipoCausaPenal";
		}
		if (objetoJuicioNuevo == null) {
			return "expedienteMeReasignacion.error.objetoJuicio.required";
		} 
		if (tipoCausa == null || TipoInstanciaEnumeration.isPrimeraInstancia(tipoCausa.getTipoInstancia())) {
			if (objetoJuicioNuevo.getTipoInstancia() != null && !TipoInstanciaEnumeration.isPrimeraInstancia(objetoJuicioNuevo.getTipoInstancia())) {
				return "expedienteMeReasignacion.error.objetoJuicio.primeraInstanciaObligatorio";
			}
		}
		if (tipoCausa != null && TipoInstanciaEnumeration.isApelaciones(tipoCausa.getTipoInstancia())) {
			if (objetoJuicioNuevo.getTipoInstancia() != null && !TipoInstanciaEnumeration.isApelaciones(objetoJuicioNuevo.getTipoInstancia())) {
				return "expedienteMeReasignacion.error.objetoJuicio.apelacionesObligatorio";
			}
		}
		ExpedienteEspecialEdit expedienteEspecialEdit = reasignacionEdit.getExpedienteEspecialEdit();
		if (showOrigen() && !isDependenciaOpcional(tipoCausa)
				&& expedienteEspecialEdit.getDependencia() == null
				&& expedienteEspecialEdit.getOtraDependencia() == null) {
			return "expedienteEspecial.error.dependenciaOrigenRequerida";
		}
		if (showOrigen() && showFechaInformacion()) {
			if (expedienteEspecialEdit.getFechaInformacion() == null) {
				return "expedienteEspecial.error.fechaInformacionRequerida";
			} else {
				if (expedienteEspecialEdit.getFechaInformacion().after(
						new Date())) {
					return "expedienteEspecial.error.fechaInvalida";
				}
			}
		}
		if (showJuezExortante()) {
			if (Strings.isEmpty(expedienteEspecialEdit.getJuezExhortante())) {
				return "expedienteEspecial.error.juezExhortanteRequerido";
			}
		}
		if (showOrigen()) {
			if (Strings.isEmpty(expedienteEspecialEdit.getNumero())) {
				return "expedienteEspecial.error.numeroRequerido";
			}
		}
		if (showNumeroCodigo()) {
			if (Strings.isEmpty(expedienteEspecialEdit.getNumeroCodigo())) {
				return "expedienteEspecial.error.numeroCodigoRequerido";
			}
		}
		if (showCaratulaOrigen()
				&& Strings.isEmpty(expedienteEspecialEdit.getCaratulaOrigen())) {
			return "expedienteEspecial.error.caratulaOrigenRequerida";
		}
		return errKey;
	}

	private boolean tengoPermisoCambiarRadicacion(OficinaAsignacionExp radicacionAnterior) {
		boolean ret = false;
		Oficina oficinaActual = OficinaManager.instance().getOficinaActual();
		if(OficinaManager.instance().isMesaDeEntrada(oficinaActual)){
			ret = true;
		}else{
			if (radicacionAnterior == null) {
				ret = true;
			} else {
				Set<Integer> globalOficinaSet = SessionState.instance().getGlobalIdOficinaSet();
				if (CamaraManager.isCamaraActualMultiMateria() && TipoRadicacionEnumeration.Type.tribunalOral.getValue().equals(radicacionAnterior.getTipoRadicacion())) {
					OficinaAsignacionExp radicacionJuzgado = radicacionAnterior.getExpediente().getRadicacionJuzgado();
					if (radicacionJuzgado != null) {
						ret = (radicacionJuzgado.getOficina() != null && globalOficinaSet.contains(radicacionJuzgado.getOficina().getId()));
					}
					if(TipoInstanciaEnumeration.TIPO_INSTANCIA_TRIBUNAL_ORAL.equals(oficinaActual.getTipoInstancia().getCodigo())){
						OficinaAsignacionExp radicacionTO = radicacionAnterior.getExpediente().getRadicacionTribunalOral();
						if(radicacionTO != null) {
							ret = (radicacionTO.getOficina() != null && globalOficinaSet.contains(radicacionTO.getOficina().getId()));
						}
					}
				} else {
					ret = (radicacionAnterior.getOficina() != null && globalOficinaSet.contains(radicacionAnterior.getOficina().getId())) || OficinaManager.instance().isSecretariaEspecial(oficinaActual);
				}
			}
		}
		return ret;
	}

	private boolean haSeleccionadoTipoReasignacion() {
		if(isTipoRadicacionTribunalOral()){
			return reasignacionEdit.getTipoReasignacionTribulaOral() != null;
		}else{
			return reasignacionEdit.getTipoReasignacion() != null;
		}
	}

//	public void refreshExpediente() {
//		try {
//			List<Expediente> familia = FamiliaManager.getFamiliaDirectaExpediente(expedienteHome.getEntityManager(), reasignacionEdit.getExpediente());
//			for (Expediente expediente:familia) {
//				refreshExpediente(expediente);
//			}
//		} catch (Exception e) {
//			getLog().error("Error al hacer refresh del expediente POR REASIGNACION con id: " + reasignacionEdit.getExpediente().getId().toString() + " en reasignacion de causa.");
//			e.printStackTrace();
//		}
//		expedienteList.doSelection(reasignacionEdit.getExpediente(), null);
//	}
	
	public void refreshExpediente() {
		Expediente expediente = reasignacionEdit.getExpediente();
		try {
			if(expediente==null) throw new Exception();
			expediente = AbstractManager.solveMergedExpediente(expedienteHome.getEntityManager(), expediente);
			
			refreshExpediente(expediente);
		} catch (Exception e) {
			getLog().error("Error al hacer refresh del expediente POR REASIGNACION con id: " + ((expediente!=null) ? expediente.getId().toString() : "desconocido") + " en reasignacion de causa.");
			e.printStackTrace();
		}
		expedienteList.doSelection(expediente, null);
	}
	
	public void refreshExpediente(Expediente expediente) throws Exception {
		if(expediente != null){
			expediente = AbstractManager.solveMergedExpediente(expedienteHome.getEntityManager(), expediente);
			expedienteHome.getEntityManager().refresh(expediente);
			
			//solo se hace refresh del resto del expediente cuando se finaliza el sorteo, sino trae problemas en radicaciones
			if(!expediente.isSorteando()){
				if (expediente.getExpedienteIngreso() != null) {
					expedienteHome.getEntityManager().refresh(expediente.getExpedienteIngreso());
				}
				getLog().info("Realizando refresh de radicaciones por REASIGNACION del expediente: " + expediente.getClave());
				
				oficinaAsignacionExpManager.refreshRadicacionExpediente(expediente);
				expediente.reset();
				
				VinculadosExpList.instance().refresh();
				VinculadosConexosExpList.instance().refresh();
				ConexidadList.instance().refresh();
				refreshVinculadosExpediente(expedienteHome.getEntityManager(), expediente);
			}
		}
	}
	
	public static void refreshVinculadosExpediente(EntityManager entityManager, Expediente expediente) {
		VinculadosExp vinculado = FamiliaManager.getVinculadoByTipo(entityManager, expediente, TipoVinculadoEnumeration.Type.conexo_Juzgado.getValue().toString());
		if (vinculado != null)
			entityManager.refresh(vinculado);
		
		vinculado = FamiliaManager.getVinculadoByTipo(entityManager, expediente, TipoVinculadoEnumeration.Type.conexo_Sala.getValue().toString());
		if (vinculado != null)
			entityManager.refresh(vinculado);
	}

	public void refreshSelectExpediente(){
		try {
			if(reasignacionEdit.getExpediente() != null){
				List<Expediente> familia = FamiliaManager.getFamiliaDirectaExpediente(expedienteHome.getEntityManager(), reasignacionEdit.getExpediente());
				for (Expediente expediente:familia)
					refreshExpediente(expediente);
			}
		} catch (Exception e) {
			getLog().error("Error al hacer refresh expediente seleccionado con id: " + reasignacionEdit.getExpediente().getId().toString() + " en reasignacion de causa.");
		}
	}
	
	private void calculaOficinasDestinoPorRubro(Oficina oficinaSorteadora, Competencia competencia) throws Lex100Exception{
		if(oficinasSorteo == null){
			Camara camara = SessionState.instance().getGlobalCamara();
			RubrosInfo info = calculateRubrosReasignacion();
			if(info.getRubro() != null){
				List<Sorteo> sorteos = mesaSorteo.buscaSorteosValidos(camara, null, null, oficinaSorteadora, false, getTipoRadicacion(), null, reasignacionEdit.getMateria(), competencia, info.getRubroAsignacion(), info.getRubro(), false);
				oficinasSorteo = new ArrayList<Oficina>();
				if(sorteos != null){
					for(Sorteo sorteo: sorteos){
						addOficinas(oficinasSorteo, sorteo);
					}
				}
			}else{
				Sorteo sorteo = getSorteoSinRubro(oficinaSorteadora, competencia);
				oficinasSorteo = new ArrayList<Oficina>();
				addOficinas(oficinasSorteo, sorteo);
			}
		}
		
	}

	private void addOficinas(List<Oficina> list, Sorteo s) {
		for(OficinaSorteo oficinaSorteo:s.getOficinaSorteoList()){
			if(!oficinaSorteo.isLogicalDeleted()){
				list.add(oficinaSorteo.getOficina());
			}
		}		
	}

	public Sorteo getSorteoSinRubro(Oficina oficinaSorteadora, Competencia competencia) throws Lex100Exception {
		return getSorteoSinRubro(oficinaSorteadora, competencia, false);
	}
	
	public Sorteo getSorteoSinRubro(Oficina oficinaSorteadora, Competencia competencia, boolean reset) throws Lex100Exception {
		return getSorteoSinRubro(oficinaSorteadora, competencia, reset, true);
	}
	
	public Sorteo getSorteoSinRubro(Oficina oficinaSorteadora, Competencia competencia, boolean reset, boolean required) throws Lex100Exception {
		if (reset) {
			sorteo = null;
		}
		if (sorteo == null) {
			Camara camara = SessionState.instance().getGlobalCamara();
			if (isSorteoFiscaliaTribunalOral()) {
				sorteo = mesaSorteo.findSorteoFiscaliaByTipoRadicacion(camara, getTipoRadicacion(), competencia, oficinaSorteadora);
				if(sorteo == null){
					errorMsg("expedienteMeAsignacion.error.sorteoNotMatch");
					throw new Lex100Exception("Error sorteo no encontrado para el expediente");					
				}
			} else if (isTipoSorteoIntegracionSala()) {
				sorteo = mesaSorteo.findSorteoVocaliasSala(camara, getTipoRadicacion(), competencia, true, oficinaSorteadora);
				if(sorteo == null){
					errorMsg("expedienteMeAsignacion.error.sorteoNotMatch");
					throw new Lex100Exception("Error sorteo no encontrado para el expediente");					
				}
			} else if (isTipoSorteoIntegracionSalaCasacion() || isTipoSorteoIntegracionSalaEstudioCasacion()) {
				sorteo = mesaSorteo.findSorteoVocaliasSalaCasacion(camara, getTipoRadicacion(), competencia, true, oficinaSorteadora);
				if(sorteo == null){
					errorMsg("expedienteMeAsignacion.error.sorteoNotMatch");
					throw new Lex100Exception("Error sorteo no encontrado para el expediente");					
				}
			} else {
				RubrosInfo info = calculateRubrosReasignacion();
				if(info.getRubro() != null){
					sorteo = mesaSorteo.buscaSorteo(reasignacionEdit.getExpediente(), 
						getTipoRadicacion(), 
						oficinaSorteadora, false,
						competencia,
						info.getRubroAsignacion(), 
						info.getRubro(), false);
				}
				if(sorteo == null){ // puede que el rubro no este en esta camara
					sorteo = mesaSorteo.buscaSorteo(reasignacionEdit.getExpediente(), 
							getTipoRadicacion(), 
							oficinaSorteadora, false,
							competencia,
							MesaSorteo.ANY_RUBRO, 
							MesaSorteo.ANY_RUBRO, required);
				}
//				sorteo = mesaSorteo.findSorteoByTipoRadicacion(camara, getTipoRadicacion(), reasignacionEdit.getExpediente().getCompetencia(), oficinaSorteadora);
			}
		}
		return sorteo;
	}
	
	public void setSorteo(Sorteo sorteo) {
		this.sorteo = sorteo;
	}

	public void onChangeCompetencia(ValueChangeEvent event) {
		setSorteo(null);
	}
	
	public void onChangeTipoRecurso(ValueChangeEvent event) {
		setSorteo(null);
	}
	
	public boolean isPenal() {
		return OficinaManager.instance().isPenal(SessionState.instance().getGlobalOficina());
	}

	public boolean isNoPenal() {
		return OficinaManager.instance().isNoPenal(SessionState.instance().getGlobalOficina());
	}

	public boolean showTipoGiro() {
		return false;
	}
	public List<Expediente> getFamiliaExpedientes() {
		return familiaExpedientes;
	}

	public void setFamiliaExpedientes(List<Expediente> familiaExpedientes) {
		this.familiaExpedientes = familiaExpedientes;
	}

	public TipoSorteo getTipoSorteo() {
		return tipoSorteo;
	}

	public void setTipoSorteo(TipoSorteo tipoSorteo) {
		this.tipoSorteo = tipoSorteo;
	}
		
	public void setTipoSorteoJuzgado() {
		tipoSorteo = TipoSorteo.juzgado;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.juzgado);
		if (OficinaManager.instance().inJuzgado()) {
			if (OficinaManager.instance().isCurrentOficinaPenal()) {
				getReasignacionEdit().setTipoReasignacion(TipoReasignacionEnumeration.Type.oficioJuzgadoPenal);
			} else {
				getReasignacionEdit().setTipoReasignacion(TipoReasignacionEnumeration.Type.oficioJuzgadoCivil);
			}
		}
	}
	
	public void setTipoSorteoPerito() {
		tipoSorteo = TipoSorteo.perito;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.perito);
	}

	public void setTipoSorteoJuzgadoMediacion() {
		tipoSorteo = TipoSorteo.juzgadoMediacion;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.juzgado);
	}
	
	
	public void setIncompetenciaSinCambioOJ() {
		getReasignacionEdit().setTipoReasignacion(TipoReasignacionEnumeration.Type.incompetencia);
		setTipoSorteoJuzgado();
	}
	
	public void setIncompetenciaConCambioOJ() {
		getReasignacionEdit().setTipoReasignacion(TipoReasignacionEnumeration.Type.incompetencia);
		setTipoSorteoJuzgado();
		setIncompetenciaCambioOJ(true);
	}
	
	public void setTipoSorteoSala() {
		tipoSorteo = TipoSorteo.sala;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.sala);
	}

	public void setTipoSorteoSalaCasacion() {
		tipoSorteo = TipoSorteo.salaCasacion;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.salaCasacion);
	}
	
	public void setTipoSorteoSecretariaCorte() {
		tipoSorteo = TipoSorteo.secretariaCorte;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.corteSuprema);
		reasignacionEdit.setTipoReasignacion(TipoReasignacionEnumeration.Type.reasignacionSecretariaCorte);
	}
	
	public void setTipoSorteoCambioTipoCausa() {		
		tipoSorteo = TipoSorteo.cambioTipoCausa;
	}

	public void setTipoSorteoIntegracionSala() {		
		tipoSorteo = TipoSorteo.integracionSala;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.vocalias);
	}

	public void setTipoSorteoIntegracionSalaCasacion() {		
		tipoSorteo = TipoSorteo.integracionSalaCasacion;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.vocaliasCasacion);
	}

	public void setTipoSorteoIntegracionSalaEstudioCasacion() {		
		tipoSorteo = TipoSorteo.integracionSalaEstudioCasacion;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.vocaliasEstudioCasacion);
	}
	
	public void setTipoSorteoIncompetenciaExternaInformatizada1() {		
		tipoSorteo = TipoSorteo.incompetenciaExternaInformatizada;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.juzgado);

//		if (CamaraManager.isCamaraActualCivilCABA()) {
			getReasignacionEdit().setTipoReasignacion(TipoReasignacionEnumeration.Type.busquedaConexidad);
//		}
		/**ATOS COMERCIAL*/
		if(CamaraManager.isCamaraActualCOM())
			omitirSorteoComercialHabilitado = true;	
		/**ATOS COMERCIAL*/
	}
	
	@Deprecated
	//No hay incompetencias de segunda. Se resuelven con la integracion total de vocales
	public void setTipoSorteoIncompetenciaExternaInformatizada2() {		
		tipoSorteo = TipoSorteo.incompetenciaExternaInformatizada;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.sala);
//		if (CamaraManager.isCamaraActualCivilCABA()) {
			getReasignacionEdit().setTipoReasignacion(TipoReasignacionEnumeration.Type.busquedaConexidad);
//		}
	}

	public void setTipoSorteoRecuperacionIncompetencia() {		
		tipoSorteo = TipoSorteo.recuperacionIncompetencia;
	}

	public void setTipoSorteoDesafectacionSalaEstudio() {		
		tipoSorteo = TipoSorteo.desafectacionSalaEstudio;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.salaEstudio);
	}

	public void setTipoSorteoSalaEstudio() {		
		tipoSorteo = TipoSorteo.salaEstudio;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.salaEstudio);
	}
	
	
	public void setTipoSorteoSalaRecursoExtraordinario(){
		tipoSorteo = TipoSorteo.salaRecursoExtraordinario;		
		setTipoRadicacion(TipoRadicacionEnumeration.Type.sala);
		getReasignacionEdit().setTipoReasignacion(TipoReasignacionEnumeration.Type.salaRecursoExtraordinario);
	}
	
	public void setTipoSorteoJuzgadoSentencia() {
		tipoSorteo = TipoSorteo.juzgadoSentencia;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.juzgadoSentencia);		
	}

	public void setTipoSorteoFinJuzgadoSentencia() {		
		tipoSorteo = TipoSorteo.finJuzgadoSentencia;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.juzgadoSentencia);
	}


	public void setTipoSorteoJuzgadoPlenario() {
		tipoSorteo = TipoSorteo.juzgadoPlenario;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.juzgadoPlenario);		
	}

	public void setTipoSorteoTribunalOral() {
		tipoSorteo = TipoSorteo.tribunalOral;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.tribunalOral);
	}

	public void setTipoSorteoFiscaliaTribunalOral() {
		tipoSorteo = TipoSorteo.fiscaliaTribunalOral;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.tribunalOral);
	}
	
	public void setTipoSorteoMediador() {
		tipoSorteo = TipoSorteo.mediador;
		setTipoRadicacion(TipoRadicacionEnumeration.Type.juzgado);
	}

	public boolean isSorteoFiscaliaTribunalOral(){
		return TipoSorteo.fiscaliaTribunalOral == getTipoSorteo();
	}

	public boolean isSorteadoOkExpediente() {
		return getReasignacionEdit().getExpediente().getStatusSorteo() == null
				|| StatusStorteoEnumeration.Type.sorteadoOk.getValue().equals(
						getReasignacionEdit().getExpediente().getStatusSorteo());
	}

	public boolean isSorteadoErrorExpediente() {
		return StatusStorteoEnumeration.Type.sorteadoError.getValue().equals(
				getReasignacionEdit().getExpediente().getStatusSorteo());
	}

	public boolean isSorteoSecretariaCorte(){
		return TipoSorteo.secretariaCorte == getTipoSorteo();
	}
	
	public boolean isSorteoJuzgadoPlenario(){
		return TipoSorteo.juzgadoPlenario == getTipoSorteo();
	}

	public boolean isSorteoJuzgadoSentencia(){
		return TipoSorteo.juzgadoSentencia == getTipoSorteo();
	}
	
	public boolean isSorteoSala(){
		return TipoSorteo.sala == getTipoSorteo();
	}

	public boolean isSorteoSalaCasacion(){
		return TipoSorteo.salaCasacion == getTipoSorteo();
	}
	
	public boolean isSorteoSalaEstudio(){
		return TipoSorteo.salaEstudio == getTipoSorteo();
	}

	public boolean isSorteoAnySala(){
		return TipoSorteo.sala == getTipoSorteo() ||
			TipoSorteo.salaEstudio == getTipoSorteo()||
			TipoSorteo.salaRecursoExtraordinario == getTipoSorteo()
		;
	}
	
	public boolean isSorteoRecursoExtraordinario(){
		return TipoSorteo.salaRecursoExtraordinario == getTipoSorteo();
	}

	public boolean isSorteoJuzgado(){
		return TipoSorteo.juzgado == getTipoSorteo();
	}

	public boolean isSorteoJuzgadoMediacion(){
		return TipoSorteo.juzgadoMediacion == getTipoSorteo();
	}

	public boolean isSorteoTribunalOral(){
		return TipoSorteo.tribunalOral == getTipoSorteo();
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

	public boolean showCompetenciasXX(){
		return ExpedienteMeAdd.instance().showCompetencias() && (isSorteoJuzgadoPlenario() || isSorteoJuzgado() || isSorteoJuzgadoMediacion() || isSorteoTribunalOral());

	}
		
	public boolean showCompetencias() {
		if(getExpediente() != null){
			if (isPenal()) {
				return ExpedienteMeAdd.instance().showCompetenciasPenales(false, getExpediente().getTipoCausa(),  getTipoRadicacion());
			} else {
				return ExpedienteMeAdd.instance().showCompetenciasCiviles(false, getExpediente().getTipoCausa(), getTipoRadicacion(), getExpediente().getObjetoJuicioPrincipal());
			}
		}else{
			return false;
		}

	}		
		
	
	private boolean hasPermission(String target, String action){
			return Identity.instance().hasPermission(target, action);
	}

	public boolean usuarioPuedeSortear(){
		if(isSorteoJuzgado() || isSorteoJuzgadoPlenario() || isSorteoJuzgadoMediacion()){
			return hasPermission("MesaEntrada", "reasignacionJuzgado");
		}else if(isSorteoJuzgadoSentencia()){
			return hasPermission("MesaEntrada", "reasignacionJuzgadoSentencia");
		}else if(isSorteoJuzgadoPlenario()){
			return hasPermission("MesaEntrada", "reasignacionPlenario");
		}else if(isSorteoSalaCasacion()){
			return hasPermission("MesaEntrada", "reasignacionSalaCasacion");
		}else if(isSorteoAnySala()){
			return hasPermission("MesaEntrada", "reasignacionSala");
		}else if(isSorteoTribunalOral()){
			return hasPermission("MesaEntrada", "reasignacionTribunalOral");			
		}else if(isSorteoFiscaliaTribunalOral()){
			return hasPermission("MesaEntrada", "reasignacionFiscaliaTO");			
		}else if(isTipoSorteoIncompetenciaExternaInformatizada()){
			return hasPermission("MesaEntrada", "ingresoIncompetencias");			
		}else if(isTipoSorteoMediador()){
			return hasPermission("MesaEntrada", "reasignacionMediador");			
		}else {
			return false;
		}
	}

	public boolean isForceCompensacion() {
		return forceCompensacion;
	}

	public void setForceCompensacion(boolean forceCompensacion) {
		this.forceCompensacion = forceCompensacion;
	}

	public boolean isIncompetenciaCambioOJ() {
		return incompetenciaCambioOJ;
	}

	public void setIncompetenciaCambioOJ(boolean incompetenciaCambioOJ) {
		this.incompetenciaCambioOJ = incompetenciaCambioOJ;
	}
	
	public ILabeledEnum[] getTiposReasignacion() {		
		Expediente expediente = getReasignacionEdit().getExpediente();
		boolean isPenal = expediente != null && ExpedienteManager.isPenal(expediente);
		boolean isComercial = expediente != null && ExpedienteManager.isComercial(expediente);
		boolean buscaConexidad = (expediente.getTipoCausa() == null || expediente.getTipoCausa().isBuscaConexidad());
		
		if(isTipoRadicacionJuzgado() || isTipoRadicacionPlenario() || isTipoRadicacionJuzgadoSentencia() || isTipoRadicacionAnySala() || isTipoRadicacionPlenario() || isTipoRadicacionSalaCasacion()){
			return TipoReasignacionEnumeration.instance().getTiposReasignacion(getTipoRadicacion(), !isPenal, hasRadicacionesFamilia(), buscaConexidad, isComercial);
		}else if (isTipoRadicacionTribunalOral()){
			return TipoReasignacionTribunalOralEnumeration.instance().getEnumValues();
		}else {
			return null;
		}
	}

	public void setOficinasSorteo(List<Oficina> oficinasSorteo) {
		this.oficinasSorteo = oficinasSorteo;
	}

	private ObjetoJuicio doCambioObjetoJuicio(boolean createActuacion) throws Lex100Exception {
		Expediente expediente = getReasignacionEdit().getExpediente();
		ObjetoJuicio objetoJuicioAnterior = expediente.getObjetoJuicioPrincipal();
		if (objetoJuicioNuevo == null || equals(objetoJuicioNuevo, objetoJuicioAnterior)) {
			errorMsg("expedienteMeReAsignacion.error.objetoJuicio");
			throw new Lex100Exception("Error objeto de juicio vacio o igual al anterior");
		}
		cambiarObjetoJuicioYTipoProceso(expediente, objetoJuicioNuevo, false, createActuacion, false);
		
		sorteo = null;
		return objetoJuicioAnterior;
	}
	
	public void clearObjetoJuicio() {
		objetoJuicioNuevo = null;
		objetoJuicioEnumeration.setSelected(null);
	}

	public ObjetoJuicio getObjetoJuicioNuevo() {
		return objetoJuicioNuevo;
	}

	public void setObjetoJuicioNuevo(ObjetoJuicio objetoJuicioNuevo) {
		this.objetoJuicioNuevo = objetoJuicioNuevo;
	}

	public boolean equals(ObjetoJuicio original, ObjetoJuicio nuevo) {
		if (original == null && nuevo == null) {
			return true;
		} else if (original != null && nuevo != null) {
			return original.getId().equals(nuevo.getId());
		} else {
			return false;
		}
	}


	@Override
	public ObjetoJuicio getObjetoJuicio() {
		return getObjetoJuicioNuevo();
	}
	
	public String excluirOficinasIncompetencia(String toPage, String returnPage) {
		String ret = "";
		if(isIncompetenciaCambioOJ()){
			Expediente expediente = getReasignacionEdit().getExpediente();
			expedienteHome.getEntityManager().find(ObjetoJuicio.class, objetoJuicioNuevo.getId());
			try {
				ObjetoJuicio objetoJuicioAnterior = doCambioObjetoJuicio(false);
				ret = super.excluirOficinas(toPage, returnPage);
				expediente.setObjetoJuicioPrincipal(objetoJuicioAnterior);
				return ret;
			} catch (Lex100Exception e) {
				errorMsg("expedienteMeReasignacion.error.asignacion", e.getMessage());
				ret = "";
			}
		}else{
			ret = super.excluirOficinas(toPage, returnPage);
		}
		return ret;
	}

	public boolean isExcluirOficinaOrigen() {
		return reasignacionEdit.isExcluirOficinaOrigen();
	}

	public void setExcluirOficinaOrigen(boolean excluirOficinaOrigen) {
		if(excluirOficinaOrigen != reasignacionEdit.isExcluirOficinaOrigen()) {
			resetRadicacionesFamilia(false);
		}
		reasignacionEdit.setExcluirOficinaOrigen(excluirOficinaOrigen);
	}

	@Override
	public void resetRadicacionesFamilia(boolean expedienteChanged) {
		if (expedienteChanged) {
			reasignacionEdit.setExcluirOficinaOrigen(true);
		}
		super.resetRadicacionesFamilia(expedienteChanged);
	}
	
	@Override
	public Expediente getExpediente() {
		return reasignacionEdit.getExpediente();
	}

	@Override
	public EntityManager getEntityManager() {
		return expedienteHome.getEntityManager();
	}
	
	@Override
	protected boolean isCambioAsignacion() {
		return true;
	}
	
	

	
	
	public List<Tema> getTemas() {
		if (temas == null && reasignacionEdit.getMateria() != null) {
			temas = new ArrayList<Tema>();
			int i = 0;
			for (Tema tema : temaEnumeration.getItemsByMateria(reasignacionEdit.getMateria())) {
				if (isIncompetenciaCambioOJ()) {
					if(reasignacionEdit != null && reasignacionEdit.getExpediente() != null && reasignacionEdit.getExpediente().getObjetoJuicioPrincipal() != null && 
							!tema.equals(reasignacionEdit.getExpediente().getObjetoJuicioPrincipal().getTema())){
						temas.add(tema);
						if(i == 0){
							setTema(tema);
						}
					}
				} else {
					temas.add(tema);
				}
			}
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
		initFiltroObjetosJuicio(reasignacionEdit.getTipoCausa(), reasignacionEdit.getMateria());
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
	
	
	private void initFiltroObjetosJuicio(TipoCausa tipoCausa, Materia materia) {
		// Search
		
		
		objetoJuicioSearch.setMateria(materia);
		objetoJuicioSearch.setShowMateriaFilter(materia == null);
		objetoJuicioSearch.setTema(getTema());

		objetoJuicioEnumeration.setMateria(materia);

		//objetoJuicioSearch.setRubrosExcluidosList(getRubrosExcluidosList());
		////objetoJuicioEnumeration.setRubrosExcluidosList(getRubrosExcluidosList());

		if (tipoCausa != null) {
			objetoJuicioSearch.setTipoInstancia(tipoCausa.getTipoInstancia());// selecciona
																				// la
																				// instancia
																				// de
																				// los
																				// OJ
			objetoJuicioEnumeration.setTipoInstancia(tipoCausa
					.getTipoInstancia());// selecciona la instancia de los OJ

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

			objetoJuicioSearch.setAbreviaturaTipoInstancia(null);
			objetoJuicioEnumeration.setAbreviaturaTipoInstancia(null);

			objetoJuicioSearch.setCodigosList(null);
			objetoJuicioEnumeration.setCodigosList(null);
//			objetoJuicioSearch.setTipoInstancia(null);
//			objetoJuicioEnumeration.setTipoInstancia(null);
		}
		objetoJuicioSearch.updateFilters();
	}

	public ILabeledEnum[] getTiposIncompetenciaInterna() {
		return tiposIncompetenciaInterna;
	}

	public void setTiposIncompetenciaInterna(
			ILabeledEnum[] tiposIncompetenciaInterna) {
		this.tiposIncompetenciaInterna = tiposIncompetenciaInterna;
	}

	public boolean isTipoSorteoSalaRecursoExtraordinario() {
		return getTipoSorteo() != null && TipoSorteo.salaRecursoExtraordinario == getTipoSorteo();
	}

	public boolean isTipoSorteoCambioTipoCausa() {
		return getTipoSorteo() != null && TipoSorteo.cambioTipoCausa == getTipoSorteo();
	}

	public boolean isTipoSorteoIntegracionSala() {
		return getTipoSorteo() != null && TipoSorteo.integracionSala == getTipoSorteo();
	}

	public boolean isTipoSorteoIntegracionSalaCasacion() {
		return getTipoSorteo() != null && TipoSorteo.integracionSalaCasacion == getTipoSorteo();
	}
	
	public boolean isTipoSorteoIntegracionSalaEstudioCasacion() {
		return getTipoSorteo() != null && TipoSorteo.integracionSalaEstudioCasacion == getTipoSorteo();
	}
	
	public boolean isTipoSorteoMediador() {
		return getTipoSorteo() != null && TipoSorteo.mediador == getTipoSorteo();
	}

	public boolean isTipoSorteoJuzgadoMediacion() {
		return getTipoSorteo() != null && TipoSorteo.juzgadoMediacion == getTipoSorteo();
	}

	public TipoSalaEstudio getTipoSalaEstudio() {
		return tipoSalaEstudio;
	}

	public void setTipoSalaEstudio(TipoSalaEstudio tipoSalaEstudio) {
		this.tipoSalaEstudio = tipoSalaEstudio;
	}	
	
	public boolean isTipoSorteoDesafectacionSalaEstudio() {
		return getTipoSorteo() != null && TipoSorteo.desafectacionSalaEstudio == getTipoSorteo();
	}

	public boolean isTipoSorteoFinJuzgadoSentencia() {
		return getTipoSorteo() != null && TipoSorteo.finJuzgadoSentencia == getTipoSorteo();
	}

	public boolean isTipoSorteoIncompetenciaExternaInformatizada() {
		return getTipoSorteo() != null && TipoSorteo.incompetenciaExternaInformatizada == getTipoSorteo();
	}
	
	public boolean isTipoSorteoRecuperacionIncompetencia() {
		return getTipoSorteo() != null && TipoSorteo.recuperacionIncompetencia == getTipoSorteo();
	}
	
	public boolean isMostrarOficinaDestino() {
		return (isTipoSorteoDesafectacionSalaEstudio() || isTipoSorteoFinJuzgadoSentencia()) // tienen oficina de destino 
				&& reasignacionEdit != null 
				&& reasignacionEdit.getOficinaDestino() != null;
	}
	
	public String getDescripcionOficinaDestino() {
		if (reasignacionEdit != null 
		 	&& reasignacionEdit.getOficinaDestino() != null) {
			return reasignacionEdit.getOficinaDestino().getDescripcion();
		}
		return "";
	}

	public TipoAsignacion getTipoAsignacion() {
		return tipoAsignacion;
	}

	public void setTipoAsignacion(TipoAsignacion tipoAsignacion) {
		this.tipoAsignacion = tipoAsignacion;
	}

	public boolean isErrorExpediente() {
		return errorExpediente;
	}

	public List<OficinaAsignacionExp> getRadicacionesActuales() {
		if(radicacionesActuales == null) {
			radicacionesActuales = OficinaAsignacionExpManager.instance().getRadicaciones(getExpediente());
		}
		return radicacionesActuales;
	}
	
	public boolean isMostrarRadicacionesActuales() {
		return isTipoSorteoCambioTipoCausa() || isTipoSorteoIncompetenciaExternaInformatizada() || isTipoSorteoIntegracionSala() || isTipoSorteoIntegracionSalaCasacion() || isTipoSorteoIntegracionSalaEstudioCasacion() || isTipoSorteoRecuperacionIncompetencia() ;
	}

	public boolean isMostrarListaRadicacionActual() {
		return isTipoSorteoIntegracionSala() || isTipoSorteoIntegracionSalaCasacion() || isTipoSorteoIntegracionSalaEstudioCasacion();
	}
	
	public boolean isMostrarTipoCausaActual() {
		return isTipoSorteoCambioTipoCausa() || isTipoSorteoIncompetenciaExternaInformatizada();
	}

	public boolean isMostrarObjetoJuicioActual() {
		return isTipoSorteoCambioTipoCausa() || isTipoSorteoIncompetenciaExternaInformatizada() || isTipoSorteoRecuperacionIncompetencia() ;
	}
	
	public boolean showConexidadDeclarada() {
		if (reasignacionEdit.getExpediente() != null) {
			return isTipoSorteoIncompetenciaExternaInformatizada()  && isTipoRadicacionJuzgado() && !isPenal() && !ExpedienteManager.instance().isMediacion(reasignacionEdit.getExpediente());
		}
		return false;
	}

	public boolean isSorteoNecesitaRecurso(){
		return false;
	}

	public boolean isMostrarSelectorObjetoJuicio() {
		return isTipoSorteoCambioTipoCausa() || isTipoSorteoRecuperacionIncompetencia() || (isTipoSorteoIncompetenciaExternaInformatizada() && isTipoRadicacionJuzgado()) ;
	}

	public boolean isMostrarSelectorTipoProceso() {
		return (!isPenal() || OficinaManager.instance().isElectoral(OficinaManager.instance().getOficinaActual())) 
				&& objetoJuicioNuevo != null ;
	}
	public boolean isMostrarSelectorTipoCausa() {
		return isTipoSorteoCambioTipoCausa() || 
		(isTipoSorteoIncompetenciaExternaInformatizada() && isTipoRadicacionJuzgado() && isMultipleTipoCausa()) || 
		isTipoSorteoRecuperacionIncompetencia();
	}
	
	public boolean isMostrarSelectorTipoRecurso() {
		if (isPermitirIncompetenciaSegundaConRecursoAbierto()) {
			return (isTipoSorteoIncompetenciaExternaInformatizada() && isTipoRadicacionSala() && isTieneRecursosPendientes(reasignacionEdit.getExpediente()))
					||
					(isTipoSorteoRecuperacionIncompetencia() && (reasignacionEdit.getExpediente() != null) && isTieneRecursosPendientes(reasignacionEdit.getExpediente())
					);
		}
		return false;
	}

	public boolean isMostrarTipoRecursoActual() {
		return (recursoEdit.getRecursoSorteo() != null) && 
				( isSorteoSala()
				|| 
				(isPermitirIncompetenciaSegundaConRecursoAbierto() 
						&&
						(isTipoSorteoIncompetenciaExternaInformatizada() && isTipoRadicacionSala() && isTieneRecursosPendientes(reasignacionEdit.getExpediente()))
						|| (isTipoSorteoRecuperacionIncompetencia() && (reasignacionEdit.getExpediente() != null) && isTieneRecursosPendientes(reasignacionEdit.getExpediente()))
				)
				);
	}
	private void initDatosIncompetenciaExternaInformatizada() {
		setConexidadDeclarada(new ConexidadDeclarada());
		
		conexidadDeclaradaAnterior = null;
		if (ExpedienteManager.hasConexidadDeclarada(reasignacionEdit.getExpediente())) {
			conexidadDeclaradaAnterior = new ConexidadDeclarada(reasignacionEdit.getExpediente().getConexidadDeclarada());
		}
		Expediente expedienteConexo = FamiliaManager.instance().getExpedienteVinculadoConexoJuzgado(reasignacionEdit.getExpediente());
		if(expedienteConexo != null) {
			getConexidadDeclarada().setAnioExpedienteConexo(expedienteConexo.getAnio());
			getConexidadDeclarada().setNumeroExpedienteConexo(expedienteConexo.getNumero());
			getConexidadDeclarada().setExpedienteConexo(expedienteConexo);
		} else if (conexidadDeclaradaAnterior != null) {
			CreateExpedienteAction.copyConexidadDeclarada(conexidadDeclaradaAnterior, getConexidadDeclarada());
		}
		updateConexidadDeclarada();
		if ((getConexidadDeclarada().getExpedienteConexo() == null) || (getConexidadDeclarada().getOficina() == null) || !getCamara().equals(getConexidadDeclarada().getOficina().getCamara())) {
			resetConexidadDeclarada();
		}
	}

	public String getDescripcionTipoCausaActual() {
		String result = "(ninguno)";
		if (reasignacionEdit.getExpediente() != null) {
			TipoCausa tipoCausa = reasignacionEdit.getExpediente().getTipoCausa();
			if (tipoCausa != null) {
				result = tipoCausa.getCodigo() + " - " + tipoCausa.getDescripcion();
			} else {
				result = TipoCausaEnumeration.DESCRIPCION_NOR;
			}
		}
		return result;
	}
	
	public void tipoCausaChanged(ValueChangeEvent event) {
		TipoCausa tipoCausa = (TipoCausa) event.getNewValue();
		setTema(null);
		initFiltroObjetosJuicio(tipoCausa, reasignacionEdit.getMateria());
		if (tipoCausa == null || TipoInstanciaEnumeration.isPrimeraInstancia(tipoCausa.getTipoInstancia())) {
			setTipoRadicacion(TipoRadicacionEnumeration.Type.juzgado);
		} else if (TipoInstanciaEnumeration.isApelaciones(tipoCausa.getTipoInstancia())) {
			setTipoRadicacion(TipoRadicacionEnumeration.Type.sala);
		} else {
			setTipoRadicacion(null);
		}
	}

	public void tipoReasignacionChanged(ValueChangeEvent event) {
		if(event!=null){
			TipoReasignacionEnumeration.Type newValue = ((TipoReasignacionEnumeration.Type) event.getNewValue());
			if(newValue!=null){
				initOficinasExcluidas();
				resetRadicacionesFamilia(false);
				setOficinaDefault(newValue);
			}
		}
	}
	
	private void setOficinaDefault(TipoReasignacionEnumeration.Type tipoAsginacionNew) {
		if(TipoCambioAsignacionEnumeration.isOficinaAnteriorDefault((String)tipoAsginacionNew.getValue())){
			CambioAsignacionExp ultimaRadicacion = null;
			List<CambioAsignacionExp> radicacionesAnteriores = CambioAsignacionExpSearch.findByTipoRadicacion(getEntityManager(), getExpediente(), getCamara(), (String)getTipoRadicacion().getValue());
			for(CambioAsignacionExp radicacion: radicacionesAnteriores) {
				if(ultimaRadicacion == null || ultimaRadicacion.getFechaAsignacion().compareTo(radicacion.getFechaAsignacion()) < 0){
					ultimaRadicacion = radicacion;
				}
			}
			if(ultimaRadicacion != null) {
				List<Oficina> oficinas = getOficinasDestino();
				for (Oficina oficina : oficinas) {
					if(oficina.getCodigo().equals(ultimaRadicacion.getOficinaAnterior().getCodigo())){
						reasignacionEdit.setOficinaDestino(oficina); 
					}
				}
				
			}
		}
	}
	
	public boolean showFechaInformacion() {
		return showFechaInformacion(reasignacionEdit.getTipoCausa());
	}

	public boolean showFechaInformacion(TipoCausa tipoCausa) {
		boolean show = showOrigen();
		return show;
	}

	public boolean showOrigen() {
		return showOrigen(reasignacionEdit.getTipoCausa());
	}

	public boolean showOrigen(TipoCausa tipoCausa) {
		if (!isTipoSorteoCambioTipoCausa() && !isTipoSorteoIncompetenciaExternaInformatizada()) {
			return false;
		}
		return ExpedienteMeAdd.showOrigen(tipoCausa);
	}

	public boolean showFojasPase() {
		return !ExpedienteManager.isPenal(getExpediente()) && 
			(isSorteoJuzgado() || isSorteoJuzgadoSentencia() || isTipoSorteoFinJuzgadoSentencia() ||
			(isTipoSorteoIncompetenciaExternaInformatizada() && isTipoRadicacionJuzgado())					
		);
	}

	public boolean showFojasRecurso() {
		return !ExpedienteManager.isPenal(getExpediente()) &&
				( isSorteoSala()
				|| (isPermitirIncompetenciaSegundaConRecursoAbierto() && isTipoSorteoIncompetenciaExternaInformatizada() && isTipoRadicacionSala() && isTieneRecursosPendientes(reasignacionEdit.getExpediente())));
	}
	
	public boolean showFojasExpediente() {
		return !(ExpedienteManager.isPenal(getExpediente()) && isSorteoSala());
	}

	private boolean isJuzgadoOSecretariaPenalOrdinario() {
		Oficina oficinaActual = SessionState.instance().getGlobalOficina();
		return CamaraManager.isCamaraPenalOrdinario(CamaraManager
				.getCamaraActual())
				&& (OficinaManager.instance()
						.isSecretariaJuzgado(oficinaActual) || OficinaManager
						.instance().isJuzgado(oficinaActual));
	}
	
	public boolean showNumeroCodigo() {
		return showNumeroCodigo(reasignacionEdit.getTipoCausa());
	}

	public boolean showNumeroCodigo(TipoCausa tipoCausa) {
		boolean show = showOrigen() && CamaraManager.isCamaraActualCivilCABA();
		return show;
	}

	public boolean showJuezExortante() {
		TipoCausa tipoCausa = reasignacionEdit.getTipoCausa();
		return showJuezExortante(tipoCausa);
	}

	public boolean showJuezExortante(TipoCausa tipoCausa) {
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

	public boolean showCaratulaOrigen() {
		TipoCausa tipoCausa = reasignacionEdit.getTipoCausa();
		return showCaratulaOrigen(tipoCausa);
	}

	public boolean showCaratulaOrigen(TipoCausa tipoCausa) {
		if (!isTipoSorteoCambioTipoCausa() && !isTipoSorteoIncompetenciaExternaInformatizada()) {
			return false;
		}
		if (tipoCausa == null)
			return false;
		boolean show = false;
		if (tipoCausa != null) {
			if (TipoCausaEnumeration.isExhorto(tipoCausa)
					|| TipoCausaEnumeration.isInhibitoria(tipoCausa)) {
				show = true;
			}
		}
		return show;
	}

	private boolean isDependenciaOpcional(TipoCausa tipoCausa) {
		return TipoCausaEnumeration.isExequatur(tipoCausa);
	}
	
	private TipoInstancia getPrimeraInstancia() {
		return TipoInstanciaSearch
				.findByAbreviatura(
						getEntityManager(),
						TipoInstanciaEnumeration.TIPO_INSTANCIA_PRIMERA_INSTANCIA_ABREVIATURA);
	}

	private TipoInstancia getInstanciaApelaciones() {
		return TipoInstanciaSearch
				.findByAbreviatura(
						getEntityManager(),
						TipoInstanciaEnumeration.TIPO_INSTANCIA_APELACIONES_ABREVIATURA);
	}

	private TipoInstancia getInstanciaCasacion() {
		return TipoInstanciaSearch
				.findByAbreviatura(
						getEntityManager(),
						TipoInstanciaEnumeration.TIPO_INSTANCIA_CASACION_ABREVIATURA);
	}
	
	public List<SelectItem> getVocaliasARecusar() {
		List<SelectItem> selectItemList = new ArrayList<SelectItem>();
		for(Oficina oficina: vocaliasSalaActuales) {
			String descripcion = oficina.getDescripcion();
			String titular = getTitularVocalia(oficina);
			if (Strings.isEmpty(titular)) {
				titular = "vacante"; 
			}
			selectItemList.add(new SelectItem(vocaliasSalaActuales.indexOf(oficina), descripcion + " ("+titular+")"));
		}
		if (!this.integracionTotal) {
			selectItemList.add(new SelectItem(-2, "Sortear NUEVO vocal"));
		}
		return selectItemList;
	}
	
	public List<Oficina> getVocaliasSalaActuales() {
		return vocaliasSalaActuales;
	}

	public void setVocaliasSalaActuales(List<Oficina> vocaliasSalaActuales) {
		this.vocaliasSalaActuales = vocaliasSalaActuales;
	}

	public Oficina getVocaliaSeleccionada() {
		return vocaliaSeleccionada;
	}

	public void setVocaliaSeleccionada(Oficina vocaliaSeleccionada) {
		this.vocaliaSeleccionada = vocaliaSeleccionada;
	}
	
	public Integer getIndiceVocaliaSeleccionada() {
		return  vocaliasSalaActuales.indexOf(this.vocaliaSeleccionada);
	}
	
	public void setIndiceVocaliaSeleccionada(Integer index) {
		if (index > -1) {
			vocaliaSeleccionada = vocaliasSalaActuales.get(index);
		} else {
			vocaliaSeleccionada = null;
			nuevoVocal = index == -2;
		}
	}
	
	public boolean showSelectorVocalias() {
		return isTipoSorteoIntegracionSala() || isTipoSorteoIntegracionSalaCasacion() || isTipoSorteoIntegracionSalaEstudioCasacion();
	}
	
	public String getTitularVocalia(Oficina vocalia) {
		if (vocalia == null || vocalia.getId() == null) {
			return null;
		}
		String value = titularVocaliaMap.get(vocalia);
		if (value == null && vocalia != null) {
			PersonalOficina juez = PersonalOficinaManager.instance().getJuez(vocalia);
			if (juez != null) {
				value = juez.getApellidosNombre();
			} else {
				value = NOT_FOUND;
			}
			titularVocaliaMap.put(vocalia, value);
		}
		if (value != NOT_FOUND) {
			return value;
		} else {
			return null;
		}
	}
	
	public boolean showTiposReasignacion(){
		boolean ret;
		
		if(isTipoSorteoIncompetenciaExternaInformatizada() || isTipoSorteoRecuperacionIncompetencia()) {
			ret = false;
		} else {
			ret = !isTipoSorteoSalaRecursoExtraordinario() && usuarioPuedeSortear() && 
	 			getReasignacionEdit() != null && getReasignacionEdit().getExpediente() != null && !isTipoSorteoMediador();	
			if (CamaraManager.isCamaraActualMultiMateria() && isTipoRadicacionTribunalOral() && Identity.instance().hasPermission("MesaEntrada","reasignacionTribunalOral")) {
				ret &= true;
			} else {
				ret &= OficinaManager.instance().inAlgunaMesaDeEntrada();
			}
		}
		return ret;
	}

	public Boolean soloRecursosAsignados(){
		Boolean soloRecursosAsignados = true;
		if (isTipoSorteoIncompetenciaExternaInformatizada() || isTipoSorteoRecuperacionIncompetencia()) {
			soloRecursosAsignados = null;	//asignados y no asignados
		}
		return soloRecursosAsignados;
	}
	
	private boolean isTieneRecursosPendientes(Expediente expediente) {
		if (Boolean.TRUE.equals(soloRecursosAsignados())){
			return expediente.isTieneRecursosAsignadosPteResolver();
		} else if (Boolean.FALSE.equals(soloRecursosAsignados())){
			return expediente.isTieneRecursosPteAsignar();
		} else {
			return expediente.isTieneRecursosPteResolver();
		}
	}

	public boolean isMultiplesRecursosASeleccionar(){
		List<RecursoExp> list = recursoEdit.getListaRecursosSalaASeleccionar(getReasignacionEdit().getExpediente(), soloRecursosAsignados());
		return list != null && list.size() > 1;
	}

	public List<RecursoExp> getListaRecursosASeleccionar(){
		return recursoEdit.getListaRecursosSalaASeleccionar(getExpediente(), soloRecursosAsignados());
	}

	public RecursoEdit getRecursoEdit() {
		return recursoEdit;
	}

	public void setRecursoEdit(RecursoEdit recursoEdit) {
		this.recursoEdit = recursoEdit;
	}

	public FojasEdit getFojasEdit() {
		return fojasEdit;
	}

	public void setFojasEdit(FojasEdit fojasEdit) {
		this.fojasEdit = fojasEdit;
	}

	public String getDestinoAsignacion() {
		return destinoAsignacion;
	}

	public void setDestinoAsignacion(String destinoAsignacion) {
		this.destinoAsignacion = destinoAsignacion;
	}
	
	public void onChangeDestinoAsignacion(ValueChangeEvent valueChangeEvent){
		if(reasignacionEdit != null){
			reasignacionEdit.setOficinaDestino(null);
		}
	}
	
	public boolean showSelectorDestinos(){
		return isSorteoJuzgado() && 
		(OficinaManager.instance().inOficinaRadicacionOMesa(getRadicacionActual()));
	}

	public boolean isMultipleTipoCausa(){
		return getTipoCausaList().size() > 1;
	}
	
	public List<TipoCausa> getTipoCausaList(){
		TipoCausaEnumeration.instance().setIncompetenciaInformatizadaSelected(null);
		TipoCausaEnumeration.instance().setIncompetenciaNoInformatizadaSelected(null);
		
		if(isTipoSorteoIncompetenciaExternaInformatizada()){
			List<String> abreviaturasTipoInstancia = new ArrayList<String>();
			if (isTipoRadicacionAnySala()) {
				abreviaturasTipoInstancia.add(TipoInstanciaEnumeration.TIPO_INSTANCIA_APELACIONES_ABREVIATURA);
			} else {
				abreviaturasTipoInstancia.add(TipoInstanciaEnumeration.TIPO_INSTANCIA_PRIMERA_INSTANCIA_ABREVIATURA);
			}
			TipoCausaEnumeration.instance().setAbreviaturasTipoInstanciaFilter(new HashSet<String>(abreviaturasTipoInstancia));
			
			if(CamaraManager.isCamaraCCF(SessionState.instance().getGlobalCamara())){
				TipoCausaEnumeration.instance().setIncompetenciaInformatizadaSelected(true);
			}
			/*
			 * se buscan solo los tipos de causa segun el expediente seleccionado
			 * ya que sino puede tomar rubros incorrectos y cae en bolilleros invalidos
			 */
			Expediente expedienteSelected = reasignacionEdit.getExpediente();
			if (expedienteSelected!=null){
				Materia materia = expedienteSelected.getMateria();
				if(materia!=null && materia.getGrupo()!=null){
					TipoCausaEnumeration.instance().setGrupoMateriaFilter(materia.getGrupo());
				}
			}
		}
		return TipoCausaEnumeration.instance().getItems(); 
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoRecurso> getTiposRecurso() {
		if ((getExpediente() != null) && (recursoEdit.getRecursoSorteo() != null)){
			return TipoRecursoEnumeration.getItemsByTipoSala(getEntityManager(), reasignacionEdit.getMateria(), recursoEdit.getRecursoSorteo().getTipoRecurso().getTipoSala());
		}
		return null;
	}


	@Override
	public boolean isPuedeSeleccionarRadicacionesFamiliaPrevia() {
		if (isTipoSorteoIncompetenciaExternaInformatizada() || isTipoSorteoRecuperacionIncompetencia()) {
			return false;
		}
		return super.isPuedeSeleccionarRadicacionesFamiliaPrevia();
	}

	// Conexidad Declarada

	@Override
	public Materia getMateria() {
		return reasignacionEdit.getMateria();
	}

	public List<Oficina> getOficinasConexidadDeclarada() { 
		return getOficinasDestino();
	}

	private boolean isPermitirIncompetenciaSegundaConRecursoAbierto() {
		return false;	// Según trabajan actualmente, no deden venir recursos abiertos de la otra camara. 
	}

	public boolean isNuevoVocal() {
		return nuevoVocal;
	}

	public void setNuevoVocal(boolean nuevoVocal) {
		this.nuevoVocal = nuevoVocal;
	}

	public boolean isIntegracionTotal() {
		return integracionTotal;
	}

	public void setIntegracionTotal(boolean integracionTotal) {
		this.integracionTotal = integracionTotal;
		if (integracionTotal) {
			this.vocaliaSeleccionada = null;
		}
	}

	public boolean isSorteandoExpediente() {
		if (reasignacionEdit.getExpediente() != null) {
			return StatusStorteoEnumeration.Type.sorteando.getValue().equals(
					reasignacionEdit.getExpediente().getStatusSorteo());
		}
		return false;
	}
	public List<TipoProceso> getTiposProceso(){
		return TipoProcesoEnumeration.instance().getItemsByMateria(getMateria());
	}

	public void setTipoProcesoNuevo(TipoProceso tipoProcesoNuevo) {
		this.tipoProcesoNuevo = tipoProcesoNuevo;
	}

	public TipoProceso getTipoProcesoNuevo() {
		if (tipoProcesoNuevo==null)
			return getExpediente().getTipoProceso();
		return tipoProcesoNuevo;
	}
	private void checkCambioUnico() throws EntityOperationException {
		if(TipoCambioAsignacionEnumeration.isTipoCambioUnico(getCodigoTipoCambioAsignacion())){
			if (isSorteoJuzgado() || isSorteoJuzgadoSentencia() || isSorteoAnySala()) {
				List<CambioAsignacionExp> radicacionesAnteriores = CambioAsignacionExpSearch.findByTipoRadicacion(getEntityManager(), getExpediente(), getCamara(), (String)getTipoRadicacion().getValue());
				for(CambioAsignacionExp radicacion: radicacionesAnteriores) {
					if (getCodigoTipoCambioAsignacion().equals(radicacion.getCodigoTipoCambioAsignacion())) {
						errorMsg("expedienteMeReasignacion.error.cambioUnico");
						throw new EntityOperationException();
					}
				}
			}
		}
	}
	
	/*******ATOS COMERCIAL*******/
	//Fecha: 26-06-2014
	//Desarrollador:  Luis Suarez
	private boolean selectAll;
	
	public Boolean getSelectAll() {
		return selectAll;
	}
	
	public void setSelectAll(Boolean value) {
		if (value != null) {
			if (value) {
				for (Oficina oficina : reasignacionEdit.getVocaliasSalaSorteo()) {
					reasignacionEdit.getVocaliasSalaSorteoSelectionMap().put(oficina, true);
				}
			} else {
				reasignacionEdit.getVocaliasSalaSorteoSelectionMap().clear();
			}
		}
		selectAll = value;
	}
	
	public boolean isOmitirSorteoComercialHabilitado() {
		return omitirSorteoComercialHabilitado;
	}
	
	public void setOmitirSorteoComercialHabilitado(
			boolean omitirSorteoComercialHabilitado) {
		this.omitirSorteoComercialHabilitado = omitirSorteoComercialHabilitado;
	}
	/*******ATOS COMERCIAL*******/
	
}
