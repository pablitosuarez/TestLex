package com.base100.lex100.mesaEntrada.ingreso;

import java.util.Date;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.component.enumeration.DependenciaContendienteEnumeration;
import com.base100.lex100.component.enumeration.DependenciaOrigenEnumeration;
import com.base100.lex100.component.enumeration.MateriaEnumeration;
import com.base100.lex100.component.enumeration.TipoCausaEnumeration;
import com.base100.lex100.controller.entity.tipoCausa.TipoCausaSearch;
import com.base100.lex100.entity.Dependencia;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.TipoSubexpediente;

public class ExpedienteEspecialEdit extends AbstractEditPojo{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8502370674523750209L;
	
	private TipoCausa tipoCausa;

	private String codigo;
	private Date fechaHecho;
	private Date fechaInformacion;
	private String numeroCodigo;	// Tipo Codigo Relacionado
	private String numero;			// Codigo Relacionado
	private String anioConcat;
	private String numeroConcat;
	private String descripcion;
//	private String organismoInterno;
//	private String organismoExterno;
	private Dependencia dependencia;
	private Dependencia dependencia2;
	private String otraDependencia;
	private String otraDependencia2;
	
	///// Especifico Penal
	private Integer incidentes;
	private Integer hechos;
	private Boolean menores;
	private Boolean detenidos;
	private Boolean internados;
	private Boolean identidadReservada;
	private Boolean efecto;
	private Boolean derechosHumanos;
	private boolean corrupcion;
	private String declaracionHecho;
	private boolean denunciaFirmada;
	private boolean defensorOficial;	//ITO (Incompetencia TO)

	//Exhortos
	private String juezExhortante;
	private String juzgadoFueroExhorto;
	private String jurisdiccionExhorto;
	private String caratulaOrigen;
	
	private String ultimoNumero;
	private String ultimoNumeroCodigo;
	private List<Expediente> expedientesConexosPorNumeroOrigen;
	private boolean tratamientoIncidental;
	private TipoSubexpediente tipoSubexpediente;
	
	////////////////////// interno 
	//private boolean error;
	private boolean errorTipoCausa;
	
	public ExpedienteEspecialEdit(){
		this.fechaInformacion = new Date();
		this.numeroCodigo = "EX";
	}

	private String calculateNumeroCodigo(String codigoTipoCausa){
		String ret = null;
		if(TipoCausaEnumeration.CODIGO_CAS.equals(codigoTipoCausa)){
			ret = "CA";
		}else if(TipoCausaEnumeration.CODIGO_HCO.equals(codigoTipoCausa)){
			;
		}else if(TipoCausaEnumeration.CODIGO_TRS.equals(codigoTipoCausa)){
			ret = "CA";
		}else if(TipoCausaEnumeration.CODIGO_INC.equals(codigoTipoCausa)){
			ret = "CA";
		}else if(TipoCausaEnumeration.CODIGO_EXH.equals(codigoTipoCausa)){
			ret = "CA";
		}else if(TipoCausaEnumeration.CODIGO_EXT.equals(codigoTipoCausa)){
			ret = "CA";
		}else if(TipoCausaEnumeration.CODIGO_FIS.equals(codigoTipoCausa)){
			;
		}else if(TipoCausaEnumeration.CODIGO_MPR.equals(codigoTipoCausa)){
			ret = "CA";
		}else if(TipoCausaEnumeration.CODIGO_TRC.equals(codigoTipoCausa)){
			ret = "SU";
		}else if(TipoCausaEnumeration.CODIGO_NN .equals(codigoTipoCausa)){
			ret = "SU";
		}else if(TipoCausaEnumeration.CODIGO_POO.equals(codigoTipoCausa)){
			ret = "SU";
		}else if(TipoCausaEnumeration.CODIGO_TES.equals(codigoTipoCausa)){
			ret = "CA";
		}else if(TipoCausaEnumeration.CODIGO_DEN.equals(codigoTipoCausa)){
			ret = "CA";
		}else if(TipoCausaEnumeration.CODIGO_QUE.equals(codigoTipoCausa)){
			ret = "CA";
		}else if(TipoCausaEnumeration.CODIGO_AMP.equals(codigoTipoCausa)){
			;
		}else if(TipoCausaEnumeration.CODIGO_PRE.equals(codigoTipoCausa)){
			ret = "SU";
		}else if(TipoCausaEnumeration.CODIGO_EXP.equals(codigoTipoCausa)){
			;
		}else if(TipoCausaEnumeration.CODIGO_HAD.equals(codigoTipoCausa)){
			;
		}else if(TipoCausaEnumeration.CODIGO_OTO.equals(codigoTipoCausa)){
			ret = "EX";
		}else if(TipoCausaEnumeration.CODIGO_ECA.equals(codigoTipoCausa)){
			;
		}else if(TipoCausaEnumeration.CODIGO_ANT.equals(codigoTipoCausa)){
			ret = "EX";
		}else if(TipoCausaEnumeration.CODIGO_SEC.equals(codigoTipoCausa)){
			ret = "SC";
		}
		return ret;
	}
	
