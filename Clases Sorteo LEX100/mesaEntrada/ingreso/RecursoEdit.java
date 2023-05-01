package com.base100.lex100.mesaEntrada.ingreso;

import java.util.Date;
import java.util.List;

import com.base100.lex100.component.currentdate.CurrentDateManager;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.RecursoExp;
import com.base100.lex100.entity.TipoRecurso;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.mesaEntrada.asignacion.ExpedienteMeAsignacion;

public class RecursoEdit {
    // seleccion de recurso de una lista
    private RecursoExp recursoSorteo;
    private List<RecursoExp> listaRecursosASeleccionar;
    
    private List<RecursoExp> listaRecursosParaCirculacionExpediente;

	
	
	private TipoRecurso tipoRecurso;
	private boolean tipoRecursoDetenidos;
	private boolean tipoRecursoLey2372;
	private boolean tipoRecursoLey23984;
	
	private Integer regimenProcesal = 0;
	private String observaciones;
	private String resolucionApelacion;
    private String tipoResolucionApelada;
    private String tipoSentencia;

	private Date fechaPresentacion;
	
	private boolean detenidos;
	private boolean urgente;
	
	
    private Integer fojas;
    private Integer paquetes;
    private Integer cuerpos;
    private Integer agregados;
    private String comentarios;

	
	public RecursoEdit() {
		this.fechaPresentacion =  CurrentDateManager.instance().getCurrentDate();
		this.regimenProcesal = 0;
	}

	public TipoRecurso getTipoRecurso() {
		return tipoRecurso;
	}

	public void setTipoRecurso(TipoRecurso tipoRecurso) {
		this.tipoRecurso = tipoRecurso;
	}

	public boolean isTipoRecursoDetenidos() {
		return tipoRecursoDetenidos;
	}

	public void setTipoRecursoDetenidos(boolean tipoRecursoDetenidos) {
		this.tipoRecursoDetenidos = tipoRecursoDetenidos;
	}

	public boolean isTipoRecursoLey2372() {
		return tipoRecursoLey2372;
	}

	public void setTipoRecursoLey2372(boolean tipoRecursoLey2372) {
		this.tipoRecursoLey2372 = tipoRecursoLey2372;
	}

	public boolean isTipoRecursoLey23984() {
		return tipoRecursoLey23984;
	}

	public void setTipoRecursoLey23984(boolean tipoRecursoLey23984) {
		this.tipoRecursoLey23984 = tipoRecursoLey23984;
	}

	public Date getFechaPresentacion() {
		return fechaPresentacion;
	}

	public void setFechaPresentacion(Date fechaPresentacion) {
		this.fechaPresentacion = fechaPresentacion;
	}

	public Integer getRegimenProcesal() {
		return regimenProcesal;
	}

