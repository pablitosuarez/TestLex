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

import mondrian.olap.MondrianProperties.SolveOrderModeEnum;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.Defensoria;
import com.base100.lex100.entity.Dependencia;
import com.base100.lex100.entity.TipoInstancia;

public class DefensoriaLoader extends AbstractConfigurationLoader{

	
	private String DEFENSORIA_VALUE = "DEFENSORIA";
	
	private int insertCount = 0; 
	private int updateCount = 0; 

	
	public DefensoriaLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		
		if(isDefensoriaRow(row)){
			cargaDefensoria(row);
		}
		
//		cargaCUID(row);
//		cargaCUIL(row);
		
	}

	protected void end() {
		info("Defensoría: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
	}

	private boolean isDefensoriaRow(Cell[] row) {
		return DEFENSORIA_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private void cargaCUID(Cell[] row) throws LoaderException {

		String dependencia = getContent(row, 0);
		String cuid = getContent(row, 1);	
		
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
		
		getEntityManager().createQuery("update Defensoria set cuid = :cuid where id_dependencia in (:dependenciaList)").
						setParameter("cuid", cuid).setParameter("dependenciaList", dependenciaList.toString()).
						executeUpdate();
		
	}

	private void cargaCUIL(Cell[] row) throws LoaderException {

		String dependencia = getContent(row, 0);
		String cuilDefensor = getContent(row, 1);	
		
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
		
		getEntityManager().createQuery("update Defensoria set cuilDefensor = :cuilDefensor where id_dependencia in (:dependenciaList)").
						setParameter("cuilDefensor", cuilDefensor).setParameter("dependenciaList", dependenciaList.toString()).
						executeUpdate();
		
	}

	private Defensoria cargaDefensoria(Cell[] row) throws LoaderException {
		Camara camara = buscaCamara(getContent(row, 1));
		if(camara == null){
			fatal("Proceso abortado");
		}
		
		Integer codigoDefensoria = getInteger(row, 2, null);
		String descripcion = getContent(row, 3);
		Date fechaInicio = getDate(row, 4);
		String competenciaCodigo = getUpperContent(row, 5);
		String codigoDependencia = getContent(row, 6);
		String instanciaAbreviatura = getUpperContent(row, 7);
		String zonaOficina = getContent(row, 8);
		
		return actualizaDefensoria(camara, codigoDefensoria, descripcion, fechaInicio, competenciaCodigo, codigoDependencia,instanciaAbreviatura, zonaOficina);
		//
	}

		
	private Defensoria actualizaDefensoria(Camara camara, Integer codigoDefensoria,
				String descripcion, Date fechaInicio, String competenciaCodigo, String codigoDependencia,
				String instanciaAbreviatura, String zonaOficina) throws LoaderException {
		
		Defensoria defensoria = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			TipoInstancia tipoInstancia = buscaTipoInstanciaByAbreviatura(instanciaAbreviatura);
			Competencia competencia = buscaCompetencia(camara, competenciaCodigo, false);
			defensoria = buscaDefensoria(codigoDefensoria, camara, tipoInstancia.getAbreviatura(), competencia, fechaInicio, zonaOficina);
			if (defensoria == null) {
				defensoria = new Defensoria();
				defensoria.setCamara(camara);
				defensoria.setCodigo(codigoDefensoria);
				defensoria.setCompetencia(competencia);
				defensoria.setTipoInstancia(tipoInstancia);
				defensoria.setFechaInicio(fechaInicio);
				defensoria.setZonaOficina(zonaOficina);
			}
			defensoria.setStatus(0);
			defensoria.setDescripcion(descripcion);
			
			defensoria.setDependencia(buscaDependencia(codigoDependencia));
			//
			if(Strings.isEmpty(defensoria.getUuid())){
				defensoria.setUuid(UUID.randomUUID().toString());
			}
			if (defensoria.getId() == null) {
				getEntityManager().persist(defensoria);
				insertCount++;
			}else{
				updateCount++;
				isNew = false;
			}
			getEntityManager().flush();
			
		}
		return defensoria;
	}

	public static void main(String[] args) {
		File file = new File(args[0]);
		
		DefensoriaLoader defensoriaLoader = new DefensoriaLoader(null, null);
		
		try {
			defensoriaLoader.load(new FileInputStream(file));			
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
