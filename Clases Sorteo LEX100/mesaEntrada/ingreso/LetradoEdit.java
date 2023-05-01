package com.base100.lex100.mesaEntrada.ingreso;

import org.jboss.seam.util.Strings;

import com.base100.lex100.component.enumeration.AgenteFiscalEnumeration;
import com.base100.lex100.component.enumeration.VFLetradoEnumeration;
import com.base100.lex100.entity.AgenteFiscal;
import com.base100.lex100.entity.Letrado;
import com.base100.lex100.entity.LetradoIntervinienteExp;
import com.base100.lex100.entity.Poder;
import com.base100.lex100.entity.TipoLetrado;
import com.base100.lex100.entity.VFLetrado;

public class LetradoEdit extends AbstractEditPojo {
	private String tipoLetrado;
	private String tomo;
	private String folio;
	private String tipoDocumento;//cuil o matricula
	private String documento;	// cuil o matricula dependiendo de tipoDocumento
	private String nombre;
	private boolean inhabilitado;
	private Poder poder;
	private String cuil;	// tomado del poder
	private String email;  // tomado del poder

	public static final String TIPO_DOC_MATRICULA = "M";
	public static final String TIPO_DOC_CUIT_CUIL = "C";
	
	private static final String TIPO_DOC_MATRICULA_LABEL = "Matricula";
	private static final String TIPO_DOC_CUIT_CUIL_LABEL = "Cuit/Cuil";
	
	private LetradoIntervinienteExp letradoIntervinienteExp;
	private Letrado letrado;

	private VFLetrado vfletrado;
	private AgenteFiscal agenteFiscal;

	public LetradoEdit(){
    	setTipoDocumento(TIPO_DOC_CUIT_CUIL);
    }

	public LetradoEdit(LetradoIntervinienteExp letradoIntervinienteExp) {
		this.letradoIntervinienteExp = letradoIntervinienteExp;
		setLetrado(letradoIntervinienteExp.getLetrado());
		setTipoLetrado(letradoIntervinienteExp.getTipoLetrado());
		setPoder(letradoIntervinienteExp.getPoder());
		setNew(false);
		//tipoLetrado = letradoIntervinienteExp.getTipoLetrado();
	}

	private boolean isEmpty(String s){
		return Strings.isEmpty(s);
	}
		
	public boolean isTomoYFolioDefined(){
		return !isEmpty(getTomo()) && !isEmpty(getFolio());
	}

	public boolean isDocumentoDefined(){
		return !isEmpty(getDocumento());
	}

	public boolean isTomoYFolioBadDefined(){
		return isEmpty(getTomo()) ? !isEmpty(getFolio()) : isEmpty(getFolio());
	}
	
	public boolean isTomoYFolioBadDefined(Letrado letrado){
		return isEmpty(letrado.getTomo()) ? !isEmpty(letrado.getFolio()) : isEmpty(letrado.getFolio());
	}
	
	public boolean isTomoYFolioEmpty(){
		return isEmpty(getFolio()) && isEmpty(getFolio());
	}
	    
    
	public void setLetrado(Letrado letrado) {
		this.letrado = letrado;
		if(letrado == null){
			this.nombre = null;
			this.tomo = null;
			this.folio = null;
			this.tipoDocumento = null;
			this.documento = null;
		}else{
			this.nombre = letrado.getNombre();
			this.tomo = letrado.getTomo();
			this.folio = letrado.getFolio();
			this.tipoDocumento = calculateTipoDocumentoFromLetrado(letrado);
			this.documento = calculateDocumentoFromLetrado(letrado);			
		}
		setError(false);
	}

	public void setTipoLetrado(TipoLetrado tipoLetrado) {
		if(tipoLetrado == null){
			this.tipoLetrado = null;
		}else{
			this.tipoLetrado = tipoLetrado.getCodigo();
		}
		
		setError(false);
	}

	public void clearLetrado(){
		setLetrado(null);
	}
	
	public String getTipoLetrado() {
		return tipoLetrado;
	}

	public void setTipoLetrado(String tipoLetrado) {
		this.tipoLetrado = tipoLetrado;
		
		
	}

	public String getTomo() {
		return tomo;
	}

	public void setTomo(String tomo) {
		this.tomo = tomo;
	}




