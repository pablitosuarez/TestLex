package com.base100.lex100.mesaEntrada.loader;

import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.TipoInterviniente;
import com.base100.lex100.entity.TipoProceso;

public class TipoIntervinienteLoader extends AbstractConfigurationLoader{

	
	
	private static final String TIPO_INTERVINIENTE_VALUE = "TIPO_INTERVINIENTE";
	private int insertCount = 0; 
	private int updateCount = 0; 

	public TipoIntervinienteLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isTipoIntervinienteRow(row)){
			cargaTipoInterviniente(row);
		}
	}

	protected void end() {
		info("TipoInterviniente: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}


	private boolean isTipoIntervinienteRow(Cell[] row) {
		return TIPO_INTERVINIENTE_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private TipoInterviniente cargaTipoInterviniente(Cell[] row) throws LoaderException {
		Materia materia = buscaMateria(getContent(row, 1), true);		
		if(materia == null){
			fatal("Proceso abortado");
		}
		TipoProceso tipoProceso = buscaTipoProceso(getContent(row, 2), materia);
		if(tipoProceso == null){
			fatal("Proceso abortado");
		}
		String actorDemandado = getUpperContent(row, 3);
		String abreviatura = getContent(row, 4);		
		String descripcion = getContent(row, 5);

		return actualizaTipoInterviniente(materia, tipoProceso, descripcion, actorDemandado, abreviatura);
		
	}

	private TipoInterviniente actualizaTipoInterviniente(Materia materia,
			TipoProceso tipoProceso, String descripcion, String actorDemandado,
			String abreviatura) throws LoaderException {
		TipoInterviniente tipoInterviniente = null;		
		boolean isNew = true;
		isNew = true;
		if (getEntityManager() != null) {
			if(materia == null){
				fatal("Proceso abortado");
			}
			if(tipoProceso == null){
				fatal("Proceso abortado");
			}
			tipoInterviniente = buscaTipoInterviniente(tipoProceso, abreviatura, descripcion, false);
			if(tipoInterviniente == null){
				tipoInterviniente = new TipoInterviniente();
				tipoInterviniente.setDescripcion(descripcion);
				tipoInterviniente.setTipoProceso(tipoProceso);
			}			
			if(actorDemandado != null){
				boolean actor = actorDemandado.toUpperCase().equals("A");
				boolean demandado = actorDemandado.toUpperCase().equals("D");
				if(actor || demandado){
					tipoInterviniente.setNumeroOrden(actor ? 1 : 2);		
				}
			}
			tipoInterviniente.setStatus(0);
			tipoInterviniente.setAbreviatura(abreviatura);
			if(Strings.isEmpty(tipoInterviniente.getUuid())){
				tipoInterviniente.setUuid(UUID.randomUUID().toString());
			}
			if (tipoInterviniente.getId() == null) {
				getEntityManager().persist(tipoInterviniente);
				insertCount++;
			}else{
				isNew = false;
				updateCount++;
			}
			getEntityManager().flush();
		}
		return tipoInterviniente;
	
	}
	
}
