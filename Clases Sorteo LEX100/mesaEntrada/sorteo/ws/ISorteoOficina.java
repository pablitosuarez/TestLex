package com.base100.lex100.mesaEntrada.sorteo.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.base100.lex100.mesaEntrada.sorteo.BolaSorteo;

@WebService()
public interface ISorteoOficina {

	/**
	 * 
	 * Asigna la oficina al expediente
	 * 
	 * @param Identificador de Expediente
	 * @return
	 */
	@WebMethod
	public abstract void sorteoAsincrono(
			@WebParam(name = "idExpediente")String idExpediente);

	/**
	 * 
	 * Devuelve la BolaSorteo 
	 * 
	 * @param Identificador de Expediente
	 * @return
	 */
	@WebMethod
	public abstract BolaSorteo sorteoSincrono(
			@WebParam(name = "idExpediente")String idExpediente);
	
	/**
	 * 
	 * Arranca un Bolillero
	 * 
	 * @param Identificador de Bolillero
	 * @return
	 */
	@WebMethod
	public abstract void arrancarBolillero(
			@WebParam(name = "idBolillero")String idBolillero);

	/**
	 * 
	 * Detiene un Bolillero
	 * 
	 * @param Identificador de Bolillero
	 * @return
	 */
	@WebMethod
	public abstract void pararBolillero(
			@WebParam(name = "idBolillero")String idBolillero);

	/**
	 * 
	 * Arranca un Sorteador
	 * 
	 * @param Identificador de Sorteador
	 * @return
	 */
	@WebMethod
	public abstract void arrancarSorteador(
			@WebParam(name = "idSorteador")String idSorteador);

	/**
	 * 
	 * Detiene un Sorteador
	 * 
	 * @param Identificador de Sorteador
	 * @return
	 */
	@WebMethod
	public abstract void PararSorteador(
			@WebParam(name = "idSorteador")String idSorteador);

}