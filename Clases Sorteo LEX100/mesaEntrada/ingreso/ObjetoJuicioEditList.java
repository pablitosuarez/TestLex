package com.base100.lex100.mesaEntrada.ingreso;

import java.util.ArrayList;
import java.util.List;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.component.enumeration.ObjetoJuicioEnumeration;
import com.base100.lex100.entity.ObjetoJuicio;

/**
 * Editing functionalities for a list of objetojuicio 
 * @author cesar
 *
 */
public class ObjetoJuicioEditList extends AbstractBeanEditList<ObjetoJuicioEdit>{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4105818850321044853L;

	
	public ObjetoJuicioEditList(boolean autoAddMode) {
		setAutoAddMode(autoAddMode);
    }

    public ObjetoJuicioEditList(List<ObjetoJuicio> list) {    	
		if(list != null){
			super.setList(createList(list));
		}
	}

	private static List<ObjetoJuicioEdit> createList(List<ObjetoJuicio> objetoJuicioList) {
		ArrayList<ObjetoJuicioEdit> list = new ArrayList<ObjetoJuicioEdit>();
		for(ObjetoJuicio objetoJuicio:objetoJuicioList){
			list.add(new ObjetoJuicioEdit(objetoJuicio));
		}
		return list;
	}

	@Override
	public void copyDataToTmp(ObjetoJuicioEdit from, ObjetoJuicioEdit tmp){
		tmp.setCodigo(from.getCodigo());
		tmp.setObjetoJuicio(from.getObjetoJuicio());
		tmp.setEnConcurso(from.getEnConcurso());
	}
	
	@Override
	public void copyDataFromTmp(ObjetoJuicioEdit tmp, ObjetoJuicioEdit dest) {
		dest.setCodigo(tmp.getCodigo());
		dest.setObjetoJuicio(tmp.getObjetoJuicio());
		dest.setEnConcurso(tmp.getEnConcurso());
	}
	
	@Override
	public void onAcceptLine() throws EntityOperationException {
		super.onAcceptLine();
	}

	@Override
	public void checkAccept() throws EntityOperationException{
		getTmpInstance().setError(false);
		checkDups(getTmpInstance(), getEditingInstance());	
		copyDataFromTmp(getTmpInstance(), getEditingInstance());
	}


	private void checkDups(ObjetoJuicioEdit objetoJuicioToVerify, ObjetoJuicioEdit excludeInstance) throws EntityOperationException {
		for (ObjetoJuicioEdit objetoJuicioEdit : getList()) {
			if (!objetoJuicioEdit.isEmpty()	&& objetoJuicioEdit != excludeInstance) {
				if (equals(objetoJuicioEdit.getCodigo(),objetoJuicioToVerify.getCodigo())) {
					String errKey = "objetoJuicio.error.duplicado";
					errorMsg(errKey);
					throw new EntityOperationException();
				}
			}
		}
	}


	@Override
	public ObjetoJuicioEdit createNewInstance() {
		ObjetoJuicioEnumeration.instance().setSelected(null);
		return new ObjetoJuicioEdit();
	}

	@Override
	public boolean isEmpty(ObjetoJuicioEdit instance) {
		return instance.isEmpty();
	}
	
	
}
