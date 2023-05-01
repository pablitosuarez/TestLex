package com.base100.lex100.mesaEntrada.ingreso;

import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.Mediador;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.mediador.MediadorManager;

public class RadicacionPreviaEdit extends ScreenController {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 5059713169131856900L;

	private Oficina oficinaDestino;
	private Mediador mediador;
	private boolean mediacion;
	
	private String returnPage;
	
	private boolean error = true;
	

	public RadicacionPreviaEdit(boolean mediacion) {
		this.mediacion = mediacion;
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

	public void setReturnPage(String returnPage) {
		this.returnPage = returnPage;
	}

	public String getReturnPage() {
		return returnPage;
	}

	public void checkAccept(ObjetoJuicio objetoJuicio) throws EntityOperationException {
		setError(false);

		if (getOficinaDestino() == null) {
			String errKey = "expedienteMe.radicacionPrevia.error.oficinaDestinoRequerida";
			errorMsg(errKey);
			setError(true);
		}
		if (isMediacion() && mediador == null) {
			String errKey = "expedienteMe.radicacionPrevia.error.mediadorDestinoRequerido";
			errorMsg(errKey);
			setError(true);
		}
		if(error){
			throw new EntityOperationException();
		}
	}


	public Mediador getMediador() {
		return mediador;
	}

	public void setMediador(Mediador mediador) {
		this.mediador = mediador;
	}

	public boolean isMediacion() {
		return mediacion;
	}


}
