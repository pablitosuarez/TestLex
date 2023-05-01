package com.base100.lex100.mesaEntrada.ingreso;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.util.Strings;

import com.base100.lex100.component.enumeration.SiglaEnumeration;
import com.base100.lex100.component.enumeration.TipoDocumentoIdentidadEnumeration;
import com.base100.lex100.component.enumeration.TipoIntervinienteEnumeration;
import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.controller.entity.nacionalidad.NacionalidadSearch;
import com.base100.lex100.controller.entity.provincia.ProvinciaSearch;
import com.base100.lex100.controller.entity.tipoDocumentoIdentidad.TipoDocumentoIdentidadSearch;
import com.base100.lex100.entity.CentroInternamiento;
import com.base100.lex100.entity.DemandadoPoder;
import com.base100.lex100.entity.Dependencia;
import com.base100.lex100.entity.Interviniente;
import com.base100.lex100.entity.IntervinienteExp;
import com.base100.lex100.entity.LeyAplicable;
import com.base100.lex100.entity.Nacionalidad;
import com.base100.lex100.entity.Poder;
import com.base100.lex100.entity.Provincia;
import com.base100.lex100.entity.Sigla;
import com.base100.lex100.entity.TestigoPoder;
import com.base100.lex100.entity.TipoDocumentoIdentidad;
import com.base100.lex100.entity.TipoInterviniente;
import com.base100.lex100.manager.intervinienteExp.IntervinienteExpManager;

public class IntervinienteEdit extends AbstractEditPojo{
	
	private String actorODemandado;
	private String nombre;
	private String apellidos;
	private String apellidoMaterno;
	private String iniciales;
	
	private String domicilio;
	private String numeroDomicilio;
	private String detalleDomicilio;	
    private String localidad;
    private Provincia provincia;    
    private String codPostal;
    private String telefono;
    private String fax;
    private Integer codZona;

    
	private String domicilioConstituido;
	private String numeroDomicilioConstituido;
	private String detalleDomicilioConstituido;
    private String localidadConstituido;
    private Provincia provinciaConstituido;
    private String codPostalConstituido;
    private Integer codZonaConstituido;

	private String domicilioArresto;
	private String numeroDomicilioArresto;
	private String detalleDomicilioArresto;
    private String localidadArresto;
    private Provincia provinciaArresto;

    private String numeroIGJ;
    private String nroExpAdm;
    private String beneficiario;
    private LeyAplicable leyAplicable;
    
	private String tipoDocumentoCodigo;
	private TipoDocumentoIdentidad tipoDocumento;
	private String numero;
	private String control;
	private boolean identidadReservada;
	private boolean menor;
	private boolean detenido;
	private boolean arrestoDomiciliario;
	private boolean personaJuridica;
	private boolean internado;
	private boolean funcionario;
	private boolean domicilioInvestigado;
	private String sexo;
	private Nacionalidad nacionalidad;
	private Interviniente interviniente;
	private TipoInterviniente tipoInterviniente;
	private Dependencia dependencia;
	private CentroInternamiento centroInternamiento;
	private Date fechaDetencion;

	private boolean frecuente;
	
	private boolean copiarDomicilioDenunciado;
	
	TipoDocumentoIdentidad tipoDocumentoCuit;
	
	private static Integer INTERVINIENTE_TIPO_ACTOR = TipoIntervinienteEnumeration.ACTOR_NUMERO_ORDEN;
	private static Integer INTERVINIENTE_TIPO_DEMANDADO = TipoIntervinienteEnumeration.DEMANDADO_NUMERO_ORDEN;
	
	private static String INTERVINIENTE_DENUNCIANTE = "DENUNCIANTE";
	private static String INTERVINIENTE_VICTIMA = "VICTIMA";
	
	private IntervinienteExp intervinienteExp;
	private IntervinienteExp intervinienteExpOld;
	
	private boolean selected;
	
	private Sigla sigla;
	
	private Poder poder;

