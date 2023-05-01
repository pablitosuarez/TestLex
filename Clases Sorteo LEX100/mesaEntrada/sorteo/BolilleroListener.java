package com.base100.lex100.mesaEntrada.sorteo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import com.base100.document.generator.util.Base64.OutputStream;
import com.base100.lex100.mesaEntrada.sorteo.listeners.IListener;

public class BolilleroListener implements IListener {
	
	private PrintStream output;
	
	public BolilleroListener(String outputPath) {
		try {
			output = new PrintStream(new FileOutputStream(new File(outputPath), true));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void notify(String msg) {
		if(output != null){
			output.println(msg);
		}
	}

}
