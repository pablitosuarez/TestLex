package com.base100.lex100.mesaEntrada.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import jxl.Cell;
import jxl.read.biff.BiffException;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.MotivoPase;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.ReglaTipoRemesa;
import com.base100.lex100.entity.Tema;
import com.base100.lex100.entity.TipoOficina;
import com.base100.lex100.entity.TipoRemesa;

public class ReglaTipoRemesaLoader extends AbstractConfigurationLoader {

	private static final String TIPO_REMESA_VALUE = "TIPO_REMESA";
	private static final String TIPO_REGLA_TIPO_REMESA_VALUE = "REGLA_TIPO_REMESA";
	private int insertCount = 0;
	private int updateCount = 0;
	private int deleteCount = 0;
	private int insertTipoRemesaCount = 0;
	private int updateTipoRemesaCount = 0;
	private List<ReglaTipoRemesa> reglasTipoRemesa = new ArrayList<ReglaTipoRemesa>();
	private Camara lastCamara = null;

	public ReglaTipoRemesaLoader(EntityManager entityManager,
			StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}

	protected void init() {
		insertCount = 0;
		updateCount = 0;
		deleteCount = 0;

		insertTipoRemesaCount = 0;
		updateTipoRemesaCount = 0;
	}

	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if (isReglaTipoRemesaRow(row)) {
			ReglaTipoRemesa reglaTipoRemesa = cargaReglaTipoRemesa(row);
			if (reglaTipoRemesa != null) {
				reglasTipoRemesa.add(reglaTipoRemesa);
			}
		}
		if (isTipoRemesaRow(row)) {
			cargaTipoRemesa(row);
		}
	}

	protected void end() {
		if (lastCamara != null) {
			deleteOtherReglasTipoRemesa(lastCamara, reglasTipoRemesa);
		}
		if (insertTipoRemesaCount > 0 || updateTipoRemesaCount > 0) {
			info("Tipo Remesa: " + insertTipoRemesaCount + " filas añadidas, "
					+ updateTipoRemesaCount + " filas modificadas");
		}

		info("ReglaTipoRemesa: " + insertCount + " filas añadidas, "
				+ updateCount + " filas modificadas, " + deleteCount
				+ " filas eliminadas (borrado lógico)");

	}

	private boolean isReglaTipoRemesaRow(Cell[] row) {
		return TIPO_REGLA_TIPO_REMESA_VALUE.equalsIgnoreCase(row[0]
				.getContents());
	}

	private boolean isTipoRemesaRow(Cell[] row) {
		return TIPO_REMESA_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private void deleteOtherReglasTipoRemesa(Camara camara,
			List<ReglaTipoRemesa> reglasTipoRemesa) {
		if (camara != null) {
			List<Integer> ids = new ArrayList<Integer>();
			for (ReglaTipoRemesa item : reglasTipoRemesa) {
				ids.add(item.getId());
			}
			if (ids.size() > 0) {
				deleteCount += getEntityManager()
						.createQuery(
								"update ReglaTipoRemesa set status = -1 where camara = :camara and id not in (:ids)")
						.setParameter("camara", camara)
						.setParameter("ids", ids).executeUpdate();
			} else {
				deleteCount += getEntityManager()
						.createQuery(
								"update ReglaTipoRemesa set status = -1 where camara = :camara")
						.setParameter("camara", camara).executeUpdate();
			}
		}
	}

	private TipoRemesa cargaTipoRemesa(Cell[] row) throws LoaderException {
		Camara camara = buscaCamara(getContent(row, 1));
		if (camara == null) {
			fatal("Proceso abortado");
		} else {
			if ((lastCamara != null) && !lastCamara.equals(camara)) {
				deleteOtherReglasTipoRemesa(lastCamara, reglasTipoRemesa);
				reglasTipoRemesa.clear();
			}
			lastCamara = camara;
		}
		String codigo = getContent(row, 2);
		String descripcion = getContent(row, 3);

		return actualizaTipoRemesa(camara, codigo, descripcion);

	}

	private TipoRemesa actualizaTipoRemesa(Camara camara, String codigo,
			String descripcion) throws LoaderException {

		TipoRemesa tipoRemesa = null;
		if (getEntityManager() != null) {
			tipoRemesa = buscaTipoRemesa(codigo, camara, false);
			if (tipoRemesa == null) {
				tipoRemesa = new TipoRemesa();
				tipoRemesa.setCamara(camara);
				tipoRemesa.setCodigo(codigo);
			}
			tipoRemesa.setDescripcion(descripcion);
			tipoRemesa.setStatus(0);

			//
			if (Strings.isEmpty(tipoRemesa.getUuid())) {
				tipoRemesa.setUuid(UUID.randomUUID().toString());
			}
			if (tipoRemesa.getId() == null) {
				getEntityManager().persist(tipoRemesa);
				insertTipoRemesaCount++;
			} else {
				updateTipoRemesaCount++;
			}
			getEntityManager().flush();
		}
		return tipoRemesa;
	}

	private ReglaTipoRemesa cargaReglaTipoRemesa(Cell[] row)
			throws LoaderException {
		Camara camara = buscaCamara(getContent(row, 1));
		if (camara == null) {
			fatal("Proceso abortado");
		} else {
			if ((lastCamara != null) && !lastCamara.equals(camara)) {
				deleteOtherReglasTipoRemesa(lastCamara, reglasTipoRemesa);
				reglasTipoRemesa.clear();
			}
			lastCamara = camara;
		}
		Materia materia = buscaMateria(getContent(row, 2), false);
		Materia defaultMateria = materia;
		if ((defaultMateria == null) && (camara.getMateriaList().size() == 1)){
			defaultMateria = camara.getMateriaList().get(0);
		}
		
		Integer prioridad = getInteger(row, 3, 0);
		Tema tema = isEmpty(row, 4) ? null : buscaTema(getContent(row, 4),
				defaultMateria, camara, true);
		ObjetoJuicio objetoJuicio = isEmpty(row, 5) ? null : buscaObjetoJuicio(
				camara, defaultMateria, getContent(row, 5), true);
		String rubro = getContent(row, 6);
		MotivoPase motivoPase = isEmpty(getContent(row, 7))? null: buscaMotivoPase(getContent(row, 7), camara, true);
		TipoOficina tipoOficina = isEmpty(row, 8) ? null
				: buscaTipoOficina(getUpperContent(row, 8));
		Oficina oficina = isEmpty(row, 9) ? null : buscaOficina(getContent(row,
				9));
		Boolean urgente = getBoolean(row, 10);
		Integer maximoNumeroExpedientes = getInteger(row, 11, null);
		Integer maximoNumeroFojas = getInteger(row, 12, null);
		TipoRemesa tipoRemesa = isEmpty(row, 13) ? null : buscaTipoRemesa(
				getContent(row, 13), camara, true);

		if ((objetoJuicio != null) && (tema != null)) {
			fatal("No se puede definir una regla para un Objeto de Juicio y Tema al mismo tiempo");
		}
		if ((objetoJuicio != null) && (rubro != null)) {
			fatal("No se puede definir una regla para un Objeto de Juicio y Rubro al mismo tiempo");
		}

		return actualizaReglaTipoRemesa(camara, materia, prioridad, tema,
				objetoJuicio, rubro, motivoPase, tipoOficina, oficina, urgente,
				maximoNumeroExpedientes, maximoNumeroFojas, tipoRemesa);
	}

	private ReglaTipoRemesa actualizaReglaTipoRemesa(Camara camara,
			Materia materia, Integer prioridad, Tema tema,
			ObjetoJuicio objetoJuicio, String rubro, MotivoPase motivoPase,
			TipoOficina tipoOficina, Oficina oficina, Boolean urgente,
			Integer maximoNumeroExpedientes, Integer maximoNumeroFojas,
			TipoRemesa tipoRemesa) throws LoaderException {

		ReglaTipoRemesa reglaTipoRemesa = null;

		if (getEntityManager() != null) {
			reglaTipoRemesa = buscaReglaTipoRemesa(camara, materia, tema,
					objetoJuicio, rubro, motivoPase, tipoOficina, oficina,
					urgente, tipoRemesa);
			if (reglaTipoRemesa == null) {
				reglaTipoRemesa = new ReglaTipoRemesa();
				reglaTipoRemesa.setCamara(camara);
				reglaTipoRemesa.setMateria(materia);
			}
			reglaTipoRemesa.setPrioridad(prioridad);
			reglaTipoRemesa.setTema(tema);
			reglaTipoRemesa.setObjetoJuicio(objetoJuicio);
			reglaTipoRemesa.setRubro(rubro);
			reglaTipoRemesa.setMotivoPase(motivoPase);
			reglaTipoRemesa.setTipoOficina(tipoOficina);
			reglaTipoRemesa.setOficina(oficina);
			reglaTipoRemesa.setUrgente(urgente);
			reglaTipoRemesa.setMaximoNumeroExpedientes(maximoNumeroExpedientes);
			reglaTipoRemesa.setMaximoNumeroFojas(maximoNumeroFojas);
			reglaTipoRemesa.setTipoRemesa(tipoRemesa);
			reglaTipoRemesa.setStatus(0);

			//
			if (Strings.isEmpty(reglaTipoRemesa.getUuid())) {
				reglaTipoRemesa.setUuid(UUID.randomUUID().toString());
			}
			if (reglaTipoRemesa.getId() == null) {
				getEntityManager().persist(reglaTipoRemesa);
				insertCount++;
			} else {
				updateCount++;
			}
			getEntityManager().flush();
		}
		return reglaTipoRemesa;
	}

	@SuppressWarnings("unchecked")
	private ReglaTipoRemesa buscaReglaTipoRemesa(Camara camara,
			Materia materia, Tema tema, ObjetoJuicio objetoJuicio,
			String rubro, MotivoPase motivoPase, TipoOficina tipoOficina,
			Oficina oficina, Boolean urgente, TipoRemesa tipoRemesa) {

		String stmt = "from ReglaTipoRemesa where camara = :camara and tipoRemesa=:tipoRemesa";
		if (materia != null) {
			stmt += " and materia = :materia";
		}
		if (tema != null) {
			stmt += " and tema = :tema";
		}
		if (objetoJuicio != null) {
			stmt += " and objetoJuicio = :objetoJuicio";
		}
		if (rubro != null) {
			stmt += " and rubro = :rubro";
		}
		if (motivoPase != null) {
			stmt += " and motivoPase = :motivoPase";
		}
		if (tipoOficina != null) {
			stmt += " and tipoOficina = :tipoOficina";
		}
		if (oficina != null) {
			stmt += " and oficina = :oficina";
		}
		if (urgente != null) {
			stmt += " and urgente = :urgente";
		}

		Query query = getEntityManager().createQuery(stmt);
		query.setParameter("camara", camara);
		query.setParameter("tipoRemesa", tipoRemesa);
		if (materia != null) {
			query.setParameter("materia", materia);
		}
		if (tema != null) {
			query.setParameter("tema", tema);
		}
		if (objetoJuicio != null) {
			query.setParameter("objetoJuicio", objetoJuicio);
		}
		if (rubro != null) {
			query.setParameter("rubro", rubro);
		}
		if (motivoPase != null) {
			query.setParameter("motivoPase", motivoPase);
		}
		if (tipoOficina != null) {
			query.setParameter("tipoOficina", tipoOficina);
		}
		if (oficina != null) {
			query.setParameter("oficina", oficina);
		}
		if (urgente != null) {
			query.setParameter("urgente", urgente);
		}
		List<ReglaTipoRemesa> reglas = query.getResultList();
		if (reglas.size() > 0) {
			return reglas.get(0);
		}

		return null;
	}

	public static void main(String[] args) {
		File file = new File(args[0]);

		ReglaTipoRemesaLoader reglaTipoRemesaLoader = new ReglaTipoRemesaLoader(
				null, null);

		try {
			reglaTipoRemesaLoader.load(new FileInputStream(file));
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
