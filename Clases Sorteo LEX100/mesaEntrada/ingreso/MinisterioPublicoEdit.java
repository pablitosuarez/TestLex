package com.base100.lex100.mesaEntrada.ingreso;

import java.util.List;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.enumeration.TipoMinisterioPublicoCivilEnumeration;
import com.base100.lex100.component.enumeration.TipoMinisterioPublicoCivilEnumeration.Type;
import com.base100.lex100.controller.entity.turnoFiscalia.TurnoFiscaliaSearch;
import com.base100.lex100.entity.Oficina;

public class MinisterioPublicoEdit extends ScreenController{
	private boolean error;

	
	private String tipoMinisterioPublico;
    private Integer numero;

	public void checkAccept() throws EntityOperationException {
		setError(false);
		TipoMinisterioPublicoCivilEnumeration.Type tipo = getTipoMinisterioPublicoCivil(tipoMinisterioPublico);
		if(tipo == null){
			String errKey = "ministerioPublico.error.tipoSelect";
			errorMsg(errKey);
			setError(true);
		}
		if(!isError() && numero == null){
			String errKey = "ministerioPublico.error.numeroSelect";
			errorMsg(errKey);
			setError(true);
		}else{
			if(findOficinaMinisterio() == null){
				String errKey = "ministerioPublico.error.ministerioNotFound";
				errorMsg(errKey);
				setError(true);				
			}
		}
	}

	private Oficina findOficinaMinisterio() {
		if(tipoMinisterioPublico != null && numero != null){
			List<Oficina> oficina = TurnoFiscaliaSearch.findOficinasForOficinaTurno(ExpedienteMeAdd.instance().getEntityManager(), SessionState.instance().getGlobalCamara(), tipoMinisterioPublico, numero);
			if(oficina != null && oficina.size() > 0){
				return oficina.get(0);
			}
		}
		return null;
	}

	private Type getTipoMinisterioPublicoCivil(String t) {
		if(TipoMinisterioPublicoCivilEnumeration.Type.fiscalia.getValue().equals(t)){
			return TipoMinisterioPublicoCivilEnumeration.Type.fiscalia;
		}
		if(TipoMinisterioPublicoCivilEnumeration.Type.defensoria.getValue().equals(t)){
			return TipoMinisterioPublicoCivilEnumeration.Type.defensoria;
		}
		if(TipoMinisterioPublicoCivilEnumeration.Type.asesoria.getValue().equals(t)){
			return TipoMinisterioPublicoCivilEnumeration.Type.asesoria;
		}
		return null;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getTipoMinisterioPublico() {
		return tipoMinisterioPublico;
	}

	public void setTipoMinisterioPublico(String tipoMinisterioPublico) {
		this.tipoMinisterioPublico = tipoMinisterioPublico;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getNombreMinisterio() {
		TipoMinisterioPublicoCivilEnumeration.Type tipo = getTipoMinisterioPublicoCivil(tipoMinisterioPublico);
		if(tipo != null){
			return getMessages().get(tipo.getLabel())+" NUMERO "+numero;
		}else{
			return null;
		}
		
	}


}
