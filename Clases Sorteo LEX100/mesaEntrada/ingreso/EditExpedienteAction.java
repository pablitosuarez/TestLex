package com.base100.lex100.mesaEntrada.ingreso;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.util.Strings;

import com.base100.lex100.Lex100Exception;
import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.currentdate.CurrentDateManager;
import com.base100.lex100.component.datasheet.DataSheetEditorManager;
import com.base100.lex100.component.datasheet.IDataSheetProperty;
import com.base100.lex100.component.enumeration.DependenciaCentroPoliciaEnumeration;
import com.base100.lex100.component.enumeration.DependenciaContendienteEnumeration;
import com.base100.lex100.component.enumeration.DependenciaEnumeration;
import com.base100.lex100.component.enumeration.DependenciaExternaEnumeration;
import com.base100.lex100.component.enumeration.ObjetoJuicioEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoCambioAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoCausaEnumeration;
import com.base100.lex100.component.enumeration.TipoInformacionEnumeration;
import com.base100.lex100.component.enumeration.TipoInstanciaEnumeration;
import com.base100.lex100.component.enumeration.TipoIntervinienteEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration.Type;
import com.base100.lex100.component.enumeration.TipoVinculadoEnumeration;
import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.controller.entity.intervinienteExp.IntervinienteExpSearch;
import com.base100.lex100.controller.entity.parametro.ParametroSearch;
import com.base100.lex100.controller.entity.parametroExpediente.ParametroExpedienteSearch;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.ConexidadDeclarada;
import com.base100.lex100.entity.Dependencia;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.ExpedienteEspecial;
import com.base100.lex100.entity.ExpedienteIngreso;
import com.base100.lex100.entity.Interviniente;
import com.base100.lex100.entity.IntervinienteExp;
import com.base100.lex100.entity.LetradoIntervinienteExp;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.Parametro;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.TipoInterviniente;
import com.base100.lex100.entity.TipoProceso;
import com.base100.lex100.entity.VinculadosExp;
import com.base100.lex100.manager.AbstractManager;
import com.base100.lex100.manager.actuacionExp.ActuacionExpManager;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.configuracionMateria.ConfiguracionMateriaManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.manager.objetoJuicio.ObjetoJuicioManager;
import com.base100.lex100.manager.oficina.OficinaManager;
import com.base100.lex100.manager.vinculadosExp.VinculadosExpManager;
import com.base100.lex100.mesaEntrada.conexidad.ConexidadManager;
import com.base100.lex100.mesaEntrada.sorteo.MesaSorteo;
import com.base100.lex100.mesaEntrada.sorteo.RubrosInfo;

@Name("editExpedienteAction")
@Scope(ScopeType.CONVERSATION)
public class EditExpedienteAction extends ScreenController
{
	@In(create=true)
	ExpedienteHome expedienteHome;
    @In(create=true)
    private EntityManager entityManager;

    private IntervinienteEdit intervinienteEdit = new IntervinienteEdit();
    
	public void prepareToVerifyExpediente(Expediente expediente) throws Lex100Exception {
		ExpedienteMeAdd expedienteEdit = ExpedienteMeAdd.instance();		
		copyExpedienteToEdit(expediente, expedienteEdit, false);
		expedienteEdit.setEditMode(true);
		expedienteEdit.setVerifyMode(true);
	}
    
	public void prepareToEditExpediente(Expediente expediente) throws Lex100Exception {
		ExpedienteMeAdd expedienteEdit = ExpedienteMeAdd.instance();		
		expedienteEdit.setEditMode(true);
		copyExpedienteToEdit(expediente, expedienteEdit, false);
	}

	public void copyExpedienteToEdit(Expediente expediente,	ExpedienteMeAdd expedienteEdit, boolean ignoreDatosMediacion) {
		expedienteEdit.init();
		expedienteEdit.setCurrentEditExpediente(expediente);
		expedienteEdit.setFechaIngresoExpediente(expediente.getFechaIngreso());
		expedienteEdit.setTipoCausaEspecial(expediente.getTipoCausa());
		//expedienteEdit.setMateria(ExpedienteManager.getMateria(expediente));
		expedienteEdit.setCompetencia(expediente.getCompetencia());
		expedienteEdit.setComentarios(expediente.getComentarios());
		expedienteEdit.setMateria(expediente.getMateria());
		if (ExpedienteManager.isPenal(expediente)) {
			expedienteEdit.setObjetoJuicio(null);
		} else {
			if(!ignoreDatosMediacion && ExpedienteManager.instance().isMediacion(expediente)){
				copyObjetosJuicioToEdit(expediente, expedienteEdit);
				expedienteEdit.setObjetoJuicio(expediente.getObjetoJuicioPrincipal());
				if(expediente.getObjetoJuicioList().size() == 0){
					expedienteEdit.getObjetoJuicioEditList().getTmpInstance().setObjetoJuicio(expediente.getObjetoJuicioPrincipal());
					expedienteEdit.getObjetoJuicioEditList().doSaveLine();
				}
			}else{
				//expedienteEdit.getObjetoJuicioEdit().setObjetoJuicio(expediente.getObjetoJuicioPrincipal());
				expedienteEdit.setObjetoJuicio(expediente.getObjetoJuicioPrincipal());
			}
			expedienteEdit.setDescripcionObjetoJuicio(expediente.getDescripcionObjetoJuicio());
			ObjetoJuicioEnumeration.instance().setSelected(expediente.getObjetoJuicioPrincipal());
			ObjetoJuicioEnumeration.instance().setCurrentObjetoJuicio(expediente.getObjetoJuicioPrincipal());
//			if (expediente.getObjetoJuicioPrincipal() != null){
//				expedienteEdit.setTema(expediente.getObjetoJuicioPrincipal().getTema());
//			}
		}
		if ((expediente.getTipoProceso() != null) && expediente.getObjetoJuicioPrincipal() != null && !expediente.getTipoProceso().equals(expediente.getObjetoJuicioPrincipal().getTipoProceso())) {
			expedienteEdit.setTipoProceso(expediente.getTipoProceso());
		}
		expedienteEdit.initFiltroMateriaObjetoJuicioForEditing(expediente);		
		expedienteEdit.setTema(null);
		copyDelitosToEdit(expediente, expedienteEdit);
		copyIntervinientesToEdit(expediente, expedienteEdit);
		copyLetradosToEdit(expediente, expedienteEdit);
		copyExpedienteEspecialToEdit(expediente, expedienteEdit);
		copyOtrosDatosToEdit(expediente, expedienteEdit);
		copyConexidadDeclaraToEdit(expediente, expedienteEdit);
		copyFojasToEdit(expediente, expedienteEdit);
		copyMontoToEdit(expediente, expedienteEdit);
		copyExpedienteInfoToEdit(expediente, expedienteEdit); 
		copySecloToEdit(expediente, expedienteEdit);
		
		expedienteEdit.setMedidasCautelares(expediente.isMedidasCautelares());
		copyParametrosToEdit(expediente, expedienteEdit);
	}
	
