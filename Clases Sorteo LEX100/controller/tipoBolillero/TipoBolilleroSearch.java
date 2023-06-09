package com.base100.lex100.controller.entity.tipoBolillero;

import com.base100.lex100.component.enumeration.TipoBolilleroEnumeration;
import com.base100.lex100.entity.TipoBolillero;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;


import com.base100.expand.seam.framework.entity.AbstractEntitySearch;

/**
 * Class for TipoBolillero search filters.
 * 
 * Generated by Expand.
 *
 */
@Name("tipoBolilleroSearch")
@BypassInterceptors
public class TipoBolilleroSearch extends AbstractEntitySearch<TipoBolillero>
{

	static public final String BY_UUID_STMT = 
		"FROM TipoBolillero tipoBolillero WHERE " +
	    "tipoBolillero.uuid = :uuid";

	static public final String BY_CODIGO_STMT = 
		"FROM TipoBolillero tipoBolillero WHERE " +
	    "tipoBolillero.codigo = :codigo and status <> -1";


	private String codigo;
	private String descripcion;

	private TipoBolilleroSearch filter;

	public TipoBolilleroSearch() {
		setDefaultOrder("tipoBolillero.codigo, tipoBolillero.descripcion");
	}

	@Override
	public void clear() {
		this.codigo = null;
		this.descripcion = null;
	}

	@Override
	public String getInitialPanelState()  {
		if (true
			&& this.codigo == null
			&& this.descripcion == null

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


    public TipoBolillero findByUuid(String uuid) {
    	return findByUuid (getEntityManager(), uuid); 
	}

    static public TipoBolillero findByUuid(EntityManager entityManager, String tipoBolilleroUuid) {
		Query query = entityManager.createQuery(BY_UUID_STMT);
		TipoBolillero tipoBolillero = null;
		try {
			query.setParameter("uuid", tipoBolilleroUuid);
			tipoBolillero = (TipoBolillero)query.getSingleResult();
		} catch (Exception e) {
		}
		return tipoBolillero;
	}

    public TipoBolillero findByCodigo(String codigo) {
    	return findByCodigo (getEntityManager(), codigo); 
	}

    static public TipoBolillero findByCodigo(EntityManager entityManager, String tipoBolilleroCodigo) {
		Query query = entityManager.createQuery(BY_CODIGO_STMT);
		TipoBolillero tipoBolillero = null;
		try {
			query.setParameter("codigo", tipoBolilleroCodigo);
			tipoBolillero = (TipoBolillero)query.getSingleResult();
		} catch (Exception e) {
		}
		return tipoBolillero;
	}

	public static TipoBolillero getTipoBolilleroAsignacion(EntityManager entityManager) {
		return findByCodigo(entityManager, TipoBolilleroEnumeration.ASIGNACION);
	}


    /**
     * Returns the codigo search filter
     * 
     * @return	the codigo search filter
     */
    public String getCodigo() {
    	return codigo;
    }

    /**
     * Sets the codigo search filter
     * 
     * @param codigo the codigo search filter
     */
    public void setCodigo(String codigo) {
    	this.codigo = codigo;
    }
    
   /**
     * Clears the codigo search filter
     */
    public void clearCodigo() {
    	setCodigo(null);
    }

    /**
     * Returns the descripcion search filter
     * 
     * @return	the descripcion search filter
     */
    public String getDescripcion() {
    	return descripcion;
    }

    /**
     * Sets the descripcion search filter
     * 
     * @param descripcion the descripcion search filter
     */
    public void setDescripcion(String descripcion) {
    	this.descripcion = descripcion;
    }
    
   /**
     * Clears the descripcion search filter
     */
    public void clearDescripcion() {
    	setDescripcion(null);
    }



	public TipoBolilleroSearch getFilter() {
		if (filter == null) {
			filter = new TipoBolilleroSearch();
		}
		return filter;
	}

    @Override
    public void updateFilters() {
    	super.updateFilters();
		getFilter().setCodigo(getCodigo());
		getFilter().setDescripcion(getDescripcion());
	}


}

