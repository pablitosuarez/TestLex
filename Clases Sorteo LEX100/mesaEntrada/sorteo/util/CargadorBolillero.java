package com.base100.lex100.mesaEntrada.sorteo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.Controller;

import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.enumeration.TipoOficinaCargaEnumeration;
import com.base100.lex100.component.enumeration.TipoOficinaEnumeration;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaCargaExp;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.TipoBolillero;
import com.base100.lex100.mesaEntrada.sorteo.SorteadorFactory;

@Name("cargadorBolillero")
public class CargadorBolillero extends Controller{
	
	private static final int MAX_BOLAS_POR_OFICINA = 10;

	@In(create=true)
    private EntityManager entityManager;
	
	private static final int TIPO_OFICINA_JUZGADO_NACIONAL = 1;
	private static final int TIPO_OFICINA_JUZGADO_FEDERAL = 4;
	private static String rubros = "abcdefghijklmnoABCDEFGHIJKLMNOPQRSTUVWXY12 .";
	private Map<Integer, String> materiaRubros = null;
	
//	private TipoOficina tipoOficinaJuzgadoNacional;

	public static CargadorBolillero instance() {
		return (CargadorBolillero) Component.getInstance(CargadorBolillero.class,true);
	}

	// NO SE USA
//	public void cargaBolillero(){
//		SessionState sessionState = SessionState.instance();
//		Camara camara = sessionState.getGlobalCamara();
//		Oficina oficinaActual = sessionState.getGlobalOficina();
//		if(camara != null && oficinaActual != null){
//			List<Oficina> oficinas;
//			List<String> rubros;
//			
//			oficinas = getOficinas(TIPO_OFICINA_JUZGADO_NACIONAL);
//			
//			for(Materia materia: camara.getMateriaList()){
//				if(CamaraManager.isPenal(camara)){
//					rubros = getRubrosTipoCausa(materia);
//				}else{
//					rubros = getRubrosObjetoJuicio(materia);
//				}
//				cargaBolillero(oficinas, rubros, 1, materia, true);
//			}
//		}
//	}
	
	public void cargaBolillero(Sorteo sorteo, TipoBolillero tipoBolillero, List<Oficina> oficinas, List<String> rubros, int bolasPorOficina, Materia materia, boolean doBlanqueo){		
		if(doBlanqueo){
			entityManager.createQuery("update OficinaCargaExp set cargaTrabajo = 0, numeroExpedientes = 0 where sorteo = :sorteo and tipoBolillero = :tipoBolillero and status <> -1")
			.setParameter("sorteo", sorteo)
			.setParameter("tipoBolillero", tipoBolillero)
			.executeUpdate();
		}
		for(Oficina oficina: oficinas){
			//String rubrosDeLaOficina = getRubrosOficina(oficina);
			for(String rubro: rubros){
				int num = bolasPorOficina < 1 || bolasPorOficina > MAX_BOLAS_POR_OFICINA ? 1 : bolasPorOficina;
				List<OficinaCargaExp> items = findOficinaCargaExp(sorteo, tipoBolillero, oficina, rubro);
				if(items.size() > 0 && doBlanqueo){
					for(OficinaCargaExp item: items){
						//No se resetea la carga de trabajo		
						item.setCargaTrabajo(0);
						item.setMateria(sorteo.getMateria());
						item.setAuditableOperation("CARGA BOLILLERO");
					}
				}
				num -= items.size();
				while(num > 0){
					OficinaCargaExp item = createOficinaCargaExp(sorteo, tipoBolillero, oficina, materia, rubro);		
					item.setAuditableOperation("CARGA BOLILLERO");
					entityManager.persist(item);
					num--;
				}
			}
		}
		entityManager.flush();
		SorteadorFactory.instance().clearColasDeSorteo();
	}
	
