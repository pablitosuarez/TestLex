package com.base100.lex100.mesaEntrada.sorteo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Collections;

import com.base100.lex100.mesaEntrada.sorteo.listeners.IListener;
import com.base100.lex100.mesaEntrada.sorteo.listeners.ListenerPool;

public class Bolillero {	
	private String rubro;
	private List<BolaSorteo> bolas;
	private ListenerPool listeners = new ListenerPool();
	private String name;
	private int rangoMinimos;
	private int minimoNumeroBolas;
	private boolean sorteaRubro; // por defecto se sortea oficina, pero en ordende circulaicon se sortea rubro (osea orden de circulacion)
	
	
	public Bolillero(String name, String rubro, List<BolaSorteo> bolas, int rangoMinimos, int minimoNumeroBolas, boolean sorteaRubro) {
		this.name = name;
		this.rubro = rubro;
		this.bolas = new ArrayList<BolaSorteo>(bolas);
		this.rangoMinimos = rangoMinimos;
		this.minimoNumeroBolas = minimoNumeroBolas;
		this.sorteaRubro = sorteaRubro;
		Collections.sort(this.bolas, new Comparator<BolaSorteo>() {
			@Override
			public int compare(BolaSorteo o1, BolaSorteo o2) {
				String codigo1 = o1.getOficinaCargaExp().getOficina().getCodigo();
				String codigo2 = o2.getOficinaCargaExp().getOficina().getCodigo();
				return codigo1.compareTo(codigo2);
			}
		});
	}
	
	public void addListener(IListener listener){
		listeners.addListener(listener);
	}
	
	public List<BolaSorteo> getBolas() {
		return bolas;
	}

	public void setBolas(List<BolaSorteo> bolas) {
		this.bolas = bolas;		
	}

	public ListenerPool getListeners() {
		return listeners;
	}

	public void notify(String msg){
		listeners.notify(msg);
	}

	public List<BolaSorteo> find(String oficinaSorteo) {
		List<BolaSorteo> list = new ArrayList<BolaSorteo>();
		for(BolaSorteo bola: bolas){
			if(bola.getOficina().equals(oficinaSorteo)){
				list.add(bola);
			}
		}
		return list;
		
	}

	public List<BolaSorteo> findBolasByOficinaId(Integer oficinaId, String rubro) {
		List<BolaSorteo> list = new ArrayList<BolaSorteo>();
		if(rubro != null){
			for(BolaSorteo bola: bolas){
				if(bola.getOficinaCargaExp().getOficina() != null 
						&& oficinaId.equals(bola.getOficinaCargaExp().getOficina().getId())
						&& rubro.equals(bola.getRubro()) 
						){
					list.add(bola);
				}
			}
		}
		return list;
		
	}

	public String getRubro() {
		return rubro;
	}


	public String getName() {
		return name;
	}


	public String getStatus() {
		return getStatus(getBolas());
	}
	
	public String getStatus(List<BolaSorteo> listaMenorCarga) {
		StringBuffer sb = new StringBuffer();
		for(BolaSorteo bola:listaMenorCarga){
			if(sorteaRubro){
				sb.append(bola.getRubro());
			}else{
				sb.append(bola.getOficinaCargaExp().getOficina().getCodigo());
			}
			sb.append("["+bola.getCargaTrabajo()+"]");
			if (bola.getOficinaCargaExp().isInhibido()) {
				sb.append("[I]");
			}
			sb.append(' ');
		}
		return sb.toString();		
	}

	public int getRangoMinimos() {
		return rangoMinimos;
	}

	public int getMinimoNumeroBolas() {
		return minimoNumeroBolas;
	}

	public boolean isSorteaRubro() {
		return sorteaRubro;
	}

	public void setSorteaRubro(boolean sorteaRubro) {
		this.sorteaRubro = sorteaRubro;
	}
}
