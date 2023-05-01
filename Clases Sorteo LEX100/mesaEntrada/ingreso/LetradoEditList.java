package com.base100.lex100.mesaEntrada.ingreso;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.component.enumeration.TipoDocumentoLetradoEnumeration;
import com.base100.lex100.component.enumeration.VFLetradoEnumeration;
import com.base100.lex100.component.validator.CuilValidator;
import com.base100.lex100.component.validator.MatriculaValidator;
import com.base100.lex100.entity.AbogadoPoder;
import com.base100.lex100.entity.Letrado;
import com.base100.lex100.entity.LetradoIntervinienteExp;
import com.base100.lex100.entity.VFLetrado;

/**
 * Editing functionalities for a list of letradoIntervinienteExp 
 * @author fermin
 *
 */
public class LetradoEditList extends AbstractBeanEditList<LetradoEdit>{

	public static final String TIPO_LETRADO_PATROCINANTE = "P"; 
	public static final String TIPO_LETRADO_APODERADO = "A";  
	public static final String TIPO_LETRADO_DERECHO_PROPIO = "D";
	public static final String TIPO_LETRADO_GESTOR_FISCAL = "G";
	public static final String TIPO_LETRADO_DEFENSOR_OFICIAL = "DOF";
	public static final String TIPO_LETRADO_APODERADO_PARTIDO_POLITICO = "E";
	public static final String TIPO_LETRADO_ADHOC = "H";
	public static final String TIPO_LETRADO_VALIDADOS_AJENA_JURISDICCION = "V";
	/** ATOS NOTIFICACIONES */
	public static final String TIPO_LETRADO_DEFENSORIA = "DEF";
	/** ATOS NOTIFICACIONES */
    /**
	 * 
	 */
	private static final long serialVersionUID = -4105818850321044853L;
	private boolean permitirAltaLetrado;
	private boolean letradosDelDemandado = false;

	public LetradoEditList(boolean autoAddMode, boolean permitirAltaLetrado) {
		setAutoAddMode(autoAddMode);
		this.permitirAltaLetrado = permitirAltaLetrado;
    }

    public LetradoEditList(List<LetradoIntervinienteExp> list, boolean permitirAltaLetrado) {    	
		if(list != null){
			super.setList(createList(list));
		}
		this.permitirAltaLetrado = permitirAltaLetrado;
	}

	private static List<LetradoEdit> createList(List<LetradoIntervinienteExp> abogadoList) {
		ArrayList<LetradoEdit> list = new ArrayList<LetradoEdit>();
		for(LetradoIntervinienteExp abogado:abogadoList){
			list.add(new LetradoEdit(abogado));
		}
		return list;
	}

	@Override
	public void copyDataToTmp(LetradoEdit from, LetradoEdit tmp){
		tmp.setLetrado(from.getLetrado());
		tmp.setVfletrado(from.getVfletrado());
		tmp.setTomo(from.getTomo());
		tmp.setFolio(from.getFolio());
		tmp.setNombre(from.getNombre());
		tmp.setTipoDocumento(from.getTipoDocumento());
		tmp.setDocumento(from.getDocumento());
	}
	
	@Override
	public void copyDataFromTmp(LetradoEdit tmp, LetradoEdit dest) {
		Letrado letrado = tmp.getLetrado();
		dest.setLetrado(letrado);
		dest.setVfletrado(tmp.getVfletrado());
		if(letrado == null){
			dest.setTomo(tmp.getTomo());
			dest.setFolio(tmp.getFolio());
			dest.setNombre(tmp.getNombre());
			dest.setTipoDocumento(tmp.getTipoDocumento());
			dest.setDocumento(tmp.getDocumento());
		}
		dest.setTipoLetrado(tmp.getTipoLetrado());
	}
	
	
	@Override
	public void onEditLine(LetradoEdit entity) {
		super.onEditLine(entity);
		if(entity.isTomoYFolioDefined()){
			getTmpInstance().setVfletrado(searchVfLetradoByTomoYFolio(entity));
		}
	}

	
	@Override
	public void onAcceptLine() throws EntityOperationException {
		if (getTmpInstance() != null && getTmpInstance().getVfletrado() == null){
			VFLetradoEnumeration vfLetradoEnumeration = VFLetradoEnumeration.instance();
			getTmpInstance().setNombre(Strings.isEmpty(vfLetradoEnumeration.getUserInput()) ? null : vfLetradoEnumeration.getUserInput());
			vfLetradoEnumeration.setUserInput(null);
		}
		super.onAcceptLine();
	}

	@Override
	public void checkAccept() throws EntityOperationException{
		getTmpInstance().setError(false);
		checkVfletrado();
		checkDups(getTmpInstance(), getEditingInstance());	
		copyDataFromTmp(getTmpInstance(), getEditingInstance());
	}



