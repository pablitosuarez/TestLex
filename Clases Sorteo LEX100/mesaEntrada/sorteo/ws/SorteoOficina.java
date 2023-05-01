package com.base100.lex100.mesaEntrada.sorteo.ws;


import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.base100.lex100.mesaEntrada.sorteo.BolaSorteo;


/**
 * Proporciona los siguientes servicios:<br>
 * <br>
 * <li><b>sorteoAsincrono</b>: Asigna la oficina al expediente <li>
 * <li><b>sorteoSincrono</b>: Devuelve la BolaSorteo <li>
 * <li><b>arrancarBolillero</b>: Arranca un Bolillero <li>
 * <li><b>pararBolillero</b>: Detiene un Bolillero <li>
 * <li><b>arrancarSorteador</b>: Arranca un Sorteador  <li>
 * <li><b>detenerSorteador</b>: Detiene un Sorteador <li>
 * 
 */
@WebService(name = "sorteoOficina", portName = "SorteoOficinaPort", serviceName = "SorteoOficinaService", endpointInterface="com.base100.lex100.mesaEntrada.sorteo.ws.ISorteoOficina")
//@EndpointConfig(configName = "Seam WebService Endpoint")
public class SorteoOficina implements ISorteoOficina {

	private static Log log = Logging.getLog(SorteoOficina.class);

	public SorteoOficina() {
	}

	/**
	 * Asigna la oficina al expediente
	 * 
	 * @param Identificador de Expediente
	 * @return
	 */
	@Override
	@WebMethod
	public void sorteoAsincrono(
			@WebParam(name = "sorteoAsincronoRequest")String idExpediente) {
		
	}

	/**
	 * 
	 * Devuelve la BolaSorteo 
	 * 
	 * @param Identificador de Expediente
	 * @return
	 */
	@Override
	@WebMethod
	public BolaSorteo sorteoSincrono(
			@WebParam(name = "sorteoSincronoRequest")String idExpediente) {
		return null;
	}

	/**
	 * 
	 * Arranca un Bolillero
	 * 
	 * @param Identificador de Bolillero
	 * @return
	 */
	@Override
	@WebMethod
	public void arrancarBolillero(
			@WebParam(name = "arrancarBolilleroRequest")String idBolillero) {
		
	}

	/**
	 * 
	 * Detiene un Bolillero
	 * 
	 * @param Identificador de Bolillero
	 * @return
	 */
	@Override
	@WebMethod
	public void pararBolillero(
			@WebParam(name = "pararBolilleroRequest")String idBolillero) {
		
	}

	/**
	 * 
	 * Arranca un Sorteador
	 * 
	 * @param Identificador de Sorteador
	 * @return
	 */
	@Override
	@WebMethod
	public void arrancarSorteador(
			@WebParam(name = "arrancarSorteadorRequest")String idSorteador) {

	}

	/**
	 * 
	 * Detiene un Sorteador
	 * 
	 * @param Identificador de Sorteador
	 * @return
	 */
	@Override
	@WebMethod
	public void PararSorteador(
			@WebParam(name = "pararSorteadorRequest")String idSorteador) {

	}


}
