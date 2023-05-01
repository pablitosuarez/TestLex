package com.base100.lex100.mesaEntrada.conexidad;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.Controller;

import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.enumeration.TipoVinculadoEnumeration;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.VinculadosExp;
import com.base100.lex100.manager.expediente.ExpedienteManager;


@Name("familiaManager")
@Scope(ScopeType.CONVERSATION)
public class FamiliaManager extends Controller{

	private static final long serialVersionUID = -6998882517860250686L;

	@In(create=true)
	EntityManager entityManager;
	
	private Map<Integer, List<Integer>> expedientesProcesadosMap = new HashMap<Integer, List<Integer>>();
	private List<Expediente> familiaExpedienteExtendida = null;
	private List<Expediente> familiaExpedienteExtendidaVisible = null;
	private boolean diversidadJuzgado;
	private boolean diversidadSala;
	private Boolean hasConexos = null;
	
	public static FamiliaManager instance() {
		return (FamiliaManager) Component.getInstance(FamiliaManager.class,true);
	}
	
	
	public void test(Integer idExpediente){
		List<Expediente> expedienteList = new ArrayList<Expediente>();
		expedienteList = getFamiliaExpedienteExtendida(entityManager.find(Expediente.class, idExpediente));
		for (Expediente expediente : expedienteList) {
			System.out.println(expediente.getNumero() + "/" +expediente.getAnio());
		}
	}

	private static List<Expediente> getHijos (EntityManager entityManager, Expediente padre) {
		return getHijos (entityManager, padre.getId());
	}
	
	@SuppressWarnings("unchecked")
	private static List<Expediente> getHijos (EntityManager entityManager, Integer padreId) {
		List<Expediente> resultList= new ArrayList<Expediente>();
		List<Expediente> hijosList = entityManager.createQuery("from Expediente where expedienteOrigen.id = :origenId and naturaleza not in ('L', 'M', 'A', 'D') and status <> -1 order by numeroSubexpediente").
			setParameter("origenId", padreId).
			getResultList();

		for(Expediente hijo: hijosList) {
			resultList.add(hijo);
			resultList.addAll(getHijos(entityManager, hijo));
		}
		
		return resultList;
	}

	@SuppressWarnings("unchecked")
	private static List<Integer> getHijosIds (EntityManager entityManager, Integer padreId) {
		List<Integer> resultList= new ArrayList<Integer>();
		List<Integer> hijosIdList = entityManager.createQuery("select id from Expediente where expedienteOrigen.id = :origenId and naturaleza not in ('L', 'M', 'A', 'D') and status <> -1 order by numeroSubexpediente").
			setParameter("origenId", padreId).
			getResultList();

		for(Integer hijoId: hijosIdList) {
			resultList.add(hijoId);
			resultList.addAll(getHijosIds(entityManager, hijoId));
		}
		
		return resultList;
	}

	/**
	 * Busqueda de familia de un expediente en forma recursiva hasta el nivel mas bajo del arbol
	 * @param entityManager {@link EntityManager} con con persistence context
	 * @param expediente {@link Expediente} padre
	 * @return {@link List}a de expedientes
	 */
	@SuppressWarnings("unchecked")
	public static List<Expediente> getFamiliaDirectaExpediente(EntityManager entityManager, Expediente expediente) {
		Expediente principal =  ExpedienteManager.getExpedienteBase(entityManager, expediente);
		if(principal == null) {
			principal = expediente;
		}
		
		/* IMPORTANTE
		 * La manera de buscar que se utilizaba antes en ciertos casos no terminaba nunca,
		 * ya que se hacia recursivamente a nivel procesamiento y demoraba una eternidad.
		 * 
		 * Actualmente se delega la recursividad a la BD con la utilizacion de funciones propias de Oracle
		 */
		List<Expediente> expedienteList = new ArrayList<Expediente>();
		List<Integer> expedientes = entityManager
			.createNativeQuery("SELECT id_expediente FROM expediente WHERE naturaleza_expediente not in ('L', 'M', 'A', 'D') and status=0 START WITH id_expediente = :idExpediente CONNECT BY PRIOR id_expediente = id_expediente_origen ORDER BY numero_subexpediente asc")
			.setParameter("idExpediente", principal.getId())
			.getResultList();
		
		for (Object expFamiliaId : expedientes) {
			Integer idExpediente = ((BigDecimal)expFamiliaId).intValue();
			expedienteList.add(entityManager.find(Expediente.class, idExpediente));
		}
		return expedienteList;
	}

