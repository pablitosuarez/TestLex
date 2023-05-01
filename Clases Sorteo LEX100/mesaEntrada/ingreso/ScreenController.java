package com.base100.lex100.mesaEntrada.ingreso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Init.ObserverMethodExpression;
import org.jboss.seam.framework.Controller;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

import com.base100.expand.seam.framework.entity.AbstractEntityList;
import com.base100.expand.seam.framework.entity.AbstractEntitySearch;
import com.base100.expand.seam.framework.entity.IEntitySelectionListener;
import com.base100.lex100.manager.web.WebManager;

public class ScreenController extends Controller {
	
	private Map<String, IEntitySelectionListener<?>> selectionListenerMap = new HashMap<String, IEntitySelectionListener<?>>();
	public void addStatusMessage(StatusMessage.Severity severity, 
			String message, Object ... parameters) {
		getStatusMessages().add(severity, message, parameters);
	}

	public void errorMsg(String key, Object... params) {
		getStatusMessages().addFromResourceBundle(Severity.ERROR, key, params);		
		if(isAddNotificacion()) {
			WebManager.instance().getNotificationManager().addError(getMessages().get(key), params);
		}
	}
	
	public void warnMsg(String key, Object... params) {
		getStatusMessages().addFromResourceBundle(Severity.WARN, key, params);		
		if(isAddNotificacion()) {
			WebManager.instance().getNotificationManager().addWarn(getMessages().get(key), params);
		}
	}
	
	public void infoMsg(String key, Object... params) {
		getStatusMessages().addFromResourceBundle(Severity.INFO, key, params);		
		if(isAddNotificacion()) {
			WebManager.instance().getNotificationManager().addInfo(getMessages().get(key), params);
		}
	}
	
	public void errorFieldRequired(String keyField){
		addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['"+keyField+"']} :" + org.jboss.seam.international.Messages.instance().get("error_notnull"));
	}

	public boolean isAddNotificacion() {
		return ExpedienteMeAdd.instance().isIngresoWeb();
	}

}