	public void setRegimenProcesal(Integer regimenProcesal) {
		this.regimenProcesal = regimenProcesal;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public boolean isDetenidos() {
		return detenidos;
	}

	public void setDetenidos(boolean detenidos) {
		this.detenidos = detenidos;
	}

	public Integer getFojas() {
		return fojas;
	}

	public void setFojas(Integer fojas) {
		this.fojas = fojas;
	}

	public Integer getPaquetes() {
		return paquetes;
	}

	public void setPaquetes(Integer paquetes) {
		this.paquetes = paquetes;
	}

	public Integer getCuerpos() {
		return cuerpos;
	}

	public void setCuerpos(Integer cuerpos) {
		this.cuerpos = cuerpos;
	}

	public Integer getAgregados() {
		return agregados;
	}

	public void setAgregados(Integer agregados) {
		this.agregados = agregados;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}


	public void resetFojasInfo(){
		setFojas(null);
		setCuerpos(null);
		setAgregados(null);
		setPaquetes(null);
		setComentarios(null);
	}
	

	public void copyFojasToRecurso(RecursoExp recursoExp) {
		if(recursoExp != null){
			recursoExp.setFojas(getFojas());
			recursoExp.setCuerpos(getCuerpos());
			recursoExp.setAgregados(getAgregados());
			recursoExp.setPaquetes(getPaquetes());
			recursoExp.setComentarios(getComentarios());			
		}
	}
	
	public void copyFojasInfoFromRecurso(RecursoExp recursoExp) {
		if(recursoExp == null){
			resetFojasInfo();
		}else{
			setFojas(recursoExp.getFojas());
			setCuerpos(recursoExp.getCuerpos());
			setAgregados(recursoExp.getAgregados());
			setPaquetes(recursoExp.getPaquetes());
			setComentarios(recursoExp.getComentarios());
		}
	}

	private void resetRecursosASortear(){
		listaRecursosASeleccionar = null;
		listaRecursosParaCirculacionExpediente = null;
		setRecursoSorteo(null);
	}

	public void recalculateExpedienteSala(Expediente expediente, Boolean reasignacionMode){
			reset();
			getListaRecursosSalaASeleccionar(expediente, reasignacionMode);
	}
	
	public void recalculateExpedienteCasacion(Expediente expediente, boolean reasignacionMode){
		reset();
		getListaRecursosCasacionASeleccionar(expediente, reasignacionMode);
	}

	public void recalculateExpedienteCorte(Expediente expediente, boolean reasignacionMode){
		reset();
		getListaRecursosCorteASeleccionar(expediente, reasignacionMode);
	}
	
	public List<RecursoExp> getListaRecursosParaCirculacionExpediente(Expediente expediente){
		if(listaRecursosParaCirculacionExpediente == null){
			listaRecursosParaCirculacionExpediente = ExpedienteManager.instance().computeRecursosParaCirculacionExpediente(expediente);
		}
		return listaRecursosParaCirculacionExpediente;
	}
	
	public List<RecursoExp> getListaRecursosSalaASeleccionar(Expediente expediente, Boolean sorteados){
		if(expediente == null){
			resetRecursosASortear();
		}else if(listaRecursosASeleccionar == null){
			if(Boolean.TRUE.equals(sorteados)){
				listaRecursosASeleccionar = ExpedienteManager.instance().computeRecursosSalaAsignadosPteResolver(expediente);
			}else if (Boolean.FALSE.equals(sorteados)){
				listaRecursosASeleccionar = ExpedienteManager.instance().computeRecursosSalaPteAsignacion(expediente);
			} else {	// sorteados y no sorteados
				listaRecursosASeleccionar = ExpedienteManager.instance().computeRecursosSalaPteResolver(expediente);
			}
			if(listaRecursosASeleccionar.size() > 0){
				setRecursoSorteo(ExpedienteManager.instance().computeUltimoRecurso(listaRecursosASeleccionar));
			}
		}
		return listaRecursosASeleccionar;
	}

	public List<RecursoExp> getListaRecursosCasacionASeleccionar(Expediente expediente, boolean sorteados){
		if(expediente == null){
			resetRecursosASortear();
		}else if(listaRecursosASeleccionar == null){
			if(Boolean.TRUE.equals(sorteados)){
				listaRecursosASeleccionar = ExpedienteManager.instance().computeRecursosCasacionAsignadosPteResolver(expediente);
			}else if (Boolean.FALSE.equals(sorteados)){
				listaRecursosASeleccionar = ExpedienteManager.instance().computeRecursosCasacionPteAsignacion(expediente);
			} else {	// sorteados y no sorteados
				listaRecursosASeleccionar = ExpedienteManager.instance().computeRecursosCasacionPteResolver(expediente);
			}
			if(listaRecursosASeleccionar.size() > 0){
				setRecursoSorteo(ExpedienteManager.instance().computeUltimoRecurso(listaRecursosASeleccionar));
				ExpedienteMeAsignacion.instance().initRecursoInfo();
			}
		}
		return listaRecursosASeleccionar;
	}

	public List<RecursoExp> getListaRecursosCorteASeleccionar(Expediente expediente, boolean sorteados){
		if(expediente == null){
			resetRecursosASortear();
		}else if(listaRecursosASeleccionar == null){
			if(Boolean.TRUE.equals(sorteados)){
				listaRecursosASeleccionar = ExpedienteManager.instance().computeRecursosCorteAsignadosPteResolver(expediente);
			}else if (Boolean.FALSE.equals(sorteados)){
				listaRecursosASeleccionar = ExpedienteManager.instance().computeRecursosCortePteAsignacion(expediente);
			} else {	// sorteados y no sorteados
				listaRecursosASeleccionar = ExpedienteManager.instance().computeRecursosCortePteResolver(expediente);
			}
			if(listaRecursosASeleccionar.size() > 0){
				setRecursoSorteo(ExpedienteManager.instance().computeUltimoRecurso(listaRecursosASeleccionar));
			}
		}
		return listaRecursosASeleccionar;
	}
	
	public RecursoExp getRecursoSorteo() {
		return recursoSorteo;
	}

	public void setRecursoSorteo(RecursoExp recursoSorteo) {
		if(this.recursoSorteo != recursoSorteo){
			copyFojasInfoFromRecurso(recursoSorteo);
		}
		this.recursoSorteo = recursoSorteo;
		
	}

	public void reset() {
		 resetRecursosASortear();		
	}

	public String getResolucionApelacion() {
		return resolucionApelacion;
	}

	public void setResolucionApelacion(String resolucionApelacion) {
		this.resolucionApelacion = resolucionApelacion;
	}

	public String getTipoResolucionApelada() {
		return tipoResolucionApelada;
	}

	public void setTipoResolucionApelada(String tipoResolucionApelada) {
		this.tipoResolucionApelada = tipoResolucionApelada;
	}

	public String getTipoSentencia() {
		return tipoSentencia;
	}

	public void setTipoSentencia(String tipoSentencia) {
		this.tipoSentencia = tipoSentencia;
	}

	public boolean isUrgente() {
		return urgente;
	}

	public void setUrgente(boolean urgente) {
		this.urgente = urgente;
	}



}
