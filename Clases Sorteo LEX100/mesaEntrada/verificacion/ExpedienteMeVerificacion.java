package com.base100.lex100.mesaEntrada.verificacion;

import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.entity.LookupEntitySelectionListener;
import com.base100.lex100.Lex100Exception;
import com.base100.lex100.component.enumeration.TipoVerificacionMesaEnumeration;
import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.controller.entity.expediente.ExpedienteList;
import com.base100.lex100.controller.entity.expediente.ExpedienteListState;
import com.base100.lex100.controller.entity.expediente.ExpedienteSearch;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.mesaEntrada.ingreso.EditExpedienteAction;
import com.base100.lex100.mesaEntrada.ingreso.ScreenController;


@Name("expedienteMeVerificacion")
@Scope(ScopeType.CONVERSATION)
public class ExpedienteMeVerificacion extends ScreenController
{
	
	@In(create=true)
	private EntityManager entityManager;
	
	@In(create=true)
	ExpedienteHome expedienteHome;
	@In(create=true)
	ExpedienteList expedienteList;
	@In(create=true)
	ExpedienteSearch expedienteSearch;	
	
	private Integer numeroExpediente;
 	private Integer anioExpediente;
 	private Expediente expediente;
 	private List<Expediente> familiaExpedientes;
	
	@Create
	public void init() {
	    setNumeroExpediente(null);
	    setAnioExpediente(null);
	    setExpediente(null);
	    familiaExpedientes = null;
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

	public void onChangeAnioExpedienteQueja(ValueChangeEvent event) {
		setAnioExpediente((Integer)(event.getNewValue()));
	}

	public String getCodigoSubexpediente() {
		String codigoSubexpediente = null;
		if (getExpediente() != null) {
			codigoSubexpediente = getExpediente().getCodigoSubexpediente();
			if (Strings.isEmpty(codigoSubexpediente) && (getExpediente().getNumeroSubexpediente() != null) && getExpediente().getNumeroSubexpediente() > 0) {
				codigoSubexpediente = getExpediente().getNumeroSubexpediente().toString();
			}
		}
		return codigoSubexpediente;
	}
	
	private void updateExpediente() {
		Expediente selected = null;
		if ((this.numeroExpediente != null) && (this.anioExpediente != null)) {
			familiaExpedientes = findExpedientes();
			if (familiaExpedientes.size() == 1) {
				selected = familiaExpedientes.get(0);
			}
		} else {
			familiaExpedientes = null;
		}
		setExpediente(selected);
	}
	
	private List<Expediente> findExpedientes() {
		expedienteSearch.init();
		addExpedienteFilters();
		expedienteSearch.updateFilters();
		return getExpedienteList().getAllResults();
	}
	
	public Expediente getExpediente() {
		return expediente;
	}

	public void setExpediente(Expediente expediente) {
		if ((expediente != null) && !expediente.equals(this.expediente)){ 
			expediente.reset();
			ExpedienteHome expedienteHome = (ExpedienteHome)Component.getInstance(ExpedienteHome.class, true);
			expedienteHome.setId(expediente.getId());
			this.expediente = expediente;
		}
		this.expediente = expediente;
		
	}

	
	public String lookupExpediente(String toPage, String returnPage) {
		expedienteSearch.init();
		setExpediente(null);
		addExpedienteFilters();
		expedienteSearch.updateFilters();
		expedienteSearch.setShowClearSearchButton(false);
		expedienteHome.initializeLookup(expedienteSearch, new LookupEntitySelectionListener<Expediente>(returnPage, Expediente.class) {
			public void updateLookup(Expediente entity) {
				setExpediente(entity);
				setNumeroExpediente(null);
				setAnioExpediente(null);
			}
		});
		return toPage;
	}
    
	public String doSelectExpediente(Expediente expediente) {
		setExpediente(expediente);
		return doSave1();
	}
	
	
	private void addExpedienteFilters() {
		expedienteSearch.initForMediacion(false); // @Fixme No hay verificacioon para extes de mediacion?
		expedienteSearch.setUseFilterByNumero(false);
		if (getNumeroExpediente() != null) {
			expedienteSearch.setCodigoBarras(getNumeroExpediente().toString());
		} else {
			expedienteSearch.setNumero(null);
		}
		expedienteSearch.setAnio(getAnioExpediente());
		
		if (expedienteSearch.getCodigoBarras() != null || expedienteSearch.getAnio() != null || expedienteSearch.getNumero() != null){
			ExpedienteListState.instance().setShowList(true);
		}
		expedienteSearch.setMostrarTodosExpedientes(true);
		expedienteSearch.setTipoVerificadoMesa((String)TipoVerificacionMesaEnumeration.Type.nuevos.getValue());
	}
	
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	private ExpedienteList getExpedienteList() {
		return expedienteList;
	}
	
	public List<Expediente> getFamiliaExpedientes() {
		return familiaExpedientes;
	}

	public void setFamiliaExpedientes(List<Expediente> familiaExpedientes) {
		this.familiaExpedientes = familiaExpedientes;
	}
	
	
	public String doSave1(){
		String page = null;
		boolean error = false;
		
		if ((getExpediente() == null)  || !getExpediente().getNumero().equals(getNumeroExpediente()) || !getExpediente().getAnio().equals(getAnioExpediente())){
			updateExpediente();
		}
	
		if (getExpediente() == null) {
			if(getFamiliaExpedientes() == null || getFamiliaExpedientes().size() == 0){
				errorMsg("expedienteMeVerificacion.error.expedienteRequerido");
				error = true;
			}else{
				warnMsg("expedienteMeVerificacion.error.variosExpedientes");
				error = true;
			}
		} else if((!ExpedienteManager.instance().isSituacionEnOficinaORemision(expediente) && ExpedienteManager.instance().isIniciado(expediente)) || ExpedienteManager.instance().isLegajoPersonalidad(expediente) || !CamaraManager.isMesaEntradaActiva()){
			errorMsg("expedienteMeVerificacion.error.expedienteRequerido");
			error = true;						
		}
		
		if(!error){
			try {
				EditExpedienteAction.instance().prepareToVerifyExpediente(this.expediente);
				page = "/webCustom/mesaEntrada/expediente/add/expedienteMeAdd-1.xhtml";
			} catch (Lex100Exception e) {
				e.printStackTrace();
			}
		}

		return page;
	}
	
}