	public List<StatusBolillero> verificaBolillero(Sorteo sorteo, TipoBolillero tipoBolillero, List<Oficina> oficinas, List<String> rubros, int bolasPorOficina, Materia materia, boolean returnOnlyErrors){
		List<StatusBolillero> ret = new ArrayList<StatusBolillero>();
		
		List<String> rubrosCreados = findRubrosCreados(sorteo, tipoBolillero);
		for(String rubro:rubrosCreados){
			if(!rubros.contains(rubro)){
				StatusBolillero err = new StatusBolillero(tipoBolillero, rubro, StatusBolillero.STATUS_RUBRO_SOBRANTE);
				ret.add(err);				
			}
		}
			//String rubrosDeLaOficina = getRubrosOficina(oficina);
		for(String rubro: rubros){
			int status = StatusBolillero.STATUS_OK;
			for(Oficina oficina: oficinas){
				int num = bolasPorOficina < 1 || bolasPorOficina > MAX_BOLAS_POR_OFICINA ? 1 : bolasPorOficina;
				List<OficinaCargaExp> items = findOficinaCargaExp(sorteo, tipoBolillero, oficina, rubro);
				if(items.size() == 0){
					status = StatusBolillero.STATUS_RUBRO_NOCREADO;
					break;
				}else{
					if(num > items.size()){
						status = StatusBolillero.STATUS_BOLAS_FALTANTES;
						break;
					}else if(num < items.size()){
						status = StatusBolillero.STATUS_BOLAS_SOBRANTES;
						break;
					}
				}
			}
			if(status != StatusBolillero.STATUS_OK ||!returnOnlyErrors){
				StatusBolillero err = new StatusBolillero(tipoBolillero, rubro, status);
				ret.add(err);									
			}
		} 
		return ret;
	}

	public boolean repararBolillero(Sorteo sorteo, TipoBolillero tipoBolillero, List<Oficina> oficinas, List<String> rubros, int bolasPorOficina, Materia materia){
		
		boolean ret = false;		
		List<String> rubrosCreados = findRubrosCreados(sorteo, tipoBolillero);
		for(String rubro:rubrosCreados){
			if(!rubros.contains(rubro)){
				ret = true;
				borrarRubro(sorteo, tipoBolillero, rubro);
			}
		}
		boolean needCargaSinBanqueo = false;
		for(String rubro: rubros){
			int status = StatusBolillero.STATUS_OK;
			for(Oficina oficina: oficinas){
				int num = bolasPorOficina < 1 || bolasPorOficina > MAX_BOLAS_POR_OFICINA ? 1 : bolasPorOficina;
				List<OficinaCargaExp> items = findOficinaCargaExp(sorteo, tipoBolillero, oficina, rubro);
				if(items.size() == 0){
					needCargaSinBanqueo = true;
				}else{
					if(num > items.size()){
						needCargaSinBanqueo = true;
					}else if(num < items.size()){
						int sobran = items.size() - num;
						for(OficinaCargaExp item:items){
							if(sobran > 0){
								ret = true;
								item.setAuditableOperation("BORRADO BOLAS");
								item.setLogicalDeleted(true);
								sobran--;
							}
						}
					}
				}
			}
		} 
		if(needCargaSinBanqueo){
			ret = true;
			cargaBolillero(sorteo, tipoBolillero, oficinas, rubros, bolasPorOficina, materia, false);
		}
		entityManager.flush();
		return ret;
	}

	private void borrarRubro(Sorteo sorteo, TipoBolillero tipoBolillero, String rubro) {
		List<OficinaCargaExp> items = findOficinaCargaExp(sorteo, tipoBolillero, rubro);
		for(OficinaCargaExp item: items){
			item.setLogicalDeleted(true);
			item.setAuditableOperation("BORRADO RUBRO");
		}
	}

	public void resetBolillero(){
		SessionState sessionState = SessionState.instance();
		Camara camara = sessionState.getGlobalCamara();
		
		String stmt = "update OficinaCargaExp set cargaTrabajo = 0, numeroExpedientes = 0 where materia = :materia and status <> -1";
		
		for(Materia materia: camara.getMateriaList()){
			Query query = entityManager.createQuery(stmt);
			query.setParameter("materia", materia);
			query.executeUpdate();
		}
		entityManager.flush();
	}