	public void setTipoCausa(TipoCausa entity) {
		tipoCausa = entity;
		if(tipoCausa != null){
			setCodigo(tipoCausa.getCodigo());
			if (MateriaEnumeration.isAnyPenal(tipoCausa.getMateria())) {
				setNumeroCodigo(calculateNumeroCodigo(getCodigo()));
			} else if(TipoCausaEnumeration.CODIGO_SEC.equals(getCodigo())){
				setNumeroCodigo("SC");
			}
		}else{
			setCodigo(null);
		}
	}

	public Date getFechaHecho() {
		return fechaHecho;
	}

	public void setFechaHecho(Date fechaHecho) {
		this.fechaHecho = fechaHecho;
	}

	public Date getFechaInformacion() {
		return fechaInformacion;
	}

	public void setFechaInformacion(Date fechaInformacion) {
		this.fechaInformacion = fechaInformacion;
	}

	public String getNumeroCodigo() {
		return numeroCodigo;
	}

	public void setNumeroCodigo(String numeroCodigo) {
		if (numeroCodigo != null) {
			numeroCodigo = numeroCodigo.trim();
		}
		this.numeroCodigo = numeroCodigo;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		if (numero != null) {
			numero = numero.trim();
		}
		this.numero = numero;
	}
	
	public String getAnioConcat() {
		if(anioConcat !=null){
			return anioConcat;
		}else if (getNumero()!=null && getNumero().contains("/")){
			return 	getNumero().substring(getNumero().indexOf("/"),getNumero().length()).replace("/", "");
		}else{
			return anioConcat;
		}
		
		
	}

	public void setAnioConcat(String anioConcat) {
		this.anioConcat = anioConcat;
	}

	public String getNumeroConcat() {
		if(numeroConcat !=null){
			return numeroConcat;
		}else 	if (getNumero()!=null && getNumero().contains("/")){
			return 	getNumero().substring(0, getNumero().lastIndexOf("/"));
		}else{
			return numeroConcat;
		}
	}

	public void setNumeroConcat(String numeroConcat) {
		this.numeroConcat = numeroConcat;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

//	public String getOrganismoInterno() {
//		return organismoInterno;
//	}
//
//
//	public void setOrganismoInterno(String organismoInterno) {
//		this.organismoInterno = organismoInterno;
//	}
//
//
//	public String getOrganismoExterno() {
//		return organismoExterno;
//	}
//
//
//	public void setOrganismoExterno(String organismoExterno) {
//		this.organismoExterno = organismoExterno;
//	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public TipoCausa getTipoCausa() {
		return tipoCausa;
	}

	public void checkAccept() throws EntityOperationException {
		setError(false);
		setErrorTipoCausa(false);
		
		if(getTipoCausa() == null || !getTipoCausa().getCodigo().equals(getCodigo())){
			if(getCodigo() != null){
				TipoCausaSearch tipoCausaSearch = (TipoCausaSearch) Component.getInstance(TipoCausaSearch.class, true);
				this.tipoCausa = tipoCausaSearch.findByNaturalKey(getCodigo().trim(), ExpedienteMeAdd.instance().getMateria());
				if(tipoCausa == null){
					String errKey = "tipoCausa.error.notExist";
					errorMsg(errKey);
					setErrorTipoCausa(true);
					setError(true);
				}
			}else{
				this.tipoCausa = null;
			}
		}
		if(Boolean.TRUE.equals(getInternados())){
			if(!Boolean.TRUE.equals(getMenores())){
				String errKey = "expedienteEspecial.error.internadosSinMenores";
				errorMsg(errKey);
				setError(true);							
			}
		}		
		if (getDependencia() == null) {
			DependenciaOrigenEnumeration dependenciaOrigenEnumeration = DependenciaOrigenEnumeration.instance();
			String otraDependencia = Strings.isEmpty(dependenciaOrigenEnumeration.getUserInput()) ? null : dependenciaOrigenEnumeration.getUserInput();
			setOtraDependencia(otraDependencia);
			dependenciaOrigenEnumeration.setUserInput(null);
		}
		if (getDependencia2() == null) {
			DependenciaContendienteEnumeration dependenciaContendienteEnumeration = DependenciaContendienteEnumeration.instance();
			String otraDependencia2 = Strings.isEmpty(dependenciaContendienteEnumeration.getUserInput()) ? null : dependenciaContendienteEnumeration.getUserInput();
			setOtraDependencia2(otraDependencia2);
			dependenciaContendienteEnumeration.setUserInput(null);
		}
		
//		if(getDeclaracionHecho() != null && getDeclaracionHecho().length() > 199){
//			setDeclaracionHecho(getDeclaracionHecho().substring(0, 199));
//		}
//		if(isError()){
//			throw new EntityOperationException();
//		}
	}
	
	public boolean isErrorTipoCausa() {
		return errorTipoCausa;
	}

	public void setErrorTipoCausa(boolean errorTipoExpedienteEspecias) {
		this.errorTipoCausa = errorTipoExpedienteEspecias;
	}

	public Dependencia getDependencia() {
		return dependencia;
	}

	public void setDependencia(Dependencia dependencia) {
		this.dependencia = dependencia;
	}

