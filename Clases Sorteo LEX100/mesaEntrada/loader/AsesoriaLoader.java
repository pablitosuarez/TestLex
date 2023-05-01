package com.base100.lex100.mesaEntrada.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;
import jxl.read.biff.BiffException;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Asesoria;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.TipoInstancia;

public class AsesoriaLoader extends AbstractConfigurationLoader{

	
	private String ASESORIA_VALUE = "ASESORIA";
	
	private int insertCount = 0; 
	private int updateCount = 0; 

	
	public AsesoriaLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isAsesoriaRow(row)){
			cargaAsesoria(row);
		}
	}

	protected void end() {
		info("Asesoría: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}

	private boolean isAsesoriaRow(Cell[] row) {
		return ASESORIA_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private Asesoria cargaAsesoria(Cell[] row) throws LoaderException {
		Camara camara = buscaCamara(getContent(row, 1));
		if(camara == null){
			fatal("Proceso abortado");
		}
		
		Integer codigoAsesoria = getInteger(row, 2, null);
		String descripcion = getContent(row, 3);
		Date fechaInicio = getDate(row, 4);
		String competenciaCodigo = getUpperContent(row, 5);
		String codigoDependencia = getContent(row, 6);
		String instanciaAbreviatura = getUpperContent(row, 7);
		String zonaOficina = getContent(row, 8);
		
		return actualizaAsesoria(camara, codigoAsesoria, descripcion, fechaInicio, competenciaCodigo, codigoDependencia,instanciaAbreviatura, zonaOficina);
		//
	}

		
	private Asesoria actualizaAsesoria(Camara camara, Integer codigoAsesoria,
				String descripcion, Date fechaInicio, String competenciaCodigo, String codigoDependencia,
				String instanciaAbreviatura, String zonaOficina) throws LoaderException {
		
		Asesoria asesoria = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			TipoInstancia tipoInstancia = buscaTipoInstanciaByAbreviatura(instanciaAbreviatura);
			Competencia competencia = buscaCompetencia(camara, competenciaCodigo, false);
			asesoria = buscaAsesoria(codigoAsesoria, camara, tipoInstancia.getAbreviatura(), competencia, fechaInicio, zonaOficina);
			if (asesoria == null) {
				asesoria = new Asesoria();
				asesoria.setCamara(camara);
				asesoria.setCodigo(codigoAsesoria);
				asesoria.setCompetencia(competencia);
				asesoria.setTipoInstancia(tipoInstancia);
				asesoria.setFechaInicio(fechaInicio);
				asesoria.setZonaOficina(zonaOficina);
			}
			asesoria.setStatus(0);
			asesoria.setDescripcion(descripcion);
			
			asesoria.setDependencia(buscaDependencia(codigoDependencia));
			//
			if(Strings.isEmpty(asesoria.getUuid())){
				asesoria.setUuid(UUID.randomUUID().toString());
			}
			if (asesoria.getId() == null) {
				getEntityManager().persist(asesoria);
				insertCount++;
			}else{
				updateCount++;
				isNew = false;
			}
			getEntityManager().flush();
			
		}
		return asesoria;
	}

	public static void main(String[] args) {
		File file = new File(args[0]);
		
		AsesoriaLoader asesoriaLoader = new AsesoriaLoader(null, null);
		
		try {
			asesoriaLoader.load(new FileInputStream(file));			
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LoaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
