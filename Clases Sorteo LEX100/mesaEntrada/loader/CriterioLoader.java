package com.base100.lex100.mesaEntrada.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import jxl.Cell;
import jxl.read.biff.BiffException;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.component.enumeration.TipoComparacionEnumeration;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.CondicionCnx;
import com.base100.lex100.entity.CriterioCnx;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Tema;
import com.base100.lex100.entity.TipoCausa;

public class CriterioLoader extends AbstractConfigurationLoader{

	
	private String CRITERIO_VALUE = "CRITERIO";
	private String CRITERIO_TIPO_CAUSA_VALUE = "CRITERIO_TIPO_CAUSA";

	private Camara lastCamara = null;

	private CriterioCnx lastCriterio = null;
	private int insertCountCriterio = 0; 
	private int updateCountCriterio = 0;
	private int deleteCountCriterio = 0;
	private List<CriterioCnx> criterios = new ArrayList<CriterioCnx>();

	
	public CriterioLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
	}
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isCriterioRow(row) || isCriterioByTipoCausaRow(row)){
			lastCriterio = cargaCriterio(row);
			if(lastCriterio == null){
				fatal("Proceso abortado");
			}
			criterios.add(lastCriterio);
		}
	}

	protected void end() {
		if (lastCamara != null) {
			deleteOtherCriterios(lastCamara, criterios);
		}
		info("Criterio: "+insertCountCriterio+" filas añadidas, "+updateCountCriterio+" filas modificadas, "+deleteCountCriterio+" filas eliminadas (borrado lógico)");
	}
	
	private boolean isCriterioRow(Cell[] row) {
		return CRITERIO_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private boolean isCriterioByTipoCausaRow(Cell[] row) {
		return CRITERIO_TIPO_CAUSA_VALUE.equalsIgnoreCase(row[0].getContents());
	}

	private CriterioCnx cargaCriterio(Cell[] row) throws LoaderException {
		if (getEntityManager() == null) {
			return null;
		}
		
		String codigoCamara = getContent(row, 1);
		String codigoMateria = getContent(row, 2);
		String descripcionTema = getContent(row, 3);
		String codigoObjetoJuicio = getContent(row, 4);

		String nombreCriterio = getContent(row, 5);
		Integer prioridad = getInteger(row, 6, null);
		
		String objetosIncluidos = getUpperContent(row, 7);
		if (objetosIncluidos != null) {
			objetosIncluidos = objetosIncluidos.trim();
		}
		String objetosExcluidos = getUpperContent(row, 8);
		if (objetosExcluidos != null) {
			objetosExcluidos = objetosExcluidos.trim();
		}
		
		boolean asigna = getBoolean(row, 9, false);

		String parametros = getContent(row, 10);

		boolean dobleInicio = getBoolean(row, 11, false);

		String codigoTipoCausa = getContent(row, 12);
		String tiposCausaIncluidos = getContent(row, 13);

		String temasIncluidos = getContent(row, 14);

		String codigoAsignacion = getContent(row, 15);
		Boolean juicioVoluntario = getBoolean(row, 16);

		String codigoCambioAsignacion = getContent(row, 17);

		Boolean ambitoVoluntarios = getBoolean(row, 18);

		boolean excluirPersonasJuridicas = getBoolean(row, 19, false);

		boolean ambitoGlobal = getBoolean(row, 20, false);
		
		Boolean expedientesIniciados = getBoolean(row, 21);
		Boolean expedientesEnTramite = getBoolean(row, 22);
		Boolean igualObjetoJuicio = getBoolean(row, 23);
		Date desdeFecha = getDate(row, 24, null);
		Integer aniosAntiguedad = getInteger(row, 25, null);
		String tiempoFinalizacion = getContent(row, 26);


		return actualizaCriterio(codigoCamara, codigoMateria, descripcionTema, codigoObjetoJuicio, codigoTipoCausa,
			nombreCriterio, prioridad, objetosIncluidos, objetosExcluidos, tiposCausaIncluidos, asigna, parametros,
			dobleInicio, temasIncluidos, codigoAsignacion, juicioVoluntario, codigoCambioAsignacion, ambitoVoluntarios, excluirPersonasJuridicas, ambitoGlobal,
			expedientesIniciados, expedientesEnTramite, igualObjetoJuicio, desdeFecha, aniosAntiguedad, tiempoFinalizacion); 
		
	}

	private CriterioCnx actualizaCriterio(String codigoCamara, String codigoMateria, String descripcionTema, String codigoObjetoJuicio,
			String codigoTipoCausa, String nombreCriterio, Integer prioridad, String objetosIncluidos, String objetosExcluidos, String tiposCausaIncluidos,
			boolean asigna, String parametros, boolean dobleInicio, String temasIncluidos, String codigoAsignacion, Boolean juicioVoluntario,
			String codigoCambioAsignacion, Boolean ambitoVoluntarios, boolean excluirPersonasJuridicas, boolean ambitoGlobal,
			Boolean expedientesIniciados , Boolean expedientesEnTramite, Boolean igualObjetoJuicio, Date desdeFecha, Integer aniosAntiguedad, String tiempoFinalizacion) throws LoaderException {
		
		CriterioCnx criterio = null;
		ObjetoJuicio objetoJuicio = null;
		Tema tema = null;
		TipoCausa tipoCausa = null;
		Materia materia = null;

		Camara camara = buscaCamara(codigoCamara);
		if(camara == null){
			fatal("Proceso abortado");
		} else {
			if ((lastCamara != null) && !lastCamara.equals(camara)) {
				deleteOtherCriterios(lastCamara, criterios);
				criterios.clear();
			}
			lastCamara = camara;
		}
		if (codigoMateria != null) {
			materia = buscaMateria(codigoMateria, true);
		}
		if (codigoObjetoJuicio != null) {
			objetoJuicio = buscaObjetoJuicio(camara, materia, codigoObjetoJuicio, true);
		}
		if (descripcionTema != null) {
			tema = buscaTemaPorDescripcion(descripcionTema, materia, camara, true);
		}
		if (codigoTipoCausa != null) {
			tipoCausa = buscaTipoCausa(camara, materia, codigoTipoCausa, true);
		}

		criterio = buscaCriterio(nombreCriterio, juicioVoluntario, tipoCausa, objetoJuicio, tema, materia, camara, false);
		if (criterio == null) {
			criterio = new CriterioCnx();
			criterio.setCamara(camara);

			if (objetoJuicio != null) {
				criterio.setObjetoJuicio(objetoJuicio);
			} else if (tema != null) {
				criterio.setTema(tema);
			} else if (tipoCausa != null) {
				criterio.setTipoCausa(tipoCausa);
			} else {
				criterio.setMateria(materia);
			}
			criterio.setNombre(nombreCriterio);
		}
		criterio.setPrioridad(prioridad);
		criterio.setAsigna(asigna);
		criterio.setDobleInicio(dobleInicio);
		if (!Strings.isEmpty(objetosExcluidos)) {
			criterio.setExcluirObjetosJuicio(true);
			criterio.setObjetoJuicioList(toListaObjetos(objetosExcluidos, camara, materia));
		} else if (!Strings.isEmpty(objetosIncluidos)) {
			criterio.setExcluirObjetosJuicio(false);
			criterio.setObjetoJuicioList(toListaObjetos(objetosIncluidos, camara, materia));
		} else {
			criterio.setExcluirObjetosJuicio(false);
			criterio.setObjetoJuicioList(new ArrayList<ObjetoJuicio>(0));
		}

		criterio.setJuicioVoluntario(juicioVoluntario);
		criterio.setExcluirPersonasJuridicas(excluirPersonasJuridicas);

		criterio.setTiposCausa(tiposCausaIncluidos);
		criterio.setTemas(temasIncluidos);
		criterio.setAmbitoVoluntarios(ambitoVoluntarios);
		criterio.setCodigoAsignacion(codigoAsignacion);
		criterio.setCodigoCambioAsignacion(codigoCambioAsignacion);
		
		criterio.setAmbitoGlobal(ambitoGlobal);

		criterio.setExpedientesIniciados(expedientesIniciados);
		criterio.setExpedientesEnTramite(expedientesEnTramite);
		criterio.setIgualObjetoJuicio(igualObjetoJuicio);
		criterio.setDesdeFecha(desdeFecha);
		criterio.setAniosAntiguedad(aniosAntiguedad);
		criterio.setTiempoFinalizacion(tiempoFinalizacion);

		if(Strings.isEmpty(criterio.getUuid())){
			criterio.setUuid(UUID.randomUUID().toString());
		}
		if (criterio.getId() == null) {
			getEntityManager().persist(criterio);
			insertCountCriterio++;
		}else{
			updateCountCriterio++;
		}
		getEntityManager().flush();

		criterio = getEntityManager().find(CriterioCnx.class, criterio.getId());
		actualizaCondiciones(criterio, parametros);
		
		return criterio;
	}

	private void deleteOtherCriterios(Camara camara, List<CriterioCnx> criterios) {
		if(camara != null){
			List<Integer> ids = new ArrayList<Integer>();
			for(CriterioCnx item: criterios){
				ids.add(item.getId());
			}
			if(ids.size() > 0){
				deleteCountCriterio += getEntityManager().createQuery("update CriterioCnx set status = -1 where camara = :camara and id not in (:ids)")
				.setParameter("camara", camara)			
				.setParameter("ids", ids).executeUpdate();
			}else {
				deleteCountCriterio += getEntityManager().createQuery("update CriterioCnx set status = -1 where camara = :camara")
				.setParameter("camara", camara).executeUpdate();
			}
		}
		
	}

	private void actualizaCondiciones(CriterioCnx criterio, String parametros) throws LoaderException {
		List<CondicionCnx> condiciones = new ArrayList<CondicionCnx>();
		if (!Strings.isEmpty(parametros)) {
			StringTokenizer tokenizer = new StringTokenizer(parametros, ",");
			while(tokenizer.hasMoreTokens()) {
				String parametro = tokenizer.nextToken();
				parametro = parametro.trim();
				String tipoComparacion = (String)TipoComparacionEnumeration.Type.paralela.getValue();
				int idx = parametro.indexOf("(");
				if (idx >= 0) {
					if (parametro.length() > idx) {
						if (((String)TipoComparacionEnumeration.Type.paralela.getValue()).charAt(0) == parametro.charAt(idx+1)) {
							tipoComparacion = (String)TipoComparacionEnumeration.Type.paralela.getValue();
						} else if (((String)TipoComparacionEnumeration.Type.indistinta.getValue()).charAt(0) == parametro.charAt(idx+1)) {
							tipoComparacion = (String)TipoComparacionEnumeration.Type.indistinta.getValue();
						} else if (((String)TipoComparacionEnumeration.Type.cruzada.getValue()).charAt(0) == parametro.charAt(idx+1)) {
							tipoComparacion = (String)TipoComparacionEnumeration.Type.cruzada.getValue();
						} else {
							error("Criterio '"+criterio.getNombre()+": Tipo de comparacion '" + parametro.charAt(idx+1) + "' desconocido");
						}
					}
					parametro = parametro.substring(0, idx);
					parametro = parametro.trim();
				}
				CondicionCnx condicion = buscaCondicion(criterio, parametro);
				if (condicion == null) {
					condicion = new CondicionCnx();
					condicion.setParametro(parametro);
					condicion.setCriterioCnx(criterio);
				}
				condicion.setTipoComparacion(tipoComparacion);

				if(Strings.isEmpty(condicion.getUuid())){
					condicion.setUuid(UUID.randomUUID().toString());
				}
				if (condicion.getId() == null) {
					getEntityManager().persist(condicion);
				}
				getEntityManager().flush();
				condiciones.add(condicion);
			}
		}
		deleteOtherCondiciones(criterio, condiciones);
	}

	private void deleteOtherCondiciones(CriterioCnx criterio, List<CondicionCnx> condiciones) {
		if(criterio != null){
			List<Integer> ids = new ArrayList<Integer>();
			for(CondicionCnx item: condiciones){
				ids.add(item.getId());
			}
			if(ids.size() > 0){
				getEntityManager().createQuery("update CondicionCnx set status = -1 where criterioCnx = :criterio and id not in (:ids)")
				.setParameter("criterio", criterio)			
				.setParameter("ids", ids).executeUpdate();
			}else {
				getEntityManager().createQuery("update CondicionCnx set status = -1 where criterioCnx = :criterio")
				.setParameter("criterio", criterio).executeUpdate();
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	private CondicionCnx buscaCondicion(CriterioCnx criterio, String parametro) {
		List<CondicionCnx> list = 
				getEntityManager().createQuery("from CondicionCnx where criterioCnx = :criterio and lower(parametro) = :parametro and status = 0")
				.setParameter("criterio", criterio)
				.setParameter("parametro", parametro.toLowerCase())
				.getResultList();
			if (list.size() == 1) {
				return list.get(0);
			}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected CriterioCnx buscaCriterio(String nombreCriterio, Boolean juicioVoluntario, TipoCausa tipoCausa, ObjetoJuicio objetoJuicio, Tema tema, Materia materia, Camara camara, boolean required)  throws LoaderException {
		StringBuffer stmt = new StringBuffer("from CriterioCnx where status = 0 and nombre = :nombre and camara = :camara");
//		if (juicioVoluntario != null) {
//			stmt.append(" and juicioVoluntario = :juicioVoluntario");
//		}
		if (objetoJuicio != null) {
			stmt.append(" and objetoJuicio = :objetoJuicio");
		} else if (tema != null) {
			stmt.append(" and tema = :tema");
		} else if (tipoCausa != null) {
			stmt.append(" and tipoCausa = :tipoCausa");
		} else if (materia != null) {
			stmt.append(" and materia = :materia");
		}
		Query query = getEntityManager().createQuery(stmt.toString());
		query.setParameter("nombre", nombreCriterio)
				.setParameter("camara", camara);

//		if (juicioVoluntario != null) {
//			query.setParameter("juicioVoluntario", juicioVoluntario);
//		}
		
		if (objetoJuicio != null) {
			query.setParameter("objetoJuicio", objetoJuicio);
		} else if (tema != null) {
			query.setParameter("tema", tema);
		} else if (tipoCausa != null) {
			query.setParameter("tipoCausa", tipoCausa);
		} else if (materia != null) {
			query.setParameter("materia", materia);
		}
		List<CriterioCnx> criterioList = query.getResultList();

		if (criterioList.size() == 1) {
			return criterioList.get(0);
		}else{
			if(required){
				error("Criterio '"+nombreCriterio+"' no encontrado o ambiguo");
			}
		}
		return null;
	}

	private List<String> toCodigoList(List<ObjetoJuicio> listaObjetos) {
		if (listaObjetos != null) {
			List<String> codigos = new ArrayList<String>();
			for(ObjetoJuicio objetoJuicio: listaObjetos) {
				codigos.add(objetoJuicio.getCodigo());
			}
			return codigos;
		}
		return null;
	}

	private List<ObjetoJuicio> toListaObjetos(String codigosObjetosJuicio, Camara camara, Materia materia) throws LoaderException {
		List<ObjetoJuicio> objetoJuicioList = null;
		if (!Strings.isEmpty(codigosObjetosJuicio)) {
			objetoJuicioList = new ArrayList<ObjetoJuicio>();
			StringTokenizer tokenizer = new StringTokenizer(codigosObjetosJuicio, ",");
			while(tokenizer.hasMoreTokens()) {
				String codigo = tokenizer.nextToken().trim();
				ObjetoJuicio objetoJuicio = buscaObjetoJuicio(camara, materia, codigo, true);
				objetoJuicioList.add(objetoJuicio);
			}
		}
		return objetoJuicioList;
	}

	public static void main(String[] args) {
		File file = new File(args[0]);
		
		CriterioLoader temaLoader = new CriterioLoader(null, null);
		
		try {
			temaLoader.load(new FileInputStream(file));			
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
