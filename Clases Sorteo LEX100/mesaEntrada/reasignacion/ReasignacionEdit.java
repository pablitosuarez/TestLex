package com.base100.lex100.mesaEntrada.reasignacion;

import java.util.List;
import java.util.Map;

import org.jboss.seam.util.Strings;

import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.enumeration.DependenciaCentroPoliciaEnumeration;
import com.base100.lex100.component.enumeration.DependenciaEnumeration;
import com.base100.lex100.component.enumeration.DependenciaExternaEnumeration;
import com.base100.lex100.component.enumeration.TipoCausaEnumeration;
import com.base100.lex100.component.enumeration.TipoIncompetenciaInternaEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.enumeration.TipoReasignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoReasignacionTribunalOralEnumeration;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.Dependencia;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.ExpedienteEspecial;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.Mediador;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.TipoAsignacion;
import com.base100.lex100.entity.TipoCambioAsignacion;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.configuracionMateria.ConfiguracionMateriaManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.mesaEntrada.ingreso.ExpedienteEspecialEdit;
import com.base100.lex100.mesaEntrada.ingreso.ExpedienteMeAdd;

public class ReasignacionEdit {

	private String observaciones;
	private Oficina oficinaDestino;
	private Mediador mediadorDestino;
	private boolean verTodasOficinasDestino = false;
	private boolean sortearMediador = true;

	private boolean fijarRadicacion = false;
	private TipoReasignacionEnumeration.Type tipoReasignacion;
	private TipoReasignacionTribunalOralEnumeration.Type tipoReasignacionTribunalOral;
	private boolean excluirOficinaOrigen = true;
	private List<Oficina> oficinasExcluidas;
    private Integer numeroExpediente;
    private Integer anioExpediente;
    private Expediente expediente;
	private TipoCambioAsignacion tipoCambioAsignacion;
	private TipoRadicacionEnumeration.Type tipoRadicacion;
	private Competencia competencia;
	private TipoIncompetenciaInternaEnumeration.Type tipoIncompetenciaInterna = TipoIncompetenciaInternaEnumeration.Type.sorteo;
	private TipoAsignacion tipoAsignacionCodificada;
	private String categoriaTipoAsignacion;
	private ExpedienteEspecialEdit expedienteEspecialEdit;
	private TipoCausa tipoCausa;
	private Materia materia;
	private List<Oficina> vocaliasSalaSorteo;
	private Map<Oficina,Boolean> vocaliasSalaSorteoSelectionMap;
	private Map<Oficina,Boolean> vocaliasSalaSorteoInhibidasMap;
	private Map<Oficina, Boolean> vocaliasSalaSorteoRecusadasMap;
	private boolean omitirSorteo;

	
	/**ATOS COMERCIAL*/
	private boolean omitirSorteoComercial;
	/**ATOS COMERCIAL*/
	
	public ReasignacionEdit(TipoRadicacionEnumeration.Type tipoRadicacion){
		this.tipoRadicacion = tipoRadicacion;
	}
	
	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public TipoReasignacionEnumeration.Type getTipoReasignacion() {
		return tipoReasignacion;
	}

	public void setTipoReasignacion(TipoReasignacionEnumeration.Type tipoReasignacion) {
		this.tipoReasignacion = tipoReasignacion;
	}

	public boolean isExcluirOficinaOrigen() {
		return excluirOficinaOrigen;
	}

	public void setExcluirOficinaOrigen(boolean excluirOficinaOrigen) {
		this.excluirOficinaOrigen = excluirOficinaOrigen;
	}

