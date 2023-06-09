package com.base100.lex100.controller.entity.sorteoAud;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.base100.expand.seam.framework.entity.AbstractEntitySearch;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.SorteoAud;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoOficina;



/**
 * Class for SorteoAud search filters.
 * 
 * Generated by Expand.
 *
 */
@Name("sorteoAudSearch")
@BypassInterceptors
public class SorteoAudSearch extends AbstractEntitySearch<SorteoAud>
{

	static public final String BY_UUID_STMT = 
		"FROM SorteoAud sorteoAud WHERE " +
	    "sorteoAud.uuid = :uuid";

	static public final String LAST_SORTEO_OPERACION_STMT = 
		"FROM SorteoAud sorteoAud WHERE " +
	    "sorteoAud.idSorteo = :idSorteo and sorteoAud.descripcionModificacion = :operacion order by sorteoAud.fechaModificacion desc";

	private String codigo;
	private String descripcion;
	private String rubros;
	private Camara camara;
	private TipoInstancia tipoInstancia;
	private Competencia competencia;
	private TipoOficina tipoOficina;
	private String usuario;
	private String descripcionModificacion;
	private Date fechaModificacionDesde;
	private Date fechaModificacionHasta;

	private SorteoAudSearch filter;

	public SorteoAudSearch() {
		setDefaultOrder("sorteoAud.fechaModificacion desc");
	}

	@Override
	public void clear() {
		this.codigo = null;
		this.descripcion = null;
		this.rubros = null;
		this.camara = null;
		this.tipoInstancia = null;
		this.competencia = null;
		this.tipoOficina = null;
		this.usuario = null;
		this.descripcionModificacion = null;
		this.fechaModificacionDesde = null;
		this.fechaModificacionHasta = null;
	}

	@Override
	public String getInitialPanelState()  {
		if (true
			&& this.codigo == null
			&& this.descripcion == null
			&& this.rubros == null
			&& this.camara == null
			&& this.tipoInstancia == null
			&& this.competencia == null
			&& this.tipoOficina == null
			&& this.usuario == null
			&& this.descripcionModificacion == null
			&& this.fechaModificacionDesde == null
			&& this.fechaModificacionHasta == null

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


    public SorteoAud findByUuid(String uuid) {
    	return findByUuid (getEntityManager(), uuid); 
	}

    static public SorteoAud findByUuid(EntityManager entityManager, String sorteoAudUuid) {
		Query query = entityManager.createQuery(BY_UUID_STMT);
		SorteoAud sorteoAud = null;
		try {
			query.setParameter("uuid", sorteoAudUuid);
			sorteoAud = (SorteoAud)query.getSingleResult();
		} catch (Exception e) {
		}
		return sorteoAud;
	}

    @SuppressWarnings("unchecked")
	static public SorteoAud findBySorteoOperacion(EntityManager entityManager, Sorteo sorteo, String operacion) {
		Query query = entityManager.createQuery(LAST_SORTEO_OPERACION_STMT);
		SorteoAud sorteoAud = null;
		try {
			List<SorteoAud> results = query.
				setParameter("idSorteo", sorteo.getId()).
				setParameter("operacion", operacion).
				getResultList();
			if (results.size() > 0) {
				sorteoAud = results.get(0);
			}
		} catch (Exception e) {
		}
		return sorteoAud;
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
     * Returns the rubros search filter
     * 
     * @return	the rubros search filter
     */
    public String getRubros() {
    	return rubros;
    }

    /**
     * Sets the rubros search filter
     * 
     * @param rubros the rubros search filter
     */
    public void setRubros(String rubros) {
    	this.rubros = rubros;
    }
    
   /**
     * Clears the rubros search filter
     */
    public void clearRubros() {
    	setRubros(null);
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

    /**
     * Returns the tipoOficina search filter
     * 
     * @return	the tipoOficina search filter
     */
    public TipoOficina getTipoOficina() {
    	return tipoOficina;
    }

    /**
     * Sets the tipoOficina search filter
     * 
     * @param tipoOficina the tipoOficina search filter
     */
    public void setTipoOficina(TipoOficina tipoOficina) {
    	this.tipoOficina = tipoOficina;
    }
    
   /**
     * Clears the tipoOficina search filter
     */
    public void clearTipoOficina() {
    	setTipoOficina(null);
    }

    /**
     * Returns the usuario search filter
     * 
     * @return	the usuario search filter
     */
    public String getUsuario() {
    	return usuario;
    }

    /**
     * Sets the usuario search filter
     * 
     * @param usuario the usuario search filter
     */
    public void setUsuario(String usuario) {
    	this.usuario = usuario;
    }
    
   /**
     * Clears the usuario search filter
     */
    public void clearUsuario() {
    	setUsuario(null);
    }

    /**
     * Returns the descripcionModificacion search filter
     * 
     * @return	the descripcionModificacion search filter
     */
    public String getDescripcionModificacion() {
    	return descripcionModificacion;
    }

    /**
     * Sets the descripcionModificacion search filter
     * 
     * @param descripcionModificacion the descripcionModificacion search filter
     */
    public void setDescripcionModificacion(String descripcionModificacion) {
    	this.descripcionModificacion = descripcionModificacion;
    }
    
   /**
     * Clears the descripcionModificacion search filter
     */
    public void clearDescripcionModificacion() {
    	setDescripcionModificacion(null);
    }



	public SorteoAudSearch getFilter() {
		if (filter == null) {
			filter = new SorteoAudSearch();
		}
		return filter;
	}

    @Override
    public void updateFilters() {
		getFilter().setCodigo(getCodigo());
		getFilter().setDescripcion(getDescripcion());
		getFilter().setRubros(getRubros());
		getFilter().setCamara(getCamara());
		getFilter().setTipoInstancia(getTipoInstancia());
		getFilter().setCompetencia(getCompetencia());
		getFilter().setTipoOficina(getTipoOficina());
		getFilter().setUsuario(getUsuario());
		getFilter().setDescripcionModificacion(getDescripcionModificacion());
		getFilter().setFechaModificacionDesde(getFechaModificacionDesde());
		getFilter().setFechaModificacionHasta(getFechaModificacionHasta());
	}

	public Date getFechaModificacionDesde() {
		return fechaModificacionDesde;
	}

	public void setFechaModificacionDesde(Date fechaModificacionDesde) {
		this.fechaModificacionDesde = fechaModificacionDesde;
	}

	public Date getFechaModificacionHasta() {
		return fechaModificacionHasta;
	}

	public void setFechaModificacionHasta(Date fechaModificacionHasta) {
		this.fechaModificacionHasta = fechaModificacionHasta;
	}

	public Date getFechaModificacionHastaFinal() {
		if (fechaModificacionHasta != null) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(fechaModificacionHasta);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			return calendar.getTime();
		}
		return fechaModificacionHasta;
	}

}

