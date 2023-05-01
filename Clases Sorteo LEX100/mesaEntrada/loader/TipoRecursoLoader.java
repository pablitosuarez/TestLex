package com.base100.lex100.mesaEntrada.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;
import jxl.read.biff.BiffException;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaSorteo;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.TipoRecurso;

public class TipoRecursoLoader extends AbstractConfigurationLoader{

	
	
	private static final String TIPO_RECURSO_VALUE = "TIPO_RECURSO";
	private int insertCount = 0; 
	private int updateCount = 0; 



	public TipoRecursoLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isTipoRecursoRow(row)){
			cargaTipoRecurso(row);
		}
	}

	protected void end() {
		info("TipoRecurso: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}

	private boolean isTipoRecursoRow(Cell[] row) {
		return TIPO_RECURSO_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	
	private TipoRecurso cargaTipoRecurso(Cell[] row) throws LoaderException {
		Materia materia = buscaMateria(getContent(row, 1), true);
		if(materia == null){
			fatal("Proceso abortado");
		}
		String codigoTipoRecurso = getContent(row, 2);
		String rubro = getContent(row, 3);
		String rubro2 = getContent(row, 4);
		String rubroAsignacion = getContent(row, 5);
		String tipoRecursoSala = getContent(row, 6);
		String descripcion = getContent(row, 7);
		String informacion = getContent(row, 8);
		String tipoResolucion = getContent(row, 9);
		int status = getInt(row, 10, 0);
		
		if(isEmpty(tipoRecursoSala)){
			tipoRecursoSala = "R";
		}
		return actualizaTipoRecurso(materia, codigoTipoRecurso, rubro, rubro2, rubroAsignacion, tipoRecursoSala, descripcion, informacion, tipoResolucion, status);
		
	}

		
	private TipoRecurso actualizaTipoRecurso(Materia materia,
			String codigoTipoRecurso, 
			String rubro, 
			String rubro2,
			String rubroAsignacion,
			String tipoRecursoSala,
			String descripcion, 
			String informacion,
			String tipoResolucion,
			int status
			) throws LoaderException {
		
		TipoRecurso tipoRecurso = null;		
		if (getEntityManager() != null) {
			if(materia == null){
				fatal("Proceso abortado");
			}
			tipoRecurso = buscaTipoRecurso(materia, codigoTipoRecurso, false);
			if (tipoRecurso == null) {
				tipoRecurso = new TipoRecurso();
				tipoRecurso.setMateria(materia);
				tipoRecurso.setCodigo(codigoTipoRecurso);
				
			}
			tipoRecurso.setStatus(status);
			tipoRecurso.setRubro(rubro);
			tipoRecurso.setRubro2(rubro2);
			tipoRecurso.setRubroAsignacion(rubroAsignacion);
			tipoRecurso.setDescripcion(descripcion);
			tipoRecurso.setInformacion(informacion);
			tipoRecurso.setTipoSala(tipoRecursoSala);
			if(isEmpty(tipoResolucion)){
				tipoResolucion = "U";
			}
			tipoRecurso.setTipoResolucion(tipoResolucion);
			
			if(Strings.isEmpty(tipoRecurso.getUuid())){
				tipoRecurso.setUuid(UUID.randomUUID().toString());
			}
			if (tipoRecurso.getId() == null) {
				getEntityManager().persist(tipoRecurso);
				insertCount++;
			}else{
				updateCount++;
			}
			getEntityManager().flush();
		}
		return tipoRecurso;
	}




	public static void main(String[] args) {
		File file = new File(args[0]);
		
		TipoRecursoLoader sorteoLoader = new TipoRecursoLoader(null, null);
		
		try {
			sorteoLoader.load(new FileInputStream(file));			
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