	public boolean showSelectorDeOficinas(){
		if(this.tipoRadicacion != null && this.tipoRadicacion == TipoRadicacionEnumeration.Type.tribunalOral){
			return 	(tipoReasignacionTribunalOral != null && tipoReasignacionTribunalOral.isOficio());
		}else{
			return (tipoReasignacion != null &&	tipoReasignacion.isOficio()) || isFijarRadicacion()|| 
			//19-08-2014 Si es COMERCIAL y es del tipo reasignacion RCA,RCD,RSA o RSD no sortea, y se coloca juzgado destino 
			(CamaraManager.isCamaraActualCOM() && (tipoReasignacion==TipoReasignacionEnumeration.Type.recusacionComRCA
					|| tipoReasignacion==TipoReasignacionEnumeration.Type.recusacionComRCD
					|| tipoReasignacion==TipoReasignacionEnumeration.Type.recusacionComRSA
					|| tipoReasignacion==TipoReasignacionEnumeration.Type.recusacionComRSD
					|| omitirSorteoComercial));
		}
	}
	
	public boolean isFijarRadicacion() {
		return fijarRadicacion;
	}

	public void setFijarRadicacion(boolean fijarRadicacion) {
		this.fijarRadicacion = fijarRadicacion;
	}

	public boolean isFijarRadicacionPermitida(){
		if(expediente != null){
			return ExpedienteMeReasignacion.instance().isTipoSorteoIncompetenciaExternaInformatizada() && ConfiguracionMateriaManager.instance().isFijarRadicacionEnIncompetenciaExterna(SessionState.instance().getGlobalCamara(), expediente.getMateria());
		} 
		return false;
	}

	public boolean isMediacion(){
		return getExpediente() != null && ExpedienteManager.instance().isMediacion(getExpediente());
	}
	
	public boolean showSelectorDeMediador(){
		return !isSortearMediador();
	}

	public boolean isShowSelectorTipoAsignacionCodificado() {
		return tipoReasignacion != null && 
			(tipoReasignacion == TipoReasignacionEnumeration.Type.desafectacionSalaEstudio || tipoReasignacion == TipoReasignacionEnumeration.Type.finJuzgadoSentencia);
	}
	
	public boolean isShowOficinaDestino() {
		return tipoReasignacion != null && 
			(tipoReasignacion == TipoReasignacionEnumeration.Type.desafectacionSalaEstudio || tipoReasignacion == TipoReasignacionEnumeration.Type.finJuzgadoSentencia);
	}
	
	public boolean isNecesitaSorteo(){
		if(ExpedienteMeReasignacion.instance().isTipoSorteoMediador()){
			return !isAsignacionManualMediador();
		}else if(tipoRadicacion == TipoRadicacionEnumeration.Type.tribunalOral){
			return (tipoReasignacionTribunalOral != null && !tipoReasignacionTribunalOral.isOficio());			
		}else{
			//19-08-2014 Si es COMERCIAL y es del tipo reasignacion RCA,RCD,RSA o RSD no sortea, y se coloca juzgado destino sin sorteo
			if(tipoReasignacion!=null && (CamaraManager.isCamaraActualCOM() && (tipoReasignacion==TipoReasignacionEnumeration.Type.recusacionComRCA
					|| tipoReasignacion==TipoReasignacionEnumeration.Type.recusacionComRCD
					|| tipoReasignacion==TipoReasignacionEnumeration.Type.recusacionComRSA
					|| tipoReasignacion==TipoReasignacionEnumeration.Type.recusacionComRSD
					|| omitirSorteoComercial))){
				return false;
			}else
			return tipoReasignacion != null && !tipoReasignacion.isOficio() &&
				!TipoReasignacionEnumeration.Type.manual.getValue().equals(tipoReasignacion.getValue()) &&
				!TipoReasignacionEnumeration.Type.desafectacionSalaEstudio.getValue().equals(tipoReasignacion.getValue()) &&
				!TipoReasignacionEnumeration.Type.finJuzgadoSentencia.getValue().equals(tipoReasignacion.getValue()) &&
				!(TipoReasignacionEnumeration.Type.incompetencia.getValue().equals(tipoReasignacion.getValue()) && tipoIncompetenciaInterna != TipoIncompetenciaInternaEnumeration.Type.sorteo) &&
				!TipoReasignacionEnumeration.Type.secretariaEspecial.getValue().equals(tipoReasignacion.getValue());
		}
	}
	
