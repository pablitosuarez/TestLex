package com.base100.lex100.mesaEntrada.loader;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import jxl.Cell;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.util.Strings;

import com.base100.lex100.component.enumeration.TipoInstanciaEnumeration;
import com.base100.lex100.component.enumeration.TipoOficinaEnumeration;
import com.base100.lex100.entity.EstadoExpediente;
import com.base100.lex100.entity.EstadoInterviniente;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.TipoInstancia;
import com.base100.lex100.entity.TipoOficina;

public class EstadoExpedienteLoader extends AbstractConfigurationLoader {

	private static final String ASTERISCO_VALUE = "*";
	
	private Materia materia;
	private boolean applyToAnyMateria = false;
	
	public EstadoExpedienteLoader(EntityManager entityManager, StatusMessages statusMessages) {
		super(entityManager, statusMessages);
	}
	
	@Override
	protected void init() {
	}

	@Override
	protected void processRow(Cell[] row, int numRow) throws LoaderException {
		if (isMateria(row)) {
			cargarMateria(row);
		} else if (isEstadoExpediente(row)) {
			cargarEstadoExpediente(row);
		} else if (isCorrespondencia(row)) {
			cargarCorrespondenciaEstados(row);
		}
	}

	private void cargarEstadoExpediente(Cell[] row) {
		//int off = row.length >= 24 ? 1 : 0; // Nueva columna para corte Suprema;
		boolean fiscalias = materia.getAbreviatura().equals("SS");
		int off = materia.getAbreviatura().equals("SU") || fiscalias ? 1 : 0;
		
		String m = getRowData(row, 1, true, null);
		String j = getRowData(row, 2, true, null);
		String c = getRowData(row, 3, true, null);
		String t = getRowData(row, 4, true, null);
		String e = getRowData(row, 5, true, null);
		String s = getRowData(row, 6, true, null);
		String cs = getRowData(row, 7, true, null);
		
		String su = off == 1 && !fiscalias ? getRowData(row, 8, true, null) : null;
		String fi = off == 1 && fiscalias ? getRowData(row, 8, true, null) : null;
		String codigoObjeto = getRowData(row, 8+off, true, ASTERISCO_VALUE);
		String descripcionObjeto = getRowData(row, 9+off, true, null);
		String ep = getRowData(row, 10+off, true, null);
		String be = getRowData(row, 11+off, true, null);
		String li = getRowData(row, 12+off, true, null);
		String codigoSeleccionObjeto = getRowData(row, 13+off, true, null); // disparador			
		String estadoIntervinienteObjeto = getRowData(row, 14+off, true, null);			
		String ambitoAplicacionObjeto = getRowData(row, 15+off, true, null);			
		String movimientoDetencionesObjeto = getRowData(row, 16+off, true, null);			
		String bajaLogicaObjeto = getRowData(row, 17+off, true, null);			
		String tipoFinalizacion = getRowData(row, 18+off, true, null);
		String tipoEstado = getRowData(row, 19+off, true, null);
		String situacion = getRowData(row, 20+off, true, null);
		String detalle = getRowData(row, 21+off, true, null);
		String publico = getRowData(row, 22+off, true, null);
		String copiarEnPrincipal = getRowData(row, 23+off, true, null);
		if (!Strings.isEmpty(descripcionObjeto)) {
			List<EstadoExpediente> estadoExpedienteList;
			if (codigoObjeto.equals(ASTERISCO_VALUE)) {
				estadoExpedienteList = null;
			} else {
				if(applyToAnyMateria){
					estadoExpedienteList =
						getEntityManager().createQuery("from EstadoExpediente where materia is null and codigo = :codigo and status = 0 order by id")
							.setParameter("codigo", codigoObjeto)
							.getResultList();
				}else{
					estadoExpedienteList =
						getEntityManager().createQuery("from EstadoExpediente where materia = :materia and codigo = :codigo and status = 0 order by id")
							.setParameter("materia", materia)
							.setParameter("codigo", codigoObjeto)
							.getResultList();
					
				}
			}
			EstadoExpediente estadoExpediente = null;
			if (estadoExpedienteList != null && estadoExpedienteList.size() > 0) {
				estadoExpediente = estadoExpedienteList.get(0);
			} else if (!Strings.isEmpty(descripcionObjeto)){
				if(applyToAnyMateria){
					estadoExpedienteList =
						getEntityManager().createQuery("from EstadoExpediente where materia is null and descripcion = :descripcion order by id")
							.setParameter("descripcion", descripcionObjeto)
							.getResultList();
				}else{
					estadoExpedienteList =
						getEntityManager().createQuery("from EstadoExpediente where materia = :materia and descripcion = :descripcion order by id")
							.setParameter("materia", materia)
							.setParameter("descripcion", descripcionObjeto)
							.getResultList();
					
				}
				if (estadoExpedienteList.size() > 0) {
					estadoExpediente = estadoExpedienteList.get(0);
					codigoObjeto = estadoExpediente.getCodigo();
				} 
			}
			
			
			if (estadoExpediente == null) {
				estadoExpediente = new EstadoExpediente();
				estadoExpediente.setMateria(materia);
				if (codigoObjeto.equals(ASTERISCO_VALUE))  {
					codigoObjeto = calculaNuevoCodigo(descripcionObjeto, materia);
				}
				estadoExpediente.setCodigo(codigoObjeto);
			} 
			estadoExpediente.setDescripcion(descripcionObjeto);
			estadoExpediente.setTipoInstanciaList(solveTipoInstanciaList(m,j,c,t,e,s,cs,su)); 
			estadoExpediente.setTipoOficinaList(solveTipoOficinaList(fi)); 
			estadoExpediente.setGeneraEstadoProcesal(solveAsterisco(ep));
			estadoExpediente.setBandejaEntrada(solveAsterisco(be));
			estadoExpediente.setLibertad(solveAsterisco(li));
			estadoExpediente.setCodigoSeleccion(codigoSeleccionObjeto);
			estadoExpediente.setEstadoInterviniente(solveEstadoInterviniente(estadoIntervinienteObjeto));
			estadoExpediente.setAmbitoAplicacion(ambitoAplicacionObjeto);
			estadoExpediente.setDetenciones(solveDetenciones(movimientoDetencionesObjeto));
			estadoExpediente.setBajaLogica(solveBajaLogica(bajaLogicaObjeto));
			estadoExpediente.setTipoFinalizacion(solveTipoFinalizacion(tipoFinalizacion));
			estadoExpediente.setPublico(solvePublico(publico));
			estadoExpediente.setCopiaEnPrincipal(solveCopiaEnPrincipal(copiarEnPrincipal));
			
			estadoExpediente.setLogicalDeleted(estadoExpediente.isBajaLogica());
			if (!Strings.isEmpty(tipoEstado)) {
				estadoExpediente.setTipo(tipoEstado);
			} else {
				estadoExpediente.setTipo(null);
			}
			if (!Strings.isEmpty(situacion)) {
				estadoExpediente.setSituacionAsociada(situacion);
			} else {
				estadoExpediente.setSituacionAsociada(null);
			}
			estadoExpediente.setDetalle(ASTERISCO_VALUE.equals(detalle));
			if (estadoExpediente.getId() == null) {
				getEntityManager().persist(estadoExpediente);
			}
		}
	}

