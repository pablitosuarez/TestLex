package com.base100.lex100.controller.entity.conexidadLog;

import java.util.List;
import java.util.ArrayList;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.entity.ConexidadLog;
import com.base100.expand.seam.framework.entity.AbstractEntityList;
import com.base100.expand.seam.framework.entity.AbstractEntityHome;
import com.base100.expand.seam.framework.entity.AbstractEntitySearch;

/**
 * Class for ConexidadLog List objects.
 * 
 * Generated by Expand.
 *
 */
@Name("conexidadLogList")
public class ConexidadLogList extends AbstractEntityList<ConexidadLog>
{
	@In(create=true)
	private Configuration configuration;
    @In(create=true)
    private ConexidadLogSearch conexidadLogSearch;
    @In(create=true)
    private ConexidadLogHome conexidadLogHome;
    private static final String[] FILTERS = {
		"conexidadLog.camara = #{sessionState.globalCamara}",
		"lower(conexidadLog.informacionConexidad) like #{likePattern[conexidadLogSearch.filter.informacionConexidad]}",
		"conexidadLog.fechaComienzo >= #{conexidadLogSearch.filter.fromFechaComienzo}",
		"conexidadLog.fechaComienzo <= #{conexidadLogSearch.filter.toFechaComienzo}",
		"conexidadLog.fechaFin >= #{conexidadLogSearch.filter.fromFechaFin}",
		"conexidadLog.fechaFin <= #{conexidadLogSearch.filter.toFechaFin}",
		"conexidadLog.intervalo > #{conexidadLogSearch.filter.minIntervalo}",
		"conexidadLog.camara = #{conexidadLogSearch.filter.camara}",
		"conexidadLog.expediente.numero = #{conexidadLogSearch.filter.numeroExpediente}",
		"conexidadLog.expediente.anio = #{conexidadLogSearch.filter.anioExpediente}",
		"lower(conexidadLog.expediente.clave) like #{likePattern[conexidadLogSearch.filter.claveExpediente]}",
		"conexidadLog.expediente = #{conexidadLogSearch.filter.expediente}",
		"lower(conexidadLog.tipoRadicacion) like #{likePattern[conexidadLogSearch.filter.tipoRadicacion]}",

    };
	
    private static String[] byCamaraRestrictions ={
		"conexidadLog.camara = #{camaraHome.isIdDefined() ? camaraHome.instance : null}"
    };
    
    public List<ConexidadLog> getbyCamaraResultList(){
    	return getResultList(byCamaraRestrictions);
    }

    public List<ConexidadLog> getbyCamaraAllResultList(){
    	return getAllResults(byCamaraRestrictions);
    }
    private static String[] byExpedienteRestrictions ={
		"conexidadLog.expediente = #{expedienteHome.isIdDefined() ? expedienteHome.instance : null}"
    };
    
    public List<ConexidadLog> getbyExpedienteResultList(){
    	return getResultList(byExpedienteRestrictions);
    }

    public List<ConexidadLog> getbyExpedienteAllResultList(){
    	return getAllResults(byExpedienteRestrictions);
    }

    @Override
    public String getEjbql() 
    { 
        return "select conexidadLog from ConexidadLog conexidadLog";
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
    
	public AbstractEntityHome<ConexidadLog> getEntityHome() {
		return conexidadLogHome;
	}

	public AbstractEntitySearch<ConexidadLog> getEntitySearch() {
		return conexidadLogSearch;
	}
	
	/* (non-Javadoc)
	 * @see com.base100.expand.seam.framework.entity.AbstractEntityList#doSelection(java.lang.Object, java.lang.String)
	 */
	public String doSelection (ConexidadLog conexidadLog, String page) {
		return super.doSelection(conexidadLog, page);
	}

	public String doSelection (ConexidadLog conexidadLog, String page, String returnPage) {
		page = doSelection(conexidadLog, page);
		if (returnPage != null) {
			getEntitySearch().setReturnPage(returnPage);
		}
		return page;
	}



}
