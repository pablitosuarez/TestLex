package com.base100.lex100.mesaEntrada.loader;

import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.ConfiguracionMateria;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.NumeradorExpediente;
import com.base100.lex100.manager.camara.CamaraManager;

public class ConfiguracionLoader extends AbstractConfigurationLoader{

	
	
	private static final String CONFIGURACION_VALUE = "CONFIGURACION";
	private static final String NUMERADOR_EXPEDIENTE = "NUMERADOR_EXPEDIENTE";
	private int insertCount = 0; 
	private int updateCount = 0; 
	private int insertNumeradorExpedienteCount = 0; 
	private int updateNumeradorExpedienteCount = 0; 

	public ConfiguracionLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
		insertNumeradorExpedienteCount = 0; 
		updateNumeradorExpedienteCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isConfiguracionRow(row)){
			cargaConfiguracion(row);
		}else if(isNumeradorExpedienteRow(row)){
			cargaNumeradorExpediente(row);
		}
	}

	protected void end() {
		info("Configuracion: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
		if(insertNumeradorExpedienteCount > 0 || updateNumeradorExpedienteCount > 0){
			info("Numerador Expediente: "+insertNumeradorExpedienteCount+" filas añadidas, "+updateNumeradorExpedienteCount+" filas modificadas");
		}
	}


	private boolean isConfiguracionRow(Cell[] row) {
		return CONFIGURACION_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private boolean isNumeradorExpedienteRow(Cell[] row) {
		return NUMERADOR_EXPEDIENTE.equalsIgnoreCase(row[0].getContents());
	}
	
	private ConfiguracionMateria cargaConfiguracion(Cell[] row) throws LoaderException {
		Camara camara = null;
		if(!isEmpty(row,1)){
			camara = buscaCamara(getContent(row, 1));		
			if(camara == null){
				fatal("Proceso abortado");
			}
		}
		
		Materia materia = null;
		if(!isEmpty(row,2)){
			materia = buscaMateria(getContent(row, 2), true);
			if(materia == null){
				fatal("Proceso abortado");
			}
		}
		String parametro = getContent(row, 3);
		String valor = getContent(row, 4);
		if(camara == null && materia == null){
			info("Atencion Parametro de Configuracion sin Materia ni Camara :"+parametro);
		}else if(camara == null){
			info("Atencion Parametro de Configuracion sin Camara :"+parametro);
		}else if((materia == null) && CamaraManager.isCamaraMultiMateria(camara)) {
			info("Atencion Parametro de Configuracion sin Materia :"+parametro);
		}

		return actualizaConfiguracionMateria(camara, materia, parametro, valor);
		
	}

	private ConfiguracionMateria actualizaConfiguracionMateria(Camara camara, Materia materia, String parametro, String valor) throws LoaderException {
	
		ConfiguracionMateria configuracionMateria = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			if(Strings.isEmpty(parametro)){
				fatal("nombre de parametro requerido");
			}
			configuracionMateria = buscaConfiguracionMateria(camara, materia, parametro, false);
			if(configuracionMateria == null){
				configuracionMateria = new ConfiguracionMateria();
				configuracionMateria.setCamara(camara);
				configuracionMateria.setMateria(materia);
				configuracionMateria.setParametro(parametro);
			}
			configuracionMateria.setValor(valor);
			 			
			if (configuracionMateria.getId() == null) {
				getEntityManager().persist(configuracionMateria);
				insertCount++;
			}else{
				isNew = false;
				updateCount++;
			}
			
			getEntityManager().flush();
		}
		return configuracionMateria;
	
	}

	private NumeradorExpediente cargaNumeradorExpediente(Cell[] row) throws LoaderException {
		Camara camara = buscaCamara(getContent(row, 1));		
		if(camara == null){
			fatal("Proceso abortado");
		}
		int anio = getInt(row, 2, -1);
		int ultimoNumero = getInt(row, 3, -1);
		
		if(anio < 0 ){
			fatal("Numerador Expediente: año erroneo "+anio);
		}
		if(ultimoNumero < 0 ){
			fatal("Numerador Expediente: valor de Ultimo Numero erroneo :"+ultimoNumero);
		}

		return actualizaNumeradorExpediente(camara, anio, ultimoNumero);
		
	}

	private NumeradorExpediente actualizaNumeradorExpediente(Camara camara, int anio, int ultimoNumero) throws LoaderException {
	
		NumeradorExpediente numeradorExpediente = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			if(camara == null){
				fatal("Proceso abortado");
			}
			numeradorExpediente = buscaNumeradorExepediente(camara, anio, false);
			if(numeradorExpediente == null){
				numeradorExpediente = new NumeradorExpediente();
				numeradorExpediente.setCamara(camara);
				numeradorExpediente.setAnio(anio);
			}
			numeradorExpediente.setUltimoNumero(ultimoNumero);
			if(Strings.isEmpty(numeradorExpediente.getUuid())){
				numeradorExpediente.setUuid(UUID.randomUUID().toString());
			}
			if (numeradorExpediente.getId() == null) {
				getEntityManager().persist(numeradorExpediente);
				insertCount++;
			}else{
				isNew = false;
				updateCount++;
			}
			
			getEntityManager().flush();
		}
		return numeradorExpediente;
	
	}

	


	
	
}
