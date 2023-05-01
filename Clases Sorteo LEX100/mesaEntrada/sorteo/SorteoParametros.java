package com.base100.lex100.mesaEntrada.sorteo;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.base100.lex100.component.enumeration.TipoGiroEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.RecursoExp;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.TipoBolillero;
import com.base100.lex100.mesaEntrada.ingreso.FojasEdit;

public class SorteoParametros {
	private SorteoOperation sorteoOperation;
	private Camara camara;
	private Sorteo sorteo;
	private TipoBolillero tipoBolillero;
	private TipoRadicacionEnumeration.Type tipoRadicacion;
	private TipoGiroEnumeration.Type tipoGiro;
	private Oficina oficinaSorteo;
	private String codigoTipoCambioAsignacion;
	private String descripcionCambioAsignacion;
	private String comentarios;
	private Expediente expediente;
	private Materia materia;

	private String rubro;
	private String rubroAnterior;
	private String rubroAsignacion;
	private Boolean compensacionAnterior;
	private List<Integer> excludeOficinasIds;	
	private List<Integer> onlyOficinasIds;	
	private Oficina oficinaAsignada;
	private Oficina oficinaDesasignada;
	private String usuario;
	private Date fechaTurnosActivos;
	private boolean sorteaMediador;
	private boolean sorteoSecudario;

	private Sorteo sorteoDependiente;

	private boolean sortea = true;
	private boolean buscaConexidad;
	private boolean asignaPorConexidad;
	private boolean ignoreCheckDobleInicio;
	
	private boolean descompensarAnterior;
	
	private boolean remisionPorUsuario;
	private FojasEdit fojasPase;
	
	private boolean verificarAsignarOrdenCirculacion;

	/** ATOS COMERCIAL */
	private Integer idCausa = null;
	/** ATOS OVD */
	private Integer anioOVD;
	private Integer legajoOVD;
	private String caratulaOVD;
	/** ATOS OVD */
	public void setIdCausa(Integer idCausa) {
		this.idCausa = idCausa;
	}

	public Integer getIdCausa() {
		return idCausa;
	}
	/** ATOS COMERCIAL */
	
	private RecursoExp recursoExp;
	
	private SorteoParametros siguienteSorteoEncadenado;
	
	public SorteoParametros(SorteoOperation sorteoOperation, 
			Camara camara, 
			Materia materia, 
			Sorteo sorteo,
			TipoBolillero tipoBolillero,
			String rubro,
			TipoRadicacionEnumeration.Type tipoRadicacion, 
			TipoGiroEnumeration.Type tipoGiro, 
			Oficina oficinaSorteo, 
			String codigoTipoCambioAsignacion, 
			String descripcionCambioAsignacion, 
			String comentarios, 
			Date fechaTurnosActivos,
			boolean descompensarAnterior,
			final Expediente expediente) {
		this.sorteoOperation = sorteoOperation;
		this.camara = camara;
		this.sorteo = sorteo;
		this.tipoBolillero = tipoBolillero;
		this.rubro = rubro;
		this.tipoRadicacion = tipoRadicacion;
		this.tipoGiro = tipoGiro;
		this.oficinaSorteo = oficinaSorteo;
		this.codigoTipoCambioAsignacion = codigoTipoCambioAsignacion;
		this.descripcionCambioAsignacion = descripcionCambioAsignacion;
		this.comentarios = comentarios;
		this.fechaTurnosActivos = fechaTurnosActivos;
		this.expediente = expediente;
		this.materia = materia;
		this.descompensarAnterior = descompensarAnterior;
	}

