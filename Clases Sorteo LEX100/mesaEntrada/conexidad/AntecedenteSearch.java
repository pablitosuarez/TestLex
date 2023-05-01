package com.base100.lex100.mesaEntrada.conexidad;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.base100.expand.seam.framework.entity.AbstractEntitySearch;
import com.base100.lex100.component.enumeration.NaturalezaSubexpedienteEnumeration;
import com.base100.lex100.component.enumeration.TipoComparacionEnumeration;
import com.base100.lex100.component.estadoexpediente.EstadoExpedientePredefinedEnumeration;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.CondicionCnx;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.TipoCausa;



/**
 * Class for ExpedienteIngreso search filters.
 * 
 * Generated by Expand.
 *
 */
@Name("antecedenteSearch")
@BypassInterceptors
public class AntecedenteSearch extends AbstractEntitySearch<Expediente>
{
	private Camara camara;
	private Camara camaraOriginal;
	private Camara camaraRadicacion;
	private ObjetoJuicio objetoJuicio;	// filtro por Objeto de Juicio
	private List<ObjetoJuicio> objetosJuicioIncluidos;
	private List<ObjetoJuicio> objetosJuicioExcluidos;
	private List<String> codigoObjetosJuicioIncluidos;
	private List<String> codigoObjetosJuicioExcluidos;
	private String naturalezaSubexpediente;
	private List<String> naturalezaSubexpedienteList;
	private List<String> cuitParte1List;
	private List<String> cuitParte2List;
	private List<String> cuitActorList;
	private List<String> cuitDemandadoList;
	private List<String> dniParte1List;
	private List<String> dniParte2List;
	private List<String> dniActorList;
	private List<String> dniDemandadoList;
	private List<String> nombreParte1List;
	private List<String> nombreParte2List;
	private List<String> nombreActorList;
	private List<String> nombreDemandadoList;
	private String materiaGrupo;
	private Map<String, String> parametro = new HashMap<String, String>();

	private String dependenciaOrigen;
	private String codigoRelacionado;
	private TipoCausa tipoCausa;
	private List<String> codigoTiposCausaIncluidos;
	
	private List<String> codigoTemasIncluidos;
	private List<Integer> idTemasIncluidos;

	private List<Integer> estadosValidosIds = new ArrayList<Integer>();
	private List<Integer> estadosNoValidosIds = new ArrayList<Integer>();

	private Boolean voluntario;

	private Integer fromAnioExpediente;
	
	private Date fromFechaIngreso;
	private Date toFechaIngreso;
	
	private Expediente expedienteOrigen;
	private Expediente expedienteBase;
	// Objeto de Juicio principal del expediente sobre el que se buscan antecedentes.
	// Se utiliza antecenteList para la generacion de los filtros por par�metros
	private ObjetoJuicio objetoJuicioPrincipal;	
	private String tipoComparacionPartes = null;
	private Integer tipoFiltroPartes = null;

	private Boolean soloIniciados;
	private Boolean soloEnTramite;
	private String conTipoRadicacion;
	
	private boolean excluirPersonasJuridicas;
	
	private AntecedenteSearch filter;

	public AntecedenteSearch() {
		setDefaultOrder("expediente.expedienteIngreso.fechaIngreso");
	}