	@SuppressWarnings("unchecked")
	private List<OficinaCargaExp> findOficinaCargaExp(Sorteo sorteo, TipoBolillero tipoBolillero, String rubro) {
		String stmt = "from OficinaCargaExp jce where sorteo = :sorteo and rubro = :rubro and tipoBolillero = :tipoBolillero and status <> -1 order by cargaTrabajo";
		Query query = entityManager.createQuery(stmt);
		query.setParameter("sorteo", sorteo);
		query.setParameter("tipoBolillero", tipoBolillero);
		query.setParameter("rubro", rubro);
		List<OficinaCargaExp> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	private List<OficinaCargaExp> findOficinaCargaExp(Sorteo sorteo, TipoBolillero tipoBolillero, Oficina juzgado, String rubro) {
		String stmt = "from OficinaCargaExp jce where oficina = :oficina and sorteo = :sorteo and tipoBolillero = :tipoBolillero and rubro = :rubro and status <> -1 order by cargaTrabajo";
		Query query = entityManager.createQuery(stmt);
		query.setParameter("oficina", juzgado);
		query.setParameter("sorteo", sorteo);
		query.setParameter("tipoBolillero", tipoBolillero);
		query.setParameter("rubro", rubro);
		List<OficinaCargaExp> list = query.getResultList();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private List<String> findRubrosCreados(Sorteo sorteo, TipoBolillero tipoBolillero) {
		String stmt = "Select rubro from OficinaCargaExp jce where sorteo = :sorteo and tipoBolillero = :tipoBolillero and status <> -1 group by rubro";
		Query query = entityManager.createQuery(stmt);
		query.setParameter("sorteo", sorteo);
		query.setParameter("tipoBolillero", tipoBolillero);
		List<String> list = query.getResultList();
		List<String> ret = new ArrayList<String>();
		for(String rubro: list){
			ret.add(rubro);
		}
		return ret;
	}

//	public List<OficinaCargaExp> findOficinaCargaExpByRubro(List<String> rubros) {
//		String stmt = "from OficinaCargaExp jce where rubro in (:rubros)";
//		Query query = entityManager.createQuery(stmt);
//		query.setParameter("rubros", rubros);
//		List<OficinaCargaExp> list = query.getResultList();
//		return list;
//	}
	
	@SuppressWarnings("unchecked")
	public List<OficinaCargaExp> findOficinaCargaExpBySorteo(Sorteo sorteo, TipoBolillero tipoBolillero) {
		String stmt = "from OficinaCargaExp jce where sorteo = :sorteo and tipoBolillero = :tipoBolillero and status <> -1";
		Query query = entityManager.createQuery(stmt);
		query.setParameter("sorteo", sorteo);
		query.setParameter("tipoBolillero", tipoBolillero);
		List<OficinaCargaExp> list = query.getResultList();
		return list;
	}
	

	@SuppressWarnings("unchecked")
	private List<String> getRubrosObjetoJuicio(Materia materia) {
		List<String> ret = new ArrayList<String>();
		String stmt = "select rubroLetter from ObjetoJuicio oj where oj.tipoProceso.materia.id = :materia group by rubroLetter";
		Query query = entityManager.createQuery(stmt);
		query.setParameter("materia", materia.getId());
		List<String> list = query.getResultList();
		StringBuffer sb = new StringBuffer();		
		for(String rubro:list){
			ret.add(rubro);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private List<String> getRubrosTipoCausa(Materia materia) {
		List<String> ret = new ArrayList<String>();
		String stmt = "select rubro from TipoCausa tc where tc.materia.id = :materia group by rubro";
		Query query = entityManager.createQuery(stmt);
		query.setParameter("materia", materia.getId());
		List<String> list = query.getResultList();
		StringBuffer sb = new StringBuffer();		
		for(String rubro:list){
			ret.add(rubro);
		}
		return ret;
	}
	


	private OficinaCargaExp createOficinaCargaExp(Sorteo sorteo, TipoBolillero tipoBolillero, Oficina oficina, Materia materia, String rubro) {
		OficinaCargaExp oficinaCargaExp = new OficinaCargaExp();
		oficinaCargaExp.setSorteo(sorteo);
		oficinaCargaExp.setTipoBolillero(tipoBolillero);
		oficinaCargaExp.setOficina(oficina);
		oficinaCargaExp.setRubro(rubro);
		oficinaCargaExp.setFactorSuma(1);
		oficinaCargaExp.setNumeroExpedientes(0);		
		oficinaCargaExp.setCargaTrabajo(0);
		oficinaCargaExp.setMateria(materia);
		oficinaCargaExp.setStatus(0);
		oficinaCargaExp.setUuid(UUID.randomUUID().toString());
		
		String tipoOficinaCarga = TipoOficinaCargaEnumeration.Type.juzgado.getValue().toString();
		if(oficina.getTipoOficina() != null){
			Integer codigo = oficina.getTipoOficina().getCodigo();
			if(TipoOficinaEnumeration.TIPO_OFICINA_JUZGADO.equals(codigo)){
				tipoOficinaCarga = TipoOficinaCargaEnumeration.Type.juzgado.getValue().toString();
			}else if(TipoOficinaEnumeration.TIPO_OFICINA_SALA_APELACIONES.equals(codigo)){
				tipoOficinaCarga = TipoOficinaCargaEnumeration.Type.sala.getValue().toString();
			}else if(TipoOficinaEnumeration.TIPO_OFICINA_TRIBUNAL_ORAL.equals(codigo)){
				tipoOficinaCarga = TipoOficinaCargaEnumeration.Type.tribunalOral.getValue().toString();				
			}else if(TipoOficinaEnumeration.TIPO_OFICINA_FISCALIA.equals(codigo)){
				tipoOficinaCarga = TipoOficinaCargaEnumeration.Type.fiscalia.getValue().toString();				
			}else if(TipoOficinaEnumeration.TIPO_OFICINA_SECRETARIA_JUZGADO.equals(codigo)){
				tipoOficinaCarga = TipoOficinaCargaEnumeration.Type.secretariaJuzgado.getValue().toString();				
			}else if(TipoOficinaEnumeration.TIPO_OFICINA_MEDIACION.equals(codigo)){
				tipoOficinaCarga = TipoOficinaCargaEnumeration.Type.mediador.getValue().toString();				
			}else if(TipoOficinaEnumeration.TIPO_OFICINA_VOCALIA.equals(codigo)){
				tipoOficinaCarga = TipoOficinaCargaEnumeration.Type.vocalia.getValue().toString();				
			}else if(TipoOficinaEnumeration.TIPO_OFICINA_DEFENSORIA.equals(codigo)){
				tipoOficinaCarga = TipoOficinaCargaEnumeration.Type.defensoria.getValue().toString();				
			}
			oficinaCargaExp.setTipoOficina(tipoOficinaCarga);
		}else{
			oficinaCargaExp.setTipoOficina(TipoOficinaCargaEnumeration.Type.juzgado.getValue().toString());						
		}
		return oficinaCargaExp;
	}

//	private TipoOficina getTipoOficinaJuzgadoNacional() {
//		if(this.tipoOficinaJuzgadoNacional == null){
//			Integer codigo = (Integer) (CodigoTipoOficinaEnumeration.Type.juzgadoNacional.getValue());
//			this.tipoOficinaJuzgadoNacional = (TipoOficina) entityManager.createQuery("from TipoOficina where codigo = :codigo").setParameter("codigo", codigo).getSingleResult();
//		}
//		return this.tipoOficinaJuzgadoNacional;
//	}

	private List<Oficina> getOficinas(int tipoOficina){
		SessionState sessionState = SessionState.instance();
		Query query;
		if(sessionState.getGlobalCamara() != null){
			query = entityManager.createQuery("from Oficina oficina where oficina.tipoOficina.codigo = :tipoOficina and oficina.camara.id = :camara");
			query.setParameter("tipoOficina", tipoOficina);			
			query.setParameter("camara", sessionState.getGlobalCamara().getId());			
		}else{
			query = entityManager.createQuery("from Oficina oficina where oficina.tipoOficina.codigo = :tipoOficina");
			query.setParameter("tipoOficina", tipoOficina);
			
		}
		@SuppressWarnings("unchecked")
		List<Oficina> list = query.getResultList();
		return list;		
	}

	@SuppressWarnings("unchecked")
	public void borraBolillero(Sorteo sorteo, TipoBolillero tipoBolillero) {
		Query query;
		String stmt = "from OficinaCargaExp oce where sorteo = :sorteo and tipoBolillero = :tipoBolillero and status <> -1";		
		query = entityManager.createQuery(stmt);
		query.setParameter("sorteo", sorteo);
		query.setParameter("tipoBolillero", tipoBolillero);
		List<OficinaCargaExp> list = query.getResultList();
		for(OficinaCargaExp item:list){
			item.setAuditableOperation("BORRADO BOLILLERO");
			item.setLogicalDeleted(true);
		}
		entityManager.flush();		
	}
	
}	

