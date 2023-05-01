package com.base100.lex100.mesaEntrada.sorteo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.Controller;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.UserTransaction;

import com.base100.lex100.component.enumeration.StatusStorteoEnumeration;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.mesaEntrada.sorteo.listeners.ISorteoListener;

@Name("sorteoManager")
@Scope(ScopeType.APPLICATION)
@Startup
public class SorteoManager extends Controller implements ISorteoListener {
	private static final long serialVersionUID = 5727520117879844393L;
	
	private List<SorteoExpedienteEntry> taskList = new ArrayList<SorteoExpedienteEntry>();
	private List<StatusMessage> internalMessages = new ArrayList<StatusMessage>();
	
	@In(create=true)
	private SorteadorFactory sorteadorFactory;
	
	public List<SorteoExpedienteEntry> getTaskList() {
		return new ArrayList<SorteoExpedienteEntry>(taskList);
	}
	
	public synchronized void updateMessages() {
		for (StatusMessage message : internalMessages) {
			getStatusMessages().add(message.getSeverity(), message.getDetail());
		}
		internalMessages.clear();
	}
	
	public void runTransaction(SorteoParametros sorteoParametros) {
		// commit transaction
		 UserTransaction transaction = Transaction.instance();
		 try{
		     if (transaction.isActive()) {
		    	 transaction.commit();
		     }
		     transaction.begin();
		     getEntityManager().joinTransaction();
		    //
			addExpedienteEntry(sorteoParametros, true);		
		 } catch (Exception e) {
				throw new RuntimeException(e);
		}
	}

	@Transactional
	public String addExpedienteEntry(SorteoParametros sorteoParametros, boolean async) {		
		ColaDeSorteo colaDeSorteo = getColaDeSorteo(sorteoParametros);		
		if (colaDeSorteo != null) {
			setStatusSorteoExpediente(getEntityManager(), sorteoParametros.getExpediente(), (Integer)StatusStorteoEnumeration.Type.sorteando.getValue());
			if (async) {
				colaDeSorteo.addEntry(new SorteoExpedienteEntry(sorteoParametros));
			} else {   
				colaDeSorteo.addSyncEntry(new SorteoExpedienteEntry(sorteoParametros));
			}
			return colaDeSorteo.getRubro();
		}
		return null;
	}

	public static void setStatusSorteoExpediente(EntityManager em, Expediente expediente, Integer statusSorteo) {
		if(expediente != null){
			em.joinTransaction();
		    Expediente expedienteUpdated = em.find(Expediente.class, expediente.getId());
			int retries = 5;
		    while(expedienteUpdated == null){ // Se contempla la situacion en la que no ha habido tiempo para que el otro thread actualice en la B.D. y este pued acceder al registro
		    	try {
					retries--;
					Thread.sleep(1000);
					expedienteUpdated = em.find(Expediente.class, expediente.getId());
				} catch (InterruptedException e) {
					break;
				}
		    }
		    if(expedienteUpdated != null){
		    	expedienteUpdated.setStatusSorteo(statusSorteo);
		    	if(!em.contains(expedienteUpdated))
		    		em.find(Expediente.class, expedienteUpdated.getId());
		    	em.flush();
		    }
		}
	}

	private ColaDeSorteo getColaDeSorteo(SorteoParametros sorteoParametros) {
		ColaDeSorteo colaDeSorteo = sorteadorFactory.getColaDeSorteoForExpediente(sorteoParametros);
		if(colaDeSorteo != null && colaDeSorteo.getSorteoListener() == null){
			colaDeSorteo.setSorteoListener(this);
		}
		return colaDeSorteo;
	}
	
		
	public void resetSorteadores() {
		sorteadorFactory.resetSorteadores();
	}
	

	public static SorteoManager instance() {
		return (SorteoManager) Component.getInstance(SorteoManager.class, true);
		
	}

	public EntityManager getEntityManager() {
		return (EntityManager) Component.getInstance("entityManager");
	}
	
	public void notify(String msg) {
	}
	
	public void notifySorteoStarted(EntityManager entityManager, SorteoExpedienteEntry entry) {
	}
	
	public void notifySorteoFinished(EntityManager em, SorteoExpedienteEntry entry, boolean ok) {
		// usar instance() para obtener el bean ( no usar this.method porque viene de una conversacion/transaccion distinta 
		if(ok && entry.getSiguienteSorteoEncadenado() != null){			
			instance().runTransaction(entry.getSiguienteSorteoEncadenado());
		}else{
			Expediente expediente = entry.getExpediente(em);
			getLog().info("Notificando finalizacion del sorteo del expediente con ID " + expediente.getId() + " y clave " + expediente.getClave());
			StatusStorteoEnumeration.Type status = ok ? StatusStorteoEnumeration.Type.sorteadoOk : StatusStorteoEnumeration.Type.sorteadoError;
			setStatusSorteoExpediente(em, expediente, (Integer)status.getValue());
			
			//copia de radicaciones en segundo plano
			if(status.equals(StatusStorteoEnumeration.Type.sorteadoOk)){
				getLog().info("Realizando copia de radicacion a familia del expediente con ID " + expediente.getId() + " y clave " + expediente.getClave());
				ExpedienteManager expedienteManager = (ExpedienteManager)Component.getInstance(ExpedienteManager.class, true);
				expedienteManager.copiarRadicacionesFamilia(expediente, entry.getTipoRadicacion());
			}
		}
		entry.setFinished(true);
	}
	
	/*******ATOS COMERCIAL*******/
	//Fecha: 11-06-2014
	//Desarrollador:  Luis Suarez
	@Transactional
	public void runTransactionSorteoWebComercial(SorteoParametros sorteoParametros) throws SorteoException {
		ColaDeSorteo colaDeSorteo = getColaDeSorteo(sorteoParametros);        
        if (colaDeSorteo != null) {
            setStatusSorteoExpediente(getEntityManager(), sorteoParametros.getExpediente(), (Integer)StatusStorteoEnumeration.Type.sorteando.getValue());
			colaDeSorteo.processEntry(new SorteoExpedienteEntry(sorteoParametros));
		}
    }
	/*******ATOS COMERCIAL*******/

}
