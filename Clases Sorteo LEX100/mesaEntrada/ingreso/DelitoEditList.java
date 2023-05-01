package com.base100.lex100.mesaEntrada.ingreso;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.component.enumeration.DelitoEnumeration;
import com.base100.lex100.component.validator.CuilValidator;
import com.base100.lex100.controller.entity.letrado.LetradoSearch;
import com.base100.lex100.entity.Delito;
import com.base100.lex100.entity.DelitoExpediente;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Letrado;
import com.base100.lex100.entity.TipoDocumentoIdentidad;

public class DelitoEditList extends AbstractBeanEditList<DelitoEdit> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8917863248954850565L;

	public DelitoEditList(boolean autoAddMode) {
		setAutoAddMode(autoAddMode);
    }

//	public DelitoEditList(List<Delito> delitoList) {
//		if (delitoList != null) {
//			super.setList(getListFromDelitoList(delitoList));
//		}
//	}

	public DelitoEditList(List<DelitoExpediente> delitoExpedienteList) {
		if (delitoExpedienteList != null) {
			super.setList(getListFromDelitoExpedienteList(delitoExpedienteList));
		}
	}

	private static List<DelitoEdit> getListFromDelitoList(List<Delito> delitoList) {
		ArrayList<DelitoEdit> list = new ArrayList<DelitoEdit>();
		for(Delito delito:delitoList){
			list.add(new DelitoEdit(delito));
		}
		return list;
	}
	
	private static List<DelitoEdit> getListFromDelitoExpedienteList(List<DelitoExpediente> delitoExpedienteList) {
		ArrayList<DelitoEdit> list = new ArrayList<DelitoEdit>();
		for(DelitoExpediente delitoExpediente:delitoExpedienteList){
			list.add(new DelitoEdit(delitoExpediente));
		}
		return list;
	}
	
	@Override
	public void onEditLine(DelitoEdit entity) {
		super.onEditLine(entity);
		DelitoEnumeration delitoEnumeration = (DelitoEnumeration) Component.getInstance(DelitoEnumeration.class, true);
		delitoEnumeration.setSelected(entity.getDelito());
	}

	@Override
	public DelitoEdit createNewInstance() {
		return new DelitoEdit();
	}

	public void copyData(DelitoEdit from, DelitoEdit to) {
		to.copyFrom(from);
	}

	@Override
	public void copyDataToTmp(DelitoEdit from, DelitoEdit tmp) {
		copyData(from, tmp);
	}

	@Override
	public void copyDataFromTmp(DelitoEdit tmp, DelitoEdit dest) {
		copyData(tmp, dest);
	}

	@Override
	public void checkAccept() throws EntityOperationException {
		getTmpInstance().setError(false);
		getTmpInstance().checkAccept();
		checkDups(getTmpInstance(), getEditingInstance());
		copyDataFromTmp(getTmpInstance(), getEditingInstance());
	}

	public boolean checkConcurso(){
		int idx = getList().size();
		boolean ret = true;
		DelitoEdit last = null;
		while(idx > 0){
			if(!getList().get(idx-1).isEmpty()){
				last = getList().get(idx-1);
				break;
			}
			idx--;
		}
		if(last != null){
			ret = Strings.isEmpty(last.getEnConcurso());
		}
		return ret;
	}

	private void checkDups(DelitoEdit delitoToVerify, DelitoEdit excludeInstance) throws EntityOperationException {
		for(DelitoEdit delitoEdit: getList()){
			if(!delitoEdit.isEmpty() && delitoEdit != excludeInstance){
				if(delitoEdit.getDelito() != null && delitoToVerify.getDelito() != null &&
					delitoEdit.getDelito().getId() == delitoToVerify.getDelito().getId()){
					String errKey = "delito.error.delitoDuplicado";
					errorMsg(errKey);
					delitoToVerify.setError(true);
					throw new EntityOperationException();
				}
			}
		}
		
	}
	
	@Override
	public boolean isEmpty(DelitoEdit instance) {
		return instance.isEmpty();
	}

	public boolean contains(Delito entity) {
		for(DelitoEdit line: getList()){
			if(line.getCodigo() != null && line.getCodigo().equals(entity.getCodigo())){
				return true;
			}
		}
		return false;
	}

	public String getDelitoLongDescription(DelitoEdit item){
		Delito delito = item.getDelito();
		if(delito == null){
			return "";
		}else{
			StringBuffer sb = new StringBuffer();
			if(delito.getLey() != null){
				if(delito.getLey().equals(11179)){
					sb.append("C.P.");
				} else {
					sb.append("Ley "+delito.getLey());
				}
			}
			if(delito.getArticulo() != null){
				sb.append(" - Art. "+delito.getArticulo());
			}
			if(delito.getInciso() != null){
				sb.append(" - Inc. "+delito.getInciso());
			}
			sb.append(" - "+delito.getDescripcion());
			return sb.toString();
		}
	}
	
	public void insertDelitoAt(DelitoEdit delitoEdit, int idx){
		if(idx > getList().size()){
			idx  = getList().size();
		}
		getList().add(idx, delitoEdit);
	}
	
	public void insertDelitoAt(DelitoExpediente delitoExpediente, int idx){
		
	}
	
	
}
