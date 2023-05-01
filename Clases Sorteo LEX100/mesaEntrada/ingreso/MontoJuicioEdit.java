package com.base100.lex100.mesaEntrada.ingreso;

import java.math.BigDecimal;

import org.jboss.seam.Component;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.component.enumeration.MonedaEnumeration;
import com.base100.lex100.controller.entity.moneda.MonedaSearch;
import com.base100.lex100.entity.Moneda;

public class MontoJuicioEdit extends ScreenController{
	private Moneda moneda;
	private BigDecimal cantidad;
	
	private boolean error;

    public MontoJuicioEdit(){
    	this.moneda = calculateDefaultMoneda();
    }

	public Moneda getMoneda() {
		return moneda;
	}

	public void setMoneda(Moneda moneda) {
		this.moneda = moneda;
	}

	public String getDescripcion(){
		if(moneda != null){
			return moneda.getDescripcion();
		}else{
			return null;
		}
	}

	public Moneda calculateDefaultMoneda(){
		MonedaSearch monedaSearch = (MonedaSearch) Component.getInstance(MonedaSearch.class, true);
		return monedaSearch.findByCodigoMoneda(MonedaEnumeration.CODIGO_PESO);
	}
	
	public void checkAccept() throws EntityOperationException{
		if((getCantidad() != null) && !BigDecimal.ZERO.equals(getCantidad()) && (getMoneda() == null)){
			String errKey = "moneda.error.notExist";
			errorMsg(errKey);
			setError(true);
			throw new EntityOperationException();
		}
		setError(false);
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public BigDecimal getCantidad() {
		return cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}
}
