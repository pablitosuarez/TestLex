package com.base100.lex100.mesaEntrada.loader;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.international.StatusMessages;

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Dependencia;
import com.base100.lex100.entity.GrupoDependencia;
import com.base100.lex100.entity.TipoGrupoDependencia;

import jxl.Cell;

public class DependenciaLoader extends AbstractConfigurationLoader {

	private String DEPENDENCIA_VALUE = "DEPENDENCIA";
	private String GRUPO_DEPENDENCIAS_VALUE = "GRUPO_DEPENDENCIAS";
	
	private int dependenciaInsertCount = 0; 
	private int dependenciaUpdateCount = 0; 
	private int grupoDependenciasInsertCount = 0; 
	private int grupoDependenciasUpdateCount = 0; 
	
	
	public DependenciaLoader(EntityManager entityManager,
			StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}

	@Override
	protected void init() {
		dependenciaInsertCount = 0; 
		dependenciaUpdateCount = 0; 
		grupoDependenciasInsertCount = 0; 
		grupoDependenciasUpdateCount = 0; 
	}

	@Override
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isDependenciaRow(row)){
			cargaDependencia(row);
		} else if (isGrupoDependenciasRow(row)) {
			cargaGrupoDependencia(row);
		}
	}

	private void cargaGrupoDependencia(Cell[] row) throws LoaderException {
		String codigoGrupo = getContent(row, 1);
		String descripcion = getContent(row, 2);
		String codigoCamara = getContent(row, 3);
		String codigoDependencia = getContent(row, 4);
		String tipoDependencia = getContent(row, 5);
		String abreviatura = getContent(row, 6);
		
		Camara camara = buscaCamara(codigoCamara);
		actualizaGrupoDependencia(codigoGrupo, descripcion, camara, codigoDependencia, tipoDependencia, abreviatura);
	}

	private void actualizaGrupoDependencia(String codigoGrupo,
			String descripcion, Camara camara, String codigoDependencia,
			String tipoDependencia, String abreviatura) throws LoaderException {
		TipoGrupoDependencia tipoGrupoDependencia = resuelveTipoGrupoDependencia(codigoGrupo, descripcion, camara);
		List<Dependencia> dependenciaList = getEntityManager().createQuery("from Dependencia where codigo like :codigo and tipo = :tipo and status <> -1")
			.setParameter("codigo", codigoDependencia)
			.setParameter("tipo", tipoDependencia)
			.getResultList();
		if (dependenciaList.size() == 0) {
			throw new LoaderException("No existe dependencia con codigo='"+codigoDependencia+"' y tipo="+tipoDependencia);
		}
		for (Dependencia dependencia : dependenciaList) {
			actualizaFilaGrupoDependencia(tipoGrupoDependencia,dependencia,abreviatura);
		}
		getEntityManager().flush();
	}

	private void actualizaFilaGrupoDependencia(
			TipoGrupoDependencia tipoGrupoDependencia, Dependencia dependencia, String abreviatura) {
		List<GrupoDependencia> grupoDependenciaList =
		getEntityManager().createQuery("from GrupoDependencia where tipoGrupoDependencia = :tipoGrupoDependencia and dependencia = :dependencia")
			.setParameter("tipoGrupoDependencia", tipoGrupoDependencia)
			.setParameter("dependencia", dependencia)
			.getResultList();
		GrupoDependencia grupoDependencia;
		if (grupoDependenciaList.size() > 0) {
			grupoDependencia = grupoDependenciaList.get(0);
		} else {
			grupoDependencia = new GrupoDependencia();
			grupoDependencia.setTipoGrupoDependencia(tipoGrupoDependencia);
			grupoDependencia.setDependencia(dependencia);
		}
		grupoDependencia.setAbreviaturaDependencia(abreviatura);
		if (grupoDependencia.getId() == null) {
			getEntityManager().persist(grupoDependencia);
			grupoDependenciasInsertCount++;
		} else {
			grupoDependenciasUpdateCount++;
		}
	}

	private TipoGrupoDependencia resuelveTipoGrupoDependencia(
			String codigoGrupo, String descripcion, Camara camara) {
		String ejbql = "from TipoGrupoDependencia where codigo = :codigo ";
		if (camara == null) {
			ejbql += "and camara is null ";
		} else {
			ejbql += "and camara = :camara ";
		}
		ejbql += "and status <> -1 order by id";
		Query query = getEntityManager().createQuery(ejbql)
			.setParameter("codigo", codigoGrupo);
		if (camara != null) {
			query.setParameter("camara", camara);
		}
		List<TipoGrupoDependencia> tipoGrupoDependenciaList = query.getResultList();
		TipoGrupoDependencia tipoGrupoDependencia;
		if (tipoGrupoDependenciaList.size() > 0) {
			tipoGrupoDependencia = tipoGrupoDependenciaList.get(0);
		} else {
			tipoGrupoDependencia = new TipoGrupoDependencia();
			tipoGrupoDependencia.setCodigo(codigoGrupo);
			tipoGrupoDependencia.setCamara(camara);
		}
		tipoGrupoDependencia.setDescripcion(descripcion);
		if (tipoGrupoDependencia.getId() == null) {
			getEntityManager().persist(tipoGrupoDependencia);
		}
		return tipoGrupoDependencia;
	}

	private void cargaDependencia(Cell[] row) {
		String codigo = getContent(row, 1);
		String descripcion = getContent(row, 2);
		String tipo = getContent(row, 3);
		
		actualizaDependencia(codigo, descripcion, tipo);
	}

	private void actualizaDependencia(String codigo, String descripcion,
			String tipo) {
		List<Dependencia> dependenciaList = getEntityManager().createQuery("from Dependencia where codigo = :codigo and tipo = :tipo and status <> -1 order by id")
			.setParameter("codigo", codigo)
			.setParameter("tipo", tipo)
			.getResultList();
		if (dependenciaList.size() == 0) {
			dependenciaList = getEntityManager().createQuery("from Dependencia where codigo = :codigo and tipo = :tipo and status = -1 order by id")
			.setParameter("codigo", codigo)
			.setParameter("tipo", tipo)
			.getResultList();
		}
		Dependencia dependencia;
		if (dependenciaList.size() > 0) {
			dependencia = dependenciaList.get(0);
		} else {
			dependencia = new Dependencia();
			dependencia.setCodigo(codigo);
			dependencia.setTipo(tipo);
		}
		dependencia.setDescripcion(descripcion);
		dependencia.setStatus(0);
		if (dependencia.getId() == null) {
			getEntityManager().persist(dependencia);
			dependenciaInsertCount++;
		} else {
			dependenciaUpdateCount++;
		}
		getEntityManager().flush();
	}

	private boolean isDependenciaRow(Cell[] row) {
		return DEPENDENCIA_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private boolean isGrupoDependenciasRow(Cell[] row) {
		return GRUPO_DEPENDENCIAS_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	@Override
	protected void end() {
		info("Dependencia: "+dependenciaInsertCount+" filas añadidas, "+dependenciaUpdateCount+" filas modificadas");
		info("Grupo Dependencias: "+grupoDependenciasInsertCount+" filas añadidas, "+grupoDependenciasUpdateCount+" filas modificadas");
	}

}
