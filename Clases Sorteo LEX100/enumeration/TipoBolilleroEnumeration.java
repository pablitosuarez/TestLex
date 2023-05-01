package com.base100.lex100.component.enumeration;

import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.Util;
import com.base100.expand.seam.framework.component.enumeration.IEntityEnumeration;

import com.base100.lex100.entity.TipoBolillero;

@Name("tipoBolilleroEnumeration")
@Scope(ScopeType.CONVERSATION)
public class TipoBolilleroEnumeration implements IEntityEnumeration<TipoBolillero>{

	public static final String ASIGNACION = "ASG";

	public static final String RECUSACION = "REC";
	public static final String RECUSACION_CON_CAUSA = "RCC";
	public static final String RECUSACION_SIN_CAUSA = "RSC";
	public static final String EXCUSACION = "EXC";

	public static final String RECURSO_EXTRAORDINARIO="REX";
	
	private List<TipoBolillero> tipoBolilleroEnumeration;

	private List<TipoBolillero> tipoBolilleroEnumerationSelection;
	private String selectionPattern;
	private Integer maxResults = 50;

	private String userInput;
	private TipoBolillero selected;

	@In
	private EntityManager entityManager;
	
	public void reset() {
		tipoBolilleroEnumeration = null;
		selectionPattern = null;
		tipoBolilleroEnumerationSelection = null;
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoBolillero>  getItems() {
		if (tipoBolilleroEnumeration == null) {
			Query query = entityManager.createQuery("from TipoBolillero");
			if(maxResults != null){
				query.setMaxResults(maxResults);
			}
			tipoBolilleroEnumeration = query.getResultList();
		}
		return tipoBolilleroEnumeration;
	}

	@SuppressWarnings("unchecked")
	public List<TipoBolillero>  getItems(String pattern) {
		if(pattern == null || pattern.trim().equals("")){
			return getItems();
		}
		if (selectionPattern == null || !selectionPattern.equals(pattern)) {
			Query query = entityManager.createQuery("from TipoBolillero where lower(rtrim(codigo) || '-' || rtrim(descripcion)) like '%"+pattern.toLowerCase()+"%'");
			if(maxResults != null){
				query.setMaxResults(getMaxResults());
			}			
			tipoBolilleroEnumerationSelection = query.getResultList();
			selectionPattern = pattern;
		}
		return tipoBolilleroEnumerationSelection;
	}

	public void setUserInput(String value) {
		this.userInput = value;
	}

	public String getUserInput() {
		return userInput;
	}

	public List<TipoBolillero>  autocomplete(Object patternObject) {
		String pattern = patternObject.toString();
		return getItems(pattern);
	}

	@SuppressWarnings("unchecked")
	public TipoBolillero getItem(String pattern){
		if(!Strings.isEmpty(pattern)){
			Query query = entityManager.createQuery("from TipoBolillero where (rtrim(codigo) || '-' || rtrim(descripcion)) = '"+Util.rtrim(pattern)+"'");
			query.setMaxResults(1);
			List<TipoBolillero> list = query.getResultList();
			if(list.size() > 0){
				return (TipoBolillero)(list.get(0));
			}
		}
		return null;
	}


	public String labelFor(TipoBolillero item) {
		if(item == null){
			return "";
		}else{
			return ""
				+  (item.getCodigo() != null ? Util.rtrim(item.getCodigo()) : "" ) 
				+ "-"
				+  (item.getDescripcion() != null ? Util.rtrim(item.getDescripcion()) : "" ) 
				 ;
		}		
	}
	
	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	public TipoBolillero getSelected() {
		return selected;
	}

	public void setSelected(TipoBolillero selected) {
		this.selected = selected;
	}

	public static boolean isAsignacion(TipoBolillero tipoBolillero) {
		return (tipoBolillero == null)
				|| ASIGNACION.equals(tipoBolillero.getCodigo())
				|| RECURSO_EXTRAORDINARIO.equals(tipoBolillero.getCodigo());
	}
}

