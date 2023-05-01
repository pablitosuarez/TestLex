package com.base100.lex100.controller.entity.tipoAsignacionCodificado;

import java.util.List;
import java.util.ArrayList;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.entity.TipoAsignacion;
import com.base100.lex100.component.audit.AbstractLogicalDeletionEntityList;
import com.base100.expand.seam.framework.entity.AbstractEntityHome;
import com.base100.expand.seam.framework.entity.AbstractEntitySearch;

/**
 * Class for TipoAsignacionCodificado List objects.
 * 
 * Generated by Expand.
 *
 */
@Name("tipoAsignacionCodificadoList")
public class TipoAsignacionCodificadoList extends AbstractLogicalDeletionEntityList<TipoAsignacion>
{
	@In(create=true)
	private Configuration configuration;
    @In(create=true)
    private TipoAsignacionCodificadoSearch tipoAsignacionCodificadoSearch;
    @In(create=true)
    private TipoAsignacionCodificadoHome tipoAsignacionCodificadoHome;
    private static final String[] FILTERS = {
		"lower(tipoAsignacion.codigo) like #{likePattern[tipoAsignacionCodificadoSearch.filter.codigo]}",
		"lower(tipoAsignacion.descripcion) like #{likePattern[tipoAsignacionCodificadoSearch.filter.descripcion]}",
		"tipoAsignacion.materia = #{tipoAsignacionCodificadoSearch.filter.materia}",
		"tipoAsignacion.tipoInstancia = #{tipoAsignacionCodificadoSearch.filter.tipoInstancia}",
		"lower(tipoAsignacion.categoria) like #{likePattern[tipoAsignacionCodificadoSearch.filter.categoria]}",

    };
	
    private static String[] byMateriaRestrictions ={
		"tipoAsignacion.materia = #{materiaHome.isIdDefined() ? materiaHome.instance : null}"
    };
    
    public List<TipoAsignacion> getbyMateriaResultList(){
    	return getResultList(byMateriaRestrictions);
    }

    public List<TipoAsignacion> getbyMateriaAllResultList(){
    	return getAllResults(byMateriaRestrictions);
    }
    private static String[] byTipoInstanciaRestrictions ={
		"tipoAsignacion.tipoInstancia = #{tipoInstanciaHome.isIdDefined() ? tipoInstanciaHome.instance : null}"
    };
    
    public List<TipoAsignacion> getbyTipoInstanciaResultList(){
    	return getResultList(byTipoInstanciaRestrictions);
    }

    public List<TipoAsignacion> getbyTipoInstanciaAllResultList(){
    	return getAllResults(byTipoInstanciaRestrictions);
    }

    @Override
    public String getEjbql() 
    { 
        return "select tipoAsignacion from TipoAsignacion tipoAsignacion where categoria is not null";
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
    
	public AbstractEntityHome<TipoAsignacion> getEntityHome() {
		return tipoAsignacionCodificadoHome;
	}

	public AbstractEntitySearch<TipoAsignacion> getEntitySearch() {
		return tipoAsignacionCodificadoSearch;
	}
	
	/* (non-Javadoc)
	 * @see com.base100.expand.seam.framework.entity.AbstractEntityList#doSelection(java.lang.Object, java.lang.String)
	 */
	public String doSelection (TipoAsignacion tipoAsignacion, String page) {
		return super.doSelection(tipoAsignacion, page);
	}

	public String doSelection (TipoAsignacion tipoAsignacion, String page, String returnPage) {
		page = doSelection(tipoAsignacion, page);
		if (returnPage != null) {
			getEntitySearch().setReturnPage(returnPage);
		}
		return page;
	}



}
