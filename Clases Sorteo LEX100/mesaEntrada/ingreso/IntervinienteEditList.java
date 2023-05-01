package com.base100.lex100.mesaEntrada.ingreso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.enumeration.SiglaEnumeration;
import com.base100.lex100.component.enumeration.TipoDocumentoIdentidadEnumeration;
import com.base100.lex100.component.enumeration.TipoIntervinienteEnumeration;
import com.base100.lex100.component.enumeration.UnidadDetencionEnumeration;
import com.base100.lex100.component.validator.CuilValidator;
import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.controller.entity.sigla.SiglaSearch;
import com.base100.lex100.entity.DemandadoPoder;
import com.base100.lex100.entity.IntervinienteExp;
import com.base100.lex100.entity.Nacionalidad;
import com.base100.lex100.entity.Poder;
import com.base100.lex100.entity.Provincia;
import com.base100.lex100.entity.Sigla;
import com.base100.lex100.entity.TipoDocumentoIdentidad;
import com.base100.lex100.entity.TipoInterviniente;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.configuracionMateria.ConfiguracionMateriaManager;

public class IntervinienteEditList extends AbstractBeanEditList<IntervinienteEdit> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2640686298441606273L;
	private IEditListController editListController;
	private boolean inAddExpedienteMode;

	
	private final int MODO_RAPIDO_MIN_LINES = 5;	
	private final int MODO_RAPIDO_MIN_EMPTY_LINES = 3;		
	private boolean modoRapido = false;
	private IntervinienteEdit instanceSelected;
	private boolean showSiglaDialog = false;
	private Sigla siglaMasiva;
	

	public boolean isInAddExpedienteMode() {
		return inAddExpedienteMode;
	}

	public void setInAddExpedienteMode(boolean inAddExpedienteMode) {
		this.inAddExpedienteMode = inAddExpedienteMode;
	}

	public IntervinienteEditList(boolean autoAddMode, IEditListController editListController) {
		setAutoAddMode(autoAddMode);
		this.editListController = editListController;
    }

	public IntervinienteEditList(List<IntervinienteExp> intervinienteExpList, IEditListController editListController) {
		this.editListController = editListController;
		if (intervinienteExpList != null) {
			super.setList(getList(intervinienteExpList));
		}
	}

	private static List<IntervinienteEdit> getList(List<IntervinienteExp> intervinienteExpList) {
		ArrayList<IntervinienteEdit> list = new ArrayList<IntervinienteEdit>();
		for(IntervinienteExp intervinienteExp:intervinienteExpList){
			if(!intervinienteExp.isLogicalDeleted()){
				list.add(new IntervinienteEdit(intervinienteExp));
			}
		}
		return list;
	}
	
