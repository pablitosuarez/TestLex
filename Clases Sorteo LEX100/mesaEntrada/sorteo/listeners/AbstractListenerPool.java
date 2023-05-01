package com.base100.lex100.mesaEntrada.sorteo.listeners;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractListenerPool<C extends IListener>{
	private List<C> listeners;
	
	public void addListener(C listener){
		if(listeners == null){
			listeners = new ArrayList<C>();
		}
		listeners.add(listener);
	}
	
	public void removeListener(C listener){
		if(listeners != null){
			listeners.remove(listener);
		}
	}
	
	public void notify(String msg){
		if(listeners != null){
			for(IListener listener: listeners){
				listener.notify(msg);
			}
		}
	}

}
