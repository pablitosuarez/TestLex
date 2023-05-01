package com.base100.lex100.mesaEntrada.sorteo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;

import com.base100.lex100.mesaEntrada.sorteo.listeners.IListener;

public class Sorteador extends Tracer {	
	private static final int MAX_TRACE_LENGTH = 138;
	private Bolillero bolillero;
	private Log log = Logging.getLog(Sorteador.class);
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	private long seed = System.currentTimeMillis();
	private int randomCount;
	private Random randomGenerator = new Random(seed);
	private StringBuffer informacionSorteo = new StringBuffer();
	
	public Sorteador(Bolillero bolillero) {
		super();
		this.bolillero = bolillero;
		super.setLog(log);
	}
	
	public BolaSorteo sorteo(SorteoExpedienteEntry entry) throws SorteoException{
		trace("Sorteador", bolillero.getName());
		trace("Estado previo", bolillero.getStatus());
		BolaSorteo bola = seleccionAleatoriaMenorCarga(entry);
		return bola;
	}
	
	private BolaSorteo seleccionAleatoriaMenorCarga(SorteoExpedienteEntry entry) throws SorteoException{
		List<BolaSorteo> listaMenorCarga = seleccionaBolasMenorCarga(entry);
		trace("Selección", bolillero.getStatus(listaMenorCarga));
		
		int idx = random(listaMenorCarga.size());
		notify("Sorteo", "rubro ("+bolillero.getRubro()+") numero de bolas "+listaMenorCarga.size()+", Numero aleatorio de bola: "+(idx+1));
		notify("Sorteo", "** Semilla: "+new DecimalFormat("####").format(seed)+", función \"Random.nextInt("+ listaMenorCarga.size() +")\", nº invocaciones: "+randomCount+", resultado: "+idx);
		BolaSorteo resultado = listaMenorCarga.get(idx);

		if(bolillero.isSorteaRubro()){
			trace("Resultado", "Bola numero("+(idx+1)+") rubro : "+resultado.getRubro()+"["+resultado.getCargaTrabajo()+"] ");
		}else{
			trace("Resultado", "Bola numero("+(idx+1)+") oficina : "+resultado.getOficinaCargaExp().getOficina().getCodigo()+"["+resultado.getCargaTrabajo()+"] ");
		}
		
		return resultado;
	}

	@Override
	public String notify(String label, String msg){
		String text = super.notify(label, msg);
		bolillero.notify(text);
		return text;
	}
	
	public void addListener(IListener listener){
		bolillero.addListener(listener);
	}

	public List<BolaSorteo> seleccionaBolasMenorCarga(SorteoExpedienteEntry entry) throws SorteoException{
		boolean empty = bolillero == null || bolillero.getBolas() == null || bolillero.getBolas().size() == 0;
		List<BolaSorteo> bolas = null;
		if(!empty){
			bolas =  bolillero.getBolas();
			if(entry != null){
				if(entry.getExcludeOficinaIds() != null && entry.getExcludeOficinaIds().size() > 0){
					 bolas = excludeBolas(bolas, entry.getExcludeOficinaIds());
					 empty = bolas.size() == 0;
				}
				if(entry.getOnlyOficinaIds() != null && entry.getOnlyOficinaIds().size() > 0){
					 bolas = onlyBolas(bolas, entry.getOnlyOficinaIds());
					 empty = bolas.size() == 0;				
				}
			}
		}
		List<BolaSorteo> list = new ArrayList<BolaSorteo>();
		if(!empty){
			int minValue = getMinValue(bolas);			
			int rango = bolillero.getRangoMinimos();
			while(true){			
				boolean puedeReintentar = false;			
				for(BolaSorteo bola:bolas){
					if(!bola.isInhibido()){
						if(bola.getCargaTrabajo() <= minValue + rango){
							list.add(bola);
						}else{
							puedeReintentar = true;
						}
					}				
				}
				if(list.size() >= bolillero.getMinimoNumeroBolas() || !puedeReintentar){
					break;
				}
				list.clear();
				// reintentar
				rango++;
			}
		}
		if(empty || list.size() == 0 || list.size() < bolillero.getMinimoNumeroBolas()){
			throw new SorteoException("No hay elementos para el sorteo");
		}
		return list;
	}

	private void addMinValue(int value, List<Integer> minValueList,
			int rangoMinimos) {
		if (minValueList.size() >= rangoMinimos) {
			minValueList.remove(minValueList.size()-1);
		}
		minValueList.add(value);
		Collections.sort(minValueList);
	}

	private boolean isMinRange(int value, List<Integer> minValueList,
			int rangoMinimos) {
		if (minValueList.size() < rangoMinimos) {
			return true;
		} else {
			for (Integer minValue : minValueList) {
				if (value < minValue) {
					return true;
				}
			}
		}
		return false;
	}

	private List<BolaSorteo> excludeBolas(List<BolaSorteo> bolas, List<Integer> excludeOficinaIds) {
		List<BolaSorteo> nuevaLista =  new ArrayList<BolaSorteo>();
		StringBuffer sb = new StringBuffer();
		for(BolaSorteo bola:bolas){
			if(!excludeOficinaIds.contains(bola.getOficinaCargaExp().getOficina().getId())){
				nuevaLista.add(bola);
			}else{
				sb.append(bola.getOficinaCargaExp().getOficina().getCodigo());
				sb.append("["+bola.getCargaTrabajo()+"] ");
			}
		}
		if(sb.length() > 0){
			trace("Exclusión", sb.toString());
		}
		return nuevaLista;
	}

	private List<BolaSorteo> onlyBolas(List<BolaSorteo> bolas, List<Integer> onlyOficinaIds) {
		List<BolaSorteo> nuevaLista =  new ArrayList<BolaSorteo>();
		StringBuffer sb = new StringBuffer();
		for(BolaSorteo bola:bolas){
			if(onlyOficinaIds.contains(bola.getOficinaCargaExp().getOficina().getId())){
				nuevaLista.add(bola);
				sb.append(bola.getOficinaCargaExp().getOficina().getCodigo());
				sb.append("["+bola.getCargaTrabajo()+"] ");
			}
		}
		if(sb.length() > 0){
			trace("Restricción", sb.toString());
		}
		return nuevaLista;
	}

	private boolean isEmptyBolilleroExcluding(List<Integer> excludeOficinaIds) {
		boolean empty = false;
		if(excludeOficinaIds != null && excludeOficinaIds.size() > 0){
			boolean foundOther = false;
			for(BolaSorteo bola:bolillero.getBolas()){
				if(!excludeOficinaIds.contains(bola.getOficinaCargaExp().getOficina().getId())){
					foundOther = true;
					break;
				}
			}
			empty = !foundOther;
		}
		return empty;
	}

	private int getMinValue(List<BolaSorteo> bolas) {
		int minValue = 0;
		boolean first = true;
		for(BolaSorteo bola:bolas){
			if(!bola.isInhibido() &&(bola.getCargaTrabajo() < minValue || first)){
				minValue = bola.getCargaTrabajo();
				first = false;
			}
		}
		return minValue;
	}
	

	private int random(int range) {
		 randomCount++;
		 return randomGenerator.nextInt(range);
	}
	
	public String getStatus(){
		return bolillero.getStatus();
	}

	public Bolillero getBolillero() {
		return bolillero;
	}

	public String getInformacionSorteo() {
		return getInformacion();
	}

	public long getSeed() {
		return seed;
	}

	public int getRandomCount() {
		return randomCount;
	}

	public static void main(String[] args) {
		System.out.println(new Random(1362941704836L).nextInt(1));
	}
	
}
