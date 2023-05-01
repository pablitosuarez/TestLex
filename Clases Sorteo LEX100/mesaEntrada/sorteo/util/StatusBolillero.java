package com.base100.lex100.mesaEntrada.sorteo.util;

import com.base100.lex100.entity.TipoBolillero;

public class StatusBolillero {
	private TipoBolillero tipoBolillero;
	private String rubro;
	private int status;
	
	public static final int STATUS_OK = 0;
	public static final int STATUS_RUBRO_NOCREADO = 1;
	public static final int STATUS_BOLAS_FALTANTES = 2;
	public static final int STATUS_BOLAS_SOBRANTES = 3;
	public static final int STATUS_RUBRO_SOBRANTE = 4;
	
	
	public StatusBolillero(TipoBolillero tipoBolillero, String rubro, int status) {
		this.tipoBolillero = tipoBolillero;
		this.rubro = rubro;
		this.status = status;
	}
	
	public String getRubro() {
		return rubro;
	}
	public void setRubro(String rubro) {
		this.rubro = rubro;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public TipoBolillero getTipoBolillero() {
		return tipoBolillero;
	}

	public void setTipoBolillero(TipoBolillero tipoBolillero) {
		this.tipoBolillero = tipoBolillero;
	}
	
	
}