	private List<TipoOficina> solveTipoOficinaList(String fi) {
		List<TipoOficina> result = new ArrayList<TipoOficina>();
		List<Integer> codigos = new ArrayList<Integer>();
		if (!Strings.isEmpty(fi)) {
			codigos.add(TipoOficinaEnumeration.TIPO_OFICINA_FISCALIA);
		}
 		if (codigos.size() > 0) {
	 		result = getEntityManager().createQuery("from TipoOficina where codigo in (:codigos) and status = 0")
 			.setParameter("codigos", codigos)
 			.getResultList();
 		}
		return result;
	}

	private void cargarCorrespondenciaEstados(Cell[] row) {
		String codigoEstadoInicial = getRowData(row, 1, true, null);
		String codigoEstadoFinal = getRowData(row, 2, true, null);

		EstadoExpediente estadoInicial = (EstadoExpediente) getEntityManager().createQuery("from EstadoExpediente where materia = :materia and codigo = :codigo and status = 0")
		.setParameter("materia", materia).setParameter("codigo", codigoEstadoInicial).getSingleResult();
		EstadoExpediente estadoFinal = (EstadoExpediente) getEntityManager().createQuery("from EstadoExpediente where materia = :materia and codigo = :codigo and status = 0")
		.setParameter("materia", materia).setParameter("codigo", codigoEstadoFinal).getSingleResult();
		
		estadoInicial.getEstadoExpedienteList().add(estadoFinal);

		getEntityManager().persist(estadoInicial);
	
	}

	private boolean solvePublico(String publico) {
		return publico != null && publico.equals("S");
	}

	private boolean solveCopiaEnPrincipal(String value) {
		return value != null && value.equals("S");
	}

	private String solveTipoFinalizacion(String tipoFinalizacion) {
		if (tipoFinalizacion != null) {
			tipoFinalizacion = tipoFinalizacion.toUpperCase();
		}
		return tipoFinalizacion;
	}

	public String getRowData(Cell[] row, int pos, boolean uppercase, String defaultValue) {
		String result = row.length > pos ? row[pos].getContents().trim(): null;
		if (! Strings.isEmpty(result)) {
			result = result.trim();
			if (uppercase) {
				result = result.toUpperCase();
			}
		} else {
			result = defaultValue;
		}
		return result;
	}
	
	private void cargarMateria(Cell[] row) {
		String abreviaturaMateria = getContent(row, 1);
		if(ASTERISCO_VALUE.equals(abreviaturaMateria)){
			applyToAnyMateria = true;
			materia = null;
		}else{
			List<Materia> materiaList =
			getEntityManager().createQuery("from Materia where abreviatura=:abreviatura and status <> -1")
				.setParameter("abreviatura", abreviaturaMateria)
				.getResultList();
			if (materiaList.size() > 0) {
				materia = materiaList.get(0);
			}
		}
	}

