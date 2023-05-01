package com.base100.lex100.mesaEntrada.sorteo;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.util.Strings;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Collections;

import com.base100.lex100.Lex100Exception;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.enumeration.CodigoTipoOficinaEnumeration;
import com.base100.lex100.component.enumeration.NaturalezaSubexpedienteEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionLex100Enumeration;
import com.base100.lex100.component.enumeration.TipoBolilleroEnumeration;
import com.base100.lex100.component.enumeration.TipoCambioAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoCausaEnumeration;
import com.base100.lex100.component.enumeration.TipoOficinaEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.enumeration.TurnoSorteoEnumeration;
import com.base100.lex100.controller.entity.oficinaAsignacionExp.OficinaAsignacionExpSearch;
import com.base100.lex100.controller.entity.oficinaCargaExp.OficinaCargaExpList;
import com.base100.lex100.controller.entity.registrosContables.migracion.SorteoExpedienteWeb;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Mediador;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.OficinaCargaExp;
import com.base100.lex100.entity.RecursoExp;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.SorteoLog;
import com.base100.lex100.entity.TipoBolillero;
import com.base100.lex100.manager.accion.AccionCambioAsignacion;
import com.base100.lex100.manager.accion.impl.AccionException;
import com.base100.lex100.manager.actuacionExp.ActuacionExpManager;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.manager.mediador.SorteadorMediador;
import com.base100.lex100.manager.oficina.OficinaManager;
import com.base100.lex100.mesaEntrada.ingreso.CreateExpedienteAction;
import com.base100.lex100.mesaEntrada.sorteo.listeners.IListener;
import com.base100.lex100.mesaEntrada.sorteo.listeners.ISorteoListener;

public class ColaDeSorteo {	
	private static final int DEFAULT_PESO = 1;
	private static final int DEFAULT_FACTOR_MEDIDAS_CAUTELARES = 1;
	
	private Object lock = new Object();
	private Queue<SorteoExpedienteEntry> expedientesASortear = new LinkedList<SorteoExpedienteEntry>();
	private Sorteo sorteo;
	private TipoBolillero tipoBolillero;
	private Camara camara;
	private String rubro;
	private Materia materia;
	//private Integer filtroOficinaId;
//	private Oficina oficinaSorteo;
	private String clave;
	private boolean started;
	private boolean doStop = false;
	
	private IListener sorteoLogListener;
	private ISorteoListener sorteoListener;
	
	private Log log = Logging.getLog(ColaDeSorteo.class);
	private Sorteador sorteador;
	
	public ColaDeSorteo(Sorteo sorteo, TipoBolillero tipoBolillero, Camara camara, String rubro, Materia materia, String clave) {
		this.sorteo = sorteo;
		this.tipoBolillero = tipoBolillero;
		this.camara = camara;
		this.rubro = rubro;
		this.materia = materia;
		this.clave = clave;
	}
	
	public void stop() {
		synchronized (lock){
			if(started){
				doStop = true;
			}
		}
	}
		