	private void copyParametrosToEdit(Expediente expediente, ExpedienteMeAdd expedienteEdit) {

		if(expedienteEdit.getParametrosAsPropertyList() != null) {
			for(IDataSheetProperty property: expedienteEdit.getParametrosAsPropertyList()) {
				Camara camara = (expediente.getObjetoJuicioPrincipal() != null)? expediente.getObjetoJuicioPrincipal().getCamara(): expediente.getCamara();
				Parametro parametro = ParametroSearch.findByName(entityManager, camara, property.getName());
				if (parametro != null) {
					List<String> valueList = ParametroExpedienteSearch.getParametroValues(entityManager, parametro, expediente.getExpedienteIngreso());
					String values = Configuration.getCommaSeparatedString(valueList);
					if (values != null) {
						DataSheetEditorManager.instance().getInstanceProperties().put(property.getDataSheetPropertyId(), values);
					}
				}
			}
		}
	}

	private void copyMontoToEdit(Expediente expediente,	ExpedienteMeAdd expedienteEdit) {
		expedienteEdit.getMontoJuicioEdit().setMoneda(expediente.getMoneda());
		expedienteEdit.getMontoJuicioEdit().setCantidad(expediente.getMonto());
	}

	private void copyFojasToEdit(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		FojasEdit fojasEdit = expedienteEdit.getFojasEdit();
		fojasEdit.setFojas(expediente.getFojas());
		fojasEdit.setCuerpos(expediente.getCuerpos());
		fojasEdit.setAgregados(expediente.getAgregados());
	}

	private void copyConexidadDeclaraToEdit(Expediente expediente,
			ExpedienteMeAdd expedienteEdit) {
		
		if (expediente.getConexidadDeclarada() != null) {
			CreateExpedienteAction.copyConexidadDeclarada(expediente.getConexidadDeclarada(), expedienteEdit.getConexidadDeclarada());
		}
	}

	private void copyOtrosDatosToEdit(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		ExpedienteIngreso expedienteIngreso = expediente.getExpedienteIngreso();
		OtrosDatosEdit datos = expedienteEdit.getOtrosDatos();
		if (expedienteIngreso != null) {
			datos.setAbonarTasa(expedienteIngreso.isAbonarTasa());
			datos.setExentoTasa(expedienteIngreso.isExentoTasa());
			datos.setNoAlcanzadoTasa(expedienteIngreso.isNoAlcanzadoTasa());
			datos.setPagoDiferidoTasa(expedienteIngreso.isPagoDiferidoTasa());
			datos.setMedidaPrecautoria(expedienteIngreso.isMedidaPrecautoria());
			datos.setMedidaPreliminar(expedienteIngreso.isMedidaPreliminar());
			datos.setPruebaAnticipada(expedienteIngreso.isPruebaAnticipada());
			datos.setAdjuntaCopia(expedienteIngreso.isAdjuntaCopia());
			datos.setResolucionAdministrativa(expedienteIngreso.isResolucionAdministrativa());
		}
	}

	private void copyExpedienteEspecialToEdit(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		ExpedienteEspecial expedienteEspecial = expediente.getExpedienteEspecial();
		ExpedienteEspecialEdit expedienteEspecialEdit = expedienteEdit.getExpedienteEspecialEdit();
		if (expedienteEspecial != null) {
			expedienteEspecialEdit.setNumeroCodigo(expedienteEspecial.getTipoCodigoRelacionado());
			expedienteEspecialEdit.setNumero(expedienteEspecial.getCodigoRelacionado());
			expedienteEspecialEdit.setDescripcion(expedienteEspecial.getDescripcionInformacion());
			expedienteEspecialEdit.setFechaInformacion(expedienteEspecial.getFechaInformacion());
			
			setDependencia(expedienteEspecialEdit, expediente.getTipoCausa(), expedienteEspecial.getDependenciaOrigen());
			setDependenciaContendiente(expedienteEspecialEdit, expediente.getTipoCausa(), expedienteEspecial.getDependenciaContendiente());

			expedienteEdit.getFojasEdit().setSobres(expedienteEspecial.getSobres());
			
		
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
			expedienteEdit.setDerechosHumanos(expedienteEspecial.isDerechosHumanos());

			expedienteEdit.getRecursoEdit().setTipoRecurso(expedienteEspecial.getTipoRecurso());

			// Exhortos
			expedienteEspecialEdit.setJuezExhortante(expedienteEspecial.getJuezExhortante());

			expedienteEspecialEdit.setJuzgadoFueroExhorto(expedienteEspecial.getJuzgadoFueroExhorto());
			expedienteEspecialEdit.setCaratulaOrigen(expedienteEspecial.getCaratulaOrigen());
			expedienteEspecialEdit.setJurisdiccionExhorto(expedienteEspecial.getJurisdiccionExhorto());
		}
	}
	private void copySecloToEdit(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		expedienteEdit.setNumeroSeclo(expediente.getNumeroSeclo());
		expedienteEdit.setAnioSeclo(expediente.getAnioSeclo());
	}
	
	private void copyExpedienteInfoToEdit(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		if (expediente.getExpedienteInfo() != null) {
			expedienteEdit.setExpedienteInfoValor(expediente.getExpedienteInfo().getValor());
		}
	}
	
	private void copyObjetosJuicioToEdit(Expediente expediente,	ExpedienteMeAdd expedienteEdit) {
		ObjetoJuicioEditList objetoJuicioEditList = new ObjetoJuicioEditList(expediente.getObjetoJuicioList());
		objetoJuicioEditList.setAutoAddMode(true);
		expedienteEdit.setObjetoJuicioEditList(objetoJuicioEditList);
		objetoJuicioEditList.checkAutoAdd();
	}

