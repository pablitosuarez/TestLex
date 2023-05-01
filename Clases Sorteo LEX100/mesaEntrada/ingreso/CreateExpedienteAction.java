package com.base100.lex100.mesaEntrada.ingreso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.UserTransaction;
import org.jboss.seam.util.Strings;

import com.base100.document.util.GUID;
import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.Lex100Exception;
import com.base100.lex100.component.audit.AbstractLogicalDeletion;
import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.currentdate.CurrentDateManager;
import com.base100.lex100.component.enumeration.DependenciaEnumeration;
import com.base100.lex100.component.enumeration.EstadoPoderEnumeration;
import com.base100.lex100.component.enumeration.NaturalezaSubexpedienteEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionLex100Enumeration;
import com.base100.lex100.component.enumeration.TipoCambioAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoCausaEnumeration;
import com.base100.lex100.component.enumeration.TipoDocumentoIdentidadEnumeration;
import com.base100.lex100.component.enumeration.TipoGiroEnumeration;
import com.base100.lex100.component.enumeration.TipoGiroEnumeration.Type;
import com.base100.lex100.component.enumeration.TipoInformacionEnumeration;
import com.base100.lex100.component.enumeration.TipoLetradoEnumeration;
import com.base100.lex100.component.enumeration.TipoOficinaEnumeration;
import com.base100.lex100.component.enumeration.TipoProcesoEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.enumeration.TipoSubexpedienteEnumeration;
import com.base100.lex100.component.enumeration.TipoVinculadoEnumeration;
import com.base100.lex100.controller.entity.delitoExpediente.DelitoExpedienteHome;
import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.controller.entity.expedienteEspecial.ExpedienteEspecialHome;
import com.base100.lex100.controller.entity.expedienteIngreso.ExpedienteIngresoHome;
import com.base100.lex100.controller.entity.interviniente.IntervinienteHome;
import com.base100.lex100.controller.entity.intervinienteExp.IntervinienteExpHome;
import com.base100.lex100.controller.entity.intervinienteExp.IntervinienteExpSearch;
import com.base100.lex100.controller.entity.letrado.LetradoHome;
import com.base100.lex100.controller.entity.letradoIntervinienteExp.LetradoIntervinienteExpHome;
import com.base100.lex100.controller.entity.numeradorExpediente.NumeradorExpedienteHome;
import com.base100.lex100.controller.entity.objetoJuicio.ObjetoJuicioSearch;
import com.base100.lex100.controller.entity.oficinaAsignacionExp.OficinaAsignacionExpSearch;
import com.base100.lex100.controller.entity.parametroExpediente.ParametroExpedienteSearch;
import com.base100.lex100.controller.entity.poder.PoderSearch;
import com.base100.lex100.controller.entity.registrosContables.migracion.SorteoExpedienteWeb;
import com.base100.lex100.controller.entity.tipoBolillero.TipoBolilleroSearch;
import com.base100.lex100.controller.entity.tipoInstancia.TipoInstanciaSearch;
import com.base100.lex100.controller.entity.tipoSubexpediente.TipoSubexpedienteSearch;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Conexidad;
import com.base100.lex100.entity.ConexidadDeclarada;
import com.base100.lex100.entity.CriterioCnx;
import com.base100.lex100.entity.Delito;
import com.base100.lex100.entity.DelitoExpediente;
import com.base100.lex100.entity.DemandaPoder;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.ExpedienteEspecial;
import com.base100.lex100.entity.ExpedienteInfo;
import com.base100.lex100.entity.ExpedienteIngreso;
import com.base100.lex100.entity.Interviniente;
import com.base100.lex100.entity.IntervinienteExp;
import com.base100.lex100.entity.Letrado;
import com.base100.lex100.entity.LetradoIntervinienteExp;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Moneda;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.Parametro;
import com.base100.lex100.entity.ParametroExpediente;
import com.base100.lex100.entity.Poder;
import com.base100.lex100.entity.RecursoExp;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.TipoCambioAsignacion;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.TipoInformacion;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoInterviniente;
import com.base100.lex100.entity.TipoLetrado;
import com.base100.lex100.entity.TipoProceso;
import com.base100.lex100.entity.TipoRecurso;
import com.base100.lex100.entity.TipoSubexpediente;
import com.base100.lex100.entity.VinculadosExp;
import com.base100.lex100.homelistener.ExpedienteHomeListener;
import com.base100.lex100.manager.AbstractManager;
import com.base100.lex100.manager.accion.AccionCambioAsignacion;
import com.base100.lex100.manager.accion.impl.AccionException;
import com.base100.lex100.manager.actuacionExp.ActuacionExpManager;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.configuracionMateria.ConfiguracionMateriaManager;
import com.base100.lex100.manager.cuaderno.CuadernoManager;
import com.base100.lex100.manager.expediente.ExpedienteIngresoManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.manager.expediente.OficinaAsignacionExpManager;
import com.base100.lex100.manager.intervinienteExp.IntervinienteExpManager;
import com.base100.lex100.manager.oficina.OficinaManager;
import com.base100.lex100.manager.poder.PoderManager;
import com.base100.lex100.manager.usuarioExpediente.UsuarioExpedienteManager;
import com.base100.lex100.manager.vinculadosExp.VinculadosExpManager;
import com.base100.lex100.manager.web.WebManager;
import com.base100.lex100.mesaEntrada.asignacion.ExpedienteMeAsignacion;
import com.base100.lex100.mesaEntrada.conexidad.ConexidadManager;
import com.base100.lex100.mesaEntrada.conexidad.FamiliaManager;
import com.base100.lex100.mesaEntrada.recurso.TipoRecursoFinderMe;
import com.base100.lex100.mesaEntrada.sorteo.MesaSorteo;
import com.base100.lex100.mesaEntrada.sorteo.RubrosInfo;
import com.base100.lex100.mesaEntrada.sorteo.SorteoException;
import com.base100.lex100.mesaEntrada.sorteo.SorteoManager;
import com.base100.lex100.mesaEntrada.sorteo.SorteoOperation;
import com.base100.lex100.mesaEntrada.sorteo.SorteoParametros;

@Name("createExpedienteAction")
public class CreateExpedienteAction extends ScreenController
{
    private static final String CODIGO_SORTEO_JUZGADOS = "JUZGADOS";
	private static final String DEFAULT_TIPO_LETRADO_CODIGO = TipoLetradoEnumeration.PATROCINANTE_CODIGO;
	@In(create=true)
	ExpedienteHome expedienteHome;
    @In(create=true)
	ExpedienteIngresoManager expedienteIngresoManager;
    @In(create=true)
    private IntervinienteExpHome intervinienteExpHome;
    @In(create=true)
    private IntervinienteExpSearch intervinienteExpSearch;
    @In(create=true)
    private IntervinienteHome intervinienteHome;
    @In(create=true)
    private LetradoHome letradoHome;
    @In(create=true)
    private LetradoIntervinienteExpHome letradoIntervinienteExpHome ;
    @In(create=true)
    private TipoLetradoEnumeration tipoLetradoEnumeration;
    @In(create=true)
    private EntityManager entityManager;
	@In(create=true)
	private MesaSorteo mesaSorteo;
	@In(create=true)
	private DependenciaEnumeration dependenciaEnumeration;
	@In(create=true)
	private CurrentDateManager currentDateManager;

	Map<IntervinienteExp, Boolean> intervinientesSeleccionados = new HashMap<IntervinienteExp, Boolean>();

	private static final int INTERVINIENTE_ACTOR_ORDER = 1;
	private static final int INTERVINIENTE_DEMANDADO_ORDER = 2;
	
	/** ATOS OVD */
	@In(create=true)
	ExpedienteManager expedienteManager;
	/** ATOS OVD */
	
	public Sorteo buscaSorteo(ExpedienteMeAdd expedienteEdit) throws Lex100Exception {
		TipoRadicacionEnumeration.Type tipoRadicacion = expedienteEdit.getTipoRadicacion();
		Oficina oficinaActual = getOficinaActual();
		boolean inMesaDeEntrada = OficinaManager.instance().isMesaDeEntrada(oficinaActual);
		
		RubrosInfo rubrosInfo = calculaRubrosAsignacion(expedienteEdit, tipoRadicacion);
		Oficina oficinaSorteadora = oficinaActual;
		Materia materia = expedienteEdit.getMateria();
		
		Sorteo sorteo = null;
		if(!expedienteEdit.isIngresoAnteriorSistema()){
			boolean sorteoRequired = expedienteEdit.isIngresoDiferido() || inMesaDeEntrada;
	
			sorteo = mesaSorteo.buscaSorteo(oficinaSorteadora, 
					!sorteoRequired,
					tipoRadicacion, 
					materia.getGrupo(), 
					materia, 
					CamaraManager.isCamaraActualCorteSuprema() ? null : expedienteEdit.getCompetencia(), 
					rubrosInfo.getRubroAsignacion(), 
					rubrosInfo.getRubro(),
					sorteoRequired);
		}
		return sorteo;
	}

	private void checkRecurso(ExpedienteMeAdd expedienteEdit) throws Lex100Exception {
		if(expedienteEdit.isIngresoASala() && expedienteEdit.getRecursoEdit().getTipoRecurso() == null){
			boolean error = false;
			if(expedienteEdit.getRecursoEdit().getFechaPresentacion()==null){
				errorMsg("expedienteMeAsignacion.error.fechaPresentacionRequerido");
				error = true;
			}else if(expedienteEdit.getRecursoEdit().getFechaPresentacion().after(currentDateManager.getCurrentDate())) {
				errorMsg("expedienteMeAsignacion.error.fechaPresentacionPosterior");
				error = true;
			}
			TipoRecurso tipoRecurso = calculateTipoRecurso(expedienteEdit);
			if(tipoRecurso == null){
				errorMsg("ingresoExpediente.tipoRecurso.empty");
				error = true;
			}
			if(error){
				throw new Lex100Exception("Error en definicion del recurso");
			}
			expedienteEdit.getRecursoEdit().setTipoRecurso(tipoRecurso);
		}
	}

	@Transactional
	public void crearExpedienteYAsignarOficina(ExpedienteMeAdd expedienteEdit) throws Lex100Exception {		
		entityManager.joinTransaction();
		try {
			checkRecurso(expedienteEdit);
			
			TipoRadicacionEnumeration.Type tipoRadicacion = expedienteEdit.getTipoRadicacion();	//ITO (Incompetencia TO)
			Oficina oficinaActual = getOficinaActual();
			boolean inMesaDeEntrada = OficinaManager.instance().isMesaDeEntrada(oficinaActual);
			
		 	RubrosInfo rubrosInfo = calculaRubrosAsignacion(expedienteEdit, tipoRadicacion);
			Oficina oficinaSorteadora = oficinaActual;
			Materia materia = expedienteEdit.getMateria();
			
			Sorteo sorteo = null;
			if(!expedienteEdit.isIngresoAnteriorSistema()){
				boolean sorteoRequired = expedienteEdit.isIngresoDiferido() || expedienteEdit.isRadicacionPrevia() || inMesaDeEntrada;
		
				sorteo = mesaSorteo.buscaSorteo(oficinaSorteadora, 
						!sorteoRequired,
						tipoRadicacion, 
						materia.getGrupo(), 
						materia, 
						expedienteEdit.getCompetencia(), 
						rubrosInfo.getRubroAsignacion(), 
						rubrosInfo.getRubro(),
						sorteoRequired);
			}
			
			
			if (expedienteEdit.isIngresoDiferido() || expedienteEdit.isRadicacionPrevia() || 
					(TipoCausaEnumeration.isProvenienteMediacion(expedienteEdit.getTipoCausa()) && isAsignaJuzgadoMediacion(expedienteEdit.getTipoCausa())&& !expedienteEdit.getTipoCausa().isBuscaConexidad())) {

				ExpedienteIngreso expedienteIngreso = ingresarExpediente(expedienteEdit, expedienteEdit.isIngresoDiferido()? expedienteEdit.getIngresoDiferidoEdit().getFechaIngreso(): null);
				
				TipoAsignacionEnumeration.Type tipoAsignacion;
				TipoAsignacionLex100Enumeration.Type tipoAsignacionLex100;
				Oficina oficinaDestino = null;
				
				String tipoCambioAsignacion = TipoCambioAsignacionEnumeration.INGRESO_CAUSAS_TRAMITE;
				
				if(expedienteEdit.isIngresoAnteriorSistema()){
					tipoAsignacion = TipoAsignacionEnumeration.fromValue(expedienteEdit.getIngresoDiferidoEdit().getTipoAsignacion(), 
							TipoRadicacionEnumeration.calculateCodigoTipoInstanciaByTipoRadicacion(tipoRadicacion),
							CamaraManager.isCamaraActualCivilCABA()? TipoAsignacionEnumeration.GRUPO_CIVIL: TipoAsignacionEnumeration.GRUPO_PENAL);
//					tipoAsignacion = TipoAsignacionEnumeration.getTipoAsignacionAnteriorAlSistema(tipoRadicacion);
					oficinaDestino = expedienteEdit.getIngresoDiferidoEdit().getOficinaDestino();
					tipoAsignacionLex100 = TipoAsignacionLex100Enumeration.Type.anteriorAlSistema;
				} else if (expedienteEdit.isIngresoDiferido()){
					buscaConexidadInformativa(entityManager, expedienteIngreso.getExpediente(), null, tipoRadicacion, sorteo, rubrosInfo.getRubro(), oficinaSorteadora, expedienteEdit.isIgnoreCheckDobleInicio(), expedienteEdit.getFojasEdit());
					tipoAsignacion = TipoAsignacionEnumeration.fromValue(expedienteEdit.getIngresoDiferidoEdit().getTipoAsignacion(), 
							TipoRadicacionEnumeration.calculateCodigoTipoInstanciaByTipoRadicacion(tipoRadicacion),
							CamaraManager.isCamaraActualCivilCABA()? TipoAsignacionEnumeration.GRUPO_CIVIL: TipoAsignacionEnumeration.GRUPO_PENAL);
					//tipoAsignacion = TipoAsignacionEnumeration.getTipoAsignacionIngresoDiferido(tipoRadicacion);
					oficinaDestino = expedienteEdit.getIngresoDiferidoEdit().getOficinaDestino();
					tipoAsignacionLex100 = TipoAsignacionLex100Enumeration.Type.ingresoDiferido;
					
				} else if (TipoCausaEnumeration.isProvenienteMediacion(expedienteEdit.getTipoCausa())) {
					tipoAsignacion = TipoAsignacionEnumeration.Type.asignacion_ProvenienteMediacion;
					oficinaDestino = expedienteEdit.getExpedienteMediacionOficinaRadicacion();
					tipoAsignacionLex100 = TipoAsignacionLex100Enumeration.Type.provenienteMediacion;
					tipoCambioAsignacion = TipoCambioAsignacionEnumeration.INGRESO_JUZGADO_MEDIACION;
				} else {
					tipoAsignacion = TipoAsignacionEnumeration.getTipoAsignacionRadicacionPrevia(tipoRadicacion);
					oficinaDestino = expedienteEdit.getRadicacionPreviaEdit().getOficinaDestino();
					tipoAsignacionLex100 = TipoAsignacionLex100Enumeration.Type.radicacionPrevia;
				}
				String descripcionCambioAsignacion = getDescripcionCambioAsignacion(expedienteEdit, tipoCambioAsignacion, tipoRadicacion);
				
				// OJO Provisional para que en CSJ gire los expedientes aunque sea por radicacion previa
				TipoGiroEnumeration.Type tipoGiro = null;
				if(CamaraManager.isCamaraActualCorteSuprema()){
					tipoGiro = expedienteEdit.getTipoGiro();
				}
				//

				
				asignacionDirectaAOficina(expedienteIngreso, 
						oficinaDestino, 
						oficinaActual,
						tipoRadicacion, 
						tipoAsignacion,
						tipoAsignacionLex100,
						tipoCambioAsignacion,
						descripcionCambioAsignacion,
						sorteo,
						rubrosInfo.getRubro(),
						tipoGiro,
						!expedienteEdit.isIngresoAnteriorSistema() && !TipoCausaEnumeration.isVinculadoMediacion(expedienteEdit.getTipoCausa()) && (!TipoCausaEnumeration.isJuiciosOriginarios(expedienteEdit.getTipoCausa()) || expedienteEdit.isPenal()),	// compensa
						expedienteEdit.isIngresoDiferido()? expedienteEdit.getIngresoDiferidoEdit().getFechaIngreso(): null,
						!inMesaDeEntrada || expedienteEdit.isIngresoAnteriorSistema());
				
				if(expedienteEdit.isIngresoAnteriorSistema()) {
					asignarOtrasRadicaciones(expedienteIngreso, oficinaActual, expedienteEdit, tipoRadicacion);
				}else if(expedienteEdit.isRadicacionPrevia() && isMediacion(expedienteEdit) && expedienteEdit.getRadicacionPreviaEdit().getMediador() != null){
					ExpedienteManager.instance().cambiarMediador(expedienteIngreso.getExpediente(), oficinaActual, tipoRadicacion, expedienteEdit.getRadicacionPreviaEdit().getMediador(), expedienteIngreso.getExpediente().getIdxAnaliticoFirst());
				}
			}  else if(inMesaDeEntrada){

				ExpedienteIngreso expedienteIngreso = ingresarExpediente(expedienteEdit, null);
				
				String codigoTipoCambioAsignacion = getCodigoTipoCambioAsignacion(expedienteEdit, inMesaDeEntrada);
				RecursoExp recursoASortear = getRecursoASortear(expedienteIngreso.getExpediente());
				
		
				SorteoParametros sorteoParametros = mesaSorteo.createSorteoParametros(sorteo,
						TipoBolilleroSearch.getTipoBolilleroAsignacion(entityManager),
						oficinaSorteadora, 
						expedienteIngreso.getExpediente(), 
						recursoASortear,
						tipoRadicacion, 
						codigoTipoCambioAsignacion,
						getDescripcionCambioAsignacion(expedienteEdit, codigoTipoCambioAsignacion, tipoRadicacion),
						expedienteEdit.getTipoGiro(), 
						null, 
						rubrosInfo.getRubro(), 
						(expedienteIngreso != null) ? expedienteIngreso.getFechaHecho(): null,
						false,
						null,
						true);
	
				/**ATOS OVD*/
				sorteoParametros.setAnioOVD(ExpedienteMeAdd.instance().getAnioLegajoOVD());
				sorteoParametros.setLegajoOVD(ExpedienteMeAdd.instance().getLegajoOVD());
				sorteoParametros.setCaratulaOVD(ExpedienteMeAdd.instance().getCaratulaOVD());
				/**ATOS OVD*/
				
				asignarOficinaConexidadSorteo(expedienteIngreso, expedienteEdit, sorteoParametros);
				
			} else { // por ej: Juzgado de turno

				TipoRadicacionEnumeration.Type tipoRadicacionByOficina = calculateTipoRadicacionByTipoOficina(oficinaActual, tipoRadicacion);
				if(tipoRadicacionByOficina != tipoRadicacion){
					tipoRadicacion = tipoRadicacionByOficina;
					expedienteEdit.setTipoRadicacion(tipoRadicacion);
					rubrosInfo = calculaRubrosAsignacion(expedienteEdit, tipoRadicacion);
				}
				ExpedienteIngreso expedienteIngreso = ingresarExpediente(expedienteEdit, null);
				RecursoExp recursoASortear = getRecursoASortear(expedienteIngreso.getExpediente());
				boolean omitirBusquedaConexidad = expedienteEdit.isOmitirBusquedaConexidad();
				if(!omitirBusquedaConexidad) {
					buscaConexidadInformativa(entityManager, expedienteIngreso.getExpediente(), recursoASortear, tipoRadicacion, sorteo, rubrosInfo.getRubro(), oficinaSorteadora, expedienteEdit.isIgnoreCheckDobleInicio(), expedienteEdit.getFojasEdit());
				}

				String codigoTipoCambioAsignacion = getCodigoTipoCambioAsignacion(expedienteEdit, inMesaDeEntrada);

				asignacionDirectaAOficinaActual(oficinaActual, 
						expedienteIngreso,
						tipoRadicacion,
						codigoTipoCambioAsignacion, 
						getDescripcionCambioAsignacion(expedienteEdit, codigoTipoCambioAsignacion, tipoRadicacion),
						sorteo, 
						rubrosInfo.getRubro(),
						expedienteEdit.isIniciarEnIngresoTurno());
			}
		} catch (Exception e) {
			getLog().error("Error creando expediente", e);
			getStatusMessages().addFromResourceBundle(Severity.ERROR, "completarIngresoExpediente.error");
			getRedirect().setViewId("/web/home.xhtml");
			getRedirect().execute();
		}
	}

	private TipoRadicacionEnumeration.Type calculateTipoRadicacionByTipoOficina(Oficina oficinaActual, TipoRadicacionEnumeration.Type defaultValue) {
		Oficina oficina = CamaraManager.getOficinaPrincipal(oficinaActual);
		TipoRadicacionEnumeration.Type ret = defaultValue;
		if(TipoOficinaEnumeration.isTipoOficinaTribunalOral(oficina.getTipoOficina())){
			ret = TipoRadicacionEnumeration.Type.tribunalOral;
		}
		if(TipoOficinaEnumeration.isTipoOficinaJuzgado(oficina.getTipoOficina())){
			ret =  TipoRadicacionEnumeration.Type.juzgado;
		}
		return ret;
	}

