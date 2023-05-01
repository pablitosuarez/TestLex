package com.base100.lex100.mesaEntrada.sorteo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.base100.lex100.component.enumeration.TipoGiroEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.RecursoExp;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.mesaEntrada.ingreso.FojasEdit;

public class SorteoExpedienteEntry {
	private SorteoOperation operation;
	private Camara camara;
	private Expediente expediente;
	private RecursoExp recursoExp;
	private TipoRadicacionEnumeration.Type tipoRadicacion;
	private TipoGiroEnumeration.Type tipoGiro;
	private boolean finished;
	private List<Integer> excludeOficinaIds;
	private List<Integer> onlyOficinaIds;
	private Oficina oficinaAsignada;
	private Oficina oficinaDesasignada;
	private String comentarios;
	private String codigoTipoCambioAsignacion;
	private String descripcionCambioAsignacion;
	private String usuario;
    private Integer codigoTipoOficina;
	private Oficina oficinaSorteo;
	private String rubro;
	private String rubroAnterior;
	private Boolean compensacionAnterior;
	private Date fechaTurnosActivos;
	private boolean sorteaMediador;
	private Sorteo sorteoDependiente;
	private boolean sortea;
	private boolean buscaConexidad;
	private boolean asignaPorConexidad;
	private boolean ignoreCheckDobleInicio;
	
	private boolean descompensarAnterior;
	private boolean verificarAsignarOrdenCirculacion;

	private boolean remisionPorUsuario;
	
	private FojasEdit fojasPase;

	/** ATOS COMERCIAL */
	private Integer idCausa = null;
	
	public void setIdCausa(Integer idCausa) {
		this.idCausa = idCausa;
	}

	public Integer getIdCausa() {
		return idCausa;
	}
	/*******ATOS COMERCIAL*******/
	
	private SorteoParametros siguienteSorteoEncadenado;
	
	public SorteoExpedienteEntry(SorteoParametros sorteoParametros) {
		super();
		this.operation = sorteoParametros.getSorteoOperation();
		this.camara = sorteoParametros.getCamara();
		this.tipoRadicacion = sorteoParametros.getTipoRadicacion();
		this.tipoGiro = sorteoParametros.getTipoGiro();
		this.expediente = sorteoParametros.getExpediente();
		this.recursoExp = sorteoParametros.getRecursoExp();
		if ((sorteoParametros.getSorteo() != null) && (sorteoParametros.getSorteo().getTipoOficina() != null)){
			this.codigoTipoOficina = sorteoParametros.getSorteo().getTipoOficina().getCodigo();
		}
		this.excludeOficinaIds = sorteoParametros.getExcludeOficinasIds();
		this.onlyOficinaIds = sorteoParametros.getOnlyOficinasIds();
		this.oficinaAsignada = sorteoParametros.getOficinaAsignada();
		this.oficinaDesasignada = sorteoParametros.getOficinaDesasignada();
		this.codigoTipoCambioAsignacion = sorteoParametros.getCodigoTipoCambioAsignacion();
		this.descripcionCambioAsignacion = sorteoParametros.getDescripcionCambioAsignacion();
		this.comentarios = sorteoParametros.getComentarios();		
		this.usuario = sorteoParametros.getUsuario();		
		this.oficinaSorteo = sorteoParametros.getOficinaSorteo();
		this.rubro = sorteoParametros.getRubro();
		this.rubroAnterior = sorteoParametros.getRubroAnterior();
		this.compensacionAnterior = sorteoParametros.getCompensacionAnterior();
		this.fechaTurnosActivos = sorteoParametros.getFechaTurnosActivos();
		this.sorteaMediador = sorteoParametros.isSorteaMediador();
		
		this.sortea = sorteoParametros.isSortea();
		this.buscaConexidad = sorteoParametros.isBuscaConexidad();
		this.asignaPorConexidad = sorteoParametros.isAsignaPorConexidad();
		this.ignoreCheckDobleInicio = sorteoParametros.isIgnoreCheckDobleInicio();

		this.descompensarAnterior = sorteoParametros.isDescompensarAnterior();

		setSiguienteSorteoEncadenado(sorteoParametros.getSiguienteSorteoEncadenado());
		
		this.remisionPorUsuario = sorteoParametros.isRemisionPorUsuario();
		
		this.sorteoDependiente = sorteoParametros.getSorteoDependiente();
		this.verificarAsignarOrdenCirculacion = sorteoParametros.isVerificarAsignarOrdenCirculacion();

		this.fojasPase = sorteoParametros.getFojasPase();
		
		/** ATOS COMERCIAL */
		this.idCausa = sorteoParametros.getIdCausa();
		/** ATOS COMERCIAL */
	}

