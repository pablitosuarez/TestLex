package com.base100.lex100.mesaEntrada.ingreso;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.component.enumeration.DelitoEnumeration;

/**
 * Editing functionalities for a list of Beans 
 * @author fermin
 *
 */
public abstract class AbstractBeanEditList<E extends IEditPojo> extends ScreenController{
       
	private E editInstance;
	private E tmpInstance;
	private E newInstance;
	private List<E> entityList;
	private List<E> deletedEntityList;
	private boolean autoAddMode;

	private boolean duplicateLast;

	public abstract E createNewInstance();
	public abstract void copyDataToTmp(E from, E tmp);
	public abstract void copyDataFromTmp(E tmp, E dest);
	public abstract void checkAccept() throws EntityOperationException;
	public abstract boolean isEmpty(E instance);
	
	
	public void duplicate(E from, E to) {
		 copyDataFromTmp(from, to);
	}
	
	public void setList(List<E> list){
		entityList = new ArrayList<E>(list);
		addNewInstanceToList();
	}
	
	public List<E> getList(){
		if(entityList == null){
			entityList = new ArrayList<E>();
			addNewInstanceToList();
		}
		return entityList;
	}	

	public int size(){
		int count = 0;
		for(E item: getList()){
			if(!item.isEmpty()){
				count++;
			}
		}
		return count;
	}
	
	public void addNewInstanceToList() {
		newInstance = createNewInstance();
		if (duplicateLast) {
			duplicateLast = false;
			E last = getLastInstance();
			if(last != null){
				duplicate(last, newInstance);
			}
		}
		newInstance.setNew(true);
		getList().add(newInstance);
		checkAutoAdd();
	}
	
	
	public void addInstanceToList(E instance, Integer position) {
		if(position != null){
			getList().add(position,instance);
		}else{
			getList().add(instance);
		}
	}
	
	public void onDeleteLine(E entity){
		if(getList().contains(entity)){
			getList().remove(entity);
			getDeletedEntityList().add(entity);
		}
		if(this.editInstance == entity){
			this.editInstance = null;
		}
	}
	
	public void onEditLine(E entity){
		editInstance = entity;
		tmpInstance = createNewInstance();
		copyDataToTmp(entity, tmpInstance);
	}


	public void onCancelLine() throws EntityOperationException{
		this.editInstance = null;
		checkAutoAdd();
	}
		

	public void onAcceptLine() throws EntityOperationException{
		if(isEditing()){
			if(!isEditNew() || !isEmpty(getTmpInstance())){
				checkAccept();
				if(isEditNew()){
					addNewInstanceToList();
				}
				getEditingInstance().setModified(true);
				
			}
			this.editInstance = null;
		}
		checkAutoAdd();
	}
	
	public void doSaveLine(){
		try {
			onAcceptLine();
		} catch (EntityOperationException e) {
			// catch controlled exception
		}
	}
	
	public void doSaveLineAndDup() {
		duplicateLast = true;
		doSaveLine();
	}
	
	public void checkAutoAdd(){
		if(!isEditing() && autoAddMode && getNewInstance() != null){
			onEditLine(getNewInstance());
		}		
	}

	public E getLastInstance() {
		return getList().size() > 0 ? getList().get(getList().size()-1) : null;
	}

	public boolean isEditNew(){
		return isNew(getEditingInstance());		
	}
	
	public boolean isNew(E instance){
		return instance == getNewInstance();		
	}

	public E getNewInstance() {
		return newInstance;
	}
	
	public E getTmpInstance() {
		return tmpInstance;
	}
	
	public E getEditingInstance() {
		return editInstance;
	}

	public boolean isEditing(E instance){
		return editInstance == instance;
	}

	public boolean isEditing(){
		return editInstance != null;
	}

	public void cancelEditing(){
		editInstance = null;
	}
	
	public boolean isAutoAddMode() {
		return autoAddMode;
	}
	
	public void setAutoAddMode(boolean autoAddMode) {
		this.autoAddMode = autoAddMode;
	}
	public List<E> getDeletedEntityList() {
		if(deletedEntityList == null){
			deletedEntityList = new ArrayList<E>();
		}
		return deletedEntityList;
	}
	
	public boolean equals(String s1, String s2) {
		return Strings.nullIfEmpty(s1) == null ? Strings.nullIfEmpty(s2) == null : s1.equals(s2); 
	}

	public void deleteEmptyLines() {
		for(int i=0; i < getList().size(); i++){
			if(getList().get(i).isEmpty()){
				getList().remove(i);
				i--;
			}
		}
		checkAutoAdd();
	}
}
