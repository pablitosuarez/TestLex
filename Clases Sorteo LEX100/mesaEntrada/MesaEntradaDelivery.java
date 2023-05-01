package com.base100.lex100.mesaEntrada;

import com.base100.lex100.component.admin.UserTaskManager;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.mesaEntrada.asignacion.PropagarRadicacionesTask;

/**
 * @author Sergio Forastieri
 */
public class MesaEntradaDelivery {
	
	private Expediente entity;
	
	public MesaEntradaDelivery(Expediente expediente) {
		this.entity = expediente;
	}
	
	/**
	 * Creacion de Task en forma async para propagar radicaciones a la familia de un {@link Expediente}
	 */
	public void sendTaskPropagarRadicacionesAsync(TipoRadicacionEnumeration.Type tipoRadicacion, OficinaAsignacionExp oficinaAsignacionExp, String username, boolean isReasignacion) {
		clean(username);
		
		PropagarRadicacionesTask task = new PropagarRadicacionesTask(entity, tipoRadicacion, oficinaAsignacionExp, isReasignacion);
		UserTaskManager.instance().doStartTaskAsync(task, username);
	}
	
	/**
	 * Elimina todas las tareas que se encuentren finalizadas para un determinado usuario
	 * @param username
	 */
	public static void clean(String username){
		UserTaskManager.instance().doClearFinalizedTaks(username);
	}
	
}
