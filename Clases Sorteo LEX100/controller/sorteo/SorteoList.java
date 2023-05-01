package com.base100.lex100.controller.entity.sorteo;

import java.util.List;
import java.util.ArrayList;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.component.enumeration.TurnoSorteoEnumeration;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.component.audit.AbstractLogicalDeletionEntityList;
import com.base100.expand.seam.framework.entity.AbstractEntityHome;
import com.base100.expand.seam.framework.entity.AbstractEntitySearch;

/**
 * Class for Sorteo List objects.
 * 
 * Generated by Expand.
 *
 */
@Name("sorteoList")
public class SorteoList extends AbstractLogicalDeletionEntityList<Sorteo>
{
	@In(create=true)
	private Configuration configuration;
    @In(create=true)
    private SorteoSearch sorteoSearch;
    @In(create=true)
    private SorteoHome sorteoHome;
    private static final String[] FILTERS = {
		"sorteo.camara = #{sessionState.globalCamara}",
		"lower(sorteo.codigo) like #{likePattern[sorteoSearch.filter.codigo]}",
		"lower(sorteo.descripcion) like #{likePattern[sorteoSearch.filter.descripcion]}",
		"sorteo.conTurno = #{sorteoSearch.filter.conTurno}",
		"sorteo.conTurno <> #{sorteoSearch.filter.conTurnoDistinct}",
		"sorteo.camara = #{sorteoSearch.filter.camara}",
		"sorteo.tipoInstancia = #{sorteoSearch.filter.tipoInstancia}",
		"sorteo.competencia = #{sorteoSearch.filter.competencia}",
    };
	
    private static String[] byCamaraRestrictions ={
		"sorteo.camara = #{camaraHome.isIdDefined() ? camaraHome.instance : null}"
    };
    
    public List<Sorteo> getbyCamaraResultList(){
    	return getResultList(byCamaraRestrictions);
    }

    public List<Sorteo> getbyCamaraAllResultList(){
    	return getAllResults(byCamaraRestrictions);
    }

    private static String[] byTipoInstanciaRestrictions ={
		"sorteo.tipoInstancia = #{tipoInstanciaHome.isIdDefined() ? tipoInstanciaHome.instance : null}"
    };

    public List<Sorteo> getbyTipoInstanciaResultList(){
    	return getResultList(byTipoInstanciaRestrictions);
    }

    public List<Sorteo> getbyTipoInstanciaAllResultList(){
    	return getAllResults(byTipoInstanciaRestrictions);
    }

    private static String[] byCompetenciaRestrictions ={
		"sorteo.competencia = #{competenciaHome.isIdDefined() ? competenciaHome.instance : null}"
    };
    
    public List<Sorteo> getbyCompetenciaResultList(){
    	return getResultList(byCompetenciaRestrictions);
    }

    public List<Sorteo> getbyCompetenciaAllResultList(){
    	return getAllResults(byCompetenciaRestrictions);
    }

    public List<Sorteo> getConTurnoManualResultList(){
    	sorteoSearch.filterByConTurnoManual();
    	return getResultList();
    }

    public List<Sorteo> getConTurnoManualAllResultList(){
    	sorteoSearch.filterByConTurnoManual();
    	return getAllResults();
    }

    public List<Sorteo> getConTurnoResultList(){
    	sorteoSearch.filterByConTurno();
    	return getResultList();
    }

    public List<Sorteo> getConTurnoAllResultList(){
    	sorteoSearch.filterByConTurno();
    	return getAllResults();
    }

    @Override
    public String getEjbql() 
    { 
        return "select sorteo from Sorteo sorteo";
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
    
	public AbstractEntityHome<Sorteo> getEntityHome() {
		return sorteoHome;
	}

	public AbstractEntitySearch<Sorteo> getEntitySearch() {
		return sorteoSearch;
	}
	
	/* (non-Javadoc)
	 * @see com.base100.expand.seam.framework.entity.AbstractEntityList#doSelection(java.lang.Object, java.lang.String)
	 */
	public String doSelection (Sorteo sorteo, String page) {
		return super.doSelection(sorteo, page);
	}

	public String doSelection (Sorteo sorteo, String page, String returnPage) {
		page = doSelection(sorteo, page);
		if (returnPage != null) {
			getEntitySearch().setReturnPage(returnPage);
		}
		return page;
	}

}
