package com.base100.lex100.controller.entity.turnoFiscaliaSala;

import com.base100.expand.json.JSONException;
import com.base100.expand.json.JSONObject;
import com.base100.expand.seam.framework.entity.AbstractEntityHome;
import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.TurnoFiscaliaSala;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessage;

/**
 * Class for TurnoFiscaliaSala Home objects.
 * 
 * Generated by Expand.
 *
 */
@Name("turnoFiscaliaSalaHome")
public class TurnoFiscaliaSalaHome extends AbstractEntityHome<TurnoFiscaliaSala>
{

    /**
     * Set/change the entity being managed by id.
     * 
     * @param id new instance id
     */
    public void setTurnoFiscaliaSalaIdTurnoFiscaliaSala(Integer id) {
        setId(id);
    }

    /**
     * Returns the current instance id.
     * 
     * @return the current id.
     */
    public Integer getTurnoFiscaliaSalaIdTurnoFiscaliaSala() {
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
    public TurnoFiscaliaSala getDefinedInstance() {
        return isIdDefined() ? getInstance() : null;
    }
 	

    @Override
	public String serializeId(TurnoFiscaliaSala turnoFiscaliaSala) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("IdTurnoFiscaliaSala", turnoFiscaliaSala.getIdTurnoFiscaliaSala());
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
	public void afterNew(TurnoFiscaliaSala turnoFiscaliaSala) {
        Camara camara=(Camara)evaluateElExpression("#{sessionState.globalObjects.camara}");
        if ( camara!=null )
        {
			turnoFiscaliaSala.setCamara(getEntityManager().merge(camara));
        }
        
        // settings of default values for properties can be inserted here

	}

	private boolean checkRequired(TurnoFiscaliaSala turnoFiscaliaSala) {
		boolean error = false;
		if (turnoFiscaliaSala.getCodFiscaliaPrimera() == null) {
			error = true;
			addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['turnoFiscaliaSala.codFiscaliaPrimera']} :" + org.jboss.seam.international.Messages.instance().get("error_notnull"));
		}
		if (turnoFiscaliaSala.getCodFiscaliaSegunda() == null) {
			error = true;
			addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['turnoFiscaliaSala.codFiscaliaSegunda']} :" + org.jboss.seam.international.Messages.instance().get("error_notnull"));
		}
		if (turnoFiscaliaSala.getCompetencia() == null) {
			error = true;
			addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['turnoFiscaliaSala.competencia']} :" + org.jboss.seam.international.Messages.instance().get("error_notnull"));
		}
		if (turnoFiscaliaSala.getCamara() == null) {
			error = true;
			addStatusMessage(StatusMessage.Severity.ERROR, "#{messages['turnoFiscaliaSala.camara']} :" + org.jboss.seam.international.Messages.instance().get("error_notnull"));
		}
		return error;
	}

	@Override
	public void beforeAdd(TurnoFiscaliaSala turnoFiscaliaSala) throws EntityOperationException {
		boolean error = false;

		error |= checkRequired(turnoFiscaliaSala);
		if (error) {
			throw new EntityOperationException();
		}
	}
	
	@Override
	public void beforeUpdate(TurnoFiscaliaSala turnoFiscaliaSala) throws EntityOperationException {
		boolean error = false;
		error |= checkRequired(turnoFiscaliaSala);
		if (error) {
			throw new EntityOperationException();
		}
	}
	
	@Override
	public void beforeDelete(TurnoFiscaliaSala turnoFiscaliaSala) throws EntityOperationException {
	}

	@Override
	public void afterUpdate(TurnoFiscaliaSala turnoFiscaliaSala) throws EntityOperationException {
	}
	
	@Override
	public void afterAdd(TurnoFiscaliaSala turnoFiscaliaSala) throws EntityOperationException {
	}

	@Override
	public void afterDelete(TurnoFiscaliaSala turnoFiscaliaSala) throws EntityOperationException {
	}



}

