package com.base100.lex100.controller.entity.turnoOficina;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.base100.expand.seam.framework.entity.AbstractEntitySearch;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoOficina;
import com.base100.lex100.entity.TurnoOficina;



/**
 * Class for TurnoOficina search filters.
 * 
 * Generated by Expand.
 *
 */
@Name("turnoOficinaSearch")
@BypassInterceptors
public class TurnoOficinaSearch extends AbstractEntitySearch<TurnoOficina>
{

	static public final String BY_UUID_STMT = 
		"FROM TurnoOficina turnoOficina WHERE " +
	    "turnoOficina.uuid = :uuid and turnoOficina.status = 0";

	static public final String BY_DATE_STMT = 
		"FROM TurnoOficina turnoOficina WHERE " +
	    "turnoOficina.sorteo = :sorteo and turnoOficina.oficina = :oficina and turnoOficina.desdeFecha <= :desdeFecha and turnoOficina.hastaFecha >= :hastaFecha and turnoOficina.status = 0";


	private Date desdeFecha;
	private Date hastaFecha;
	private String oficinaCodigo;
	private String oficinaDescripcion;
	private String oficinaNombreResponsable;
	private Competencia oficinaCompetencia;
	private TipoInstancia oficinaTipoInstancia;
	private Camara oficinaCamara;
	private TipoOficina oficinaTipoOficina;


	private TurnoOficinaSearch filter;

	public TurnoOficinaSearch() {
		setDefaultOrder("turnoOficina.id");
	}

	@Override
	public void clear() {
		this.desdeFecha = null;
		this.hastaFecha = null;
		this.oficinaCodigo = null;
		this.oficinaDescripcion = null;
		this.oficinaNombreResponsable = null;
		this.oficinaCompetencia = null;
		this.oficinaTipoInstancia = null;
		this.oficinaCamara = null;
		this.oficinaTipoOficina = null;
	}

