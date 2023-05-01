package com.base100.lex100.manager.expediente;

import java.util.Date;

import com.base100.lex100.component.enumeration.TipoAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.Oficina;

public class ParametrosAsignacion {
	private String usuario; 
	private Oficina oficinaSorteo;
	private Expediente expediente;
	private Oficina oficina; 
	private TipoRadicacionEnumeration.Type tipoRadicacion; 
	private TipoAsignacionEnumeration.Type tipoAsignacion; 
	private Date fechaAsignacion; 
	private Integer fiscalia; 
	private Integer defensoria; 
	private Integer asesoria; 
	private String rubro;
	private String codigoTipoCambioAsignacion;
	private String observaciones;
	
	public ParametrosAsignacion(
			String usuario, 
			Oficina oficinaSorteo,
			Expediente expediente,
			Oficina oficina, 
			TipoRadicacionEnumeration.Type tipoRadicacion, 
			TipoAsignacionEnumeration.Type tipoAsignacion, 
			Date fechaAsignacion, 
			Integer fiscalia, 
			Integer defensoria, 
			Integer asesoria, 
			String rubro,
			String codigoTipoCambioAsignacion,
			String observaciones) {
		this.usuario = usuario;
		this.oficinaSorteo = oficinaSorteo;
		this.expediente = expediente;
		this.oficina = oficina;
		this.tipoRadicacion = tipoRadicacion; 
		this.tipoAsignacion = tipoAsignacion; 
		this.fechaAsignacion = fechaAsignacion; 
		this.fiscalia = fiscalia; 
		this.defensoria = defensoria; 
		this.asesoria = asesoria; 
		this.rubro = rubro;
		this.codigoTipoCambioAsignacion = codigoTipoCambioAsignacion;
		this.observaciones = observaciones;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Oficina getOficinaSorteo() {
		return oficinaSorteo;
	}

	public void setOficinaSorteo(Oficina oficinaSorteo) {
		this.oficinaSorteo = oficinaSorteo;
	}

	public Expediente getExpediente() {
		return expediente;
	}

	public void setExpediente(Expediente expediente) {
		this.expediente = expediente;
	}

	public Oficina getOficina() {
		return oficina;
	}

	public void setOficina(Oficina oficina) {
		this.oficina = oficina;
	}

	public TipoRadicacionEnumeration.Type getTipoRadicacion() {
		return tipoRadicacion;
	}

	public void setTipoRadicacion(TipoRadicacionEnumeration.Type tipoRadicacion) {
		this.tipoRadicacion = tipoRadicacion;
	}

	public TipoAsignacionEnumeration.Type getTipoAsignacion() {
		return tipoAsignacion;
	}

	public void setTipoAsignacion(TipoAsignacionEnumeration.Type tipoAsignacion) {
		this.tipoAsignacion = tipoAsignacion;
	}

	public Date getFechaAsignacion() {
		return fechaAsignacion;
	}

	public void setFechaAsignacion(Date fechaAsignacion) {
		this.fechaAsignacion = fechaAsignacion;
	}

	public Integer getFiscalia() {
		return fiscalia;
	}

	public void setFiscalia(Integer fiscalia) {
		this.fiscalia = fiscalia;
	}

	public Integer getDefensoria() {
		return defensoria;
	}

	public void setDefensoria(Integer defensoria) {
		this.defensoria = defensoria;
	}

	public Integer getAsesoria() {
		return asesoria;
	}

	public void setAsesoria(Integer asesoria) {
		this.asesoria = asesoria;
	}

	public String getRubro() {
		return rubro;
	}

	public void setRubro(String rubro) {
		this.rubro = rubro;
	}

	public String getCodigoTipoCambioAsignacion() {
		return codigoTipoCambioAsignacion;
	}

	public void setCodigoTipoCambioAsignacion(String codigoTipoCambioAsignacion) {
		this.codigoTipoCambioAsignacion = codigoTipoCambioAsignacion;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
}