	private void copyDelitosToEdit(Expediente expediente,	ExpedienteMeAdd expedienteEdit) {
		DelitoEditList delitoEditList = new DelitoEditList(expediente.getDelitoExpedienteList());
		delitoEditList.setAutoAddMode(true);
		expedienteEdit.setDelitoEditList(delitoEditList);
		delitoEditList.checkAutoAdd();

	}

	public void copyIntervinientesToEdit(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		List<IntervinienteExp> intervinienteExpList = expediente.getIntervinienteExpList();
		if(IntervinienteExpSearch.instance().isOcultarIntervinientesNS()){
			intervinienteExpList  = quitarIntervinientesNS(intervinienteExpList);
		}
		IntervinienteEditList intervinienteEditList = new IntervinienteEditList(intervinienteExpList, expedienteEdit);
		intervinienteEditList.setAutoAddMode(true);
		expedienteEdit.setIntervinienteEditList(intervinienteEditList);
		intervinienteEditList.checkAutoAdd();
	}

	
	private List<IntervinienteExp> quitarIntervinientesNS(List<IntervinienteExp> intervinienteExpList) {
		boolean found = false;
		List<IntervinienteExp> list = intervinienteExpList;
		for(IntervinienteExp item: intervinienteExpList){
			if(IntervinienteExp.NO_SALE_EN_CARATULA.equals(item.getControl())){
				found = true;
				break;
			}
		}
		if(found){
			list = new ArrayList<IntervinienteExp>();
			for(IntervinienteExp item: intervinienteExpList){
				if(!IntervinienteExp.NO_SALE_EN_CARATULA.equals(item.getControl())){
					list.add(item);
				}
			}			
		}
		return list;
	}

	private void copyLetradosToEdit(Expediente expediente, ExpedienteMeAdd expedienteEdit) {
		List<LetradoIntervinienteExp> letrados = new ArrayList<LetradoIntervinienteExp>();
		boolean letradosDelDemandado = false;
		for (IntervinienteExp intervinienteExp : expediente.getOrderedIntervinientes()) {
			if(!TipoIntervinienteEnumeration.isDemandado(intervinienteExp.getTipoInterviniente())){
				List<LetradoIntervinienteExp> letradosInterviniente = intervinienteExp.getActiveLetrados();
				if(letradosInterviniente != null) {
					for(LetradoIntervinienteExp letradoInterviniente: letradosInterviniente){
						if (findLetrado(letrados, letradoInterviniente) == null) {
							letrados.add(letradoInterviniente);	
						}
					}
//					if(TipoIntervinienteEnumeration.isDemandado(intervinienteExp.getTipoInterviniente())){
//						letradosDelDemandado = true;
//					}
//					break;
				}
			}
		}
		LetradoEditList letradoEditList = new LetradoEditList(letrados, ExpedienteMeAdd.instance().isPermitirAltaLetrado());
		letradoEditList.setLetradosDelDemandado(letradosDelDemandado);
		letradoEditList.setAutoAddMode(true);
		expedienteEdit.setLetradoEditList(letradoEditList);
		letradoEditList.checkAutoAdd();
	}	

	private LetradoIntervinienteExp findLetrado(List<LetradoIntervinienteExp> letrados, LetradoIntervinienteExp letrado) {
		LetradoIntervinienteExp ret = null;
		for(LetradoIntervinienteExp letradoIntervinienteExp : letrados){
			if(CreateExpedienteAction.isEqualLetrado(letradoIntervinienteExp, letrado)){
				ret = letradoIntervinienteExp;
				break;
			}
		}
		return ret;
	}