	private boolean isEstadoExpediente(Cell[] row) {
		String tipo = getContent(row, 0);
		return tipo.equals("Estado Expediente");
	}

	private boolean isMateria(Cell[] row) {
		String tipo = getContent(row, 0);
		return tipo.equals("Materia");
	}

	private boolean isCorrespondencia(Cell[] row) {
		String tipo = getContent(row, 0);
		return tipo.equals("Correspondencia");
	}

	@Override
	protected void end() {
		getEntityManager().flush();
	}

	private String calculaNuevoCodigo(String descripcionObjeto, Materia materia) {
		int len = Math.min(3, descripcionObjeto.length());
		String abreviatura = descripcionObjeto.substring(0, len);
		Character suffix = null;
		while (existeEstado(abreviatura, suffix, materia)) {
			if (suffix == null) {
				suffix = '1';
			} else if (suffix == '9') {
				suffix = 'A';
			} else {
				suffix++;
			}
		}
		if (suffix != null) {
			abreviatura += suffix;
		}
		return abreviatura;
	}

	private boolean existeEstado(String abreviatura, Character suffix, Materia materia) {
		if (suffix != null) {
			abreviatura += suffix;
		}
		Long count;

		if(materia == null){
			count = (Long)	getEntityManager().createQuery("select count(*) from EstadoExpediente where materia is null and codigo = :abreviatura and status = 0")
				.setParameter("abreviatura", abreviatura)
				.getSingleResult();
		}else{
			count = (Long)	getEntityManager().createQuery("select count(*) from EstadoExpediente where materia = :materia and codigo = :abreviatura and status = 0")
			.setParameter("materia", materia)
			.setParameter("abreviatura", abreviatura)
			.getSingleResult();
		}
			
		return count != null && count > 0;
	}

	private boolean solveBajaLogica(String bajaLogicaObjeto) {
		return bajaLogicaObjeto != null && bajaLogicaObjeto.equals("B");
	}

	private boolean solveDetenciones(String movimientoDetencionesObjeto) {
		return movimientoDetencionesObjeto != null && movimientoDetencionesObjeto.equals("S");
	}

	private EstadoInterviniente solveEstadoInterviniente(
			String estadoIntervinienteObjeto) {
		EstadoInterviniente result = null;
		if (!Strings.isEmpty(estadoIntervinienteObjeto)) {
			List<EstadoInterviniente> estadoIntervinienteList =
			getEntityManager().createQuery("from EstadoInterviniente where codigo = :codigo and status <> -1")
				.setParameter("codigo", estadoIntervinienteObjeto)
				.getResultList();
			if (estadoIntervinienteList.size() > 0) {
				result = estadoIntervinienteList.get(0);
			}
		}
		return result;
	}

	private boolean solveAsterisco(String datos) {
		if (!Strings.isEmpty(datos) && datos.equals(ASTERISCO_VALUE)) {
			return true;			
		}
		return false;
	}

	private List<TipoInstancia> solveTipoInstanciaList(String m, String j, String c, String t, String e, String s, String cs, String su) {
		List<TipoInstancia> result = new ArrayList<TipoInstancia>();
		List<String> abreviaturas = new ArrayList<String>();
		if (!Strings.isEmpty(m)) {
			abreviaturas.add(TipoInstanciaEnumeration.TIPO_INSTANCIA_MESA_ABREVIATURA);
		}
		if (!Strings.isEmpty(j)) {
			abreviaturas.add(TipoInstanciaEnumeration.TIPO_INSTANCIA_PRIMERA_INSTANCIA_ABREVIATURA);
		}
		if (!Strings.isEmpty(c)) {
			abreviaturas.add(TipoInstanciaEnumeration.TIPO_INSTANCIA_APELACIONES_ABREVIATURA);
		}
		if (!Strings.isEmpty(t)) {
			abreviaturas.add(TipoInstanciaEnumeration.TIPO_INSTANCIA_TRIBUNAL_ORAL_ABREVIATURA);
		}
		if (!Strings.isEmpty(e)) {
			abreviaturas.add(TipoInstanciaEnumeration.TIPO_INSTANCIA_EJECUCION_ABREVIATURA);
		}
		if (!Strings.isEmpty(s)) {
			abreviaturas.add(TipoInstanciaEnumeration.TIPO_INSTANCIA_SECRETARIA_ESPECIAL_ABREVIATURA);
		}
		if (!Strings.isEmpty(cs)) {
			abreviaturas.add(TipoInstanciaEnumeration.TIPO_INSTANCIA_CASACION_ABREVIATURA);
		}
		if (!Strings.isEmpty(su)) {
			abreviaturas.add(TipoInstanciaEnumeration.TIPO_INSTANCIA_CORTESUPREMA_ABREVIATURA);
		}
 		if (abreviaturas.size() > 0) {
	 		result = getEntityManager().createQuery("from TipoInstancia where abreviatura in (:abreviaturas) and status = 0")
 			.setParameter("abreviaturas", abreviaturas)
 			.getResultList();
 		}
		return result;
	}
	
}
