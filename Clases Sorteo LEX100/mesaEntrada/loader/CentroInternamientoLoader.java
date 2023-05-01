package com.base100.lex100.mesaEntrada.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;
import jxl.read.biff.BiffException;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.CentroInternamiento;
import com.base100.lex100.entity.TipoLugarInternamiento;

public class CentroInternamientoLoader extends AbstractConfigurationLoader{

	
	private String CENTRO_INTERNAMIENTO_VALUE = "CENTRO INTERNAMIENTO";
	
	private int insertCount = 0; 
	private int updateCount = 0; 

	
	public CentroInternamientoLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isCentroInternamientoRow(row)){
			cargaCentroInternamiento(row);
		}
	}

	protected void end() {
		info("CentroInternamiento: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}

	private boolean isCentroInternamientoRow(Cell[] row) {
		return CENTRO_INTERNAMIENTO_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private CentroInternamiento cargaCentroInternamiento(Cell[] row) throws LoaderException {
		
		String codigoTipoLugar = getContent(row, 1);
		String nombre = getContent(row, 2);
		String domicilio = getContent(row, 3);
		String localidad = getContent(row, 4);
		String correoElectronico = getContent(row, 5);
		String telefono = getContent(row, 6);
		String fax = getContent(row, 7);
		
		TipoLugarInternamiento tipoLugar = buscaTipoLugarInternamiento(codigoTipoLugar, true);
		if(tipoLugar == null) {
			fatal("No existe el tipo de lugar de internamiento");
		}
		return actualizaCentroInternamiento(tipoLugar, nombre, domicilio, localidad, correoElectronico, telefono, fax);		
	}

	private CentroInternamiento actualizaCentroInternamiento(TipoLugarInternamiento tipoLugarInternamiento, String nombre, String domicilio, String localidad, String correoElectronico, String telefono, String fax) throws LoaderException {
		
		CentroInternamiento centroInternamiento = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			centroInternamiento = buscaCentroInternamiento(tipoLugarInternamiento, nombre);
			if (centroInternamiento == null) {
				centroInternamiento = new CentroInternamiento();
				centroInternamiento.setTipoLugarInternamiento(tipoLugarInternamiento);
				centroInternamiento.setNombre(nombre);
			}
			centroInternamiento.setDomicilio(domicilio);
			centroInternamiento.setLocalidad(localidad);
			centroInternamiento.setCorreoElectronico(correoElectronico);
			centroInternamiento.setTelefono(telefono);
			centroInternamiento.setFax(fax);
			centroInternamiento.setStatus(0);
			
			if(Strings.isEmpty(centroInternamiento.getUuid())){
				centroInternamiento.setUuid(UUID.randomUUID().toString());
			}
			if (centroInternamiento.getId() == null) {
				getEntityManager().persist(centroInternamiento);
				insertCount++;
			}else{
				updateCount++;
				isNew = false;
			}
			getEntityManager().flush();
			
		}
		return centroInternamiento;
	}

	@SuppressWarnings("unchecked")
	private CentroInternamiento buscaCentroInternamiento(TipoLugarInternamiento tipoLugarInternamiento, String nombre) {
		List<CentroInternamiento> list = 
			getEntityManager().createQuery("from CentroInternamiento where tipoLugarInternamiento = :tipoLugarInternamiento and nombre = :nombre and status = 0")
				.setParameter("tipoLugarInternamiento", tipoLugarInternamiento)
				.setParameter("nombre", nombre)
				.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	public static void main(String[] args) {
		File file = new File(args[0]);
		
		CentroInternamientoLoader centroInternamientoLoader = new CentroInternamientoLoader(null, null);
		
		try {
			centroInternamientoLoader.load(new FileInputStream(file));			
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
