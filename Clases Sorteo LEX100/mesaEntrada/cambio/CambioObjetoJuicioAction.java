package com.base100.lex100.mesaEntrada.cambio;

import java.util.Date;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.Controller;

import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.entity.DocumentoExp;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.manager.AbstractManager;

@Name("CambioObjetoJuicioAction")
public class CambioObjetoJuicioAction extends AbstractManager  {
	@In(create=true)
	ExpedienteHome expedienteHome;

	private ObjetoJuicio objetoJuicioNuevo;

	public ObjetoJuicio getObjetoJuicioNuevo() {
		return objetoJuicioNuevo;
	}

	public void setObjetoJuicioNuevo(ObjetoJuicio objetoJuicioNuevo) {
		this.objetoJuicioNuevo = objetoJuicioNuevo;
	}
	
	private void doCambio() {
		cambiarObjetoJuicio(expedienteHome.getInstance(), getObjetoJuicioNuevo(), new Date());
	}
	@Transactional
	public boolean cambiarObjetoJuicio(Expediente expediente,
			ObjetoJuicio objetoJuicioNuevo, Date fechaCambio) {
		if ((objetoJuicioNuevo != null) && !objetoJuicioNuevo.equals(expediente.getObjetoJuicioPrincipal())) {
			try {
				if (fechaCambio != null) {
					getActuacionExpManager().setFechaActuacionVirtual(fechaCambio);
				}
//				getActuacionExpManager().addActuacionCambioObjetoJuicio(expediente, objetoJuicioNuevo, null);
				expediente.setObjetoJuicioPrincipal(objetoJuicioNuevo);
			} finally {
				getActuacionExpManager().setFechaActuacionVirtual(null);
			}
			getEntityManager().flush();
		}
		return true;
	}
}
