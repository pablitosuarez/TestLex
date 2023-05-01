package com.base100.lex100.controller.entity.conexidadLog;

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.ConexidadLog;
import com.base100.lex100.entity.Expediente;
import java.util.Date;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;


import com.base100.expand.seam.framework.entity.AbstractEntitySearch;

/**
 * Class for ConexidadLog search filters.
 * 
 * Generated by Expand.
 *
 */
@Name("conexidadLogSearch")
@BypassInterceptors
public class ConexidadLogSearch extends AbstractEntitySearch<ConexidadLog>
{
	private String informacionConexidad;
	private Date fromFechaComienzo;
	private Date toFechaComienzo;
	private Date fromFechaFin;
	private Date toFechaFin;
	private Long minIntervaloSeg;
	private Long minIntervalo;
	private Integer numeroExpediente;
	private Integer anioExpediente;
	private String claveExpediente;
	private Camara camara;
	private Expediente expediente;
	private String tipoRadicacion;

	private boolean showDetails = false;

	private ConexidadLogSearch filter;

	public ConexidadLogSearch() {
		setDefaultOrder("conexidadLog.fechaComienzo desc");
	}

	@Override
	public void clear() {
		this.informacionConexidad = null;
		this.fromFechaComienzo = null;
		this.toFechaComienzo = null;
		this.fromFechaFin = null;
		this.toFechaFin = null;
		this.minIntervalo = null;
		this.minIntervaloSeg = null;
		this.camara = null;
		this.expediente = null;
		this.numeroExpediente = null;
		this.anioExpediente = null;
		this.claveExpediente = null;
		this.tipoRadicacion = null;
	}

