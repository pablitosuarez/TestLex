package com.base100.lex100.mesaEntrada.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import jxl.Cell;
import jxl.read.biff.BiffException;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.component.datasheet.controller.dataSheetProperty.DataSheetPropertyUtils;
import com.base100.lex100.component.enumeration.ParametroPredefinidoEnumeration;
import com.base100.lex100.component.enumeration.TipoDatoEnumeration;
import com.base100.lex100.component.enumeration.TipoDatoEnumeration.Type;
import com.base100.lex100.component.enumeration.TipoDocumentoEnumeration;
import com.base100.lex100.controller.entity.parametro.ParametroSearch;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Parametro;
import com.base100.lex100.entity.Tema;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoProceso;
import com.base100.lex100.entity.TipoRemesa;

public class ObjetoJuicioLoader extends AbstractConfigurationLoader{

	
	
	private static final String UNIQUE_OBJETO_JUICIO_VALUE = "UNIQUE_OBJETO_JUICIO";
	private static final String OBJETO_JUICIO_VALUE = "OBJETO_JUICIO";
	private static final String PARAMETRO_VALUE = "PARAMETRO";
	private static final String SUBMATERIA_VALUE = "SUBMATERIA";
	private static final String SUBSUBMATERIA_VALUE = "SUBSUBMATERIA";
	private static final String FIN_SUBMATERIA_VALUE = "FINSUBMATERIA";
	
	private int insertCount = 0; 
	private int updateCount = 0; 
	private int insertParametroCount = 0;
	private int updateParametroCount = 0;
	private int insertSubmateriaCount = 0;
	private int updateSubmateriaCount = 0;


	private List<String> parametrosPredefinidos;
	private Map<String, Parametro> parametrosMap = new HashMap<String, Parametro>();
	private Tema currentParentTema;
	private Tema currentTema;
	private boolean simulation;

