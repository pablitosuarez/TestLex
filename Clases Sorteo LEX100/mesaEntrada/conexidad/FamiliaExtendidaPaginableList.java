package com.base100.lex100.mesaEntrada.conexidad;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.Controller;

import com.base100.expand.seam.framework.IListPaginator;
import com.base100.expand.seam.framework.IPaginable;
import com.base100.expand.seam.framework.ListPaginator;
import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.entity.Expediente;


@Name("familiaExtendidaPaginableList")
@Scope(ScopeType.CONVERSATION)
public class FamiliaExtendidaPaginableList extends Controller implements IPaginable{

	private static final long serialVersionUID = -6998882517860250686L;

	private Integer firstResult;
	private IListPaginator paginator;
	
	
	public IListPaginator getPaginator() {
		this.paginator = new ListPaginator(this);
		return this.paginator;
	}
	
	@Override
	public Long getResultCount() {
		return (long) getFamiliaExtendidaList().size();
	}


	@Override
	public Integer getMaxResults() {
		return 20;
	}


	@Override
	public Integer getFirstResult() {
		return firstResult;
	}


	@Override
	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;		
	}
	
	
	public List<Expediente> getResultList() {
		List<Expediente> familiaList = getFamiliaExtendidaList();
		int firstResult = getFirstResult() == null ? 0 : getFirstResult();
		if (getMaxResults() != null) {
			return familiaList.subList(firstResult, Math.min(firstResult + getMaxResults(), familiaList.size()));
		} else {
			return familiaList;
		}
	}

	public List<Expediente> getAllResultList() {
		return getFamiliaExtendidaList();
	}
	
	private List<Expediente> getFamiliaExtendidaList(){
		return FamiliaManager.instance().getFamiliaExpedienteExtendida(ExpedienteHome.instance().getInstance());
	}
	
	
}
