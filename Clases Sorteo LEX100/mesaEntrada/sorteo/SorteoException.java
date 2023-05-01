package com.base100.lex100.mesaEntrada.sorteo;

public class SorteoException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8786567158585708979L;

	public SorteoException() {
	}
	
	SorteoException(String message){
		super(message);
	}

	SorteoException(String message, Throwable e){
		super(message, e);
	}
	
}