	public List<Expediente> getFamiliaExpedienteExtendidaVisible(Expediente expediente){
		getFamiliaExpedienteExtendida(expediente);
		return familiaExpedienteExtendidaVisible;
	}
	
	public List<Expediente> getFamiliaExpedienteExtendida(Expediente expediente){
		if(familiaExpedienteExtendida == null){
			familiaExpedienteExtendida = new ArrayList<Expediente>();
			Expediente expedientePrincipal = ExpedienteManager.getExpedienteBase(entityManager, expediente);
			if(expedientePrincipal != null){
				Integer idExpediente = expedientePrincipal.getId();
				if(!expedientesProcesadosMap.containsKey(idExpediente)){
					List<Integer> relacionadosList = new ArrayList<Integer>();
					relacionadosList.addAll(processExpediente(idExpediente));
					expedientesProcesadosMap.put(idExpediente, relacionadosList);
				}
				familiaExpedienteExtendida =  resolveExpedientesId(expedientesProcesadosMap.get(idExpediente));
			}
			diversidadJuzgado = hasDiversidad(expediente, TipoRadicacionEnumeration.Type.juzgado.getValue().toString());
			diversidadSala = hasDiversidad(expediente, TipoRadicacionEnumeration.Type.sala.getValue().toString());
		}
//		familiaExpedienteExtendidaVisible = familiaExpedienteExtendida;
//		if(familiaExpedienteExtendidaVisible.size() > 100){
//			familiaExpedienteExtendidaVisible = familiaExpedienteExtendidaVisible.subList(0, 100);
//		}
		return familiaExpedienteExtendida;
	}
	
	
	
	public boolean hasDiversidadJuzgado(Expediente expediente){
		return diversidadJuzgado;
	}
	
	public boolean hasDiversidadSala(Expediente expediente){
		return diversidadSala;
	}
	
	private boolean hasDiversidad(Expediente expediente, String tipo){
		Set<Integer> diversidadMap = new HashSet<Integer>();
		for (Expediente exp : getFamiliaExpedienteExtendida(expediente)) {
			OficinaAsignacionExp oficinaAsignacionExp = ExpedienteManager.instance().getRadicacion(exp, tipo);
			if(oficinaAsignacionExp != null && oficinaAsignacionExp.getOficina() != null){
				diversidadMap.add(oficinaAsignacionExp.getOficina().getId());
			}
		}
		return diversidadMap.size() > 1 ? true : false;		
	}
	
	
	public void reset(){
		expedientesProcesadosMap.clear();
		familiaExpedienteExtendida = null;
		familiaExpedienteExtendidaVisible = null;
		hasConexos = null;
		diversidadJuzgado = false;
		diversidadSala = false;
	}
	
