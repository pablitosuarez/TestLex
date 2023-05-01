package com.base100.lex100.mesaEntrada.sorteo;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.framework.Controller;

import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.TipoBolillero;

@Name("sorteadorFactory")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
public class SorteadorFactory extends Controller {

	private Map<String, ColaDeSorteo> indexerMap = new HashMap<String, ColaDeSorteo>();
	private static String LOG_BOLILLERO_PROPERTY = "sorteo_log";
	
	private synchronized ColaDeSorteo getColaDeSorteo(Sorteo sorteo, TipoBolillero tipoBolillero, Camara camara, Materia materia, String rubro, Integer filtroOficinaId) {		
		String keyBolillero = camara.getId()+" - "+rubro+" - "+materia.getId();
		if(sorteo != null){
			keyBolillero += " - "+sorteo.getId();
		}
//		if(filtroOficinaId != null){
//			keyBolillero += " - of:"+filtroOficinaId;
//		}
		if(tipoBolillero != null){
			keyBolillero += " - "+tipoBolillero.getId();
		}
		ColaDeSorteo colaDeSorteo = indexerMap.get(keyBolillero);
		if (colaDeSorteo == null) {
			colaDeSorteo = new ColaDeSorteo(sorteo, tipoBolillero, camara, rubro, materia, keyBolillero);
			checkListeners(colaDeSorteo);
			indexerMap.put(keyBolillero, colaDeSorteo);
			colaDeSorteo.start();
		}
		return colaDeSorteo;
	}

	private void checkListeners(ColaDeSorteo colaDeSorteo) {
		String sorteoLogPath = Configuration.instance().getProperty(LOG_BOLILLERO_PROPERTY, null);
		if(sorteoLogPath != null){
			BolilleroListener bolilleroListener = new BolilleroListener(sorteoLogPath);
			colaDeSorteo.setSorteoLogListener(bolilleroListener);	
		}
	}
	
	public synchronized ColaDeSorteo getColaDeSorteoForExpediente(SorteoParametros sorteoParametros) {
		String rubro = sorteoParametros.getRubro();
		if(rubro == null){
			rubro = getRubro(sorteoParametros.getCamara(), sorteoParametros.getExpediente());
		}
		
		Materia materia = sorteoParametros.getMateria();
		if (materia == null) {
			materia = getMateria(sorteoParametros.getExpediente());
		}
			
		
		//Competencia competencia = expedienteIngreso.getCompetencia();
		
		if(rubro != null){
			Integer filtroOficinaId = null;
			if(sorteoParametros.getSorteo().isSorteoOrdenCirculacion() && sorteoParametros.getOficinaAsignada() != null){
				filtroOficinaId = sorteoParametros.getOficinaAsignada().getId();
			}
			return getColaDeSorteo(sorteoParametros.getSorteo(), sorteoParametros.getTipoBolillero(), sorteoParametros.getCamara(),  materia, rubro, filtroOficinaId);
		}else{
			return null;
		}

	}
	

	// FIXME: provisional para resetar colas en memoria
	public void clearColasDeSorteo(){
		indexerMap = new HashMap<String, ColaDeSorteo>();
	}

	private Materia getMateria(Expediente expediente) {
		Materia materia = null;
		if (expediente.getMateria()!= null) {
			materia = expediente.getMateria();
		} else if(expediente.getTipoProceso()!=null) {
			materia = expediente.getTipoProceso().getMateria();
		} else if (expediente.getObjetoJuicioPrincipal().getTipoProceso()!=null){
			materia = expediente.getObjetoJuicioPrincipal().getTipoProceso().getMateria();
		}
		return materia;
	}

	private String getRubro(Camara camara, Expediente expediente) {
		if(expediente.getTipoCausa() != null && expediente.getTipoCausa().getRubro() != null){
			return expediente.getTipoCausa().getRubro();
		}else if (expediente.getObjetoJuicioPrincipal() != null) {
			return expediente.getObjetoJuicioPrincipal().getRubroLetter();
		}else {
			return null;
		}
	
	}
	
//	private String getRubroRecurso(Expediente expediente, boolean useRubro2) {
//		String ret = null;
//		if(expediente.getExpedienteEspecial() != null){
//			TipoRecurso tipoRecurso = expediente.getExpedienteEspecial().getTipoRecurso();
//			if(tipoRecurso != null){
//				 ret = useRubro2 ? tipoRecurso.getRubro2() : tipoRecurso.getRubro();
//			}
//		}
//		if(Strings.isEmpty(ret)){
//			ret = useRubro2 ? "B" : "A";
//		}
//		return ret;
//	}


	public static SorteadorFactory instance(){
		return (SorteadorFactory)Component.getInstance(SorteadorFactory.class, true);
	}

	
	public void resetSorteadores() {
		for(String key:indexerMap.keySet()){
			ColaDeSorteo colaDeSorteo = indexerMap.get(key);
			colaDeSorteo.stop();
			indexerMap.put(key, null);
		}
		indexerMap.clear();		
	}
	
}
