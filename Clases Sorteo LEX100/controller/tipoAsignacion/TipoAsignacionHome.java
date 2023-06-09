package com.base100.lex100.controller.entity.tipoAsignacion;

import com.base100.expand.json.JSONException;
import com.base100.expand.json.JSONObject;
import com.base100.expand.seam.framework.entity.AbstractEntityHome;
import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.entity.TipoAsignacion;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessage;

/**
 * Class for TipoAsignacion Home objects.
 * 
 * Generated by Expand.
 *
 */
@Name("tipoAsignacionHome")
public class TipoAsignacionHome extends AbstractEntityHome<TipoAsignacion>
{
    @In(create=true)
    private TipoAsignacionSearch tipoAsignacionSearch;

    /**
     * Set/change the entity being managed by id.
     * 
     * @param id new instance id
     */
    public void setTipoAsignacionId(Integer id) {
        setId(id);
    }

    /**
     * Returns the current instance id.
     * 
     * @return the current id.
     */
    public Integer getTipoAsignacionId() {
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
    public TipoAsignacion getDefinedInstance() {
        return isIdDefined() ? getInstance() : null;
    }
 	

    @Override
	public String serializeId(TipoAsignacion tipoAsignacion) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("Id", tipoAsignacion.getId());
		} catch (JSONException e) {
		}
		return jsonObject.toString(); 
	}

// Lookups

// Validation

// Operation event callbacks

	@Override
	public void afterSelect(){
	}

	@Override
	public void afterNew(TipoAsignacion tipoAsignacion) {
        
        // settings of default values for properties can be inserted here

	}

	private boolean checkRequired(TipoAsignacion tipoAsignacion) {
		boolean error = false;
		if (tipoAsignacion.getCodigo() == null) {
			error = true;
			addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['tipoAsignacion.codigo']} :" + org.jboss.seam.international.Messages.instance().get("error_notnull"));
		}
		if (tipoAsignacion.getDescripcion() == null) {
			error = true;
			addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['tipoAsignacion.descripcion']} :" + org.jboss.seam.international.Messages.instance().get("error_notnull"));
		}
		return error;
	}

	@Override
	public void beforeAdd(TipoAsignacion tipoAsignacion) throws EntityOperationException {
		boolean error = false;
		if (tipoAsignacionSearch.findByUuid(tipoAsignacion.getUuid()) != null){
			error = true;
			addStatusMessage(StatusMessage.Severity.ERROR, org.jboss.seam.international.Messages.instance().get("error_duplicated_id") + " ("  + "UUID" + ")");
		}

		error |= checkRequired(tipoAsignacion);
		if (error) {
			throw new EntityOperationException();
		}
	}
	
	@Override
	public void beforeUpdate(TipoAsignacion tipoAsignacion) throws EntityOperationException {
		boolean error = false;
		error |= checkRequired(tipoAsignacion);
		if (error) {
			throw new EntityOperationException();
		}
	}
	
	@Override
	public void beforeDelete(TipoAsignacion tipoAsignacion) throws EntityOperationException {
	}

	@Override
	public void afterUpdate(TipoAsignacion tipoAsignacion) throws EntityOperationException {
	}
	
	@Override
	public void afterAdd(TipoAsignacion tipoAsignacion) throws EntityOperationException {
	}

	@Override
	public void afterDelete(TipoAsignacion tipoAsignacion) throws EntityOperationException {
	}



}

