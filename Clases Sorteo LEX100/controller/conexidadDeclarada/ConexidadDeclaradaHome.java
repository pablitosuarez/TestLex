package com.base100.lex100.controller.entity.conexidadDeclarada;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage.Severity;

import com.base100.expand.json.JSONException;
import com.base100.expand.json.JSONObject;
import com.base100.expand.seam.framework.component.enumeration.ILabeledEnum;
import com.base100.expand.seam.framework.entity.AbstractEntityHome;
import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.Lex100Exception;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.enumeration.CambiaRadicacionEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoConexidadSolicitadaEnumeration;
import com.base100.lex100.component.enumeration.TipoInstanciaEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration.Type;
import com.base100.lex100.component.enumeration.TipoReasignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoVinculadoEnumeration;
import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.entity.ConexidadDeclarada;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.OficinaSorteo;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.VinculadosExp;
import com.base100.lex100.manager.actuacionExp.ActuacionExpManager;
import com.base100.lex100.manager.configuracionMateria.ConfiguracionMateriaManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.manager.oficina.OficinaManager;
import com.base100.lex100.manager.vinculadosExp.VinculadosExpManager;
import com.base100.lex100.mesaEntrada.conexidad.ConexidadManager;
import com.base100.lex100.mesaEntrada.conexidad.FamiliaManager;
import com.base100.lex100.mesaEntrada.reasignacion.ExpedienteMeReasignacion;

/**
 * Class for ConexidadDeclarada Home objects.
 * 
 * Generated by Expand.
 *
 */
@Name("conexidadDeclaradaHome")
public class ConexidadDeclaradaHome extends AbstractEntityHome<ConexidadDeclarada>
{
	
    @In(create=true)
    private ExpedienteHome expedienteHome;

	private List<Oficina> oficinasConexidadDeclarada;

	private Integer numeroAnterior;
	private Integer anioAnterior;
	private Oficina oficinaAnterior;
	private ConexidadDeclarada conexidadDeclaradaAnterior;
	private Expediente expedientenConexoAnterior;
	
	private String cambiaRadicacion = (String)CambiaRadicacionEnumeration.Type.noCambiar.getValue();
	
	private List<SelectItem> cambiaRadicacionItems;

    /**
     * Set/change the entity being managed by id.
     * 
     * @param id new instance id
     */
    public void setConexidadDeclaradaId(Integer id) {
        setId(id);
    }

    /**
     * Returns the current instance id.
     * 
     * @return the current id.
     */
    public Integer getConexidadDeclaradaId() {
        return (Integer) getId();
    }

    
    public boolean isWired() {
        return true;
    }

    /**
     * Get the managed entity, or null if the id is not defined.
     * 
     * @return the managed entity, if any.
     */
    public ConexidadDeclarada getDefinedInstance() {
        return isIdDefined() ? getInstance() : null;
    }
 	

