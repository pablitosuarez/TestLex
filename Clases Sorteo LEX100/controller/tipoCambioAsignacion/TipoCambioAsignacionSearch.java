package com.base100.lex100.controller.entity.tipoCambioAsignacion;

import com.base100.expand.seam.framework.entity.AbstractEntitySearch;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.TipoCambioAsignacion;
import com.base100.lex100.entity.TipoInstancia;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;



/**
 * Class for TipoCambioAsignacion search filters.
 * 
 * Generated by Expand.
 *
 */
@Name("tipoCambioAsignacionSearch")
@BypassInterceptors
public class TipoCambioAsignacionSearch extends AbstractEntitySearch<TipoCambioAsignacion>
{
	static public final String BY_NATURAL_KEY_STMT = 
		"FROM TipoCambioAsignacion tipoCambioAsignacion WHERE " +
	    "tipoCambioAsignacion.codigo = :codigo";


	private String codigo;
	private String descripcion;
	private TipoInstancia tipoInstancia;
	private Materia materia;

	private TipoCambioAsignacionSearch filter;

	public TipoCambioAsignacionSearch() {
		setDefaultOrder("tipoCambioAsignacion.descripcion");
	}

	@Override
	public void clear() {
		this.codigo = null;
		this.descripcion = null;
		this.tipoInstancia = null;
		this.materia = null;
	}

	@Override
	public String getInitialPanelState()  {
		if (true
			&& this.codigo == null
			&& this.descripcion == null
			&& this.tipoInstancia == null
			&& this.materia == null

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

    public TipoCambioAsignacion findByNaturalKey(String codigo) {
    	return findByNaturalKey (getEntityManager(), codigo); 
	}

    static public TipoCambioAsignacion findByNaturalKey(EntityManager entityManager, String tipoCambioAsignacionCodigo) {
		Query query = entityManager.createQuery(BY_NATURAL_KEY_STMT);
		TipoCambioAsignacion tipoCambioAsignacion = null;
		try {
			query.setParameter("codigo", tipoCambioAsignacionCodigo);
			tipoCambioAsignacion = (TipoCambioAsignacion)query.getSingleResult();
		} catch (Exception e) {
		}
		return tipoCambioAsignacion;
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

    /**
     * Returns the tipoInstancia search filter
     * 
     * @return	the tipoInstancia search filter
     */
    public TipoInstancia getTipoInstancia() {
    	return tipoInstancia;
    }

    /**
     * Sets the tipoInstancia search filter
     * 
     * @param tipoInstancia the tipoInstancia search filter
     */
    public void setTipoInstancia(TipoInstancia tipoInstancia) {
    	this.tipoInstancia = tipoInstancia;
    }
    
   /**
     * Clears the tipoInstancia search filter
     */
    public void clearTipoInstancia() {
    	setTipoInstancia(null);
    }

    /**
     * Returns the materia search filter
     * 
     * @return	the materia search filter
     */
    public Materia getMateria() {
    	return materia;
    }

    /**
     * Sets the materia search filter
     * 
     * @param materia the materia search filter
     */
    public void setMateria(Materia materia) {
    	this.materia = materia;
    }
    
   /**
     * Clears the materia search filter
     */
    public void clearMateria() {
    	setMateria(null);
    }



	public TipoCambioAsignacionSearch getFilter() {
		if (filter == null) {
			filter = new TipoCambioAsignacionSearch();
		}
		return filter;
	}

    @Override
    public void updateFilters() {
		getFilter().setCodigo(getCodigo());
		getFilter().setDescripcion(getDescripcion());
		getFilter().setTipoInstancia(getTipoInstancia());
		getFilter().setMateria(getMateria());
	}


}