	public boolean isMostrarSelectorDeFamilia(){
		return tipoReasignacion != null && !tipoReasignacion.isSorteoPuro() && !tipoReasignacion.isBuscaConexidad() && !tipoReasignacion.isOficio();
	}

	public Oficina getOficinaDestino() {
		return oficinaDestino;
	}

	public void setOficinaDestino(Oficina oficinaDestino) {
		this.oficinaDestino = oficinaDestino;
	}

	public List<Oficina> getOficinasExcluidas() {
		return oficinasExcluidas;
	}

	public void setOficinasExcluidas(List<Oficina> oficinasExcluidas) {
		this.oficinasExcluidas = oficinasExcluidas;
	}
	
	public TipoCambioAsignacion getTipoCambioAsignacion() {
		return tipoCambioAsignacion;
	}

	public void setTipoCambioAsignacion(TipoCambioAsignacion tipoCambioAsignacion) {
		this.tipoCambioAsignacion = tipoCambioAsignacion;
	}

	public Integer getNumeroExpediente() {
		if(this.numeroExpediente == null){
			this.numeroExpediente = (getExpediente() != null)? getExpediente().getNumero(): null;
		}
		return this.numeroExpediente;
	}

	public void setNumeroExpediente(Integer numeroExpediente) {
		this.numeroExpediente = numeroExpediente;
	}

	public Integer getAnioExpediente() {
		if(this.anioExpediente == null){
			this.anioExpediente = (getExpediente() != null)? getExpediente().getAnio(): null;
		}
		return this.anioExpediente;
	}

	public void setAnioExpediente(Integer anioExpediente) {
		this.anioExpediente = anioExpediente;
	}

	public Expediente getExpediente() {
		return expediente;
	}

	public void setExpediente(Expediente expediente) {
		this.expediente = expediente;
		copyExpedienteEspecialToEdit(expediente);		
	}

	public boolean numeroyAnioNotNull() {
		return (this.numeroExpediente != null) && (this.anioExpediente != null);
	}

	public TipoReasignacionTribunalOralEnumeration.Type getTipoReasignacionTribulaOral() {
		return tipoReasignacionTribunalOral;
	}

	public void setTipoReasignacionTribulaOral(TipoReasignacionTribunalOralEnumeration.Type tipoReasignacionTribulaOral) {
		this.tipoReasignacionTribunalOral = tipoReasignacionTribulaOral;
	}

	public TipoRadicacionEnumeration.Type getTipoRadicacion() {
		return tipoRadicacion;
	}

	public void setTipoRadicacion(TipoRadicacionEnumeration.Type tipoRadicacion) {
		this.tipoRadicacion = tipoRadicacion;
	}

	public Competencia getCompetencia() {
		return competencia;
	}
	
	public void setCompetencia(Competencia competencia) {
		this.competencia = competencia;
	}

	public boolean hasCompetenciaDestino() {
		return (showSelectorDeOficinas() && (getOficinaDestino() != null) && (getOficinaDestino().getCompetencia() != null));
	}

	public TipoIncompetenciaInternaEnumeration.Type getTipoIncompetenciaInterna() {
		return tipoIncompetenciaInterna;
	}

	public void setTipoIncompetenciaInterna(
			TipoIncompetenciaInternaEnumeration.Type tipoIncompetenciaInterna) {
		this.tipoIncompetenciaInterna = tipoIncompetenciaInterna;
	}

	public TipoAsignacion getTipoAsignacionCodificada() {
		return tipoAsignacionCodificada;
	}

	public void setTipoAsignacionCodificada(TipoAsignacion tipoAsignacionCodificada) {
		this.tipoAsignacionCodificada = tipoAsignacionCodificada;
	}

