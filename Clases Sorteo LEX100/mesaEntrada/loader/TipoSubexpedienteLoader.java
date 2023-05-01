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
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.TipoSubexpediente;

public class TipoSubexpedienteLoader extends AbstractConfigurationLoader{

	
	
	private static final String TIPO_SUBEXPEDIENTE_VALUE = "TIPO_SUBEXPEDIENTE";
	private int insertCount = 0; 
	private int updateCount = 0; 
	private int deleteCount = 0;
	private Materia lastMateria = null;
	private boolean deleteOthers = false;
	
	private static final String DELETE_OTHERS_VALUE = "DELETE_OTHERS";

	private List<TipoSubexpediente> tiposSubexpediente = new ArrayList<TipoSubexpediente>();


	public TipoSubexpedienteLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0;
		deleteCount = 0;
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isTipoSubexpedienteRow(row)){
			TipoSubexpediente tipoSubexpediente = cargaTipoSubexpediente(row);
			if (tipoSubexpediente != null) {
				tiposSubexpediente.add(tipoSubexpediente);
			}
		}else if(isTipoDeleteOthersRow(row)){
			processDeleteOthersRow(row);
		}

	}

	protected void end() {
		if (lastMateria != null && deleteOthers) {
			deleteOtherTiposSubexpediente(lastMateria, tiposSubexpediente);
		}
		info("TipoSubexpediente: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas, "+deleteCount+" filas eliminadas (borrado lógico)");
	}


	private boolean isTipoSubexpedienteRow(Cell[] row) {
		return TIPO_SUBEXPEDIENTE_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private boolean isTipoDeleteOthersRow(Cell[] row) {
		return DELETE_OTHERS_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private void processDeleteOthersRow(Cell[] row) throws LoaderException{
		deleteOthers = getBoolean(row, 1, false);
	}
	
	private TipoSubexpediente cargaTipoSubexpediente(Cell[] row) throws LoaderException {
		Materia materia = buscaMateria(getContent(row, 1), true);
		if(materia == null){
			fatal("Proceso abortado");
		} else {
			if ((lastMateria != null) && !lastMateria.equals(materia) && deleteOthers) {
				deleteOtherTiposSubexpediente(lastMateria, tiposSubexpediente);
				tiposSubexpediente.clear();
			}
		}
		lastMateria = materia;
		Integer codigo = getInteger(row, 2, null);
		String abreviatura = getContent(row, 3);
		String descripcion = getContent(row, 4);
		String naturaleza = getContent(row, 5);
		return actualizaTipoSubexpediente(materia, codigo, abreviatura, descripcion, naturaleza);
		
	}

		
	private TipoSubexpediente actualizaTipoSubexpediente(Materia materia, Integer codigo,
			String abreviatura, String descripcion, String naturaleza)  throws LoaderException {
		
		TipoSubexpediente tipoSubexpediente = null;		
		if (getEntityManager() != null) {
			if(materia == null){
				fatal("Proceso abortado");
			}
			tipoSubexpediente = buscaTipoSubexpediente(materia, codigo, abreviatura, descripcion, false);
			if (tipoSubexpediente == null) {
				tipoSubexpediente = new TipoSubexpediente();
				tipoSubexpediente.setMateria(materia);
				tipoSubexpediente.setAbreviatura(abreviatura);
				tipoSubexpediente.setCodigo(0);
			}
			tipoSubexpediente.setStatus(0);
			if(codigo != null){
				tipoSubexpediente.setCodigo(codigo);
			}
			if(abreviatura != null){
				tipoSubexpediente.setAbreviatura(abreviatura);
			}
			tipoSubexpediente.setDescripcion(descripcion);
			tipoSubexpediente.setNaturalezaSubexpediente(naturaleza);
			
			if(Strings.isEmpty(tipoSubexpediente.getUuid())){
				tipoSubexpediente.setUuid(UUID.randomUUID().toString());
			}
			if (tipoSubexpediente.getId() == null) {
				getEntityManager().persist(tipoSubexpediente);
				insertCount++;
			}else{
				updateCount++;
			}
			getEntityManager().flush();
		}
		return tipoSubexpediente;
	}


	private void deleteOtherTiposSubexpediente(Materia materia, List<TipoSubexpediente> tiposSubexpediente) {
		if(materia != null){
			List<Integer> ids = new ArrayList<Integer>();
			for(TipoSubexpediente item: tiposSubexpediente){
				ids.add(item.getId());
			}
			if(ids.size() > 0){
				deleteCount += getEntityManager().createQuery("update TipoSubexpediente set status = -1 where materia = :materia and id not in (:ids)")
				.setParameter("materia", materia)
				.setParameter("ids", ids).executeUpdate();
			}else {
				deleteCount += getEntityManager().createQuery("update TipoSubexpediente set status = -1 where materia = :materia")
				.setParameter("materia", materia)
				.executeUpdate();
			}
		}
	}

	public static void main(String[] args) {
		File file = new File(args[0]);
		
		TipoSubexpedienteLoader sorteoLoader = new TipoSubexpedienteLoader(null, null);
		
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