//	public void onDupLine(IntervinienteEdit newEntity){
//		IntervinienteEdit ultimo = null;
//		for(IntervinienteEdit intervinienteEdit: getList()){
//			if(!intervinienteEdit.isEmpty()){
//				ultimo = intervinienteEdit;
//			}
//		}
//		if (ultimo != null) {
//			copyData(ultimo, newEntity);
//			newEntity.setNombre(null);
//			newEntity.setApellidos(null);
//			newEntity.setApellidoMaterno(null);
//			newEntity.setNumeroIGJ(null);
//			newEntity.setNumero(null);
//		}
//		
//		onEditLine(newEntity);
//	}

	public void duplicate(IntervinienteEdit from, IntervinienteEdit to) {
		 super.duplicate(from, to);
		 to.setNombre(null);
		 to.setApellidos(null);
		 to.setApellidoMaterno(null);
		 to.setIniciales(null);
		 to.setNumeroIGJ(null);
		 to.setNroExpAdm(null);
		 to.setBeneficiario(null);
		 to.setLeyAplicable(null);
		 to.setNumero(null);
	}

	@Override
	public IntervinienteEdit createNewInstance() {
		IntervinienteEdit instance = new IntervinienteEdit();
		
		if(editListController.isInputDomicilio()){
			instance.setLocalidad(createValueExpression("#{oficinaManager.localidadDefecto}",String.class).getValue());
			instance.setProvincia(createValueExpression("#{oficinaManager.provinciaDefecto}",Provincia.class).getValue());
			if(editListController.isUseDomicilioDenunciadoConstituido()){
				instance.setLocalidadConstituido(instance.getLocalidad());
				instance.setProvinciaConstituido(instance.getProvincia());
			}
		}
		
		if(!isModoRapido() || !CamaraManager.isCamaraActualLaboral()) {
			instance.setTipoDocumento(getDefaultTipoDocumento());
		}
		instance.setNacionalidad(getDefaultNacionalidad());
		instance.setActorODemandado("A");
		
		IntervinienteEdit last = getLastInstance();
		if((last != null) && (last.getTipoInterviniente() != null)){
			if(!isTipoNN(last) && ExpedienteMeAdd.instance().getTiposInterviniente().contains(last.getTipoInterviniente())){
				instance.setTipoInterviniente(last.getTipoInterviniente());
			} 
		}
		return instance;
	}

	private TipoDocumentoIdentidad getDefaultTipoDocumento() {
		String codigoTipoDocumento = ConfiguracionMateriaManager.instance().getTipoDocumentoIdentidadDefecto(SessionState.instance().getGlobalCamara());
		if (!Strings.isEmpty(codigoTipoDocumento)) {
			EntityManager entityManager = ExpedienteHome.instance().getEntityManager();
			List<TipoDocumentoIdentidad> tipoDocumentoIdentidadList =
			entityManager.createQuery("from TipoDocumentoIdentidad where codigo = :codigo and status <> -1 order by id")
					.setParameter("codigo", codigoTipoDocumento)
					.getResultList();
			if (tipoDocumentoIdentidadList.size() > 0) {
				return tipoDocumentoIdentidadList.get(0);
			}
		}
		return null;
	}

	private Nacionalidad getDefaultNacionalidad() {
		EntityManager entityManager = ExpedienteHome.instance().getEntityManager();
		List<Nacionalidad> nacionalidadList =
			entityManager.createQuery("from Nacionalidad where codigo = '1' and status <> -1 order by id").getResultList();
		if (nacionalidadList.size() > 0) {
			return nacionalidadList.get(0);
		}
		return null;
	}

	public void copyDomicilioDenunciado() {
		getTmpInstance().setDomicilioConstituido(getTmpInstance().getDomicilio());
		getTmpInstance().setNumeroDomicilioConstituido(getTmpInstance().getNumeroDomicilio());
		getTmpInstance().setLocalidadConstituido(getTmpInstance().getLocalidad());
		getTmpInstance().setProvinciaConstituido(getTmpInstance().getProvincia());
		getTmpInstance().setDetalleDomicilioConstituido(getTmpInstance().getDetalleDomicilio());
		getTmpInstance().setCodPostalConstituido(getTmpInstance().getCodPostal());
		getTmpInstance().setCodZonaConstituido(getTmpInstance().getCodZona());
	}

	public void copyData(IntervinienteEdit from, IntervinienteEdit to) {
		to.setInterviniente(from.getInterviniente());
		to.setControl(from.getControl());
		to.setNombre(from.getNombre());
		to.setApellidos(from.getApellidos());
		to.setApellidoMaterno(from.getApellidoMaterno());
		to.setIniciales(from.getIniciales());
		
		to.setNumeroIGJ(from.getNumeroIGJ());

		to.setNroExpAdm(from.getNroExpAdm());
		to.setBeneficiario(from.getBeneficiario());
		to.setLeyAplicable(from.getLeyAplicable());

		to.setDomicilio(from.getDomicilio());
		to.setLocalidad(from.getLocalidad());
		to.setProvincia(from.getProvincia());
		to.setNumeroDomicilio(from.getNumeroDomicilio());
		to.setDetalleDomicilio(from.getDetalleDomicilio());
		to.setCodPostal(from.getCodPostal());
		to.setCodZona(from.getCodZona());
		
		to.setDomicilioArresto(from.getDomicilioArresto());
		to.setNumeroDomicilioArresto(from.getNumeroDomicilioArresto());
		to.setLocalidadArresto(from.getLocalidadArresto());
		to.setProvinciaArresto(from.getProvinciaArresto());
		to.setDetalleDomicilioArresto(from.getDetalleDomicilioArresto());
		
		to.setDomicilioConstituido(from.getDomicilioConstituido());
		to.setNumeroDomicilioConstituido(from.getNumeroDomicilioConstituido());
		to.setLocalidadConstituido(from.getLocalidadConstituido());
		to.setProvinciaConstituido(from.getProvinciaConstituido());
		to.setDetalleDomicilioConstituido(from.getDetalleDomicilioConstituido());
		to.setCodPostalConstituido(from.getCodPostalConstituido());
		to.setCodZonaConstituido(from.getCodZonaConstituido());

		//ATOS COMERCIAL
		if (CamaraManager.isCamaraActualCOM() && from != null && from.getTipoDocumento() != null && TipoDocumentoIdentidadEnumeration.NO_CONSTA.equals(from.getTipoDocumento().getCodigo())) {
			to.setTipoDocumento(from.getTipoDocumento());
			to.setNumero(from.getNumero());
			to.setNacionalidad(from.getNacionalidad());
		//ATOS COMERCIAL
		}else{
			if (!Strings.isEmpty(from.getNumero()) || from.equals(getTmpInstance())) {
				to.setTipoDocumento(from.getTipoDocumento());
				to.setNumero(from.getNumero());
				to.setNacionalidad(from.getNacionalidad());
			}
		}
		
		to.setPersonaJuridica(from.isPersonaJuridica());
		to.setSexo(from.getSexo());
		to.setTipoInterviniente(from.getTipoInterviniente());
		to.setIdentidadReservada(from.isIdentidadReservada());
		to.setMenor(from.isMenor());
		to.setDetenido(from.isDetenido());
		to.setInternado(from.isInternado());
		to.setFuncionario(from.isFuncionario());
		to.setDomicilioInvestigado(from.isDomicilioInvestigado());
		to.setArrestoDomiciliario(from.isArrestoDomiciliario());
		to.setDependencia(from.getDependencia());
		to.setCentroInternamiento(from.getCentroInternamiento());
		to.setFechaDetencion(from.getFechaDetencion());
		to.setNew(from.isNew());
		to.setFrecuente(from.isFrecuente());
		to.setSigla(from.getSigla());

	}

	@Override
	public void copyDataToTmp(IntervinienteEdit from, IntervinienteEdit tmp) {
		copyData(from, tmp);
		UnidadDetencionEnumeration unidadDetencionEnumeration = (UnidadDetencionEnumeration) getComponentInstance(UnidadDetencionEnumeration.class);
		unidadDetencionEnumeration.setSelected(from.getDependencia());
	}

	@Override
	public void copyDataFromTmp(IntervinienteEdit tmp, IntervinienteEdit dest) {
		copyData(tmp, dest);
	}

	@Override
	public void checkAccept() throws EntityOperationException {
		SiglaEnumeration.instance().reset();
		checkInterviniente(getTmpInstance());
		checkDups(getTmpInstance(), getEditingInstance());	
		checkDupsDocumentLetrado(getTmpInstance());	
		copyDataFromTmp(getTmpInstance(), getEditingInstance());
		desdoblarPersonaJuridica(getEditingInstance());
	}


	public void checkAccept(IntervinienteEdit instance) throws EntityOperationException {
		SiglaEnumeration.instance().reset();
		checkInterviniente(instance);
		checkDups(instance, instance);	
		checkDupsDocumentLetrado(instance);	
		desdoblarPersonaJuridica(instance);
	}

	
	
	public void desdoblarPersonaJuridica(IntervinienteEdit interviniente) {
		if(!ExpedienteMeAdd.instance().isEditMode() && ConfiguracionMateriaManager.instance().isDesdoblarIntervinientePersonaJuridica(SessionState.instance().getGlobalCamara())){
			Sigla sigla = interviniente.getSigla();
			if(sigla != null){
				if(!chekDupSigla(sigla.getNombreAlternativo())){
					IntervinienteEdit newInstance = createNewInstance();
					duplicate(interviniente, newInstance);
					newInstance.setApellidos(sigla.getNombreAlternativo());
					newInstance.setNoSaleEnCaratula(true);
					newInstance.setPersonaJuridica(interviniente.isPersonaJuridica());
					newInstance.setTipoInterviniente(interviniente.getTipoInterviniente());
					addInstanceToList(newInstance, getList().indexOf(interviniente)+1);
				}						
			}	
		}
	}
	
	private boolean chekDupSigla(String nombre){
		boolean ret = false;
		for(IntervinienteEdit intervinienteEdit: getList()){
			if(!intervinienteEdit.isEmpty()){
				if(intervinienteEdit.getApellidos().equals(nombre)){
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	
	
	private void checkDupsDocumentLetrado( IntervinienteEdit intervinienteToVerify) throws EntityOperationException {
		boolean error = false; 
		if(intervinienteToVerify.getTipoDocumento() != null &&
				TipoDocumentoIdentidadEnumeration.isCuitCuil(intervinienteToVerify.getTipoDocumento()) ){
			
			for ( LetradoEdit letradoEdit: ExpedienteMeAdd.instance().getLetradoEditList().getList()){
				if (!LetradoEditList.TIPO_LETRADO_DERECHO_PROPIO.equals(letradoEdit.getTipoLetrado())) {
					if(!Strings.isEmpty(letradoEdit.getDocumento()) && letradoEdit.getDocumento().equals(intervinienteToVerify.getNumero())){
						error=true;
					}
				}
			}
			if (error){
				String errField;
				errField = "documento";
				intervinienteToVerify.addErrorField(errField);
				String errKey = "abogado.error.interviniente.documento.letrado";
				errorMsg(errKey);
				throw new EntityOperationException();
			}
		}
	}
	
	private void checkDups(IntervinienteEdit intervinienteToVerify, IntervinienteEdit excludeInstance) throws EntityOperationException {
		boolean same = false;
		for(IntervinienteEdit intervinienteEdit: getList()){
			if(!intervinienteEdit.isEmpty() && intervinienteEdit != excludeInstance){
				// En civil, si se marca no sale en caratula no se chequea como duplicado
				if(!(intervinienteToVerify.isNoSaleEnCaratula() && ExpedienteMeAdd.instance().isCamaraCivilNacional())){
					if(!Strings.isEmpty(intervinienteToVerify.getNumeroIGJ())){
						if(equalsIGJ(intervinienteEdit, intervinienteToVerify)){
							String errField;
							errField = "NumeroIGJ";
							intervinienteToVerify.addErrorField(errField);
							String errKey = "interviniente.numeroIGJ.duplicado";
							errorMsg(errKey);
							throw new EntityOperationException();
							
						}
					}
					if(!isNombreNN(intervinienteToVerify)){
						if(	equals(intervinienteEdit.getNombre(), intervinienteToVerify.getNombre()) &&
								equals(intervinienteEdit.getApellidos(), intervinienteToVerify.getApellidos())){
							boolean error = false; 
							
							if(Strings.isEmpty(intervinienteEdit.getNumero()) && Strings.isEmpty(intervinienteToVerify.getNumero())){
								error = true;
							} else if(equals(intervinienteEdit, intervinienteToVerify)){
								error = true;
							}
							
							if(error){
								String errField;
								errField = "nombre";
								intervinienteToVerify.addErrorField(errField);
								errField = "apellidos";
								intervinienteToVerify.addErrorField(errField);
								same = true;
							}
							
						}else if(equals(intervinienteEdit, intervinienteToVerify)){					
							String errKey = "actorDemandado.warm.actorDemandadoDocDuplicado";
							errorMsg(errKey);
							throw new EntityOperationException();
						}
					}else{
						same = false;
					}
				}
			}
			if(same){
				String errKey = "actorDemandado.error.actorDemandadoDuplicado";
				errorMsg(errKey);
				throw new EntityOperationException();
			}
		}
		
	}
	private boolean equals(IntervinienteEdit i1, IntervinienteEdit i2) {
		boolean equals = false;
		if(!Strings.isEmpty(i1.getNumero()) && !Strings.isEmpty(i2.getNumero())){
			equals = (equals(i1.getTipoDocumento(), i2.getTipoDocumento()) && equals(i1.getNumero(), i2.getNumero()));
		} 
		if (!equals) {
			String dni1 = getDNI(i1);
			String dni2 = getDNI(i2);
			if ((dni1 != null) && (dni2 != null)) {
				return equals(dni1, dni2);
			}
		}
		return equals;
	}
	
	private boolean equalsIGJ(IntervinienteEdit i1, IntervinienteEdit i2) {
		boolean equals = false;
		String v1 = i1.getNumeroIGJ();
		String v2 = i2.getNumeroIGJ();
		if(v1 == v2 || (v1 != null && v2 != null && v1.equals(v2))){
			equals = true;
		} 
		return equals;
	}
	
	
	public static String getDNI(IntervinienteEdit interviniente) {
		if((interviniente.getTipoDocumento() != null) && !Strings.isEmpty(interviniente.getNumero())) {
			if(TipoDocumentoIdentidadEnumeration.isCuitCuil(interviniente.getTipoDocumento())) {
				return CuilValidator.extractDni(interviniente.getNumero());
			} else if(TipoDocumentoIdentidadEnumeration.isDni(interviniente.getTipoDocumento())) {
				return interviniente.getNumero();
			}
		}
		return null;
	}


	private boolean equals(TipoDocumentoIdentidad t1, TipoDocumentoIdentidad t2) {		
		return (t1 == null || t2 == null) ? t1 == null && t2 == null : t1.getId() == t2.getId();
	}

	public void checkInterviniente(IntervinienteEdit intervinienteEdit) throws EntityOperationException {
		intervinienteEdit.setError(false);	
		if(intervinienteEdit.getInterviniente() == null && !isEmpty(intervinienteEdit)){	
			boolean error = false;
			String errKey = null;
			String errField = null;
			

			if(isTipoNN(intervinienteEdit)){
				if(Strings.isEmpty(intervinienteEdit.getApellidos())){
					intervinienteEdit.setApellidos(TipoIntervinienteEnumeration.NN_NOMBRE);
					intervinienteEdit.setNombre(null);
					intervinienteEdit.setFrecuente(true);
				}
			}else if(isNombreNN(intervinienteEdit)){
				intervinienteEdit.setTipoInterviniente(((TipoIntervinienteEnumeration)Component.getInstance(TipoIntervinienteEnumeration.class, true)).getNNItem());
				intervinienteEdit.setFrecuente(true);
			}
			
			if(!Strings.isEmpty(intervinienteEdit.getApellidos()) && Strings.isEmpty(intervinienteEdit.getNombre())){
				Sigla sigla = null;
				if (!ExpedienteMeAdd.instance().isUsaIntervinientesFrecuentes()) {	// las camaras que no tiene la busqueda especifica por siglas, buscan en la tabla de siglas por el apellido
					sigla = SiglaSearch.findByCodigo(ExpedienteHome.instance().getEntityManager(), intervinienteEdit.getApellidos());
				}
				if (sigla != null) {
					intervinienteEdit.setApellidos(sigla.getDescripcion());
					intervinienteEdit.setNombre(null);
					intervinienteEdit.setFrecuente(sigla.isFrecuente());
					intervinienteEdit.setPersonaJuridica(!sigla.isPersonaFisica());
					if (!Strings.isEmpty(sigla.getCuit())){
						intervinienteEdit.setTipoDocumento(intervinienteEdit.getTipoDocumentoCuit());
						intervinienteEdit.setNumero(sigla.getCuit());
					}
					intervinienteEdit.setDomicilio(sigla.getDomicilio());
					intervinienteEdit.setNumeroDomicilio(sigla.getNumeroDomicilio());
					intervinienteEdit.setDetalleDomicilio(sigla.getDetalleDomicilio());
					
					if (!Strings.isEmpty(sigla.getLocalidad())) {
						intervinienteEdit.setLocalidad(sigla.getLocalidad());
					}
					if (sigla.getProvincia() != null) {
						intervinienteEdit.setProvincia(createValueExpression("#{oficinaManager.provinciaDefecto}",Provincia.class).getValue());
					}
					if (sigla.getCodCodigoPostal() != null) {
						intervinienteEdit.setCodPostal(sigla.getCodCodigoPostal());
					}

				} else if (isValidacionExhaustiva(intervinienteEdit) && !isPersonaJuridica(intervinienteEdit) && !isTipoNN(intervinienteEdit)) {
					errKey = "actorDemandado.error.nombreEmpty";				
					errorMsg(errKey);
					errField = "nombre";
					intervinienteEdit.addErrorField(errField);
					error = true;
				}
			}
			
			if(intervinienteEdit.isArrestoDomiciliario()){
				intervinienteEdit.setDetenido(false);				
			}
			if(Strings.isEmpty(intervinienteEdit.getApellidos())){
				errKey = "actorDemandado.error.nombreEmpty";				
				errorMsg(errKey);
				errField = "nombre";
				intervinienteEdit.addErrorField(errField);
				errField = "apellidos";
				intervinienteEdit.addErrorField(errField);
				error = true;
			}
			if((isValidacionExhaustiva(intervinienteEdit, true)) && Strings.isEmpty(intervinienteEdit.getSexo()) && !isTipoNN(intervinienteEdit) && !isPersonaJuridica(intervinienteEdit)){
				errKey = "actorDemandado.error.sexoEmpty";				
				errField = "sexo";
				errorMsg(errKey);	
				intervinienteEdit.addErrorField(errField);
				error = true;
			} 
			if(intervinienteEdit.getTipoInterviniente() == null){
				errKey = "actorDemandado.error.tipoIntervinienteEmpty";				
				errField = "tipoInterviniente";
				errorMsg(errKey);
				intervinienteEdit.addErrorField(errField);
				error = true;
			}else if(!isDemandado(intervinienteEdit)){
				if(intervinienteEdit.isInternado()){
					errKey = "actorDemandado.error.internadoDebeSerDemandado";				
					errorMsg(errKey);
					errField = "internado";
					intervinienteEdit.addErrorField(errField);
					error = true;										
				}
				if(intervinienteEdit.isDetenido() || intervinienteEdit.isArrestoDomiciliario()){
					errKey = "actorDemandado.error.detenidoDebeSerDemandado";				
					errorMsg(errKey);
					errField = "detenido";
					intervinienteEdit.addErrorField(errField);
					error = true;															
				}
			}
			if(intervinienteEdit.isInternado() && !intervinienteEdit.isMenor()){
				intervinienteEdit.setMenor(true);
			}
			if(intervinienteEdit.isDetenido() && intervinienteEdit.isMenor()){
				errKey = "actorDemandado.error.detenidoNoPuedeSerMenor";				
				errorMsg(errKey);
				errField = "menor";
				intervinienteEdit.addErrorField(errField);
				errField = "detenido";
				intervinienteEdit.addErrorField(errField);
				error = true;					
			}
			if(intervinienteEdit.isNew() && intervinienteEdit.isDetenido() && intervinienteEdit.getDependencia() == null){
				errKey = "actorDemandado.error.dependenciaEmpty";	
				errorMsg(errKey);
				errField = "dependencia";
				intervinienteEdit.addErrorField(errField);
				error = true;									
			}
			if(intervinienteEdit.isNew() &&intervinienteEdit.isInternado() && intervinienteEdit.getCentroInternamiento() == null){
				errKey = "actorDemandado.error.centroInternamientoEmpty";
				errorMsg(errKey);
				errField = "centroInternamiento";
				intervinienteEdit.addErrorField(errField);
				error = true;									
			}
			if(intervinienteEdit.isNew() && intervinienteEdit.isArrestoDomiciliario() && (Strings.isEmpty(intervinienteEdit.getDomicilioArresto()) || !validDatosDomicilioArresto(intervinienteEdit))){
				errKey = "actorDemandado.error.domicilioArrestoIncomplete";				
				errorMsg(errKey);
				errField = "domicilioArresto";
				intervinienteEdit.addErrorField(errField);
				errField = "numeroDomicilioArresto";
				intervinienteEdit.addErrorField(errField);
				errField = "localidadArresto";
				intervinienteEdit.addErrorField(errField);
				errField = "provinciaArresto";
				intervinienteEdit.addErrorField(errField);
				error = true;
			}
			
			if(intervinienteEdit.isNew() &&intervinienteEdit.isArrestoDomiciliario() || intervinienteEdit.isDetenido() || intervinienteEdit.isInternado()){
				if(intervinienteEdit.getFechaDetencion() == null){
					errKey = "actorDemandado.error.fechaDetencionEmpty";
					errorMsg(errKey);
					errField = "fechaDentencion";
					intervinienteEdit.addErrorField(errField);
					error = true;															
				}else if(intervinienteEdit.getFechaDetencion().after(new Date())){
					errKey = "actorDemandado.error.fechaDetencion";
					errorMsg(errKey);
					errField = "fechaDentencion";
					intervinienteEdit.addErrorField(errField);
					error = true;			 												
				}
			}
			if(!checkTipoDocumentoIdentidad(intervinienteEdit)){
				error = true;
			}
			
			if(isValidacionExhaustiva(intervinienteEdit) && !validDatosDomicilio(intervinienteEdit)){
				errKey = "actorDemandado.error.domicilioIncomplete";				
				errorMsg(errKey);
				errField = "domicilio";
				intervinienteEdit.addErrorField(errField);
				errField = "numeroDomicilio";
				intervinienteEdit.addErrorField(errField);
				errField = "localidad";
				intervinienteEdit.addErrorField(errField);
				errField = "provincia";
				intervinienteEdit.addErrorField(errField);
				error = true;
			} else {
				if(intervinienteEdit.isCopiarDomicilioDenunciado()) {
					copyDomicilioDenunciado();
				}
				if(isValidacionExhaustiva(intervinienteEdit) && intervinienteEdit.isNew() && ExpedienteMeAdd.instance().isUseDomicilioDenunciadoConstituido()) {
					if(!validDatosDomicilioConstituido(intervinienteEdit)){
						errKey = "actorDemandado.error.domicilioIncomplete";				
						errorMsg(errKey);
						errField = "domicilioConstituido";
						intervinienteEdit.addErrorField(errField);
						errField = "numeroDomicilioConstituido";
						intervinienteEdit.addErrorField(errField);
						errField = "localidadConstituido";
						intervinienteEdit.addErrorField(errField);
						errField = "provinciaConstituido";
						intervinienteEdit.addErrorField(errField);
						error = true;
					}
				}
			}
			if(isValidacionExhaustiva(intervinienteEdit) && ExpedienteMeAdd.instance().isUseApellidoMaterno() && !intervinienteEdit.isPersonaJuridica() &&  intervinienteEdit.isActor() && Strings.isEmpty(intervinienteEdit.getApellidoMaterno())){
				errKey = "actorDemandado.error.apellidoMaterno";
				errorMsg(errKey);
				errField = "apellidoMaterno";
				intervinienteEdit.addErrorField(errField);
				error = true;			 												
			}
			if (isValidacionExhaustiva(intervinienteEdit) && ExpedienteMeAdd.instance().isUseNumeroIGJ() && isActor(intervinienteEdit) &&  intervinienteEdit.isPersonaJuridica() && Strings.isEmpty(intervinienteEdit.getNumeroIGJ())) {
				errKey = "actorDemandado.error.numeroIGJObligatorio";
				errorMsg(errKey);
				errField = "NumeroIGJ";
				intervinienteEdit.addErrorField(errField);
				error = true;			 												
			}

			if (intervinienteEdit.isMostrarIniciales() && Strings.isEmpty(intervinienteEdit.getIniciales())) {
				errKey = "actorDemandado.error.iniciales";
				errorMsg(errKey);
				errField = "iniciales";
				intervinienteEdit.addErrorField(errField);
				error = true;
			}
			if(isValidacionExhaustiva(intervinienteEdit) && isActor(intervinienteEdit) && ExpedienteMeAdd.instance().isRequiredNumeroExpedienteAdministrativo() && Strings.isEmpty(intervinienteEdit.getNroExpAdm())) {
				errKey = "actor.error.nroExpAdm";
				errorMsg(errKey);
				errField = "nroExpAdm";
				intervinienteEdit.addErrorField(errField);
				error = true;
			}
			if(isValidacionExhaustiva(intervinienteEdit) && isActor(intervinienteEdit) && ExpedienteMeAdd.instance().isRequiredNumeroBeneficio() && Strings.isEmpty(intervinienteEdit.getBeneficiario())) {
				errKey = "actor.error.beneficiario";
				errorMsg(errKey);
				errField = "beneficiario";
				intervinienteEdit.addErrorField(errField);
				error = true;
			}
			if(isValidacionExhaustiva(intervinienteEdit) && isActor(intervinienteEdit) && ExpedienteMeAdd.instance().isRequiredLeyAplicable() && (intervinienteEdit.getLeyAplicable() == null)) {
				errKey = "actor.error.leyAplicable";
				errorMsg(errKey);
				errField = "leyAplicable";
				intervinienteEdit.addErrorField(errField);
				error = true;
			}
			
			if(error){
				throw new EntityOperationException();
			}
		}		

	}
	private boolean isRelajarValidacion() {
		return this.editListController != null && this.editListController.isRelajarValidacion();
	}
	
	private boolean isValidacionExhaustiva (IntervinienteEdit intervinienteEdit) {
		return isValidacionExhaustiva (intervinienteEdit, false); 
	}
	
	private boolean isValidacionExhaustiva (IntervinienteEdit intervinienteEdit, boolean validacionSexo) {
		boolean ret = true;
		if(isRelajarValidacion() || intervinienteEdit.isNoSaleEnCaratula()){
			ret = false;
		}else{
			if(ExpedienteMeAdd.instance().isEditMode()){
				Date fechaMinimaValidacion = ConfiguracionMateriaManager.instance().getFechaInicialValidacionIntervinientes(SessionState.instance().getGlobalCamara());
				Date fechaIngresoExpediente = ExpedienteMeAdd.instance().getFechaIngresoExpediente();
				if(fechaMinimaValidacion != null && fechaIngresoExpediente != null && fechaIngresoExpediente.before(fechaMinimaValidacion)){
					ret = false;
				}
			}
		}
		return ret; 
	}

	private boolean validDatosDomicilio(IntervinienteEdit intervinienteEdit) {
		boolean ret = true;
		int num = 0;
		if(!Strings.isEmpty(intervinienteEdit.getDomicilio())){
			if(Strings.isEmpty(intervinienteEdit.getNumeroDomicilio())){
				num++;
			}
			if(Strings.isEmpty(intervinienteEdit.getLocalidad())){
				num++;
			}
			if(intervinienteEdit.getProvincia() == null){
				num++;
			}
		} 
		
		if(((num > 0) && (num < 3)) || (ExpedienteMeAdd.instance().isDomicilioRequerido(intervinienteEdit) && Strings.isEmpty(intervinienteEdit.getDomicilio()))){
			ret = false;
		}
		return ret;
	}

	private boolean validDatosDomicilioConstituido(IntervinienteEdit intervinienteEdit) {
		boolean ret = true;
		int num = 0;
		if(!Strings.isEmpty(intervinienteEdit.getDomicilioConstituido())){
			if(Strings.isEmpty(intervinienteEdit.getNumeroDomicilioConstituido())){
				num++;
			}
			if(Strings.isEmpty(intervinienteEdit.getLocalidadConstituido())){
				num++;
			}
			if(intervinienteEdit.getProvinciaConstituido() == null){
				num++;
			}
		}
		
		if(((num > 0) && (num < 4)) || (ExpedienteMeAdd.instance().isDomicilioRequerido(intervinienteEdit)  && Strings.isEmpty(intervinienteEdit.getDomicilioConstituido()))){
			ret = false;
		}
		return ret;
	}
	
	private boolean validDatosDomicilioArresto(IntervinienteEdit intervinienteEdit) {
		boolean ret = true;
		int num = 0;
		if(Strings.isEmpty(intervinienteEdit.getDomicilioArresto())){
			num++;
		}
		if(Strings.isEmpty(intervinienteEdit.getNumeroDomicilioArresto())){
			num++;
		}
		if(Strings.isEmpty(intervinienteEdit.getLocalidadArresto())){
			num++;
		}
		if(intervinienteEdit.getProvinciaArresto() == null){
			num++;
		}
	
		if(num > 0 && num < 4){
			ret = false;
		}
		return ret;
	}
	
	

	private boolean isTipoNN(IntervinienteEdit tmpInstance) {
		return tmpInstance.isTipoNN();
	}
	
	private boolean isNombreNN(IntervinienteEdit tmpInstance) {
		return tmpInstance.isNombreNN();
	}

	private boolean isPersonaJuridica(IntervinienteEdit tmpInstance) {
		return tmpInstance.isPersonaJuridica();
	}
	
	private boolean isDemandado(IntervinienteEdit intervinienteEdit) {
		return isDemandado(intervinienteEdit.getTipoInterviniente());
	}
	private boolean isDemandado(TipoInterviniente tipoInterviniente) {
		return tipoInterviniente.getNumeroOrden() != null && tipoInterviniente.getNumeroOrden().equals(TipoIntervinienteEnumeration.DEMANDADO_NUMERO_ORDEN);
	}
	
	private boolean isActor(IntervinienteEdit intervinienteEdit) {
		return isActor(intervinienteEdit.getTipoInterviniente());
	}
	
	private boolean isActor(TipoInterviniente tipoInterviniente) {
		return (tipoInterviniente != null) && tipoInterviniente.getNumeroOrden() != null && tipoInterviniente.getNumeroOrden().equals(TipoIntervinienteEnumeration.ACTOR_NUMERO_ORDEN);
	}

	private boolean checkTipoDocumentoIdentidad(IntervinienteEdit intervinienteEdit ) throws EntityOperationException {
		boolean error = false;
		String numero = intervinienteEdit.getNumero();
		TipoDocumentoIdentidad tipoDocumentoIdentidad = intervinienteEdit.getTipoDocumento();
		String errKey = null;
		String errParam = null;
		String errField = null;
		
		if(tipoDocumentoIdentidad != null && !Strings.isEmpty(numero)){
			if (TipoDocumentoIdentidadEnumeration.isCuitCuil(tipoDocumentoIdentidad) && !CuilValidator.validateCUIL(numero)){
				try {
					numero = !numero.contains("-") ? numero.substring(0, 2) + "-" + numero.substring(2, 10) + "-" + numero.substring(10, numero.length()) : numero;
					Integer digito = CuilValidator.calculateDigitoControl(numero);
					if(digito != null){
						errKey = "actorDemandado.error.cuitCuil.digito";
						errField = "documento";
						errParam = String.valueOf(digito); 
					}else{
						errKey = "actorDemandado.error.cuitCuil.format";
						errField = "documento";
					}
				} catch (Exception e) {
					errKey = "actorDemandado.error.cuitCuil.format";
					errField = "documento";
				}
			}else if (TipoDocumentoIdentidadEnumeration.isPasaporte(tipoDocumentoIdentidad) ) {
				if (!CuilValidator.hasDigitNot0(numero)) {
					errKey = "actorDemandado.error.documento.format";
					errField = "documento";
				} else if (intervinienteEdit.getNacionalidad() == null){
					errKey = "actorDemandado.error.nacionalidadEmpty";
				}
			} else if (TipoDocumentoIdentidadEnumeration.isDni(tipoDocumentoIdentidad) && !CuilValidator.validateDNI(numero)){
				errKey = "actorDemandado.error.dni.format";
				errField = "documento";
			} else if (!CuilValidator.hasDigitNot0(numero)) {
				errKey = "actorDemandado.error.documento.format";
				errField = "documento";
			}
		}else if(!Strings.isEmpty(numero)){
			errKey = "actorDemandado.error.tipoDocumentoNull";
			errField = "documento";
		} else if(ExpedienteMeAdd.instance().isDocumentoIdentidadRequerido(intervinienteEdit) && isValidacionExhaustiva(intervinienteEdit)) {
			errKey = "actorDemandado.error.numeroDocumentoNull";
			errField = "documento";
		}
		if(errKey != null){
			if(errParam == null){
				errorMsg(errKey);
			}else{
				errorMsg(errKey, errParam);
			}
			intervinienteEdit.addErrorField(errField);
			error = true;
		}
		
		return !error;
	}

	@Override
	public boolean isEmpty(IntervinienteEdit instance) {
		return instance.isEmpty();
	}

	public String rowStyle(IntervinienteEdit instance, String newStyle, String normalStyle, String reservedStyle){
		if(isNew(instance)){
			return newStyle;
		}else if(instance.isIdentidadReservada()){
			return reservedStyle;
		}else if(instance.isNoSaleEnCaratula()){
			return "rowColor1a";
		}else{
			return normalStyle;
		}
	}
	
	public String getFlagsAsText(IntervinienteEdit item){
		String ret = "";
		if(item.isMenor()){
			ret += "M";
		}
		if(item.isDetenido() || item.isArrestoDomiciliario()){
			ret += "D";
		}
		if(item.isInternado()){
			ret += "I";
		}
		return ret.length() > 0 ? " ("+ret+")" : "";
	}
	
	public List<IntervinienteEdit> getIntervinientesValidos() {
		List<IntervinienteEdit> intervinientes = new ArrayList<IntervinienteEdit>();
		for (IntervinienteEdit intervinienteEdit : getList()) {
			if(!intervinienteEdit.isEmpty()){
				intervinientes.add(intervinienteEdit);
			}
		}
		return intervinientes;
	}
	
	public void doSeleccionarTodosIntervinientes() {
		for (IntervinienteEdit intervinienteEdit : getList()) {
			if(!intervinienteEdit.isEmpty()){
				intervinienteEdit.setSelected(true);
			}
		}
	}

	public void doDeseleccionarTodosIntervinientes() {
		for (IntervinienteEdit intervinienteEdit : getList()) {
			if(!intervinienteEdit.isEmpty()){
				intervinienteEdit.setSelected(false);
			}
		}
	}
	
	public boolean isAnyIntervinienteSelected() {
		 for(IntervinienteEdit intervinienteEdit: getIntervinientesValidos() ) {
			 if (intervinienteEdit.isSelected()) {
				 return true;
			 }
		 }
		 return false;
	}
	
	public boolean isModoRapido() {
		return modoRapido;
	}

	private void setModoRapido(boolean modoRapido) {
		this.modoRapido = modoRapido;
	}

	/////
	
	public void cambiarModoRapido(boolean activar) {
		if(activar){
			if(!isModoRapido()){
				try {
					onAcceptLine();
				} catch (EntityOperationException e) {
					return;
				}								
				setModoRapido(true);
				cambiarIntervinientesAModoRapido();
				cancelEditing();
			}
			checkModoRapido();
		}else{
			if(cambiarIntervinientesAModoDetalle()){
				borrarIntervinientesEmpty();
				setModoRapido(false);
				cancelEditing();
				addNewInstanceToList();
				
			}
		}	
	}

	private void checkModoRapido(){
		int numLines = getList().size();
		int numEmpty = countEmptyLineIntervinientes();
		while(numLines < MODO_RAPIDO_MIN_LINES || numEmpty < MODO_RAPIDO_MIN_EMPTY_LINES){
			addNewInstanceToList();
			numEmpty++;
			numLines++;
		}		
	}
	
	private void setActorDemandadoFromTipoInterviniente(IntervinienteEdit intervinienteEdit){
		if(TipoIntervinienteEnumeration.isActor(intervinienteEdit.getTipoInterviniente())){
			intervinienteEdit.setActorODemandado("A");
		}else if(TipoIntervinienteEnumeration.isDemandado(intervinienteEdit.getTipoInterviniente())){
			intervinienteEdit.setActorODemandado("D");
		}
	}
	
	private void setTipoIntervinienteFromActorDemandado(IntervinienteEdit intervinienteEdit){
		if("D".equals(intervinienteEdit.getActorODemandado())){
			if(intervinienteEdit.getTipoInterviniente() == null || intervinienteEdit.getTipoInterviniente().getNumeroOrden() != 2){
				intervinienteEdit.setTipoInterviniente(getDefaultDemandado());
			}
		}else if("A".equals(intervinienteEdit.getActorODemandado())){
			if(intervinienteEdit.getTipoInterviniente() == null || intervinienteEdit.getTipoInterviniente().getNumeroOrden() != 1){
				intervinienteEdit.setTipoInterviniente(getDefaultActor());
			}			
		}
	}
	
	private TipoInterviniente getDefaultTipoInteviniente(Integer orden) {
		for(TipoInterviniente tipoInterviniente: editListController.getTiposInterviniente()){
			if(orden ==null || orden.equals(tipoInterviniente.getNumeroOrden())){
				return tipoInterviniente;
			}
		}
		return null;
	}
	
	private TipoInterviniente getDefaultDemandado() {
		return getDefaultTipoInteviniente(2);
	}

	private TipoInterviniente getDefaultActor() {
		return getDefaultTipoInteviniente(1);
	}

	private boolean cambiarIntervinientesAModoRapido() {
		boolean error = false;
		for(IntervinienteEdit intervinienteEdit: getList()){
			if(!intervinienteEdit.isEmpty()){
				setActorDemandadoFromTipoInterviniente(intervinienteEdit);
//				concatApellidoNombre(intervinienteEdit);
			} else if(CamaraManager.isCamaraActualLaboral()){
				intervinienteEdit.setTipoDocumento(null);
			}
		}
		return !error;
	}
	
	private boolean cambiarIntervinientesAModoDetalle() {
		boolean error = false;
		for(IntervinienteEdit intervinienteEdit: getList()){
			if(!intervinienteEdit.isEmpty()){
				setTipoIntervinienteFromActorDemandado(intervinienteEdit);
//				splitApellidoNombre(intervinienteEdit);
				
			}
		}
		return !error;
	}

//	private void concatApellidoNombre(IntervinienteEdit intervinienteEdit) {
//		if(!Strings.isEmpty(intervinienteEdit.getNombre()) && !Strings.isEmpty(intervinienteEdit.getApellidos())){
//			intervinienteEdit.setApellidos(intervinienteEdit.getApellidos()+", "+intervinienteEdit.getNombre());
//			intervinienteEdit.setNombre(null);
//		}
//	}

//	private void splitApellidoNombre(IntervinienteEdit intervinienteEdit) {
//		String nombre = intervinienteEdit.getNombre();
//		String apellidos = intervinienteEdit.getApellidos();
//		if(Strings.isEmpty(nombre) && !Strings.isEmpty(apellidos)){
//			int idx = apellidos.indexOf(',');
//			if(idx > 1){
//				intervinienteEdit.setApellidos(apellidos.substring(0, idx));
//				intervinienteEdit.setNombre(apellidos.substring(idx+1).trim());
//			}
//		}
//	}

	
	private void borrarIntervinientesEmpty() {
		for(int i=0; i < getList().size(); i++){
			if(getList().get(i).isEmpty()){
				getList().remove(i);
				i--;
			}
		}
		checkAutoAdd();
	}

	private int countEmptyLineIntervinientes() {
		int num = 0;
		for(IntervinienteEdit intervinienteEdit: getList()){
			if(intervinienteEdit.isEmpty()){
				num++;
			}
		}
		return num;
	}

	public boolean verify(boolean complete){
		boolean error = false;
		if(isModoRapido()){
			if(complete){
				cambiarModoRapido(false);
				if(!verifyEachLine()){	
					error = true;
				}
				cambiarModoRapido(true);
			}else{
				checkModoRapido();
			}
		}else{
			try {
				onAcceptLine();
			} catch (EntityOperationException e) {
				error = true;
			}
		}
		return !error;
	}

	private boolean verifyEachLine() {
		boolean error = false;
		for(IntervinienteEdit intervinienteEdit: getList()){
			if(!intervinienteEdit.isEmpty()){
				//onEditLine(intervinienteEdit);
				try {
					checkAccept(intervinienteEdit);
				} catch (EntityOperationException e) {
					error = true;
				}
				//cancelEditing();
			}
		}
		return !error;
	}

	

	public Sigla getSiglaMasiva() {
		return siglaMasiva;
	}

	public void setSiglaMasiva(Sigla siglaMasiva) {
		this.siglaMasiva = siglaMasiva;
	}

	public boolean isShowSiglaDialog() {
		return showSiglaDialog;
	}

	public void setShowSiglaDialog(boolean showSiglaDialog) {
		this.showSiglaDialog = showSiglaDialog;
	}
	
	public void doShowSiglaDialog(IntervinienteEdit intervinienteEdit){
		SiglaEnumeration.instance().reset();
		setSiglaMasiva(null);
		setShowSiglaDialog(true);
		setInstanceSelected(intervinienteEdit);
	}
	
	public void doAceptarSigla() throws EntityOperationException{
		setShowSiglaDialog(false);
		if(getSiglaMasiva() != null && getInstanceSelected() != null){
			getInstanceSelected().setSigla(getSiglaMasiva());
			getInstanceSelected().setApellidos(getSiglaMasiva().getDescripcion());
			getInstanceSelected().setPersonaJuridica(!getSiglaMasiva().isPersonaFisica());

			if (!Strings.isEmpty(getSiglaMasiva().getCuit())){
				getInstanceSelected().setTipoDocumento(getInstanceSelected().getTipoDocumentoCuit());
				getInstanceSelected().setNumero(getSiglaMasiva().getCuit());
			} else {
				getInstanceSelected().setTipoDocumento(null);
				getInstanceSelected().setNumero(null);
			}
			getInstanceSelected().setDomicilio(getSiglaMasiva().getDomicilio());
			getInstanceSelected().setNumeroDomicilio(getSiglaMasiva().getNumeroDomicilio());
			getInstanceSelected().setDetalleDomicilio(getSiglaMasiva().getDetalleDomicilio());
			getInstanceSelected().setLocalidad(getSiglaMasiva().getLocalidad());
			getInstanceSelected().setProvincia(getSiglaMasiva().getProvincia());
			getInstanceSelected().setCodPostal(getSiglaMasiva().getCodCodigoPostal());

			desdoblarPersonaJuridica(getInstanceSelected());
		}
	}
	
	public void doCancelarSigla(){
		setShowSiglaDialog(false);
		setInstanceSelected(null);
	}
	

	public IntervinienteEdit getInstanceSelected() {
		return instanceSelected;
	}

	public void setInstanceSelected(IntervinienteEdit instanceSelected) {
		this.instanceSelected = instanceSelected;
	}
	
	public IntervinienteEdit findIntervinienteEdit(Poder poder) {
		for(IntervinienteEdit intervinienteEdit: getList()){
			if(!intervinienteEdit.isEmpty()){
				if(	equals(intervinienteEdit.getNombre(), poder.getNombrePersona()) &&
						equals(intervinienteEdit.getApellidos(), poder.getApellidos())){
					return intervinienteEdit;
				}
				if(equals(intervinienteEdit.getNombreCompleto(), poder.getNombre())) {
					return intervinienteEdit;
				}
				if(!Strings.isEmpty(intervinienteEdit.getNumero()) ) {
//						equals(intervinienteEdit.getNumero(), poder.getNumeroDocumento())){
					String codigoTipoDocumento = (intervinienteEdit.getTipoDocumento() != null)? intervinienteEdit.getTipoDocumento().getCodigo(): null;
					if (TipoDocumentoIdentidadEnumeration.equals(codigoTipoDocumento, intervinienteEdit.getNumero(),  poder.getCodTipoDocumento(), poder.getNumeroDocumento())){
						return intervinienteEdit;
					}
				}
			}
		}
		return null;
	}

	public IntervinienteEdit findIntervinienteEdit(DemandadoPoder demandadoPoder) {
		for(IntervinienteEdit intervinienteEdit: getList()){
			if(!intervinienteEdit.isEmpty()){
				if(	equals(intervinienteEdit.getNombre(), demandadoPoder.getNombrePersona()) &&
						equals(intervinienteEdit.getApellidos(), demandadoPoder.getApellidos())){
					return intervinienteEdit;
				}
				if(equals(intervinienteEdit.getNombreCompleto(), demandadoPoder.getNombre())) {
					return intervinienteEdit;
				}
			}
		}
		return null;
	}
}
