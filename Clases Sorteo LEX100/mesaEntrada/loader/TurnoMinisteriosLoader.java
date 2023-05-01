package com.base100.lex100.mesaEntrada.loader;

import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.component.enumeration.TipoOficinaTurnoEnumeration;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.TurnoFiscalia;

public class TurnoMinisteriosLoader extends AbstractConfigurationLoader{

	private static final String TURNO_FISCALIA = "TURNO_FISCALIA";
	private static final String TURNO_DEFENSORIA = "TURNO_DEFENSORIA";
	private static final String TURNO_ASESORIA = "TURNO_ASESORIA";
	private int insertCountFicalia = 0;
	private int updateCountFicalia = 0;	
	private int insertCountDefensoria = 0;
	private int updateCountDefensoria = 0;
	private int insertCountAsesoria = 0;
	private int updateCountAsesoria = 0;
	

	public TurnoMinisteriosLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCountFicalia = 0;
		updateCountFicalia = 0;	
		insertCountDefensoria = 0;
		updateCountDefensoria = 0;
		insertCountAsesoria = 0;
		updateCountAsesoria = 0;
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isTurnoFiscaliaRow(row) || isTurnoDefensoriaRow(row) || isTurnoAsesoriaRow(row)){
			cargaTurnoMinisterio(row);
		}
	}

	protected void end() {
		info("TurnoFiscalia: "+insertCountFicalia+" filas añadidas, "+updateCountFicalia+" filas modificadas");
		info("TurnoDefensoria: "+insertCountDefensoria+" filas añadidas, "+updateCountDefensoria+" filas modificadas");
		info("TurnoAsesoria: "+insertCountAsesoria+" filas añadidas, "+updateCountAsesoria+" filas modificadas");
	}


	private boolean isTurnoFiscaliaRow(Cell[] row) {
		return TURNO_FISCALIA.equalsIgnoreCase(row[0].getContents());
	}
	
	private boolean isTurnoDefensoriaRow(Cell[] row) {
		return TURNO_DEFENSORIA.equalsIgnoreCase(row[0].getContents());
	}	
	
	private boolean isTurnoAsesoriaRow(Cell[] row) {
		return TURNO_ASESORIA.equalsIgnoreCase(row[0].getContents());
	}
	
	private TurnoFiscalia cargaTurnoMinisterio(Cell[] row) throws LoaderException {
		String codigoCamara = getContent(row, 1);		
		Date fecha = getDate(row, 2);
		int ministerio = getInt(row, 3, -1);
		String codigoOficina = getContent(row, 4);
		String tipoOficinaTurno = "";
		if(isTurnoFiscaliaRow(row)){
			tipoOficinaTurno = TipoOficinaTurnoEnumeration.Type.fiscalia.getValue().toString();
		}else if(isTurnoDefensoriaRow(row)){
			tipoOficinaTurno = TipoOficinaTurnoEnumeration.Type.defensoria.getValue().toString();
		}else if(isTurnoAsesoriaRow(row)){
			tipoOficinaTurno = TipoOficinaTurnoEnumeration.Type.asesoria.getValue().toString();
		}
		return actualizaTurnoMinisterio(codigoCamara, fecha, ministerio, codigoOficina, tipoOficinaTurno); 
	}

	
	private TurnoFiscalia actualizaTurnoMinisterio(String codigoCamara,	Date fecha, int ministerio, String codigoOficina, String tipoOficinaTurno)  throws LoaderException {
		
		TurnoFiscalia turnoMinisterio = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			Camara camara = buscaCamara(codigoCamara);
			if(camara == null){
				fatal("Proceso abortado");
			}
			Oficina oficina = buscaOficina(codigoOficina);
			turnoMinisterio = buscaTurnoMinisterio(fecha,  ministerio, codigoOficina, false, tipoOficinaTurno);			
			if(turnoMinisterio == null){
				turnoMinisterio = new TurnoFiscalia();
				turnoMinisterio.setDesdeFecha(fecha);
				turnoMinisterio.setFiscalia(ministerio);
				turnoMinisterio.setOficina(oficina);
				turnoMinisterio.setTipoOficinaTurno(tipoOficinaTurno);
			}
			turnoMinisterio.setStatus(0);
			if(Strings.isEmpty(turnoMinisterio.getUuid())){
				turnoMinisterio.setUuid(UUID.randomUUID().toString());
			}
			if (turnoMinisterio.getId() == null) {
				getEntityManager().persist(turnoMinisterio);
			}else{
				isNew = false;
			}
			updateCounter(tipoOficinaTurno, isNew);
			
			getEntityManager().flush();
		}
		return turnoMinisterio;
	
	}

	
	private void updateCounter(String tipoOficinaTurno, boolean isNew){
		if(TipoOficinaTurnoEnumeration.Type.fiscalia.getValue().toString().equals(tipoOficinaTurno)){
			if(isNew){
				insertCountFicalia++;
			}else{
				updateCountFicalia++;
			}			
		}else if(TipoOficinaTurnoEnumeration.Type.defensoria.getValue().toString().equals(tipoOficinaTurno)){
			if(isNew){
				insertCountDefensoria++;
			}else{
				updateCountDefensoria++;
			}	
		}else if(TipoOficinaTurnoEnumeration.Type.asesoria.getValue().toString().equals(tipoOficinaTurno)){
			if(isNew){
				insertCountAsesoria++;
			}else{
				updateCountAsesoria++;
			}	
		}
	}


	
}