	public SorteoParametros(SorteoParametros sorteoParametros, boolean sorteoSecudario) {
		this.sorteoOperation = sorteoParametros.getSorteoOperation();
		this.camara = sorteoParametros.getCamara();
		this.sorteo = sorteoParametros.getSorteo();
		this.tipoBolillero = sorteoParametros.getTipoBolillero();
		this.tipoRadicacion = sorteoParametros.getTipoRadicacion();
		this.tipoGiro = sorteoSecudario ? null : sorteoParametros.getTipoGiro();
		this.oficinaSorteo = sorteoParametros.getOficinaSorteo();
		this.codigoTipoCambioAsignacion = sorteoParametros.getCodigoTipoCambioAsignacion();
		this.descripcionCambioAsignacion = sorteoParametros.getDescripcionCambioAsignacion();
		this.comentarios = sorteoParametros.getComentarios();
		this.fechaTurnosActivos = sorteoParametros.getFechaTurnosActivos();
		this.expediente = sorteoParametros.getExpediente();
		this.materia = sorteoParametros.getMateria();				
		this.descompensarAnterior = sorteoParametros.isDescompensarAnterior();
		
		this.excludeOficinasIds = sorteoParametros.getExcludeOficinasIds();
		this.rubro = sorteoParametros.getRubro();
		this.rubroAsignacion = sorteoParametros.getRubroAsignacion();
		this.usuario = sorteoParametros.getUsuario();
		this.sorteoSecudario = sorteoSecudario;
		this.remisionPorUsuario = sorteoParametros.isRemisionPorUsuario();
		this.fojasPase = sorteoParametros.getFojasPase();

	}
	
	

	public String getRubro() {
		return rubro;
	}

	public List<Integer> getExcludeOficinasIds() {
		return excludeOficinasIds;
	}

	public void setExcludeOficinasIds(List<Integer> excludeOficinasIds) {
		this.excludeOficinasIds = excludeOficinasIds;
	}

	public Sorteo getSorteo() {
		return sorteo;
	}

	public void setSorteo(Sorteo sorteo) {
		this.sorteo = sorteo;
	}

	public TipoRadicacionEnumeration.Type getTipoRadicacion() {
		return tipoRadicacion;
	}

	public void setTipoRadicacion(TipoRadicacionEnumeration.Type tipoRadicacion) {
		this.tipoRadicacion = tipoRadicacion;
	}

	public Oficina getOficinaSorteo() {
		return oficinaSorteo;
	}

	public void setOficinaSorteo(Oficina oficinaSorteo) {
		this.oficinaSorteo = oficinaSorteo;
	}

	public Camara getCamara() {
		return camara;
	}

	public Expediente getExpediente() {
		return expediente;
	}

	public Oficina getOficinaAsignada() {
		return oficinaAsignada;
	}

	public void setOficinaAsignada(Oficina oficinaAsignada) {
		this.oficinaAsignada = oficinaAsignada;
	}

	public Oficina getOficinaDesasignada() {
		return oficinaDesasignada;
	}

	public void setOficinaDesasignada(Oficina oficinaDesasignada) {
		this.oficinaDesasignada = oficinaDesasignada;
	}

	public SorteoOperation getSorteoOperation() {
		return sorteoOperation;
	}

	public TipoGiroEnumeration.Type getTipoGiro() {
		return tipoGiro;
	}