	public ObjetoJuicioLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	protected void init() {
		insertCount = 0; 
		updateCount = 0; 
	}
	
	
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if(isObjetoJuicioRow(row)){
			cargaObjetoJuicio(row, false);
		}else if(isUniqueObjetoJuicioRow(row)){
			cargaObjetoJuicio(row, true);
		}else if(isParametroRow(row)){
			cargaParametro(row);
		}else if(isSubmateriaRow(row)){
			cargaSubmateria(row, false);
		}else if(isSubSubmateriaRow(row)){
			cargaSubmateria(row, true);
		}else if(isFinSubmateriaRow(row)){
			finSubmateria();
		}
	}

	
	protected void end() {
		info("ObjetoJuicio: "+insertCount+" filas añadidas, "+updateCount+" filas modificadas");
		if(insertParametroCount > 0 || updateParametroCount > 0){
			info("Parametro: "+insertParametroCount+" filas añadidas, "+updateParametroCount+" filas modificadas");
		}
		if(insertSubmateriaCount > 0 || updateSubmateriaCount > 0){
			info("Submateria: "+insertSubmateriaCount+" filas añadidas, "+updateSubmateriaCount+" filas modificadas");
		}
	}

	private boolean isObjetoJuicioRow(Cell[] row) {
		return OBJETO_JUICIO_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	private boolean isUniqueObjetoJuicioRow(Cell[] row) {
		return UNIQUE_OBJETO_JUICIO_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	private boolean isParametroRow(Cell[] row) {
		return PARAMETRO_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	private boolean isSubmateriaRow(Cell[] row) {
		return SUBMATERIA_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	private boolean isSubSubmateriaRow(Cell[] row) {
		return SUBSUBMATERIA_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	private boolean isFinSubmateriaRow(Cell[] row) {
		return FIN_SUBMATERIA_VALUE.equalsIgnoreCase(row[0].getContents());
	}
	
	private void finSubmateria() {
		currentParentTema = null;
		currentTema = null;		
	}


	private Parametro cargaParametro(Cell[] row) throws LoaderException {
		String columna1 = getContent(row, 1);
		Camara camara = null;
		if(!Strings.isEmpty(columna1)){
			camara = buscaCamara(columna1);
		}
		String codigo = getContent(row, 2);		
		String type = getContent(row, 3);		
		String lengthStr = getContent(row, 4);		
		boolean required = getBoolean(row, 5, true);		
		String label = getContent(row, 6);
		boolean multiple = getBoolean(row, 7, false);		
		String comment = getContent(row, 8);
		String initData = getContent(row, 9);
		boolean editable = getBoolean(row, 10, true);		

		Integer length = null;
		if(!isEmpty(lengthStr)){
				try {
					length = Integer.valueOf(lengthStr);
				} catch (NumberFormatException e) {
					fatal("Longitud erronea: "+lengthStr);
				}
		}
		return actualizaParametro(camara, codigo, type, length, required, label, multiple, comment, initData, editable);
		
	}

	private Parametro actualizaParametro(Camara camara, String codigo, String tipo, Integer length, boolean required, String labelParametro, boolean multiple, String comment, String initData, boolean editable) throws LoaderException {
		Parametro parametro = null;
		boolean update = false;

		if(isEmpty(labelParametro)){
			fatal("Parametro con descripción vacía");
		}
		if(isEmpty(codigo)){
			codigo = normalizeId(labelParametro, false, false, getParametrosPredefinidos());
			
		}
		parametro = parametrosMap.get(codigo);
		if(parametro == null){
			parametro = buscaParametro(getEntityManager(), camara, codigo);
			if (parametro == null) {
				parametro = new Parametro();
				parametro.setName(codigo);
			}
			parametro.setCamara(camara);
			parametro.setLabel(labelParametro);
			parametro.setType(checkTipoDato(tipo));
			parametro.setLength(length);
			parametro.setParameters(null);
			if(required){
				addParameter(parametro, DataSheetPropertyUtils.REQUIRED_PARAMETER+"=true");
			}
			if(!editable){
				addParameter(parametro, DataSheetPropertyUtils.DISABLED_PARAMETER+"=true");
			}
			if(!isEmpty(initData)){
				addParameter(parametro, DataSheetPropertyUtils.INIT_DATA_PARAMETER+"="+initData);
			}
			if(!isEmpty(comment)){
				addParameter(parametro, DataSheetPropertyUtils.COMMENT_PARAMETER+"="+comment);
				addParameter(parametro, DataSheetPropertyUtils.LAYOUT_PARAMETER+"="+"editComment");
			}
			parametro.setMultiple(multiple);
			parametro.setStatus(0);
			if (parametro.getId() == null) {
				getEntityManager().persist(parametro);
				insertParametroCount++;
			}else{
				updateParametroCount++;
			}
			getEntityManager().flush();
			parametrosMap.put(codigo, parametro);
		}
		return parametro;
	}


	private void addParameter(Parametro parametro, String newParameter) {
		if (!Strings.isEmpty(newParameter)) {
			StringBuffer sb = new StringBuffer();
			if(parametro.getParameters() != null) {
				sb.append(parametro.getParameters());
				sb.append("\n");
			}
			sb.append(newParameter);
			parametro.setParameters(sb.toString());
		}
	}

	private String checkTipoDato(String tipo) throws LoaderException {
		String ret = TipoDatoEnumeration.Type.alfanumericoMayusculas.getValue().toString();
		if(!isEmpty(tipo)){
			Type[] values = TipoDatoEnumeration.Type.values();
			ret = null;
			for(Type type: values){
				if(type.getValue().equals(tipo)){
					ret = tipo;
					break;
				}
			}
			if(ret == null){
				fatal("tipo de dato desconocido :"+ tipo);
			}
		}
		return ret;
	}

	private Tema cargaSubmateria(Cell[] row, boolean child) throws LoaderException {
		Camara camara = buscaCamara(getContent(row, 1));
		if(camara == null){
			fatal("Proceso abortado");
		}
		
		Materia materia = buscaMateria(getContent(row, 2), true);		
		if(materia == null){
			fatal("Proceso abortado");
		}
		
		String codigo = getContent(row, 5);		
		String descripcion = getContent(row, 6);
		return actualizaSubmateria(camara, materia, codigo, descripcion, child);
		
	}

	private Tema actualizaSubmateria(Camara camara, Materia materia, String codigo, String descripcion, boolean child) throws LoaderException {
		Tema tema = null;
		boolean update = false;

		if(isEmpty(descripcion)){
			fatal((child ? "SubSubmateria" : "Submateria") + "con descripción vacía");
		}
		if(isEmpty(codigo)){
			codigo = normalizeId(descripcion, false, false, null);			
		}
		tema = buscaTema(codigo, materia, camara, false);
		if (tema == null) {
			tema = new Tema();
			tema.setCodigo(codigo);
			tema.setCamara(camara);
			tema.setMateria(materia);
		}
		tema.setDescripcion(descripcion);
		tema.setStatus(0);
		if(child){
			tema.setTemaPadre(currentParentTema);
		}
		if(Strings.isEmpty(tema.getUuid())){
			tema.setUuid(UUID.randomUUID().toString());
		}		
		if (tema.getId() == null) {
			getEntityManager().persist(tema);
			insertSubmateriaCount++;
		}else{
			updateSubmateriaCount++;
		}
		getEntityManager().flush();
		if(!child){
			currentParentTema = tema;
		}
		currentTema = tema;
		
		return tema;
	}

	

	private ObjetoJuicio cargaObjetoJuicio(Cell[] row, boolean unique) throws LoaderException {
		Camara camara = buscaCamara(getContent(row, 1));
		if(camara == null){
			fatal("Proceso abortado");
		}
		
		Materia materia = buscaMateria(getContent(row, 2), true);		
		if(materia == null){
			fatal("Proceso abortado");
		}
		Competencia competencia = isEmpty(row, 3) ? null : buscaCompetencia(camara, getContent(row, 3), false);	
		
		String descripcionTipoProceso = getContent(row, 4);
		String codigoObjetoJuicio = getContent(row, 5);		
		String descripcion = getContent(row, 6);
		String rubro = getContent(row, 7);
		String rubroEstadistico = getContent(row, 8);
		String rubroAsignacion = getContent(row, 9);
		String instancia = getContent(row, 10);
		String codigoRecurso = getContent(row, 11);
		TipoRemesa tipoRemesa = isEmpty(row, 12) ? null : buscaTipoRemesa(getContent(row, 12), camara, true);
		boolean buscaConexidad = getBoolean(row, 13, true);		
		String asignaConexidad = getContent(row, 14);
		boolean seleccionaConexidad = getBoolean(row, 15, false);		
		boolean voluntario = "V".equalsIgnoreCase(getContent(row, 16)) || "SI".equalsIgnoreCase(getContent(row, 16));
		String parametros = getContent(row, 17);
		Integer status = getInteger(row, 18, 0);

		boolean noConexidadCaratula = getBoolean(row, 19, false);		
		boolean conexidadSolicitadaMesa = getBoolean(row, 20, false);		
		String rubro2 = getContent(row, 21);

		return actualizaObjetoJuicio(camara, materia, descripcionTipoProceso, competencia, codigoObjetoJuicio, descripcion, rubro, rubroEstadistico, rubroAsignacion, instancia, codigoRecurso, 
				parametros, tipoRemesa, buscaConexidad, asignaConexidad, seleccionaConexidad, voluntario, unique, status, noConexidadCaratula, conexidadSolicitadaMesa, rubro2);
		
	}


	private ObjetoJuicio actualizaObjetoJuicio(Camara camara, Materia materia,
			String descripcionTipoProceso, Competencia competencia, String codigoObjetoJuicio,
			String descripcion, String rubro, String rubroEstadistico,String rubroAsignacion,
			String instancia, String codigoTipoRecurso,
			String parametros,	TipoRemesa tipoRemesa, 
			boolean buscaConexidad, 
			String asignaConexidad, 
			boolean seleccionaConexidad,
			boolean voluntario,
			boolean uniqueByMateria,
			Integer status,
			boolean noConexidadCaratula,
			boolean conexidadSolicitadaMesa,
			String rubro2
			) throws LoaderException {
		
		ObjetoJuicio objetoJuicio = null;		
		boolean isNew = true;
		if (getEntityManager() != null) {
			if(materia == null){
				fatal("Proceso abortado");
			}
			if(uniqueByMateria){
				List<ObjetoJuicio> list = buscaObjetosJuicio(camara, materia);	
				if(list.size()>1){
					fatal("Hay mas de un Objeto de Juicio para esta materia/camara");
				}else if(list.size()==1){
					objetoJuicio = list.get(0);
				}
			}
			if(objetoJuicio == null){
				if(camara != null && materia != null && codigoObjetoJuicio != null){
					objetoJuicio = buscaObjetoJuicio(camara, materia, codigoObjetoJuicio, false);
				}
				if(objetoJuicio == null && descripcionTipoProceso != null){
					objetoJuicio = buscaObjetoJuicio(camara, descripcionTipoProceso, codigoObjetoJuicio, false);				
				}
			}
			
			TipoProceso tipoProceso = null;
			if(uniqueByMateria){
				List<TipoProceso> list = buscaTiposProceso(materia);
				if(list.size() > 1){
					fatal("Hay mas de un Tipo de Proceso para esta materia");						
				}else if(list.size() == 1){
					tipoProceso = list.get(0);
				}
			}
			if(tipoProceso == null){
				if(descripcionTipoProceso != null){
					tipoProceso = buscaTipoProceso(descripcionTipoProceso, materia);
				}else{
					tipoProceso = buscaTipoProcesoDefault(materia, false);
					if(tipoProceso == null){
						tipoProceso = buscaTipoProceso("ORDINARIO", materia);
					}
				}
			}
			if(tipoProceso == null){
				fatal("Tipo de proceso requerido.");
			}
				
			if (objetoJuicio == null) {
				objetoJuicio = new ObjetoJuicio();
				objetoJuicio.setCodigo(codigoObjetoJuicio);				
				objetoJuicio.setMateria(materia);				
				objetoJuicio.setCamara(camara);
				objetoJuicio.setPeso(BigDecimal.ONE);
				System.out.println("Nuevo Objeto Juicio: "+codigoObjetoJuicio);
			}else{
				compareOJ(objetoJuicio, 
						tipoProceso,  
						competencia, 
						codigoObjetoJuicio, 
						descripcion, 
						rubro, 
						rubroEstadistico, 
						rubroAsignacion, 
						calculaTipoInstancia(instancia), 
						codigoTipoRecurso, 
						tipoRemesa, 
						buscaConexidad, 
						asignaConexidad, 
						seleccionaConexidad, 
						voluntario, 0,
						noConexidadCaratula,
						conexidadSolicitadaMesa,
						rubro2);
			}
			if(!simulation){
				objetoJuicio.setTipoProceso(tipoProceso);			
				objetoJuicio.setStatus(status);
				if(competencia != null){
					objetoJuicio.setCompetencia(competencia);
				}
				objetoJuicio.setDescripcion(descripcion); // TRIMAR ???
				objetoJuicio.setRubroLetter(rubro);
				objetoJuicio.setRubroEstadistico(rubroEstadistico);			
				objetoJuicio.setRubroAsignacion(rubroAsignacion);			
				objetoJuicio.setTipoInstancia(calculaTipoInstancia(instancia));
				objetoJuicio.setCodigoTipoRecurso(codigoTipoRecurso);
				objetoJuicio.setTema(currentTema);
				objetoJuicio.setBuscaConexidad(buscaConexidad);
				objetoJuicio.setAsignaConexidad(asignaConexidad);
				objetoJuicio.setSeleccionaConexidad(seleccionaConexidad);
				objetoJuicio.setVoluntario(voluntario);
				objetoJuicio.setTipoRemesa(tipoRemesa);
				objetoJuicio.setNoConexidadCaratula(noConexidadCaratula);
				objetoJuicio.setConexidadSolicitadaMesa(conexidadSolicitadaMesa);
				objetoJuicio.setRubroLetter2(rubro2);
				if(Strings.isEmpty(objetoJuicio.getUuid())){
					objetoJuicio.setUuid(UUID.randomUUID().toString());
				}
				if (objetoJuicio.getId() == null) {
					getEntityManager().persist(objetoJuicio);
					insertCount++;
				}else{
					isNew = false;
					updateCount++;
				}
				
				if(objetoJuicio.getTema() == null){
					System.out.println("FLush: oj="+objetoJuicio.getId()+",tema=null,"+objetoJuicio.getDescripcion()+"[null]");				
				}else{
					System.out.println("FLush: oj="+objetoJuicio.getId()+",tema="+objetoJuicio.getTema().getId()+","+objetoJuicio.getDescripcion()+"["+objetoJuicio.getTema().getDescripcion()+"]");
				}
				
				getEntityManager().flush();
				if(!isNew && objetoJuicio.getParametroList().size() > 0){
					objetoJuicio.getParametroList().clear();
				}				
				actualizaParametrosObjetoJuicio(objetoJuicio, isNew, parametros);

				getEntityManager().flush();
			}


		}
		return objetoJuicio;
	
	}

	private boolean compareOJ(ObjetoJuicio oj, 
			TipoProceso tipoProceso, Competencia competencia, String codigoObjetoJuicio,
			String descripcion, String rubro, String rubroEstadistico,String rubroAsignacion,
			TipoInstancia tipoInstancia, String codigoTipoRecurso,
			TipoRemesa tipoRemesa, 
			boolean buscaConexidad, 
			String asignaConexidad, 
			boolean seleccionaConexidad,
			boolean voluntario,
			Integer status,
			boolean noConexidadCaratula,
			boolean conexidadSolicitadaMesa,
			String rubro2
			) {
		boolean ret = false;
		ret |= compare(tipoProceso, oj.getTipoProceso(), "Tipo Proceso");
		ret |= compare(competencia, oj.getCompetencia(), "Competencia");
		ret |= compare(codigoObjetoJuicio, oj.getCodigo(), "codigoObjetoJuicio");
		ret |= compare(descripcion, oj.getDescripcion(), "descripcion");
		ret |= compare(rubro, oj.getRubroLetter(), "rubro");
		ret |= compare(rubroEstadistico, oj.getRubroEstadistico(), "rubroEstadistico");
		ret |= compare(rubroAsignacion, oj.getRubroAsignacion(), "rubroAsignacion");
		ret |= compare(tipoInstancia, oj.getTipoInstancia(), "tipoInstancia");
		ret |= compare(codigoTipoRecurso, oj.getCodigoTipoRecurso(), "codigoTipoRecurso");
		ret |= compare(tipoRemesa, oj.getTipoRemesa(), "tipoRemesa");
		ret |= compare(buscaConexidad, oj.isBuscaConexidad(), "buscaConexidad");
		ret |= compare(asignaConexidad, oj.getAsignaConexidad(), "asignaConexidad");
		ret |= compare(seleccionaConexidad, oj.isSeleccionaConexidad(), "seleccionaConexidad");
		ret |= compare(voluntario, oj.isVoluntario(), "voluntario");
		ret |= compare(status, oj.getStatus(), "status");
		ret |= compare(noConexidadCaratula, oj.isNoConexidadCaratula(), "noConexidadCaratula");
		ret |= compare(conexidadSolicitadaMesa, oj.isConexidadSolicitadaMesa(), "conexidadSolicitadaMesa");
		ret |= compare(rubro2, oj.getRubroLetter2(), "rubro2");
		return ret;
	}

	private boolean compare(Object o1, Object o2, String msg) {
		boolean ret;
		if(o1 != null && o2 != null){
			ret = o1.equals(o2);
		}else{
			ret = o1 == o2;
		}
		if(!ret){
			System.out.println("Diferente: "+msg);
		}
		return ret;
		
	}


	private void actualizaParametrosObjetoJuicio(ObjetoJuicio objetoJuicio, boolean isNew,	String parametros) throws LoaderException {
		if(!isNew){
			objetoJuicio.getParametroList().clear();
		}
		if (!Strings.isEmpty(parametros)) {
			StringTokenizer tk = new StringTokenizer(parametros, ",");
			while(tk.hasMoreTokens()){
				String nombreParametro = tk.nextToken();
				if(!isEmpty(nombreParametro)){
					Parametro parametro = parametrosMap.get(nombreParametro);
					if(parametro == null){
						fatal("Parametro no encontrado: "+nombreParametro);
					}
					actualizaParametroObjetoJuicio(objetoJuicio, isNew, parametro);
				}
			}
		}
		getEntityManager().flush();
	}

	private void actualizaParametroObjetoJuicio(ObjetoJuicio objetoJuicio, boolean isNew, Parametro parametro) {
		if (!objetoJuicio.getParametroList().contains(parametro)) {
			objetoJuicio.getParametroList().add(parametro);
		}
	}

	private TipoInstancia calculaTipoInstancia(String instancia) throws LoaderException {
		TipoInstancia tipoInstancia = null;
		if(!isEmpty(instancia)){
			if("1".equals(instancia)){				 
				tipoInstancia = buscaTipoInstancia(INSTANCIA_PRIMERA);				
			}else if("2".equals(instancia)){
				tipoInstancia = buscaTipoInstancia(INSTANCIA_APELACIONES);				
			}else if("6".equals(instancia)){
				tipoInstancia = buscaTipoInstancia(INSTANCIA_CORTE_SUPREMA);				
			}else{
				fatal("Codigo de instancia erroneo: Se permite solo 1, 2 o vacio");
			}
		}
		return tipoInstancia;
	}

	public static void main(String[] args) {
		File file = new File(args[0]);
		
		ObjetoJuicioLoader objetoJuicioLoader = new ObjetoJuicioLoader(null, null);
		
		try {
			objetoJuicioLoader.load(new FileInputStream(file));			
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

	private List<String> getParametrosPredefinidos() {
		if(parametrosPredefinidos == null) {
			parametrosPredefinidos = new ArrayList<String>();
			List<SelectItem> parametros = ParametroPredefinidoEnumeration.instance().getItems();
			for(SelectItem parametro: parametros) {
				parametrosPredefinidos.add((String)parametro.getValue());
			}
		}
		return parametrosPredefinidos;
	}


}
