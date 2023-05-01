package com.base100.lex100.mesaEntrada.loader;

import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Rubro;
import com.base100.lex100.entity.TipoCaratula;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoProceso;

public class RubroLoader extends AbstractConfigurationLoader{

	
	
	private static final String RUBRO_VALUE = "RUBRO";
	private int insertCount = 0; 
	private int updateCount = 0; 

	public RubroLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isRubroRow(row)){
			cargaRubro(row);
		}
	}

	protected void end() {
		info("Rubro: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}


	private boolean isRubroRow(Cell[] row) {
		return RUBRO_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private Rubro cargaRubro(Cell[] row) throws LoaderException {
		String codigoCamara = getContent(row, 1);
		String codigoMateria = getContent(row, 2);
		String codigoRubro = getContent(row, 3);
		String descripcionRubro = getContent(row, 4);
		String tipoInstancia = getUpperContent(row, 5);

		return actualizaRubro(codigoCamara, codigoMateria, codigoRubro, descripcionRubro, tipoInstancia); 
	}

	private Rubro actualizaRubro(String codigoCamara, String codigoMateria, String codigoRubro,  String descripcionRubro, String codigoTipoInstancia) throws LoaderException {
		
		Rubro rubro = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			Camara camara = buscaCamara(codigoCamara);
			if(camara == null){
				fatal("Proceso abortado");
			}
			Materia materia = buscaMateria(codigoMateria, true);
			if(materia == null){
				fatal("Proceso abortado");
			}
			TipoInstancia tipoInstancia = buscaTipoInstancia(codigoTipoInstancia);
			rubro = buscaRubro(camara, materia, codigoRubro, tipoInstancia, false);
			
			if(rubro == null){
				rubro = new Rubro();
				rubro.setCodigo(codigoRubro);
				rubro.setCamara(camara);
				rubro.setMateria(materia);
			}
			
			rubro.setDescripcion(descripcionRubro);
			
			rubro.setStatus(0);
			if(Strings.isEmpty(rubro.getUuid())){
				rubro.setUuid(UUID.randomUUID().toString());
			}
			if (rubro.getId() == null) {
				getEntityManager().persist(rubro);
				insertCount++;
			}else{
				isNew = false;
				updateCount++;
			}
			
			getEntityManager().flush();
		}
		return rubro;
	
	}


	
}