	public String getCategoriaTipoAsignacion() {
		return categoriaTipoAsignacion;
	}

	public void setCategoriaTipoAsignacion(String categoriaTipoAsignacion) {
		this.categoriaTipoAsignacion = categoriaTipoAsignacion;
	}

	public ExpedienteEspecialEdit getExpedienteEspecialEdit() {
		return expedienteEspecialEdit;
	}

	public void setExpedienteEspecialEdit(
			ExpedienteEspecialEdit expedienteEspecialEdit) {
		this.expedienteEspecialEdit = expedienteEspecialEdit;
	}

	public void copyExpedienteEspecialToEdit(Expediente expediente) {
		expedienteEspecialEdit = new ExpedienteEspecialEdit();
		if (expediente != null) {
			ExpedienteEspecial expedienteEspecial = expediente.getExpedienteEspecial();
			if (expedienteEspecial != null) {
				expedienteEspecialEdit.setNumeroCodigo(expedienteEspecial.getTipoCodigoRelacionado());
				expedienteEspecialEdit.setNumero(expedienteEspecial.getCodigoRelacionado());
				expedienteEspecialEdit.setDescripcion(expedienteEspecial.getDescripcionInformacion());
				expedienteEspecialEdit.setFechaInformacion(expedienteEspecial.getFechaInformacion());
				
				setDependencia(expedienteEspecialEdit, expediente.getTipoCausa(), expedienteEspecial.getDependenciaOrigen());

				// Penal
				expedienteEspecialEdit.setHechos(expedienteEspecial.getHechos());
				expedienteEspecialEdit.setIncidentes(expedienteEspecial.getIncidentes());
				expedienteEspecialEdit.setMenores(expedienteEspecial.isMenores());
				expedienteEspecialEdit.setInternados(expedienteEspecial.isInternados());
				expedienteEspecialEdit.setDetenidos(expedienteEspecial.isDetenidos());
				expedienteEspecialEdit.setDefensorOficial(expedienteEspecial.isDefensorOficial());	//ITO (Incompetencia TO)
				expedienteEspecialEdit.setEfecto(expedienteEspecial.isEfectos());
				expedienteEspecialEdit.setIdentidadReservada(expedienteEspecial.isIdentidadReservada());
				expedienteEspecialEdit.setDeclaracionHecho(expedienteEspecial.getDeclaracionHecho());
				expedienteEspecialEdit.setDenunciaFirmada(expedienteEspecial.isDenunciaFirmada());
				expedienteEspecialEdit.setDerechosHumanos(expedienteEspecial.isDerechosHumanos());
				expedienteEspecialEdit.setCorrupcion(expedienteEspecial.isCorrupcion());

				// Exhortos
				expedienteEspecialEdit.setJuezExhortante(expedienteEspecial.getJuezExhortante());

				expedienteEspecialEdit.setJuzgadoFueroExhorto(expedienteEspecial.getJuzgadoFueroExhorto());
				expedienteEspecialEdit.setCaratulaOrigen(expedienteEspecial.getCaratulaOrigen());
				expedienteEspecialEdit.setJurisdiccionExhorto(expedienteEspecial.getJurisdiccionExhorto());
			}
		}
	}

	private void setDependencia(ExpedienteEspecialEdit expedienteEspecialEdit, TipoCausa tipoCausa, String dependenciaDescription) {
		Dependencia dependencia = findDependencia(tipoCausa, dependenciaDescription);

		if (dependencia != null) {
			expedienteEspecialEdit.setDependencia(dependencia);
		} else {
			expedienteEspecialEdit.setOtraDependencia(dependenciaDescription);
			if (ExpedienteMeAdd.isExterna(tipoCausa)) {
				DependenciaExternaEnumeration.instance().setUserInput(dependenciaDescription);
			} else if(ExpedienteMeAdd.isPrevencion(tipoCausa)){
				DependenciaCentroPoliciaEnumeration.instance().setUserInput(dependenciaDescription);
			} else {
				DependenciaEnumeration.instance().setUserInput(dependenciaDescription);
			}
		}
		
	}
	