	@Override
	public String getInitialPanelState()  {
		if (true
			&& this.informacionConexidad == null
			&& this.fromFechaComienzo == null
			&& this.toFechaComienzo == null
			&& this.fromFechaFin == null
			&& this.toFechaFin == null
			&& this.minIntervalo == null
			&& this.minIntervaloSeg == null
			&& this.numeroExpediente == null
			&& this.anioExpediente == null
			&& this.claveExpediente == null
			&& this.camara == null
			&& this.expediente == null
			&& this.tipoRadicacion == null

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



    /**
     * Returns the informacionConexidad search filter
     * 
     * @return	the informacionConexidad search filter
     */
    public String getInformacionConexidad() {
    	return informacionConexidad;
    }

    /**
     * Sets the informacionConexidad search filter
     * 
     * @param informacionConexidad the informacionConexidad search filter
     */
    public void setInformacionConexidad(String informacionConexidad) {
    	this.informacionConexidad = informacionConexidad;
    }
    
   /**
     * Clears the informacionConexidad search filter
     */
    public void clearInformacionConexidad() {
    	setInformacionConexidad(null);
    }

    /**
     * Returns the fromFechaComienzo search filter
     * 
     * @return	the fromFechaComienzo search filter
     */
    public Date getFromFechaComienzo() {
    	return fromFechaComienzo;
    }

    /**
     * Sets the fromFechaComienzo search filter
     * 
     * @param fromFechaComienzo the fromFechaComienzo search filter
     */
    public void setFromFechaComienzo(Date fromFechaComienzo) {
    	this.fromFechaComienzo = fromFechaComienzo;
    }
    
   /**
     * Clears the fromFechaComienzo search filter
     */
    public void clearFromFechaComienzo() {
    	setFromFechaComienzo(null);
    }

    /**
     * Returns the toFechaComienzo search filter
     * 
     * @return	the toFechaComienzo search filter
     */
    public Date getToFechaComienzo() {
    	return toFechaComienzo;
    }

    /**
     * Sets the toFechaComienzo search filter
     * 
     * @param toFechaComienzo the toFechaComienzo search filter
     */
    public void setToFechaComienzo(Date toFechaComienzo) {
    	this.toFechaComienzo = toFechaComienzo;
    }
    
   /**
     * Clears the toFechaComienzo search filter
     */
    public void clearToFechaComienzo() {
    	setToFechaComienzo(null);
    }

    /**
     * Returns the fromFechaFin search filter
     * 
     * @return	the fromFechaFin search filter
     */
    public Date getFromFechaFin() {
    	return fromFechaFin;
    }

    /**
     * Sets the fromFechaFin search filter
     * 
     * @param fromFechaFin the fromFechaFin search filter
     */
    public void setFromFechaFin(Date fromFechaFin) {
    	this.fromFechaFin = fromFechaFin;
    }
    
   /**
     * Clears the fromFechaFin search filter
     */
    public void clearFromFechaFin() {
    	setFromFechaFin(null);
    }

    /**
     * Returns the toFechaFin search filter
     * 
     * @return	the toFechaFin search filter
     */
    public Date getToFechaFin() {
    	return toFechaFin;
    }

    /**
     * Sets the toFechaFin search filter
     * 
     * @param toFechaFin the toFechaFin search filter
     */
    public void setToFechaFin(Date toFechaFin) {
    	this.toFechaFin = toFechaFin;
    }
    
   /**
     * Clears the toFechaFin search filter
     */
    public void clearToFechaFin() {
    	setToFechaFin(null);
    }

    /**
     * Returns the minIntervalo search filter
     * 
     * @return	the minIntervalo search filter
     */
    public Long getMinIntervalo() {
    	return minIntervalo;
    }

    /**
     * Sets the minIntervalo search filter
     * 
     * @param minIntervalo the minIntervalo search filter
     */
    public void setMinIntervalo(Long minIntervalo) {
    	this.minIntervalo = minIntervalo;
    }
    
   /**
     * Clears the minIntervalo search filter
     */
    public void clearMinIntervalo() {
    	setMinIntervalo(null);
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
     * Returns the expediente search filter
     * 
     * @return	the expediente search filter
     */
    public Expediente getExpediente() {
    	return expediente;
    }

    /**
     * Sets the expediente search filter
     * 
     * @param expediente the expediente search filter
     */
    public void setExpediente(Expediente expediente) {
    	this.expediente = expediente;
    }
    
   /**
     * Clears the expediente search filter
     */
    public void clearExpediente() {
    	setExpediente(null);
    }

    /**
     * Returns the tipoRadicacion search filter
     * 
     * @return	the tipoRadicacion search filter
     */
    public String getTipoRadicacion() {
    	return tipoRadicacion;
    }

    /**
     * Sets the tipoRadicacion search filter
     * 
     * @param tipoRadicacion the tipoRadicacion search filter
     */
    public void setTipoRadicacion(String tipoRadicacion) {
    	this.tipoRadicacion = tipoRadicacion;
    }
    
   /**
     * Clears the tipoRadicacion search filter
     */
    public void clearTipoRadicacion() {
    	setTipoRadicacion(null);
    }

	public String getClaveExpediente() {
		return claveExpediente;
	}

	public void setClaveExpediente(String claveExpediente) {
		this.claveExpediente = claveExpediente;
	}

	public Integer getNumeroExpediente() {
		return numeroExpediente;
	}

	public void setNumeroExpediente(Integer numeroExpediente) {
		this.numeroExpediente = numeroExpediente;
	}

	public Integer getAnioExpediente() {
		return anioExpediente;
	}

	public void setAnioExpediente(Integer anioExpediente) {
		this.anioExpediente = anioExpediente;
	}

	public boolean isShowDetails() {
		return showDetails;
	}

	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	public ConexidadLogSearch getFilter() {
		if (filter == null) {
			filter = new ConexidadLogSearch();
		}
		return filter;
	}

    @Override
    public void updateFilters() {
    	super.updateFilters();
		getFilter().setInformacionConexidad(getInformacionConexidad());
		getFilter().setFromFechaComienzo(getFromFechaComienzo());
		getFilter().setToFechaComienzo(getToFechaComienzo());
		getFilter().setFromFechaFin(getFromFechaFin());
		getFilter().setToFechaFin(getToFechaFin());
		getFilter().setMinIntervalo(getMinIntervalo());
		getFilter().setNumeroExpediente(getNumeroExpediente());
		getFilter().setAnioExpediente(getAnioExpediente());
		getFilter().setClaveExpediente(getClaveExpediente());
		getFilter().setCamara(getCamara());
		getFilter().setExpediente(getExpediente());
		getFilter().setTipoRadicacion(getTipoRadicacion());
	}

	public Long getMinIntervaloSeg() {
		return minIntervaloSeg;
	}

	public void setMinIntervaloSeg(Long minIntervaloSeg) {
		this.minIntervaloSeg = minIntervaloSeg;
		if (minIntervaloSeg != null) {
			this.minIntervalo = minIntervaloSeg * 1000;
		} else {
			this.minIntervalo = null;
		}
	}


}