	@Override
	public String getInitialPanelState()  {
		if (true
			&& this.desdeFecha == null
			&& this.hastaFecha == null
			&& this.oficinaCodigo == null
			&& this.oficinaDescripcion == null
			&& this.oficinaNombreResponsable == null
			&& this.oficinaCompetencia == null
			&& this.oficinaTipoInstancia == null
			&& this.oficinaCamara == null
			&& this.oficinaTipoOficina == null

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


    public TurnoOficina findByUuid(String uuid) {
    	return findByUuid (getEntityManager(), uuid); 
	}

    static public TurnoOficina findByUuid(EntityManager entityManager, String turnoOficinaUuid) {
		Query query = entityManager.createQuery(BY_UUID_STMT);
		TurnoOficina turnoOficina = null;
		try {
			query.setParameter("uuid", turnoOficinaUuid);
			turnoOficina = (TurnoOficina)query.getSingleResult();
		} catch (Exception e) {
		}
		return turnoOficina;
	}

    static public TurnoOficina findByDate(EntityManager entityManager, Sorteo sorteo, Oficina oficina,Date fecha) {
		Query query = entityManager.createQuery(BY_DATE_STMT);
		TurnoOficina turnoOficina = null;
		try {
			query.setParameter("sorteo", sorteo).
			setParameter("oficina", oficina).
			setParameter("desdeFecha", fecha).
			setParameter("hastaFecha", fecha);
			turnoOficina = (TurnoOficina)query.getSingleResult();
		} catch (Exception e) {
		}
		return turnoOficina;
	}

    static public boolean isOficinaDeTurno(EntityManager entityManager, Sorteo sorteo, Oficina oficina, Date dia) {
    	return findByDate(entityManager, sorteo, oficina, dia) != null;
    }
    
    /**
     * Returns the desdeFecha search filter
     * 
     * @return	the desdeFecha search filter
     */
    public Date getDesdeFecha() {
    	return desdeFecha;
    }

    /**
     * Sets the desdeFecha search filter
     * 
     * @param desdeFecha the desdeFecha search filter
     */
    public void setDesdeFecha(Date desdeFecha) {
    	this.desdeFecha = desdeFecha;
    }
    
   /**
     * Clears the desdeFecha search filter
     */
    public void clearDesdeFecha() {
    	setDesdeFecha(null);
    }

    /**
     * Returns the hastaFecha search filter
     * 
     * @return	the hastaFecha search filter
     */
    public Date getHastaFecha() {
    	return hastaFecha;
    }

    /**
     * Sets the hastaFecha search filter
     * 
     * @param hastaFecha the hastaFecha search filter
     */
    public void setHastaFecha(Date hastaFecha) {
    	this.hastaFecha = hastaFecha;
    }
    
   /**
     * Clears the hastaFecha search filter
     */
    public void clearHastaFecha() {
    	setHastaFecha(null);
    }

    /**
     * Returns the oficinaCodigo search filter
     * 
     * @return	the oficinaCodigo search filter
     */
    public String getOficinaCodigo() {
    	return oficinaCodigo;
    }

    /**
     * Sets the oficinaCodigo search filter
     * 
     * @param oficinaCodigo the oficinaCodigo search filter
     */
    public void setOficinaCodigo(String oficinaCodigo) {
    	this.oficinaCodigo = oficinaCodigo;
    }
    
   /**
     * Clears the oficinaCodigo search filter
     */
    public void clearOficinaCodigo() {
    	setOficinaCodigo(null);
    }

    /**
     * Returns the oficinaDescripcion search filter
     * 
     * @return	the oficinaDescripcion search filter
     */
    public String getOficinaDescripcion() {
    	return oficinaDescripcion;
    }

    /**
     * Sets the oficinaDescripcion search filter
     * 
     * @param oficinaDescripcion the oficinaDescripcion search filter
     */
    public void setOficinaDescripcion(String oficinaDescripcion) {
    	this.oficinaDescripcion = oficinaDescripcion;
    }
    
   /**
     * Clears the oficinaDescripcion search filter
     */
    public void clearOficinaDescripcion() {
    	setOficinaDescripcion(null);
    }

    /**
     * Returns the oficinaNombreResponsable search filter
     * 
     * @return	the oficinaNombreResponsable search filter
     */
    public String getOficinaNombreResponsable() {
    	return oficinaNombreResponsable;
    }

    /**
     * Sets the oficinaNombreResponsable search filter
     * 
     * @param oficinaNombreResponsable the oficinaNombreResponsable search filter
     */
    public void setOficinaNombreResponsable(String oficinaNombreResponsable) {
    	this.oficinaNombreResponsable = oficinaNombreResponsable;
    }
    
   /**
     * Clears the oficinaNombreResponsable search filter
     */
    public void clearOficinaNombreResponsable() {
    	setOficinaNombreResponsable(null);
    }

    /**
     * Returns the oficinaCompetencia search filter
     * 
     * @return	the oficinaCompetencia search filter
     */
    public Competencia getOficinaCompetencia() {
    	return oficinaCompetencia;
    }

    /**
     * Sets the oficinaCompetencia search filter
     * 
     * @param oficinaCompetencia the oficinaCompetencia search filter
     */
    public void setOficinaCompetencia(Competencia oficinaCompetencia) {
    	this.oficinaCompetencia = oficinaCompetencia;
    }
    
   /**
     * Clears the oficinaCompetencia search filter
     */
    public void clearOficinaCompetencia() {
    	setOficinaCompetencia(null);
    }

    /**
     * Returns the oficinaTipoInstancia search filter
     * 
     * @return	the oficinaTipoInstancia search filter
     */
    public TipoInstancia getOficinaTipoInstancia() {
    	return oficinaTipoInstancia;
    }

    /**
     * Sets the oficinaTipoInstancia search filter
     * 
     * @param oficinaTipoInstancia the oficinaTipoInstancia search filter
     */
    public void setOficinaTipoInstancia(TipoInstancia oficinaTipoInstancia) {
    	this.oficinaTipoInstancia = oficinaTipoInstancia;
    }
    
   /**
     * Clears the oficinaTipoInstancia search filter
     */
    public void clearOficinaTipoInstancia() {
    	setOficinaTipoInstancia(null);
    }

    /**
     * Returns the oficinaCamara search filter
     * 
     * @return	the oficinaCamara search filter
     */
    public Camara getOficinaCamara() {
    	return oficinaCamara;
    }

    /**
     * Sets the oficinaCamara search filter
     * 
     * @param oficinaCamara the oficinaCamara search filter
     */
    public void setOficinaCamara(Camara oficinaCamara) {
    	this.oficinaCamara = oficinaCamara;
    }
    
   /**
     * Clears the oficinaCamara search filter
     */
    public void clearOficinaCamara() {
    	setOficinaCamara(null);
    }

    /**
     * Returns the oficinaTipoOficina search filter
     * 
     * @return	the oficinaTipoOficina search filter
     */
    public TipoOficina getOficinaTipoOficina() {
    	return oficinaTipoOficina;
    }

    /**
     * Sets the oficinaTipoOficina search filter
     * 
     * @param oficinaTipoOficina the oficinaTipoOficina search filter
     */
    public void setOficinaTipoOficina(TipoOficina oficinaTipoOficina) {
    	this.oficinaTipoOficina = oficinaTipoOficina;
    }
    
   /**
     * Clears the oficinaTipoOficina search filter
     */
    public void clearOficinaTipoOficina() {
    	setOficinaTipoOficina(null);
    }




	public TurnoOficinaSearch getFilter() {
		if (filter == null) {
			filter = new TurnoOficinaSearch();
		}
		return filter;
	}

    @Override
    public void updateFilters() {
		getFilter().setDesdeFecha(getDesdeFecha());
		getFilter().setHastaFecha(getHastaFecha());
		getFilter().setOficinaCodigo(getOficinaCodigo());
		getFilter().setOficinaDescripcion(getOficinaDescripcion());
		getFilter().setOficinaNombreResponsable(getOficinaNombreResponsable());
		getFilter().setOficinaCompetencia(getOficinaCompetencia());
		getFilter().setOficinaTipoInstancia(getOficinaTipoInstancia());
		getFilter().setOficinaCamara(getOficinaCamara());
		getFilter().setOficinaTipoOficina(getOficinaTipoOficina());

	}


}