    @Override
	public String serializeId(ConexidadDeclarada conexidadDeclarada) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("Id", conexidadDeclarada.getId());
		} catch (JSONException e) {
		}
		return jsonObject.toString(); 
	}


	public List<Oficina> getOficinasConexidadDeclarada() {
		if (oficinasConexidadDeclarada == null) {
			ExpedienteMeReasignacion.instance().init();
			if(isConexidadDenunciadaSala()) {
				ExpedienteMeReasignacion.instance().setTipoSorteoSala();
			} else {
				ExpedienteMeReasignacion.instance().setTipoSorteoJuzgado();
			}
			ExpedienteMeReasignacion.instance().getReasignacionEdit().setExpediente(ExpedienteHome.instance().getInstance());
			Oficina oficinaActual = OficinaManager.instance().getOficinaActual();
			if(oficinaActual.getCompetencia() == null){
				ExpedienteMeReasignacion.instance().getReasignacionEdit().setCompetencia(ExpedienteHome.instance().getInstance().getCompetencia());
			}
			Sorteo sorteo = null;
			try {
				sorteo = ExpedienteMeReasignacion.instance().getSorteoSinRubro(oficinaActual, ExpedienteMeReasignacion.instance().getReasignacionEdit().getCompetencia());
			} catch (Lex100Exception e) {
			}
			if(sorteo != null) {
				oficinasConexidadDeclarada = new ArrayList<Oficina>();
				for(OficinaSorteo item: sorteo.getOficinaSorteoList()){
					if (item.getStatus() != -1) {
						if(!item.getOficina().isInhabilitada()) {
							oficinasConexidadDeclarada.add(item.getOficina());
						}
					}
				}
			}
		}
		return oficinasConexidadDeclarada;
	}

	public void onChangeNumeroExpedienteConexo(ValueChangeEvent event) {
		if (event != null && event.getNewValue() != null) {
			if (!event.getNewValue().equals(getInstance().getNumeroExpedienteConexo())) {
				getInstance().setNumeroExpedienteConexo((Integer)event.getNewValue());
				if(getInstance().getAnioExpedienteConexo() != null) {
					updateConexidadDeclarada();
				}
			}
		}
	}
	
	public void onChangeAnioExpedienteConexo(ValueChangeEvent event) {
		if (event != null && event.getNewValue() != null) {
			if (!event.getNewValue().equals(getInstance().getAnioExpedienteConexo())) {
				getInstance().setAnioExpedienteConexo((Integer)event.getNewValue());
				if(getInstance().getNumeroExpedienteConexo() != null) {
					updateConexidadDeclarada();
				}
			}
		}
	}

	public boolean isUseConexidadSolicitadaMesa() {
		if ((getInstance().getExpediente() != null) && (getInstance().getExpediente().getObjetoJuicioPrincipal() != null)){
			return getInstance().getExpediente().getObjetoJuicioPrincipal().isConexidadSolicitadaMesa();
		}
		return false;
	}
	
	public List<SelectItem> getTiposConexidadSolicitada() {
		return TipoConexidadSolicitadaEnumeration.instance().getTiposConexidadSolicitada(isUseConexidadSolicitadaMesa());
	}

	public boolean hayTiposConexidadSolicitada() {
		return getTiposConexidadSolicitada().size() > 1;
	}
	
	public void updateAndCheckConexidadDeclarada() {
		updateConexidadDeclarada();
		checkConexidadDeclarada(getInstance());
	}

	public void resetConexidadDeclarada() {
		getInstance().setNumeroExpedienteConexo(null);
		getInstance().setAnioExpedienteConexo(null);
		getInstance().setOficina(null);
		getInstance().setExpedienteConexo(null);
	}

	public void updateConexidadDeclarada() {
		if(!getInstance().isAnteriorAlSistema()) {
			Oficina oficina = null;
			ConexidadManager.instance().solveExpedienteConexidadDeclarada(getInstance());
			if (getInstance().getExpedienteConexo() != null) {
				OficinaAsignacionExp radicacion = ExpedienteManager.instance().findRadicacionExpediente(getInstance().getExpedienteConexo(), getTipoRadicacion(getInstance()));
				if(radicacion != null) {
					if (getOficinasConexidadDeclarada().contains(radicacion.getOficina())){
						oficina = radicacion.getOficina();
					} else if (getOficinasConexidadDeclarada().contains(radicacion.getSecretaria())){
						oficina = radicacion.getSecretaria();
					} else {
						oficina = radicacion.getOficina();
					}
				}
			}
			getInstance().setOficina(oficina);
		} else {
			getInstance().setExpedienteConexo(null);
		}
	}

	private Type getTipoRadicacion(ConexidadDeclarada conexidadDeclarada) {
		TipoRadicacionEnumeration.Type tipoRadicacion = TipoRadicacionEnumeration.Type.juzgado;
		if (conexidadDeclarada.getExpediente() != null) {
			TipoCausa tipoCausa = conexidadDeclarada.getExpediente().getTipoCausa();
			if (tipoCausa != null){
				if(TipoInstanciaEnumeration.isApelaciones(tipoCausa.getTipoInstancia())){
					tipoRadicacion = TipoRadicacionEnumeration.Type.sala;
				}
			}
		}
		return tipoRadicacion;
	}

	public boolean isConexidadDenunciadaSala() {
		return TipoRadicacionEnumeration.isRadicacionSala(getTipoRadicacion(getInstance()));
	}
	
	