	private Dependencia findDependencia(TipoCausa tipoCausa, String dependenciaDescription){
		if (ExpedienteMeAdd.isExterna(tipoCausa)) {
			return DependenciaExternaEnumeration.instance().getItemByDescripcion(dependenciaDescription);
		} else if(ExpedienteMeAdd.isPrevencion(tipoCausa)){
			return DependenciaCentroPoliciaEnumeration.instance().getItemByDescripcion(dependenciaDescription);
		} else {
			return DependenciaEnumeration.instance().getItemByDescripcion(dependenciaDescription);
		}
	}

	public TipoCausa getTipoCausa() {
		return tipoCausa;
	}

	public void setTipoCausa(TipoCausa tipoCausa) {
		this.tipoCausa = tipoCausa;
	}

	public Materia getMateria() {
		return materia;
	}

	public void setMateria(Materia materia) {
		this.materia = materia;
	}
	
	public void actualizarDatosExpediente() {
		completarDatosExpedienteEspecial(expediente);
	}
	
	private void completarDatosExpedienteEspecial(Expediente expediente) {
		ExpedienteEspecialEdit expedienteEspecialEdit = getExpedienteEspecialEdit();
		
		ExpedienteEspecial expedienteEspecial = expediente.getExpedienteEspecial();
		if (expedienteEspecial == null) {
			expedienteEspecial = new ExpedienteEspecial();
			expedienteEspecial.setExpediente(expediente);
			expediente.setExpedienteEspecial(expedienteEspecial);
		}
		
		
		expedienteEspecial.setTipoCodigoRelacionado(expedienteEspecialEdit.getNumeroCodigo());
		expedienteEspecial.setCodigoRelacionado(expedienteEspecialEdit.getNumero());
		expedienteEspecial.setDescripcionInformacion(limitText(expedienteEspecialEdit.getDescripcion(), 600));
		expedienteEspecial.setFechaInformacion(expedienteEspecialEdit.getFechaInformacion());
		if(expedienteEspecialEdit.getDependencia() != null){
			expedienteEspecial.setDependenciaOrigen(expedienteEspecialEdit.getDependencia().getDescripcion());
		} else if (!Strings.isEmpty(expedienteEspecialEdit.getOtraDependencia())) {
			expedienteEspecial.setDependenciaOrigen(expedienteEspecialEdit.getOtraDependencia());
		}

		
		// Penal
		expedienteEspecial.setHechos(expedienteEspecialEdit.getHechos());
		expedienteEspecial.setIncidentes(expedienteEspecialEdit.getIncidentes());
		expedienteEspecial.setMenoresFlag(expedienteEspecialEdit.getMenores());
		expedienteEspecial.setInternadosFlag(expedienteEspecialEdit.getInternados());
		expedienteEspecial.setDetenidosFlag(expedienteEspecialEdit.getDetenidos());
		expedienteEspecial.setEfectosFlag(expedienteEspecialEdit.getEfecto());
		expedienteEspecial.setDefensorOficialFlag(expedienteEspecialEdit.isDefensorOficial());	//ITO (Incompetencia TO)
		expedienteEspecial.setIdentidadReservadaFlag(expedienteEspecialEdit.getIdentidadReservada());
		expedienteEspecial.setDeclaracionHecho(expedienteEspecialEdit.getDeclaracionHecho());
		expedienteEspecial.setDenunciaFirmada(expedienteEspecialEdit.isDenunciaFirmada());
		expedienteEspecial.setDerechosHumanosFlag(expedienteEspecialEdit.getDerechosHumanos());

		// Exhortos
		expedienteEspecial.setJuezExhortante(expedienteEspecialEdit.getJuezExhortante());

		if (!Strings.isEmpty(expedienteEspecialEdit.getJuzgadoFueroExhorto())){
			expedienteEspecial.setJuzgadoFueroExhorto(expedienteEspecialEdit.getJuzgadoFueroExhorto());
		}
		if (!Strings.isEmpty(expedienteEspecialEdit.getCaratulaOrigen())){
			expedienteEspecial.setCaratulaOrigen(expedienteEspecialEdit.getCaratulaOrigen());
		}
	}

