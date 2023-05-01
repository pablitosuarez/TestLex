package com.base100.lex100.mesaEntrada.sorteo;

import javax.persistence.EntityManager;

import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.OficinaCargaExp;


public class BolaSorteo {
	private OficinaCargaExp oficinaCargaExp;
//	private Integer idOficina;
//	private String oficina;
//	private String rubro;
//	private int cargaTrabajo; // cantidadAsignada ???
//	private int numeroExpedientes;
//	private int factorSuma;
//	private boolean inhibido; // total / parcial????
	
	public BolaSorteo() {
		
	}
	
	public BolaSorteo(OficinaCargaExp oficinaCargaExp) {
		this.oficinaCargaExp = oficinaCargaExp;
		this.getOficina();// force read
	}
		
	public String getOficina() {
		return oficinaCargaExp.getOficina().getCodigo();
	}


	public String getRubro() {
		return oficinaCargaExp.getRubro();
	}
	
	public int getCargaTrabajo() {
		return oficinaCargaExp.getCargaTrabajo();
	}
	
	public int getNumeroExpedientes() {
		return oficinaCargaExp.getNumeroExpedientes();
	}
	
	public int getFactorSuma() {
		return oficinaCargaExp.getFactorSuma();
	}
		
	public boolean isInhibido() {
		return oficinaCargaExp.isInhibido();
	}

	public void incrementaCarga(int pesoObjetoJuicio, String info, String usuario, Expediente expediente) {
		int incremento = calculaPesoCompensacion(pesoObjetoJuicio);
		// Si se suma el numero de expedientes (representa los sorteos o reasignaciones)
		this.oficinaCargaExp.setNumeroExpedientes(oficinaCargaExp.getNumeroExpedientes()+1);		
		this.oficinaCargaExp.setCargaTrabajo(this.oficinaCargaExp.getCargaTrabajo() + incremento);
		this.oficinaCargaExp.setAuditableOperation("COMP. POSITIVA "+info);
		this.oficinaCargaExp.setAuditableUsuario(usuario);
		this.oficinaCargaExp.setAuditableExpediente(expediente);
	}
	
	public void incrementaCargaFija(int incremento, String info, String usuario, Expediente expediente) {
	 
		// Si se suma el numero de expedientes (representa los sorteos o reasignaciones)
		this.oficinaCargaExp.setNumeroExpedientes(oficinaCargaExp.getNumeroExpedientes()+1);		
		this.oficinaCargaExp.setCargaTrabajo(this.oficinaCargaExp.getCargaTrabajo() + incremento);
		this.oficinaCargaExp.setAuditableOperation("COMP. POSITIVA "+info);
		this.oficinaCargaExp.setAuditableUsuario(usuario);
		this.oficinaCargaExp.setAuditableExpediente(expediente);
	}
	
	public boolean decrementaCarga(int pesoObjetoJuicio, String info, String usuario, Expediente expediente) {
		this.oficinaCargaExp.setAuditableUsuario(usuario);
		this.oficinaCargaExp.setAuditableExpediente(expediente);
		int decremento = calculaPesoCompensacion(pesoObjetoJuicio);
		this.oficinaCargaExp.setNumeroExpedientes(oficinaCargaExp.getNumeroExpedientes()-1);
		this.oficinaCargaExp.setCargaTrabajo(this.oficinaCargaExp.getCargaTrabajo() - decremento);
		this.oficinaCargaExp.setAuditableOperation("COMP. NEGATIVA "+info);
		return true;
	}

	private int calculaPesoCompensacion(int pesoObjetoJuicio) {
		return pesoObjetoJuicio * getFactorSuma();
	}


	public Integer getIdOficina() {
		return oficinaCargaExp.getOficina().getId();
	}


	public OficinaCargaExp getOficinaCargaExp() {
		return oficinaCargaExp;
	}

	void mergeOficinaCargaExp(EntityManager entityManager){
		if(this.oficinaCargaExp != null){
			this.oficinaCargaExp = entityManager.find(OficinaCargaExp.class, this.oficinaCargaExp.getId());
		}
	}

	public void setOficinaCargaExp(OficinaCargaExp oficinaCargaExp) {
		this.oficinaCargaExp = oficinaCargaExp;
	}

}

