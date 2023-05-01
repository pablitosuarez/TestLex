package com.base100.lex100.mesaEntrada.loader;

import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;

public class CompetenciaLoader extends AbstractConfigurationLoader{

	
	
	private static final String COMPETENCIA_VALUE = "COMPETENCIA";
	private int insertCount = 0; 
	private int updateCount = 0; 

	public CompetenciaLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isCompetenciaRow(row)){
			cargaCompetencia(row);
		}
	}

	protected void end() {
		info("Competencia: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}


	private boolean isCompetenciaRow(Cell[] row) {
		return COMPETENCIA_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private Competencia cargaCompetencia(Cell[] row) throws LoaderException {
		Camara camara = buscaCamara(getContent(row, 1));		
		if(camara == null){
			fatal("Proceso abortado");
		}
		String codigo = getContent(row, 2);
		String descripcion = getContent(row, 3);
		String grupo = getContent(row, 4);

		return actualizaCompetencia(camara, codigo, descripcion, grupo);
		
	}

	private Competencia actualizaCompetencia(Camara camara, String codigo, String descripcion, String grupo) throws LoaderException {
		
		Competencia competencia = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			if(camara == null){
				fatal("Proceso abortado");
			}
			if(Strings.isEmpty(codigo)){
				fatal("codigo de competencia requerido");
			}
			competencia = buscaCompetencia(camara, codigo, false);
			if(competencia == null){
				competencia = new Competencia();
				competencia.setCodigo(codigo);
				competencia.setCamara(camara);
			}
			competencia.setDescripcion(descripcion);
			competencia.setGrupo(grupo);
			
			competencia.setStatus(0);
			if(Strings.isEmpty(competencia.getUuid())){
				competencia.setUuid(UUID.randomUUID().toString());
			}
			if (competencia.getId() == null) {
				getEntityManager().persist(competencia);
				insertCount++;
			}else{
				isNew = false;
				updateCount++;
			}
			
			getEntityManager().flush();
		}
		return competencia;
	
	}

	
	
}