	public void init() {
		filter = null;
		clear();
		naturalezaSubexpediente = NaturalezaSubexpedienteEnumeration.Type.principal.getValue().toString();
	}
	@Override
	public void clear() {
		this.camara = null;
		this.camaraOriginal = null;
		this.camaraRadicacion = null;
		this.objetoJuicio = null;
		this.objetosJuicioIncluidos = null;
		this.objetosJuicioExcluidos = null;
		this.codigoObjetosJuicioIncluidos = null;
		this.codigoObjetosJuicioExcluidos = null;
		this.codigoTiposCausaIncluidos = null;
		this.codigoTemasIncluidos = null;
		this.idTemasIncluidos = null;
		this.naturalezaSubexpediente = null;
		this.naturalezaSubexpedienteList = null;
		this.fromFechaIngreso = null;
		this.toFechaIngreso = null;
		this.cuitParte1List = null;
		this.cuitParte2List = null;
		this.cuitActorList = null;
		this.cuitDemandadoList = null;
		this.dniParte1List = null;
		this.dniParte2List = null;
		this.dniActorList = null;
		this.dniDemandadoList = null;
		this.nombreParte1List = null;
		this.nombreParte2List = null;
		this.nombreActorList = null;
		this.nombreDemandadoList = null;
		this.dependenciaOrigen = null;
		this.codigoRelacionado = null;
		this.tipoCausa = null;
		this.expedienteOrigen = null;
		this.expedienteBase = null;
		this.materiaGrupo = null;
		this.voluntario = null;
		this.fromAnioExpediente = null;
		
		this.tipoComparacionPartes = null;

		this.soloEnTramite = null;
		this.conTipoRadicacion= null;
		
		this.excluirPersonasJuridicas = false;
		
		this.parametro.clear();
	}

	@Override
	public String getInitialPanelState()  {
		if (true
			&& this.camara == null
			&& this.camaraOriginal == null
			&& this.camaraRadicacion == null
			&& this.objetoJuicio == null
			&& this.objetosJuicioIncluidos == null
			&& this.objetosJuicioExcluidos == null
			&& this.codigoObjetosJuicioIncluidos == null
			&& this.codigoObjetosJuicioExcluidos == null
			&& this.naturalezaSubexpediente == null
			&& this.codigoTiposCausaIncluidos == null
			&& this.codigoTemasIncluidos == null
			&& this.idTemasIncluidos == null
			&& this.naturalezaSubexpedienteList == null
			&& this.fromFechaIngreso == null
			&& this.toFechaIngreso == null
			&& this.cuitParte1List == null
			&& this.cuitParte2List == null
			&& this.cuitActorList == null
			&& this.cuitDemandadoList == null
			&& this.dniParte1List == null
			&& this.dniParte2List == null
			&& this.dniActorList == null
			&& this.dniDemandadoList == null
			&& this.nombreParte1List == null
			&& this.nombreParte2List == null
			&& this.nombreActorList == null
			&& this.nombreDemandadoList == null
			&& this.dependenciaOrigen == null
			&& this.codigoRelacionado == null
			&& this.tipoCausa == null
			&& this.materiaGrupo == null
			&& this.voluntario == null
			&& this.fromAnioExpediente == null
			&& this.soloEnTramite == null
			&& this.conTipoRadicacion == null
			&& this.parametro.isEmpty()

	    ){
			return getPanelState();
		} else {
			return "open";
		}
	}

	public boolean isFiltered()  {
		return !(
				parametro.isEmpty() &&
				isEmpty(this.cuitParte1List) &&
				isEmpty(this.cuitParte2List) &&
				isEmpty(this.cuitActorList) &&
				isEmpty(this.cuitDemandadoList) &&
				isEmpty(this.dniParte1List) &&
				isEmpty(this.dniParte2List) &&
				isEmpty(this.dniActorList) &&
				isEmpty(this.dniDemandadoList) &&
				isEmpty(this.nombreParte1List) &&
				isEmpty(this.nombreParte2List) &&
				isEmpty(this.nombreActorList) &&
				isEmpty(this.nombreDemandadoList) &&
				this.dependenciaOrigen == null &&
				this.codigoRelacionado == null &&
				this.tipoCausa == null &&
				this.tipoComparacionPartes == null
				);
	}
	
	private boolean isEmpty(List<String> list) {
		return (list == null) || (list.size() == 0);
	}
	
	@Override
	public boolean hasActiveSearch() {
		// TODO Auto-generated method stub
		return false;
	}

	public ObjetoJuicio getObjetoJuicio() {
		return objetoJuicio;
	}

