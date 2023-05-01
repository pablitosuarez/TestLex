package com.base100.lex100.manager.accion;

import java.util.Date;
import java.util.Map;

import com.base100.lex100.Lex100Exception;
import com.base100.lex100.component.enumeration.TipoAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionLex100Enumeration;
import com.base100.lex100.component.enumeration.TipoGiroEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.entity.DocumentoExp;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.RecursoExp;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.manager.accion.impl.AbstractAccionExpediente;
import com.base100.lex100.manager.accion.impl.AccionException;
import com.base100.lex100.manager.accion.impl.ParameterAccionManager;
import com.base100.lex100.manager.accion.impl.ParameterFormatException;
import com.base100.lex100.mesaEntrada.ingreso.FojasEdit;

public class AccionCambioAsignacion extends AbstractAccionExpediente {
	private static final long serialVersionUID = 5174131732576988508L;

	public static final String ACCION_NAME = "cambioAsignacion";

	public static final String OFICINA = "oficina";
	public static final String TIPO_RADICACION = "tipoRadicacion";
	public static final String TIPO_ASIGNACION = "tipoAsignacion";
	public static final String TIPO_ASIGNACION_LEX100 = "tipoAsignacionLex100";
	public static final String OFICINA_ACTUAL = "oficinaActual";
	public static final String USUARIO = "usuario";
	public static final String TIPO_GIRO = "tipoGiro";
	public static final String COMPENSAR_OFICINAS = "compensarOficinas";
	public static final String RUBRO = "rubro";
	public static final String CODIGO_TIPO_CAMBIO_ASIGNACION = "codigoTipoCambioAsignacion";
	public static final String DESCRIPCION_CAMBIO_ASIGNACION = "descripcionCambioAsignacion";
	public static final String OBSERVACIONES = "observaciones";
	
	private Oficina oficina;
	private TipoGiroEnumeration.Type tipoGiro;
	private boolean compensarOficinas;
	private TipoRadicacionEnumeration.Type tipoRadicacion;
	private TipoAsignacionEnumeration.Type tipoAsignacion;
	private TipoAsignacionLex100Enumeration.Type tipoAsignacionLex100;
	private Oficina oficinaActual;
	private String rubro;
	private String codigoTipoCambioAsignacion;
	private String descripcionCambioAsignacion;
	private String observaciones;
	private String usuario;
	private Date FECHA_ASIGNACION_EN_CURSO = null;
	@Override
	public void doAccion(Map<String, Object> context, Expediente expediente,
			DocumentoExp documento) throws AccionException {
		try {
			loadParameters(context, expediente);
			doAccion(usuario, expediente, null, oficina, tipoRadicacion, tipoAsignacion, tipoAsignacionLex100, oficinaActual, tipoGiro, compensarOficinas, null, rubro, codigoTipoCambioAsignacion, descripcionCambioAsignacion, observaciones, FECHA_ASIGNACION_EN_CURSO, null);
		} catch (ParameterFormatException e) {
			String msg = "Error cargando parámetros";
			getLog().error(msg, e);
			throw new AccionException(msg, e);
		}
	}
	
