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

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.MotivoPase;
import com.base100.lex100.entity.TipoCaratula;
import com.base100.lex100.entity.MotivoPase;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoOficina;

public class MotivoPaseLoader extends AbstractConfigurationLoader{

	
	
	private static final String MOTIVO_PASE_VALUE = "MOTIVO_PASE";
	private int insertCount = 0; 
	private int updateCount = 0; 
	private int deleteCount = 0;
	private List<MotivoPase> motivosPase = new ArrayList<MotivoPase>();
	private Camara lastCamara = null;

	private Map<String, TipoOficina> tipoOficinaMap = new HashMap<String, TipoOficina>();
	List<TipoOficina> todoTiposOficina;
	
	public MotivoPaseLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
		deleteCount = 0;
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isMotivoPaseRow(row)){
			MotivoPase motivoPase = cargaMotivoPase(row);
			if (motivoPase != null) {
				motivosPase.add(motivoPase);
			}
		}
	}

	protected void end() {
		if (lastCamara != null) {
			deleteOtherMotivosPase(lastCamara, motivosPase);
		}
		info("MotivoPase: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas, "+deleteCount+" filas eliminadas (borrado lógico)");
	}

	private boolean isMotivoPaseRow(Cell[] row) {
		return MOTIVO_PASE_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private void deleteOtherMotivosPase(Camara camara, List<MotivoPase> motivosPase) {
		if(camara != null){
			List<Integer> ids = new ArrayList<Integer>();
			for(MotivoPase item: motivosPase){
				ids.add(item.getId());
			}
			if(ids.size() > 0){
				deleteCount += getEntityManager().createQuery("update MotivoPase set status = -1 where camara = :camara and id not in (:ids)")
				.setParameter("camara", camara)		
				.setParameter("ids", ids).executeUpdate();
			}else {
				deleteCount += getEntityManager().createQuery("update MotivoPase set status = -1 where camara = :camara")
				.setParameter("camara", camara)
				.executeUpdate();
			}
		}
		
	}

	private MotivoPase cargaMotivoPase(Cell[] row) throws LoaderException {
		Camara camara = buscaCamara(getContent(row, 1));
		if(camara == null){
			fatal("Proceso abortado");
		} else {
			if ((lastCamara != null) && !lastCamara.equals(camara) ) {
				deleteOtherMotivosPase(lastCamara, motivosPase);
				motivosPase.clear();
			}
			lastCamara = camara;
		}

		String codigoMotivoPase = getContent(row, 2);
		String descripcion = getContent(row, 3);
		String tipoOficinas = getContent(row, 4);
		Integer status = getInteger(row, 5, 0);

		return actualizaMotivoPase(camara, codigoMotivoPase, descripcion, tipoOficinas, status);
		//
	}

		
	private MotivoPase actualizaMotivoPase(Camara camara, String codigoMotivoPase,
				String descripcion,	String tipoOficinas, Integer status) throws LoaderException {	
		MotivoPase motivoPase = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			motivoPase = buscaMotivoPase(codigoMotivoPase, camara, false);
			if (motivoPase == null) {
				motivoPase = new MotivoPase();
				motivoPase.setCamara(camara);
				motivoPase.setCodigo(codigoMotivoPase);
			}
			motivoPase.setStatus(status);
			motivoPase.setDescripcion(descripcion);
			//
			if(Strings.isEmpty(motivoPase.getUuid())){
				motivoPase.setUuid(UUID.randomUUID().toString());
			}
			if (motivoPase.getId() == null) {
				getEntityManager().persist(motivoPase);
				insertCount++;
			}else{
				updateCount++;
				isNew = false;
			}
			getEntityManager().flush();
			
			if(!isEmpty(tipoOficinas)){
				actualizaTiposOficinaMotivoPase(motivoPase, isNew, tipoOficinas);
			}
			getEntityManager().flush();
		}
		return motivoPase;
	}

	private void actualizaTiposOficinaMotivoPase (MotivoPase motivoPase, boolean isNew,	String parametros) throws LoaderException {
		if(parametros.equals("*TODOS")){
			if(todoTiposOficina == null){
				todoTiposOficina = getEntityManager().createQuery("from TipoOficina where status = 0")
					.getResultList();
			}
			for(TipoOficina tipoOficina: todoTiposOficina){
				actualizaTipoOficinaMotivoPase(motivoPase, isNew, tipoOficina);				
			}
			return;
		}
		StringTokenizer tk = new StringTokenizer(parametros, ",");
		if(!isNew){
			motivoPase.getTipoOficinaList().clear();
		}
		while(tk.hasMoreTokens()){
			String nombreTipoOficina = tk.nextToken();
			nombreTipoOficina = nombreTipoOficina.trim();
			if(!isEmpty(nombreTipoOficina)){
				TipoOficina tipoOficina = tipoOficinaMap.get(nombreTipoOficina);
				if(tipoOficina == null){
					tipoOficina = buscaTipoOficina(nombreTipoOficina);
					if(tipoOficina != null){
						tipoOficinaMap.put(nombreTipoOficina, tipoOficina);
					}
				}
				if(tipoOficina == null){
					fatal("Tipo Oficina no encontrado o ambiguo: "+nombreTipoOficina);
				}
				actualizaTipoOficinaMotivoPase(motivoPase, isNew, tipoOficina);
			}
		}
		getEntityManager().flush();		
	}

	private void actualizaTipoOficinaMotivoPase(MotivoPase motivoPase, boolean isNew, TipoOficina tipoOficina) {
		if (!motivoPase.getTipoOficinaList().contains(tipoOficina)) {
			motivoPase.getTipoOficinaList().add(tipoOficina);
		}
	}


	public static void main(String[] args) {
		File file = new File(args[0]);
		
		MotivoPaseLoader sorteoLoader = new MotivoPaseLoader(null, null);
		
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