	public Dependencia getDependencia2() {
		return dependencia2;
	}

	public void setDependencia2(Dependencia dependencia2) {
		this.dependencia2 = dependencia2;
	}

	public boolean isDefined(){
		return (tipoCausa != null) && tipoCausa.isEspecial();
	}

	public Integer getIncidentes() {
		return incidentes;
	}
	public void setIncidentes(Integer incidentes) {
		this.incidentes = incidentes;
	}
	public Integer getHechos() {
		return hechos;
	}
	public void setHechos(Integer hechos) {
		this.hechos = hechos;
	}
	public Boolean getMenores() {
		return menores;
	}
	public void setMenores(Boolean menores) {
		this.menores = menores;
	}
	public Boolean getDetenidos() {
		return detenidos;
	}
	public void setDetenidos(Boolean detenidos) {
		this.detenidos = detenidos;
	}
	public Boolean getInternados() {
		return internados;
	}
	public void setInternados(Boolean internados) {
		this.internados = internados;
	}
	public Boolean getEfecto() {
		return efecto;
	}
	public void setEfecto(Boolean efecto) {
		this.efecto = efecto;
	}

	public String getDeclaracionHecho() {
		return declaracionHecho;
	}

	public void setDeclaracionHecho(String declaracionHecho) {
		this.declaracionHecho = declaracionHecho;
	}

	public Boolean getIdentidadReservada() {
		return identidadReservada;
	}

	public void setIdentidadReservada(Boolean identidadReservada) {
		this.identidadReservada = identidadReservada;
	}

	//ITO (Incompetencia TO)
	public boolean isDefensorOficial() {
		return defensorOficial;
	}

	public void setDefensorOficial(boolean defensorOficial) {
		this.defensorOficial = defensorOficial;
	}
	//
	public String getJuezExhortante() {
		return juezExhortante;
	}

	public void setJuezExhortante(String juezExhortante) {
		this.juezExhortante = juezExhortante;
	}

	public String getDependenciaContendiente() {
		return juzgadoFueroExhorto;
	}

	public void setDependenciaContendiente(String dependenciaContendiente) {
		this.juzgadoFueroExhorto = dependenciaContendiente;
	}

	public String getJuzgadoFueroExhorto() {
		return juzgadoFueroExhorto;
	}

	public void setJuzgadoFueroExhorto(String juzgadoFueroExhorto) {
		this.juzgadoFueroExhorto = juzgadoFueroExhorto;
	}

	public String getJurisdiccionExhorto() {
		return jurisdiccionExhorto;
	}

	public void setJurisdiccionExhorto(String jurisdiccionExhorto) {
		this.jurisdiccionExhorto = jurisdiccionExhorto;
	}

	public String getCaratulaOrigen() {
		return caratulaOrigen;
	}

	public void setCaratulaOrigen(String caratulaOrigen) {
		this.caratulaOrigen = caratulaOrigen;
	}

	public String getOtraDependencia() {
		return otraDependencia;
	}

	public void setOtraDependencia(String otraDependencia) {
		this.otraDependencia = otraDependencia;
	}

	public String getOtraDependencia2() {
		return otraDependencia2;
	}

	public void setOtraDependencia2(String otraDependencia2) {
		this.otraDependencia2 = otraDependencia2;
	}
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDenunciaFirmada() {
		return denunciaFirmada;
	}

	public void setDenunciaFirmada(boolean denunciaFirmada) {
		this.denunciaFirmada = denunciaFirmada;
	}

	public Boolean getDerechosHumanos() {
		return derechosHumanos;
	}

	public void setDerechosHumanos(Boolean derechosHumanos) {
		this.derechosHumanos = derechosHumanos;
	}

	public List<Expediente> getExpedientesConexosPorNumeroOrigen() {
		return expedientesConexosPorNumeroOrigen;
	}

	public void setExpedientesConexosPorNumeroOrigen(
			List<Expediente> expedientesConexosPorNumeroOrigen) {
		this.expedientesConexosPorNumeroOrigen = expedientesConexosPorNumeroOrigen;
	}

	public String getUltimoNumero() {
		return ultimoNumero;
	}

	public void setUltimoNumero(String ultimoNumero) {
		this.ultimoNumero = ultimoNumero;
	}

	public String getUltimoNumeroCodigo() {
		return ultimoNumeroCodigo;
	}

	public void setUltimoNumeroCodigo(String ultimoNumeroCodigo) {
		this.ultimoNumeroCodigo = ultimoNumeroCodigo;
	}

	public boolean isTratamientoIncidental() {
		return tratamientoIncidental;
	}

	public void setTratamientoIncidental(boolean tratamientoIncidental) {
		this.tratamientoIncidental = tratamientoIncidental;
	}

	public TipoSubexpediente getTipoSubexpediente() {
		return tipoSubexpediente;
	}

	public void setTipoSubexpediente(TipoSubexpediente tipoSubexpediente) {
		this.tipoSubexpediente = tipoSubexpediente;
	}

	public void setCorrupcion(boolean corrupcion) {
		this.corrupcion = corrupcion;
	}

	public boolean isCorrupcion() {
		return corrupcion;
	}

	
}
