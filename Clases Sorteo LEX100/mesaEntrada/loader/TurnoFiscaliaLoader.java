package com.base100.lex100.mesaEntrada.loader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.TurnoFiscalia;

public class TurnoFiscaliaLoader extends AbstractConfigurationLoader{

	private static final String TURNO_FISCALIA = "TURNO_FISCALIA";
	private int insertCount = 0; 
	private int updateCount = 0;
	

	public TurnoFiscaliaLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isTurnoFiscaliaRow(row)){
			cargaTurnoFiscalia(row);
		}
	}

	protected void end() {
		info("TurnoFiscalia: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}


	private boolean isTurnoFiscaliaRow(Cell[] row) {
		return TURNO_FISCALIA.equalsIgnoreCase(row[0].getContents());
	}
	
	private TurnoFiscalia cargaTurnoFiscalia(Cell[] row) throws LoaderException {
		String codigoCamara = getContent(row, 1);		
		Date fecha = getDate(row, 2);
		int fiscalia = getInt(row, 3, -1);
		String codigoOficina = getContent(row, 4);
		return actualizaTurnoFiscalia(codigoCamara, fecha, fiscalia, codigoOficina); 
	}

	
	private TurnoFiscalia actualizaTurnoFiscalia(String codigoCamara,	Date fecha, int fiscalia, String codigoOficina)  throws LoaderException {
		
		TurnoFiscalia turnoFiscalia = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			Camara camara = buscaCamara(codigoCamara);
			if(camara == null){
				fatal("Proceso abortado");
			}
			Oficina oficina = buscaOficina(codigoOficina);
			turnoFiscalia = buscaTurnoFiscalia(fecha,  fiscalia, codigoOficina, false);			
			if(turnoFiscalia == null){
				turnoFiscalia = new TurnoFiscalia();
				turnoFiscalia.setDesdeFecha(fecha);
				turnoFiscalia.setFiscalia(fiscalia);
				turnoFiscalia.setOficina(oficina);
				turnoFiscalia.setTipoOficinaTurno("F");
			}
			turnoFiscalia.setStatus(0);
			if(Strings.isEmpty(turnoFiscalia.getUuid())){
				turnoFiscalia.setUuid(UUID.randomUUID().toString());
			}
			if (turnoFiscalia.getId() == null) {
				getEntityManager().persist(turnoFiscalia);
				insertCount++;
			}else{
				isNew = false;
				updateCount++;
			}
			
			getEntityManager().flush();
		}
		return turnoFiscalia;
	
	}

	private TurnoFiscalia buscaTurnoFiscalia(Date fecha, int fiscalia,
			String codigoOficina, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
