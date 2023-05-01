package com.base100.lex100.mesaEntrada.ingreso;

import org.jboss.seam.Component;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.controller.entity.objetoJuicio.ObjetoJuicioSearch;
import com.base100.lex100.entity.ObjetoJuicio;

public class OtrosDatosEdit extends ScreenController{
	private boolean error;
    private Integer asesoria;
    private Integer defensoria;
    private Integer fiscalia;
    private boolean abonarTasa;
    private boolean exentoTasa;
    private boolean noAlcanzadoTasa;
    private boolean pagoDiferidoTasa;
    private boolean medidaPrecautoria;
    private boolean medidaPreliminar;
    private boolean pruebaAnticipada;

    private boolean resolucionAdministrativa;
    private boolean adjuntaCopia;

	
	public void checkAccept() throws EntityOperationException{
		setError(false);
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public Integer getAsesoria() {
		return asesoria;
	}

	public void setAsesoria(Integer asesoria) {
		this.asesoria = asesoria;
	}

	public Integer getDefensoria() {
		return defensoria;
	}

	public void setDefensoria(Integer defensoria) {
		this.defensoria = defensoria;
	}

	public Integer getFiscalia() {
		return fiscalia;
	}

	public void setFiscalia(Integer fiscalia) {
		this.fiscalia = fiscalia;
	}

	public boolean isAbonarTasa() {
		return abonarTasa;
	}

	public void setAbonarTasa(boolean abonarTasa) {
		this.abonarTasa = abonarTasa;
	}

	public boolean isExentoTasa() {
		return exentoTasa;
	}

	public void setExentoTasa(boolean exentoTasa) {
		this.exentoTasa = exentoTasa;
	}

	public boolean isNoAlcanzadoTasa() {
		return noAlcanzadoTasa;
	}

	public void setNoAlcanzadoTasa(boolean noAlcanzadoTasa) {
		this.noAlcanzadoTasa = noAlcanzadoTasa;
	}

	public boolean isPagoDiferidoTasa() {
		return pagoDiferidoTasa;
	}

	public void setPagoDiferidoTasa(boolean pagoDiferidoTasa) {
		this.pagoDiferidoTasa = pagoDiferidoTasa;
	}

	public boolean isMedidaPrecautoria() {
		return medidaPrecautoria;
	}

	public void setMedidaPrecautoria(boolean medidaPrecautoria) {
		this.medidaPrecautoria = medidaPrecautoria;
	}

	public boolean isMedidaPreliminar() {
		return medidaPreliminar;
	}

	public void setMedidaPreliminar(boolean medidaPreliminar) {
		this.medidaPreliminar = medidaPreliminar;
	}

	public boolean isPruebaAnticipada() {
		return pruebaAnticipada;
	}

	public void setPruebaAnticipada(boolean pruebaAnticipada) {
		this.pruebaAnticipada = pruebaAnticipada;
	}

	public boolean isResolucionAdministrativa() {
		return resolucionAdministrativa;
	}

	public void setResolucionAdministrativa(boolean resolucionAdministrativa) {
		this.resolucionAdministrativa = resolucionAdministrativa;
	}

	public boolean isAdjuntaCopia() {
		return adjuntaCopia;
	}

	public void setAdjuntaCopia(boolean adjuntaCopia) {
		this.adjuntaCopia = adjuntaCopia;
	}
}
