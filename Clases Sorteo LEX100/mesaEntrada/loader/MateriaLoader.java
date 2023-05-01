package com.base100.lex100.mesaEntrada.loader;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Materia;

public class MateriaLoader extends AbstractConfigurationLoader{

	
	
	private static final String MATERIA_VALUE = "MATERIA";
	private int insertCount = 0; 
	private int updateCount = 0; 

	public MateriaLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isMateriaRow(row)){
			cargaMateria(row);
		}
	}

	protected void end() {
		info("Materia: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}


	private boolean isMateriaRow(Cell[] row) {
		return MATERIA_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private Materia cargaMateria(Cell[] row) throws LoaderException {
		int codigo = getInt(row, 1, -1);
		if(codigo < 0){
			fatal("Codigo de Materia erroneo");
		}
		String abreviatura = getContent(row, 2);
		String descripcion = getContent(row, 3);		
		String grupo = getContent(row, 4);
		return actualizaMateria(codigo, abreviatura, descripcion, grupo);
		
	}

	protected List<Materia> getAnyMateria(int codigoMateria, String abreviaturaMateria,  boolean required)  throws LoaderException {
		List<Materia> list = getEntityManager().createQuery("from Materia where status <> -1 and (codigo = :codigo or abreviatura = :abreviatura)")
			.setParameter("codigo", codigoMateria)
			.setParameter("abreviatura", abreviaturaMateria)
			.getResultList();
		return list;
	}


	private Materia actualizaMateria(int codigo, String abreviatura, String descripcion, String grupo) throws LoaderException {
		
		Materia materia = null;		
		if (getEntityManager() != null) {			
			List<Materia> list= getAnyMateria(codigo, abreviatura, false);
			if(list.size() > 1){
				fatal("Hay mas de una Materia con este codigo o abreviatura");
			}
			if(list.size() == 0){
				materia = new Materia();
				materia.setCodigo(codigo);
				materia.setAbreviatura(abreviatura);			
			}else{
				materia = list.get(0);
			}
			
			materia.setDescripcion(descripcion);
			materia.setGrupo(grupo);
			materia.setStatus(0);
			if(Strings.isEmpty(materia.getUuid())){
				materia.setUuid(UUID.randomUUID().toString());
			}
			if (materia.getId() == null) {
				getEntityManager().persist(materia);
				insertCount++;
			}else{
				updateCount++;
			}
			
			getEntityManager().flush();
			
		}
		return materia;
	
	}

	
	
}
