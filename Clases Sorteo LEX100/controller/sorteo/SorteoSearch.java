package com.base100.lex100.controller.entity.sorteo;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.base100.expand.seam.framework.entity.AbstractEntitySearch;
import com.base100.lex100.component.enumeration.TurnoSorteoEnumeration;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.TipoInstancia;



/**
 * Class for Sorteo search filters.
 * 
 * Generated by Expand.
 *
 */
@Name("sorteoSearch")
@BypassInterceptors
public class SorteoSearch extends AbstractEntitySearch<Sorteo>
{

	static public final String BY_CODIGO_STMT = 
		"FROM Sorteo sorteo WHERE " +
	    "sorteo.codigo = :codigo and sorteo.camara = #{sessionState.globalCamara} and sorteo.status = 0";

	static public final String BY_UUID_STMT = 
		"FROM Sorteo sorteo WHERE " +
	    "sorteo.uuid = :uuid";

	private String codigo;
	private String descripcion;
	private Camara camara;
	private TipoInstancia tipoInstancia;
	private Competencia competencia;
	
    private Integer conTurno;
    private Integer conTurnoDistinct;

	private SorteoSearch filter;

	public SorteoSearch() {
		setDefaultOrder("sorteo.codigo");
	}

	@Override
	public void clear() {
		this.codigo = null;
		this.descripcion = null;
		this.conTurno = null;
		this.conTurnoDistinct = null;
		this.camara = null;
		this.tipoInstancia = null;
		this.competencia = null;
	}

	@Override
	public String getInitialPanelState()  {
		if (true
			&& this.codigo == null
			&& this.descripcion == null
			&& this.conTurno == null
			&& this.conTurnoDistinct == null
			&& this.camara == null
			&& this.tipoInstancia == null
			&& this.competencia == null

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


    public Sorteo findByUuid(String uuid) {
    	return findByUuid (getEntityManager(), uuid); 
	}

    static public Sorteo findByUuid(EntityManager entityManager, String sorteoUuid) {
		Query query = entityManager.createQuery(BY_UUID_STMT);
		Sorteo sorteo = null;
		try {
			query.setParameter("uuid", sorteoUuid);
			sorteo = (Sorteo)query.getSingleResult();
		} catch (Exception e) {
		}
		return sorteo;
	}

    public Sorteo findByCodigo(String codigo) {
    	return findByCodigo (getEntityManager(), codigo); 
	}

    static public Sorteo findByCodigo(EntityManager entityManager, String codigo) {
		Query query = entityManager.createQuery(BY_CODIGO_STMT);
		Sorteo sorteo = null;
		try {
			query.setParameter("codigo", codigo);
			sorteo = (Sorteo)query.getSingleResult();
		} catch (Exception e) {
		}
		return sorteo;
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
     * Returns the camara search filter
     * 
     * @return	the camara search filter
     */
    public Camara getCamara() {
    	return camara;
    }

    /**
     * Sets the camara search filter
     * 
     * @param camara the camara search filter
     */
    public void setCamara(Camara camara) {
    	this.camara = camara;
    }
    
   /**
     * Clears the camara search filter
     */
    public void clearCamara() {
    	setCamara(null);
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
     * Returns the competencia search filter
     * 
     * @return	the competencia search filter
     */
    public Competencia getCompetencia() {
    	return competencia;
    }

    /**
     * Sets the competencia search filter
     * 
     * @param competencia the competencia search filter
     */
    public void setCompetencia(Competencia competencia) {
    	this.competencia = competencia;
    }
    
   /**
     * Clears the competencia search filter
     */
    public void clearCompetencia() {
    	setCompetencia(null);
    }



	public SorteoSearch getFilter() {
		if (filter == null) {
			filter = new SorteoSearch();
		}
		return filter;
	}

	public Integer getConTurno() {
		return conTurno;
	}

	public void setConTurno(Integer conTurno) {
		this.conTurno = conTurno;
	}

	public Integer getConTurnoDistinct() {
		return conTurnoDistinct;
	}

	public void setConTurnoDistinct(Integer conTurnoDistinct) {
		this.conTurnoDistinct = conTurnoDistinct;
	}

	public void filterByConTurnoManual() {
		setConTurnoDistinct(null);
		setConTurno((Integer)TurnoSorteoEnumeration.Type.conTurnoManual.getValue());
		updateFilters();
	}

	public void filterByConTurno() {
		setConTurno(null);
		setConTurnoDistinct((Integer)TurnoSorteoEnumeration.Type.sinTurno.getValue());
		updateFilters();
	}

	@Override
    public void updateFilters() {
		getFilter().setCodigo(getCodigo());
		getFilter().setDescripcion(getDescripcion());
		getFilter().setCamara(getCamara());
		getFilter().setTipoInstancia(getTipoInstancia());
		getFilter().setCompetencia(getCompetencia());
		getFilter().setConTurno(getConTurno());
		getFilter().setConTurnoDistinct(getConTurnoDistinct());
	}


}
