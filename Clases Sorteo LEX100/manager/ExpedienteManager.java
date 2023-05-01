package com.base100.lex100.manager.expediente;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Status;
import javax.transaction.Synchronization;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.cache.CacheProvider;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.UserTransaction;
import org.jboss.seam.util.Strings;

import com.base100.document.generator.xml.IXmlDataSourceTags;
import com.base100.document.model.Document;
import com.base100.document.model.DocumentMetadata;
import com.base100.document.model.definition.DocumentDefinition;
import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.Lex100Exception;
import com.base100.lex100.component.audit.AbstractLogicalDeletion;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.currentdate.CurrentDateManager;
import com.base100.lex100.component.ecm.EcmManagerFactory;
import com.base100.lex100.component.ecm.IEcmManager;
import com.base100.lex100.component.enumeration.CodigoSeleccionEstadoExpedienteEnumeration;
import com.base100.lex100.component.enumeration.CodigoTipoOficinaEnumeration;
import com.base100.lex100.component.enumeration.DelitoEnumeration;
import com.base100.lex100.component.enumeration.DependenciaEnumeration;
import com.base100.lex100.component.enumeration.EstadoExpedienteEnumeration;
import com.base100.lex100.component.enumeration.EstadoRemesaEnumeration;
import com.base100.lex100.component.enumeration.EstadoResolucionRecursoEnumeration;
import com.base100.lex100.component.enumeration.MateriaEnumeration;
import com.base100.lex100.component.enumeration.MotivoPaseEnumeration;
import com.base100.lex100.component.enumeration.NaturalezaSubexpedienteEnumeration;
import com.base100.lex100.component.enumeration.ObjetoJuicioEnumeration;
import com.base100.lex100.component.enumeration.OficinaEnumeration;
import com.base100.lex100.component.enumeration.ParametroConfiguracionMateriaEnumeration;
import com.base100.lex100.component.enumeration.PersonalOficinaEnumeration;
import com.base100.lex100.component.enumeration.ResultadoVisitaEnumeration;
import com.base100.lex100.component.enumeration.SituacionExpedienteEnumeration;
import com.base100.lex100.component.enumeration.TipoActuacionEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionLex100Enumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionOficinaEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionResponsableEnumeration;
import com.base100.lex100.component.enumeration.TipoCambioAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoCaratulaEnumeration;
import com.base100.lex100.component.enumeration.TipoCausaEnumeration;
import com.base100.lex100.component.enumeration.TipoCedulaEnumeration;
import com.base100.lex100.component.enumeration.TipoDocumentoBandejaEnumeration;
import com.base100.lex100.component.enumeration.TipoDocumentoEnumeration;
import com.base100.lex100.component.enumeration.TipoDocumentoIdentidadEnumeration;
import com.base100.lex100.component.enumeration.TipoEditorModeloEnumeration;
import com.base100.lex100.component.enumeration.TipoFinalizacionEnumeration;
import com.base100.lex100.component.enumeration.TipoGiroEnumeration;
import com.base100.lex100.component.enumeration.TipoInformacionEnumeration;
import com.base100.lex100.component.enumeration.TipoInstanciaEnumeration;
import com.base100.lex100.component.enumeration.TipoIntervinienteEnumeration;
import com.base100.lex100.component.enumeration.TipoOficinaEnumeration;
import com.base100.lex100.component.enumeration.TipoOficinaTurnoEnumeration;
import com.base100.lex100.component.enumeration.TipoPaseEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration.Type;
import com.base100.lex100.component.enumeration.TipoReasignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoRecursoEnumeration;
import com.base100.lex100.component.enumeration.TipoResolucionRecursoEnumeration;
import com.base100.lex100.component.enumeration.TipoRestriccionEnumeration;
import com.base100.lex100.component.enumeration.TipoSalaResolucionRecursoEnumeration;
import com.base100.lex100.component.enumeration.TipoVinculadoEnumeration;
import com.base100.lex100.component.enumeration.VerificacionMesaEnumeration;
import com.base100.lex100.component.estadoexpediente.EstadoExpedientePredefinedEnumeration;
import com.base100.lex100.component.jasper.JasperReportManager;
import com.base100.lex100.component.jasper.report.CaratulaReportManager;
import com.base100.lex100.component.jasper.report.EtiquetaMediacionReportManager;
import com.base100.lex100.component.misc.DateUtil;
import com.base100.lex100.component.validator.CuilValidator;
import com.base100.lex100.controller.entity.VFLetrado.VFLetradoSearch;
import com.base100.lex100.controller.entity.actuacionExp.ActuacionExpSearch;
import com.base100.lex100.controller.entity.cambioAsignacionExp.CambioAsignacionExpSearch;
import com.base100.lex100.controller.entity.documentoBandeja.DocumentoBandejaHome;
import com.base100.lex100.controller.entity.estadoExpediente.EstadoExpedienteSearch;
import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.controller.entity.expediente.ExpedienteList;
import com.base100.lex100.controller.entity.expediente.ExpedienteListState;
import com.base100.lex100.controller.entity.expediente.ExpedienteSearchState;
import com.base100.lex100.controller.entity.expedienteAntiguedad.AntiguedadExpedienteSearch;
import com.base100.lex100.controller.entity.expedienteTramite.ExpedienteTramiteSearch;
import com.base100.lex100.controller.entity.expedientesAsignadosSecretariaCSJN.ExpedientesAsignadosSecretariaCSJNSearch;
import com.base100.lex100.controller.entity.interviniente.IntervinienteHome;
import com.base100.lex100.controller.entity.intervinienteExp.IntervinienteExpHome;
import com.base100.lex100.controller.entity.intervinienteExp.IntervinienteExpList;
import com.base100.lex100.controller.entity.letradoIntervinienteExp.LetradoIntervinienteExpHome;
import com.base100.lex100.controller.entity.oficina.OficinaSearch;
import com.base100.lex100.controller.entity.oficinaAsignacionExp.OficinaAsignacionExpSearch;
import com.base100.lex100.controller.entity.omPreguntas.OmPreguntasList;
import com.base100.lex100.controller.entity.omRespuestas.OmRespuestasHome;
import com.base100.lex100.controller.entity.peritoExp.PeritoExpList;
import com.base100.lex100.controller.entity.resolucionRecursoExp.ResolucionRecursoExpHome;
import com.base100.lex100.controller.entity.responsableExp.ResponsableExpSearch;
import com.base100.lex100.controller.entity.tipoBolillero.TipoBolilleroSearch;
import com.base100.lex100.controller.entity.tipoInstancia.TipoInstanciaSearch;
import com.base100.lex100.controller.entity.tipoOficina.TipoOficinaSearch;
import com.base100.lex100.controller.entity.turnoFiscalia.TurnoFiscaliaSearch;
import com.base100.lex100.controller.entity.vocaliasResponsablesExpedientes.VocaliasResponsablesExpedientesSearch;
import com.base100.lex100.entity.ActuacionExp;
import com.base100.lex100.entity.CabeceraLIP;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.CambioAsignacionExp;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.Conexidad;
import com.base100.lex100.entity.DelitoExpediente;
import com.base100.lex100.entity.Dependencia;
import com.base100.lex100.entity.DetalleSubasta;
import com.base100.lex100.entity.DocumentoExp;
import com.base100.lex100.entity.EfectoExp;
import com.base100.lex100.entity.EstadoExpediente;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.ExpedienteEspecial;
import com.base100.lex100.entity.Fiscalia;
import com.base100.lex100.entity.FormularioCorrupcionExp;
import com.base100.lex100.entity.Interviniente;
import com.base100.lex100.entity.IntervinienteExp;
import com.base100.lex100.entity.IntervinientePersonalExp;
import com.base100.lex100.entity.IntervinienteVisitaExp;
import com.base100.lex100.entity.Letrado;
import com.base100.lex100.entity.LetradoIntervinienteExp;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Mediador;
import com.base100.lex100.entity.Modelo;
import com.base100.lex100.entity.MotivoPase;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.OficinaViolenciaDomesticaExp;
import com.base100.lex100.entity.OmPreguntas;
import com.base100.lex100.entity.OrdenCirculacionRecurso;
import com.base100.lex100.entity.OrdenCirculacionTipoRecurso;
import com.base100.lex100.entity.PaseExp;
import com.base100.lex100.entity.Perito;
import com.base100.lex100.entity.PeritoExp;
import com.base100.lex100.entity.PersonalOficina;
import com.base100.lex100.entity.PrestamoExp;
import com.base100.lex100.entity.Profesion;
import com.base100.lex100.entity.RecursoExp;
import com.base100.lex100.entity.ReglaTipoRemesa;
import com.base100.lex100.entity.RemesaExpediente;
import com.base100.lex100.entity.RemesaExpedienteLinea;
import com.base100.lex100.entity.ResolucionRecursoExp;
import com.base100.lex100.entity.ResponsableExp;
import com.base100.lex100.entity.SituacionEfecto;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.TipoBolillero;
import com.base100.lex100.entity.TipoCambioAsignacion;
import com.base100.lex100.entity.TipoCaratula;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.TipoDocumentoIdentidad;
import com.base100.lex100.entity.TipoInformacion;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoInterviniente;
import com.base100.lex100.entity.TipoOficina;
import com.base100.lex100.entity.TipoProceso;
import com.base100.lex100.entity.TipoRecurso;
import com.base100.lex100.entity.TipoRemesa;
import com.base100.lex100.entity.TipoSubexpediente;
import com.base100.lex100.entity.TurnoFiscalia;
import com.base100.lex100.entity.TurnoFiscaliaSala;
import com.base100.lex100.entity.VFLetrado;
import com.base100.lex100.entity.VinculadosExp;
import com.base100.lex100.homelistener.ExpedienteHomeListener;
import com.base100.lex100.manager.AbstractManager;
import com.base100.lex100.manager.accion.AccionActualizarReservaExpediente;
import com.base100.lex100.manager.accion.AccionAutoRadicarExpediente;
import com.base100.lex100.manager.accion.AccionCalcularConexidad;
import com.base100.lex100.manager.accion.AccionCambioElevacion;
import com.base100.lex100.manager.accion.AccionCambioEstadoExpediente;
import com.base100.lex100.manager.accion.AccionCambioSecretaria;
import com.base100.lex100.manager.accion.AccionCambioSituacionExpediente;
import com.base100.lex100.manager.accion.AccionCambioTipoCausaExpediente;
import com.base100.lex100.manager.accion.AccionCambioTipoProcesoExpediente;
import com.base100.lex100.manager.accion.AccionCambioTipoSubexpedienteExpediente;
import com.base100.lex100.manager.accion.AccionCambioTramite;
import com.base100.lex100.manager.accion.AccionCancelarCuaderno;
import com.base100.lex100.manager.accion.AccionCancelarElevacionTO;
import com.base100.lex100.manager.accion.AccionCancelarPase;
import com.base100.lex100.manager.accion.AccionCerrarCuaderno;
import com.base100.lex100.manager.accion.AccionPase;
import com.base100.lex100.manager.accion.AccionReabrirCuaderno;
import com.base100.lex100.manager.accion.AccionRecibirPase;
import com.base100.lex100.manager.accion.AccionRehabilitacionPlazoVencido;
import com.base100.lex100.manager.accion.AccionRetornoPase;
import com.base100.lex100.manager.accion.AccionTraerExpediente;
import com.base100.lex100.manager.accion.impl.AccionException;
import com.base100.lex100.manager.accion.impl.AccionManager;
import com.base100.lex100.manager.actuacionExp.ActuacionExpManager;
import com.base100.lex100.manager.alarma.AlarmaManager;
import com.base100.lex100.manager.calendario.CalendarioManager;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.configuracionMateria.ConfiguracionMateriaManager;
import com.base100.lex100.manager.despacho.IExpedienteXMLDatasourceTags;
import com.base100.lex100.manager.expediente.evento.EventoInfo;
import com.base100.lex100.manager.expediente.evento.EventoManager;
import com.base100.lex100.manager.expediente.evento.RecursoInfo;
import com.base100.lex100.manager.intervinienteExp.IntervinienteExpManager;
import com.base100.lex100.manager.legajoPersonalidadExp.LegajoPersonalidadExpManager;
import com.base100.lex100.manager.mediador.SorteadorMediador;
import com.base100.lex100.manager.objetoJuicio.ObjetoJuicioManager;
import com.base100.lex100.manager.oficina.OficinaManager;
import com.base100.lex100.manager.ordenCirculacion.OrdenCirculacionManager;
import com.base100.lex100.manager.pase.PaseExpManager;
import com.base100.lex100.manager.personalOficina.PersonalOficinaManager;
import com.base100.lex100.manager.recursoExp.RecursoExpManager;
import com.base100.lex100.manager.remesa.RemesaExpedienteManager;
import com.base100.lex100.manager.system.ImpresionManager;
import com.base100.lex100.manager.usuarioExpediente.UsuarioExpedienteManager;
import com.base100.lex100.manager.web.WebManager;
import com.base100.lex100.mesaEntrada.MesaEntradaDelivery;
import com.base100.lex100.mesaEntrada.asignacion.ExpedienteMeAsignacion;
import com.base100.lex100.mesaEntrada.asignacion.TipoSorteo;
import com.base100.lex100.mesaEntrada.conexidad.ConexidadManager;
import com.base100.lex100.mesaEntrada.conexidad.FamiliaManager;
import com.base100.lex100.mesaEntrada.ingreso.ExpedienteMeAdd;
import com.base100.lex100.mesaEntrada.ingreso.FojasEdit;
import com.base100.lex100.mesaEntrada.sorteo.MesaSorteo;
import com.base100.lex100.mesaEntrada.sorteo.RubrosInfo;
import com.base100.lex100.mesaEntrada.sorteo.SorteoManager;
import com.base100.lex100.mesaEntrada.sorteo.SorteoOperation;
import com.base100.lex100.mesaEntrada.sorteo.SorteoParametros;
import com.base100.lex100.util.CalendarUtil;

@Name("expedienteManager")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class ExpedienteManager extends AbstractManager implements IExpedienteXMLDatasourceTags{
	public static final String NUMERO_ABREVIADO = " Nº ";
	public static final String CODIGO_ABREVIADO = " COD. ";
	public static final Integer PENAL_ORDINARIO = 9;
	
	private static final int LONGITUD_CARATULA_REDUCIDA = 85;
	private static final int LONGITUD_TIPO_SUBEXPEDIENTE_REDUCIDA = 14;

	public static final String EXPEDIENTE_NAME = "Expediente";

	public final static String ESTADO_SIN_DEFINIR_CODIGO = (String)EstadoExpedientePredefinedEnumeration.Type.$SD.getValue();
	public final static String ESTADO_SIN_INICIAR_CODIGO = (String)EstadoExpedientePredefinedEnumeration.Type.$SI.getValue();
	public final static String ESTADO_INICIADO_CODIGO = (String)EstadoExpedientePredefinedEnumeration.Type.$IN.getValue();
	public final static String ESTADO_DEMANDA_INICIACION_CODIGO = (String)EstadoExpedientePredefinedEnumeration.Type.D01.getValue();

	public static final String ENTITY_ID_PROPERTY = "entityId";
	public static final String ENTITY_NAME_PROPERTY = "entityName";
	public static final String ANIO_PROPERTY = "anioExpediente";
	public static final String NUM_EXPEDIENTE_PROPERTY = "numeroExpediente";
	public static final String NUM_SUBEXPEDIENTE_PROPERTY = "numeroSubexpediente";
	public static final String CLAVE_PROPERTY = "claveExpediente";
	public static final String CARATULA_PROPERTY = "caratulaExpediente";
	public static final String SITUACION_PROPERTY = "situacionExpediente";
	public static final String FECHA_INICIO_PROPERTY = "fechaInicioExpediente";
	public static final String FECHA_SITUACION_PROPERTY = "fechaSituacionExpediente";
	public static final String FECHA_PASE_PROPERTY = "fechaPaseExpediente";
	public static final String FECHA_PRESTAMO_PROPERTY = "fechaPrestamoExpediente";
	public static final String FECHA_ESTADO_PROPERTY = "fechaEstadoExpediente";
	public static final String FECHA_ARCHIVO_PROPERTY = "fechaArchivoExpediente";
	public static final String FECHA_SORTEO_CARGA_PROPERTY = "fechaSorteoCargaExpediente";
	public static final String COMENTARIOS_PROPERTY = "comentariosExpediente";
	public static final String PRIVADO_PROPERTY = "privadoExpediente";
	public static final String QUIEBRA_PROPERTY = "quiebraExpediente";
	public static final String RESERVADO_PROPERTY = "reservadoExpediente";
	public static final String LEGAJO_ARCHIVO_PROPERTY = "legajoArchivoExpediente";
	public static final String LEGAJO_PARALIZADO_PROPERTY = "legajoParalizadoExpediente";
	public static final String ESTADO_PROPERTY = "estadoExpediente";
	public static final String ID_EXPEDIENTE_ORIGEN_PROPERTY = "idExpedienteOrigenExpediente";
	public static final String ID_CAMARA_PROPERTY = "idCamaraExpediente";
	public static final String ID_MONEDA_PROPERTY = "idMonedaExpediente";
	public static final String ID_OFICINA_ACTUAL_PROPERTY = "idOficinaActualExpediente";
	public static final String ID_OFICINA_PASE_PROPERTY = "idOficinaPaseExpediente";
	public static final String ID_OFICINA_RETORNO_PROPERTY = "idOficinaRetornoExpediente";
	public static final String DOMICILIO_INTERVINIENTE_EXP_PROPERTY = "domicilioIntervinienteExpExpediente";
	public static final String LOCALIDAD_INTERVINIENTE_EXP_PROPERTY = "localidadIntervinienteExpExpediente";
	public static final String NOMBRE_INTERVINIENTE_INTERVINIENTE_EXP_PROPERTY = "nombreIntervinienteIntervinienteExpExpediente";
	public static final String DOMICILIO_INTERVINIENTE_INTERVINIENTE_EXP_PROPERTY = "domicilioIntervinienteIntervinienteExpExpediente";
	public static final String LOCALIDAD_INTERVINIENTE_INTERVINIENTE_EXP_PROPERTY = "localidadIntervinienteIntervinienteExpExpediente";
	public static final String NOMBRE_LETRADO_INTERVINIENTE_EXP_PROPERTY = "nombreLetradoIntervinienteExpExpediente";
	public static final String DOMICILIO_LETRADO_INTERVINIENTE_EXP_PROPERTY = "domicilioLetradoIntervinienteExpExpediente";
	public static final String LOCALIDAD_LETRADO_INTERVINIENTE_EXP_PROPERTY = "localidadLetradoIntervinienteExpExpediente";

	public final static DocumentDefinition EXPEDIENTE_DEFINITION;
	private static final DecimalFormat decimalFormat9 = new DecimalFormat("000000000");
	private static final DecimalFormat decimalFormat6 = new DecimalFormat("000000");
	private static final DecimalFormat decimalFormat4 = new DecimalFormat("0000");
	private static final DecimalFormat decimalFormat2 = new DecimalFormat("00");
	
	private static final SimpleDateFormat SDF_FECHA_INICIO_NE = new SimpleDateFormat("yyyy-MM-dd"); 
	
	private boolean bandejaCambioSituacion;
	
	private Date fechaCambio;
	private Date fechaInicio;
	private Date fechaEnvio;
	private Date fechaRecepcion;
	
	private String escritoMasivoDescripcion;
	
	// Cambio de situacion fields
	private String cambioSituacion;
	
	// Tipo Reserva fields
	private Integer tipoReserva;
	
	
	// Cambio de estado expediente fields
	private EstadoExpediente cambioEstadoExpediente;
	private EstadoExpediente movimiento;
	private EventoInfo eventoInfo;
	private String detalleCambioEstadoExpediente;
	private Date fechaCambioEstadoExpediente;
	private Document documentoActual;
	private String documentCacheKey;
	private boolean mostrarModalDatos;
	// Cambio Tipo Causa
	private TipoCausa cambioTipoCausa;
	// Cambio Tipo Proceso
	private TipoProceso cambioTipoProceso;
	// Cambio Tipo Incidente
	private TipoSubexpediente cambioTipoSubexpediente;
	private TipoSubexpediente cambioTipoCirculacion;
	
	private IntervinienteExp lastIntervinienteAdded;
	private boolean repararSituacion;
	private boolean repararReserva;
	
	private boolean cambioSituacionDone = false;
	
	private Map<Integer, Boolean> cedulasPorIncoporarMap = new HashMap<Integer, Boolean>();
	private Map<Integer, Boolean> documentosEnBandejaMap = new HashMap<Integer, Boolean>();
	private Map<Integer, Boolean> informacionAdicionalMap = new HashMap<Integer, Boolean>();
	private Map<Integer, Boolean> pruebasMap = new HashMap<Integer, Boolean>();
	private Map<Integer, Boolean> alarmasMap = new HashMap<Integer, Boolean>();
	private Map<Integer, InstructorFacade> instructorMap = new HashMap<Integer, InstructorFacade>();
	/** ATOS - OM */
	private Map<Integer, Boolean> cuestionarioOMMap = new HashMap<Integer, Boolean>();
	/** ATOS - OM */

	private ExpedienteHome expedienteHome;
	
	private String legajo;
	private String caratula;
	private String comentarios;
	private String participante;
	private Oficina oficinaElevacion;
	
	
	//Cambio Denuncia
	private String declaracionHecho;
	private boolean denunciaFirmada;
	
	//Asignacion Instructor
	PersonalOficina instructor;
	
	private Oficina cambioSecretaria;
	
	//Cacheo de instructores
	private Map<String, InstructorFacade> instructores = new HashMap<String, InstructorFacade>();
	
	private List<IntervinienteExp> intervinientesReordenarList = new ArrayList();
	
	private String detalleRenovarCaratula; //ATOS COMERCIAL


	//Actuaciones en Eventos 
	private ActuacionExp ultimaActuacionEvento;
	
	//alerta pruebas
	private final String HAY = "Hay ";
	private final String PRUEBAS = " pruebas, ";
	private final String SIN_FINALIZAR = " sin finalizar";
	private Long totalPruebas;
	private Long pruebasAbiertas;
	
	private OmRespuestasHome omRespuestasHome = new OmRespuestasHome();
	
	// Voto masivo
	private ResolucionRecursoExp votoMasivo = new ResolucionRecursoExp();
	
	// Cierre circulacion
	private String estadoCerrarCirculacion;
	private DocumentoExp despachoCierreCirculacion;
	private OficinaViolenciaDomesticaExp oficinaViolenciaDomesticaExp;
	
	// Corrupcion
	private FormularioCorrupcionExp formularioCorrupcionExp;
	private boolean mostrarFormularioCorrupcion;
	
/* Atención NO PONER inyecciones, se pasa a BYPASS Interceptors, 
 * utilizar los componentes invocando al método get correspondiente
 */

	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	/** ATOS - SUBASTA */
	private Map<Integer, Boolean> turnosSubastalMap = new HashMap<Integer, Boolean>();
	/** ATOS - SUBASTA */
	
	public static ExpedienteManager instance() {
		return (ExpedienteManager) Component.getInstance(ExpedienteManager.class, true);
	}

	public String getLegajo() {
		return legajo;
	}


	public void setLegajo(String legajo) {
		this.legajo = legajo;
	}

	static {
		EXPEDIENTE_DEFINITION = new DocumentDefinition(EXPEDIENTE_NAME);
		EXPEDIENTE_DEFINITION.addIntegerProperty(ENTITY_ID_PROPERTY);
		EXPEDIENTE_DEFINITION.addIntegerProperty(ANIO_PROPERTY);
		EXPEDIENTE_DEFINITION.addIntegerProperty(NUM_SUBEXPEDIENTE_PROPERTY);
		EXPEDIENTE_DEFINITION.addStringProperty(CLAVE_PROPERTY);
		EXPEDIENTE_DEFINITION.addTextProperty(CARATULA_PROPERTY);
		EXPEDIENTE_DEFINITION.addStringProperty(SITUACION_PROPERTY);
		EXPEDIENTE_DEFINITION.addDateProperty(FECHA_INICIO_PROPERTY);
		EXPEDIENTE_DEFINITION.addDateProperty(FECHA_SITUACION_PROPERTY);
		EXPEDIENTE_DEFINITION.addDateProperty(FECHA_PASE_PROPERTY);
		EXPEDIENTE_DEFINITION.addDateProperty(FECHA_PRESTAMO_PROPERTY);
		EXPEDIENTE_DEFINITION.addDateProperty(FECHA_ESTADO_PROPERTY);
		EXPEDIENTE_DEFINITION.addDateProperty(FECHA_ARCHIVO_PROPERTY);
		EXPEDIENTE_DEFINITION.addDateProperty(FECHA_SORTEO_CARGA_PROPERTY);
		EXPEDIENTE_DEFINITION.addTextProperty(COMENTARIOS_PROPERTY);
		EXPEDIENTE_DEFINITION.addBooleanProperty(PRIVADO_PROPERTY);
		EXPEDIENTE_DEFINITION.addBooleanProperty(QUIEBRA_PROPERTY);
		EXPEDIENTE_DEFINITION.addBooleanProperty(RESERVADO_PROPERTY);
		EXPEDIENTE_DEFINITION.addStringProperty(LEGAJO_ARCHIVO_PROPERTY);
		EXPEDIENTE_DEFINITION.addStringProperty(LEGAJO_PARALIZADO_PROPERTY);
		EXPEDIENTE_DEFINITION.addStringProperty(ESTADO_PROPERTY);
		EXPEDIENTE_DEFINITION.addIntegerProperty(ID_EXPEDIENTE_ORIGEN_PROPERTY);
		EXPEDIENTE_DEFINITION.addIntegerProperty(ID_CAMARA_PROPERTY);
		EXPEDIENTE_DEFINITION.addIntegerProperty(ID_MONEDA_PROPERTY);
		EXPEDIENTE_DEFINITION.addIntegerProperty(ID_OFICINA_ACTUAL_PROPERTY);
		EXPEDIENTE_DEFINITION.addIntegerProperty(ID_OFICINA_PASE_PROPERTY);
		EXPEDIENTE_DEFINITION.addIntegerProperty(ID_OFICINA_RETORNO_PROPERTY);
		EXPEDIENTE_DEFINITION.addIntegerProperty(ID_OFICINA_RETORNO_PROPERTY);
		EXPEDIENTE_DEFINITION.addTextProperty(LOCALIDAD_INTERVINIENTE_EXP_PROPERTY, true);
		EXPEDIENTE_DEFINITION.addTextProperty(DOMICILIO_INTERVINIENTE_EXP_PROPERTY, true);
		EXPEDIENTE_DEFINITION.addTextProperty(NOMBRE_INTERVINIENTE_INTERVINIENTE_EXP_PROPERTY, true);
		EXPEDIENTE_DEFINITION.addTextProperty(LOCALIDAD_INTERVINIENTE_INTERVINIENTE_EXP_PROPERTY, true);
		EXPEDIENTE_DEFINITION.addTextProperty(DOMICILIO_INTERVINIENTE_INTERVINIENTE_EXP_PROPERTY, true);
		EXPEDIENTE_DEFINITION.addTextProperty(NOMBRE_LETRADO_INTERVINIENTE_EXP_PROPERTY, true);
		EXPEDIENTE_DEFINITION.addTextProperty(LOCALIDAD_LETRADO_INTERVINIENTE_EXP_PROPERTY, true);
		EXPEDIENTE_DEFINITION.addTextProperty(DOMICILIO_LETRADO_INTERVINIENTE_EXP_PROPERTY, true);
		EXPEDIENTE_DEFINITION.setRestrictPropertiesToModel(false);
	}

	public static DocumentMetadata getDocumentMetadata(Expediente expediente) {
		DocumentMetadata documentMetadata = new DocumentMetadata(EXPEDIENTE_DEFINITION);
		documentMetadata.addProperty(ENTITY_NAME_PROPERTY, "DocumentoExp");
		documentMetadata.addProperty(ENTITY_ID_PROPERTY, expediente.getId());
		documentMetadata.addProperty(CLAVE_PROPERTY, expediente.getClave());
		documentMetadata.addProperty(NUM_EXPEDIENTE_PROPERTY, expediente.getNumero());
		documentMetadata.addProperty(ANIO_PROPERTY, expediente.getAnio());
		documentMetadata.addProperty(NUM_SUBEXPEDIENTE_PROPERTY, expediente.getCodigoSubexpediente());
		documentMetadata.addProperty(CARATULA_PROPERTY, expediente.getCaratula());
		documentMetadata.addProperty(SITUACION_PROPERTY, expediente.getSituacion());
		documentMetadata.addProperty(FECHA_ARCHIVO_PROPERTY, expediente.getFechaArchivo());
		documentMetadata.addProperty(FECHA_ESTADO_PROPERTY, expediente.getFechaEstado());
		documentMetadata.addProperty(FECHA_PASE_PROPERTY, expediente.getFechaPase());
		documentMetadata.addProperty(FECHA_INICIO_PROPERTY, expediente.getFechaInicio());
		documentMetadata.addProperty(FECHA_SITUACION_PROPERTY, expediente.getFechaSituacion());
		documentMetadata.addProperty(FECHA_PRESTAMO_PROPERTY, expediente.getFechaPrestamo());
		documentMetadata.addProperty(FECHA_SORTEO_CARGA_PROPERTY, expediente.getFechaSorteoCarga());
		documentMetadata.addProperty(COMENTARIOS_PROPERTY, expediente.getComentarios());
		documentMetadata.addProperty(QUIEBRA_PROPERTY, expediente.isQuiebra());
		documentMetadata.addProperty(RESERVADO_PROPERTY, expediente.getReservado());
		documentMetadata.addProperty(LEGAJO_ARCHIVO_PROPERTY, expediente.getLegajoArchivo());
		documentMetadata.addProperty(LEGAJO_PARALIZADO_PROPERTY, expediente.getLegajoParalizado());
		if (expediente.getEstadoExpediente() != null) {
			documentMetadata.addProperty(ESTADO_PROPERTY, expediente.getEstadoExpediente().getCodigo());
		}
		if (expediente.getExpedienteOrigen() != null) {
			documentMetadata.addProperty(ID_EXPEDIENTE_ORIGEN_PROPERTY, expediente.getExpedienteOrigen().getId());
		}
		if (expediente.getOficinaActual().getCamara() != null) {
			documentMetadata.addProperty(ID_CAMARA_PROPERTY, expediente.getOficinaActual().getCamara().getId());
		}
		if (expediente.getMoneda() != null) {
			documentMetadata.addProperty(ID_MONEDA_PROPERTY, expediente.getMoneda().getId());
		}
		if (expediente.getOficinaActual() != null) {
			documentMetadata.addProperty(ID_OFICINA_ACTUAL_PROPERTY, expediente.getOficinaActual().getId());
		}
		if (expediente.getOficinaPase() != null) {
			documentMetadata.addProperty(ID_OFICINA_PASE_PROPERTY, expediente.getOficinaPase().getId());
		}
		if (expediente.getOficinaRetorno() != null) {
			documentMetadata.addProperty(ID_OFICINA_RETORNO_PROPERTY, expediente.getOficinaRetorno().getId());
		}
		List<String> multipleDomicilioIntervinienteExpList = new ArrayList<String>();
		List<String> multipleLocalidadIntervinienteExpList = new ArrayList<String>();
		List<String> multipleNombreIntervinienteIntervinienteExpList = new ArrayList<String>();
		List<String> multipleDomicilioIntervinienteIntervinienteExpList = new ArrayList<String>();
		List<String> multipleLocalidadIntervinienteIntervinienteExpList = new ArrayList<String>();
		List<String> multipleNombreLetradoIntervinienteExpList = new ArrayList<String>();
		List<String> multipleDomicilioLetradoIntervinienteExpList = new ArrayList<String>();
		List<String> multipleLocalidadLetradoIntervinienteExpList = new ArrayList<String>();
		for (IntervinienteExp intervinienteExp: expediente.getIntervinienteExpList()) {
			if (!intervinienteExp.isLogicalDeleted()) {
			multipleDomicilioIntervinienteExpList.add(intervinienteExp.getDomicilio());
			multipleLocalidadIntervinienteExpList.add(intervinienteExp.getLocalidad());
			if (intervinienteExp.getInterviniente() != null) {
				multipleNombreIntervinienteIntervinienteExpList.add(intervinienteExp.getInterviniente().getNombre());
				multipleDomicilioIntervinienteIntervinienteExpList.add(intervinienteExp.getInterviniente().getDomicilio());
				multipleLocalidadIntervinienteIntervinienteExpList.add(intervinienteExp.getInterviniente().getLocalidad());
			}
			for (LetradoIntervinienteExp letradoIntervinienteExp: intervinienteExp.getLetradoIntervinienteExpList()) {
				multipleNombreLetradoIntervinienteExpList.add(letradoIntervinienteExp.getLetrado().getNombre());
				multipleDomicilioLetradoIntervinienteExpList.add(letradoIntervinienteExp.getDomicilio());
				multipleLocalidadLetradoIntervinienteExpList.add(letradoIntervinienteExp.getLocalidad());
			}
		}
		}
		documentMetadata.addProperty(NOMBRE_INTERVINIENTE_INTERVINIENTE_EXP_PROPERTY, multipleNombreIntervinienteIntervinienteExpList.toArray());
		documentMetadata.addProperty(DOMICILIO_INTERVINIENTE_INTERVINIENTE_EXP_PROPERTY, multipleDomicilioIntervinienteIntervinienteExpList.toArray());
		documentMetadata.addProperty(LOCALIDAD_INTERVINIENTE_INTERVINIENTE_EXP_PROPERTY, multipleLocalidadIntervinienteIntervinienteExpList.toArray());
		documentMetadata.addProperty(DOMICILIO_INTERVINIENTE_EXP_PROPERTY, multipleDomicilioIntervinienteExpList.toArray());
		documentMetadata.addProperty(LOCALIDAD_INTERVINIENTE_EXP_PROPERTY, multipleLocalidadIntervinienteExpList.toArray());
		documentMetadata.addProperty(NOMBRE_LETRADO_INTERVINIENTE_EXP_PROPERTY, multipleNombreLetradoIntervinienteExpList.toArray());
		documentMetadata.addProperty(DOMICILIO_LETRADO_INTERVINIENTE_EXP_PROPERTY, multipleDomicilioLetradoIntervinienteExpList.toArray());
		documentMetadata.addProperty(LOCALIDAD_LETRADO_INTERVINIENTE_EXP_PROPERTY, multipleLocalidadLetradoIntervinienteExpList.toArray());
		return documentMetadata;
	}

	public boolean hasEstadoExpediente(Expediente expediente, EstadoExpediente estadoExpediente){
		if (estadoExpediente.getTipo() == null && expediente.getEstadoExpediente() != null && expediente.getEstadoExpediente().getCodigo().equals(estadoExpediente.getCodigo())) {
			return true;
		}else{
			return false;
		}
		
	}
	public boolean cambiarEstadoExpediente(Expediente expediente, DocumentoExp documento, EstadoExpediente estadoExpediente, String detalle, boolean doUpdate){
		boolean ret = true;
		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
		getExpedienteHome().setId(expediente.getId());
		if (estadoExpediente.getTipo() == null && expediente.getEstadoExpediente() != null && expediente.getEstadoExpediente().getCodigo().equals(estadoExpediente.getCodigo())) {
			return ret;
		}
		if ((expediente.getEstadoExpediente() == null || expediente.getEstadoExpediente().getCodigo().equals(ESTADO_SIN_INICIAR_CODIGO)) && estadoExpediente.getCodigo().equals(EstadoExpedientePredefinedEnumeration.Type.D01.getValue())) {
			expediente.setFechaInicio(actuacionExpManager.getFechaActuacion());
		}
		
		// solo se actualiza la cabecera del expediente si es un estado procesal
		if (estadoExpediente.isGeneraEstadoProcesal()) {
			this.cambiarCabeceraEstadoExpediente(expediente, estadoExpediente, actuacionExpManager.getFechaActuacion());
		}
		
//		if(detalle != null){ // FIXME: revisar si el campo detalle se queda almacenado en el campo "localizacion" o crear un campo "detalle" en expediente....
//			expediente.setLocalizacion(detalle.length() > 200 ? detalle.substring(0, 200) : detalle);
//		}
		if (estadoExpediente.getTipoFinalizacion() != null) {
			if (TipoFinalizacionEnumeration.Type.finalizacion_expediente.getValue().equals(estadoExpediente.getTipoFinalizacion())) {
				expediente.setEnTramite(false);
			} else if (TipoFinalizacionEnumeration.Type.reapertura_expediente.getValue().equals(estadoExpediente.getTipoFinalizacion())) {
				expediente.setEnTramite(true);
			}
		}
		if(doUpdate){
			ret = updateSelectedExpediente(ret);
		}
		if(ret){
			actuacionExpManager.addActuacionExpediente(expediente, documento, estadoExpediente, detalle, estadoExpediente.isGeneraMovimiento());
			if(estadoExpediente.getSituacionAsociada() != null){
				cambiarSituacion(expediente, documento, estadoExpediente.getSituacionAsociada(), false);
			}
			
			if(estadoExpediente.isCopiaEnPrincipal() && isCirculacion(expediente) && expediente.getExpedienteOrigen() != null){
				actuacionExpManager.addActuacionExpediente(expediente.getExpedienteOrigen(), null, estadoExpediente, detalle, estadoExpediente.isGeneraMovimiento());				
			}
			if(doUpdate){
				getEntityManager().flush();
			}
		}
		return ret;
	}
	
	public boolean addEventoExpediente(Expediente expediente, DocumentoExp documento, EstadoExpediente evento, IntervinienteExp intervinienteExp, String comentarios){
		boolean ret = true;
		if (intervinienteExp != null) {
			intervinienteExp.setEstadoExpediente(evento);
		}
		
		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
		this.ultimaActuacionEvento = actuacionExpManager.addActuacionEventoExpediente(expediente, documento, evento, intervinienteExp, comentarios);
		
		// si el evento genera estado procesal se guarda en la cabecera del expediente
		if (evento.isGeneraEstadoProcesal()) {
			this.cambiarCabeceraEstadoExpediente(expediente, evento, actuacionExpManager.getFechaActuacion());
		}
		
		if(evento.isCopiaEnPrincipal() && expediente.getExpedienteOrigen() != null){
			actuacionExpManager.addActuacionEventoExpediente(expediente.getExpedienteOrigen(), null, evento, null, comentarios);
		}

		return ret;
	}
	
	/**
	 * Realiza el cambio del Estado del Expediente y la fecha en un determinado expediente 
	 * @param expediente {@link Expediente} a cargar estado y fecha
	 * @param estadoExpediente {@link EstadoExpediente} a incorporar en el expediente
	 * @param fecha fecha del cambio de estado
	 */
	private void cambiarCabeceraEstadoExpediente(Expediente expediente, EstadoExpediente estadoExpediente, Date fecha) {
		expediente.setFechaEstado(fecha);
		expediente.setEstadoExpediente(estadoExpediente);
		getEntityManager().persist(expediente);
		getEntityManager().flush();
	}

	public boolean addEstadoExpedienteInterviniente(Expediente expediente, DocumentoExp documento, EstadoExpediente estado, IntervinienteExp intervinienteExp, String comentarios){
		boolean ret = true;
		intervinienteExp.setEstadoExpediente(estado);
		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
		actuacionExpManager.addActuacionEstadoExpedienteInterv(expediente, documento, estado, intervinienteExp, comentarios);
		return ret;
	}
	
	public boolean addActuacionLIP(Expediente expedienteLIP, DocumentoExp documento, EstadoExpediente estado, IntervinienteExp intervinienteExp, String comentarios, TipoActuacionEnumeration.Type tipoActuacion){
		boolean ret = true;
		intervinienteExp.setEstadoExpediente(estado);
		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
		actuacionExpManager.addActuacionLIP(expedienteLIP, documento, estado, intervinienteExp, comentarios, tipoActuacion);
		return ret;
	}
	
	private boolean updateSelectedExpediente(boolean ret) {
		ValueExpression<?> updatedMessage = getExpedienteHome().getUpdatedMessage();
		getExpedienteHome().setUpdatedMessage(Expressions.instance().createValueExpression(""));
		if(getExpedienteHome().update() == null){
			ret = false;
		}
		getExpedienteHome().setUpdatedMessage(updatedMessage);
		return ret;
	}
	
	public boolean cambiarSituacion(Expediente expediente, DocumentoExp documentoExp, String situacion, boolean doUpdate){
		boolean ret = situacion != null;
		getExpedienteHome().setId(expediente.getId());
		if(situacion != null){
			// Los expedientes penales nunca marcan cambio de situación
			// Los laborales tampoco. Antonio 31/05/2014
			// Laboral ahora si marca cambio de situación. Mariano 22/04/2015
			if ((isPenal(expediente)) /*|| isLaboral(expediente)*/ && SituacionExpedienteEnumeration.Type.en_Letra.getValue().equals(situacion)) {
				return true;
			}
			if (expediente.getSituacion() != null && expediente.getSituacion().equals(situacion)) {
				setCambioSituacionDone(true);
				return ret;
			}
			expediente.setSituacion(situacion);
			String detalle = null;
			
			ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
			actuacionExpManager.addActuacionSituacionExpediente(expediente, documentoExp, situacion, detalle);
			if(situacion.equals(SituacionExpedienteEnumeration.Type.archivo.getValue())){
				detalle = expediente.getLegajoArchivo();
				expediente.setEnTramite(false);
			}else if(situacion.equals(SituacionExpedienteEnumeration.Type.archivese.getValue())){
				expediente.setEnTramite(false);
			}else if(situacion.equals(SituacionExpedienteEnumeration.Type.confronte.getValue())){
				;
			}else if(situacion.equals(SituacionExpedienteEnumeration.Type.despacho.getValue())){
				;
			}else if(situacion.equals(SituacionExpedienteEnumeration.Type.giro.getValue())){
				expediente.setFechaPase(actuacionExpManager.getFechaActuacion());
			}else if(situacion.equals(SituacionExpedienteEnumeration.Type.prestamo.getValue())){
				expediente.setFechaPrestamo(actuacionExpManager.getFechaActuacion());
			}else if(situacion.equals(SituacionExpedienteEnumeration.Type.sentencia.getValue())){
				;
			}else if(situacion.equals(SituacionExpedienteEnumeration.Type.vista.getValue())){
				expediente.setFechaPase(actuacionExpManager.getFechaActuacion());
			}else if(situacion.equals(SituacionExpedienteEnumeration.Type.paralizado.getValue())){
				expediente.setEnTramite(false);
			}
			expediente.setFechaSituacion(actuacionExpManager.getFechaActuacion());
			if(ret){
				if(doUpdate){
					ret = updateSelectedExpediente(ret);
				}
			}
			if(ret){
				setCambioSituacionDone(true);
			}
		} else {
			if (!isIniciado(expediente)) {
				expediente.setSituacion(null);
				ret = true;
			}
		}
		return ret;
	}
	
	public boolean changeSituacion(Expediente expediente, DocumentoExp documento, SituacionExpedienteEnumeration.Type situacion, boolean doUpdate){
		return cambiarSituacion(expediente, documento, situacion.getValue().toString(), doUpdate);		
	}
	

	public void actualizarIndexacion(Expediente entity, boolean async) {
		String documentName = getDocumentName();
		String documentId = getDocumentId(entity);
		DocumentMetadata documentMetadata = ExpedienteManager.getDocumentMetadata(entity);
		if (!entity.isLogicalDeleted()) {
			getIndexManager().indexarDocumento(documentName, documentId, documentMetadata, async);
		} else {
			getIndexManager().eliminarIndexacionDocumento(documentId, async);
		}
	}
	
	private String getDocumentName() {
		return getExpedienteHome().getEntityName();
	}
	
	public String getDocumentId(Expediente entity) {
		return getDocumentId(entity.getId());
	}
	
	public String getDocumentId(Integer id) {
		return getDocumentName()+":"+id.toString();
	}
	
	public static Oficina getOficinaCodigoTipo(Oficina oficina, Integer codigoTipoOficina) {
		Oficina result = oficina;
		while (result.getOficinaSuperior() !=  null && result.getTipoOficina().getId() != codigoTipoOficina) {
			result = result.getOficinaSuperior();
		}
		return result;
	}
	
	public String getExpedienteIdxAnalitico(){
		return getIdxAnalitico(getExpedienteHome().getInstance());
	}

	public String getExpedienteIdxAnaliticoFirst(){
		return getIdxAnaliticoFirst(getExpedienteHome().getInstance());
	}
	
	public String getExpedienteIdxAnaliticoSecond(){
		return getIdxAnaliticoSecond(getExpedienteHome().getInstance());
	}

	public void iniciarExpediente(Expediente expediente, Date fechaInicio) {
		if(!isIniciado(expediente)){
			if (fechaInicio == null) {
				fechaInicio = new Date();
			}
			setFechaInicio(fechaInicio);
			getExpedienteHome().setId(expediente.getId());
			doIniciar();
		}
	}

	public String doIniciar() {
		Expediente expediente = getExpedienteHome().getInstance();
		if(!isIniciado(expediente)){
			try {
				Date ahora = new Date();
				if(fechaInicio == null || fechaInicio.after(ahora)){
					String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_fecha_iniciar_expediente");
					getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);					
				}else{
					// Verifico que la fecha no sea mas antigua de 100 años.
					GregorianCalendar gc = new GregorianCalendar();
					gc.setTime(ahora);
					gc.add(GregorianCalendar.YEAR, -100);
					if(fechaInicio.before(gc.getTime())){
						String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_fecha_iniciar_expedienteAntigua");
						getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);					
					} else {
						//Verifico que la fecha de inicio no sea anterior a la fecha de sorteo
						if ((expediente.getFechaSorteoCarga() != null) &&expediente.getFechaSorteoCarga().after(fechaInicio)) {
							String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_fecha_iniciar_expedienteSorteo");
							getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);					
							
						} else {
							getActuacionExpManager().setFechaActuacionVirtual(fechaInicio);
							if (isPenal(expediente)) {
								new AccionCambioEstadoExpediente().doAccion(expediente, EstadoExpedientePredefinedEnumeration.Type.$IN.getValue().toString(), null, true);
								expediente.setSituacion((String) SituacionExpedienteEnumeration.Type.despacho.getValue());
							} else {
								new AccionCambioEstadoExpediente().doAccion(expediente, EstadoExpedientePredefinedEnumeration.Type.D01.getValue().toString(), null, true);
								new AccionCambioSituacionExpediente().doAccion(expediente, SituacionExpedienteEnumeration.Type.despacho.getValue().toString(), true);
							}
							if (isSeguridadSocial(expediente)) {
								inicioTramiteJuzgado(expediente);
							}
							expediente.setFechaInicio(fechaInicio);
							getEntityManager().flush();
							if(expediente.isIngresoWeb()) {
								// genera pase a la oficina de radicacion
								OficinaAsignacionExp oficinaAsignacionExp = expediente.getRadicacionJuzgado();
								Oficina oficinaDestino = oficinaAsignacionExp != null ? oficinaAsignacionExp.getOficinaConSecretaria(): null;
								if ((oficinaDestino != null) && !oficinaDestino.equals(expediente.getOficinaActual()) && OficinaManager.instance().isMesaEntradaCamaraOficinaActual()) {
									Oficina oficinaActual = OficinaManager.instance().getOficinaActual();
									new AccionPase().doAccion(expediente, null, getFojas(expediente, null, null), getPaquetes(expediente, null, null), getCuerpos(expediente, null, null), getAgregados(expediente, null, null), false, 
											(String)TipoPaseEnumeration.Type.giro.getValue(), MotivoPaseEnumeration.instance().getMotivoPaseAsignacionWEB(), getComentarioAgregados(expediente, null, null), oficinaDestino, null, fechaInicio,
											ConfiguracionMateriaManager.instance().getTipoGiroAsignacion(TipoRadicacionEnumeration.Type.juzgado, expediente.getCamara(), expediente.getMateria()),
											oficinaActual, null);
								}

							}
						}
					}
				}
			} catch (AccionException e) {
				String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_iniciar_expediente");
				getLog().error(msg, e);
				getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			} finally {
				getActuacionExpManager().setFechaActuacionVirtual(null);
			}
		}
		
		return "";
	}

	private void inicioTramiteJuzgado(Expediente expediente) {
		String comentarios =  "Iniciado en " + CamaraManager.getOficinaTitle(CamaraManager.getOficinaPrincipal(SessionState.instance().getGlobalOficina()), true);
		EstadoExpediente evento = EstadoExpedienteSearch.findByCodigo(getEntityManager(), EstadoExpedientePredefinedEnumeration.Type.INI.getValue().toString(), expediente.getMateria());
		if (evento != null) {
			if (evento.isGeneraEstadoProcesal()) {
				cambiarEstadoExpediente(expediente, null, evento, comentarios, true);
			} else {
				addEventoExpediente(expediente, null, evento, null, comentarios);
			}
		}
//	Con Actuacion de tipo nformacion en lugar de Evento 		
//		String descripcion =  "Iniciado en " + CamaraManager.getOficinaTitle(CamaraManager.getOficinaPrincipal(SessionState.instance().getGlobalOficina()), true);
//		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
//		actuacionExpManager.addInformacionExpediente(expediente, null, TipoInformacionEnumeration.getItemByCodigo(getEntityManager(), TipoInformacionEnumeration.INICIO_TRAMITE_JUZGADO, expediente.getMateria()), 
//				descripcion, null, null, SessionState.instance().getUsername(), null);
		
	}

	public String doRecepcionar() {
		Expediente expediente = getExpedienteHome().getInstance();
		if(!isRecepcionado(expediente)){
			try {
				Date ahora = new Date();
				if(fechaRecepcion == null || fechaRecepcion.after(ahora)){
					String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_fecha_recepcionar_expediente");
					getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);					
				}else{
					//Verifico que la fecha de recepcion no sea anterior a la fecha de sorteo
					if ((expediente.getFechaSorteoCarga() != null) &&expediente.getFechaSorteoCarga().after(fechaRecepcion)) {
						String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_fecha_recepcionar_expedienteSorteo");
						getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);					
						
					} else {
						getActuacionExpManager().setFechaActuacionVirtual(fechaRecepcion);
						expediente.setEstadoExpediente(getEstadoSinIniciar(expediente.getMateria()));

						ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
						actuacionExpManager.addInformacionExpediente(expediente, null, TipoInformacionEnumeration.getItemByCodigo(getEntityManager(), TipoInformacionEnumeration.RECIBIDO_MESA_RECEPTORA), "Recibido en " + SessionState.instance().getGlobalOficina().getNombre(), null, null, SessionState.instance().getUsername(), null);
						
						if (isPlazoInicioVencido(expediente)) {
							new AccionRehabilitacionPlazoVencido().doAccion(expediente);
						}
						if(expediente.isIngresoWeb()) {
							// genera pase a la oficina de radicacion
							OficinaAsignacionExp oficinaAsignacionExp = expediente.getRadicacionJuzgado();
							Oficina oficinaDestino = oficinaAsignacionExp != null ? oficinaAsignacionExp.getOficinaConSecretaria(): null;
							if ((oficinaDestino != null) && !oficinaDestino.equals(expediente.getOficinaActual()) && OficinaManager.instance().isMesaEntradaCamaraOficinaActual()) {
								Oficina oficinaActual = OficinaManager.instance().getOficinaActual();
								new AccionPase().doAccion(expediente, null, getFojas(expediente, null, null), getPaquetes(expediente, null, null), getCuerpos(expediente, null, null), getAgregados(expediente, null, null), false, 
										(String)TipoPaseEnumeration.Type.giro.getValue(), MotivoPaseEnumeration.instance().getMotivoPaseAsignacionWEB(), getComentarioAgregados(expediente, null, null), oficinaDestino, null, ahora,
										ConfiguracionMateriaManager.instance().getTipoGiroAsignacion(TipoRadicacionEnumeration.Type.juzgado, expediente.getCamara(), expediente.getMateria()),
										oficinaActual, null);
							}

						}
					}
				}
			} catch (AccionException e) {
				String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_recepcionar_expediente");
				getLog().error(msg, e);
				getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			} finally {
				getActuacionExpManager().setFechaActuacionVirtual(null);
			}
		}
		
		return "";
	}

	public String doAsignarmeExpediente(Expediente expediente) {
		String textoCambio = "";
		
		expediente.setResponsable(getSessionState().getUsername());
		textoCambio = expediente.getResponsable();
		getExpedienteHome().doUpdate();

		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
		actuacionExpManager.addInformacionExpediente(expediente, null, TipoInformacionEnumeration.getItemByCodigo(getEntityManager(), TipoInformacionEnumeration.COMENTARIO), "Se asignó el responsable: " + textoCambio, null, null, SessionState.instance().getUsername(), "Se asignó el responsable: " + textoCambio);
		return "";
	}

	private boolean puedeAsignarResponsable(Expediente expediente) {
		if (isOficinaActual(expediente)) {
			TipoAsignacionResponsableEnumeration.Type tipoAsignacionResponsable = CamaraManager.instance().getTipoAsignacionResponsable(expediente);
			switch (tipoAsignacionResponsable) {
				case auto:
					return isDespacho(expediente) && !isResponsable(expediente);
				case responsableUnico:
					return isDespacho(expediente) && !hasResponsable(expediente);
				case responsablePorOficina:
					return isDespacho(expediente);
				case instructor:
					return isDespacho(expediente) && isPenal(expediente);
			}
		}
		return false;
	}
	
	private boolean puedeDesasignarResponsable(Expediente expediente) {
		if (isOficinaActual(expediente)) {
			TipoAsignacionResponsableEnumeration.Type tipoAsignacionResponsable = CamaraManager.instance().getTipoAsignacionResponsable(expediente);
			switch (tipoAsignacionResponsable) {
				case auto:
					return isDespacho(expediente) && isResponsable(expediente);
				case responsableUnico:
					return isDespacho(expediente) && hasResponsable(expediente);
				case responsablePorOficina:
					return false;
				case instructor:
					return false;
			}
		}
		return false;
	}
	
	public String doAsignarResponsable() {
		return doAsignarResponsable(getExpedienteHome().getInstance());
	}
	
	public String doAsignarResponsable(Expediente expediente) {
		TipoAsignacionResponsableEnumeration.Type tipoAsignacionResponsable = CamaraManager.instance().getTipoAsignacionResponsable(expediente);
		switch (tipoAsignacionResponsable) {
			case auto:
				return doAsignarmeExpediente(expediente);
			case responsableUnico:
			case responsablePorOficina:
				return doAsignarResponsableExpediente(expediente);
			case instructor:
				return doAsignarInstructor(expediente);
			default:
				break;
		}
		return "";
	}

	public String doDesasignarResponsable() {
		desasignarResponsable(getExpedienteHome().getInstance());
		return "";
	}

	public void desasignarResponsable(Expediente expediente) {
		expediente.setResponsable(null);
		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
		actuacionExpManager.addInformacionExpediente(expediente, null, TipoInformacionEnumeration.getItemByCodigo(getEntityManager(), TipoInformacionEnumeration.COMENTARIO), "Se desasigna el responsable del expediente", null, null, SessionState.instance().getUsername(), "Desasignar responsable del expediente");
		
		getEntityManager().flush();
	}

	public String doAsignarResponsableExpediente(Expediente expediente) {
		getExpedienteHome().setResponsable(getResponsableExpediente(expediente));
		getExpedienteHome().setExpedienteSeleccionado(expediente);
		getExpedienteHome().setVisibleDialog("asignarResponsableExpediente", true);
		return "";
	}
	
	public void asignarResponsableExpediente() {
		asignarResponsableExpediente(getExpedienteHome().getExpedienteSeleccionado(), getExpedienteHome().getResponsable());
		getExpedienteHome().setVisibleDialog("asignarResponsableExpediente", false);
	}
	
	public void asignarResponsableExpediente(Expediente expediente, PersonalOficina responsable) {
		if(responsable != null){
			String codigoTipoInformacion = TipoInformacionEnumeration.COMENTARIO;
			String textoCambio = "";
			PersonalOficinaEnumeration personalOficinaEnumeration = (PersonalOficinaEnumeration) Component.getInstance(PersonalOficinaEnumeration.class, true);
			
			TipoAsignacionResponsableEnumeration.Type tipoAsignacionResponsable = CamaraManager.instance().getTipoAsignacionResponsable(expediente);
			if(TipoAsignacionResponsableEnumeration.Type.responsablePorOficina.equals(tipoAsignacionResponsable)) {
				Oficina oficina = SessionState.instance().getGlobalOficina();
				setResponsableOficina(expediente, responsable.getCuil(), oficina);
				textoCambio = oficina.getDescripcion() + ": " + personalOficinaEnumeration.labelCuilFor(responsable);
				codigoTipoInformacion = TipoInformacionEnumeration.ASIGNAR_PROYECTISTA;
			} else {
				expediente.setResponsable(responsable.getCuil());
				textoCambio = "Se asignó el responsable: " + personalOficinaEnumeration.labelCuilFor(responsable);
			}
			ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
			actuacionExpManager.addInformacionExpediente(expediente, null, TipoInformacionEnumeration.getItemByCodigo(getEntityManager(), codigoTipoInformacion), textoCambio, null, null, SessionState.instance().getUsername(), textoCambio);

			getEntityManager().flush();
		}
	}
	
	public void setResponsableOficina(Expediente expediente, String responsable, Oficina oficina) {
		ResponsableExp responsableExp = ResponsableExpSearch.findByOficina(getEntityManager(), expediente, oficina);
		if (responsableExp != null) {
			responsableExp.setResponsable(responsable);
			responsableExp.setFecha(CurrentDateManager.instance().getCurrentDate());
		} else {
			responsableExp = new ResponsableExp();
			responsableExp.setExpediente(expediente);
			responsableExp.setOficina(SessionState.instance().getGlobalOficina());

			responsableExp.setResponsable(responsable);
			responsableExp.setFecha(CurrentDateManager.instance().getCurrentDate());
			getEntityManager().persist(responsableExp);
		}
		getEntityManager().flush();
	}

	public PersonalOficina getResponsableExpediente(Expediente expediente) {
		if (expediente != null) {
			TipoAsignacionResponsableEnumeration.Type tipoAsignacionResponsable = CamaraManager.instance().getTipoAsignacionResponsable(expediente);
			if(TipoAsignacionResponsableEnumeration.Type.responsablePorOficina.equals(tipoAsignacionResponsable)) {
				ResponsableExp responsableExp = ResponsableExpSearch.findByOficina(getEntityManager(), expediente, SessionState.instance().getGlobalOficina());
				if (responsableExp != null) {
					if (responsableExp.getResponsable() != null) {
						return PersonalOficinaEnumeration.instance().getItemByCuil(responsableExp.getResponsable());
					}
				}
			} else {
				return getResponsable(expediente);
			}
		}
		return null;
	}

	// Iniciar Expediente
	public String doStartIniciar() {
		setFechaInicio(new Date());
		getExpedienteHome().setVisibleDialog("iniciar", true);
		return "";
	}
	
	// Recepcionar Expediente
	public String doStartRecepcionar() {
		setFechaRecepcion(new Date());
		getExpedienteHome().setVisibleDialog("recepcionar", true);
		return "";
	}
	
	public String doStartModificarDatos() {
		setCaratula(getExpedienteHome().getInstance().getCaratula());
		setComentarios(getExpedienteHome().getInstance().getComentarios());
		getExpedienteHome().setVisibleDialog("modificarDatos", true);
		return "";
	}

	// Cambio de estado expediente
	public String doCambioEstadoExpediente() {
		initCambioEstadoExpediente();
		getExpedienteHome().setVisibleDialog("cambioEstadoExpediente", true);
		return "";

	}

	public String doCambioTipoCausa() {
		initCambioTipoCausa();
		getExpedienteHome().setVisibleDialog("cambioTipoCausa", true);
		return "";
	}
	public String doCambioTipoProceso() {
		initCambioTipoProceso();
		getExpedienteHome().setVisibleDialog("cambioTipoProceso", true);
		return "";
	}
	public String doCambioTipoSubexpediente() {
		initCambioTipoSubexpediente();
		getExpedienteHome().setVisibleDialog("cambioTipoSubexpediente", true);
		return "";
	}
	public String doElevacionExpediente() {
		initElevacionExpediente();
		getExpedienteHome().setVisibleDialog("elevacionExpediente", true);
		return "";
	}
	
	public String doModificarDenunciaExpediente() {
		initModificarDenunciaExpediente();
		getExpedienteHome().setVisibleDialog("modificarDenuncia", true);
		return "";
	}
	

	private void initModificarDenunciaExpediente() {
		if(getExpedienteHome().getInstance().getExpedienteEspecial()==null){
			createExpedienteEspecial(getExpedienteHome().getInstance());
		}
		this.declaracionHecho = getExpedienteHome().getInstance().getExpedienteEspecial().getDeclaracionHecho();
		this.denunciaFirmada = getExpedienteHome().getInstance().getExpedienteEspecial().isDenunciaFirmada();
	}
	
	public void modificarDenunciaExpediente() {
		getExpedienteHome().getInstance().getExpedienteEspecial().setDeclaracionHecho(this.declaracionHecho);
		getExpedienteHome().getInstance().getExpedienteEspecial().setDenunciaFirmada(this.denunciaFirmada);
		getEntityManager().flush();		
		getExpedienteHome().setVisibleDialog("modificarDenuncia", false);
	}
	
	
	private void initElevacionExpediente() {
		this.oficinaElevacion = OficinaAsignacionExpManager.instance().getSalaApelacion(getExpedienteHome().getInstance());
	}

	private void initCambioEstadoExpediente() {
		this.cambioEstadoExpediente = null; //getExpedienteHome().getInstance().getEstadoExpediente(); -- para que se muestre el ultimo en el input
		this.detalleCambioEstadoExpediente = null;
	}

	private void initCambioTipoCausa() {
		this.cambioTipoCausa = getExpedienteHome().getInstance().getTipoCausa();
	}
	private void initCambioTipoProceso() {
		this.cambioTipoProceso = getExpedienteHome().getInstance().getTipoProceso();
	}
	private void initCambioTipoSubexpediente() {
		this.cambioTipoSubexpediente = getExpedienteHome().getInstance().getTipoSubexpediente();
		this.cambioTipoCirculacion = getExpedienteHome().getInstance().getTipoSubexpedienteExtendido();
	}
	// Cambio de estado expediente
	public String doMovimientoExpediente() {
		initMovimientoExpediente();
		getExpedienteHome().setVisibleDialog("movimientoExpediente", true);
		return "";
	}
	
	public void initMovimientoExpediente() {
		this.movimiento = null;
		this.detalleCambioEstadoExpediente = null;
	}
	
	public String doEventoExpediente() {
		initEventoExpediente();
		getExpedienteHome().setVisibleDialog("eventoExpediente", true);
		return "";
	}
	
	public void initEventoExpediente() {
		this.eventoInfo = new EventoInfo();
		this.detalleCambioEstadoExpediente = null;
		EventoManager.instance().setModoExpediente();
	}
	
	public String doDejarNota() {
		initDejarNota();
		getExpedienteHome().setVisibleDialog("dejarNota", true);
		return "";
	}
	
	public void initDejarNota() {
		this.eventoInfo = new EventoInfo();
		this.detalleCambioEstadoExpediente = null;
	}
	
	public String initCerrarCirculacion(){
		setEstadoCerrarCirculacion(null);
		setDespachoCierreCirculacion(null);
		getExpedienteHome().setVisibleDialog("cerrarCirculacion", true);
		return "";		
	}
	
	public String doFinalizeCerrarCirculacion() throws AccionException{		
		Expediente expediente = getExpedienteHome().getInstance();
		Expediente principal = isCirculacion(expediente) ? expediente.getExpedienteOrigen() : expediente;
		DocumentoExp despacho = getDespachoCierreCirculacion();
		
		if(getEstadoCerrarCirculacion() == null){
			getStatusMessages().addFromResourceBundle(Severity.ERROR, "expedienteManager.errorEstadoCerrarCirculacion.empty");
			return "";
		}
		if(despacho == null){
			getStatusMessages().addFromResourceBundle(Severity.ERROR, "expedienteManager.errorDespachoCerrarCirculacion.empty");
			return "";
		}
		getExpedienteHome().setVisibleDialog("cerrarCirculacion", false);
		
		new AccionCambioEstadoExpediente().doAccion(getExpedienteHome().getInstance(), getEstadoCerrarCirculacion(), null, true);
		
		if(isCirculacion(expediente) && principal != null){
			expediente.setSituacion(SituacionExpedienteEnumeration.Type.cuaderno_cerrado.getValue().toString());
			if (!getDespachoManager().incluirDespacho(despacho, principal, expediente)) {
				throw new RuntimeException(interpolate(getMessages().get("expedienteManager.error_incorporar_despacho_cuaderno"), despacho.getDescripcion(), despacho.getFojaInicial()));
			}
		}

		if (principal.isAcumuladoJuridicoMadre() && RecursoExpManager.instance().isPropagarFalloHijas(expediente.getRecursoExp())) {
			for (Expediente hija: getAcumuladoJuridicoHijas(principal)) {
				if (!getDespachoManager().incluirDespacho(despacho, hija, expediente)) {
					getStatusMessages().addFromResourceBundle(Severity.ERROR, "expedienteManager.error_incorporar_despacho_hija", getIdxAnaliticoFirst(hija), despacho.getDescripcion(), despacho.getFojaInicial());
				}
				new AccionCambioEstadoExpediente().doAccion(hija, getEstadoCerrarCirculacion(), null, true);
			}
		}

		if(isCirculacion(expediente)){
			new AccionCambioEstadoExpediente().doAccion(expediente, EstadoExpedientePredefinedEnumeration.Type.$CE.getValue().toString(), null, true);
			expediente.setEnTramite(false);
		}
		
		getEntityManager().flush();
		return "";		
	}

	public boolean doFinalizeCambioEstadoExpediente() {
		boolean ret = true;
		Expediente valorAnterior = saveEstadoExpediente(getExpedienteHome().getInstance());
		if(cambioEstadoExpediente != null && !cambioEstadoExpediente.equals(getExpedienteHome().getInstance().getEstadoExpediente())){			
			try {
				new AccionCambioEstadoExpediente().doAccion(getExpedienteHome().getInstance(), cambioEstadoExpediente.getCodigo(), detalleCambioEstadoExpediente, true);
			} catch (AccionException e) {
				ret = false;
			}
		}
		if(ret){
			getExpedienteHome().setVisibleDialog("cambioEstadoExpediente", false);
		}else{
			restoreEstadoExpediente(valorAnterior, getExpedienteHome().getInstance());
		}
		return ret;
	}

	public boolean doFinalizeMovimientoExpediente() {
		boolean ret = true;
		Expediente valorAnterior = saveEstadoExpediente(getExpedienteHome().getInstance());
		if(movimiento != null){			
			try {
				new AccionCambioEstadoExpediente().doAccion(getExpedienteHome().getInstance(), null, movimiento, detalleCambioEstadoExpediente, true);
			} catch (AccionException e) {
				ret = false;
			}
		}
		if(ret){
			getExpedienteHome().setVisibleDialog("cambioEstadoExpediente", false);
		}else{
			restoreEstadoExpediente(valorAnterior, getExpedienteHome().getInstance());
		}
		return ret;
	}
	
	public void doAcceptEventoExpediente() {		
		doAcceptEventoExpediente("");
	}
	
	public String doAcceptEventoExpediente(String page) {		
		if(!doFinalizeEventoExpediente(getExpedienteHome().getInstance())){
			page = null;
		} else {
			if(eventoInfo.isCodigoSeleccion(CodigoSeleccionEstadoExpedienteEnumeration.Type.elevacionRecurso)) {
				RecursoInfo recursoInfo = eventoInfo.getRecursoInfo();
				if (recursoInfo.isSortearSala()) {
					page = "/webCustom/mesaEntrada/expediente/asignacion/asignacionMeFin.xhtml";
				}
			}
		}
		return page;
	}
	
	public boolean doFinalizeEventoExpediente(Expediente expediente) {
		boolean ret = true;
		EventoManager.instance().beforeValidate(eventoInfo);
		if (!EventoManager.instance().validateEventoInfo(eventoInfo, getExpedienteHome().getInstance())) {
			ret = false;
		} else {
			Expediente valorAnterior = saveEstadoExpediente(expediente);
			if(eventoInfo.getEstadoExpediente() != null){
				try {
					EventoManager.instance().confirmarEvento(eventoInfo, expediente, null);
				} catch (EntityOperationException e) {
					ret = false;
				}
			}
			if(!ret){
				restoreEstadoExpediente(valorAnterior, expediente);
			}
		}
		if(ret){
			SelectorIntervinienteCedulasManager.instance().clear();
			getExpedienteHome().setVisibleDialog("eventoExpediente", false);
		}
		return ret;
	}

	public boolean doFinalizeDejarNota() {
		boolean ret = true;
		try {
			eventoInfo.setEstadoExpediente(EventoManager.instance().findNTA());
			if(participante != null){
				eventoInfo.setComentarios(participante + " - " + eventoInfo.getComentarios());
			}
			participante = null;
			EventoManager.instance().confirmarEvento(eventoInfo, getExpedienteHome().getInstance(), null);
		} catch (Exception e) {
			ret = false;
		}
		if(ret){
			getExpedienteHome().setVisibleDialog("dejarNota", false);
		}
		return ret;
	}

	private String nombreParticipante() {
		if(participante != null){
			return participante + " :";
		}
		return "";
	}
	
	public void doCancelCambioEstadoExpediente() {
		getExpedienteHome().setVisibleDialog("cambioEstadoExpediente", false);
	}
	
	public void doCancelMovimientoExpediente() {
		getExpedienteHome().setVisibleDialog("movimientoExpediente", false);
	}

	public void doCancelEventoExpediente() {
		getExpedienteHome().setVisibleDialog("eventoExpediente", false);
	}

	public void doCancelDejarNota() {
		getExpedienteHome().setVisibleDialog("dejarNota", false);
	}

	private Expediente saveEstadoExpediente(Expediente instance) {
		Expediente save = new Expediente();
		save.setEstadoExpediente(instance.getEstadoExpediente());
		save.setFechaEstado(instance.getFechaEstado());
		return save;
	}

	private void restoreEstadoExpediente(Expediente save, Expediente instance) {
		instance.setEstadoExpediente(save.getEstadoExpediente());
		instance.setFechaEstado(save.getFechaEstado());
	}

	private Expediente saveOficinaActualExpediente(Expediente instance) {
		Expediente save = new Expediente();
		save.setOficinaActual(instance.getOficinaActual());
		return save;
	}
	
	// Cambio de situacion
	public String doCambioSituacion() {
		initCambioSituacion(false);
		getExpedienteHome().setVisibleDialog("cambioSituacion", true);
		return "";
	}
	
	public String doCambioSituacionReparar() {
		initCambioSituacion(true);
		getExpedienteHome().setVisibleDialog("cambioSituacion", true);
		return "";
	}

	// Cambio de situacion
	public String doSeleccionTipoReserva() {
		initDoSeleccionTipoReserva(false);
		getExpedienteHome().setVisibleDialog("tipoReserva", true);
		return "";
	}
	
	public void initDoSeleccionTipoReserva(boolean repararReserva) {
		this.repararReserva = repararReserva; 
		this.cambioSituacion = null;
	}
	
	public void initCambioSituacion(boolean repararSituacion) {
		this.repararSituacion = repararSituacion; 
		this.cambioSituacion = null;
	}
	
	public boolean isRepararSituacion() {
		return repararSituacion;
	}

	public void setRepararSituacion(boolean repararSituacion) {
		this.repararSituacion = repararSituacion;
	}
	
	public boolean doFinalizeCambioSituacion() {
		return doFinalizeCambioSituacion(true);
	}
	
	public boolean doFinalizeReservaExpediente() {
		return doFinalizeReservaExpediente(true);
	}
	
	public boolean doFinalizeCambioSituacion(boolean mostrarMensaje) {
		boolean ret = true;
		Expediente valorAnterior = saveSituacionExpediente(getExpedienteHome().getInstance());
		if(cambioSituacion != null && !cambioSituacion.equals(getExpedienteHome().getInstance().getSituacion())){			
			try {
				new AccionCambioSituacionExpediente().doAccion(getExpedienteHome().getInstance(), cambioSituacion, true);
			} catch (AccionException e) {
				ret = false;
			}
		}
		if(ret){
			getExpedienteHome().setVisibleDialog("cambioSituacion", false);
			setBandejaCambioSituacion(false);
			if (mostrarMensaje) {
				getStatusMessages().addFromResourceBundle("expediente.cambioSituacionRealizado");
			}
		}else{
			restoreSituacionExpediente(valorAnterior, getExpedienteHome().getInstance());
		}
		return ret;
	}
	
	public boolean doFinalizeReservaExpediente(boolean mostrarMensaje) {
		boolean ret = true;
		Expediente valorAnterior = saveSituacionExpediente(getExpedienteHome().getInstance());
		if(tipoReserva != null && !tipoReserva.equals(getExpedienteHome().getInstance().getReservado())){			
			try {
				new AccionActualizarReservaExpediente().doAccion(getExpedienteHome().getInstance(), tipoReserva);
			} catch (AccionException e) {
				ret = false;
			}
		}
		if(ret){
			getExpedienteHome().setVisibleDialog("reservaExpediente", false);
			if (mostrarMensaje) {
				getStatusMessages().addFromResourceBundle("expediente.tipoReservaRealizado");
			}
		}else{
			restoreSituacionExpediente(valorAnterior, getExpedienteHome().getInstance());
		}
		return ret;
	}
	
	public void doCancelCambioSituacion() {
		getExpedienteHome().setVisibleDialog("cambioSituacion", false);
		setBandejaCambioSituacion(false);
	}
	
	public void doCancelReservaExpediente() {
		getExpedienteHome().setVisibleDialog("reservaExpediente", false);
		
	}
	
	
	private Expediente saveSituacionExpediente(Expediente instance) {
		Expediente save = new Expediente();
		save.setSituacion(instance.getSituacion());
		save.setFechaArchivo(instance.getFechaArchivo());
		save.setFechaPase(instance.getFechaPase());
		save.setFechaInicio(instance.getFechaInicio());
		save.setFechaSituacion(instance.getFechaSituacion());
		save.setFechaPrestamo(instance.getFechaPrestamo());
		save.setReservado(instance.getReservado());
		return save;
	}

	private void restoreSituacionExpediente(Expediente save, Expediente instance) {
		instance.setSituacion(save.getSituacion());
		instance.setFechaArchivo(save.getFechaArchivo());
		instance.setFechaPase(save.getFechaPase());
		instance.setFechaInicio(save.getFechaInicio());
		instance.setFechaSituacion(save.getFechaSituacion());
		instance.setFechaPrestamo(save.getFechaPrestamo());
		instance.setReservado(save.getReservado());
	}

// Tipo Causa
	public boolean doFinalizeCambioTipoCausa() {
		return doFinalizeCambioTipoCausa(true);
	}
	
	public boolean doFinalizeCambioTipoCausa(boolean mostrarMensaje) {
		boolean ret = true;
		Expediente valorAnterior = saveSituacionExpediente(getExpedienteHome().getInstance());
		if(!equals(cambioTipoCausa, getExpedienteHome().getInstance().getTipoCausa())){			
			try {
				new AccionCambioTipoCausaExpediente().doAccion(getExpedienteHome().getInstance(), cambioTipoCausa, true);
			} catch (AccionException e) {
				ret = false;
			}
		}
		if(ret){
			getExpedienteHome().setVisibleDialog("cambioTipoCausa", false);
			if (mostrarMensaje) {
				getStatusMessages().addFromResourceBundle("expediente.cambioTipoCausaRealizado");
			}
		}else{
			restoreSituacionExpediente(valorAnterior, getExpedienteHome().getInstance());
		}
		return ret;
	}
	
	// Tipo Proceso
	public boolean doFinalizeCambioTipoProceso() {
		return doFinalizeCambioTipoProceso(true);
	}
	
	public boolean doFinalizeCambioTipoProceso(boolean mostrarMensaje) {
		boolean ret = true;
		Expediente valorAnterior = saveSituacionExpediente(getExpedienteHome().getInstance());
		if(cambioTipoProceso != null && !equals(cambioTipoProceso, getExpedienteHome().getInstance().getTipoProceso())){			
			try {
				new AccionCambioTipoProcesoExpediente().doAccion(getExpedienteHome().getInstance(), cambioTipoProceso, true);
			} catch (AccionException e) {
				ret = false;
			}
		}
		if(ret){
			getExpedienteHome().setVisibleDialog("cambioTipoProceso", false);
			if (mostrarMensaje) {
				getStatusMessages().addFromResourceBundle("expediente.cambioTipoProcesoRealizado");
			}
		}else{
			restoreSituacionExpediente(valorAnterior, getExpedienteHome().getInstance());
		}
		return ret;
	}
	
	// Tipo Incidente
	public boolean doFinalizeCambioTipoSubexpediente() {
		return doFinalizeCambioTipoSubexpediente(true);
	}
	
	public boolean doFinalizeCambioTipoSubexpediente(boolean mostrarMensaje) {
		boolean ret = true;
		Expediente expediente = getExpedienteHome().getInstance();
		
		if(isCirculacion(expediente)){
			checkCambioMotivoCirculacion(cambioTipoSubexpediente, cambioTipoCirculacion);
		}else{		
			Expediente valorAnterior = saveSituacionExpediente(expediente);
			if(cambioTipoSubexpediente != null && !equals(cambioTipoSubexpediente, expediente.getTipoSubexpediente())){			
				try {
					new AccionCambioTipoSubexpedienteExpediente().doAccion(expediente, cambioTipoSubexpediente, true);
				} catch (AccionException e) {
					ret = false;
				}
			}
			if(ret){
				if(!equals(cambioTipoCirculacion, expediente.getTipoSubexpedienteExtendido())){
					expediente.setTipoSubexpedienteExtendido(cambioTipoCirculacion);
					
					getEntityManager().flush();
				}
				if (mostrarMensaje) {
					getStatusMessages().addFromResourceBundle("expediente.cambioTipoSubexpedienteRealizado");
				}			
			}else{
				restoreSituacionExpediente(valorAnterior, expediente);
			}
		}
		if(ret){
			getExpedienteHome().setVisibleDialog("cambioTipoSubexpediente", false);
		}
		return ret;
	}
	
	public void checkCambioMotivoCirculacion(TipoSubexpediente nuevoMotivoCirculacion, TipoSubexpediente nuevoTipoCirculacion) {
		Expediente expediente = getExpedienteHome().getInstance();
		if(ExpedienteManager.isCirculacion(expediente)){
			if(expediente.getTipoSubexpediente() != nuevoMotivoCirculacion ||expediente.getTipoSubexpedienteExtendido() != nuevoTipoCirculacion){
				String key = "expediente.cambioMotivoCirculacion";
				String antes = getMotivoTipoCirculacionAsString(expediente.getTipoSubexpedienteExtendido(), expediente.getTipoSubexpedienteExtendido());
				String despues = getMotivoTipoCirculacionAsString(nuevoMotivoCirculacion, nuevoTipoCirculacion);

				String detalle = Interpolator.instance().interpolate(getMessages().get(key), antes, despues);
				expediente.setTipoSubexpediente(nuevoMotivoCirculacion);
				expediente.setTipoSubexpedienteExtendido(nuevoTipoCirculacion);
				ExpedienteManager.instance().actualizarCaratula(expediente, TipoInformacionEnumeration.CAMBIO_CARATULA, detalle);
			}
		}
	}
	
	private String getMotivoTipoCirculacionAsString(TipoSubexpediente motivoCirculacion,	TipoSubexpediente tipoCirculacion) {
		String ret = motivoCirculacion != null ? motivoCirculacion.getDescripcion() : null;
		if(tipoCirculacion != null){
			ret = ret == null ? tipoCirculacion.getDescripcion() : ret + " - " + tipoCirculacion.getDescripcion();
		}
		return ret == null ? "" : ret;
	}


	
	private boolean equals(TipoSubexpediente t1, TipoSubexpediente t2) {
		if(t1 == null || t2 == null){
			return t1 == t2;
		}else{
			return t1.getId().equals(t2.getId());
		}
	}

	private boolean equals(TipoCausa t1, TipoCausa t2) {
		if(t1 == null || t2 == null){
			return t1 == t2;
		}else{
			return t1.getId().equals(t2.getId());
		}
	}
	
	private boolean equals(TipoProceso t1, TipoProceso t2) {
		if(t1 == null || t2 == null){
			return t1 == t2;
		}else{
			return t1.getId().equals(t2.getId());
		}
	}
	
	private boolean equals(Camara t1, Camara t2) {
		if(t1 == null || t2 == null){
			return t1 == t2;
		}else{
			return t1.getId().equals(t2.getId());
		}
	}

	public void doCancelCambioTipoCausa() {
		getExpedienteHome().setVisibleDialog("cambioTipoCausa", false);
	}
	
	
	public TipoCausa getCambioTipoCausa() {
		return cambioTipoCausa;
	}


	public void setCambioTipoCausa(TipoCausa cambioTipoCausa) {
		this.cambioTipoCausa = cambioTipoCausa;
	}
	
/******/
	
	public void doCancelCambioTipoProceso() {
		getExpedienteHome().setVisibleDialog("cambioTipoProceso", false);
	}
	
	
	public TipoProceso getCambioTipoProceso() {
		return cambioTipoProceso;
	}


	public void setCambioTipoProceso(TipoProceso cambioTipoProceso) {
		this.cambioTipoProceso = cambioTipoProceso;
	}

	
	public void doCancelCambioTipoSubexpediente() {
		getExpedienteHome().setVisibleDialog("cambioTipoSubexpediente", false);
	}
	
	public TipoSubexpediente getCambioTipoSubexpediente() {
		return cambioTipoSubexpediente;
	}


	public void setCambioTipoSubexpediente(TipoSubexpediente cambioTipoSubexpediente) {
		this.cambioTipoSubexpediente = cambioTipoSubexpediente;
	}

	public TipoSubexpediente getCambioTipoCirculacion() {
		return cambioTipoCirculacion;
	}

	public void setCambioTipoCirculacion(
			TipoSubexpediente cambioTipoCirculacion) {
		this.cambioTipoCirculacion = cambioTipoCirculacion;
	}

	/***** AUXILIAR FUNCTTIONS ********/
	public String getIdxAnaliticoFirst(Expediente expediente) {
		return getIdxAnaliticoFirst(expediente, true);
	}

	public String getIdxAnaliticoFirst(Expediente expediente, boolean comprobarRecurso) {
		return getIdxAnaliticoFirst(expediente, comprobarRecurso, true);
	}
	
	public String getIdxAnaliticoFirst(Expediente expediente, boolean comprobarRecurso, boolean eliminarCeros) {
		String ret = null;
		if(expediente != null){
			if (!getEntityManager().contains(expediente) && expediente.getId() != null) {	
				expediente = getEntityManager().find(Expediente.class, expediente.getId());
			}
		}
		if(expediente != null){
			if (isLegajoPersonalidad(expediente) && expediente.getId() != null) {
				Expediente expedienteGrabacion = LegajoPersonalidadExpManager.instance().getExpedienteGrabacion();
				if (expedienteGrabacion != null) {
					String clave = IntervinienteExpManager.instance().getClaveLegajoPersonalidad(expedienteGrabacion, expediente);
					if (!Strings.isEmpty(clave)) {
						return clave;
					}
				}
			}
			StringBuilder sb = new StringBuilder();
			if (comprobarRecurso && expediente.isTieneRecursosAsignadosPteResolver()) {
				String claveRecursos = calculateClaveRecursosSinResolver(expediente, eliminarCeros, 2);
				if(claveRecursos != null){
					return claveRecursos;
				}
			}
			if (expediente.getExpedienteOrigen() != null && expediente.getExpedienteOrigen() != expediente) {
				sb = new StringBuilder(getIdxAnaliticoFirst(expediente.getExpedienteOrigen(), false, false));
				appendNumeroSubexpediente(expediente.getTipoSubexpediente(), expediente.getNumeroSubexpediente(), expediente.getCodigoSubexpediente(), sb);
				if (isExpedienteAdministrativo(expediente)){
					String anexo = getEtiquetaAnexo(expediente);
					if(!Strings.isEmpty(anexo)){
						sb.append(anexo);
					}
				}
			} else {
				actualizarPrefijoIdxExpediente(expediente, sb);
				actualizarNumeroExpedienteAnalitico(expediente.getNumero(), expediente.getAnio(), !eliminarCeros, sb);
				if(!isPrincipal(expediente)) {
					appendNumeroSubexpediente(expediente.getTipoSubexpediente(), expediente.getNumeroSubexpediente(), expediente.getCodigoSubexpediente(), sb);
				}
				String tomoLetra = getTomoLetra(expediente);
				if(!Strings.isEmpty(tomoLetra)){
					sb.append(tomoLetra);
				}
			}
			ret = sb.toString();
		}
		return ret != null && eliminarCeros ? removeCerosDerecha(ret) : ret;
	}

	private String calculateClaveRecursosSinResolver(Expediente expediente, boolean eliminarCeros, int maxNum) {		
		List<RecursoExp> list = computeRecursosAsignadosPteResolver(expediente);
		StringBuffer sb = new StringBuffer();
		int num = 0;
		for(RecursoExp recurso: list){
			getEntityManager().refresh(recurso);
			String claveRecurso = recurso.getClave();
			if(!Strings.isEmpty(claveRecurso)){
				claveRecurso = reemplazarCamara(expediente.getCamara(), claveRecurso);
				if (!Strings.isEmpty(claveRecurso) && eliminarCeros) {
					claveRecurso = removeCerosDerecha(claveRecurso);
				}
			}
			if(!Strings.isEmpty(claveRecurso)){
				if(sb.length() != 0){
					sb.append(" - ");
					claveRecurso = extractExpediente(expediente, claveRecurso);					
				}
				if(num == maxNum){
					sb.append("...");
					break;
				}else{
					sb.append(claveRecurso);
					num++;
				}
			}			
		}	
		return sb.toString();
	}
	
	private String extractExpediente(Expediente expediente, String claveRecurso) {
		String ret = claveRecurso;
		int idx = claveRecurso.lastIndexOf("/");
		if(idx != -1){
			ret = claveRecurso.substring(idx+1);
		}
		return ret;
	}

	private String calculateClaveUltimoRecursoSinResolver(Expediente expediente, boolean eliminarCeros) {
		String claveRecurso = buscarClaveRecursosAsignadosPteResolver(expediente);
		claveRecurso = reemplazarCamara(expediente.getCamara(), claveRecurso);
		if (!Strings.isEmpty(claveRecurso) && eliminarCeros) {
			claveRecurso = removeCerosDerecha(claveRecurso);
		}else{
			claveRecurso = null;
		}
		return claveRecurso;
	}
	
	
	private String reemplazarCamara(Camara camara, String claveRecurso) {
		if (Strings.isEmpty(claveRecurso)) {
			return null;
		}
		int pos = claveRecurso.indexOf(" ");
		if (pos != -1) {
			claveRecurso = camara.getAbreviatura() + claveRecurso.substring(pos);
		}
		return claveRecurso;
	}

	private String removeCerosDerecha(String claveRecurso) {
		StringBuilder builder = new StringBuilder();
		boolean eliminarCeros = true;
		for (int i=0; i<claveRecurso.length(); i++) {
			char c = claveRecurso.charAt(i);
			if (Character.isDigit(c)) {
				if (eliminarCeros) {
					if (c != '0') {
						builder.append(c);
						eliminarCeros = false;
					}
				} else {
					builder.append(c);
				}
			} else {
				builder.append(c);
				eliminarCeros = true;
			}
		}
		return builder.toString();
	}
	
	public String getPrefijoCamaraExpediente(Expediente expediente){
		String ret = "";
		if(expediente != null){
			Camara camara = expediente.getCamara();
			Oficina oficinaInicial = expediente.getOficinaInicial();
			if (camara == null) {
				if (oficinaInicial != null) {
					camara = oficinaInicial.getCamara();
				}else if (expediente.getOficinaActual() != null){
					camara = expediente.getOficinaActual().getCamara();
				}
			}
			if (camara != null && camara.getAbreviatura() != null) {
				ret = camara.getAbreviatura();
			}
			if(TipoCausaEnumeration.isMediacion(expediente.getTipoCausa())){
				ret += "-M";
			}
		}
		return ret;
	}

	public void actualizarPrefijoIdxExpediente(Expediente expediente,
			StringBuilder sb
			) {
		String prefijoCamaraExpediente = getPrefijoCamaraExpediente(expediente);
		if (prefijoCamaraExpediente != null && prefijoCamaraExpediente.length() > 0) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(prefijoCamaraExpediente);
		}
		if (sb.length() > 0) {
			sb.append(" ");
		}
	}
	
	public String getIdxAnaliticoSecond(Expediente expediente) {
		if(expediente == null){
			return null;
		}else{
			StringBuilder sb = new StringBuilder();
			
			actualizarOficina(expediente.getOficinaInicial(), sb);
			
			return sb.toString();
		}
	}

	public String getIdxAnalitico(Expediente expediente) {
		if(expediente == null){
			return null;
		}else{
			return getIdxAnaliticoFirst(expediente, false, false)+"-"+getIdxAnaliticoSecond(expediente);
		}
	}

	public String getEtiquetaAnexo(Expediente expediente){
		StringBuilder sb = new StringBuilder();	
		String etiqueta = expediente.getEtiqueta();
		if (!Strings.isEmpty(etiqueta)){
			sb.append("(").append(etiqueta).append(")");
		}
		return sb.toString();
	}
	public String getTomoLetra(Expediente expediente){
		StringBuilder sb = new StringBuilder();	
		String tomoLetra = expediente.getTomoLetra();
		if (!Strings.isEmpty(tomoLetra)){
			sb.append("(").append(tomoLetra).append(")");
		}
		return sb.toString();
	}
	
	public String getIdxAnalitico(Integer numero, Integer anio, Oficina oficina) {
		return getIdxAnalitico(numero, anio, oficina, null, null, null);
	}

	public String getIdxAnalitico(Integer numero, Integer anio, Oficina oficina, TipoSubexpediente tipoSubexpediente, Integer numeroSubexpediente, String codigoSubexpediente) {
		StringBuilder sb = new StringBuilder();			
		
		actualizarNumeroExpedienteAnalitico(numeroSubexpediente, anio, true, sb);
		if (tipoSubexpediente != null && numeroSubexpediente != null && numeroSubexpediente != 0) {
			appendNumeroSubexpediente(tipoSubexpediente, numeroSubexpediente, codigoSubexpediente, sb);
		}
		
		sb.append("-");

		actualizarOficina(oficina, sb);
		
		return sb.toString();
	}

	private void actualizarOficina(Oficina oficina, StringBuilder sb) {
		int camara = oficina == null ? 0 : oficina.getCamara().getCodigo(); 
		sb.append(format2(camara));
		sb.append("-");
		int nivel = oficina == null || oficina.getTipoInstancia() == null ? 0 : oficina.getTipoInstancia().getCodigo();
		sb.append(format2(nivel));
		sb.append("-");
		if (oficina == null) {
			sb.append("0");
		} else {
			Oficina juzgado = oficina;
			if (getOficinaManager().isSecretariaJuzgado(oficina)) {
				juzgado = CamaraManager.getOficinaPrincipal(oficina);
			} 
			if(juzgado != null){
				sb.append(juzgado.getCodigo().replaceAll("_", "."));
			}
		}
	}

	public void actualizarNumeroExpedienteAnalitico(Integer numero, Integer anio, boolean rellenarCeros, StringBuilder sb) {
		if(numero <= 999999) {
			sb.append(format6(numero, rellenarCeros));
		} else {	// Expedientes Anteriores al Sistema (nº de 9 digitos)
			sb.append(format9(numero, rellenarCeros));
		}
		sb.append("/");
		sb.append(format4(anio, rellenarCeros));
	}

	public void appendNumeroSubexpediente(TipoSubexpediente tipoSubexpediente,
			Integer numeroSubexpediente, String codigoSubexpediente,
			StringBuilder sb) {
		if (tipoSubexpediente != null &&  !NaturalezaSubexpedienteEnumeration.Type.principal.getValue().equals(tipoSubexpediente.getNaturalezaSubexpediente())) {
			sb.append("/");
			if (!Strings.isEmpty(codigoSubexpediente)) {
				if (NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue().equals(tipoSubexpediente.getNaturalezaSubexpediente()) ||
					NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue().equals(tipoSubexpediente.getNaturalezaSubexpediente()) ||
					NaturalezaSubexpedienteEnumeration.Type.escrito.getValue().equals(tipoSubexpediente.getNaturalezaSubexpediente()) ||
					NaturalezaSubexpedienteEnumeration.Type.circulacion.getValue().equals(tipoSubexpediente.getNaturalezaSubexpediente())
					) {
					if (numeroSubexpediente != 0) {
						sb.append(numeroSubexpediente).append("/");
					}
				} 
				sb.append(codigoSubexpediente);
			} else {
				sb.append(numeroSubexpediente);
			}
		}
	}

	public String getOldFormatIdxAnalitico(Integer numero, Integer anio, Oficina oficina) {
		StringBuilder sb = new StringBuilder();			
		
		actualizarNumeroExpedienteAnalitico(numero, anio, true, sb);
		
		sb.append("-");

		actualizarOficinaOldFormat(oficina, sb);
		
		return sb.toString();
	}

	private void actualizarOficinaOldFormat(Oficina oficina, StringBuilder sb) {
		int camara = oficina == null ? 0 : oficina.getCamara().getCodigo(); 
		sb.append(camara);
		sb.append("-");
		int nivel = oficina == null ? 0 : oficina.getTipoInstancia().getCodigo();
		sb.append(nivel);
		sb.append("-");
		if (oficina == null) {
			sb.append("0");
		} else {
			Oficina juzgado = oficina;
			if (getOficinaManager().isSecretariaJuzgado(oficina)) {
				juzgado = CamaraManager.getOficinaPrincipal(oficina);
			} 
			sb.append(juzgado.getCodigo());
		}
	}

	// Expedientes Anteriores al Sistema (nº de 9 digitos)
	private String format9(Integer i, boolean rellenarCeros) {
		if(i == null){
			return null;
		}
		if (rellenarCeros) {
			return decimalFormat9.format(i);
		} else {
			return String.valueOf(i);
		}
	}

	private String format6(Integer i, boolean rellenarCeros) {
		if(i == null){
			return null;
		}
		if (rellenarCeros) {
			return decimalFormat6.format(i);
		} else {
			return String.valueOf(i);
		}
	}
	
	private String format4(Integer i, boolean rellenarCeros) {
		if(i == null){
			return null;
		}
		if (rellenarCeros) {
			return decimalFormat4.format(i);
		} else {
			return String.valueOf(i);
		}
	}

	private String format2(Integer i) {
		if(i == null){
			return null;
		}
		return decimalFormat2.format(i);
	}


	public String getCambioSituacion() {
		return cambioSituacion;
	}


	public void setTipoReserva(Integer tipoReserva) {
		this.tipoReserva = tipoReserva;
	}
	

	public Integer getTipoReserva() {
		return tipoReserva;
	}


	public void setCambioSituacion(String cambioSituacion) {
		this.cambioSituacion = cambioSituacion;
	}

	public boolean isCamaraActual(Expediente expediente) {
		return getSessionState().getGlobalCamara() != null && getSessionState().getGlobalCamara().equals(expediente.getCamaraActual());
	}
	
	private boolean isAccionPermitidaOtraOficina(ExpedienteAction expedienteAction, boolean inCamaraActual) {
		if (expedienteAction == ExpedienteAction.CancelarPase) {
			return true;
		}
		if ((!CamaraManager.isCamaraActualComercial() || inCamaraActual) &&
			(  expedienteAction == ExpedienteAction.TraerExpediente 
			|| expedienteAction == ExpedienteAction.GenerarRemisionExpedientesHijasMadre
			|| expedienteAction == ExpedienteAction.CrearCuaderno
			|| expedienteAction == ExpedienteAction.CrearVinculado)
			) {
			return true;
		}
		if ( inCamaraActual &&
			(  expedienteAction == ExpedienteAction.CambioOrdenIntervinientes
			|| expedienteAction == ExpedienteAction.CambioTipoCausa
			|| expedienteAction == ExpedienteAction.CrearEscrito
			|| expedienteAction == ExpedienteAction.CambioTipoProceso
			|| expedienteAction == ExpedienteAction.CambioTipoSubexpediente
			|| expedienteAction == ExpedienteAction.ModificarDatosCabecera
			|| expedienteAction == ExpedienteAction.RehabilitacionPlazoVencido
			|| expedienteAction == ExpedienteAction.CrearSubexpedienteAdministrativo)
			) {
			return true;
		}
		return false;
	}
	
	public boolean isActionEnabled(String actionName) {
		return isActionEnabled(getExpedienteHome().getInstance(), actionName);
	}
	public boolean isItemNotNull(Expediente item) {
		return item != null;
	}
	
	public boolean isActionEnabled(Expediente expediente, String actionName) {
		if (expediente == null) {
			return false;
		}
		ExpedienteAction expedienteAction = ExpedienteAction.getFromName(actionName);
		if (!isOficinaActual(expediente) && !isLegajoPersonalidad(expediente) && !isAccionPermitidaOtraOficina(expedienteAction, isCamaraActual(expediente))) {
			return false;
		} else if (isLegajoPersonalidad(expediente) && expedienteAction != ExpedienteAction.ValidarLIP && expedienteAction != ExpedienteAction.AgregarLIP ) {
			return false;
		}
		if (expedienteAction != null) {
			if(expedienteAction != ExpedienteAction.Pase && !isContentsVisible(expediente)){
				return false;
			}
			boolean comprobarPermiso = true;
//			if (expedienteAction == ExpedienteAction.DesasignarmeExpediente && 
//				expediente.getResponsable() != null && 
//				expediente.getResponsable().equalsIgnoreCase(getIdentity().getCredentials().getUsername())) {
//				comprobarPermiso = false;
//			}
//			
//			if (expedienteAction == ExpedienteAction.DesasignarExpediente && 
//					this.hasResponsable(expediente)) {
//				comprobarPermiso = false;
//			}
			
			if (comprobarPermiso && 
				!getIdentity().hasPermission("Expediente", actionName)) {
				return false;
			}
			if (!expedienteAction.equals(ExpedienteAction.ReabrirCuaderno) && isSubexpedienteNoActivo(expediente)) {
				return false;
			}
			switch (expedienteAction) {
			case Iniciar:
				return isPermiteEstados(expediente) && !isIniciado(expediente) && !isMediacion(expediente) && (!expediente.isIngresoWeb() || isRecepcionado(expediente));
			case Recepcionar:
				return !isIniciado(expediente) && !isRecepcionado(expediente) && !isPase(expediente) && !isRemision(expediente);
			case CambioSituacion:
				return (isSituacionEnOficinaYOficinaActual(expediente) || isLetraOficinaActual(expediente) || isConfronteOficinaActual(expediente) || isSentenciaOficinaActual(expediente)) && isRecibidoOficinaActual(expediente) && isPermiteCambioSituacion(expediente);
			case CambioSituacionReparar:
				return !isExpedienteAdministrativo(expediente);
			/** ATOS - OFICINA DE LA MUJER */
			case AgregarInformacionOficinaMujer:
				return true;	
			/** ATOS - OFICINA DE LA MUJER */
			case CambioEstadoExpediente:
				return isPermiteEstados(expediente)
					&& isOficinaActual(expediente)
					&& (isDespacho(expediente) || 
						isLetra(expediente) || 
						isConfronte(expediente) || 
						isSentencia(expediente))
					&& ConfiguracionMateriaManager.instance().isAplicarEstadoProcesal(expediente.getOficinaActual().getCamara(), expediente.getMateria())
					&& !isPenal(expediente);
			case CambioEnTramite:
				return isRecibidoOficinaActual(expediente) && isPenal(expediente);
			case EmitirCedulas:
			case EmitirPrestamo:
				return isSituacionEnOficinaYOficinaActual(expediente);
			case Paralizar:
				return isPermiteEstados(expediente) && (isSituacionEnOficinaYOficinaActual(expediente) || isConfronteOficinaActual(expediente) || isLetraOficinaActual(expediente)) && !getOficinaManager().isPenal(expediente.getOficinaActual());
			case Archivar:
				return isPermiteEstados(expediente) && isRadicadoOficinaActual(expediente) && (isSituacionEnOficinaYOficinaActual(expediente) || isConfronteOficinaActual(expediente) || isLetraOficinaActual(expediente));
			case Pase:
				return isVistaOficinaActual(expediente) || (((!isIniciado(expediente) && getOficinaManager().inAlgunaMesaDeEntrada() && !isRemision(expediente)) || isSituacionEnOficinaYOficinaActual(expediente)) && !isPase(expediente));
			case RecibirPase:
				return expediente.getPasePendiente() != null && isPase(expediente) && expediente.isPendienteRecepcion() && (getSessionState().getGlobalIdOficinaSet().contains(expediente.getOficinaActual().getId()));
			case RetornarPase:
				return expediente.getPasePendiente() != null && isPase(expediente) && expediente.getOficinaRetorno() !=  null && getSessionState().getGlobalIdOficinaSet().contains(expediente.getOficinaActual().getId()) && !getSessionState().getGlobalIdOficinaSet().contains(expediente.getOficinaRetorno().getId());
			case CancelarPase:
				return expediente.getPasePendiente() != null && isPase(expediente) && expediente.isPendienteRecepcion() && expediente.getOficinaRetorno() !=  null && getSessionState().getGlobalIdOficinaSet().contains(expediente.getOficinaRetorno().getId());
			case CancelarRemision:
				return isRemision(expediente) && isOficinaActual(expediente);
			case CrearLegajoContienda:				
				return !CamaraManager.isCamaraActualCorteSuprema() && isSituacionEnOficinaYOficinaActual(expediente) && (getOficinaManager().isPenal(expediente.getOficinaActual())) && !isExpedienteAdministrativo(expediente);
			case CrearEscrito:
				if(isEscrito(expediente))  {
					return false;
				} else if (CamaraManager.isCamaraActualCOM()) {
					return !isLegajoPersonalidad(expediente) && (!CamaraManager.isMesaEntradaActiva() || isRadicadoOficinaActual(expediente) || getOficinaManager().inAlgunaMesaDeEntrada() || (OficinaManager.instance().inJuzgado() && isRadicacionPreviaOficinaActual(expediente))) ;
				} else {
					return !isExpedienteAdministrativo(expediente) && !isCirculacion(expediente) && !isLegajoPersonalidad(expediente) && (!CamaraManager.isMesaEntradaActiva() || isRadicadoOficinaActual(expediente) || getOficinaManager().inAlgunaMesaDeEntrada() || (OficinaManager.instance().inJuzgado() && isRadicacionPreviaOficinaActual(expediente))) ;
				}
			case CrearSubexpedienteAdministrativo:
				return !isExpedienteAdministrativo(expediente) && !isCirculacion(expediente) && !isLegajoPersonalidad(expediente) && (!CamaraManager.isMesaEntradaActiva() || isRadicadoOficinaActual(expediente) || getOficinaManager().inAlgunaMesaDeEntrada() || (OficinaManager.instance().inJuzgado() && isRadicacionPreviaOficinaActual(expediente))) ;
			case CopiarSubexpedienteAdministrativo:
				return isExpedienteAdministrativoDigital(expediente) && (!CamaraManager.isMesaEntradaActiva() || isRadicadoOficinaActual(expediente) || getOficinaManager().inAlgunaMesaDeEntrada() || (OficinaManager.instance().inJuzgado() && isRadicacionPreviaOficinaActual(expediente))) ;
			case CrearCuaderno:
				return !isExpedienteAdministrativo(expediente) && !isCirculacion(expediente) && !isLegajoPersonalidad(expediente) && (!CamaraManager.isMesaEntradaActiva() || isRadicadoOficinaActual(expediente) || getOficinaManager().inAlgunaMesaDeEntrada() || (OficinaManager.instance().inJuzgado() && (isRadicacionPreviaOficinaActual(expediente) || CamaraManager.isCamaraActualElectoral()))) ;
			case CrearCirculacion:
				return !isLegajoPersonalidad(expediente) && !isCirculacion(expediente) && !isEscrito(expediente) && isOficinaActual(expediente);
			case CrearSubexpediente:
				return isPrincipal(expediente) && isSituacionEnOficinaYOficinaActual(expediente) && (getOficinaManager().isCivilComercialFederal(expediente.getOficinaActual()) || getOficinaManager().isContenciosoAdministrativoFederal(expediente.getOficinaActual()));
			case Elevacion:
				return isPermiteEstados(expediente) && isSituacionEnOficinaYOficinaActual(expediente) && isRadicadoOficinaActual(expediente);
			case Confronte:
			case Prestamo:
				return false;
			case AsignarResponsable:
				return !OficinaManager.instance().isMesaEntradaCamaraOficinaActual() && puedeAsignarResponsable(expediente);
			case DesasignarResponsable:
				return puedeDesasignarResponsable(expediente);
//			case DesasignarmeExpediente:
//				return isDespachoOficinaActual(expediente) && isResponsable(expediente) && !CamaraManager.instance().isCamaraUsaResponsableExpediente();
//			case DesasignarExpediente:
//				return isDespachoOficinaActual(expediente) && CamaraManager.instance().isCamaraUsaResponsableExpediente() && hasResponsable(expediente);
//			case AsignarmeExpediente:
//				return isDespachoOficinaActual(expediente) && !isResponsable(expediente) && !CamaraManager.instance().isCamaraUsaResponsableExpediente();
//			case AsignarExpedienteA:
//				return isDespachoOficinaActual(expediente) && CamaraManager.instance().isCamaraUsaResponsableExpediente();
			case Desparalizar:
				return isPermiteEstados(expediente) && isParalizadoOficinaActual(expediente);
			case Desarchivar:
				return isPermiteEstados(expediente) && isArchivadoOficinaActual(expediente);
			case GenerarCaratula:
				return true;
			case ReservarExpediente:
				return isPermiteEstados(expediente) && isSituacionEnOficinaYOficinaActual(expediente) && !tieneReserva(expediente) && !isPase(expediente) && isPermiteReservar(expediente) && !isSubasta(expediente);
			case DesreservarExpediente:
				return isPermiteEstados(expediente) && isSituacionEnOficinaYOficinaActual(expediente) && tieneReserva(expediente) && !isPase(expediente);
			case CancelarCuaderno:
				if(isCirculacion(expediente)){
					return true;
				}else{
					return isPermiteEstados(expediente) && isSubexpedienteActivo(expediente) && isSituacionEnOficinaYOficinaActual(expediente) && (!CamaraManager.isMesaEntradaActiva() || isRadicadoOficinaActual(expediente)) && !hasElementosPendientes(expediente) && isBorrable(expediente);
				}
			case CerrarCuaderno:
				return isPermiteEstados(expediente) && isSubexpedienteActivo(expediente) && isSituacionEnOficinaYOficinaActual(expediente) && !isSubexpedienteReal(expediente) && 
					(!CamaraManager.isMesaEntradaActiva() || ExpedienteManager.isCirculacion(expediente) || isRadicadoOficinaActual(expediente)) && !hasElementosPendientes(expediente) && !isElevacion(expediente);
			case ReabrirCuaderno:
				return isPermiteEstados(expediente) && (!isPrincipal(expediente) || isIncidente(expediente) || isLegajo(expediente)) && isCerrado(expediente) && (!CamaraManager.isMesaEntradaActiva() || isRadicadoOficinaActual(expediente));
			case CrearVinculado:
				return OficinaManager.instance().inAlgunaMesaDeEntrada() || isSituacionEnOficinaYOficinaActual(expediente);
			case RecalcularCaratula:
				return (!isIniciado(expediente) || isSituacionEnOficinaYOficinaActual(expediente)) && !isLegajoPersonalidad(expediente) && !CamaraManager.isMesaEntradaActiva();
			case ModificarDatos:
				return !CamaraManager.isMesaEntradaActiva() || (!isVerificadoMesaEntrada(expediente) && ConfiguracionMateriaManager.instance().isUsarCaratulaAntigua(expediente.getCamaraActual(), expediente.getMateria()));
			case ModificarDatosCabecera:
			case CambioTipoCausa:
			case CambioTipoProceso: 
				return (!isIniciado(expediente) || isSituacionEnOficinaORemision(expediente) || isEnPaseDesdeMesa(expediente)) && !isLegajoPersonalidad(expediente) && CamaraManager.isMesaEntradaActiva();
			case CambioTipoSubexpediente:
				return (!isIniciado(expediente) || isSituacionEnOficinaORemision(expediente)) && (isIncidente(expediente) || isCirculacion(expediente));
			case CrearRecurso:
				return (isPrincipal(expediente) || isIncidente(expediente) || isLegajo(expediente) || isElevacion(expediente) || isSubExpediente(expediente) || isRecursoQueja(expediente)) && isSituacionEnOficinaYOficinaActual(expediente) && expediente.isEnTramite() && isInPrimeraOMesa(expediente) && !OficinaManager.instance().isMesaEntradaCamaraCasacion(expediente.getOficinaActual());
			case CrearRecursoCasacion:
				return (isPrincipal(expediente) || isIncidente(expediente) || isLegajo(expediente) || isLegajoEjecucion(expediente) || isElevacion(expediente) || isRecursoQueja(expediente)) && isSituacionEnOficinaYOficinaActual(expediente) && isPenal(expediente) && expediente.isEnTramite() && (isInPrimeraOApelacionesOTribunalOral(expediente) || OficinaManager.instance().isMesaEntradaCamaraCasacion(expediente.getOficinaActual()) || OficinaManager.instance().isSecretariaEspecial(expediente.getOficinaActual()) || OficinaManager.instance().isMesaEntradaCamara(expediente.getOficinaActual()));
			case CrearRecursoCorteSuprema:
			case CrearRecursoHonorariosCorteSuprema:
				return (isPrincipal(expediente) || isIncidente(expediente) || isLegajo(expediente) || isElevacion(expediente) || isSubExpediente(expediente) || isRecursoQueja(expediente)) && isSituacionEnOficinaYOficinaActual(expediente) && expediente.isEnTramite();
			case CrearElevacion:
				return (isPrincipal(expediente) && isSituacionEnOficinaYOficinaActual(expediente) && isPenal(expediente) && getOficinaManager().isPrimeraInstancia(expediente.getOficinaActual()))
					||
					(SubexpedienteManager.instance().isElevacion(expediente) && isSituacionEnOficinaYOficinaActual(expediente) && isPenal(expediente) )
					;
			case MovimientoExpediente:
				return isPermiteEstados(expediente) && 
				(isDespacho(expediente) || 
					isLetra(expediente) || 
					isConfronte(expediente) || 
					isSentencia(expediente)) && 
				isRecibidoOficinaActual(expediente) &&
				isPermiteMovimientos(expediente);
			case EventoExpediente:
				return isPermiteEstados(expediente) && 
					(isDespacho(expediente) || 
						isLetra(expediente) || 
						isConfronte(expediente) || 
						isSentencia(expediente)) && 
					isRecibidoOficinaActual(expediente) &&
					isPermiteEventos(expediente);
			case CambioSecretaria:
				return isSituacionEnOficinaYOficinaActual(expediente) && getOficinaManager().puedeAsignarSecretaria(expediente.getOficinaActual());
			case CambioEstadoInterviniente:
				return isLegajoPersonalidad(expediente);
			case TraerExpediente:
				return !isOficinaActual(expediente) && getIdentity().hasPermission("Expediente", "traerExpediente") && (!isMediacion(expediente) || OficinaManager.instance().isMesaEntradaCamaraOficinaActual());
			case TraerExpedientePrincipal:				
				{
					Expediente principal = getExpedienteBase(getEntityManager(), expediente);
					return principal != null && principal != expediente && isCirculacion(expediente) && !isOficinaActual(principal);					
				}
			case TraerExpedientesHijasMadre:
				return expediente.isAcumuladoJuridicoMadre();
			case GenerarRemisionExpedientesHijasMadre:
				return !isOficinaActual(expediente) && expediente.isAcumuladoJuridicoMadre();
			case AutoRadicarExpediente:
				return isOficinaActual(expediente) && !isRadicadoOficinaActual(expediente) && getIdentity().hasPermission("Expediente", "autoRadicarExpediente") && !isMediacion(expediente);
			case ModificarDenuncia:
				return isPenal(expediente) && isDenuncia(expediente);
			case AsignarInstructor:
				return !CamaraManager.isCamaraActualCorteSuprema() && getOficinaManager().isPenal(expediente.getOficinaActual()) && isPenal(expediente) && isSituacionEnOficinaYOficinaActual(expediente);
			case ImprimirMinutaElevacionTO:
				return isPenal(expediente) && isElevacion(expediente);
			case CancelaElevacionTO:
				return isPenal(expediente) && isElevacion(expediente) && (getRadicacionByTipoRadicacion(expediente, TipoRadicacionEnumeration.Type.tribunalOral) == null) && (getRadicacionByTipoRadicacion(expediente, TipoRadicacionEnumeration.Type.juzgadoPlenario) == null);
			case CambioElevacion:
				return isPenal(expediente) && isElevacion(expediente) && 
				(
						(isNaturalezaTribunalOral(expediente) && (expediente.getRadicacionTribunalOral() != null))
						||
						(isNaturalezaJuzgadoPlenario(expediente) && (expediente.getRadicacionJuzgadoPlenario() != null))
				);
			case BorrarExpediente:
				return true;
			case ValidarLIP:
				return isLIPValidable(expediente) && LegajoPersonalidadExpManager.instance().isEditable();
			case AgregarLIP:
				return isLIPValidado(expediente) && LegajoPersonalidadExpManager.instance().isEditable();
			case CambioOrdenIntervinientes:
				return getOficinaManager().inAlgunaMesaDeEntrada() || (isRadicadoOficinaActual(expediente) && isSituacionEnOficinaYOficinaActual(expediente));
			case ReasignarLetrado:
				return this.isEditable();
			case RehabilitacionPlazoVencido:
				return (isMediacion(expediente) && !CamaraManager.isCamaraActualComercial()) || isPlazoInicioVencido(expediente);
			case FusionarInterviniente:
				return isSituacionEnOficinaYOficinaActual(expediente);
			case DejarNota:
				return true;
			default:
				break;
			}
		}
		return false;
	}

	private boolean isInPrimeraOApelacionesOTribunalOral(Expediente expediente) {
		return (OficinaManager.instance().isPrimeraInstancia(expediente.getOficinaActual()) || OficinaManager.instance().isApelaciones(expediente.getOficinaActual()) || OficinaManager.instance().isTipoInstanciaTribunalOral(expediente.getOficinaActual()));
	}

	public boolean isInPrimeraOMesa(Expediente expediente) {
		return (OficinaManager.instance().isPrimeraInstancia(expediente.getOficinaActual()) || OficinaManager.instance().isMesaDeEntrada(expediente.getOficinaActual()));
	}

	public boolean isInPrimeraOMesaOCorte(Expediente expediente) {
		return (OficinaManager.instance().isPrimeraInstancia(expediente.getOficinaActual()) 
				|| OficinaManager.instance().isMesaDeEntrada(expediente.getOficinaActual())
				|| OficinaManager.instance().isCorteSuprema(expediente.getOficinaActual())
				);
	}

	private boolean isLIPValidable(Expediente expediente) {
		return isLIP(expediente) && LegajoPersonalidadExpManager.instance().getCabeceraLIPSelected() != null && LegajoPersonalidadExpManager.instance().getCabeceraLIPSelected().isValidable()
			&& !LegajoPersonalidadExpManager.instance().getCabeceraLIPSelected().isValidado();
	}

	private boolean isLIPValidado(Expediente expediente) {
		return isLIP(expediente) && LegajoPersonalidadExpManager.instance().getCabeceraLIPSelected() != null && LegajoPersonalidadExpManager.instance().getCabeceraLIPSelected().isValidado();
	}
	
	private boolean hasElementosPendientes(Expediente expediente) {
		boolean result = false;
		if (expediente.isTieneRecursosAsignadosPteResolver()) {
			result = true;
		} else if (isElevacion(expediente) && 
				(expediente.getRadicacionJuzgadoPlenario() != null ||
				 expediente.getRadicacionTribunalOral() != null)) {
			result = true;
		} else {
			for (Expediente subExpediente: expediente.getExpedienteList()) {
				if (isSubexpedienteActivo(subExpediente)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	private boolean isElevacion(Expediente expediente) {
		if (expediente.getTipoSubexpediente() != null && 
				(expediente.getTipoSubexpediente().getNaturalezaSubexpediente().equals(NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue()) ||
				 expediente.getTipoSubexpediente().getNaturalezaSubexpediente().equals(NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue()))) {
			return true;
		}
		return false;
	}

	private boolean isPermiteMovimientos(Expediente expediente) {
		if (isCivilComFederal(expediente) || 
			isContenciosoAdmFederal(expediente) || 
			ConfiguracionMateriaManager.instance().isAplicarMovimientos(expediente.getOficinaActual().getCamara(), expediente.getMateria())) {
			return true;				
		} else {
			return false;
		}
	}
	
	private boolean isPermiteEventos(Expediente expediente) {
		if (ConfiguracionMateriaManager.instance().isAplicarEventos(expediente.getOficinaActual().getCamara(), expediente.getMateria())) {
			return true;				
		} else {
			return false;
		}
	}
	
	private boolean isPermiteCambioSituacion(Expediente expediente) {
		if (!getOficinaManager().isCivilComercialFederal(expediente.getOficinaActual()) && 
			!getOficinaManager().isContenciosoAdministrativoFederal(expediente.getOficinaActual()) && 
			!getOficinaManager().isPenal(expediente.getOficinaActual()) && 
			!ConfiguracionMateriaManager.instance().isAplicarMovimientos(expediente.getOficinaActual().getCamara(), expediente.getMateria())) {
			return true;
		} else {
			return false;
		}
		
	}
	
	private boolean isPermiteReservar(Expediente expediente) {
		Camara camara = expediente.getCamara();
		if (camara == null && expediente.getOficinaInicial() != null) {
			camara = expediente.getOficinaInicial().getCamara();
		}
		return camara != null; // Se quita esta condicion a peticion de la comision el 09/05/2014 && !CamaraManager.isCamaraPenalFederal(camara);
	}

	private boolean isPermiteEstados(Expediente expediente) {
		if (expediente != null) {
			return expediente.isPermiteEstados();
		}
		return  false;
	}

	private boolean isSubexpedienteNoActivo(Expediente expediente) {
		return !isPrincipal(expediente) && (SituacionExpedienteEnumeration.Type.cuaderno_cerrado.getValue().equals(expediente.getSituacion()) || SituacionExpedienteEnumeration.Type.cuaderno_cancelado.getValue().equals(expediente.getSituacion())) ;	
	}


	private boolean isSubexpedienteActivo(Expediente expediente) {
		return !isPrincipal(expediente) && !isLegajoPersonalidad(expediente) && !isSubexpedienteNoActivo(expediente) ;
	}
	
	private boolean isSubexpedienteReal(Expediente expediente) {
		return expediente.getTipoSubexpediente() != null && NaturalezaSubexpedienteEnumeration.Type.subexpediente.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}

	public boolean isExpedienteConsulta(Expediente expediente) {
		ExpedienteList expedienteList = (ExpedienteList) Component.getInstance(ExpedienteList.class, true);
		return expediente != null && !expedienteList.isEditable(expediente);
	}
	
	public boolean isReservadoTotal(){
		return isReservadoTotal(getExpedienteHome().getInstance());
	}

	public boolean isReservadoTotal(Expediente expediente) {
    	return expediente.getReservado().equals((Integer)TipoRestriccionEnumeration.Type.total.getValue());
    }
	
	public boolean isReservadoWeb(){
		return isReservadoWeb(getExpedienteHome().getInstance());
	}
	
	public boolean isReservadoWeb(Expediente expediente) {
    	return expediente.getReservado().equals((Integer)TipoRestriccionEnumeration.Type.web.getValue());
    }
	
	public boolean tieneReserva(Expediente expediente){
		return (isReservadoTotal(expediente) || isReservadoWeb(expediente) || isReservadoVisualizacionPartes(expediente));
	}
	
		
	public boolean isHabilitadoReservaPartes() {
		Expediente expediente = getExpedienteHome().getInstance();
		return (isReservadoVisualizacionPartes(expediente) ||  ( ((isPenal(expediente) || isExpedienteFamilia(expediente))) && (isReservadoWeb(expediente) || sinReserva(expediente)) ) ) ;
    }
	
	public boolean isReservadoVisualizacionPartes() {
    	return isReservadoVisualizacionPartes(getExpedienteHome().getInstance());
    }
	
	public boolean isReservadoVisualizacionPartes(Expediente expediente) {
    	return expediente.getReservado().equals((Integer)TipoRestriccionEnumeration.Type.visualizacionSoloPartes.getValue());
    }


	public boolean sinReserva(Expediente expediente) {
    	return expediente.getReservado().equals((Integer)TipoRestriccionEnumeration.Type.sinRestriccion.getValue());
    }

	private boolean isRestringido(Expediente expediente) {
		return 	(expediente.getObjetoJuicioPrincipal() != null) && expediente.getObjetoJuicioPrincipal().isRestringido();
	}

	public boolean isParalizado(Expediente expediente) {
		return SituacionExpedienteEnumeration.Type.paralizado.getValue().equals(expediente.getSituacion());
	}

	public boolean isArchivado(Expediente expediente) {
		return SituacionExpedienteEnumeration.Type.archivese.getValue().equals(expediente.getSituacion());
	}

	private boolean isVista(Expediente expediente) {
		return 	SituacionExpedienteEnumeration.Type.vista.getValue().equals(expediente.getSituacion());
	}

	private boolean isVistaOficinaActual(Expediente expediente) {
		return isVista(expediente) && isRecibidoOficinaActual(expediente);
	}

	private boolean isParalizadoOficinaActual(Expediente expediente) {
		return isParalizado(expediente) && isRecibidoOficinaActual(expediente) ;
	}

	private boolean isArchivadoOficinaActual(Expediente expediente) {
		return isArchivado(expediente) && isRecibidoOficinaActual(expediente) ;
	}

	private boolean isResponsable(Expediente expediente) {
		return !Strings.isEmpty(expediente.getResponsable()) && expediente.getResponsable().equalsIgnoreCase(getSessionState().getUsername());
	}

	private boolean hasResponsable(Expediente expediente){
		return getResponsable(expediente) != null;
	}

	private PersonalOficina getResponsable(Expediente expediente){
		if ((expediente != null) && (expediente.getResponsable() != null)) {
			return PersonalOficinaEnumeration.instance().getItemByCuil(expediente.getResponsable());
		}
		return null;
	}

	public boolean isOficinaActual(Oficina oficina) {
		return oficina != null && getSessionState().getGlobalIdOficinaSet().contains(oficina.getId());
	}

	public boolean isCurrentOficina(Oficina oficina) {
		return oficina != null && getSessionState().getGlobalOficina() != null && oficina.getId().equals(getSessionState().getGlobalOficina().getId());
	}
	
	public boolean isOficinaActual(Expediente expediente, Oficina oficina) {
		return isOficinaActual(expediente) && isCurrentOficina(oficina);
	}
	
	public boolean isOficinaActual(Expediente expediente) {
		return expediente != null && expediente.getOficinaActual() != null && getSessionState().getGlobalIdOficinaSet().contains(expediente.getOficinaActual().getId());
	}

	public boolean isEnPaseDesdeOficinaActual(Expediente expediente){
		return isPase(expediente) && expediente != null && expediente.getOficinaRetorno() != null && getSessionState().getGlobalIdOficinaSet().contains(expediente.getOficinaRetorno().getId());
	}
	
	private boolean isRecibidoOficinaActual(Expediente expediente) {
		return isOficinaActual(expediente) && expediente != null && !expediente.isPendienteRecepcion();
	}

	public boolean isMarcadoGris(Expediente expediente) {
		return !isOficinaActual(expediente) || isPase(expediente);
	}
	
	public String rowStyleClass(Expediente expediente){
		String ret = "";
		
		if(expediente == null){
			return ret;
		}
		
		if(isReservadoTotal(expediente) || hasMedidasCautelares(expediente)){
			ret = "rowBck3";
		}else if(isMarcadoGris(expediente)){
			ret = "rowBck2";
		}else if(isRemision(expediente)){
			ret = "rowBck6";
		}else if(isCirculacion(expediente)){
			ret = "rowBck7";
		}
		
		if(isDetenidos(expediente)){
			ret += " rowColor1";
		}
		return ret;
	}	
	
	public String situacionStyleClass(Expediente expediente){
		String ret = "";
		if(isCirculacion(expediente)){
			ret = "rowBck7";
		}		
		return ret;		
	}
	
	public String intervinienteRowStyleClass(IntervinienteExp intervinienteExp){
		String ret = "";
		
		if(intervinienteExp.isRestriccionWeb() && intervinienteExp.isDetenido()) {
			return ret = "rowColorWebDetenido";
		}
		
		if(intervinienteExp.isRestriccionWeb()){
			return ret= "rowColorWeb";
		}
		
		if(intervinienteExp.isDetenido()){
			return ret = "rowColor1";
		} else if (IntervinienteExpManager.instance().isPendienteVerificar(intervinienteExp) ||
				intervinienteExp.isNoSaleEnCaratula()) {
			return ret = "rowColor1a";
		}
		return ret;
	}
	
	public static boolean isPase(Expediente expediente) {
		return expediente.isPase();	
	}

	public boolean isRadicadoOficinaActual(Expediente expediente) {
		return expediente.getRadicadoEnOficinaActual();
	}

	public boolean isRadicacionPreviaOficinaActual(Expediente expediente) {
		for (Integer idOficina : expediente.getOficinaRadicacionPreviaIdSet()) {
			if (getSessionState().getGlobalIdOficinaSet().contains(idOficina)) {
				return true;
			}
		}
		return false;
	}
	
	public String getDescripcionOficinaRemitentePase(Expediente expediente){
		PaseExp pasePendiente = buscarPasePendiente(expediente);
		String ret = null;
		if(pasePendiente != null){
			if(pasePendiente.getOficina() != null){
				ret = pasePendiente.getOficinaRetorno() != null ? pasePendiente.getOficinaRetorno().getDescripcion() : null;
			}else{
				if(pasePendiente.getLocalizacion() != null){
					ret = pasePendiente.getLocalizacion(); 
				}else if(pasePendiente.getDependencia() != null){
					ret = pasePendiente.getDependencia().getDescripcion();
				}
			}
		}
		return ret;
	}

	public boolean isPaseOficinaDestino(Expediente expediente) {
		Oficina oficinaPase = getOficinaPase(expediente);
		if (getSessionState().getGlobalOficina() == null) {
			return false;
		}else{
			boolean ret = isPase(expediente) && oficinaPase != null 
				&& oficinaPase.getOficinasAsociadasIdSet().contains(getSessionState().getGlobalOficina().getId());
			if(ret){
				// Si la oficina que envia el pase es Asociada -> solo Se muestra como receptoras la oficina del Paso
				if(expediente.getOficinaRetorno() != null && oficinaPase.getOficinasAsociadasIdSet().contains(expediente.getOficinaRetorno().getId())){
					ret = oficinaPase.equals(getSessionState().getGlobalOficina());
				}
			}
			return ret;
		}
	}
	
	public Oficina getOficinaPase(Expediente expediente) {
		PaseExp pasePendiente = buscarPasePendiente(expediente);
		Oficina oficinaPase = null;
		if (pasePendiente != null) {
			oficinaPase = pasePendiente.getOficina();
		}
		return oficinaPase;
	}

	public Oficina getOficinaRetornoPase(Expediente expediente) {
		//return expediente.getOficinaRetorno();
		PaseExp pasePendiente = buscarPasePendiente(expediente);
		Oficina oficinaPase = null;
		if (pasePendiente != null) {
			oficinaPase = pasePendiente.getOficinaRetorno();
		}
		return oficinaPase;
	}
	
	public boolean isPrestamo(Expediente expediente) {
		return SituacionExpedienteEnumeration.Type.prestamo.getValue().equals(expediente.getSituacion());
	}
	
	public String getPrestamoInfo(Expediente expediente){
		String ret = null;
		if(isPrestamo(expediente)){
			ExpedienteHomeListener expedienteHomeListener = (ExpedienteHomeListener) Component.getInstance(ExpedienteHomeListener.class, true);
			PrestamoExp prestamoExp = expedienteHomeListener.getCurrentPrestamo();
			if(prestamoExp != null){
				ret = expedienteHomeListener.getCurrentPrestamoInfo();
			}
		}
		return ret;
	}
	public boolean isLetra() {
		Expediente expediente = getExpedienteHome().getInstance();
		return SituacionExpedienteEnumeration.Type.en_Letra.getValue().equals(expediente.getSituacion());
	}

	public boolean isLetra(Expediente expediente) {
		return SituacionExpedienteEnumeration.Type.en_Letra.getValue().equals(expediente.getSituacion());
	}

	private boolean isLetraOficinaActual(Expediente expediente) {
		return SituacionExpedienteEnumeration.Type.en_Letra.getValue().equals(expediente.getSituacion()) && isOficinaActual(expediente);
	}
	
	private boolean isConfronte(Expediente expediente) {
		return SituacionExpedienteEnumeration.Type.confronte.getValue().equals(expediente.getSituacion());
	}

	private boolean isConfronteOficinaActual(Expediente expediente) {
		return SituacionExpedienteEnumeration.Type.confronte.getValue().equals(expediente.getSituacion()) && isOficinaActual(expediente);
	}
	
	public boolean isSituacionEnOficinaORemision(Expediente expediente) {
		return isSituacionEnOficinaYOficinaActual(expediente) || isRemisionEnOficinaActual(expediente);
	}
	
	private boolean isRemisionEnOficinaActual(Expediente expediente) {
		return SituacionExpedienteEnumeration.Type.remision.getValue().equals(expediente.getSituacion()) && isOficinaActual(expediente);
	}
	
	public static boolean isRemision(Expediente expediente) {
		return SituacionExpedienteEnumeration.Type.remision.getValue().equals(expediente.getSituacion());
	}
	
	public boolean isDespacho(Expediente expediente) {
		return SituacionExpedienteEnumeration.Type.despacho.getValue().equals(expediente.getSituacion()) && isOficinaActual(expediente);
	}
	/** ATOS DEO */
	public boolean isSituacionEnOficinaYOficinaActual(Expediente expediente) {
		return (SituacionExpedienteEnumeration.Type.despacho.getValue().equals(expediente.getSituacion()) ||
				SituacionExpedienteEnumeration.Type.en_Letra.getValue().equals(expediente.getSituacion()) ||
				SituacionExpedienteEnumeration.Type.confronte.getValue().equals(expediente.getSituacion()) ||
				SituacionExpedienteEnumeration.Type.sentencia.getValue().equals(expediente.getSituacion()))
				&& isOficinaActual(expediente) ;
	}
	/** ATOS DEO */
	private boolean isSentencia(Expediente expediente) {
		return SituacionExpedienteEnumeration.Type.sentencia.getValue().equals(expediente.getSituacion());
	}

	private boolean isSentenciaOficinaActual(Expediente expediente) {
		return SituacionExpedienteEnumeration.Type.sentencia.getValue().equals(expediente.getSituacion()) && isOficinaActual(expediente);
	}
	
	public static  boolean isExpedienteBase(Expediente expediente) {
		return isPrincipal(expediente) || isPlenario(expediente) || isTribunalOral(expediente);
	}

	public static  boolean isPrincipal(Expediente expediente) {
		return expediente.getTipoSubexpediente() == null 
		|| NaturalezaSubexpedienteEnumeration.Type.principal.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente())
		|| NaturalezaSubexpedienteEnumeration.Type.mediacion.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}

	public static boolean esNaturalezaPrincipal(Expediente expediente) {
		return NaturalezaSubexpedienteEnumeration.Type.principal.getValue().equals(expediente.getNaturaleza());
	}

	
	public static  boolean isPlenario(Expediente expediente) {
		return expediente.getTipoSubexpediente() != null && NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}

	public static  boolean isTribunalOral(Expediente expediente) {
		return expediente.getTipoSubexpediente() != null && NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}

	public static  boolean isIncidente(Expediente expediente) {
		return expediente.getTipoSubexpediente() != null && NaturalezaSubexpedienteEnumeration.Type.incidente.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}
	
	public static boolean isLegajo(Expediente expediente) {
		return expediente.getTipoSubexpediente() != null && NaturalezaSubexpedienteEnumeration.Type.legajo.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}

	public static boolean isLegajoEjecucion(Expediente expediente) {
		return expediente.getTipoSubexpediente() != null && NaturalezaSubexpedienteEnumeration.Type.legajoEjecucion.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}

	public static boolean isLegajoSecretariaEspecial(Expediente expediente) {
		return expediente.getTipoSubexpediente() != null && NaturalezaSubexpedienteEnumeration.Type.legajoSecretariaEspecial.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}

	public static boolean isSubExpediente(Expediente expediente) {
		return expediente.getTipoSubexpediente() != null && NaturalezaSubexpedienteEnumeration.Type.subexpediente.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}

	public static boolean isRecursoQueja(Expediente expediente) {
		return expediente.getTipoSubexpediente() != null && NaturalezaSubexpedienteEnumeration.Type.recursoQueja.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}
	
	public static boolean isLIP(Expediente expediente) {
		return expediente.getTipoSubexpediente() != null && NaturalezaSubexpedienteEnumeration.Type.legajoPersonalidad.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}
	 
	public static  boolean isEscrito(Expediente expediente) {
		return expediente.getTipoSubexpediente() != null && NaturalezaSubexpedienteEnumeration.Type.escrito.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}

	public static  boolean isCirculacion(Expediente expediente) {
		return expediente.getTipoSubexpediente() != null && NaturalezaSubexpedienteEnumeration.Type.circulacion.getValue().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente());
	}

	public static boolean isCerrado(Expediente expediente) {
		return expediente.getTipoSubexpediente() == null || SituacionExpedienteEnumeration.Type.cuaderno_cerrado.getValue().equals(expediente.getSituacion());
	}

	public boolean isIniciado(Expediente expediente) {
		if (!isPermiteEstados(expediente)) {
			return true;
		} else if (expediente.getEstadoExpediente() != null) {
			return !expediente.getEstadoExpediente().getCodigo().equals(EstadoExpedientePredefinedEnumeration.Type.$SI.getValue());
		}
		return false;
	}

	public boolean isRecepcionado(Expediente expediente) {
		return ActuacionExpSearch.findByTipoInformacion(getEntityManager(), expediente, TipoInformacionEnumeration.RECIBIDO_MESA_RECEPTORA) != null;
	}


	public boolean isEditableProyectos() {
		Expediente expediente = getExpedienteHome().getInstance();
		return (getExpedienteHome().isManaged() && isIniciado(expediente)) 
				&& !isSubexpedienteNoActivo(expediente)
				&& !isAcumulado(expediente)
				&& (isEditableOficinaActual() || isRadicadoOficinaActual(expediente))
				&& (!isLIP(expediente) || LegajoPersonalidadExpManager.instance().isEditable())
//				&& (!isCirculacion(expediente) || (getResponsableExpediente(expediente) != null))
				;
	}

	public boolean isEditableInfoAdicional() {
		Expediente expediente = getExpedienteHome().getInstance();
		return (getExpedienteHome().isManaged() && isIniciado(expediente)) 
				&& !isSubexpedienteNoActivo(expediente)
				&& !isAcumulado(expediente)
				&& (isEditableOficinaActual() || isRadicadoOficinaActual(expediente))
				&& (!isLIP(expediente) || LegajoPersonalidadExpManager.instance().isEditable())
				;
	}
	
	public String getExpedienteTitleStyleClass(){
		Expediente expediente = getExpedienteHome().getInstance();
		if(!isEditable()){
			return "disabled";
		}else if(isRemisionEnOficinaActual(expediente)){
			return "warning";
		}else{
			return "";
		}
	}
	
	public boolean isEditableOficinaActual() {		
		Expediente expediente = getExpedienteHome().getInstance();
		return expediente.getEditableEnOficinaActual();
	}
	
	public boolean isEditableOEnPaseDesdeMesa() {
		Expediente expediente = getExpedienteHome().getInstance();
		return (isOficinaActual(expediente) || isEnPaseDesdeMesa(expediente)) 
				&& isEditable();
	}
	
	
	private boolean isEnPaseDesdeMesa(Expediente expediente) {
		return OficinaManager.instance().inAnyMesaEntrada() && isEnPaseDesdeOficinaActual(expediente); 
	}
	
	public boolean isEditable() {
		Expediente expediente = getExpedienteHome().getInstance();
		return isEditable(expediente, getExpedienteHome().isManaged(), true);
	}

	public boolean isEditable(Expediente expediente) {
		return isEditable(expediente, true);
	}
	
	public boolean isEditable(Expediente expediente, boolean checkDespachoTramite) {
		return isEditable(expediente, true, checkDespachoTramite);
	}
	
	private boolean isEditable(Expediente expediente, boolean managed, boolean checkDespachoOrRemision) {
		return (
					!managed
					|| (isIniciadoEnSituacionEnOficinaORemision(expediente, checkDespachoOrRemision))
					|| (!isIniciado(expediente) && getIdentity().hasPermission("Expediente", "editarIntervinientesSinIniciar"))
					|| (getIdentity().hasPermission("Expediente", "manejoRemoto"))
				)
				&& !isSubexpedienteNoActivo(expediente)
				&& !isLegajoPersonalidad(expediente)
				&& !isAcumulado(expediente)
				&&  isContentsVisible(expediente)
				&& expediente.isEnTramite();
		
	}
	
	/**
	 * Se verifica si un expediente esta iniciado y segun parametro 2 tambien se evalua si esta en despacho o remision
	 * @param expediente {@link Expediente} a evaluar
	 * @param checkDespacho condiciona si se verifica que el expediente este en despacho o remision
	 * @return <code>true</code> en caso que cumpla con todas la condiciones o <code>false</code> caso contrario
	 */
	private boolean isIniciadoEnSituacionEnOficinaORemision(Expediente expediente, boolean checkDespacho){
		boolean result = isIniciado(expediente);
		if (result && checkDespacho)
			result = isSituacionEnOficinaORemision(expediente);
		return result;
	}
	
	public boolean isBorrable() {
		return isBorrable(getExpedienteHome().getInstance());
	}

	public boolean isBorrable(Expediente expediente) {
		return (getExpedienteHome().isManaged(expediente)
				&& isOficinaActual(expediente)
				&& (
						!isIniciado(expediente) 
						|| (
							 isDespacho(expediente)
							  && expediente.isBorrable() 
						    )
					)
				) 
				&& (!isPenal(expediente) || isSubexpedienteActivo(expediente))
				; 
	}

	public boolean isContentsVisible(Expediente expediente){
		boolean ret = true;
		if(isReservadoTotal(expediente)){
			ret = getIdentity().hasPermission("Expediente", "verReservados");
		}
		if (isRestringido(expediente)){
			ret = getIdentity().hasPermission("Expediente", "verRestringidos") && (isRadicadoOficinaActual(expediente) || getOficinaManager().inAlgunaMesaDeEntrada() || CamaraManager.isCamaraActualCorteSuprema()) ;
		}
		if (ret && isCirculacion(expediente)) {
			ret = isVisibleCarpetaCirculacion (expediente.getRecursoExp());
		}
		return ret;
	}
	public boolean isVisibleCarpetaCirculacion (RecursoExp recursoExp) {
		boolean ret = false;
		if (recursoExp != null) {
			boolean isCirculacionExpediente = TipoRecursoEnumeration.instance().isTipoRecursoCirculacionExpediente(recursoExp.getTipoRecurso());
			if(CamaraManager.isCamaraActualCorteSuprema()){
				Oficina oficinaElevacion = recursoExp.getOficinaElevacion(); 
				if(inVocaliaDeCorte() || OficinaManager.instance().inCoherencia() || OficinaManager.instance().inCopias() || OficinaManager.instance().isHonorariosOficinaActual()) {
					// las vocalias tienen visibilidad en todos los expedientes aunque no los tengan ellas
					ret = isCirculacionExpediente || oficinaElevacion != null; 
				} else if (inSecretariaDeCorte()) {					
					if(oficinaElevacion != null){ // lo ve si es la oficina que tiene el recurso o la de radicacion
						ret = isOficinaInGlobalOficinaList(oficinaElevacion) || ExpedienteManager.instance().isRadicadoOficinaActual(recursoExp.getExpediente());
					}else if(isCirculacionExpediente){	// una circulacion de expediente, lo ve solo la que tiene radicacion en el expte. 					
						ret = ExpedienteManager.instance().isRadicadoOficinaActual(recursoExp.getExpediente());
					}
				}
			}else{
				return true;
			}
		}
		return ret;
	}
	
	public boolean isOficinaInGlobalOficinaList(Oficina oficina){
		boolean ret = false;
		if(oficina != null && oficina.getId() != null){
			Integer idOficina = oficina.getId();
			for (Oficina oficinaGlobal : SessionState.instance().getGlobalOficinaList()) {
				if(idOficina.equals(oficinaGlobal.getId())){
					ret = true;
					break;
				}
			}
		}
		return ret;		
	}
	
	public boolean isDenuncia(Expediente expediente){
		return TipoCausaEnumeration.isDenuncia(expediente.getTipoCausa());
	}
	
	public boolean isQuerella(Expediente expediente){
		return TipoCausaEnumeration.isQuerella(expediente.getTipoCausa());
	}
	
	public EstadoExpediente getCambioEstadoExpediente() {
		return cambioEstadoExpediente;
	}


	public void setCambioEstadoExpediente(EstadoExpediente cambioEstadoExpediente) {
		this.cambioEstadoExpediente = cambioEstadoExpediente;
	}



	public void redirectToExpediente(){
		try {
			getFacesContext().getExternalContext().redirect("/lex100pjn/web/expediente/expedienteCompleto.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String doParalizar(String legajo, Expediente expediente, DocumentoExp documentoExp) {
		try {
			
			expediente.setLegajoParalizado(legajo);
			// Estado Procesal = Paralizado
			EstadoExpediente estadoExpediente = getEstadoExpediente(EstadoExpedientePredefinedEnumeration.Type.PDO.getValue().toString(), expediente.getMateria());
			getExpedienteManager().cambiarEstadoExpediente(expediente, documentoExp, estadoExpediente, null, false);

			// Actuacion paralizar
			ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
			actuacionExpManager.addActuacionParalizarExpediente(expediente, documentoExp, legajo);

			// Situacion -> paralizado
			new AccionCambioSituacionExpediente().doAccion(getExpedienteHome().getInstance(), SituacionExpedienteEnumeration.Type.paralizado.getValue().toString(), true);
			
			return "";
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_paralizar_expediente");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return null;
		}
	}

	public String doDesparalizar(Expediente expediente, DocumentoExp documentoExp) {
		try {
			String legajo = expediente.getLegajoParalizado();
			
			// Estado Procesal = Sacado de paralizado
			EstadoExpediente estadoExpediente = getEstadoExpediente(EstadoExpedientePredefinedEnumeration.Type.SAC.getValue().toString(), expediente.getMateria());
			getExpedienteManager().cambiarEstadoExpediente(expediente, documentoExp, estadoExpediente, null, false);

			// Actuacion desparalizado
			ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
			actuacionExpManager.addActuacionDesparalizarExpediente(expediente, documentoExp, legajo);
			
			// Situacion despacho
			new AccionCambioSituacionExpediente().doAccion(getExpedienteHome().getInstance(), SituacionExpedienteEnumeration.Type.despacho.getValue().toString(), true);
			expediente.setLegajoParalizado(null);
			return "";
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_desparalizar_expediente");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return null;
		}
	}
	
	public String doArchivar(String legajo, Expediente expediente, DocumentoExp documentoExp) {
		try {
			expediente.setLegajoArchivo(legajo);
			expediente.setEnTramite(false);

			// Estado Procesal = Archivo
			EstadoExpediente estadoExpediente;
			
			if (isCivilComFederal(expediente)) {	// Hay conflito con el codigo ARE, en CCF Archivar es ARV
				estadoExpediente = getEstadoExpediente("ARV", expediente.getMateria());
			} else {
				estadoExpediente = getEstadoExpediente(EstadoExpedientePredefinedEnumeration.Type.ARE.getValue().toString(), expediente.getMateria());
			}
			getExpedienteManager().cambiarEstadoExpediente(expediente, documentoExp, estadoExpediente, null, false);

			// Situacion -> archivese
			new AccionCambioSituacionExpediente().doAccion(expediente, SituacionExpedienteEnumeration.Type.archivese.getValue().toString(), true);
			
			return "";
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_archivar_expediente");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return null;
		}
	}

	public String doDesarchivar(Expediente expediente, DocumentoExp documentoExp) {
		try {
			// validacion: si el expediente NO esta en Archivese o (esta en archivese y esta en un paquete) -> no deja desarchivar
			if(!expediente.isValidoParaDesarchivese()) {
				String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_desarchivar_expediente_no_valido");
				getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
				return null;
			}
			
//			String legajo = expediente.getLegajoArchivo();
			expediente.setEnTramite(true);
			
			// Estado Procesal = $SD (Sin Definir)
			EstadoExpediente estadoExpediente = getEstadoSinDefinir(expediente.getMateria());
			getExpedienteManager().cambiarEstadoExpediente(expediente, documentoExp, estadoExpediente, null, false);

			// Situacion despacho
			new AccionCambioSituacionExpediente().doAccion(getExpedienteHome().getInstance(), SituacionExpedienteEnumeration.Type.despacho.getValue().toString(), true);
//			expediente.setLegajoArchivo(null);
			return "";
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_desarchivar_expediente");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return null;
		}
	}

	public String doEmitirCedulas(){
		AccionManager accionManager = (AccionManager) Component.getInstance(AccionManager.class);
		getSelectorIntervinienteManager().clear();
		SelectorIntervinienteCedulasManager.instance().clear();
		getCedulaExpManager().setVincularLetrado(false);		
		accionManager.resetDataSheets();
		accionManager.loadAccionDataExtension("cedula", true);
		return "/webCustom/despacho/cedulas.xhtml";	
	}
	

	@Deprecated
	public String doRecepcionarCedulasLetrado(){
		AccionManager accionManager = (AccionManager) Component.getInstance(AccionManager.class);
		getSelectorIntervinienteManager().clear();
		accionManager.resetDataSheets();
		accionManager.loadAccionDataExtension("cedulaLetrado", false);
		return "/webCustom/despacho/cedulasLetrado.xhtml";
	}
	
	private boolean validarDatosCedulas() {
		boolean error = false;
		// Verion nueva
		List<CedulaInfo> cedulaInfoList = SelectorIntervinienteCedulasManager.instance().getCedulasInfoList();
		if(cedulaInfoList.size() == 0){			
			SelectorIntervinienteManager selectorIntervinienteManager = (SelectorIntervinienteManager) Component.getInstance(SelectorIntervinienteManager.class, true);		
			List<DomicilioSelected> domiciliosIntervinientes = selectorIntervinienteManager.getDomicilioSelectedList();
			if(domiciliosIntervinientes.size() == 0){
				
				getStatusMessages().addFromResourceBundle(Severity.ERROR, "despachoManager.errorSelectorDomicilioMultiple.empty");
				error = true;
			}
		/** ATOS NOTIFICACIONES */
		List<LetradoIntervinienteExp> letradosNoValidos = selectorIntervinienteManager.getLetradosNoValidos();
		
		if(letradosNoValidos.size() > 0){
			for(LetradoIntervinienteExp letradoIntervinienteExp : letradosNoValidos)
				getStatusMessages().addFromResourceBundle(Severity.ERROR, "despachoManager.errorSelectorDomicilioElectronico", letradoIntervinienteExp.getLetrado().getNombre());
			
			error = true;
		}
		/** ATOS NOTIFICACIONES */
		}
//		if(!error){
//			List<DocumentoExp> list = SelectorIntervinienteCedulasManager.instance().getDocumentoExpCheckedList();
//			if(list != null && list.size() > 1){
//				getStatusMessages().addFromResourceBundle(Severity.ERROR, "expedienteHomeListener.errorEmitiendoCedulas.seleccionar1despacho");
//				error = true;				
//			}
//		}
		return !error;
	}
	
	public String doFinalizaCedulas(){
		AccionManager accionManager = (AccionManager) Component.getInstance(AccionManager.class);
		String ret = "/web/expediente/expedienteCompleto.xhtml";
		try {
			if(!validarDatosCedulas()){
				ret = null;
			}else{
				//accionManager.prepareBorradorAccion("cedula", getExpedienteHome().getInstance());
				accionManager.executeAccion("cedula", getExpedienteHome().getInstance());
				getEntityManager().flush();
			}
		} catch (AccionException e) {
			getLog().error("Imposible emitir Cedulas", e);
			getStatusMessages().addFromResourceBundle(Severity.ERROR, "expedienteHomeListener.errorEmitiendoCedulas");
			ret = null;
		}
		return ret;
	}

	public String doFinalizaCedulasLetrado(){
		AccionManager accionManager = (AccionManager) Component.getInstance(AccionManager.class);
		String ret = "/web/expediente/expedienteCompleto.xhtml";
		try {
			if(!validarDatosCedulas()){
				ret = null;
			}else{
				//accionManager.prepareBorradorAccion("cedula", getExpedienteHome().getInstance());
				accionManager.executeAccion("cedulaLetrado", getExpedienteHome().getInstance());
				getEntityManager().flush();
			}
		} catch (AccionException e) {
			getLog().error("Imposible recepcionar Cedulas de Letrado", e);
			getStatusMessages().addFromResourceBundle(Severity.ERROR, "expedienteHomeListener.errorRecepcionandoCedulasLetrado");
			ret = null;
		}
		return ret;
	}

	
	public String doEmitirPrestamo(){
		AccionManager accionManager = (AccionManager) Component.getInstance(AccionManager.class);
		getSelectorIntervinienteManager().clear();
		accionManager.resetDataSheets();
		accionManager.loadAccionDataExtension("prestamo", true);
		return "/webCustom/despacho/prestamo.xhtml";
	}

	private boolean validarDatosPrestamo() {
		boolean error = false;

//		List<DomicilioSelected> list = getSelectorIntervinienteManager()
//				.getIntervinienteSelectedList();
//		if (list == null || list.size() != 1) {
//			error = true;
//			String msg = list == null || list.size() == 0 ? "despacho.seleccionInterviniente.error.interviniente.requerido"
//					: "despacho.seleccionInterviniente.error.interviniente.multiple";
//			getStatusMessages().addFromResourceBundle(
//					StatusMessage.Severity.ERROR, msg);
//		}
		return !error;
	}
		
		
	public String doFinalizaPrestamo(){
		return doFinalizaPrestamo(getExpedienteHome().getInstance());
	}
	
	
	public String doFinalizaPrestamo(Expediente expediente){
		AccionManager accionManager = (AccionManager) Component.getInstance(AccionManager.class);
		String ret = "/web/expediente/expedienteCompleto.xhtml";
		try {
			if(!validarDatosPrestamo()){
				ret = null;
			}else{
				//accionManager.prepareBorradorAccion("cedula", getExpedienteHome().getInstance());
				accionManager.executeAccion("prestamo", expediente);
				getEntityManager().flush();
			}
		} catch (AccionException e) {
			getLog().error("Imposible emitir Prestamo", e);
			getStatusMessages().addFromResourceBundle(Severity.ERROR, "expedienteHomeListener.errorEmitiendoCedulas");
			ret = null;
		}
		return ret;
	}
	
	public static org.dom4j.Document generateExpedienteXmlDataSource(Expediente expediente, Date currentDate, boolean testMode) {
		org.dom4j.Document document = DocumentFactory.getInstance().createDocument();
		Element rootElement = document.addElement(IXmlDataSourceTags.XML_DATA_SOURCE);
		Element row = rootElement.addElement(IXmlDataSourceTags.XML_ROW);
		String xmlDataSourceDateFormat = getXmlDataSourceDateFormat();
		DateFormat dateFormat = new SimpleDateFormat(xmlDataSourceDateFormat);
		updateXmlDataSource(expediente, currentDate, row, dateFormat, testMode);
		OficinaManager.updateXmlDataSource(expediente.getOficinaActual(), currentDate, row,
				dateFormat, testMode);

		return document;
	}


	public static void updateXmlDataSource(Expediente expediente,
			Date currentDate, Element row, DateFormat dateFormat, boolean testMode) {
		addTextElement(row, NUMERO_EXPEDIENTE_TAG, expediente.getNumero(), testMode);
		addTextElement(row, ANIO_EXPEDIENTE_TAG, expediente.getAnio(), testMode);
		addTextElement(row, NUMERO_SUBEXPEDIENTE_TAG, expediente.getCodigoSubexpediente(), testMode);
		addTextElement(row, INDICE_ANALITICO_TAG, ExpedienteManager.instance().getIdxAnaliticoFirst(expediente), testMode);
		addTextElement(row, CLAVE_TAG, expediente.getClave(), testMode);
		addTextElement(row, UUID_TAG, expediente.getUuid(), testMode);
		addTextElement(row, CARATULA_TAG, expediente.getCaratula(), testMode);
		addTextElement(row, SITUACION_TAG, SituacionExpedienteEnumeration.getLabelFromValue(expediente.getSituacion()), testMode);
		addTextElement(row, ETIQUETA_TAG, expediente.getEtiqueta(), testMode);
		addTextElement(row, RESPONSABLE_TAG, expediente.getResponsable(), testMode);
		addTextElement(row, FECHA_SORTEO_CARGA_TAG, expediente.getFechaSorteoCarga(), dateFormat, testMode);
		addTextElement(row, FECHA_INICIO_TAG, expediente.getFechaInicio(), dateFormat, testMode);
		addTextElement(row, FECHA_PASE_TAG, expediente.getFechaPase(), dateFormat, testMode);
		addTextElement(row, FECHA_SITUACION_TAG, expediente.getFechaSituacion(), dateFormat, testMode);
		addTextElement(row, FECHA_PRESTAMO_TAG, expediente.getFechaPrestamo(), dateFormat, testMode);
		addTextElement(row, FECHA_ESTADO_TAG, expediente.getFechaEstado(), dateFormat, testMode);
		addTextElement(row, FECHA_ARCHIVO_TAG, expediente.getFechaArchivo(), dateFormat, testMode);
		addTextElement(row, FOJAS_TAG, expediente.getFojas(), testMode);
		addTextElement(row, CUERPOS_TAG, expediente.getCuerpos(), testMode);
		addTextElement(row, PAQUETES_TAG, expediente.getPaquetes(), testMode);
		addTextElement(row, AGREGADOS_TAG, expediente.getAgregados(), testMode);
		addTextElement(row, MESES_PARALIZA_TAG, expediente.getMesesParaliza(), testMode);
		if (expediente.getMonto() != null) {
			addTextElement(row, MONTO_TAG, expediente.getMonto().toString(), testMode);
		} else {
			addTextElement(row, MONTO_TAG, "", testMode);
		}
		addTextElement(row, COMENTARIOS_TAG, expediente.getComentarios(), testMode);
		addTextElement(row, QUIEBRA_TAG, expediente.isQuiebra(), testMode);
		addTextElement(row, RESERVADO_TAG, expediente.getReservado(), testMode);
		addTextElement(row, LEGAJO_ARCHIVO_TAG, expediente.getLegajoArchivo(), testMode);
		addTextElement(row, LEGAJO_PARALIZADO_TAG, expediente.getLegajoParalizado(), testMode);
		if (expediente.getEstadoExpediente() != null) {
			addTextElement(row, CODIGO_ESTADO_EXPEDIENTE_TAG, expediente.getEstadoExpediente().getCodigo(), testMode);
			addTextElement(row, DESCRIPCION_ESTADO_EXPEDIENTE_TAG, expediente.getEstadoExpediente().getDescripcion(), testMode);
		}
		if (expediente.getOficinaActual() != null) {
			if (expediente.getOficinaActual().getCamara() != null) {
				addTextElement(row, DESCRIPCION_CAMARA_TAG, expediente.getOficinaActual().getCamara().getDescripcion(), testMode);
			}	
		}
		if (expediente.getMoneda() != null) {
			addTextElement(row, DESCRIPCION_MONEDA_TAG, expediente.getMoneda()
					.getDescripcion(), testMode);
		}
		String descripcionOficinaActual = null;
		if (SessionState.instance().getGlobalOficina() != null) {
			Oficina oficina = CamaraManager.getSecretaria(SessionState.instance().getGlobalOficina());
			if (oficina == null) {
				oficina = SessionState.instance().getGlobalOficina();
			}
			descripcionOficinaActual = oficina.getDescripcion();
		}
		addTextElement(row, OFICINA_ACTUAL_TAG, descripcionOficinaActual, testMode);
		if (expediente.getOficinaPase() != null) {
			addTextElement(row, OFICINA_PASE_TAG, expediente.getOficinaPase().getDescripcion(), testMode);
		}
		if (expediente.getOficinaRetorno() != null) {
			addTextElement(row, OFICINA_RETORNO_TAG, expediente.getOficinaRetorno().getDescripcion(), testMode);
		}
		if (expediente.getTipoProceso() != null) {
			addTextElement(row, TIPO_PROCESO_TAG, expediente.getTipoProceso().getDescripcion(), testMode);
		}
		if (expediente.getExpedienteOrigen() != null) {
			addTextElement(row, ANIO_EXPEDIENTE_ORIGEN_TAG, expediente.getExpedienteOrigen().getAnio(), testMode);
			addTextElement(row, NUMERO_EXPEDIENTE_ORIGEN_TAG, expediente.getExpedienteOrigen().getNumero(), testMode);
		}
		if (expediente.getTipoSubexpediente() != null) {
			addTextElement(row, TIPO_SUBEXPEDIENTE_TAG, expediente.getTipoSubexpediente().getDescripcion(), testMode);
			NaturalezaSubexpedienteEnumeration naturalezaSubexpedienteEnumeration = (NaturalezaSubexpedienteEnumeration) Component.getInstance(NaturalezaSubexpedienteEnumeration.class, true);
			addTextElement(row, NATURALEZA_SUBEXPEDIENTE_TAG, naturalezaSubexpedienteEnumeration.getLabel(expediente.getTipoSubexpediente().getNaturalezaSubexpediente()), testMode);
			addTextElement(row, PARTES_SUBEXPEDIENTE_TAG, expediente.getPartesSubexpediente(), testMode);
		}
		if (expediente.getExpedienteOrigen() != null) {
			addTextElement(row, CARATULA_PRINCIPAL_TAG, expediente.getExpedienteOrigen().getCaratula(), testMode);
		}
		
		if (!testMode && (expediente.isAcumuladoJuridicoMadre() || expediente.isAcumuladoJuridicoHija() || isCirculacion(expediente))) {
			addTextElement(row, CARATULAS_ACUMULADOS_TAG, ExpedienteManager.instance().getCaratulasAcumuladosJuridicos(expediente), testMode);
		} else {
			addTextElement(row, CARATULAS_ACUMULADOS_TAG, "", testMode);
		}

		String descripcionJuzgado = null;
		String domicilioJuzgado = null;
		String responsableJuzgado = null;
		String descripcionSecretaria = null;
		String domicilioSecretaria = null;
		String telefonoSecretaria = null;
		if (SessionState.instance().getGlobalOficina() != null) {
			Oficina juzgado = CamaraManager.getOficinaPrincipal(SessionState.instance().getGlobalOficina());
			Oficina secretaria = CamaraManager.getSecretaria(SessionState.instance().getGlobalOficina());
			if (juzgado != null) {
				descripcionJuzgado = juzgado.getDescripcion();
				domicilioJuzgado = juzgado.getDomicilio();
				responsableJuzgado = juzgado.getNombreResponsable();
			}
			if (secretaria != null) {
				descripcionSecretaria = secretaria.getDescripcion();
				domicilioSecretaria = secretaria.getDomicilio();
				telefonoSecretaria = secretaria.getDependencia().getTelefono();
			}
		}
		addTextElement(row, DESCRIPCION_JUZGADO_TAG, descripcionJuzgado, testMode);
		addTextElement(row, DOMICILIO_JUZGADO_TAG, domicilioJuzgado, testMode);
		addTextElement(row, RESPONSABLE_JUZGADO_TAG, responsableJuzgado, testMode);
		addTextElement(row, DESCRIPCION_SECRETARIA_TAG, descripcionSecretaria, testMode);
		addTextElement(row, DOMICILIO_SECRETARIA_TAG, domicilioSecretaria, testMode);
		addTextElement(row, TELEFONO_SECRETARIA_TAG, telefonoSecretaria, testMode);

		ExpedienteManager expManager = ExpedienteManager.instance().getExpedienteManager();

		if (expediente.getObjetoJuicioList()!=null)
			addTextElement(row, OBJETO_JUICIO, expManager.objetosJuicio(expediente), testMode);
		else
			addTextElement(row, OBJETO_JUICIO, "", testMode);
		
		if ((expediente.getIntervinienteExpList()!=null))
			addTextElement(row, ACTOR_PRINCIPAL, expManager.actoresPrincipal(expediente), testMode);
		else
			addTextElement(row, ACTOR_PRINCIPAL, "", testMode);
		
		addTextElement(row, DEMANDADOS_PRINCIPAL_TAG, expManager.demandadosExpedita(expediente).toString(), testMode);	
		
		// Ciudadanias
		IntervinienteExp intervinienteActor = expManager.actorCiudadania(expediente);
		addTextElement(row, NOMBRE_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor,"NOMBRE_ACTOR_TAG"), testMode);
		addTextElement(row, SEXO_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "SEXO_ACTOR_TAG"), testMode);
		addTextElement(row, ESTADO_CIVIL_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "ESTADO_CIVIL_ACTOR_TAG"), testMode);
		addTextElement(row, PROFESION_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "PROFESION_ACTOR_TAG"), testMode);
		addTextElement(row, TELEFONO_PARTICULAR_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "TELEFONO_PARTICULAR_ACTOR_TAG"), testMode);
		addTextElement(row, TELEFONO_LABORAL_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "TELEFONO_LABORAL_ACTOR_TAG"), testMode);
		addTextElement(row, CONYUGUE_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "CONYUGUE_ACTOR_TAG"), testMode);
		
		addTextElement(row, NACIONALIDAD_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "NACIONALIDAD_ACTOR_TAG"), testMode);
		addTextElement(row, FECHA_NACIMIENTO_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "FECHA_NACIMIENTO_ACTOR_TAG"), testMode);
		addTextElement(row, FECHA_ENTRADA_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "FECHA_ENTRADA_ACTOR_TAG"), testMode);
		addTextElement(row, PADRE_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "PADRE_ACTOR_TAG"), testMode);
		addTextElement(row, MADRE_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "MADRE_ACTOR_TAG"), testMode);
		addTextElement(row, DOMICILIO_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "DOMICILIO_ACTOR_TAG"), testMode);
		addTextElement(row, CIUDAD_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "CIUDAD_ACTOR_TAG"), testMode);
		addTextElement(row, REGION_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "REGION_ACTOR_TAG"), testMode);
		addTextElement(row, PROVINCIA_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "PROVINCIA_ACTOR_TAG"), testMode);
		addTextElement(row, PAIS_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "PAIS_ACTOR_TAG"), testMode);
		
		addTextElement(row, JUBILACION_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "JUBILACION_ACTOR_TAG"), testMode);
		addTextElement(row, OBRA_SOCIAL_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "OBRA_SOCIAL_ACTOR_TAG"), testMode);
		addTextElement(row, COMENTARIOS_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "COMENTARIOS_ACTOR_TAG"), testMode);
		addTextElement(row, OFICIO_LIBRADO_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "OFICIO_LIBRADO_ACTOR_TAG"), testMode);
		addTextElement(row, OFICIO_RECIBIDO_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "OFICIO_RECIBIDO_ACTOR_TAG"), testMode);
		addTextElement(row, OFICIO_COPIA_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "OFICIO_COPIA_ACTOR_TAG"), testMode);
		addTextElement(row, DNI_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "DNI_ACTOR_TAG"), testMode);
		addTextElement(row, DNI_ORIGEN_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "DNI_ORIGEN_ACTOR_TAG"), testMode);
		addTextElement(row, CI_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "CI_ACTOR_TAG"), testMode);
		addTextElement(row, CTL_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "CTL_ACTOR_TAG"), testMode);
		addTextElement(row, PASAPORTE_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "PASAPORTE_ACTOR_TAG"), testMode);
		addTextElement(row, TRANSPORTE_ENTRADA_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "TRANSPORTE_ENTRADA_ACTOR_TAG"), testMode);
		addTextElement(row, RADICACION_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "RADICACION_ACTOR_TAG"), testMode);
		addTextElement(row, AUTO_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "AUTO_ACTOR_TAG"), testMode);
		addTextElement(row, NUMERO_AUTO_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "NUMERO_AUTO_ACTOR_TAG"), testMode);
		addTextElement(row, FECHA_AUTO_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "FECHA_AUTO_ACTOR_TAG"), testMode);
		addTextElement(row, LUGAR_TRABAJO_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "LUGAR_TRABAJO_ACTOR_TAG"), testMode);
		addTextElement(row, DIARIO_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "DIARIO_ACTOR_TAG"), testMode);
		addTextElement(row, VIAJES_REALIZADOS_ACTOR_TAG, expManager.datosCiudadania(intervinienteActor, "VIAJES_REALIZADOS_ACTOR_TAG"), testMode);
	}
	
	private String getCaratulasAcumuladosJuridicos(Expediente expediente) {
		StringBuffer caratulasAcumulados = new StringBuffer();
		Expediente madre = null;
		if (isCirculacion(expediente) && expediente.getExpedienteOrigen() != null) {
			expediente = expediente.getExpedienteOrigen();
		}
		if (expediente.isAcumuladoJuridicoMadre()) {
			madre = expediente;
		} else if (expediente.isAcumuladoJuridicoHija()) {
			madre = expediente.getExpedienteAcumuladoJuridicoMadre();
		}
		if (madre != null) {
			caratulasAcumulados.append(getIdxAnaliticoFirst(madre)).append(" ");
			caratulasAcumulados.append(madre.getCaratula());
			for (Expediente hija: getAcumuladoJuridicoHijas(madre)) {
				caratulasAcumulados.append("; ");
				caratulasAcumulados.append(getIdxAnaliticoFirst(hija)).append(" ");
				caratulasAcumulados.append(hija.getCaratula());
			}
		}
		return caratulasAcumulados.toString();
	}

	public String datosCiudadania(IntervinienteExp interviniente,String valor)
	{
		if (interviniente!=null)
		{
			if (valor.equals("NOMBRE_ACTOR_TAG"))
				return interviniente.getInterviniente().getNombre();
			
			if(!interviniente.getIntervinientePersonalExpList().isEmpty())
			{
				IntervinientePersonalExp intervinientePersonalExp = interviniente.getIntervinientePersonalExpList().get(0);
				if (valor.equals("SEXO_ACTOR_TAG") && (intervinientePersonalExp.getSexo()!=null))
				{
					if (intervinientePersonalExp.getSexo().equals("M"))
						return "Masculino";
					else
						return "Femenino";
				}
				
				if ((valor.equals("ESTADO_CIVIL_ACTOR_TAG")) && (intervinientePersonalExp.getEstadoCivil()!=null) )
				{
					if (intervinientePersonalExp.getEstadoCivil().equals("C"))
						return "Casado";
					if (intervinientePersonalExp.getEstadoCivil().equals("S"))
						return "Soltero";
					if (intervinientePersonalExp.getEstadoCivil().equals("V"))
						return "Viudo";
					if (intervinientePersonalExp.getEstadoCivil().equals("D"))
						return "Divorciado-Separado";
				}
				
				if (valor.equals("PROFESION_ACTOR_TAG"))
					return intervinientePersonalExp.getProfesion();
				if (valor.equals("TELEFONO_PARTICULAR_ACTOR_TAG"))
					return intervinientePersonalExp.getTelefonoParticular();
				if (valor.equals("TELEFONO_LABORAL_ACTOR_TAG"))
					return intervinientePersonalExp.getTelefonoLaboral();
				if (valor.equals("CONYUGUE_ACTOR_TAG"))
					return intervinientePersonalExp.getCasadoCon();
				if (valor.equals("NACIONALIDAD_ACTOR_TAG"))
					return intervinientePersonalExp.getNacionalidad();
				if (valor.equals("FECHA_NACIMIENTO_ACTOR_TAG") && (intervinientePersonalExp.getFechaNacimiento()!=null))
					return intervinientePersonalExp.getFechaNacimiento().toString();
				if (valor.equals("FECHA_ENTRADA_ACTOR_TAG") && (intervinientePersonalExp.getFechaEntrada()!=null))
					return intervinientePersonalExp.getFechaEntrada().toString();
				if (valor.equals("PADRE_ACTOR_TAG"))
					return intervinientePersonalExp.getPadre();
				if (valor.equals("MADRE_ACTOR_TAG"))
					return intervinientePersonalExp.getMadre();
				if (valor.equals("DOMICILIO_ACTOR_TAG"))
					return intervinientePersonalExp.getDomicilio();
				if (valor.equals("CIUDAD_ACTOR_TAG"))
					return intervinientePersonalExp.getCiudad();
				if (valor.equals("REGION_ACTOR_TAG"))
					return intervinientePersonalExp.getRegion();
				if (valor.equals("PROVINCIA_ACTOR_TAG"))
					return intervinientePersonalExp.getProvincia();
				if (valor.equals("PAIS_ACTOR_TAG"))
					return intervinientePersonalExp.getPais();
				if (valor.equals("JUBILACION_ACTOR_TAG"))
					return intervinientePersonalExp.getJubilacion();
				if (valor.equals("OBRA_SOCIAL_ACTOR_TAG"))
					return intervinientePersonalExp.getObraSocial();
				if (valor.equals("COMENTARIOS_ACTOR_TAG"))
					return intervinientePersonalExp.getComentarios();
				if (valor.equals("OFICIO_LIBRADO_ACTOR_TAG"))
					return intervinientePersonalExp.getOficioLibrado();
				if (valor.equals("OFICIO_RECIBIDO_ACTOR_TAG"))
					return intervinientePersonalExp.getOficioRecibido();
				if (valor.equals("OFICIO_COPIA_ACTOR_TAG"))
					return intervinientePersonalExp.getOficioCopia();
				if (valor.equals("DNI_ACTOR_TAG"))
					return intervinientePersonalExp.getDni();
				if (valor.equals("DNI_ORIGEN_ACTOR_TAG"))
					return intervinientePersonalExp.getDniOrigen();
				if (valor.equals("CI_ACTOR_TAG"))
					return intervinientePersonalExp.getCi();
				if (valor.equals("CTL_ACTOR_TAG"))
					return intervinientePersonalExp.getCtl();
				if (valor.equals("PASAPORTE_ACTOR_TAG"))
					return intervinientePersonalExp.getPasaporte();
				if (valor.equals("TRANSPORTE_ENTRADA_ACTOR_TAG"))
					return intervinientePersonalExp.getTransporteEntrada();
				if (valor.equals("RADICACION_ACTOR_TAG"))
					return intervinientePersonalExp.getRadicacion();
				if (valor.equals("AUTO_ACTOR_TAG"))
					return intervinientePersonalExp.getAutor();
				if (valor.equals("NUMERO_AUTO_ACTOR_TAG"))
					return intervinientePersonalExp.getNumeroAuto();
				if (valor.equals("FECHA_AUTO_ACTOR_TAG"))
					return intervinientePersonalExp.getFechaAuto();
				if (valor.equals("LUGAR_TRABAJO_ACTOR_TAG"))
					return intervinientePersonalExp.getLugarTrabajo();
				if (valor.equals("DIARIO_ACTOR_TAG"))
					return intervinientePersonalExp.getDiario();
				if (valor.equals("VIAJES_REALIZADOS_ACTOR_TAG"))
					return intervinientePersonalExp.getViajesRealizados();
			}
		}
		return null;
	}
	
	public IntervinienteExp actorCiudadania(Expediente expediente){
		IntervinienteExp interviniente = new IntervinienteExp();
		for (IntervinienteExp intervinienteExp: expediente.getIntervinienteExpList()) {
			if(!intervinienteExp.isLogicalDeleted() && isActor(intervinienteExp)){
				interviniente = intervinienteExp;
				return interviniente;
			}
		}
		return null;
	}

	private boolean isActor(IntervinienteExp intervinienteExp) {
		return !(intervinienteExp.isLogicalDeleted()) && intervinienteExp.getTipoInterviniente()!=null && intervinienteExp.getTipoInterviniente().getNumeroOrden()!=null && intervinienteExp.getTipoInterviniente().getNumeroOrden().equals(TipoIntervinienteEnumeration.ACTOR_NUMERO_ORDEN);
	}
	
	public StringBuilder demandadosExpedita(Expediente expediente){
		StringBuilder intervinientes = new StringBuilder();
		String separador = ", ";
		
		List<IntervinienteExp> demandadosExpList = new ArrayList<IntervinienteExp>(expediente.getIntervinienteExpList());
		for (IntervinienteExp intervinienteExp: expediente.getIntervinienteExpList()) {
			if (intervinienteExp.isLogicalDeleted() || !isDemandado(intervinienteExp)){
				demandadosExpList.remove(intervinienteExp);
			}
		}
		
		int procesados = 1;
		for (IntervinienteExp demandadoExpList: demandadosExpList) {
			String nombre = demandadoExpList.getInterviniente().getNombre();
			if (procesados!=1)
			{
				if ((demandadosExpList.size()-procesados)!=0)
					intervinientes.append(separador);
				else
					intervinientes.append(conector(nombre));
			}
			intervinientes.append(nombre);
			procesados++;
		}
		return intervinientes;
	}

	private boolean isDemandado(IntervinienteExp intervinienteExp) {
		return (!intervinienteExp.isLogicalDeleted()) && intervinienteExp.getTipoInterviniente()!=null && intervinienteExp.getTipoInterviniente().getNumeroOrden()!=null && intervinienteExp.getTipoInterviniente().getNumeroOrden().equals(TipoIntervinienteEnumeration.DEMANDADO_NUMERO_ORDEN);
	}
	
	public static String conector(String palabra)
	{
		if(palabra.startsWith("HI") || palabra.startsWith("I") || palabra.startsWith("Y"))
			return " E ";
		return " Y ";
	}
	
	public byte[] generateDocument(Modelo modelo, Expediente expediente) {
		if (modelo.getTipoEditor().equals(TipoEditorModeloEnumeration.Type.word.getValue())) {
			byte[] templateData = modelo.getContenido();
			if (templateData != null) {
				org.dom4j.Document xmlDataSource = generateExpedienteXmlDataSource(expediente, getCurrentDateManager().getCurrentDate(), false);
				byte[] result = getEcmManager(expediente).generateDocumentWordFusionFromXml(templateData, xmlDataSource, false);
				return result;
			}
		}
		return null;
	}


	public IEcmManager getEcmManager(Expediente expediente) {
		return EcmManagerFactory.getEcmManager(expediente.getOficinaInicial());
	}

	public String doGenerarCaratula() {
		Expediente expediente = getExpedienteHome().getInstance();
		if (!isVerificadoMesaEntrada(expediente) && 
			ConfiguracionMateriaManager.instance().isUsarCaratulaAntigua(expediente.getCamaraActual(), expediente.getMateria())) {
			updatePreviewCaratulaAntigua();
		} else {
			updatePreview();
		}
		visualizarCaratula();
		return null;
	}
	
	public boolean isVerificadoMesaEntrada(Expediente expediente) {
		return VerificacionMesaEnumeration.getVerificadoMesaList().contains(expediente.getVerificacionMesa());
	}

	private boolean updatePreviewCaratulaAntigua() {
		Expediente expediente = getExpedienteHome().getInstance();
		if(expediente.getOficinaInicial() != null){
			if(isPenal(expediente)){
				return updatePreviewCaratulaAntiguaPenal();
			}else{
				return updatePreviewCaratulaAntiguaCivil();
			}
		}else{
			return false;
		}
	}
	
	private boolean updatePreviewCaratulaAntiguaCivil() {
		Expediente expediente = getExpedienteHome().getInstance();
		if (!Strings.isEmpty(expediente.getIdDocumentStore())) {
			if (!getEcmManager(expediente).removeDocument(expediente.getIdDocumentStore())) {
				getStatusMessages().addFromResourceBundle(Severity.ERROR, "expediente.errorActualizandoDocumento");
				return false;
			}
		}
		// evitar crear el documento de la caratula antes de añadir intervinientes.
		if(expediente.getIntervinienteExpList().size() == 0){
			return true;
		}
		Modelo modeloCaratula;
		modeloCaratula = buscarModeloCaratula();
		if (modeloCaratula == null) {
			getStatusMessages().addFromResourceBundle(Severity.ERROR, "expedienteManager.errorBuscarModeloCaratula");
			return false;
		}
		byte[] caratulaGenerada = generateDocument(modeloCaratula, getExpedienteHome().getInstance());
		if (caratulaGenerada == null) {
			getStatusMessages().addFromResourceBundle(Severity.ERROR, "expedienteManager.errorGenerarCaratula");
			return false;
		}
		Document document = new Document();
		document.setContent(caratulaGenerada);
		document.setDocumentFileName(modeloCaratula.getFichero());
		setDocumentoActual(document);
		if (documentoActual != null) {
			String idDocumentStore = getEcmManager(expediente).storeDocument(
					documentoActual.getDocumentFileName(), 
					documentoActual.getContent(), 
					documentoActual.getDocumentMetadata(), 
					true); 
			expediente.setIdDocumentStore(idDocumentStore);
			setDocumentoActual(getEcmManager(expediente).retrieveDocument(idDocumentStore, true));
			updateSilent();
		} 
		return true;
	}

	private boolean updatePreviewCaratulaAntiguaPenal() {
		Document document = new Document();
		//byte[] content = CaratulaReportManager.instance().pdf(getExpedienteHome().getInstance(),"concatenado");
		Expediente expediente = getExpedienteHome().getInstance();

		byte[] content = CaratulaReportManager.instance().getPdfCaratula(expediente, false);
		document.setContent(content);
		document.setPdfContent(content);
		document.setDocumentFileName("Caratula"+getExpedienteIdxAnaliticoFirst());
		setDocumentoActual(document);
		return true;
	}

	private boolean updatePreview(){
		Document document = new Document();
		Expediente expediente = getExpedienteHome().getInstance();
		byte[] content = CaratulaReportManager.instance().getPdfCaratula(expediente, false);
		document.setContent(content);
		document.setPdfContent(content);
		document.setDocumentFileName("Caratula"+getExpedienteIdxAnaliticoFirst().replace(' ', '$').replace('/', '_'));
		setDocumentoActual(document);
		return true;
	}

	public void imprimirCaratula(Expediente expediente, String colaImpresion) {
		ImpresionExpManager impresionExpManager = (ImpresionExpManager) Component.getInstance(ImpresionExpManager.class, true);
		impresionExpManager.imprimirCaratula(expediente, colaImpresion, getIdxAnalitico(expediente));
	}

	public Modelo buscarModeloCaratula(){
		Modelo modeloCaratula;
		if (isPrincipal(getExpedienteHome().getInstance())) {
			modeloCaratula = getOficinaManager().getModeloCaratula();
		}else{
			modeloCaratula = getOficinaManager().getModeloCaratulaCuaderno();
		}	
		return modeloCaratula;
	}

	private String calculaCaratulaAdministrativo(Expediente expediente,
			String numero) {
			
		String prefix =  calculaCaratulaPrefix(expediente.getTipoSubexpediente(), numero);
		String ret = prefix;
		if (expediente.getExpedienteOrigen() != null) {
			ret += expediente.getExpedienteOrigen().getCaratula();
		} else {
			ret += expediente.getTipoSubexpedienteExtendido().getDescripcion();
		}
		return ret;
	}	
	
	private String calculaCaratulaCirculacion(Expediente expediente,
			String numero) {
			
		String prefix =  calculaCaratulaPrefix(expediente.getTipoSubexpediente(), numero);
		String ret = prefix;
		if (expediente.getExpedienteOrigen() != null) {
			ret += expediente.getExpedienteOrigen().getCaratula();
		} else {
			ret += expediente.getTipoSubexpedienteExtendido().getDescripcion();
		}
		return ret;
		
	}	
	
	
	private String calculaCaratulaIncidente(Expediente expediente,
			String numero, 
			IntervinienteExp[] intervinientes) {
			
		String prefix =  calculaCaratulaPrefix(expediente.getTipoSubexpediente(), numero);
		TipoSubexpediente tipoSubexpediente = expediente.getTipoSubexpediente();
		
		String plantillaCaratula  = "<1s> <2s> s/<ts>";
		
		plantillaCaratula = fillIntervinientesCaratula(plantillaCaratula, intervinientes, null);		
		
		if(tipoSubexpediente != null){
			plantillaCaratula = plantillaCaratula.replaceAll(Matcher.quoteReplacement(TipoCaratulaEnumeration.TIPO_SUBEXPEDIENTE_KEY), Matcher.quoteReplacement(tipoSubexpediente.getDescripcion()));
		}
		
		/** ATOS COMERCIAL **/
		String suffix =  "";
		if(isComercial(expediente) && isIncidente(expediente) && expediente.getComentarios() != null){
			suffix = " "  + expediente.getComentarios();
		}
		
		/** ATOS COMERCIAL **/
		
		return prefix + plantillaCaratula  + suffix;
		
	}	
	
	
	
	private String calculaCaratulaObjetosJuicio(Expediente expediente,
				String numero, 
				String tipoCausa,
				List<ObjetoJuicio> objetoJuicioList, 
				String descripcionObjetoJuicio,
				IntervinienteExp[] intervinientes, 
				TipoCaratula tipoCaratula) {
		ObjetoJuicio[] objetosJuicio = null;
		if(objetoJuicioList != null){
			objetosJuicio = objetoJuicioList.toArray(new ObjetoJuicio[objetoJuicioList.size()]);
		}
		String prefix =  calculaCaratulaPrefix(expediente.getTipoSubexpediente(), numero);	
		/** ATOS SUBASTA */
		if (!Strings.isEmpty(expediente.getCodigoSubexpediente())) {
			String naturalezaSubexpediente = expediente.getTipoSubexpediente().getNaturalezaSubexpediente();
			if (NaturalezaSubexpedienteEnumeration.Type.subasta.getValue().equals(naturalezaSubexpediente) ){
				String comentario = "";
				if(expediente.getComentarios() != null && !expediente.getComentarios().isEmpty())
					comentario = expediente.getComentarios() + " - ";
				return prefix + comentario  + expediente.getExpedienteOrigen().getCaratula();
				
			}
		} 
		/** ATOS SUBASTA */
		
		String plantillaCaratula  = tipoCaratula.getDescripcion();
		
		if(isComercial(expediente) && isPedidoQuiebra(expediente)){
			plantillaCaratula = "<2> LE PIDE LA QUIEBRA <1>";
		}
		
		plantillaCaratula = fillSolicitanteCaratula(plantillaCaratula, expediente, intervinientes);
		plantillaCaratula = fillIntervinientesCaratula(plantillaCaratula, intervinientes,tipoCaratula);		
		plantillaCaratula = replaceTipoCausa(plantillaCaratula, tipoCausa);

		plantillaCaratula = fillCaratulaObjetos(plantillaCaratula, objetosJuicio, descripcionObjetoJuicio);
		return (prefix + plantillaCaratula);//.toUpperCase();
	}
	

	private String fillSolicitanteCaratula(String plantillaCaratula, Expediente expediente, IntervinienteExp[] intervinientes) {
		return replaceSolicitante(plantillaCaratula, expediente, intervinientes);
	}

	private String fillIntervinientesCaratula(String plantillaCaratula, IntervinienteExp[] intervinientes, TipoCaratula tipoCaratula) {
		plantillaCaratula = calculaCaratulaIntervinientes(plantillaCaratula, intervinientes);
		return plantillaCaratula;
	}

	private String calculaCaratulaPrefix(TipoSubexpediente tipoSubexpediente, String numero) {
		StringBuilder builder = new StringBuilder();
		NaturalezaSubexpedienteEnumeration naturalezaSubexpedienteEnumeration = (NaturalezaSubexpedienteEnumeration) Component.getInstance(NaturalezaSubexpedienteEnumeration.class, true);
		NaturalezaSubexpedienteEnumeration.Type naturalezaSubexpediente = (NaturalezaSubexpedienteEnumeration.Type) 
			naturalezaSubexpedienteEnumeration.getType(tipoSubexpediente.getNaturalezaSubexpediente());
		if (naturalezaSubexpediente != NaturalezaSubexpedienteEnumeration.Type.principal) {
			builder.append(naturalezaSubexpedienteEnumeration.getLabel(tipoSubexpediente.getNaturalezaSubexpediente()));
			if (naturalezaSubexpediente == null || naturalezaSubexpediente.isMostrarDescripcion()) {
				builder.append(" ").append(tipoSubexpediente.getDescripcion());
			}
			if (naturalezaSubexpediente == null || naturalezaSubexpediente.isMostrarNumero()) {
				builder.append(NUMERO_ABREVIADO);
			} else {
				builder.append(" ");
			}
			builder.append(numero).append(" - ");
		}
		return builder.toString();
	}


	public String getDomicilioDenunciado(IntervinienteExp intervinienteExp){
		return intervinienteExp.getInterviniente().getDomicilio();
	}

	public String getNumeroDomicilioDenunciado(IntervinienteExp intervinienteExp){
		return intervinienteExp.getInterviniente().getNumeroDomicilio();
	}

	public String getLocalidadDenunciado(IntervinienteExp intervinienteExp){
		return intervinienteExp.getInterviniente().getLocalidad();
	}

	private boolean hasDomicilioConstituido(IntervinienteExp intervinienteExp) {
		return !Strings.isEmpty(intervinienteExp.getDomicilio()) || !Strings.isEmpty(intervinienteExp.getLocalidad());
	}

	public String getDomicilio(IntervinienteExp intervinienteExp){
		if(hasDomicilioConstituido(intervinienteExp) && !isTestigo(intervinienteExp)){
			return intervinienteExp.getDomicilio();
		}else if(intervinienteExp.getInterviniente() != null && !Strings.isEmpty(intervinienteExp.getInterviniente().getDomicilio())){
			return ConfiguracionMateriaManager.instance().getPrefijoDomicilioDenunciado(getSessionState().getGlobalCamara(), null) + " " + intervinienteExp.getInterviniente().getDomicilio();
		} else {
			return null;
		}
	}
	
	public String getDomicilioCompleto(IntervinienteExp intervinienteExp){
		if(hasDomicilioConstituido(intervinienteExp) && !isTestigo(intervinienteExp)){
			return intervinienteExp.getDomicilioCompleto();
		}else if(intervinienteExp.getInterviniente() != null && !Strings.isEmpty(intervinienteExp.getInterviniente().getDomicilio())){
			return ConfiguracionMateriaManager.instance().getPrefijoDomicilioDenunciado(getSessionState().getGlobalCamara(), null) + " " + intervinienteExp.getInterviniente().getDomicilioCompleto();
		} else {
			return null;
		}
	}

	public String getNumeroDomicilio(IntervinienteExp intervinienteExp){
		if(hasDomicilioConstituido(intervinienteExp) && !isTestigo(intervinienteExp)){
			return intervinienteExp.getNumeroDomicilio();
		}else if(intervinienteExp.getInterviniente() != null && !Strings.isEmpty(intervinienteExp.getInterviniente().getNumeroDomicilio())){
			return intervinienteExp.getInterviniente().getNumeroDomicilio();
		} else {
			return null;
		}
	}
	

	public String getLocalidad(IntervinienteExp intervinienteExp){
		if(hasDomicilioConstituido(intervinienteExp) && !isTestigo(intervinienteExp)){
			return intervinienteExp.getLocalidad();
		}else if(intervinienteExp.getInterviniente() != null){
			return intervinienteExp.getInterviniente().getLocalidad();
		}else{
			return null;
		}
	}
	
	/**
	 * Obtiene el domicilio electronico a partir de un interviniente
	 * @param intervinienteExp {@link IntervinienteExp} del cual obtener el domicilio electronico
	 * @return {@link String} con el Domicilio Electronico
	 */
	public String getDomicilioElectronico(IntervinienteExp intervinienteExp) {
		if (intervinienteExp==null) return null;
		return intervinienteExp.getCuil();
	}

	public String getTelefono(IntervinienteExp intervinienteExp){
		if(hasDomicilioConstituido(intervinienteExp) && !isTestigo(intervinienteExp)){
			return intervinienteExp.getTelefono();
		}else if(intervinienteExp.getInterviniente() != null){
			return intervinienteExp.getInterviniente().getTelefono();
		}else{
			return null;
		}
	}
	
	public String getCorreoElectronico(IntervinienteExp intervinienteExp){
		if(hasDomicilioConstituido(intervinienteExp) && !isTestigo(intervinienteExp)){
			return intervinienteExp.getCorreoElectronico();
		}else if(intervinienteExp.getInterviniente() != null){
			return intervinienteExp.getInterviniente().getCorreoElectronico();
		}else{
			return null;
		}
	}
	
	public boolean isTestigo(IntervinienteExp intervinienteExp) {
		boolean ret = false;
		if (intervinienteExp.getTipoInterviniente() != null && intervinienteExp.getTipoInterviniente().getDescripcion() != null
				&& "TESTIGO".equals(intervinienteExp.getTipoInterviniente().getDescripcion())) {
			
			ret = true;
		}
		return ret;
	}
	
	public String getDocumento(IntervinienteExp intervinienteExp){
		String ret = null;
		Interviniente interviniente = intervinienteExp.getInterviniente();
		if(interviniente != null && !Strings.isEmpty(interviniente.getNumeroDocId())){
			ret = formatDocumento(interviniente.getTipoDocumentoIdentidad(), interviniente.getNumeroDocId());
		}
		return ret;
	}

	private String formatDocumento(	TipoDocumentoIdentidad tipoDocumentoIdentidad, String numeroDocId) {
		String ret = "";
		if(tipoDocumentoIdentidad == null){
			ret = numeroDocId;
		}else{
			ret = tipoDocumentoIdentidad.getCodigo() +  ": ";
			if(TipoDocumentoIdentidadEnumeration.isDni(tipoDocumentoIdentidad)){
				ret += formatAsNumber(numeroDocId);
			}else{
				ret += numeroDocId;
			}
		}
		return ret;
	}

	private static String formatAsNumber(String numeroDocId) {
		String ret = numeroDocId;
		try{
			if(numeroDocId != null){
				Long num = Long.valueOf(numeroDocId.trim());
				ret = new DecimalFormat("#,###").format(num);
			}
		}catch(NumberFormatException e){	
			e.printStackTrace();
		}
		return ret;
	}

	public String getDocumento(LetradoIntervinienteExp letradoIntervinienteExp){
		String ret = null;
		Letrado letrado = letradoIntervinienteExp.getLetrado();
		if(letrado != null){
			StringBuffer sb = new StringBuffer();
			if(!isEmptyOrZero(letrado.getTomo())){
				sb.append("Tomo: ");
				sb.append(letrado.getTomo());
			}
			if(!isEmptyOrZero(letrado.getFolio())){
				if(sb.length() > 0){
					sb.append(" ");
				}
				sb.append("Folio: ");
				sb.append(letrado.getFolio());
			}
			if(sb.length() > 0){
				sb.append("    ");
			}
			if(!isEmptyOrZero(letrado.getCuitCuil())){
				sb.append("CUIL: ");
				sb.append(letrado.getCuitCuil());				
			}else if(!isEmptyOrZero(letrado.getMatricula())){
				sb.append("Matrícula: ");
				sb.append(letrado.getMatricula());								
			}
			ret = sb.toString();
		}
		return ret;
	}

	private boolean isEmptyOrZero(String s) {
		return Strings.isEmpty(s) || s.equals("0");
	}

	private String fillCaratulaObjetos(String plantillaCaratula,
			ObjetoJuicio[] objetosJuicio, String descripcionObjetoJuicio) {
		if(objetosJuicio != null){
			StringBuilder builder = new StringBuilder();
			int numObjetos = objetosJuicio.length;
			int count = 0;
			for(ObjetoJuicio objeto: objetosJuicio) {
				if (!ObjetoJuicioEnumeration.isObjetoSinDescripcion(objeto)) {
					count ++;
					if (builder.length() > 0) {
						if (count < numObjetos) {
							builder.append(", ");
						} else {
							builder.append(" y ");
						}
					}
					builder.append(objeto.getDescripcion());
				}
			}
			if(!Strings.isEmpty(descripcionObjetoJuicio)){
				if (count > 0) {
					builder.append(" - ");
				}
				builder.append(descripcionObjetoJuicio);
			}
			plantillaCaratula = plantillaCaratula.replaceAll(Matcher.quoteReplacement(TipoCaratulaEnumeration.OBJETO_KEY), Matcher.quoteReplacement(builder.toString()));
		}
		return plantillaCaratula;
	}
	
	private String calculaCaratulaIntervinientes(String plantillaCaratula, IntervinienteExp[] intervinientes) {
		int INTERVINIENTE_ANY_ORDER = 0;		
		int INTERVINIENTE_ACTOR_ORDER = 1;		
		int INTERVINIENTE_DEMANDADO_ORDER = 2;
		

		plantillaCaratula = replaceParte(plantillaCaratula, intervinientes,	
				TipoCaratulaEnumeration.ACTORES_KEY, false,  
				INTERVINIENTE_ACTOR_ORDER, 
				1, 
				" Y OTRO", " Y OTROS");
		
		plantillaCaratula = replaceParte(plantillaCaratula, intervinientes,	
				TipoCaratulaEnumeration.DEMANDADOS_KEY, false,
				INTERVINIENTE_DEMANDADO_ORDER, 
				1, 
				" Y OTRO", " Y OTROS");
		
		plantillaCaratula = replaceParte(plantillaCaratula, intervinientes,	
				TipoCaratulaEnumeration.INTERVINIENTES_KEY, false,  
				INTERVINIENTE_ANY_ORDER, 
				1, 
				" Y OTRO", " Y OTROS");

		plantillaCaratula = replaceParte(plantillaCaratula, intervinientes,	
				TipoCaratulaEnumeration.ACTORES_RESTRINGIDO_KEY, true, 
				INTERVINIENTE_ACTOR_ORDER, 
				1, 
				" Y OTRO", " Y OTROS");
		
		plantillaCaratula = replaceParte(plantillaCaratula, intervinientes,	
				TipoCaratulaEnumeration.DEMANDADOS_REASTRINGIDO_KEY, true,
				INTERVINIENTE_DEMANDADO_ORDER, 
				1, 
				" Y OTRO", " Y OTROS");
		
		plantillaCaratula = replaceParte(plantillaCaratula, intervinientes,	
				TipoCaratulaEnumeration.INTERVINIENTES_RESTRINGIDO_KEY, true,  
				INTERVINIENTE_ANY_ORDER, 
				1, 
				" Y OTRO", " Y OTROS");
		
		return plantillaCaratula;
	}

	private String replaceSolicitante(String plantillaCaratula, Expediente expediente, IntervinienteExp[] intervinientes) {
		if(plantillaCaratula.indexOf(TipoCaratulaEnumeration.SOLICITANTE_KEY) >= 0){
			String solicitante = null;
//			IntervinienteExp intervinienteExp = findIntetvinienteByTypeName(intervinientes, SOLICITANTE_DESCRIPTION);
//			if(intervinienteExp != null){
//				solicitante = intervinienteExp.getInterviniente().getNombre();
//			}else if(expediente.getExpedienteEspecial() != null && expediente.getExpedienteEspecial().getDependenciaOrigen() != null){
//				solicitante = expediente.getExpedienteEspecial().getDependenciaOrigen();
//			}
			if(expediente.getExpedienteEspecial() != null && expediente.getExpedienteEspecial().getDependenciaOrigen() != null){
				solicitante = expediente.getExpedienteEspecial().getDependenciaOrigen();
			}
			if(solicitante != null){
				plantillaCaratula = plantillaCaratula.replaceAll(TipoCaratulaEnumeration.SOLICITANTE_KEY, solicitante);
			}
		}
		return plantillaCaratula;
	}

	private IntervinienteExp findIntetvinienteByTypeName(IntervinienteExp[] intervinientes, String typeName) {
		IntervinienteExp ret = null;
		for(IntervinienteExp item: intervinientes){
			if(item.getTipoInterviniente() != null && item.getTipoInterviniente().getDescripcion().equals(typeName)){
				ret = item;
				break;
			}
		}
		return ret;
	}

	private String replaceTipoCausa(String plantillaCaratula, String tipoCausa) {
		if(plantillaCaratula.indexOf(TipoCaratulaEnumeration.TIPO_CAUSA_KEY) >= 0){
			plantillaCaratula = plantillaCaratula.replaceAll(TipoCaratulaEnumeration.TIPO_CAUSA_KEY, tipoCausa);
		}
		if(plantillaCaratula.indexOf(TipoCaratulaEnumeration.TIPO_CAUSA_KEY2) >= 0){
			plantillaCaratula = plantillaCaratula.replaceAll(TipoCaratulaEnumeration.TIPO_CAUSA_KEY, tipoCausa);
		}
		return plantillaCaratula;
	}

	private String replaceParte(String plantillaCaratula, IntervinienteExp[] intervinientes, String key, boolean addTipoInterviniente, int orden, int numMax, String otroText, String otrosText) {
		if(plantillaCaratula.indexOf(key) >= 0){
			List<IntervinienteExp> list = getIntervinienteExpListByType(intervinientes, orden);
			String declaracionIntervinientes = calculaDeclaracionIntervinientes(list, numMax, addTipoInterviniente, otroText, otrosText);	
			declaracionIntervinientes = Matcher.quoteReplacement(declaracionIntervinientes);
			plantillaCaratula = plantillaCaratula.replaceAll(key, declaracionIntervinientes);
		}
		return plantillaCaratula;
	}

	private List<IntervinienteExp> getIntervinienteExpListByType(IntervinienteExp[] intervinientes, int type) {
		List<IntervinienteExp> list = new ArrayList<IntervinienteExp>();
		int num = 0;
		for(IntervinienteExp item: intervinientes) {
			boolean select = false;
			if(type == 0){
				select = true;
			}else if(item.getTipoInterviniente() != null 
					&& item.getTipoInterviniente() != null
					&& item.getTipoInterviniente().getNumeroOrden() != null
					&& item.getTipoInterviniente().getNumeroOrden().equals(type)){
				select = true;
			}			
			if(select){
				list.add(item);
			}
		}
		sort(list);
		return list;
	}
		

	private void sort(List<IntervinienteExp> intervinienteList) {
		Collections.sort(intervinienteList, new Comparator<IntervinienteExp>() {
			@Override
			public int compare(IntervinienteExp o1,
					IntervinienteExp o2) {
				if ((o1.getOrden() != null) &&  o2.getOrden() != null) {
					return o1.getOrden().compareTo(o2.getOrden());
				} else {
					return -1;
				}
			}
		});
	}
	

	private String calculaDeclaracionIntervinientes(List<IntervinienteExp> intervinienteList, int numMax, boolean addTipoInterviniente, String otroText, String otrosText) {
		int num = 1;
		StringBuilder builder = new StringBuilder();
		int size = intervinienteList.size();
		for (IntervinienteExp intervinienteExp : intervinienteList) {
			if (num > numMax) {
				builder.append(num < size ? otrosText : otroText);
				break;
			}
			if (builder.length() > 0) {
				builder.append(", ");
			}
			if(addTipoInterviniente && intervinienteExp.getTipoInterviniente() != null){
				builder.append(intervinienteExp.getTipoInterviniente().getDescripcion());	
				builder.append(": ");	
			}
			if (intervinienteExp.isMostrarIniciales() && !Strings.isEmpty(intervinienteExp.getIniciales())) {
				builder.append(intervinienteExp.getIniciales());
			} else {
				builder.append(intervinienteExp.getNombreCompleto());
			}
			
			String representacion = intervinienteExp.getRepresentacion();
			if (representacion != null) {
				builder.append(representacion);
			}

			num++;
		}
		return builder.toString();
	}


	private Map<Integer, List<IntervinienteExp>> calcularMapaIntervinientesCaratula(
			IntervinienteExp[] intervinientes) {
		Map<Integer, List<IntervinienteExp>> intervinienteMap = new HashMap<Integer, List<IntervinienteExp>>();
		for(IntervinienteExp interviniente: intervinientes) {
			if (interviniente.getTipoInterviniente() != null) {
				List<IntervinienteExp> intervinienteList = intervinienteMap.get(interviniente.getTipoInterviniente().getNumeroOrden());
				if (intervinienteList == null) {
					intervinienteList = new ArrayList<IntervinienteExp>();
					intervinienteMap.put(interviniente.getTipoInterviniente().getNumeroOrden(), intervinienteList);
				}
				intervinienteList.add(interviniente);
			}
		}
		return intervinienteMap;
	}
	
	public boolean actualizarCaratulaSiMesaActiva(String motivo, String detalle) {
		boolean ret = true;
		if (CamaraManager.isMesaEntradaActiva()) { // Solo si la mesa esta activa. Si no se cambia a mano.
			Expediente expediente = getExpedienteHome().getInstance();
			if (isVerificadoMesaEntrada(expediente)  
				|| !ConfiguracionMateriaManager.instance().isUsarCaratulaAntigua(expediente.getCamaraActual(), expediente.getMateria())) {
				ret = actualizarCaratula(motivo, detalle);
			}
		}
		return ret;
	}

	public boolean actualizarCaratula(String motivo, String detalle) {
		return actualizarCaratula(getExpedienteHome().getInstance(), motivo,
				detalle);
	}
	
	public boolean actualizarCaratula(Expediente expediente, String motivo, String detalle) {
		String newCaratula =  calculaCaratula(expediente);
		if (!Strings.isEmpty(newCaratula)) { 
//				&& !newCaratula.equals(expediente.getCaratula()) ) {
			asignarCaratula(expediente, truncate(newCaratula,500), motivo, detalle);
			
			actualizarCaratulaCirculaciones(expediente, motivo, detalle);
		}
		return true;
	}
	
	public boolean actualizarCaratulaCuadernosRelacionados(Expediente expediente, String motivo, String detalle) {
		Set<String> naturalezas = new TreeSet<String>();	
		naturalezas.add(NaturalezaSubexpedienteEnumeration.Type.legajo.getValue().toString());
		naturalezas.add(NaturalezaSubexpedienteEnumeration.Type.incidente.getValue().toString());
		for (Expediente cuaderno : expediente.getExpedienteList()) {
			if (cuaderno.getNaturaleza() != null && naturalezas.contains(cuaderno.getNaturaleza())) {
				actualizarCaratula(cuaderno, motivo, detalle);
			}
		}
		return true;
	}
	
	public boolean actualizarCaratulaCirculaciones(Expediente expediente, String motivo, String detalle) {
		for (Expediente circulacion : getCirculaciones(getEntityManager(), expediente)) {
			actualizarCaratula(circulacion, motivo, detalle);
		}
		return true;
	}
	
	public static String truncate(String value, int len) {
		if ((value != null) && value.length() > len) {
			value = value.substring(0, len);
		}
		return value;
	}

	private String calculaCaratula (Expediente expediente) {
		
		Collections.sort(expediente.getIntervinienteExpList());
		if(TipoCausaEnumeration.hasTipoCausaCaratulaOrigen(expediente.getTipoCausa()) && 
				expediente.getExpedienteEspecial() != null &&
				!Strings.isEmpty(expediente.getExpedienteEspecial().getCaratulaOrigen())){
			return expediente.getExpedienteEspecial().getCaratulaOrigen();
		}
		if (isLegajoPersonalidad(expediente)) {
			return calculaCaratulaLegajo(expediente);
		}
		
		
		List<IntervinienteExp> intervinienteExpList = calculateRecursiveIntervinienteExpList(expediente);		
		for (IntervinienteExp intervinienteExp: expediente.getIntervinienteExpList()) {
			if (intervinienteExp.isLogicalDeleted() || intervinienteExp.isNoSaleEnCaratula() ||
				(intervinienteExp.isPteVerificacion() && 
				 ConfiguracionMateriaManager.instance().isCaratulaSoloIntervinientesVerificados(CamaraManager.getCamaraActual()))) {
				intervinienteExpList.remove(intervinienteExp);
			}
		}
		String numero;
		if (!Strings.isEmpty(expediente.getCodigoSubexpediente())) {
			String naturalezaSubexpediente = expediente.getTipoSubexpediente().getNaturalezaSubexpediente();
			if ((NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue().equals(naturalezaSubexpediente) ||
				NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue().equals(naturalezaSubexpediente)
				)
				&& expediente.getNumeroSubexpediente() != 0) {
				numero = expediente.getNumeroSubexpediente().toString()+"/"+expediente.getCodigoSubexpediente();
			} else {
				numero =expediente.getCodigoSubexpediente();
			}
		}else{
			numero =expediente.getNumeroSubexpediente().toString();
		}
		
		List<ObjetoJuicio> objetoJuicioList = null;
		String tipoCausa = expediente.getTipoCausa() == null ? null : expediente.getTipoCausa().getDescripcion();
		TipoCaratula tipoCaratula = null;

		if(expediente.getTipoCausa() != null && expediente.getTipoCausa().getTipoCaratula() != null){
			tipoCaratula = expediente.getTipoCausa().getTipoCaratula();
		} else if (expediente.getTipoProceso() != null){
			tipoCaratula = expediente.getTipoProceso().getTipoCaratula();
		}
		
		if(ExpedienteManager.isPenal(expediente)){
			objetoJuicioList = new ArrayList<ObjetoJuicio>();
			if(expediente.getDelitoExpedienteList().size() != 0){
				
				List<DelitoExpediente> list = new ArrayList<DelitoExpediente>(expediente.getDelitoExpedienteList());
				
				DelitoEnumeration.sort(list);
				
				for(DelitoExpediente delitoExpediente: list){
					ObjetoJuicio oj = new ObjetoJuicio();
					oj.setDescripcion(delitoExpediente.getDescripcion());
					objetoJuicioList.add(oj);
				}
			}
			if (objetoJuicioList.size() == 0 && expediente.getObjetoJuicioPrincipal() != null) {
				objetoJuicioList.add(expediente.getObjetoJuicioPrincipal());
			}
		}else{
			objetoJuicioList = new ArrayList<ObjetoJuicio>();
			if (expediente.getObjetoJuicioPrincipal() != null) {
				objetoJuicioList.add(expediente.getObjetoJuicioPrincipal());				
			} 
			if (isMediacion(expediente)) {
				for (ObjetoJuicio objetoJuicio: expediente.getObjetoJuicioList()) {
					if (!objetoJuicioList.contains(objetoJuicio)) {
						objetoJuicioList.add(objetoJuicio);
					}
				}
			}
		}
		
		if (TipoCaratulaEnumeration.isTipoCaratulaContradictoria(tipoCaratula) && !hasDemandados(intervinienteExpList.toArray(new IntervinienteExp[intervinienteExpList.size()]))) {
			tipoCaratula = TipoCaratulaEnumeration.getTipoCaratulaVoluntaria(getEntityManager());
		} else if (TipoCaratulaEnumeration.isTipoCaratulaVoluntaria(tipoCaratula) && hasDemandados(intervinienteExpList.toArray(new IntervinienteExp[intervinienteExpList.size()]))) {
			tipoCaratula = TipoCaratulaEnumeration.getTipoCaratulaContradictoria(getEntityManager());
		}
		
		if(isMediacion(expediente)){
			return calculaCaratulaMediacion(expediente, objetoJuicioList, expediente.getDescripcionObjetoJuicio(), intervinienteExpList.toArray(new IntervinienteExp[intervinienteExpList.size()]));			
//		}else if(isCirculacion(expediente) && expediente.getExpedienteOrigen() != null){
//			 return calculaCaratulaCirculacion(expediente, 
//						numero);
		}else if(isIncidenteCuadernolegajo(expediente) && expediente.getTipoSubexpediente() != null){
			return calculaCaratulaIncidente(expediente, 
					numero,
					intervinienteExpList.toArray(new IntervinienteExp[intervinienteExpList.size()]));
		}else if(isExpedienteAdministrativo(expediente)) {
			return calculaCaratulaAdministrativo(expediente, numero);
		}else if(isCirculacion(expediente)) {
			return calculaCaratulaCirculacion(expediente, numero);
		}else if (tipoCaratula != null) {
			return calculaCaratulaObjetosJuicio(expediente, 
					numero,
					tipoCausa,
					objetoJuicioList,
					expediente.getDescripcionObjetoJuicio(),
					intervinienteExpList.toArray(new IntervinienteExp[intervinienteExpList.size()]),
					tipoCaratula);
		} else {
			return null;
		}

	}

	public static boolean isIncidenteCuadernolegajo(Expediente expediente) {
		return (
				NaturalezaSubexpedienteEnumeration.Type.incidente.getValue().equals(expediente.getNaturaleza()) ||
				NaturalezaSubexpedienteEnumeration.Type.cuaderno.getValue().equals(expediente.getNaturaleza()) ||
				NaturalezaSubexpedienteEnumeration.Type.legajo.getValue().equals(expediente.getNaturaleza()) );
	}

	private boolean hasDemandados(IntervinienteExp[] intervinientes) {
		for (IntervinienteExp intervinienteExp : intervinientes) {
			if (!intervinienteExp.isNoSaleEnCaratula() && !intervinienteExp.isPteVerificacion()) {
				if (intervinienteExp.isDemandado()) {
					return true;
				}
			}
		}
		return false;
	}

	private List<IntervinienteExp> calculateRecursiveIntervinienteExpList(Expediente expediente) {
// Se permite que los subexpedientes no tengan intervinientes
//		while(expediente.getIntervinienteExpList().size() == 0 && expediente.getExpedienteOrigen() != null){
//			expediente = expediente.getExpedienteOrigen();
//		}
		List<IntervinienteExp> intervinienteExpList = new ArrayList<IntervinienteExp>(expediente.getIntervinienteExpList());
		return intervinienteExpList;
	}


	private String calculaCaratulaLegajo(Expediente expediente) {
		CabeceraLIP cabeceraLIP = expediente.getCabeceraLIP();
		StringBuilder builder = new StringBuilder();
		
		String title;
		if (cabeceraLIP != null && cabeceraLIP.isMenor()) {
			title = getMessages().get("caratulaLegajo.title.menores");
		} else{
			title = getMessages().get("caratulaLegajo.title");
		}
		builder.append(title).append(" ");
		if (cabeceraLIP != null) {
			builder.append(IntervinienteExpManager.instance().getClaveLegajoPersonalidad(cabeceraLIP));
			if (!Strings.isEmpty(cabeceraLIP.getApellidos())) {
				builder.append(" ").append(cabeceraLIP.getApellidos());
			}
			if (!Strings.isEmpty(cabeceraLIP.getNombre())) {
				builder.append(" ").append(cabeceraLIP.getNombre());
			}
			
		}
		return builder.toString().toUpperCase();
	}
	
	
	private String updateSilent() {
		ValueExpression<?> updatedMessage = getExpedienteHome().getUpdatedMessage();
		getExpedienteHome().setUpdatedMessage(createValueExpression(""));
		String returnValue = getExpedienteHome().update();
		getExpedienteHome().setUpdatedMessage(updatedMessage);
		return returnValue;
	}
	
	
	public String getCaratulaResumida(String caratula, int longitud) {
		return reducirCadena(caratula, longitud);
	}
	
	public String getCaratulaResumida(String caratula) {
		return getCaratulaResumida(caratula, LONGITUD_CARATULA_REDUCIDA);
	}

	public String getTipoSubexpedienteResumido(String tipoSubexpediente) {
		return reducirCadena(tipoSubexpediente, LONGITUD_TIPO_SUBEXPEDIENTE_REDUCIDA);
	}
	
	private String reducirCadena(String cadena, int length) {
		if (cadena != null && cadena.length() > length) {
			return cadena.substring(0, length) + "...";
		}
		return cadena;
	}
	
	public List<LetradoIntervinienteExp> getLetrados(IntervinienteExp intervinienteExp) {
		List<LetradoIntervinienteExp> result = new ArrayList<LetradoIntervinienteExp>();
		for (LetradoIntervinienteExp letradoIntervinienteExp : intervinienteExp.getLetradoIntervinienteExpList()) {
			if (letradoIntervinienteExp.getStatus() != LetradoIntervinienteExp.DELETED_STATUS_FLAG) {
				result.add(letradoIntervinienteExp);
			}
		}
		return result;
	}

	public List<IntervinienteExp> getIntervinientesRelacionados(IntervinienteExp intervinienteExp) {
		List<IntervinienteExp> result = new ArrayList<IntervinienteExp>();
		for (IntervinienteExp intervinienteExpRelacionado : intervinienteExp.getIntervinienteExpList()) {
			if (intervinienteExpRelacionado.getStatus() != LetradoIntervinienteExp.DELETED_STATUS_FLAG) {
				result.add(intervinienteExpRelacionado);
			}
		}
		return result;
	}

	public String doReservar() {
		/*try {
			new AccionActualizarReservaExpediente().doAccion(getExpedienteHome().getInstance(), (Integer)TipoRestriccionEnumeration.Type.total.getValue());
			return "";
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_reservar_expediente");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return null;
		}*/
		// Cambio de situacion

			initDoSeleccionTipoReserva(false);
			getExpedienteHome().setVisibleDialog("reservaExpediente", true);
			return "";
	
		
	}


	public String doDesreservar() {
		try {
			new AccionActualizarReservaExpediente().doAccion(getExpedienteHome().getInstance(), (Integer)TipoRestriccionEnumeration.Type.sinRestriccion.getValue());
			return "";
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_desreservar_expediente");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return null;
		}
	}


	@Transactional
	public void actualizarReserva(Expediente expediente,
			DocumentoExp documento, Integer reserva) {
		getEntityManager().joinTransaction();
		
		if (expediente.getReservado() != reserva) {
			expediente.setReservado(reserva);
					
			switch(reserva){
			case 0:
				getActuacionExpManager().addActuacionDesreservaExpediente(getExpedienteHome().getInstance(), null);
				/*if(!isPenal(expediente) || !isExpedienteFamilia(expediente)){	*/		
					for(IntervinienteExp intervinienteExp: expediente.getIntervinienteExpList()){
						intervinienteExp.setRestriccionWeb(false);
					/*}*/
				}
				break;
			case 1:
				getActuacionExpManager().addActuacionReservaTotalExpediente(getExpedienteHome().getInstance(), null);
					for(IntervinienteExp intervinienteExp: expediente.getIntervinienteExpList()){
						intervinienteExp.setRestriccionWeb(true);
					}
				break;
			case 2:
				getActuacionExpManager().addActuacionReservaWebExpediente(getExpedienteHome().getInstance(), null);
					for(IntervinienteExp intervinienteExp: expediente.getIntervinienteExpList()){
						intervinienteExp.setRestriccionWeb(true);
					}
				break;
			case 3:
				getActuacionExpManager().addActuacionReservaExpedienteVisualizcionPartes(getExpedienteHome().getInstance(), null);
				break;
		}
			getEntityManager().flush();
		
	}
		
}
	
	public Boolean getBloquearExpedientesReservados() {
		if (getIdentity().hasPermission("Expediente", "verReservados")) {
			return null;
		}
		return true;
	}
	
	public Interviniente findIntervinienteByLetrado(Expediente expediente, Letrado letrado){
		for(IntervinienteExp intervinienteExp : expediente.getIntervinienteExpList()){
			if (!intervinienteExp.isLogicalDeleted()) {
				for(LetradoIntervinienteExp letradoIntervinienteExp: intervinienteExp.getLetradoIntervinienteExpList()){
					if(letradoIntervinienteExp.getLetrado().equals(letrado)){
						return intervinienteExp.getInterviniente();
					}
				}
			}
		}
		return null;
	}
	
	public TipoInterviniente findTipoIntervinienteByLetrado(Expediente expediente, Letrado letrado){
		for(IntervinienteExp intervinienteExp : expediente.getIntervinienteExpList()){
			if (!intervinienteExp.isLogicalDeleted()) {
			for(LetradoIntervinienteExp letradoIntervinienteExp: intervinienteExp.getLetradoIntervinienteExpList()){
				if(letradoIntervinienteExp.getLetrado() != null && letradoIntervinienteExp.getLetrado().equals(letrado)){
					return intervinienteExp.getTipoInterviniente();
				}
			}
		}
		}
		return null;
		
	}
	
	public TipoInterviniente findTipoIntervinienteByInterviniente(Expediente expediente,Interviniente interviniente){
		for(IntervinienteExp intervinienteExp : expediente.getIntervinienteExpList()){
			if (!intervinienteExp.isLogicalDeleted()) {
			if(intervinienteExp.getInterviniente() != null && intervinienteExp.getInterviniente().equals(interviniente)){
					return intervinienteExp.getTipoInterviniente();
			}
		}
		}
		return null;	
	}

	public Profesion findProfesionByPerito(Expediente expediente, Perito perito){
		for(PeritoExp peritoExp : expediente.getPeritoExpList()){
			if(peritoExp.getPeritoProfesion() != null && peritoExp.getPeritoProfesion().getPerito() != null && peritoExp.getPeritoProfesion().getPerito().equals(perito)){
					return peritoExp.getPeritoProfesion().getProfesion();
			}
		}
		return null;
		
	}

	public String doRecibirPase() {
		return doRecibirPase(getExpedienteHome().getInstance());
	}
	
	public String doRecibirPase(Expediente expediente) {
		try {
			boolean pasarADespacho = 
				((expediente.getOficinaRetorno() != null && (getSessionState().getGlobalIdOficinaSet().contains(expediente.getOficinaRetorno().getId()))) || 
				SituacionExpedienteEnumeration.Type.giro.getValue().equals(expediente.getSituacion()) || isVista(expediente)) && isIniciado(expediente);
			PaseExp paseExp = expediente.getPasePendiente();
			new AccionRecibirPase().doAccion(expediente);
			if (pasarADespacho) {
				String situacion = null;
				if (isIniciado(expediente)) {
					situacion = SituacionExpedienteEnumeration.Type.despacho.getValue().toString();
				}
				new AccionCambioSituacionExpediente().doAccion(expediente, situacion, true);
				if (paseExp != null && paseExp.getExpedientesAgregadosList().size() > 0) {
					for (Expediente expedienteAgregado: paseExp.getExpedientesAgregadosList()) {
						new AccionCambioSituacionExpediente().doAccion(expedienteAgregado, situacion, true);
					}
					ExpedienteHome.instance().setId(paseExp.getExpediente().getId());
				}
			}
			return "";
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_retornar_pase_expediente");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return null;
		}
	}

	
	public String doRetornarPase() {
		try {
			new AccionRetornoPase().doAccion(getExpedienteHome().getInstance(), null);
			return "";
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_retornar_pase_expediente");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return null;
		}
	}

	public String doCancelarPase() {
		try {
			new AccionCancelarPase().doAccion(getExpedienteHome().getInstance());
			return "";
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_cancelar_pase_expediente");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return null;
		}
	}
	
	public String doCancelarRemision(Expediente expediente) { 
		cancelaExpedienteEnRemisionesPendientesEnvio(expediente);
		if (isRemision(expediente)) {	// Si esta en situacion de Remision sin tener remision pendiente
			try {
				new AccionCambioSituacionExpediente().doAccion(expediente, 
						(String) SituacionExpedienteEnumeration.Type.despacho.getValue(), true);
			} catch (AccionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

	public String doCancelarRemision() { 
		return doCancelarRemision(getExpedienteHome().getInstance());
	}

	public String doCancelarCuaderno() {
		try {
			new AccionCancelarCuaderno().doAccion(getExpedienteHome().getInstance());
			new AccionCambioEstadoExpediente().doAccion(getExpedienteHome().getInstance(), EstadoExpedientePredefinedEnumeration.Type.$CA.getValue().toString(), null, true);
			return "";
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_cancelar_cuaderno");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return null;
		}
	}


	public String doCancelarElevacionTO() {
		try {
			new AccionCancelarElevacionTO().doAccion(getExpedienteHome().getInstance());
			return "";
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_cancelar_elevacionTO");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return null;
		}
	}

	public String doCambioEnTramite(Expediente expediente, boolean enTramite) {
		try {
			new AccionCambioTramite().doAccion(expediente, enTramite);
			return "";
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_cambio_en_tramite");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return null;
		}
	}
	
	public String doCambioEnTramite() {
		try {
			new AccionCambioTramite().doAccion(getExpedienteHome().getInstance(), !getExpedienteHome().getInstance().isEnTramite());
			return "";
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_cambio_en_tramite");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return null;
		}
	}
	
	public String doCerrarCuaderno() {
		Expediente cuaderno = getExpedienteHome().getInstance();
		try {
			new AccionCerrarCuaderno().doAccion(cuaderno);
			new AccionCambioEstadoExpediente().doAccion(cuaderno, EstadoExpedientePredefinedEnumeration.Type.$CE.getValue().toString(), null, true);
			cuaderno.setEnTramite(false);
			getEntityManager().flush();
			reingresarEfectosDesgajados();
			getStatusMessages().addFromResourceBundle("expedienteManager.cerrarCuaderno.sucess", getIdxAnaliticoFirst(cuaderno));
			return "";
		} catch (AccionException e) {
			getLog().error("Error cerrando cuaderno", e);
			getStatusMessages().addFromResourceBundle(Severity.ERROR, "expedienteManager.cerrarCuaderno.error");
		}
		return null;
	}

	public String doReabrirCuaderno() {
		Expediente cuaderno = getExpedienteHome().getInstance();
		try {
			new AccionReabrirCuaderno().doAccion(cuaderno);
			new AccionCambioEstadoExpediente().doAccion(cuaderno, EstadoExpedientePredefinedEnumeration.Type.$IN.getValue().toString(), null, true);
			cuaderno.setEnTramite(true);
			getEntityManager().flush();
			getStatusMessages().addFromResourceBundle("expedienteManager.reabrirCuaderno.sucess", getIdxAnaliticoFirst(cuaderno));
			return "";
		} catch (AccionException e) {
			getLog().error("Error reabriendo cuaderno", e);
			getStatusMessages().addFromResourceBundle(Severity.ERROR, "expedienteManager.reabrirCuaderno.error");
		}
		return null;
	}

	public void reingresarEfectosDesgajados() {
		List<EfectoExp> listaEfectos = getEntityManager().createQuery("" +
			"from EfectoExp efectoExp where expediente.id = :idExpediente").
			setParameter("idExpediente", getExpedienteHome().getInstance().getId()).getResultList();
		
		for (EfectoExp efecto : listaEfectos) {
			
			EfectoExp efectoOrigen = efecto.getEfectoOrigen();
			efectoOrigen.setFechaEgreso(null);
			SituacionEfecto situacionEfectoOrigen = new SituacionEfecto();
			situacionEfectoOrigen.setFechaDeposito(new Date());
			situacionEfectoOrigen.setDepositadoEn("Depositado en Oficina (Reingreso)");
			if (!getEntityManager().contains(efectoOrigen)) {
				efectoOrigen = getEntityManager().find(EfectoExp.class, efectoOrigen.getIdEfecto());
			}
			situacionEfectoOrigen.setEfectoExp(efectoOrigen);
			getEntityManager().persist(situacionEfectoOrigen);
			getEntityManager().flush();
			
			efecto.setEfectoOrigen(efectoOrigen);
			efecto.setFechaEgreso(new Date());
			SituacionEfecto situacionCuaderno = new SituacionEfecto();
			situacionCuaderno.setFechaDeposito(new Date());
			situacionCuaderno.setDepositadoEn("Devuelto a la Oficina de Origen");
			if (!getEntityManager().contains(efecto)) {
				efecto = getEntityManager().find(EfectoExp.class, efecto.getIdEfecto());
			}
			situacionCuaderno.setEfectoExp(efecto);
			getEntityManager().persist(situacionCuaderno);
			getEntityManager().flush();

		}
	}

	public boolean incorporarCuadernoExpedienteOrigen(Expediente cuaderno) {
		UserTransaction tx = Transaction.instance();
		try {
			// Desmarcar flag de elevado en los intervinientes del cuaderno
			for (IntervinienteExp intervinienteExpCuaderno : cuaderno.getIntervinienteExpList()) {
				for (IntervinienteExp intervinienteExpOrigen : cuaderno.getExpedienteOrigen().getIntervinienteExpList()) {
					if (intervinienteExpCuaderno.getUuid().equals(intervinienteExpOrigen.getUuid())) {
						intervinienteExpOrigen.setElevado(false);
						getEntityManager().persist(intervinienteExpOrigen);
						break;
					}
				}
			}
			Expediente expedienteOrigen = cuaderno.getExpedienteOrigen();

			if(!getExpedienteManager().isEscrito(cuaderno)){ // los escritos no incorporan la caratula
				if (!getDespachoManager().incluirCaratula(cuaderno, expedienteOrigen)) {
					throw new RuntimeException(getMessages().get("expedienteManager.error_incorporando_caratula_cuaderno"));
				}
			}
			Integer tipoDocumento = (Integer)TipoDocumentoEnumeration.Type.despacho.getValue();
			List<DocumentoExp> despachoList = getEntityManager().createQuery("from DocumentoExp where expediente = :expediente and firma=1 and tipoDocumento = "+tipoDocumento+" and status <> -1 order by fojaInicial, fechaFirma")
				.setParameter("expediente", cuaderno)
				.getResultList();
			for (DocumentoExp despacho : despachoList) {
				if (!despacho.isLogicalDeleted() && despacho.getDocumentoExpOrigen()==null) {
					if (!getDespachoManager().incluirDespacho(despacho, expedienteOrigen, cuaderno)) {
						throw new RuntimeException(interpolate(getMessages().get("expedienteManager.error_incorporar_despacho_cuaderno"), despacho.getDescripcion(), despacho.getFojaInicial()));
					}
				}
			}
			actualizarIndexacion(cuaderno, true);
			getEntityManager().flush();
			return true;
		} catch (Exception e) {
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, getMessages().get("expedienteManager.error_transaccion_incorporar_cuaderno"));
			getLog().error("Error comprobando la transacción", e);
			try {
				tx.rollback();
			} catch (Exception e1) {
				getLog().error("Error deshaciendo la transacción", e1);
			}
		} 
		return false;
	}

	public boolean reabrirCuadernoExpedienteOrigen(Expediente cuaderno) {

		return true;
	}

	public boolean doDevolucionPrestamo(Expediente expediente) {
		boolean ret = false;
		PrestamoExp prestamoExp = getCurrentPrestamo(expediente);
		if(prestamoExp != null){
			prestamoExp.setFechaDevolucion(getCurrentDateManager().getCurrentDate());
		} 
		ret = true;
		ActuacionExpManager.instance().addActuacionDevolucionPrestamo(expediente, null, prestamoExp);
		return ret;
	}


	public PrestamoExp getCurrentPrestamo(Expediente expediente) {
		PrestamoExp ret = null;
		if(isPrestamo(expediente)){
			Query query = getEntityManager().createQuery("from PrestamoExp where expediente = :expediente and fechaDevolucion is null order by fechaPrestamo desc");
			query.setParameter("expediente", expediente);
			query.setMaxResults(1);
			List<PrestamoExp> list = query.getResultList();
			if(list.size()>0){
				ret = list.get(0);
			}
		}
		return ret;
	}
	
	public String getDescripcionEstadoPase(Expediente expediente) {
		StringBuilder builder = new StringBuilder();
		if (expediente.isPase()) {
			PaseExp pasePendiente = buscarPasePendiente(expediente);
			if (pasePendiente != null) {
				TipoPaseEnumeration tipoPaseEnumeration = (TipoPaseEnumeration) Component.getInstance(TipoPaseEnumeration.class, true);
				builder.append(tipoPaseEnumeration.getLabel(pasePendiente.getTipoPase()));
			} else {
				builder.append(getMessages().get("expedienteManager.pase"));
			}
			builder.append(" ");
//			if (expediente.isPendienteRecepcion()) {
//				builder.append(getMessages().get("expedienteManager.pendienteRecibir"));
//			}
		}
		return builder.toString();
	}

	public Date getFechaPase(Expediente expediente) {
		PaseExp paseExp = buscarPasePendiente(expediente);
		if (paseExp != null) {
			return paseExp.getFechaPase();
		}
		return null;
	}
	
	public PaseExp buscarPasePendiente(Expediente expediente) {
		return expediente.getPasePendiente();
	}

	public static PaseExp getUltimoPase(EntityManager entityManager, Expediente expediente) {
		PaseExp result = null;
		List<PaseExp> paseExpList = entityManager.createQuery("from PaseExp where expediente = :expediente and status <> -1 order by fechaPase desc")
		.setParameter("expediente", expediente)
		.getResultList();
		if (paseExpList.size() > 0) {
			result = paseExpList.get(0);
		}
		return result;
	}

	public PaseExp resolverPasePendiente(Expediente expediente) {
		PaseExp result = null;
		if (expediente != null && expediente.getId() != null) {
			List<PaseExp> paseExpList = getEntityManager().createQuery("from PaseExp where expediente = :expediente and fechaRecepcion is null and status <> -1 order by fechaPase desc")
			.setParameter("expediente", expediente)
			.getResultList();
			if (paseExpList.size() > 0) {
				result = paseExpList.get(0);
			}
		}
		return result;
	}
	
	public String getToolTipExpediente(){
		return getToolTipExpediente(getExpedienteHome().getInstance(), true);
	}

	public String getRutaExpediente(Expediente expediente){
		return getToolTipExpediente(expediente, false);	
	}
	
	public String getToolTipExpediente(Expediente expediente, boolean extended){
		if(isRemision(expediente)){
			return getRemisionInfo(expediente, EstadoRemesaEnumeration.Type.pendienteEnvio.getValue().toString(), extended);
		}else if(isPase(expediente)){
			return getRemisionInfo(expediente, EstadoRemesaEnumeration.Type.enviada.getValue().toString(), extended);
		}else{
			return "";
		}
	}
	
	public String getTitleExpediente(Expediente expediente){
		String ret = "";
		if (expediente != null) {
			String materia = "";
			String titleMessage = null;
			String msgFueraDeTramite = getMessages().get("expedienteCompleto.title.fuera_de_tramite");
			
			if (isLegajoPersonalidad(expediente)) {
				titleMessage = "expedienteCompleto.title.datosPersonales";
			} else {
				if(getEntityManager().contains( expediente) && (!isDespacho(expediente) || !isContentsVisible(expediente))){
					if(!isIniciado(expediente)){
						titleMessage = "expedienteCompleto.title.noIniciado";
					}else if(isPase(expediente)){
						titleMessage = "expedienteCompleto.title.pase";
					}else if(isRemision(expediente)){
						titleMessage = "expedienteCompleto.title.remision";
					}else if(!isDespacho(expediente)){
						titleMessage = SituacionExpedienteEnumeration.getTitleFromValue(expediente.getSituacion());
					}else if(!isContentsVisible(expediente)){
						titleMessage = "expedienteCompleto.title.reservado";
					}	
				}else{
					if(isDespacho(expediente)){
						titleMessage = SituacionExpedienteEnumeration.getTitleFromValue(expediente.getSituacion());
					}	
				}
				if (CamaraManager.isCamaraActualMultiMateria() && expediente.getMateria() != null) {
					materia = " - "+expediente.getMateria().getDescripcion()+" ";
				}
				if(isParalizado(expediente) && (expediente.getLegajoParalizado() != null)){
					return getMessages().get(titleMessage) + materia + "(" + expediente.getLegajoParalizado() + ")";
				}
				if(isArchivado(expediente) && (expediente.getLegajoArchivo() != null)){
					return getMessages().get(titleMessage) + materia + "(" + expediente.getLegajoArchivo() + ")";
				}
			}
			
			if (Strings.isEmpty(titleMessage))
				titleMessage = "expedienteCompleto.title";
			ret = getMessages().get(titleMessage) + materia;
			if(!expediente.isEnTramite()) 
				ret += " - "+msgFueraDeTramite;
			if(isRemision(expediente)){
				String detalleRemision = getToolTipExpediente(expediente, false);
				if(!Strings.isEmpty(detalleRemision))
					ret += " ["+detalleRemision+"]";
			}
			if(isParalizado(expediente) && (expediente.getLegajoParalizado() != null))
				return getMessages().get(titleMessage) + materia + "(" + expediente.getLegajoParalizado() + ") - " + msgFueraDeTramite;
			if(isArchivado(expediente) && (expediente.getLegajoArchivo() != null))
				return getMessages().get(titleMessage) + materia + "(" + expediente.getLegajoArchivo() + ") - " + msgFueraDeTramite;
			if(isExpedienteBorrado(expediente)){
				String msgBorrado = getMessages().get("expedienteCompleto.title.borrado");
				ret += " ("+msgBorrado+")";
			}
		}
		return ret;
	}

	private boolean isExpedienteBorrado(Expediente expediente) {
		return expediente.getStatus() == AbstractLogicalDeletion.DELETED_STATUS_FLAG;		
	}

	private List<RemesaExpedienteLinea> findRemesaExpedienteLineaListByEstado(Expediente expediente, String estadoRemesa) {
		List<RemesaExpedienteLinea> list =  getEntityManager().createQuery("from RemesaExpedienteLinea l  where expediente = :expediente and remesaExpediente.estado='"+estadoRemesa+"'")
		.setParameter("expediente", expediente).getResultList();
		return list;
	}
	
	public RemesaExpediente getRemesaPendienteEnvio(Expediente expediente) {
		List<RemesaExpedienteLinea> list =  findRemesaExpedienteLineaListByEstado(expediente, EstadoRemesaEnumeration.Type.pendienteEnvio.getValue().toString());
		if(list.size() > 0){
			return list.get(0).getRemesaExpediente();
		}
		return null;
	}
	
	public void cancelaExpedienteEnRemisionesPendientesEnvio(Expediente expediente){
		List<RemesaExpedienteLinea> list =  findRemesaExpedienteLineaListByEstado(expediente, 
				EstadoRemesaEnumeration.Type.pendienteEnvio.getValue().toString());
		for(RemesaExpedienteLinea linea: list){
			RemesaExpedienteManager.instance().deleteRemesaExpedienteLinea(linea);
		}
	}
	
	public String getRemisionInfo(Expediente expediente, String estadoRemision, boolean extended) {		
		List<RemesaExpedienteLinea> list =  findRemesaExpedienteLineaListByEstado(expediente, estadoRemision);
		String ret = "";
		if(list.size() == 1){
			ret = formatRemesaInfo(list.get(0).getRemesaExpediente(), estadoRemision, extended);
		}
		return ret;
	}
		
	private String formatRemesaInfo(RemesaExpediente r, String estadoRemision, boolean extended) {
		StringBuffer sb = new StringBuffer();
		OficinaEnumeration oficinaEnumeration = (OficinaEnumeration) Component.getInstance(OficinaEnumeration.class, true);
		String ruta = r.getDetalleRemesa();
		String destino = null;
		if(r.getOficinaDestino() != null){
			destino = oficinaEnumeration.labelFor(r.getOficinaDestino());
		}else if(r.getDependencia() != null){
			destino = DependenciaEnumeration.instance().labelFor(r.getDependencia());
		}
		String origen = oficinaEnumeration.labelFor(r.getOficinaOrigen());
		sb.append(Messages.instance().get("paseExp.ruta"));
		sb.append(": ");
		sb.append(ruta);
		
		if (!Strings.isEmpty(r.getUsuario())) {
			sb.append(": ").append(r.getUsuario());
		}
		if(extended){
			Date date = null;
			String msgLabel = null;
			if(EstadoRemesaEnumeration.Type.pendienteEnvio.getValue().toString().equals(estadoRemision)){			
				msgLabel = "remesaExpediente.fechaCreacion";
				date = r.getFechaCreacion();
			}else if(EstadoRemesaEnumeration.Type.enviada.getValue().toString().equals(estadoRemision)){			
				msgLabel = "remesaExp.fechaEnvio.shortLabel";
				date = r.getFechaEnvio();
			}
			if(msgLabel != null){
				sb.append("    ");
				sb.append(Messages.instance().get(msgLabel));
			}
			if(date != null){
				sb.append(": ");
				sb.append(simpleDateFormat.format(date));
			}
			sb.append("\n");
			sb.append(Messages.instance().get("remesaExpediente.oficinaOrigen.shortLabel"));
			sb.append(" : ");
			sb.append(origen);
			if(destino != null){
				sb.append("\n");
				sb.append(Messages.instance().get("remesaExpediente.oficinaDestino.shortLabel"));
				sb.append(" : ");
				sb.append(destino);
			}
		}
		return sb.toString();
	}

		
	public String getTitleExpediente(){
		return getTitleExpediente(getExpedienteHome().getInstance());
	}

	public IntervinienteExp getLastIntervinienteAdded() {
		return lastIntervinienteAdded;
	}


	public void setLastIntervinienteAdded(IntervinienteExp intervinienteExp) {
		this.lastIntervinienteAdded = intervinienteExp;
	}

	public String clip(int len, String s){
		String ret = s;
		if(s != null && s.length() > len){
			ret = s.substring(0, len)+"...";
		}
		return ret;
	}
	
	public EstadoExpediente getEstadoSinDefinir(Materia materia) {
		return getEstadoExpediente(ESTADO_SIN_DEFINIR_CODIGO, materia);
	}

	public EstadoExpediente getEstadoSinIniciar(Materia materia) {
		return getEstadoExpediente(ESTADO_SIN_INICIAR_CODIGO, materia);
	}
	
	public EstadoExpediente getEstadoIniciado(Materia materia) {
		return getEstadoExpediente(ESTADO_INICIADO_CODIGO, materia);
	}

	private EstadoExpediente getEstadoExpediente(String codigo, Materia materia) {
		return EstadoExpedienteEnumeration.getEstadoExpediente(getEntityManager(), codigo, materia);
	}
	
	public Document getDocumentoActual() {
		return documentoActual;
	}

	public void setDocumentoActual(Document documentoActual) {
		this.documentoActual = documentoActual;
		if (documentoActual != null) {
			this.documentCacheKey = EXPEDIENTE_NAME + UUID.randomUUID().toString();
			updateCache(documentoActual);
		}
	}

	private void updateCache(Document documentoActual) {
		CacheProvider cacheProvider = CacheProvider.instance();
		cacheProvider.put(this.documentCacheKey, documentoActual);
	}
	
	public void clear() {
		this.mostrarModalDatos = false;
		this.documentoActual = null;
		this.documentCacheKey = null;
		this.fechaCambio = null;
	}

	public void visualizarCaratula() {
		if (this.documentoActual != null) {
			updateCache(getDocumentoActual());
			setMostrarModalDatos(true);
		} else {
			getStatusMessages().addFromResourceBundle(Severity.ERROR, "despachoManager.errorNoDocumento");
		}
	}

	public boolean isMostrarModalDatos() {
		return mostrarModalDatos;
	}

	public void setMostrarModalDatos(boolean mostrarModalDatos) {
		this.mostrarModalDatos = mostrarModalDatos;
	}


	public String getDocumentCacheKey() {
		return documentCacheKey;
	}

	public void restoreFilters() {
		setDefaultListaExpedientesFilter();
		ExpedienteSearchState expedienteSearchState = ExpedienteSearchState.instance();
		expedienteSearchState.resetUserFilters(getExpedienteSearch());
		ExpedienteList expedienteList = (ExpedienteList) getComponentInstance(ExpedienteList.class);
		getExpedienteSearch().setPanelState("open");
		expedienteList.doSearch(null);
	}
	
	public void saveFilters() {
		ExpedienteSearchState expedienteSearchState = ExpedienteSearchState.instance();
		expedienteSearchState.saveUserFilters(getExpedienteSearch());
		getExpedienteSearch().setPanelState("open");
		ExpedienteList expedienteList = (ExpedienteList) getComponentInstance(ExpedienteList.class);
		expedienteList.doSearch(null);
	}
	
	public void setDefaultListaExpedientesFilter() {
		getExpedienteSearch().init();
		if (!getOficinaManager().isCurrentOficinaPenal() && ConfiguracionMateriaManager.instance().getBooleanProperty(SessionState.instance().getGlobalCamara(), null, ParametroConfiguracionMateriaEnumeration.Type.checkArchivadosParalizadosInactivo.getValue().toString(), false)) {
			
			if (getOficinaManager().isMesaDeEntrada(getSessionState().getGlobalOficina())) {
				getExpedienteSearch().setOcultarArchivados(false); 
				getExpedienteSearch().setOcultarParalizados(false);
			} else {
				getExpedienteSearch().setOcultarArchivados(true); 
				getExpedienteSearch().setOcultarParalizados(true);
			}
		} else {
			getExpedienteSearch().setOcultarArchivados(false); 
			getExpedienteSearch().setOcultarParalizados(false);
		}
		if (getOficinaManager().isApelaciones(getSessionState().getGlobalOficina())) {
			getExpedienteSearch().setTipoSubexpediente(null);
			getExpedienteSearch().setNaturalezaSubexpediente(null);
		}
		if (getOficinaManager().inAlgunaMesaDeEntrada()) {
			getExpedienteSearch().setSoloEnTramite(false);
		} 
		if (!ConfiguracionMateriaManager.instance().isMesaEntradaActiva(getSessionState().getGlobalCamara())) {
			getExpedienteSearch().setAmbitoCamaraDisabled(true);
			getExpedienteSearch().setMostrarTodosExpedientes(false);
			getExpedienteSearch().setSoloEnTramite(false);
		} else if (getOficinaManager().inAlgunaMesaDeEntrada()) {
			getExpedienteSearch().setMostrarTodosExpedientes(true);
		} 
	}

	public void setUsuarioFilters() {
		ExpedienteSearchState expedienteSearchState = ExpedienteSearchState.instance();
		
		expedienteSearchState.loadUserFilters(getExpedienteSearch());

	}
		
	public void doInitListaExpedientes() {
		getExpedienteSearch().setDesdeListaExpedientes(true);
		getExpedienteSearch().init();
		getExpedienteSearch().setFilterMediacion(false);
		initListaExpedientesSearch();
		getExpedienteSearch().updateFilters();
	}

	public void initListaExpedientesSearch() {
		getExpedienteSearch().setUseFilterByNumero(true);
		ExpedienteListState.instance().setShowList(false);
		setDefaultListaExpedientesFilter();
		
		setUsuarioFilters();
	}
	
	public void doInitListaExpedientesMediacion() {
		setDefaultListaExpedientesFilter();
		getExpedienteSearch().initForMediacion();
	}

	public void activateExpedientesMediacionFilter() {
		if(!getExpedienteSearch().isFilterMediacion()){
			setDefaultListaExpedientesFilter();
			getExpedienteSearch().initForMediacion();
		}
	}
	
	public void doInitListaExpedientesTramite() {
		getExpedienteSearch().setDesdeListaExpedientes(false);
		ExpedienteTramiteSearch expedienteTramiteSearch = (ExpedienteTramiteSearch) getComponentInstance(ExpedienteTramiteSearch.class);
		getExpedienteSearch().setUseFilterByNumero(true);
		expedienteTramiteSearch.setPanelState("open");
	}
	
	public void doInitAntiguedadExpedientesList(){
		AntiguedadExpedienteSearch antiguedadExpedienteSearch = (AntiguedadExpedienteSearch) getComponentInstance(AntiguedadExpedienteSearch.class);
		antiguedadExpedienteSearch.setPanelState("open");
	}
	public void doInitExpedientesAsignadosSecretariaCSJNList(){
		ExpedienteListState.instance().setShowList(false);
		ExpedientesAsignadosSecretariaCSJNSearch expedientesAsignadosSecretariaCSJNSearch = (ExpedientesAsignadosSecretariaCSJNSearch) getComponentInstance(ExpedientesAsignadosSecretariaCSJNSearch.class);
		expedientesAsignadosSecretariaCSJNSearch.setPanelState("open");
	}

	public void doInitVocaliasResponsablesExpedientesList(){
		VocaliasResponsablesExpedientesSearch vocaliasResponsablesExpedientesSearch = (VocaliasResponsablesExpedientesSearch) getComponentInstance(VocaliasResponsablesExpedientesSearch.class);
		vocaliasResponsablesExpedientesSearch.setPanelState("open");
	}
	
	public void doInitMisExpedientes() {
		getExpedienteSearch().setDesdeListaExpedientes(false);
		getExpedienteSearch().init();
		ExpedienteList expedienteList = (ExpedienteList) getComponentInstance(ExpedienteList.class);
		getExpedienteSearch().setUseFilterByNumero(true);
		expedienteList.setFilterByMisExpedientes(true); 
	}

	public void doInitUltimosExpedientesLeidos() {
		getExpedienteSearch().setDesdeListaExpedientes(false);
		getExpedienteSearch().init();
		ExpedienteList expedienteList = (ExpedienteList) getComponentInstance(ExpedienteList.class);
		getExpedienteSearch().setUseFilterByNumero(true);
		expedienteList.setFilterByUltimosExpedientesLeidos(true); 
	}
	
	public void doInitUltimosExpedientesModificados() {
		getExpedienteSearch().setDesdeListaExpedientes(false);
		getExpedienteSearch().init();
		ExpedienteList expedienteList = (ExpedienteList) getComponentInstance(ExpedienteList.class);
		getExpedienteSearch().setUseFilterByNumero(true);
		expedienteList.setFilterByUltimosExpedientesModificados(true); 
	}
	
	public void doInitExpedientesFavoritos() {
		getExpedienteSearch().setDesdeListaExpedientes(false);
		getExpedienteSearch().init();
		ExpedienteList expedienteList = (ExpedienteList) getComponentInstance(ExpedienteList.class);
		getExpedienteSearch().setUseFilterByNumero(true);
		expedienteList.setFilterByExpedientesFavoritos(true); 
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}


	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}


	public boolean isCambioSituacionDone() {
		return cambioSituacionDone;
	}


	public void setCambioSituacionDone(boolean cambioSituacionDone) {
		this.cambioSituacionDone = cambioSituacionDone;
	}

	public String getLocalizacionDestino(Expediente expediente) {
		if (Strings.isEmpty(expediente.getLocalizacion())) {
			if (expediente.getOficinaPase() != null) {
				return CamaraManager.getOficinaTitle(expediente.getOficinaPase());
			} else if (expediente.getOficinaActual() != null) {
				return CamaraManager.getOficinaTitle(expediente.getOficinaActual());
			} else {
				return "(no definido)";
			}
		} else {
			return expediente.getLocalizacion();
		}
	}
	
	public String getNumeroExpedienteAbreviado(Expediente expediente) {
		return getNumeroExpedienteAbreviado(expediente, true);
	}

	public String getNumeroExpedienteAbreviado(Expediente expediente, boolean eliminarCeros) {
		return getIdxAnaliticoFirst(expediente, true, eliminarCeros);
				}
	
	
	public boolean hayCedulasPorIncorporar(Expediente expediente){		
		
		String ejbql = null;
		Query query  = null;
		Boolean ret  = cedulasPorIncoporarMap.get(expediente.getId());
		OficinaManager oficinaManager =  OficinaManager.instance();
		
		if(ret == null){
	    	String tipoCedula = TipoCedulaEnumeration.Type.cedula.getValue().toString();
	    	
	    	if (oficinaManager.isOficinaMujer()) {
	    		ejbql = "select count(*) from CedulaExp where tipoCedula = '"+tipoCedula+"' and expediente.id = :id and fechaRetorno is not null and incorporada = false and status != -1";
	    		query = getEntityManager().createQuery(ejbql).setParameter("id", expediente.getId());
	    	}
	    	else {
	    		ejbql = "select count(*) from CedulaExp where tipoCedula = '"+tipoCedula+"' and expediente.id = :id and fechaRetorno is not null and incorporada = false and oficina in (:oficinas) and status != -1";
		    	query = getEntityManager().createQuery(ejbql).setParameter("id", expediente.getId());
				query.setParameter("oficinas", SessionState.instance().getGlobalOficinaList());
	    	}
			
			Long count = (Long) query.getSingleResult();
			ret = count > 0;
			cedulasPorIncoporarMap.put(expediente.getId(), ret);
		}
		return ret;
	}
	
	public boolean hayDocumentosEnBandeja(Expediente expediente){		
		Boolean ret = documentosEnBandejaMap.get(expediente.getId());
		if(ret == null){
			String ejbql = "select count(*) from DocumentoBandeja where expediente.id = :id and documentoExp is null and oficinaEntrada in (:oficinas) and status != -1";
			Query query = getEntityManager().createQuery(ejbql).setParameter("id", expediente.getId());
			query.setParameter("oficinas", SessionState.instance().getGlobalOficinaList());
			Long count = (Long) query.getSingleResult();
			ret = count > 0;
			documentosEnBandejaMap.put(expediente.getId(), ret);
		}
		return ret;
	}
	
	public void resetDocumentosEnBandejaMap(Expediente expediente){
		if(expediente != null){
			documentosEnBandejaMap.remove(expediente.getId());
		}
	}
	
	public boolean hayInformacionAdicional(Expediente expediente){
		Boolean ret = informacionAdicionalMap.get(expediente.getId());
		if(ret == null){
			String ejbql = "select count(*) from InfoAdicionalExp where expediente.id = :id and oficina in (:oficinas) and status != -1";
			Query query = getEntityManager().createQuery(ejbql);
			query.setParameter("id", expediente.getId());
			query.setParameter("oficinas", SessionState.instance().getGlobalOficinaList());
			Long count = (Long) query.getSingleResult();
			ret = count > 0;
			informacionAdicionalMap.put(expediente.getId(), ret);
		}
		return ret;
	}

	/*
	public boolean hayAlertas(Expediente expediente){
		return hayCedulasPorIncorporar(expediente) || hayDocumentosEnBandeja(expediente) || hayInformacionAdicional(expediente) || isUltimaVisitaNegativa(expediente) || hayPruebas(expediente)
			|| hayAlarmas(expediente);
	}
	*/
	
	public boolean hayAlertas(Expediente expediente){
		
		OmRespuestasHome omRespuestasHome = (OmRespuestasHome)Component.getInstance(OmRespuestasHome.class);
		boolean hayAlertas = hayCuestionarioOM(expediente) || hayCedulasPorIncorporar(expediente) || hayDocumentosEnBandeja(expediente) || hayInformacionAdicional(expediente) || isUltimaVisitaNegativa(expediente) || hayPruebas(expediente) || 
		                     omRespuestasHome.hayQueCompletarCuestPrincipal() && isIniciado(expediente);
	    
		return hayAlertas;
	}
	
	public boolean hayAlarmas(Expediente expediente) {
		Boolean ret = false;
		if (expediente.getId() != null) {
			ret = alarmasMap.get(expediente.getId());
			if (ret == null){
				ret = AlarmaManager.instance().getTodayAlarmaList(expediente, SessionState.instance().getGlobalOficina()).size() > 0;
				alarmasMap.put(expediente.getId(), ret);
			}
		}
		return ret;
	}

	public void resetAlarmasMap(Expediente expediente){
		if(expediente != null){
			alarmasMap.remove(expediente.getId());
		}
	}

	public String getCaratula() {
		return caratula;
	}


	public void setCaratula(String caratula) {
		this.caratula = caratula;
	}

	public String getComentarios() {
		return comentarios;
	}


	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}
	
	public String getDeclaracionHecho() {
		return declaracionHecho;
	}

	public void setDeclaracionHecho(String declaracionHecho) {
		this.declaracionHecho = declaracionHecho;
	}

	public boolean isDenunciaFirmada() {
		return denunciaFirmada;
	}

	public void setDenunciaFirmada(boolean denunciaFirmada) {
		this.denunciaFirmada = denunciaFirmada;
	}
	
	public PersonalOficina getInstructor() {
		return instructor;
	}

	public void setInstructor(PersonalOficina instructor) {
		this.instructor = instructor;
	}

	public String doModificarDatos() {
		if (getCaratula().length() > 500) {
			setCaratula(getCaratula().substring(0, 500));
		}
		asignarCaratula(getExpedienteHome().getInstance(), getCaratula(), null, null);
		getExpedienteHome().getInstance().setComentarios(getComentarios());
		getExpedienteHome().getInstance().setClaveFamiliaWeb(null);
		
		String ret = getExpedienteHome().update();
		if (ret != null) {
//			updatePreview();
			getExpedienteHome().setVisibleDialog("modificarDatos", false);
		}
		return ret;
	}


	private void asignarCaratula(Expediente expediente, String caratula, String codigoTipoInformacion, String detalle) {
		expediente.setCaratula(caratula);
		
		if (!Strings.isEmpty(codigoTipoInformacion)) {
			ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
			TipoInformacion tipoInformacion = TipoInformacionEnumeration.getItemByCodigo(getEntityManager(), codigoTipoInformacion, expediente.getMateria());
			String descripcion = truncate(caratula,200);
			actuacionExpManager.addInformacionExpediente(expediente, null, tipoInformacion, descripcion, null, null, SessionState.instance().getUsername(), detalle);
		}
	}

	public String doRecalcularCaratula() {
		setCaratula(calculaCaratula(getExpedienteHome().getInstance()));
		return "";
	}

	public String doCalcularConexidad() {
		try {
			new AccionCalcularConexidad().doAccion(getExpedienteHome().getInstance());
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_calculo_conexidad");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
		}
		return "";
	}

	public ExpedienteHome getExpedienteHome() {
		if(expedienteHome == null) {
			expedienteHome = (ExpedienteHome) Component.getInstance(ExpedienteHome.class, true);
		}
		return expedienteHome;
	}
	
	public boolean computeExpedienteBorrable(Expediente expediente) {
		if (isLegajoPersonalidad(expediente)) {
			return false;
		}
		boolean returnValue = true;
		int numCambioSituacion = 0;
		int numCambioEstado = 0;
		List<ActuacionExp> actuacionExpList =
			getEntityManager().createQuery("from ActuacionExp where expediente = :expediente order by fechaActuacion")
				.setParameter("expediente", expediente)
				.getResultList();
		for (ActuacionExp actuacionExp: actuacionExpList) {
			if (actuacionExp.getTipoActuacion().equals(TipoActuacionEnumeration.Type.cambio_Estado_Expediente.getValue())) {
				numCambioEstado++;
			} else if (actuacionExp.getTipoActuacion().equals(TipoActuacionEnumeration.Type.cambio_Situacion_Expediente.getValue())) {
				numCambioSituacion++;
			} else {
				returnValue = false;
				break;
			}
			if (numCambioSituacion > 1 || numCambioEstado > 1) {
				returnValue = false;
				break;
			}
		}
		if (returnValue) {
			Long count = 
				(Long) getEntityManager().createQuery("select count(*) from DocumentoExp where expediente = :expediente and status <> -1")
				.setParameter("expediente", expediente)
				.getSingleResult();
			if (count != null && count > 0) {
				returnValue = false;
			}
		}
		if (returnValue) {
			Long count = 
				(Long) getEntityManager().createQuery("select count(*) from Expediente where expedienteOrigen = :expediente and status <> -1")
				.setParameter("expediente", expediente)
				.getSingleResult();
			if (count != null && count > 0) {
				returnValue = false;
			}
		}
		return returnValue;
	}
	
	public boolean isExpedienteFamilia(Expediente expediente) {
		return getObjetoJuicioManager().isFamilia(expediente);
	}

	public boolean isCurrentExpedienteFamilia() {
		return isExpedienteFamilia(getExpedienteHome().getInstance());
	}

	public boolean isUsaPruebas(Expediente expediente) {
		return isLaboral(expediente) && !isNaturalezaCirculacion(expediente) && !isExpedienteAdministrativo(expediente);		
	}

	public boolean isUsaPruebasDocumentales(Expediente expediente) {
		return isSeguridadSocial(expediente) && !isNaturalezaCirculacion(expediente) && !isExpedienteAdministrativo(expediente);		
	}

	public boolean isVerCondenas(Expediente expediente) {
		return getObjetoJuicioManager().isFamilia(expediente);
	}

	public boolean isVerDatosMedicos(Expediente expediente) {
		return getObjetoJuicioManager().isFamilia(expediente);
	}

	public boolean isVerDatosPersonales(Expediente expediente) {
		return getObjetoJuicioManager().isFamilia(expediente) ||
		getObjetoJuicioManager().isCiudadania(expediente);
	}
	
	public boolean isVerGrupoIntervinientes(Expediente expediente) {
		return !isLegajoPersonalidad(expediente);
	}

	public boolean isVerGrupoDocumentos(Expediente expediente) {
		return !isExpedienteAdministrativo(expediente);
	}

	public boolean isVerGrupoAgenda(Expediente expediente) {
		return true;
	}

	public boolean isVerGrupoFamilia(Expediente expediente) {
		return isVerCondenas(expediente) || isVerDatosMedicos(expediente);		
	}

	public boolean isVerGrupoPrueba(Expediente expediente) {
		return (isLaboral(expediente) || isSeguridadSocial(expediente)) && !isNaturalezaCirculacion(expediente) && !isExpedienteAdministrativo(expediente);		
	}

	public boolean isVerGrupoCertPrueba(Expediente expediente) {
		boolean hide = isPenal(expediente) || isMediacion(expediente) || isLaboral(expediente) || isSeguridadSocial(expediente) || isNaturalezaCirculacion(expediente) || isExpedienteAdministrativo(expediente);
		return !hide;
	}
	
	public boolean isVerGrupoCedula(Expediente expediente) {
		return !isMediacion(expediente) && !isNaturalezaCirculacion(expediente) && !isExpedienteAdministrativo(expediente);		
	}
	
	public boolean isVerGrupoTasa(Expediente expediente) {
		return CamaraManager.isCamaraAdmiteTasaJusticia() && !isMediacion(expediente) && !isNaturalezaCirculacion(expediente) && !isExpedienteAdministrativo(expediente);		
	}

	public boolean isVerGrupoRecurso(Expediente expediente) {
		return !isLegajoPersonalidad(expediente) && !isMediacion(expediente) && !isNaturalezaCirculacion(expediente) && !isExpedienteAdministrativo(expediente);
	}

	public boolean isVerGrupoRecursoActual(Expediente expediente) {
		return isNaturalezaCirculacion(expediente);
	}
	
	public boolean isUltimaVisitaNegativa(Expediente expediente) {
		return expediente.isVisitaNegativa();
	}

	@SuppressWarnings("unchecked")
	public boolean computeVisitaNegativa(Expediente expediente) {
		
		List<IntervinienteVisitaExp> visitaList = getEntityManager()
			.createQuery("from IntervinienteVisitaExp where intervinienteExp.expediente.id = :expedienteId and status <> -1 order by fechaVisita desc")
			.setParameter("expedienteId", expediente.getId())
			.getResultList();
		if (visitaList.size() > 0) {
			return visitaList.get(0).getResultado().equals(ResultadoVisitaEnumeration.Type.Negativo.getValue());
		}
		return false;
	}

	public Date getFechaCambio() {
		return fechaCambio;
	}

	public void setFechaCambio(Date fechaCambio) {
		this.fechaCambio = fechaCambio;
	}

	public RecursoExp computeRecursoExp(Expediente expediente) {
		List<RecursoExp> recursoExpList = getEntityManager().createQuery("from RecursoExp  where expediente = :expediente and status <> -1 order by fechaPresentacion desc")
			.setParameter("expediente", expediente)
			.getResultList();
		if (recursoExpList.size() > 0) {
			return recursoExpList.get(0);
		}
		return null;
	}
	
	public List<RecursoExp> computeRecursosExpConClave(Expediente expediente) {
		List<RecursoExp> recursoExpList = getEntityManager().createQuery("from RecursoExp  where expediente = :expediente and status <> -1 and clave is not null order by fechaPresentacion desc")
			.setParameter("expediente", expediente)
			.getResultList();
		return recursoExpList;
	}

	public Oficina getOficinaElevacion() {
		return oficinaElevacion;
	}

	public void setOficinaElevacion(Oficina oficinaElevacion) {
		this.oficinaElevacion = oficinaElevacion;
	}

	
	public void addToExpedientesFavoritos(){
		if(getExpedienteHome().isManaged()){
			UsuarioExpedienteManager.instance().addExpedienteFavorito(getExpedienteHome().getInstance());
		}
	}

	public String getDetalleCambioEstadoExpediente() {
		return detalleCambioEstadoExpediente;
	}

	public void setDetalleCambioEstadoExpediente(
			String detalleCambioEstadoExpediente) {
		this.detalleCambioEstadoExpediente = detalleCambioEstadoExpediente;
	}

	public Date getFechaCambioEstadoExpediente() {
		return fechaCambioEstadoExpediente;
	}
	
	public void setFechaCambioEstadoExpediente(Date fecha) {
		this.fechaCambioEstadoExpediente = fecha;
	}
	
	public EstadoExpediente getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(EstadoExpediente movimiento) {
		this.movimiento = movimiento;
	}
	
	public EventoInfo getEventoInfo() {
		if (eventoInfo == null) {
			eventoInfo = new EventoInfo();
		}
		return eventoInfo;
	}

	public void setEvento(EventoInfo eventoInfo) {
		this.eventoInfo = eventoInfo;
	}

	@Transactional
	public boolean cambiarSecretaria(Expediente expediente,
			DocumentoExp documento, Oficina secretaria, Date fechaCambio) {

		try {
			boolean expedienteInJuzgado = false;
			// cambio de radicacion de secretaria 
			Oficina juzgadoActual = CamaraManager.getOficinaPrincipal(secretaria);
			Oficina oficinaActualExpediente = CamaraManager.getOficinaPrincipal(expediente.getOficinaActual());
			if (oficinaActualExpediente != null) {
				expedienteInJuzgado = equals(oficinaActualExpediente, juzgadoActual);
			}
			if(juzgadoActual != null){	
				OficinaAsignacionExp oficinaAsignacionExp = findRadicacionExpediente(expediente, juzgadoActual);// Se busca por el juzgado porque la radicacion puede ser juzgado o juzgadoPlenario
				if(oficinaAsignacionExp != null){
					if(equals(juzgadoActual, oficinaAsignacionExp.getOficina()) && !equals(secretaria, oficinaAsignacionExp.getSecretaria())){
						Oficina secretariaAnterior = oficinaAsignacionExp.getSecretaria();
						oficinaAsignacionExp.setSecretaria(secretaria);
						if (fechaCambio != null) {
							getActuacionExpManager().setFechaActuacionVirtual(fechaCambio);
						}
						if(secretariaAnterior != null) {
							getActuacionExpManager().addActuacionCambioSecretaria(expediente, secretaria, documento);
						} else {
							getActuacionExpManager().addActuacionAsignacionSecretaria(expediente, secretaria, documento);
						}
					}
				}
			}
			if (expedienteInJuzgado) {
			// cambio oficina actual
				expediente.setOficinaActual(secretaria);
			}
		} finally {
			getActuacionExpManager().setFechaActuacionVirtual(null);
		}
		getEntityManager().flush();
		return true;
	}

	public boolean cambiarFiscalia(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion, Integer fiscalia) {
		OficinaAsignacionExp oficinaAsignacionExp = findRadicacionExpediente(expediente, tipoRadicacion);

		if(oficinaAsignacionExp != null){
			Integer fiscaliaAnterior = oficinaAsignacionExp.getFiscalia();
			asignaFiscalia(oficinaAsignacionExp, fiscalia);
			if ((fiscaliaAnterior != null) && !fiscaliaAnterior.equals(fiscalia)) {
				getActuacionExpManager().addActuacionCambioFiscalia(oficinaAsignacionExp.getExpediente(), oficinaAsignacionExp.getFiscalia(), tipoRadicacion, null);
			}
			getEntityManager().flush();
		}		
		return true;
	}

	public boolean cambiarDefensoria(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion, Integer defensoria) {
		OficinaAsignacionExp oficinaAsignacionExp = findRadicacionExpediente(expediente, tipoRadicacion);

		if(oficinaAsignacionExp != null){
			Integer defensoriaAnterior = oficinaAsignacionExp.getFiscalia();
			oficinaAsignacionExp.setDefensoria(defensoria);
			if ((defensoriaAnterior != null) && !defensoriaAnterior.equals(defensoria)) {
				getActuacionExpManager().addActuacionCambioDefensoria(oficinaAsignacionExp.getExpediente(), oficinaAsignacionExp.getDefensoria(), tipoRadicacion, null);
			}
			getEntityManager().flush();
		}		
		return true;
	}

	public boolean cambiarMediador(Expediente expediente, Oficina oficinaSorteo, TipoRadicacionEnumeration.Type tipoRadicacion, Mediador mediador, String info) {
		OficinaAsignacionExp oficinaAsignacionExp = findRadicacionExpediente(expediente, tipoRadicacion);

		if(oficinaAsignacionExp != null){
			Mediador mediadorAnterior = oficinaAsignacionExp.getMediador();
			oficinaAsignacionExp.setMediador(mediador);
			if (!mediador.equals(mediadorAnterior)) {
				getActuacionExpManager().addActuacionCambioMediador(oficinaAsignacionExp.getExpediente(), oficinaAsignacionExp.getMediador(), tipoRadicacion, null, mediadorAnterior);
				boolean isComercial = isComercial(expediente);
				
				if (mediadorAnterior == null) {
					if(isComercial){
						/*******ATOS Comercial *******/
						if (!expediente.isExpedienteWeb() && ImpresionManager.instance().isHasColaImpresionCaratulasIngresadas(oficinaSorteo)) {
							imprimirEtiquetaMediacion(expediente, ImpresionManager.instance().getColaImpresionCaratulasIngresadasComercial(expediente, oficinaSorteo));
						}/*******ATOS Comercial *******/
						
					} else {
						if (ImpresionManager.instance().isHasColaImpresionCaratulasIngresadas(oficinaSorteo)) {
							imprimirEtiquetaMediacion(expediente, ImpresionManager.instance().getColaImpresionCaratulasIngresadas(oficinaSorteo));
						}
					}
				} else {
					if (ImpresionManager.instance().isHasColaImpresionCaratulas(oficinaSorteo)) {
						imprimirEtiquetaMediacion(expediente, ImpresionManager.instance().getColaImpresionCaratulas(oficinaSorteo));
					}
				}
			}
			if(mediadorAnterior != null){
				SorteadorMediador.decrementaCargaMediador(getEntityManager(), mediadorAnterior, expediente, SessionState.instance().getUsername(), info);
			}
			getEntityManager().flush();
		}		
		return true;
	}
	
	public void imprimirEtiquetaMediacion(Expediente expediente) {
		imprimirEtiquetaMediacion(expediente, ImpresionManager.instance().getColaImpresionCaratulas(SessionState.instance().getGlobalOficina()));
	}
	
	public void imprimirEtiquetaMediacion(Expediente expediente, String colaImpresion) {
		Document document = new Document();
		byte[] content = EtiquetaMediacionReportManager.instance().generarEtiquetaMediacion(expediente);
		document.setContent(content);
		document.setPdfContent(content);
		String filename = "Mediacion"+getIdxAnaliticoFirst(expediente);
		document.setDocumentFileName(filename.replace(' ', '$').replace('/', '_'));
		if (Strings.isEmpty(colaImpresion)) {
			JasperReportManager jasperReportManager = (JasperReportManager) Component.getInstance(JasperReportManager.class, true);
			jasperReportManager.download("etiquetaMediacion.pdf", "application/pdf", content);
		} else {
			try {
				ImpresionManager.instance().imprimirDocumento(document, colaImpresion);
				getStatusMessages().addFromResourceBundle("expediente.imprimirEtiquetaMediacion.ok", colaImpresion);
			} catch (IOException e) {
				getStatusMessages().addFromResourceBundle(Severity.ERROR, "expediente.imprimirEtiquetaMediacion.error", e.getMessage());
				getLog().error("Error imprimiendo carátula", e);
			}
		}
	}
	

	public String doCambioSecretaria() {
		initCambioSecretaria();
		getExpedienteHome().setVisibleDialog("cambioSecretaria", true);
		return "";
	}

	private void initCambioSecretaria() {
		this.cambioSecretaria = null;
	}

	public Oficina getCambioSecretaria() {
		return cambioSecretaria;
	}

	public void setCambioSecretaria(Oficina cambioSecretaria) {
		this.cambioSecretaria = cambioSecretaria;
	}

	public boolean doFinalizeCambioSecretaria() {
		boolean ret = true;
		Expediente valorAnterior = saveOficinaActualExpediente(getExpedienteHome().getInstance());
		if(cambioSecretaria != null){			
			try {
				new AccionCambioSecretaria().doAccion(getExpedienteHome().getInstance(), cambioSecretaria, null, null);
			} catch (AccionException e) {
				ret = false;
			}
		}
		if(ret){
			getExpedienteHome().setVisibleDialog("cambioSecretaria", false);
		}else{
			restoreOficinaActualExpediente(valorAnterior, getExpedienteHome().getInstance());
		}
		return ret;
	}
	
	private void restoreOficinaActualExpediente(Expediente save, Expediente instance) {
		instance.setOficinaActual(save.getOficinaActual());
	}
	
	public void doCancelCambioSecretaria() {
		getExpedienteHome().setVisibleDialog("cambioSecretaria", false);
	}

	public boolean computePenal(Materia materia) {
		return materia != null && CamaraManager.isPenal(materia);
	}

	public String computeObjetoJuicioDescription(
			Expediente expediente) {
		StringBuilder builder = new StringBuilder();
		if (expediente.getObjetoJuicioPrincipal() != null) {
			builder.append(expediente.getObjetoJuicioPrincipal().getDescriptionExpression());
		}
		return builder.toString();
	}
	
	public static boolean isTributario(String codigo) {
		
		if (codigo.startsWith("2_AT")){				
			return true;				
		
		}		
		return false;
	}

	public static boolean isTributario(Expediente expediente) {
		return isTributario(expediente.getOficinaActual().getCodigo());
	}

	public static String getNumeroJuzgado(Expediente expediente) {
		return getNumeroJuzgado(expediente.getOficinaActual());
	}

	public static String getNumeroJuzgado(Oficina oficina) {
		
		String codigo = oficina.getCodigo();
		if (codigo.length() >= 5) {
			codigo = oficina.getCodigo().substring(2, 5);

			if (isContencioso(codigo)) {
				if (isTributario(codigo)) {
					return codigo.substring(2);
				} else {
					return convertirCAF(codigo);
				}
			} else if (isCivilYComercial(codigo)) {
				return convertirCCF(codigo);
			} else if (isSalaCivil(codigo)) {
				return codigo.substring(6, 7);
			} else if (codigo != null) {
				int pos = codigo.lastIndexOf('_');
				if (pos != -1) {
					codigo = codigo.substring(pos + 1);
					while (codigo.startsWith("0")) {
						codigo = codigo.substring(1);
					}
				}
			}
		}
		return codigo;
	}
	
	private static boolean isSalaCivil(String codigo) {	
		return codigo.equals("SAL");
	}

	private static boolean isCivilYComercial(String codigo) {
		return (codigo.startsWith("V")) || (codigo.startsWith("CC"));
	}

	private static boolean isContencioso(String codigo) {
		return (codigo.startsWith("A")) || (codigo.startsWith("CA"));
	}

	private static String convertirCCF(String codigo) {
		
			if(codigo.equals("VJ1")){
			      return "1";
			}else if(codigo.equals("VJ2")){
				  return "2";
			}else if(codigo.equals("VJ3")){
			      return "3";
			}else if(codigo.equals("VJ4")){
			      return "4"; 
			}else if(codigo.equals("VJ5")){
			      return "5";
			}else if(codigo.equals("VJ6")){
			      return "6"; 
			}else if(codigo.equals("VJ7")){
			      return "7";
			}else if(codigo.equals("VJ8")){
			      return "8";  
			}else if(codigo.equals("VJ9")){
			      return "9";
			}else if(codigo.equals("VJA")){
			      return "10";  
			}else if(codigo.equals("VJB")){
			      return "11";
			}else if(codigo.equals("CC1")){
			      return "1";
			}else if(codigo.equals("CC2")){
			      return "2";
			}else if(codigo.equals("CC3")){
			      return "3";
			}else {
				return null;
			}			
		
	}

	private static String convertirCAF(String codigo) {	

		System.out.println(codigo.substring(0, 3));
		if (codigo.substring(0,3).startsWith("CA")) {	
			System.out.println(codigo.substring(2,3));
			return codigo.substring(2,3);
		}else{		
			return CamaraManager.eliminarCeros(codigo.substring(0,3));
		}
	}

	public boolean isLegajoPersonalidad(Expediente expediente) {
		if(expediente != null && expediente.getTipoSubexpediente() != null){
			return expediente.getTipoSubexpediente().getNaturalezaSubexpediente().equals(NaturalezaSubexpedienteEnumeration.Type.legajoPersonalidad.getValue());
		}else{
			return false;
		}
	}

	public static boolean isMediacion(Expediente expediente) {
		return NaturalezaSubexpedienteEnumeration.Type.mediacion.getValue().toString().equals(expediente.getNaturaleza());
	}
	
	public Boolean computePermiteEstados(Expediente expediente) {
		NaturalezaSubexpedienteEnumeration naturalezaSubexpedienteEnumeration = 
			(NaturalezaSubexpedienteEnumeration) getComponentInstance(NaturalezaSubexpedienteEnumeration.class);
		NaturalezaSubexpedienteEnumeration.Type type = null;
		if (expediente != null && expediente.getTipoSubexpediente() != null) {
			String naturalezaSubexpediente = expediente.getTipoSubexpediente().getNaturalezaSubexpediente();
			type = (NaturalezaSubexpedienteEnumeration.Type) naturalezaSubexpedienteEnumeration.getType(naturalezaSubexpediente);
		}
		if (type != null) {
			return type.isPermiteEstados();
		} else {
			return false;
		}
	}

	@Transactional
	public void asignarOficina(
			String usuario, 
			Expediente expediente, 
			RecursoExp recursoExp,
			Oficina oficina, 
			TipoRadicacionEnumeration.Type tipoRadicacion, 
			TipoAsignacionEnumeration.Type tipoAsignacion, 
			TipoAsignacionLex100Enumeration.Type tipoAsignacionLex100, 
			Oficina oficinaActual, 
			TipoGiroEnumeration.Type tipoGiro,
			boolean compensarOficinas,
			Sorteo sorteo,
			String rubro,
			String codigoTipoCambioAsignacion,
			String descripcionCambioAsignacion,
			String observaciones, 
			Date fechaAsignacion,
			FojasEdit fojasPase
			) throws Lex100Exception, AccionException {
		
		OficinaAsignacionExp oficinaAsignacionExp = null;
		boolean generarPase = true;
		
		if (expediente.isIngresoWeb() && !isIniciado(expediente) && !isRecepcionado(expediente) && OficinaManager.instance().isMesaEntradaCamara(oficinaActual)) {
			generarPase = false;
		}

		if(fechaAsignacion == null){
			fechaAsignacion = getCurrentDateManager().getCurrentDate();
		}

		if (expediente.getOficinaInicial() == null) {	// asignacion Inicial
			expediente.setOficinaInicial(oficina);
			expediente.setClave(getIdxAnalitico(expediente));
			expediente.setFechaSorteoCarga(fechaAsignacion);
		} else {
			if (expediente.getFechaSorteoCarga() == null) {
				expediente.setFechaSorteoCarga(fechaAsignacion);
			}
		}
		
		if (OficinaManager.instance().isApelaciones(oficina)) {
			expediente.setTieneRecursosSalaAsignadosPteResolver(null);
			if(recursoExp != null && RecursoExpManager.instance().isSala(recursoExp)){
				actualizarAsignacionRecursosSorteados(computeRecursosSalaAsignadosPteResolver(expediente), oficina, tipoAsignacion, rubro, fechaAsignacion);
				actualizarClaveRecurso(expediente, recursoExp, oficina, tipoAsignacion, rubro, fechaAsignacion);
			}else if (expediente.isTieneRecursosSalaPteAsignar()) {
				actualizarClaveRecursos(computeRecursosSalaPteAsignacion(expediente) , oficina, tipoAsignacion, rubro, fechaAsignacion); 
			}
		} else if (OficinaManager.instance().isCasacion(oficina)) {
			if(recursoExp != null && RecursoExpManager.instance().isCasacion(recursoExp)){
				actualizarAsignacionRecursosSorteados(computeRecursosCasacionAsignadosPteResolver(expediente), oficina, tipoAsignacion, rubro, fechaAsignacion);
				actualizarClaveRecurso(expediente, recursoExp, oficina, tipoAsignacion, rubro, fechaAsignacion);
			}else if (expediente.isTieneRecursosCasacionPteAsignar()) {
				actualizarClaveRecursos(computeRecursosCasacionPteAsignacion(expediente), oficina, tipoAsignacion, rubro, fechaAsignacion); 
			}
		} else if (OficinaManager.instance().isCorteSuprema(oficina)) {
			if(recursoExp != null && RecursoExpManager.instance().isCorteSuprema(recursoExp)){
				actualizarAsignacionRecursosSorteados(computeRecursosCasacionAsignadosPteResolver(expediente), oficina, tipoAsignacion, rubro, fechaAsignacion);
				actualizarClaveRecurso(expediente, recursoExp, oficina, tipoAsignacion, rubro, fechaAsignacion);
			}else if (expediente.isTieneRecursosCortePteAsignar()) {
				actualizarClaveRecursos(computeRecursosCasacionPteAsignacion(expediente), oficina, tipoAsignacion, rubro, fechaAsignacion); 
			}
		} else if (OficinaManager.instance().isSecretariaEspecial(oficina)) {
			if(recursoExp != null && RecursoExpManager.instance().isSecretariaEspecial(recursoExp)){
				actualizarAsignacionRecursosSorteados(computeRecursosSecretariaEspecialAsignadosPteResolver(expediente), oficina, tipoAsignacion, rubro, fechaAsignacion);
				actualizarClaveRecurso(expediente, recursoExp, oficina, tipoAsignacion, rubro, fechaAsignacion);
			}else if (expediente.isTieneRecursosSecretariaEspecialPteAsignar()) {
				actualizarClaveRecursos(computeRecursosSecretariaEspecialPteAsignacion(expediente), oficina, tipoAsignacion, rubro, fechaAsignacion); 
			}
		}
		// Radicacion
		OficinaAsignacionExp radicacionAnterior = null;
		if(tipoRadicacion != null){
			boolean descompensarAnterior = true;
			radicacionAnterior = getRadicacionByTipoRadicacion(expediente, tipoRadicacion);			
			Oficina oficinaAnterior =  radicacionAnterior != null ? radicacionAnterior.getOficinaConSecretaria() : null;
			String rubroAnterior =  radicacionAnterior != null ? radicacionAnterior.getRubro() : null;
			Boolean compensacionAnterior =  radicacionAnterior != null ? radicacionAnterior.getCompensado(): null;
			if(oficinaAnterior != null && oficinaAnterior.getId().equals(oficina.getId())){
				oficinaAnterior = null; // no debe descompensar ya que es ella misma (solo incrementa)
				descompensarAnterior = false;
			}
			if (TipoRadicacionEnumeration.isRadicacionVocalias(tipoRadicacion) || TipoRadicacionEnumeration.isRadicacionVocaliasCasacion(tipoRadicacion) || TipoRadicacionEnumeration.isRadicacionVocaliasEstudioCasacion(tipoRadicacion) ) {
				// El sorteo de vocalias no debe descompensar nunca
				oficinaAnterior = null;
				rubroAnterior = null;
				compensacionAnterior = null;
				descompensarAnterior = false;
				generarPase = false;
			}

			/** ATOS COMERCIAL */
			Integer fiscalia = null;
			if(!isComercial(expediente) || isMediacion(expediente)) {
				fiscalia = findMinisterioDeTurno(oficina, TipoOficinaTurnoEnumeration.Type.fiscalia.getValue().toString());
			}
			/** ATOS COMERCIAL */
			Integer defensoria = findMinisterioDeTurno(oficina, TipoOficinaTurnoEnumeration.Type.defensoria.getValue().toString());
			Integer asesoria = findMinisterioDeTurno(oficina, TipoOficinaTurnoEnumeration.Type.asesoria.getValue().toString());
			
			//agrega la radicacion
			oficinaAsignacionExp = setRadicacionExpediente(usuario, oficinaActual, expediente, recursoExp, oficina, tipoRadicacion, tipoAsignacion, tipoAsignacionLex100, fechaAsignacion, fiscalia, defensoria, asesoria, rubro, codigoTipoCambioAsignacion, observaciones, true);
			if(compensarOficinas && sorteo != null){
				enviarCompensacion(sorteo, TipoBolilleroSearch.getTipoBolilleroAsignacion(getEntityManager()), expediente, recursoExp, tipoRadicacion, oficinaAnterior, oficina, rubroAnterior, rubro, compensacionAnterior, descompensarAnterior, usuario, codigoTipoCambioAsignacion, descripcionCambioAsignacion, true);
			}
		}
		
		//ATOS COMERCIAL
		if (CamaraManager.isCamaraActualComercial() && ObjetoJuicioManager.isJuicioUniversal(expediente.getObjetoJuicio())) {
			tipoGiro = TipoGiroEnumeration.Type.remesa;
		}
		//ATOS COMERCIAL
		
		if (generarPase) {
			Oficina oficinaDestino = oficinaAsignacionExp != null ? oficinaAsignacionExp.getOficinaConSecretaria(): oficina;
			if (isAsignarOficinaSinPase(oficinaDestino.getCamara(), tipoRadicacion, expediente) ) {
				moverFamiliaSinIniciar(expediente, oficinaDestino);
			} else if (!oficinaDestino.equals(expediente.getOficinaActual())  || tipoGiro == null) { // Giro
				MotivoPase motivoPase = MotivoPaseEnumeration.instance().getMotivoPaseAsignacion();
				if ( (recursoExp != null) && RecursoExpManager.instance().isElevacionJuzgado(recursoExp) && (OficinaManager.instance().isSalaApelaciones(oficinaDestino))) {
					motivoPase = MotivoPaseEnumeration.instance().getMotivoPaseElevacion(recursoExp.isUrgente());
					if ((radicacionAnterior != null) && (oficinaDestino.equals(radicacionAnterior.getOficina()) || oficinaDestino.equals(radicacionAnterior.getOficinaConSecretaria()))) {
						MotivoPase motivoPaseElevacionPrevenida = MotivoPaseEnumeration.instance().getMotivoPaseElevacionPrevenida();
						if (motivoPaseElevacionPrevenida != null) {
							motivoPase = motivoPaseElevacionPrevenida;
						}
					}
				}
				new AccionPase().doAccion(expediente, null, getFojas(expediente, recursoExp, fojasPase), getPaquetes(expediente, recursoExp, fojasPase), getCuerpos(expediente, recursoExp, fojasPase), getAgregados(expediente, recursoExp, fojasPase), false, 
						(String)TipoPaseEnumeration.Type.giro.getValue(), motivoPase, getComentarioAgregados(expediente, recursoExp, fojasPase), oficinaDestino, null, fechaAsignacion, tipoGiro, oficinaActual, null);
				
				if (TipoAsignacionEnumeration.Type.asignacion_TO_Sorteo.equals(tipoAsignacion)) {
					doPaseExpedientesOrigen(expediente, motivoPase, oficinaDestino, fechaAsignacion, tipoGiro, oficinaActual);
				}
			}
		}
		if (TipoRadicacionEnumeration.isRadicacionCorteSuprema(tipoRadicacion) && (recursoExp != null)) {
			actualizarOrdenCirculacionPorTipoRecurso(recursoExp, oficina, tipoRadicacion);
		}
		//expediente.setStatusSorteo((Integer)StatusStorteoEnumeration.Type.sorteadoOk.getValue());
		getEntityManager().flush();

		ImpresionExpManager impresionExpManager = (ImpresionExpManager) Component.getInstance(ImpresionExpManager.class, true);
		impresionExpManager.imprimir(expediente, oficinaAsignacionExp, tipoRadicacion, oficinaActual, recursoExp);
		
		if (expediente.isIngresoWeb() &&  CamaraManager.isCamaraSeguridadSocial(expediente.getCamara())) {
			WebManager.instance().updateExpedienteWeb(expediente);
		}
	}
	
	/**
	 * Realiza el pase de los expedientes origen (incidentes, legajos, etc.) pertenecientes a una determinado Expediente
	 */
	private void doPaseExpedientesOrigen(Expediente expediente, MotivoPase motivoPase, Oficina oficinaDestino, Date fechaAsignacion, TipoGiroEnumeration.Type tipoGiro, Oficina oficinaActual) throws AccionException {
		List<Expediente> expedientesOrigen = expediente.getExpedienteList();
		if (expedientesOrigen.size()>0) {
			for (Expediente expedienteOrigen : expedientesOrigen) {
				new AccionPase().doAccion(expedienteOrigen, null, getFojas(expedienteOrigen, null, null), getPaquetes(expedienteOrigen, null, null), getCuerpos(expedienteOrigen, null, null), getAgregados(expedienteOrigen, null, null), false, 
						(String)TipoPaseEnumeration.Type.giro.getValue(), motivoPase, getComentarioAgregados(expedienteOrigen, null, null), oficinaDestino, null, fechaAsignacion, tipoGiro, oficinaActual, null);
			}
		}
	}

	public boolean isAsignarOrdenCirculacionEnabled() {
		if (CamaraManager.instance().isAsignaOrdenCirculacionSala(CamaraManager.getCamaraActual())) {
			OficinaAsignacionExp oficinaAsignacionExp = getExpedienteHome().getInstance().getRadicacionSala();
			if ((oficinaAsignacionExp != null) && (oficinaAsignacionExp.getCodigoOrdenCirculacion() == null)) {
				return true;
			}
		}
		return false;
	}

	public String doAsignarOrdenCirculacion(Expediente expediente, String returnPage) {
		OficinaAsignacionExp oficinaAsignacionExp = expediente.getRadicacionSala();
		if (oficinaAsignacionExp != null) {
			try {
				ExpedienteMeAsignacion expedienteMeAsignacion = ExpedienteMeAsignacion.instance();
				expedienteMeAsignacion.init();
				expedienteMeAsignacion.setTipoSorteo(TipoSorteo.ordenCirculacionSala);
				expedienteMeAsignacion.setExpediente(expediente);

				ExpedienteManager.instance().verificarAsignarOrdenCirculacion(null, SessionState.instance().getGlobalOficina(), SessionState.instance().getUsername(), 
						(TipoRadicacionEnumeration.Type) TipoRadicacionEnumeration.instance().getType(oficinaAsignacionExp.getTipoRadicacion()), oficinaAsignacionExp.getRubro(), oficinaAsignacionExp);
				return returnPage;
			} catch (Lex100Exception e) {
				getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, e.getMessage());
			} catch (Exception ex){
				getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, ex.getMessage());
			}
		}
		return null;
	}

	public void verificarAsignarOrdenCirculacion(Sorteo sorteoSala, 
			Oficina oficinaSorteadora, 
			String usuario,
			TipoRadicacionEnumeration.Type tipoRadicacion,
			String rubro,
			OficinaAsignacionExp oficinaAsignacionExp)
			throws Lex100Exception, Exception {
		// Orden de Circulacion
		
		Oficina oficina = oficinaAsignacionExp.getOficina();
		if(oficinaAsignacionExp.getCodigoOrdenCirculacion() == null &&
				CamaraManager.instance().isAsignaOrdenCirculacionSala(oficina.getCamara()) && 
				TipoRadicacionEnumeration.isRadicacionSala(tipoRadicacion)){
			
			if(CamaraManager.instance().isAsignaOrdenCirculacionRotativo(oficina.getCamara())){
				asignarSiguienteOrdenCirculacion(oficinaAsignacionExp);
				getEntityManager().flush();
			}else{
				Expediente expediente = oficinaAsignacionExp.getExpediente();
				if(!isPrincipal(expediente)){
					Expediente principal = getExpedientePrincipal(getEntityManager(), expediente);
					getLog().info("Realizando refresh de radicaciones por VERIFICACION DEL ORDEN DE CIRCULACION del expediente: " + expediente.getClave());
					OficinaAsignacionExpManager.instance().refreshRadicacionExpediente(principal);
					OficinaAsignacionExp  oficinaAsignacionExpPrincipal = findRadicacionExpediente(principal, tipoRadicacion);
					// copia el orden de circulacion de la asignacion de sala del padre si es la misma sala
					if ((oficinaAsignacionExpPrincipal != null) && (oficinaAsignacionExpPrincipal.getOficina() != null) &&  oficinaAsignacionExpPrincipal.getOficina().getId() == oficina.getId()){
						oficinaAsignacionExp.setCodigoOrdenCirculacion(oficinaAsignacionExpPrincipal.getCodigoOrdenCirculacion());
					}
				}
				if(oficinaAsignacionExp.getCodigoOrdenCirculacion() == null) {
					Sorteo sorteoOrdenCirculacion = MesaSorteo.instance().buscaSorteoOrdenCirculacion(oficinaSorteadora, tipoRadicacion);
					if(sorteoOrdenCirculacion != null){
						getEntityManager().flush();
						enviarSorteoOrdenCirculacion(sorteoSala, oficinaSorteadora, sorteoOrdenCirculacion, oficinaAsignacionExp.getExpediente(), tipoRadicacion, oficina, rubro, usuario);
					}
				}
			}
		}
	}
	
	private void actualizarOrdenCirculacionPorTipoRecurso(RecursoExp recursoExp, Oficina oficina, Type tipoRadicacion) {
		List<OrdenCirculacionTipoRecurso> ordenCirculacionTipoRecursoList =
		getEntityManager().createQuery("from OrdenCirculacionTipoRecurso where oficina = :oficina and tipoRecurso = :tipoRecurso")
			.setParameter("oficina", oficina)
			.setParameter("tipoRecurso", recursoExp.getTipoRecurso())
			.getResultList();
		if (ordenCirculacionTipoRecursoList.size() > 0) {
			OrdenCirculacionTipoRecurso ordenCirculacionTipoRecurso = ordenCirculacionTipoRecursoList.get(0);
			getEntityManager().createQuery("delete from OrdenCirculacionRecurso where recursoExp = :recursoExp")
				.setParameter("recursoExp", recursoExp)
				.executeUpdate();
			
			
			List<Oficina> oficinaList = RecursoExpManager.instance().getVocaliasDisponibles(recursoExp);
			StringTokenizer stringTokenizer = new StringTokenizer(ordenCirculacionTipoRecurso.getOrden(), ", ");
			int pos = 0;
			while (stringTokenizer.hasMoreTokens()) {
				String numero = stringTokenizer.nextToken();
				for (Oficina vocalia: oficinaList) {
					if (numero.equals(vocalia.getNumero())) {
						pos++;
						OrdenCirculacionRecurso ordenCirculacionRecurso = new OrdenCirculacionRecurso();
						ordenCirculacionRecurso.setRecursoExp(recursoExp);
						ordenCirculacionRecurso.setVocalia(vocalia);
						ordenCirculacionRecurso.setNumeroOrden(pos);
						ordenCirculacionRecurso.setPersonalVocal(PersonalOficinaManager.instance().getJuez(vocalia));
						getEntityManager().persist(ordenCirculacionRecurso);
					}
				}
			}
			
		}
	}

	@SuppressWarnings("unchecked")
	public void generarOrdenCirculacion(RecursoExp recursoExp) {
		List<OrdenCirculacionRecurso> ordenCirculacionRecursoList =
			getEntityManager().createQuery("from OrdenCirculacionRecurso where recursoExp = :recursoExp")
			.setParameter("recursoExp", recursoExp)
			.getResultList();
		if(ordenCirculacionRecursoList.size() == 0) {
			OficinaAsignacionExp oficinaAsignacionExp = null;
			Oficina oficina = recursoExp.getOficinaElevacion();
			if (oficina != null) {
				TipoRadicacionEnumeration.Type tipoRadicacion = TipoRadicacionEnumeration.calculateTipoRadicacionByTipoInstancia(oficina.getTipoInstancia().getCodigo());
				if (tipoRadicacion != null) {
					oficinaAsignacionExp = getOficinaAsignacionExp(recursoExp.getExpediente(), tipoRadicacion);
				}
			}
			if (oficinaAsignacionExp !=null){
				String codigoOrdenCirculacion = oficinaAsignacionExp.getCodigoOrdenCirculacion();
				if (!Strings.isEmpty(codigoOrdenCirculacion)) {
					String ordenCirculacion = OrdenCirculacionManager.instance().findOrdenCirculacionOficina(CamaraManager.getCamaraActual(),oficinaAsignacionExp.getOficina(),oficinaAsignacionExp.getCodigoOrdenCirculacion());
					if (!Strings.isEmpty(ordenCirculacion)){

						List<Oficina> oficinaList = RecursoExpManager.instance().getVocaliasDisponibles(recursoExp);
						StringTokenizer stringTokenizer = new StringTokenizer(ordenCirculacion, ", ");
						int pos = 0;
						while (stringTokenizer.hasMoreTokens()) {
							String numero = stringTokenizer.nextToken();
							for (Oficina vocalia: oficinaList) {
								if (numero.equals(vocalia.getNumero())) {
									pos++;
									OrdenCirculacionRecurso ordenCirculacionRecurso = new OrdenCirculacionRecurso();
									ordenCirculacionRecurso.setRecursoExp(recursoExp);
									ordenCirculacionRecurso.setVocalia(vocalia);
									ordenCirculacionRecurso.setNumeroOrden(pos);
									ordenCirculacionRecurso.setPersonalVocal(PersonalOficinaManager.instance().getJuez(vocalia));
									getEntityManager().persist(ordenCirculacionRecurso);
								}
							}
						}
						getEntityManager().flush();
					}
				}
			}
		}
	}

	private boolean isAsignarOficinaSinPase(Camara camara, TipoRadicacionEnumeration.Type tipoRadicacion, Expediente expediente) {
		return
			TipoRadicacionEnumeration.isAnyJuzgado(tipoRadicacion) &&
			!isIniciado(expediente) && 
			!ConfiguracionMateriaManager.instance().isGenerarPaseExpedientesSinIniciar(camara) && 
			!TipoCausaEnumeration.isIncompetenciaExternaNoInformatizada(expediente.getTipoCausa())
			;
	}

	private void moverExpedienteSinIniciar(Expediente expediente, Oficina oficina) {
		if(!oficina.equals(expediente.getOficinaActual())) {
			expediente.setOficinaActual(oficina);
		}
	}

	private void moverFamiliaSinIniciar(Expediente expediente, Oficina oficina) {
		List<Expediente> familiaExpediente = ExpedienteManager.getFamiliaDirectaExpediente(getEntityManager(), expediente);
		for(Expediente expedienteFamilia: familiaExpediente){
			if(!isIniciado(expedienteFamilia)){
				if(!TipoInstanciaEnumeration.isApelaciones(oficina.getTipoInstancia()) || expedienteFamilia.isTieneRecursosSalaPteAsignar()) {
					moverExpedienteSinIniciar(expedienteFamilia, oficina);
				}
			}
		}
	}
	
	public void actualizarAsignacionRecursosSorteados(List<RecursoExp> recursoExpList, Oficina oficina, TipoAsignacionEnumeration.Type tipoAsignacion, String rubro, Date fechaAsignacion) {
		for (RecursoExp recursoExp : recursoExpList) {
			if(isRecursoEnTramite(recursoExp) && isRecursoSinClave(recursoExp)){
				recursoExp.setOficinaElevacion(oficina);
				recursoExp.setFechaPresentacion(fechaAsignacion);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void actualizarClaveRecursos(List<RecursoExp> recursoExpList, Oficina oficina, TipoAsignacionEnumeration.Type tipoAsignacion, String rubro, Date fechaAsignacion) {
		for (RecursoExp recursoExp : recursoExpList) {
			actualizarClaveRecurso(recursoExp.getExpediente(), recursoExp, oficina, tipoAsignacion, rubro, fechaAsignacion);
		}
	}
		
	public boolean isRecursoEnTramite(RecursoExp recursoExp){
		return recursoExp.getEstadoResolucion() != null && EstadoResolucionRecursoEnumeration.Type.en_tramite.getValue().equals(recursoExp.getEstadoResolucion());
	}
	
	private boolean isRecursoSinClave(RecursoExp recursoExp){
		return Strings.isEmpty(recursoExp.getClave());
	}
	
	public void actualizarClaveRecurso(Expediente expediente, RecursoExp recursoExp, Oficina oficina, TipoAsignacionEnumeration.Type tipoAsignacion, String rubro, Date fechaAsignacion) {
		if(isRecursoEnTramite(recursoExp)){
			if(isRecursoSinClave(recursoExp)){
				String abr = "CA";
				String tipoSala = recursoExp.getTipoRecurso().getTipoSala();
				if (tipoSala != null){
					if(tipoSala.equals(TipoSalaResolucionRecursoEnumeration.Type.recurso_queja.getValue().toString()) ||
						tipoSala.equals(TipoSalaResolucionRecursoEnumeration.Type.recurso_queja_oex.getValue().toString()) ||
						tipoSala.equals(TipoSalaResolucionRecursoEnumeration.Type.recurso_queja_corte_suprema.getValue().toString()) ||
						tipoSala.equals(TipoSalaResolucionRecursoEnumeration.Type.recurso_queja_casacion.getValue().toString())){
						abr = "RH";
					}else if(tipoSala.equals(TipoSalaResolucionRecursoEnumeration.Type.recurso_salto_instancia_corte_suprema.getValue().toString())){
						abr = "RS";
					}else if(tipoSala.equals(TipoSalaResolucionRecursoEnumeration.Type.recurso_honorarios_corte_suprema.getValue().toString())){
						abr = "HO";
					}else if(tipoSala.equals(TipoSalaResolucionRecursoEnumeration.Type.intervencion.getValue().toString())){
						abr = "SE";
					}else if(tipoSala.equals(TipoSalaResolucionRecursoEnumeration.Type.recurso_casacion.getValue().toString())){
						if (CamaraManager.isCamaraCasacionFederal(oficina.getCamara())) {
							abr = "CFC";
						} else if (CamaraManager.isCamaraCasacionNacional(oficina.getCamara())) {
							abr = "CNC";
						} else {
							abr = "CC";
						}
					}else if(tipoSala.equals(TipoSalaResolucionRecursoEnumeration.Type.recurso_corte_suprema.getValue().toString()) ||
							 tipoSala.equals(TipoSalaResolucionRecursoEnumeration.Type.presentaciones_varias_con_retardo_de_justicia.getValue().toString()) ||
							 tipoSala.equals(TipoSalaResolucionRecursoEnumeration.Type.recurso_directo_corte_suprema.getValue().toString())){
						abr = "CS";
					}
				}
				recursoExp.setClave(RecursoExpManager.instance().calcularClaveRecurso(recursoExp, abr));
				recursoExp.setTipoAsignacion(tipoAsignacion != null ? tipoAsignacion.getValue().toString() : null);
				recursoExp.setRubro(rubro);				
				expediente.reset();
			}
			recursoExp.setOficinaElevacion(oficina);
			recursoExp.setFechaPresentacion(fechaAsignacion);
			getEntityManager().flush();
		}
	}
	

	public String enviarCompensacion(Sorteo sorteo,
			TipoBolillero tipoBolillero,
			Expediente expediente,
			RecursoExp recursoExp,
			TipoRadicacionEnumeration.Type tipoRadicacion, 
			Oficina oficinaAnterior,	
			Oficina oficinaNueva,
			String rubroAnterior,
			String rubroNuevo,
			Boolean compensacionAnterior,
			boolean descompensarAnterior,
			String usuario,
			String tipoCambio,
			String detalle,
			boolean verificarAsignarOrdenCirculacion) {
		
		Camara camara = SessionState.instance().getGlobalCamara();
		Oficina oficinaActual = SessionState.instance().getGlobalOficina();
		
		final SorteoParametros sorteoParametros = new SorteoParametros(SorteoOperation.compensacion, 
				camara, 
				expediente.getMateria(), 
				sorteo,
				tipoBolillero,
				rubroNuevo,
				tipoRadicacion, 
				null, 
				oficinaActual, 
				tipoCambio, 
				detalle,
				null,
				null,
				descompensarAnterior,
				expediente);	
		sorteoParametros.setOficinaAsignada(oficinaNueva);
		sorteoParametros.setUsuario(usuario);
		sorteoParametros.setRubroAnterior(rubroAnterior);
		sorteoParametros.setOficinaDesasignada(oficinaAnterior);
		sorteoParametros.setCompensacionAnterior(compensacionAnterior);
		sorteoParametros.setRecursoExp(recursoExp);
		sorteoParametros.setVerificarAsignarOrdenCirculacion(verificarAsignarOrdenCirculacion);
		Transaction.instance().registerSynchronization(new Synchronization() {
			@Override
			public void beforeCompletion() {
			}
			
			@Override
			public void afterCompletion(int status) {
				if (status == Status.STATUS_COMMITTED) {
					SorteoManager.instance().addExpedienteEntry(sorteoParametros, true);	
				}
			}
		});
		return null;
	}

	public String enviarSorteoOrdenCirculacion(Sorteo sorteoSala, Oficina oficinaActual, Sorteo sorteo, Expediente expediente,
			TipoRadicacionEnumeration.Type tipoRadicacion, 
			Oficina oficinaOrdenCirculacion,
			String rubro,
			String usuario) {
		
		Camara camara = SessionState.instance().getGlobalCamara();
		
		final SorteoParametros sorteoParametros = new SorteoParametros(SorteoOperation.sorteoOrdenCirculacion, 
				camara, 
				expediente.getMateria(), 
				sorteo,
				TipoBolilleroSearch.getTipoBolilleroAsignacion(getEntityManager()),
				rubro,
				tipoRadicacion, 
				null, 
				oficinaActual, 
				null, 
				null,
				null,
				null,
				false,
				expediente);	
		sorteoParametros.setSorteoDependiente(sorteoSala);
		sorteoParametros.setOficinaAsignada(oficinaOrdenCirculacion);
		sorteoParametros.setUsuario(usuario);
		Transaction.instance().registerSynchronization(new Synchronization() {
			@Override
			public void beforeCompletion() {
			}
			
			@Override
			public void afterCompletion(int status) {
				if (status == Status.STATUS_COMMITTED) {
					SorteoManager.instance().addExpedienteEntry(sorteoParametros, true);	
				}
			}
		});
		return null;
	}


	public OficinaAsignacionExp getOficinaAsignacionExp(Expediente expediente,  TipoRadicacionEnumeration.Type tipoRadicacion){
		return OficinaAsignacionExpSearch.findByTipoRadicacion(getEntityManager(), expediente, (String)tipoRadicacion.getValue());
	}

	public Oficina getOficinaByRadicacion(Expediente expediente,  TipoRadicacionEnumeration.Type tipoRadicacion){
		return getOficinaByRadicacion(getEntityManager(), expediente,  tipoRadicacion);
	}
	
	public OficinaAsignacionExp getRadicacionByTipoRadicacion(Expediente expediente,  TipoRadicacionEnumeration.Type tipoRadicacion){
		return OficinaAsignacionExpSearch.findByTipoRadicacion(getEntityManager(), expediente, (String)tipoRadicacion.getValue());
	}
	
	public static Oficina getOficinaByRadicacion(EntityManager entityManager, Expediente expediente,  TipoRadicacionEnumeration.Type tipoRadicacion){
		return getOficinaByRadicacion(entityManager, expediente, (String)tipoRadicacion.getValue());
	}

	public static Oficina getOficinaByRadicacion(EntityManager entityManager, Expediente expediente,  String tipoRadicacionValue){
		Oficina oficina = null;
		if(expediente != null && tipoRadicacionValue != null) {
			OficinaAsignacionExp oficinaAsignacionExp = OficinaAsignacionExpSearch.findByTipoRadicacion(entityManager, expediente, tipoRadicacionValue);
			if (oficinaAsignacionExp != null) {
				oficina = oficinaAsignacionExp.getOficinaConSecretaria();
			}
		}
		return oficina;
	}

	public static String getDescripcionOficinaByRadicacion(EntityManager entityManager, Expediente expediente,  String tipoRadicacionValue){
		String ret = null;
		if(expediente != null && tipoRadicacionValue != null) {
			OficinaAsignacionExp oficinaAsignacionExp = OficinaAsignacionExpSearch.findByTipoRadicacion(entityManager, expediente, tipoRadicacionValue);
			if (oficinaAsignacionExp != null) {
				ret = oficinaAsignacionExp.getDescripcionOficinaRadicacion();
			}
		}
		return ret;
	}

	public String getDescripcionOficinaByRadicacion(Expediente expediente,  String tipoRadicacionValue){
		return getDescripcionOficinaByRadicacion(getEntityManager(), expediente, tipoRadicacionValue);
	}
	

	public List<CambioAsignacionExp> findCambioAsignacionExpByOficinaAndCodigoTipoCambioAsignacion(EntityManager entityManager, Expediente expediente,	Oficina oficina, String codigoTipoCambioAsignacion) {
		return CambioAsignacionExpSearch.findByOficinaAndCodigoTipoCambioAsignacion(entityManager, expediente, oficina, codigoTipoCambioAsignacion);
	}
	
	public boolean hasIngresoJuzgadoTurno(EntityManager entityManager, Expediente expediente, Oficina oficina) {
		List<CambioAsignacionExp> list = findCambioAsignacionExpByOficinaAndCodigoTipoCambioAsignacion(entityManager, 
				expediente, 
				oficina, 
				TipoCambioAsignacionEnumeration.INGRESO_JUZGADO_TURNO);
		return list.size() > 0;
	}

	public List<CambioAsignacionExp> findCambioAsignacionExpByOficinaAndTipoAsignacion(EntityManager entityManager, Expediente expediente,	Oficina oficina, TipoAsignacionEnumeration.Type tipoAsignacion) {
		return CambioAsignacionExpSearch.findByOficinaAndTipoAsignacion(entityManager, expediente, oficina, tipoAsignacion);
	}
	
	public boolean hasIngresoAnteriorSistema(EntityManager entityManager, Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion, Oficina oficina) {
		List<CambioAsignacionExp> list = findCambioAsignacionExpByOficinaAndTipoAsignacion(entityManager, 
				expediente, 
				oficina, 
				TipoAsignacionEnumeration.getTipoAsignacionAnteriorAlSistema(tipoRadicacion));
		return list.size() > 0;
	}

	
	@Transactional
	public OficinaAsignacionExp setRadicacionExpediente(String usuario, 
				Oficina oficinaSorteo,
				Expediente expediente,
				RecursoExp recursoExp,
				Oficina oficina, 
				TipoRadicacionEnumeration.Type tipoRadicacion, 
				TipoAsignacionEnumeration.Type tipoAsignacion, 
				TipoAsignacionLex100Enumeration.Type tipoAsignacionLex100, 
				Date fechaAsignacion, 
				Integer fiscalia, 
				Integer defensoria, 
				Integer asesoria, 
				String rubro,
				String codigoTipoCambioAsignacion,
				String observaciones,
				boolean generaActuacion) {		

		Oficina oficinaAnterior = null;
		boolean isReasignacion = false;
		getEntityManager().joinTransaction();
		Oficina secretaria = null;
		if ((oficina.getTipoOficina() != null) && CodigoTipoOficinaEnumeration.isSecretaria(oficina.getTipoOficina().getCodigo())) {
			Oficina principal = CamaraManager.getOficinaPrincipal(oficina);
			if ((TipoRadicacionEnumeration.isAnyJuzgado(tipoRadicacion) && (principal.getTipoOficina() != null) && CodigoTipoOficinaEnumeration.isJuzgado(principal.getTipoOficina().getCodigo()))
				||
				(TipoRadicacionEnumeration.isAnySala(tipoRadicacion) && (principal.getTipoOficina() != null) && CodigoTipoOficinaEnumeration.isSalaApelacion(principal.getTipoOficina().getCodigo()))
				||
				(TipoRadicacionEnumeration.isRadicacionTribunalOral(tipoRadicacion) && (principal.getTipoOficina() != null) && CodigoTipoOficinaEnumeration.isTribunalOral(principal.getTipoOficina().getCodigo()))
				){
				secretaria = oficina;
				oficina = principal;
			}
		}
		if (TipoRadicacionEnumeration.isAnyJuzgado(tipoRadicacion) && (getCompetencia(expediente) != null) && ConfiguracionMateriaManager.instance().isAsignacionSecretariaPorCompetencia(SessionState.instance().getGlobalCamara())) {
			secretaria = OficinaManager.instance().getSecretariaPorCompentencia(oficina, getCompetencia(expediente));
		}
		if (TipoRadicacionEnumeration.isAnyJuzgado(tipoRadicacion)){
			// cambia competencia si procede
			Competencia competencia = OficinaManager.instance().getCompetencia(oficina);
			if(competencia != null){
				expediente.setCompetencia(competencia);
			}
		}
		OficinaAsignacionExp oficinaAsignacionExp = findRadicacionExpediente(getEntityManager(), expediente, tipoRadicacion);
		// Si cambia de camara, restaura radicaciones de la camara en curso desde el historico
		if(oficinaAsignacionExp != null && isCambioDeCamara(oficina, oficinaAsignacionExp.getOficina()) && !CamaraManager.isCamaraActualCasacion()){
			restauraOtrasRadicaciones(getEntityManager(), oficina.getCamara(), expediente, tipoRadicacion);
		}		
		if(TipoRadicacionEnumeration.isRadicacionVocalias(tipoRadicacion) || TipoRadicacionEnumeration.isRadicacionVocaliasCasacion(tipoRadicacion) || TipoRadicacionEnumeration.isRadicacionVocaliasEstudioCasacion(tipoRadicacion)) {
			// Las vocalias nunca reemplazan una radicacion siempre suman
			oficinaAsignacionExp = null;
		}
		if (oficinaAsignacionExp == null) {
			oficinaAsignacionExp = OficinaAsignacionExpManager.createNewAsignacion(usuario, oficinaSorteo, expediente, tipoRadicacion, 
					(String)tipoAsignacion.getValue(), (String)tipoAsignacionLex100.getValue(), TipoAsignacionOficinaEnumeration.Type.asignacion,
					fechaAsignacion, oficina, secretaria, fiscalia, defensoria, asesoria, rubro, observaciones, codigoTipoCambioAsignacion, oficinaAnterior);
			getEntityManager().persist(oficinaAsignacionExp);
		} else {
			isReasignacion = true;
			oficinaAnterior = oficinaAsignacionExp.getOficina();
			if (!oficina.equals(oficinaAnterior)) {
				oficinaAsignacionExp.setCodigoOrdenCirculacion(null);
			}
			oficinaAnterior = oficinaAsignacionExp.getOficinaConSecretaria();
			oficinaAsignacionExp.setOperador(usuario);
			oficinaAsignacionExp.setOficinaAsignadora(oficinaSorteo);
			oficinaAsignacionExp.setTipoAsignacion((String)tipoAsignacion.getValue());
			oficinaAsignacionExp.setTipoAsignacionLex100((String)tipoAsignacionLex100.getValue());
			oficinaAsignacionExp.setFechaAsignacion(fechaAsignacion);
			if (fiscalia != null){
				//limpia la referencia a la fiscalia y a la dependencia de la fiscalia en caso que sea distinta. Ejemplo de incompetencias en la misma camara en Penal
				if(oficinaAsignacionExp.getFiscalia()!=null && !oficinaAsignacionExp.getFiscalia().equals(fiscalia)){
					oficinaAsignacionExp.setDependenciaFiscalia(null);
					oficinaAsignacionExp.setFiscalFiscalia(null);
				}
				oficinaAsignacionExp.setFiscalia(fiscalia);
			}
			if (defensoria != null) oficinaAsignacionExp.setDefensoria(defensoria);
			if (asesoria != null) oficinaAsignacionExp.setAsesoria(asesoria);
			oficinaAsignacionExp.setOficina(oficina);
			oficinaAsignacionExp.setSecretaria(secretaria);
			oficinaAsignacionExp.setInstructor(null);
			oficinaAsignacionExp.setRubro(rubro);
			oficinaAsignacionExp.setComentarios(observaciones);
			oficinaAsignacionExp.setCompensado(false);
			oficinaAsignacionExp.setCodigoTipoCambioAsignacion(codigoTipoCambioAsignacion);
			oficinaAsignacionExp.setOficinaAnterior(oficinaAnterior);			
			oficinaAsignacionExp.setTipoAsignacionOficina(TipoAsignacionOficinaEnumeration.Type.reasignacion.getValue().toString());
		}
		
		// Actuacion
		if (generaActuacion) {
			CambioAsignacionExp cambioAsignacionExp = new CambioAsignacionExp(codigoTipoCambioAsignacion, oficinaAsignacionExp, oficinaAnterior);
			cambioAsignacionExp.setComentarios(observaciones);
			cambioAsignacionExp.setInfoSorteo(InfoSorteoManager.generaInfoEstadoExpedienteSorteado(expediente, cambioAsignacionExp, false));
			getEntityManager().persist(cambioAsignacionExp);
			ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
			if (cambioAsignacionExp.getFechaAsignacion() != null) {
				actuacionExpManager.setFechaActuacionVirtual(cambioAsignacionExp.getFechaAsignacion());
			}
			String detalle;
			if (TipoRadicacionEnumeration.isRadicacionVocalias(tipoRadicacion)) {
				detalle = ": "+CamaraManager.getOficinaPrincipal(oficina).getCodigoNumerico()+" "+oficina.getCodigoNumerico();
				PersonalOficina vocal = PersonalOficinaManager.instance().getJuez(oficina);
				if (vocal != null) {
					detalle += " "+vocal.getApellidosNombre();
				}
			} else {
				detalle = CamaraManager.getOficinaAbreviada(oficina);
			}
			try {
				actuacionExpManager.addCambioAsignacionExpediente(expediente, null, cambioAsignacionExp, detalle, recursoExp);
			} finally {
				actuacionExpManager.setFechaActuacionVirtual(null);
			}
		}
		if(isReasignacion) {
			if(TipoRadicacionEnumeration.Type.juzgado.equals(tipoRadicacion)) {
				Conexidad conexidadDetectada = getConexidadDetectada(expediente);
				if ((conexidadDetectada != null) && !TipoAsignacionEnumeration.TYPE_CONEXIDAD.equals(tipoAsignacion.getType())){
					conexidadDetectada.setPrincipal(false);
				}
			}
		}
		
		getLog().info("Calculando Fiscalia");
		OficinaAsignacionExpManager.getFiscaliaAsignada(oficinaAsignacionExp);
		getLog().info("Calculando Fiscal-Fiscalia");
		OficinaAsignacionExpManager.getFiscalFiscaliaAsignada(oficinaAsignacionExp);
		
		getEntityManager().flush();
		getLog().info("Refrescando el expediente: " + expediente.getClave() + " al asignar la radicacion");
		getEntityManager().refresh(expediente);

		/*
		// Camara de Apelaciones de Casacion Penal
		if (!isReasignacion && CamaraManager.instance().isCamaraActualApelacionCasacion() && TipoRadicacionEnumeration.isRadicacionVocaliasCasacion(tipoRadicacion)) {
			asignarVocaliasCasacion(expediente);
		}
		*/	
		
		// Camara de Apelaciones de Casacion Penal Ordinario
		// Al asignar sala se asignan las radicaciones de las vocalias dependientes de la misma
        if (!isReasignacion && CamaraManager.isCamaraActualCasacionPenalNacional() && TipoRadicacionEnumeration.isRadicacionSalaCasacion(tipoRadicacion)) {
            asignarVocaliasCasacion(expediente);
        }
		
		return oficinaAsignacionExp;
	}
	
	/**
	 * @param expediente
	 * @param radicacionBase
	 * @param tipoRadicacion
	 * @param tipoAsignacion
	 * @param tipoAsignacionLex100
	 * @return
	 */
	@Transactional
	public OficinaAsignacionExp setRadicacionExpedienteFromOther(EntityManager entityManager,
				Expediente expediente,
				OficinaAsignacionExp radicacionBase,
				TipoRadicacionEnumeration.Type tipoRadicacion) {		

		if(entityManager==null) entityManager = getEntityManager();
		
		//comprueba que este attached los objetos en el entity manager, sino los agrega
		expediente = solveMergedExpediente(entityManager, expediente);
		if(!entityManager.contains(radicacionBase))
			radicacionBase = entityManager.find(OficinaAsignacionExp.class, radicacionBase.getId());
		
		TipoAsignacionEnumeration.Type tipoAsignacion = (TipoAsignacionEnumeration.Type) TipoAsignacionEnumeration.instance().getType(radicacionBase.getTipoAsignacion());
		String usuario = radicacionBase.getOperador();
		Oficina oficinaSorteo = radicacionBase.getOficinaAsignadora();
		Oficina oficinaAnterior = radicacionBase.getOficinaAnterior();
		Oficina oficina = radicacionBase.getOficina();
		Oficina secretaria = radicacionBase.getSecretaria();
		Date fechaAsignacion = radicacionBase.getFechaAsignacion();
		Integer fiscalia = radicacionBase.getFiscalia();
		Integer defensoria = radicacionBase.getDefensoria();
		Integer asesoria = radicacionBase.getAsesoria();
		String rubro = radicacionBase.getRubro();
		String codigoTipoCambioAsignacion = radicacionBase.getCodigoTipoCambioAsignacion();
		String observaciones = radicacionBase.getComentarios();
		boolean isReasignacion = false;
		
		if (TipoRadicacionEnumeration.isAnyJuzgado(tipoRadicacion)){
			// cambia competencia si aplica
			Competencia competencia = radicacionBase.getExpediente().getCompetencia();
			if(competencia != null) expediente.setCompetencia(competencia);
		}
		
		OficinaAsignacionExp oficinaAsignacionExp = findRadicacionExpediente(entityManager, expediente, tipoRadicacion);
		// Si cambia de camara, restaura radicaciones de la camara en curso desde el historico o elimina las existentes en caso que no posea
		if(oficinaAsignacionExp != null && isCambioDeCamara(oficina, oficinaAsignacionExp.getOficina()) && !CamaraManager.isCamaraActualCasacion())
			restauraOtrasRadicaciones(entityManager, oficina.getCamara(), expediente, tipoRadicacion);
		
		if(TipoRadicacionEnumeration.isRadicacionVocalias(tipoRadicacion) || TipoRadicacionEnumeration.isRadicacionVocaliasCasacion(tipoRadicacion) || TipoRadicacionEnumeration.isRadicacionVocaliasEstudioCasacion(tipoRadicacion)) {
			// Las vocalias nunca reemplazan una radicacion siempre insertan una nueva
			oficinaAsignacionExp = null;
		}
		if (oficinaAsignacionExp == null) {
			//creo nueva radicacion porque no posee de esa instancia
			oficinaAsignacionExp = OficinaAsignacionExpManager.createNewAsignacion(usuario, oficinaSorteo, expediente, tipoRadicacion,
					(String)tipoAsignacion.getValue(), radicacionBase.getTipoAsignacionLex100(), TipoAsignacionOficinaEnumeration.Type.asignacion, fechaAsignacion,
					oficina, secretaria, fiscalia, defensoria, asesoria, rubro, observaciones, codigoTipoCambioAsignacion, oficinaAnterior);
			
			entityManager.persist(oficinaAsignacionExp);
		} else {
			//actualizo la radicacion que ya posee de esa instancia
			isReasignacion = true;
			OficinaAsignacionExpManager.copyAsignacionFromOther(oficinaAsignacionExp, radicacionBase);
		}
		
		if(isReasignacion) {
			if(TipoRadicacionEnumeration.Type.juzgado.equals(tipoRadicacion)) {
				Conexidad conexidadDetectada = getConexidadDetectada(expediente);
				if ((conexidadDetectada != null) && !TipoAsignacionEnumeration.TYPE_CONEXIDAD.equals(tipoAsignacion.getType())){
					conexidadDetectada.setPrincipal(false);
				}
			}
		}
		entityManager.flush();

		// Al asignar sala se asignan las radicaciones de las vocalias dependientes de la misma
        if (!isReasignacion && CamaraManager.isCamaraActualCasacionPenalNacional() && TipoRadicacionEnumeration.isRadicacionSalaCasacion(tipoRadicacion)) {
            asignarVocaliasCasacion(expediente);
        }
        
        //se manda a imprimir la caratula del expediente (incidente) con la nueva radicacion seteada
        try{
        	ImpresionExpManager impresionExpManager = (ImpresionExpManager) Component.getInstance(ImpresionExpManager.class, true);
        	impresionExpManager.imprimirCaratulaIncidente(expediente, expediente.getOficinaRadicacionPrincipal(), expediente.getOficinaActual());
        }catch(Exception e){
        	getLog().error("No fue posible imprimir las caratulas al realizar la asignacion de la radicacion a partir de una radicacion base", e);
        }
        
		return oficinaAsignacionExp;
	}
	
	/**
	 * @param oficinaAsignacionExp
	 * @param tipoRadicacion
	 * @param expediente
	 * @param isReasignacion
	 */
	public void copiarRadicacionesFamilia(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion){
		if(tipoRadicacion!=null){
			OficinaAsignacionExp oficinaAsignacionExp = findRadicacionExpediente(getEntityManager(), expediente, tipoRadicacion);
			boolean isReasignacion = (oficinaAsignacionExp!=null);
			
			if(!TipoRadicacionEnumeration.isRadicacionVocalias(tipoRadicacion) && !TipoRadicacionEnumeration.isRadicacionVocaliasCasacion(tipoRadicacion)) {
				if(isExpedienteBase(expediente))
					copiaRadicacionDesdePrincipal(tipoRadicacion, oficinaAsignacionExp, expediente, isReasignacion);
				if(!isPrincipal(expediente)){
					copiaRadicacionAlPrincipal(tipoRadicacion, oficinaAsignacionExp, expediente);
					if (TipoRadicacionEnumeration.isRadicacionCorteSuprema(tipoRadicacion))
						copiaRadicacionACirculaciones(tipoRadicacion, oficinaAsignacionExp, expediente, isReasignacion);
				}
			}
		}
	}
	
//	/**
//	 * @param expedientePadre
//	 * @param expediente
//	 * @param tipoRadicacion
//	 */
//	public void copiarRadicacionesFamilia(Expediente expedientePadre, Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion){
//		if(tipoRadicacion!=null){
//			OficinaAsignacionExp oficinaAsignacionExpPadre = getExpedienteManager().getRadicacionByTipoRadicacion(expedientePadre, tipoRadicacion);
//			if(oficinaAsignacionExpPadre!=null)
//				getExpedienteManager().copiarRadicacionesFamilia(oficinaAsignacionExpPadre, expediente, tipoRadicacion);
//		}
//	}

    public void asignarVocaliasCasacion(Expediente expediente) {
        List<Oficina> vocalias = expediente.getVocaliasCasacion();
        OficinaAsignacionExp radicacionSalaCasacion = expediente.getRadicacionCasacion();
        List<OficinaAsignacionExp> radicacionesVocalia = expediente.getRadicacionesVocaliaCasacion();

        if (radicacionesVocalia.size() == 0) {
            for (Oficina vocalia: vocalias) {
                OficinaAsignacionExp oficinaAsignacionExp = new OficinaAsignacionExp(TipoAsignacionOficinaEnumeration.Type.asignacion.getValue().toString(), 
                        radicacionSalaCasacion.getFechaAsignacion(), expediente);
                oficinaAsignacionExp.setOficina(vocalia);
                oficinaAsignacionExp.setCodigoTipoCambioAsignacion(radicacionSalaCasacion.getCodigoTipoCambioAsignacion());
                oficinaAsignacionExp.setOficinaAsignadora(radicacionSalaCasacion.getOficinaAsignadora());
                oficinaAsignacionExp.setRubro("VOC");
                oficinaAsignacionExp.setTipoAsignacion(radicacionSalaCasacion.getTipoAsignacion());
                oficinaAsignacionExp.setTipoAsignacionLex100(radicacionSalaCasacion.getTipoAsignacionLex100());
                oficinaAsignacionExp.setTipoAsignacionOficina(radicacionSalaCasacion.getTipoAsignacionOficina());
                oficinaAsignacionExp.setTipoRadicacion(TipoRadicacionEnumeration.Type.vocaliasCasacion.getValue().toString());
                oficinaAsignacionExp.setOperador(radicacionSalaCasacion.getOperador());
                getEntityManager().persist(oficinaAsignacionExp);
            }
            getEntityManager().flush();
            expediente.setRadicacionesVocalia(null);
            expediente.setRadicacionesVocaliaCasacion(null);
            expediente.setRadicacionesVocaliaEstudioCasacion(null);
        }

    }

	public String calculaSiguienteOrdenCirculacion(OficinaAsignacionExp oficinaAsignacionExp) {
		Oficina oficina = oficinaAsignacionExp.getOficina();
		String codigo = null;
		try {
			codigo = OrdenCirculacionManager.instance().getSiguienteCodigoOrdenCirculacion(oficina.getCamara(), oficina, oficinaAsignacionExp.getRubro());
		} catch (Lex100Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return codigo;
	}

	private void restauraOtrasRadicaciones(EntityManager entityManager, Camara camara, Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacionActual) {
		for(TipoRadicacionEnumeration.Type tipoRadicacion: TipoRadicacionEnumeration.Type.values()){
			if (TipoRadicacionEnumeration.isAnyJuzgado(tipoRadicacion) ||
				TipoRadicacionEnumeration.isAnySala(tipoRadicacion) ||
				TipoRadicacionEnumeration.isRadicacionTribunalOral(tipoRadicacion) ||
				TipoRadicacionEnumeration.isRadicacionJuzgadoPlenario(tipoRadicacion)) {
				OficinaAsignacionExp radicacion =  findRadicacionExpediente(entityManager, expediente, tipoRadicacion);
				if(radicacion != null) {
					if(!tipoRadicacionActual.getValue().equals(radicacion.getTipoRadicacion())) {
						CambioAsignacionExp cambioAsignacionExp = CambioAsignacionExpSearch.findLastByTipoRadicacionAndCamara(entityManager, expediente, camara, (String)tipoRadicacion.getValue());
						if(cambioAsignacionExp != null) {
							radicacion.updateFrom(cambioAsignacionExp, false);
						} else {
							entityManager.remove(radicacion);
						}
					} else { // limpio la radicacion que se va a asignar
						radicacion.setFiscalia(null);
						radicacion.setDefensoria(null);
						radicacion.setAsesoria(null);
						radicacion.setDependenciaFiscalia(null);
						radicacion.setFiscalFiscalia(null);
					}
				} else {
					CambioAsignacionExp cambioAsignacionExp = CambioAsignacionExpSearch.findLastByTipoRadicacionAndCamara(entityManager, expediente, camara, (String)tipoRadicacion.getValue());
					if(cambioAsignacionExp != null) {
						OficinaAsignacionExp radicacionCamara = new OficinaAsignacionExp(cambioAsignacionExp);
						entityManager.persist(radicacionCamara);
					}
				}
			}
		}
		entityManager.flush();
	}
	
	private boolean isCambioDeCamara(Oficina oficinaNueva, Oficina oficinaAnterior) {
		return oficinaNueva != null && oficinaAnterior != null && !equals(oficinaNueva.getCamara(), oficinaAnterior.getCamara()); 
	}
	
	/**
	 * Realiza la copia de la radicacion de un expediente a sus hijos en segundo plano
	 * @param tipoRadicacion tipo de radicacion
	 * @param oficinaAsignacionExp radicacion a copiar
	 * @param expediente expediente base
	 * @param isReasignacion
	 */
	public void copiaRadicacionDesdePrincipal(TipoRadicacionEnumeration.Type tipoRadicacion, OficinaAsignacionExp oficinaAsignacionExp, Expediente expediente, boolean isReasignacion){
		//si la radicacion es null es porque no se debe copiar radicacion, sino que por ejemplo se esta eliminando una
		if(isExpedienteBase(expediente) && oficinaAsignacionExp!=null){
			//crea proceso de copia de radicaciones en background solo si posee algun hijo
			List<Expediente> vinculadosDirectos = expediente.getExpedienteList();
			String username = SessionState.instance().getUsername();
			//limpia las tareas finalizadas del usuario
			MesaEntradaDelivery.clean(username);
			if(vinculadosDirectos!=null && vinculadosDirectos.size()>0){
				//en caso de ser reasignacion o no ser expediente nuevo, entonces se hace el copiado async, sino se hace sync. Esto sirve para la impresion de caratulas en civil
				//ya que alli se crea principal e incidente en el momento y hay que esperar que copie la radicacion para luego mandar a imprimir
				boolean isAsignacionExpedienteNuevo = (oficinaAsignacionExp != null) && TipoAsignacionOficinaEnumeration.Type.asignacion.getValue().equals(oficinaAsignacionExp.getTipoAsignacionOficina());
				if(!isAsignacionExpedienteNuevo)
					new MesaEntradaDelivery(expediente).sendTaskPropagarRadicacionesAsync(tipoRadicacion, oficinaAsignacionExp, username, isReasignacion);
				else
					this.propagarRadicacionesSync(vinculadosDirectos, oficinaAsignacionExp, tipoRadicacion);
			}
		}
	}
	
	/**
	 * realiza el copiado de la radicacion en forma sincronica a los expedientes vinculados directos.
	 * Es para los casos de ingreso de expedientes nuevos junto con incidentes
	 * 
	 * @param vinculadosDirectos
	 * @param oficinaAsignacionExp
	 * @param tipoRadicacion
	 */
	private void propagarRadicacionesSync(List<Expediente> vinculadosDirectos, OficinaAsignacionExp oficinaAsignacionExp, TipoRadicacionEnumeration.Type tipoRadicacion){
		for (Expediente vinculado : vinculadosDirectos) {
			setRadicacionExpedienteFromOther(getEntityManager(), 
					vinculado,
					oficinaAsignacionExp,
					tipoRadicacion);					
			
		}
	}

	public void copiaRadicacionACirculaciones(TipoRadicacionEnumeration.Type tipoRadicacion, OficinaAsignacionExp oficinaAsignacionExp, Expediente expediente, boolean isReasignacion){
		if(oficinaAsignacionExp!=null){
			for (Expediente circulacion : getCirculaciones(getEntityManager(), expediente)) {
				if (circulacion.getNaturaleza() != null && circulacion.getNaturaleza().equals(NaturalezaSubexpedienteEnumeration.Type.circulacion.getValue().toString())) {
					if (isReasignacion || (getOficinaAsignacionExp(circulacion, tipoRadicacion) == null)){
						setRadicacionExpediente(oficinaAsignacionExp.getOperador(), 
								oficinaAsignacionExp.getOficinaAsignadora(), 
								circulacion,
								null,
								oficinaAsignacionExp.getOficinaConSecretaria(),
								tipoRadicacion,
								(TipoAsignacionEnumeration.Type)TipoAsignacionEnumeration.instance().getType(oficinaAsignacionExp.getTipoAsignacion()),
								TipoAsignacionLex100Enumeration.Type.copiaDesdePrincipal,
								oficinaAsignacionExp.getFechaAsignacion(), 
								oficinaAsignacionExp.getFiscalia(), 
								oficinaAsignacionExp.getDefensoria(), 
								oficinaAsignacionExp.getAsesoria(), 
								oficinaAsignacionExp.getRubro(), 
								null, 
								oficinaAsignacionExp.getComentarios(), false);
					}
				}
			}
		}
	}		

	public boolean isAnySubexpedienteNaturaleza(Expediente instance){
		return !isMediacion(instance) && !isNaturalezaPrincipal(instance) && !isNaturalezaTribunalOral(instance) && !isNaturalezaJuzgadoPlenario(instance) && !isNaturalezaLegajoPersonalidad(instance);
	}
	
	public boolean isNaturalezaLegajo(Expediente instance) {
		return NaturalezaSubexpedienteEnumeration.Type.legajo.getValue().equals(getNaturaleza(instance));
	}
	
	public boolean isNaturalezaIncidente(Expediente instance) {
		return NaturalezaSubexpedienteEnumeration.Type.incidente.getValue().equals(getNaturaleza(instance));
	}

	public boolean isNaturalezaTribunalOral(Expediente instance) {
		return NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue().equals(getNaturaleza(instance));
	}
	
	public boolean isNaturalezaJuzgadoPlenario(Expediente instance) {
		return NaturalezaSubexpedienteEnumeration.Type.juzgadoPlenario.getValue().equals(getNaturaleza(instance));
	}

	public boolean isNaturalezaPrincipal(Expediente instance) {
		return NaturalezaSubexpedienteEnumeration.Type.principal.getValue().equals(getNaturaleza(instance));
	}
	
	public boolean isNaturalezaLegajoPersonalidad(Expediente instance) {
		return NaturalezaSubexpedienteEnumeration.Type.legajoPersonalidad.getValue().equals(getNaturaleza(instance));
	}

	public boolean isNaturalezaCirculacion(Expediente instance) {
		return NaturalezaSubexpedienteEnumeration.Type.circulacion.getValue().equals(getNaturaleza(instance));
	}

	public boolean isExpedienteAdministrativo(Expediente instance) {
		return NaturalezaSubexpedienteEnumeration.Type.administrativo.getValue().equals(getNaturaleza(instance)) || NaturalezaSubexpedienteEnumeration.Type.administrativoDigital.getValue().equals(getNaturaleza(instance));
	}

	public boolean isExpedienteAdministrativoDigital(Expediente instance) {
		return NaturalezaSubexpedienteEnumeration.Type.administrativoDigital.getValue().equals(getNaturaleza(instance));
	}

	public String getNaturaleza(Expediente expediente) {
		if (expediente.getTipoSubexpediente() != null) {
			return expediente.getTipoSubexpediente().getNaturalezaSubexpediente();
		}
		return expediente.getNaturaleza();
	}
	
	private boolean hayQueCopiarRadicacionAlPrincipal(String tipoRadicacion) {
		boolean ret = false;
		if(TipoRadicacionEnumeration.Type.tribunalOral.getValue().equals(tipoRadicacion)){
			ret = ConfiguracionMateriaManager.instance().getBooleanProperty(SessionState.instance().getGlobalCamara(), null, ParametroConfiguracionMateriaEnumeration.Type.sientaRadicacionTribunalOral.getValue().toString(), true);
		}else if(TipoRadicacionEnumeration.Type.juzgadoPlenario.getValue().equals(tipoRadicacion)){
			ret = ConfiguracionMateriaManager.instance().getBooleanProperty(SessionState.instance().getGlobalCamara(), null, ParametroConfiguracionMateriaEnumeration.Type.sientaRadicacionJuzgadoPlenario.getValue().toString(), true);			
		}
		return ret;
	}

	private boolean hayQueCopiarRadicacionAlBase(String tipoRadicacion) {
		return TipoRadicacionEnumeration.Type.juzgado.getValue().equals(tipoRadicacion) ||
			TipoRadicacionEnumeration.Type.sala.getValue().equals(tipoRadicacion) ||
			TipoRadicacionEnumeration.Type.salaEstudio.getValue().equals(tipoRadicacion) ||
			TipoRadicacionEnumeration.Type.salaCasacion.getValue().equals(tipoRadicacion) ||
			TipoRadicacionEnumeration.Type.corteSuprema.getValue().equals(tipoRadicacion) ||
			TipoRadicacionEnumeration.Type.vocalias.getValue().equals(tipoRadicacion) ||
			TipoRadicacionEnumeration.Type.vocaliasCasacion.getValue().equals(tipoRadicacion);
	}

	/**
	 * @param tipoRadicacion
	 * @param oficinaAsignacionExp
	 * @param expediente
	 */
	private void copiaRadicacionAlPrincipal(TipoRadicacionEnumeration.Type tipoRadicacion, OficinaAsignacionExp oficinaAsignacionExp, Expediente expediente) {
		//en caso que oficinaAsignacionExp sea null es porque por ejemplo se elimina una radicacion y no es que se genera una nueva, entonces no se debe copiar
		if(oficinaAsignacionExp!=null){
			if(hayQueCopiarRadicacionAlBase(oficinaAsignacionExp.getTipoRadicacion())){		
				Expediente base = getExpedienteBase(getEntityManager(), expediente);
				if(!base.equals(expediente)){
					copiaRadicacion(tipoRadicacion, oficinaAsignacionExp, base);
				}
			}		
			if(hayQueCopiarRadicacionAlPrincipal(oficinaAsignacionExp.getTipoRadicacion())){
				Expediente principal = getExpedientePrincipal(getEntityManager(), expediente);
				if(!principal.equals(expediente)){
					copiaRadicacion(tipoRadicacion, oficinaAsignacionExp, principal);
				}
				
			}
		}
	}

	private void copiaRadicacion(TipoRadicacionEnumeration.Type tipoRadicacion, OficinaAsignacionExp oficinaAsignacionExp, Expediente expediente) {
		OficinaAsignacionExp  oficinaAsignacionExpAnterior = findRadicacionExpediente(expediente, tipoRadicacion);
		// copia radicacion si cambia con la anterior
		if (oficinaAsignacionExpAnterior == null ||	oficinaAsignacionExpAnterior.getOficina() == null || oficinaAsignacionExpAnterior.getOficina().getId() != oficinaAsignacionExp.getOficina().getId()){
//			TipoAsignacionEnumeration.Type asignacionPorRadicacionPrevia = TipoAsignacionEnumeration.getTipoAsignacionRadicacionPrevia(tipoRadicacion);			
			setRadicacionExpediente(oficinaAsignacionExp.getOperador(), 
					oficinaAsignacionExp.getOficinaAsignadora(), 
					expediente,
					null,
					oficinaAsignacionExp.getOficinaConSecretaria(),
					tipoRadicacion,
					(TipoAsignacionEnumeration.Type)TipoAsignacionEnumeration.instance().getType(oficinaAsignacionExp.getTipoAsignacion()),
					TipoAsignacionLex100Enumeration.Type.copiaAlPrincipal,
					oficinaAsignacionExp.getFechaAsignacion(), 
					oficinaAsignacionExp.getFiscalia(), 
					oficinaAsignacionExp.getDefensoria(), 
					oficinaAsignacionExp.getAsesoria(), 
					oficinaAsignacionExp.getRubro(), 
					null, 
					oficinaAsignacionExp.getComentarios(), false);
		}
		
	}

	public static Expediente getExpedienteBase(EntityManager entityManager, Expediente expediente) {
		while(!isExpedienteBase(expediente) && expediente.getExpedienteOrigen() != null){
			expediente = solveMergedExpediente(entityManager, expediente.getExpedienteOrigen());
		}
		return expediente;
	}

	public static Expediente getExpedientePrincipal(EntityManager entityManager, Expediente expediente) {
		while(expediente.getExpedienteOrigen() != null)
			expediente = solveMergedExpediente(entityManager, expediente.getExpedienteOrigen());
		return expediente;
	}

	// oficinas asignada e una fiscalia/defensoria/asesoria el dia de hoy
	public List<Oficina> findOficinasForOficinaDeTurno(Camara camara, String tipoOficinaTurno, int numeroOficinaTurno) {
		List<Oficina> list = TurnoFiscaliaSearch.findOficinasForOficinaTurno(getEntityManager(), camara, tipoOficinaTurno, numeroOficinaTurno);
		return list;
	}
	
	
	// fiscalia/defensoria/asesoria asignada a una oficina el dia de hoy
	@Transactional
	private Integer findMinisterioDeTurno(Oficina oficina, String tipoOficinaTurno) {
		if (oficina != null) {
			TurnoFiscalia turnoFiscalia = TurnoFiscaliaSearch.findByOficina(getEntityManager(), oficina, tipoOficinaTurno);
			
			if (turnoFiscalia == null) {
				if ((oficina.getTipoOficina() != null) && CodigoTipoOficinaEnumeration.isSecretaria(oficina.getTipoOficina().getCodigo())) {
					Oficina juzgado = CamaraManager.getOficinaPrincipal(oficina);
					if ((juzgado.getTipoOficina() != null) && CodigoTipoOficinaEnumeration.isJuzgado(juzgado.getTipoOficina().getCodigo())){
						return findMinisterioDeTurno(juzgado, tipoOficinaTurno);
					}
				}
			} else {
				return turnoFiscalia.getFiscalia();
			}
		}
		return null;
	}

	public OficinaAsignacionExp findRadicacionExpediente(EntityManager entityManager, Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		return OficinaAsignacionExpSearch.findByTipoRadicacion(entityManager, expediente, (String)tipoRadicacion.getValue());
	}
	
	public OficinaAsignacionExp findRadicacionExpediente(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		return this.findRadicacionExpediente(getEntityManager(), expediente, tipoRadicacion);
	}
	
	public Oficina getOficinaRadicacionExpediente(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		Oficina oficina = null;
		OficinaAsignacionExp oficinaAsignacionExp = findRadicacionExpediente(expediente, tipoRadicacion);
		if (oficinaAsignacionExp != null) {
			oficina =  oficinaAsignacionExp.getOficina();
		}
		return oficina;
	}
	
	public Oficina getFiscaliaRadicacionExpediente(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		Oficina oficina = null;
		OficinaAsignacionExp oficinaAsignacionExp = findRadicacionExpediente(expediente, tipoRadicacion);
		if (oficinaAsignacionExp != null) {
			Integer fiscalia = oficinaAsignacionExp.getFiscalia();
			if (fiscalia != null) {
				Competencia competencia = expediente.getCompetencia();
				if (competencia == null) {
					competencia = getOficinaManager().getCompetencia(oficinaAsignacionExp.getOficina());
				}
				oficina = OficinaSearch.findByNumero(getEntityManager(), fiscalia.toString(), oficinaAsignacionExp.getOficina().getCamara(), oficinaAsignacionExp.getOficina().getTipoInstancia(), competencia, getTipoOficinaFiscalia());
			}
		}
		return oficina;
	}

	public Oficina getDefensoriaRadicacionExpediente(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		Oficina oficina = null;
		OficinaAsignacionExp oficinaAsignacionExp = findRadicacionExpediente(expediente, tipoRadicacion);
		if (oficinaAsignacionExp != null) {
			Integer defensoria = oficinaAsignacionExp.getDefensoria();
			if (defensoria != null) {
				Competencia competencia = expediente.getCompetencia();
				if (competencia == null) {
					competencia = getOficinaManager().getCompetencia(oficinaAsignacionExp.getOficina());
				}
				oficina = OficinaSearch.findByNumero(getEntityManager(), defensoria.toString(), oficinaAsignacionExp.getOficina().getCamara(), oficinaAsignacionExp.getOficina().getTipoInstancia(), competencia, getTipoOficinaDefensoria());
			}
		}
		return oficina;
	}

	private TipoOficina getTipoOficinaDefensoria() {
		return TipoOficinaSearch.findByCodigo(getEntityManager(), TipoOficinaEnumeration.TIPO_OFICINA_DEFENSORIA);
	}

	private TipoOficina getTipoOficinaFiscalia() {
		return TipoOficinaSearch.findByCodigo(getEntityManager(), TipoOficinaEnumeration.TIPO_OFICINA_FISCALIA);
	}

	public OficinaAsignacionExp findRadicacionExpediente(Expediente expediente, Oficina oficina) {
		return OficinaAsignacionExpSearch.findByOficina(getEntityManager(), expediente, oficina);
	}

	public OficinaAsignacionExp findRadicacionExpedienteByOficinaOrSecretaria(Expediente expediente, Oficina oficinaOrSecretaria) {
		if(oficinaOrSecretaria.getOficinaSuperior() != null){
			oficinaOrSecretaria = CamaraManager.getOficinaPrincipal(oficinaOrSecretaria);
		}
		return OficinaAsignacionExpSearch.findByOficina(getEntityManager(), expediente, oficinaOrSecretaria);
	}
	
	public OficinaAsignacionExp findRadicacionExpedienteBySecretaria(Expediente expediente, Oficina oficina) {
		return OficinaAsignacionExpSearch.findBySecretaria(getEntityManager(), expediente, oficina);
	}
	
	@Transactional
	public RemesaExpedienteLinea asignarRemesa(
			Oficina oficinaOrigen, 
			Oficina oficinaDestino, 
			String dependenciaDestino, 
			Expediente expediente, 
			PaseExp pase, 
			Dependencia dependencia,
			Integer fojas, 
	   		Integer paquetes, 
	   		Integer cuerpos, 
	   		Integer agregados,
	   		String comentarios,
	   		String tipoPase,
	   		MotivoPase motivoPase) throws Lex100Exception {
		
		ReglaTipoRemesa reglaTipoRemesa = RemesaExpedienteManager.instance().getReglaTipoRemesa(oficinaDestino, expediente, motivoPase);
		return asignarRemesa(oficinaOrigen, oficinaDestino, dependenciaDestino, expediente, pase, dependencia, fojas, paquetes, cuerpos, agregados, comentarios, tipoPase, motivoPase, reglaTipoRemesa);
	}
	
	public RemesaExpedienteLinea asignarRemesa(
			Oficina oficinaOrigen, 
			Oficina oficinaDestino, 
			String dependenciaDestino, 
			Expediente expediente, 
			PaseExp pase, 
			Dependencia dependencia,
			Integer fojas, 
	   		Integer paquetes, 
	   		Integer cuerpos, 
	   		Integer agregados,
	   		String comentarios,
	   		String tipoPase,
	   		MotivoPase motivoPase,
	   		ReglaTipoRemesa reglaTipoRemesa) throws Lex100Exception {
			try {
				TipoRemesa tipoRemesa = null;
				Integer maxNumExpedientes = ConfiguracionMateriaManager.instance().getMaxNumExpedientesEnRemesa(CamaraManager.getCamaraActual());
				Integer maxNumFojas = ConfiguracionMateriaManager.instance().getMaxNumFojasEnRemesa(CamaraManager.getCamaraActual());

				if (reglaTipoRemesa != null){
					tipoRemesa = reglaTipoRemesa.getTipoRemesa();
					if (reglaTipoRemesa.getMaximoNumeroExpedientes() != null) {
						maxNumExpedientes = reglaTipoRemesa.getMaximoNumeroExpedientes();
					}
					if (reglaTipoRemesa.getMaximoNumeroFojas() != null) {
						maxNumFojas = reglaTipoRemesa.getMaximoNumeroFojas();
					}
				}
				return RemesaExpedienteManager.instance().asignarRemesa(oficinaOrigen, oficinaDestino, dependenciaDestino, tipoRemesa, expediente, pase, dependencia, maxNumExpedientes, maxNumFojas, fojas, paquetes, cuerpos, agregados, comentarios, tipoPase, motivoPase);
			} catch (EntityOperationException e) {
				throw new Lex100Exception("No se ha podido asignar el expediente a una remesa", e);
			}
//		}
	}


	
	public boolean hasMedidasCautelares(Expediente expediente) {
		return expediente.isMedidasCautelares();
	}
	
	public boolean isDetenidos(Expediente expediente) {
		ExpedienteEspecial expedienteEspecial = expediente.getExpedienteEspecial();
		return expedienteEspecial != null 
				&& Boolean.TRUE.equals(expedienteEspecial.isDetenidos());
	}
		
	public boolean isDetenidos() {
		return isDetenidos(getExpedienteHome().getInstance());
	}

	public boolean isMenores() {
		ExpedienteEspecial expedienteEspecial = getExpedienteHome().getInstance().getExpedienteEspecial();
		return expedienteEspecial != null 
				&& Boolean.TRUE.equals(expedienteEspecial.isMenores());
	}
	
	public boolean isIdentidadReservada() {
		ExpedienteEspecial expedienteEspecial = getExpedienteHome().getInstance().getExpedienteEspecial();
		return expedienteEspecial != null 
				&& Boolean.TRUE.equals(expedienteEspecial.isIdentidadReservada());
	}

	public Set<Integer> computeOficinaRadicacionIdSet(Expediente expediente) {
		expediente = solveMergedExpediente(expediente);
		Set<Integer> result = new HashSet<Integer>();
		for (OficinaAsignacionExp oficinaAsignacionExp: expediente.getOficinaAsignacionExpList()) {
			result.add(oficinaAsignacionExp.getOficina().getId());
		}
		return result;
	}

	public Set<Integer> computeOficinaRadicacionPreviaIdSet(Expediente expediente) {
		expediente = solveMergedExpediente(expediente);
		Set<Integer> result = new HashSet<Integer>();
		for (CambioAsignacionExp cambioAsignacionExp: expediente.getCambioAsignacionExpList()) {
			if (!cambioAsignacionExp.isLogicalDeleted()) {
				result.add(cambioAsignacionExp.getOficina().getId());
			}
		}
		return result;
	}
	
	public OficinaAsignacionExp computeRadicacionPrincipal(Expediente expediente) {
		OficinaAsignacionExp juzgado = null;
		OficinaAsignacionExp sala = null;
		OficinaAsignacionExp tribunalOral = null;
		OficinaAsignacionExp juzgadoPlenario = null;
		OficinaAsignacionExp salaCasacion = null;
		OficinaAsignacionExp juzgadoMediacion = null;
		OficinaAsignacionExp corteSuprema = null;
		
		boolean isNaturalezaTribunalOral = NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue().equals(expediente.getNaturaleza());
		if (!isExpedienteBase(expediente)) {
			Expediente expedienteBase = getExpedienteBase(getEntityManager(), expediente);
			if (expedienteBase != null) {
				isNaturalezaTribunalOral =  NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue().equals(expedienteBase.getNaturaleza());
			}
		}
		for (OficinaAsignacionExp oficinaAsignacionExp: expediente.getOficinaAsignacionExpList()) {		
			if (oficinaAsignacionExp.getTipoRadicacion() != null){
				if (oficinaAsignacionExp.getTipoRadicacion().equals(TipoRadicacionEnumeration.Type.juzgado.getValue().toString())){ 
					juzgado = oficinaAsignacionExp;
				}else if (oficinaAsignacionExp.getTipoRadicacion().equals(TipoRadicacionEnumeration.Type.sala.getValue().toString())){ 
					sala = oficinaAsignacionExp;
				} if (oficinaAsignacionExp.getTipoRadicacion().equals(TipoRadicacionEnumeration.Type.juzgadoPlenario.getValue().toString())){
					juzgadoPlenario = oficinaAsignacionExp;
				} if (oficinaAsignacionExp.getTipoRadicacion().equals(TipoRadicacionEnumeration.Type.tribunalOral.getValue().toString())){
					tribunalOral = oficinaAsignacionExp;
				} if (oficinaAsignacionExp.getTipoRadicacion().equals(TipoRadicacionEnumeration.Type.salaCasacion.getValue().toString())){
					salaCasacion = oficinaAsignacionExp;
				} if (oficinaAsignacionExp.getTipoRadicacion().equals(TipoRadicacionEnumeration.Type.juzgadoMediacion.getValue().toString())){
					juzgadoMediacion = oficinaAsignacionExp;
				} if (oficinaAsignacionExp.getTipoRadicacion().equals(TipoRadicacionEnumeration.Type.corteSuprema.getValue().toString())){
					corteSuprema = oficinaAsignacionExp;
				}
			}
		}
		if(corteSuprema != null && expediente.isTieneRecursosCorteAsignadosPteResolver()) {
			return corteSuprema;
		}
		if(salaCasacion != null && expediente.isTieneRecursosCasacionAsignadosPteResolver()){
			return salaCasacion;
		}
		if(tribunalOral != null && isNaturalezaTribunalOral) {
			return tribunalOral;
		}
		if(juzgadoPlenario != null){
			return juzgadoPlenario;
		}
		if(sala != null && (expediente.isTieneRecursosSalaAsignadosPteResolver() || juzgado == null)){ 
			return sala;
		}
		if(juzgado != null){
			return juzgado;
		}
		if(juzgadoMediacion != null){
			return juzgadoMediacion;
		}
		return null;
	}
	
	
	public OficinaAsignacionExp computeOficinaRadicacionByTipo(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		for (OficinaAsignacionExp oficinaAsignacionExp: expediente.getOficinaAsignacionExpList()) {		
			if (oficinaAsignacionExp.getTipoRadicacion() != null){
				if (oficinaAsignacionExp.getTipoRadicacion().equals(tipoRadicacion.getValue().toString())){ 
					return oficinaAsignacionExp;
				}
			}
		}
		return null;
	}

	public List<OficinaAsignacionExp> computeListaOficinaRadicacionByTipo(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion) {
		List<OficinaAsignacionExp> result =
		getEntityManager().createQuery("from OficinaAsignacionExp where expediente = :expediente and tipoRadicacion = :tipoRadicacion order by fechaAsignacion, id")
			.setParameter("expediente", expediente)
			.setParameter("tipoRadicacion", tipoRadicacion.getValue().toString())
			.getResultList();
		return result;
	}
	
/*	public boolean isReservado() {
		return getExpedienteHome().getInstance().isReservado();
	}*/
	
	public boolean isDomicilioInvestigado() {
		return getExpedienteHome().getInstance().isDomicilioInvestigado();
	}
	
	public boolean isDerechosHumanos() {
		ExpedienteEspecial ee = getExpedienteHome().getInstance().getExpedienteEspecial();
		if (ee != null) {
			return ee.isDerechosHumanos();
		}
		return false;
	}

	public boolean isCorrupcion() {
		ExpedienteEspecial ee = getExpedienteHome().getInstance().getExpedienteEspecial();
		if (ee != null) {
			return ee.isCorrupcion();
		}
		return false;
	}
	
	public CabeceraLIP calculaCabeceraLip(Expediente expediente) {
		Expediente expedienteGrabacion =  LegajoPersonalidadExpManager.instance().getExpedienteGrabacion();
		if (expedienteGrabacion != null) {
			List<CabeceraLIP> cabeceraLIPList =
				getEntityManager().createQuery("from CabeceraLIP where expediente = :expediente and expedienteOrigen.camara = :camara and expedienteOrigen.numero = :numero and expedienteOrigen.anio = :anio and status<>-1 order by fechaModificacion desc")
					.setParameter("expediente", expediente)
					.setParameter("camara", expedienteGrabacion.getCamara())
					.setParameter("numero", expedienteGrabacion.getNumero())
					.setParameter("anio", expedienteGrabacion.getAnio())
					.getResultList();
				if (cabeceraLIPList.size() >0 ) {
					return cabeceraLIPList.get(0);
				}
		}
		return null;
	}
	
	public String getExpedienteDisplayPage(Expediente expediente) {
		if (isLegajoPersonalidad(expediente)) {
			return "/web/expediente/expedienteLIP.xhtml";
		} else {
			return isExpedienteConsulta(expediente) ? "/web/expediente/expedienteConsulta.xhtml" : "/web/expediente/expedienteCompleto.xhtml";
		}
	}
	
	public String getSN(boolean value, boolean isShort) {
		String key;
		if (value) {
			key = "si.mayusculas";
		} else {
			key = "no.mayusculas";
		}
		if (isShort) {
			key += ".short";
		}
		return getMessages().get(key);
	}
		
	@SuppressWarnings("unchecked")
	public static Expediente findExpedienteMediacion(EntityManager entityManager, Integer numero, Integer anio) {
		List<String> naturaleza = null;
		naturaleza = new ArrayList<String>();
		naturaleza.add(NaturalezaSubexpedienteEnumeration.Type.mediacion.getValue().toString());
		return findExpediente(entityManager, numero, anio, naturaleza, false);
	}

	@SuppressWarnings("unchecked")
	public static Expediente findExpedienteIniciado(EntityManager entityManager, Integer numero, Integer anio, boolean principal) {
		List<String> naturaleza = null;
		if(principal){
			naturaleza = new ArrayList<String>();
			naturaleza.add(NaturalezaSubexpedienteEnumeration.Type.principal.getValue().toString());
		}
		return findExpediente(entityManager, numero, anio, naturaleza, true);
	}

	@SuppressWarnings("unchecked")
	public static Expediente findExpediente(EntityManager entityManager, Integer numero, Integer anio, List<String> naturalezaSubexpedienteList, boolean iniciado) {
		List<Expediente> expedienteList = findExpedientes (entityManager, numero, anio, naturalezaSubexpedienteList, iniciado);

		if (expedienteList.size() == 1) {
			return expedienteList.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Expediente> findExpedientes(EntityManager entityManager, Integer numero, Integer anio, List<String> naturalezaSubexpedienteList, boolean iniciado) {
		List<Expediente> expedienteList;
		String stmt = "from Expediente where oficinaActual.camara = :camara and numero = :numero and anio = :anio and status <> -1";
		if (naturalezaSubexpedienteList != null) {
			if (naturalezaSubexpedienteList.size() == 1) {
				stmt += " and tipoSubexpediente.naturalezaSubexpediente = :naturaleza";
			}else{
				stmt += " and tipoSubexpediente.naturalezaSubexpediente in (:naturaleza)";
			}
		}
		if (iniciado) {
			stmt += " and estadoExpediente.codigo <> :codigoNoIniciado";
		}
		Query query = entityManager.createQuery(stmt).
			setParameter("camara", SessionState.instance().getGlobalCamara()).
			setParameter("numero", numero).
			setParameter("anio", anio);
		
		if (naturalezaSubexpedienteList != null) {
			if (naturalezaSubexpedienteList.size() == 1) {
				query.setParameter("naturaleza", naturalezaSubexpedienteList.get(0));
			}else{
				query.setParameter("naturaleza", naturalezaSubexpedienteList);
			}
		}
		if (iniciado) {
			query.setParameter("codigoNoIniciado", ExpedienteManager.ESTADO_SIN_INICIAR_CODIGO);
		}
		expedienteList = query.getResultList();

		return expedienteList;
		}

	@SuppressWarnings("unchecked")
	public static List<Expediente> findExpedientes(EntityManager entityManager, Integer numero, Integer anio) {
		List<Expediente> expedienteList;

		expedienteList = entityManager.createQuery("from Expediente where oficinaActual.camara.id = :camaraId and numero = :numero and anio = :anio and status <> -1 order by numeroSubexpediente").
		setParameter("camaraId", SessionState.instance().getGlobalCamara().getId()).
		setParameter("numero", numero).
		setParameter("anio", anio).
		getResultList();

		return expedienteList;
	}

	public static List<Expediente> getFamiliaDirectaExpediente(EntityManager entityManager, Expediente expediente) {
		return FamiliaManager.getFamiliaDirectaExpediente(entityManager, expediente);
	}

	@SuppressWarnings("unchecked")
	public static List<Expediente> getFamiliaExpediente(EntityManager entityManager, Expediente expediente, boolean extendida) {
		List<Expediente> expedienteList = null;
		
		if(extendida){
			expedienteList = FamiliaManager.instance().getFamiliaExpedienteExtendida(expediente);
		} else {
			expedienteList = getFamiliaDirectaExpediente(entityManager, expediente);
			if (!MateriaEnumeration.isAnyPenal(expediente.getMateria())) {
				List<VinculadosExp> vinculadosList = entityManager.createQuery("from VinculadosExp where expediente.id = :id and expedienteRelacionado is not null").
				setParameter("id", expediente.getId()).
				getResultList();
				
				for(VinculadosExp vinculado: vinculadosList) {
					if (TipoVinculadoEnumeration.isTipoVinculadoConexo(vinculado.getTipoVinculado()) &&  !expedienteList.contains(vinculado.getExpedienteRelacionado())) {
						expedienteList.add(vinculado.getExpedienteRelacionado());
					}
				}
		
				for(Conexidad conexidad: expediente.getConexidadList()) {
					if(Boolean.TRUE.equals(conexidad.getPrincipal())) {
						if (!expedienteList.contains(conexidad.getExpedienteRelacionado())) {
							expedienteList.add(conexidad.getExpedienteRelacionado());
						}
					}
				}
		
				if(expediente.getConexidadDeclarada() != null && !expediente.getConexidadDeclarada().isEmpty()) {
					ConexidadManager.instance().solveExpedienteConexidadDeclarada(expediente.getConexidadDeclarada());
					if ((expediente.getConexidadDeclarada().getExpedienteConexo() != null) && !expedienteList.contains(expediente.getConexidadDeclarada().getExpedienteConexo())) {
						expedienteList.add(expediente.getConexidadDeclarada().getExpedienteConexo());
					}
				}
			}
		}
		return expedienteList;
	}
	

	public boolean isCivil(Expediente expediente) {
		return checkAbreviaturaMateria(expediente, MateriaEnumeration.CODIGO_CIVIL);
	}

	public boolean isContenciosoAdmFederal(Expediente expediente) {
		return checkAbreviaturaMateria(expediente, MateriaEnumeration.CODIGO_CAF);
	}

	public boolean isCivilComFederal(Expediente expediente) {
		return checkAbreviaturaMateria(expediente, MateriaEnumeration.CODIGO_CYCF);
	}
	
	public static boolean isPenal(Expediente expediente) {
		for(String abreviaturaPenal: MateriaEnumeration.getAbreviaturasPenalSet()) {
			if (checkAbreviaturaMateria(expediente, abreviaturaPenal)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isLaboral(Expediente expediente) {
		return checkAbreviaturaMateria(expediente, MateriaEnumeration.getAbreviaturaLaboral());
	}
	
	public static boolean isSeguridadSocial(Expediente expediente) {
		return checkAbreviaturaMateria(expediente, MateriaEnumeration.CODIGO_SEGURIDAD_SOCIAL);
	}
	
	public static boolean isCorteSuprema(Expediente expediente){
		return checkAbreviaturaMateria(expediente, MateriaEnumeration.CODIGO_CORTE_SUPREMA);
	}
	
	/** ATOS - COMERCIAL */
	public static boolean isComercial(Expediente expediente) {
		return checkAbreviaturaMateria(expediente, MateriaEnumeration.CODIGO_COMER);
	}
	/** ATOS - COMERCIAL */

	public boolean isTucuman(Expediente expediente) {
		return checkAbreviaturaMateria(expediente, MateriaEnumeration.CODIGO_TUCUMAN);// REVISAR Si sería mejor preguntar por la Camara ???
	}
	
	public String getConexidadDeclaradaDescripcion(Expediente expediente) {
		String descripcion = "";
		if (hasConexidadDeclarada(expediente)) {
			if (!expediente.getConexidadDeclarada().isEmpty()) {
				String oficinaDesc = expediente.getConexidadDeclarada().getOficina() == null ? "" : expediente.getConexidadDeclarada().getOficina().getDescripcion(); 
				descripcion = Interpolator.instance().interpolate(getMessages().get("expedienteIngreso.conexidadDeclarada.descripcion"), 
					expediente.getConexidadDeclarada().getNumeroExpedienteConexo().toString(), 
					expediente.getConexidadDeclarada().getAnioExpedienteConexo().toString(),
					oficinaDesc);
				
			} 
			if (!Strings.isEmpty(expediente.getConexidadDeclarada().getObservaciones())) {
				if (descripcion.length() > 0) {
					descripcion += ": " + expediente.getConexidadDeclarada().getObservaciones();
				} else {
					descripcion = Interpolator.instance().interpolate(getMessages().get("expedienteIngreso.conexidadDeclarada.observaciones"),expediente.getConexidadDeclarada().getObservaciones());
				}
			}
		}
		
		return descripcion;
	}
	
	public static boolean isUseConexidadDeclarada(TipoCausa tipoCausa) {
		boolean show = true;
		if (tipoCausa != null) {
			if (MateriaEnumeration.isAnyPenal(tipoCausa.getMateria())) {
				show = false;
			} 
		}
		return show;
	}

	public static boolean isUseConexidadDeclaradaAnteriorSistema() {
		return !CamaraManager.isCamaraActualCivilCABA();
	}
	
	public static boolean hasConexidadDeclarada(Expediente expediente) {
		
		return (expediente.getConexidadDeclarada() != null) && (!expediente.getConexidadDeclarada().isEmpty() || !Strings.isEmpty(expediente.getConexidadDeclarada().getObservaciones()));
	}

	public boolean isConexidadInformativa(Expediente expediente) {
		return ConfiguracionMateriaManager.instance().isConexidadInformativa(expediente.getCamara(), expediente.getMateria()) && !hasConexidadDeclarada(expediente);
	}

	private static boolean checkAbreviaturaMateria(Expediente expediente, String abreviaturaMateria) {
		return (expediente.getMateria() != null && expediente.getMateria().getAbreviatura().equals(abreviaturaMateria))
			|| (expediente.getTipoProceso() != null && expediente.getTipoProceso().getMateria() != null && expediente.getTipoProceso().getMateria().getAbreviatura().equals(abreviaturaMateria));
	}

	public boolean isCamaraVisible() {
		return getIdentity().hasPermission("Expediente", "verCamaraCompleta");
	}

	@Transactional
	public boolean cambiarTipoCausa(Expediente expediente,	DocumentoExp documento, TipoCausa tipoCausa, boolean doUpdate) {
		boolean ret = false;
		if((tipoCausa != null) || (!isPenal(expediente))){
			ret = true;
			String detalle = null;
			TipoCausa tipoCausaAnterior = expediente.getTipoCausa();
			if(!equals(tipoCausa, expediente.getTipoCausa())){			
				detalle = "Cambio de Tipo de Causa";
				if (expediente.getTipoCausa() != null) {
					detalle += " de " + expediente.getTipoCausa().getCodigo();
				}
				detalle +=  " a " + ((tipoCausa != null) ? tipoCausa.getCodigo(): TipoCausaEnumeration.DESCRIPCION_NOR);
			}
			getExpedienteHome().setId(expediente.getId());
			expediente.setTipoCausa(tipoCausa);
			resetOrigen(expediente);
			ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
			actuacionExpManager.addActuacionTipoCausaExpediente(expediente, null, tipoCausa);
			expediente.setFechaSituacion(actuacionExpManager.getFechaActuacion());
			if(ret){
				if(doUpdate){
					ret = updateSelectedExpediente(ret);
				}
			}
			if(ret) {
				try {
					if(compensaPorCambioTipoCausa(expediente, tipoCausa, tipoCausaAnterior)){
						RubrosInfo rubrosInfo = MesaSorteo.instance().calculaRubrosAsignacion(expediente, TipoRadicacionEnumeration.Type.juzgado, null, false);
						compensarOficina(expediente, null, TipoRadicacionEnumeration.Type.juzgado, rubrosInfo, TipoInformacionEnumeration.CAMBIO_CARATULA, detalle);
					}
					actualizarCaratula(expediente, TipoInformacionEnumeration.CAMBIO_CARATULA, detalle);
					getEntityManager().flush();
				} catch (Lex100Exception e) {
					ret = false;
				}
			}
		}
		return ret;
	}
	
	public static void resetOrigen(Expediente expediente) {
		if(expediente.getExpedienteEspecial() != null) {
			if (!ExpedienteMeAdd.showOrigen(expediente.getTipoCausa())) {
				expediente.getExpedienteEspecial().setDependenciaOrigen(null);
				expediente.getExpedienteEspecial().setCodigoRelacionado(null);
				expediente.getExpedienteEspecial().setTipoCodigoRelacionado(null);
				expediente.getExpedienteEspecial().setDescripcionInformacion(null);
				expediente.getExpedienteEspecial().setCaratulaOrigen(null);
			}
			if (!ExpedienteMeAdd.showOrigen(expediente.getTipoCausa())) {
				expediente.getExpedienteEspecial().setFechaInformacion(null);
			}
			if (!ExpedienteMeAdd.showJuezExortante(expediente.getTipoCausa())) {
				expediente.getExpedienteEspecial().setJurisdiccionExhorto(null);
				expediente.getExpedienteEspecial().setJuzgadoFueroExhorto(null);
				expediente.getExpedienteEspecial().setJuezExhortante(null);
			}
		}
	}

	private boolean compensaPorCambioTipoCausa(Expediente expediente,	TipoCausa tipoCausa, TipoCausa tipoCausaAnterior) {
		boolean ret = false;
		if((tipoCausa != null) && !Strings.isEmpty(tipoCausa.getRubro())){
			ret = tipoCausaAnterior == null || !tipoCausa.getRubro().equals(tipoCausaAnterior.getRubro());
		}
		return ret;
	}

	@Transactional
	public boolean cambiarTipoProceso(Expediente expediente,	DocumentoExp documento, TipoProceso tipoProceso, boolean doUpdate) {
		boolean ret = tipoProceso != null;
		if(tipoProceso != null){
			String detalle = null;
			if(!equals(tipoProceso, expediente.getTipoProceso())){			
				detalle = "Cambio de Tipo de Proceso de " + expediente.getTipoProceso().getCodigo() + " a " + tipoProceso.getCodigo();
			}
			getExpedienteHome().setId(expediente.getId());
			expediente.setTipoProceso(tipoProceso);
			ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
			actuacionExpManager.addActuacionTipoProcesoExpediente(expediente, null, tipoProceso);
			expediente.setFechaSituacion(actuacionExpManager.getFechaActuacion());
			if(ret){
				if(doUpdate){
					ret = updateSelectedExpediente(ret);
				}
			}
			if(ret) {
				try {
					RubrosInfo rubrosInfo = MesaSorteo.instance().calculaRubrosAsignacion(expediente, TipoRadicacionEnumeration.Type.juzgado, null, false);
					compensarOficina(expediente, null, TipoRadicacionEnumeration.Type.juzgado, rubrosInfo, TipoInformacionEnumeration.CAMBIO_CARATULA, detalle);
					actualizarCaratula(expediente, TipoInformacionEnumeration.CAMBIO_CARATULA, detalle);
					getEntityManager().flush();
				} catch (Lex100Exception e) {
					ret = false;
				}
			}
		}
		return ret;
	}
	@Transactional
	public boolean cambiarTipoSubexpediente(Expediente expediente,	DocumentoExp documento, TipoSubexpediente tipoSubexpediente, boolean doUpdate) {
		boolean ret = tipoSubexpediente != null;
		if(tipoSubexpediente != null){
			String detalle = null;
			if(!equals(tipoSubexpediente, expediente.getTipoSubexpediente())){
				String key = isCirculacion(expediente) ? "expediente.cambioMotivoCirculacion": "expediente.cambioTipoSubexpediente";
				detalle = Interpolator.instance().interpolate(getMessages().get(key), expediente.getTipoSubexpediente().getDescripcion(), tipoSubexpediente.getDescripcion());
			}
			getExpedienteHome().setId(expediente.getId());
			expediente.setTipoSubexpediente(tipoSubexpediente);
			ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
			actuacionExpManager.addActuacionTipoSubexpedienteExpediente(expediente, null, tipoSubexpediente);
			expediente.setFechaSituacion(actuacionExpManager.getFechaActuacion());
			if(ret){
				if(doUpdate){
					ret = updateSelectedExpediente(ret);
				}
			}
			if(ret) {
				actualizarCaratula(expediente, TipoInformacionEnumeration.CAMBIO_CARATULA, detalle);
				getEntityManager().flush();
			}
		}
		return ret;
	}

	// compensa por cambio de tipo de recurso, no hay sorteo
	public void compensarCambioTipoRecurso(Expediente expediente, TipoRadicacionEnumeration.Type tipoRadicacion, RecursoExp recursoExp, RubrosInfo rubrosInfo, String motivo, String detalle) throws Lex100Exception {
		if ((recursoExp != null) && !Strings.isEmpty(recursoExp.getRubro()) && !rubrosInfo.getRubro().equals(recursoExp.getRubro()) && !Boolean.FALSE.equals(recursoExp.getCompensado())) {
			Oficina oficinaSorteadora = OficinaManager.instance().getOficinaSorteadoraOAsignadora();
			Materia materia = expediente.getMateria();
			Sorteo sorteo = MesaSorteo.instance().buscaSorteo(oficinaSorteadora, false,
					tipoRadicacion, 
					materia.getGrupo(), 
					materia, 
					expediente.getCompetencia(), 
					rubrosInfo.getRubroAsignacion(), 
					rubrosInfo.getRubro(), false);
			if (sorteo != null) {
				enviarCompensacion(sorteo, TipoBolilleroSearch.getTipoBolilleroAsignacion(getEntityManager()), expediente, recursoExp, tipoRadicacion, recursoExp.getOficinaElevacion(), recursoExp.getOficinaElevacion(), recursoExp.getRubro(), 
						rubrosInfo.getRubro(), recursoExp.getCompensado(), true, SessionState.instance().getUsername(), motivo, detalle, false);
				recursoExp.setRubro(rubrosInfo.getRubro());
			}
		}
	}

	// compensa por cambio de rubro, no hay sorteo
	public void compensarOficina(Expediente expediente, RecursoExp recursoExp, TipoRadicacionEnumeration.Type tipoRadicacion, RubrosInfo rubrosInfo, String motivo, String detalle) throws Lex100Exception {
		compensarOficina(expediente, recursoExp, tipoRadicacion, rubrosInfo, motivo, detalle, true);
	}
	public void compensarOficina(Expediente expediente, RecursoExp recursoExp, TipoRadicacionEnumeration.Type tipoRadicacion, RubrosInfo rubrosInfo, String motivo, String detalle, boolean descompensarAnterior) throws Lex100Exception {
		OficinaAsignacionExp radicacion = ExpedienteManager.instance().findRadicacionExpediente(expediente, tipoRadicacion);
		if ((radicacion != null) && (rubrosInfo.getRubro() != null) && !rubrosInfo.getRubro().equals(radicacion.getRubro()) && !Boolean.FALSE.equals(radicacion.getCompensado())) {
			Oficina oficinaSorteadora = radicacion.getOficinaAsignadora();
			Materia materia = expediente.getMateria();
			Sorteo sorteo = MesaSorteo.instance().buscaSorteo(oficinaSorteadora, false,
					tipoRadicacion, 
					materia.getGrupo(), 
					materia, 
					expediente.getCompetencia(), 
					rubrosInfo.getRubroAsignacion(), 
					rubrosInfo.getRubro(), false);
			if (sorteo != null) {
				enviarCompensacion(sorteo, TipoBolilleroSearch.getTipoBolilleroAsignacion(getEntityManager()), expediente, recursoExp, tipoRadicacion, radicacion.getOficinaConSecretaria(), radicacion.getOficinaConSecretaria(), radicacion.getRubro(), 
						rubrosInfo.getRubro(), radicacion.getCompensado(), descompensarAnterior, SessionState.instance().getUsername(), motivo, detalle, false);
				radicacion.setRubro(rubrosInfo.getRubro());
			}
		}
	}

	public Integer getNumeroFiscaliaUbicacion (Expediente exp) {
		List<OficinaAsignacionExp> listAsignaciones = exp.getOficinaAsignacionExpList();
		int idOficinaActual = exp.getOficinaActual().getId();
		for (Iterator oficinaAsignacion = listAsignaciones.iterator(); oficinaAsignacion
				.hasNext();) {
			OficinaAsignacionExp oficinaAsignacionExp = (OficinaAsignacionExp) oficinaAsignacion
					.next();
			if (oficinaAsignacionExp.getId() == idOficinaActual) {
				return oficinaAsignacionExp.getFiscalia();
			}
		}
		
		return new Integer(-1);
	}

	private Integer getPaquetes(Expediente expediente, RecursoExp recursoExp, FojasEdit fojasPase) {
		if(recursoExp != null) {
			return recursoExp.getPaquetes();
		} else if (fojasPase != null) {
			return fojasPase.getPaquetes();
		} else {
			return 1;
		}
	}
	private Integer getFojas(Expediente expediente, RecursoExp recursoExp, FojasEdit fojasPase) {
		if(recursoExp != null) {
			return recursoExp.getFojas();
		} else if (fojasPase != null) {
			return fojasPase.getFojas();
		} else {
			return expediente.getFojas();
		}
	}
	private Integer getCuerpos(Expediente expediente, RecursoExp recursoExp, FojasEdit fojasPase) {
		if(recursoExp != null) {
			return recursoExp.getCuerpos();
		} else if (fojasPase != null) {
			return fojasPase.getCuerpos();
		} else {
			return expediente.getCuerpos();
		}
	}
	private Integer getAgregados(Expediente expediente, RecursoExp recursoExp, FojasEdit fojasPase) {
		if(recursoExp != null) {
			return recursoExp.getAgregados();
		} else if (fojasPase != null) {
			return fojasPase.getAgregados();
		} else {
			return expediente.getAgregados();
		}
	}

	private String getComentarioAgregados(Expediente expediente, RecursoExp recursoExp, FojasEdit fojasPase) {
		if(recursoExp != null) {
			return recursoExp.getComentarios();
		} else if (fojasPase != null) {
			return fojasPase.getComentarios();
		} else {
			return null;
		}
	}

	public String doTraerExpediente(Expediente expediente) {
		boolean ret = true;
		Expediente valorAnterior = saveOficinaActualExpediente(expediente);
		try {
			new AccionTraerExpediente().doAccion(expediente, null, null);
		} catch (AccionException e) {
			ret = false;
		}
		if(!ret){
			restoreOficinaActualExpediente(valorAnterior, expediente);
		}
		return null;
	}

	public String doTraerExpedientePrincipal() {
		Expediente expediente = getExpedienteHome().getInstance();
		Expediente principal = getExpedienteBase(getEntityManager(), expediente);
		String ret = null;
		if(principal != null && principal != expediente){
			ret = doTraerExpediente(principal);
		}
		return ret;
	}
	
	public String doTraerExpediente() {
		return doTraerExpediente(getExpedienteHome().getInstance());
	}

	public String doAutoRadicarExpediente() {
		boolean ret = true;
		try {
			new AccionAutoRadicarExpediente().doAccion(getExpedienteHome().getInstance(), null, null);
		} catch (AccionException e) {
			ret = false;
		}
		return null;
	}


	public boolean autoRadicarExpediente(Expediente expediente, DocumentoExp documento, Date fechaCambio, boolean cambiarRadicacion) {
		boolean ret = true;
		if (isOficinaActual(expediente) && !isRadicadoOficinaActual(expediente)) {
			try {
				if (fechaCambio != null) {
					getActuacionExpManager().setFechaActuacionVirtual(fechaCambio);
				}
				Oficina oficinaActual = getSessionState().getGlobalOficina();
				if (! getEntityManager().contains(oficinaActual)) {
					oficinaActual = getEntityManager().find(Oficina.class, oficinaActual.getId());
				}
				getActuacionExpManager().addActuacionAutoradicarExpediente(expediente, documento);
				cambiaRadicacionConCompensacion(expediente, oficinaActual);
				expediente.reset();
			}catch (Lex100Exception e) {				
				getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['expedienteManager.error_autoRadicarExpediente']}");
				ret = false;
			} catch (AccionException e) {
				getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['expedienteManager.error_autoRadicarExpediente']}");
				ret = false;
			} finally {
				getActuacionExpManager().setFechaActuacionVirtual(null);
				getEntityManager().flush();
			}
		}
		return ret;
	}
	
	public boolean traerExpediente(Expediente expediente) {
		return traerExpediente(expediente, null, null, false);
	}
	public boolean traerExpediente(Expediente expediente, DocumentoExp documento, Date fechaCambio, boolean cambiarRadicacion) {
		if (!isOficinaActual(expediente)) {
			try {
				if (fechaCambio != null) {
					getActuacionExpManager().setFechaActuacionVirtual(fechaCambio);
				}
				Oficina oficinaActual = getSessionState().getGlobalOficina();
				if (! getEntityManager().contains(oficinaActual)) {
					oficinaActual = getEntityManager().find(Oficina.class, oficinaActual.getId());
				}
				expediente.setOficinaActual(oficinaActual);
				if(isRemision(expediente)){
					doCancelarRemision(expediente);
				}
				if (expediente.isPase()) {
					
					PaseExp paseExp = buscarPasePendiente(expediente);
//					if (paseExp == null) {
//						throw new AccionException(interpolate(getMessages().get("accionRecibirPase.errorPaseInexistente"),expediente.getNumero(),expediente.getAnio()));
//					}
					expediente.setOficinaRetorno(null);
					if (paseExp != null) {
						paseExp.setLogicalDeleted(true);
					}
					expediente.setOficinaPase(null);
					expediente.setPase(false);
					expediente.setPendienteRecepcion(false);
					expediente.setLocalizacion(null);
					
				}
				if(cambiarRadicacion){
					cambiaRadicacionSinCompensar(expediente, oficinaActual);
				}
				getActuacionExpManager().addActuacionTraerExpediente(expediente, documento);
				if (getExpedienteManager().isIniciado(expediente)) {
					cambiarSituacion(expediente, null, SituacionExpedienteEnumeration.Type.despacho.getValue().toString(), true);
				}
			} finally {
				getActuacionExpManager().setFechaActuacionVirtual(null);
			}
			getEntityManager().flush();
		}
		return true;
	}

	private void cambiaRadicacionConCompensacion(Expediente expediente, Oficina oficinaActual) throws Lex100Exception, AccionException{
		TipoRadicacionEnumeration.Type tipoRadicacion = null;
		if(oficinaActual.getTipoInstancia() != null){
			tipoRadicacion = TipoRadicacionEnumeration.calculateTipoRadicacionByTipoInstancia(oficinaActual.getTipoInstancia().getCodigo());
		}
		if(tipoRadicacion != null){
			
		 	RubrosInfo rubrosInfo = MesaSorteo.instance().calculaRubrosAsignacion(expediente, tipoRadicacion, null, true);
			Sorteo sorteo = MesaSorteo.instance().buscaSorteo(oficinaActual, 
						!true,
						tipoRadicacion, 
						expediente.getMateria().getGrupo(), 
						expediente.getMateria(), 
						expediente.getCompetencia(), 
						rubrosInfo.getRubroAsignacion(), 
						rubrosInfo.getRubro(),
						false);

			
			asignarOficina(
					SessionState.instance().getUsername(),
					expediente,
					null,
					oficinaActual,
					tipoRadicacion, 
					TipoAsignacionEnumeration.getTipoAsignacionManual(tipoRadicacion),
					TipoAsignacionLex100Enumeration.Type.manual,
					oficinaActual,
					null, true,
					sorteo, 
					rubrosInfo.getRubro(),
					TipoCambioAsignacionEnumeration.ASIGNACION_MANUAL,
					null, null, null, null);
			
			//Realiza la copia de las radicaciones de un expediente a toda su familia.
			//Se deja esta opcion aca ya que este metodo se utiliza al momento de auto-radicar el expediente unicamente
			copiarRadicacionesFamilia(expediente, tipoRadicacion);
			
			//Realiza la impresion automatica en caso que corresponda
			ImpresionExpManager impresionExpManager = (ImpresionExpManager) Component.getInstance(ImpresionExpManager.class, true);
			impresionExpManager.imprimir(expediente, expediente.getOficinaRadicacionPrincipal(), tipoRadicacion, oficinaActual, null);
		}else{
			throw new Lex100Exception("Imposible radicar en esta oficina");
		}
	}

	private void cambiaRadicacionSinCompensar(Expediente expediente, Oficina oficinaActual) {
		boolean radicacionEncontrada = false;
		
		Oficina oficinaPrincipal = oficinaActual.getOficinaSuperior();
		for (OficinaAsignacionExp asignacionExp: expediente.getOficinaAsignacionExpList()) {
			if (asignacionExp.getOficina().getTipoInstancia().equals(oficinaActual.getTipoInstancia())) {
				asignacionExp.setOficina(oficinaPrincipal);
				if(oficinaPrincipal != oficinaActual){
					asignacionExp.setSecretaria(oficinaActual);
				}
				radicacionEncontrada = true;
				break;
			}
		}
		if (!radicacionEncontrada) {
			TipoRadicacionEnumeration.Type tipoRadicacion = null;
			if(oficinaActual.getTipoInstancia() != null){
				tipoRadicacion = TipoRadicacionEnumeration.calculateTipoRadicacionByTipoInstancia(oficinaActual.getTipoInstancia().getCodigo());
			}
			String tipoAsignacion =	null;
			if(tipoRadicacion != null){
				TipoAsignacionEnumeration.Type type = TipoAsignacionEnumeration.getTipoAsignacionManual(tipoRadicacion);
				if(type != null){
					tipoAsignacion = type.getValue().toString(); 
				}
			}
			OficinaAsignacionExp oficinaAsignacionExp = new OficinaAsignacionExp();
			oficinaAsignacionExp.setOficina(oficinaPrincipal);
			oficinaAsignacionExp.setExpediente(expediente);					
			if(oficinaPrincipal != oficinaActual){
				oficinaAsignacionExp.setSecretaria(oficinaActual);
			}
			oficinaAsignacionExp.setTipoAsignacion(tipoAsignacion);
			oficinaAsignacionExp.setFechaAsignacion(getCurrentDateManager().getCurrentDate());
			oficinaAsignacionExp.setTipoAsignacionOficina((String) TipoAsignacionOficinaEnumeration.Type.reasignacion.getValue());
			getEntityManager().persist(oficinaAsignacionExp);
		}
	}

	private String getLabelTipoOficinaAsignacion(Oficina oficina){
		String result = "";
		if (oficina.getTipoOficina().getCodigo().equals(CodigoTipoOficinaEnumeration.Type.secretaria.getValue())) {
			result =  OficinaManager.instance().hasMultiplesSecretarias(oficina) ? "Juzgado/Secretaría Nº " : OficinaManager.instance().isCorteSuprema(oficina)? "Secretaria Nº ": "Juzgado Nº " ;
		} else if (oficina.getTipoOficina().getCodigo().equals(CodigoTipoOficinaEnumeration.Type.juzgado.getValue())) {
			result = "Juzgado Nº ";
		} else if (oficina.getTipoOficina().getCodigo().equals(CodigoTipoOficinaEnumeration.Type.salaApelacion.getValue())) {
			result = "Sala ";
		} else if (oficina.getTipoOficina().getCodigo().equals(CodigoTipoOficinaEnumeration.Type.tribunalOral.getValue())) {
			result = "Tribunal Oral Nº ";
		} else {
			result = oficina.getTipoOficina().getDescripcion() + " Nº ";
		}
		return result;
	}
	
	private String getDeescripcionOficinaAsignadaEnActuacion(Oficina oficina){
		if(TipoOficinaEnumeration.isMediador(oficina)){
			return oficina.getDescripcionMediadorYMatricula();
		}else{
			return oficina.getCodigoAbreviado();
		}
	}
	
	public String getActuacionCambioAsignacionDescripcion(ActuacionExp actuacionExp){
		return getCambioAsignacionDescripcion(actuacionExp.getCambioAsignacionExp(), actuacionExp.getRecursoExp(), actuacionExp.getTipoRecurso());
	}
	
	public String getCambioAsignacionDescripcion(CambioAsignacionExp cambioAsignacionExp){
		return getCambioAsignacionDescripcion(cambioAsignacionExp, null, null);
	}

	public String getCambioAsignacionDescripcion(CambioAsignacionExp cambioAsignacionExp, boolean addComentarios) {
		return getCambioAsignacionDescripcion(cambioAsignacionExp, null, null);
	}

	public String getCambioAsignacionDescripcion(CambioAsignacionExp cambioAsignacionExp, RecursoExp recursoExp, TipoRecurso tipoRecurso){
		String descripcion = null;
		TipoRadicacionEnumeration tipoRadicacionEnumeration = (TipoRadicacionEnumeration) Component.getInstance(TipoRadicacionEnumeration.class, true);
		if (cambioAsignacionExp != null) {
			if(!Strings.isEmpty(cambioAsignacionExp.getCodigoTipoCambioAsignacion())) {
				Oficina oficinaAsignada = cambioAsignacionExp.getOficinaConSecretaria();
				String oficinaAsignadaDescripcion = getLabelTipoOficinaAsignacion(oficinaAsignada)+" "+getDeescripcionOficinaAsignadaEnActuacion(oficinaAsignada);
				if ((TipoRadicacionEnumeration.Type.sala.getValue().equals(cambioAsignacionExp.getTipoRadicacion()) || TipoRadicacionEnumeration.Type.salaCasacion.getValue().equals(cambioAsignacionExp.getTipoRadicacion()) || TipoRadicacionEnumeration.Type.corteSuprema.getValue().equals(cambioAsignacionExp.getTipoRadicacion()))
						&& !TipoCambioAsignacionEnumeration.isTipoCambioSala(cambioAsignacionExp.getCodigoTipoCambioAsignacion())	) {
					if (tipoRecurso == null) {
						if(recursoExp != null){
							tipoRecurso = recursoExp.getTipoRecurso();
						}
						if (tipoRecurso == null) {
							tipoRecurso = TipoRecursoEnumeration.getItemByCodigo(getEntityManager(), cambioAsignacionExp.getCodigoTipoCambioAsignacion(), getMateria(cambioAsignacionExp.getExpediente()));
						}
					}
					if (tipoRecurso != null) {
						descripcion = tipoRecurso.getDescripcion() + " - " +oficinaAsignadaDescripcion;
					} 
					if(recursoExp != null){
						descripcion = recursoExp.getClave() + " - " + descripcion;							
					}
				} else {
					TipoRadicacionEnumeration.Type tipoRadicacion = (TipoRadicacionEnumeration.Type)tipoRadicacionEnumeration.getType(cambioAsignacionExp.getTipoRadicacion());
					Integer tipoInstanciaCodigo = TipoRadicacionEnumeration.calculateCodigoTipoInstanciaByTipoRadicacion(tipoRadicacion);
					TipoInstancia tipoInstancia = TipoInstanciaSearch.findByCodigo(getEntityManager(), tipoInstanciaCodigo);
					TipoCambioAsignacion tipoCambioAsignacion = TipoCambioAsignacionEnumeration.getItemByCodigo(getEntityManager(), cambioAsignacionExp.getCodigoTipoCambioAsignacion(), tipoInstancia, getMateria(cambioAsignacionExp.getExpediente()));
					String tipoRadicacionLabel = tipoRadicacionEnumeration.getLabel(cambioAsignacionExp.getTipoRadicacion());					
					if (tipoCambioAsignacion != null) {
						if (tipoRadicacion == TipoRadicacionEnumeration.Type.vocalias || tipoRadicacion == TipoRadicacionEnumeration.Type.vocaliasCasacion) {
							descripcion = tipoCambioAsignacion.getDescripcion() + ": " + CamaraManager.getOficinaPrincipal(oficinaAsignada).getCodigoNumerico() +
								" " +oficinaAsignada.getCodigoNumerico();
							PersonalOficina juez = PersonalOficinaManager.instance().getJuez(oficinaAsignada);
							if (juez != null) {
								descripcion += " " + juez.getApellidosNombre();
							} else {
								descripcion += " (vacante)";
							}
						} else {
							descripcion = tipoCambioAsignacion.getDescripcion() + " " + tipoRadicacionLabel + " - " + oficinaAsignadaDescripcion;
						}
					}else{
						TipoReasignacionEnumeration.Type tipo = TipoReasignacionEnumeration.instance().findByCodigo(cambioAsignacionExp);
						if(tipo != null){
							String label = getMessages().get(tipo.getLabel());
							if(label == null){
								label = tipo.getLabel();
							}
							descripcion =  label + " " + tipoRadicacionLabel + " - " + oficinaAsignadaDescripcion;
						}
					}
				}
			} 
			if(descripcion == null) {
				TipoAsignacionEnumeration tipoAsignacionEnumeration = (TipoAsignacionEnumeration) Component.getInstance(TipoAsignacionEnumeration.class, true);

				String tipoRadicacionLabel = tipoRadicacionEnumeration.getLabel(cambioAsignacionExp.getTipoRadicacion());
				String tipoAsignacionLabel = tipoAsignacionEnumeration.getLabel(cambioAsignacionExp.getTipoAsignacion(), (TipoRadicacionEnumeration.Type)tipoRadicacionEnumeration.getType(cambioAsignacionExp.getTipoRadicacion()));
				// por si no encuentra el tipo
				if(tipoAsignacionLabel == null && cambioAsignacionExp.getTipoAsignacion() != null){
					tipoAsignacionLabel = cambioAsignacionExp.getTipoAsignacion();
				}
				if(tipoAsignacionLabel == null){
					tipoAsignacionLabel="";
				}
				descripcion = Interpolator.instance().interpolate(getMessages().get("actuacion.cambioAsignacionOficina.descripcion"), 
					tipoRadicacionLabel, 
					tipoAsignacionLabel,
					getDeescripcionOficinaAsignadaEnActuacion(cambioAsignacionExp.getOficinaConSecretaria()));
			}
		}
		String comentarios = cambioAsignacionExp.getComentarios();
		if(comentarios != null){
			if(descripcion == null){
				descripcion = comentarios;
			}else{
				descripcion += " - " + comentarios;
			}
		}
		return descripcion;
	}

	public String getCambioAsignacionDescripcionLibro(CambioAsignacionExp cambioAsignacionExp) {
		String descripcion = null;
		TipoRadicacionEnumeration tipoRadicacionEnumeration = (TipoRadicacionEnumeration) Component.getInstance(TipoRadicacionEnumeration.class, true);
		if (cambioAsignacionExp != null) {
			if(!Strings.isEmpty(cambioAsignacionExp.getCodigoTipoCambioAsignacion())) {
				if (TipoRadicacionEnumeration.Type.sala.getValue().equals(cambioAsignacionExp.getTipoRadicacion()) && !TipoCambioAsignacionEnumeration.isTipoCambioSala(cambioAsignacionExp.getCodigoTipoCambioAsignacion()) ) {
					TipoRecurso tipoRecurso = TipoRecursoEnumeration.getItemByCodigo(getEntityManager(), cambioAsignacionExp.getCodigoTipoCambioAsignacion(), getMateria(cambioAsignacionExp.getExpediente()));
					if (tipoRecurso != null) {
						descripcion = tipoRecurso.getDescripcion();
					}
				} else {
					TipoRadicacionEnumeration.Type tipoRadicacion = (TipoRadicacionEnumeration.Type)tipoRadicacionEnumeration.getType(cambioAsignacionExp.getTipoRadicacion());
					Integer tipoInstanciaCodigo = TipoRadicacionEnumeration.calculateCodigoTipoInstanciaByTipoRadicacion(tipoRadicacion);
					TipoInstancia tipoInstancia = TipoInstanciaSearch.findByCodigo(getEntityManager(), tipoInstanciaCodigo);
					TipoCambioAsignacion tipoCambioAsignacion = TipoCambioAsignacionEnumeration.getItemByCodigo(getEntityManager(), cambioAsignacionExp.getCodigoTipoCambioAsignacion(), tipoInstancia, getMateria(cambioAsignacionExp.getExpediente()));
					if (tipoCambioAsignacion != null) {
						descripcion = tipoCambioAsignacion.getDescripcion();
					}
				}
			} 
			if(descripcion == null) {
				TipoAsignacionEnumeration tipoAsignacionEnumeration = (TipoAsignacionEnumeration) Component.getInstance(TipoAsignacionEnumeration.class, true);

				String tipoRadicacionLabel = tipoRadicacionEnumeration.getLabel(cambioAsignacionExp.getTipoRadicacion());
				String tipoAsignacionLabel = tipoAsignacionEnumeration.getLabel(cambioAsignacionExp.getTipoAsignacion(), (TipoRadicacionEnumeration.Type)tipoRadicacionEnumeration.getType(cambioAsignacionExp.getTipoRadicacion()));
				// por si no encuentra el tipo
				if(tipoAsignacionLabel == null && cambioAsignacionExp.getTipoAsignacion() != null){
					tipoAsignacionLabel = cambioAsignacionExp.getTipoAsignacion();
				}
				descripcion = Interpolator.instance().interpolate(getMessages().get("actuacion.cambioAsignacionOficina.descripcion"), 
					tipoRadicacionLabel, 
					tipoAsignacionLabel,
					CamaraManager.getOficinaAbreviada((cambioAsignacionExp.getSecretaria() != null)? cambioAsignacionExp.getSecretaria(): cambioAsignacionExp.getOficina()));
			}
		}
		return descripcion;
	}
	
	public String doAsignarInstructor() {
		return doAsignarInstructor(getExpedienteHome().getInstance());
	}
	
	public String doAsignarInstructor(Expediente expediente) {
		initAsignarInstructorExpediente(expediente);
		getExpedienteHome().setVisibleDialog("asignarInstructor", true);
		return "";
	}
	
	private void initAsignarInstructorExpediente(Expediente expediente) {
		InstructorFacade instructorFacade = getInstructor(expediente); 
		if (instructorFacade != null) {
			this.instructor = instructorFacade.getPersonalOficina();
		}
	}
	
	public void asignarInstructor() {
		asignarInstructor(getExpedienteHome().getInstance());
	}
	
	public void asignarInstructor(Expediente expediente) {
		asignarInstructor(expediente, instructor);
		getExpedienteHome().setVisibleDialog("asignarInstructor", false);
	}
	
	public void asignarInstructor(Expediente expediente, PersonalOficina instructor) {
		String instructorAnterior = expediente.getResponsable();
		expediente.setResponsable((instructor != null ? instructor.getCuil() : null));
		if (!equals(instructorAnterior, expediente.getResponsable())) {
			String detalle = (instructor != null ? Strings.emptyIfNull(instructor.getCuil()) + " " + Strings.emptyIfNull(instructor.getNombre()) + " " + Strings.emptyIfNull(instructor.getApellidos()) : "");
			getActuacionExpManager().addActuacionCambioInstructor(expediente, detalle,  null);
		}
		getEntityManager().flush();
	}
	
	public InstructorFacade getInstructor(Expediente expediente){
		String responsable = expediente.getResponsable();
		if (responsable == null) return null;
		if (!instructores.containsKey(responsable)) {
			InstructorFacade instructorFacade = new InstructorFacade();
			String instructorData = responsable;
			PersonalOficina personalOficina;
			try {
				if (CuilValidator.validateCUIL(instructorData)) {
					personalOficina = (PersonalOficina)getEntityManager().createQuery("select e from PersonalOficina e where e.cuil = :cuil")
							.setParameter("cuil", instructorData)
							.getSingleResult();
				} else {
					if (instructorData.length() > 3) {
						instructorData = instructorData.substring(instructorData.length()-3);
					}
					personalOficina = (PersonalOficina)getEntityManager().createQuery("select e from PersonalOficina e where lower(e.usernameCaravel) like '%'||:usernameCaravel")
					.setParameter("usernameCaravel", instructorData.toLowerCase())
					.getSingleResult();
				}
				instructorFacade.setPersonalOficina(personalOficina);
				instructorFacade.setInciales(personalOficina.getIniciales());
				instructores.put(responsable, instructorFacade);
			} catch (Exception e) {
				instructorFacade.setInciales(instructorData);
			}										
			return instructorFacade;
		} else {
			return instructores.get(responsable);
		}
	}
	
	public void actualizarDatosExpediente(Expediente expedienteOrigen) {
		List<IntervinienteExp> intervinienteExpList =
			getEntityManager().createQuery("from IntervinienteExp where expediente = :expediente and status <> -1")
				.setParameter("expediente", expedienteOrigen)
				.getResultList();
		boolean detenidos = false;
		boolean menores = false;
		boolean identidadReservada = false;
		boolean internados = false;
		for (IntervinienteExp intervinienteExp : intervinienteExpList) {
			detenidos |= intervinienteExp.isDetenido();
			menores |= intervinienteExp.isMenor();
			identidadReservada |= intervinienteExp.isIdentidadReservada();
			internados |= intervinienteExp.isInternado();
		}
		if (expedienteOrigen.getExpedienteEspecial() == null) {
			ExpedienteEspecial expedienteEspecial = new ExpedienteEspecial();
			expedienteEspecial.setExpediente(expedienteOrigen);
			expedienteOrigen.setExpedienteEspecial(expedienteEspecial);
			getEntityManager().persist(expedienteEspecial);
		}
		expedienteOrigen.getExpedienteEspecial().setMenoresFlag(menores);
		expedienteOrigen.getExpedienteEspecial().setDetenidosFlag(detenidos);
		expedienteOrigen.getExpedienteEspecial().setIdentidadReservadaFlag(identidadReservada);
		expedienteOrigen.getExpedienteEspecial().setInternadosFlag(internados);
	}

	@SuppressWarnings("unchecked")
	public void actualizaTipoVoluntario(Expediente expediente) {
		List<IntervinienteExp> intervinienteExpList =
			getEntityManager().createQuery("from IntervinienteExp where expediente = :expediente and status <> -1")
				.setParameter("expediente", expediente)
				.getResultList();

		boolean hayDemandados = false;
		for (IntervinienteExp intervinienteExp : intervinienteExpList) {
			if (intervinienteExp.isDemandado() ){
				hayDemandados = true;
				break;
			}
		}
		expediente.setVoluntario(!hayDemandados);
	}

	@SuppressWarnings("unchecked")
	public boolean isLitisConsorcio(Expediente expediente) {
		List<IntervinienteExp> intervinienteExpList =
			getEntityManager().createQuery("from IntervinienteExp where expediente = :expediente and status <> -1")
				.setParameter("expediente", expediente)
				.getResultList();

		int actores = 0;
		int demandados = 0;
		for (IntervinienteExp intervinienteExp : intervinienteExpList) {
			if (intervinienteExp.isActor() ){
				actores++;
			} else if (intervinienteExp.isDemandado() ){
				demandados++;
			}
		}
		return (actores > 1) || (demandados > 1);
	}

	public class InstructorFacade {		
		private String inciales;
		private PersonalOficina personalOficina;
		
		public String getInciales() {
			return inciales;
		}
		public void setInciales(String inciales) {
			this.inciales = inciales;
		}
		public String getNombreCompleto() {
			return (personalOficina != null ? Strings.emptyIfNull(personalOficina.getNombre()) + " " + Strings.emptyIfNull(personalOficina.getApellidos()) : "");
		}
		public PersonalOficina getPersonalOficina() {
			return personalOficina;
		}
		public void setPersonalOficina(PersonalOficina personalOficina) {
			this.personalOficina = personalOficina;
		}
	}
		
	public boolean hasDenuncia(Expediente expediente){
		boolean ret = false;
		if(expediente != null && expediente.getExpedienteEspecial() != null){
			ret = (Strings.nullIfEmpty(expediente.getExpedienteEspecial().getDeclaracionHecho())!= null);
		}
		return ret;
	}
	
	
	private void createExpedienteEspecial(Expediente expediente){
		ExpedienteEspecial expedienteEspecial = new ExpedienteEspecial();
		expedienteEspecial.setExpediente(expediente);
		getEntityManager().persist(expedienteEspecial);
		getEntityManager().flush();
	}

	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosAsignadosPteResolver(Expediente expediente) {
		String enTramite = EstadoResolucionRecursoEnumeration.Type.en_tramite.getValue().toString();

		List<RecursoExp> recursoExpList =
			getEntityManager().createQuery("from RecursoExp where expediente.id = :idExpediente and status <> -1 and clave is not null and oficinaElevacion is not null and estadoResolucion = :enTramite")
			.setParameter("idExpediente", expediente.getId()).setParameter("enTramite", enTramite)
			.getResultList();
		return recursoExpList;
	}
	
	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosPteAsignacion(Expediente expediente) {
		String enTramite = EstadoResolucionRecursoEnumeration.Type.en_tramite.getValue().toString();

		List<RecursoExp> recursoExpList =
		getEntityManager().createQuery("from RecursoExp where expediente.id = :expedienteId and status <> -1 and clave is null and estadoResolucion = :enTramite")
			.setParameter("expedienteId", expediente.getId()).setParameter("enTramite", enTramite)
			.getResultList();
		return recursoExpList;
	}

	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosPteResolver(Expediente expediente) {
		String enTramite = EstadoResolucionRecursoEnumeration.Type.en_tramite.getValue().toString();

		List<RecursoExp> recursoExpList =
		getEntityManager().createQuery("from RecursoExp where expediente.id = :expedienteId and status <> -1 and estadoResolucion = :enTramite")
			.setParameter("expedienteId", expediente.getId()).setParameter("enTramite", enTramite)
			.getResultList();
		return recursoExpList;
	}

	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosSalaPteResolver(Expediente expediente) {
		return RecursoExpManager.instance().filtrarSala(computeRecursosPteResolver(expediente));
	}

	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosCasacionPteResolver(Expediente expediente) {
		return RecursoExpManager.instance().filtrarCasacion(computeRecursosPteResolver(expediente));
	}

	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosCortePteResolver(Expediente expediente) {
		return RecursoExpManager.instance().filtrarCorteSuprema(computeRecursosPteResolver(expediente));
	}
	
	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosSalaPteAsignacion(Expediente expediente) {
		return RecursoExpManager.instance().filtrarSala(computeRecursosPteAsignacion(expediente));
	}
	
	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosSalaAsignadosPteResolver(Expediente expediente) {
		return RecursoExpManager.instance().filtrarSala(computeRecursosAsignadosPteResolver(expediente));
	}

	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosCasacionPteAsignacion(Expediente expediente) {
		return RecursoExpManager.instance().filtrarCasacion(computeRecursosPteAsignacion(expediente));
	}

	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosCortePteAsignacion(Expediente expediente) {
		return RecursoExpManager.instance().filtrarCorteSuprema(computeRecursosPteAsignacion(expediente));
	}
	
	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosCasacionAsignadosPteResolver(Expediente expediente) {
		return RecursoExpManager.instance().filtrarCasacion(computeRecursosAsignadosPteResolver(expediente));
	}

	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosCorteAsignadosPteResolver(Expediente expediente) {
		return RecursoExpManager.instance().filtrarCorteSuprema(computeRecursosAsignadosPteResolver(expediente));
	}
	
	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosSecretariaEspecialPteAsignacion(Expediente expediente) {
		return RecursoExpManager.instance().filtrarSecretariaEspecial(computeRecursosPteAsignacion(expediente));
	}

	@SuppressWarnings("unchecked")
	public List<RecursoExp>  computeRecursosParaCirculacionExpediente(Expediente expediente) {
		String enTramite = EstadoResolucionRecursoEnumeration.Type.en_tramite.getValue().toString();

		List<RecursoExp> recursoExpList =
		getEntityManager().createQuery("from RecursoExp where expediente.id = :expedienteId and status <> -1 and estadoResolucion = :enTramite and tipoRecurso.tipoSala = :tipoSala")
			.setParameter("expedienteId", expediente.getId())
			.setParameter("enTramite", enTramite)
			.setParameter("tipoSala", TipoSalaResolucionRecursoEnumeration.Type.circulacion_del_expediente.getValue())
			.getResultList();
		return recursoExpList;
	}
	

	////
	
	
	@SuppressWarnings("unchecked")
	public List<RecursoExp> computeRecursosSecretariaEspecialAsignadosPteResolver(Expediente expediente) {
		return RecursoExpManager.instance().filtrarSecretariaEspecial(computeRecursosAsignadosPteResolver(expediente));
	}
	
	@SuppressWarnings("unchecked")
	public RecursoExp computeUltimoRecursoAsignadoPteResolver(Expediente expediente) {
		return computeUltimoRecurso(computeRecursosAsignadosPteResolver(expediente));
	}

	@SuppressWarnings("unchecked")
	public RecursoExp computeUltimoRecursoSalaAsignadoPteResolver(Expediente expediente) {
		return computeUltimoRecurso(computeRecursosSalaAsignadosPteResolver(expediente));
	}

	@SuppressWarnings("unchecked")
	public RecursoExp computeUltimoRecursoCasacionAsignadoPteResolver(Expediente expediente) {
		return computeUltimoRecurso(computeRecursosCasacionAsignadosPteResolver(expediente));
	}

	@SuppressWarnings("unchecked")
	public RecursoExp computeUltimoRecursoCorteAsignadoPteResolver(Expediente expediente) {
		return computeUltimoRecurso(computeRecursosCorteAsignadosPteResolver(expediente));
	}
	
	@SuppressWarnings("unchecked")
	public RecursoExp computeUltimoRecursoSecretariaEspecialAsignadoPteResolver(Expediente expediente) {
		return computeUltimoRecurso(computeRecursosSecretariaEspecialAsignadosPteResolver(expediente));
	}
	
	@SuppressWarnings("unchecked")
	public RecursoExp computeUltimoRecursoSalaPteAsignacion(Expediente expediente) {
		return computeUltimoRecurso(computeRecursosSalaPteAsignacion(expediente));
	}

	@SuppressWarnings("unchecked")
	public RecursoExp computeUltimoRecursoCasacionPteAsignacion(Expediente expediente) {
		return computeUltimoRecurso(computeRecursosCasacionPteAsignacion(expediente));
	}

	@SuppressWarnings("unchecked")
	public RecursoExp computeUltimoRecursoCortePteAsignacion(Expediente expediente) {
		return computeUltimoRecurso(computeRecursosCortePteAsignacion(expediente));
	}
	
	@SuppressWarnings("unchecked")
	public RecursoExp computeUltimoRecursoSecretariaEspecialPteAsignacion(Expediente expediente) {
		return computeUltimoRecurso(computeRecursosSecretariaEspecialPteAsignacion(expediente));
	}

	@SuppressWarnings("unchecked")
	public RecursoExp computeUltimoRecursoPteAsignacion(Expediente expediente) {
		return computeUltimoRecurso(computeRecursosPteAsignacion(expediente));
	}

	
	public RecursoExp computeUltimoRecurso(
			List<RecursoExp> recursoExpList) {
		Collections.sort(recursoExpList, new Comparator<RecursoExp>() {
			@Override
			public int compare(RecursoExp o1, RecursoExp o2) {
				int returnValue = 0;
				if (o1.getClave() != null && o2.getClave() != null) {
					returnValue = -1 * o1.getClave().compareTo(o2.getClave());
				}
				if (returnValue == 0) {
					if (o1.getFechaPresentacion() != null && o2.getFechaPresentacion() != null) {
						returnValue = -1 * o1.getFechaPresentacion().compareTo(o2.getFechaPresentacion());
					}
				}
				if (returnValue == 0) {
					if (o1.getFechaResolucionElevacion() !=  null && o2.getFechaResolucionElevacion() !=  null) {
						returnValue = -1 * o1.getFechaResolucionElevacion().compareTo(o2.getFechaResolucionElevacion());
					}
				}
				if (returnValue == 0) {
					returnValue = -1 * new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
				}
				return returnValue;
			}
		});
		if (recursoExpList.size() > 0) {
			return recursoExpList.get(0);
		} else {
			return null;
		}
	}
	
	
	public static boolean isUltimoRecursoAsignadoPteResolver(EntityManager entityManager, RecursoExp recursoExp) {
		RecursoExp ultimoRecursoAsignadoPteResolver = recursoExp.getExpediente().getUltimoRecursoAsignadoPteResolver();
		return ultimoRecursoAsignadoPteResolver != null && ultimoRecursoAsignadoPteResolver.getId().equals(recursoExp.getId());
	}	

	@SuppressWarnings("unchecked")
	private String buscarClaveRecursosAsignadosPteResolver(Expediente expediente) {
		RecursoExp ret = computeUltimoRecursoAsignadoPteResolver(expediente);
		if (ret != null) {
			getEntityManager().refresh(ret);
			return  ret.getClave();
		}
		return null;
	}

	public static Materia getMateria (Expediente expediente) {
		Materia ret = null;
		ObjetoJuicio o = expediente.getObjetoJuicioPrincipal();
		if (o != null) {
			ret = o.getMateria();
		}
		if (ret == null) {
			ret = expediente.getMateria();
		}
		return ret;
	}
	
	public OficinaAsignacionExp getRadicacion (Expediente expediente, String tipo) {
		OficinaAsignacionExp ret = null;
		if(expediente != null){
			if(TipoRadicacionEnumeration.Type.juzgado.getValue().equals(tipo)){
				ret = expediente.getRadicacionJuzgado();
			}else if(TipoRadicacionEnumeration.Type.sala.getValue().equals(tipo)){
				ret = expediente.getRadicacionSala();
			}else if(TipoRadicacionEnumeration.Type.salaEstudio.getValue().equals(tipo)){
				ret = expediente.getRadicacionSalaEstudio();
			}
		}
		return ret;
		
	}
	public String getRadicacionAbreviatura (Expediente expediente, String tipo) {
		OficinaAsignacionExp oae = getRadicacion(expediente, tipo);
		if(oae != null){
			return oae.getOficinaConSecretaria().getCodigoAbreviado();
		}else{
			return null;
		}
	}
	
	public String getRadicacionDescripcion (Expediente expediente, String tipo) {
		OficinaAsignacionExp oae = getRadicacion(expediente, tipo);
		if(oae != null){
			return oae.getOficinaConSecretaria().getDescripcion();
		}else{
			return null;
		}				
	}	
	
	public String getRadicacionAbreviatura (Expediente expediente) {
		String abreviaturaRadicacion = "";
		OficinaAsignacionExp juzgado = expediente.getRadicacionJuzgado();
		OficinaAsignacionExp juzgadoPlenario = expediente.getRadicacionJuzgadoPlenario();
		OficinaAsignacionExp sala = expediente.getRadicacionSala();
		OficinaAsignacionExp tribunalOral = expediente.getRadicacionTribunalOral();
		OficinaAsignacionExp casacion = expediente.getRadicacionCasacion();
		OficinaAsignacionExp corte = expediente.getRadicacionCorteSuprema();
		
		if (juzgado == null && sala == null && tribunalOral == null && juzgadoPlenario == null && casacion == null && corte == null) {
			Oficina oficina = expediente.getOficinaInicial();
			if (oficina != null) {
				abreviaturaRadicacion  = oficina.getCodigoAbreviado();
			}
		} else {
			if (juzgado!=null) {
				if (juzgado.getSecretaria()!=null) {
					abreviaturaRadicacion = juzgado.getSecretaria().getCodigoAbreviado();
				} else {
					abreviaturaRadicacion = juzgado.getOficina().getCodigoAbreviado();
				}
			}
			if (juzgadoPlenario!=null) {
				if (!"".equals(abreviaturaRadicacion)) {
					abreviaturaRadicacion+=" - ";
				}
				if (juzgadoPlenario.getSecretaria()!=null) {
					abreviaturaRadicacion += juzgadoPlenario.getSecretaria().getCodigoAbreviado();
				} else {
					abreviaturaRadicacion += juzgadoPlenario.getOficina().getCodigoAbreviado();
				}
			}
			if (sala!=null) {
				if (!"".equals(abreviaturaRadicacion)) {
					abreviaturaRadicacion+=" - ";
				}
				abreviaturaRadicacion+= sala.getOficina().getCodigoAbreviado();
			}
			if (tribunalOral!=null) {
				if (!"".equals(abreviaturaRadicacion)) {
					abreviaturaRadicacion+=" - ";
				}
				abreviaturaRadicacion+= tribunalOral.getOficina().getCodigoAbreviado();
			}
			if (casacion != null) {
				if (!"".equals(abreviaturaRadicacion)) {
					abreviaturaRadicacion+=" - ";
				}
				abreviaturaRadicacion+= casacion.getOficina().getCodigoAbreviado();
			}
			if (corte != null) {
				if (!"".equals(abreviaturaRadicacion)) {
					abreviaturaRadicacion+=" - ";
				}
				abreviaturaRadicacion+= corte.getOficina().getCodigoAbreviado();
			}
		}
		
		
		return abreviaturaRadicacion;
	}
	
	

	public String getRadicacionDescripcion (Expediente expediente) {
		String descripcionRadicacion = "";
		OficinaAsignacionExp juzgado = expediente.getRadicacionJuzgado();
		OficinaAsignacionExp juzgadoPlenario = expediente.getRadicacionJuzgadoPlenario();
		OficinaAsignacionExp sala = expediente.getRadicacionSala();
		OficinaAsignacionExp tribunalOral = expediente.getRadicacionTribunalOral();
		OficinaAsignacionExp casacion = expediente.getRadicacionCasacion();
		OficinaAsignacionExp corte = expediente.getRadicacionCorteSuprema();
		
		if (juzgado == null && sala == null && tribunalOral == null && juzgadoPlenario == null && casacion == null && corte == null) {
			Oficina oficina = expediente.getOficinaInicial();
			if (oficina != null) {
				descripcionRadicacion  = oficina.getDescripcion();
			}
		} else {
			if (juzgado!=null) {
				if (juzgado.getSecretaria()!=null) {
					descripcionRadicacion = juzgado.getSecretaria().getDescripcion();
				} else {
					descripcionRadicacion = juzgado.getOficina().getDescripcion();
				}
			}
			if (juzgadoPlenario!=null) {
				if (!"".equals(descripcionRadicacion)) {
					descripcionRadicacion+=" - ";
				}
				if (juzgadoPlenario.getSecretaria()!=null) {
					descripcionRadicacion += juzgadoPlenario.getSecretaria().getDescripcion();
				} else {
					descripcionRadicacion += juzgadoPlenario.getOficina().getDescripcion();
				}
			}
			if (sala!=null) {
				if (!"".equals(descripcionRadicacion)) {
					descripcionRadicacion+=" - ";
				}
				descripcionRadicacion+= sala.getOficina().getDescripcion();
			}
			if (tribunalOral!=null) {
				if (!"".equals(descripcionRadicacion)) {
					descripcionRadicacion+=" - ";
				}
				descripcionRadicacion+= tribunalOral.getOficina().getDescripcion();
			}
			if (casacion!=null) {
				if (!"".equals(descripcionRadicacion)) {
					descripcionRadicacion+=" - ";
				}
				descripcionRadicacion+= casacion.getOficina().getDescripcion();
			}
			if (corte!=null) {
				if (!"".equals(descripcionRadicacion)) {
					descripcionRadicacion+=" - ";
				}
				descripcionRadicacion+= corte.getOficina().getDescripcion();
			}
		}
		
		return descripcionRadicacion;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean calcularDomicilioInvestigado(Expediente expediente) {
		boolean returnValue = false;
		
		if (expediente.getId() != null) {
			List<IntervinienteExp> intervinienteExpList = getEntityManager().createQuery("from IntervinienteExp where expediente = :expediente and status <> -1")
			.setParameter("expediente", expediente)
			.getResultList();
			for (IntervinienteExp intervinienteExp: intervinienteExpList) {
				if (intervinienteExp.getInterviniente().isDomicilioInvestigado()) {
					returnValue = true;
					break;
				}
			}
			return returnValue;
		} else { 
			return null;
		}
		
	}

	public Expediente findById(Integer id) {
		return getEntityManager().find(Expediente.class, id);
	}
	
	public String solveCancelaElevacionLabel(Expediente expediente) {
		if (expediente.getTipoSubexpediente() != null) {
			if (NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue().toString().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente())) {
				return "expediente.action.cancelaElevacionTO.label";
			} else {
				return "expediente.action.cancelaElevacionPlenario.label";
			}
		} else {
			return null;
		}
	}
	
	public String solveImprimirMinuta(Expediente expediente) {
		if (expediente.getTipoSubexpediente() != null) {
			if (NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue().toString().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente())) {
				return "expediente.action.imprimirMinutaElevacionTO";
			} else {
				return "expediente.action.imprimirMinutaElevacionPlenario";
			}
		} else {
			return null;
		}
	}
	
	public String solveImprimirMinutaAction(Expediente expediente) {
		if (expediente.getTipoSubexpediente() != null) {
			if (NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue().toString().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente())) {
				return "imprimirMinutaElevacionTO";
			} else {
				return "imprimirMinutaElevacion";
			}
		} else {
			return null;
		}
	}
	
	public String solveCambioElevacion(Expediente expediente) {
		if (expediente.getTipoSubexpediente() != null) {
			if (NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue().toString().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente())) {
				return "expediente.action.cambiarElevacionPlenario";
			} else {
				return "expediente.action.cambiarElevacionTO";
			}
		} else {
			return null;
		}
	}
	public String solveCambioElevacionComment(Expediente expediente) {
		if (expediente.getTipoSubexpediente() != null) {
			if (NaturalezaSubexpedienteEnumeration.Type.tribunalOral.getValue().toString().equals(expediente.getTipoSubexpediente().getNaturalezaSubexpediente())) {
				return "expediente.action.cambiarElevacionPlenario.comment";
			} else {
				return "expediente.action.cambiarElevacionTO.comment";
			}
		} else {
			return null;
		}
	}

	public String doCambioElevacion() {
		getExpedienteHome().setVisibleDialog("cambioElevacion", true);
		return "";
	}
	
	public String gestionTags(){
		getExpedienteHome().setVisibleDialog("gestionTags", true);
		return "";
	}
	
	public String doGestionTags(){
		getEntityManager().flush();
		getExpedienteHome().setVisibleDialog("gestionTags", false);
		return "";
	}
	
	public void doCancelGestionTags(){
		getExpedienteHome().setVisibleDialog("gestionTags", false);
	}
	
	/** ATOS COMERCIAL */
	public String doInitCalculaLiquidacion() {
		getExpedienteHome().setVisibleDialog("calculaLiquidacion", true);
		return "";
	}
	
	public void doCancelCalculaLiquidacion() {
		getExpedienteHome().setVisibleDialog("calculaLiquidacion", false);
	}
	
	public boolean isQuiebra(Expediente expediente) {
		if (expediente == null || expediente.getObjetoJuicio() == null) return false;
		return ObjetoJuicioEnumeration.QUIEBRAS_COMERCIAL.equals(expediente.getObjetoJuicio().getCodigo()) ||
				ObjetoJuicioEnumeration.PROPIA_QUIEBRA_ART_66_BIS_COMERCIAL.equals(expediente.getObjetoJuicio().getCodigo()) ||
				ObjetoJuicioEnumeration.PROPIAS_QUIEBRAS_COMERCIAL.equals(expediente.getObjetoJuicio().getCodigo())  ;
	}

	public boolean isPedidoQuiebra(Expediente expediente) {
		if (expediente == null || expediente.getObjetoJuicio() == null) return false;
		return ObjetoJuicioEnumeration.PEDIDOS_QUIEBRA_COMERCIAL.equals(expediente.getObjetoJuicio().getCodigo()) ||
					ObjetoJuicioEnumeration.PEDIDO_QUIEBRA_ART_66_BIS_COMERCIAL.equals(expediente.getObjetoJuicio().getCodigo())	;
	}

	public boolean isConcursoPreventivo(Expediente expediente){
		if (expediente == null || expediente.getObjetoJuicio() == null) return false;
		return ObjetoJuicioEnumeration.CONCURSOS_PREVENTIVOS_COMERCIAL.equals(expediente.getObjetoJuicio().getCodigo())	;
	}
	
	public boolean isAcuerdoPreventivo(Expediente expediente){
		if (expediente == null || expediente.getObjetoJuicio() == null) return false;
		return ObjetoJuicioEnumeration.ACUERDO_PREVENTIVO_EXTRAJUDICIAL_COMERCIAL.equals(expediente.getObjetoJuicio().getCodigo()) ||
				ObjetoJuicioEnumeration.ACUERDOS_PRE_CONCURSALES_COMERCIAL.equals(expediente.getObjetoJuicio().getCodigo())	;
	}
	
	public void renovarCaratula(Expediente expedinte) {
		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
		actuacionExpManager.addActuacionRenovarCaratula(expedinte, detalleRenovarCaratula);
		detalleRenovarCaratula = null;
	}
	
	public String getDetalleRenovarCaratula() {
		return detalleRenovarCaratula;
	}

	public void setDetalleRenovarCaratula(String detalleRenovarCaratula) {
		this.detalleRenovarCaratula = detalleRenovarCaratula;
	}
 
	
	@SuppressWarnings("unchecked")
	public List<Expediente> findByNumeroAnioOficinaActual(Integer numero, Integer anio) {
		Oficina oficinaActual = SessionState.instance().getGlobalOficina();
		String query = "select e from Expediente e where e.status = 0 and " +
				"e.oficinaActual = :oficinaActual and " +
				"e.numero = :numero and e.anio = :anio";
		Query q = getEntityManager().createQuery(query)
									.setParameter("numero", numero)
									.setParameter("anio", anio)
									.setParameter("oficinaActual", oficinaActual);
		
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Expediente> findByNumeroAnioCamaraActual(Integer numero, Integer anio) {
//		String zonaOficina = SessionState.instance().getGlobalOficina().getZonaOficina();
		Camara camaraAcutal = SessionState.instance().getGlobalCamara();
		String query = "select e from Expediente e where e.status = 0 and " +
//				"e.oficinaActual in (select o from Oficina o where o.zonaOficina = :zonaOficina and status <> -1) and " +
				"e.oficinaActual.camara = :camaraActual and " +
				"e.numero = :numero and e.anio = :anio";
		Query q = getEntityManager().createQuery(query)
									.setParameter("numero", numero)
									.setParameter("anio", anio)
									.setParameter("camaraActual", camaraAcutal);
//									.setParameter("zonaOficina", zonaOficina);
		
		return q.getResultList();
	}
	/** ATOS COMERCIAL */
	
	public String rehabilitacionPlazoVencido(){
		getExpedienteHome().setVisibleDialog("rehabilitacionPlazoVencido", true);
		return "";
	}
	
	public String doRehabilitacionPlazoVencido(){
		getExpedienteHome().setVisibleDialog("rehabilitacionPlazoVencido", false);
		try {
			new AccionRehabilitacionPlazoVencido().doAccion(getExpedienteHome().getInstance());
			if (ImpresionManager.instance().isHasColaImpresionCaratulas(SessionState.instance().getGlobalOficina())) {
				imprimirCaratula(getExpedienteHome().getInstance(), ImpresionManager.instance().getColaImpresionCaratulas(SessionState.instance().getGlobalOficina()));
			}
		} catch (AccionException e) {
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_rehabilitacion_plazo_vencido");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			getExpedienteHome().setVisibleDialog("rehabilitacionPlazoVencido", true);
		}
		return "";		
	}
	
	public void doCancelRehabilitacionPlazoVencido(){
		getExpedienteHome().setVisibleDialog("rehabilitacionPlazoVencido", false);
	}

	public boolean isPlazoInicioVencido(Expediente expediente) {
		boolean plazoVencido = false;
		Integer diasPlazo = ConfiguracionMateriaManager.instance().getDiasPlazoInicioExpedientes(SessionState.instance().getGlobalCamara(), null);
		if(!isIniciado(expediente) && (diasPlazo != null)) {
			Date diaSorteo = CalendarUtil.toDay(expediente.getFechaRehabilitacionPlazo());
			if(diaSorteo == null) {
				diaSorteo = CalendarUtil.toDay(expediente.getFechaSorteoCarga());
			}
			if (diaSorteo != null) {
				Date diaUltimoPlazo = CalendarioManager.instance().getDiaHabil(diaSorteo, diasPlazo);
				if (DateUtil.getToday().after(diaUltimoPlazo)) {
					plazoVencido = true;
				}
			}
		}
		return plazoVencido;
	}
	public String getMensagePlazoInicioVencido(Expediente expediente) {
		if (isPlazoInicioVencido(expediente) ) {
			Integer diasPlazo = ConfiguracionMateriaManager.instance().getDiasPlazoInicioExpedientes(SessionState.instance().getGlobalCamara(), null);
			return interpolate(getMessages().get("expediente.plazoInicioVencido"), diasPlazo);
		}
		return null;
	}
	
	public boolean isPlazoInicioRehabilitado(Expediente expediente) {
		if(ConfiguracionMateriaManager.instance().getDiasPlazoInicioExpedientes(SessionState.instance().getGlobalCamara(), null) != null) {
			return expediente.getFechaRehabilitacionPlazo() != null;
		}
		return false;
	}
	
	public String gestionGrupoExpediente(){
		getExpedienteHome().setVisibleDialog("gestionGrupoExpediente", true);
		return "";
	}
	
	public String doGestionGrupoExpediente(){
		getEntityManager().flush();
		getExpedienteHome().setVisibleDialog("gestionGrupoExpediente", false);
		return "";
	}
	
	public void doCancelGestionGrupoExpediente(){
		getExpedienteHome().setVisibleDialog("gestionGrupoExpediente", false);
	}

	public boolean doFinalizeCambioElevacion() {
		boolean ret = true;
		try {
			new AccionCambioElevacion().doAccion(getExpedienteHome().getInstance());
		} catch (AccionException e) {
			ret = false;
			String msg = org.jboss.seam.international.Messages.instance().get("expedienteManager.error_cambio_elevacion");
			getLog().error(msg, e);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
		}
		if(ret){
			getExpedienteHome().setVisibleDialog("cambioElevacion", false);
		}
		return ret;
	}
	
	public void doCancelCambioElevacion() {
		getExpedienteHome().setVisibleDialog("cambioElevacion", false);
	}
	
	///// Borrado de Radicaciones

	public CambioAsignacionExp findCambioAsignacionExpByOficinaRadicacion(Expediente expediente, Oficina oficina, String tipoRadicacion){
		List<CambioAsignacionExp> historicoAsignaciones = CambioAsignacionExpSearch.findByExpediente(getEntityManager(), expediente);		
		CambioAsignacionExp  ret = findLastCambioAsignacioExpByOficina(
				historicoAsignaciones, oficina, tipoRadicacion);
		return ret;
						
	}
	
	public CambioAsignacionExp findLastCambioAsignacionByCamara(Expediente expediente, String tipoRadicacion, Camara camara){
		List<CambioAsignacionExp> historicoAsignaciones = new ArrayList<CambioAsignacionExp>(CambioAsignacionExpSearch.findByExpediente(getEntityManager(), expediente));
		CambioAsignacionExp ret = null;
		for (CambioAsignacionExp c : historicoAsignaciones) {
			if(!isCambioAsignacionMigracion(c)){
				if (equals(camara, c.getOficina().getCamara())	&& 
					(tipoRadicacion == null || tipoRadicacion.equals(c.getTipoRadicacion()))) {
					if (ret == null
							|| ret.getFechaAsignacion().before(
									c.getFechaAsignacion())) {
						ret = c;
					}
				}
			}
		}
		return ret;
		

	}

	private boolean isCambioAsignacionMigracion(CambioAsignacionExp c) {
		return TipoAsignacionLex100Enumeration.Type.migracion.getValue().equals(c.getTipoAsignacionLex100());
	}

	public void deleteOficinaAsignacionExp(OficinaAsignacionExp oficinaAsignacionExp, String descripcionTipoAsignacion, String codigoTipoAsignacion,  String observaciones, boolean eliminarHistorico, boolean forceDelete, boolean descompensar, boolean addActuacion) {
		Expediente expediente = oficinaAsignacionExp.getExpediente();
		
		TipoRadicacionEnumeration.Type tipoRadicacion = (TipoRadicacionEnumeration.Type) TipoRadicacionEnumeration.instance().getType(oficinaAsignacionExp.getTipoRadicacion());
		
		// check historio cambioAsignacionExp
		List<CambioAsignacionExp> historicoAsignaciones = new ArrayList<CambioAsignacionExp>(CambioAsignacionExpSearch.findByExpediente(getEntityManager(), expediente));		
		CambioAsignacionExp  cambioAsignacionExpABorrar = findLastCambioAsignacioExpByOficina(
				historicoAsignaciones, oficinaAsignacionExp.getOficina(),
				oficinaAsignacionExp.getTipoRadicacion());
				
		if (cambioAsignacionExpABorrar != null) {
			historicoAsignaciones.remove(cambioAsignacionExpABorrar);
		}else if (!forceDelete) {
			String errInIncidente =	"expedienteManager.error_borrar_radicacion_copia_del_principal";
			String errInPrincipal =	"expedienteManager.error_borrar_radicacion_copia_del_subexpediente";
			
			String err = isPrincipal(expediente) ? errInPrincipal : errInIncidente;
			String msg = org.jboss.seam.international.Messages.instance().get(err);
			getExpedienteHome().addStatusMessage(StatusMessage.Severity.ERROR, msg);
			return;			
		}
		/*
		CambioAsignacionExp cambioAsignacionExpAnterior = findLastCambioAsignacioExpByTipoRadicacion(	historicoAsignaciones, oficinaAsignacionExp.getTipoRadicacion());		
		OficinaAsignacionExp oficinaAsignacionExpAnteriorRecuperada = cambioAsignacionExpAnterior == null ? null : new OficinaAsignacionExp(cambioAsignacionExpAnterior);
		*/
		//
		getEntityManager().remove(oficinaAsignacionExp);
		if(eliminarHistorico && (cambioAsignacionExpABorrar != null)){
			cambioAsignacionExpABorrar.setLogicalDeleted(true);
		}
		/*
		if(oficinaAsignacionExpAnteriorRecuperada != null){
			oficinaAsignacionExpAnteriorRecuperada.setExpediente(expediente);
			oficinaAsignacionExpAnteriorRecuperada.setTipoAsignacionOficina(TipoAsignacionOficinaEnumeration.Type.reasignacion.getValue().toString());
			getEntityManager().persist(oficinaAsignacionExpAnteriorRecuperada);
		}
		*/

		if(TipoRadicacionEnumeration.Type.juzgado.equals(tipoRadicacion)) {
			Conexidad conexidadDetectada = getConexidadDetectada(expediente);
			if (conexidadDetectada != null) {
				conexidadDetectada.setPrincipal(false);
			}
		}

		String detalle = interpolate(getMessages().get("expediente.cancelarAsignacion.detalle"), TipoRadicacionEnumeration.instance().getLabel(tipoRadicacion.getValue()), OficinaAsignacionExpManager.instance().getDescripcion(oficinaAsignacionExp));
		if (descripcionTipoAsignacion != null) {
			detalle += " - " + descripcionTipoAsignacion;
		}
		if (!Strings.isEmpty(observaciones)) {
			detalle += " - " + observaciones;
		}
		String codigoCambioAsignacion = (codigoTipoAsignacion != null ? codigoTipoAsignacion : TipoCambioAsignacionEnumeration.CANCELACION_ASIGNACION);

		if (descompensar) {
			Sorteo sorteo = MesaSorteo.instance().buscaSorteoByOficina(oficinaAsignacionExp.getOficina(),oficinaAsignacionExp.getRubro(), tipoRadicacion);
			if ((cambioAsignacionExpABorrar != null) && (sorteo != null) && !Boolean.FALSE.equals(oficinaAsignacionExp.getCompensado())) { // Solo descompensa si se ha sorteado en este expediente (cambioAsignacionExp) si no sera una copia de la familia
				ExpedienteManager.instance().enviarCompensacion(sorteo, TipoBolilleroSearch.getTipoBolilleroAsignacion(getEntityManager()), expediente, null,
						tipoRadicacion, oficinaAsignacionExp.getOficina(),
						null, // oficina nueva no hay
						oficinaAsignacionExp.getRubro(), null,
						oficinaAsignacionExp.getCompensado(),
						true,
						SessionState.instance().getUsername(), codigoCambioAsignacion, detalle, false);
			}
		}
		if(addActuacion) {
			ActuacionExpManager.instance().addActuacionCancelarRadicacionExpediente(expediente, oficinaAsignacionExp, cambioAsignacionExpABorrar, detalle, codigoCambioAsignacion);
		}
		getEntityManager().flush();

		getEntityManager().refresh(expediente);
	}
	
	@Transactional
	public void deleteOficinaAsignacionExp(OficinaAsignacionExp oficinaAsignacionExp, boolean eliminarHistorico, boolean force, boolean descompensar) {
		deleteOficinaAsignacionExp(oficinaAsignacionExp, null, null, null, eliminarHistorico, force, descompensar, true);
	}

	private CambioAsignacionExp findLastCambioAsignacioExpByOficina(
			List<CambioAsignacionExp> historicoAsignaciones, Oficina oficina,
			String tipoRadicacion) {
		CambioAsignacionExp ret = null;
		for (CambioAsignacionExp c : historicoAsignaciones) {
			if (equals(oficina, c.getOficina())	&& 
				(tipoRadicacion == null || tipoRadicacion.equals(c.getTipoRadicacion()))) {
				if (ret == null
						|| ret.getFechaAsignacion().before(
								c.getFechaAsignacion())) {
					ret = c;
				}
			}
		}
		return ret;
	}

	private CambioAsignacionExp findLastCambioAsignacioExpByTipoRadicacion(
			List<CambioAsignacionExp> historicoAsignaciones,
			String tipoRadicacion) {
		CambioAsignacionExp ret = null;
		for (CambioAsignacionExp c : historicoAsignaciones) {
			if (tipoRadicacion.equals(c.getTipoRadicacion())) {
				if (ret == null	|| ret.getFechaAsignacion().before(	c.getFechaAsignacion())) {
					ret = c;
				}
			}
		}
		return ret;
	}

	@Transactional
	public String borrarExpediente() {
		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
		actuacionExpManager.addActuacionBorradoExpediente(getExpedienteHome().getInstance());
		getExpedienteHome().getInstance().setLogicalDeleted(true);
		return getExpedienteHome().doUpdate("");		
	}

	
	public boolean isBandejaCambioSituacion() {
		return bandejaCambioSituacion;
	}

	public void setBandejaCambioSituacion(boolean bandejaCambioSituacion) {
		this.bandejaCambioSituacion = bandejaCambioSituacion;
	}
	
	public boolean isInhabilitadoPadron(LetradoIntervinienteExp letradoIntervinienteExp) {		
		return isInhabilitadoPadron(getAsInteger(letradoIntervinienteExp.getLetrado().getTomo()), getAsInteger(letradoIntervinienteExp.getLetrado().getFolio()),letradoIntervinienteExp.getLetrado().getFederal());
	}
	
	public boolean isInhabilitadoPadron(Integer tomo, Integer folio, boolean federal) {
		VFLetradoSearch vfletradoSearch = (VFLetradoSearch) Component.getInstance(VFLetradoSearch.class, true);
		VFLetrado vfletrado = vfletradoSearch.findAnyByNaturalKey(tomo, folio, federal);
		if(vfletrado != null){
			return  vfletrado.isInhabilitado();
		}
		return false;
	}

	private Integer getAsInteger(String number) {
		Integer value = null;
		try {
			value = Integer.valueOf(number.trim());
		} catch (Exception e) {
		}
		return value;
	}

	public Conexidad getConexidadDetectada(Expediente expediente) {
		for(Conexidad conexidad: expediente.getConexidadList()){
			if(Boolean.TRUE.equals(conexidad.getPrincipal())){
				return conexidad;
			}
		}
		return null;
	}

	public boolean isAnyIntervinienteIdentidadReservada(Expediente expediente) {
		boolean anyIdentidadReservada = false;
		List<IntervinienteExp> intervinienteExpList = getEntityManager().createQuery("from IntervinienteExp where expediente = :expediente and status <> -1")
														.setParameter("expediente", expediente).getResultList();
		for (IntervinienteExp intervinienteExp : intervinienteExpList) {
			if (intervinienteExp.isIdentidadReservada()) {
				anyIdentidadReservada = true;
				break;
			}
		}
		return anyIdentidadReservada;
	}

	public static SimpleDateFormat getSimpleDateFormat() {
		return simpleDateFormat;
	}

	public String objetosJuicio(Expediente expediente){
		String ret = new String();
		String separador = ", ";
		List<ObjetoJuicio> objetosJuicioExpList = new ArrayList<ObjetoJuicio>(expediente.getObjetoJuicioList());
		
		if (objetosJuicioExpList.size()>0) {
			for (ObjetoJuicio objetoJuicioExp: expediente.getObjetoJuicioList())
				ret += objetoJuicioExp.getDescripcion() + separador;
			ret = (ret.substring(0, ret.length()-2)).trim();
		} else {
			ret = null;
		}
		
		return ret;
	}
	
	public String actoresPrincipal(Expediente expediente){
		String ret = new String();
		List<IntervinienteExp> actoresExpList = new ArrayList<IntervinienteExp>(expediente.getIntervinienteExpList());
		for (IntervinienteExp intervinienteExp: expediente.getIntervinienteExpList()) {
			if (intervinienteExp.isLogicalDeleted() || !isActor(intervinienteExp)){
				actoresExpList.remove(intervinienteExp);
			}
		}
		
		if (actoresExpList.size()>0) {
			String nombre = actoresExpList.get(0).getInterviniente().getNombre();
			if (actoresExpList.size()>2){
				nombre += " Y OTROS";
			}else if (actoresExpList.size()>1){
				nombre += " Y OTRO";
			}
			ret = nombre;
		} else {
			ret = null;
		}
		return ret;
	}

	public boolean isEditableToolBar(Expediente expediente) {
		return canDoSelectionToolBar(expediente);
	}
	
	private boolean canDoSelectionToolBar(Expediente expediente) {
		return expediente.getEditableEnOficinaActual();
	}
	
	
	public boolean computeEditableEnOficinaActual(Expediente expediente) {
		if(CamaraManager.isCamaraActualCorteSuprema()){
			 // las vocalias tienen visibilidad en todos los expedientes aunque no los tengan ellas
			if(inVocaliaDeCorte() || inSecretariaHonorariosCorte()){
				return true;
			}
		}
		
		
		if(ExpedienteManager.isCirculacion(expediente)){
			// las secretarias de corte tienen visibilidad de todos los expedientes aunque no los tengan ellas. EN circulaciones solo cuando las tinen ellas
			if (inSecretariaDeCorte() || inSalaOficinaActual() || inVocaliaOficinaActual()) {
				return isOficinaActual(expediente) || isVisibleCarpetaCirculacion(expediente.getRecursoExp());
			}
			// Las mesas de entrada no deben de ver las circulaciones a no ser que estén en su oficina.
			if (OficinaManager.instance().inAlgunaMesaDeEntrada()) {
				return isOficinaActual(expediente);
			}
		}
		
		return 	getIdentity().hasPermission("Expediente", "verContenidoExternos")
			||
			OficinaManager.instance().inCoherencia()
			||
			OficinaManager.instance().inCopias()
			||
			OficinaManager.instance().inAlgunaMesaDeEntrada() 
			||
			ExpedienteManager.instance().isOficinaActual(expediente)
			||
			(expediente.isPase() && SessionState.instance().getGlobalOficinaList().contains(expediente.getOficinaRetorno()))
			||
			ExpedienteManager.instance().isRadicadoOficinaActual(expediente)
			||
			ExpedienteManager.instance().isRadicacionPreviaOficinaActual(expediente)
			||
			ExpedienteManager.instance().isVisibleSubmateria(expediente);
	}
	
	private boolean inSecretariaHonorariosCorte() {
		return CamaraManager.isCamaraActualCorteSuprema() && OficinaManager.instance().isHonorariosOficinaActual();
	}

	public boolean inVocaliaDeCorte(){
		return  CamaraManager.isCamaraActualCorteSuprema() && OficinaManager.instance().isVocaliaOficinaActual();
	}

	public boolean inSecretariaDeCorte(){
		return  CamaraManager.isCamaraActualCorteSuprema() && OficinaManager.instance().isSecretariaCorteSupremaOficinaActual();
	}
	
	public boolean inSalaOficinaActual(){
		return  OficinaManager.instance().isSalaApelaciones(SessionState.instance().getGlobalOficina());
	}

	public boolean inVocaliaOficinaActual(){
		return  OficinaManager.instance().isVocalia(SessionState.instance().getGlobalOficina());
	}

	public boolean computeRadicadoEnOficinaActual(Expediente expediente) {
		if (isLIP(expediente)) {
			return true;
		}
		for (Integer idOficina : expediente.getOficinaRadicacionIdSet()) {
			if (getSessionState().getGlobalIdOficinaSet().contains(idOficina)) {
				return true;
			}
		}
		
		return false;
		
	}
	
	public boolean computeRadicadoEnOficina(Expediente expediente, Oficina oficina) {
		if (isLIP(expediente)) {
			return true;
		}
		for (Integer idOficina : expediente.getOficinaRadicacionIdSet()) {
			if (oficina.getId().equals(idOficina)) {
				return true;
			}
		}
		
		return false;
		
	}
	

	
	public String getPrimeraIntervencionSala(Expediente expediente) {
		List<CambioAsignacionExp> cambioAsignacionExpList = (List<CambioAsignacionExp>)getEntityManager().createQuery("select e from CambioAsignacionExp e where e.expediente = :expediente and e.tipoRadicacion = :tipoRadicacion order by e.fechaAsignacion asc")
		.setParameter("expediente", expediente)
		.setParameter("tipoRadicacion", TipoRadicacionEnumeration.Type.sala.getValue().toString())
		.setMaxResults(1)
		.getResultList();
		if(cambioAsignacionExpList != null & cambioAsignacionExpList.size() > 0){
			CambioAsignacionExp cambioAsignacionExp = cambioAsignacionExpList.get(0);
			if(cambioAsignacionExp != null){
				if(cambioAsignacionExp.getFechaAsignacion() != null){
					return simpleDateFormat.format(cambioAsignacionExp.getFechaAsignacion());
				}
				return null;
			}
		}	
		return null;
	}
	
	public List<Oficina> getVocaliasSala(Expediente expediente) {
		return expediente.getVocaliasSala();
	}

	public List<Oficina> calcularVocaliasSala(Expediente expediente) {
		List<Oficina> result = new ArrayList<Oficina>();
		if (expediente.getRadicacionesVocalia().size() > 0) {
			for (OficinaAsignacionExp oficinaAsignacionExp : expediente.getRadicacionesVocalia()) {
				result.add(oficinaAsignacionExp.getOficina());
			}
		} else if (expediente.getRadicacionSala() != null){
			result.addAll(
				getEntityManager().createQuery("from Oficina where tipoOficina.codigo = :tipoOficina and status <> -1 and oficinaSuperior = :sala order by codigo")
					.setParameter("tipoOficina", TipoOficinaEnumeration.TIPO_OFICINA_VOCALIA)
					.setParameter("sala", expediente.getRadicacionSala().getOficina())
					.getResultList());
		}
		return result;
	}

	public List<Oficina> calcularVocaliasCasacion(Expediente expediente) {
		List<Oficina> result = new ArrayList<Oficina>();
		if (expediente.getRadicacionesVocaliaCasacion().size() > 0) {
			for (OficinaAsignacionExp oficinaAsignacionExp : expediente.getRadicacionesVocaliaCasacion()) {
				result.add(oficinaAsignacionExp.getOficina());
			}
		} else if (expediente.getRadicacionCasacion() != null){
			result.addAll(
				getEntityManager().createQuery("from Oficina where tipoOficina.codigo = :tipoOficina and status <> -1 and oficinaSuperior = :sala order by codigo")
					.setParameter("tipoOficina", TipoOficinaEnumeration.TIPO_OFICINA_VOCALIA)
					.setParameter("sala", expediente.getRadicacionCasacion().getOficina())
					.getResultList());
		}
		return result;
	}
	
	public String doInitCambioOrdenIntervinientes() {
		List<IntervinienteExp> intervinienteExpList =
		getEntityManager().createQuery("from IntervinienteExp ie " +
				"where ie.expediente = :expediente " +
				"and ie.status <> -1 " +
				"and ie.tipoInterviniente.numeroOrden is not null " +
				"order by ie.orden")
			.setParameter("expediente", getExpedienteHome().getInstance())
			.getResultList();
		this.intervinientesReordenarList.clear();
		getExpedienteHome().getInstance().resetOrderedIntervinientes();
		IntervinienteExpManager intervinienteExpManager = IntervinienteExpManager.instance();
		for (IntervinienteExp intervinienteExp : intervinienteExpList) {
			if (intervinienteExpManager.isPendienteVerificar(intervinienteExp) || intervinienteExp.isNoSaleEnCaratula()) {
				continue;
			}
			this.intervinientesReordenarList.add(intervinienteExp);
		}
		getExpedienteHome().setVisibleDialog("cambioOrdenIntervinientes", true);
		return "";
	}
	
	public String doReasignarLetrado() {
		getExpedienteHome().setVisibleDialog("reasignarLetrado", true);
		return "";
	}
	
	public void doCancelReasignarLetrado(){
		getExpedienteHome().setVisibleDialog("reasignarLetrado", false);
	}
	
	@Transactional
	public String doFinalizeReasignarLetrado() {
		LetradoIntervinienteExp letradoIntervinienteExp = getExpedienteHome().getLetradoIntervinienteExpActual();
		letradoIntervinienteExp.setIntervinienteExp(getExpedienteHome().getIntervinienteAReasignar());
		LetradoIntervinienteExpHome letradoIntervinienteExpHome = (LetradoIntervinienteExpHome) Component.getInstance(LetradoIntervinienteExpHome.class, false);
		letradoIntervinienteExpHome.setInstance(letradoIntervinienteExp);
		letradoIntervinienteExpHome.update(letradoIntervinienteExp);
	    
		IntervinienteExpHome intervinienteExpHome = (IntervinienteExpHome) Component.getInstance(IntervinienteExpHome.class, false);
		for(IntervinienteExp intervinienteExp : this.getExpedienteHome().getInstance().getIntervinienteExpList()){
			intervinienteExpHome.setInstance(intervinienteExp);
			intervinienteExpHome.doReset();
		}
		
		getExpedienteHome().setVisibleDialog("reasignarLetrado", false);
		
		return "";
	}
	/***/
	public String doCopiarLetrado() {
		getExpedienteHome().setVisibleDialog("copiarLetrado", true);
		return "";
	}
	
	public void doCancelCopiarLetrado(){
		getExpedienteHome().setVisibleDialog("copiarLetrado", false);
	}
	
	@Transactional
	public String doFinalizeCopiarLetrado() {
		LetradoIntervinienteExpHome letradoIntervinienteExpHome = (LetradoIntervinienteExpHome) Component.getInstance(LetradoIntervinienteExpHome.class, false);
		letradoIntervinienteExpHome.setInstance(getExpedienteHome().getLetradoIntervinienteDeCopiar());
		
		letradoIntervinienteExpHome.setEmptyInstance(null);
		LetradoIntervinienteExp letradoIntervinienteExp = (LetradoIntervinienteExp)letradoIntervinienteExpHome.getEmptyInstance();
		letradoIntervinienteExpHome.duplicate(letradoIntervinienteExp);
		letradoIntervinienteExp.setIntervinienteExp(getExpedienteHome().getIntervinienteDestino());
		
		letradoIntervinienteExpHome.setInstance(letradoIntervinienteExp);
		letradoIntervinienteExpHome.doAddValidarSau(null);
		
		/*Para el refresco del listado*/
		IntervinienteExpHome intervinienteExpHome = (IntervinienteExpHome) Component.getInstance(IntervinienteExpHome.class, false);
		for(IntervinienteExp intervinienteExp : this.getExpedienteHome().getInstance().getIntervinienteExpList()){
			intervinienteExpHome.setInstance(intervinienteExp);
			intervinienteExpHome.doReset();
			intervinienteExpHome.clearInstance();
		}
		
		intervinienteExpHome.clearInstance();
		getExpedienteHome().setVisibleDialog("copiarLetrado", false);
		
		return "";
	}
	
	public String doFusionarInterviniente() {
		getExpedienteHome().setVisibleDialog("fusionarInterviniente", true);
		return "";
	}
	
	public void doCancelFusionarInterviniente(){
		getExpedienteHome().setVisibleDialog("fusionarInterviniente", false);
	}
	
	private void fusionarInterviniente(IntervinienteExp origen, IntervinienteExp destino){
		destino.setPoder(origen.getPoder());
		destino.getInterviniente().setApellidos(origen.getInterviniente().getApellidos());
	}
	
	@Transactional
	public String doFinalizeFusionarInterviniente(){
		
		this.fusionarInterviniente(getExpedienteHome().getIntervinienteOrigenFusionar(), getExpedienteHome().getIntervinienteDestinoFusionar());
		
		IntervinienteHome intervinienteHome = (IntervinienteHome) Component.getInstance(IntervinienteHome.class, false);
		intervinienteHome.setInstance(getExpedienteHome().getIntervinienteDestinoFusionar().getInterviniente());
		
		IntervinienteExpHome intervinienteExpHome = (IntervinienteExpHome) Component.getInstance(IntervinienteExpHome.class, false);
		intervinienteExpHome.setInstance(getExpedienteHome().getIntervinienteDestinoFusionar());
		intervinienteExpHome.doUpdate();
		
		this.getExpedienteHome().doReset();
		
		/*Para el refresco del listado*/
		for(IntervinienteExp intervinienteExp : this.getExpedienteHome().getInstance().getIntervinienteExpList()){
			intervinienteExpHome.setInstance(intervinienteExp);
			intervinienteExpHome.doReset();
			intervinienteExpHome.clearInstance();
		}
		
		this.getExpedienteHome().setVisibleDialog("fusionarInterviniente", false);
		return "/web/expediente/expedienteCompleto.xhtml";
	}
	/***/
	
	public List<IntervinienteExp> getIntervinientesReordenarList() {
		return intervinientesReordenarList;
	}

	public void setIntervinientesReordenarList(List<IntervinienteExp> intervinientesReordenarList) {
		this.intervinientesReordenarList = intervinientesReordenarList;
	}
	
	public void doCancelReordenacionIntervinientes() {
		getExpedienteHome().setVisibleDialog("cambioOrdenIntervinientes", false);
	}
	@Transactional
	public void doFinalizeReordenacionIntervinientes() {
		int orden = 0;
		if (intervinientesReordenarList.size() > 0) {
			for (IntervinienteExp intervinienteExp : intervinientesReordenarList) {
				orden++;
				intervinienteExp.setOrden(orden);
			}
			List<IntervinienteExp> intervinienteExpList =
				getEntityManager().createQuery("from IntervinienteExp ie " +
						"where ie.expediente = :expediente " +
						"and ie not in (:intervinientesReordenados) "+
						"and ie.status <> -1 " +
						"order by ie.orden")
					.setParameter("expediente", getExpedienteHome().getInstance())
					.setParameter("intervinientesReordenados", this.intervinientesReordenarList)
					.getResultList();
			for (IntervinienteExp intervinienteExp : intervinienteExpList) {
				orden++;
				intervinienteExp.setOrden(orden);
			}
		}
		actualizarCaratula(null, null);
		getEntityManager().flush();
		getEntityManager().refresh(getExpedienteHome().getInstance());
		getExpedienteHome().setVisibleDialog("cambioOrdenIntervinientes", false);
		setDocumentoActual(null);
		
	}
	
	public Date getFechaAsignacionJuzgado(Expediente expediente) {
		if (expediente.getRadicacionJuzgado() != null) {
			return expediente.getRadicacionJuzgado().getFechaAsignacion();
		} 
		return null;
	}
	
	public Date getFechaAsignacionSala(Expediente expediente) {
		if (expediente.getRadicacionSala() != null) {
			return expediente.getRadicacionSala().getFechaAsignacion();
		} 
		return null;
	}

	public boolean hasIntegracionVocalesSala(Expediente expediente) {
		for(OficinaAsignacionExp oficinaAsignacionExp: expediente.getOficinaAsignacionExpList()) {
			if (TipoRadicacionEnumeration.isRadicacionVocalias(oficinaAsignacionExp.getTipoRadicacion()) && OficinaManager.instance().isApelaciones(oficinaAsignacionExp.getOficina())) {
				return true;
			}
		}
		return false;
	}
	
	private String calculaCaratulaMediacion(Expediente expediente, List<ObjetoJuicio> objetoJuicioList, String descripcionObjetoJuicio, IntervinienteExp[] intervinientes) {
		String plantillaCaratula = calculaCaratulaIntervinientes("Requirente: <1> - Requerido: <2> s/objeto", intervinientes); 
		if(objetoJuicioList != null){
			ObjetoJuicio[] objetosJuicio = null;
			objetosJuicio = objetoJuicioList.toArray(new ObjetoJuicio[objetoJuicioList.size()]);
			plantillaCaratula = fillCaratulaObjetos(plantillaCaratula, objetosJuicio, descripcionObjetoJuicio);
		}
		return plantillaCaratula;
}

	public boolean isVisibleSubmateria(Expediente expediente) {
		String submateria = ConfiguracionMateriaManager.instance().getVisibilidadCamaraSubmateria(CamaraManager.getCamaraActual());
		if (!Strings.isEmpty(submateria)) {
			if (submateria.equalsIgnoreCase("ALL")) {
				return true;
			} else {
				ObjetoJuicio objetoJuicio = expediente.getObjetoJuicioPrincipal();
				if (objetoJuicio != null && objetoJuicio.getTema() != null) {
					return submateria.equalsIgnoreCase(objetoJuicio.getTema().getCodigo());
				}
			}
		}
		
		return false;
	}
	
	
	public static Competencia getCompetencia(Expediente expediente) {
		Competencia ret = null;
		ObjetoJuicio o = expediente.getObjetoJuicioPrincipal();
		if (o != null) {
			ret = o.getCompetencia();
		}
		if (ret == null) {
			ret = expediente.getCompetencia();
		}
		return ret;
	}
	
	public List<Fiscalia> computeFiscaliaList(Expediente expediente) {
		List<Fiscalia> result = new ArrayList<Fiscalia>();
		boolean segundaResolved = false;
		Fiscalia fiscaliaPrimera = null;
		for (OficinaAsignacionExp oficinaAsignacionExp: expediente.getOficinaAsignacionExpList()) {
			Fiscalia fiscalia = OficinaAsignacionExpManager.getFiscaliaAsignada(oficinaAsignacionExp, true);
			if (fiscalia != null) {
				result.add(fiscalia);
				if(TipoRadicacionEnumeration.isRadicacionJuzgado(oficinaAsignacionExp.getTipoRadicacion())){
					fiscaliaPrimera = fiscalia;					
				}
				if(TipoRadicacionEnumeration.isRadicacionSala(oficinaAsignacionExp.getTipoRadicacion())){
					segundaResolved = true;
				}
			}
		}
		
		if(!segundaResolved && fiscaliaPrimera !=null){
			Fiscalia segunda = findFiscaliaSegunda(fiscaliaPrimera);
			if(segunda != null){
				result.add(segunda);
			}
		}
		
		return result;
	}
	
	private Fiscalia findFiscaliaSegunda(Fiscalia fiscaliaPrimera){
		Fiscalia segunda = null;
//		SessionState sessionState = SessionState.instance();
		Query query = getEntityManager().createQuery("from TurnoFiscaliaSala turno " +
				"where turno.competencia = :competencia " +
				"and turno.codFiscaliaPrimera = :fiscalia "+
				"and turno.camara = :camara " +
				"order by turno.fechaDesde DESC");
		
		query.setParameter("competencia", getExpedienteHome().getInstance().getCompetencia());
		query.setParameter("fiscalia", fiscaliaPrimera.getCodigo());
		query.setParameter("camara", getExpedienteHome().getInstance().getCamaraActual());
		
		List<TurnoFiscaliaSala> turnoFiscaliaSalaList = query.getResultList();
		if(!turnoFiscaliaSalaList.isEmpty() 
			&& turnoFiscaliaSalaList.get(0).getFechaDesde().before(new Date())){
			Query queryFiscalia = getEntityManager().createQuery("from Fiscalia fiscalia " +
					"where fiscalia.competencia = :competencia " +
					"and fiscalia.codigo = :codigo ");
			queryFiscalia.setParameter("competencia", getExpedienteHome().getInstance().getCompetencia());
			queryFiscalia.setParameter("codigo", turnoFiscaliaSalaList.get(0).getCodFiscaliaSegunda());
			
			List<Fiscalia> fiscalias = queryFiscalia.getResultList();
			for (Fiscalia fiscalia : fiscalias) {
				if(fiscalia.getTipoInstancia() != null && fiscalia.getTipoInstancia().getAbreviatura() != null &&
				   fiscalia.getTipoInstancia().getAbreviatura().equals(TipoInstanciaEnumeration.TIPO_INSTANCIA_APELACIONES_ABREVIATURA)){
					
					segunda=fiscalia;
				}
			}
		}else{//si no encuentra con competencia, busca sin competencia
			query = getEntityManager().createQuery("from TurnoFiscaliaSala turno " +
					"where turno.codFiscaliaPrimera = :fiscalia "+
					"and turno.camara = :camara " +
					"order by turno.fechaDesde DESC");
			
			query.setParameter("fiscalia", fiscaliaPrimera.getCodigo());
			query.setParameter("camara", getExpedienteHome().getInstance().getCamaraActual());
			
			if(!turnoFiscaliaSalaList.isEmpty() 
				&& turnoFiscaliaSalaList.get(0).getFechaDesde().before(new Date())){
				Query queryFiscalia = getEntityManager().createQuery("from Fiscalia fiscalia " +
						"where fiscalia.competencia = :competencia " +
						"and fiscalia.codigo = :codigo ");
				queryFiscalia.setParameter("competencia", getExpedienteHome().getInstance().getCompetencia());
				queryFiscalia.setParameter("codigo", turnoFiscaliaSalaList.get(0).getCodFiscaliaSegunda());
				
				List<Fiscalia> fiscalias = queryFiscalia.getResultList();
				for (Fiscalia fiscalia : fiscalias) {
					if(fiscalia.getTipoInstancia() != null && fiscalia.getTipoInstancia().getAbreviatura() != null &&
					   fiscalia.getTipoInstancia().getAbreviatura().equals(TipoInstanciaEnumeration.TIPO_INSTANCIA_APELACIONES_ABREVIATURA)){
						segunda=fiscalia;
					}
				}
			}
		}
		return segunda;
	}
	
	
	private boolean hasFiscaliaSegundaAutomatica(Camara camara){
		
		return camara.getId().intValue() == PENAL_ORDINARIO.intValue();
	}

	public ActuacionExp getUltimaActuacionEvento() {
		return ultimaActuacionEvento;
	}

	public List<Oficina> calcularVocaliasEstudioCasacion(Expediente expediente) {
		List<Oficina> result = new ArrayList<Oficina>();
		if (expediente.getRadicacionesVocaliaEstudioCasacion().size() > 0) {
			for (OficinaAsignacionExp oficinaAsignacionExp : expediente.getRadicacionesVocaliaEstudioCasacion()) {
				result.add(oficinaAsignacionExp.getOficina());
			}
		} else if (expediente.getRadicacionCasacion() != null){
			result.addAll(
				getEntityManager().createQuery("from Oficina where tipoOficina.codigo = :tipoOficina and status <> -1 and oficinaSuperior = :sala order by codigo")
					.setParameter("tipoOficina", TipoOficinaEnumeration.TIPO_OFICINA_VOCALIA)
					.setParameter("sala", expediente.getRadicacionCasacion().getOficina())
					.getResultList());
		}
		return result;
	}
	
	public String getSubCaratula(){
		Expediente instance = getExpedienteHome().getInstance();
		String ret = null;
		if(isEscrito(instance)){
			ret = instance.getDescripcionEscrito();
		}else if(isCirculacion(instance)){			
			StringBuffer sb = new StringBuffer();
			if(instance.getRecursoExp() != null){
				concat(sb, instance.getRecursoExp().getClave(), " - ");
				if(instance.getRecursoExp().getTipoRecurso() != null){
					concat(sb, instance.getRecursoExp().getTipoRecurso().getDescripcion(), " - ");
				}

			}
			if(instance.getDescripcionCirculacion() != null){
				concat(sb, instance.getDescripcionCirculacion(), " - ");
			}
			ret = sb.toString();
		}
		return ret;
	}
	
	private void concat(StringBuffer sb, String value, String separator) {
		if(value != null){
			if(sb.length() > 0){
				sb.append(separator);
			}
			sb.append(value);
		}		
	}

	public boolean isInOficinaGestionRecursos(){
		Oficina oficina = SessionState.instance().getGlobalOficina();
		return OficinaManager.instance().isApelaciones(oficina) || OficinaManager.instance().isCorteSuprema(oficina);
	}
	
	public boolean isHasRecursosAsignadosPteResolver(){
		return getRecursosAsignadosPteResolver().size() > 0;
	}
	
	public List<RecursoExp> getRecursosAsignadosPteResolver(){
		return getRecursosAsignadosPteResolver(ExpedienteHome.instance().getInstance());
	}

	public List<RecursoExp> getRecursosAsignadosPteResolver(Expediente expediente){
		if(CamaraManager.isCorteSuprema(CamaraManager.getCamaraActual())){
			return computeRecursosCorteAsignadosPteResolver(ExpedienteHome.instance().getInstance());			
		}else{
			return computeRecursosSalaAsignadosPteResolver(ExpedienteHome.instance().getInstance());
		}
	}
	
	public boolean hayPruebas(Expediente expediente){
		Boolean ret = pruebasMap.get(expediente.getId());
		if(ret == null){
			String ejbql = "select count(*) from Prueba where expediente.id = :id ";
			Query query = getEntityManager().createQuery(ejbql);
			query.setParameter("id", expediente.getId());
			totalPruebas = (Long) query.getSingleResult();
			ret = totalPruebas > 0;
			pruebasMap.put(expediente.getId(), ret);
			if(ret){
				buscarPruebasSinFinalizar(expediente);
			}
		}
		return ret;
	}
	
	private void buscarPruebasSinFinalizar(Expediente expediente){
		String ejbql = "select count(*) from Prueba where expediente.id = :id and finalizada = :finalizada";
		Query query = getEntityManager().createQuery(ejbql);
		query.setParameter("id", expediente.getId());
		query.setParameter("finalizada", false);
		pruebasAbiertas = (Long) query.getSingleResult();
	}

	public String getAlertaPruebas(){
		if (pruebasAbiertas != null) {
			return HAY + totalPruebas + PRUEBAS + pruebasAbiertas + SIN_FINALIZAR;
		}
		return "";
	}
	
	public void finalizarPrueba(){
		if (pruebasAbiertas != null) {
			pruebasAbiertas--;
		}
	}
	
	public List<RecursoExp> getRecursoAsList(Expediente expediente){
		List<RecursoExp> list = new ArrayList<RecursoExp>();
		if(expediente != null && expediente.getRecursoExp() != null){
			list.add(expediente.getRecursoExp());
		}
		return list;
	}

	public ResolucionRecursoExp getVotoMasivo() {
		return votoMasivo;
	}

	public void setVotoMasivo(ResolucionRecursoExp votoMasivo) {
		this.votoMasivo = votoMasivo;
	}
	
	/**
	 * Retorna el recurso en circulacion asociado a este expedeinte
	 */
	public RecursoExp getRecursoCirculacion(Expediente expediente){
		RecursoExp recursoExp = null;
		if(ExpedienteManager.isCirculacion(expediente) || RecursoExpManager.instance().isRecursoCirculacionExpediente(expediente.getRecursoExp())){
			recursoExp = expediente.getRecursoExp();
		}else{
			List<RecursoExp> list = ExpedienteManager.instance().getRecursosAsignadosPteResolver();
			if(list != null && list.size() > 0){
				recursoExp = list.get(0);
			}
		}
		return recursoExp;
	}

//	public void indicarVoto(Expediente expediente, ResolucionRecursoExp voto, DocumentoExp documento) throws AccionException {
//		RecursoExp recursoExp = getRecursoCirculacion(expediente);
//		if(recursoExp == null){
//			String msg = interpolate(getMessages().get("eventoManager.error.noHayRecurso"), expediente.getIdxAnaliticoFirst());
//			throw new AccionException(expediente.getIdxAnaliticoFirst()+" : "+msg);
//		}		
//		boolean ok = ResolucionRecursoExpManager.instance().doEventoIndicarVotacion(recursoExp, voto.getFechaResolucion(), voto.getTipoVoto(), voto.getComentarios(), documento);
//		if(!ok){
//			String msg = interpolate(getMessages().get("eventoManager.error.errorIndicarVotacion"), expediente.getIdxAnaliticoFirst());
//			throw new AccionException(expediente.getIdxAnaliticoFirst()+" : "+msg);
//		}
//		
//	}

	public void doPrepararEnvioAcuerdo(Expediente expediente, DocumentoExp documentoExp) throws AccionException {
		// Estado Procesal = EPA - Preparado para envio a Acuerdo
		EstadoExpediente estadoExpediente = getEstadoExpediente(EstadoExpedientePredefinedEnumeration.Type.EPA.getValue().toString(),  MateriaEnumeration.instance().getMateriaCorteSuprema());
		getExpedienteManager().cambiarEstadoExpediente(expediente, documentoExp, estadoExpediente, null, false);
	
	}

	public void doEnvioAcuerdo(Expediente expediente, Date fechaEnvio, DocumentoExp documentoExp)  {
		// Estado Procesal = ACD - Acuerdo
		EstadoExpediente estadoExpediente = getEstadoExpediente(EstadoExpedientePredefinedEnumeration.Type.ACD.getValue().toString(),  MateriaEnumeration.instance().getMateriaCorteSuprema());
		getExpedienteManager().cambiarEstadoExpediente(expediente, documentoExp, estadoExpediente, null, false);
	
		// Actuacion Información ACD
		String info = "Se envía a acuerdo con fecha " + simpleDateFormat.format(fechaEnvio);
		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
		actuacionExpManager.addInformacionExpediente(expediente, null, TipoInformacionEnumeration.getItemByCodigo(getEntityManager(), TipoInformacionEnumeration.ACUERDO), info, null, fechaEnvio, SessionState.instance().getUsername(), info);
	}

	public void doCancelarEnvioAcuerdo(Expediente expediente, DocumentoExp documentoExp)  {
		if(isPreparadoOEnAcuerdo(expediente)){
			// Estado Procesal = DJR - Devuelto de Jurisprudencia
			EstadoExpediente estadoExpediente = getEstadoExpediente(EstadoExpedientePredefinedEnumeration.Type.DJR.getValue().toString(),  MateriaEnumeration.instance().getMateriaCorteSuprema());
			getExpedienteManager().cambiarEstadoExpediente(expediente, documentoExp, estadoExpediente, null, false);				
		}
	}
	
	private boolean isPreparadoOEnAcuerdo(Expediente expediente) {
		String codigo = expediente.getEstadoExpediente() != null ? expediente.getEstadoExpediente().getCodigo() : null;
		boolean ret = false;
		if(!Strings.isEmpty(codigo) && 
			(codigo.equals(EstadoExpedientePredefinedEnumeration.Type.ACD.getValue()) || codigo.equals(EstadoExpedientePredefinedEnumeration.Type.EPA.getValue()))){
			ret = true;			
		}
		return ret;
	}
	
	public boolean computeAcumuladoJuridicoMadre(Expediente expediente) {
		return !expediente.isAcumuladoJuridicoHija() && getAcumuladoJuridicoHijas(expediente).size() > 0;
	}
	
	public boolean computeAcumuladoJuridicoHija(Expediente expediente) {
		return computeExpedienteAcumuladoJuridicoMadre(expediente) != null;
	}

	public List<Expediente> getAcumuladoJuridicoHijas(Expediente expediente) {
		return getEntityManager().createQuery("select ve.expedienteRelacionado from VinculadosExp ve where ve.expediente = :expediente and ve.tipoVinculado = '"+TipoVinculadoEnumeration.Type.acumulado_Material.getValue()+"' ")
			.setParameter("expediente", expediente)
			.getResultList();
	}

	public Expediente computeExpedienteAcumuladoJuridicoMadre(
			Expediente expediente) {
		try {
			Expediente expedienteMadre = (Expediente) 
			getEntityManager().createQuery("select ve.expediente from VinculadosExp ve where ve.expedienteRelacionado = :expediente and ve.tipoVinculado='"+TipoVinculadoEnumeration.Type.acumulado_Material.getValue()+"' order by ve.idVinculadosExp")
			.setParameter("expediente", expediente)
			.setMaxResults(1)
			.getSingleResult();
			return expedienteMadre;
		} catch (NoResultException e) {
		}
		
		return null;
	}
	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}
	
	public boolean isPosibleFirmarDocumento(){
		boolean ret = true;
		String IEPValue = EstadoExpedientePredefinedEnumeration.Type.IEP.getValue().toString();
		EstadoExpediente estadoExpediente = getExpedienteHome().getInstance().getEstadoExpediente();		
		if(estadoExpediente != null && IEPValue.equals(estadoExpediente.getCodigo())){
			ret = false;
		}
		return ret;
	}

	public String getEstadoCerrarCirculacion() {
		return estadoCerrarCirculacion;
	}

	public void setEstadoCerrarCirculacion(String estadoCerrarCirculacion) {
		this.estadoCerrarCirculacion = estadoCerrarCirculacion;
	}

	public DocumentoExp getDespachoCierreCirculacion() {
		return despachoCierreCirculacion;
	}

	public void setDespachoCierreCirculacion(DocumentoExp despachoCierreCirculacion) {
		this.despachoCierreCirculacion = despachoCierreCirculacion;
	}

	public static boolean showConexidadDeclarada() {
		return showConexidadDeclarada(CamaraManager.getCamaraActual());
	}

	public static boolean showConexidadDeclarada(Camara camara) {
		return !CamaraManager.isCamaraCOM(camara) && !CamaraManager.isCorteSuprema(camara);
	}

	public Expediente getExpedienteSuperiorPrincipal(Expediente expediente) {
		Expediente expedienteLocal = expediente;
		while (expedienteLocal.getExpedienteOrigen() != null) {
			expedienteLocal = expedienteLocal.getExpedienteOrigen();
		}
		return expedienteLocal;
	}

	public String getLetradosExpediente(Expediente expediente){
		StringBuffer buffer = new StringBuffer();
		
		for(IntervinienteExp intervinienteExp : expediente.getIntervinienteExpList()) {
			if(!intervinienteExp.isPteVerificacion()){
				List<LetradoIntervinienteExp> list = getLetrados(intervinienteExp);
					for(LetradoIntervinienteExp letrado : list){
						Letrado letrado2 = letrado.getLetrado();
						buffer.append(letrado2.getNombre())
							  .append(",");
					}
			}
		}
		if(buffer.length() > 1)
			buffer.delete(buffer.length()-1, buffer.length());
		return buffer.toString();
	}
	/* ATOS - Notificaciones Electronicas */


	@Transactional
	public void traerAcumuladoJuridicoHijas(Expediente expediente) {
		getEntityManager().joinTransaction();
		int numHijas = 0;
		for (Expediente hija: getAcumuladoJuridicoHijas(expediente)) {
			if (!isOficinaActual(hija)) {
				traerExpediente(hija);
				numHijas++;
			}
		}
		getStatusMessages().addFromResourceBundle("expediente.action.traerVinculadosHijas.message", numHijas);
	}

	public boolean isPosiblePaseAutomaticoRetornoFiscalia(Expediente expediente){
		if(ExpedienteManager.instance().isOficinaActual(expediente) && ExpedienteManager.instance().isEditable(expediente,false)){
			Oficina oficinaActual = SessionState.instance().getGlobalOficina();
			Oficina retorno = calcularRetornoFiscalia(expediente, oficinaActual);
			return retorno != null;
		} else {
			return false;
		}
	}
	
	/**
	 * Verifica si existe una fiscalia como dependencia externa asociada a la radicacion de un {@link Expediente}
	 * @param expediente {@link Expediente} instanciado
	 * @return <code>true</code> en caso de que exista la fiscalia externa o <code>false</code> en caso contrario
	 */
	public boolean isPosiblePaseAutomaticoFiscalia(Expediente expediente){
		if(ExpedienteManager.instance().isOficinaActual(expediente) && ExpedienteManager.instance().isEditable(expediente,false)){
			Oficina oficinaActual = SessionState.instance().getGlobalOficina();
			TipoRadicacionEnumeration.Type tipoRadicacion = TipoRadicacionEnumeration.calculateTipoRadicacionByTipoInstancia(oficinaActual.getTipoInstancia().getId());
			Oficina fiscalia = calcularFiscalia(expediente, tipoRadicacion);
			if (fiscalia!=null) {
				// si la oficina de fiscalia esta no esta habilitada para pases se busca la dependencia para usar esa como pase externo
				if (!fiscalia.isHabilitadaPases())
					return (fiscalia.getDependencia()!=null);
				else
					return true;
			}
		}
		return false;
	}

	public Oficina calcularRetornoFiscalia(Expediente expediente,
			Oficina oficinaActual) {
		OficinaAsignacionExp radicacion = null;
		if (OficinaManager.instance().isPrimeraInstancia(oficinaActual)) {
			radicacion = getRadicacionByTipoRadicacion(expediente, TipoRadicacionEnumeration.Type.juzgado);
		} else if (OficinaManager.instance().isApelaciones(oficinaActual)) {
			radicacion = getRadicacionByTipoRadicacion(expediente, TipoRadicacionEnumeration.Type.sala);
		}
		if (radicacion != null) {
			return radicacion.getOficinaConSecretaria();
		}
		return null;
	}
	
	/**
	 * Obtiene la Fiscalia como {@link Oficina} interna a partir de un {@link Expediente} y su radicacion
	 * @param expediente {@link Expediente} instanciado
	 * @param tipoRadicacion
	 * @return {@link Oficina} con la fiscalia interna
	 */
	public Oficina calcularFiscalia(Expediente expediente, Type tipoRadicacion) {
		return getFiscaliaRadicacionExpediente(expediente, tipoRadicacion);
	}
	
	public boolean isPosiblePaseAutomaticoSecretaria(Expediente expediente){
		return isPosiblePaseAutomaticoSecretaria(expediente, getRecursoCirculacion(expediente));
	}

	public boolean isPosiblePaseAutomaticoSecretaria(Expediente expediente, RecursoExp recursoExp){
		if(ExpedienteManager.instance().isOficinaActual(expediente) && recursoExp != null){
			Oficina secretaria = ResolucionRecursoExpHome.instance().getSecretariaRadicacion(recursoExp);
			Oficina oficinaActual = OficinaManager.instance().getOficinaActual();		
			return secretaria != null && oficinaActual != null && !secretaria.equals(oficinaActual);
		}else{
			return false;
		}
	}

	private boolean isAcumulado(Expediente expediente) {
		return SituacionExpedienteEnumeration.Type.acumulado.getValue().equals(expediente.getSituacion()) ;	
	}

	public boolean puedeCircular(Expediente expediente) {
		return
			ConfiguracionMateriaManager.instance().isUsaCirculacionCamara(SessionState.instance().getGlobalCamara(), expediente.getMateria())
			&& (expediente.getRecursoExp() != null) 
			&& TipoResolucionRecursoEnumeration.Type.firma_individual.getValue().equals(expediente.getRecursoExp().getTipoRecurso().getTipoResolucion()) 
			&& (isCirculacion(expediente) || TipoRecursoEnumeration.instance().isTipoRecursoCirculacionExpediente(expediente.getRecursoExp().getTipoRecurso()))
		;
	}

	public boolean showCirculacionTab(Expediente expediente) {
		return puedeCircular(expediente);
	}

	@SuppressWarnings("unchecked")
	public static List<Expediente> getCirculaciones(EntityManager entityManager, Expediente expediente){
		List<Expediente> circulacionList;
		circulacionList = entityManager.createQuery("select e from Expediente e where e.expedienteOrigen = :expediente and e.naturaleza = :naturalezaSubexpediente and e.status <> -1")
			.setParameter("expediente", expediente)
			.setParameter("naturalezaSubexpediente", NaturalezaSubexpedienteEnumeration.Type.circulacion.getValue().toString())
			.getResultList();

		return circulacionList;
	}

	public boolean isUrgente(Expediente expediente) {
		return (expediente != null) && (expediente.isUrgente() || ObjetoJuicioEnumeration.instance().isUrgente(expediente.getObjetoJuicio()));
	}

	public boolean isEventoNotificar() {
		return getEventoInfo() != null && getEventoInfo().isCodigoSeleccion(CodigoSeleccionEstadoExpedienteEnumeration.Type.notificar);
	}

	public static IntervinienteExp findActorByDocumento(Expediente expediente, String codigoTipoDocumento, String numeroDocumento){
		if (!Strings.isEmpty(numeroDocumento)) {
			for(IntervinienteExp intervinienteExp : expediente.getIntervinienteExpList()){
				if (!intervinienteExp.isLogicalDeleted() && intervinienteExp.isActor() && (intervinienteExp.getInterviniente() != null)) {
//					if(numeroDocumento.equals(intervinienteExp.getInterviniente().getNumeroDocId()) || numeroDocumento.equals(intervinienteExp.getInterviniente().getDni())){
					String codigoTipoDocumentoActor = (intervinienteExp.getInterviniente().getTipoDocumentoIdentidad() != null) ? intervinienteExp.getInterviniente().getTipoDocumentoIdentidad().getCodigo(): null;
					if(TipoDocumentoIdentidadEnumeration.equals(codigoTipoDocumentoActor, intervinienteExp.getInterviniente().getNumeroDocId(), codigoTipoDocumento, numeroDocumento) 
						||
						TipoDocumentoIdentidadEnumeration.equals(TipoDocumentoIdentidadEnumeration.DNI_CODE, intervinienteExp.getInterviniente().getDni(), codigoTipoDocumento, numeroDocumento) ){
						return intervinienteExp;
					} 
				}
			}
		}
		return null;
	}
	
	public void asignarSiguienteOrdenCirculacion(OficinaAsignacionExp oficinaAsignacionExp) {
		asignarOrdenCirculacion(oficinaAsignacionExp, calculaSiguienteOrdenCirculacion(oficinaAsignacionExp));
	}

	public void asignarOrdenCirculacion(OficinaAsignacionExp oficinaAsignacionExp, String codigoOrden) {
		oficinaAsignacionExp.setCodigoOrdenCirculacion(codigoOrden);
		Expediente expediente = oficinaAsignacionExp.getExpediente();
		if(!isPrincipal(expediente)){
			Expediente principal = getExpedientePrincipal(getEntityManager(), expediente);
			TipoRadicacionEnumeration.Type tipoRadicacion = (TipoRadicacionEnumeration.Type) TipoRadicacionEnumeration.instance().getType(oficinaAsignacionExp.getTipoRadicacion());
			OficinaAsignacionExp  oficinaAsignacionExpPrincipal = findRadicacionExpediente(principal, tipoRadicacion);
			// copia el orden de circulacion de la asignacion de sala al padre si es la misma sala y no tiene orden
			if ((oficinaAsignacionExpPrincipal != null) && (oficinaAsignacionExpPrincipal.getOficina() != null) &&  (oficinaAsignacionExpPrincipal.getOficina().getId() == oficinaAsignacionExp.getOficina().getId())){
				oficinaAsignacionExpPrincipal.setCodigoOrdenCirculacion(oficinaAsignacionExp.getCodigoOrdenCirculacion());
			}
		}
	}

	private void asignaFiscalia(OficinaAsignacionExp oficinaAsignacionExp, Integer fiscalia) {
		oficinaAsignacionExp.setFiscalia(fiscalia);
		Expediente expediente = oficinaAsignacionExp.getExpediente();
		String tipoRadicacion = oficinaAsignacionExp.getTipoRadicacion();
		if(!ExpedienteManager.isPrincipal(expediente)){
			Expediente principal = ExpedienteManager.getExpedientePrincipal(getEntityManager(), expediente);
			OficinaAsignacionExp  oficinaAsignacionExpPrincipal = OficinaAsignacionExpSearch.findByTipoRadicacion(getEntityManager(), principal, tipoRadicacion);
			// copia la fiscalia si es la misma sala y no tiene 
			if ((oficinaAsignacionExpPrincipal != null) && (oficinaAsignacionExpPrincipal.getFiscalia() == null) && (oficinaAsignacionExpPrincipal.getOficina() != null) &&  (oficinaAsignacionExpPrincipal.getOficina().getId() == oficinaAsignacionExp.getOficina().getId())){
				oficinaAsignacionExpPrincipal.setFiscalia(fiscalia);
			}
		}
	}
	
	public void copiaRadicacionFiscalia(Type tipoRadicacion, Expediente expediente, OficinaAsignacionExp radicacionConexo) {
		// Copia la fiscalia de la radicacion del conexo si no la tiene asignada por turno
		OficinaAsignacionExp oficinaAsignacionExp = ExpedienteManager.instance().findRadicacionExpediente(expediente, tipoRadicacion);
		if((oficinaAsignacionExp != null) && (oficinaAsignacionExp.getFiscalia() == null)){
			if (radicacionConexo.getFiscalia() != null) {
				asignaFiscalia(oficinaAsignacionExp, radicacionConexo.getFiscalia());
			}
		}
	}

	public void copiaRadicacionOrdenCirculacion(Type tipoRadicacion, Expediente expediente, OficinaAsignacionExp radicacionConexo) {
		// Copia el código de orden de circulacion de la radicacion del conexo si no lo tiene asignado
		OficinaAsignacionExp oficinaAsignacionExp = ExpedienteManager.instance().findRadicacionExpediente(expediente, tipoRadicacion);
		if((oficinaAsignacionExp != null) && (oficinaAsignacionExp.getCodigoOrdenCirculacion() == null)){
			if (radicacionConexo.getCodigoOrdenCirculacion() != null) {
				asignarOrdenCirculacion(oficinaAsignacionExp, radicacionConexo.getCodigoOrdenCirculacion());
			}
		}
	}

	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}

	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}

	public String getEscritoMasivoDescripcion() {
		return escritoMasivoDescripcion;
	}

	public void setEscritoMasivoDescripcion(String escritoMasivoDescripcion) {
		this.escritoMasivoDescripcion = escritoMasivoDescripcion;
	}
	
	public void doAddEscritoBandeja (Expediente expediente, String escritoDescripcion) {
		DocumentoBandejaHome documentoBandejaHome= (DocumentoBandejaHome) Component.getInstance(DocumentoBandejaHome.class, true);
		documentoBandejaHome.createNewInstance();
		documentoBandejaHome.getInstance().setExpediente(expediente);
		documentoBandejaHome.getInstance().setDescripcion(escritoDescripcion);
		documentoBandejaHome.persist();
	}

//	public void asignarVocaliasCasacion(Expediente expediente) {
//		List<Oficina> vocalias = expediente.getVocaliasCasacion();
//		OficinaAsignacionExp radicacionSalaCasacion = expediente.getRadicacionCasacion();
//		List<OficinaAsignacionExp> radicacionesVocalia = expediente.getRadicacionesVocaliaCasacion();
//
//		if (radicacionesVocalia.size() == 0) {
//			for (Oficina vocalia: vocalias) {
//				OficinaAsignacionExp oficinaAsignacionExp = new OficinaAsignacionExp(TipoAsignacionOficinaEnumeration.Type.asignacion.getValue().toString(), 
//						radicacionSalaCasacion.getFechaAsignacion(), expediente);
//				oficinaAsignacionExp.setOficina(vocalia);
//				oficinaAsignacionExp.setCodigoTipoCambioAsignacion(radicacionSalaCasacion.getCodigoTipoCambioAsignacion());
//				oficinaAsignacionExp.setOficinaAsignadora(radicacionSalaCasacion.getOficinaAsignadora());
//				oficinaAsignacionExp.setRubro("VOC");
//				oficinaAsignacionExp.setTipoAsignacion(radicacionSalaCasacion.getTipoAsignacion());
//				oficinaAsignacionExp.setTipoAsignacionLex100(radicacionSalaCasacion.getTipoAsignacionLex100());
//				oficinaAsignacionExp.setTipoAsignacionOficina(radicacionSalaCasacion.getTipoAsignacionOficina());
//				oficinaAsignacionExp.setTipoRadicacion(TipoRadicacionEnumeration.Type.vocaliasCasacion.getValue().toString());
//				oficinaAsignacionExp.setOperador(radicacionSalaCasacion.getOperador());
//				getEntityManager().persist(oficinaAsignacionExp);
//			}
//			getEntityManager().flush();
//			expediente.setRadicacionesVocalia(null);
//			expediente.setRadicacionesVocaliaCasacion(null);
//			expediente.setRadicacionesVocaliaEstudioCasacion(null);
//		}
//
//	}

	public List<String> getParticipantesList(){
		List<String> retorno = new ArrayList<String>();
		for(IntervinienteExp interviniente : IntervinienteExpList.instance().getbyExpedienteSinTestigosResultList()){
			retorno.add(interviniente.getNombreCompleto());
			for(LetradoIntervinienteExp letrado : interviniente.getLetradoIntervinienteExpList()){
				retorno.add(letrado.getLetrado().getNombre());
			}
		}
		for(PeritoExp perito : PeritoExpList.instance().getbyExpedienteResultList()){
			retorno.add(perito.getPeritoProfesion().getPerito().getNombre());
		}
		
		return retorno;
	}

	public void setParticipante(String participante) {
		this.participante = participante;
	}

	public String getParticipante() {
		return participante;
	}
	
	public void cambiarAdespacho(){
		String aDespacho = SituacionExpedienteEnumeration.Type.despacho
				.getValue().toString();
			this.cambiarSituacion(expedienteHome.getInstance(),
					null, aDespacho, true);
	}

	public void refreshExpediente(Expediente expediente) {

		if(expediente != null) {
			if(!getEntityManager().contains(expediente)){
				expediente = getEntityManager().find(Expediente.class, expediente.getId());
			}
			getEntityManager().refresh(expediente);
			expediente.reset();
		}
//		getEntityManager().refresh(expediente.getExpedienteIngreso());
		try{
			getLog().info("Realizando refresh de radicaciones GENERICO del expediente: " + expediente.getClave());
			OficinaAsignacionExpManager.instance().refreshRadicacionExpediente(expediente);
		}catch(Exception e){
			getLog().warn("No se ha podido realizar el refresh del expediente GENERICO en forma correcta");
			e.printStackTrace();
		}
	}
	
	public void restaurarEstadoSorteoExpediente(Expediente expediente) {
		SorteoManager.setStatusSorteoExpediente(getEntityManager(), expediente, null);
	}
	
	public List<SelectItem> tipoReservaPermitidos(String lookupSelect){
	
		TipoRestriccionEnumeration tipoRestriccionEnumeration = (TipoRestriccionEnumeration) Component.getInstance(TipoRestriccionEnumeration.class, true);
		
		return (isPenal(getExpedienteHome().getInstance()) || isExpedienteFamilia(getExpedienteHome().getInstance()))
				? tipoRestriccionEnumeration.getTipoRestriccionPenalItems(lookupSelect) : tipoRestriccionEnumeration.getTipoRestriccionItems(lookupSelect);

	}
	/**ATOS OVD */
	public String noEsViolenciaDomestica() {
		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
		actuacionExpManager.addActuacionAgregarInformeViolenciaDomestica(getExpedienteHome().getInstance(), TipoCambioAsignacionEnumeration.SIN_VIOLENCIA_DOMESTICA, "Se declara que la causa no tiene violencia domestica");
		
		Expediente expediente=getExpedienteHome().getInstance();
		OficinaViolenciaDomesticaExp ovd=expediente.getOficinaViolenciaDomesticaExp();
		if (ovd==null){
			ovd=new OficinaViolenciaDomesticaExp(expediente);
			expediente.setOficinaViolenciaDomesticaExp(ovd);
		}
		
		expediente.getOficinaViolenciaDomesticaExp().noDeclaraViolencia();
		getEntityManager().persist(ovd);
		getEntityManager().persist(expediente);
		
		getEntityManager().flush();
		
		return "";
	}
	
	
	public String esViolenciaDomestica() {
		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
		actuacionExpManager.addActuacionAgregarInformeViolenciaDomestica(getExpedienteHome().getInstance(), TipoCambioAsignacionEnumeration.CON_VIOLENCIA_DOMESTICA, "Se declara que la causa tiene violencia domestica");

		
		
		Expediente expediente=getExpedienteHome().getInstance();
		OficinaViolenciaDomesticaExp ovd=expediente.getOficinaViolenciaDomesticaExp();
		if (ovd==null){
			ovd=new OficinaViolenciaDomesticaExp(expediente);
			expediente.setOficinaViolenciaDomesticaExp(ovd);
		}
		
		expediente.getOficinaViolenciaDomesticaExp().declaraViolencia();
		
		getEntityManager().persist(ovd);
		getEntityManager().persist(expediente);
		
		getEntityManager().flush();
		
		return "";
	}
	public String esViolenciaDomestica(Expediente expediente) {
		ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
		actuacionExpManager.addActuacionAgregarInformeViolenciaDomestica(expediente, TipoCambioAsignacionEnumeration.CON_VIOLENCIA_DOMESTICA, "Se declara que la causa tiene violencia domestica");

		OficinaViolenciaDomesticaExp ovd=expediente.getOficinaViolenciaDomesticaExp();
		if (ovd==null){
			ovd=new OficinaViolenciaDomesticaExp(expediente);
			expediente.setOficinaViolenciaDomesticaExp(ovd);
		}
		
		expediente.getOficinaViolenciaDomesticaExp().declaraViolencia();
		
		getEntityManager().persist(ovd);
		getEntityManager().persist(expediente);
		
		getEntityManager().flush();
		
		return "";
	}
    public void setOficinaViolenciaDomesticaExp(OficinaViolenciaDomesticaExp oficinaViolenciaDomesticaExp) {
        this.oficinaViolenciaDomesticaExp=oficinaViolenciaDomesticaExp;
    }

	public boolean isOVD(){
		return ConfiguracionMateriaManager.instance().isOVD(getExpedienteHome().getInstance().getCamara());
	}
	/**ATOS OVD */
	
	/**ATOS SUBASTA */
	public boolean isSubExpedienteCerradoOCancelado(Expediente expediente) {
		return (SituacionExpedienteEnumeration.Type.cuaderno_cerrado.getValue().equals(expediente.getSituacion()) || SituacionExpedienteEnumeration.Type.cuaderno_cancelado.getValue().equals(expediente.getSituacion()));
	}
	
	public boolean isVerDetalleBienes(Expediente expediente) {
		if (expediente != null) {
		//PASE = 1 y PENDIENTE_RECEPCION = 1 no deberan ver el detalle de los bienes
			return (!expediente.isPase() || !expediente.isPendienteRecepcion());
		} else {
			return false;
		}
	}	
	
	 public boolean haySubastasPendientesTurno(Expediente expediente){

			//Boolean ret = turnosSubastalMap.get(expediente.getId());
			Boolean ret = null;
			//if(ret == null){
				String ejbql = "select count(*) from DetalleSubasta detalleSubasta where " +
				" detalleSubasta.situacion='P' " + 
				" and detalleSubasta.expediente.oficinaActual.id in (#{sessionState.globalIdOficinaSet}) " +
				" and expediente.id = :id " +
				" and status != -1";
				Query query = getEntityManager().createQuery(ejbql);
				query.setParameter("id", expediente.getId());
				Long count = (Long) query.getSingleResult();
				ret = count > 0;
				turnosSubastalMap.put(expediente.getId(), ret);
			//}
			return ret;
		}
		
		public boolean haySubastasSuspendidas(Expediente expediente) {
//			14049707
			Boolean ret = null;
			Boolean subastaValida = false;
			
			for (DetalleSubasta detalleSubasta : expediente.getDetallesSubastaList()) {
				if ("P".equals(detalleSubasta.getSituacion()) || "A".equals(detalleSubasta.getSituacion())) {
					subastaValida = true;
					break;
				}
			}
			if (subastaValida) {
				String ejbql = "select count(*) from DocumentoBandeja documentoBandeja where " +
							" documentoBandeja.expediente.id = :id " +
							" and documentoBandeja.tipo = '" + TipoDocumentoBandejaEnumeration.Type.subasta.getValue().toString() + "' "; 
				Query query = getEntityManager().createQuery(ejbql);
				query.setParameter("id", expediente.getId());
				Long count = (Long) query.getSingleResult();
				ret = count > 0;
				turnosSubastalMap.put(expediente.getId(), ret);
			} else {
				ret = false;
			}
			return ret;
		}
	
		public boolean isSubasta(Expediente instance) {
			return instance != null && instance.getTipoSubexpediente() != null && NaturalezaSubexpedienteEnumeration.Type.subasta.getValue().equals(instance.getTipoSubexpediente().getNaturalezaSubexpediente());
		}
	
		public String doConfirmarSubasta(Expediente instance) {
			getExpedienteHome().setVisibleDialog("confirmarTurnoSubastaExpediente", true);
			this.detalleCambioEstadoExpediente = null;
			return null;
		}
		
		 
		public boolean doFinalizeConfirmarSubasta() {
			boolean ret = true;
			
			Expediente expediente = getExpedienteHome().getInstance();
			try {
				generarPaseSubastas(expediente);
			} catch (AccionException e) {
				getLog().error("Error al relizar pase de expediente de subasta " + expediente.getIdxAnaliticoFirst(), e);
				getStatusMessages().addFromResourceBundle(Severity.ERROR, "confirmaTurnoSubasta.error.pase");
				return false;
			}
			
			
			ActuacionExpManager actuacionExpManager = (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
			TipoInformacion tipoInformacion = TipoInformacionEnumeration.getItemByCodigo(getEntityManager(),
					TipoInformacionEnumeration.SUBASTA_TURNO_CONFIRMADO);
			
			actuacionExpManager.addInformacionExpediente(expediente, null, tipoInformacion, this.detalleCambioEstadoExpediente  ,
					null, null, SessionState.instance().getUsername(), this.detalleCambioEstadoExpediente);
			
			
			getExpedienteHome().setVisibleDialog("confirmarTurnoSubastaExpediente", false);
			
			return ret;
		}
		
		public void generarPaseSubastas(Expediente expediente)
				throws AccionException {
			Oficina oficinaActual = expediente.getOficinaActual();
			
			if(oficinaActual == null){
				oficinaActual = SessionState.instance().getGlobalOficina();
			} 
			OficinaManager oficinaManager = (OficinaManager) Component.getInstance(OficinaManager.class, true);
			Oficina oficinaSubastas = oficinaManager.getOficinaSubasta();
			new AccionPase().doAccion(
					expediente,
					null,
					expediente.getFojas(),
					expediente.getPaquetes(),
					expediente.getCuerpos(),
					expediente.getAgregados(),
					false,
					(String)TipoPaseEnumeration.Type.giro.getValue(),  
					null, this.detalleCambioEstadoExpediente, 
					oficinaSubastas, 
					null, 
					CurrentDateManager.instance().getCurrentDate(), 
					TipoGiroEnumeration.Type.automatico,
					oficinaActual,
					null);
		}
		
		public void doCancelConfirmarSubasta() {
			getExpedienteHome().setVisibleDialog("confirmarTurnoSubastaExpediente", false);
		}
		/** ATOS - SUBASTA */
		
		
		/** ATOS - OM */
		public boolean hayCuestionarioOM(Expediente expediente){
			
			boolean mostrarAlerta = true;
			
			// Oficina oficinaActualExpediente = (Oficina) SessionState.instance().getGlobalObjects().get("oficina");
			
			Long count = (Long)
			getEntityManager().createQuery("select count(*) from OmCuestionario c where c.idExpediente.id = :idExpediente and c.idOficina = :idOficina")
				.setParameter("idExpediente", expediente.getId())
				.setParameter("idOficina", expediente.getOficinaActual())
				.getSingleResult();
			
			if(count == 0){
				mostrarAlerta = false;
			}
			
			return mostrarAlerta;
		}
		
		/** ATOS - OM */
		public boolean estaProtocolizadoOM(Expediente expediente){
			
			if(hayCuestionarioOM(expediente)){
				return false;
			}
			
			boolean protocolizado = false;
			
			@SuppressWarnings("unchecked")
			List<DocumentoExp> despachoList = getEntityManager().createQuery("from DocumentoExp d where d.expediente = :expediente")
											 .setParameter("expediente", expediente)
											 .getResultList();
			
			if(despachoList != null){
			
				for (DocumentoExp despacho : despachoList) {
					if (despacho.isSentencia() && !hayCuestionarioOM(expediente)) {
						protocolizado = true;
					}
				}
			}	
			
			return protocolizado;
		}	
		/** ATOS - OM */		
		
		public boolean mostrarCuestOM() {
			
			Expediente expediente = getExpedienteHome().getInstance();
			
			/*
			System.out.println();
			System.out.println("/////////////////////////////////////////////////////////////////////////////");
			System.out.println("IS MANAGED:         " + getExpedienteHome().isManaged());
			System.out.println("ES NATURALEZA PPAL: " + esNaturalezaPrincipal(expediente));
			System.out.println("ESTA INICIADO: 		" + isIniciado(expediente));
			System.out.println("ESTA PARALIZADO: 	" + isParalizado(expediente));
			System.out.println("/////////////////////////////////////////////////////////////////////////////");
			System.out.println();
			*/
			
			return (getExpedienteHome().isManaged()
					&& esNaturalezaPrincipal(expediente)
					&& isIniciado(expediente)
					&& !isParalizado(expediente));
		}

		public List<LetradoIntervinienteExp> getLetradosPorInterviniente(IntervinienteExp interviniente) {
			return interviniente.getLetradoIntervinienteExpList();
		}
		
		public String doAgregarInformacionOficinaMujer() {
			return "";
		}
		
		public boolean condicionesIconoAlerta(Expediente expediente) {
			
			if(expediente == null)
				expediente = expedienteHome.getInstance();
			
			Camara  camara	= expediente.getCamaraActual();
			
			PaseExpManager paseExpManager  = (PaseExpManager)Component.getInstance(PaseExpManager.class);
			
			// Se determina si el expediente es ppal
			if (ExpedienteManager.isPrincipal(expediente)) {
				
			   // La oficina actual del expediente SE ENCUENTRA en la Lista de Oficinas de Radicacion
			   if (omRespuestasHome.existeOficinaActualEnListadoRadicacion(expediente.getOficinaActual().getId(), null, expediente)) {	
				   
				   // Se determina si existen Documentos para en el expediente corriente en la tabla DOCUMENTO_EXP.
				   // Si no existen documentos  => no se muestra el icono
				   List<DocumentoExp> documentoExp = paseExpManager.existenDocumentosExpediente(expediente, camara);
				   if (documentoExp.size() > 0 ) {
					   		
					   // Se deben verificar las condiciones de Formulario Completo 
					   if (validacionesFormulario(expediente, camara, paseExpManager)) {
					   	
						   // SE MUESTRA ICONO DE ALERTA
						   return true;
		
					   }
				   }
			   }	
			   
			}	
			// NO SE MUESTSRA ICONO DE ALERTA
			return false;
			
		}
		
		private boolean validacionesFormulario(Expediente expediente, Camara camara, PaseExpManager paseExpManager) {
			
			OmRespuestasHome omRespuestasHome = (OmRespuestasHome) Component.getInstance(OmRespuestasHome.class, true);
			OmPreguntasList omPreguntasList   = (OmPreguntasList) Component.getInstance(OmPreguntasList.class, true);
			List<OmPreguntas> preguntasPrincipales   = omPreguntasList.getPreguntasPrincipales();
			List<OmPreguntas> preguntasInterviniente = omPreguntasList.getPreguntasInterviniente();
			
			// Hay preguntas ppales definidas
			if(preguntasPrincipales.size() > 0) {
				
				// Se respondio el cuestionario ppal ?
				if (!omRespuestasHome.hayQueCompletarCuestPrincipal()){
					
					// Cuestionario Ppal RESPONDIDO
					// Se valida si en el cuestionario ppal se respondieron en forma negativa todas las preguntas de opcion obligatorias => 
					// => No se muestra el icono
					if(omRespuestasHome.cuestionarioPpalRespuestasNegativas() == omRespuestasHome.getNroPreguntasObligatorias()) {
						omRespuestasHome.setNroPreguntasObligatorias(0);
						return false;
					}
					else {
						
						// Cuestionario ppal con preguntas de Violencia respondidas afirmativamete. Se sigue hacia los cuestionario de Intervinientes
	                    // Existen preguntas definidas para los intervinientes
						if(preguntasInterviniente.size() > 0) {
							
							
							// Se determina si estan respondidos todos los cuestionarios de los intervinientes
							IntervinienteExpList intervinienteExpList = (IntervinienteExpList)Component.getInstance(IntervinienteExpList.class);
							int cantIntervinientes = intervinienteExpList.getAllResults().size();
							
							if (paseExpManager.cuestionariosIntervinientesRespondidos(cantIntervinientes) == cantIntervinientes) {
							  	
								//	No Se muestra el icono
								return false;
							}
							else {
							  	//  Si faltan cuestion interv => Se muestra el icono
								return true;
							}
							
						}
						else {
							// No existen preguntas definidas para los intervinientes => No Se muestra el icono
							return false;
						}
						
					}

				} 
				else {
					// Cuestionario Ppal NO RESPONDIDO => Se muestra el icono
					return true;
				}

			} // NO hay preguntas ppales definidas => NO Se muestra el icono
			return false;
		}
	
	public void confirmarFormularioCorrupcion(){
		FormularioCorrupcionExp formulario = new FormularioCorrupcionExp();
		formulario.setAlteracionDeProcedimiento(this.getFormularioCorrupcionExp().isAlteracionDeProcedimiento());
		formulario.setUsuario(SessionState.instance().getUsername());
		formulario.setEjercicioDeFuncion(this.getFormularioCorrupcionExp().isEjercicioDeFuncion());
		formulario.setExisteBeneficioPrivado(this.getFormularioCorrupcionExp().isExisteBeneficioPrivado());
		formulario.setFechaFormulario(new Date());
		formulario.setFuncionarioPublico(this.getFormularioCorrupcionExp().isFuncionarioPublico());
		formulario.setPerjuicioParaElEstado(this.getFormularioCorrupcionExp().isPerjuicioParaElEstado());
		formulario.setExpediente(expedienteHome.getInstance());
		formulario.setEstadoExpediente(expedienteHome.getInstance().getEstadoExpediente());
		
		getEntityManager().persist(formulario);
		
		getExpedienteHome().setVisibleDialog("formularioCorrupcion", false);
		
	}
	
	public FormularioCorrupcionExp findUltimoFormularioCorrupcion(){
		return null;
	}

	public void iniciarFormularioCorrupcion(){
		this.formularioCorrupcionExp = new FormularioCorrupcionExp();
	}
	public void setFormularioCorrupcionExp(FormularioCorrupcionExp formularioCorrupcionExp) {
		this.formularioCorrupcionExp = formularioCorrupcionExp;
	}

	public FormularioCorrupcionExp getFormularioCorrupcionExp() {
		return formularioCorrupcionExp;
	}

	public void setMostrarFormularioCorrupcion(boolean mostrarFormularioCorrupcion) {
		this.mostrarFormularioCorrupcion = mostrarFormularioCorrupcion;
	}

	public boolean isMostrarFormularioCorrupcion() {
		return mostrarFormularioCorrupcion;
	}
	
	public String doFormularioCorrupcion() {
		iniciarFormularioCorrupcion();
		getExpedienteHome().setVisibleDialog("formularioCorrupcion", true);
		return "";
	}
		
}