package com.base100.lex100.mesaEntrada.ingreso;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.controller.entity.delito.DelitoSearch;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.DelitoExpediente;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Delito;

public class DelitoEdit extends AbstractEditPojo{
	private String codigo;	
	private Delito delito;
	private DelitoExpediente delitoExpediente;
	private boolean tentativa;
	private String enConcurso;
	
	private boolean error;

    public DelitoEdit(){
    }

    public DelitoEdit(Delito delito){
    	setDelito(delito);
    }

    public DelitoEdit(DelitoExpediente delitoExpediente){
    	setDelitoExpediente(delitoExpediente);
    	setNew(false);
    }

    public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	public Delito getDelito() {
		return delito;
	}

	public void setDelito(Delito delito) {
		this.delito = delito;
		if(delito == null){
			codigo = null;
		}else{
			codigo = delito.getCodigo();
		}
	}

	public String getDescripcion(){
		if(delito != null){
			return delito.getDescripcion();
		}else{
			return null;
		}
	}

	public void checkAccept() throws EntityOperationException{
		if(getDelito() == null || !getDelito().getCodigo().equals(getCodigo())){
			if(getCodigo() != null){
				DelitoSearch delitoSearch = (DelitoSearch) Component.getInstance(DelitoSearch.class, true);
				this.delito = delitoSearch.findByNaturalKey(getCodigo().trim());
			}else{
				this.delito = null;
			}
		}
		if(delito == null){
			String errKey = "delito.error.notExist";
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

	public void copyFrom(DelitoEdit from) {
		this.setCodigo(from.getCodigo());
		this.setDelito(from.getDelito());
		this.setTentativa(from.isTentativa());
		this.setEnConcurso(from.getEnConcurso());
		this.error = from.isError();		
	}
	
	public boolean isEmpty(){
		return getDelito() == null && Strings.isEmpty(getCodigo());
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

	public void clearDelito() {
		setDelito(null);
		this.setTentativa(false);
		this.setEnConcurso(null);
	}

	public DelitoExpediente getDelitoExpediente() {
		return delitoExpediente;
	}

	public void setDelitoExpediente(DelitoExpediente delitoExpediente) {
		this.delitoExpediente = delitoExpediente;
		if(delitoExpediente == null){
			setDelito(null);
			setTentativa(false);
			setEnConcurso(null);
		}else{
			setDelito(delitoExpediente.getDelito());
			setTentativa(delitoExpediente.isTentativa());
			setEnConcurso(delitoExpediente.getEnConcurso());
		}
	}

	public void copyToDelitoExpediente(DelitoExpediente delitoExpediente) {
		delitoExpediente.setTentativa(isTentativa());
		delitoExpediente.setDelito(getDelito());
		delitoExpediente.setEnConcurso(getEnConcurso());
	}
	
	public boolean isDrogas_Ley_23737(){
		return (this.delito != null && this.delito.getLey().equals(23737)) ;
	}
	
	public boolean isTrata_de_personas_Ley_26364_Ley_11179_arts_145_bis_y_ter(){
		return (this.delito != null && 
				( 
				 this.delito.getLey().equals(26364) ||  
				(this.delito.getLey().equals(11179) && this.delito.getArticulo().equals(145) && ("B".equals(this.delito.getAperturaArticulo()) || "T".equals(this.delito.getAperturaArticulo())))) 
				) ;
	}
	
	@Override
	public boolean needDelete() {
		if(isNew() || getDelitoExpediente() == null){
			return false;
		}else{
			return true;
		}
	}
}
