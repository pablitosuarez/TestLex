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

import com.base100.lex100.component.enumeration.TurnoSorteoEnumeration;
import com.base100.lex100.controller.entity.tipoBolillero.TipoBolilleroSearch;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaSorteo;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.TipoBolillero;

public class SorteoLoader extends AbstractConfigurationLoader{

	
	private String SORTEO_VALUE = "SORTEO";
	private String OFICINA_VALUE = "OFICINA";
	
	private List<OficinaSorteo> oficinas = new ArrayList<OficinaSorteo>();
	private Sorteo lastSorteo = null;
	private int insertCountSorteo = 0; 
	private int updateCountSorteo = 0; 
	private int insertCountOficinaSorteo = 0; 
	private int updateCountOficinaSorteo = 0; 
	private TipoBolillero tipoBolilleroAsignacion = null;
	
	public SorteoLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isSorteoRow(row)){
			deleteOtherOficinas(lastSorteo, oficinas);
			oficinas.clear();
			lastSorteo = cargaSorteo(row);
			if(lastSorteo == null){
				fatal("Proceso abortado");
			}
		}else if(isOficinaRow(row)){
			OficinaSorteo oficinaSorteo = cargaOficina(row, lastSorteo);
			if(oficinaSorteo != null){
				oficinas.add(oficinaSorteo);
			}
		}
	}

	protected void end() {
		if(lastSorteo != null){
			deleteOtherOficinas(lastSorteo, oficinas);
		}
		info("Sorteo: "+insertCountSorteo+" filas añadidas, "+updateCountSorteo+" filas modificadas");
		info("OficinaSorteo: "+insertCountOficinaSorteo+" filas añadidas, "+updateCountOficinaSorteo+" filas modificadas");
	}

	
	private void deleteOtherOficinas(Sorteo sorteo,	List<OficinaSorteo> oficinas) {
		if(sorteo != null){
			List<Integer> ids = new ArrayList<Integer>();
			for(OficinaSorteo item: oficinas){
				ids.add(item.getId());
			}
			if(ids.size() > 0){
				getEntityManager().createQuery("update OficinaSorteo set status = -1 where sorteo = :sorteo and id not in (:ids)")
				.setParameter("sorteo", sorteo)			
				.setParameter("ids", ids).executeUpdate();
			}else {
				getEntityManager().createQuery("update OficinaSorteo set status = -1 where sorteo = :sorteo")
				.setParameter("sorteo", sorteo).executeUpdate();
				
			}
		}
		
	}


	private boolean isOficinaRow(Cell[] row) {
		return OFICINA_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private boolean isSorteoRow(Cell[] row) {
		return SORTEO_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private OficinaSorteo cargaOficina(Cell[] row, Sorteo lastSorteo) throws LoaderException {
		String codigoOficina = getContent(row, 1);
		boolean isTurno =  getBoolean(row, 2, false);
		int orden = getInt(row, 3, 0);
		return actualizaOficinaSorteo(lastSorteo, codigoOficina, isTurno, orden);
	}

	private OficinaSorteo actualizaOficinaSorteo(Sorteo lastSorteo,
			String codigoOficina, boolean isTurno, int orden) throws LoaderException {
		OficinaSorteo oficinaSorteo = null;
		if (getEntityManager() != null) {
			oficinaSorteo = buscaOficinaSorteo(lastSorteo, codigoOficina);
			if (oficinaSorteo == null) {
				Oficina oficina = buscaOficina(codigoOficina);
				if(oficina == null){
					return null;
				}
				oficinaSorteo = new OficinaSorteo();
				oficinaSorteo.setSorteo(lastSorteo);
				oficinaSorteo.setOficina(oficina);
			}
			
			oficinaSorteo.setTurno(isTurno);
			oficinaSorteo.setOrden(orden);
			if(Strings.isEmpty(oficinaSorteo.getUuid())){
				oficinaSorteo.setUuid(UUID.randomUUID().toString());
			}
			oficinaSorteo.setStatus(0);
			if (oficinaSorteo.getId() == null) {
				getEntityManager().persist(oficinaSorteo);
				insertCountOficinaSorteo++;
			}else{
				updateCountOficinaSorteo++;
			}
			getEntityManager().flush();
		}
		return oficinaSorteo;
	}

	private OficinaSorteo buscaOficinaSorteo(Sorteo sorteo,	String codigoOficina) {
		List<OficinaSorteo> list =
			getEntityManager().createQuery("from OficinaSorteo where sorteo = :sorteo and oficina.codigo = :codigoOficina")
				.setParameter("sorteo", sorteo)
				.setParameter("codigoOficina", codigoOficina)
				.getResultList();
			if (list.size() > 0) {
				return list.get(0);
			}else{
				return null;
			}
	}

	private Sorteo cargaSorteo(Cell[] row) throws LoaderException {
		String codigoCamara = getContent(row, 1);
		String codigoSorteo = getContent(row, 2);
		String codigoMesa = getContent(row, 3);		
		String descripcionSorteo = getContent(row, 4);
		String grupoMateria = getUpperContent(row, 5);
		String abreviaturaMateria = getUpperContent(row, 6);
		String tipoInstanciaYRadicacion = getUpperContent(row, 7);
		String tipoInstancia = getLeftPart(tipoInstanciaYRadicacion, "-");
		String tipoRadicacion = getRightPart(tipoInstanciaYRadicacion, "-");
		String tipoOficina = getUpperContent(row, 8);
		String codigoCompetencia = getUpperContent(row, 9);
		int conTurno = getTurno(row, 10, 0);
		int bolasPorOficina = getInt(row, 11, 1);
		int rangoMinimos = getInt(row, 12, 1);
		int minimoNumeroBolas = getInt(row, 13, 1);
		String rubrosAsignacion = getContent(row, 14);
		String rubros = getContent(row, 15);

		// extract tipo Sorteo
		String tipoSorteo = null;
		if(codigoSorteo!=null){			
			int idx1 = codigoSorteo.indexOf("(");
			int idx2 = codigoSorteo.indexOf(")", idx1);
			if(idx1 >= 0 && idx2 >= 0){
				tipoSorteo = codigoSorteo.substring(idx1+1, idx2).trim();
				codigoSorteo = codigoSorteo.substring(0, idx1).trim();
			}
		}

		
		return actualizaSorteo(codigoCamara, codigoSorteo, tipoSorteo, codigoMesa, 
				descripcionSorteo, grupoMateria, abreviaturaMateria, tipoOficina, tipoInstancia, tipoRadicacion, codigoCompetencia, conTurno, bolasPorOficina, rangoMinimos, minimoNumeroBolas, rubrosAsignacion, rubros);
		
	}

	private String getRightPart(String cadena, String sep) {
		String ret = null;
		if(cadena != null && cadena.indexOf(sep) >= 0){
			ret = cadena.substring(cadena.indexOf(sep)+1).trim().toUpperCase();
			if(ret.length() == 0){
				ret = null;
			}			
		}
		return ret;
	}

	private String getLeftPart(String cadena, String sep) {
		String ret = cadena;
		if(cadena != null && cadena.indexOf(sep) >= 0){
			ret = cadena.substring(0, cadena.indexOf(sep)).trim();
			if(ret.length() == 0){
				ret = null;
			}			
		}
		return ret;
	}

	private int getTurno(Cell[] row, int col, Integer defaulValue) throws LoaderException {
		String s = getUpperContent(row, col);
		int ret = defaulValue;
		if(s != null){
			if(s.equals("NO")){
				ret = (Integer)TurnoSorteoEnumeration.Type.sinTurno.getValue();
			}else if(s.equals("SI")){	// MANUAL
				ret = (Integer)TurnoSorteoEnumeration.Type.conTurnoManual.getValue();
			}else if(s.equals("MANUAL")){	// MANUAL
				ret = (Integer)TurnoSorteoEnumeration.Type.conTurnoManual.getValue();
			}else if(s.equals("AUTOMATICO")){
				ret = (Integer)TurnoSorteoEnumeration.Type.conTurnoAutomatico.getValue();;
			}else if(s.equals("CONTINUO")){
				ret = (Integer)TurnoSorteoEnumeration.Type.conTurnoContinuo.getValue();;
			}else{
				unexpectedValue(s, "NO", "SI o MANUAL", "AUTOMATICO", "CONTINUO");
			}
		}
		return ret;
	}

	private TipoBolillero getTipoBolilleroAsignacion() {
		if (tipoBolilleroAsignacion == null) {
			tipoBolilleroAsignacion = TipoBolilleroSearch.getTipoBolilleroAsignacion(getEntityManager());
		}
		return tipoBolilleroAsignacion;
	}
	
	private Sorteo actualizaSorteo(String codigoCamara, String codigoSorteo, String tipoSorteo,
			String codigoMesa, String descripcionSorteo,
			String grupoMateria, String abreviaturaMateria, String tipoOficina, String tipoInstancia, String tipoRadicacion,
			String codigoCompetencia, int conTurno, int bolasPorOficina,
			int rangoMinimos, int minimoNumeroBolas, String rubrosAsignacion, String rubros) throws LoaderException {
		
		Sorteo sorteo = null;
		if (getEntityManager() != null) {
			Camara camara = buscaCamara(codigoCamara);
			if(camara == null){
				fatal("Proceso abortado");
			}
			sorteo = buscaSorteo(codigoSorteo, camara, false);
			if (sorteo == null) {
				sorteo = new Sorteo();
				sorteo.setCamara(camara);
				sorteo.setCodigo(codigoSorteo);
				if(getTipoBolilleroAsignacion() != null) {
					sorteo.getTipoBolilleroList().add(getTipoBolilleroAsignacion());
				}
			}
			Materia materia = buscaMateria(abreviaturaMateria, false);
			
			sorteo.setTipoSorteo(tipoSorteo);
			sorteo.setStatus(0);
			sorteo.setCodigoMesa(codigoMesa);
			sorteo.setCompetencia(buscaCompetencia(camara, codigoCompetencia, false));
			sorteo.setConTurno(conTurno);
			sorteo.setDescripcion(descripcionSorteo);
			sorteo.setGrupoMateria(grupoMateria);			
			sorteo.setMateria(materia);			
			sorteo.setMinimoNumeroBolas(minimoNumeroBolas);
			sorteo.setRangoMinimos(rangoMinimos);
			sorteo.setBolasPorOficina(bolasPorOficina);
			sorteo.setRubrosAsignacion(rubrosAsignacion);
			sorteo.setRubros(rubros);
			sorteo.setTipoInstancia(buscaTipoInstancia(tipoInstancia));
			sorteo.setTipoRadicacion(tipoRadicacion);
			sorteo.setTipoOficina(buscaTipoOficina(tipoOficina));
			if(Strings.isEmpty(sorteo.getUuid())){
				sorteo.setUuid(UUID.randomUUID().toString());
			}
			if (sorteo.getId() == null) {
				getEntityManager().persist(sorteo);
				insertCountSorteo++;
			}else{
				updateCountSorteo++;
			}
			getEntityManager().flush();
		}
		return sorteo;
	}




	public static void main(String[] args) {
		File file = new File(args[0]);
		
		SorteoLoader sorteoLoader = new SorteoLoader(null, null);
		
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
