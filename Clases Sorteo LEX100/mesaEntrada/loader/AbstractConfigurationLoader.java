package com.base100.lex100.mesaEntrada.loader;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.component.converter.UpperCaseFilteredConverter;
import com.base100.lex100.component.enumeration.TipoInstanciaEnumeration;
import com.base100.lex100.component.enumeration.TipoOficinaEnumeration;
import com.base100.lex100.entity.Asesoria;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.ConfiguracionMateria;
import com.base100.lex100.entity.Defensoria;
import com.base100.lex100.entity.Delito;
import com.base100.lex100.entity.Dependencia;
import com.base100.lex100.entity.Fiscalia;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.MotivoPase;
import com.base100.lex100.entity.NumeradorExpediente;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.Parametro;
import com.base100.lex100.entity.Provincia;
import com.base100.lex100.entity.Rubro;
import com.base100.lex100.entity.Sigla;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.Tema;
import com.base100.lex100.entity.TipoCaratula;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoInterviniente;
import com.base100.lex100.entity.TipoLetrado;
import com.base100.lex100.entity.TipoLugarInternamiento;
import com.base100.lex100.entity.TipoOficina;
import com.base100.lex100.entity.TipoProceso;
import com.base100.lex100.entity.TipoRecurso;
import com.base100.lex100.entity.TipoRemesa;
import com.base100.lex100.entity.TipoSubexpediente;
import com.base100.lex100.entity.TurnoFiscalia;

public abstract class AbstractConfigurationLoader {

	private static final String SPACE_DELIMITERS = "_. ()'´%<>";

	protected static final String INSTANCIA_PRIMERA = "PRIMERA";
	protected static final String INSTANCIA_APELACIONES = "APELACIONES";
	protected static final String INSTANCIA_TRIBUNAL_ORAL = "TRIBUNAL ORAL";
	protected static final String INSTANCIA_MESA = "MESA";
	protected static final String INSTANCIA_CASACION = "CASACION";
	protected static final String INSTANCIA_CORTE_SUPREMA = "CORTE SUPREMA";

	private static final int MAX_NUM_MESSAGES = 20;

	private String FIN_VALUE = "FIN";

	private EntityManager entityManager;
	private StatusMessages statusMessages;
	private int currentRow = 0;
	private int numErrors = 0;

	private String forzeCamara;
	
	public AbstractConfigurationLoader(EntityManager entityManager, StatusMessages statusMessages) {
		this.entityManager = entityManager;
		this.statusMessages = statusMessages;
	}
	
	public void load(InputStream in) throws BiffException, IOException, LoaderException {
		WorkbookSettings ws = new WorkbookSettings();
		ws.setEncoding("Cp1252");
		Workbook workbook = Workbook.getWorkbook(in, ws);
		Sheet mainSheet = workbook.getSheet(0);
		processSheet(mainSheet);
	}
	
	protected void addMessage(Severity severity, String message){
		if(statusMessages != null){
			message = "Linea "+currentRow+": "+message;
			statusMessages.add(severity, message);
		}
	}

	protected void error(String message) throws LoaderException {
		addMessage(Severity.ERROR, message);
		numErrors++;
		if(numErrors > MAX_NUM_MESSAGES){
			throw new LoaderException("Demasiados errores");
		}
	}
	
	protected void warning(String message){
		addMessage(Severity.WARN, message);		
	}
	
	protected void info(String message){
		addMessage(Severity.INFO, message);		
	}

	protected void fatal(String message) throws LoaderException{
		addMessage(Severity.ERROR, message);
		throw new LoaderException(message);
	}
	
	protected void processSheet(Sheet sheet) throws LoaderException {
		int numEmptyRows = 0;
		init();		
		boolean fin = false;
		for(currentRow = 0; !fin; currentRow++){			
			if(currentRow >= sheet.getRows()){
				fin = true;
			}else{
				Cell[] row = sheet.getRow(currentRow);
				if(isEmptyRow(row)){
					numEmptyRows++;
					if(numEmptyRows > 10){
						fin = true;
					}
				}else{
					numEmptyRows = 0;
					if(isFinRow(row)){
						fin = true;
					}else{
						processRow(row, currentRow);
					}
				}
			}
		}
		end();
	}
	
	abstract protected void init();
	abstract protected  void processRow(Cell[] row, int numRow) throws LoaderException;
	abstract protected void end();

	private boolean isFinRow(Cell[] row) {
		return FIN_VALUE.equalsIgnoreCase(getContent(row, 0));
	}


	private boolean isEmptyRow(Cell[] row) {
		return Strings.isEmpty(getContent(row, 0));
	}
	
	protected boolean isEmpty(Cell[] row, int col) {
		return Strings.isEmpty(getContent(row, col));
	}

	protected boolean isEmpty(String s) {
		return Strings.isEmpty(s);
	}


	protected int getInt(Cell[] row, int col, int defaulValue) throws LoaderException {
		String s = getUpperContent(row, col);
		int ret = defaulValue;
		if(s != null){
			try {
				ret = Integer.valueOf(s);
			} catch (NumberFormatException e) {
				error("Error en valor entero :"+s);
			}
		}
		return ret;
	}
	
	protected Date getDate(Cell[] row, int col, Date defaultValue) throws LoaderException {
		String s = getContent(row, col);
		Date ret = defaultValue;
		if(s != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			try {
				ret = sdf.parse(s);
			} catch (ParseException e) {
				error("Error en valor fecha "+s);
			}
		}
		return ret;
	}

	protected Date getDate(Cell[] row, int col) throws LoaderException {
		return getDate(row, col, null);
	}

	protected Integer getInteger(Cell[] row, int col, Integer defaulValue) throws LoaderException {
		String s = getContent(row, col);
		Integer ret = defaulValue;
		if(s != null){
			try {
				ret = Integer.valueOf(s);
			} catch (NumberFormatException e) {
				error("Error en valor entero :"+s);
			}
		}
		return ret;
	}

