package com.base100.lex100.mesaEntrada.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import jxl.Cell;
import jxl.read.biff.BiffException;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.Dependencia;
import com.base100.lex100.entity.Fiscalia;
import com.base100.lex100.entity.TipoInstancia;

public class FiscaliaLoader extends AbstractConfigurationLoader{

	
	private String FISCALIA_VALUE = "FISCALIA";
	
	private int insertCount = 0; 
	private int updateCount = 0; 

	
	public FiscaliaLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		
		if(isFiscaliaRow(row)){
			cargaFiscalia(row);
		}
		
//		cargaCUIF(row);
		
	}

	protected void end() {
		info("Fiscalía: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}

	private boolean isFiscaliaRow(Cell[] row) {
		return FISCALIA_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private void cargaCUIF(Cell[] row) throws LoaderException {

		String dependencia = getContent(row, 0);
		String cuif = getContent(row, 1);	
		
		List<Dependencia> listaDependencias = getEntityManager().createQuery("from Dependencia where descripcion = :descripcion").setParameter("descripcion", dependencia).getResultList();
		StringBuffer dependenciaList = new StringBuffer();
		int cont = 0;
		for (Dependencia dep : listaDependencias) {
			if (cont > 0) {
				dependenciaList.append(",");
			}
			dependenciaList.append(dep.getId());
			cont++;
		}
		
		getEntityManager().createQuery("update Fiscalia set cuif = :cuif where id_dependencia in (:dependenciaList)").
						setParameter("cuif", cuif).setParameter("dependenciaList", dependenciaList.toString()).
						executeUpdate();
		
	}

	private Fiscalia cargaFiscalia(Cell[] row) throws LoaderException {
		Camara camara = buscaCamara(getContent(row, 1));
		if(camara == null){
			fatal("Proceso abortado");
		}
		
		Integer codigoFiscalia = getInteger(row, 2, null);
		String nombreFiscal = getContent(row, 3);
		Date fechaInicio = getDate(row, 4);
		String competenciaCodigo = getUpperContent(row, 5);
		String codigoDependencia = getContent(row, 6);
		String instanciaAbreviatura = getUpperContent(row, 7);
		String codigoZona = getContent(row, 8);
		
		return actualizaFiscalia(camara, codigoFiscalia, nombreFiscal, fechaInicio, competenciaCodigo, codigoDependencia,instanciaAbreviatura, codigoZona);
		//
	}

		
	private Fiscalia actualizaFiscalia(Camara camara, Integer codigoFiscalia,
				String nombreFiscal, Date fechaInicio, String competenciaCodigo, String codigoDependencia,
				String instanciaAbreviatura, String zonaOficina) throws LoaderException {
		
		Fiscalia fiscalia = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			TipoInstancia tipoInstancia = buscaTipoInstanciaByAbreviatura(instanciaAbreviatura);
			Competencia competencia = buscaCompetencia(camara, competenciaCodigo, false);
			fiscalia = buscaFiscalia(codigoFiscalia, camara, tipoInstancia.getAbreviatura(), competencia, fechaInicio, zonaOficina);
			if (fiscalia == null) {
				fiscalia = new Fiscalia();
				fiscalia.setCamara(camara);
				fiscalia.setCodigo(codigoFiscalia);
				fiscalia.setCompetencia(competencia);
				fiscalia.setTipoInstancia(tipoInstancia);
				fiscalia.setFechaInicio(fechaInicio);
				fiscalia.setZonaOficina(zonaOficina);
			}
			fiscalia.setStatus(0);
			fiscalia.setDescripcion(nombreFiscal);
			
			fiscalia.setDependencia(buscaDependencia(codigoDependencia));
			//
			if(Strings.isEmpty(fiscalia.getUuid())){
				fiscalia.setUuid(UUID.randomUUID().toString());
			}
			if (fiscalia.getId() == null) {
				getEntityManager().persist(fiscalia);
				insertCount++;
			}else{
				updateCount++;
				isNew = false;
			}
			getEntityManager().flush();
			
		}
		return fiscalia;
	}

	public static void main(String[] args) {
		File file = new File(args[0]);
		
		FiscaliaLoader fiscaliaLoader = new FiscaliaLoader(null, null);
		
		try {
			fiscaliaLoader.load(new FileInputStream(file));			
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
