package com.base100.lex100.mesaEntrada.asignacion;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jboss.seam.Component;

import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;



public class RadicacionFamiliaPreviaInfo {

	private OficinaAsignacionExp oficinaAsignacionExp;
	private boolean excluida;
	private String label;
	

	public RadicacionFamiliaPreviaInfo(OficinaAsignacionExp oficinaAsignacionExp) {
		this.oficinaAsignacionExp = oficinaAsignacionExp;
	}

	public OficinaAsignacionExp getOficinaAsignacionExp() {
		return oficinaAsignacionExp;
	}

	public void setOficinaAsignacionExp(OficinaAsignacionExp oficinaAsignacionExp) {
		this.oficinaAsignacionExp = oficinaAsignacionExp;
	}
	
	public String getLabel(){
		if (label == null) {
			StringBuffer sb = new StringBuffer();
			if(this.oficinaAsignacionExp == null){
				sb.append("Sortear");
			}else{
				if(this.oficinaAsignacionExp.getOficina() != null){
					ExpedienteManager expedienteManager = ((ExpedienteManager)Component.getInstance(ExpedienteManager.class, true));
					sb.append(CamaraManager.getOficinaAbreviada(oficinaAsignacionExp.getOficinaConSecretaria()));
					if(oficinaAsignacionExp.getFechaAsignacion() != null){
						sb.append(" - ");
						sb.append(new SimpleDateFormat("dd/MM/yyyy").format(oficinaAsignacionExp.getFechaAsignacion()));
					}
					if(oficinaAsignacionExp.getExpediente() != null){
						sb.append(" - ");
						sb.append(expedienteManager.getIdxAnaliticoFirst(oficinaAsignacionExp.getExpediente()));
						sb.append(" - ");
						sb.append(expedienteManager.getCaratulaResumida(oficinaAsignacionExp.getExpediente().getCaratula(), 70));
					}
				}
			}
			label =  sb.toString();
		}
		return label;
	}

	public boolean isExcluida() {
		return excluida;
	}

	public void setExcluida(boolean excluida) {
		this.excluida = excluida;
	}

	public String getCaratula(){
		String ret = null;
		if(this.oficinaAsignacionExp != null && this.oficinaAsignacionExp.getExpediente() != null){
			ret = ExpedienteManager.instance().getCaratulaResumida(oficinaAsignacionExp.getExpediente().getCaratula(), 70);
		}
		return ret;
		
	}
	public Date getFecha(){		
		Date ret = null;
		if(this.oficinaAsignacionExp != null){
			ret = this.oficinaAsignacionExp.getFechaAsignacion();
		}
		return ret;		
	}
	
	public String getDescripcionRadicacion(){		
		String ret = null;
		if(this.oficinaAsignacionExp != null && this.oficinaAsignacionExp.getOficinaConSecretaria() != null){
			ret = oficinaAsignacionExp.getDescripcionOficinaRadicacion();
		}
		return ret;
	}
}
