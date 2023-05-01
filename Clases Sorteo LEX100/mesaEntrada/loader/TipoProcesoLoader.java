package com.base100.lex100.mesaEntrada.loader;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.TipoCaratula;
import com.base100.lex100.entity.TipoProceso;

public class TipoProcesoLoader extends AbstractConfigurationLoader{

	
	
	private static final String UNIQUE_TIPO_PROCESO_VALUE = "UNIQUE_TIPO_PROCESO";
	private static final String TIPO_PROCESO_VALUE = "TIPO_PROCESO";
	private static final String TIPO_CARATULA_VALUE = "TIPO_CARATULA";
	private int insertTipoProcesoCount = 0; 
	private int updateTipoProcesoCount = 0; 
	private int insertTipoCaratulaCount = 0; 
	private int updateTipoCaratulaCount = 0; 

	public TipoProcesoLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertTipoProcesoCount = 0; 
		updateTipoProcesoCount = 0; 
		insertTipoCaratulaCount = 0; 
		updateTipoCaratulaCount = 0;
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isTipoProcesoRow(row)){
			cargaTipoProceso(row, false);
		}else if(isUniqueTipoProcesoRow(row)){
			cargaTipoProceso(row, true);
		}else if(isTipoCaratulaRow(row)){
			cargaTipoCaratula(row);
		}
	}

	protected void end() {
		if(insertTipoCaratulaCount > 0 || updateTipoCaratulaCount > 0){
			info("TipoCaratula: "+insertTipoCaratulaCount+" filas añadidas, "+updateTipoCaratulaCount+" filas modificadas");
		}
		info("TipoProceso: "+insertTipoProcesoCount+" filas añadidas, "+updateTipoProcesoCount+" filas modificadas");
	}


	private boolean isTipoProcesoRow(Cell[] row) {
		return TIPO_PROCESO_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private boolean isUniqueTipoProcesoRow(Cell[] row) {
		return UNIQUE_TIPO_PROCESO_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private boolean isTipoCaratulaRow(Cell[] row) {
		return TIPO_CARATULA_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private TipoCaratula cargaTipoCaratula(Cell[] row) throws LoaderException {
		String codigo = getContent(row, 1);
		String descripcion = getContent(row, 2);

		return actualizaTipoCaratula(codigo, descripcion);
	}

	private TipoCaratula actualizaTipoCaratula(String codigo, String descripcion) throws LoaderException {
		
		TipoCaratula tipoCaratula = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			tipoCaratula = buscaTipoCaratula(codigo, false);
			
			if(tipoCaratula == null){
				tipoCaratula = new TipoCaratula();
				tipoCaratula.setCodigo(Integer.valueOf(codigo));
			}
			
			tipoCaratula.setDescripcion(descripcion);
			
			tipoCaratula.setStatus(0);
			if(Strings.isEmpty(tipoCaratula.getUuid())){
				tipoCaratula.setUuid(UUID.randomUUID().toString());
			}
			if (tipoCaratula.getId() == null) {
				getEntityManager().persist(tipoCaratula);
				insertTipoCaratulaCount++;
			}else{
				isNew = false;
				updateTipoCaratulaCount++;
			}
			
			getEntityManager().flush();
		}
		return tipoCaratula;
	
	}

	private TipoProceso cargaTipoProceso(Cell[] row, boolean uniqueByMateria) throws LoaderException {
		Materia materia = buscaMateria(getContent(row, 1), true);		
		if(materia == null){
			fatal("Proceso abortado");
		}
		String codigo = getContent(row, 2);
		String descripcion = getContent(row, 3);
		String tipoCaratula = getContent(row, 4);

		return actualizaTipoProceso(materia, descripcion, codigo, tipoCaratula, uniqueByMateria);
		
	}

	private TipoProceso actualizaTipoProceso(Materia materia,
			String descripcion, String codigo, String tipoCaratula, boolean uniqueByMateria) throws LoaderException {
		
		TipoProceso tipoProceso = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			if(materia == null){
				fatal("Proceso abortado");
			}
			Integer codigo_entero = null;
			if(!Strings.isEmpty(codigo)){
				codigo_entero = Integer.valueOf(codigo);
			}
			if(uniqueByMateria){
				List<TipoProceso> list = buscaTiposProceso(materia);
				if(list.size()>1){
					fatal("Hay mas de un Tipo de Proceso para esta materia");
				}else if(list.size()==1){
					tipoProceso = list.get(0);
				}
			}
			if(tipoProceso == null){
				if(!Strings.isEmpty(descripcion)){
					tipoProceso = buscaTipoProceso(materia, descripcion, false);
					if(tipoProceso != null && codigo_entero != null){
						tipoProceso.setCodigo(codigo_entero);
					}
				}else if(codigo_entero != null){
					tipoProceso = buscaTipoProceso(materia, codigo_entero, false);
					if(descripcion != null){
						tipoProceso.setDescripcion(descripcion);
					}
				}
			}
			if(tipoProceso == null){
				tipoProceso = new TipoProceso();
				tipoProceso.setCodigo(codigo_entero);
				tipoProceso.setDescripcion(descripcion);
				tipoProceso.setMateria(materia);
			}
			Integer codigo_caratula_entero = null;
			if(!Strings.isEmpty(tipoCaratula)){
				codigo_caratula_entero = Integer.valueOf(tipoCaratula);
				tipoProceso.setTipoCaratula(buscaTipoCaratula(codigo_caratula_entero, true));
			}
			tipoProceso.setStatus(0);
			if(Strings.isEmpty(tipoProceso.getUuid())){
				tipoProceso.setUuid(UUID.randomUUID().toString());
			}
			if (tipoProceso.getId() == null) {
				getEntityManager().persist(tipoProceso);
				insertTipoProcesoCount++;
			}else{
				isNew = false;
				updateTipoProcesoCount++;
			}
			
			getEntityManager().flush();
		}
		return tipoProceso;
	
	}
	
}