	@Transactional
	public void sortearJuzgado(Expediente expediente) {
		TipoRadicacionEnumeration.Type tipoRadicacion = TipoRadicacionEnumeration.Type.juzgado;

		entityManager.joinTransaction();		
		try {
			Oficina oficinaActual = getOficinaActual();
			boolean inMesaDeEntrada = OficinaManager.instance().isMesaDeEntrada(oficinaActual);
			
		 	RubrosInfo rubrosInfo = ExpedienteMeAsignacion.instance().calculaRubrosAsignacion(expediente, tipoRadicacion);
			Oficina oficinaSorteadora = oficinaActual;
			Materia materia = expediente.getMateria();
			
			Sorteo sorteo = null;
			sorteo = mesaSorteo.buscaSorteo(oficinaSorteadora, 
						false,
						tipoRadicacion, 
						materia.getGrupo(), 
						materia, 
						expediente.getCompetencia(), 
						rubrosInfo.getRubroAsignacion(), 
						rubrosInfo.getRubro(),
						true);
			
			if(inMesaDeEntrada){
				String codigoTipoCambioAsignacion = TipoCambioAsignacionEnumeration.SORTEO_JUZGADO;
				SorteoParametros sorteoParametros = mesaSorteo.createSorteoParametros(sorteo,
						TipoBolilleroSearch.getTipoBolilleroAsignacion(entityManager),
						oficinaSorteadora, 
						expediente,
						null,
						tipoRadicacion, 
						codigoTipoCambioAsignacion,
						getDescripcionCambioAsignacion(expediente, codigoTipoCambioAsignacion, tipoRadicacion),
						ConfiguracionMateriaManager.instance().getTipoGiroAsignacion(SessionState.instance().getGlobalCamara(), expediente.getMateria()), 
						null, 
						rubrosInfo.getRubro(), 
						null,
						false,
						null,
						true);
				sorteoParametros.setBuscaConexidad(false);
				sorteoParametros.setAsignaPorConexidad(false);
				sorteoParametros.setSortea(true);
				
				mesaSorteo.enviarAColaDeSorteo(sorteoParametros);
				
			} else { // por ej: Juzgado de turno

				ExpedienteIngreso expedienteIngreso = expediente.getExpedienteIngreso();
				
				String codigoTipoCambioAsignacion = TipoCambioAsignacionEnumeration.INGRESO_JUZGADO_TURNO;

				asignacionDirectaAOficinaActual(oficinaActual, 
						expedienteIngreso,
						tipoRadicacion,
						codigoTipoCambioAsignacion, 
						getDescripcionCambioAsignacion(expediente, codigoTipoCambioAsignacion, tipoRadicacion),
						sorteo, 
						rubrosInfo.getRubro(),
						false);
			}
		} catch (Exception e) {
			getLog().error("Error sorteando expediente", e);
		}
	}
	
	public static boolean isAsignaJuzgadoMediacion(TipoCausa tipoCausa) {
		boolean mantieneJuzgadoMediacion;
		if (tipoCausa.getMantieneJuzgadoMediacion() != null) {
			mantieneJuzgadoMediacion = tipoCausa.getMantieneJuzgadoMediacion();
		} else {
			mantieneJuzgadoMediacion = ConfiguracionMateriaManager.instance().isMantieneJuzgadoMediacion(SessionState.instance().getGlobalCamara());
		}
		return mantieneJuzgadoMediacion;
	}