	public IntervinienteEdit(){
	}

	public IntervinienteEdit(IntervinienteExp intervinienteExp) {
		this.intervinienteExp = intervinienteExp;
		this.intervinienteExpOld = cloneIntervinienteExp(intervinienteExp);
		copyFieldsFromIntervinienteExp(intervinienteExp);
		setNew(false);
	}
	
	private IntervinienteExp cloneIntervinienteExp(IntervinienteExp intervinienteExp) {
		Interviniente intervinienteCloned = new Interviniente(intervinienteExp.getInterviniente());
		IntervinienteExp intervinienteExpCloned = new IntervinienteExp(intervinienteExp);
		intervinienteExpCloned.setInterviniente(intervinienteCloned);
		return intervinienteExpCloned;
	}

	public void setInterviniente(Interviniente interviniente) {
		this.interviniente = interviniente;
		resetFields();
		if(interviniente != null){
			copyFielsFromInterviniente(interviniente);
		}
	}
	
	private void resetFields() {
		this.nombre = null;
		this.apellidos = null;
		this.apellidoMaterno = null;
		this.iniciales = null;
		this.domicilio = null;			
		this.numeroDomicilio = null;			
		this.detalleDomicilio = null;
		this.localidad = null;			
		this.provincia = null;			
		this.codPostal = null;			
		this.codZona = null;			
		
		this.domicilioArresto = null;			
		this.numeroDomicilioArresto = null;		
		this.detalleDomicilioArresto = null;
		this.localidadArresto = null;			
		this.provinciaArresto = null;
		
		this.domicilioConstituido = null;			
		this.numeroDomicilioConstituido = null;		
		this.detalleDomicilioConstituido = null;
		this.localidadConstituido = null;			
		this.provinciaConstituido = null;
		this.codPostalConstituido = null;
		this.codZonaConstituido = null;

		this.numeroIGJ = null;

		this.nroExpAdm= null;
		this.beneficiario = null;
		this.leyAplicable = null;

		this.sexo = null;
		this.control = null;
		this.personaJuridica = false;
		this.identidadReservada = false;
		this.arrestoDomiciliario = false;
		this.internado = false;
		this.menor = false;
		this.detenido = false;
		this.funcionario = false;
		this.interviniente = null;
		this.tipoInterviniente = null;	
		this.dependencia = null;	
		this.centroInternamiento = null;	
		this.fechaDetencion = null;	
		this.frecuente = false;
		this.copiarDomicilioDenunciado = false;
		this.sigla = null;
		
//		this.intervinienteExp = null;
//		this.intervinienteExpOld = null;
	}