	@Transactional
	public void updateExpediente(ExpedienteMeAdd expedienteEdit) throws Lex100Exception {
		
		if(ConfiguracionMateriaManager.instance().isNumeroExpedienteDividido(CamaraManager.getCamaraActual())){
			StringBuffer sb = new StringBuffer();
			if(expedienteEdit.getExpedienteEspecialEdit().getNumeroConcat()!=null && expedienteEdit.getExpedienteEspecialEdit().getAnioConcat()!=null ){
				sb.append(expedienteEdit.getExpedienteEspecialEdit().getNumeroConcat());
				sb.append("/");
				sb.append(expedienteEdit.getExpedienteEspecialEdit().getAnioConcat());
				expedienteEdit.getExpedienteEspecialEdit().setNumero(sb.toString());
			}else{
				if(expedienteEdit.getExpedienteEspecialEdit().getNumeroConcat()!=null){
					sb.append(expedienteEdit.getExpedienteEspecialEdit().getNumeroConcat());
					expedienteEdit.getExpedienteEspecialEdit().setNumero(sb.toString());
				}else if(expedienteEdit.getExpedienteEspecialEdit().getAnioConcat()!=null){
					sb.append("/");
					sb.append(expedienteEdit.getExpedienteEspecialEdit().getAnioConcat());
					expedienteEdit.getExpedienteEspecialEdit().setNumero(sb.toString());
				}
			 }
		}
		
		Expediente expediente = expedienteHome.getInstance();
		String dependenciaOrigenAnterior = (expediente.getExpedienteEspecial() != null) ? expediente.getExpedienteEspecial().getDependenciaOrigen(): null;
		String caratulaOrigenAnterior = (expediente.getExpedienteEspecial() != null) ? expediente.getExpedienteEspecial().getCaratulaOrigen(): null;
		ObjetoJuicio objetoJuicioAnterior = expediente.getObjetoJuicioPrincipal();
		String descripcionObjetoJuicioAnterior = expediente.getDescripcionObjetoJuicio();
		
		ConexidadDeclarada conexidadDeclaradaAnterior = null;
		if (ExpedienteManager.hasConexidadDeclarada(expediente)) {
			conexidadDeclaradaAnterior = new ConexidadDeclarada(expediente.getConexidadDeclarada());
		}

		CreateExpedienteAction.instance().updateObjetoJuicioToExpediente(expediente, expedienteEdit);
		CreateExpedienteAction.instance().updateTipoProcesoToExpediente(expediente, expedienteEdit);
		CreateExpedienteAction.instance().updateDelitosToExpediente(expediente, expedienteEdit);
		CreateExpedienteAction.instance().updateIntervinientesToExpediente(expediente, expedienteEdit);
		CreateExpedienteAction.instance().updateExpedienteIngresoToExpediente(expediente, expedienteEdit);
    	CreateExpedienteAction.instance().updateExpedienteEspecialToExpediente(expediente, expedienteEdit) ;
    	CreateExpedienteAction.instance().updateFojasToExpediente(expediente, expedienteEdit);
    	CreateExpedienteAction.instance().updateMontoToExpediente(expediente, expedienteEdit);
    	CreateExpedienteAction.instance().updateSecloToExpediente(expediente, expedienteEdit);
    	CreateExpedienteAction.instance().updateExpedienteInfoToExpediente(expediente, expedienteEdit) ;
    	/**ATOS OVD */
    	List<VinculadosExp> vinculados=VinculadosExpManager.instance().findByTipo(TipoVinculadoEnumeration.Type.OVD.getValue().toString(), expediente);
		if (!vinculados.isEmpty()){
			VinculadosExp vinculado=vinculados.get(0);
			VinculadosExpManager vinculadoManager = (VinculadosExpManager)Component.getInstance(VinculadosExpManager.class, true);    	
	    	vinculadoManager.updateOVD(expedienteEdit, vinculado);
	 	}
		/**ATOS OVD */
    	boolean representacionChanged = CreateExpedienteAction.instance().updatePoderesDemanda(expediente, expedienteEdit);
    	expediente.setComentarios(expedienteEdit.getComentarios());
		expediente.setMedidasCautelares(expedienteEdit.isMedidasCautelares());

    	ExpedienteManager expedienteManager = (ExpedienteManager)Component.getInstance(ExpedienteManager.class, true);
    	expediente.setEstadoEsParte(expedienteEdit.isEstadoEsParte());
		entityManager.flush();
		expedienteManager.actualizarDatosExpediente(expediente);
//		expedienteManager.actualizaTipoVoluntario(expediente);
		entityManager.flush();
		entityManager.refresh(expediente);
		
		String objetoJuicioChanged = objetoJuicioChanged(objetoJuicioAnterior, descripcionObjetoJuicioAnterior, expediente);
		String intervinientesChanged = intervinientesChanged(expedienteEdit);
		String delitosChanged = delitosChanged(expedienteEdit);
		String dependenciaOrigenChanged = dependenciaOrigenChanged(dependenciaOrigenAnterior, expediente);
		String caratulaOrigenChanged = caratulaOrigenChanged(caratulaOrigenAnterior, expediente);
		String parametrosChanged = expedienteEdit.getParametrosChanged();
		
		if (intervinientesChanged != null) {
			String tipoInformacion = TipoInformacionEnumeration.CAMBIO_INTERVINIENTE;
			if (ExpedienteManager.isPenal(expediente)) {
				tipoInformacion = TipoInformacionEnumeration.CAMBIO_CARATULA;
			}
			ExpedienteManager.instance().actualizarCaratula(expediente, tipoInformacion, intervinientesChanged);
			ExpedienteManager.instance().actualizarCaratulaCuadernosRelacionados(expediente, tipoInformacion, intervinientesChanged);
		}
		if(objetoJuicioChanged != null) {
			ExpedienteManager.instance().actualizarCaratula(expediente, TipoInformacionEnumeration.CAMBIO_OBJETO_JUICIO, objetoJuicioChanged);
			RubrosInfo rubrosInfo = MesaSorteo.instance().calculaRubrosAsignacion(expediente, TipoRadicacionEnumeration.Type.juzgado, null, false);
			boolean descompensarAnterior = !ObjetoJuicioManager.isObjetoJucioQuiebras(expediente.getObjetoJuicioPrincipal());
			ExpedienteManager.instance().compensarOficina(expediente, null, TipoRadicacionEnumeration.Type.juzgado, rubrosInfo, TipoInformacionEnumeration.CAMBIO_OBJETO_JUICIO, objetoJuicioChanged, descompensarAnterior);
			if (ObjetoJuicioManager.isObjetoJucioQuiebras(expediente.getObjetoJuicioPrincipal())) {
				objetoJuicioChanged = null; 	// Para que no busque conexidad por cambio de OJ
			}
			
			if (objetoJuicioAnterior != null ) {
				OficinaAsignacionExp radicacionJuzgado = expediente.getRadicacionJuzgado();
				if (radicacionJuzgado != null) {
					if ((ExpedienteManager.getCompetencia(expediente) != null) && ConfiguracionMateriaManager.instance().isAsignacionSecretariaPorCompetencia(SessionState.instance().getGlobalCamara())) {
						Oficina secretaria = OficinaManager.instance().getSecretariaPorCompentencia(radicacionJuzgado.getOficina(), ExpedienteManager.getCompetencia(expediente));
						if (secretaria != null) {
							ExpedienteManager.instance().cambiarSecretaria(expediente, null, secretaria, CurrentDateManager.instance().getCurrentDate());
						}
					}
				}
			}
		}
		if(delitosChanged != null) {
			ExpedienteManager.instance().actualizarCaratula(expediente, TipoInformacionEnumeration.CAMBIO_DELITO, delitosChanged);
		}
		if (dependenciaOrigenChanged != null) {
			ExpedienteManager.instance().actualizarCaratula(expediente, TipoInformacionEnumeration.CAMBIO_CARATULA, dependenciaOrigenChanged);
		}
		if (caratulaOrigenChanged != null) {
			ExpedienteManager.instance().actualizarCaratula(expediente, TipoInformacionEnumeration.CAMBIO_CARATULA, caratulaOrigenChanged);
		}
		if (representacionChanged) {
			ExpedienteManager.instance().actualizarCaratula(expediente, null, null);
		}
		if(parametrosChanged != null) {
			ActuacionExpManager.instance().addActuacionCambioParametrosConexidad(expediente, parametrosChanged);
		}
		TipoRadicacionEnumeration.Type tipoRadicacionPrincipal = getTipoRadicacionPrincipal(expediente);
		Oficina oficinaActual = SessionState.instance().getGlobalOficina();
		
		boolean asignada = false;
		
		if(expedienteEdit.showConexidadDeclarada() && isConexidadDeclaradaChanged(conexidadDeclaradaAnterior, expediente)) {
			conexidadDeclaradaChanged(entityManager, expediente, conexidadDeclaradaAnterior, tipoRadicacionPrincipal);
			boolean conexidadDenunciadaPrioritaria = CreateExpedienteAction.isConexidadDenunciadaPrioritaria(expediente);
			if (conexidadDenunciadaPrioritaria && !ExpedienteManager.instance().isIniciado(expediente) && (expediente.getConexidadDeclarada() != null) && OficinaManager.instance().isMesaDeEntrada(oficinaActual)) {
				// ASIGNA
				Materia materia = expedienteEdit.getMateria();
				String codigoTipoCambioAsignacion = TipoCambioAsignacionEnumeration.CONEXIDAD_SOLICITADA;
				String descripcionCambioAsignacion = CreateExpedienteAction.getDescripcionCambioAsignacion(entityManager, codigoTipoCambioAsignacion, tipoRadicacionPrincipal, expediente.getMateria()); 
			 	RubrosInfo rubrosInfo = MesaSorteo.instance().calculaRubrosAsignacion(expediente, tipoRadicacionPrincipal, null, false);
				Sorteo sorteo = MesaSorteo.instance().buscaSorteo(oficinaActual, 
							false,
							tipoRadicacionPrincipal, 
							materia.getGrupo(), 
							materia, 
							expedienteEdit.getCompetencia(), 
							rubrosInfo.getRubroAsignacion(), 
							rubrosInfo.getRubro(),
							true);
				asignada = CreateExpedienteAction.buscaConexidadDenunciada(entityManager, 
						expediente,
						null,
						tipoRadicacionPrincipal,
						TipoCambioAsignacionEnumeration.CONEXIDAD_SOLICITADA, 
						descripcionCambioAsignacion,
						ConfiguracionMateriaManager.instance().getTipoGiroAsignacion(tipoRadicacionPrincipal, expedienteEdit.getCamara(), expedienteEdit.getMateria()), 
						sorteo, 
						rubrosInfo.getRubro(), 
						true,
						SessionState.instance().getGlobalOficina(),
						false,
						false,
						expedienteEdit.getFojasEdit());
			}
		}
		boolean expedienteModificado = false;
		boolean isPrincipal = ExpedienteManager.isPrincipal(expediente) || ExpedienteManager.isTribunalOral(expediente);
		if (isPrincipal && ((parametrosChanged != null) || (objetoJuicioChanged != null) || (intervinientesChanged != null) || ((dependenciaOrigenChanged != null) && TipoCausaEnumeration.isExhorto(expediente.getTipoCausa())))) {
			expedienteModificado = true;
		}
		if(expedienteModificado && !expedienteEdit.isOmitirBusquedaConexidad()) {
			buscaConexidad(expediente, expedienteEdit.getCamara(), expedienteEdit.getMateria(), expedienteEdit.getCompetencia(), tipoRadicacionPrincipal,	oficinaActual);
		}
		entityManager.flush();
		expediente.setDelitosDescription(null); // reset
	}

