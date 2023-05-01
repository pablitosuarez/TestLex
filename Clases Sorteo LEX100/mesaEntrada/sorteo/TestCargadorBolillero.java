package com.base100.lex100.mesaEntrada.sorteo;

import java.util.ArrayList;
import java.util.List;

import com.base100.lex100.entity.Oficina;

public class TestCargadorBolillero {
	public List<Oficina> oficinas;
	public static int FACTOR_SUMA_DEFECTO = 1;
	public static int RANGO_MINIMOS_DEFECTO = 1;
	

	public Bolillero creaBolillero(String rubro) throws SorteoException{
		if(oficinas == null || oficinas.size() == 0){
			throw new SorteoException("No se han definido oficinas para el bolillero");			
		}
		
		List<BolaSorteo> bolas = new ArrayList<BolaSorteo>();
		for(int idxOficina = 0; idxOficina < oficinas.size(); idxOficina++){
			Oficina oficina = oficinas.get(idxOficina);
//			bolas.add(createBolaJuzgado(oficina.getId(), oficina.getCodigo(), rubro, FACTOR_SUMA_DEFECTO));	
		}
		String name = "rubro:"+rubro;
		return new Bolillero(name, rubro, bolas, RANGO_MINIMOS_DEFECTO, 1, false);
		
	}
	
//	private static BolaSorteo createBolaJuzgado(Integer idOficina, String oficina, String rubro, int factorSuma){
//		 return new BolaSorteo(idOficina, oficina, rubro, factorSuma);
//	}

	public List<Oficina> getOficinas() {
		return oficinas;
	}

	public void setOficinas(List<Oficina> oficinas) {
		this.oficinas = oficinas;
	}

}
