package com.base100.lex100.mesaEntrada.loader;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.TurnoOficina;

public class TurnoOficinaLoader extends AbstractConfigurationLoader{

	private String SORTEO_VALUE = "SORTEO";
	private static final String TURNO_OFICINA_VALUE = "TURNO_OFICINA";
	private Sorteo lastSorteo = null;

	private int insertCount = 0; 
	private int updateCount = 0;
	private int deleteCount = 0; 
	

	public TurnoOficinaLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0;
		deleteCount = 0;
	}
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isSorteoRow(row)){
			lastSorteo = cargaSorteo(row);
			if(lastSorteo == null){
				fatal("Proceso abortado");
			}
			
		} else if(isTurnoOficinaRow(row)){
			cargaTurnoOficina(row, lastSorteo);
		}
	}

	protected void end() {
		info("TurnoOficina: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas, "+deleteCount+" filas eliminadas (borrado lógico)");
	}

	private boolean isSorteoRow(Cell[] row) {
		return SORTEO_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private boolean isTurnoOficinaRow(Cell[] row) {
		return TURNO_OFICINA_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private Sorteo cargaSorteo(Cell[] row) throws LoaderException {
		String codigoCamara = getContent(row, 1);
		String codigoSorteo = getContent(row, 2);
		Date desdeFecha = getDate(row, 3);
		Date hastaFecha = getDate(row, 4);

		Sorteo sorteo = null;
		if (getEntityManager() != null) {
			Camara camara = buscaCamara(codigoCamara);
			if(camara == null){
				fatal("Proceso abortado");
			}
			sorteo = buscaSorteo(codigoSorteo, camara, true);
			if (sorteo != null) {
				deleteTurnosSorteo(sorteo, desdeFecha, hastaFecha);
			}
		}
		return sorteo;
	}

	private void deleteTurnosSorteo(Sorteo sorteo, Date desdeFecha,	Date hastaFecha) {
		deleteCount = getEntityManager().createQuery("update TurnoOficina set status = -1 where sorteo = :sorteo and desdeFecha >= :desdeFecha and hastaFecha <= :hastaFecha")
			.setParameter("sorteo", sorteo)			
			.setParameter("desdeFecha", desdeFecha)
			.setParameter("hastaFecha", hastaFecha)
			.executeUpdate();
		
	}

	private TurnoOficina cargaTurnoOficina(Cell[] row, Sorteo sorteo) throws LoaderException {
		String codigoOficina = getContent(row, 1);
		Date desdeFecha = getDate(row, 3);
		Date hastaFecha = getDate(row, 4);
		return actualizaTurnoOficina(lastSorteo, codigoOficina, desdeFecha, hastaFecha); 
	}
	
	private TurnoOficina actualizaTurnoOficina(Sorteo lastSorteo, String codigoOficina, Date desdeFecha, Date hastaFecha)  throws LoaderException {
		
		TurnoOficina turnoOficina = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			Oficina oficina = buscaOficina(codigoOficina);
			turnoOficina = buscaTurnoOficina(lastSorteo, oficina, desdeFecha, hastaFecha);			
			if(turnoOficina == null){
				turnoOficina = new TurnoOficina();
				turnoOficina.setDesdeFecha(desdeFecha);
				turnoOficina.setHastaFecha(hastaFecha);
				turnoOficina.setSorteo(lastSorteo);
				turnoOficina.setOficina(oficina);
			}
			turnoOficina.setStatus(0);
			if(Strings.isEmpty(turnoOficina.getUuid())){
				turnoOficina.setUuid(UUID.randomUUID().toString());
			}
			if (turnoOficina.getId() == null) {
				getEntityManager().persist(turnoOficina);
				insertCount++;
			}else{
				isNew = false;
				updateCount++;
			}
			
			getEntityManager().flush();
		}
		return turnoOficina;
	}

	@SuppressWarnings("unchecked")
	private TurnoOficina buscaTurnoOficina(Sorteo sorteo, Oficina oficina,	Date desdeFecha, Date hastaFecha) throws LoaderException {
		List<TurnoOficina> list = 
			getEntityManager().createQuery("from TurnoOficina where desdeFecha = :desdeFecha and hastaFecha = :hastaFecha and sorteo = :sorteo and oficina = :oficina")
				.setParameter("desdeFecha", desdeFecha)
				.setParameter("hastaFecha", hastaFecha)
				.setParameter("sorteo", sorteo)
				.setParameter("oficina", oficina)
				.getResultList();
		
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

}
