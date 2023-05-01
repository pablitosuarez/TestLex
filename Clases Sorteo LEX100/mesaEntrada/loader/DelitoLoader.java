package com.base100.lex100.mesaEntrada.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;
import jxl.read.biff.BiffException;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Delito;
import com.base100.lex100.entity.TipoInstancia;

public class DelitoLoader extends AbstractConfigurationLoader{

	
	private String DELITO_VALUE = "DELITO";
	
	private int insertCount = 0; 
	private int updateCount = 0; 

	
	public DelitoLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isDelitoRow(row)){
			cargaDelito(row);
		}
	}

	protected void end() {
		info("Delito: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}

	private boolean isDelitoRow(Cell[] row) {
		return DELITO_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private Delito cargaDelito(Cell[] row) throws LoaderException {
		String codigoDelito = getContent(row, 1);
		String descripcion = getContent(row, 2);
		Integer ley = getInteger(row, 3, null);
		Integer libro = getInteger(row, 4, null);
		Integer titulo = getInteger(row, 5, null);
		String aperturaTitulo = getContent(row, 6);
		Integer capitulo = getInteger(row, 7, null);
		String aperturaCapitulo = getContent(row, 8);
		Integer articulo = getInteger(row, 9, null);
		String aperturaArticulo = getContent(row, 10);
		String inciso = getContent(row, 11);
		String aperturaInciso = getContent(row, 12);
		String apertura = getContent(row, 13);
		String rubro = getContent(row, 14);
		String fueroCompetencia = getContent(row, 15);
		String instancia = getContent(row, 16);
		
		return actualizaDelito(codigoDelito, descripcion, ley, libro, titulo, aperturaTitulo, capitulo, aperturaCapitulo, articulo, aperturaArticulo, inciso, aperturaInciso, apertura, rubro, fueroCompetencia, instancia);
		//
	}

		
	private Delito actualizaDelito(String codigoDelito, String descripcion,
			Integer ley, Integer libro, Integer titulo, String aperturaTitulo,
			Integer capitulo, String aperturaCapitulo, Integer articulo,
			String aperturaArticulo, String inciso, String aperturaInciso,
			String apertura, String rubro, String fueroCompetencia, String instancia) throws LoaderException {
		
		Delito delito = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			delito = buscaDelito(codigoDelito, ley, false);
			if (delito == null) {
				delito = new Delito();
				delito.setCodigo(codigoDelito);
			}
			delito.setStatus(0);

			delito.setDescripcion(descripcion);
			delito.setLey(ley);
			delito.setLibro(libro);
			delito.setTitulo(titulo);
			delito.setAperturaTitulo(aperturaTitulo);
			delito.setCapitulo(capitulo);
			delito.setAperturaCapitulo(aperturaCapitulo);
			delito.setArticulo(articulo);
			delito.setAperturaArticulo(aperturaArticulo);
			delito.setInciso(inciso);
			delito.setAperturaInciso(aperturaInciso);
			delito.setApertura(apertura);
			delito.setRubro(rubro);
			delito.setFueroCompetencia(fueroCompetencia);
			delito.setTipoInstancia(calculaTipoInstancia(instancia));
			//
			if(Strings.isEmpty(delito.getUuid())){
				delito.setUuid(UUID.randomUUID().toString());
			}
			if (delito.getId() == null) {
				getEntityManager().persist(delito);
				insertCount++;
			}else{
				updateCount++;
				isNew = false;
			}
			getEntityManager().flush();
			
		}
		return delito;
	}

	private TipoInstancia calculaTipoInstancia(String instancia) throws LoaderException {
		TipoInstancia tipoInstancia = null;
		if(!isEmpty(instancia)){
			if("1".equals(instancia)){				 
				tipoInstancia = buscaTipoInstancia(INSTANCIA_PRIMERA);				
			}else if("2".equals(instancia)){
				tipoInstancia = buscaTipoInstancia(INSTANCIA_APELACIONES);				
			}else{
				fatal("Codigo de instancia erroneo: Se permite solo 1, 2 o vacio");
			}
		}
		return tipoInstancia;
	}
	
	public static void main(String[] args) {
		File file = new File(args[0]);
		
		DelitoLoader delitoLoader = new DelitoLoader(null, null);
		
		try {
			delitoLoader.load(new FileInputStream(file));			
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
