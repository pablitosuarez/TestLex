package com.base100.lex100.mesaEntrada.sorteo.oficina;

public class OficinaSorteo {
	private String name;

	public OficinaSorteo(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public boolean equals(OficinaSorteo oficinaSorteo){
		return name.equals(oficinaSorteo.getName());
	}

}