	public String getFolio() {
		return folio;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public boolean isInhabilitado(){
		return inhabilitado;
	}

	public Letrado getLetrado() {
		return letrado;
	}


//	public void copyDataTo(AbogadoEdit dest) {
//		Letrado letrado = getLetrado();
//		if(letrado == null){
//			letrado = new Letrado();
//			letrado.setTomo(getTomo());
//			letrado.setFolio(getFolio());
//			letrado.setMatricula(getMatricula());
//			letrado.setNombre(getNombre());
//		}		
//		dest.setLetrado(letrado);
//		//dest.setTipoLetrado(tipoLetrado);
//	}
//	
//	public void copyDataFrom(AbogadoEdit letradoIntervinienteExp) {
//		this.causa = letradoIntervinienteExp.getCausa();
//		setLetrado(letradoIntervinienteExp.getLetrado());
//		//tipoLetrado = letradoIntervinienteExp.getTipoLetrado();		
//	}

//	private void verifyNewLetrado(Letrado letrado, boolean showMsgError) throws EntityOperationException  {
//		boolean errTomoFolioMatricula = isTomoYFolioBadDefined(letrado);
//		if(errTomoFolioMatricula){
//			if(showMsgError){
//				errorMsg("letradoIntervinienteExp.error.errTomoFolio");
//			}
//			error = true;
//			throw new EntityOperationException();
//		}
//		if(isEmpty(letrado.getNombre())){
//			if(showMsgError){
//				errorFieldRequired("letradoIntervinienteExp.nombre");
//			}
//			error = true;
//			throw new EntityOperationException();			
//		}		
//	}
	
//	private Letrado addNewLetrado(Letrado letrado) throws EntityOperationException  {
//		verifyNewLetrado(letrado, true);
//		LetradoHome letradoHome = (LetradoHome)Component.getInstance(LetradoHome.class, true);
//		letradoHome.createNewInstance();
//		Letrado newLetrado = letradoHome.getInstance();
//		
//		newLetrado.setTomo(letrado.getTomo());
//		newLetrado.setFolio(letrado.getFolio());
//		newLetrado.setMatricula(letrado.getMatricula());
//		newLetrado.setNombre(letrado.getNombre());
//		letradoHome.doAdd();
//		return newLetrado;
//	}

	
	public LetradoIntervinienteExp getAbogado() {
		return letradoIntervinienteExp;
	}


	public boolean isEmpty() {
		return letradoIntervinienteExp == null && vfletrado == null && isEmpty(tomo) && isEmpty(folio) && isEmpty(nombre) && isEmpty(documento);
	}
	
	public void copyFielsTo(Letrado letrado) {
		letrado.setNombre(nombre);
		letrado.setTomo(tomo);
		letrado.setFolio(folio);			
		if(TIPO_DOC_MATRICULA.equals(tipoDocumento)){
			letrado.setMatricula(documento);
		}else{
			letrado.setCuitCuil(documento);
		}
	}

	public boolean isTipDocumentoMatricula(){
		return TIPO_DOC_MATRICULA.equals(tipoDocumento);
	}

	public boolean isTipDocumentoCuitCuil(){
		return TIPO_DOC_CUIT_CUIL.equals(tipoDocumento);
	}
	
	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String calculateTipoDocumentoFromLetrado(Letrado letrado){
		if(letrado != null){
			if(!Strings.isEmpty(letrado.getCuitCuil())){
				return TIPO_DOC_CUIT_CUIL; 
			}else if(!Strings.isEmpty(letrado.getMatricula())){
				return TIPO_DOC_MATRICULA;
			}
		}
		return null;
	}

	public String calculateDocumentoFromLetrado(Letrado letrado){
		String tipo = calculateTipoDocumentoFromLetrado(letrado);
		if(tipo != null){
			if(tipo.equals(TIPO_DOC_CUIT_CUIL)){
				return letrado.getCuitCuil(); 
			}else if(tipo.equals(TIPO_DOC_MATRICULA)){
				return letrado.getMatricula();
			}
		}
		return null;
	}
	
//	public String calculateTipoDocumentoFromVLetrado(VLetrado vletrado){
//		if(vletrado != null){
//			if(vletrado.getCuil() != null){
//				return TIPO_DOC_CUIT_CUIL; 
//			}else if(vletrado.getMatricula() != null){
//				return TIPO_DOC_MATRICULA;
//			}
//		}
//		return null;
//	}
//
//	public String calculateDocumentoFromVLetrado(VLetrado vletrado){
//		String tipo = calculateTipoDocumentoFromVLetrado(vletrado);
//		if(tipo != null){
//			if(tipo.equals(TIPO_DOC_CUIT_CUIL)){
//				return vletrado.getCuil().toString(); 
//			}else if(tipo.equals(TIPO_DOC_MATRICULA)){
//				return vletrado.getMatricula().toString();
//			}
//		}
//		return null;
//	}
//	
	public String calculateTipoDocumentoFromVFLetrado(VFLetrado vfletrado){
		if(vfletrado != null){
			if(vfletrado.getCuil() != null){
				return TIPO_DOC_CUIT_CUIL; 
			}else if(vfletrado.getMatricula() != null){
				return TIPO_DOC_MATRICULA;
			}
		}
		return null;
	}

	public String calculateDocumentoFromVFLetrado(VFLetrado vfletrado){
		String tipo = calculateTipoDocumentoFromVFLetrado(vfletrado);
		if(tipo != null){
			if(tipo.equals(TIPO_DOC_CUIT_CUIL)){
				return vfletrado.getCuil().toString(); 
			}else if(tipo.equals(TIPO_DOC_MATRICULA)){
				return vfletrado.getMatricula().toString();
			}
		}
		return null;
	}
	
	public String calculateTipoDocumentoFromAgenteFiscal(AgenteFiscal agenteFiscal){
		if(agenteFiscal != null){
			if(agenteFiscal.getNumeroDocId() != null){
				return TIPO_DOC_CUIT_CUIL; 
			}else if(agenteFiscal.getMatricula() != null){
				return TIPO_DOC_MATRICULA;
			}
		}
		return null;
	}

	public String calculateDocumentoFromAgenteFiscal(AgenteFiscal agenteFiscal){
		String tipo = calculateTipoDocumentoFromAgenteFiscal(agenteFiscal);
		if(tipo != null){
			if(tipo.equals(TIPO_DOC_CUIT_CUIL)){
				return agenteFiscal.getNumeroDocId().toString(); 
			}else if(tipo.equals(TIPO_DOC_MATRICULA)){
				return agenteFiscal.getMatricula().toString();
			}
		}
		return null;
	}
	
	public String getTipoDocumentoLabel(){
		if(tipoDocumento != null){
			if(tipoDocumento.equals(TIPO_DOC_CUIT_CUIL)){
				return TIPO_DOC_CUIT_CUIL_LABEL; 
			}else if(tipoDocumento.equals(TIPO_DOC_MATRICULA)){
				return TIPO_DOC_MATRICULA_LABEL;
			}
		}
		return null;
	}

	
	public VFLetrado getVfletrado() {
		return vfletrado;
	}

	public void setVfletrado(VFLetrado vfletrado) {
		this.vfletrado = vfletrado;
		if(vfletrado != null) {
			this.nombre = vfletrado.getNombreCompleto();
			this.tomo = vfletrado.getId().getTomo().toString();
			this.folio = vfletrado.getId().getFolio().toString();
			this.tipoDocumento = calculateTipoDocumentoFromVFLetrado(vfletrado);
			this.documento = calculateDocumentoFromVFLetrado(vfletrado);
			this.inhabilitado = vfletrado.isInhabilitado();
		}
	}

	public void clearVFletrado(){
		setVfletrado(null);
		VFLetradoEnumeration.instance().reset();
		this.nombre = null;
		this.tomo = null;
		this.folio = null;
		this.tipoDocumento = null;
		this.documento = null;
		this.inhabilitado = false;
	}
	
	public AgenteFiscal getAgenteFiscal() {
		return agenteFiscal;
	}

	public void setAgenteFiscal(AgenteFiscal agenteFiscal) {
		this.agenteFiscal = agenteFiscal;
		if(agenteFiscal != null) {
			this.nombre = agenteFiscal.getNombreCompleto();
			this.tomo = agenteFiscal.getTomo();
			this.folio = agenteFiscal.getFolio();
			this.tipoDocumento = calculateTipoDocumentoFromAgenteFiscal(agenteFiscal);
			this.documento = calculateDocumentoFromAgenteFiscal(agenteFiscal);
			this.inhabilitado = agenteFiscal.isInhabilitado();
		}
	}

	public void clearAgenteFiscal(){
		setAgenteFiscal(null);
		AgenteFiscalEnumeration.instance().reset();
		this.nombre = null;
		this.tomo = null;
		this.folio = null;
		this.tipoDocumento = null;
		this.documento = null;
		this.inhabilitado = false;
	}

	public String getCuitCuil() {
		return isTipDocumentoCuitCuil() ?  getDocumento() : null;		
	}

	public String getMatricula() {
		return isTipDocumentoMatricula() ?  getDocumento() : null;		
	}

	public Poder getPoder() {
		return poder;
	}

	public void setPoder(Poder poder) {
		this.poder = poder;
	}

	public String getCuil() {
		return cuil;
	}

	public void setCuil(String cuil) {
		this.cuil = cuil;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