	public void buscaConexidad(Expediente expediente, Camara camara, Materia materia, Competencia competencia,
			TipoRadicacionEnumeration.Type tipoRadicacionPrincipal,
			Oficina oficinaActual) throws Lex100Exception {
		
		boolean asignada;
		
		if(ConexidadManager.isBuscaConexidad(tipoRadicacionPrincipal, camara, materia)){

			boolean isConexidadInformativa = ConexidadManager.isConexidadInformativa(tipoRadicacionPrincipal, camara, materia);
            boolean isNoReasignarSiCambioConexidad = ConfiguracionMateriaManager.instance().isNoReasignarSiCambioConexidad(camara, materia);

			if (isNoReasignarSiCambioConexidad || isConexidadInformativa || CamaraManager.isCamaraActualLaboral() || ExpedienteManager.instance().isIniciado(expediente) || (expediente.getConexidadDeclarada() != null) ||  !OficinaManager.instance().isMesaDeEntrada(oficinaActual)) {
				ConexidadManager.instance().buscaConexidadPorCriterios(expediente, tipoRadicacionPrincipal, null,  expediente.getParametroExpedienteList(), false, true, false, false);
			} else{
				String codigoTipoCambioAsignacion = TipoCambioAsignacionEnumeration.CONEXIDAD_DETECTADA;
				String descripcionCambioAsignacion = CreateExpedienteAction.getDescripcionCambioAsignacion(entityManager, codigoTipoCambioAsignacion, tipoRadicacionPrincipal, expediente.getMateria()); 
			 	RubrosInfo rubrosInfo = MesaSorteo.instance().calculaRubrosAsignacion(expediente, tipoRadicacionPrincipal, null, false);
				
				Sorteo sorteo = MesaSorteo.instance().buscaSorteo(oficinaActual, 
							false,
							tipoRadicacionPrincipal, 
							materia.getGrupo(), 
							materia, 
							competencia, 
							rubrosInfo.getRubroAsignacion(), 
							rubrosInfo.getRubro(),
							true);
				asignada = CreateExpedienteAction.buscaConexidadPorCriterios(entityManager, 
						expediente,
						null,
						tipoRadicacionPrincipal, 
						codigoTipoCambioAsignacion, 
						descripcionCambioAsignacion,
						ConfiguracionMateriaManager.instance().getTipoGiroAsignacion(tipoRadicacionPrincipal, expediente.getCamara(), expediente.getMateria()),
						CreateExpedienteAction.puedeCompensarPorConexidadAutomatica(expediente, tipoRadicacionPrincipal),
						true,
						sorteo,
						rubrosInfo.getRubro(),
						null,
						true,
						oficinaActual,
						true,
						false,
						null, null, null, null);
			}
		}
	}

