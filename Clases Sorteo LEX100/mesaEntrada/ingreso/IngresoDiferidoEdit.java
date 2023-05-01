package com.base100.lex100.mesaEntrada.ingreso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.entity.Oficina;

public class IngresoDiferidoEdit extends ScreenController {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 6733248799066831822L;
	
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	private Integer numeroExpediente;
	private Integer numeroExpedienteAnteriorAlSistema;
    private Integer anioExpediente;
	private Oficina oficinaDestino;
	private Date fechaIngreso;
	private boolean showSelectorDeOficinas;

	private Oficina juzgado;
	private Oficina sala;
	private Oficina tribunalOral;
	
	private String returnPage;
	
	private boolean error = true;
	
	private String tipoAsignacion;
	
	public IngresoDiferidoEdit() {
		if(!ExpedienteMeAdd.instance().isIngresoAnteriorSistema()) {
			fechaIngreso = new Date();
		}
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

	public Oficina getOficinaDestino() {
		return oficinaDestino;
	}

	public void setOficinaDestino(Oficina oficinaDestino) {
		this.oficinaDestino = oficinaDestino;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean isShowSelectorDeOficinas(){
		return showSelectorDeOficinas;
	}
	
	public void setShowSelectorDeOficinas(boolean showSelectorDeOficinas) {
		this.showSelectorDeOficinas = showSelectorDeOficinas;
	}

	public void setReturnPage(String returnPage) {
		this.returnPage = returnPage;
	}

	public String getReturnPage() {
		return returnPage;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public Integer getNumeroExpedienteAnteriorAlSistema() {
		return numeroExpedienteAnteriorAlSistema;
	}

	public void setNumeroExpedienteAnteriorAlSistema(
			Integer numeroExpedienteAnteriorAlSistema) {
		this.numeroExpedienteAnteriorAlSistema = numeroExpedienteAnteriorAlSistema;
	}
	
	public void checkAccept(String fechaArranqueDelSistemaStr) throws EntityOperationException {
		setError(false);

		if (getOficinaDestino() == null) {
			String errKey = "expedienteMe.ingresoDiferido.error.oficinaDestinoRequerida";
			errorMsg(errKey);
			setError(true);
		}
		if (getNumeroExpediente() == null
				|| getAnioExpediente() == null) {
			String errKey = "expedienteMe.ingresoDiferido.error.numeroExpedienteRequerido";
			errorMsg(errKey);
			setError(true);
		} else if(getAnioExpediente().intValue() < 1000){
			String errKey = "expedienteMe.ingresoDiferido.error.AnioCorto";
			errorMsg(errKey);
			setError(true);
		} else if(getTipoAsignacion() == null){
			String errKey = "expedienteMe.ingresoDiferido.error.tipoAsignacionRequerida";
			errorMsg(errKey);
			setError(true);
		}
		Date fechaArranqueDelSistema = null;
		if(fechaArranqueDelSistemaStr != null){
			try {
				fechaArranqueDelSistema = SIMPLE_DATE_FORMAT.parse(fechaArranqueDelSistemaStr);
			} catch (ParseException e) {
				String errKey = "expedienteMe.ingresoDiferido.error.parametroFechaArranqueSistema";
				errorMsg(errKey);
			}
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date());
		gc.add(GregorianCalendar.DAY_OF_MONTH, -1);
		if(fechaArranqueDelSistema == null){
			fechaArranqueDelSistema = gc.getTime();
		}
		if(fechaIngreso == null){
			String errKey = "expedienteMe.ingresoDiferido.error.fechaIngresoRequerida";
			errorMsg(errKey);
			setError(true);
		} else{
			if(ExpedienteMeAdd.instance().isIngresoAnteriorSistema() && fechaIngreso.after(fechaArranqueDelSistema)){
				String errKey = "expedienteMe.ingresoDiferido.error.fechaIngreso";
				errorMsg(errKey);
				setError(true);
			}
			// Verifico que el año de la fecha de ingreso coincide con la del expediente.
			GregorianCalendar gcIngreso = new GregorianCalendar();
			gcIngreso.setTime(fechaIngreso);
			if(getAnioExpediente() != null && gcIngreso.get(GregorianCalendar.YEAR) != getAnioExpediente().intValue()){
				String errKey = "expedienteMe.ingresoDiferido.error.anioFechaDistintoAnio";
				errorMsg(errKey);
				setError(true);
			}
			// Verifico que la fecha no sea mas antigua de 100 años.
			gc.add(GregorianCalendar.YEAR, -100);
			if(fechaIngreso.before(gc.getTime())){
				String errKey = "expedienteMe.ingresoDiferido.error.fechaIngresoAntigua";
				errorMsg(errKey);
				setError(true);
			}
		}
		if(error){
			throw new EntityOperationException();
		}
	}

	public Oficina getJuzgado() {
		return juzgado;
	}

	public void setJuzgado(Oficina juzgado) {
		this.juzgado = juzgado;
	}

	public Oficina getSala() {
		return sala;
	}

	public void setSala(Oficina sala) {
		this.sala = sala;
	}

	public Oficina getTribunalOral() {
		return tribunalOral;
	}

	public void setTribunalOral(Oficina tribunalOral) {
		this.tribunalOral = tribunalOral;
	}

	public String getTipoAsignacion() {
		return tipoAsignacion;
	}

	public void setTipoAsignacion(String tipoAsignacion) {
		this.tipoAsignacion = tipoAsignacion;
	}
	
}
