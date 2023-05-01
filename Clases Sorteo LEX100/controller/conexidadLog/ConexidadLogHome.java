package com.base100.lex100.controller.entity.conexidadLog;

import com.base100.expand.json.JSONException;
import com.base100.expand.json.JSONObject;
import com.base100.expand.seam.framework.entity.AbstractEntityHome;
import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.expand.seam.framework.entity.LookupEntitySelectionListener;
import com.base100.lex100.controller.entity.expediente.ExpedienteSearch;
import com.base100.lex100.entity.ConexidadLog;
import com.base100.lex100.entity.Expediente;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * Class for ConexidadLog Home objects.
 * 
 * Generated by Expand.
 *
 */
@Name("conexidadLogHome")
public class ConexidadLogHome extends AbstractEntityHome<ConexidadLog>
{
    @In(create=true)
    private ExpedienteSearch expedienteSearch;
    @In(create=true)
    private ConexidadLogSearch conexidadLogSearch;

    /**
     * Set/change the entity being managed by id.
     * 
     * @param id new instance id
     */
    public void setConexidadLogId(Integer id) {
        setId(id);
    }

    /**
     * Returns the current instance id.
     * 
     * @return the current id.
     */
    public Integer getConexidadLogId() {
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
    public ConexidadLog getDefinedInstance() {
        return isIdDefined() ? getInstance() : null;
    }
 	

    @Override
	public String serializeId(ConexidadLog conexidadLog) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("Id", conexidadLog.getId());
		} catch (JSONException e) {
		}
		return jsonObject.toString(); 
	}

// Lookups


	/**
	 * Action method called from the lookup button/link to select the expediente property.   
	 * 
	 * @param toPage		the page to go
	 * @param returnPage	the page to return after selection was done. 
	 */
	public String lookupExpediente(String toPage, String returnPage) {
		lookupExpediente(returnPage); 
		return toPage;
	}

	/**
	 * Action method called from the lookup button/link to select the expediente property.   
	 * 
	 * @param returnPage	the page to return after selection was done. 
	 */
	public void lookupExpediente(String returnPage) {
		initializeLookup(expedienteSearch, new LookupEntitySelectionListener<Expediente>(returnPage, Expediente.class) {
			public void updateLookup(Expediente entity) {
				getInstance().setExpediente(entity);
			}
		});
	}

	/**
	 * Action method called from the clear lookup button/link to clear the expediente property.   
	 */
	public void clearLookupExpediente() {
		getInstance().setExpediente(null);
	}


	/**
	 * Action method called from the lookup button/link to select the search expediente property.   
	 * 
	 * @param toPage		the page to go
	 * @param returnPage	the page to return after selection was done. 
	 */
	public String lookupSearchExpediente(String toPage, String returnPage) {
		lookupSearchExpediente(returnPage); 
		return toPage;
	}

	/**
	 * Action method called from the lookup button/link to select the search expediente property.   
	 * 
	 * @param returnPage	the page to return after selection was done. 
	 */
	public void lookupSearchExpediente(String returnPage) {
		initializeLookup(expedienteSearch, new LookupEntitySelectionListener<Expediente>(returnPage, Expediente.class) {
			public void updateLookup(Expediente entity) {
				conexidadLogSearch.setExpediente(entity);
			}
		});
	}

// Validation

// Operation event callbacks

	@Override
	public void afterSelect(){
	}

	@Override
	public void afterNew(ConexidadLog conexidadLog) {
        
        // settings of default values for properties can be inserted here

	}

	private boolean checkRequired(ConexidadLog conexidadLog) {
		boolean error = false;
		return error;
	}

	@Override
	public void beforeAdd(ConexidadLog conexidadLog) throws EntityOperationException {
		boolean error = false;

		error |= checkRequired(conexidadLog);
		if (error) {
			throw new EntityOperationException();
		}
	}
	
	@Override
	public void beforeUpdate(ConexidadLog conexidadLog) throws EntityOperationException {
		boolean error = false;
		error |= checkRequired(conexidadLog);
		if (error) {
			throw new EntityOperationException();
		}
	}
	
	@Override
	public void beforeDelete(ConexidadLog conexidadLog) throws EntityOperationException {
	}

	@Override
	public void afterUpdate(ConexidadLog conexidadLog) throws EntityOperationException {
	}
	
	@Override
	public void afterAdd(ConexidadLog conexidadLog) throws EntityOperationException {
	}

	@Override
	public void afterDelete(ConexidadLog conexidadLog) throws EntityOperationException {
	}



}