	public static void conexidadDeclaradaChanged(EntityManager entityManager, Expediente expediente, ConexidadDeclarada conexidadDeclaradaAnterior,
			TipoRadicacionEnumeration.Type tipoRadicacion) {

		boolean conexidadDenunciadaPrioritaria = CreateExpedienteAction.isConexidadDenunciadaPrioritaria(expediente);
		
		if(conexidadDeclaradaAnterior != null){
			if (expediente.getConexidadDeclarada() != null) {
				String detalle = ConexidadDeclarada.getConexidadDeclaradaAsString(expediente.getConexidadDeclarada());
				detalle += " Anterior: " + ConexidadDeclarada.getConexidadDeclaradaAsString(conexidadDeclaradaAnterior);
				ActuacionExpManager.instance().addActuacionCambioDeclaracionConexidad(expediente.getConexidadDeclarada(), expediente, detalle);
				if (conexidadDenunciadaPrioritaria && (expediente.getConexidadDeclarada().getExpedienteConexo() != null)) {
					/**ATOS OVD */
					CreateExpedienteAction.addVinculadoConexo(entityManager, tipoRadicacion, expediente, expediente.getConexidadDeclarada().getExpedienteConexo(), TipoAsignacionEnumeration.getTipoAsignacionConexidadDenunciada(tipoRadicacion, expediente.getConexidadDeclarada()), null, null, null);
					/**ATOS OVD */
				}
			} else {
				ActuacionExpManager.instance().addActuacionBorradoDeclaracionConexidad(expediente.getConexidadDeclarada(), expediente, ConexidadDeclarada.getConexidadDeclaradaAsString(conexidadDeclaradaAnterior));
				String tipoVinculado = null;
				if(TipoRadicacionEnumeration.isAnyJuzgado(tipoRadicacion)) {
					tipoVinculado = (String)TipoVinculadoEnumeration.Type.conexo_Juzgado.getValue();
				} else if(TipoRadicacionEnumeration.isAnySala(tipoRadicacion)) {
					tipoVinculado = (String)TipoVinculadoEnumeration.Type.conexo_Sala.getValue();
				} else {
					tipoVinculado = (String)TipoVinculadoEnumeration.Type.conexo.getValue();
				}
				if (tipoVinculado != null) {
					VinculadosExp vinculadosExp = VinculadosExpManager.instance().findVinculado(tipoVinculado.toString(), expediente, conexidadDeclaradaAnterior.getExpedienteConexo());
					if (conexidadDenunciadaPrioritaria && (vinculadosExp != null) && vinculadosExp.getExpedienteRelacionado().equals(conexidadDeclaradaAnterior.getExpedienteConexo())) {
						VinculadosExpManager.instance().deleteVinculado(vinculadosExp);
					}
				}
			}
		} else if (expediente.getConexidadDeclarada() != null) {
			ActuacionExpManager.instance().addActuacionDeclaracionConexidad(conexidadDeclaradaAnterior, expediente, ConexidadDeclarada.getConexidadDeclaradaAsString(expediente.getConexidadDeclarada()));
			if (conexidadDenunciadaPrioritaria && (expediente.getConexidadDeclarada().getExpedienteConexo() != null)) {
				/**ATOS OVD */
				CreateExpedienteAction.addVinculadoConexo(entityManager, tipoRadicacion, expediente, expediente.getConexidadDeclarada().getExpedienteConexo(), TipoAsignacionEnumeration.getTipoAsignacionConexidadDenunciada(tipoRadicacion, expediente.getConexidadDeclarada()), null, null, null);
				/**ATOS OVD */
			}
		}
	}
	
	private Type getTipoRadicacionPrincipal(Expediente expediente) {
		TipoRadicacionEnumeration.Type tipoRadicacion = (TipoRadicacionEnumeration.Type) TipoRadicacionEnumeration.instance().getType(expediente.getTipoRadicacionPrincipal());
		if (tipoRadicacion == null) {
			tipoRadicacion = TipoRadicacionEnumeration.Type.juzgado;
			if (expediente.getTipoCausa() != null) {
				if (TipoInstanciaEnumeration.isApelaciones(expediente.getTipoCausa()
						.getTipoInstancia())) {
					tipoRadicacion = TipoRadicacionEnumeration.Type.sala;
				} else if (TipoInstanciaEnumeration
						.isTribunalOral(expediente.getTipoCausa().getTipoInstancia())) {
					tipoRadicacion = TipoRadicacionEnumeration.Type.tribunalOral;
				} else if (TipoCausaEnumeration.isMediacion(expediente.getTipoCausa())){
					tipoRadicacion = TipoRadicacionEnumeration.Type.juzgado;
				}else{
					tipoRadicacion = TipoRadicacionEnumeration.Type.juzgado;
				}
			}
		}
		return tipoRadicacion;
	}

	public static Boolean isConexidadDeclaradaChanged(ConexidadDeclarada conexidadDeclaradaAnterior, Expediente expediente) {
		if ((conexidadDeclaradaAnterior == null) && ExpedienteManager.hasConexidadDeclarada(expediente)) {
			return true;
		} else if (ExpedienteManager.hasConexidadDeclarada(expediente)) {
			return  !equals(expediente.getConexidadDeclarada().getNumeroExpedienteConexo(), conexidadDeclaradaAnterior.getNumeroExpedienteConexo())
					||
					!equals(expediente.getConexidadDeclarada().getAnioExpedienteConexo(),conexidadDeclaradaAnterior.getAnioExpedienteConexo())
					||
					!equals(expediente.getConexidadDeclarada().getOficina(), conexidadDeclaradaAnterior.getOficina())
					||
					!equals(nullIfEmpty(expediente.getConexidadDeclarada().getObservaciones()), nullIfEmpty(conexidadDeclaradaAnterior.getObservaciones()));
		} else {
			return (conexidadDeclaradaAnterior != null);
		}
	}

	private static String nullIfEmpty(String s) {
		return Strings.isEmpty(s)? null: s;
	}


	private static  boolean equals(Integer i1, Integer i2) {
		return AbstractManager.equals(i1, i2);
	}
	
	private static  boolean equals(String s1, String s2) {
		return AbstractManager.equals(s1, s2);
	}

	public static boolean equals(Oficina o1, Oficina o2) {
		return AbstractManager.equals(o1, o2);
	}

	public void updateDenunciaExpediente(ExpedienteMeAdd expedienteEdit) throws Lex100Exception {
		Expediente expediente = expedienteHome.getInstance();
		entityManager.flush();
		entityManager.refresh(expediente);
	}
	
	public static EditExpedienteAction instance(){
		return (EditExpedienteAction) Component.getInstance(EditExpedienteAction.class, true);
	}
	
	private String intervinientesChanged(ExpedienteMeAdd expedienteEdit) {
		StringBuffer changes = new StringBuffer();
		for(IntervinienteEdit intervinienteEdit : expedienteEdit.getIntervinienteEditList().getList()){
			if(intervinienteEdit.needUpdateOrAdd()){
				String detalle = null;
				if(!intervinienteEdit.isNew()){
					if(intervinienteEdit.getIntervinienteExpOld() != null) {
						boolean cambioDeDocumento = cambioDeDocumento(intervinienteEdit.getIntervinienteExp(), intervinienteEdit.getIntervinienteExpOld());
						if(cambioDeNombreOTipo(intervinienteEdit.getIntervinienteExp(), intervinienteEdit.getIntervinienteExpOld()) || cambioDeDocumento){
							detalle = getDetalleIntervieniente(intervinienteEdit.getIntervinienteExpOld(), cambioDeDocumento);
							detalle += " -> ";
							detalle +=  getDetalleIntervieniente(intervinienteEdit, cambioDeDocumento);
						}
					}
				} else {
					 detalle = getDetalleIntervieniente(intervinienteEdit, false);
				}
				if(detalle != null){
					if (changes.length() > 0) {
						changes.append(", ");
					}
					changes.append(detalle);
					changes.append(intervinienteEdit.isNew()? getMessages().get("intervinienteExp.added"): getMessages().get("intervinienteExp.updated"));
				}
			}
		}
	
		List<IntervinienteEdit> deletedList = expedienteEdit.getIntervinienteEditList().getDeletedEntityList();
		if(deletedList != null){
			for(IntervinienteEdit intervinienteEdit: deletedList){
				if(intervinienteEdit.needDelete()){
					if (changes.length() > 0) {
						changes.append(", ");
					}
					changes.append("'").append(intervinienteEdit.getNombreCompleto()).append("' ");
					changes.append(getMessages().get("intervinienteExp.deleted"));
				}
			}
		}
		return (changes.length() > 0)? changes.toString(): null;
	}