	public void setObjetoJuicio(ObjetoJuicio objetoJuicio) {
		this.objetoJuicio = objetoJuicio;
	}

	public List<ObjetoJuicio> getObjetosJuicioIncluidos() {
		return objetosJuicioIncluidos;
	}

	public void setObjetosJuicioIncluidos(List<ObjetoJuicio> objetosJuicioIncluidos) {
		this.objetosJuicioIncluidos = objetosJuicioIncluidos;
	}

	public List<ObjetoJuicio> getObjetosJuicioExcluidos() {
		return objetosJuicioExcluidos;
	}

	public void setObjetosJuicioExcluidos(List<ObjetoJuicio> objetosJuicioExcluidos) {
		this.objetosJuicioExcluidos = objetosJuicioExcluidos;
	}

	public String getNaturalezaSubexpediente() {
		return naturalezaSubexpediente;
	}

	public void setNaturalezaSubexpediente(String naturalezaSubexpediente) {
		this.naturalezaSubexpediente = naturalezaSubexpediente;
	}

	public List<String> getNaturalezaSubexpedienteList() {
		return naturalezaSubexpedienteList;
	}

	public void setNaturalezaSubexpedienteList(
			List<String> naturalezaSubexpedienteList) {
		this.naturalezaSubexpedienteList = naturalezaSubexpedienteList;
	}

    /**
     * Returns the fromFechaIngreso search filter
     * 
     * @return	the fromFechaIngreso search filter
     */
    public Date getFromFechaIngreso() {
    	return fromFechaIngreso;
    }

    /**
     * Sets the fromFechaIngreso search filter
     * 
     * @param fromFechaIngreso the fromFechaIngreso search filter
     */
    public void setFromFechaIngreso(Date fromFechaIngreso) {
    	this.fromFechaIngreso = fromFechaIngreso;
    }
    
   /**
     * Clears the fromFechaIngreso search filter
     */
    public void clearFromFechaIngreso() {
    	setFromFechaIngreso(null);
    }

    /**
     * Returns the toFechaIngreso search filter
     * 
     * @return	the toFechaIngreso search filter
     */
    public Date getToFechaIngreso() {
    	return toFechaIngreso;
    }

    /**
     * Sets the toFechaIngreso search filter
     * 
     * @param toFechaIngreso the toFechaIngreso search filter
     */
    public void setToFechaIngreso(Date toFechaIngreso) {
    	this.toFechaIngreso = toFechaIngreso;
    }
    
   /**
     * Clears the toFechaIngreso search filter
     */
    public void clearToFechaIngreso() {
    	setToFechaIngreso(null);
    }

	public List<String> getCuitParte1List() {
		return cuitParte1List;
	}

	public void setCuitParte1List(List<String> cuitParte1List) {
		this.cuitParte1List = cuitParte1List;
	}

	public List<String> getCuitParte2List() {
		return cuitParte2List;
	}

	public void setCuitParte2List(List<String> cuitParte2List) {
		this.cuitParte2List = cuitParte2List;
	}

	public List<String> getCuitActorList() {
		return cuitActorList;
	}

	public void setCuitActorList(List<String> actorCuitList) {
		this.cuitActorList = actorCuitList;
	}

	public List<String> getCuitDemandadoList() {
		return cuitDemandadoList;
	}

	public void setCuitDemandadoList(List<String> demandadoCuitList) {
		this.cuitDemandadoList = demandadoCuitList;
	}

	public AntecedenteSearch getFilter() {
		return this;
	}

    @Override
    public void updateFilters() {
	}

	public Camara getCamara() {
		return camara;
	}

	public void setCamara(Camara camara) {
		this.camara = camara;
	}

	public Camara getCamaraOriginal() {
		return camaraOriginal;
	}

	public void setCamaraOriginal(Camara camaraOriginal) {
		this.camaraOriginal = camaraOriginal;
	}