	private void checkDups(LetradoEdit letradoToVerify, LetradoEdit excludeInstance) throws EntityOperationException {
		boolean same = false;
		for(LetradoEdit letradoEdit: getList()){
			if(!letradoEdit.isEmpty() && letradoEdit != excludeInstance){

				if ((letradoToVerify.getPoder() != null) && (letradoEdit.getPoder() != null)) {
					if ((letradoToVerify.getPoder().getNumero() != null) && !letradoToVerify.getPoder().getNumero().equals(letradoEdit.getPoder().getNumero())) {
						continue;
					}
					if ((letradoToVerify.getPoder().getAnio() != null) && !letradoToVerify.getPoder().getAnio().equals(letradoEdit.getPoder().getAnio())) {
						continue;
					}
				}

				if(isEqualLetradoPadron(letradoEdit, letradoToVerify)){
					same = true;
				}else{
					if(	equals(letradoEdit.getNombre(), letradoToVerify.getNombre())){
						boolean error = false; 
						
						if(Strings.isEmpty(letradoEdit.getDocumento()) && Strings.isEmpty(letradoToVerify.getDocumento())){
							error = true;
						} else if(equals(letradoEdit, letradoToVerify)){
							error = true;
						}
						
						if(error){
							String errField;
							errField = "nombre";
							letradoToVerify.addErrorField(errField);
							same = true;
						}
						
					}
					if(!same && equals(letradoEdit, letradoToVerify)){					
						String errKey = "abogado.warn.letradoDocDuplicado";
						warnMsg(errKey);
					}
				}			

			}
			if(same){
				String errKey = "abogado.error.letradoDuplicado";
				errorMsg(errKey);
				throw new EntityOperationException();
			}
		}
		
	}

	private boolean equals(LetradoEdit l1, LetradoEdit l2) {
		boolean equals = false;
		if(!Strings.isEmpty(l1.getDocumento()) && !Strings.isEmpty(l2.getDocumento())){
			equals = (equals(l1.getTipoDocumento(), l2.getTipoDocumento()) && equals(l1.getDocumento(), l2.getDocumento()));
		} 
		return equals;
	}

	private void checkVfletrado() throws EntityOperationException {
		checkTomoFolio();
		checkTipoDocumentoIdentidad();
		checkPermitirAltaLetrado();
	}
	
	
	private void checkPermitirAltaLetrado() throws EntityOperationException {
		String errKey = null;
		String errField = null;
		boolean isDefensorOficial =  TIPO_LETRADO_DEFENSOR_OFICIAL.equals(getTmpInstance().getTipoLetrado());

		if(isDefensorOficial || permitirAltaLetrado){
			if(getTmpInstance().getVfletrado() == null && !isEmpty(getTmpInstance()) && Strings.isEmpty(getTmpInstance().getNombre())){
				errKey = "abogado.error.nombreEmpty";				
				errField = "nombre";
			}else{
				//Edicion
				if(getTmpInstance().getLetrado() != null){
					getTmpInstance().getLetrado().setNombre(getTmpInstance().getNombre());
				}
			}
		}else{
			if(getTmpInstance().getVfletrado() == null){
				errKey = "abogado.error.letradoNoExistente";				
				errField = "nombre";
			}
		}
		if(errKey != null){
			errorMsg(errKey);
			getTmpInstance().addErrorField(errField);
			throw new EntityOperationException();
		}
	}
	

	private void checkTomoFolio() throws EntityOperationException {
		if(getTmpInstance().getVfletrado() == null){			
			String errKey = null;
			String errField = null;
			if(getTmpInstance().isTomoYFolioBadDefined()){
				errKey = "abogado.error.tomoFolioBadDefined";
				errField = "tomoFolio";
			}else if(getTmpInstance().isTomoYFolioDefined()){
				VFLetrado vfletrado = searchVfLetradoByTomoYFolio(getTmpInstance());
				if(vfletrado == null){
					errKey = "abogado.error.notExistTomoFolio";
					errField = "tomoFolio";
				}else{
					getTmpInstance().setVfletrado(vfletrado);
				}
			}
			if(errKey != null){
				errorMsg(errKey);
				getTmpInstance().addErrorField(errField);
				throw new EntityOperationException();
			}
		}
	}
	
