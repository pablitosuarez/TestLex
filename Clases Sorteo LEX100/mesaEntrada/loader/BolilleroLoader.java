package com.base100.lex100.mesaEntrada.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import jxl.Cell;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.component.enumeration.CompetenciaEnumeration;
import com.base100.lex100.component.enumeration.TipoBolilleroEnumeration;
import com.base100.lex100.component.enumeration.TipoOficinaEnumeration;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaCargaExp;

public class BolilleroLoader extends AbstractConfigurationLoader{

	private static final String BOLILLERO = "BOLILLERO";
	private int insertCount = 0; 
	private int updateCount = 0;
	private List<Integer> updatedRows = new ArrayList<Integer>();
	private List<OficinaCargaExp> lastResultList;
	private int lastCamara;
	private String lastCodigoOficina;
	private String lastRubro;
	private String lastSorteo;
	private String lastTipoBolillero;
	private Map<String, String> oficinasMap = new HashMap<String, String>();

	public BolilleroLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isBolilleroRow(row)){
			cargaBolillero(row);
		}
	}

	protected void end() {
		info("Bolillero: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}


	private boolean isBolilleroRow(Cell[] row) {
		return BOLILLERO.equalsIgnoreCase(row[0].getContents());
	}
	
	private OficinaCargaExp cargaBolillero(Cell[] row) throws LoaderException {
		String codigoCamara = getContent(row, 1);		
		String codigoOficina = getContent(row, 2);
		String tipoOficina = getContent(row, 3);
		String competencia = getContent(row, 4);
		String numeroOficina = getContent(row, 5);
		String rubro = getContent(row, 6);
		int carga = getInt(row, 7, 1);
		int factor = getInt(row, 8, 1);
		boolean inhibido = getBoolean(row, 9, false);
		String sorteo = getContent(row, 10);
		String tipoBolillero = getContent(row, 11);
		
		if (Strings.isEmpty(tipoBolillero)) {
			tipoBolillero = TipoBolilleroEnumeration.ASIGNACION;
		}
		
		return actualizaBolillero(codigoCamara, sorteo, codigoOficina, tipoOficina, competencia, numeroOficina, rubro, carga, factor, inhibido, tipoBolillero); 
	}

	
	private OficinaCargaExp actualizaBolillero(String codigoCamara, String sorteo,
			String codigoOficina, String tipoOficina, String competencia, 
			String numeroOficina, String rubro, int carga, int factor,
			boolean inhibido, String tipoBolillero) throws LoaderException {
		
		OficinaCargaExp oficinaCargaExp = null;		
		if (getEntityManager() != null) {
			Camara camara = buscaCamara(codigoCamara);
			if(camara == null){
				fatal("Proceso abortado");
			}
			boolean updated = false;
			if(isEmpty(codigoOficina)){
				codigoOficina =  buscaCodigoOficina(camara.getId(), tipoOficina, competencia, numeroOficina);
			}
			List<OficinaCargaExp> resultList = getResultList(camara.getId(), sorteo, codigoOficina, rubro, tipoBolillero);
			for(OficinaCargaExp oce:resultList){
				if(!updatedRows.contains(oce.getId())){
					oce.setCargaTrabajo(carga);
					oce.setFactorSuma(factor);
					oce.setInhibido(inhibido);
					updatedRows.add(oce.getId());
					updated = true;
					oficinaCargaExp = oce;
					updateCount++;
					getEntityManager().flush();
					break;
				}
			}
			if(!updated){
				fatal("No se ha encontrado  registro OficinaCargaExp ");
			}
		}
		return oficinaCargaExp;
	
	}

	private String buscaCodigoOficina(Integer idCamara, String tipoOficina, String competenciaStr, String numeroOficina) {
		String key = getKeyOficina(idCamara,tipoOficina,competenciaStr,numeroOficina);
		String codigo = oficinasMap.get(key);
		if(codigo == null){
			int tipoOficinaNum = getTipoOficinaNum(tipoOficina);
			Query query = getEntityManager().createQuery(
					"from Oficina oficina where oficina.tipoOficina.codigo = :tipoOficinaNum"+
					" and oficina.camara.id = :idCamara"+
					" and numero = :numeroOficina"+
					" and status <> -1"+(
					competenciaStr == null ? " and competencia is null" : " and competencia.codigo = :competencia")
					);
			query.setParameter("tipoOficinaNum", tipoOficinaNum);
			query.setParameter("idCamara", idCamara);
			query.setParameter("numeroOficina", numeroOficina);
			if(competenciaStr != null){
				query.setParameter("competencia", competenciaStr);
			}
			List<Oficina> list = query.getResultList();
			if(list.size() == 1){
				codigo = list.get(0).getCodigo();
				oficinasMap.put(key, codigo);
			}else{
				return null;
			}
		}
		return codigo;
	}


	private String getKeyOficina(Integer idCamara, String tipoOficina, String competenciaStr, String numeroOficina) {
		return ""+idCamara+"_"+tipoOficina+"_"+(competenciaStr == null ? "" : competenciaStr)+"_"+numeroOficina;
	}

	private int getTipoOficinaNum(String tipoOficina) {
		if("J".equalsIgnoreCase(tipoOficina)){
			return TipoOficinaEnumeration.TIPO_OFICINA_JUZGADO;
		}else if("S".equalsIgnoreCase(tipoOficina)){
			return TipoOficinaEnumeration.TIPO_OFICINA_SALA_APELACIONES;
		}else if("TO".equalsIgnoreCase(tipoOficina)){
			return TipoOficinaEnumeration.TIPO_OFICINA_TRIBUNAL_ORAL;
		}else{
			return -1;
		}
	}

	@SuppressWarnings("unchecked")
	private List<OficinaCargaExp> getResultList(Integer idCamara, String sorteo, String codigoOficina, String rubro, String tipoBolillero) {
		List<OficinaCargaExp> ret = null;
		if(lastResultList != null){
			if(lastCamara == idCamara && 
					lastCodigoOficina != null && 
					lastCodigoOficina.equals(codigoOficina) &&
					lastRubro != null &&
					lastRubro.equals(rubro) &&
					equals(sorteo, lastSorteo) &&
					equals(tipoBolillero, lastTipoBolillero)
					){
				ret = lastResultList;
			}
		}
		if(ret == null){
			String stmt = "from OficinaCargaExp where oficina.codigo = :codigoOficina and oficina.camara.id = :idCamara and rubro = :rubro and status <> -1";
			if(sorteo != null){
				stmt += " and sorteo.codigo = :sorteo";
			}
			if(tipoBolillero != null){
				stmt += " and tipoBolillero.codigo = :tipoBolillero";
			}
			Query query = getEntityManager().createQuery(stmt);
			query.setParameter("codigoOficina", codigoOficina);
			query.setParameter("idCamara", idCamara);
			query.setParameter("rubro", rubro);
			if(sorteo != null){
				query.setParameter("sorteo", sorteo);
			}
			if(tipoBolillero != null){
				query.setParameter("tipoBolillero", tipoBolillero);
			}
			ret = query.getResultList();
			lastResultList = ret;
			lastCodigoOficina = codigoOficina;
			lastCamara = idCamara;
			lastRubro = rubro;
			lastSorteo = sorteo;
			lastTipoBolillero = tipoBolillero;
		}
		return ret;
	}

	private boolean equals(String s1, String s2) {
		if(s1 == null || s2 == null){
			return s1 == s2;
		}else{
			return s1.equals(s2);
		}
	}



	
}
