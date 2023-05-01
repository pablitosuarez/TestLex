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

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Provincia;
import com.base100.lex100.entity.Sigla;

public class SiglaLoader extends AbstractConfigurationLoader{

	
	private String SIGLA_VALUE = "SIGLA";
	private String GRUPO_VALUE = "GRUPO";
	private String FIN_GRUPO_VALUE = "FIN";
	
	private int insertCount = 0; 
	private int updateCount = 0; 

	private Sigla currentGrupo;
	
	public SiglaLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isGrupoRow(row)) {
			currentGrupo = cargaSigla(row);
		}else if(isSiglaRow(row)){
			cargaSigla(row);
		}else if(isFinGrupoRow(row)){
			currentGrupo = null;
		}
	}

	protected void end() {
		info("Sigla: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}

	private boolean isSiglaRow(Cell[] row) {
		return SIGLA_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private boolean isGrupoRow(Cell[] row) {
		return GRUPO_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private boolean isFinGrupoRow(Cell[] row) {
		return FIN_GRUPO_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private Sigla cargaSigla(Cell[] row) throws LoaderException {
		Camara camara = buscaCamara(getContent(row, 1));
		if(camara == null){
			fatal("Proceso abortado");
		}
		
		String codigoSigla = getUpperFilteredContent(row, 2);
		boolean frecuente = getBoolean(row, 3, true);
		String descripcion = getUpperFilteredContent(row, 4);
		String nombreAlternativo = getUpperFilteredContent(row, 5);
		String cuit = getContent(row, 6);
		
		String domicilio = getUpperContent(row, 7);
		String numeroDomicilio = getContent(row, 8);
		String detalleDomicilio = getContent(row, 9);
		String localidad = getUpperContent(row, 10);
		String provincia = getUpperContent(row, 11);
		String codCodigoPostal = getContent(row, 12);
		
		return actualizaSigla(camara, codigoSigla, frecuente, descripcion, nombreAlternativo, cuit, domicilio, numeroDomicilio, detalleDomicilio, localidad, provincia, codCodigoPostal);		
	}

	private Sigla actualizaSigla(Camara camara, String codigoSigla, boolean frecuente, String descripcion,
			String nombreAlternativo, String cuit, String domicilio, String numeroDomicilio, String detalleDomicilio,
			String localidad, String provincia, String codCodigoPostal) throws LoaderException {

		Sigla sigla = null;
		if (getEntityManager() != null) {
			sigla = buscaSigla(codigoSigla, camara, descripcion, nombreAlternativo, cuit);
			if (sigla == null) {
				sigla = new Sigla();
				sigla.setCamara(camara);
			}
			sigla.setCodigo(codigoSigla);
			sigla.setDescripcion(descripcion);
			sigla.setNombreAlternativo(nombreAlternativo);
			sigla.setFrecuente(frecuente);
			sigla.setCuit(cuit);

			sigla.setDomicilio(domicilio);
			sigla.setNumeroDomicilio(numeroDomicilio);
			sigla.setDetalleDomicilio(detalleDomicilio);
			sigla.setLocalidad(localidad);
			if (provincia != null) {
				sigla.setProvincia(buscaProvinciaPorDescripcion(provincia, true));
			}
			sigla.setCodCodigoPostal(codCodigoPostal);
			
			sigla.setGrupoEmpresario(currentGrupo);

			sigla.setStatus(0);

			if (Strings.isEmpty(sigla.getUuid())) {
				sigla.setUuid(UUID.randomUUID().toString());
			}
			if (sigla.getId() == null) {
				getEntityManager().persist(sigla);
				insertCount++;
			} else {
				updateCount++;
			}
			getEntityManager().flush();

		}
		return sigla;
	}

	public static void main(String[] args) {
		File file = new File(args[0]);
		
		SiglaLoader siglaLoader = new SiglaLoader(null, null);
		
		try {
			siglaLoader.load(new FileInputStream(file));			
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
