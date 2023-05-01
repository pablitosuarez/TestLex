package com.base100.lex100.mesaEntrada.conexidad;

import java.util.ArrayList;
import java.util.List;

public class ListaIntervinientes {

	private ActorDemandado tipo;
	private List<String> nombres;
	private List<String> cuitCuils;
	private List<String> dnis;
	private boolean hayPersonaJuridica;

	public ListaIntervinientes(ActorDemandado tipo) {
		this.tipo = tipo;
	}
	
	public ListaIntervinientes(ActorDemandado tipo, List<String> nombres, List<String> cuitCuils, List<String> dnis, boolean hayPersonaJuridica) {
		this.tipo = tipo;
		this.nombres = nombres;
		this.cuitCuils = cuitCuils;
		this.dnis = dnis;
		this.hayPersonaJuridica = hayPersonaJuridica;
	}
	
	public ActorDemandado getTipo() {
		return tipo;
	}
	public void setTipo(ActorDemandado tipo) {
		this.tipo = tipo;
	}
	public List<String> getNombres() {
		return nombres;
	}
	public void setNombres(List<String> nombres) {
		if (nombres != null) {
			this.nombres = new ArrayList<String>(nombres);
		} else {
			this.nombres = null;
		}
	}

	public void addNombres(List<String> nombres) {
		if (nombres != null) {
			if (this.nombres != null) {
				this.nombres.addAll(nombres);
			}else {
				setNombres(nombres);
			}
		}
	}

	public List<String> getCuitCuils() {
		return cuitCuils;
	}
	
	public void setCuitCuils(List<String> cuitCuils) {
		if (cuitCuils != null) {
			this.cuitCuils = new ArrayList<String>(cuitCuils);
		} else {
			this.cuitCuils = null;
		}
	}

	public void addCuitCuils(List<String> cuitCuils) {
		if(cuitCuils != null) {
			if (this.cuitCuils != null) {
				this.cuitCuils.addAll(cuitCuils);
			}else {
				setCuitCuils(cuitCuils);
			}
		}
	}
	
	public List<String> getDnis() {
		return dnis;
	}
	
	public void setDnis(List<String> dnis) {
		if (dnis != null) {
			this.dnis = new ArrayList<String>(dnis);
		} else {
			this.dnis = null;
		}
	}
	
	public void addDnis(List<String> dnis) {
		if(dnis != null) {
			if (this.dnis != null) {
				this.dnis.addAll(dnis);
			}else {
				setDnis(dnis);
			}
		}
	}
	
	public String getParametroNombre(){
		return "nombre"+tipo.getLabel();
	}
	
	public String getParametroCuit(){
		return "documento"+tipo.getLabel();
	}
	
	public String getParametroDni(){
		return "dni"+tipo.getLabel();
	}

	public void addNombre(String nombre) {
		if(this.nombres == null){
			this.nombres = new ArrayList<String>();
		}
		this.nombres.add(nombre);		
	}

	public void addCuitCuil(String cuitCuil) {
		if(this.cuitCuils == null){
			this.cuitCuils = new ArrayList<String>();
		}
		this.cuitCuils.add(cuitCuil);		
	}
	
	public void addDni(String dni) {
		if(this.dnis == null){
			this.dnis = new ArrayList<String>();
		}
		this.dnis.add(dni);		
	}

	public boolean hasNombres() {
		return nombres != null && nombres.size() > 0;
	}

	public boolean hasCuitCuils() {
		return cuitCuils != null && cuitCuils.size() > 0;
	}

	public boolean hasDnis() {
		return dnis != null && dnis.size() > 0;
	}
		
	public boolean hasMultipleInfo(){
		int num = 0;
		if(hasNombres()){
			num++;
		}
		if(hasCuitCuils()){
			num++;
		}
		if(hasDnis()){
			num++;
		}
		return num > 1;
	}
	
	public boolean hasTooMuchValues(){
		return ((nombres!=null && nombres.size()>=1000) || (cuitCuils!=null && cuitCuils.size()>=1000) || (dnis!=null && dnis.size()>=1000));
	}
	
	public boolean hasAnyInfo(){
		return hasNombres() || hasCuitCuils() || hasDnis();		
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		if(nombres != null){
			sb.append("nombres: ");
			sb.append(nombres.toString());
		}
		if(cuitCuils != null){
			sb.append(" cuitCuils: ");
			sb.append(cuitCuils.toString());
		}
		if(dnis != null){
			sb.append(" dnis: ");
			sb.append(dnis.toString());
		}
		return sb.toString();
	}

	public boolean isHayPersonaJuridica() {
		return hayPersonaJuridica;
	}

	public void setHayPersonaJuridica(boolean hayPersonaJuridica) {
		this.hayPersonaJuridica = hayPersonaJuridica;
	}
}
