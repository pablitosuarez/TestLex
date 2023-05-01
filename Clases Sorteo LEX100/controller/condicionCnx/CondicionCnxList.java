package com.base100.lex100.controller.entity.condicionCnx;

import java.util.List;
import java.util.ArrayList;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.entity.CondicionCnx;
import com.base100.lex100.component.audit.AbstractLogicalDeletionEntityList;
import com.base100.expand.seam.framework.entity.AbstractEntityHome;
import com.base100.expand.seam.framework.entity.AbstractEntitySearch;

/**
 * Class for CondicionCnx List objects.
 * 
 * Generated by Expand.
 *
 */
@Name("condicionCnxList")
public class CondicionCnxList extends AbstractLogicalDeletionEntityList<CondicionCnx>
{
	@In(create=true)
	private Configuration configuration;
    @In(create=true)
    private CondicionCnxSearch condicionCnxSearch;
    @In(create=true)
    private CondicionCnxHome condicionCnxHome;
    private static final String[] FILTERS = {
		"lower(condicionCnx.parametro) like #{likePattern[condicionCnxSearch.filter.parametro]}",
		"lower(condicionCnx.tipoComparacion) like #{likePattern[condicionCnxSearch.filter.tipoComparacion]}",

    };
	
    private static String[] byCriterioCnxRestrictions ={
		"condicionCnx.criterioCnx = #{criterioCnxHome.isIdDefined() ? criterioCnxHome.instance : null}"
    };
    
    public List<CondicionCnx> getbyCriterioCnxResultList(){
    	return getResultList(byCriterioCnxRestrictions);
    }

    public List<CondicionCnx> getbyCriterioCnxAllResultList(){
    	return getAllResults(byCriterioCnxRestrictions);
    }

    @SuppressWarnings("unchecked")
    public List<CondicionCnx> getbyCriterioCnxResultListWithNew(){
		List<CondicionCnx> list = new ArrayList(getResultList(byCriterioCnxRestrictions));
		list.add(getEntityHome().getEmptyInstance());
		return list;
	}

    public List<CondicionCnx> getbyParentAllResults(){
    	return getbyCriterioCnxAllResultList();
    }

    @Override
    public String getEjbql() 
    { 
        return "select condicionCnx from CondicionCnx condicionCnx";
    }

    @Override
    public String[] getFilters()
    {
        return FILTERS;
    }
    
    @Override
    public Integer getRowsPerPage()
    {
    	return configuration.getRowsPerPage();
    }
    
	public AbstractEntityHome<CondicionCnx> getEntityHome() {
		return condicionCnxHome;
	}

	public AbstractEntitySearch<CondicionCnx> getEntitySearch() {
		return condicionCnxSearch;
	}
	
	/* (non-Javadoc)
	 * @see com.base100.expand.seam.framework.entity.AbstractEntityList#doSelection(java.lang.Object, java.lang.String)
	 */
	public String doSelection (CondicionCnx condicionCnx, String page) {
		return super.doSelection(condicionCnx, page);
	}

	public String doSelection (CondicionCnx condicionCnx, String page, String returnPage) {
		page = doSelection(condicionCnx, page);
		if (returnPage != null) {
			getEntitySearch().setReturnPage(returnPage);
		}
		return page;
	}

	/* (non-Javadoc)
	 * @see com.base100.expand.seam.framework.entity.AbstractEntityList#doLineSelection(java.lang.Object, java.lang.String, java.lang.String)
	 */
	public String doLineSelection(CondicionCnx condicionCnx, String toPage, String fromPage) {
		return super.doLineSelection(condicionCnx, toPage, fromPage);
	}	


}
