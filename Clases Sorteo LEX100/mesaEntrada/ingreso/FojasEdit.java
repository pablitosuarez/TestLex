package com.base100.lex100.mesaEntrada.ingreso;

import org.jboss.seam.Component;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.controller.entity.tipoCausa.TipoCausaSearch;

public class FojasEdit extends ScreenController {
	
	private Integer fojas;
	private Integer paquetes;
	private Integer cuerpos;
	private Integer agregados;
	private Integer sobres;
    private String comentarios;

	private boolean error;

	public FojasEdit() {
		this.fojas = 1;
	}
	
	public Integer getFojas() {
		return fojas;
	}
	public void setFojas(Integer fojas) {
		this.fojas = fojas;
	}
	public Integer getCuerpos() {
		return cuerpos;
	}
	public void setCuerpos(Integer cuerpos) {
		this.cuerpos = cuerpos;
	}
	public Integer getAgregados() {
		return agregados;
	}
	public void setAgregados(Integer agregados) {
		this.agregados = agregados;
	}
	public Integer getSobres() {
		return sobres;
	}
	public void setSobres(Integer sobres) {
		this.sobres = sobres;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}

	public void checkAccept() throws EntityOperationException {
		setError(false);
		
		if(fojas == null || fojas <= 0){
			String errKey = "tipoCausa.error.fojasCero";
			errorMsg(errKey);
			setError(true);
		}
		if(error){
			throw new EntityOperationException();
		}
	}

	public Integer getPaquetes() {
		return paquetes;
	}

	public void setPaquetes(Integer paquetes) {
		this.paquetes = paquetes;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

}