// Lookups



// Validation

// Operation event callbacks
	
	public String onAdd(String page){
		createNewInstance();
		return page;
	}	

	@Override
	public void afterSelect(){
		
		oficinaAnterior = getInstance().getOficina();
		numeroAnterior =  getInstance().getNumeroExpedienteConexo();
		anioAnterior = getInstance().getAnioExpedienteConexo();
		expedientenConexoAnterior = getInstance().getExpedienteConexo();		
		conexidadDeclaradaAnterior = new ConexidadDeclarada(getInstance());
		
		cambiaRadicacion = (String)CambiaRadicacionEnumeration.Type.noCambiar.getValue();

		oficinasConexidadDeclarada = null;
	}

	@Override
	public void afterNew(ConexidadDeclarada conexidadDeclarada) {
        
		oficinaAnterior = null;
		numeroAnterior =  null;
		anioAnterior = null;
		expedientenConexoAnterior = null;
		
		cambiaRadicacion = (String)CambiaRadicacionEnumeration.Type.noCambiar.getValue();
    
		Expediente expediente= expedienteHome.getDefinedInstance();
        if ( expediente!=null )
        {
        	conexidadDeclarada.setExpediente(expediente);
        	conexidadDeclarada.setExpedienteIngreso(expediente.getExpedienteIngreso());
        }
        
        oficinasConexidadDeclarada = null;
        // settings of default values for properties can be inserted here

	}

	public String getCambiaRadicacion() {
		return cambiaRadicacion;
	}

	public void setCambiaRadicacion(String cambiaRadicacion) {
		this.cambiaRadicacion = cambiaRadicacion;
	}

	public List<SelectItem> getCambiaRadicacionItems() {
		if (cambiaRadicacionItems == null) {
			if (ConfiguracionMateriaManager.instance().isPermitirSortearEnCambioConexo(SessionState.instance().getGlobalCamara(), null)) {
				cambiaRadicacionItems = CambiaRadicacionEnumeration.instance().getItems();
			} else {
				cambiaRadicacionItems = new ArrayList<SelectItem>();
				for (SelectItem item: CambiaRadicacionEnumeration.instance().getItems()) {
					if (!CambiaRadicacionEnumeration.instance().isSortear((String)item.getValue())) {
						cambiaRadicacionItems.add(item);
					}
				}
			}
		}
		return cambiaRadicacionItems;
	}
	
	public void errorMsg(String key, Object... params) {
		getStatusMessages().addFromResourceBundle(Severity.ERROR, key, params);		
	}
	
	private boolean checkConexidadDeclarada(ConexidadDeclarada conexidadDeclarada) {
		boolean error = false;
		
		boolean updated = false;
		
		// Si ha habido cambios se actualiza
		if (((conexidadDeclarada.getExpedienteConexo() != null) && 
				((!conexidadDeclarada.getExpedienteConexo().getNumero().equals(conexidadDeclarada.getNumeroExpedienteConexo()) ||
					!conexidadDeclarada.getAnioExpedienteConexo().equals(conexidadDeclarada.getAnioExpedienteConexo()))
					||
					conexidadDeclarada.isAnteriorAlSistema()))
			||
			(!conexidadDeclarada.isAnteriorAlSistema() && (conexidadDeclarada.getExpedienteConexo() == null) && (conexidadDeclarada.getAnioExpedienteConexo() != null)
					&& (conexidadDeclarada.getNumeroExpedienteConexo() != null))) {
			updated = true;
			updateConexidadDeclarada();
		}

		if(!conexidadDeclarada.isEmpty()) {
			if ( (conexidadDeclarada.isAnteriorAlSistema() && (conexidadDeclarada.getOficina() == null))
				|| (conexidadDeclarada.getAnioExpedienteConexo() == null)
				|| (conexidadDeclarada.getNumeroExpedienteConexo() == null) ) {
				String errKey = "expedienteMe.error.conexidadDeclarada";
				errorMsg(errKey);
				error = true;
			}
			if (!error) {
				if(!conexidadDeclarada.isAnteriorAlSistema()) {
					if (conexidadDeclarada.getExpedienteConexo() == null) {
						String errKey = "conexidad.conexidadDeclarada.expediente.notFound";
						errorMsg(errKey, String.valueOf(conexidadDeclarada.getNumeroExpedienteConexo()) + "/" + String.valueOf(conexidadDeclarada.getAnioExpedienteConexo()));
						error = true;
					} else {
						if (conexidadDeclarada.getOficina() != null) {
							if (!getOficinasConexidadDeclarada().contains(conexidadDeclarada.getOficina())) {
								errorMsg("expedienteMe.error.oficinaConexidadDeclarada", Messages.instance().get(getTipoRadicacion(conexidadDeclarada).getLabel()));
								error = true;
							}
						} else {
							String errKey = "conexidad.conexidadDeclarada.oficina.notExists";
							errorMsg(errKey, Messages.instance().get(getTipoRadicacion(conexidadDeclarada).getLabel()));
							error = true;
						}
					}
				}
			}
		}
		if(! error & updated) {
			getStatusMessages().addFromResourceBundle(Severity.WARN, "conexidad.conexidadDeclarada.comprobarDatos");		
			error = true;
		}

		return error;
	}
	
	private boolean checkRequired(ConexidadDeclarada conexidadDeclarada) {
		boolean error = false;
		return error;
	}
	
	public String asignar(String returnPage) {
		if(returnPage != null) {
			
			boolean error = checkConexidadDeclarada(getInstance());
			if(!error) {
				if(getInstance().getOficina() != null && !getInstance().getOficina().equals(ExpedienteManager.instance().getOficinaRadicacionExpediente(getInstance().getExpediente(), getTipoRadicacion(getInstance())))){			
					ExpedienteMeReasignacion.instance().init();
					if(isConexidadDenunciadaSala()) {
						ExpedienteMeReasignacion.instance().setTipoSorteoSala();
					} else {
						ExpedienteMeReasignacion.instance().setTipoSorteoJuzgado();
					}
					ExpedienteMeReasignacion.instance().getReasignacionEdit().setTipoReasignacion(TipoReasignacionEnumeration.Type.manual);
					ExpedienteMeReasignacion.instance().getReasignacionEdit().setExpediente(ExpedienteHome.instance().getInstance());
					ExpedienteMeReasignacion.instance().setForceCompensacion(true);
					Oficina oficinaActual = OficinaManager.instance().getOficinaActual();
					if(oficinaActual.getCompetencia() == null){
						ExpedienteMeReasignacion.instance().getReasignacionEdit().setCompetencia(ExpedienteHome.instance().getInstance().getCompetencia());
					}
					ExpedienteMeReasignacion.instance().getReasignacionEdit().setOficinaDestino(getInstance().getOficina());
					returnPage = ExpedienteMeReasignacion.instance().doSave1(returnPage);
					if(returnPage != null) {
						if(!isManaged()) {
							returnPage = super.doAdd(returnPage);
						} else {
							returnPage = super.doUpdate(returnPage);
						}
						if(returnPage != null) {
							return ExpedienteMeReasignacion.instance().doSaveFin(1, returnPage);
						}
					}
				} else {
					if(getInstance().getOficina() != null) {
						getStatusMessages().addFromResourceBundle(Severity.WARN, "conexidad.conexidadDeclarada.oficinaRadicacionActual");
					} else {
						
					}
				}
			}
		}
		return null;
	}

	public String sortear(String returnPage) {
		if(returnPage != null) {

			boolean error = checkConexidadDeclarada(getInstance());
			if(!error) {
				ExpedienteMeReasignacion.instance().init();
				if(isConexidadDenunciadaSala()) {
					ExpedienteMeReasignacion.instance().setTipoSorteoSala();
				} else {
					ExpedienteMeReasignacion.instance().setTipoSorteoJuzgado();
				}
				ExpedienteMeReasignacion.instance().getReasignacionEdit().setTipoReasignacion(TipoReasignacionEnumeration.Type.resorteo);
				ExpedienteMeReasignacion.instance().getReasignacionEdit().setExpediente(ExpedienteHome.instance().getInstance());
				Oficina oficinaActual = OficinaManager.instance().getOficinaActual();
				if(oficinaActual.getCompetencia() == null){
					ExpedienteMeReasignacion.instance().getReasignacionEdit().setCompetencia(ExpedienteHome.instance().getInstance().getCompetencia());
				}
				returnPage = ExpedienteMeReasignacion.instance().doSave1(returnPage);
				if(returnPage != null) {
					if(!isManaged()) {
						returnPage = super.doAdd(returnPage);
					} else {
						returnPage = super.doUpdate(returnPage);
					}
					if(returnPage != null) {
						return ExpedienteMeReasignacion.instance().doSaveFin(1, returnPage);
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public void beforeAdd(ConexidadDeclarada conexidadDeclarada) throws EntityOperationException {
		boolean error = false;

		error |= checkRequired(conexidadDeclarada);
		if (!error) {
			error |= checkConexidadDeclarada(conexidadDeclarada);
		}
		if (error) {
			throw new EntityOperationException();
		}
	}
	
	@Override
	public void beforeUpdate(ConexidadDeclarada conexidadDeclarada) throws EntityOperationException {
		boolean error = false;

		error |= checkRequired(conexidadDeclarada);
		if (!error) {
			error |= checkConexidadDeclarada(conexidadDeclarada);
		}
		if (error) {
			throw new EntityOperationException();
		}
	}
	
	@Override
	public void beforeDelete(ConexidadDeclarada conexidadDeclarada) throws EntityOperationException {
		conexidadDeclarada.getExpedienteIngreso().setConexidadDeclarada(null);
//		conexidadDeclarada.getExpediente().setConexidadDeclarada(null);
	}

	@Override
	public void afterUpdate(ConexidadDeclarada conexidadDeclarada) throws EntityOperationException {
		if((conexidadDeclarada.getOficina() != null && oficinaAnterior != null && !oficinaAnterior.getId().equals(conexidadDeclarada.getOficina().getId())) 
				|| (conexidadDeclarada.getNumeroExpedienteConexo() != null && !conexidadDeclarada.getNumeroExpedienteConexo().equals(numeroAnterior))
				|| (conexidadDeclarada.getAnioExpedienteConexo() != null && !conexidadDeclarada.getAnioExpedienteConexo().equals(anioAnterior))
				){
			String detalle = ConexidadDeclarada.getConexidadDeclaradaAsString(conexidadDeclarada);
			if(conexidadDeclaradaAnterior != null){
				detalle += " Anterior: " + ConexidadDeclarada.getConexidadDeclaradaAsString(conexidadDeclaradaAnterior);
			}
			ActuacionExpManager.instance().addActuacionCambioDeclaracionConexidad(conexidadDeclarada, conexidadDeclarada.getExpediente(), detalle);
		}
		String tipoVinculado = (String)TipoVinculadoEnumeration.Type.conexo_Juzgado.getValue();
		if (TipoRadicacionEnumeration.Type.sala.equals(getTipoRadicacion(conexidadDeclarada))) {
			tipoVinculado = (String)TipoVinculadoEnumeration.Type.conexo_Sala.getValue();
		}
		if ((expedientenConexoAnterior != null) && ((conexidadDeclarada.getExpedienteConexo() == null) || !expedientenConexoAnterior.getId().equals(conexidadDeclarada.getExpedienteConexo().getId()))) {
			VinculadosExp vinculado = VinculadosExpManager.instance().findVinculado(tipoVinculado, conexidadDeclarada.getExpediente(), expedientenConexoAnterior);
			if(vinculado != null) {
				if ((conexidadDeclarada.getExpedienteConexo() == null)) {
					VinculadosExpManager.instance().deleteVinculado(vinculado);
				} else {
					vinculado.setExpedienteRelacionado(conexidadDeclarada.getExpedienteConexo());
					FamiliaManager.instance().resetExpediente(conexidadDeclarada.getExpediente().getId());
				}
			}
		}
		if((conexidadDeclarada.getExpedienteConexo() != null) && ((expedientenConexoAnterior == null) || !expedientenConexoAnterior.getId().equals(conexidadDeclarada.getExpedienteConexo().getId())) ) {
			/**ATOS OVD */
			VinculadosExpManager.instance().createVinculado(tipoVinculado, conexidadDeclarada.getExpediente(), conexidadDeclarada.getExpedienteConexo(), null, null, (String)TipoAsignacionEnumeration.getTipoAsignacionManual(getTipoRadicacion(conexidadDeclarada)).getValue(), null, null, null);
			/**ATOS OVD */
		}
		
		getEntityManager().flush();
		numeroAnterior = conexidadDeclarada.getNumeroExpedienteConexo();
		anioAnterior = conexidadDeclarada.getAnioExpedienteConexo();
		oficinaAnterior = conexidadDeclarada.getOficina();
		expedientenConexoAnterior = conexidadDeclarada.getExpedienteConexo();
	}
	
	@Override
	public void afterAdd(ConexidadDeclarada conexidadDeclarada) throws EntityOperationException {
		conexidadDeclarada.getExpedienteIngreso().setConexidadDeclarada(conexidadDeclarada);
		ActuacionExpManager.instance().addActuacionCambioDeclaracionConexidad(conexidadDeclarada, conexidadDeclarada.getExpediente(), ConexidadDeclarada.getConexidadDeclaradaAsString(conexidadDeclarada));

		if((conexidadDeclarada.getExpedienteConexo() != null)) {
			String tipoVinculado = (String)TipoVinculadoEnumeration.Type.conexo_Juzgado.getValue();
			if (TipoRadicacionEnumeration.Type.sala.equals(getTipoRadicacion(conexidadDeclarada))) {
				tipoVinculado = (String)TipoVinculadoEnumeration.Type.conexo_Sala.getValue();
			}
			/**ATOS OVD */
			VinculadosExpManager.instance().createVinculado(tipoVinculado, conexidadDeclarada.getExpediente(), conexidadDeclarada.getExpedienteConexo(), null, null, (String)TipoAsignacionEnumeration.getTipoAsignacionManual(getTipoRadicacion(conexidadDeclarada)).getValue(), null, null, null);
			/**ATOS OVD */
		}

		getEntityManager().flush();
	}

	@Override
	public void afterDelete(ConexidadDeclarada conexidadDeclarada) throws EntityOperationException {
		ActuacionExpManager.instance().addActuacionBorradoDeclaracionConexidad(conexidadDeclarada, conexidadDeclarada.getExpediente(), ConexidadDeclarada.getConexidadDeclaradaAsString(conexidadDeclarada));

		if (conexidadDeclarada.getExpedienteConexo() != null) {
			String tipoVinculado = (String)TipoVinculadoEnumeration.Type.conexo_Juzgado.getValue();
			if (TipoRadicacionEnumeration.Type.sala.equals(getTipoRadicacion(conexidadDeclarada))) {
				tipoVinculado = (String)TipoVinculadoEnumeration.Type.conexo_Sala.getValue();
			}
			VinculadosExp vinculado = VinculadosExpManager.instance().findVinculado(tipoVinculado, conexidadDeclarada.getExpediente(), conexidadDeclarada.getExpedienteConexo());
			if(vinculado != null) {
				VinculadosExpManager.instance().deleteVinculado(vinculado);
			}
		}
		getEntityManager().flush();
	}

}