	public void doAccion(String usuario, Expediente expediente, RecursoExp recursoExp, Oficina oficina, TipoRadicacionEnumeration.Type tipoRadicacion, TipoAsignacionEnumeration.Type tipoAsignacion, TipoAsignacionLex100Enumeration.Type tipoAsignacionLex100, Oficina oficinaAsignadora, TipoGiroEnumeration.Type tipoGiro, boolean compensarOficinas, Sorteo sorteo, String rubro, String codigoTipoCambioAsignacion, String descripcionCambioAsignacion, String observaciones, Date fechaAsignacion, FojasEdit fojasPase) throws AccionException {
		if (oficina != null && tipoRadicacion != null) {
			try {
				getExpedienteManager().asignarOficina(usuario, expediente, recursoExp, oficina, tipoRadicacion, tipoAsignacion, tipoAsignacionLex100, oficinaAsignadora, tipoGiro, compensarOficinas, sorteo, rubro, codigoTipoCambioAsignacion, descripcionCambioAsignacion, observaciones, fechaAsignacion, fojasPase);

				//se realiza el copiado de las radicaciones de la familia en caso que no sea sorteo. Sino la copia se hace al finalizar el proceso de la cola SorteoManager
				//se realiza la impresion de caratulas en forma automatica en caso que corresponda
				if(sorteo==null){
					getExpedienteManager().copiarRadicacionesFamilia(expediente, tipoRadicacion);
					getImpresionExpManager().imprimir(expediente, expediente.getOficinaRadicacionPrincipal(), tipoRadicacion, expediente.getOficinaActual(), recursoExp);
				}
			} catch (Lex100Exception e) {
				getLog().error(e.getMessage());
				throw new AccionException(e);				
			}
		}
	}
	
	public void cambioAsignacionOficinaEspecial(String usuario, Expediente expediente, Oficina oficina, TipoRadicacionEnumeration.Type tipoRadicacion, TipoAsignacionEnumeration.Type tipoAsignacion, Oficina oficinaAsignadora, TipoGiroEnumeration.Type tipoGiro, boolean compensarOficinas, String rubro, String codigoTipoCambioAsignacion, String descripcionCambioAsignacion, String observaciones, Date fechaAsignacion, FojasEdit fojasPase) throws AccionException {
		doAccion(usuario, expediente, null, oficina, tipoRadicacion, tipoAsignacion, null, oficinaAsignadora, tipoGiro, compensarOficinas, null, rubro, codigoTipoCambioAsignacion, descripcionCambioAsignacion, observaciones, fechaAsignacion, fojasPase);
	}
	
	private void loadParameters(Map<String, Object> context, Expediente expediente) throws ParameterFormatException {
		oficina = ParameterAccionManager.getAsOficina(OFICINA,context, null);		
		compensarOficinas = ParameterAccionManager.getAsBoolean(COMPENSAR_OFICINAS,context, null);
		
		String tipoGiroValue = ParameterAccionManager.getAsString(TIPO_GIRO,context, null);
		tipoGiro = (TipoGiroEnumeration.Type)new TipoGiroEnumeration().getType(tipoGiroValue);

		String tipoRadicacionValue = ParameterAccionManager.getAsString(TIPO_RADICACION,context, null);
		tipoRadicacion = (TipoRadicacionEnumeration.Type)new TipoRadicacionEnumeration().getType(tipoRadicacionValue);
		
		String tipoAsignacionValue = ParameterAccionManager.getAsString(TIPO_ASIGNACION,context, null);
		tipoAsignacion =(TipoAsignacionEnumeration.Type)new TipoAsignacionEnumeration().getType(tipoAsignacionValue);

		String tipoAsignacionLex100Value = ParameterAccionManager.getAsString(TIPO_ASIGNACION_LEX100,context, null);
		tipoAsignacionLex100 =(TipoAsignacionLex100Enumeration.Type)new TipoAsignacionLex100Enumeration().getType(tipoAsignacionLex100Value);

		rubro = ParameterAccionManager.getAsString(RUBRO,context, null);

		oficinaActual = ParameterAccionManager.getAsOficina(OFICINA_ACTUAL,context, null);		
		
		usuario = ParameterAccionManager.getAsString(USUARIO, context, null);
		
		codigoTipoCambioAsignacion =  ParameterAccionManager.getAsString(CODIGO_TIPO_CAMBIO_ASIGNACION, context, null);

		descripcionCambioAsignacion =  ParameterAccionManager.getAsString(DESCRIPCION_CAMBIO_ASIGNACION, context, null);

		observaciones =  ParameterAccionManager.getAsString(OBSERVACIONES, context, null);
	}
	

}