	public Camara getCamaraRadicacion() {
		return camaraRadicacion;
	}

	public void setCamaraRadicacion(Camara camaraRadicacion) {
		this.camaraRadicacion = camaraRadicacion;
	}

	public Map<String, String> getParametro() {
		return parametro;
	}

	public void setParametro(Map<String, String> parametro) {
		this.parametro = parametro;
	}

	public List<String> getCodigoObjetosJuicioIncluidos() {
		return codigoObjetosJuicioIncluidos;
	}

	public void setCodigoObjetosJuicioIncluidos(
			List<String> codigoObjetosJuicioIncluidos) {
		this.codigoObjetosJuicioIncluidos = codigoObjetosJuicioIncluidos;
	}

	public List<String> getCodigoObjetosJuicioExcluidos() {
		return codigoObjetosJuicioExcluidos;
	}

	public void setCodigoObjetosJuicioExcluidos(
			List<String> codigoObjetosJuicioExcluidos) {
		this.codigoObjetosJuicioExcluidos = codigoObjetosJuicioExcluidos;
	}

	public void setValorParametro(String nombreParametro, String value) {
		parametro.put(nombreParametro, value);
	}

	public List<String> getDniParte1List() {
		return dniParte1List;
	}

	public void setDniParte1List(List<String> dniParte1List) {
		this.dniParte1List = dniParte1List;
	}

	public List<String> getDniParte2List() {
		return dniParte2List;
	}

	public void setDniParte2List(List<String> dniParte2List) {
		this.dniParte2List = dniParte2List;
	}

	public List<String> getDniActorList() {
		return dniActorList;
	}

	public void setDniActorList(List<String> dniActorList) {
		this.dniActorList = dniActorList;
	}

	public List<String> getDniDemandadoList() {
		return dniDemandadoList;
	}

	public void setDniDemandadoList(List<String> dniDemandadoList) {
		this.dniDemandadoList = dniDemandadoList;
	}

	public List<String> getNombreParte1List() {
		return nombreParte1List;
	}

	public void setNombreParte1List(List<String> nombreParte1List) {
		this.nombreParte1List = nombreParte1List;
	}

	public List<String> getNombreParte2List() {
		return nombreParte2List;
	}

	public void setNombreParte2List(List<String> nombreParte2List) {
		this.nombreParte2List = nombreParte2List;
	}

	public List<String> getNombreActorList() {
		return nombreActorList;
	}

	public void setNombreActorList(List<String> nombreActorList) {
		this.nombreActorList = nombreActorList;
	}

	public List<String> getNombreDemandadoList() {
		return nombreDemandadoList;
	}

	public void setNombreDemandadoList(List<String> nombreDemandadoList) {
		this.nombreDemandadoList = nombreDemandadoList;
	}

	public Expediente getExpedienteOrigen() {
		return expedienteOrigen;
	}

	public void setExpedienteOrigen(Expediente expedienteOrigen) {
		this.expedienteOrigen = expedienteOrigen;
	}

	public void setExpedienteBase(Expediente expedienteBase) {
		this.expedienteBase = expedienteBase;
	}

	public Expediente getExpedienteBase() {
		return expedienteBase;
	}

	public ObjetoJuicio getObjetoJuicioPrincipal() {
		return objetoJuicioPrincipal;
	}

	public void setObjetoJuicioPrincipal(ObjetoJuicio objetoJuicioPrincipal) {
		this.objetoJuicioPrincipal = objetoJuicioPrincipal;
	}

	public String getTipoComparacionPartes() {
		return tipoComparacionPartes;
	}

	public void setTipoComparacionPartes(
			String tipoComparacionPartes) {
		this.tipoComparacionPartes = tipoComparacionPartes;
	}

	public Integer getTipoFiltroPartes() {
		return tipoFiltroPartes;
	}

	public void setTipoFiltroPartes(Integer tipoFiltroPartes) {
		this.tipoFiltroPartes = tipoFiltroPartes;
	}