	public SorteoExpedienteEntry(SorteoExpedienteEntry entry, EntityManager entityManager) {
		super();
		this.operation = entry.operation;
		this.camara = entry.camara == null ? null : entityManager.find(Camara.class, entry.camara.getId());
		this.tipoRadicacion = entry.tipoRadicacion;
		this.tipoGiro = entry.tipoGiro;
		this.expediente = entry.expediente == null ? null : entityManager.find(Expediente.class, entry.expediente.getId());
		this.recursoExp = entry.recursoExp == null ? null : entityManager.find(RecursoExp.class, entry.recursoExp.getId());
		this.codigoTipoOficina = entry.codigoTipoOficina;
		this.excludeOficinaIds = entry.excludeOficinaIds == null ? null : new  ArrayList<Integer>(entry.excludeOficinaIds);
		this.onlyOficinaIds = entry.onlyOficinaIds == null ? null : new  ArrayList<Integer>(entry.onlyOficinaIds);
		this.oficinaAsignada = entry.oficinaAsignada == null ? null : entityManager.find(Oficina.class, entry.oficinaAsignada.getId());
		this.oficinaDesasignada = entry.oficinaDesasignada == null ? null : entityManager.find(Oficina.class, entry.oficinaDesasignada.getId());
		this.codigoTipoCambioAsignacion = entry.codigoTipoCambioAsignacion;
		this.descripcionCambioAsignacion = entry.descripcionCambioAsignacion;
		this.comentarios = entry.comentarios;		
		this.usuario = entry.usuario;		
		this.oficinaSorteo = entry.oficinaSorteo == null ? null : entityManager.find(Oficina.class, entry.oficinaSorteo.getId());
		this.rubro = entry.rubro;
		this.rubroAnterior = entry.rubroAnterior;
		this.compensacionAnterior = entry.compensacionAnterior;
		this.fechaTurnosActivos = entry.fechaTurnosActivos;
		this.siguienteSorteoEncadenado = entry.siguienteSorteoEncadenado;
		this.sorteaMediador = entry.isSorteaMediador();

		this.sortea = entry.isSortea();
		this.buscaConexidad = entry.isBuscaConexidad();
		this.asignaPorConexidad = entry.isAsignaPorConexidad();
		this.ignoreCheckDobleInicio = entry.isIgnoreCheckDobleInicio();
		this.descompensarAnterior = entry.isDescompensarAnterior();
		this.verificarAsignarOrdenCirculacion = entry.isVerificarAsignarOrdenCirculacion();
		
		this.remisionPorUsuario = entry.remisionPorUsuario;		
		this.fojasPase = entry.getFojasPase();

		this.sorteoDependiente = entry.sorteoDependiente == null ? null : entityManager.find(Sorteo.class, entry.sorteoDependiente.getId());
			
		/** ATOS COMERCIAL */
		this.idCausa = entry.getIdCausa();
		/** ATOS COMERCIAL */
		
	}

	public Expediente getExpediente(EntityManager entityManager) {
		if (expediente != null && !entityManager.contains(expediente)) {
			return (Expediente)entityManager.find(Expediente.class, expediente.getId());
		}
		
		/*******ATOS Comercial *******/
		//Fecha: 07-05-2014
		//Desarrollador:  Luis Suarez
		if(expediente != null && this.idCausa != null){
			expediente.setExpedienteWeb(Boolean.TRUE); 
		}
		/*******ATOS Comercial *******/
		
		return expediente;
	}

	public RecursoExp getRecursoExp(EntityManager entityManager) {
		if (recursoExp != null && !entityManager.contains(recursoExp)) {
			return (RecursoExp)entityManager.find(RecursoExp.class, recursoExp.getId());
		}
		return recursoExp;
	}

	public void setExpediente(Expediente expediente) {
		this.expediente = expediente;
	}
	