	private String getDetalleIntervieniente(IntervinienteEdit intervinienteEdit, boolean conDocumento) {
		String ret = "";
		if (intervinienteEdit.getTipoInterviniente() != null) {
			ret = intervinienteEdit.getTipoInterviniente().getDescripcion()+" ";
		}
		ret += "'"+intervinienteEdit.getNombreCompleto()+"' ";
		if (conDocumento && (intervinienteEdit.getTipoDocumento() != null) && !Strings.isEmpty(intervinienteEdit.getNumero())) {
			ret += " "+ intervinienteEdit.getTipoDocumento().getCodigo() + ":" + intervinienteEdit.getNumero()+" ";
		}
		return ret;
	}

	private String getDetalleIntervieniente(IntervinienteExp intervinienteExp, boolean conDocumento) {
		String ret = "";
		if (intervinienteExp.getTipoInterviniente() != null) {
			ret = intervinienteExp.getTipoInterviniente().getDescripcion() + " ";
		}
		ret += "'"+ intervinienteExp.getNombreCompleto()+"' ";
		if (conDocumento && (intervinienteExp.getInterviniente() != null) && (intervinienteExp.getInterviniente().getTipoDocumentoIdentidad() != null) && !Strings.isEmpty(intervinienteExp.getInterviniente().getNumeroDocId())) {
			ret += " "+ intervinienteExp.getInterviniente().getTipoDocumentoIdentidad().getCodigo() + ":" + intervinienteExp.getInterviniente().getNumeroDocId()+" ";
		}
		return ret;
	}

