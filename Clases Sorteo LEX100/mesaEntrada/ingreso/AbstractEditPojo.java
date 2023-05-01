package com.base100.lex100.mesaEntrada.ingreso;

import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Letrado;
import com.base100.lex100.entity.LetradoIntervinienteExp;

public abstract class AbstractEditPojo extends ScreenController implements IEditPojo  {
	private boolean modified;
	private boolean newPojo = true;
	private boolean error;
	private Set<String> errorFields = new HashSet<String>();


	public boolean isError(String field) {
		return errorFields.contains(field);
	}
	
	public boolean isError() {
		return error;
	}

	public void addErrorField(String field) {
		if(field != null){
			errorFields.add(field);
		}
		setError(true);
	}

	public void setError(boolean error) {		
		if(!error){
			errorFields.clear();
		}
		this.error = error;
	}

	public abstract boolean isEmpty();

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public boolean isNew() {
		return newPojo;
	}

	public void setNew(boolean isNew) {
		this.newPojo = isNew;
	}
	
	public boolean needUpdateOrAdd() {
		boolean ret = false;
		if(!isEmpty()){
			ret = isNew() || isModified();
		}
		return ret;
	}

	public boolean needDelete() {
		if(isNew()){
			return false;
		}else{
			return true;
		}
	}

}
