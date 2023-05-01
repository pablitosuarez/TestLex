package com.base100.lex100.mesaEntrada.loader;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.TipoCaratula;
import com.base100.lex100.entity.TipoLetrado;

public class TipoLetradoLoader extends AbstractConfigurationLoader{

	
	
	private static final String TIPO_LETRADO_VALUE = "TIPO_LETRADO";
	private int insertCount = 0; 
	private int updateCount = 0; 

	public TipoLetradoLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isTipoLetradoRow(row)){
			cargaTipoLetrado(row);
		}
	}

	protected void end() {
		info("TipoLetrado: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}


	private boolean isTipoLetradoRow(Cell[] row) {
		return TIPO_LETRADO_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private TipoLetrado cargaTipoLetrado(Cell[] row) throws LoaderException {
		Materia materia = buscaMateria(getContent(row, 1), true);		
		if(materia == null){
			fatal("Proceso abortado");
		}
		String codigo = getContent(row, 2);
		String descripcion = getContent(row, 3);
		int orden = getInt(row, 4, 0);

		return actualizaTipoLetrado(materia, descripcion, codigo, orden);
		
	}

	private TipoLetrado actualizaTipoLetrado(Materia materia,
			String descripcion, String codigo, int orden) throws LoaderException {
		
		TipoLetrado tipoLetrado = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			if(materia == null){
				fatal("Proceso abortado");
			}
			tipoLetrado = buscaTipoLetrado(materia, codigo, descripcion, false);
			if(tipoLetrado == null){
				tipoLetrado = new TipoLetrado();
				tipoLetrado.setMateria(materia);
			}
			tipoLetrado.setCodigo(codigo);
			tipoLetrado.setDescripcion(descripcion);
			tipoLetrado.setOrden(orden);
			tipoLetrado.setStatus(0);
			if(Strings.isEmpty(tipoLetrado.getUuid())){
				tipoLetrado.setUuid(UUID.randomUUID().toString());
			}
			if (tipoLetrado.getId() == null) {
				getEntityManager().persist(tipoLetrado);
				insertCount++;
			}else{
				isNew = false;
				updateCount++;
			}
			
			getEntityManager().flush();
		}
		return tipoLetrado;
	
	}


	
}