	public void setTipoGiro(TipoGiroEnumeration.Type tipoGiro) {
		this.tipoGiro = tipoGiro;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Materia getMateria() {
		return materia;
	}

	public String getCodigoTipoCambioAsignacion() {
		return codigoTipoCambioAsignacion;
	}

	public void setCodigoTipoCambioAsignacion(String codigoTipoCambioAsignacion) {
		this.codigoTipoCambioAsignacion = codigoTipoCambioAsignacion;
	}

	public SorteoParametros getSiguienteSorteoEncadenado() {
		return siguienteSorteoEncadenado;
	}

	public void setSiguienteSorteoEncadenado(
			SorteoParametros siguienteSorteoAutomatico) {
		this.siguienteSorteoEncadenado = siguienteSorteoAutomatico;
	}

	public void refreshPojos(EntityManager entityManager) {
		if(expediente != null){
		    expediente = entityManager.find(Expediente.class, expediente.getId());
		}
		
	}

	public String getRubroAsignacion() {
		return rubroAsignacion;
	}

	public void setRubroAsignacion(String rubroAsignacion) {
		this.rubroAsignacion = rubroAsignacion;
	}

	public Date getFechaTurnosActivos() {
		return fechaTurnosActivos;
	}

	public void setFechaTurnosActivos(Date fechaTurnosActivos) {
		this.fechaTurnosActivos = fechaTurnosActivos;
	}

	public String getRubroAnterior() {
		return rubroAnterior;
	}

	public void setRubroAnterior(String rubroAnterior) {
		this.rubroAnterior = rubroAnterior;
	}

	public String getDescripcionCambioAsignacion() {
		return descripcionCambioAsignacion;
	}

	public void setDescripcionCambioAsignacion(String descripcionCambioAsignacion) {
		this.descripcionCambioAsignacion = descripcionCambioAsignacion;
	}

	public Boolean getCompensacionAnterior() {
		return compensacionAnterior;
	}

	public void setCompensacionAnterior(Boolean compensacionAnterior) {
		this.compensacionAnterior = compensacionAnterior;
	}

	public void setRubro(String rubro) {
		this.rubro = rubro;
	}

	public boolean isSorteoSecudario() {
		return sorteoSecudario;
	}

	public void setSorteoSecudario(boolean sorteoSecudario) {
		this.sorteoSecudario = sorteoSecudario;
	}

	public boolean isBuscaConexidad() {
		return buscaConexidad;
	}

	public void setBuscaConexidad(boolean buscaConexidad) {
		this.buscaConexidad = buscaConexidad;
	}

	public List<Integer> getOnlyOficinasIds() {
		return onlyOficinasIds;
	}

	public void setOnlyOficinasIds(List<Integer> onlyOficinasIds) {
		this.onlyOficinasIds = onlyOficinasIds;
	}

	public boolean isSorteaMediador() {
		return sorteaMediador;
	}

	public void setSorteaMediador(boolean sorteaMediador) {
		this.sorteaMediador = sorteaMediador;
	}

	public void setSorteoOperation(SorteoOperation sorteoOperation) {
		this.sorteoOperation = sorteoOperation;
	}

	public boolean isAsignaPorConexidad() {
		return asignaPorConexidad;
	}

	public void setAsignaPorConexidad(boolean asignaPorConexidad) {
		this.asignaPorConexidad = asignaPorConexidad;
	}

	public RecursoExp getRecursoExp() {
		return recursoExp;
	}

	public void setRecursoExp(RecursoExp recursoExp) {
		this.recursoExp = recursoExp;
	}

	public boolean isSortea() {
		return sortea;
	}

	public void setSortea(boolean sortea) {
		this.sortea = sortea;
	}

	public boolean isIgnoreCheckDobleInicio() {
		return ignoreCheckDobleInicio;
	}

	public void setIgnoreCheckDobleInicio(boolean ignoreCheckDobleInicio) {
		this.ignoreCheckDobleInicio = ignoreCheckDobleInicio;
	}

	public boolean isDescompensarAnterior() {
		return descompensarAnterior;
	}

	public void setDescompensarAnterior(boolean descompensarAnterior) {
		this.descompensarAnterior = descompensarAnterior;
	}

	public boolean isRemisionPorUsuario() {
		return remisionPorUsuario;
	}

	public void setRemisionPorUsuario(boolean remisionPorUsuario) {
		this.remisionPorUsuario = remisionPorUsuario;
	}

	public Sorteo getSorteoDependiente() {
		return sorteoDependiente;
	}

	public void setSorteoDependiente(Sorteo sorteoDependiente) {
		this.sorteoDependiente = sorteoDependiente;
	}

	public boolean isVerificarAsignarOrdenCirculacion() {
		return verificarAsignarOrdenCirculacion;
	}

	public void setVerificarAsignarOrdenCirculacion(
			boolean verificarAsignarOrdenCirculacion) {
		this.verificarAsignarOrdenCirculacion = verificarAsignarOrdenCirculacion;
	}

	public FojasEdit getFojasPase() {
		return fojasPase;
	}

	public void setFojasPase(FojasEdit fojasPase) {
		this.fojasPase = fojasPase;
	}

	public TipoBolillero getTipoBolillero() {
		return tipoBolillero;
	}

	public void setTipoBolillero(TipoBolillero tipoBolillero) {
		this.tipoBolillero = tipoBolillero;
	}
	/** ATOS OVD */
	public Integer getLegajoOVD() {
		return legajoOVD;
	}

	public Integer getAnioOVD() {
		return anioOVD;
	}
	public void setAnioOVD(Integer anioOVD) {
		this.anioOVD = anioOVD;
	}

	public void setLegajoOVD(Integer legajoOVD) {
		this.legajoOVD = legajoOVD;
	}

	public void setCaratulaOVD(String caratulaOVD) {
		this.caratulaOVD=caratulaOVD;
	}
	public String getCaratulaOVD() {
		return caratulaOVD;
	}
	/** ATOS OVD */
}