	public void start() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				started = true;
				synchronized (lock) {
					while (!doStop) {
//						Lifecycle.beginCall();
//						Manager.instance().initializeTemporaryConversation();
//						Conversation.instance().begin();
						while(!expedientesASortear.isEmpty()) {
							Lifecycle.beginCall();
							Manager.instance().initializeTemporaryConversation();
							Conversation.instance().begin();
							refreshPojos();
							SorteoExpedienteEntry entry = expedientesASortear.poll();
							try {
								entry = new SorteoExpedienteEntry(entry, getEntityManager());
								processEntry(entry);
							} catch (SorteoException e) {
								log.error("Error al procesar entidad en la cola de sorteo");
								e.printStackTrace();
								//entry.getExpediente().setStatusSorteo((Integer)StatusStorteoEnumeration.Type.sorteadoError.getValue());
							} finally {
								resetSorteador();
								//started = false;
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
						try {
//							resetSorteador();
//							Conversation.instance().end();
//							if(Transaction.instance().isActive()){
//								Transaction.instance().commit();
//							}
//							Lifecycle.endCall();
							lock.wait();
						} catch (Exception e) {
							log.error("Error cerrando cola de sorteo", e);
						}
					}
				}
				started = false;
			}


			private void notifySorteoStarted(SorteoExpedienteEntry entry) {
				if(sorteoListener != null){
					sorteoListener.notifySorteoStarted(getEntityManager(), entry);
				}
			}
		});
		thread.setName("Sorteador: "+this.getClave());
		thread.start();
	}

	private void notifySorteoFinished(SorteoExpedienteEntry entry, boolean result) {
		if(sorteoListener != null){
			sorteoListener.notifySorteoFinished(getEntityManager(), entry, result);
		}
	}

	protected void processEntry(SorteoExpedienteEntry entry) throws SorteoException {
		if(entry.getUsuario() != null){
			SessionState.instance().setUsername(entry.getUsuario());
		}
		if(entry.getOperation() == SorteoOperation.sorteo){
			processSorteo(entry);
		} else if(entry.getOperation() == SorteoOperation.sorteoOrdenCirculacion){
			processSorteo(entry);
		} else if(entry.getOperation() == SorteoOperation.sorteoMediador){
			processSorteoMediador(entry);
		}else if(entry.getOperation() == SorteoOperation.compensacion){
			processCompensacion(entry);
		}else if(entry.getOperation() == SorteoOperation.simulacionSorteo){
			processSimulacionSorteo(entry);
		}
		
		/** ATOS COMERCIAL */
		EntityManager entityManager = getEntityManager();
		Expediente expediente = entry.getExpediente(entityManager);
		  
		if(CamaraManager.isCamaraActualCOM() && entry.getIdCausa()!= null ) {
			SorteoExpedienteWeb.getInstance().actualizarBaseDeDatosLetrado(entry.getIdCausa(),expediente);
		}
		/** ATOS COMERCIAL */
	}
	
	private void processSimulacionSorteo(SorteoExpedienteEntry entry) throws SorteoException {
		boolean done = false;
		try {
			beginTransaction();
			bloquearSorteo();
			BolaSorteo bola = null;
			try {
				if (entry.getExcludeOficinaIds() != null) {
					bola = getSorteador().sorteo(entry);
				} else {
					bola = getSorteador().sorteo(null);
				}
			} catch (Exception e1) {
				log.error("Error configurando sorteo", e1);
			}
			if(bola == null){
				getSorteador().trace("Asignacion", "NO HAY OFICINA DISPONIBLE PARA EL SORTEO");
				/** ATOS COMERCIAL */
				SorteoExpedienteWeb.getInstance().addError(9);
				/** ATOS COMERCIAL */
			}else{
				getSorteador().trace("Asignacion", bola.getOficinaCargaExp().getOficina().getCodigo());
				sorteoFinalizado(entry, bola);
				checkSorteoMediadorIncluido(entry);
			}
			getEntityManager().flush();
			getSorteador().trace("Estado final", getSorteador().getStatus());
			//getSorteador().trace("=============", "===========================");
			//getSorteador().traceLn();
			done = true;
			
		} catch (Exception e) {
			String err = "Error cambiando asignacion";				
			log.error(err, e);
			throw new SorteoException(err, e);
		} finally {
			endTransaction(entry, done);
			afterSorteoFinalizado(entry);
		}			
	}

	protected void processCompensacion(SorteoExpedienteEntry entry) throws SorteoException {
		boolean done = false;
		try {
			beginTransaction();
			bloquearSorteo();
			
			log.info("Comienzo del proceso de compensacion");
			done = doCompensacion(entry);

		} catch (Exception e) {				
			log.error("Error cambiando compensacion", e);
			getSorteador().trace("Error", "Error cambiando compensacion");
		} finally {
			log.info("Fin del proceso de compensacion");
			endTransaction(entry, done);
			afterCompensacionFinalizada(entry);
		}			
	}

	protected boolean doCompensacion(SorteoExpedienteEntry entry) {
		boolean done = false;
		try {

			Oficina oficinaAnterior = entry.getOficinaDesasignada(getEntityManager());
			boolean isReasignacion = oficinaAnterior != null;
			Boolean compensacionAnterior = entry.getCompensacionAnterior();
	
			BolaSorteo bolaSorteo = null;
			
			getSorteador().trace(entry.getCodigoTipoCambioAsignacion(), entry.getDescripcionCambioAsignacion());
			if(tipoBolillero != null) {
				getSorteador().trace("Bolillero", tipoBolillero.getCodigo() + " - " + tipoBolillero.getDescripcion());
			}
			
			if(entry.isDescompensarAnterior() && (oficinaAnterior != null) && !Boolean.FALSE.equals(compensacionAnterior)){
				bolaSorteo = doCompensacionNegativa(entry, entry.getOficinaAsignada(getEntityManager())!= null);
			}
			getEntityManager().flush();
			if(entry.getOficinaAsignada(getEntityManager()) != null){	
				bolaSorteo = doCompensacionPositiva(null, entry, isReasignacion);
			}
			getEntityManager().flush();
			getSorteador().trace("Estado final", getSorteador().getStatus());
			done = true;
			
			if (bolaSorteo != null) {
				addSorteoLog(getEntityManager(), entry.getUsuario(), entry.getExpediente(getEntityManager()), entry.getCodigoTipoCambioAsignacion(), bolaSorteo);
			}
		} catch (Exception e) {				
			log.error("Error cambiando compensacion", e);
			getSorteador().trace("Error", "Error cambiando compensacion");
		}
		return done;
	}

	private boolean isCompensacionSorteoActiva(SorteoExpedienteEntry entry, TipoCompensacion tipoCompensacion, boolean isReasignacion) {
		boolean ret = true;
		if(entry.getCamara() != null){
				
			if(isCamaraPenalBsAs(camara)){
				return isCompensacionSorteoActivaPenal(entry, tipoCompensacion, isReasignacion);
			}else if(CamaraManager.isCamaraComercial(camara) && !TipoBolilleroEnumeration.isAsignacion(tipoBolillero)){ 
				if(TipoCompensacion.Negativa == tipoCompensacion){
					String codigo = entry.getCodigoTipoCambioAsignacion();
					return codigo != null 
						&& !TipoCambioAsignacionEnumeration.EXCUSACION_COM.equals(codigo)
						&& !TipoCambioAsignacionEnumeration.EXCUSACION_DE_REC_C_CAUSA.equals(codigo)
						&& !TipoCambioAsignacionEnumeration.RECUSACION_RCA.equals(codigo)
						&& !TipoCambioAsignacionEnumeration.RECUSACION_RCD.equals(codigo)
						&& !TipoCambioAsignacionEnumeration.RECUSACION_RCT.equals(codigo)
						&& !TipoCambioAsignacionEnumeration.RECUSACION_RSA.equals(codigo)
						&& !TipoCambioAsignacionEnumeration.RECUSACION_RSD.equals(codigo)
						&& !TipoCambioAsignacionEnumeration.RECUSACION_SALA_COM.equals(codigo)
						&& !TipoCambioAsignacionEnumeration.EXCUSACION_SALA_COM.equals(codigo);
				}
				if(TipoCompensacion.Positiva == tipoCompensacion){
					String codigo = entry.getCodigoTipoCambioAsignacion();
					return codigo != null 
					&& !TipoCambioAsignacionEnumeration.ANULACION_DE_EXCUSACION.equals(codigo)
					&& !TipoCambioAsignacionEnumeration.ANULACION_DE_REC_C_CAUSA.equals(codigo)
					&& !TipoCambioAsignacionEnumeration.ANULACION_DE_REC_S_CAUSA.equals(codigo)
					&& !TipoCambioAsignacionEnumeration.DESESTIMACION_EXCUSACION.equals(codigo)
					&& !TipoCambioAsignacionEnumeration.DESESTIMACION_EXC_SALA_COM.equals(codigo)
					&& !TipoCambioAsignacionEnumeration.DESESTIMACION_REC_SALA_COM.equals(codigo);
				}
			}else if(TipoRadicacionEnumeration.Type.sala.equals(entry.getTipoRadicacion())){ // reasignaciones  de Sala por REX/INL/REC/ etc
				if(TipoCompensacion.Negativa == tipoCompensacion){
					String codigo = entry.getCodigoTipoCambioAsignacion();
					return codigo != null 
							&& !codigo.equals(TipoCambioAsignacionEnumeration.SALA_REX)
							&& !codigo.equals(TipoCambioAsignacionEnumeration.SALA_INL)
							&& !codigo.equals(TipoCambioAsignacionEnumeration.SALA_REC)
							&& !codigo.equals(TipoCambioAsignacionEnumeration.SALA_REVOCACION_DE_CORTE_LABORAL)
							&& !codigo.equals(TipoCambioAsignacionEnumeration.SALA_NULIDAD_PROCEDIMIENTO_LABORAL)
							&& !codigo.equals(TipoCambioAsignacionEnumeration.SALA_NULIDAD_SENTENCIA_LABORAL)
							;
				}
			}
		}
		return ret;
	}

	private boolean isCompensacionSorteoActivaPenal(SorteoExpedienteEntry entry, TipoCompensacion tipoCompensacion, boolean isReasignacion) {
		boolean ret = true;
		if(CamaraManager.isCamaraPenalFederal(entry.getCamara())){
			return true;
		}else if(CamaraManager.isCamaraPenalOrdinario(entry.getCamara())){
			if(TipoRadicacionEnumeration.Type.juzgado.equals(entry.getTipoRadicacion())){
				if(tipoCompensacion == TipoCompensacion.Negativa){
					ret =  !isReasignacion; //solo si es un borrado de radicacion
				}else{
					ret = isMesaSorteo(entry.getOficinaSorteo()); // solo compensa si lo hace la mesa
				}
			}
		}else if(CamaraManager.isCamaraPenalEconomico(entry.getCamara())){
			;
		}
		return ret;
	}

	private boolean isMesaSorteo(Oficina oficina) {
		boolean ret = false;
		if (camara != null) {
			if (!getEntityManager().contains(oficina)) {
				oficina = getEntityManager().find(Oficina.class, oficina.getId());				
			}
			if(oficina.getTipoOficina() != null && oficina.getTipoOficina().getCodigo() != null){ 
				ret = oficina.getTipoOficina().getCodigo().equals(TipoOficinaEnumeration.TIPO_OFICINA_MESA_ENTRADA);
			}
		}
		return ret;
	}
		
	private boolean isCamaraPenalBsAs(Camara camara) {
		return CamaraManager.isCamaraPenalFederal(camara) ||
				CamaraManager.isCamaraPenalOrdinario(camara) ||
				CamaraManager.isCamaraPenalEconomico(camara);
	}

	public int getPesoObjetoJuicio(SorteoExpedienteEntry entry){
		int peso = DEFAULT_PESO;
		int factorMedidasCautelares = DEFAULT_FACTOR_MEDIDAS_CAUTELARES;
		Expediente expediente = entry.getExpediente(getEntityManager());
		if(expediente != null) {
			ObjetoJuicio objetoJuicio = expediente.getObjetoJuicioPrincipal();
			if(objetoJuicio != null){ 
				if(objetoJuicio.getPeso() != null){
					peso = objetoJuicio.getPeso().intValue();
					if(peso < 1){
						peso = DEFAULT_PESO;
					}
				}
				if(objetoJuicio.getFactorMedidasCautelares() != null){
					factorMedidasCautelares = objetoJuicio.getFactorMedidasCautelares().intValue();
					if(factorMedidasCautelares < 1){
						factorMedidasCautelares = DEFAULT_FACTOR_MEDIDAS_CAUTELARES;
					}
				}
				peso = peso * factorMedidasCautelares;
			}
		}
		return peso;
	}

	/** ATOS - COMERCIAL */
	private boolean isComercialMediacion(Expediente expediente) {
		return ExpedienteManager.isComercial(expediente) 
		&& (NaturalezaSubexpedienteEnumeration.Type.mediacion.getValue().equals(expediente.getNaturaleza())
				|| TipoCausaEnumeration.isProvenienteMediacion(expediente.getTipoCausa()));
	}
	/** ATOS - COMERCIAL */
	
	private void incrementaCarga(BolaSorteo bola, SorteoExpedienteEntry entry) {
		bola.mergeOficinaCargaExp(getEntityManager());
		Expediente expediente = entry.getExpediente(getEntityManager());
		
		/** ATOS - COMERCIAL */
		if(!isComercialMediacion(expediente)) {
			bola.incrementaCarga(getPesoObjetoJuicio(entry), getInfoBolillero(entry), entry.getUsuario(), expediente);
			getSorteador().trace("Compensacion +", "Expediente "+getExpedienteInfo(entry)+" -> rubro["+bola.getRubro()+"]"+" -> oficina "+bola.getOficinaCargaExp().getOficina().getCodigo()+"["+bola.getOficinaCargaExp().getCargaTrabajo()+"]");
		} else {
			
			bola.incrementaCargaFija(1, getInfoBolillero(entry), entry.getUsuario(), expediente);
			getSorteador().trace("Compensacion +", "Expediente "+getExpedienteInfo(entry)+" -> rubro["+bola.getRubro()+"]"+" -> oficina "+bola.getOficinaCargaExp().getOficina().getCodigo()+"["+bola.getOficinaCargaExp().getCargaTrabajo()+"]");
		}
		/** ATOS - COMERCIAL */

	}

	private BolaSorteo doCompensacionPositiva(BolaSorteo bolaSeleccionada, SorteoExpedienteEntry entry, boolean isReasignacion) {
		if(isCompensacionSorteoActiva(entry, TipoCompensacion.Positiva, isReasignacion)){
			Oficina oficinaAsignada = entry.getOficinaAsignada(getEntityManager());
			if(bolaSeleccionada == null){
				bolaSeleccionada = buscaBolaSorteoMinimoValor(sorteo, tipoBolillero, entry.getRubro(), oficinaAsignada);
			}
			if(bolaSeleccionada != null){
				if(anteriorIngresoTurno(entry, oficinaAsignada)) {
					getSorteador().trace("Compensacion +", "Expediente "+getExpedienteInfo(entry)+" -> oficina " + oficinaAsignada.getCodigo() + " -> No compensa por ser Oficina de ingreso de turno");
				} else if (ingresoAnteriorSistema(entry, oficinaAsignada)){
					getSorteador().trace("Compensacion +", "Expediente "+getExpedienteInfo(entry)+" -> oficina " + oficinaAsignada.getCodigo() + " -> No compensa por ser ingreso anterior al sistema");
				} else if (isOtraCamara(entry, oficinaAsignada)) {
					getSorteador().trace("Compensacion +", "Expediente "+getExpedienteInfo(entry)+" -> oficina " + oficinaAsignada.getCodigo() + " -> No compensa por ser ingreso desde otra Cámara");
				} else {
					checkCopiaBolillero(bolaSeleccionada.getOficinaCargaExp().getOficina());
					incrementaCarga(bolaSeleccionada, entry);
					if(!isSorteoDatoAuxiliar(entry)){
						OficinaAsignacionExp radicacion = OficinaAsignacionExpSearch.findByTipoRadicacion(getEntityManager(), entry.getExpediente(getEntityManager()), (String)entry.getTipoRadicacion().getValue());	
						radicacion.setCompensado(true);
						RecursoExp recursoExp = entry.getRecursoExp(getEntityManager());
						if (recursoExp != null) {
							recursoExp.setCompensado(true);
						}
					}
				}
				return bolaSeleccionada;
			}
		}
		return null;
	}

	
	
	private boolean isSorteoDatoAuxiliar(SorteoExpedienteEntry entry) {
		boolean isSorteoAuxiliar = false;
		if((entry.getOperation() == SorteoOperation.sorteoOrdenCirculacion)
			|| (entry.getOperation() == SorteoOperation.sorteoMediador)){
			isSorteoAuxiliar = true;
		}else if(isSorteoFiscalia(entry) || isSorteoDefensoria(entry)){
			isSorteoAuxiliar = true;
		}
		return isSorteoAuxiliar;
	}

	private void checkCopiaBolillero(Oficina oficina) {
		OficinaCargaExpList oficinaCargaExpList = (OficinaCargaExpList) Component.getInstance(OficinaCargaExpList.class, true);
		if (oficina != null && !oficinaCargaExpList.isBolilleroCopiado(oficina.getCamara())) {
			oficinaCargaExpList.copiaBolillero(oficina.getCamara());
		}
	}

	private BolaSorteo buscaBolaSorteoMinimoValor(Sorteo sorteoOpcional, TipoBolillero tipoBolillero, String rubro, Oficina oficinaAsignada) {
		List<BolaSorteo> bolas = MesaSorteo.instance().findBolasByOficinaId(sorteoOpcional, tipoBolillero, oficinaAsignada.getCamara(), rubro, oficinaAsignada.getId());
		if ((bolas.size() == 0) && OficinaManager.instance().isSecretariaJuzgado(oficinaAsignada) && (oficinaAsignada.getOficinaSuperior() != null)){
			bolas = MesaSorteo.instance().findBolasByOficinaId(sorteoOpcional, tipoBolillero, oficinaAsignada.getCamara(), rubro, oficinaAsignada.getOficinaSuperior().getId());
		}
		BolaSorteo bolaMinimoValor = null;
		for(BolaSorteo bola: bolas){
			if(bolaMinimoValor == null || bolaMinimoValor.getCargaTrabajo() > bola.getCargaTrabajo()){
				bolaMinimoValor = bola;
			}
		}
		return bolaMinimoValor;
	}

	
	private BolaSorteo buscaBolaSorteoMaximoValor(Sorteo sorteoOpcional, TipoBolillero tipoBolillero, String rubro,	Oficina oficinaDesasignada) {
		List<BolaSorteo> bolas = MesaSorteo.instance().findBolasByOficinaId(sorteoOpcional, tipoBolillero, oficinaDesasignada.getCamara(), rubro, oficinaDesasignada.getId());
		if ((bolas.size() == 0) && OficinaManager.instance().isSecretariaJuzgado(oficinaDesasignada) && (oficinaDesasignada.getOficinaSuperior() != null)){
			bolas = MesaSorteo.instance().findBolasByOficinaId(sorteoOpcional, tipoBolillero, oficinaDesasignada.getCamara(), rubro, oficinaDesasignada.getOficinaSuperior().getId());
		}
		BolaSorteo bolaMaximoValor = null;
		for(BolaSorteo bola: bolas){
			if(bolaMaximoValor == null || bolaMaximoValor.getCargaTrabajo() < bola.getCargaTrabajo()){
				bolaMaximoValor = bola;
			}
		}
		return bolaMaximoValor;
	}

	private BolaSorteo doCompensacionNegativa(SorteoExpedienteEntry entry, Oficina oficinaDesasignada, boolean isReasignacion) {
		if(isCompensacionSorteoActiva(entry, TipoCompensacion.Negativa, isReasignacion)){
			String rubroAnterior = entry.getRubroAnterior() != null ? entry.getRubroAnterior() : entry.getRubro();
			BolaSorteo bolaMaximoValor = buscaBolaSorteoMaximoValor(sorteo, tipoBolillero, rubroAnterior,	oficinaDesasignada);
			if(bolaMaximoValor == null){ // si no lo encuentro en el sorteo Actual lo busco en todo el bolillero
				bolaMaximoValor = buscaBolaSorteoMaximoValor(null, tipoBolillero, rubroAnterior,	oficinaDesasignada);
				if(bolaMaximoValor != null){
					bloqueaSorteoDescompensacion(bolaMaximoValor);
				}
			}
			checkCopiaBolillero(oficinaDesasignada);
			if(bolaMaximoValor != null){
				bolaMaximoValor.mergeOficinaCargaExp(getEntityManager());
				
				if(anteriorIngresoTurno(entry, oficinaDesasignada)) {
					getSorteador().trace("Compensacion -", "Expediente "+getExpedienteInfo(entry)+" -> oficina " + oficinaDesasignada.getCodigo() + " -> No descompensa por ser Oficina de ingreso de turno");
					return null;
				} else if (ingresoAnteriorSistema(entry, oficinaDesasignada)) {
					getSorteador().trace("Compensacion -", "Expediente "+getExpedienteInfo(entry)+" -> oficina " + oficinaDesasignada.getCodigo() + " -> No descompensa por ser ingreso anterior al sistema");
					return null;
				} else if (isOtraCamara(entry, oficinaDesasignada)) {
					getSorteador().trace("Compensacion -", "Expediente "+getExpedienteInfo(entry)+" -> oficina " + oficinaDesasignada.getCodigo() + " -> No descompensa por ser Oficina de otra Cámara");
					return null;
				} else {			
					if(bolaMaximoValor.decrementaCarga(getPesoObjetoJuicio(entry), getInfoBolillero(entry), entry.getUsuario(), entry.getExpediente(getEntityManager()))){
						getSorteador().trace("Compensacion -", "Expediente "+getExpedienteInfo(entry)+" -> rubro["+bolaMaximoValor.getRubro()+"]"+" -> oficina "+bolaMaximoValor.getOficinaCargaExp().getOficina().getCodigo() +"["+bolaMaximoValor.getOficinaCargaExp().getCargaTrabajo()+"]");
					}
				}
				return bolaMaximoValor;
			}
		}
		return null;
	}


	private void bloqueaSorteoDescompensacion(BolaSorteo bolaSorteo) {
		if(bolaSorteo != null && bolaSorteo.getOficinaCargaExp() != null && bolaSorteo.getOficinaCargaExp().getSorteo() != null){
			bloquearSorteo(bolaSorteo.getOficinaCargaExp().getSorteo());
		}
	}


	private String getInfoBolillero(SorteoExpedienteEntry entry) {
		return getInfoBolillero(getExpedienteInfo(entry), entry.getCodigoTipoCambioAsignacion(), entry.getDescripcionCambioAsignacion());
	}
	
	public static String getInfoBolillero(String expedienteInfo, String codigoTipoCambioAsignacion, String descripcionTipoCambioAsignacion) {
		StringBuffer info = new StringBuffer(); 
		info.append(expedienteInfo);
		if (codigoTipoCambioAsignacion != null) {
			info.append(" [").append(codigoTipoCambioAsignacion).append("]");
		}
		if (descripcionTipoCambioAsignacion != null) {
			info.append(" ").append(descripcionTipoCambioAsignacion);
		}
		return info.toString();
	}
	
	private boolean anteriorIngresoTurno(SorteoExpedienteEntry entry, Oficina oficina) {
		boolean ret = false;
		ExpedienteManager expedienteManager = ExpedienteManager.instance();
		if(oficina != null){
			if(TipoRadicacionEnumeration.Type.juzgado.equals(entry.getTipoRadicacion())){
				ret = expedienteManager.hasIngresoJuzgadoTurno(getEntityManager(), entry.getExpediente(getEntityManager()), oficina);
			}
		}			
		return ret;
	}

	private boolean ingresoAnteriorSistema(SorteoExpedienteEntry entry, Oficina oficina) {
		boolean ret = false;
		ExpedienteManager expedienteManager = ExpedienteManager.instance();
		if(oficina != null){
			ret = expedienteManager.hasIngresoAnteriorSistema(getEntityManager(), entry.getExpediente(getEntityManager()), entry.getTipoRadicacion(), oficina);
		}			
		return ret;
	}

	private boolean isOtraCamara(SorteoExpedienteEntry entry, Oficina oficina) {
		if ((entry.getCamara() != null) && (oficina != null)) {
			return !entry.getCamara().equals(oficina.getCamara());
		}
		return false;
	}

	private BolaSorteo doCompensacionNegativa(SorteoExpedienteEntry entry, boolean isReasignacion) {
		return doCompensacionNegativa(entry,  entry.getOficinaDesasignada(getEntityManager()), isReasignacion);
	}
	
	protected void processSorteoMediador(SorteoExpedienteEntry entry) throws SorteoException {
		//resetSorteador();		
		boolean done = false;

		
		try {
			beginTransaction();
			if (sorteo == null) {
				String err = "No existe una configuración de Sorteo para el expediente " + getExpedienteInfo(entry);
				log.error(err);
				throw new SorteoException(err);
			}
			bloquearSorteo();

			if(procesaSorteoMediador(entry)){
				done = true;
			}
			getEntityManager().flush();
			getSorteador().trace("Estado final", getSorteador().getStatus());
			getSorteador().traceLn();
			
		} catch (Exception e) {
			String err = "Error en Sorteo Mediador";				
			log.error(err, e);
			getSorteador().trace("Error", "Error en sorteo Mediador");
			throw new SorteoException(err, e);
		} finally {
			endTransaction(entry, done);
		}			
	}

	

	protected void processSorteo(SorteoExpedienteEntry entry) throws SorteoException {
		//resetSorteador();		
		boolean done = false;

		
		try {
			beginTransaction();
			if (sorteo == null) {
				String err = "No existe una configuración de Sorteo para el expediente " + getExpedienteInfo(entry);
				log.error(err);
				throw new SorteoException(err);
			}
// PRUEBA PARA QUE EL SORTEO DE ORDEN CIRCULACION BLOQUEE EL SORTEO/COMPENSACION DE LA SALA
//			if(entry.getSorteoDependiente() != null) {
//				bloquearSorteo(entry.getSorteoDependiente());
//			}
			bloquearSorteo();

			if(entry.isBuscaConexidad()) {
				done = procesaBusquedaConexidad(entry);
			}
			if(!done) {
				done = procesaSorteoAleatorio(entry);
			}
			
		} catch (Exception e) {
			String err = "Error en Sorteo";				
			log.error(err, e);
			getSorteador().trace("Error", "Error en sorteo");
			throw new SorteoException(err, e);
		} finally {
			log.info("Finalizando el sorteo");
			endTransaction(entry, done);
			afterSorteoFinalizado(entry);
		}			
	}

	private boolean procesaBusquedaConexidad(SorteoExpedienteEntry entry) throws SorteoException {
		Expediente expediente = entry.getExpediente(getEntityManager());
		log.info("Comienzo de proceso de busqueda de conexidad para expediente con ID: " + expediente.getId() + " y clave: " + expediente.getClave());
		String codigoTipoCambioAsignacion = TipoCambioAsignacionEnumeration.CONEXIDAD_DETECTADA;
		String descripcionCambioAsignacion = CreateExpedienteAction.getDescripcionCambioAsignacion(getEntityManager(), codigoTipoCambioAsignacion, entry.getTipoRadicacion(), expediente.getMateria()); 
		boolean asignado = false;
		try {
			ExpedienteManager expedienteManager =  ExpedienteManager.instance();			
			OficinaAsignacionExp radicacionAnterior = expedienteManager.findRadicacionExpediente(expediente, entry.getTipoRadicacion());
			Oficina oficinaAnterior = (radicacionAnterior != null)? radicacionAnterior.getOficinaConSecretaria(): null;	
			Boolean compensacionAnterior = (radicacionAnterior != null)? radicacionAnterior.getCompensado(): null; 

			SessionState.instance().setGlobalCamara(entry.getCamara());
			asignado = CreateExpedienteAction.buscaConexidadPorCriterios(getEntityManager(), 
							expediente, 
							entry.getRecursoExp(),
							entry.getTipoRadicacion(), 
							codigoTipoCambioAsignacion, 
							descripcionCambioAsignacion,
							entry.getTipoGiro(),
							false,
							false,
							sorteo,
							entry.getRubro(),
							entry.getSiguienteSorteoEncadenado(),
							entry.isAsignaPorConexidad(),
							entry.getOficinaSorteo(),
							entry.isAsignaPorConexidad(),
							entry.isIgnoreCheckDobleInicio(),
							entry.getFojasPase(), 
							null,
							null, 
							null);

			if (asignado) {
				boolean compensar = CreateExpedienteAction.puedeCompensarPorConexidadAutomatica(expediente, entry.getTipoRadicacion());
				
				if(compensar) {
					entry.setOficinaDesasignada(oficinaAnterior);
					entry.setCompensacionAnterior(compensacionAnterior);
					OficinaAsignacionExp radicacion = expedienteManager.findRadicacionExpediente(expediente, entry.getTipoRadicacion());
					entry.setOficinaAsignada((radicacion != null)? radicacion.getOficinaConSecretaria(): null);	
					entry.setCodigoTipoCambioAsignacion(codigoTipoCambioAsignacion);
					entry.setDescripcionCambioAsignacion(descripcionCambioAsignacion);
					doCompensacion(entry);
				}
				if (entry.isSorteaMediador()){
					OficinaAsignacionExp radicacion = ExpedienteManager.instance().findRadicacionExpediente(expediente, entry.getTipoRadicacion());
					if ((radicacion != null) && radicacion.getMediador() == null) { 
						checkSorteoMediadorIncluido(entry);
					}
				}
				
			}
		} catch (Lex100Exception e) {
			log.error("Error buscando conexidad para expediente con id: " + ((expediente!=null) ? expediente.getId() : "ID desconocido"));
			e.printStackTrace();
		}

		
		return asignado;
	}

	private boolean procesaSorteoAleatorio(SorteoExpedienteEntry entry) throws SorteoException {
		Expediente expediente = entry.getExpediente(getEntityManager());
		log.info("Comienzo de proceso de sorteo aleatoreo para el expediente con ID " + expediente.getId() + " y clave: " + expediente.getClave());
		boolean ok = false;
		
		Integer filtroOficinaId = null;
		if(entry.getOperation() == SorteoOperation.sorteoOrdenCirculacion && entry.getOficinaAsignadaId() != null ){
			filtroOficinaId = entry.getOficinaAsignadaId();
		}		
		if(TurnoSorteoEnumeration.Type.conTurnoAutomatico.getValue().equals(sorteo.getConTurno())) {
			createSorteador(null, filtroOficinaId);
		} else if(TurnoSorteoEnumeration.Type.conTurnoContinuo.getValue().equals(sorteo.getConTurno())) {
			createSorteador(entry.getFechaTurnosActivos(), filtroOficinaId);
		}else if(filtroOficinaId != null){
			createSorteador(null, filtroOficinaId);
		}
		BolaSorteo bola = null;
		try {
			getSorteador().trace(entry.getCodigoTipoCambioAsignacion(), entry.getDescripcionCambioAsignacion());
			if(tipoBolillero != null) {
				getSorteador().trace("Bolillero", tipoBolillero.getCodigo() + " - " + tipoBolillero.getDescripcion());
			}
			bola = getSorteador().sorteo(entry);
		} catch (Exception e1) {
			log.error("Error configurando sorteo", e1);
		}

		if(bola == null){
			String err = "ERROR: No hay suficientes oficinas disponibles para el sorteo.";				
			getSorteador().trace("Asignacion", "Expediente "+getExpedienteInfo(entry)+" -> "+err);
			log.error(err);
			throw new SorteoException(err);				
			//entry.getExpediente().setStatusSorteo((Integer)StatusStorteoEnumeration.Type.sorteadoError.getValue());			
		}else{
			getSorteador().trace("Asignacion", "Expediente "+getExpedienteInfo(entry)+" -> oficina "+bola.getOficinaCargaExp().getOficina().getCodigo());
			sorteoFinalizado(entry, bola);
			checkSorteoMediadorIncluido(entry);
			ok = true;
		}
		getEntityManager().flush();
		getSorteador().trace("Estado final", getSorteador().getStatus());
		//getSorteador().trace("=============", "===========================");
		getSorteador().traceLn();
		
		return ok;
	}

	private boolean procesaSorteoMediador(SorteoExpedienteEntry entry) throws SorteoException {
		boolean ok = false;
		getSorteador().trace(entry.getCodigoTipoCambioAsignacion(), entry.getDescripcionCambioAsignacion());
		EntityManager entityManager = getEntityManager();
		Expediente expediente = entry.getExpediente(entityManager);
		Mediador excluirMediador = null;
		if ((entry.getExcludeOficinaIds() != null) && entry.getExcludeOficinaIds().size() > 0) {
			excluirMediador = getEntityManager().find(Mediador.class, entry.getExcludeOficinaIds().get(0));
		}
		String info = getExpedienteInfo(entry);
		Mediador mediador = SorteadorMediador.sorteaMediador(getEntityManager(), camara, sorteo.getCompetencia(), excluirMediador, expediente, entry.getUsuario(), info);
		if(mediador == null){
			String err = "ERROR: No hay mediadores disponibles para el sorteo.";				
			getSorteador().trace("Asignacion Mediador", "Expediente "+getExpedienteInfo(entry)+" -> "+err);
			log.error(err);
			throw new SorteoException(err);				
		}else{
			cambiaMediador(entry, expediente, mediador, info);
			ok = true;
		}
		return ok;
	}


	private void sorteoFinalizado(SorteoExpedienteEntry entry, BolaSorteo bola) throws SorteoException {
 		EntityManager entityManager = getEntityManager();
		Expediente expediente = entry.getExpediente(entityManager);
		RecursoExp recursoExp = entry.getRecursoExp(entityManager);
		Oficina oficina = null;
		oficina = entityManager.find(Oficina.class, bola.getIdOficina());
		
		if(isSorteoFiscalia(entry)){
			cambiaFiscalia(entry, bola, expediente, oficina);
		}else if(isSorteoDefensoria(entry)){
			cambiaDefensoria(entry, bola, expediente, oficina);
		}else{
			cambiaAsignacion(entry, bola, expediente, recursoExp, oficina);
		}
	}

	private void afterCompensacionFinalizada(SorteoExpedienteEntry entry) throws SorteoException {
		if (entry.isVerificarAsignarOrdenCirculacion()) {
			verificarAsignarOrdenCirculacion(entry);
		}
	}

	private void afterSorteoFinalizado(SorteoExpedienteEntry entry) throws SorteoException {
		verificarAsignarOrdenCirculacion(entry);
	}
	
	private void verificarAsignarOrdenCirculacion(SorteoExpedienteEntry entry) throws SorteoException {
		if(TipoRadicacionEnumeration.isRadicacionSala(entry.getTipoRadicacion()) && !isSorteoDatoAuxiliar(entry)) {
			OficinaAsignacionExp oficinaAsignacionExp = entry.getExpediente(getEntityManager()).getRadicacionSala();			
			try {
				if (oficinaAsignacionExp != null) {
					SessionState.instance().setGlobalCamara(entry.getCamara());
					ExpedienteManager.instance().verificarAsignarOrdenCirculacion(null, entry.getOficinaSorteo(), entry.getUsuario(), entry.getTipoRadicacion(), rubro, oficinaAsignacionExp);
				}
			} catch (Lex100Exception e) {
				e.printStackTrace();
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
	

	private void checkSorteoMediadorIncluido(SorteoExpedienteEntry entry) throws SorteoException {
		if(entry.isSorteaMediador()){
			try {
				procesaSorteoMediador(entry);
			} catch (Exception e) {

			}
		}
	}
	

		
	private boolean isSorteoFiscalia(SorteoExpedienteEntry entry) {
		return CodigoTipoOficinaEnumeration.isFiscalia(entry.getCodigoTipoOficina());
	}

	private boolean isSorteoDefensoria(SorteoExpedienteEntry entry) {
		return CodigoTipoOficinaEnumeration.isDefensoria(entry.getCodigoTipoOficina());
	}

	
	private void bloquearSorteo(Sorteo param) {
		Sorteo sorteoAux = param;
		if (!getEntityManager().contains(sorteoAux)) {
			sorteoAux = getEntityManager().find(Sorteo.class, sorteoAux.getId());
		}
		Session session = (Session) getEntityManager().getDelegate();
		session.lock(sorteoAux, LockMode.UPGRADE);		
	}
	
	private void bloquearSorteo() {
		bloquearSorteo(this.sorteo);
	}
	
	public void beginTransaction() throws SystemException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			NotSupportedException {
		if(Transaction.instance().isActive()){
			Transaction.instance().commit();
		}
		Transaction.instance().begin();
		getEntityManager().joinTransaction();
	}


	private void addSorteoLog(EntityManager entityManager, String usuario, Expediente expediente, String codigoTipoCambioAsignacion, BolaSorteo bola) { 
		
		Oficina oficina = (bola != null) ? bola.getOficinaCargaExp().getOficina(): null;
		Integer carga = (bola != null) ? bola.getOficinaCargaExp().getCargaTrabajo(): null;
		
		SorteoLog sorteoLog = new SorteoLog();
		sorteoLog.setUsuario(usuario);
		sorteoLog.setExpediente(expediente);
		sorteoLog.setOficina(oficina);
		sorteoLog.setCodigoSorteo(sorteo != null ? sorteo.getCodigo() : null);
		sorteoLog.setFechaSorteo(new Date());
		sorteoLog.setRangoMinimos(sorteo != null ? sorteo.getRangoMinimos() : null);
		if (bola != null) {
			sorteoLog.setRubro(bola.getRubro());
		}
		sorteoLog.setCargaTrabajo(carga);
		sorteoLog.setCodigoTipoCambioAsignacion(codigoTipoCambioAsignacion);
		sorteoLog.setInformacionSorteo(getSorteador().getInformacionSorteo());
		
		entityManager.persist(sorteoLog);
		entityManager.flush();
	}

	private String getExpedienteInfo(SorteoExpedienteEntry entry){
		Expediente expediente = entry.getExpediente(getEntityManager());
		return expediente != null ? expediente.getIdxAnaliticoFirst() : "";
	}

	private boolean endTransaction(SorteoExpedienteEntry entry, boolean done) {
		boolean finished = false;
		try {
			if(!done){				
				Transaction.instance().rollback();
				Transaction.instance().begin();
				getEntityManager().joinTransaction();
				addSorteoLog(getEntityManager(), entry.getUsuario(), entry.getExpediente(getEntityManager()), entry.getCodigoTipoCambioAsignacion(), null);
			}
			Transaction.instance().commit();
			Transaction.instance().begin();
			getEntityManager().joinTransaction();

			notifySorteoFinished(entry, done);
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
	
	
	private void cambiaOrdenCirculacion(SorteoExpedienteEntry entry, BolaSorteo bola, Expediente expediente, Oficina oficina) throws SorteoException {
		String codigoOrden = extractOrdenCirculacion(bola);

		EntityManager entityManager = getEntityManager();

		if (expediente != null) {
			log.info("Asignacion de expediente: "+ getClaveExpediente(entry.getExpediente(entityManager))+" -> a oficina "+bola.getOficinaCargaExp().getOficina().getCodigo());
			
			ExpedienteManager expedienteManager =  ExpedienteManager.instance();			

			OficinaAsignacionExp radicacion= expedienteManager.findRadicacionExpediente(expediente, entry.getTipoRadicacion());
			expedienteManager.asignarOrdenCirculacion(radicacion, codigoOrden);
			log.info("Asignacion de orden de circulacion: " + codigoOrden);
		}
		entityManager.flush();
	}
	
	private String extractOrdenCirculacion(BolaSorteo bola) {
		String rubro = bola.getRubro();
		int idx = rubro.indexOf("#");
		String ret = null;
		if(idx >=0){
			ret = rubro.substring(idx+1);
		}
		return ret;
	}

	private void cambiaFiscalia(SorteoExpedienteEntry entry, BolaSorteo bola, Expediente expediente, Oficina oficina) throws SorteoException {
		if(entry.getTipoRadicacion() != null && !Strings.isEmpty(oficina.getNumero())){
			ExpedienteManager expedienteManager =  ExpedienteManager.instance();
			EntityManager entityManager = getEntityManager();
			try {
				Oficina oficinaAnterior = expedienteManager.getFiscaliaRadicacionExpediente(expediente, entry.getTipoRadicacion());

				expedienteManager.cambiarFiscalia(expediente, entry.getTipoRadicacion(), Integer.parseInt(oficina.getNumero()));
				
				boolean isReasignacion = oficinaAnterior != null;
				// compensacion
				if(oficinaAnterior != null){
					doCompensacionNegativa(entry, oficinaAnterior, isReasignacion);	
				}
				entityManager.flush();
				if(bola != null){
					doCompensacionPositiva(bola, entry, isReasignacion);
				}
				entityManager.flush();
				
				getSorteador().trace("Estado final", getSorteador().getStatus());
				addSorteoLog(entityManager, entry.getUsuario(), expediente, entry.getCodigoTipoCambioAsignacion(), bola);
				
				/** ATOS COMERCIAL */
				SorteoExpedienteWeb.getInstance().noErrorDetectado();
				/** ATOS COMERCIAL */

			} catch (NumberFormatException e) {
			}
		}		
	}

	private void cambiaDefensoria(SorteoExpedienteEntry entry, BolaSorteo bola, Expediente expediente, Oficina oficina) throws SorteoException {
		if(entry.getTipoRadicacion() != null && !Strings.isEmpty(oficina.getNumero())){
			ExpedienteManager expedienteManager =  ExpedienteManager.instance();
			EntityManager entityManager = getEntityManager();
			try {
				Oficina oficinaAnterior = expedienteManager.getDefensoriaRadicacionExpediente(expediente, entry.getTipoRadicacion());

				expedienteManager.cambiarDefensoria(expediente, entry.getTipoRadicacion(), Integer.parseInt(oficina.getNumero()));
				
				boolean isReasignacion = oficinaAnterior != null;
				// compensacion
				if(oficinaAnterior != null){
					doCompensacionNegativa(entry, oficinaAnterior, isReasignacion);	
				}
				entityManager.flush();
				if(bola != null){
					doCompensacionPositiva(bola, entry, isReasignacion);
				}
				entityManager.flush();
				
				getSorteador().trace("Estado final", getSorteador().getStatus());
				addSorteoLog(entityManager, entry.getUsuario(), expediente, entry.getCodigoTipoCambioAsignacion(), bola);
				
				/** ATOS COMERCIAL */
				SorteoExpedienteWeb.getInstance().noErrorDetectado();
				/** ATOS COMERCIAL */

			} catch (NumberFormatException e) {
			}
		}		
	}
	private void cambiaMediador(SorteoExpedienteEntry entry, Expediente expediente, Mediador mediador, String info) throws SorteoException {
		if(entry.getTipoRadicacion() != null){
			ExpedienteManager expedienteManager =  ExpedienteManager.instance();
			try {
				expedienteManager.cambiarMediador(expediente, entry.getOficinaSorteo(), entry.getTipoRadicacion(), mediador, info);
			} catch (NumberFormatException e) {
			}
		}		
	}
	private void cambiaAsignacion(SorteoExpedienteEntry entry, BolaSorteo bola, Expediente expediente, RecursoExp recursoExp, Oficina oficina) throws SorteoException {		
		EntityManager entityManager = getEntityManager();
		Oficina oficinaAnterior = null;
		Boolean compensacionAnterior = null;

		if (expediente != null) {
			log.info("Asignacion de expediente: "+ getClaveExpediente(entry.getExpediente(entityManager))+" -> a oficina "+bola.getOficinaCargaExp().getOficina().getCodigo());
			
			ExpedienteManager expedienteManager =  ExpedienteManager.instance();			

			if (entry.getExcludeOficinaIds() != null
					&& entry.getExcludeOficinaIds().size() > 0) {
				updateComentarios(entry);
			}

			// ATOS COMERCIAL 13/05/14
			//Si es comercial y es sorteo de un vocal, debo agregar en la caratula la conformacion de los vocales.
			if(CamaraManager.instance().isCamaraCOM(expediente.getCamaraActual()) && "VOC".equals(entry.getRubro()))
				updateConformacionVocales(entry, bola, expediente);
			// ********
			
			if(entry.getOperation() == SorteoOperation.sorteoOrdenCirculacion){
				cambiaOrdenCirculacion(entry, bola, expediente, oficina);
			}else{
				OficinaAsignacionExp radicacionAnterior = expedienteManager.findRadicacionExpediente(expediente, entry.getTipoRadicacion());
				oficinaAnterior = (radicacionAnterior != null)? radicacionAnterior.getOficinaConSecretaria(): null;	
				compensacionAnterior = (radicacionAnterior != null)? radicacionAnterior.getCompensado(): null; 
				
				cambiaAsignacionOficina(entry, expediente, recursoExp, oficina);
			}
			
		}
		
		bola.setOficinaCargaExp(entityManager.find(OficinaCargaExp.class, bola.getOficinaCargaExp().getId()));

		if (TipoRadicacionEnumeration.isRadicacionVocalias(entry.getTipoRadicacion()) 
				|| TipoRadicacionEnumeration.isRadicacionVocaliasCasacion(entry.getTipoRadicacion())
				|| entry.getOperation() == SorteoOperation.sorteoOrdenCirculacion) {
			compensacionAnterior = false;
		}
		
		boolean isReasignacion = oficinaAnterior != null;
		// compensacion
		if((oficinaAnterior != null) && !Boolean.FALSE.equals(compensacionAnterior)) {
			doCompensacionNegativa(entry, oficinaAnterior, isReasignacion);	
		}
		entityManager.flush();
		if(bola != null){
			doCompensacionPositiva(bola, entry, isReasignacion);
		}
		entityManager.flush();
		log.info("Expediente "+
				(expediente != null ? expediente.getNumero() :  "")+"/"+(expediente != null ? expediente.getAnio() : "")+
				" rubro "+bola.getOficinaCargaExp().getRubro()+
				" -> Asignado a la oficina id:"+bola.getOficinaCargaExp().getOficina().getId()+" = "+bola.getOficina()+ 
				" nueva carga: "+bola.getOficinaCargaExp().getCargaTrabajo());
		
		getSorteador().trace("Estado final", getSorteador().getStatus());
		addSorteoLog(entityManager, entry.getUsuario(), expediente, entry.getCodigoTipoCambioAsignacion(), bola);
		
		/** ATOS COMERCIAL */
		SorteoExpedienteWeb.getInstance().noErrorDetectado();
		/** ATOS COMERCIAL */
	}

	private void cambiaAsignacionOficina(SorteoExpedienteEntry entry, Expediente expediente, RecursoExp recursoExp, Oficina oficina)
			throws SorteoException {
		TipoAsignacionEnumeration.Type tipoAsignacionSorteo = TipoAsignacionEnumeration.getTipoSorteo(entry.getTipoRadicacion());
		try {
			Date FECHA_ASIGNACION_EN_CURSO = null;
			
			boolean integracionTotal = false;
			if (TipoRadicacionEnumeration.isRadicacionAnyVocalias(entry.getTipoRadicacion())) {
				if (TipoRadicacionEnumeration.isRadicacionVocalias(entry.getTipoRadicacion())) {
					expediente.setRadicacionesVocalia(null);
					integracionTotal = expediente.getRadicacionesVocalia().isEmpty();
				} else if (TipoRadicacionEnumeration.isRadicacionVocaliasCasacion(entry.getTipoRadicacion())) {
					expediente.setRadicacionesVocaliaCasacion(null);
					integracionTotal = expediente.getRadicacionesVocaliaCasacion().isEmpty();
				}
				
			} 
			List<Oficina> oficinasAsignacion;
			if (integracionTotal) {
				oficinasAsignacion = OficinaManager.instance().getVocalias(CamaraManager.getOficinaPrincipal(oficina));
			} else {
				oficinasAsignacion = new ArrayList<Oficina>();
				oficinasAsignacion.add(oficina);
			}
			for (Oficina oficinaAsignacion: oficinasAsignacion)  {
				SessionState.instance().setGlobalCamara(entry.getCamara());
				if(entry.getUsuario() != null){
					SessionState.instance().setUsername(entry.getUsuario());
					SessionState.instance().setRemisionesPorUsuario(entry.isRemisionPorUsuario());
				}
				new AccionCambioAsignacion().doAccion(
						entry.getUsuario(),
						expediente,
						recursoExp,
						oficinaAsignacion,
						entry.getTipoRadicacion(),
						tipoAsignacionSorteo,
						TipoAsignacionLex100Enumeration.Type.sorteo,
						entry.getOficinaSorteo(),
						entry.getTipoGiro(),
						false, // (optimizacion) la compensacion se hace en esta funcion mas abajo
						getSorteo(),
						getRubro(),
						entry.getCodigoTipoCambioAsignacion(),
						entry.getDescripcionCambioAsignacion(),
						entry.getComentarios(),
						FECHA_ASIGNACION_EN_CURSO,
						entry.getFojasPase()
						);
			}
		} catch (AccionException e) {
			String err = "Error cambiando asignacion";
			log.error(err, e);
			throw new SorteoException(err, e);
		}
	}

	// ATOS COMERCIAL 13/05/14
	private void updateConformacionVocales(SorteoExpedienteEntry entry,
			BolaSorteo bola, Expediente expediente) {
		String oficinaActual = expediente.getOficinaActual().getCodigo();
		String vocales="*["+oficinaActual.substring(oficinaActual.indexOf("_")+2)+": ";
		for (Oficina vocalia: expediente.getVocaliasSala()) {
			String codigo = vocalia.getCodigo().trim();
			vocales+=getVocalCodOficina(codigo)+" - ";
		}
		vocales+=getVocalCodOficina(bola.getOficina())+"]*";
		entry.setComentarios(entry.getComentarios()+vocales);
	}

	private String getVocalCodOficina(String codigo) {
		return codigo.substring(codigo.lastIndexOf("_")+1);
	}

	// *************************************
	private void updateComentarios(SorteoExpedienteEntry entry) {
		StringBuilder builder = new StringBuilder(entry.getComentarios() != null ? entry.getComentarios() : "");
		if (entry.getExcludeOficinaIds() != null) {
			String result = "";
			List<Integer> excluded = new ArrayList<Integer>(entry.getExcludeOficinaIds());
			Collections.sort(excluded);
			for (Integer excludedId: excluded) {
				Oficina excludedOficina = getEntityManager().find(Oficina.class, excludedId);
				if (!Strings.isEmpty(result)) {
					result += ", ";
				} 
				if (!excludedOficina.getCamara().equals(entry.getCamara())) {
					result += excludedOficina.getCamara().getAbreviatura()+" ";
				}
				result += excludedOficina.getCodigoAbreviado();
			}
			if (!Strings.isEmpty(result)) {
				result = "Excluyendo del sorteo: "+result+" - ";
				builder.insert(0, result);
			}
		}
		entry.setComentarios(builder.toString());
	}

	private String getClaveExpediente(Expediente expediente) {
		 return expediente.getNumero() + "/" + expediente.getAnio();
	}

	public void addEntry(SorteoExpedienteEntry entry) {
		synchronized (lock) {
			expedientesASortear.add(entry);
			if(!started){
				start();
			}else{
				lock.notify();
			}
		}
	}
	
	public void addSyncEntry(SorteoExpedienteEntry entry) {
		addEntry(entry);
		entry.join();
	}

	public boolean isEmpty(){
		return expedientesASortear.isEmpty(); 
	}

	private void refreshPojos() {
		EntityManager entityManager = getEntityManager();
		if(this.sorteo != null){
			this.sorteo = entityManager.find(Sorteo.class, this.sorteo.getId());
		}
		if(this.camara != null){
			this.camara = entityManager.find(Camara.class, this.camara.getId());
		}
		if(this.materia != null){
			this.materia = entityManager.find(Materia.class, this.materia.getId());
		}
	}

	private Sorteador getSorteador() {
		if (sorteador == null) {
			sorteador = createSorteador(null, null);
		}
		return sorteador;
	}
	
	private Sorteador createSorteador(Date fechaTurnosActivos, Integer filtroOficinaId) {

		String rubroColaSorteo = rubro;
		if(filtroOficinaId != null && rubro.indexOf("#") > 0){
			rubroColaSorteo = rubro.substring(0, rubro.indexOf("#"));
		}
		
		sorteador = MesaSorteo.instance().createSorteador(sorteo, tipoBolillero, fechaTurnosActivos, camara, materia, rubroColaSorteo, filtroOficinaId);
		if (sorteoLogListener != null) {
			sorteador.addListener(sorteoLogListener);
		}
		return sorteador;
	}

	private void resetSorteador() {
		sorteador = null;
	}

	public ISorteoListener getSorteoListener() {
		return sorteoListener;
	}

	public void setSorteoListener(ISorteoListener sorteoListener) {
		this.sorteoListener = sorteoListener;
	}

	public Sorteo getSorteo() {
		return sorteo;
	}

	public void setSorteo(Sorteo sorteo) {
		this.sorteo = sorteo;
	}

	public String getRubro() {
		return rubro;
	}
	
	public EntityManager getEntityManager() {
		return (EntityManager) Component.getInstance("entityManager");
	}

	public ActuacionExpManager getActuacionExpManager() {
		return (ActuacionExpManager) Component.getInstance(ActuacionExpManager.class, true);
	}
	
	public IListener getSorteoLogListener() {
		return sorteoLogListener;
	}

	public void setSorteoLogListener(IListener sorteoLogListener) {
		this.sorteoLogListener = sorteoLogListener;
	}

	public String getClave() {
		return clave;
	}
	
	private enum TipoCompensacion{
		Positiva,
		Negativa
	}		
	
	
}