	private String limitText(String s, int len) {
		if(s == null || s.length() <= len){
			return s;
		}else{
			return s.substring(0, len);
		}
	}

	public List<Oficina> getVocaliasSalaSorteo() {
		return vocaliasSalaSorteo;
	}

	public void setVocaliasSalaSorteo(List<Oficina> vocaliasSalaSorteo) {
		this.vocaliasSalaSorteo = vocaliasSalaSorteo;
	}

	public Map<Oficina, Boolean> getVocaliasSalaSorteoSelectionMap() {
		return vocaliasSalaSorteoSelectionMap;
	}

	public void setVocaliasSalaSorteoSelectionMap(
			Map<Oficina, Boolean> vocaliasSalaSorteoSelectionMap) {
		this.vocaliasSalaSorteoSelectionMap = vocaliasSalaSorteoSelectionMap;
	}

	public Map<Oficina, Boolean> getVocaliasSalaSorteoInhibidasMap() {
		return vocaliasSalaSorteoInhibidasMap;
	}

	public void setVocaliasSalaSorteoInhibidasMap(
			Map<Oficina, Boolean> vocaliasSalaSorteoInhibidasMap) {
		this.vocaliasSalaSorteoInhibidasMap = vocaliasSalaSorteoInhibidasMap;
	}

	public Map<Oficina, Boolean> getVocaliasSalaSorteoRecusadasMap() {
		return vocaliasSalaSorteoRecusadasMap;
	}

	public void setVocaliasSalaSorteoRecusadasMap(
			Map<Oficina, Boolean> vocaliasSalaSorteoRecusadasMap) {
		this.vocaliasSalaSorteoRecusadasMap = vocaliasSalaSorteoRecusadasMap;
	}

	public boolean isVerTodasOficinasDestino() {
		return verTodasOficinasDestino;
	}

	public void setVerTodasOficinasDestino(boolean verTodasOficinasDestino) {
		this.verTodasOficinasDestino = verTodasOficinasDestino;
	}

	public Mediador getMediadorDestino() {
		return mediadorDestino;
	}

	public void setMediadorDestino(Mediador mediadorDestino) {
		this.mediadorDestino = mediadorDestino;
	}

	public boolean isSortearMediador() {
		return sortearMediador;
	}

	public void setSortearMediador(boolean sortearMediador) {
		this.sortearMediador = sortearMediador;
	}

	public boolean isAsignacionManualMediador() {
		return !isSortearMediador();
	}

	public boolean isOmitirSorteo() {
		if (TipoReasignacionEnumeration.Type.busquedaConexidad.equals(getTipoReasignacion())) {
			return omitirSorteo;
		} else {
			return false;
		}
	}

	public Boolean getOmitirSorteo() {
		return isOmitirSorteo();
	}
	
	public void setOmitirSorteo(boolean omitirSorteo) {
		this.omitirSorteo = omitirSorteo;
	}

	/**ATOS COMERCIAL*/
	public boolean isOmitirSorteoComercial() {
		return omitirSorteoComercial;
	}
	
	public void setOmitirSorteoComercial(boolean omitirSorteoComercial) {
		this.omitirSorteoComercial = omitirSorteoComercial;
	}

	public boolean isTipoCausaFat(){
		return tipoCausa != null && tipoCausa.getCodigo().equals(TipoCausaEnumeration.CODIGO_FAT);
	}
	/**ATOS COMERCIAL*/
}