	public String getDependenciaOrigen() {
		return dependenciaOrigen;
	}

	public void setDependenciaOrigen(String dependenciaOrigen) {
		this.dependenciaOrigen = dependenciaOrigen;
	}

	public String getCodigoRelacionado() {
		return codigoRelacionado;
	}

	public void setCodigoRelacionado(String codigoRelacionado) {
		this.codigoRelacionado = codigoRelacionado;
	}

	public TipoCausa getTipoCausa() {
		return tipoCausa;
	}

	public void setTipoCausa(TipoCausa tipoCausa) {
		this.tipoCausa = tipoCausa;
	}

	public Boolean getSoloIniciados() {
		return soloIniciados;
	}

	public void setSoloIniciados(Boolean soloIniciados) {
		this.soloIniciados = soloIniciados;
	}
	
	public String getEstadoNoValido(){
		if(Boolean.TRUE.equals(soloIniciados)){
			return EstadoExpedientePredefinedEnumeration.Type.$SI.getValue().toString();
		}
		return null;
	}

	public String getEstadoValido(){
		if(Boolean.FALSE.equals(soloIniciados)){
			return EstadoExpedientePredefinedEnumeration.Type.$SI.getValue().toString();
		}
		return null;
	}

	public List<Integer> getEstadosNoValidosIds(){
		return estadosNoValidosIds;
	}

	public void setEstadosNoValidosIds(List<Integer> estadosNoValidosIds) {
		this.estadosNoValidosIds = estadosNoValidosIds;
	}

	public List<Integer> getEstadosValidosIds(){
		return estadosValidosIds;
	}

	public void setEstadosValidosIds(List<Integer> estadosValidosIds) {
		this.estadosValidosIds = estadosValidosIds;
	}

	public String getMateriaGrupo() {
		return materiaGrupo;
	}

	public void setMateriaGrupo(String materiaGrupo) {
		this.materiaGrupo = materiaGrupo;
	}

	public Boolean getSoloEnTramite() {
		return soloEnTramite;
	}

	public void setSoloEnTramite(Boolean soloEnTramite) {
		this.soloEnTramite = soloEnTramite;
	}

	public List<String> getCodigoTiposCausaIncluidos() {
		return codigoTiposCausaIncluidos;
	}

	public void setCodigoTiposCausaIncluidos(List<String> codigoTiposCausaIncluidos) {
		this.codigoTiposCausaIncluidos = codigoTiposCausaIncluidos;
	}

	public List<String> getCodigoTemasIncluidos() {
		return codigoTemasIncluidos;
	}

	public void setCodigoTemasIncluidos(List<String> codigoTemasIncluidos) {
		this.codigoTemasIncluidos = codigoTemasIncluidos;
	}

	public List<Integer> getIdTemasIncluidos() {
		return idTemasIncluidos;
	}

	public void setIdTemasIncluidos(List<Integer> idTemasIncluidos) {
		this.idTemasIncluidos = idTemasIncluidos;
	}

	public Boolean getVoluntario() {
		return voluntario;
	}

	public void setVoluntario(Boolean voluntario) {
		this.voluntario = voluntario;
	}

	public Integer getFromAnioExpediente() {
		return fromAnioExpediente;
	}

	public void setFromAnioExpediente(Integer fromAnioExpediente) {
		this.fromAnioExpediente = fromAnioExpediente;
	}

	public String getConTipoRadicacion() {
		return conTipoRadicacion;
	}

	public void setConTipoRadicacion(String conTipoRadicacion) {
		this.conTipoRadicacion = conTipoRadicacion;
	}

	public boolean isExcluirPersonasJuridicas() {
		return excluirPersonasJuridicas;
	}

	public void setExcluirPersonasJuridicas(boolean excluirPersonasJuridicas) {
		this.excluirPersonasJuridicas = excluirPersonasJuridicas;
	}


}