	public void clearInterviniente(){
		setInterviniente(null);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public TipoDocumentoIdentidad getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumentoIdentidad tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getNumero() {
		return numero;
	}
	
	public String getNumeroFormat() {
		if (!Strings.isEmpty(numero) && StringUtils.isNumeric(numero) && tipoDocumento != null && 
				(tipoDocumento.getCodigo().equals(TipoDocumentoIdentidadEnumeration.DU_CODE) || 
				tipoDocumento.getCodigo().equals(TipoDocumentoIdentidadEnumeration.DNI_CODE))) {
			return new DecimalFormat("#,##0").format(Long.valueOf(numero));
		}
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getControl() {
		return control;
	}

	public void setControl(String control) {
		this.control = control;
	}

	public Interviniente getInterviniente() {
		return interviniente;
	}
	
	public Sigla getSigla() {
		return sigla;
	}

	public void setSigla(Sigla sigla) {
		this.sigla = sigla;
	}

	public boolean isEmpty() {
		return !isTipoNN() && !personaJuridica && interviniente == null && isEmpty(nombre) && isEmpty(apellidos) && isEmpty(numero);
	}

	private boolean isEmpty(String s){
		return Strings.isEmpty(s);
	}

	public void copyFielsToInterviniente(Interviniente instance) {
		instance.setNombrePersona(nombre);
		instance.setApellidos(apellidos);
		instance.setApellidoMaterno(apellidoMaterno);
		instance.setIniciales(iniciales);
		if (!Strings.isEmpty(numero)) {
			instance.setTipoDocumentoIdentidad(tipoDocumento);
			instance.setNumeroDocId(numero);
			instance.setNacionalidad(nacionalidad);
		} else {
			instance.setTipoDocumentoIdentidad(null);
			instance.setNumeroDocId(null);
			instance.setNacionalidad(null);
		}
		instance.setDomicilio(domicilio);
		instance.setNumeroDomicilio(numeroDomicilio);
		instance.setDetalleDomicilio(detalleDomicilio);
		instance.setPersonaJuridica(personaJuridica);
		instance.setSexo(sexo);
		instance.setLocalidad(localidad);
		instance.setProvincia(provincia);
		instance.setCodZonaNotificacion(codZona);
		instance.setCodCodigoPostal(codPostal);
		instance.setDomicilioInvestigado(domicilioInvestigado);
		instance.setFrecuente(frecuente);
	}
	
	public void copyFielsFromInterviniente(Interviniente instance) {
		setNombre(instance.getNombrePersona());
		setApellidos(instance.getApellidos());
		setApellidoMaterno(instance.getApellidoMaterno());
		setIniciales(instance.getIniciales());
		setTipoDocumento(instance.getTipoDocumentoIdentidad());
		setNumero(instance.getNumeroDocId());
		setDomicilio(instance.getDomicilio());
		setNumeroDomicilio(instance.getNumeroDomicilio());
		setDetalleDomicilio(instance.getDetalleDomicilio());
		setPersonaJuridica(instance.isPersonaJuridica());
		setNacionalidad(instance.getNacionalidad());
		setSexo(instance.getSexo());
		setLocalidad(instance.getLocalidad());
		setProvincia(instance.getProvincia());
		setCodPostal(instance.getCodCodigoPostal());
		setCodZona(instance.getCodZonaNotificacion());
		setFrecuente(instance.isFrecuente());
	}

	public void copyFieldsToIntervinienteExp(IntervinienteExp instance){
		instance.setTipoInterviniente(tipoInterviniente);
		instance.setIdentidadReservada(isIdentidadReservada());
		instance.setMenor(isMenor());
		instance.setDetenido(isDetenido() || isArrestoDomiciliario());
		instance.setInternado(isInternado());
		instance.setCentroInternamiento(getCentroInternamiento());
		instance.setFechaDetencion(getFechaDetencion());
		instance.setDependencia(getDependencia());
		instance.setFuncionario(isFuncionario());

		instance.setNroExpAdm(getNroExpAdm());
		instance.setBeneficiario(getBeneficiario());
		instance.setLeyAplicable(getLeyAplicable());

		instance.setDomicilio(getDomicilioConstituido());
		instance.setNumeroDomicilio(getNumeroDomicilioConstituido());
		instance.setDetalleDomicilio(getDetalleDomicilioConstituido());
		instance.setLocalidad(getLocalidadConstituido());
		instance.setProvincia(getProvinciaConstituido());
		instance.setCodCodigoPostal(getCodPostalConstituido());
		instance.setCodZonaNotificacion(getCodZonaConstituido());
		
		if(IntervinienteExpManager.instance().deberiaCargar(instance)){
			IntervinienteExpManager.instance().cargarCodigosDomicilio(instance);
		}
		instance.setNumeroIGJ(getNumeroIGJ());
		instance.setControl(getControl());
	}

	public void copyFieldsFromIntervinienteExp(IntervinienteExp instance){
		setInterviniente(instance.getInterviniente());
		
		setTipoInterviniente(instance.getTipoInterviniente());
		setIdentidadReservada(instance.isIdentidadReservada());
		setMenor(instance.isMenor());
	    setFuncionario(instance.isFuncionario());

		setDomicilioConstituido(instance.getDomicilio());
		setNumeroDomicilioConstituido(instance.getNumeroDomicilio());
		setDetalleDomicilioConstituido(instance.getDetalleDomicilio());
		setLocalidadConstituido(instance.getLocalidad());
		setProvinciaConstituido(instance.getProvincia());
		
		if(IntervinienteExpManager.instance().deberiaCargar(instance)){
			IntervinienteExpManager.instance().cargarCodigosDomicilio(instance);
		}
		
		setCodPostalConstituido(instance.getCodCodigoPostal());
		setCodZonaConstituido(instance.getCodZonaNotificacion());

		setNumeroIGJ(instance.getNumeroIGJ());

		setNroExpAdm(instance.getNroExpAdm());
		setBeneficiario(instance.getBeneficiario());
		setLeyAplicable(instance.getLeyAplicable());

	    //FIXME falta guardadr domicilio aresto en instance.?
		setDetenido(instance.isDetenido() &&  instance.getDependencia() != null);
		setArrestoDomiciliario(instance.isDetenido() &&  instance.getDependencia() == null);
		
		setInternado(instance.isInternado());
		setCentroInternamiento(instance.getCentroInternamiento());
		setFechaDetencion(instance.getFechaDetencion());
		setDependencia(instance.getDependencia());	
		
		setControl(instance.getControl());
		
	}
	
	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public boolean isPersonaJuridica() {
		return personaJuridica;
	}

	public void setPersonaJuridica(boolean personaJuridica) {
		this.personaJuridica = personaJuridica;
		if(personaJuridica){
			setSexo(null);
			setNombre(null);
			setApellidoMaterno(null);
			setIniciales(null);
			//setTipoDocumento(getTipoDocumentoCuit()); de momento -> opcional
		} else {
			setNumeroIGJ(null);
		}
	}

	public void personaJuridicaChanged(Boolean newValue) {
		if (Boolean.TRUE.equals(newValue) ) {
			setSexo(null);
			setNombre(null);
			setApellidoMaterno(null);
			setIniciales(null);
		} else {
			if ((sigla != null) && sigla.isPersonaJuridica()) {
				setSigla(null);
				SiglaEnumeration.instance().setSelected(null);
				SiglaEnumeration.instance().reset();
				setApellidos(null);
				setNombre(null);
				setFrecuente(false);
				setDomicilio(null);
				setNumeroDomicilio(null);
				setDetalleDomicilio(null);
				setCodPostal(null);
				setLocalidad(createValueExpression("#{oficinaManager.localidadDefecto}",String.class).getValue());
				setProvincia(createValueExpression("#{oficinaManager.provinciaDefecto}",Provincia.class).getValue());
				setTipoDocumento(null);
				setNumero(null);
			}
			setNumeroIGJ(null);

		}
		
	}
	
	protected TipoDocumentoIdentidad getTipoDocumentoCuit() {
		if(tipoDocumentoCuit == null){
			List<TipoDocumentoIdentidad> list = TipoDocumentoIdentidadEnumeration.instance().getItemsMesa();
			for(TipoDocumentoIdentidad item: list){
				if(TipoDocumentoIdentidadEnumeration.CUIT_CODE.equals(item.getCodigo())){
					tipoDocumentoCuit = item;
					break;
				}
			}			
		}
		return tipoDocumentoCuit;
	}

	public String getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}

	public TipoInterviniente getTipoInterviniente() {
		return tipoInterviniente;
	}

	public void setTipoInterviniente(TipoInterviniente tipoInterviniente) {
		this.tipoInterviniente = tipoInterviniente;
	}

	public boolean isIdentidadReservada() {
		return identidadReservada;
	}

	public void setIdentidadReservada(boolean identidadReservada) {
		this.identidadReservada = identidadReservada;
	}

	public boolean isNoSaleEnCaratula() {
		return !Strings.isEmpty(getControl()) && IntervinienteExp.NO_SALE_EN_CARATULA.equalsIgnoreCase(getControl()); 
	}
	
	public void setNoSaleEnCaratula(boolean noSaleEnCaratula) {
		setControl(noSaleEnCaratula? IntervinienteExp.NO_SALE_EN_CARATULA: null);
	}

	@Transient
	public boolean isMostrarIniciales() {
		return !Strings.isEmpty(getControl()) && IntervinienteExp.MOSTRAR_INICIALES.equalsIgnoreCase(getControl()); 
	}
	
	public void setMostrarIniciales(boolean mostrarIniciales) {
		setControl(mostrarIniciales? IntervinienteExp.MOSTRAR_INICIALES: null);
	}

	public boolean isMenor() {
		return menor;
	}

	public void setMenor(boolean menor) {
		this.menor = menor;
	}

	public boolean isDetenido() {
		return detenido;
	}

	public void setDetenido(boolean detenido) {
		this.detenido = detenido;
	}

	public boolean isInternado() {
		return internado;
	}

	public void setInternado(boolean internado) {
		this.internado = internado;
	}

	public boolean isActor() {
		return tipoInterviniente != null && INTERVINIENTE_TIPO_ACTOR.equals(tipoInterviniente.getNumeroOrden()); 
	}

	public boolean isDemandado() {
		return tipoInterviniente != null && INTERVINIENTE_TIPO_DEMANDADO.equals(tipoInterviniente.getNumeroOrden()); 
	}
	
	public boolean isDenunciante() {
		return tipoInterviniente != null && INTERVINIENTE_DENUNCIANTE.equals(tipoInterviniente.getDescripcion()); 
	}
	
	public boolean isVictima() {
		return tipoInterviniente != null && INTERVINIENTE_VICTIMA.equals(tipoInterviniente.getDescripcion()); 
	}

	public Nacionalidad getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(Nacionalidad nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getApellidoMaterno() {
		return apellidoMaterno;
	}

	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}

	public Dependencia getDependencia() {
		return dependencia;
	}

	public void setDependencia(Dependencia dependencia) {
		this.dependencia = dependencia;
	}

	public CentroInternamiento getCentroInternamiento() {
		return centroInternamiento;
	}

	public void setCentroInternamiento(CentroInternamiento centroInternamiento) {
		this.centroInternamiento = centroInternamiento;
	}

	public Date getFechaDetencion() {
		return fechaDetencion;
	}

	public void setFechaDetencion(Date fechaDetencion) {
		this.fechaDetencion = fechaDetencion;
	}

	public String getNumeroDomicilio() {
		return numeroDomicilio;
	}

	public void setNumeroDomicilio(String numeroDomicilio) {
		this.numeroDomicilio = numeroDomicilio;
	}

	public String getDomicilioArresto() {
		return domicilioArresto;
	}

	public void setDomicilioArresto(String domicilioArresto) {
		this.domicilioArresto = domicilioArresto;
	}

	public String getNumeroDomicilioArresto() {
		return numeroDomicilioArresto;
	}

	public void setNumeroDomicilioArresto(String numeroDomicilioArresto) {
		this.numeroDomicilioArresto = numeroDomicilioArresto;
	}

	public boolean isArrestoDomiciliario() {
		return arrestoDomiciliario;
	}

	public void setArrestoDomiciliario(boolean arrestoDomiciliario) {
		this.arrestoDomiciliario = arrestoDomiciliario;
	}

	public boolean isFuncionario() {
		return funcionario;
	}

	public void setFuncionario(boolean funcionario) {
		this.funcionario = funcionario;
	}

	public IntervinienteExp getIntervinienteExp() {
		return intervinienteExp;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public Provincia getProvincia() {
		return provincia;
	}

	public void setProvincia(Provincia provincia) {
		this.provincia = provincia;
	}

	public String getCodPostal() {
		return codPostal;
	}

	public void setCodPostal(String codPostal) {
		this.codPostal = codPostal;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public boolean isDomicilioInvestigado() {
		return domicilioInvestigado;
	}

	public void setDomicilioInvestigado(boolean domicilioInvestigado) {
		this.domicilioInvestigado = domicilioInvestigado;
	}

	public String getLocalidadArresto() {
		return localidadArresto;
	}

	public void setLocalidadArresto(String localidadArresto) {
		this.localidadArresto = localidadArresto;
	}

	public Provincia getProvinciaArresto() {
		return provinciaArresto;
	}

	public void setProvinciaArresto(Provincia provinciaArresto) {
		this.provinciaArresto = provinciaArresto;
	}

	@Override
	public boolean needDelete() {
		if(isNew() || getIntervinienteExp() == null){
			return false;
		}else{
			return true;
		}
	}

	public boolean isTipoNN() {
		return TipoIntervinienteEnumeration.isNN(getTipoInterviniente());
	}
	
	public boolean isNombreNN() {
		return TipoIntervinienteEnumeration.NN_NOMBRE.equals(getApellidos());
	}

	public boolean isFrecuente() {
		return frecuente;
	}

	public void setFrecuente(boolean frecuente) {
		this.frecuente = frecuente;
	}
	
	public String getNombreCompleto() {
		if (isIdentidadReservada()){
			return IntervinienteExp.IDENTIDAD_RESERVADA;
		}
		if (!Strings.isEmpty(apellidos) || !Strings.isEmpty(nombre)) {
			StringBuilder builder = new StringBuilder();
			if (!Strings.isEmpty(apellidos)) {
				builder.append(apellidos.trim());
			}
			if (!Strings.isEmpty(apellidoMaterno)) {
				if (builder.length() > 0) {
					builder.append(" ");
				}
				builder.append(apellidoMaterno.trim());
			}
			if (!Strings.isEmpty(nombre)) {
				if (builder.length() > 0) {
					builder.append(" ");
				}
				builder.append(nombre.trim());
			}
			return builder.toString();
		} else {
			return "";
		}
	}

	public String getDomicilioConstituido() {
		return domicilioConstituido;
	}

	public void setDomicilioConstituido(String domicilioConstituido) {
		this.domicilioConstituido = domicilioConstituido;
	}

	public String getNumeroDomicilioConstituido() {
		return numeroDomicilioConstituido;
	}

	public void setNumeroDomicilioConstituido(String numeroDomicilioConstituido) {
		this.numeroDomicilioConstituido = numeroDomicilioConstituido;
	}

	public String getLocalidadConstituido() {
		return localidadConstituido;
	}

	public void setLocalidadConstituido(String localidadConstituido) {
		this.localidadConstituido = localidadConstituido;
	}

	public Provincia getProvinciaConstituido() {
		return provinciaConstituido;
	}

	public void setProvinciaConstituido(Provincia provinciaConstituido) {
		this.provinciaConstituido = provinciaConstituido;
	}

	public String getCodPostalConstituido() {
		return codPostalConstituido;
	}

	public void setCodPostalConstituido(String codPostalConstituido) {
		this.codPostalConstituido = codPostalConstituido;
	}

	public String getNumeroIGJ() {
		return numeroIGJ;
	}

	public void setNumeroIGJ(String numeroIGJ) {
		this.numeroIGJ = numeroIGJ;
	}

	public String getDetalleDomicilio() {
		return detalleDomicilio;
	}

	public void setDetalleDomicilio(String detalleDomicilio) {
		this.detalleDomicilio = detalleDomicilio;
	}

	public String getDetalleDomicilioConstituido() {
		return detalleDomicilioConstituido;
	}

	public void setDetalleDomicilioConstituido(String detalleDomicilioConstituido) {
		this.detalleDomicilioConstituido = detalleDomicilioConstituido;
	}

	public String getDetalleDomicilioArresto() {
		return detalleDomicilioArresto;
	}

	public void setDetalleDomicilioArresto(String detalleDomicilioArresto) {
		this.detalleDomicilioArresto = detalleDomicilioArresto;
	}

	public boolean isCopiarDomicilioDenunciado() {
		return copiarDomicilioDenunciado;
	}

	public void setCopiarDomicilioDenunciado(boolean copiarDomicilioDenunciado) {
		this.copiarDomicilioDenunciado = copiarDomicilioDenunciado;
	}
	
	
	public String getNoCaratula_PenditeVerificar_Label(){
		String ret = "";
		if(isNoSaleEnCaratula()){
			ret = "NS";
		}else if(getIntervinienteExp() != null && getIntervinienteExp().isPteVerificacion()){
				ret = "PV";			
		}
		return ret;
	}

	
	
	public String getActorODemandado() {
		return actorODemandado;
	}

	public void setActorODemandado(String actorODemandado) {
		this.actorODemandado = actorODemandado;
	}

	public String getTipoDocumentoCodigo() {
		return tipoDocumentoCodigo;
	}

	public void setTipoDocumentoCodigo(String tipoDocumentoCodigo) {
		this.tipoDocumentoCodigo = tipoDocumentoCodigo;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void onSelectSigla(){
		if(sigla != null){
			setApellidos(sigla.getDescripcion());
			setNombre(null);
			setFrecuente(sigla.isFrecuente());
			setPersonaJuridica(!sigla.isPersonaFisica());
			setDomicilio(sigla.getDomicilio());
			setNumeroDomicilio(sigla.getNumeroDomicilio());
			setDetalleDomicilio(sigla.getDetalleDomicilio());
			setLocalidad(sigla.getLocalidad());
			setProvincia(sigla.getProvincia());
			setCodPostal(sigla.getCodCodigoPostal());

			if (!Strings.isEmpty(sigla.getCuit())){
				setTipoDocumento(getTipoDocumentoCuit());
				setNumero(sigla.getCuit());
			} else {
				setTipoDocumento(null);
				setNumero(null);
			}
		}
	}

	public IntervinienteExp getIntervinienteExpOld() {
		return intervinienteExpOld;
	}

	public void setIntervinienteExpOld(IntervinienteExp intervinienteExpOld) {
		this.intervinienteExpOld = intervinienteExpOld;
	}
	
	/** ATOS COMERCIAL */
	public boolean isPresuntoFallido(){
		return tipoInterviniente != null && TipoIntervinienteEnumeration.INTERVINIENTE_STR_PRESUNTO_FALLIDO.equals(tipoInterviniente.getDescripcion());
	}
	/** ATOS COMERCIAL */
	
	public String getDomicilioCompleto() {
		if (!Strings.isEmpty(this.domicilio)) {
			StringBuilder builder = new StringBuilder();
			builder.append(domicilio);
			if (!Strings.isEmpty(this.numeroDomicilio)) {
				builder.append(" ").append(this.numeroDomicilio);
			}
			if (!Strings.isEmpty(this.detalleDomicilio)) {
				builder.append(" ").append(this.detalleDomicilio);
			}
			return builder.toString();
		}
		return null;
	}
	
	public void setIntervinienteExp(IntervinienteExp intervinienteExp){
		this.intervinienteExp = intervinienteExp;
	}

	public void copyFielsFromPoder(Poder instance) {
		EntityManager entityManager = ExpedienteHome.instance().getEntityManager();

		if (Strings.isEmpty(instance.getApellidos())){
			setApellidos(instance.getNombre());
		} else {
			setApellidos(instance.getApellidos());
		}
		setNombre(instance.getNombrePersona());
		setTipoDocumento(TipoDocumentoIdentidadSearch.findByCodigo(entityManager, instance.getCodTipoDocumento()));
		setNumero(instance.getNumeroDocumento());
		setDomicilio(instance.getCalle());
		setNumeroDomicilio(instance.getNumeroCalle());
		setDetalleDomicilio(instance.getDetalleDomicilio());
		setPersonaJuridica(instance.isPersonaJuridica());
		setNacionalidad(NacionalidadSearch.findByCodigo(entityManager, instance.getCodNacionalidad()));
		setSexo(instance.getSexo());
		setLocalidad(instance.getLocalidad());
		setProvincia(ProvinciaSearch.findByCodigo(entityManager, instance.getCodProvincia()));
		setCodPostal(instance.getCodCodigoPostal());
//		setFrecuente(instance.isFrecuente());
		setPoder(instance);
	}

	public void copyFielsFromDemandadoPoder(DemandadoPoder instance) {
		EntityManager entityManager = ExpedienteHome.instance().getEntityManager();

		if (Strings.isEmpty(instance.getApellidos())){
			setApellidos(instance.getNombre());
		} else {
			setApellidos(instance.getApellidos());
		}
		setNombre(instance.getNombrePersona());
//		setTipoDocumento(TipoDocumentoIdentidadSearch.findByCodigo(entityManager, instance.getCodTipoDocumento()));
//		setNumero(instance.getNumeroDocumento());
		setDomicilio(instance.getCalle());
		setNumeroDomicilio(instance.getNumeroCalle());
		setDetalleDomicilio(instance.getDetalleDomicilio());
		setPersonaJuridica(instance.isPersonaJuridica());
//		setNacionalidad(NacionalidadSearch.findByCodigo(entityManager, instance.getCodNacionalidad()));
		setSexo(instance.getSexo());
		setLocalidad(instance.getLocalidad());
		setProvincia(ProvinciaSearch.findByCodigo(entityManager, instance.getCodProvincia()));
		setCodPostal(instance.getCodCodigoPostal());
//		setFrecuente(instance.isFrecuente());
	}

	public void copyFielsFromTestigoPoder(TestigoPoder instance) {
		EntityManager entityManager = ExpedienteHome.instance().getEntityManager();

		if (Strings.isEmpty(instance.getApellidos())){
			setApellidos(instance.getNombre());
		} else {
			setApellidos(instance.getApellidos());
		}
		setNombre(instance.getNombrePersona());
		setTipoDocumento(TipoDocumentoIdentidadSearch.findByCodigo(entityManager, instance.getCodTipoDocumento()));
		setNumero(instance.getNumeroDocumento());
		setDomicilio(instance.getDireccion());
//		setNumeroDomicilio(instance.getNumeroCalle());
//		setDetalleDomicilio(instance.getDetalleDomicilio());
//		setPersonaJuridica(instance.isPersonaJuridica());
//		setNacionalidad(NacionalidadSearch.findByCodigo(entityManager, instance.getCodNacionalidad()));
//		setSexo(instance.getSexo());
//		setLocalidad(instance.getLocalidad());
//		setProvincia(ProvinciaSearch.findByCodigo(entityManager, instance.getCodProvincia()));
//		setCodPostal(instance.getCodCodigoPostal());
//		setFrecuente(instance.isFrecuente());
	}

	public Poder getPoder() {
		return poder;
	}

	public void setPoder(Poder poder) {
		this.poder = poder;
	}

	public String getIniciales() {
		return iniciales;
	}

	public void setIniciales(String iniciales) {
		this.iniciales = iniciales;
	}

	public String getNroExpAdm() {
		return nroExpAdm;
	}

	public void setNroExpAdm(String nroExpAdm) {
		this.nroExpAdm = nroExpAdm;
	}

	public String getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}

	public LeyAplicable getLeyAplicable() {
		return leyAplicable;
	}

	public void setLeyAplicable(LeyAplicable leyAplicable) {
		this.leyAplicable = leyAplicable;
	}

	public Integer getCodZona() {
		return codZona;
	}

	public void setCodZona(Integer codZona) {
		this.codZona = codZona;
	}

	public Integer getCodZonaConstituido() {
		return codZonaConstituido;
	}

	public void setCodZonaConstituido(Integer codZonaConstituido) {
		this.codZonaConstituido = codZonaConstituido;
	}
}
