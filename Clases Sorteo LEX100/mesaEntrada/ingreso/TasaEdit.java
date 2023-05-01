package com.base100.lex100.mesaEntrada.ingreso;

import java.math.BigDecimal;

import org.jboss.seam.Component;
import org.jboss.seam.international.StatusMessage;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.expanddocuments.wsclient.controller.Runner;
import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.entity.TasaExp;

public class TasaEdit extends ScreenController
{
	public static final String MONTO_JUICIO_POR_DEFECTO = "montoJuicioPorDefecto";
	public static final String PORCENTAJE_TASA_MINIMA = "porcentajeTasaMinima";
	
    
    private BigDecimal CIEN = new BigDecimal(100);
    
    private boolean marca;
    private BigDecimal cantidad;
    private BigDecimal porcentajeTasaPagado;

    private TasaExp tasaExp;
    
    public TasaEdit() {
    	this.tasaExp = new TasaExp();
	}
    

	private boolean checkRequired() {
		boolean error = false;
		if (getInstance().getMonto() == null) {
			error = true;
			addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['tasaExp.monto']} :" + org.jboss.seam.international.Messages.instance().get("error_notnull"));
		}
		if (getInstance().getPorcentajeTasaPagado() == null) {
			error = true;
			addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['tasaExp.porcentajeTasaPagado']} :" + org.jboss.seam.international.Messages.instance().get("error_notnull"));
		}
		if (getInstance().getCantidadTasaPagada() == null) {
			error = true;
			addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['tasaExp.cantidadTasaPagada']} :" + org.jboss.seam.international.Messages.instance().get("error_notnull"));
		}
		if (getInstance().getTotalPagado() == null) {
			error = true;
			addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['tasaExp.totalPagado']} :" + org.jboss.seam.international.Messages.instance().get("error_notnull"));
		}
		if (getInstance().getFechaIntimacion() == null) {
			error = true;
			addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['tasaExp.fechaIntimacion']} :" + org.jboss.seam.international.Messages.instance().get("error_notnull"));
		}
		if (getInstance().getExpediente() == null) {
			error = true;
			addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['tasaExp.expediente']} :" + org.jboss.seam.international.Messages.instance().get("error_notnull"));
		}
		return error;
	}

	public void beforeAdd(TasaExp tasaExp) throws EntityOperationException {
		boolean error = false;
		doCalcularTasa();
		copyValuesToInstance();
		error |= checkRequired();
		if (error) {
			throw new EntityOperationException();
		}
	}
	
	public void beforeUpdate(TasaExp tasaExp) throws EntityOperationException {
		boolean error = false;
		doCalcularTasa();
		copyValuesToInstance();
		error |= checkRequired();
		if (error) {
			throw new EntityOperationException();
		}
	}
	
	//Bussiness
	public void doCalcularTasa(){
		BigDecimal total = BigDecimal.ZERO;
		
		if(getInstance().getCantidadTasaPagada() != null){
			total = total.add(getInstance().getCantidadTasaPagada());
		}
		if(getInstance().getActualizacion() != null){
			total = total.add(getInstance().getActualizacion());
		}
		if(getInstance().getOtrosImportes() != null){
			total = total.add(getInstance().getOtrosImportes());
		}
		if(getInstance().getIntereses() != null){
			total = total.add(getInstance().getIntereses());
		}
		if(getInstance().getMulta() != null){
			total = total.add(getInstance().getMulta());
		}
		getInstance().setTotalPagado(total);
	}
	
	private BigDecimal getActiveCantidad(){
		if(isMarca()){
			return this.getCantidad();
		}else{
			return getDefaultMonto();
		}
	}

	private BigDecimal getAPagarCantidad(){
		return calculaPorcent(getActiveCantidad(), getPorcentageTasaMinima());
	}
	
	private BigDecimal calculaPorcent(BigDecimal number, BigDecimal porcent){
		if(number == null || porcent == null){
			return null;
		}
		return number.multiply(porcent).divide(CIEN, 2, BigDecimal.ROUND_HALF_UP);
		
	}
	
	public boolean isMarca() {
		return marca;
	}
	
	private BigDecimal getDefaultMonto(){
		String value = getMontoJuicioPorDefecto();
		BigDecimal montoJuicioPorDefecto = BigDecimal.ZERO;
		try{
			montoJuicioPorDefecto =  new BigDecimal(value);
		}catch (NumberFormatException e){
		}
		return montoJuicioPorDefecto;		
	}


	private BigDecimal getPorcentageTasaMinima(){
		String value = getPorcentajeTasaMinima();
		BigDecimal porcentageTasaMinima = BigDecimal.ZERO;
		try{
			porcentageTasaMinima =  new BigDecimal(value);
		}catch (NumberFormatException e){
		}
		return porcentageTasaMinima;		
	}


	
	private void copyValuesToInstance(){
		getInstance().setMarca(isMarca());
		getInstance().setMonto(getActiveCantidad());
		getInstance().setPorcentajeTasaPagado(getPorcentajeTasaPagado());
	}
	
	public String obtenerUrlTicketGenerarTasa() {
		Runner runner = (Runner) Component.getInstance(Runner.class,true);
		runner.setCurrentPageKey("causaTasaEditExecute");
		runner.setPrograma("CausaTasa");
		runner.setDocumento(1131L);
		runner.setSincronizar(false);
		runner.agregarParametro("ID_BTAS_CAU", Integer.toString(getInstance().getId()));
		String ticketAmisDoc = runner.ejecucionAmisdoc(false,false);
		return runner.getUrlAmisdoc(ticketAmisDoc);
	}
	
	public void conPasarelaDepago(){
		getStatusMessages().add(StatusMessage.Severity.ERROR, "No se puede acceder a la Pasarela de Pago, pasarela no activada.");		
	}
	
	/*
	public String sinPasarelaDepago(String returnPage){
		setVisibleDialog("addTasaDialog", false);
		return doAdd(returnPage);
	}
	
	public void onAddTasa(){
		setVisibleDialog("addTasaDialog", true);	
	}
	
	public void doCancelAdd(){
		setVisibleDialog("addTasaDialog", false);	
	}
	*/

	public void setPorcentajeTasaPagado(BigDecimal porcentajeTasaPagado) {
		this.porcentajeTasaPagado = porcentajeTasaPagado;
	}

	public BigDecimal getPorcentajeTasaPagado() {
		return porcentajeTasaPagado;
	}


	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}

	public BigDecimal getCantidad() {
		return cantidad;
	}

	public void setMarca(boolean marca) {
		this.marca = marca;
	}

	public TasaExp getInstance() {
		return getTasaExp();
	}

	public TasaExp getTasaExp() {
		return tasaExp;
	}

	public void setTasaExp(TasaExp tasaExp) {
		this.tasaExp = tasaExp;
	}

	
	private String getConfigurationProperty(String property, String defaultValue){
		Configuration configuration = (Configuration) Component.getInstance(Configuration.class, true);
		return configuration.getProperty(property, defaultValue);
	}
	
	private String getMontoJuicioPorDefecto() {
		return getConfigurationProperty(MONTO_JUICIO_POR_DEFECTO, "30000");
	}
	
	private String getPorcentajeTasaMinima() {
		return getConfigurationProperty(PORCENTAJE_TASA_MINIMA, "0");
	}
	

}