	private Integer getAsInteger(String number) {
		Integer value = null;
		try {
			value = Integer.valueOf(number.trim());
		} catch (Exception e) {
		}
		return value;
	}
	
	
	private void checkTipoDocumentoIdentidad() throws EntityOperationException {
		String tipoDocumento = getTmpInstance().getTipoDocumento();
		String numero = getTmpInstance().getDocumento();
		String errKey = null;
		String errField = null;
		String errParam = null;
		boolean isDefensorOficial =  TIPO_LETRADO_DEFENSOR_OFICIAL.equals(getTmpInstance().getTipoLetrado());
			
		if(tipoDocumento != null){
			if(Strings.isEmpty(numero)){
				if(!isDefensorOficial){
					errKey = "abogado.error.numeroDocumentoNull";
					errField = "documento";
				}
			}else{
				if (TipoDocumentoLetradoEnumeration.isMatricula(tipoDocumento) && !MatriculaValidator.validateMatricula(numero)){ //&& vfletrado == null
					errKey = "abogado.error.matricula.format";
					errField = "documento";
				}else if (TipoDocumentoLetradoEnumeration.isCuitCuil(tipoDocumento) && !CuilValidator.validateCUIL(numero)){ //&& vfletrado == null
					try {
						numero = !numero.contains("-") ? numero.substring(0, 2) + "-" + numero.substring(2, 10) + "-" + numero.substring(10, numero.length()) : numero;
						Integer digito = CuilValidator.calculateDigitoControl(numero);
						if(digito != null){
							errKey = "abogado.error.cuitCuil.digito";
							errField = "documento";
							errParam = String.valueOf(digito); 
						}else{
							errKey = "abogado.error.cuitCuil.format";
							errField = "documento";
						}
					} catch (Exception e) {
						errKey = "abogado.error.cuitCuil.format";
						errField = "documento";
					}
				}
			}
		
		}else if(!Strings.isEmpty(numero) && !isDefensorOficial){
			errKey = "abogado.error.tipoDocumentoNull";
			errField = "tipoDocumento";
		}
		
		if(errKey != null){
			if(errParam == null){
				errorMsg(errKey);
			}else{
				errorMsg(errKey, errParam);
			}
			getTmpInstance().addErrorField(errField);
			throw new EntityOperationException();
		}
	}


	@Override
	public LetradoEdit createNewInstance() {
		VFLetradoEnumeration.instance().setSelected(null);
		return new LetradoEdit();
	}

	@Override
	public boolean isEmpty(LetradoEdit instance) {
		return instance.isEmpty();
	}

	
	public void clearVFletrado() {
		getTmpInstance().clearVFletrado();
	}
	
	public void searchVfLetrado() {
		LetradoEdit instance = getTmpInstance();
		if(instance.getVfletrado() == null){
			VFLetrado vfletrado = null;
			if(instance.isTomoYFolioDefined()){
				vfletrado = searchVfLetradoByTomoYFolio(instance);
				if(vfletrado == null){
					addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['abogado.error.notExistTomoFolio']}");
				}
			} else if (instance.isDocumentoDefined()){
				vfletrado = findVFLetradoByTipoDocumentoNumDocumento(instance.getTipoDocumento(), instance.getMatricula(), instance.getDocumento());
			}
			if(vfletrado != null){
				instance.setVfletrado(vfletrado);
			}
		}
	}
	
	private VFLetrado searchVfLetradoByTomoYFolio(LetradoEdit instance){
		return VFLetradoEnumeration.instance().findVFLetradoByTomoFolio(getAsInteger(instance.getTomo()), getAsInteger(instance.getFolio()));
	}

	public VFLetrado findVFLetradoByTipoDocumentoNumDocumento(String tipoDocumento, String matriculaStr, String documento){
		Long cuil = null;
		if (TipoDocumentoLetradoEnumeration.isCuitCuil(tipoDocumento) && (documento != null)) {
			try {
				cuil = Long.parseLong(documento);
			} catch (Exception e) {
			}
		}
		Integer matricula = null;
		if (matriculaStr != null) {
			try {
				matricula = Integer.parseInt(matriculaStr);
			} catch (Exception e) {
			}
		}
		return VFLetradoEnumeration.instance().findVFLetradoByTipoDocumentoNumDocumento(tipoDocumento, matricula, cuil);
	}

	public boolean isLetradosDelDemandado() {
		return letradosDelDemandado;
	}

	public void setLetradosDelDemandado(boolean letradosDelDemandado) {
		this.letradosDelDemandado = letradosDelDemandado;
	}
	
	
	
	private boolean isEqualLetradoPadron(LetradoEdit letradoEdit1, LetradoEdit letradoEdit2){
		boolean ret = false;
		if(isLetradoPadron(letradoEdit1) && isLetradoPadron(letradoEdit2)){
			if(letradoEdit1.getTomo().equals(letradoEdit2.getTomo())
					&& letradoEdit1.getFolio().equals(letradoEdit2.getFolio())){
				ret = true;
			}
		}
		return ret;
	}
	
	
	private boolean isLetradoPadron(LetradoEdit letradoEdit){
		return(letradoEdit != null && Strings.nullIfEmpty(letradoEdit.getTomo()) != null && Strings.nullIfEmpty(letradoEdit.getFolio()) != null);
	}
	
	public LetradoEdit findLetradoEdit(AbogadoPoder abogadoPoder) {
		for(LetradoEdit letradoEdit: getList()){
			if(!letradoEdit.isEmpty()){
				if (abogadoPoder.getPoder().equals(letradoEdit.getPoder())) {
					if(equals(letradoEdit.getNombre(), abogadoPoder.getNombreAbogado())) {
						return letradoEdit;
					}
					if((abogadoPoder.getTomoAbogado() != null) && (abogadoPoder.getFolioAbogado() != null) && letradoEdit.getTomo().equals(abogadoPoder.getTomoAbogado().toString())
							&& letradoEdit.getFolio().equals(abogadoPoder.getFolioAbogado().toString())){
						return letradoEdit;
					}
				}
			}
		}
		return null;
	}

}
