package com.base100.lex100.mesaEntrada.ingreso;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.controller.entity.objetoJuicio.ObjetoJuicioSearch;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.ObjetoJuicio;

public class ObjetoJuicioEdit extends AbstractEditPojo{
	private String codigo;	
	private ObjetoJuicio objetoJuicio;
	private String descripcionObjetoJuicio;
	private boolean tentativa;
	private String enConcurso;
	
	private boolean error;

    public ObjetoJuicioEdit(){
    }

    public ObjetoJuicioEdit(ObjetoJuicio objetoJuicio){
    	setObjetoJuicio(objetoJuicio);
    }

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public ObjetoJuicio getObjetoJuicio() {
		return objetoJuicio;
	}

	public void setObjetoJuicio(ObjetoJuicio objetoJuicio) {
		this.objetoJuicio = objetoJuicio;
		if(objetoJuicio == null){
			codigo = null;
		}else{
			codigo = objetoJuicio.getCodigo();
		}
	}

	public String getDescripcion(){
		if(objetoJuicio != null){
			return objetoJuicio.getDescripcion();
		}else{
			return null;
		}
	}

	public void checkAccept() throws EntityOperationException{
		if(getObjetoJuicio() == null || !getObjetoJuicio().getCodigo().equals(getCodigo())){
			if(getCodigo() != null){
				ObjetoJuicioSearch objetoJuicioSearch = (ObjetoJuicioSearch) Component.getInstance(ObjetoJuicioSearch.class, true);
				this.objetoJuicio = objetoJuicioSearch.findByCodigo(getCodigo().trim(), ExpedienteMeAdd.instance().getMateria());
			}else{
				this.objetoJuicio = null;
			}
		}
		if(objetoJuicio == null){
			String errKey = "objetoJuicio.error.notExist";
			errorMsg(errKey);
			setError(true);
			throw new EntityOperationException();
		}
		setError(false);
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public void copyFrom(ObjetoJuicioEdit from) {
		this.setCodigo(from.getCodigo());
		this.setObjetoJuicio(from.getObjetoJuicio());
		this.setTentativa(from.isTentativa());
		this.setEnConcurso(from.getEnConcurso());
		this.error = from.isError();		
	}
	
	public boolean isEmpty(){
		return getObjetoJuicio() == null && Strings.isEmpty(getCodigo());
	}

	public boolean isTentativa() {
		return tentativa;
	}

	public void setTentativa(boolean tentativa) {
		this.tentativa = tentativa;
	}

	public String getEnConcurso() {
		return enConcurso;
	}

	public void setEnConcurso(String enConcurso) {
		this.enConcurso = enConcurso;
	}

	public void clearObjetoJuicio() {
		setObjetoJuicio(null);
		this.setTentativa(false);
		this.setEnConcurso(null);
	}

	public String getDescripcionObjetoJuicio() {
		return descripcionObjetoJuicio;
	}

	public void setDescripcionObjetoJuicio(String descripcionObjetoJuicio) {
		this.descripcionObjetoJuicio = descripcionObjetoJuicio;
	}
}
