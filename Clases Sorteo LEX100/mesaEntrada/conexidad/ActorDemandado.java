package com.base100.lex100.mesaEntrada.conexidad;

public enum ActorDemandado {
	ACTOR("Actor", 1),
	DEMANDADO("Demandado", 2),
	CUALQUIERA("Parte", null),
	;

	private String label;	
	public String getLabel() {
		return label;
	}

	public Integer getOrder() {
		return order;
	}

	private Integer order;
	
	private ActorDemandado(String label, Integer order) {
		this.label = label;
		this.order = order;
	}
}
