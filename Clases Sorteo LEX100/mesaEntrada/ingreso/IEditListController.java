package com.base100.lex100.mesaEntrada.ingreso;

import java.util.List;

import com.base100.lex100.entity.TipoInterviniente;

public interface IEditListController {
	public boolean isRelajarValidacion();
	public boolean isInputDomicilio(); 
	public boolean isUseDomicilioDenunciadoConstituido();
	public List<TipoInterviniente>  getTiposInterviniente();
}