	public void resetExpediente(Integer idExpediente){
		if(idExpediente != null){
			expedientesProcesadosMap.remove(idExpediente);
			familiaExpedienteExtendida = null;
			familiaExpedienteExtendidaVisible = null;
			hasConexos = null;
		}
	}
	
	
	private List<Integer> processExpediente(Integer idExpediente){
		List<Integer> list = new ArrayList<Integer>();
		Map<Integer, Boolean> expedientesMap = new HashMap<Integer, Boolean>();
		boolean finProcesamiento = false;
		expedientesMap.put(idExpediente, false);
		while(!finProcesamiento){
			Set<Integer> keySet = new HashSet<Integer>(expedientesMap.keySet());
			for (Integer idExpedienteActual : keySet) {
				if(Boolean.FALSE.equals(expedientesMap.get(idExpedienteActual))){
					expedientesMap.put(idExpedienteActual, true);
					List<Integer> relacionadosList = getExpedientesRelacionados(idExpedienteActual);
					for (Integer idExpedienteRelacionado : relacionadosList) {
						if(!expedientesMap.containsKey(idExpedienteRelacionado)){
							expedientesMap.put(idExpedienteRelacionado, false);
						}
					}
				}
			}
			finProcesamiento = !expedientesMap.containsValue(Boolean.FALSE);
		}
		for(Integer idExp: expedientesMap.keySet()){
			list.add(idExp);
		}
		return list;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Expediente> resolveExpedientesId(List<Integer> idExpedienteList){
		List<Expediente> resueltosList = new ArrayList<Expediente>();
		if(idExpedienteList != null && !idExpedienteList.isEmpty()){
			resueltosList = entityManager.createQuery("select e from Expediente e where " +this.getCondicionLista(idExpedienteList))
			.getResultList();
		}
		return resueltosList;
	}
	
	private String getCondicionLista(List<Integer> idExpedienteList){
		
		List<Integer> lista = idExpedienteList;
		StringBuilder condicion = new StringBuilder();
		while(lista.size() > 0){
			if(lista.size()>900){
				List<Integer> aEnviar = new ArrayList<Integer>();
				for(int i = 0; i < 900 && i < lista.size(); i++){
					aEnviar.add(idExpedienteList.get(i));
				}
				condicion.append(" e.id in ("
						+ aEnviar.toString().replace("[", "").replace("]", "") + ") or ");
				for(int i = 0; i < 900 && lista.size() > 0; i++){
					lista.remove(0);
				}
			}else{
				List<Integer> aEnviar = new ArrayList<Integer>();
				for(int i = 0; i < lista.size(); i++){
					aEnviar.add(idExpedienteList.get(i));
				}
				condicion.append(" e.id in ("
						+ aEnviar.toString().replace("[", "").replace("]", "") + ")");
				for(int i = 0; lista.size() > 0; i++){
					lista.remove(0);
				}
			}
		}
		
		return condicion.toString();
	}
	
	
	private List<Integer> getExpedientesRelacionados(Integer idExpediente){
		Set<Integer> relacionadosSet = new HashSet<Integer>();
		
		Expediente expediente = entityManager.find(Expediente.class, idExpediente);
		if(expediente != null && ExpedienteManager.isPrincipal(expediente)){
			relacionadosSet.add(idExpediente);
			relacionadosSet.addAll(getHijosIds (entityManager, idExpediente));
		}
		
		relacionadosSet.addAll(getExpedientesVinculadosDirecta(idExpediente));
		relacionadosSet.addAll(getExpedientesVinculadosInversa(idExpediente));

//		Ya no es necesario, la conexidad declarada y la principal se añaden en vinculados como conexos
//		relacionadosSet.addAll(getConexidad(idExpediente));

		return new ArrayList<Integer>(relacionadosSet);
	}
	
	
//	private List<Integer> getConexidad(Integer idExpediente){
//		Set<Integer> conexidadSet = new HashSet<Integer>();
//		conexidadSet.addAll(getConexidadPrincipal(idExpediente));
//		conexidadSet.addAll(getConexidadDeclarada(idExpediente));
//		return new ArrayList<Integer>(conexidadSet);
//	}
	
//	private List<Integer> getConexidadPrincipal(Integer idExpediente){
//		Set<Integer> conexidadPrincipalSet = new HashSet<Integer>();
//		Expediente expediente = entityManager.find(Expediente.class, idExpediente);
//		//Directa
//		Expediente expedienteConexidadPrincipal = ConexidadManager.instance().findExpedienteConexidadPrincipal(expediente);
//		if(expedienteConexidadPrincipal != null){
//			conexidadPrincipalSet.add(expedienteConexidadPrincipal.getId());
//		}
//		//Inversa
//		List<Expediente> expedienteConexidadPrincipalInversaList = ConexidadManager.instance().findExpedientesConexidadPrincipalInversa(expediente);
//		for (Expediente expedienteInverso : expedienteConexidadPrincipalInversaList) {
//			conexidadPrincipalSet.add(expedienteInverso.getId());
//		}
//		return new ArrayList<Integer>(conexidadPrincipalSet);
//	}
	
//	private List<Integer> getConexidadDeclarada(Integer idExpediente){
//		Set<Integer> conexidadDeclaradaSet = new HashSet<Integer>();
//		Expediente expediente = entityManager.find(Expediente.class, idExpediente);
//		//Directa
//		if(expediente.getConexidadDeclarada() != null && !expediente.getConexidadDeclarada().isEmpty()) {
//			ConexidadManager.instance().solveExpedienteConexidadDeclarada(expediente.getConexidadDeclarada());
//			if(expediente.getConexidadDeclarada().getExpedienteConexo() != null){
//				conexidadDeclaradaSet.add(expediente.getConexidadDeclarada().getExpedienteConexo().getId());
//			}
//		}		
//		//Inversa
//		List<Expediente> expedienteConexidadDeclaradaInversaList = ConexidadManager.instance().getExpedientesConexidadDeclaradaInversa(expediente);
//		for (Expediente expedienteInverso : expedienteConexidadDeclaradaInversaList) {
//			conexidadDeclaradaSet.add(expedienteInverso.getId());
//		}
//		return new ArrayList<Integer>(conexidadDeclaradaSet);
//	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getExpedientesVinculadosDirecta(Integer idExpediente){
		return entityManager.createQuery("select e.expedienteRelacionado.id from VinculadosExp e where e.expediente.id = :id and e.expedienteRelacionado is not null and e.tipoVinculado in (:tipoVinculadoFamilia)").
		setParameter("id", idExpediente).
		setParameter("tipoVinculadoFamilia", TipoVinculadoEnumeration.getTiposVinculadosConexos()).
		getResultList();
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getExpedientesVinculadosInversa(Integer idExpediente){
		return entityManager.createQuery("select e.expediente.id from VinculadosExp e where e.expedienteRelacionado.id = :id and e.expediente is not null and e.tipoVinculado in (:tipoVinculadoFamilia)").
		setParameter("id", idExpediente).
		setParameter("tipoVinculadoFamilia", TipoVinculadoEnumeration.getTiposVinculadosConexos()).
		getResultList();
	}
	
	public static Expediente getExpedienteVinculadoMediacion(EntityManager entityManager, Expediente expediente){
		return getExpedienteVinculadoByTipo(entityManager, expediente, TipoVinculadoEnumeration.Type.mediacion.getValue().toString());
	}
	
	public Expediente getExpedienteVinculadoConexoJuzgado(Expediente expediente){
		return getExpedienteVinculadoByTipo(expediente, TipoVinculadoEnumeration.Type.conexo_Juzgado.getValue().toString());
	}
	
	public Expediente getExpedienteVinculadoConexoSala(Expediente expediente){
		return getExpedienteVinculadoByTipo(expediente, TipoVinculadoEnumeration.Type.conexo_Sala.getValue().toString());
	}

	@SuppressWarnings("unchecked")
	private Expediente getExpedienteVinculadoByTipo(Expediente expediente, String tipo){
		return getExpedienteVinculadoByTipo(entityManager, expediente, tipo);
	}
	
	@SuppressWarnings("unchecked")
	public static Expediente getExpedienteVinculadoByTipo(EntityManager entityManager, Expediente expediente, String tipo){
		VinculadosExp vinculado = getVinculadoByTipo(entityManager, expediente, tipo);
		if(vinculado != null) {
			return vinculado.getExpedienteRelacionado();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static VinculadosExp getVinculadoByTipo(EntityManager entityManager, Expediente expediente, String tipo){
		List<VinculadosExp> list = entityManager.createQuery("select e from VinculadosExp e where e.expediente = :expediente and e.expedienteRelacionado is not null and e.tipoVinculado = :tipo").
		setParameter("expediente", expediente).
		setParameter("tipo", tipo).
		getResultList();
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	public boolean hasConexos(Expediente expediente){
		if(hasConexos == null){
			int count = 0;
			hasConexos = Boolean.FALSE;
			if(expediente != null){
				count = getExpedientesVinculadosDirecta(expediente.getId()).size();
				if(count > 0){
					hasConexos = Boolean.TRUE;
				}else {
					count = getExpedientesVinculadosInversa(expediente.getId()).size();
					if(count > 0){
						hasConexos = Boolean.TRUE;
					}
				}
			}
		}
		return hasConexos;
	}
}
