package com.base100.lex100.controller.entity.condicionCnx;

import com.base100.expand.seam.framework.entity.AbstractEntitySearch;
import com.base100.lex100.entity.CondicionCnx;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;



/**
 * Class for CondicionCnx search filters.
 * 
 * Generated by Expand.
 *
 */
@Name("condicionCnxSearch")
@BypassInterceptors
public class CondicionCnxSearch extends AbstractEntitySearch<CondicionCnx>
{

	static public final String BY_UUID_STMT = 
		"FROM CondicionCnx condicionCnx WHERE " +
	    "condicionCnx.uuid = :uuid";

	private String parametro;
	private String tipoComparacion;

	private CondicionCnxSearch filter;

	public CondicionCnxSearch() {
		setDefaultOrder("condicionCnx.id");
	}

	@Override
	public void clear() {
		this.parametro = null;
		this.tipoComparacion = null;
	}

	@Override
	public String getInitialPanelState()  {
		if (true
			&& this.parametro == null
			&& this.tipoComparacion == null

	    ){
			return getPanelState();
		} else {
			return "open";
		}
	}

	@Override
	public boolean hasActiveSearch() {
		// TODO Auto-generated method stub
		return false;
	}


    public CondicionCnx findByUuid(String uuid) {
    	return findByUuid (getEntityManager(), uuid); 
	}

    static public CondicionCnx findByUuid(EntityManager entityManager, String condicionCnxUuid) {
		Query query = entityManager.createQuery(BY_UUID_STMT);
		CondicionCnx condicionCnx = null;
		try {
			query.setParameter("uuid", condicionCnxUuid);
			condicionCnx = (CondicionCnx)query.getSingleResult();
		} catch (Exception e) {
		}
		return condicionCnx;
	}

    /**
     * Returns the parametro search filter
     * 
     * @return	the parametro search filter
     */
    public String getParametro() {
    	return parametro;
    }

    /**
     * Sets the parametro search filter
     * 
     * @param parametro the parametro search filter
     */
    public void setParametro(String parametro) {
    	this.parametro = parametro;
    }
    
   /**
     * Clears the parametro search filter
     */
    public void clearParametro() {
    	setParametro(null);
    }

    /**
     * Returns the tipoComparacion search filter
     * 
     * @return	the tipoComparacion search filter
     */
    public String getTipoComparacion() {
    	return tipoComparacion;
    }

    /**
     * Sets the tipoComparacion search filter
     * 
     * @param tipoComparacion the tipoComparacion search filter
     */
    public void setTipoComparacion(String tipoComparacion) {
    	this.tipoComparacion = tipoComparacion;
    }
    
   /**
     * Clears the tipoComparacion search filter
     */
    public void clearTipoComparacion() {
    	setTipoComparacion(null);
    }



	public CondicionCnxSearch getFilter() {
		if (filter == null) {
			filter = new CondicionCnxSearch();
		}
		return filter;
	}

    @Override
    public void updateFilters() {
		getFilter().setParametro(getParametro());
		getFilter().setTipoComparacion(getTipoComparacion());
	}


}