	protected boolean getBoolean(Cell[] row, int col, boolean defaultValue) throws LoaderException {
		boolean ret = defaultValue;
		String s = getUpperContent(row, col);
		if(s != null){
			ret = s.equals("SI");
			checkValues(s, "SI", "NO");
		}
		return ret;
	}
	
	protected Boolean getBoolean(Cell[] row, int col) throws LoaderException {
		Boolean ret = null;
		String s = getUpperContent(row, col);
		if(s != null){
			ret = s.equals("SI");
			checkValues(s, "SI", "NO");
		}
		return ret;
	}
	
	protected void unexpectedValue(String value, String... expected) throws LoaderException{
		StringBuffer sb = new StringBuffer();
		sb.append("Valor "+value+" no valido. Se esperaba: ");
		boolean first = true;
		for(String item: expected){
			sb.append(first ? item : ", "+item);
			first = false;
		}
		error(sb.toString());		
	}

	private void checkValues(String value, String... args) throws LoaderException{
		boolean ok = false;
		for(String item: args){
			if(item.equals(value)){
				ok = true;
				break;
			}
		}
		if(!ok){
			unexpectedValue(value, args);
		}
	}

	protected String getUpperFilteredContent(Cell[] row, int col) {
		String s = getContent(row, col);
		if(s != null){
			s = UpperCaseFilteredConverter.filterValue(s);
		}
		return s;
	}

	protected String getUpperContent(Cell[] row, int col) {
		String s = getContent(row, col);
		if(s != null){
			s = s.toUpperCase();
		}
		return s;
	}

	protected String getContent(Cell[] row, int col) {
		String s = row.length > col ? row[col].getContents() : null;
		if(s!=null){
			s = s.trim();
		}
		return s != null && s.length() > 0 ? s.trim() : null;
	}



