package com.base100.lex100.mesaEntrada.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;
import jxl.read.biff.BiffException;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.component.enumeration.TipoCausaEnumeration;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.CriterioCnx;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.TipoCaratula;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoInterviniente;

public class TipoCausaLoader extends AbstractConfigurationLoader{

	
	
	private static final String TIPO_CAUSA_VALUE = "TIPO_CAUSA";
	private static final String TIPO_CARATULA_VALUE = "TIPO_CARATULA";
	private int insertCount = 0; 
	private int updateCount = 0; 
	private int deleteCount = 0;
	private int insertTipoCaratulaCount = 0; 
	private int updateTipoCaratulaCount = 0; 
	private List<TipoCausa> tiposCausa = new ArrayList<TipoCausa>();
	private Camara lastCamara = null;
	private Materia lastMateria = null;

	private Map<String, TipoInterviniente> tipoIntervinienteMap = new HashMap<String, TipoInterviniente>();
	private Map<String, List<TipoInterviniente>> todosIntervinienteMap = new HashMap<String, List<TipoInterviniente>>();

	public TipoCausaLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
		deleteCount = 0;
		insertTipoCaratulaCount = 0; 
		updateTipoCaratulaCount = 0;
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isTipoCausaRow(row)){
			TipoCausa tipoCausa = cargaTipoCausa(row);
			if (tipoCausa != null) {
				tiposCausa.add(tipoCausa);
			}
		}else if(isTipoCaratulaRow(row)){
			cargaTipoCaratula(row);
		}
	}

	protected void end() {
		if ((lastCamara != null) && (lastMateria != null)) {
			deleteOtherTiposCausa(lastCamara, lastMateria, tiposCausa);
		}
		if(insertTipoCaratulaCount > 0 || updateTipoCaratulaCount > 0){
			info("TipoCaratula: "+insertTipoCaratulaCount+" filas añadidas, "+updateTipoCaratulaCount+" filas modificadas");
		}
		info("TipoCausa: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas, "+deleteCount+" filas eliminadas (borrado lógico)");
	}

	private boolean isTipoCausaRow(Cell[] row) {
		return TIPO_CAUSA_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private boolean isTipoCaratulaRow(Cell[] row) {
		return TIPO_CARATULA_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private void deleteOtherTiposCausa(Camara camara, Materia materia, List<TipoCausa> tiposCausa) {
		if(camara != null){
			List<Integer> ids = new ArrayList<Integer>();
			for(TipoCausa item: tiposCausa){
				ids.add(item.getId());
			}
			if(ids.size() > 0){
				deleteCount += getEntityManager().createQuery("update TipoCausa set status = -1 where camara = :camara and materia = :materia and id not in (:ids)")
				.setParameter("camara", camara)		
				.setParameter("materia", materia)
				.setParameter("ids", ids).executeUpdate();
			}else {
				deleteCount += getEntityManager().createQuery("update TipoCausa set status = -1 where camara = :camara and materia = :materia")
				.setParameter("camara", camara)
				.setParameter("materia", materia)
				.executeUpdate();
			}
		}
		
	}

	private TipoCaratula cargaTipoCaratula(Cell[] row) throws LoaderException {
		String codigo = getContent(row, 1);
		String descripcion = getContent(row, 2);

		return actualizaTipoCaratula(codigo, descripcion);
	}

	private TipoCaratula actualizaTipoCaratula(String codigo, String descripcion) throws LoaderException {
		
		TipoCaratula tipoCaratula = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			tipoCaratula = buscaTipoCaratula(codigo, false);
			
			if(tipoCaratula == null){
				tipoCaratula = new TipoCaratula();
				tipoCaratula.setCodigo(Integer.valueOf(codigo));
			}
			
			tipoCaratula.setDescripcion(descripcion);
			
			tipoCaratula.setStatus(0);
			if(Strings.isEmpty(tipoCaratula.getUuid())){
				tipoCaratula.setUuid(UUID.randomUUID().toString());
			}
			if (tipoCaratula.getId() == null) {
				getEntityManager().persist(tipoCaratula);
				insertTipoCaratulaCount++;
			}else{
				isNew = false;
				updateTipoCaratulaCount++;
			}
			
			getEntityManager().flush();
		}
		return tipoCaratula;
	
	}

	
	private TipoCausa cargaTipoCausa(Cell[] row) throws LoaderException {
		Camara camara = buscaCamara(getContent(row, 1));
		if(camara == null){
			fatal("Proceso abortado");
		} else {
			if ((lastCamara != null) && !lastCamara.equals(camara) && (lastMateria != null)) {
				deleteOtherTiposCausa(lastCamara, lastMateria, tiposCausa);
				tiposCausa.clear();
			}
			lastCamara = camara;
		}

		Materia materia = buscaMateria(getContent(row, 2), true);
		if(materia == null){
			fatal("Proceso abortado");
		} else {
			if ((lastMateria != null) && !lastMateria.equals(materia)&& (lastCamara != null)) {
				deleteOtherTiposCausa(lastCamara, lastMateria, tiposCausa);
				tiposCausa.clear();
			}
			lastCamara = camara;			
		}
		lastMateria = materia;
		String codigoTipoCausa = getContent(row, 3);
		String descripcion = getContent(row, 4);
		String informacion = getContent(row, 5);
		String rubro = getContent(row, 6);
		String rubroAsignacion = getContent(row, 7);
		String tipoInstancia = getUpperContent(row, 8);
		boolean ingresaNumero = getBoolean(row, 9, false);
		boolean ingresaOficina = getBoolean(row, 10, false);
		boolean buscaConexidad = getBoolean(row, 11, true);
		boolean tieneVinculado = getBoolean(row, 12, false);
		String codigoTipoCaratula = getContent(row, 13);		
		String codigoRecurso = getContent(row, 14);		
		String codigoOJ = getContent(row, 15);		
		String tipoIntervinientes = getContent(row, 16);
		Integer status = getInteger(row, 17, 0);
		boolean esEspecial = getBoolean(row, 18, true);
		String descripcionEnCaratula = getContent(row, 19);
		Boolean mantieneJuzgadoMediacion = getBoolean(row, 20);
		
		String tipoSala = null;
		
		if(!Strings.isEmpty(codigoRecurso)) {
			int length = codigoRecurso.length();
			if((length > 2) && codigoRecurso.charAt(0) == '[' && codigoRecurso.charAt(length-1) == ']') {
				tipoSala = codigoRecurso.substring(1, length-1);
				codigoRecurso = null;
			}
		}
		return actualizaTipoCausa(camara, materia, codigoTipoCausa, descripcion, informacion, rubro, rubroAsignacion,
				tipoInstancia,
				ingresaNumero,
				ingresaOficina,
				buscaConexidad,
				tieneVinculado, codigoTipoCaratula, codigoRecurso, tipoSala, codigoOJ, tipoIntervinientes,status,esEspecial,
				descripcionEnCaratula,
				mantieneJuzgadoMediacion);
		//
	}

		
	private TipoCausa actualizaTipoCausa(Camara camara, Materia materia, String codigoTipoCausa,
				String descripcion, String informacion, String rubro, String rubroAsignacion,
				String tipoInstancia,
				boolean ingresaNumero,
				boolean ingresaOficina,
				boolean buscaConexidad,
				boolean tieneVinculado,
				String codigoTipoCaratula, 
				String codigoRecurso, 
				String tipoSala, 
				String codigoOJ, 
				String tipoIntervinientes, 
				Integer status,
				boolean esEspecial,
				String descripcionEnCaratula,
				Boolean mantieneJuzgadoMediacion) throws LoaderException {	//ITO (Incompetencia TO)
		TipoCausa tipoCausa = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			if(materia == null){
				fatal("Proceso abortado");
			}
			tipoCausa = buscaTipoCausa(camara, materia, codigoTipoCausa, false);
			if (tipoCausa == null) {
				tipoCausa = new TipoCausa();
				tipoCausa.setCamara(camara);
				tipoCausa.setMateria(materia);
				tipoCausa.setCodigo(codigoTipoCausa);
				
			}
			tipoCausa.setStatus(status);
			tipoCausa.setDescripcion(descripcion);
			tipoCausa.setInformacion(informacion);
			tipoCausa.setTipoCaratula(buscaTipoCaratula(codigoTipoCaratula, true));
			tipoCausa.setCodigoTipoRecurso(codigoRecurso);
			tipoCausa.setTipoSala(tipoSala);
			tipoCausa.setCodigoObjetoJuicio(codigoOJ);
			tipoCausa.setEspecial(esEspecial);
			tipoCausa.setRubro(rubro);
			tipoCausa.setRubroAsignacion(rubroAsignacion);
			tipoCausa.setIngresaNumero(ingresaNumero);
			tipoCausa.setIngresaOficina(ingresaOficina);
			tipoCausa.setBuscaConexidad(buscaConexidad);
			tipoCausa.setVinculado(tieneVinculado);
			tipoCausa.setDescripcionEnCaratula(descripcionEnCaratula);
			tipoCausa.setMantieneJuzgadoMediacion(mantieneJuzgadoMediacion);
			
			//ITO (Incompetencia TO)
			TipoInstancia ti = buscaTipoInstancia(getTipoInstanciaFromAbreviacion(tipoInstancia));
			if (ti == null) {
				ti = buscaTipoInstanciaByAbreviatura(tipoInstancia);
			}
			if (ti != null) {
				tipoCausa.setTipoInstancia(ti);
			}
			//
			if(Strings.isEmpty(tipoCausa.getUuid())){
				tipoCausa.setUuid(UUID.randomUUID().toString());
			}
			if (tipoCausa.getId() == null) {
				getEntityManager().persist(tipoCausa);
				insertCount++;
			}else{
				updateCount++;
				isNew = false;
			}
			getEntityManager().flush();
			
			if(!isEmpty(tipoIntervinientes)){
				actualizaTiposIntervinienteTipoCausa(materia, tipoCausa, isNew, tipoIntervinientes);
			}
			getEntityManager().flush();
		}
		return tipoCausa;
	}


	private String getTipoInstanciaFromAbreviacion(String tipoInstancia) {
		if(tipoInstancia != null){
			if(tipoInstancia.equals("J")){
				return INSTANCIA_PRIMERA;
			}
			if(tipoInstancia.equals("S")){
				return INSTANCIA_APELACIONES;
			}
			if(tipoInstancia.equals("TO")){
				return INSTANCIA_TRIBUNAL_ORAL;
			}
		}
		return null;
	}

	private void actualizaTiposIntervinienteTipoCausa(Materia materia, TipoCausa tipoCausa, boolean isNew,	String parametros) throws LoaderException {
		if(parametros.equals("*TODOS")){
			List<TipoInterviniente> list = todosIntervinienteMap.get(materia.getAbreviatura());
			if(list == null){
				list = 
				getEntityManager().createQuery("from TipoInterviniente where tipoProceso.materia = :materia and status = 0")
				.setParameter("materia", materia)
				.getResultList();
				todosIntervinienteMap.put(materia.getAbreviatura(), list);
			}
			for(TipoInterviniente tipoInterviniente:list){
				actualizaTipoIntervinienteTipoCausa(tipoCausa, isNew, tipoInterviniente);				
			}
			return;
		}
		StringTokenizer tk = new StringTokenizer(parametros, ",");
		if(!isNew){
			tipoCausa.getTipoIntervinienteList().clear();
		}
		while(tk.hasMoreTokens()){
			String nombreTipoInterviniente = tk.nextToken();
			nombreTipoInterviniente = nombreTipoInterviniente.trim();
			if(!isEmpty(nombreTipoInterviniente)){
				TipoInterviniente tipoInterviniente = tipoIntervinienteMap.get(nombreTipoInterviniente);
				if(tipoInterviniente == null){
					tipoInterviniente = buscaTipoIntervinienteStatus0(materia, nombreTipoInterviniente, false);
					if(tipoInterviniente != null){
						tipoIntervinienteMap.put(nombreTipoInterviniente, tipoInterviniente);
					}
				}
				if(tipoInterviniente == null){
					fatal("Tipo Interviniente no encontrado o ambiguo: "+nombreTipoInterviniente);
				}
				actualizaTipoIntervinienteTipoCausa(tipoCausa, isNew, tipoInterviniente);
			}
		}
		getEntityManager().flush();		
	}

	private void actualizaTipoIntervinienteTipoCausa(TipoCausa tipoCausa, boolean isNew, TipoInterviniente tipoInterviniente) {
		if (!tipoCausa.getTipoIntervinienteList().contains(tipoInterviniente)) {
			tipoCausa.getTipoIntervinienteList().add(tipoInterviniente);
		}
	}


	public static void main(String[] args) {
		File file = new File(args[0]);
		
		TipoCausaLoader sorteoLoader = new TipoCausaLoader(null, null);
		
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