	private RecursoExp getRecursoASortear(Expediente expediente) {
		List<RecursoExp> list = ExpedienteManager.instance().computeRecursosPteAsignacion(expediente);
		if(list != null && list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}

	private String getDescripcionCambioAsignacion(ExpedienteMeAdd expedienteEdit, String codigoTipoCambioAsignacion, TipoRadicacionEnumeration.Type tipoRadicacion) {
		String descripcion = null;

		if(TipoRadicacionEnumeration.Type.sala == tipoRadicacion){
			descripcion = (expedienteEdit.getRecursoEdit().getTipoRecurso() != null) ? expedienteEdit.getRecursoEdit().getTipoRecurso().getDescripcion(): null;
		} else {
			descripcion = getDescripcionCambioAsignacion(expedienteHome.getEntityManager(), codigoTipoCambioAsignacion, tipoRadicacion, expedienteEdit.getMateria());
		}
		return descripcion;
	}

	private String getDescripcionCambioAsignacion(Expediente expediente, String codigoTipoCambioAsignacion, TipoRadicacionEnumeration.Type tipoRadicacion) {
		String descripcion = null;

		if(TipoRadicacionEnumeration.Type.sala == tipoRadicacion){
			RecursoExp recursoExp = getRecursoASortear(expediente);
			descripcion = (recursoExp != null && recursoExp.getTipoRecurso() != null) ? recursoExp.getTipoRecurso().getDescripcion(): null;
		} else {
			descripcion = getDescripcionCambioAsignacion(expedienteHome.getEntityManager(), codigoTipoCambioAsignacion, tipoRadicacion, expediente.getMateria());
		}
		return descripcion;
	}

	public static String getDescripcionCambioAsignacion(EntityManager entityManager, String codigoTipoCambioAsignacion, TipoRadicacionEnumeration.Type tipoRadicacion, Materia materia) {
		String descripcion = null;

		Integer tipoInstanciaCodigo = TipoRadicacionEnumeration.calculateCodigoTipoInstanciaByTipoRadicacion(tipoRadicacion);
		TipoInstancia tipoInstancia = TipoInstanciaSearch.findByCodigo(entityManager, tipoInstanciaCodigo);
		TipoCambioAsignacion tipoCambioAsignacion = TipoCambioAsignacionEnumeration.getItemByCodigo(entityManager, codigoTipoCambioAsignacion, tipoInstancia, materia);
		if (tipoCambioAsignacion != null) {
			descripcion =  tipoCambioAsignacion.getDescripcion();
		}
		return descripcion;
	}

	
	private void asignarOtrasRadicaciones(
			ExpedienteIngreso expedienteIngreso,
			Oficina oficinaActual, ExpedienteMeAdd expedienteEdit, TipoRadicacionEnumeration.Type tipoRadicacion) throws Lex100Exception {

		IngresoDiferidoEdit ingresoDiferidoEdit = expedienteEdit.getIngresoDiferidoEdit();
		if(TipoRadicacionEnumeration.Type.sala != tipoRadicacion){
			if(ingresoDiferidoEdit.getSala() != null){
				try {
					new AccionCambioAsignacion().doAccion(SessionState.instance().getUsername(), 
							expedienteIngreso.getExpediente(),
							null,
							ingresoDiferidoEdit.getSala(), 
							TipoRadicacionEnumeration.Type.sala,
							TipoAsignacionEnumeration.Type.asignacion_Sala_AnteriorAlSistema,
							TipoAsignacionLex100Enumeration.Type.anteriorAlSistema,
							oficinaActual,
							null,
							false,
							null,
							null,
							null,		// ????? REVISAR TIPO,
							null,
							null, 
							ingresoDiferidoEdit.getFechaIngreso(),
							null);
				} catch (AccionException e) {
					throw new Lex100Exception(e);
				}
			}
		}
		if(TipoRadicacionEnumeration.Type.juzgado != tipoRadicacion){
			if(ingresoDiferidoEdit.getJuzgado() != null){
				try {
				 	RubrosInfo rubrosInfo = calculaRubrosAsignacion(expedienteEdit, TipoRadicacionEnumeration.Type.juzgado);

					new AccionCambioAsignacion().doAccion(SessionState.instance().getUsername(), 
							expedienteIngreso.getExpediente(),
							null,
							ingresoDiferidoEdit.getJuzgado(), 
							TipoRadicacionEnumeration.Type.juzgado,
							TipoAsignacionEnumeration.Type.asignacion_Juzgado_AnteriorAlSistema,
							TipoAsignacionLex100Enumeration.Type.anteriorAlSistema,
							oficinaActual,
							null,
							false,
							null,
							rubrosInfo.getRubro(),
							null,		// ????? REVISAR TIPO,
							null,
							null, 
							ingresoDiferidoEdit.getFechaIngreso(),
							null);
				} catch (AccionException e) {
					throw new Lex100Exception(e);
				}
			}
		}
		if(TipoRadicacionEnumeration.Type.tribunalOral != tipoRadicacion){
			if(ingresoDiferidoEdit.getTribunalOral() != null){
				try {
				 	RubrosInfo rubrosInfo = calculaRubrosAsignacion(expedienteEdit, TipoRadicacionEnumeration.Type.tribunalOral);

					new AccionCambioAsignacion().doAccion(SessionState.instance().getUsername(), 
							expedienteIngreso.getExpediente(),
							null,
							ingresoDiferidoEdit.getTribunalOral(), 
							TipoRadicacionEnumeration.Type.tribunalOral,
							TipoAsignacionEnumeration.Type.asignacion_TO_AnteriorAlSistema,
							TipoAsignacionLex100Enumeration.Type.anteriorAlSistema,
							oficinaActual,
							null,
							false,
							null,
							rubrosInfo.getRubro(),
							null,		// ????? REVISAR TIPO,
							null,
							null, 
							ingresoDiferidoEdit.getFechaIngreso(),
							null);
				} catch (AccionException e) {
					throw new Lex100Exception(e);
				}
			}
		}
	}

	private String getCodigoTipoCambioAsignacion(ExpedienteMeAdd expedienteEdit, boolean inMesaDeEntrada) {
		String codigoTipoCambioAsignacion = null;
		if(expedienteEdit.getTipoCausa() != null){
			codigoTipoCambioAsignacion = expedienteEdit.getTipoCausa().getTipoAsignacion(); 
		}
		if(codigoTipoCambioAsignacion == null){
			if(TipoRadicacionEnumeration.Type.tribunalOral == expedienteEdit.getTipoRadicacion()){
				codigoTipoCambioAsignacion = TipoCambioAsignacionEnumeration.INCOMPETENCIA; // FIXME debe venir como campo de TipoCausa
			} else if(TipoRadicacionEnumeration.Type.sala == expedienteEdit.getTipoRadicacion()){
				codigoTipoCambioAsignacion =  (expedienteEdit.getRecursoEdit().getTipoRecurso() != null) ? expedienteEdit.getRecursoEdit().getTipoRecurso().getCodigo(): null;
			} else if(TipoRadicacionEnumeration.Type.salaCasacion == expedienteEdit.getTipoRadicacion()){
				codigoTipoCambioAsignacion =  (expedienteEdit.getRecursoEdit().getTipoRecurso() != null) ? expedienteEdit.getRecursoEdit().getTipoRecurso().getCodigo(): null;
			} else if(TipoRadicacionEnumeration.Type.corteSuprema == expedienteEdit.getTipoRadicacion()){
				codigoTipoCambioAsignacion =  (expedienteEdit.getRecursoEdit().getTipoRecurso() != null) ? expedienteEdit.getRecursoEdit().getTipoRecurso().getCodigo(): null;
			} else {
				if(inMesaDeEntrada) {
					codigoTipoCambioAsignacion = TipoCambioAsignacionEnumeration.SORTEO_JUZGADO;
				} else {
					codigoTipoCambioAsignacion = TipoCambioAsignacionEnumeration.INGRESO_JUZGADO_TURNO;
				}
			}
		}
		return codigoTipoCambioAsignacion;
	}

	
	public RubrosInfo calculaRubrosAsignacion(ExpedienteMeAdd expedienteEdit, TipoRadicacionEnumeration.Type tipoRadicacion){
		RubrosInfo rubrosInfo = new RubrosInfo();

		// FIXME no se si puede entrar aqui con radicacion != a juzgado
		
		if(TipoRadicacionEnumeration.Type.juzgadoPlenario == tipoRadicacion){
			rubrosInfo = calculaRubrosPlenario(expedienteEdit);
		}else if(TipoRadicacionEnumeration.Type.tribunalOral == tipoRadicacion) {				
			rubrosInfo = calculaRubrosTO(expedienteEdit);			
		}else if(TipoRadicacionEnumeration.Type.sala == tipoRadicacion) {				
			rubrosInfo = calculaRubrosSala(expedienteEdit);			
		}else if(TipoRadicacionEnumeration.Type.corteSuprema == tipoRadicacion) {				
			rubrosInfo = calculaRubrosSala(expedienteEdit);			
		} else if(TipoRadicacionEnumeration.Type.juzgado == tipoRadicacion){
			if(isMediacion(expedienteEdit)){
				rubrosInfo = MesaSorteo.calculaRubrosJuzgadoMediacion(null);
			} else if(CamaraManager.isCamaraActualCorteSuprema()){
				rubrosInfo = new RubrosInfo();
				rubrosInfo.setRubro("A");	
			} else {
				rubrosInfo = calculaRubrosJuzgado(expedienteEdit);			
			}
		} else if(TipoRadicacionEnumeration.isAnySala(tipoRadicacion)){
			rubrosInfo = MesaSorteo.calculaRubroSalaByTipoRadicacion(tipoRadicacion);
		}
		return rubrosInfo;
	}

//	private Sorteo buscaSorteo(ExpedienteMeAdd expedienteEdit, TipoRadicacionEnumeration.Type tipoRadicacion, Oficina oficinaSorteadora) throws Lex100Exception {
//		Materia materia = expedienteEdit.getMateria();
//		RubrosInfo info = calculaRubrosAsignacion(expedienteEdit, tipoRadicacion);
//		return mesaSorteo.buscaSorteo(oficinaSorteadora, tipoRadicacion, materia.getGrupo(), materia, expedienteEdit.getCompetencia(), info.getRubroAsignacion(), info.getRubro());
//	}

	private Oficina getOficinaActual() {
		Oficina oficinaActual = SessionState.instance().getGlobalOficina();
		return oficinaActual;
	}
	
	private TipoRecurso calculateTipoRecurso(ExpedienteMeAdd expedienteEdit) {
		TipoRecurso ret = null; 
		List<TipoRecurso> list = TipoRecursoFinderMe.instance().getTiposRecursoPredefinidos(expedienteEdit.getTipoCausa(), expedienteEdit.getMateria(), expedienteEdit.getObjetoJuicio());
		if((list != null) && (list.size() > 0)){
			ret = list.get(0);
		}
		return ret;
	}

	@Transactional
	public ExpedienteIngreso ingresarExpediente(ExpedienteMeAdd expedienteEdit, Date fechaIngresoDiferido) throws Lex100Exception{				
		/**/System.out.println("/**/ingresarExpediente -> empezando con el metodo");

		boolean ok = true;
		
		// Solo hacer commit si va al sorteador
		ok= createExpediente(expedienteEdit, (fechaIngresoDiferido == null));
		
		Expediente expediente = expedienteHome.getInstance();
		try {
			if(ok) {
				if (ConfiguracionMateriaManager.instance().isCamaraUsaPoderes(SessionState.instance().getGlobalCamara())) {
					updatePoderesDemanda(expediente, expedienteEdit);
				}
			}
			if(ok && !expedienteEdit.getExpedienteEspecialEdit().isTratamientoIncidental()) {
				ok = createRecurso(expediente, expedienteEdit, true);
			}
			checkSufijoExpediente(expedienteEdit, expediente);
			
			if(ok) {
				ok = updateExpedienteIngresoToExpediente(expediente, expedienteEdit);
			}
		    if(ok){
		    	ok = updateExpedienteEspecialToExpediente(expediente, expedienteEdit) ;
		    }
		    //
			if(ok) {
				/**/System.out.println("EMPEZANDO A CREAR EL EXPEDIENTE");
				updateExpedienteInfoToExpediente(expediente, expedienteEdit);
				
				entityManager.flush();
				entityManager.refresh(expediente);
				ExpedienteManager.instance().actualizarCaratula(expediente, null, null);
				
				if(expedienteEdit.getFechaComunicacion() != null) { 	// Carga AFIP
					String detalle = ExpedienteManager.getSimpleDateFormat().format(expedienteEdit.getFechaComunicacion());
					addActuacion(entityManager, TipoInformacionEnumeration.FECHA_COMUNICACION, expediente, detalle);
				}
				if (CamaraManager.isCamaraActualSeguridadSocial()) {
					if(expedienteEdit.getOtrosDatos().isResolucionAdministrativa()){ 	// Seg. Soc.
						addActuacion(entityManager, TipoInformacionEnumeration.CON_RESOL_ADMINISTRATIVA, expediente, null);
					}
					if(expedienteEdit.getOtrosDatos().isAdjuntaCopia()){ 	// Seg. Soc.
						addActuacion(entityManager, TipoInformacionEnumeration.ADJ_COPIA_RESOL_ADM, expediente, null);
					}
					if(expedienteEdit.isMedidasCautelares()){ 	// Seg. Soc.
						addActuacion(entityManager, TipoInformacionEnumeration.CON_MEDIDA_CAUTELAR, expediente, null);
					}
				}
				if(fechaIngresoDiferido != null){
					expediente.setFechaInicio(fechaIngresoDiferido);
					expediente.setFechaSorteoCarga(fechaIngresoDiferido);
				}

				getStatusMessages().clear();
				entityManager.flush();
				String info = expediente.getNumero()+"/"+expediente.getAnio();
				infoMsg("expedienteMe.info.expedienteCreated", info);
			} else {
				throw new Lex100Exception("No se ha podido crear el expediente");
			}
			entityManager.refresh(expediente.getExpedienteIngreso());
			entityManager.refresh(expediente);
			UsuarioExpedienteManager.instance().addExpedienteLeido(expediente);

			// Tratamiento Incidental
			if(expedienteEdit.getExpedienteEspecialEdit().isTratamientoIncidental()) {
				Expediente subexpediente = createSubexpediente(expediente, expedienteEdit, true);
				if (subexpediente == null) {
					throw new Lex100Exception("No se ha podido crear el subexpediente");
				}
				expedienteHome.setId(subexpediente.getId());
				UsuarioExpedienteManager.instance().addExpedienteLeido(subexpediente);
				
				if(expedienteEdit.isSorteaIncidenteEnTratamientoIncidental()){
					expediente = subexpediente;
				}
			}
			
	
			return expediente.getExpedienteIngreso();
			
		} catch (Exception e) {
			if (expediente != null && expediente.getId() != null) {
				getLog().error("Borrando Expediente {0}/{1}", expediente.getNumero(), expediente.getAnio());
				try {
					UserTransaction transaction = Transaction.instance();
					if (transaction.isActive()) {
						transaction.rollback();
					}
					transaction.begin();
					EntityManager entityManager = (EntityManager) Component.getInstance("entityManager", true);
					entityManager.joinTransaction();
					Expediente exp = entityManager.find(Expediente.class, expediente.getId());
					exp.setLogicalDeleted(true);
					entityManager.flush();
				} catch (Exception e1) {
					getLog().error(e1);
				}
			}
			throw new Lex100Exception(e);
		}
	}

	private static void addActuacion(EntityManager entityManager, String codigoTipoInformacion, Expediente expediente, String detalle) {
		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
		TipoInformacion tipoInformacion = TipoInformacionEnumeration.getItemByCodigo(entityManager, codigoTipoInformacion, expediente.getMateria());
		String descripcion;
		if (tipoInformacion != null) {
			descripcion = Strings.emptyIfNull(tipoInformacion.getDescripcion());
		} else {
			descripcion = codigoTipoInformacion;
		}
		if (!Strings.isEmpty(detalle)) {
			descripcion += " (" + detalle + ")";
		}
		actuacionExpManager.addInformacionExpediente(expediente, null, tipoInformacion, descripcion, null, null, SessionState.instance().getUsername(), detalle);
	}

	private String getCodigoObjetoJuicio(Expediente expediente) {
		if(expediente.getObjetoJuicioPrincipal() != null) {
			return expediente.getObjetoJuicioPrincipal().getCodigo();
		}
		return null;
	}

	private boolean validaPoderParaAsignar(EntityManager entityManager, Expediente expediente, Integer nroPoder, Integer anioPoder) {
		boolean valido = false;

		Poder poder = PoderSearch.findByNaturalKey(entityManager, SessionState.instance().getGlobalCamara(), nroPoder, anioPoder);
		if (poder == null) {
			if (WebManager.findPoderWeb(entityManager, nroPoder, anioPoder)) {
				addActuacion(entityManager, TipoInformacionEnumeration.PODER_S_ACRED_DE_IDENT, expediente, WebManager.getNumeroAnio(nroPoder, anioPoder));
			} else {
				addActuacion(entityManager, TipoInformacionEnumeration.PODER_CON_INF_ERRONEA, expediente, WebManager.getNumeroAnio(nroPoder, anioPoder));
			}
		} else {
			valido = validaPoderParaAsignar(entityManager, expediente, poder);
		}
		return valido;
	}

	private boolean validaPoderParaAsignar(EntityManager entityManager, Expediente expediente, Poder poder) {
		boolean valido = false;

		if (poder != null) {
			DemandaPoder demandaPoder = PoderManager.findDemandaPoderByPoder(expedienteHome.getEntityManager(), poder);
			if (demandaPoder != null) {
				addActuacion(entityManager, TipoInformacionEnumeration.PODER_YA_UTILIZADO, expediente, WebManager.getNumeroAnioPoder(poder));
			} else if (EstadoPoderEnumeration.Type.REVOCADO.getValue().equals(poder.getAbm())) {
				addActuacion(entityManager, TipoInformacionEnumeration.PODER_CON_REVOCATORIA, expediente, WebManager.getNumeroAnioPoder(poder));
			} else if (!EstadoPoderEnumeration.Type.ALTA.getValue().equals(poder.getAbm()) ) {
				addActuacion(entityManager, TipoInformacionEnumeration.PODER_CON_INF_ERRONEA, expediente, WebManager.getNumeroAnioPoder(poder));
			} else if (!equals(poder.getCodObjetoJuicio(), getCodigoObjetoJuicio(expediente)) || 
					(ExpedienteManager.findActorByDocumento(expediente, poder.getCodTipoDocumento(), poder.getNumeroDocumento()) == null)) {
				addActuacion(entityManager, TipoInformacionEnumeration.PODER_CON_INF_INCORRECTA, expediente, WebManager.getNumeroAnioPoder(poder));
				boolean permiteAsignarPoderInformacionIncorrecta = ConfiguracionMateriaManager.instance().isPermiteAsignarPoderInformacionIncorrecta(SessionState.instance().getGlobalCamara());
				if (permiteAsignarPoderInformacionIncorrecta) {
					valido = true;
				}
			} else {
				valido = true;
			}
		}
		return valido;
	}

	public boolean updatePoderesDemanda(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		boolean representacionChanged = false;
		List<Poder> poderes = new ArrayList<Poder>();
		for (LetradoEdit letradoEdit: expedienteEdit.getLetradoEditList().getList()) {
			if (!letradoEdit.isEmpty() && (letradoEdit.getPoder() != null) ){
				if (!poderes.contains(letradoEdit.getPoder())) {
					poderes.add(letradoEdit.getPoder());
				}
			}
		}
		for (Poder poder: poderes) {
			boolean asignado = false;
			if (expedienteEdit.isEditMode() || validaPoderParaAsignar(entityManager, expediente, poder)){
				asignado = PoderManager.asignaDemanda(entityManager, poder, expediente);
			}
			if (asignado && !Strings.isEmpty(poder.getRepresentacion())) {
				representacionChanged = true;
			}
		}
		List<DemandaPoder> otherDemandasPoder = PoderManager.getOtrasDemandasPoder(entityManager, poderes, expediente);
		if (otherDemandasPoder != null) {
			for (DemandaPoder demandaPoder:	otherDemandasPoder) {
				PoderManager.desasignaDemandaPoder(entityManager, demandaPoder, expediente);
				if (!Strings.isEmpty(demandaPoder.getPoder().getRepresentacion())) {
					representacionChanged = true;
				}
			}
		}
		if (!expedienteEdit.isEditMode() && (poderes.size() == 0) && (expedienteEdit.getPoder() == null) && (expedienteEdit.getNumeroPoder() != null) && (expedienteEdit.getAnioPoder() != null)){
			validaPoderParaAsignar(entityManager, expediente, expedienteEdit.getNumeroPoder(), expedienteEdit.getAnioPoder());
		}
		return representacionChanged;
	}

	private boolean createRecurso(Expediente expediente, ExpedienteMeAdd expedienteEdit, boolean inPrincipal) {
		boolean ok = true;
		if((expedienteEdit.isIngresoASala() || (CamaraManager.isCamaraActualCorteSuprema() && inPrincipal)) && expedienteEdit.getRecursoEdit().getTipoRecurso() != null){
			RecursoEdit recursoEdit = expedienteEdit.getRecursoEdit();
			FojasEdit fojasEdit = expedienteEdit.getFojasEdit();
			RecursoExp recursoExp = ExpedienteMeAsignacion.instance().createRecurso(expediente, 
					recursoEdit.getTipoRecurso(), 
					recursoEdit.getFechaPresentacion(), 
					recursoEdit.isDetenidos(),
					recursoEdit.getRegimenProcesal(), 
					recursoEdit.getObservaciones(),
					recursoEdit.getResolucionApelacion(),
					recursoEdit.getTipoResolucionApelada(),
					recursoEdit.getTipoSentencia(),
					fojasEdit.getFojas(),
					fojasEdit.getCuerpos(),
					fojasEdit.getSobres(),
					fojasEdit.getAgregados(),
					recursoEdit.getComentarios(),
					expedienteEdit.showTipoRecurso()? recursoEdit.isUrgente(): expedienteEdit.isIngresoUrgente());
			ok = recursoExp != null;
//			Descomentar para que pueda circular el expediente directamente sin crear un expediente de circulación						
//			if (ok && TipoRecursoEnumeration.instance().isTipoRecursoCirculacionExpediente(recursoExp.getTipoRecurso())) {
//				expediente.setRecursoExp(recursoExp);
//			}
		}
		return ok;
	}

	private void checkSufijoExpediente(ExpedienteMeAdd expedienteEdit,	Expediente expediente) {
		String codigoSubexpediente = null;
		if(expedienteEdit.isIngresoASala()){			
			codigoSubexpediente = CuadernoManager.RECURSO_EN_CAMARA + "01";
		}else if(expedienteEdit.isIngresoATO()){
			codigoSubexpediente = CuadernoManager.ELEVACION_TRIBUNAL_ORAL + "01";
			expediente.setTipoSubexpediente(getTipoSubexpedienteTO(expediente));
		}
		if(codigoSubexpediente != null){
			expediente.setCodigoSubexpediente(codigoSubexpediente);
		}
	}


	private TipoSubexpediente getTipoSubexpedienteTO(Expediente expediente) {
		List<TipoSubexpediente> tipoSubexpedienteList = 
		    entityManager.createQuery("from TipoSubexpediente where naturalezaSubexpediente = :naturalezaSubexpediente and materia = :materia and status <> -1")
		    .setParameter("naturalezaSubexpediente", NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue())
		    .setParameter("materia", expediente.getMateria())
		    .getResultList();
		   if (tipoSubexpedienteList.size() > 0) {
			   return tipoSubexpedienteList.get(0);
		   }else{
			   return null;
		   }
	}


	private Expediente createSubexpediente(Expediente expediente, ExpedienteMeAdd expedienteEdit, boolean copyConexidadDeclarada) throws Lex100Exception {
		boolean mode = 	expedienteEdit.getIntervinienteEditList().isInAddExpedienteMode();
		try {
			UsuarioExpedienteManager.instance().deactivateCookieProcessing();
			expedienteEdit.getIntervinienteEditList().setInAddExpedienteMode(true);

			if (expedienteEdit.getExpedienteEspecialEdit().getTipoSubexpediente() != null) {
				
				CuadernoManager cuadernoManager = CuadernoManager.instance();
				cuadernoManager.crearSubexpediente((String) NaturalezaSubexpedienteEnumeration.Type.incidente.getValue(), expediente, true, copyConexidadDeclarada);			
		
				cuadernoManager.getCuadernoActual().setTipoSubexpediente(expedienteEdit.getExpedienteEspecialEdit().getTipoSubexpediente());
				
				cuadernoManager.setPermitirCuadernoSinContenido(true);
				cuadernoManager.setAbreviaturaSubexpediente("");
				
				cuadernoManager.setIntervinientesSeleccionados(intervinientesSeleccionados); 
				getStatusMessages().clear();
				String done = cuadernoManager.generarCuaderno("done", true, true, false);
				if (done != null) {
					expedienteHome.setId(cuadernoManager.getCuadernoActual().getId());
					cuadernoManager.getCuadernoActual().setVerificacionMesa("N");
					createRecurso(expedienteHome.getInstance(), expedienteEdit, false);
					entityManager.flush();
					Expediente subexpediente = cuadernoManager.getCuadernoActual();
					entityManager.refresh(subexpediente.getExpedienteIngreso());
					entityManager.refresh(subexpediente);
					return subexpediente;
				}
			}
		} finally {
			expedienteEdit.getIntervinienteEditList().setInAddExpedienteMode(mode);
			UsuarioExpedienteManager.instance().activateCookieProcessing();
		}
		return null;
	}

	public boolean createExpediente(ExpedienteMeAdd expedienteEdit, boolean doCommit) throws Lex100Exception{
		boolean ok = true;
		try {
			UsuarioExpedienteManager.instance().deactivateCookieProcessing();
			expedienteHome.createNewInstance();
			Expediente expediente = expedienteHome.getInstance();
			ok = completarCamposCabecera(expediente, expedienteEdit);
			if(ok ){
				expedienteIngresoManager.clear();
				expedienteIngresoManager.setMateria(expedienteEdit.getMateria());
				ok = expedienteIngresoManager.completarIngresoExpediente(expedienteEdit.isIngresoWeb());
			}
			if(ok){
				expediente.setTipoCausa(expedienteEdit.getExpedienteEspecialEdit().getTipoCausa());
				ok = expedienteHome.doAdd("ok") != null;
			}
			if(ok){
				ok = updateDelitosToExpediente(expediente, expedienteEdit);
			}
			if(ok){
				ok = completarIntervinientes(expedienteEdit);
			}
			if(ok){
				updateIntervinientesToExpediente(expediente, expedienteEdit);
			}			
			if(ok){
				ok = expedienteHome.doUpdate("ok") != null;
			}
			
			if(ok){
				updateVinculadoAExpediente(expediente, expedienteEdit);
			}
			/** ATOS OVD */
			if(ok){
				createVinculadoOVD(expediente, expedienteEdit);
			}
			/** ATOS OVD */
			if(ok){
				updateConexidadSegundaInstancia(expediente, expedienteEdit);
			}
			if(ok) {
				addVinculadoMediacion(expedienteHome.getInstance(), expedienteEdit.getExpedienteMediacion());
				if (expedienteEdit.isIngresoDiferido()) {
					if (expedienteEdit.isIngresoDiferidoConOffset()) {
						asignarNumeroExpediente(expediente, expedienteEdit.getIngresoDiferidoEdit().getNumeroExpedienteAnteriorAlSistema(), expedienteEdit.getIngresoDiferidoEdit().getAnioExpediente(), false);
					} else {
						asignarNumeroExpediente(expediente, expedienteEdit.getIngresoDiferidoEdit().getNumeroExpediente(), expedienteEdit.getIngresoDiferidoEdit().getAnioExpediente(), false);
					}
				} else {
					generarNuevoNumeroExpediente(expediente);
				}
				try{
					ExpedienteHomeListener.instance().validarNuevoExpediente(expediente);
					entityManager.flush();
				} catch (EntityOperationException e) {
					getStatusMessages().clear();
					getStatusMessages().addFromResourceBundle(Severity.ERROR, e.getMessage());
					throw new Lex100Exception(e.getMessage());
				}
				if (doCommit) {
					try {
						Transaction.instance().commit();
						Transaction.instance().begin();
						entityManager.joinTransaction();
					} catch (Exception e) {
						throw new Lex100Exception("Error creando expediente", e);
					}
				}
			}
		} finally {
			UsuarioExpedienteManager.instance().activateCookieProcessing();
		}

		return ok;
	}

	private void updateVinculadoAExpediente(Expediente expediente,	ExpedienteMeAdd expedienteEdit) {
		if(expedienteEdit.showVinculadoAExpediente()){
			ConexidadDeclarada conexidadDeclaradaEdit = expedienteEdit.getConexidadDeclarada();
			if (!isEmpty(conexidadDeclaradaEdit)) {
				String tipoVinculado = (String)TipoVinculadoEnumeration.Type.vinculado.getValue();
				String clave =  conexidadDeclaradaEdit.getClaveExpedienteConexo(); 
				String observaciones = conexidadDeclaradaEdit.getObservaciones();
				/**ATOS OVD */
				VinculadosExpManager.instance().createVinculado(tipoVinculado, expediente, conexidadDeclaradaEdit.getExpedienteConexo(), clave, observaciones, TipoVinculadoEnumeration.Type.vinculado.getValue().toString(), null, null, null);
				/**ATOS OVD */
			}
		}
	}

	private void updateConexidadSegundaInstancia(Expediente expediente,	ExpedienteMeAdd expedienteEdit) {
		if(expedienteEdit.showConexidadDeclaradaSegundaInstancia()){	// conexidad declara en segunda instancia en ingresos en primera instancia
			ConexidadDeclarada conexidadDeclarada2 = expedienteEdit.getConexidadDeclaradaSegundaInstancia();
			if (!isEmpty(conexidadDeclarada2)) {
				TipoAsignacionEnumeration.Type tipoAsignacion = TipoAsignacionEnumeration.getTipoAsignacionConexidadDenunciada(TipoRadicacionEnumeration.Type.sala, conexidadDeclarada2);
				addVinculadoConexo(entityManager, TipoRadicacionEnumeration.Type.sala, expediente, conexidadDeclarada2.getExpedienteConexo(), tipoAsignacion, null, null, null);
				entityManager.flush();
			}
		}
	}

	public static Expediente getVinculadoMediacion(EntityManager entityManager, Expediente expediente){
		return FamiliaManager.getExpedienteVinculadoByTipo(entityManager, expediente, TipoVinculadoEnumeration.Type.mediacion.getValue().toString());
	}
	
	private void addVinculadoMediacion(Expediente expediente, Expediente expedienteMediacion) {
		if(expedienteMediacion != null){
			String tipoVinculado = (String)TipoVinculadoEnumeration.Type.mediacion.getValue();
			/**ATOS OVD */
			VinculadosExpManager.instance().createVinculado(tipoVinculado, expediente, expedienteMediacion, null, null, (String)TipoAsignacionEnumeration.Type.asignacion_Juzgado_Conexidad_Automatica.getValue(), null, null, null);
			/**ATOS OVD */	
		}		
	}

	@Transactional
	public void asignarOficinaConexidadSorteo(ExpedienteIngreso expedienteIngreso, 
			ExpedienteMeAdd expedienteEdit, 
			SorteoParametros sorteoParametros) throws Lex100Exception {
		
		boolean sortear = false;
		boolean asignada = false;

		boolean busquedaConexidadAsincrona = ConfiguracionMateriaManager.instance().isConexidadAsincrona(SessionState.instance().getGlobalCamara());

		boolean omitirBusquedaConexidad = expedienteEdit.isOmitirBusquedaConexidad() || !ConexidadManager.isBuscaConexidad(sorteoParametros.getTipoRadicacion(), CamaraManager.getCamaraActual(), null, true);
		boolean conexidadInformativa = ConexidadManager.isConexidadInformativa(sorteoParametros.getTipoRadicacion(), expedienteIngreso.getExpediente().getCamara(), expedienteIngreso.getExpediente().getMateria());
		boolean asignaPorConexidad;
		
		String descripcionCambioAsignacionConexidadSolicitada = getDescripcionCambioAsignacion(expedienteEdit, TipoCambioAsignacionEnumeration.CONEXIDAD_SOLICITADA, sorteoParametros.getTipoRadicacion());			
		
		asignada =	buscaConexidadDenunciada(entityManager, 
				expedienteIngreso.getExpediente(),
				sorteoParametros.getRecursoExp(),
				sorteoParametros.getTipoRadicacion(),
				TipoCambioAsignacionEnumeration.CONEXIDAD_SOLICITADA, 
				descripcionCambioAsignacionConexidadSolicitada,
				sorteoParametros.getTipoGiro(), 
				sorteoParametros.getSorteo(), 
				sorteoParametros.getRubro(), 
				isConexidadDenunciadaPrioritaria(expedienteIngreso.getExpediente()) || conexidadInformativa,	// asigna
				sorteoParametros.getOficinaSorteo(),
				true,
				true,
				sorteoParametros.getFojasPase());
		
		busquedaConexidadAsincrona = !expedienteIngreso.getExpediente().getObjetoJuicio().isSeleccionaConexidad() && ConfiguracionMateriaManager.instance().isConexidadAsincrona(SessionState.instance().getGlobalCamara());
		asignaPorConexidad = !conexidadInformativa && !asignada && !expedienteIngreso.getExpediente().getObjetoJuicio().isSeleccionaConexidad();
		if(!busquedaConexidadAsincrona || expedienteIngreso.getExpediente().getObjetoJuicio().isSeleccionaConexidad()) {
			String descripcionCambioAsignacionConexidadDetectada = getDescripcionCambioAsignacion(expedienteEdit, TipoCambioAsignacionEnumeration.CONEXIDAD_DETECTADA, sorteoParametros.getTipoRadicacion());			
			if(!omitirBusquedaConexidad) {
				asignada |= buscaConexidadPorCriterios(entityManager, 
						expedienteIngreso.getExpediente(),
						sorteoParametros,
						TipoCambioAsignacionEnumeration.CONEXIDAD_DETECTADA, 
						descripcionCambioAsignacionConexidadDetectada,
						asignaPorConexidad, 
						sorteoParametros.getOficinaSorteo(),
						!isConexidadDenunciadaPrioritaria(expedienteIngreso.getExpediente()),
						expedienteEdit.isIgnoreCheckDobleInicio(),
						sorteoParametros.getFojasPase());
			}
			if (!asignada) {
				if (TipoCausaEnumeration.isProvenienteMediacion(expedienteEdit.getTipoCausa()) && isAsignaJuzgadoMediacion(expedienteEdit.getTipoCausa())){
					asignada = asignarJuzgadoMediacion(entityManager, 
							expedienteIngreso.getExpediente(), 
							sorteoParametros.getOficinaSorteo(),
							sorteoParametros.getSorteo(),
							sorteoParametros.getRubro(),
							sorteoParametros.getTipoGiro(),
							null,
							sorteoParametros.getFojasPase());
				}
			} 
		} else {
			sorteoParametros.setBuscaConexidad(!omitirBusquedaConexidad);
			sorteoParametros.setAsignaPorConexidad(asignaPorConexidad);
			sorteoParametros.setIgnoreCheckDobleInicio(expedienteEdit.isIgnoreCheckDobleInicio());
		}
		if (!asignada) {
			if (expedienteIngreso.getExpediente().getObjetoJuicio().isSeleccionaConexidad() && expedienteIngreso.getExpediente().getConexidadList().size() > 0) {
				expedienteEdit.setSorteoParametros(sorteoParametros);
			} else {
				sortear = true;
			}
		}
		boolean enviadoSorteo = false;
		if(sortear){
			enviadoSorteo = sortear(expedienteEdit, sorteoParametros);
		}
		if (!enviadoSorteo) {
			Boolean entraASortear = false;
			if (TipoRadicacionEnumeration.isRadicacionJuzgado(sorteoParametros.getTipoRadicacion()) && TipoCausaEnumeration.isMediacion(expedienteIngreso.getExpediente().getTipoCausa())){
				OficinaAsignacionExp radicacion = ExpedienteManager.instance().findRadicacionExpediente(expedienteIngreso.getExpediente(), sorteoParametros.getTipoRadicacion());
				if ((radicacion != null) && radicacion.getMediador() == null) { 
					sorteoParametros.setSorteoOperation(SorteoOperation.sorteoMediador);
					entraASortear = true;
					mesaSorteo.enviarAColaDeSorteo(sorteoParametros);
				}
			}
			SorteoParametros parametrosSiguienteSorteo = sorteoParametros.getSiguienteSorteoEncadenado();
			if ((parametrosSiguienteSorteo != null) && (ExpedienteManager.instance().findRadicacionExpediente(expedienteIngreso.getExpediente(), parametrosSiguienteSorteo.getTipoRadicacion())== null)) {
				entraASortear = true;
				mesaSorteo.enviarAColaDeSorteo(sorteoParametros.getSiguienteSorteoEncadenado());
			}
		}
	}

	public boolean sortear(ExpedienteMeAdd expedienteEdit, SorteoParametros sorteoParametros) throws Lex100Exception {
		boolean enviadoSorteo = false;
		List<Oficina> oficinasActivas = mesaSorteo.getOficinasSorteoActivas(sorteoParametros.getSorteo(), sorteoParametros.getFechaTurnosActivos());
		if(oficinasActivas.size() == 1){
			TipoAsignacionEnumeration.Type tipoAsignacionTurno = TipoAsignacionEnumeration.getTipoTurno(sorteoParametros.getTipoRadicacion());

			asignacionDirectaAOficina(sorteoParametros.getExpediente().getExpedienteIngreso(), 
					oficinasActivas.get(0), 
					sorteoParametros.getOficinaSorteo(),
					sorteoParametros.getTipoRadicacion(), 
					tipoAsignacionTurno,
					TipoAsignacionLex100Enumeration.Type.turno,
					TipoCambioAsignacionEnumeration.INGRESO_CAUSAS_TRAMITE,		// ????? REVISAR TIPO
					getDescripcionCambioAsignacion(expedienteEdit, TipoCambioAsignacionEnumeration.INGRESO_CAUSAS_TRAMITE, sorteoParametros.getTipoRadicacion()),
					sorteoParametros.getSorteo(),
					sorteoParametros.getRubro(),
					sorteoParametros.getTipoGiro(),
					puedeCompensarPorAsignacionDirecta(sorteoParametros.getExpediente(), sorteoParametros.getTipoRadicacion()),
					null,
					false);
		} else  {
			if(expedienteEdit.showMinisteriosPublicos()){
				List<Integer> restringirSoloAOficinaIds = null;
				restringirSoloAOficinaIds = excluirOficinaPorOficinaTurno(oficinasActivas, expedienteEdit);
				if(restringirSoloAOficinaIds != null){
					sorteoParametros.setOnlyOficinasIds(restringirSoloAOficinaIds);
				}
			}
			mesaSorteo.enviarAColaDeSorteo(sorteoParametros);
			enviadoSorteo = true;
		}
		return enviadoSorteo;
	}
	
	private List<Integer> excluirOficinaPorOficinaTurno(List<Oficina> oficinasActivas,	ExpedienteMeAdd expedienteEdit) {
		String tipoMinisterio = expedienteEdit.getMinisterioPublicoEdit().getTipoMinisterioPublico();
		Integer numeroMinisterio = expedienteEdit.getMinisterioPublicoEdit().getNumero();
		List<Integer> ret = null;
		if(tipoMinisterio != null && numeroMinisterio != null){			
			List<Oficina> list = ExpedienteManager.instance().findOficinasForOficinaDeTurno(SessionState.instance().getGlobalCamara(), tipoMinisterio, numeroMinisterio);
			if(list != null && list.size() > 0){
				ret = new ArrayList<Integer>();
				Map<Integer, Oficina> oficinasDeTurnoMap = createOficinaMap(list);
				for(Oficina oficina: oficinasActivas){
					boolean ok = false;
					if(oficinasDeTurnoMap.get(oficina.getId()) != null){
						ok = true;
					}else if(oficina.getOficinaSuperior() != null && oficinasDeTurnoMap.get(oficina.getOficinaSuperior().getId()) != null){
						ok = true;
					}
					if(ok){
						ret.add(oficina.getId());
					}
				}
			}
		}
		if(ret != null && ret.size() > 0){
			return ret;
		}else{
			return null;
		}
	}

	private Map<Integer, Oficina> createOficinaMap(List<Oficina> list) {
		Map<Integer, Oficina> map = new HashMap<Integer, Oficina>();
		for(Oficina oficina:list){
			map.put(oficina.getId(), oficina);
		}
		return map;
	}

	//ITO (Incompetencia TO)
	private RubrosInfo calculaRubrosTO(ExpedienteMeAdd expedienteEdit) {
		boolean detenidos = expedienteEdit.getExpedienteEspecialEdit().getDetenidos() != null? expedienteEdit.getExpedienteEspecialEdit().getDetenidos(): false;
		return mesaSorteo.calculaRubrosTO(detenidos, expedienteEdit.getExpedienteEspecialEdit().getHechos(), expedienteEdit.getFojasEdit().getCuerpos(), expedienteEdit.getExpedienteEspecialEdit().isDefensorOficial());
	}
	//	

	private RubrosInfo calculaRubrosPlenario(ExpedienteMeAdd expedienteEdit) {
		RubrosInfo rubrosInfo = new RubrosInfo();

		rubrosInfo.setRubro("C");
		
		if(expedienteEdit.getTipoCausa() != null && expedienteEdit.getTipoCausa().getRubroAsignacion() != null){
			rubrosInfo.setRubroAsignacion(expedienteEdit.getTipoCausa().getRubroAsignacion());
		}else if (expedienteEdit.getObjetoJuicio() != null){
			rubrosInfo.setRubroAsignacion(expedienteEdit.getObjetoJuicio().getRubroAsignacion());
		}
		return rubrosInfo;
	}

	
	private RubrosInfo calculaRubrosJuzgado(ExpedienteMeAdd expedienteEdit) {
		RubrosInfo rubrosInfo = new RubrosInfo();
		String rubro = null;
		String rubroAsignacion = null;
		
		System.out.println("Viendo rubros");
		if(expedienteEdit.getTipoCausa() != null && expedienteEdit.getTipoCausa().getRubro() != null){
			System.out.println("entro al primer if");
			rubro = expedienteEdit.getTipoCausa().getRubro();
			System.out.println("termino el primer if");
		}else if (expedienteEdit.getObjetoJuicio() != null){
			System.out.println("entro al segundo if");
			rubro = expedienteEdit.getObjetoJuicio().getRubroLetter();
			System.out.println("termino el segundo if");
		}
		if(expedienteEdit.getTipoCausa() != null && expedienteEdit.getTipoCausa().getRubroAsignacion() != null){
			System.out.println("entro al tercer if");
			rubroAsignacion = expedienteEdit.getTipoCausa().getRubroAsignacion();
			System.out.println("termino el tercer if");
		}else if (expedienteEdit.getObjetoJuicio() != null){
			System.out.println("entro al cuarto if");
			rubroAsignacion = expedienteEdit.getObjetoJuicio().getRubroAsignacion();
			System.out.println("termino el cuarto if");
		}
		System.out.println("Por setear con camata global");
		if (ConfiguracionMateriaManager.instance().isAsignaDerechosHumanos((SessionState.instance().getGlobalCamara()!=null)?SessionState.instance().getGlobalCamara():expedienteEdit.getGlobalCamara()) 
				&& expedienteEdit.isDerechosHumanos()) {
			System.out.println("entro al derechos humanos");
			rubro = MesaSorteo.RUBRO_DERECHOS_HUMANOS;
			rubroAsignacion = null;
			System.out.println("termino al derechos humanos");
		} else if (ConfiguracionMateriaManager.instance().isAdmiteLeyesEspeciales((SessionState.instance().getGlobalCamara()!=null)?SessionState.instance().getGlobalCamara():expedienteEdit.getGlobalCamara()) &&
				expedienteEdit.isPenal()) {
			System.out.println("entro al rubro leyes especiales");
			List<Integer> leyesEspecialesList =  ConfiguracionMateriaManager.instance().getLeyesEspeciales((SessionState.instance().getGlobalCamara()!=null)?SessionState.instance().getGlobalCamara():expedienteEdit.getGlobalCamara());
			System.out.println("ya seteo global camara");
			for (DelitoEdit delitoEdit : expedienteEdit.getDelitoEditList().getList()) {
				System.out.println("FOR FOR");
				if (delitoEdit.getDelito() != null && leyesEspecialesList.contains(delitoEdit.getDelito().getLey())) {
					if (!expedienteEdit.getDelitoEditList().getDeletedEntityList().contains(delitoEdit.getDelito())) {
						rubro = MesaSorteo.RUBRO_LEYES_ESPECIALES;
						rubroAsignacion = null;
					}
				}
			}
			System.out.println("termino rubros leyes esceciales");
		}
		rubrosInfo.setRubro(rubro);
		rubrosInfo.setRubroAsignacion(rubroAsignacion);
		return rubrosInfo;
	}

	private RubrosInfo calculaRubrosSala(ExpedienteMeAdd expedienteEdit) {
		RubrosInfo ret = null;
		if(expedienteEdit.isPenal()) {
			int regimenProcesal = 0;
			boolean detenidos = false;
			detenidos = Boolean.TRUE.equals(expedienteEdit.getRecursoEdit().isDetenidos());
			regimenProcesal = expedienteEdit.getRecursoEdit().getRegimenProcesal();
			if (ConfiguracionMateriaManager.instance().isAdmiteLeyesEspeciales(SessionState.instance().getGlobalCamara()) && expedienteEdit.getTipoCausa() != null && MesaSorteo.RUBRO_LEYES_ESPECIALES.equals(expedienteEdit.getTipoCausa().getRubro())) {
				ret = new RubrosInfo();
				ret.setRubro(expedienteEdit.getTipoCausa().getRubro());
			}
			ret = mesaSorteo.calculaRubrosSalaPenal(expedienteEdit.getRecursoEdit().getTipoRecurso(), detenidos, regimenProcesal);
		}else{
			ret = mesaSorteo.calculaRubrosSalaCivil(expedienteEdit.getRecursoEdit().getTipoRecurso());
			if(ret.getRubro() == null){
				ret = calculaRubrosJuzgado(expedienteEdit);
			}
		}
		return ret;
	}
	
	private void asignacionDirectaAOficinaActual(Oficina oficinaActual, 
			ExpedienteIngreso expedienteIngreso, 
			TipoRadicacionEnumeration.Type tipoRadicacion,
			String codigoTipoCambioAsignacion,
			String descripcionCambioAsignacion,
			Sorteo sorteo,
			String rubro,
			boolean iniciar) throws Lex100Exception {

		TipoAsignacionEnumeration.Type tipoAsignacionTurno = TipoAsignacionEnumeration.getTipoTurno(tipoRadicacion);

		if(oficinaActual != null){
			asignacionDirectaAOficina(expedienteIngreso, 
					oficinaActual, 
					oficinaActual,
					tipoRadicacion, 
					tipoAsignacionTurno,
					TipoAsignacionLex100Enumeration.Type.turno,
					codigoTipoCambioAsignacion,
					descripcionCambioAsignacion,
					sorteo,
					rubro,
					null,
					puedeCompensarPorAsignacionDirecta(expedienteIngreso.getExpediente(), tipoRadicacion),
					null,
					iniciar);
		}
	}

	public static boolean asignarJuzgadoMediacion(EntityManager entityManager,
			Expediente expediente, 
			Oficina oficinaActual,
			Sorteo sorteo,
			String rubro,
			TipoGiroEnumeration.Type tipoGiro,
			Date fechaIngreso,
			FojasEdit fojasPase) throws Lex100Exception {

		boolean asignada = false;
		Expediente expedienteMediacion = getVinculadoMediacion(entityManager, expediente);
		if (expedienteMediacion != null) {
			OficinaAsignacionExp oae = OficinaAsignacionExpManager.instance().getRadicacion(expedienteMediacion, TipoRadicacionEnumeration.Type.juzgado);
			if(oae != null){
				Oficina juzgadoMediacion = oae.getOficinaConSecretaria();

				asignarOficina(entityManager,
						expediente,
						null,
						juzgadoMediacion,
						TipoRadicacionEnumeration.Type.juzgado,
						TipoAsignacionEnumeration.Type.asignacion_ProvenienteMediacion,
						TipoAsignacionLex100Enumeration.Type.provenienteMediacion,
						TipoCambioAsignacionEnumeration.INGRESO_JUZGADO_MEDIACION,
						getDescripcionCambioAsignacion(entityManager, TipoCambioAsignacionEnumeration.INGRESO_JUZGADO_MEDIACION, TipoRadicacionEnumeration.Type.juzgado, expediente.getMateria()),
						tipoGiro,
						!TipoCausaEnumeration.isVinculadoMediacion(expediente.getTipoCausa()),
						sorteo,
						rubro,
						oficinaActual,
						fojasPase);

				asignada=true;
			}
		}
		return asignada;
	}
	
	private void asignacionDirectaAOficina(ExpedienteIngreso expedienteIngreso, 
			Oficina oficina, 
			Oficina oficinaActual,
			TipoRadicacionEnumeration.Type tipoRadicacion, 
			TipoAsignacionEnumeration.Type tipoAsignacion,
			TipoAsignacionLex100Enumeration.Type tipoAsignacionLex100,
			String codigoTipoCambioAsignacion,
			String descripcionCambioAsignacion,
			Sorteo sorteo,
			String rubro,
			TipoGiroEnumeration.Type tipoGiro,
			boolean compensa,
			Date fechaIngreso, boolean iniciar) throws Lex100Exception {
		
		entityManager.refresh(expedienteIngreso);
		entityManager.refresh(expedienteIngreso.getExpediente());
		if(oficina != null){
			oficina = entityManager.find(Oficina.class, oficina.getId());			
			entityManager.refresh(oficina);
			
//			if(oficinaActual != null){
//				oficinaActual = CamaraManager.getOficinaPrincipal(oficinaActual);
//			}
			try {
				new AccionCambioAsignacion().doAccion(SessionState.instance().getUsername(), expedienteIngreso.getExpediente(),
						getRecursoASortear(expedienteIngreso.getExpediente()),
						oficina,
						tipoRadicacion,
						tipoAsignacion,
						tipoAsignacionLex100,
						oficinaActual,
						tipoGiro,
						compensa,
						sorteo,
						rubro,
						codigoTipoCambioAsignacion,
						descripcionCambioAsignacion,
						null,
						fechaIngreso,
						null);
			} catch (AccionException e) {
				throw new Lex100Exception(e);
			}
			if (iniciar) {
				if (fechaIngreso != null) {
					fechaIngreso = incrementaUnSegundo(fechaIngreso);
				}
				ExpedienteManager.instance().iniciarExpediente(expedienteIngreso.getExpediente(), fechaIngreso);
			}
		}
	}

	private static boolean puedeCompensarPorAsignacionDirecta(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		return ConfiguracionMateriaManager.instance().isCompensaIngresoPorTurno(expediente.getCamara(), expediente.getMateria());
	}
	
	private static boolean puedeCompensarPorConexidadSeleccionada(TipoRadicacionEnumeration.Type tipoRadicacion) {
		return true;
		//TipoRadicacionEnumeration.Type.sala == tipoRadicacion || TipoRadicacionEnumeration.Type.tribunalOral == tipoRadicacion ;
	}

	public static boolean puedeCompensarPorConexidadAutomatica(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		return true;
//				((TipoRadicacionEnumeration.Type.juzgado == tipoRadicacion) && ExpedienteManager.instance().isPenal(expediente)) 
//				||
//				(TipoRadicacionEnumeration.Type.sala == tipoRadicacion) ;
	}

	public static boolean isConexidadPararSiEncontrada(Expediente expediente) {
		return ConfiguracionMateriaManager.instance().isConexidadPararSiEncontrada(expediente.getCamara(), expediente.getMateria());
	}

	public static boolean isConexidadDenunciadaPrioritaria(Expediente expediente) {
		Boolean ret = null;
		
		if ((expediente.getConexidadDeclarada() != null) && !expediente.getConexidadDeclarada().isEmpty() && expediente.getConexidadDeclarada().isSolicitadaPorMesa()) {
			ret = true;
		} else {
			if(expediente.getObjetoJuicioPrincipal() != null) {
				ret = expediente.getObjetoJuicioPrincipal().getPrioridadDenunciada();
			}
			if(ret == null) {
				ret = ConfiguracionMateriaManager.instance().isConexidadDenunciadaPrioritaria(expediente.getCamara(), expediente.getMateria());
			}
		}
		return ret;
	}

	@Transactional
	private static void asignarOficina(EntityManager entityManager, Expediente expediente, RecursoExp recurso, Oficina oficina, TipoRadicacionEnumeration.Type tipoRadicacion, TipoAsignacionEnumeration.Type tipoAsignacion, TipoAsignacionLex100Enumeration.Type tipoAsignacionLex100, String codigoTipoCambioAsignacion, String descripcionCambioAsignacion, TipoGiroEnumeration.Type tipoGiro, boolean compensar, Sorteo sorteo, String rubro, Oficina oficinaAsignadora, FojasEdit fojasPase) throws Lex100Exception {
		if (oficina != null) {
			entityManager.joinTransaction();

			try {
				Date FECHA_ASIGNACION_EN_CURSO = null;
				new AccionCambioAsignacion().doAccion(SessionState.instance().getUsername(), expediente, 
						recurso,
						oficina,
						tipoRadicacion,
						tipoAsignacion,
						tipoAsignacionLex100,
						oficinaAsignadora,
						tipoGiro,
						compensar,
						sorteo,
						rubro,
						codigoTipoCambioAsignacion,
						descripcionCambioAsignacion,
						null,
						FECHA_ASIGNACION_EN_CURSO,
						fojasPase);
			} catch (AccionException e) {
				throw new Lex100Exception(e);
			}

			entityManager.flush();
		}
	}

	public boolean buscaConexidadPorFamilia(EntityManager entityManager, Expediente expediente, RecursoExp recurso, TipoRadicacionEnumeration.Type tipoRadicacion, String codigoTipoCambioAsignacion, String descripcionCambioAsignacion, TipoGiroEnumeration.Type tipoGiro, Sorteo sorteo, String rubro, Oficina oficinaAsignadora, FojasEdit fojasPase) throws Lex100Exception {
		List<Expediente> familiaExpediente = ConexidadManager.instance().getFamiliaExpediente(entityManager, expediente);
		
		TipoAsignacionEnumeration.Type tipoAsignacion = TipoAsignacionEnumeration.getTipoRadicacionAnterior(tipoRadicacion);
		for(Expediente expedienteConexo: familiaExpediente) {
			Oficina oficina = ExpedienteManager.instance().getOficinaByRadicacion(expedienteConexo, tipoRadicacion);
			if (oficina != null) {
				asignarOficina(entityManager,
						expediente,
						recurso,
						oficina,
						tipoRadicacion,
						tipoAsignacion,
						TipoAsignacionLex100Enumeration.Type.conexidadFamilia,
						codigoTipoCambioAsignacion,
						descripcionCambioAsignacion,
						tipoGiro,
						TipoRadicacionEnumeration.Type.sala == tipoRadicacion,
						sorteo,
						rubro,
						oficinaAsignadora,
						fojasPase
				);
				ExpedienteMeAdd.instance().setExpedienteConexo(expedienteConexo);
				return true;
			}
		}
		return false;
	}

	public static boolean buscaConexidadDenunciada(EntityManager entityManager, Expediente expediente, RecursoExp recurso, TipoRadicacionEnumeration.Type tipoRadicacion, String codigoTipoCambioAsignacion, String descripcionCambioAsignacion, Type tipoGiro, Sorteo sorteo, String rubro, boolean asignarOficina, Oficina oficinaAsignadora, boolean addActuacion, boolean addVinculadoConexo, FojasEdit fojasPase) throws Lex100Exception {
		ConexidadDeclarada conexidadDeclarada = expediente.getConexidadDeclarada();
		
		if ((conexidadDeclarada != null) && !conexidadDeclarada.isEmpty()) {
			if (asignarOficina) {
				if(addActuacion) {
					addActuacionConexidadDeclarada(conexidadDeclarada, expediente);
				}
				Oficina oficina = CamaraManager.getOficinaPrincipal(conexidadDeclarada.getOficina());
				if ((oficina != null) && TipoRadicacionEnumeration.isCompatible(oficina.getTipoOficina(), tipoRadicacion)) {
					ExpedienteMeAdd.instance().setExpedienteConexo(conexidadDeclarada.getExpedienteConexo());
	
					TipoAsignacionEnumeration.Type tipoAsignacion = TipoAsignacionEnumeration.getTipoAsignacionConexidadDenunciada(tipoRadicacion, conexidadDeclarada);
					asignarOficina(entityManager,
							expediente, 
							recurso,
							conexidadDeclarada.getOficina(), 
							tipoRadicacion, 
							tipoAsignacion, 
							TipoAsignacionLex100Enumeration.Type.conexidadDenunciada,
							codigoTipoCambioAsignacion,
							descripcionCambioAsignacion,
							tipoGiro, 
							true, //  debe compensar cuando asigna por conexidad  
							sorteo,
							rubro,
							oficinaAsignadora,
							fojasPase);
					
					conexidadDeclarada.setParametro(Messages.instance().get("conexidadDeclarada.asignada"));
					if (addVinculadoConexo && (conexidadDeclarada.getExpedienteConexo() != null) ){
						addVinculadoConexo(entityManager, tipoRadicacion, expediente, conexidadDeclarada.getExpedienteConexo(), tipoAsignacion, null, null, null);
					}
					// 
					// Copia la fiscalia de la radicacion del conexo si no la tiene asignada por turno
					OficinaAsignacionExp oficinaAsignacionConexo = ExpedienteManager.instance().findRadicacionExpediente(conexidadDeclarada.getExpedienteConexo(),tipoRadicacion);
					ExpedienteManager.instance().copiaRadicacionFiscalia(tipoRadicacion, expediente, oficinaAsignacionConexo);
					//

					return true;
				}
			}
		}
		return false;
	}
	
	private static void addActuacionConexidadDeclarada(ConexidadDeclarada conexidadDeclarada, Expediente expediente) {
		ActuacionExpManager.instance().addActuacionDeclaracionConexidad(conexidadDeclarada, expediente, ConexidadDeclarada.getConexidadDeclaradaAsString(conexidadDeclarada));
	}

	private static void addActuacionConexidadDeclaradaNoAsignada(ConexidadDeclarada conexidadDeclarada, Expediente expediente) {
		ActuacionExpManager.instance().addActuacionDeclaracionConexidadNoAsignada(conexidadDeclarada, expediente, ConexidadDeclarada.getConexidadDeclaradaAsString(conexidadDeclarada));
	}

	public static void addVinculadoConexo(EntityManager entityManager, TipoRadicacionEnumeration.Type tipoRadicacion, Expediente expediente, Expediente expedienteConexo, TipoAsignacionEnumeration.Type tipoAsignacion, Integer numeroLegajo, Integer anioLegajo, String caratulaOVD) {
		String tipoVinculado = null;
		if(TipoRadicacionEnumeration.isAnyJuzgado(tipoRadicacion)) {
			tipoVinculado = (String)TipoVinculadoEnumeration.Type.conexo_Juzgado.getValue();
		} else if(TipoRadicacionEnumeration.isAnySala(tipoRadicacion)) {
			tipoVinculado = (String)TipoVinculadoEnumeration.Type.conexo_Sala.getValue();
		} else {
			tipoVinculado = (String)TipoVinculadoEnumeration.Type.conexo.getValue();
		}
		if (tipoVinculado != null) {
			if ((tipoRadicacion != null) && tipoRadicacion.equals(ExpedienteMeAdd.instance().getTipoRadicacion())) {
				ExpedienteMeAdd.instance().setExpedienteConexo(expedienteConexo);
			}
			VinculadosExp vinculadoConexo = FamiliaManager.getVinculadoByTipo(entityManager, expediente, tipoVinculado);
			if (vinculadoConexo == null) {
				/**ATOS OVD */
				VinculadosExpManager.instance().createVinculado(tipoVinculado, expediente, expedienteConexo, null, null, (tipoAsignacion != null)? (String)tipoAsignacion.getValue(): null, numeroLegajo, anioLegajo, caratulaOVD);
				/**ATOS OVD */
			} else {
				vinculadoConexo.setExpedienteRelacionado(expedienteConexo);
				vinculadoConexo.setClaveExpedienteRelacionado(null);
				vinculadoConexo.setCaratulaExpedienteRelacionado(null);
				vinculadoConexo.setObservaciones(null);
				vinculadoConexo.setTipoAsignacion((tipoAsignacion != null)? (String)tipoAsignacion.getValue(): null);
				entityManager.flush();
			}
		}
	}

	private static CriterioCnx getCriterioAsignacion(Conexidad conexidad) {
		for (CriterioCnx criterio : conexidad.getCriterioCnxList()) {
			if (criterio.isAsigna()) {
				return criterio;
			}
		}
		return null;
	}

	private static TipoAsignacionEnumeration.Type getTipoAsignacion(TipoRadicacionEnumeration.Type tipoRadicacion, Conexidad conexidad) {
		TipoAsignacionEnumeration.Type tipoAsignacion = null;
		CriterioCnx criterioAsignacion = getCriterioAsignacion(conexidad);
		if ((criterioAsignacion != null) && (criterioAsignacion.getCodigoAsignacion() != null)){
			tipoAsignacion = TipoAsignacionEnumeration.fromValue(conexidad.getCriterioCnxList().get(0).getCodigoAsignacion(), 
					TipoRadicacionEnumeration.calculateCodigoTipoInstanciaByTipoRadicacion(tipoRadicacion),
					CamaraManager.isCamaraActualCivilCABA()? TipoAsignacionEnumeration.GRUPO_CIVIL: TipoAsignacionEnumeration.GRUPO_PENAL);
		}
		if(tipoAsignacion == null) {	
			tipoAsignacion = TipoAsignacionEnumeration.getTipoAsignacionConexidad(tipoRadicacion);
		}
		return tipoAsignacion;
	}
	
	public static void buscaConexidadInformativa(EntityManager entityManager, Expediente expediente, RecursoExp recurso, TipoRadicacionEnumeration.Type tipoRadicacion, Sorteo sorteo, String rubro, Oficina oficinaAsignadora, boolean ignoreCheckDobleInicio, FojasEdit fojasPase) throws Lex100Exception {
		/** ATOS OVD */		
		buscaConexidadPorCriterios(entityManager, expediente, recurso, tipoRadicacion, null, null, null, false, false, sorteo, rubro, null, false, oficinaAsignadora, false, ignoreCheckDobleInicio, fojasPase, null, null, null);
		/** ATOS OVD */
	}

//	public static boolean buscaConexidadPorCriterios(EntityManager entityManager, Expediente expediente, SorteoParametros sorteoParametros, boolean asignarOficina, Oficina oficinaAsignadora) throws Lex100Exception {
//		return buscaConexidadPorCriterios(entityManager,
//				expediente,
//				sorteoParametros,
//				TipoCambioAsignacionEnumeration.CONEXIDAD_DETECTADA,
//				getDescripcionCambioAsignacion(entityManager, TipoCambioAsignacionEnumeration.CONEXIDAD_DETECTADA, sorteoParametros.getTipoRadicacion(), expediente.getMateria()),
//				asignarOficina,
//				oficinaAsignadora, false);
//	}
	
	public static boolean buscaConexidadPorCriterios(EntityManager entityManager, Expediente expediente, SorteoParametros sorteoParametros, String codigoTipoCambioAsignacion, String descripcionCambioAsignacion, boolean asignarOficina, Oficina oficinaAsignadora, boolean usaConexidadDenunciada, boolean ignoreCheckDobleInicio, FojasEdit fojasPase) throws Lex100Exception {
	/** ATOS OVD */	
		return buscaConexidadPorCriterios(entityManager, 
				expediente, 
				sorteoParametros.getRecursoExp(),
				sorteoParametros.getTipoRadicacion(), 
				codigoTipoCambioAsignacion, 
				descripcionCambioAsignacion,
				sorteoParametros.getTipoGiro(),
				puedeCompensarPorConexidadAutomatica(expediente, sorteoParametros.getTipoRadicacion()),
				sorteoParametros.isDescompensarAnterior(),
				sorteoParametros.getSorteo(),
				sorteoParametros.getRubro(),
				sorteoParametros.getSiguienteSorteoEncadenado(),
				asignarOficina,
				oficinaAsignadora,
				usaConexidadDenunciada,
				ignoreCheckDobleInicio,
				fojasPase, null, null, null);
	/** ATOS OVD */
	}
	
	public static boolean buscaConexidadPorCriterios(EntityManager entityManager, Expediente expediente, RecursoExp recurso, TipoRadicacionEnumeration.Type tipoRadicacion, String codigoTipoCambioAsignacion, String descripcionCambioAsignacion, Type tipoGiro, boolean compensar, boolean descompensarAnterior, Sorteo sorteo, String rubro, SorteoParametros siguienteSorteoEncadenado, boolean asignarOficina, Oficina oficinaAsignadora, boolean usaConexidadDenunciada, boolean ignoreCheckDobleInicio, FojasEdit fojasPase,  Integer anioOVD, Integer legajoOVD, String caratulaOVD) throws Lex100Exception {

		boolean pararSiEncontrado = isConexidadPararSiEncontrada(expediente) && !TipoCausaEnumeration.isMediacion(expediente.getTipoCausa());

		Expediente expedienteConexoDeclarado = 	(expediente.getConexidadDeclarada() != null) ? expediente.getConexidadDeclarada().getExpedienteConexo(): null;

		List<Conexidad> conexidadList = ConexidadManager.instance().buscaConexidadPorCriterios(expediente, tipoRadicacion, expedienteConexoDeclarado, expediente.getParametroExpedienteList(), pararSiEncontrado, true, asignarOficina, ignoreCheckDobleInicio);

		boolean asignado = false;
		if (asignarOficina) {
			if (conexidadList != null){
				asignado = asignaRadicacionPorConexidad(entityManager, 
						tipoRadicacion, expediente, recurso, conexidadList, TipoAsignacionLex100Enumeration.Type.conexidadAutomatica,
						codigoTipoCambioAsignacion, descripcionCambioAsignacion, tipoGiro, compensar, descompensarAnterior, sorteo, rubro, true, oficinaAsignadora, fojasPase, anioOVD, legajoOVD, caratulaOVD);
				
				if(asignado) {
					if (TipoRadicacionEnumeration.isRadicacionJuzgado(tipoRadicacion) && TipoCausaEnumeration.isMediacion(expediente.getTipoCausa())){
						asignaMediadorPorConexidad(entityManager, oficinaAsignadora, tipoRadicacion, expediente, conexidadList);
					}
				}
			}
			if (usaConexidadDenunciada) {	// Si la conexidad denunciada no es priortaria, primero se busca conexidad y si no se encuentra entonces se asigna la declarada
				if (!asignado) {
					asignado = buscaConexidadDenunciada(entityManager, expediente, recurso, tipoRadicacion,
							TipoCambioAsignacionEnumeration.CONEXIDAD_SOLICITADA, 
							getDescripcionCambioAsignacion(entityManager, TipoCambioAsignacionEnumeration.CONEXIDAD_SOLICITADA, tipoRadicacion, expediente.getMateria()),
							tipoGiro, 
							sorteo, 
							rubro, 
							true,	// asigna
							oficinaAsignadora,
							true,
							true,
							fojasPase);
				} else if ((expediente.getConexidadDeclarada() != null) && !expediente.getConexidadDeclarada().isEmpty()) {
					addActuacionConexidadDeclarada(expediente.getConexidadDeclarada(), expediente);
				}
				if ((expediente.getConexidadDeclarada() != null) && !expediente.getConexidadDeclarada().isEmpty()) {
					Conexidad conexidad = getConexidadDetectada(expediente);
					if (!asignado || ((conexidad != null) && !conexidad.getExpedienteRelacionado().equals(expediente.getConexidadDeclarada().getExpedienteConexo()))) {
						// Conexidad declarada no otorgada
						expediente.getConexidadDeclarada().setParametro(Messages.instance().get("conexidadDeclarada.noasignada"));
						addActuacionConexidadDeclaradaNoAsignada(expediente.getConexidadDeclarada(), expediente);
					}
				}
			}
	
		}
		return asignado;
	}	

	private static Conexidad getConexidadDetectada(Expediente expediente) {
		for(Conexidad conexidad: expediente.getConexidadList()){
			if(Boolean.TRUE.equals(conexidad.getPrincipal())){
				return conexidad;
			}
		}
		return null;
	}

	public static boolean asignaMediadorPorConexidad (EntityManager entityManager, 
			Oficina oficinaAsignadora, TipoRadicacionEnumeration.Type tipoRadicacion,
			Expediente expediente,
			List<Conexidad> conexidadList) throws Lex100Exception {

		for(Conexidad conexidad: conexidadList) {
			if (ConexidadManager.instance().isAsigna(conexidad)) {
				
				boolean asignado = false;

				if (TipoCausaEnumeration.isMediacion(conexidad.getExpedienteRelacionado().getTipoCausa()) && ConexidadManager.isValidoParaAsignar(conexidad, tipoRadicacion)){
					OficinaAsignacionExp oficinaAsignacion = OficinaAsignacionExpSearch.findByTipoRadicacion(entityManager, conexidad.getExpedienteRelacionado(), (String)tipoRadicacion.getValue());
					if (oficinaAsignacion.getMediador() != null) {
						ExpedienteManager.instance().cambiarMediador(expediente, oficinaAsignadora, tipoRadicacion, oficinaAsignacion.getMediador(), expediente.getIdxAnaliticoFirst());
						asignado = true;
					}
				}
				if(asignado) {
					return true;
				}
			}
		}
		return false;
	}
	/** ATOS OVD */
	public static boolean asignaRadicacionPorConexidad (EntityManager entityManager, 
			TipoRadicacionEnumeration.Type tipoRadicacion,
			Expediente expediente,
			RecursoExp recurso,
			List<Conexidad> conexidadList,
			TipoAsignacionLex100Enumeration.Type tipoAsignacionLex100,
			String codigoTipoCambioAsignacion,
			String descripcionCambioAsignacion, Type tipoGiro,
			boolean compensar, boolean descompensarAnterior, Sorteo sorteo, String rubro,
			boolean addVinculadoConexo,
			Oficina oficinaAsignadora,
			FojasEdit fojasPase, Integer anioOVD, Integer legajoOVD, String caratulaOVD) throws Lex100Exception {

		for(Conexidad conexidad: conexidadList) {
			if (ConexidadManager.instance().isAsigna(conexidad)) {
				
				boolean asignado = asignaRadicacionPorConexidad(entityManager, 
						tipoRadicacion, expediente, recurso, conexidad, TipoAsignacionLex100Enumeration.Type.conexidadAutomatica,
						codigoTipoCambioAsignacion, descripcionCambioAsignacion, tipoGiro, compensar, descompensarAnterior, sorteo, rubro, addVinculadoConexo, oficinaAsignadora,
						fojasPase, anioOVD, legajoOVD, caratulaOVD);
				
				if(asignado) {
					return true;
				}
			}
		}
		return false;
	}
	/** ATOS OVD */
	public static boolean asignaRadicacionPorConexidad (EntityManager entityManager, 
			TipoRadicacionEnumeration.Type tipoRadicacion,
			Expediente expediente,
			RecursoExp recurso,
			Conexidad conexidad,
			TipoAsignacionLex100Enumeration.Type tipoAsignacionLex100,
			String codigoTipoCambioAsignacion,
			String descripcionCambioAsignacion, Type tipoGiro,
			boolean compensar, boolean descompensarAnterior, Sorteo sorteo, String rubro,
			boolean addVinculadoConexo,
			Oficina oficinaAsignadora,
			FojasEdit fojasPase, Integer legajoOVD, Integer anioOVD, String caratulaOVD) throws Lex100Exception {

		if (ConexidadManager.isValidoParaAsignar(conexidad, tipoRadicacion)){
			OficinaAsignacionExp oficinaAsignacionConexo = OficinaAsignacionExpSearch.findByTipoRadicacion(entityManager, conexidad.getExpedienteRelacionado(), (String)tipoRadicacion.getValue());
			if (oficinaAsignacionConexo != null) {
				Oficina oficina = oficinaAsignacionConexo.getOficina();
				if (oficina != null) {
					if (oficinaAsignacionConexo.getSecretaria() != null) {
						oficina = oficinaAsignacionConexo.getSecretaria();
					}
					if (addVinculadoConexo) {
						conexidad.setPrincipal(true);
						entityManager.flush();
					}
	
					TipoAsignacionEnumeration.Type tipoAsignacion = getTipoAsignacion(tipoRadicacion, conexidad);
	
					CriterioCnx criterioAsignacion = getCriterioAsignacion(conexidad);
					if ((criterioAsignacion != null) && (criterioAsignacion.getCodigoCambioAsignacion() != null)){
						codigoTipoCambioAsignacion = conexidad.getCriterioCnxList().get(0).getCodigoCambioAsignacion();
						descripcionCambioAsignacion = getDescripcionCambioAsignacion(entityManager, codigoTipoCambioAsignacion, tipoRadicacion, expediente.getMateria());
					}
					if (addVinculadoConexo) {
						/**ATOS OVD */
						addVinculadoConexo(entityManager, tipoRadicacion, expediente, conexidad.getExpedienteRelacionado(), tipoAsignacion, legajoOVD, anioOVD, caratulaOVD);
						/**ATOS OVD */
						entityManager.flush();
					}
	// si no hay recurso y ya estaba asignada la misma oficina, solo debe recompensar si el rubro es diferente
					OficinaAsignacionExp radicacionAnterior = OficinaAsignacionExpSearch.findByTipoRadicacion(entityManager, expediente, (String)tipoRadicacion.getValue());
					if ((recurso == null) && (radicacionAnterior != null) && (AbstractManager.equals(oficina, radicacionAnterior.getOficina()) || AbstractManager.equals(oficina, radicacionAnterior.getSecretaria())) ){
						if(compensar) {
							Oficina oficinaAnterior =  radicacionAnterior != null ? radicacionAnterior.getOficinaConSecretaria() : null;
							String rubroAnterior =  radicacionAnterior != null ? radicacionAnterior.getRubro() : null;
							Boolean compensacionAnterior =  radicacionAnterior != null ? radicacionAnterior.getCompensado(): null;
							if((rubro != null) && !rubro.equals(rubroAnterior)) {
								ExpedienteManager.instance().enviarCompensacion(sorteo, 
									TipoBolilleroSearch.getTipoBolilleroAsignacion(entityManager),
									expediente,
									recurso,
									tipoRadicacion,
									oficinaAnterior,
									oficina,
									rubroAnterior,
									rubro,
									compensacionAnterior,
									descompensarAnterior,
									SessionState.instance().getUsername(),
									codigoTipoCambioAsignacion,
									descripcionCambioAsignacion,
									false);
								radicacionAnterior.setRubro(rubro);
							}
						}
					} else {
						asignarOficina(entityManager,
								expediente,
								recurso,
								oficina, 
								tipoRadicacion, 
								tipoAsignacion,
								tipoAsignacionLex100,
								codigoTipoCambioAsignacion,
								descripcionCambioAsignacion,
								tipoGiro, 
								compensar,
								sorteo,
								rubro,
								oficinaAsignadora,
								fojasPase);
						
						// Copia la fiscalia de la radicacion del conexo si no la tiene asignada por turno
						ExpedienteManager.instance().copiaRadicacionFiscalia(tipoRadicacion, expediente, oficinaAsignacionConexo);
						if(!compensar) {
							// Copia el orden de circulacion de la radicacion del conexo
							ExpedienteManager.instance().copiaRadicacionOrdenCirculacion(tipoRadicacion, expediente, oficinaAsignacionConexo);
						}
						//
					}
					return true;
				}
			}
		}
		return false;
	}
/** ATOS OVD */
	private void forzeCommit() {
		try {
			if(Transaction.instance().isActive()){
				Transaction.instance().commit();
				Transaction.instance().begin();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ExpedienteIngreso createExpedienteIngreso(Expediente expediente,
			ExpedienteMeAdd expedienteEdit) {
		boolean ok;
		
		ExpedienteIngresoHome expedienteIngresoHome = ExpedienteIngresoHome.instance();
		expedienteIngresoHome.createNewInstance();
		ExpedienteIngreso expedienteIngreso = expedienteIngresoHome.getInstance();
		completarDatosIngreso(expediente, expedienteEdit, expedienteIngreso);
	    ok = expedienteIngresoHome.doAdd("ok") != null;
	    if(ok){
	    	ok = createOrUpdateParametrosExpediente(expedienteIngreso, expedienteEdit);
	    	expediente.setExpedienteIngreso(expedienteIngreso);
			if (expedienteEdit.showConexidadDeclarada()) {
				updateConexidadDeclaradaToExpediente(entityManager, expedienteIngreso, expedienteEdit.getConexidadDeclarada());
			}
	    }
	    return ok ? expedienteIngreso : null;
	}

	private void completarDatosIngreso(Expediente expediente,
			ExpedienteMeAdd expedienteEdit, ExpedienteIngreso expedienteIngreso) {
		expedienteIngreso.setExpediente(expediente);
		expedienteIngreso.setFechaIngreso(new Date());		
		OtrosDatosEdit datos = expedienteEdit.getOtrosDatos();
		expedienteIngreso.setAsesoria(datos.getAsesoria());
		expedienteIngreso.setDefensoria(datos.getDefensoria());
		expedienteIngreso.setFiscalia(datos.getFiscalia());
		expedienteIngreso.setAbonarTasa(datos.isAbonarTasa());
		expedienteIngreso.setExentoTasa(datos.isExentoTasa());
		expedienteIngreso.setNoAlcanzadoTasa(datos.isNoAlcanzadoTasa());
		expedienteIngreso.setPagoDiferidoTasa(datos.isPagoDiferidoTasa());
		expedienteIngreso.setMedidaPrecautoria(datos.isMedidaPrecautoria());
		expedienteIngreso.setMedidaPreliminar(datos.isMedidaPreliminar());
		expedienteIngreso.setPruebaAnticipada(datos.isPruebaAnticipada());

		expedienteIngreso.setAdjuntaCopia(datos.isAdjuntaCopia());
		expedienteIngreso.setResolucionAdministrativa(datos.isResolucionAdministrativa());
		expedienteIngreso.setUrgente(expedienteEdit.isIngresoUrgente());

		expedienteIngreso.setIngresoWeb(expedienteEdit.isIngresoWeb());

		expedienteIngreso.setUsuarioCreador(getUsername());
		
		expedienteIngreso.setFechaHecho((expedienteEdit.getExpedienteEspecialEdit().getFechaHecho() != null) ? 
				expedienteEdit.getExpedienteEspecialEdit().getFechaHecho():
				expedienteEdit.getExpedienteEspecialEdit().getFechaInformacion());

	}


	public static void copyConexidadDeclarada(ConexidadDeclarada from, ConexidadDeclarada to) {
		to.setNumeroExpedienteConexo(from.getNumeroExpedienteConexo());
		to.setAnioExpedienteConexo(from.getAnioExpedienteConexo());
		to.setAnteriorAlSistema(from.isAnteriorAlSistema());
		to.setTipoAsignacionConexo(from.getTipoAsignacionConexo());
		to.setDescripcionOficina(from.getDescripcionOficina());
		to.setOficina(from.getOficina());
		to.setExpedienteConexo(from.getExpedienteConexo());
		to.setObservaciones(from.getObservaciones());
		to.setParametro(from.getParametro());
	}


	public static boolean isEmpty(ConexidadDeclarada conexidadDeclarada) {
		return (conexidadDeclarada == null) || ((conexidadDeclarada.getNumeroExpedienteConexo() == null) || (conexidadDeclarada.getAnioExpedienteConexo() == null)) && Strings.isEmpty(conexidadDeclarada.getObservaciones());
	}

	private String getUsername() {
		return SessionState.instance().getUsername();
	}

	private boolean createOrUpdateParametrosExpediente(ExpedienteIngreso expedienteIngreso, ExpedienteMeAdd expedienteEdit) {
		StringBuffer changes = new StringBuffer();
		List <ParametroExpediente> otrosParametros = null;
		if((expedienteEdit.getParametroList() != null) && (expedienteEdit.getParametroList().size() > 0)){
			for(Parametro parametro:expedienteEdit.getParametroList()){
				String stringValue = expedienteEdit.getParametroValue(parametro);
				List<String> valueList = new ArrayList<String>();
				if(Boolean.TRUE.equals(parametro.getMultiple())) {
					valueList = Configuration.getCommaSeparatedAsNotNullList(stringValue);
				} else if (!Strings.isEmpty(stringValue)){
					valueList.add(stringValue);
				}
				List<String> oldValueList = ParametroExpedienteSearch.getParametroValues(entityManager, parametro, expedienteIngreso);
				String oldValue = Configuration.getCommaSeparatedString(oldValueList);
				boolean changed = !equals(valueList, oldValueList);
				if ((valueList.size() == 0) || changed) {
					ParametroExpedienteSearch.deleteParametro(entityManager, parametro, expedienteIngreso);
				}
				if (changed) {
					for(String value:valueList) {
						ParametroExpediente parametroExpediente = new ParametroExpediente();
						parametroExpediente.setExpedienteIngreso(expedienteIngreso);
						parametroExpediente.setParametro(parametro);
						parametroExpediente.setIndice(0);
						parametroExpediente.setLogicalDeleted(false);
						parametroExpediente.setStatus(AbstractLogicalDeletion.NORMAL_STATUS_FLAG);
						parametroExpediente.setUuid(GUID.generate());
						entityManager.persist(parametroExpediente);
						if(!Strings.isEmpty(value)){
							value = value.toUpperCase();
						}
						parametroExpediente.setValor(value);
						entityManager.flush();
					}
				}
				if(expedienteEdit.isEditMode()) {
					if (changed) {
						if (changes.length() > 0) {
							changes.append(", ");
						}
						if (!Strings.isEmpty(oldValue)) {
							if (!Strings.isEmpty(stringValue)) {
								changes.append(interpolate(getMessages().get("parametro.updated"), parametro.getLabel(), oldValue, stringValue));
							} else {
								changes.append(interpolate(getMessages().get("parametro.deleted"), parametro.getLabel(), oldValue));
							}
						} else {
							changes.append(interpolate(getMessages().get("parametro.added"), parametro.getLabel(), stringValue));
						}
					}
				}
			}
			otrosParametros = ParametroExpedienteSearch.getOtherParametros(entityManager, expedienteEdit.getParametroList(), expedienteIngreso);
			entityManager.flush();
		} else {
			otrosParametros = ParametroExpedienteSearch.getParametrosExpediente(entityManager, expedienteIngreso);
		}
		if (otrosParametros != null) {
			for(ParametroExpediente parametroExpediente:otrosParametros) {
				if (changes.length() > 0) {
					changes.append(", ");
				}
				changes.append(interpolate(getMessages().get("parametro.deleted"), parametroExpediente.getParametro().getLabel(), parametroExpediente.getValor()));
				entityManager.remove(parametroExpediente);
			}
		}
		entityManager.flush();
		entityManager.refresh(expedienteIngreso);
		expedienteEdit.setParametrosChanged((changes.length() > 0)? changes.toString(): null);
		return true;
	}


	private boolean equals(List<String> l1, List<String> l2) {
		if(l1 == null || l2 == null){
			return l1 == l2;
		}else{
			Set<String> s1 = new HashSet<String>(l1);
			Set<String> s2 = new HashSet<String>(l2);
			return s1.equals(s2);
		}
	}

	private boolean createExpedienteEspecialIfNeeded(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		boolean ok = true;
		if(expedienteEdit.getExpedienteEspecialEdit().isDefined()){
			ExpedienteEspecialHome expedienteEspecialHome = ExpedienteEspecialHome.instance();
			expedienteEspecialHome.createNewInstance();
			ExpedienteEspecial expedienteEspecial = expedienteEspecialHome.getInstance();
			
			checkFlagsExpedienteEspecial(expedienteEdit);
			
			completarDatosExpedienteEspecial(expediente, expedienteEdit,
					expedienteEspecial);
		    ok = expedienteEspecialHome.doAdd("ok") != null;
		    if (ok) {
		    	expediente.setExpedienteEspecial(expedienteEspecial);
		    }
		}
		return ok;
	}


	
	private void checkFlagsExpedienteEspecial(ExpedienteMeAdd expedienteEdit) {
		if(expedienteEdit.isDerechosHumanos()){
			expedienteEdit.getExpedienteEspecialEdit().setDerechosHumanos(true);
		}else{
			expedienteEdit.getExpedienteEspecialEdit().setDerechosHumanos(null);
		}
		if(expedienteEdit.isCorrupcion()){
			expedienteEdit.getExpedienteEspecialEdit().setCorrupcion(true);
		}else{
			expedienteEdit.getExpedienteEspecialEdit().setCorrupcion(false);
		}
		
		boolean detenidos = false;
		boolean arrestodomiciliario = false;
		boolean internados = false;
		boolean menor = false;
		boolean identidadReservada = false;
		
		for (IntervinienteEdit intervinienteEdit :  expedienteEdit.getIntervinienteEditList().getList()) {
			if(!intervinienteEdit.isEmpty()){
				detenidos |= intervinienteEdit.isDetenido();
				arrestodomiciliario |= intervinienteEdit.isArrestoDomiciliario();
				internados |= intervinienteEdit.isInternado();
				menor |= intervinienteEdit.isMenor();
				identidadReservada |= intervinienteEdit.isIdentidadReservada();
			}
		}
		
		expedienteEdit.getExpedienteEspecialEdit().setDetenidos(detenidos || arrestodomiciliario);
		expedienteEdit.getExpedienteEspecialEdit().setInternados(internados);
		expedienteEdit.getExpedienteEspecialEdit().setMenores(menor);
		expedienteEdit.getExpedienteEspecialEdit().setIdentidadReservada(identidadReservada);
		
		
//		for(IntervinienteEdit intervinienteEdit: expedienteEdit.getIntervinienteEditList().getList()){
//			if(!intervinienteEdit.isEmpty()){
//				if(intervinienteEdit.isDetenido()){
//					expedienteEdit.getExpedienteEspecialEdit().setDetenidos(true);
//				}
//				if(intervinienteEdit.isArrestoDomiciliario()){
//					expedienteEdit.getExpedienteEspecialEdit().setDetenidos(true);
//				}
//				if(intervinienteEdit.isInternado()){
//					expedienteEdit.getExpedienteEspecialEdit().setInternados(true);
//				}
//				if(intervinienteEdit.isMenor()){
//					expedienteEdit.getExpedienteEspecialEdit().setMenores(true);
//				}
//				if(intervinienteEdit.isIdentidadReservada()){
//					expedienteEdit.getExpedienteEspecialEdit().setIdentidadReservada(true);
//				}
//			}
//		}
		
		
		
		
		
	}

	private void completarDatosExpedienteEspecial(Expediente expediente,
			ExpedienteMeAdd expedienteEdit,
			ExpedienteEspecial expedienteEspecial) {
		ExpedienteEspecialEdit expedienteEspecialEdit = expedienteEdit.getExpedienteEspecialEdit();
		
		expedienteEspecial.setExpediente(expediente);
		
		expedienteEspecial.setTipoCodigoRelacionado(expedienteEspecialEdit.getNumeroCodigo());
		expedienteEspecial.setCodigoRelacionado(expedienteEspecialEdit.getNumero());
		expedienteEspecial.setDescripcionInformacion(limitText(expedienteEspecialEdit.getDescripcion(), 600));
		expedienteEspecial.setFechaInformacion(expedienteEspecialEdit.getFechaInformacion());
		if(expedienteEspecialEdit.getDependencia() != null){
			expedienteEspecial.setDependenciaOrigen(expedienteEspecialEdit.getDependencia().getDescripcion());
		} else if (!Strings.isEmpty(expedienteEspecialEdit.getOtraDependencia())) {
			expedienteEspecial.setDependenciaOrigen(expedienteEspecialEdit.getOtraDependencia());
		}
		if(expedienteEspecialEdit.getDependencia2() != null){
			expedienteEspecial.setDependenciaContendiente(expedienteEspecialEdit.getDependencia2().getDescripcion());
		} else if (!Strings.isEmpty(expedienteEspecialEdit.getOtraDependencia2())) {
			expedienteEspecial.setDependenciaContendiente(expedienteEspecialEdit.getOtraDependencia2());
		}
		expedienteEspecial.setSobres(expedienteEdit.getFojasEdit().getSobres());
		
//		expediente.setFojas(expedienteEspecial.getFojas());
//		expediente.setCuerpos(expedienteEspecial.getCuerpos());
//		expediente.setAgregados(expedienteEdit.getFojasEdit().getAgregados());
//		expediente.setSobres(expedienteEdit.getFojasEdit().getSobres());

		
		// Penal
		expedienteEspecial.setHechos(expedienteEspecialEdit.getHechos());
		expedienteEspecial.setIncidentes(expedienteEspecialEdit.getIncidentes());
		expedienteEspecial.setMenoresFlag(expedienteEspecialEdit.getMenores());
		expedienteEspecial.setInternadosFlag(expedienteEspecialEdit.getInternados());
		expedienteEspecial.setDetenidosFlag(expedienteEspecialEdit.getDetenidos());
		expedienteEspecial.setEfectosFlag(expedienteEspecialEdit.getEfecto());
		expedienteEspecial.setDefensorOficialFlag(expedienteEspecialEdit.isDefensorOficial());	//ITO (Incompetencia TO)
		expedienteEspecial.setIdentidadReservadaFlag(expedienteEspecialEdit.getIdentidadReservada());
		expedienteEspecial.setDeclaracionHecho(expedienteEspecialEdit.getDeclaracionHecho());
		expedienteEspecial.setDenunciaFirmada(expedienteEspecialEdit.isDenunciaFirmada());
		expedienteEspecial.setDerechosHumanosFlag(expedienteEspecialEdit.getDerechosHumanos());
		expedienteEspecial.setCorrupcion(expedienteEspecialEdit.isCorrupcion());
		if(expedienteEdit.isIngresoASala() && expedienteEdit.getRecursoEdit().getTipoRecurso() != null){
			expedienteEspecial.setDetenidosFlag(expedienteEdit.getRecursoEdit().isDetenidos());
			expedienteEspecial.setRegimenProcesal(expedienteEdit.getRecursoEdit().getRegimenProcesal());
		}

//		expedienteEspecial.setTipoRecurso(expedienteEdit.getTipoRecurso());

		// Exhortos
		expedienteEspecial.setJuezExhortante(expedienteEspecialEdit.getJuezExhortante());

		if (!Strings.isEmpty(expedienteEspecialEdit.getJuzgadoFueroExhorto())){
			expedienteEspecial.setJuzgadoFueroExhorto(expedienteEspecialEdit.getJuzgadoFueroExhorto());
		}
		if (!Strings.isEmpty(expedienteEspecialEdit.getCaratulaOrigen())){
			expedienteEspecial.setCaratulaOrigen(expedienteEspecialEdit.getCaratulaOrigen());
		}
	}

	private String limitText(String s, int len) {
		if(s == null || s.length() <= len){
			return s;
		}else{
			return s.substring(0, len);
		}
	}

	private void asignarNumeroExpediente(Expediente expediente, int numeroExpediente, int anioExpediente, boolean actualizarNumerador) throws Lex100Exception {
		try{
			if(actualizarNumerador) {
				Camara camara = SessionState.instance().getGlobalCamara();
				if(camara == null){
					throw new EntityOperationException("createExpediente.error.noCamaraDefined");
				}
				
				NumeradorExpedienteHome numeradorExpedienteHome = NumeradorExpedienteHome.instance();
				numeradorExpedienteHome.setLastNumero(camara, anioExpediente, getTipoNumeradorExpediente(expediente), numeroExpediente);
			}
			expediente.setNumero(numeroExpediente);
			expediente.setAnio(anioExpediente);
		} catch (NonUniqueResultException e) {
			throw new Lex100Exception(e);
		} catch (EntityOperationException e) {
			throw new Lex100Exception(e);
		}
	}

	private void generarNuevoNumeroExpediente(Expediente expediente) throws Lex100Exception {
		try{
			Camara camara = SessionState.instance().getGlobalCamara();
			if(camara == null){
				throw new EntityOperationException("createExpediente.error.noCamaraDefined");
			}
			int anioExpediente = getCurrentAnio();
			int tipoNumerador = getTipoNumeradorExpediente(expediente); 
			NumeradorExpedienteHome numeradorExpedienteHome = NumeradorExpedienteHome.instance();
			int numeroExpediente = numeradorExpedienteHome.getNextNumero(camara, anioExpediente, tipoNumerador);
			expediente.setNumero(numeroExpediente);
			expediente.setAnio(anioExpediente);
		} catch (NonUniqueResultException e) {
			throw new Lex100Exception(e);
		} catch (EntityOperationException e) {
			throw new Lex100Exception(e);
		}

	}

	private int getTipoNumeradorExpediente(Expediente expediente) {
		if(TipoCausaEnumeration.isMediacion(expediente.getTipoCausa())){
			return NumeradorExpedienteHome.TIPO_NUMERADOR_EXPEDIENTE_MEDIACION;
		}else{
			return NumeradorExpedienteHome.TIPO_NUMERADOR_EXPEDIENTE_JUDICIAL;			
		}
	}

	private int getCurrentAnio() {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date());
		return gc.get(GregorianCalendar.YEAR);
	}

	private boolean completarCamposCabecera(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		boolean ok = true;

		expediente.setNumero(-1);
		expediente.setAnio(-1);
		if(isMediacion(expedienteEdit)){
			expediente.setNaturaleza(NaturalezaSubexpedienteEnumeration.Type.mediacion.getValue().toString());
		}else{
			expediente.setNaturaleza(NaturalezaSubexpedienteEnumeration.Type.principal.getValue().toString());
		}
		expediente.setVerificacionMesa("N");
		expediente.setMedidasCautelares(expedienteEdit.isMedidasCautelares());
		expediente.setEstadoEsParte(expedienteEdit.isEstadoEsParte());
		
		expediente.setCompetencia(expedienteEdit.getCompetencia());
		expediente.setComentarios(expedienteEdit.getComentarios());
		
		// Fojas
		updateFojasToExpediente(expediente, expedienteEdit);

		// Monto
		updateMontoToExpediente(expediente, expedienteEdit);
		
		expediente.setNumeroSubexpediente(0);
		
		//SECLO
		updateSecloToExpediente(expediente, expedienteEdit);
		
		if ((expedienteEdit.getObjetoJuicio() == null) && expedienteEdit.isPenal()) {
			List<Materia> materias = new ArrayList<Materia>();
			materias.add(expedienteEdit.getMateria());
			ObjetoJuicio objetoJuicio = ObjetoJuicioSearch.findFirst(entityManager, materias);
			if(objetoJuicio != null){
				objetoJuicio = entityManager.find(ObjetoJuicio.class, objetoJuicio.getId());
			}
			expedienteEdit.setObjetoJuicio(objetoJuicio);
		}
		if (expedienteEdit.getObjetoJuicio() != null) {
			updateObjetoJuicioToExpediente(expediente, expedienteEdit);
		}
		TipoProceso tipoProceso  = expedienteEdit.getTipoProceso();
		if(tipoProceso == null) {
			if (expediente.getObjetoJuicioPrincipal() != null) {
				tipoProceso = expediente.getObjetoJuicioPrincipal().getTipoProceso();
			} else {
				tipoProceso = TipoProcesoEnumeration.instance().getDefaultTipoProceso(expedienteEdit.getTipoCausa().getMateria());
			}
		}
		if(tipoProceso != null){
			expediente.setTipoProceso(tipoProceso);
			int codigoTipoSubexpediente = isMediacion(expedienteEdit) 
							?	TipoSubexpedienteEnumeration.TIPO_SUBEXPEDIENTE_MEDIACION_CODIGO
							:	TipoSubexpedienteEnumeration.TIPO_SUBEXPEDIENTE_PRINCIPAL_CODIGO;
			expediente.setTipoSubexpediente(TipoSubexpedienteSearch.findByCodigo(entityManager, codigoTipoSubexpediente, expediente.getTipoProceso().getMateria()));
		} else {
			ok = false;
		}
		
		return ok;
	}

	private boolean isMediacion(ExpedienteMeAdd expedienteEdit) {
		TipoCausa tipoCausa = expedienteEdit.getExpedienteEspecialEdit().getTipoCausa();				
		return TipoCausaEnumeration.isMediacion(tipoCausa);
	}

	public boolean updateObjetoJuicioToExpediente(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		if(expedienteEdit.getObjetoJuicioEditList() != null && ExpedienteManager.instance().isMediacion(expediente)){
			expediente.getObjetoJuicioList().clear();
			for (ObjetoJuicioEdit objetoJuicioEdit : expedienteEdit.getObjetoJuicioEditList().getList()) {
				if(!objetoJuicioEdit.isEmpty()) {
					expediente.getObjetoJuicioList().add(objetoJuicioEdit.getObjetoJuicio());
				}
			}
		}				
		if (expedienteEdit.getObjetoJuicio() != null) {
			expediente.setObjetoJuicioPrincipal(entityManager.find(ObjetoJuicio.class, expedienteEdit.getObjetoJuicio().getId()));
		}
		expediente.setDescripcionObjetoJuicio(expedienteEdit.getDescripcionObjetoJuicio());
		return true;
	}
	
	public void updateTipoProcesoToExpediente(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		if(expedienteEdit.getTipoProceso() != null)
			expediente.setTipoProceso(expedienteEdit.getTipoProceso());
	}
	
	public void updateMontoToExpediente(Expediente expediente,	ExpedienteMeAdd expedienteEdit) {
		if (expedienteEdit.getMontoJuicioEdit().getMoneda() != null && expedienteEdit.getMontoJuicioEdit().getCantidad() != null) {
			expediente.setMoneda(entityManager.find(Moneda.class, expedienteEdit.getMontoJuicioEdit().getMoneda().getId()));
			expediente.setMonto(expedienteEdit.getMontoJuicioEdit().getCantidad());
		} else {
			expediente.setMoneda(null);
			expediente.setMonto(null);
		}
	}

	public void updateSecloToExpediente(Expediente expediente,	ExpedienteMeAdd expedienteEdit) {
		expediente.setNumeroSeclo(expedienteEdit.getNumeroSeclo());
		expediente.setAnioSeclo(expedienteEdit.getAnioSeclo());
	}

	public void updateFojasToExpediente(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		FojasEdit fojasEdit = expedienteEdit.getFojasEdit();
		expediente.setFojas(fojasEdit.getFojas());
		expediente.setCuerpos(fojasEdit.getCuerpos());
		expediente.setAgregados(fojasEdit.getAgregados());
	}

//	public boolean addDelitosToExpediente(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
//		boolean ok = true;
//		int numero = 1;
//		DelitoExpedienteHome delitoExpedienteHome = (DelitoExpedienteHome) Component.getInstance(DelitoExpedienteHome.class, true);
//		for(DelitoEdit delitoEdit: expedienteEdit.getDelitoEditList().getList()){
//			if (delitoEdit.getDelito() != null) {
//				delitoExpedienteHome.createNewInstance();
//				DelitoExpediente delitoExpediente = delitoExpedienteHome.getInstance();
//				delitoEdit.copyToDelitoExpediente(delitoExpediente);
//				delitoExpediente.setDelito(entityManager.find(Delito.class, delitoExpediente.getDelito().getId()));
//				delitoExpediente.setExpediente(expediente);
//				delitoExpediente.setNumero(numero);
//				numero++;
//				ok = delitoExpedienteHome.doAdd("ok") != null;
//				if(!ok){
//					break;
//				}
//				expediente.getDelitoExpedienteList().add(delitoExpedienteHome.getInstance());
//			}
//		}
//		return ok;
//	}
	
	public boolean updateDelitosToExpediente(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		boolean ok = true;
		int numero = countDelitos(expediente)+1;
		DelitoExpedienteHome delitoExpedienteHome = (DelitoExpedienteHome) Component.getInstance(DelitoExpedienteHome.class, true);
		for(DelitoEdit delitoEdit: expedienteEdit.getDelitoEditList().getList()){
			if(!delitoEdit.needUpdateOrAdd()){
				continue;
			}
			DelitoExpediente delitoExpediente = null;
			if(delitoEdit.isNew()){			
				delitoExpedienteHome.createNewInstance();
				delitoExpediente = delitoExpedienteHome.getInstance();
			}else{
				delitoExpediente = delitoEdit.getDelitoExpediente();
			}
			delitoExpediente.setDescripcionSinCodificar(null);
			delitoExpediente.setTentativa(delitoEdit.isTentativa());
			delitoExpediente.setDelito(entityManager.find(Delito.class, delitoEdit.getDelito().getId()));
			delitoExpediente.setEnConcurso(delitoEdit.getEnConcurso());
			if(delitoEdit.isNew()){
				delitoExpediente.setExpediente(expediente);
				delitoExpediente.setNumero(numero);
				numero++;
				ok = delitoExpedienteHome.doAdd("ok") != null;
				if(!ok){
					break;
				}
				expediente.getDelitoExpedienteList().add(delitoExpedienteHome.getInstance());
			}
		}
		if(ok && expedienteEdit.isEditMode()){
			deleteDelitosFromExpediente(expediente, expedienteEdit);
		}
		return ok;
	}

	public boolean deleteDelitosFromExpediente(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		boolean ok = true;
		List<DelitoEdit> deletedList = expedienteEdit.getDelitoEditList().getDeletedEntityList();
		if(deletedList != null){
			for(DelitoEdit delitoEdit: deletedList){
				if(!delitoEdit.needDelete()){
					continue;
				}
				entityManager.remove(delitoEdit.getDelitoExpediente());
				expediente.getDelitoExpedienteList().remove(delitoEdit.getDelitoExpediente());
			}
		}
		return ok;
	}

	private int countDelitos(Expediente expediente) {
		return expediente.getDelitoExpedienteList() == null ? 0 : expediente.getDelitoExpedienteList().size();
	}

	public boolean updateExpedienteIngresoToExpediente(Expediente expediente, ExpedienteMeAdd expedienteEdit) throws Lex100Exception{
		ExpedienteIngreso expedienteIngreso = expediente.getExpedienteIngreso();
		if (expedienteIngreso == null) {
			expedienteIngreso = createExpedienteIngreso(expediente, expedienteEdit);
		} else {
			completarDatosIngreso(expediente, expedienteEdit, expedienteIngreso);
			if (expedienteEdit.showConexidadDeclarada()) {
				updateConexidadDeclaradaToExpediente(entityManager, expedienteIngreso, expedienteEdit.getConexidadDeclarada());
			}
			createOrUpdateParametrosExpediente(expedienteIngreso, expedienteEdit);
		}
		return expedienteIngreso != null;
	}

	public boolean updateExpedienteEspecialToExpediente(Expediente expediente, ExpedienteMeAdd expedienteEdit) throws Lex100Exception{
		boolean ok = true;
		ExpedienteEspecial expedienteEspecial = expediente.getExpedienteEspecial();
    	if (expedienteEspecial == null) {
    		ok = createExpedienteEspecialIfNeeded(expediente, expedienteEdit);
    	} else {
    		if(expedienteEdit.getExpedienteEspecialEdit().isDefined()){
    			checkFlagsExpedienteEspecial(expedienteEdit);
    			
    			completarDatosExpedienteEspecial(expediente, expedienteEdit,
    					expedienteEspecial);
    		}
    	}
    	return ok;
	}

	public static void updateConexidadDeclaradaToExpediente(EntityManager entityManager, ExpedienteIngreso expedienteIngreso, ConexidadDeclarada conexidadDeclaradaEdit){
		//
		if (expedienteIngreso != null) {
			if(expedienteIngreso.getConexidadDeclarada() == null) {
				if (!isEmpty(conexidadDeclaradaEdit)) {
					ConexidadDeclarada newConexidadDeclarada = new ConexidadDeclarada(conexidadDeclaradaEdit);
					newConexidadDeclarada.setExpedienteIngreso(expedienteIngreso);
					newConexidadDeclarada.setExpediente(expedienteIngreso.getExpediente());
					expedienteIngreso.setConexidadDeclarada(newConexidadDeclarada);
					entityManager.persist(newConexidadDeclarada);
				}
			} else {
				if (isEmpty(conexidadDeclaradaEdit)) {
					ConexidadDeclarada conexidadDeclarada = expedienteIngreso.getConexidadDeclarada();
					expedienteIngreso.setConexidadDeclarada(null);
					 entityManager.remove(conexidadDeclarada);
				} else {
					copyConexidadDeclarada(conexidadDeclaradaEdit, expedienteIngreso.getConexidadDeclarada());
				}
			}
		}
		//
	}
	
	public boolean updateExpedienteInfoToExpediente(Expediente expediente, ExpedienteMeAdd expedienteEdit) throws Lex100Exception{
		boolean ok = true;
		ExpedienteInfo expedienteInfo = expediente.getExpedienteInfo();
		String stringValue = ""; //valor del parametro del expediente - Se utiliza solo para COMERCIAL
		try {
			for(Parametro parametro : expedienteEdit.getParametroList()){
				stringValue = expedienteEdit.getParametroValue(parametro);
			}
		} catch (Exception e) {
			getLog().debug("No hay parametros a recuperar");
		}
		
		String infoValue = (CamaraManager.isCamaraComercial(expediente.getCamaraActual())) ? stringValue : expedienteEdit.getExpedienteInfoValor();
    	if (expedienteInfo == null) {
			expedienteInfo = new ExpedienteInfo();
			expedienteInfo.setExpediente(expediente);
			
			//modificacion a la hecha por ATOS para COMERCIAL - Solo para COMERCIAL se guarda el valor del parametro, para el resto se guarda lo de texto de busqueda
			expedienteInfo.setValor(infoValue);
			
			//expedienteInfo.setValor((stringValue!=null && Strings.isEmpty(stringValue)) ? expedienteEdit.getExpedienteInfoValor() : stringValue);
			
			entityManager.persist(expedienteInfo);
			expediente.setExpedienteInfo(expedienteInfo);
		} else {
			expedienteInfo.setValor(infoValue);
		}
    	return ok;
	}
	
	private boolean completarIntervinientes(ExpedienteMeAdd expedienteEdit) {
		boolean ok = true;
		if(expedienteEdit.isPenal()){
			ok = expedienteEdit.crearIntervinienteNNSiNoHayDemandado();
		} else if (expedienteEdit.isObjetoJuicioAFIP()) {
			ok = expedienteEdit.crearIntervinienteAFIPSiNoHayActor();
		}
		return ok;
	}

	public void updateIntervinientesToExpediente(Expediente expediente, ExpedienteMeAdd expedienteEdit) throws Lex100Exception{
		ObjetoJuicio objetoJuicio = expediente.getObjetoJuicioPrincipal();

		IntervinienteEditList intervinienteEditList = expedienteEdit.getIntervinienteEditList();
		LetradoEditList letradoEditList = expedienteEdit.getLetradoEditList();
		
		if(expedienteEdit.isEditMode()){
			deleteIntervinientesFromExpediente(expediente, expedienteEdit);
		}
		updateIntervinientesFromListToExpediente(expediente, objetoJuicio, intervinienteEditList, expedienteEdit.showLetrados()? letradoEditList:null, expedienteEdit);
		entityManager.flush();
		ExpedienteManager.instance().actualizaTipoVoluntario(expediente);
		expediente.resetLitisConsorcio();
		entityManager.flush();

	}
	

//	private void addIntervinientesFromListToExpediente(Expediente expediente,ObjetoJuicio objetoJuicio, 
//				IntervinienteEditList intervinienteEditList,
//				LetradoEditList letradoEditList) throws Lex100Exception {
//		
//		int orden = 1;
//		for(IntervinienteEdit intervinienteEdit : intervinienteEditList.getList()){
//			if(!intervinienteEdit.isEmpty()){
//				Interviniente interviniente = intervinienteEdit.getInterviniente();
//				if(interviniente == null){
//					interviniente = createInterviniente(intervinienteEdit);				
//				}
//				
//				IntervinienteExp intervinienteExp = createIntervinienteExp(intervinienteEdit, expediente, interviniente, orden);
//				orden++;
//				
//				expediente.getIntervinienteExpList().add(intervinienteExp);
//				if(letradoEditList != null && isActor(intervinienteExp)){
//					addLetradosToIntervinienteExp(letradoEditList, intervinienteExp);
//				}
//			}
//		}		
//	}
	
	public int countIntervinientes(IntervinienteEditList intervinienteEditList){
		int count = 0;
		for(IntervinienteEdit intervinienteEdit : intervinienteEditList.getList()){
			if(!intervinienteEdit.needUpdateOrAdd()){
				continue;
			}
			count++;
		}
		return count;
	}
	
	private void updateIntervinientesFromListToExpediente(Expediente expediente,ObjetoJuicio objetoJuicio, 
			IntervinienteEditList intervinienteEditList,
			LetradoEditList letradoEditList, ExpedienteMeAdd expedienteEdit) throws Lex100Exception {
	
		intervinientesSeleccionados.clear();
		int orden = maxOrdenIntervinientes(expediente)+1;
		boolean mode = 	intervinienteEditList.isInAddExpedienteMode();
		intervinienteEditList.setInAddExpedienteMode(true);
		for(IntervinienteEdit intervinienteEdit : intervinienteEditList.getList()){
			if(!intervinienteEdit.isEmpty()){
				IntervinienteExp intervinienteExp = intervinienteEdit.getIntervinienteExp();
				if(intervinienteEdit.isNew()){
					intervinienteExp = createIntervinienteExp(expediente, orden, intervinienteEdit);
					intervinienteEdit.setIntervinienteExp(intervinienteExp);
					orden++;
					expediente.resetOrderedIntervinientes();																		
				}else {
					if(intervinienteEdit.isModified()){
						intervinienteEdit.copyFielsToInterviniente(intervinienteEdit.getIntervinienteExp().getInterviniente());
						intervinienteEdit.copyFieldsToIntervinienteExp(intervinienteEdit.getIntervinienteExp());
						intervinienteExp.setPteVerificacion(false);
					}				
				}
				
				if(intervinienteEdit.needUpdateOrAdd()){
					IntervinienteExpManager intervinienteExpManager = IntervinienteExpManager.instance();
					if (intervinienteEdit.isArrestoDomiciliario()) {//Debe ser la primera condición por que no es excluyente a detenido
						intervinienteExpManager.grabarArrestoDomiciliario(intervinienteExp,intervinienteEdit,  intervinienteEdit.getFechaDetencion(), expediente.getEstadoExpediente());
					} else if (intervinienteExp.isInternado()) {
						intervinienteExpManager.grabarInternamiento(intervinienteExp, intervinienteEdit.getCentroInternamiento(), intervinienteEdit.getFechaDetencion(), expediente.getEstadoExpediente());
					} else if (intervinienteExp.isDetenido()) {
						intervinienteExpManager.grabarDetencion(intervinienteExp, intervinienteEdit.getDependencia(), intervinienteEdit.getFechaDetencion(), expediente.getEstadoExpediente());
					}
					if (ExpedienteManager.instance().isVerDatosMedicos(expediente)) {
						intervinienteExpManager.grabarDatosMedicos(intervinienteExp, intervinienteEdit.getCentroInternamiento());
					}
					
				}
				if(letradoEditList != null) {
					updateLetradosToIntervinienteExp(letradoEditList, intervinienteExp, letradoEditList.isLetradosDelDemandado(), intervinienteEdit.isNew(), expedienteEdit);
				}
			}

		}
		intervinienteEditList.setInAddExpedienteMode(mode);
	}

	private IntervinienteExp createIntervinienteExp(Expediente expediente,
			int orden, IntervinienteEdit intervinienteEdit)
			throws Lex100Exception {
		IntervinienteExp intervinienteExp;
		Interviniente interviniente = intervinienteEdit.getInterviniente();
		if(interviniente == null){
			interviniente = createInterviniente(intervinienteEdit);				
		}
		intervinienteExp = createIntervinienteExp(intervinienteEdit, expediente, interviniente, orden);
		if(intervinienteEdit.isSelected()) {
			intervinientesSeleccionados.put(intervinienteExp, true);
		}
		
		expediente.getIntervinienteExpList().add(intervinienteExp);
		return intervinienteExp;
	}
	
	
	

	public boolean deleteIntervinientesFromExpediente(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		boolean ok = true;
		List<IntervinienteEdit> deletedList = expedienteEdit.getIntervinienteEditList().getDeletedEntityList();
		if(deletedList != null){
			for(IntervinienteEdit intervinienteEdit: deletedList){
				if(!intervinienteEdit.needDelete()){
					continue;
				}
				intervinienteEdit.getIntervinienteExp().setLogicalDeleted(true);
				expediente.getIntervinienteExpList().remove(intervinienteEdit.getIntervinienteExp());
				expediente.resetOrderedIntervinientes();
				
			}
		}
		return ok;
	}

	private int maxOrdenIntervinientes(Expediente expediente) {
		if (expediente.getIntervinienteExpList() != null ) {
			int orden = 0;
			for(IntervinienteExp intervinienteExp: expediente.getIntervinienteExpList()) {
				if (!intervinienteExp.isLogicalDeleted()) {
					orden = Math.max(orden, intervinienteExp.getOrden());
				}
			}
			return orden;
		}
		return 0;
	}

	private boolean isActor(IntervinienteExp intervinienteExp) {
		return intervinienteExp.isActor();
	}

	private boolean isDemandado(IntervinienteExp intervinienteExp) {
		return intervinienteExp.isDemandado();
	}

	private TipoInterviniente getTipoIntervinienteActor(Expediente expediente) {
		return getTipoIntervinienteActor(expediente.getObjetoJuicioPrincipal());
	}

	private TipoInterviniente getTipoIntervinienteActor(ObjetoJuicio objetoJuicio) {
		return getTipoIntervinienteByOrder(objetoJuicio, INTERVINIENTE_ACTOR_ORDER);
	}
		
	private TipoInterviniente getTipoIntervinienteDemandado(ObjetoJuicio objetoJuicio) {
		return getTipoIntervinienteByOrder(objetoJuicio, INTERVINIENTE_DEMANDADO_ORDER);
	}

	private TipoInterviniente getTipoIntervinienteByOrder(ObjetoJuicio objetoJuicio, int order) {
		if (objetoJuicio != null) {
			if(objetoJuicio.getTipoProceso() != null){
				for(TipoInterviniente tipoInterviniente: objetoJuicio.getTipoProceso().getTipoIntervinienteList()){
					if(Integer.valueOf(order).equals(tipoInterviniente.getNumeroOrden())){
						return tipoInterviniente;
					}
				}
			}
		}
		// FIXME si no hay objetoJuicio hay que asignar el tipoInterviniente que corresponda ....
		return null;
	}

	private IntervinienteExp createIntervinienteExp(IntervinienteEdit intervinienteEdit, Expediente expediente,	Interviniente interviniente, int orden) throws Lex100Exception {
		intervinienteExpHome.createNewInstance();
		
		IntervinienteExp intervinienteExp = intervinienteExpHome.getInstance();
		
		TipoInterviniente tipoInterviniente = intervinienteEdit.getTipoInterviniente();
		
		if(tipoInterviniente == null){
			tipoInterviniente = getTipoIntervinienteActor(expediente);
		}
				
		intervinienteExp.setInterviniente(interviniente);
		intervinienteExp.setExpediente(expediente);
		intervinienteExp.setOrden(orden);
		// Se resetean los valores por defecto del After New
		intervinienteExp.setLocalidad(null);
		intervinienteExp.setProvincia(null);
		
		intervinienteEdit.copyFieldsToIntervinienteExp(intervinienteExp);

		if(intervinienteExp.getTipoInterviniente() == null){
			intervinienteExp.setTipoInterviniente(getTipoIntervinienteActor(expediente));
		}
		
//		if(intervinienteEdit.getPoder() != null) {
//			intervinienteExp.setPoder(intervinienteEdit.getPoder());
//		}

		
		/* FIXME: en IntervinienteExpHomeListener en beforeAdd tambien hace persist de interviniente (esot debe esta mal ??? revisar) 
		 *  see: 	String retInterviniente = intervinienteHome.persist();
		 * 
		boolean ok = intervinienteExpHome.doAdd("ok") != null;
		if(!ok){
			throw new Lex100Exception("can not add intervinienteExp");
		}
		*/
		//
		entityManager.persist(intervinienteExp);
		entityManager.flush();

		return intervinienteExp;
	}

	private boolean equalDocumento(Interviniente interviniente, Poder poder) {
		String codigoTipoDocumento = (interviniente.getTipoDocumentoIdentidad() != null)? interviniente.getTipoDocumentoIdentidad().getCodigo(): null;
		return TipoDocumentoIdentidadEnumeration.equals(codigoTipoDocumento, interviniente.getNumeroDocId(),
				poder.getCodTipoDocumento(), poder.getNumeroDocumento());
	}
	
	
	private void updateLetradosToIntervinienteExp(LetradoEditList letradoEditList, IntervinienteExp intervinienteExp, boolean letradosDelDemandado, boolean isNewInterviniente, ExpedienteMeAdd expedienteEdit) throws Lex100Exception {
		if((letradoEditList != null) && (letradoEditList.isLetradosDelDemandado() && isDemandado(intervinienteExp)) || (!letradoEditList.isLetradosDelDemandado() && isActor(intervinienteExp))){
			if(isNewInterviniente){
				for(LetradoEdit letradoEdit: letradoEditList.getList()){
					if(!letradoEdit.isEmpty()){
						if ((letradoEdit.getPoder() == null) || 
								(equals(intervinienteExp.getInterviniente().getNombre(), letradoEdit.getPoder().getNombre()) || 
								(!Strings.isEmpty(intervinienteExp.getInterviniente().getNumeroDocId()) && equalDocumento(intervinienteExp.getInterviniente(), letradoEdit.getPoder())))) {
							addLetradoToInterviniente(letradoEdit, intervinienteExp, expedienteEdit);
						}
					}						
				}
			} else {
				
				// check borrados
				List<LetradoIntervinienteExp> letradosABorrar = new ArrayList<LetradoIntervinienteExp>();
				for(LetradoIntervinienteExp letradoIntervinienteExp : intervinienteExp.getActiveLetrados()){
					LetradoEdit letradoEdit = null;
					letradoEdit = findLetradoInLetradoEditList(letradoEditList, letradoIntervinienteExp);
					if(letradoEdit == null){
						letradosABorrar.add(letradoIntervinienteExp);
					}
				}
				for (LetradoIntervinienteExp letradoIntervinienteExp : letradosABorrar) {
					removeLetradoToInterviniente(letradoIntervinienteExp);
				}
				
				// check modificaciones 
				for(LetradoEdit letradoEdit : letradoEditList.getList()){
					if(!letradoEdit.isEmpty()){
						LetradoIntervinienteExp letradoIntervinienteExp = findLetradoInIntervineinte(intervinienteExp, letradoEdit);
						if(letradoIntervinienteExp != null){
							updateLetradoIntevinienteExp(letradoIntervinienteExp, letradoEdit, expedienteEdit);
						} else if (letradoEdit.isNew()){
							if ((letradoEdit.getPoder() == null) || 
									(equals(intervinienteExp.getInterviniente().getNombre(), letradoEdit.getPoder().getNombre()) || 
									(!Strings.isEmpty(intervinienteExp.getInterviniente().getNumeroDocId()) && equalDocumento(intervinienteExp.getInterviniente(), letradoEdit.getPoder())))) {
								addLetradoToInterviniente(letradoEdit, intervinienteExp, expedienteEdit);	
							}
						}
					}					
				}
			}
		}
	}

	private LetradoEdit findLetradoInLetradoEditList(LetradoEditList letradoEditList, LetradoIntervinienteExp letradoIntervinienteExp) {
		LetradoEdit ret = null;
		for(LetradoEdit letradoEdit : letradoEditList.getList()){
			if(isEqualLetrado(letradoIntervinienteExp,letradoEdit)){
				ret = letradoEdit;
				break;
			}
		}
		return ret;
	}

	private void updateLetradoIntevinienteExp(LetradoIntervinienteExp letradoIntervinienteExp, LetradoEdit letradoEdit, ExpedienteMeAdd expedienteEdit) {
		letradoIntervinienteExp.setTipoLetrado(getTipoLetrado(letradoEdit.getTipoLetrado(), expedienteEdit.getMateria()));
		Letrado letrado = letradoIntervinienteExp.getLetrado();
		letrado.setNombre(letradoEdit.getNombre());
		letrado.setTomo(letradoEdit.getTomo());
		letrado.setFolio(letradoEdit.getFolio());			
		letrado.setMatricula(letradoEdit.getMatricula());
		letrado.setCuitCuil(letradoEdit.getCuitCuil());				
	}
	

	private LetradoIntervinienteExp findLetradoInIntervineinte(IntervinienteExp intervinienteExp, LetradoEdit letradoEdit) {
		LetradoIntervinienteExp ret = null;
		for(LetradoIntervinienteExp letradoIntervinienteExp : intervinienteExp.getActiveLetrados()){
			if(isEqualLetrado(letradoIntervinienteExp,letradoEdit)){
				ret = letradoIntervinienteExp;
				break;
			}
		}
		return ret;
	}
	
	
	private static boolean isEqualLetrado(LetradoIntervinienteExp letradoIntervinienteExp, LetradoEdit letradoEdit) {
		boolean ret = false;
		if ((letradoIntervinienteExp.getPoder() != null) && (letradoEdit.getPoder() != null)) {
			if ((letradoIntervinienteExp.getPoder().getNumero() != null) && !letradoIntervinienteExp.getPoder().getNumero().equals(letradoEdit.getPoder().getNumero())) {
				return false;
			}
			if ((letradoIntervinienteExp.getPoder().getAnio() != null) && !letradoIntervinienteExp.getPoder().getAnio().equals(letradoEdit.getPoder().getAnio())) {
				return false;
			}
		}
		if(isEqualLetradoPadron(letradoIntervinienteExp, letradoEdit)){
			ret = true;
		}else if(isEqualNombreDocumento(letradoIntervinienteExp, letradoEdit)){
			ret = true;
		}
		return ret;
	}
	
	
	private static boolean isEqualLetradoPadron(LetradoIntervinienteExp letradoIntervinienteExp, LetradoEdit letradoEdit){
		boolean ret = false;
		if(isLetradoPadron(letradoIntervinienteExp.getLetrado()) && isLetradoPadron(letradoEdit.getLetrado())){
			if(letradoIntervinienteExp.getLetrado().getTomo().equals(letradoEdit.getLetrado().getTomo())
					&& letradoIntervinienteExp.getLetrado().getFolio().equals(letradoEdit.getLetrado().getFolio())){
				ret = true;
			}
		}
		return ret;
	}
	
	private static boolean isEqualNombreDocumento(LetradoIntervinienteExp letradoIntervinienteExp, LetradoEdit letradoEdit){
		boolean ret = false;
		if(letradoIntervinienteExp.getLetrado() != null && letradoEdit.getLetrado() != null){
			if(Strings.emptyIfNull(letradoIntervinienteExp.getLetrado().getNombre()).equals(Strings.emptyIfNull(letradoEdit.getLetrado().getNombre()))){
				ret = equals(letradoIntervinienteExp.getLetrado().getCuitCuil(),
						letradoIntervinienteExp.getLetrado().getMatricula(),
						letradoEdit.getCuitCuil(),
						letradoEdit.getMatricula());
			}
		}
		return ret;
	}
	
	public static boolean isEqualLetrado(LetradoIntervinienteExp letradoIntervinienteExp1, LetradoIntervinienteExp letradoIntervinienteExp2) {
		boolean ret = false;
		if ((letradoIntervinienteExp1.getPoder() != null) && (letradoIntervinienteExp2.getPoder() != null)) {
			if ((letradoIntervinienteExp1.getPoder().getNumero() != null) && !letradoIntervinienteExp1.getPoder().getNumero().equals(letradoIntervinienteExp2.getPoder().getNumero())) {
				return false;
			}
			if ((letradoIntervinienteExp1.getPoder().getAnio() != null) && !letradoIntervinienteExp1.getPoder().getAnio().equals(letradoIntervinienteExp2.getPoder().getAnio())) {
				return false;
			}
		}
		if(isEqualLetradoPadron(letradoIntervinienteExp1, letradoIntervinienteExp2)){
			ret = true;
		}else if(isEqualNombreDocumento(letradoIntervinienteExp1, letradoIntervinienteExp2)){
			ret = true; 
		}
		return ret;
	}
	
	
	private static boolean isEqualLetradoPadron(LetradoIntervinienteExp letradoIntervinienteExp1, LetradoIntervinienteExp letradoIntervinienteExp2){
		boolean ret = false;
		if(isLetradoPadron(letradoIntervinienteExp1.getLetrado()) && isLetradoPadron(letradoIntervinienteExp2.getLetrado())){
			if(letradoIntervinienteExp1.getLetrado().getTomo().equals(letradoIntervinienteExp2.getLetrado().getTomo())
					&& letradoIntervinienteExp1.getLetrado().getFolio().equals(letradoIntervinienteExp2.getLetrado().getFolio())){
				ret = true;
			}
		}
		return ret;
	}
	
	private static boolean isEqualNombreDocumento(LetradoIntervinienteExp letradoIntervinienteExp1, LetradoIntervinienteExp letradoIntervinienteExp2){
		boolean ret = false;
		if(letradoIntervinienteExp1.getLetrado() != null && letradoIntervinienteExp2.getLetrado() != null){
			if(Strings.emptyIfNull(letradoIntervinienteExp1.getLetrado().getNombre()).equals(Strings.emptyIfNull(letradoIntervinienteExp2.getLetrado().getNombre()))){
				ret = equals(letradoIntervinienteExp1.getLetrado().getCuitCuil(),
						letradoIntervinienteExp1.getLetrado().getMatricula(),
						letradoIntervinienteExp2.getLetrado().getCuitCuil(),
						letradoIntervinienteExp2.getLetrado().getMatricula());
			}
		}
		return ret;
	}
	private static boolean equals(String cuit1, String mat1, String cuit2, String mat2) {
		return equals(cuit1, cuit2) && equals(mat1, mat2);
	}

	public static boolean equals(String s1, String s2) {
		return s1 == null ? s2 == null : s1.equals(s2); 
	}

	
	private static boolean isLetradoPadron(Letrado letrado){
		return(letrado != null && Strings.nullIfEmpty(letrado.getTomo()) != null && Strings.nullIfEmpty(letrado.getFolio()) != null);
	}
	
	
	private void removeLetradoToInterviniente(LetradoIntervinienteExp letradoIntervinienteExp) {
		letradoIntervinienteExp.setLogicalDeleted(true);
		letradoIntervinienteExp.getIntervinienteExp().getLetradoIntervinienteExpList().remove(letradoIntervinienteExp);
	}

	

//	private void addLetradosToIntervinienteExp(LetradoEditList letradoEditList,	IntervinienteExp intervinienteExp) throws Lex100Exception {
//		boolean ok;
//		for(LetradoEdit letradoEdit: letradoEditList.getList()){
//			if(!letradoEdit.needUpdateOrAdd()){
//				continue;
//			}
//			if(letradoEdit.isNew()){
//				addLetradoToInterviniente(letradoEdit, intervinienteExp);				
//			} else {
//				if (letradoEdit.getLetrado() != null) {
//					letradoEdit.copyFielsTo(letradoEdit.getLetrado());
//				}
//			}
//		}
//	}

	public LetradoIntervinienteExp addLetradoToInterviniente(LetradoEdit letradoEdit, IntervinienteExp intervinienteExp, ExpedienteMeAdd expedienteEdit) throws Lex100Exception{
		return addLetradoToInterviniente (letradoEdit, intervinienteExp, expedienteEdit.getMateria(), expedienteEdit.isIngresoWeb());
	}
	
	public LetradoIntervinienteExp addLetradoToInterviniente(LetradoEdit letradoEdit, IntervinienteExp intervinienteExp, Materia materia, boolean ingresoWeb) throws Lex100Exception{
		letradoIntervinienteExpHome.createNewInstance();
		LetradoIntervinienteExp letradoIntervinienteExp = letradoIntervinienteExpHome.getInstance();
		letradoIntervinienteExp.setIntervinienteExp(intervinienteExp);
		letradoIntervinienteExp.setTipoLetrado(getTipoLetrado(letradoEdit.getTipoLetrado(), materia));
		if (letradoEdit.getVfletrado() != null) {
			letradoIntervinienteExpHome.setvfLetrado(letradoEdit.getVfletrado());
			letradoIntervinienteExpHome.setTipoDocumento(letradoEdit.getTipoDocumento());
			letradoIntervinienteExpHome.setDocumento(letradoEdit.getDocumento());
		} else {
			letradoIntervinienteExp.setLetrado(createLetrado(letradoEdit));
		}
		
		letradoIntervinienteExp.setPoder(letradoEdit.getPoder());
		if (!Strings.isEmpty(letradoEdit.getCuil())) {
			letradoIntervinienteExp.setCuil(letradoEdit.getCuil());
		}
		if (!Strings.isEmpty(letradoEdit.getEmail())) {
			letradoIntervinienteExp.setCorreoElectronico(letradoEdit.getEmail());
			letradoIntervinienteExp.setCorreoNotiElect(letradoEdit.getEmail());
		}
		boolean ok = letradoIntervinienteExpHome.doAdd("ok") != null;
		
		if ((ingresoWeb || ConfiguracionMateriaManager.instance().isCopiarConstituidoEnLetrado(SessionState.instance().getGlobalCamara())) && !Strings.isEmpty(intervinienteExp.getDomicilio())) {
			letradoIntervinienteExpHome.copiaConstituido(intervinienteExp, letradoIntervinienteExp);
			entityManager.flush();
		}
		if(!ok){
			throw new Lex100Exception("No se ha podido añadir el letrado al Interviniente");
		}
		
		return letradoIntervinienteExp;
								
	}
	
	private TipoLetrado getTipoLetrado(String codigo, Materia materia) {
		if(codigo == null){
			codigo = DEFAULT_TIPO_LETRADO_CODIGO;
		}
		return tipoLetradoEnumeration.getItemByCodigo(codigo, materia);
	}

	private Letrado createLetrado(LetradoEdit letradoEdit) throws Lex100Exception {
		if(letradoEdit.getLetrado() != null){
			return letradoEdit.getLetrado();
		}else if(letradoEdit.isEmpty()){
			return null;
		}else{
			letradoHome.createNewInstance();
			Letrado letrado = letradoHome.getInstance();
			letradoEdit.copyFielsTo(letrado);
			boolean ok = letradoHome.doAdd("ok") != null;
			if(!ok){
				throw new Lex100Exception("can not add letrado");
			}
			return letrado;
		}
	}

	private Interviniente createInterviniente(IntervinienteEdit intervinienteEdit)	throws Lex100Exception {
		intervinienteHome.createNewInstance();
		Interviniente interviniente = intervinienteHome.getInstance();
		intervinienteEdit.copyFielsToInterviniente(interviniente);
		boolean ok = intervinienteHome.doAdd("ok") != null;
		if(!ok){
			throw new Lex100Exception("can not add interviniente");
		}
		return interviniente;
	}

	private Date incrementaUnSegundo(Date fecha) {
		if (fecha != null) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(fecha);
			calendar.add(Calendar.SECOND, 1);
			fecha = calendar.getTime();
		}
		return fecha;
	}


	public static CreateExpedienteAction instance(){
		return (CreateExpedienteAction) Component.getInstance(CreateExpedienteAction.class, true);
	}
	
	
	private Integer getAsInteger(String number) {
		Integer value = null;
		try {
			value = Integer.valueOf(number.trim());
		} catch (Exception e) {
		}
		return value;
	}
	
	private Long getAsLong(String number){
		Long value = null;
		try {
			value = Long.valueOf(number.trim());
		} catch (Exception e) {
		}
		return value;
	}
	
	
	@Transactional
	public void asignarOficinaMed(ExpedienteMeAdd expedienteEdit) throws Lex100Exception {		
		//SorteoExpedienteWeb.getInstance().actualizarBaseDeDatosLetrado(entry.getIdCausa(),expediente);
	}
	
	@Transactional
	public void crearExpedienteYAsignarOficinaWeb(ExpedienteMeAdd expedienteEdit) throws Lex100Exception {		
		entityManager.joinTransaction();
		try {
			checkRecurso(expedienteEdit);
			
			TipoRadicacionEnumeration.Type tipoRadicacion = expedienteEdit.getTipoRadicacion();	//ITO (Incompetencia TO)
			Oficina oficinaActual = (getOficinaActual()!=null)?getOficinaActual():expedienteEdit.getOficinaActual();
			System.out.println("crearExpedienteYAsignarOficinaWeb -> oficinaActual es " +(oficinaActual==null?"NULL":(oficinaActual.getCodigo()+" - "+oficinaActual.getDescripcion())));
			boolean inMesaDeEntrada = expedienteEdit.getInMesaDeEntrada();
			
			RubrosInfo rubrosInfo = calculaRubrosAsignacion(expedienteEdit, tipoRadicacion);
			System.out.println("RubroInfo -> " +(rubrosInfo!=null?"TRUE":"NULL"));
			System.out.println("GlobalCamara -> " +(expedienteEdit.getGlobalCamara()!=null?"TRUE":"NULL"));
			System.out.println("inMesaDeEntrada -> " +inMesaDeEntrada);
			System.out.println("mesaSorteo -> " +(mesaSorteo!=null?"TRUE":"NULL"));
			Oficina oficinaSorteadora = oficinaActual;
			Materia materia = expedienteEdit.getMateria();
			System.out.println("crearExpedienteYAsignarOficinaWeb -> luego de obtener la materia antes de buscar el sorteo -> materia es " +(materia==null?"NULL":(materia.getDescripcion()+" - "+materia.getGrupo())));
			Sorteo sorteo = null;			

			if(!expedienteEdit.isIngresoAnteriorSistema()){
				boolean sorteoRequired = expedienteEdit.isIngresoDiferido() || expedienteEdit.isRadicacionPrevia() || inMesaDeEntrada;
				sorteo = mesaSorteo.buscaSorteo(oficinaSorteadora, 
						!sorteoRequired,
						tipoRadicacion, 
						materia.getGrupo(), 
						materia, 
						expedienteEdit.getCompetencia(), 
						rubrosInfo.getRubroAsignacion(), 
						rubrosInfo.getRubro(),
						sorteoRequired);
				System.out.println("Create Expediente Action: el sorteo es " + sorteo);				
			} else System.out.println("no entro al ingresoAnteriorSistema");	
			
			getLog().info("Creamos el expediente obtenido");
			ExpedienteIngreso expedienteIngreso = ingresarExpediente(expedienteEdit, null);
			getLog().info("Expediente creado");
			
			String codigoTipoCambioAsignacion = getCodigoTipoCambioAsignacion(expedienteEdit, inMesaDeEntrada);
			RecursoExp recursoASortear = getRecursoASortear(expedienteIngreso.getExpediente());
			
			/*******ATOS Comercial *******/
			//Fecha: 07-05-2014
			//Desarrollador:  Luis Suarez
			expedienteIngreso.getExpediente().setExpedienteWeb(expedienteEdit.getIdCausa() != null);
			/*******ATOS Comercial *******/
			
			SorteoParametros sorteoParametros = mesaSorteo.createSorteoParametrosWeb(sorteo,
					TipoBolilleroSearch.getTipoBolilleroAsignacion(entityManager),
					oficinaSorteadora, 
					expedienteIngreso.getExpediente(), 
					recursoASortear,
					tipoRadicacion, 
					codigoTipoCambioAsignacion,
					getDescripcionCambioAsignacion(expedienteEdit, codigoTipoCambioAsignacion, tipoRadicacion),
					expedienteEdit.getTipoGiro(), 
					null, 
					rubrosInfo.getRubro(), 
					(expedienteIngreso != null) ? expedienteIngreso.getFechaHecho(): null,
					false,
					null,
					true,
					expedienteEdit.getIdCausa());
			
			/** comentar  para ejecutar en la cola de sorteo*/
			//sorteoParametros.setAsignaPorConexidad(true);
			//sorteoParametros.setBuscaConexidad(true); 
			//SorteoManager.instance().runTransactionSorteoWebComercial(sorteoParametros);
			/** *************************** */	
			
			/** descomentar  para ejecutar en la cola de sorteo*/
			//asignarOficinaConexidadSorteo(expedienteIngreso, expedienteEdit, sorteoParametros);
			asignarOficinaConexidadSorteoWeb(expedienteIngreso, expedienteEdit, sorteoParametros);
			/** *************************** */
			
			
		} catch (Exception e) {
			getLog().error("Error creando expediente", e);
			getStatusMessages().addFromResourceBundle(Severity.ERROR, "completarIngresoExpediente.error");
			getRedirect().setViewId("/web/home.xhtml");
			getRedirect().execute();
		}
	}
	
	/** ATOS COMERCIAL 
	 * @throws SorteoException */
	
	
	@Transactional
	public void asignarOficinaConexidadSorteoWeb(ExpedienteIngreso expedienteIngreso, 
			ExpedienteMeAdd expedienteEdit, 
			SorteoParametros sorteoParametros) throws Lex100Exception, SorteoException {
		
		boolean sortear = false;
		boolean asignada = false;

		boolean busquedaConexidadAsincrona = ConfiguracionMateriaManager.instance().isConexidadAsincrona(SessionState.instance().getGlobalCamara());

		boolean conexidadInformativa = ConexidadManager.isConexidadInformativa(sorteoParametros.getTipoRadicacion(), expedienteIngreso.getExpediente().getCamara(), expedienteIngreso.getExpediente().getMateria());
		boolean asignaPorConexidad;
		
		String descripcionCambioAsignacionConexidadSolicitada = getDescripcionCambioAsignacion(expedienteEdit, TipoCambioAsignacionEnumeration.CONEXIDAD_SOLICITADA, sorteoParametros.getTipoRadicacion());			
		
		asignada =	buscaConexidadDenunciada(entityManager, 
				expedienteIngreso.getExpediente(), 
				sorteoParametros.getRecursoExp(),
				sorteoParametros.getTipoRadicacion(),
				TipoCambioAsignacionEnumeration.CONEXIDAD_SOLICITADA, 
				descripcionCambioAsignacionConexidadSolicitada,
				sorteoParametros.getTipoGiro(), 
				sorteoParametros.getSorteo(), 
				sorteoParametros.getRubro(), 
				isConexidadDenunciadaPrioritaria(expedienteIngreso.getExpediente()) || conexidadInformativa,	// asigna
				sorteoParametros.getOficinaSorteo(),
				true,
				true,
				sorteoParametros.getFojasPase());
		
		busquedaConexidadAsincrona = !expedienteIngreso.getExpediente().getObjetoJuicio().isSeleccionaConexidad() && ConfiguracionMateriaManager.instance().isConexidadAsincrona(SessionState.instance().getGlobalCamara());
		asignaPorConexidad = !conexidadInformativa && !asignada && !expedienteIngreso.getExpediente().getObjetoJuicio().isSeleccionaConexidad();
		if(!busquedaConexidadAsincrona || expedienteIngreso.getExpediente().getObjetoJuicio().isSeleccionaConexidad()) {
			String descripcionCambioAsignacionConexidadDetectada = getDescripcionCambioAsignacion(expedienteEdit, TipoCambioAsignacionEnumeration.CONEXIDAD_DETECTADA, sorteoParametros.getTipoRadicacion());			
			asignada |= buscaConexidadPorCriterios(entityManager, 
					expedienteIngreso.getExpediente(),
					sorteoParametros,
					TipoCambioAsignacionEnumeration.CONEXIDAD_DETECTADA, 
					descripcionCambioAsignacionConexidadDetectada,
					asignaPorConexidad, 
					sorteoParametros.getOficinaSorteo(),
					!isConexidadDenunciadaPrioritaria(expedienteIngreso.getExpediente()),
					expedienteEdit.isIgnoreCheckDobleInicio(),
					sorteoParametros.getFojasPase());
			if (!asignada) {
				if (TipoCausaEnumeration.isProvenienteMediacion(expedienteEdit.getTipoCausa()) && isAsignaJuzgadoMediacion(expedienteEdit.getTipoCausa())){
					asignada = asignarJuzgadoMediacion(entityManager, 
							expedienteIngreso.getExpediente(), 
							sorteoParametros.getOficinaSorteo(),
							sorteoParametros.getSorteo(),
							sorteoParametros.getRubro(),
							sorteoParametros.getTipoGiro(),
							null,
							sorteoParametros.getFojasPase());
				}
			}
		} else {
			sorteoParametros.setBuscaConexidad(true);
			sorteoParametros.setAsignaPorConexidad(asignaPorConexidad);
		}
		if (!asignada) {
			if (expedienteIngreso.getExpediente().getObjetoJuicio().isSeleccionaConexidad() && expedienteIngreso.getExpediente().getConexidadList().size() > 0) {
				expedienteEdit.setSorteoParametros(sorteoParametros);
			} else {
				sortear = true;
			}
		}
		boolean enviadoSorteo = false;
		if(sortear){
			enviadoSorteo = sortearWeb(expedienteEdit, sorteoParametros);
		}
		if (!enviadoSorteo) {
			Boolean entraASortear = false;
			if (TipoRadicacionEnumeration.isRadicacionJuzgado(sorteoParametros.getTipoRadicacion()) && TipoCausaEnumeration.isMediacion(expedienteIngreso.getExpediente().getTipoCausa())){
				OficinaAsignacionExp radicacion = ExpedienteManager.instance().findRadicacionExpediente(expedienteIngreso.getExpediente(), sorteoParametros.getTipoRadicacion());
				if ((radicacion != null) && radicacion.getMediador() == null) { 
					sorteoParametros.setSorteoOperation(SorteoOperation.sorteoMediador);
					entraASortear = true;
					SorteoManager.instance().runTransactionSorteoWebComercial(sorteoParametros);
				}
			}
			
			SorteoParametros parametrosSiguienteSorteo = sorteoParametros.getSiguienteSorteoEncadenado();
			if ((parametrosSiguienteSorteo != null) && (ExpedienteManager.instance().findRadicacionExpediente(expedienteIngreso.getExpediente(), parametrosSiguienteSorteo.getTipoRadicacion())== null)) {
				entraASortear = true;
				SorteoManager.instance().runTransactionSorteoWebComercial(sorteoParametros);
			}
			
			/** ATOS COMERCIAL */

			if(CamaraManager.isCamaraActualCOM() && !entraASortear && asignaPorConexidad) {
				if(expedienteEdit.getIdCausa()!=null) {
//					SorteoExpedienteWeb.getInstance().setConexidadSatisfactoria(true);
					SorteoExpedienteWeb.getInstance().actualizarBaseDeDatosLetrado(expedienteEdit.getIdCausa(),expedienteIngreso.getExpediente());
				}
			}

		}
	}
	
	private boolean sortearWeb(ExpedienteMeAdd expedienteEdit, SorteoParametros sorteoParametros) throws Lex100Exception, SorteoException{
		boolean enviadoSorteo = false;
		List<Oficina> oficinasActivas = mesaSorteo.getOficinasSorteoActivas(sorteoParametros.getSorteo(), sorteoParametros.getFechaTurnosActivos());
		if(oficinasActivas.size() == 1){
			TipoAsignacionEnumeration.Type tipoAsignacionTurno = TipoAsignacionEnumeration.getTipoTurno(sorteoParametros.getTipoRadicacion());

			asignacionDirectaAOficina(sorteoParametros.getExpediente().getExpedienteIngreso(), 
					oficinasActivas.get(0), 
					sorteoParametros.getOficinaSorteo(),
					sorteoParametros.getTipoRadicacion(), 
					tipoAsignacionTurno,
					TipoAsignacionLex100Enumeration.Type.turno,
					TipoCambioAsignacionEnumeration.INGRESO_CAUSAS_TRAMITE,		// ????? REVISAR TIPO
					getDescripcionCambioAsignacion(expedienteEdit, TipoCambioAsignacionEnumeration.INGRESO_CAUSAS_TRAMITE, sorteoParametros.getTipoRadicacion()),
					sorteoParametros.getSorteo(),
					sorteoParametros.getRubro(),
					sorteoParametros.getTipoGiro(),
					puedeCompensarPorAsignacionDirecta(sorteoParametros.getExpediente(), sorteoParametros.getTipoRadicacion()),
					null,
					false);
		} else  {
			if(expedienteEdit.showMinisteriosPublicos()){
				List<Integer> restringirSoloAOficinaIds = null;
				restringirSoloAOficinaIds = excluirOficinaPorOficinaTurno(oficinasActivas, expedienteEdit);
				if(restringirSoloAOficinaIds != null){
					sorteoParametros.setOnlyOficinasIds(restringirSoloAOficinaIds);
				}
			}
			SorteoManager.instance().runTransactionSorteoWebComercial(sorteoParametros);
			enviadoSorteo = true;
		}
		return enviadoSorteo;
	}

	/** ATOS COMERCIAL */
	
	
	/** ATOS OVD */
	private void createVinculadoOVD(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		if (expedienteEdit.isCompletoOVD() && !expediente.tieneOVD(expedienteEdit.getLegajoOVD(), expedienteEdit.getAnioLegajoOVD())){	
			VinculadosExpManager.instance().createVinculadoOVD(expediente, expedienteEdit);
			expedienteManager.esViolenciaDomestica(expediente);
		}
	}
	/** ATOS OVD */

}