	public synchronized void join() {
		if (!isFinished()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
			}
		}
	}

	public synchronized boolean isFinished() {
		return finished;
	}

	public synchronized void setFinished(boolean finished) {
		this.finished = finished;
		if (finished) {
			this.notifyAll();
		}
	}

	public TipoRadicacionEnumeration.Type getTipoRadicacion() {
		return tipoRadicacion;
	}

	public void setTipoRadicacion(TipoRadicacionEnumeration.Type tipoRadicacion) {
		this.tipoRadicacion = tipoRadicacion;
	}


	public List<Integer> getExcludeOficinaIds() {
		return excludeOficinaIds;
	}


	public SorteoOperation getOperation() {
		return operation;
	}

	public Integer getOficinaAsignadaId() {
		return oficinaAsignada != null ? oficinaAsignada.getId() : null;
	}
	
	public Oficina getOficinaAsignada(EntityManager entityManager) {
		if (oficinaAsignada != null && !entityManager.contains(oficinaAsignada)) {
			return (Oficina)entityManager.find(Oficina.class, oficinaAsignada.getId());
		}
		return oficinaAsignada;
	}


	public void setOficinaAsignada(Oficina oficinaAsignada) {
		this.oficinaAsignada = oficinaAsignada;
	}


	public Oficina getOficinaDesasignada(EntityManager entityManager) {
		if (oficinaDesasignada != null && !entityManager.contains(oficinaDesasignada)) {
			return (Oficina)entityManager.find(Oficina.class, oficinaDesasignada.getId());
		}
		return oficinaDesasignada;
	}


	public void setOficinaDesasignada(Oficina oficinaDesasignada) {
		this.oficinaDesasignada = oficinaDesasignada;
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


	public Oficina getOficinaSorteo(EntityManager entityManager) {
		if (oficinaSorteo != null && !entityManager.contains(oficinaSorteo)) {
			return (Oficina)entityManager.find(Oficina.class, oficinaSorteo.getId());
		}
		return oficinaSorteo;
	}

	public Oficina getOficinaSorteo() {
		return oficinaSorteo;
	}


	public void setOficinaSorteo(Oficina oficinaSorteo) {
		this.oficinaSorteo = oficinaSorteo;
	}


	public String getCodigoTipoCambioAsignacion() {
		return codigoTipoCambioAsignacion;
	}


	public void setCodigoTipoCambioAsignacion(String codigoTipoCambioAsignacion) {
		this.codigoTipoCambioAsignacion = codigoTipoCambioAsignacion;
	}

	public String getDescripcionCambioAsignacion() {
		return descripcionCambioAsignacion;
	}

	public void setDescripcionCambioAsignacion(String descripcionCambioAsignacion) {
		this.descripcionCambioAsignacion = descripcionCambioAsignacion;
	}
	
	public SorteoParametros getSiguienteSorteoEncadenado() {
		return siguienteSorteoEncadenado;
	}


	public void setSiguienteSorteoEncadenado(SorteoParametros siguienteSorteoEncadenado) {
		this.siguienteSorteoEncadenado = siguienteSorteoEncadenado;
	}


	public Integer getCodigoTipoOficina() {
		return codigoTipoOficina;
	}


	public String getRubro() {
		return rubro;
	}


	public Camara getCamara() {
		return camara;
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

	public Boolean getCompensacionAnterior() {
		return compensacionAnterior;
	}

	public void setCompensacionAnterior(Boolean compensacionAnterior) {
		this.compensacionAnterior = compensacionAnterior;
	}

	public List<Integer> getOnlyOficinaIds() {
		return onlyOficinaIds;
	}

	public void setOnlyOficinaIds(List<Integer> onlyOficinaIds) {
		this.onlyOficinaIds = onlyOficinaIds;
	}

	public boolean isSorteaMediador() {
		return sorteaMediador;
	}

	public void setSorteaMediador(boolean sorteaMediador) {
		this.sorteaMediador = sorteaMediador;
	}

	public boolean isBuscaConexidad() {
		return buscaConexidad;
	}

	public void setBuscaConexidad(boolean buscaConexidad) {
		this.buscaConexidad = buscaConexidad;
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

	public void setIgnoreCheckDobleInicio(boolean ignoreCheckDobleInicio) {
		this.ignoreCheckDobleInicio = ignoreCheckDobleInicio;
	}

	public boolean isIgnoreCheckDobleInicio() {
		return ignoreCheckDobleInicio;
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

}