	@SuppressWarnings("unchecked")
	protected TipoInstancia buscaTipoInstancia(String tipoInstancia) throws LoaderException {
		if (tipoInstancia != null) {
			Integer codigo = null;
			if (tipoInstancia.equals(INSTANCIA_PRIMERA)) {
				codigo = TipoInstanciaEnumeration.TIPO_INSTANCIA_PRIMERA_INSTANCIA;
			} else if (tipoInstancia.equals(INSTANCIA_APELACIONES)) {
				codigo = TipoInstanciaEnumeration.TIPO_INSTANCIA_APELACIONES;
			} else if (tipoInstancia.equals(INSTANCIA_TRIBUNAL_ORAL)) {
				codigo = TipoInstanciaEnumeration.TIPO_INSTANCIA_TRIBUNAL_ORAL;
			} else if (tipoInstancia.equals(INSTANCIA_MESA)) {
				codigo = TipoInstanciaEnumeration.TIPO_INSTANCIA_MESA;
			} else if (tipoInstancia.equals(INSTANCIA_CASACION)) {
				codigo = TipoInstanciaEnumeration.TIPO_INSTANCIA_CASACION;
			} else if (tipoInstancia.equals(INSTANCIA_CORTE_SUPREMA)) {
				codigo = TipoInstanciaEnumeration.TIPO_INSTANCIA_CORTESUPREMA;
			}
			if (codigo != null) {
				List<TipoInstancia> tipoInstanciaList =
				entityManager.createQuery("from TipoInstancia where codigo = :codigo and status <> -1")
					.setParameter("codigo", codigo)
					.getResultList();
				if (tipoInstanciaList.size() == 1) {
					return tipoInstanciaList.get(0);
				}
			}
			error("Tipo Instancia '"+tipoInstancia+"' no encontrada o ambigua");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected TipoInstancia buscaTipoInstanciaByAbreviatura(String abreviatura) throws LoaderException {
		if (abreviatura != null) {
			List<TipoInstancia> tipoInstanciaList =
			entityManager.createQuery("from TipoInstancia where abreviatura = :abreviatura and status <> -1")
				.setParameter("abreviatura", abreviatura)
				.getResultList();
			if (tipoInstanciaList.size() == 1) {
				return tipoInstanciaList.get(0);
			}
			error("Tipo Instancia '"+abreviatura+"' no encontrada o ambigua");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected TipoOficina buscaTipoOficina(String tipoOficina) throws LoaderException {
		Integer codigo = null;
		if (tipoOficina.equals("JUZGADO")) {
			codigo = TipoOficinaEnumeration.TIPO_OFICINA_JUZGADO;
		} else if (tipoOficina.equals("SALA")) {
			codigo = TipoOficinaEnumeration.TIPO_OFICINA_SALA_APELACIONES;
		} else if (tipoOficina.equals("SECRETARIA")) {
			codigo = TipoOficinaEnumeration.TIPO_OFICINA_SECRETARIA_JUZGADO;
		} else if (tipoOficina.equals("SECRETARIA ESPECIAL")) {
			codigo = TipoOficinaEnumeration.TIPO_OFICINA_SECRETARIA_ESPECIAL;
		} else if (tipoOficina.equals(INSTANCIA_TRIBUNAL_ORAL)) {
			codigo = TipoOficinaEnumeration.TIPO_OFICINA_TRIBUNAL_ORAL;
		} else if (tipoOficina.equals(INSTANCIA_MESA)) {
			codigo = TipoOficinaEnumeration.TIPO_OFICINA_MESA_ENTRADA;
		} else if (tipoOficina.equals("FISCALIA")) {
			codigo = TipoOficinaEnumeration.TIPO_OFICINA_FISCALIA;
		} else if (tipoOficina.equals("OTROS")) {
			codigo = TipoOficinaEnumeration.TIPO_OFICINA_OTROS;
		} else if (tipoOficina.equals("MEDIACION")) {
			codigo = TipoOficinaEnumeration.TIPO_OFICINA_MEDIACION;
		} else if (tipoOficina.equals("VOCALIA")) {
			codigo = TipoOficinaEnumeration.TIPO_OFICINA_VOCALIA;
		} else if (tipoOficina.equals("DEFENSORIA")) {
			codigo = TipoOficinaEnumeration.TIPO_OFICINA_DEFENSORIA;
		} else if (tipoOficina.equals("PERITO")) {
			codigo = TipoOficinaEnumeration.TIPO_OFICINA_OTROS;
		}else{
			codigo = TipoOficinaEnumeration.TIPO_OFICINA_OTROS;
			error("Tipo Oficina '"+tipoOficina+"' no encontrada");
		}
		if (codigo != null) {
			List<TipoOficina> tipoOficinaList =
				entityManager.createQuery("from TipoOficina where codigo = :codigo and status <> -1")
					.setParameter("codigo", codigo)
					.getResultList();
			if (tipoOficinaList.size() == 1) {
				return tipoOficinaList.get(0);
			}
			error("Tipo Oficina '"+tipoOficina+"' no encontrada o ambigua");

		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Dependencia buscaDependencia(String codigoDependencia, String tipo)  throws LoaderException {
		Dependencia dependencia = null;
		List<Dependencia> dependenciaList =
		entityManager.createQuery("from Dependencia where codigo = :codigo and tipo = :tipo and status <> -1")
			.setParameter("codigo", codigoDependencia)
			.setParameter("tipo", tipo)
			.getResultList();
		if (dependenciaList.size() > 0) {
			dependencia = dependenciaList.get(0);
		} else if (codigoDependencia.charAt(0) != 'I'){
			dependencia = buscaDependencia("I" + codigoDependencia);
		}
		if (dependencia == null) {
			dependencia = buscaDependencia("I00000001");	//entityManager.find(Dependencia.class, 8471);
		} 
		if (dependencia == null) {
			error("Dependencia '"+codigoDependencia+"' no encontrada");
		}
		return dependencia;
	}

	@SuppressWarnings("unchecked")
	protected Dependencia buscaDependencia(String codigoDependencia)  throws LoaderException {
		Dependencia dependencia = null;
		if(Strings.nullIfEmpty(codigoDependencia) != null){
			if (codigoDependencia.charAt(0) == 'I'){
				codigoDependencia = codigoDependencia.substring(1, codigoDependencia.length());
				return buscaDependencia(codigoDependencia, "I");
			}
			if (codigoDependencia.charAt(0) == 'E'){
				codigoDependencia = codigoDependencia.substring(1, codigoDependencia.length());
				return buscaDependencia(codigoDependencia, "E");
			}

			
			List<Dependencia> dependenciaList =
			entityManager.createQuery("from Dependencia where codigo = :codigo and status <> -1")
				.setParameter("codigo", codigoDependencia)
				.getResultList();
			if (dependenciaList.size() == 1) {
				dependencia = dependenciaList.get(0);
			}else{
				error("Dependencia '"+codigoDependencia+"' no encontrada");
			}
		}
		return dependencia;
	}
	
	@SuppressWarnings("unchecked")
	protected Sorteo buscaSorteo(String codigoSorteo, Camara camara, boolean required)  throws LoaderException {
		List<Sorteo> sorteoList =
			entityManager.createQuery("from Sorteo where codigo = :codigo and status <> -1 and camara = :camara")
				.setParameter("codigo", codigoSorteo)
				.setParameter("camara", camara)
				.getResultList();
		if (sorteoList.size() == 1) {
			return sorteoList.get(0);
		}else{
			if(required){
				error("Sorteo '"+codigoSorteo+"' no encontrado o ambiguo");
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected TipoCausa buscaTipoCausa(Camara camara, Materia materia, String codigo, boolean required)  throws LoaderException {
		List<TipoCausa> list =
			entityManager.createQuery("from TipoCausa where camara = :camara and codigo = :codigo and status <> -1 and materia = :materia")
				.setParameter("codigo", codigo)
				.setParameter("camara", camara)
				.setParameter("materia", materia)
				.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}else{
			if(required){
				error("Tipo Causa '"+codigo+"' no encontrada o ambigua");
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected TipoRecurso buscaTipoRecurso(Materia materia, String codigo, boolean required)  throws LoaderException {
		List<TipoRecurso> list =
			entityManager.createQuery("from TipoRecurso where codigo = :codigo and status <> -1 and materia = :materia")
				.setParameter("codigo", codigo)
				.setParameter("materia", materia)
				.getResultList();
		if (list.size() > 0) {
			return list.get(0);
		}else{
			if(required){
				error("Tipo Elevación '"+codigo+"' no encontrado o ambiguo");
			}
		}
		return null;
	}

	
	@SuppressWarnings("unchecked")
	protected TipoSubexpediente buscaTipoSubexpediente(Materia materia, Integer codigo, String abreviatura, String descripcion, boolean required)  throws LoaderException {
		TipoSubexpediente ret = null;
		boolean ambiguo = false;
		
		if(abreviatura != null){ // busca primero por  abreviatura
			List<TipoSubexpediente> list =
				entityManager.createQuery("from TipoSubexpediente where abreviatura = :abreviatura and status <> -1 and materia = :materia")
					.setParameter("abreviatura", abreviatura)
					.setParameter("materia", materia)
					.getResultList();
			if (list.size() == 1) {
				ret = list.get(0);
			}
			ambiguo = (list.size() > 1);
		}
		if(ret == null && codigo != null){ // busca por codigo
			List<TipoSubexpediente> list =
			entityManager.createQuery("from TipoSubexpediente where codigo = :codigo and status <> -1 and materia = :materia")
				.setParameter("codigo", codigo)
				.setParameter("materia", materia)
				.getResultList();
			if (list.size() == 1) {
				ret = list.get(0);
			}
			ambiguo = (list.size() > 1);
		}
		if(ret == null && descripcion != null){ // busca por descripcion
			List<TipoSubexpediente> list =
			entityManager.createQuery("from TipoSubexpediente where descripcion = :descripcion and status <> -1 and materia = :materia")
				.setParameter("descripcion", descripcion)
				.setParameter("materia", materia)
				.getResultList();
			if (list.size() == 1) {
				return list.get(0);
			}
		}

		if(ret == null && required){
			error("Tipo Subexpediente '"+abreviatura+"' '"+codigo+"' '"+descripcion+"' no encontrado o ambiguo");
		} else if (ambiguo) {
			error("Tipo Subexpediente '"+abreviatura+"' '"+codigo+"' '"+descripcion+"' ambiguo");
		}
		return ret;
	}

	
	@SuppressWarnings("unchecked")
	protected Oficina buscaOficina(String codigoOficina)  throws LoaderException {
		List<Oficina> oficinaList =
			entityManager.createQuery("from Oficina where codigo = :codigo and status <> -1")
				.setParameter("codigo", codigoOficina)
				.getResultList();
		if (oficinaList.size() == 1) {
			return oficinaList.get(0);
		}else{
			error("Oficina '"+codigoOficina+"' no encontrada o ambigua");						
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Materia buscaMateria(String abreviaturaMateria, boolean required)  throws LoaderException {
		List<Materia> list = entityManager.createQuery("from Materia where abreviatura = :abreviatura and status <> -1")
			.setParameter("abreviatura", abreviaturaMateria)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}else{
			if(required){
				error("Materia '"+abreviaturaMateria+"' no encontrada o ambigua");
			}
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<Materia> buscaMateriaList(String abreviaturaMateria)  throws LoaderException {
		List<Materia> materiaList = new ArrayList<Materia>();
		StringTokenizer tokenizer = new StringTokenizer(abreviaturaMateria, ", ");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			List<Materia> list =
			entityManager.createQuery("from Materia where abreviatura = :abreviatura and status <> -1")
				.setParameter("abreviatura", token)
				.getResultList();
			if (list.size() == 1) {
				materiaList.add(list.get(0));
			}else{
				error("Materia '"+token+"' no encontrada o ambigua");										
			}
		}
		return materiaList;
	}

	@SuppressWarnings("unchecked")
	protected Camara buscaCamara(String codigoCamara)  throws LoaderException {
		if(!isEmpty(forzeCamara)){
			codigoCamara = forzeCamara;
		}
		Integer codigo = Integer.valueOf(codigoCamara);
		List<Camara> camaraList = 
			entityManager.createQuery("from Camara where codigo = :codigo and status <> -1")
				.setParameter("codigo", codigo)
				.getResultList();
		if (camaraList.size() == 1) {
			return camaraList.get(0);
		}
		error("Camara '"+codigoCamara+"' no encontrada o ambigua");										
		return null;
	}

	@SuppressWarnings("unchecked")
	protected List<TipoProceso> buscaTiposProceso(Materia materia) throws LoaderException {
		List<TipoProceso> list = 
			entityManager.createQuery("from TipoProceso where materia = :materia and status <> -1")
			.setParameter("materia", materia)
			.getResultList();
		return list;
	}
	
	
	@SuppressWarnings("unchecked")
	protected TipoProceso buscaTipoProceso(String descripcion, Materia materia) throws LoaderException {
		if(descripcion != null){
			descripcion = descripcion.toLowerCase();
			List<TipoProceso> list = 
				entityManager.createQuery("from TipoProceso where lower(descripcion) = :descripcion and materia = :materia and status <> -1")
				.setParameter("descripcion", descripcion)
				.setParameter("materia", materia)
				.getResultList();
			if (list.size() == 1) {
				return list.get(0);
			}
		}
		error("TipoProceso '"+descripcion+"' no encontrado o ambiguo");										
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected TipoProceso buscaTipoProcesoDefault(Materia materia, boolean required) throws LoaderException {
		List<TipoProceso> list = 
			entityManager.createQuery("from TipoProceso where materia = :materia and status <> -1")
			.setParameter("materia", materia)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("TipoProceso por defecto ambiguo");
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected TipoRemesa buscaTipoRemesa(String codigo, Camara camara, boolean required) throws LoaderException {
		List<TipoRemesa> list = 
				entityManager.createQuery("from TipoRemesa where codigo = :codigo and camara = :camara and status <> -1")
				.setParameter("codigo", codigo)
				.setParameter("camara", camara)
				.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("TipoRemesa '"+codigo+"' no encontrada o ambigua");										
		} else if (list.size() > 1) {
			error("TipoRemesa '"+codigo+"' ambiguo");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Tema buscaTema(String codigo, Materia materia, Camara camara, boolean required) throws LoaderException {
		List<Tema> list = 
			entityManager.createQuery("from Tema where codigo = :codigo and camara = :camara and materia = :materia and status <> -1")
			.setParameter("codigo", codigo)
			.setParameter("camara", camara)
			.setParameter("materia", materia)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("Tema '"+codigo+"' no encontrado o ambiguo");
		} else if (list.size() > 1) {
			error("Tema '"+codigo+"' ambiguo");
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected Tema buscaTemaPorDescripcion(String descripcion, Materia materia, Camara camara, boolean required) throws LoaderException {
		List<Tema> list = 
			entityManager.createQuery("from Tema where (descripcion = :descripcion or codigo = :codigo) and camara = :camara and materia = :materia and status <> -1")
			.setParameter("descripcion", descripcion)
			.setParameter("codigo", descripcion)
			.setParameter("camara", camara)
			.setParameter("materia", materia)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("Tema '"+descripcion+"' no encontrado o ambiguo");										
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected ObjetoJuicio buscaObjetoJuicio(Camara camara, Materia materia, String codigo, boolean required) throws LoaderException {
		List<ObjetoJuicio> list = 
			entityManager.createQuery("from ObjetoJuicio where codigo = :codigo and camara = :camara and tipoProceso.materia = :materia and status = 0")
			.setParameter("codigo", codigo)
			.setParameter("camara", camara)
			.setParameter("materia", materia)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}else if(list.size() == 0){
			list = 
			entityManager.createQuery("from ObjetoJuicio where codigo = :codigo and camara = :camara and tipoProceso.materia = :materia and status != -1")
			.setParameter("codigo", codigo)
			.setParameter("camara", camara)
			.setParameter("materia", materia)
			.getResultList();
		}
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("ObjetoJuicio '"+codigo+"' no encontrado o ambiguo");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected List<ObjetoJuicio> buscaObjetosJuicio(Camara camara, Materia materia) throws LoaderException {
		List<ObjetoJuicio> list = 
			entityManager.createQuery("from ObjetoJuicio where camara = :camara and tipoProceso.materia = :materia and status <> -1")
			.setParameter("camara", camara)
			.setParameter("materia", materia)
			.getResultList();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	protected ObjetoJuicio buscaObjetoJuicio(Camara camara, String descripcionTipoProceso,	String codigo, boolean required) throws LoaderException {
		List<ObjetoJuicio> list = 
			entityManager.createQuery("from ObjetoJuicio where camara = :camara and codigo = :codigo and tipoProceso.descripcion = :descripcionTipoProceso and status <> -1")
			.setParameter("camara", camara)
			.setParameter("codigo", codigo)
			.setParameter("descripcionTipoProceso", descripcionTipoProceso)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if (list.size() > 1) {
			fatal("ObjetoJuicio '"+codigo+"' ambiguo");
		}
		if(required){
			error("ObjetoJuicio '"+codigo+"' no encontrado o ambiguo");
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected TipoProceso buscaTipoProceso(Materia materia, int codigo,	boolean required) throws LoaderException {
		List<TipoProceso> list = 
			entityManager.createQuery("from TipoProceso where codigo = :codigo and materia = :materia and status <> -1")
			.setParameter("codigo", codigo)
			.getResultList();
		if (list.size() > 1) {
			fatal("TipoProceso '"+codigo+"' ambiguo");
		}
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("TipoProceso '"+codigo+"' no encontrado o ambiguo");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected TipoProceso buscaTipoProceso(Materia materia, String descripcion,	boolean required)  throws LoaderException {
		List<TipoProceso> list = 
			entityManager.createQuery("from TipoProceso where descripcion = :descripcion and materia = :materia and status <> -1")
			.setParameter("descripcion", descripcion)
			.setParameter("materia", materia)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if (list.size() > 1) {
			fatal("TipoProceso '"+descripcion+"' ambiguo");
		}
		if(required){
			error("TipoProceso '"+descripcion+"' no encontrado o ambiguo");
		}
		return null;
	}	
	
	@SuppressWarnings("unchecked")
	protected TipoInterviniente buscaTipoInterviniente(TipoProceso tipoProceso, String abreviatura, String descripcion, boolean required)  throws LoaderException {
		List<TipoInterviniente> list = 
			entityManager.createQuery("from TipoInterviniente where abreviatura = :abreviatura and upper(descripcion) = :descripcion and tipoProceso = :tipoProceso and status = 0")
			.setParameter("descripcion", descripcion.toUpperCase())
			.setParameter("abreviatura", abreviatura)
			.setParameter("tipoProceso", tipoProceso)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
			
		list = entityManager.createQuery("from TipoInterviniente where abreviatura = :abreviatura and upper(descripcion) = :descripcion and tipoProceso = :tipoProceso and status <> -1")
			.setParameter("descripcion", descripcion.toUpperCase())
			.setParameter("abreviatura", abreviatura)
			.setParameter("tipoProceso", tipoProceso)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		list = 
			entityManager.createQuery("from TipoInterviniente where upper(descripcion) = :descripcion and tipoProceso = :tipoProceso and status <> -1")
			.setParameter("descripcion", descripcion.toUpperCase())
			.setParameter("tipoProceso", tipoProceso)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		
		if(required){
			error("TipoInterviniente '"+descripcion+"' no encontrado o ambiguo");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected TipoInterviniente buscaTipoInterviniente(TipoProceso tipoProceso, String descripcion, boolean required)  throws LoaderException {
		List<TipoInterviniente> list = 
			entityManager.createQuery("from TipoInterviniente where descripcion = :descripcion and tipoProceso = :tipoProceso and status <> -1")
			.setParameter("descripcion", descripcion)
			.setParameter("tipoProceso", tipoProceso)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("TipoInterviniente '"+descripcion+"' no encontrado o ambiguo");
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected TipoInterviniente buscaTipoIntervinienteStatus0(Materia materia,  String descripcion, boolean required)  throws LoaderException {
		List<TipoInterviniente> list = 
			entityManager.createQuery("from TipoInterviniente where descripcion = :descripcion and tipoProceso.materia = :materia and status = 0")
			.setParameter("materia", materia)
			.setParameter("descripcion", descripcion)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("TipoInterviniente '"+descripcion+"' no encontrado o ambiguo");
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected Competencia buscaCompetencia(Camara camara, String codigo, boolean required) throws LoaderException {
		List<Competencia> list = 
			entityManager.createQuery("from Competencia where camara = :camara and codigo = :codigo and status <> -1")
			.setParameter("camara", camara)
			.setParameter("codigo", codigo)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("Competencia '"+codigo+"' no encontrado o ambiguo");
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	protected TipoCaratula buscaTipoCaratula(Integer codigo, boolean required) throws LoaderException {
		List<TipoCaratula> list = 
			entityManager.createQuery("from TipoCaratula where codigo = :codigo and status <> -1")
			.setParameter("codigo", codigo)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("TipoCaratula '"+codigo+"' no encontrado o ambiguo");
		}
		return null;
	}
	
	protected TipoCaratula buscaTipoCaratula(String codigo, boolean required) throws LoaderException {
		TipoCaratula tipoCaratula = null;
		if(!Strings.isEmpty(codigo)){
			Integer codigo_entero = null;
			codigo_entero = Integer.valueOf(codigo);
			if(codigo_entero != null){
				tipoCaratula = buscaTipoCaratula(codigo_entero, required);
			}
		}			
		return tipoCaratula;
	}
	
	@SuppressWarnings("unchecked")
	public TipoLetrado buscaTipoLetrado(Materia materia, String codigo, String descripcion, boolean required ) throws LoaderException {
		List<TipoLetrado> list = null;
		if(codigo != null && descripcion != null){
			list = 
			entityManager.createQuery("from TipoLetrado where materia = :materia and codigo = :codigo and descripcion = :descripcion and status <> -1")
			.setParameter("materia", materia)
			.setParameter("codigo", codigo)
			.setParameter("descripcion", descripcion)
			.getResultList();
			if (list.size() == 1) {
				return list.get(0);
			}
			if(list.size()>1){
				error("TipoLetrado '"+codigo+" / "+descripcion+"' ambiguo");
			}
		}
		if(codigo != null){
			list = 	entityManager.createQuery("from TipoLetrado where materia = :materia and codigo = :codigo and status <> -1")
			.setParameter("materia", materia)
			.setParameter("codigo", codigo)
			.getResultList();
			if (list.size() == 1) {
				return list.get(0);
			}
			if(list.size()>1){
				error("TipoLetrado codigo '"+codigo+"' ambiguo");
			}
		}
		if(descripcion != null){
			list = 	entityManager.createQuery("from TipoLetrado where materia = :materia and descripcion = :descripcion and status <> -1")
			.setParameter("materia", materia)
			.setParameter("descripcion", descripcion)
			.getResultList();
			if (list.size() == 1) {
				return list.get(0);
			}
			if(list.size()>1){
				error("TipoLetrado descripcion '"+descripcion+"' ambiguo");
			}
		}

		if(required){
			error("TipoLetrado '"+codigo+"' no encontrado");
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Rubro buscaRubro(Camara camara, Materia materia, String codigo, TipoInstancia tipoInstancia, boolean required) throws LoaderException {
		List<Rubro> list = 
			entityManager.createQuery("from Rubro where codigo = :codigo and materia = :materia and camara = :camara and status <> -1")
			.setParameter("codigo", codigo)
			.setParameter("materia", materia)
			.setParameter("camara", camara)
			.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("Rubro '"+codigo+"' no encontrado o ambiguo");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public TurnoFiscalia buscaTurnoMinisterio(Date fecha, int ministerio, String codigoOficina, boolean required, String tipoOficinaTurno) throws LoaderException {
		List<TurnoFiscalia> list = 
			entityManager.createQuery("from TurnoFiscalia where desdeFecha = :fecha and fiscalia = :fiscalia and oficina.codigo = :codigoOficina and status <> -1 and tipoOficinaTurno = :tipoOficinaTurno")
				.setParameter("fecha", fecha)
				.setParameter("fiscalia", ministerio)
				.setParameter("codigoOficina", codigoOficina)
				.setParameter("tipoOficinaTurno", tipoOficinaTurno)
				.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("Turno Ministerio '"+fecha+" - ministerio : "+ministerio+" - oficina -"+codigoOficina+"' no encontrada o ambigua");
		}
		return null;
	}

    @SuppressWarnings("unchecked")
	public Fiscalia buscaFiscalia(Integer fiscaliaCodigo, Camara camara, String tipoInstanciaAbreviatura, Competencia competencia, Date fechaInicio, String zonaOficina) {
    	String stmt = 
    		"FROM Fiscalia fiscalia WHERE " +
    	    "fiscalia.codigo = :codigo and fiscalia.camara = :camara and fechaInicio = :fechaInicio and tipoInstancia.abreviatura = :tipoInstanciaAbreviatura and status <> -1";
    	if (competencia != null) {
    		stmt += " and competencia = :competencia";
    	} else {
    		stmt += " and competencia is null";
    	}
    	if (!Strings.isEmpty(zonaOficina)) {
    		stmt += " and fiscalia.zonaOficina = :zonaOficina";
    	} else {
    		stmt += " and fiscalia.zonaOficina is null";
    	}
    	
		Query query = entityManager.createQuery(stmt);
		Fiscalia fiscalia = null;
		try {
			query.setParameter("codigo", fiscaliaCodigo);
			query.setParameter("camara", camara);
			query.setParameter("fechaInicio", fechaInicio);
			query.setParameter("tipoInstanciaAbreviatura", tipoInstanciaAbreviatura);
	    	if (competencia != null) {
	    		query.setParameter("competencia", competencia);
	    	}
	    	if (!Strings.isEmpty(zonaOficina)) {
	    		query.setParameter("zonaOficina", zonaOficina);
	    	} 
			List<Fiscalia> fiscalias = (List<Fiscalia>) query.getResultList();
			if(fiscalias.size() > 0){
				fiscalia = fiscalias.get(0);
			}
		} catch (Exception e) {
		}
		return fiscalia;
	}

    @SuppressWarnings("unchecked")
	public Defensoria buscaDefensoria(Integer defensoriaCodigo, Camara camara, String tipoInstanciaAbreviatura, Competencia competencia, Date fechaInicio, String zonaOficina) {
    	String stmt = 
    		"FROM Defensoria defensoria WHERE defensoria.codigo = :codigo and "+
    	    "defensoria.camara = :camara and "+
    	    "fechaInicio = :fechaInicio and tipoInstancia.abreviatura = :tipoInstanciaAbreviatura and status <> -1";
    	if (competencia != null) {
    		stmt += " and competencia = :competencia";
    	} else {
    		stmt += " and competencia is null";
    	}
    	if (!Strings.isEmpty(zonaOficina)) {
    		stmt += " and zonaOficina = :zonaOficina";
    	} else {
    		stmt += " and zonaOficina is null";
    	}
    	
    	
		Query query = entityManager.createQuery(stmt);
		Defensoria defensoria = null;
		try {
			query.setParameter("codigo", defensoriaCodigo);
			query.setParameter("camara", camara);
			query.setParameter("fechaInicio", fechaInicio);
			query.setParameter("tipoInstanciaAbreviatura", tipoInstanciaAbreviatura);
	    	if (competencia != null) {
	    		query.setParameter("competencia", competencia);
	    	}
	    	if (!Strings.isEmpty(zonaOficina)) {
	    		query.setParameter("zonaOficina", zonaOficina);
	    	}
			List<Defensoria> defensorias = (List<Defensoria>) query.getResultList();
			if(defensorias.size() > 0){
				defensoria = defensorias.get(0);
			}
		} catch (Exception e) {
		}
		return defensoria;
	}
    
    
    @SuppressWarnings("unchecked")
	public Asesoria buscaAsesoria(Integer asesoriaCodigo, Camara camara, String tipoInstanciaAbreviatura, Competencia competencia, Date fechaInicio, String zonaOficina) {
    	String stmt = 
    		"FROM Asesoria asesoria WHERE asesoria.codigo = :codigo and "+
    	    "asesoria.camara = :camara and "+
    	    "fechaInicio = :fechaInicio and tipoInstancia.abreviatura = :tipoInstanciaAbreviatura and status <> -1";
    	if (competencia != null) {
    		stmt += " and competencia = :competencia";
    	} else {
    		stmt += " and competencia is null";
    	}
    	if (!Strings.isEmpty(zonaOficina)) {
    		stmt += " and zonaOficina = :zonaOficina";
    	} else {
    		stmt += " and zonaOficina is null";
    	}
    	
    	
		Query query = entityManager.createQuery(stmt);
		Asesoria asesoria = null;
		try {
			query.setParameter("codigo", asesoriaCodigo);
			query.setParameter("camara", camara);
			query.setParameter("fechaInicio", fechaInicio);
			query.setParameter("tipoInstanciaAbreviatura", tipoInstanciaAbreviatura);
	    	if (competencia != null) {
	    		query.setParameter("competencia", competencia);
	    	}
	    	if (!Strings.isEmpty(zonaOficina)) {
	    		query.setParameter("zonaOficina", zonaOficina);
	    	}
			List<Asesoria> asesorias = (List<Asesoria>) query.getResultList();
			if(asesorias.size() > 0){
				asesoria = asesorias.get(0);
			}
		} catch (Exception e) {
		}
		return asesoria;
	}
    
    
    @SuppressWarnings("unchecked")
	public Sigla buscaSigla(String codigoSigla, Camara camara, String descripcion, String nombreAlternativo, String cuit) throws LoaderException {
		List<Sigla> list = null;
		if(codigoSigla != null && descripcion != null){
			list = 
			entityManager.createQuery("from Sigla where camara = :camara and codigo = :codigo and descripcion = :descripcion and status <> -1")
			.setParameter("camara", camara)
			.setParameter("codigo", codigoSigla)
			.setParameter("descripcion", descripcion)
			.getResultList();
			if (list.size() == 1) {
				return list.get(0);
			}
			if(list.size()>1){
				error("Sigla '"+codigoSigla+" / "+descripcion+"' ambigua");
			}
		}
		if(cuit != null){
			list = 	entityManager.createQuery("from Sigla where camara = :camara and cuit = :cuit and status <> -1")
			.setParameter("camara", camara)
			.setParameter("cuit", cuit)
			.getResultList();
			if (list.size() == 1) {
				return list.get(0);
			}
			if(list.size()>1){
				error("Sigla cuit '"+cuit+"' ambigua");
			}
		}
		if(codigoSigla != null){
			list = 	entityManager.createQuery("from Sigla where camara = :camara and codigo = :codigo and status <> -1")
			.setParameter("camara", camara)
			.setParameter("codigo", codigoSigla)
			.getResultList();
			if (list.size() == 1) {
				return list.get(0);
			}
			if(list.size()>1){
				error("Sigla codigoSigla '"+codigoSigla+"' ambigua");
			}
		}
		if(descripcion != null){
			list = 	entityManager.createQuery("from Sigla where camara = :camara and descripcion = :descripcion and status <> -1")
			.setParameter("camara", camara)
			.setParameter("descripcion", descripcion)
			.getResultList();
			if (list.size() == 1) {
				return list.get(0);
			}
			if(list.size()>1){
				error("Sigla descripcion '"+descripcion+"' ambigua");
			}
		}
		return null;
	}
    
    @SuppressWarnings("unchecked")
	public ConfiguracionMateria buscaConfiguracionMateria(Camara camara,	Materia materia, String parametro, boolean required) throws LoaderException {
		String stmt = "from ConfiguracionMateria where parametro = :parametro";
		stmt += camara != null ? " and camara = :camara" : " and camara is null";
		stmt += materia != null ? " and materia = :materia" : " and materia is null";
		Query query = entityManager.createQuery(stmt);
		query.setParameter("parametro", parametro);
		if(camara != null){
			query.setParameter("camara", camara);
		}
		if(materia != null){
			query.setParameter("materia", materia);
		}
		List<ConfiguracionMateria> list = query.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}

		if(required){
			error("Configuracion materia parametro '"+parametro+"' no encontrado o ambiguo");
		} else if(list.size() > 1){
			fatal("Configuracion materia parametro '"+parametro+"' ambiguo");
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public NumeradorExpediente buscaNumeradorExepediente(Camara camara,	int anio, boolean required) throws LoaderException {
		List<NumeradorExpediente> list = 
			entityManager.createQuery("from NumeradorExpediente where tipoNumerador=0 and camara = :camara and anio = :anio")
				.setParameter("camara", camara)
				.setParameter("anio", anio)
				.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("Numerador Expediente anio '"+anio+" - camara : "+camara.getCodigo()+"' no encontrado o ambiguo");
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Delito buscaDelito(String codigoDelito, Integer ley, boolean required) throws LoaderException {
		List<Delito> list = 
			entityManager.createQuery("from Delito where codigo = :codigo and ley = :ley and status = 0")
				.setParameter("codigo", codigoDelito)
				.setParameter("ley", ley)
				.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("Delito '"+codigoDelito+"ley "+ley+"' -  no encontrado o ambiguo");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public TipoLugarInternamiento buscaTipoLugarInternamiento(String codigoTipoLugar, boolean required) throws LoaderException {
		List<TipoLugarInternamiento> list = 
			entityManager.createQuery("from TipoLugarInternamiento where codigo = :codigo and status = 0")
				.setParameter("codigo", codigoTipoLugar)
				.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		if(required){
			error("Tipo Lugar Internamiento '"+codigoTipoLugar+"' -  no encontrado o ambiguo");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Provincia buscaProvinciaPorDescripcion(String descripcion, boolean required)  throws LoaderException {
		List<Provincia> list =
			entityManager.createQuery("from Provincia where descripcion = :descripcion and status <> -1")
				.setParameter("descripcion", descripcion)
				.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}else{
			if(required){
				error("Provincia '"+descripcion+"' no encontrada o ambigua");
			}
		}
		return null;
	}

	static public Parametro buscaParametro(EntityManager entityManager, Camara camara, String parametroName) {
		Parametro parametro = null;
		
		if (parametroName != null) {
			String BY_CAMARA_Y_NAME_STMT =	"FROM Parametro parametro WHERE parametro.camara = :camara and parametro.name = :name and status <> -1";
			String BY_NAME_STMT = "FROM Parametro parametro WHERE parametro.name = :name and status <> -1";

			List<Parametro> parametroList = null;
			Query query;
			if (camara != null) {
				query = entityManager.createQuery(BY_CAMARA_Y_NAME_STMT);
				query.setParameter("camara", camara);
			} else {
				query = entityManager.createQuery(BY_NAME_STMT);
			}
			query.setParameter("name", parametroName);
			parametroList = query.getResultList();
			if (parametroList.size() > 0) {
				parametro = parametroList.get(0);
			}
		}
		return parametro;
	}

	@SuppressWarnings("unchecked")
	protected MotivoPase buscaMotivoPase(String codigoMotivoPase, Camara camara,
			boolean required) throws LoaderException {
		List<MotivoPase> list =
			getEntityManager().createQuery("from MotivoPase where camara = :camara and codigo = :codigo and status <> -1")
				.setParameter("codigo", codigoMotivoPase)
				.setParameter("camara", camara)
				.getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}else if (list.size() > 1) {
			error("Motivo Pase '"+codigoMotivoPase+"' ambiguo");
		} else if(required){
				error("Motivo Pase '"+codigoMotivoPase+"' no encontrado");
		}
		return null;
	}

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	public int getCurrentRow() {
		return currentRow;
	}

	public static String normalizeId(String id, boolean startUpperCase, boolean keepCase, List<String> forbidenIds) {
		if (id == null) {
			return id;
		}
		id = id.trim();
		id = id.replaceAll(":", "");
		id = replaceSpecialChars(id);
		id = validateForbidenId(id, forbidenIds);
		StringBuilder stringBuilder = new StringBuilder();
		StringTokenizer stringTokenizer = new StringTokenizer(id, SPACE_DELIMITERS);
		if(!keepCase && !id.equals(id.toUpperCase()) && !id.equals(id.toLowerCase())){
			keepCase = true;
		}
		while (stringTokenizer.hasMoreTokens()) {
			String fragment = stringTokenizer.nextToken();
			String newFragment;
			newFragment = normalizeJavaFragment(fragment, startUpperCase, keepCase);
			startUpperCase = true;
			stringBuilder.append(newFragment);
		}
		return stringBuilder.toString();
	}
	
	private static String replaceSpecialChars(String string) {
		StringBuilder builder = new StringBuilder(string);
		for(int i=0; i<builder.length(); i++) {
			char c = builder.charAt(i);
			if ("áàä".indexOf(c) != -1) {
				builder.setCharAt(i, 'a');
			} else if ("ÁÀÄ".indexOf(c) != -1) {
				builder.setCharAt(i, 'A');
			} else if ("éèë".indexOf(c) != -1) {
				builder.setCharAt(i, 'e');
			} else if ("ÉÈË".indexOf(c) != -1) {
				builder.setCharAt(i, 'E');
			} else if ("íìï".indexOf(c) != -1) {
				builder.setCharAt(i, 'i');
			} else if ("ÍÌÏ".indexOf(c) != -1) {
				builder.setCharAt(i, 'I');
			} else if ("óòö".indexOf(c) != -1) {
				builder.setCharAt(i, 'o');
			} else if ("ÓÒÖ".indexOf(c) != -1) {
				builder.setCharAt(i, 'O');
			} else if ("úùü".indexOf(c) != -1) {
				builder.setCharAt(i, 'u');
			} else if ("ÚÙÜ".indexOf(c) != -1) {
				builder.setCharAt(i, 'U');
//			} else if ("ñ".indexOf(c) != -1) {
//				builder.setCharAt(i, 'n');
//			} else if ("Ñ".indexOf(c) != -1) {
//				builder.setCharAt(i, 'N');
			}
		}
		return builder.toString();
	}

	private static String normalizeJavaFragment(String fragment,
			boolean startUpperCase, boolean keepCase) {
		StringBuffer buffer = new StringBuffer(fragment);
		boolean toUpper = startUpperCase;
		boolean force = true;
		for (int i = 0; i < buffer.length(); i++) {
			char ch = buffer.charAt(i);
			if ( SPACE_DELIMITERS.indexOf(ch) != -1) {
				buffer.deleteCharAt(i);
				i--;
				toUpper = true;
				force = true;
			} else {
				if (force || !keepCase ) {
					buffer.setCharAt(i, toUpper ? Character.toUpperCase(ch)
							: Character.toLowerCase(ch));
				} 
				toUpper = false;
				force = false;
			}
		}
		return buffer.toString();
	}

	private static String validateForbidenId(String id, List<String> forbidenIds) {
		if(forbidenIds != null && forbidenIds.contains(id.toLowerCase())) {
			return "x"+id;
		} else {
			return id;
		}
	}

	public String getForzeCamara() {
		return forzeCamara;
	}

	public void setForzeCamara(String forzeCamara) {
		this.forzeCamara = forzeCamara;
	}

	
}