	private boolean cambioDeNombreOTipo(IntervinienteExp intervinienteExp,	IntervinienteExp intervinienteExpOld) {
		if(!equals(intervinienteExp.getTipoInterviniente(), intervinienteExpOld.getTipoInterviniente()) ||
			!equals(intervinienteExp.getNombreCompleto(), intervinienteExpOld.getNombreCompleto()) ||
			!(intervinienteExp.isMostrarIniciales() != intervinienteExpOld.isMostrarIniciales()) ||
			!equals(intervinienteExp.getIniciales(), intervinienteExpOld.getIniciales())
			){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean cambioDeRepresentacion(IntervinienteExp intervinienteExp,	IntervinienteExp intervinienteExpOld) {
		if(!equals(intervinienteExp.getRepresentacion(), intervinienteExpOld.getRepresentacion())){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean cambioDeDocumento(IntervinienteExp intervinienteExp, IntervinienteExp intervinienteExpOld) {
		if(!equals(intervinienteExp.getInterviniente().getNumeroDocId(), intervinienteExpOld.getInterviniente().getNumeroDocId())){
			return true;
		}else{
			return false;
		}
	}

	private boolean equals(TipoInterviniente t1,		TipoInterviniente t2) {
		if(t1 == null || t2 == null){
			return t1 == t2;
		}else{
			return t1.getId().equals(t2.getId());
		}
	}

	private String delitosChanged(ExpedienteMeAdd expedienteEdit) {
		StringBuffer changes = new StringBuffer();
		for(DelitoEdit delitoEdit: expedienteEdit.getDelitoEditList().getList()){
			if(delitoEdit.needUpdateOrAdd()){
				if (changes.length() > 0) {
					changes.append(", ");
				}
				changes.append("Delito '").append(delitoEdit.getCodigo()).append("' ");
				changes.append(delitoEdit.isNew()? "añadido": "modificado");
			}
		}
		List<DelitoEdit> deletedList = expedienteEdit.getDelitoEditList().getDeletedEntityList();
		if(deletedList != null){
			for(DelitoEdit delitoEdit: deletedList){
				if(delitoEdit.needDelete()){
					if (changes.length() > 0) {
						changes.append(", ");
					}
					changes.append("Delito '").append(delitoEdit.getCodigo()).append("' eliminado");
				}
			}
		}
		return (changes.length() > 0)? changes.toString(): null;
	}

	public static String objetoJuicioChanged(ObjetoJuicio objetoJuicioAnterior, String descripcionObjetoJuicioAnterior, Expediente expediente) {
		StringBuffer changes = new StringBuffer();
		String codigoObjetoJuicioAnterior = (objetoJuicioAnterior != null)? objetoJuicioAnterior.getCodigo(): null;
		String codigoObjetoJuicio = (expediente.getObjetoJuicioPrincipal() != null)? expediente.getObjetoJuicioPrincipal().getCodigo(): null;
		if (!equals(codigoObjetoJuicioAnterior, codigoObjetoJuicio)){
			changes.append("Cambio de Objeto de Juicio de " + emptyIfNull(codigoObjetoJuicioAnterior) + 
					" a " + emptyIfNull(codigoObjetoJuicio));
		} else if(!equals(descripcionObjetoJuicioAnterior, expediente.getDescripcionObjetoJuicio())) {
			changes.append("Cambio en Objeto de Juicio de '" +emptyIfNull(descripcionObjetoJuicioAnterior)+"' a '"+emptyIfNull(expediente.getDescripcionObjetoJuicio())+"'");					
		}

		return (changes.length() > 0)? changes.toString(): null;
	}
	
	/**
	 * Verifica si un tipo de proceso anterior y nuevo coinciden o no han cambiado
	 * @param tipoProcesoAnterior {@link TipoProceso} anterior
	 * @param tipoProcesoNuevo {@link TipoProceso} nuevo
	 * @return <code>true</code> en caso que sean distintos o <code>false</code> si son iguales
	 */
	public static boolean tipoProcesoChanged(TipoProceso tipoProcesoAnterior, TipoProceso tipoProcesoNuevo) {
		if (tipoProcesoAnterior!=null && tipoProcesoNuevo!=null)
			return !tipoProcesoAnterior.equals(tipoProcesoNuevo);
		return false;
	}

	private static String emptyIfNull(String s) {
		return s == null ? "" : s;
	}

	private String dependenciaOrigenChanged(String dependenciaOrigenAnterior, Expediente expediente) {
		StringBuffer changes = new StringBuffer();
		if (!AbstractManager.equals(dependenciaOrigenAnterior, (expediente.getExpedienteEspecial() != null) ? expediente.getExpedienteEspecial().getDependenciaOrigen(): null)) {
			changes.append("Cambio de Organismo Origen");
		}
		return (changes.length() > 0)? changes.toString(): null;
	}
	private String caratulaOrigenChanged(String caratulaOrigenAnterior, Expediente expediente) {
		StringBuffer changes = new StringBuffer();
		if (!AbstractManager.equals(caratulaOrigenAnterior, (expediente.getExpedienteEspecial() != null) ? expediente.getExpedienteEspecial().getCaratulaOrigen(): null)) {
			changes.append("Cambio de Caratula Origen");
		}
		return (changes.length() > 0)? changes.toString(): null;
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

	private void setDependenciaContendiente(ExpedienteEspecialEdit expedienteEspecialEdit, TipoCausa tipoCausa, String dependenciaDescription) {
		Dependencia dependencia = findDependencia(tipoCausa, dependenciaDescription);

		if (dependencia != null) {
			expedienteEspecialEdit.setDependencia2(dependencia);
		} else {
			expedienteEspecialEdit.setOtraDependencia2(dependenciaDescription);
			DependenciaContendienteEnumeration.instance().setUserInput(dependenciaDescription);
		}
	}
	
	@Transactional
	public void updateIntervinientes(IntervinienteExp intervinienteInstance, boolean omitirBusquedaConexidad) throws Lex100Exception {
		
		Expediente expediente = expedienteHome.getInstance();
		
		String intervinienteChanged = intervinienteChanged(intervinienteInstance);
		
		if (intervinienteChanged != null || cambioDeDomicilio()) {
			String tipoInformacion = TipoInformacionEnumeration.CAMBIO_INTERVINIENTE;
			if (ExpedienteManager.isPenal(expediente)) {
				tipoInformacion = TipoInformacionEnumeration.CAMBIO_CARATULA;
			}
			ExpedienteManager.instance().actualizarCaratula(expediente, tipoInformacion, intervinienteChanged);
			ExpedienteManager.instance().actualizarCaratulaCuadernosRelacionados(expediente, tipoInformacion, intervinienteChanged);
		}
		if (!omitirBusquedaConexidad && !Strings.isEmpty(intervinienteChanged)) {
			TipoRadicacionEnumeration.Type tipoRadicacionPrincipal = getTipoRadicacionPrincipal(expediente);
			Oficina oficinaActual = SessionState.instance().getGlobalOficina();
			buscaConexidad(expediente, CamaraManager.getCamaraActual(), expediente.getMateria(), expediente.getCompetencia(), tipoRadicacionPrincipal, oficinaActual);
		}
	}
	
	public void cargarIntervinienteOld(IntervinienteExp intervinienteExp){
		intervinienteEdit.setIntervinienteExpOld(new IntervinienteExp(intervinienteExp));
		intervinienteEdit.getIntervinienteExpOld().setInterviniente(new Interviniente(intervinienteExp.getInterviniente()));
	}

	private String intervinienteChanged(IntervinienteExp intervinienteInstance) {
		intervinienteEdit.setIntervinienteExp(intervinienteInstance);
		StringBuffer changes = new StringBuffer();
		
		boolean cambioDeDocumento = cambioDeDocumento(intervinienteEdit.getIntervinienteExp(), intervinienteEdit.getIntervinienteExpOld());
		if(cambioDeNombreOTipo(intervinienteEdit.getIntervinienteExp(), intervinienteEdit.getIntervinienteExpOld()) || cambioDeDocumento){
			if(intervinienteEdit.getIntervinienteExpOld() != null) {
				changes.append(getDetalleIntervieniente(intervinienteEdit.getIntervinienteExpOld(), cambioDeDocumento));
				changes.append(" -> ");
				changes.append(getDetalleIntervieniente(intervinienteEdit.getIntervinienteExp(), cambioDeDocumento));
			}
		} 
		if(changes.length()>0){
			if (changes.length() > 0) {
				changes.append(", ");
			}
			changes.append(getMessages().get("intervinienteExp.updated"));
		}

		if(intervinienteEdit.needDelete()){
			if (changes.length() > 0) {
				changes.append(", ");
			}
			changes.append("'").append(intervinienteEdit.getNombreCompleto()).append("' ");
			changes.append(getMessages().get("intervinienteExp.deleted"));
		}
	
		if(cambioDeDomicilio() && intervinienteEdit.getIntervinienteExp().getDomicilioCompleto() != null && !intervinienteEdit.getIntervinienteExp().getDomicilioCompleto().isEmpty()){
			changes.append(getDetalleIntervieniente(intervinienteInstance, false));
				if(intervinienteEdit.getIntervinienteExpOld().getDomicilioCompleto() != null){
					changes.append(" cambio domicilio " + intervinienteEdit.getIntervinienteExpOld().getDomicilioCompleto() +
								" por " + intervinienteEdit.getIntervinienteExp().getDomicilioCompleto() + " ");
				}else{
					changes.append(" cambio domicilio: " + intervinienteEdit.getIntervinienteExp().getDomicilioCompleto() + " ");
				}
				changes.append(getMessages().get("intervinienteExp.updated"));					
		}
		
		
		return (changes.length() > 0)? changes.toString(): null;
	}
	
	public boolean cambioDeDomicilio() {
		if(this.intervinienteEdit == null || this.intervinienteEdit.getIntervinienteExpOld() == null || this.intervinienteEdit.getIntervinienteExp() == null){
			return false;
		}
		
		return (!equals(intervinienteEdit.getIntervinienteExpOld().getDomicilioCompleto(), intervinienteEdit.getIntervinienteExp().getDomicilioCompleto()));
	}
	
	public boolean cambioNombre() {
		return(!equals(intervinienteEdit.getIntervinienteExpOld().getNombreCompleto(), intervinienteEdit.getIntervinienteExp().getNombreCompleto()));
	}
	
}

