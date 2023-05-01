package com.base100.lex100.mesaEntrada;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.util.Strings;

import com.base100.document.util.GUID;
import com.base100.expand.seam.framework.entity.EntityOperationException;
import com.base100.expand.seam.framework.entity.LookupEntitySelectionListener;
import com.base100.lex100.Lex100Exception;
import com.base100.lex100.component.audit.AbstractLogicalDeletion;
import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.datasheet.DataSheetEditorManager;
import com.base100.lex100.component.datasheet.IDataSheetProperty;
import com.base100.lex100.component.datasheet.PropertyValueManager;
import com.base100.lex100.component.datasheet.controller.dataSheetProperty.DataSheetPropertyUtils;
import com.base100.lex100.component.enumeration.ObjetoJuicioEnumeration;
import com.base100.lex100.component.enumeration.ParametroEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionEnumeration;
import com.base100.lex100.component.enumeration.TipoAsignacionLex100Enumeration;
import com.base100.lex100.component.enumeration.TipoConexidadSolicitadaEnumeration;
import com.base100.lex100.component.enumeration.TipoDatoEnumeration;
import com.base100.lex100.component.enumeration.TipoGiroEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration;
import com.base100.lex100.component.enumeration.TipoRadicacionEnumeration.Type;
import com.base100.lex100.component.misc.DateUtil;
import com.base100.lex100.controller.entity.objetoJuicio.ObjetoJuicioSearch;
import com.base100.lex100.controller.entity.oficinaSorteo.OficinaSorteoHome;
import com.base100.lex100.controller.entity.oficinaSorteo.OficinaSorteoSearch;
import com.base100.lex100.controller.entity.parametroExpediente.ParametroExpedienteSearch;
import com.base100.lex100.controller.entity.sigla.SiglaSearch;
import com.base100.lex100.entity.Camara;
import com.base100.lex100.entity.Competencia;
import com.base100.lex100.entity.ConexidadDeclarada;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.ExpedienteIngreso;
import com.base100.lex100.entity.Interviniente;
import com.base100.lex100.entity.IntervinienteExp;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Oficina;
import com.base100.lex100.entity.OficinaAsignacionExp;
import com.base100.lex100.entity.OficinaSorteo;
import com.base100.lex100.entity.Parametro;
import com.base100.lex100.entity.ParametroExpediente;
import com.base100.lex100.entity.RecursoExp;
import com.base100.lex100.entity.Sigla;
import com.base100.lex100.entity.Sorteo;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.TipoOficina;
import com.base100.lex100.manager.accion.AccionCambioAsignacion;
import com.base100.lex100.manager.accion.impl.AccionException;
import com.base100.lex100.manager.camara.CamaraManager;
import com.base100.lex100.manager.configuracionMateria.ConfiguracionMateriaManager;
import com.base100.lex100.manager.expediente.ExpedienteManager;
import com.base100.lex100.manager.oficina.OficinaManager;
import com.base100.lex100.mesaEntrada.asignacion.RadicacionFamiliaPreviaInfo;
import com.base100.lex100.mesaEntrada.asignacion.TipoSalaEstudio;
import com.base100.lex100.mesaEntrada.conexidad.ConexidadManager;
import com.base100.lex100.mesaEntrada.conexidad.FamiliaManager;
import com.base100.lex100.mesaEntrada.ingreso.CreateExpedienteAction;
import com.base100.lex100.mesaEntrada.ingreso.FojasEdit;
import com.base100.lex100.mesaEntrada.ingreso.IntervinienteEdit;
import com.base100.lex100.mesaEntrada.ingreso.ParametroToDataSheetPropertyAdapter;
import com.base100.lex100.mesaEntrada.ingreso.ScreenController;

public abstract class ExpedienteMeAbstract extends ScreenController {
	private List<OficinaSorteo> excludeOficinas;

	private static final SimpleDateFormat defaultParameterDateFormat = new SimpleDateFormat(
			"dd/MM/yyyy");

	@In(create = true)
	OficinaSorteoSearch oficinaSorteoSearch;

	private List<OficinaAsignacionExp> radicacionesFamiliaPreviaList = null;
	private List<RadicacionFamiliaPreviaInfo> radicacionesFamiliaPreviaInfoList = null;
	private RadicacionFamiliaPreviaInfo radicacionFamiliaPreviaInfoSelected;

	@In(create = true)
	DataSheetEditorManager dataSheetEditorManager;
	private List<IDataSheetProperty> parametrosAsPropertyList;
	private String parametrosChanged;
	private Integer lastObjetoJuicioId;

	private ConexidadDeclarada conexidadDeclarada;

	public List<OficinaSorteo> getExcludeOficinas() {
		return excludeOficinas;
	}

	protected abstract Sorteo getSorteoSinRubro(Oficina oficinaSorteadora,
			Competencia competencia) throws Lex100Exception;

	protected abstract Competencia getCompetencia();

	public abstract Expediente getExpediente();

	public abstract EntityManager getEntityManager();

	public abstract TipoRadicacionEnumeration.Type getTipoRadicacion();

	public abstract TipoGiroEnumeration.Type getTipoGiro(Expediente expediente,
			TipoRadicacionEnumeration.Type tipoRadicacion);

	public abstract boolean puedeCompensarPorConexidadFamilia(
			TipoRadicacionEnumeration.Type tipoRadicacion);

	public String excluirOficinas(String toPage, String returnPage) {
		Oficina oficinaSorteadora = OficinaManager.instance()
				.getOficinaSorteadoraOAsignadora();
		oficinaSorteoSearch.clear();
		Sorteo sorteo;
		try {
			sorteo = getSorteoSinRubro(oficinaSorteadora, getCompetencia());
		} catch (Lex100Exception e) {
			errorMsg(e.getMessage());
			return null;
		}
		oficinaSorteoSearch.setSorteo(sorteo);
		oficinaSorteoSearch.updateFilters();
		oficinaSorteoSearch.setExcluirOficinas(true);

		Map<OficinaSorteo, Boolean> selectionMap = oficinaSorteoSearch
				.getSelectionMap();
		for (OficinaSorteo oficinaSorteo : excludeOficinas) {
			selectionMap.put(oficinaSorteo, true);
		}
		// resetOficinasExcluidas();

		OficinaSorteoHome oficinaSorteoHome = (OficinaSorteoHome) Component
				.getInstance(OficinaSorteoHome.class, true);
		oficinaSorteoHome.initializeLookup(oficinaSorteoSearch,
				new LookupEntitySelectionListener<OficinaSorteo>(returnPage,
						OficinaSorteo.class, true) {
					public void updateLookup(OficinaSorteo entity) {
						// excludeOficinas.add(entity);
					}

					public void finishSelection() {
						super.finishSelection();
						Map<OficinaSorteo, Boolean> map = OficinaSorteoSearch
								.instance().getSelectionMap();
						excludeOficinas = new ArrayList<OficinaSorteo>();
						for (OficinaSorteo oficinaSorteo : map.keySet()) {
							if (map.get(oficinaSorteo)) {
								excludeOficinas.add(oficinaSorteo);
							}
						}
						resetRadicacionesFamilia(false);
					}
				});
		return toPage;
	}

	public void resetRadicacionesFamilia(boolean expedienteChanged) {
		radicacionesFamiliaPreviaInfoList = null;
		radicacionFamiliaPreviaInfoSelected = null;
		if (expedienteChanged) {
			radicacionesFamiliaPreviaList = null;
			FamiliaManager.instance().reset();
		}
	}

	public void resetOficinasExcluidas() {
		excludeOficinas = new ArrayList<OficinaSorteo>();
	}

	protected List<Integer> getExcludeOficinaIds(Expediente expediente) {
		List<Integer> excludeOficinasIds = null;
		if (excludeOficinas != null && excludeOficinas.size() > 0) {
			excludeOficinasIds = new ArrayList<Integer>();
			for (OficinaSorteo oficinaSorteo : excludeOficinas) {
				excludeOficinasIds.add(oficinaSorteo.getOficina().getId());
			}
		}
		return excludeOficinasIds;
	}

	protected void excluirOficina(Sorteo sorteo, Oficina oficinaAExcluir) {
		List<OficinaSorteo> oficinasSorteoExcluidas = getOficinasSorteoExcluidas(
				sorteo, oficinaAExcluir);
		for (OficinaSorteo oficinaSorteo : oficinasSorteoExcluidas) {
			if (!getExcludeOficinas().contains(oficinaSorteo)) {
				getExcludeOficinas().add(oficinaSorteo);
			}
		}
	}

	public List<OficinaSorteo> getOficinasSorteoExcluidas(Sorteo sorteo,
			Oficina oficinaAExcluir) {
		List<OficinaSorteo> oficinasSorteoExcluidas = new ArrayList<OficinaSorteo>();
		if (oficinaAExcluir != null) {
			if (!CamaraManager.isOficinaPrincipal(oficinaAExcluir)) {
				Oficina oficinaPrincipal = CamaraManager.getOficinaPrincipal(oficinaAExcluir);
				oficinasSorteoExcluidas = getOficinasSorteoExcluidas(sorteo,oficinaPrincipal);
			} else {
				if ((sorteo != null) && (oficinaAExcluir != null)) {
					for (OficinaSorteo oficinaSorteo : sorteo.getOficinaSorteoList()) {
						if (!oficinaSorteo.isLogicalDeleted()) {
							if (oficinaSorteo.getOficina().getId().equals(oficinaAExcluir.getId())) {
								if (!oficinasSorteoExcluidas.contains(oficinaSorteo)) {
									oficinasSorteoExcluidas.add(oficinaSorteo);
								}
							} else if (!CamaraManager.isOficinaPrincipal(oficinaSorteo.getOficina())) {
								Oficina oficinaPrincipal = CamaraManager.getOficinaPrincipal(oficinaSorteo.getOficina());
								if (oficinaPrincipal != null) {
									if (oficinaPrincipal.getId().equals(oficinaAExcluir.getId())) {
										if (!oficinasSorteoExcluidas.contains(oficinaSorteo)) {
											oficinasSorteoExcluidas.add(oficinaSorteo);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return oficinasSorteoExcluidas;
	}

	public RadicacionFamiliaPreviaInfo getRadicacionFamiliaPreviaInfoSelected() {
		return radicacionFamiliaPreviaInfoSelected;
	}

	public void setRadicacionFamiliaPreviaInfoSelected(
			RadicacionFamiliaPreviaInfo radicacionFamiliaPreviaInfoSelected) {
		this.radicacionFamiliaPreviaInfoSelected = radicacionFamiliaPreviaInfoSelected;
	}

	public void selectRadicacionFamiliaPrevia(
			RadicacionFamiliaPreviaInfo radicacionFamiliaPreviaInfo) {
		setRadicacionFamiliaPreviaInfoSelected(radicacionFamiliaPreviaInfo);
	}

	public List<RadicacionFamiliaPreviaInfo> getRadicacionesFamiliaPreviaInfoList() {
		if (radicacionesFamiliaPreviaInfoList == null) {
			boolean addSortearOption = isSeleccionarRadicacionFamilia();
			boolean selectSortearOption = false;
			inicializeRadicacionesFamiliaPreviaInfoList(addSortearOption,
					selectSortearOption);
		}
		return radicacionesFamiliaPreviaInfoList;
	}

	public void setRadicacionesFamiliaPreviaInfoList(
			List<RadicacionFamiliaPreviaInfo> radicacionesFamiliaPreviaInfoList) {
		this.radicacionesFamiliaPreviaInfoList = radicacionesFamiliaPreviaInfoList;
	}

	public boolean isPuedeSeleccionarRadicacionesFamiliaPrevia() {
		List<RadicacionFamiliaPreviaInfo> list = getRadicacionesFamiliaPreviaInfoList();
		int available = 0;
		for (RadicacionFamiliaPreviaInfo item : list) {
			if (item.getOficinaAsignacionExp() != null && !item.isExcluida()) {
				available++;
			}
		}
		return available > 0;
	}

	private Oficina getOficinaRadicacionActual() {
		Oficina oficinaActual = null;
		oficinaActual = ExpedienteManager.instance().getOficinaByRadicacion(
				getExpediente(), getTipoRadicacion());
		if (oficinaActual == null && getExpediente() != null
				&& !ExpedienteManager.isExpedienteBase(getExpediente())) {
			oficinaActual = ExpedienteManager.instance()
					.getOficinaByRadicacion(
							ExpedienteManager.getExpedienteBase(
									getEntityManager(), getExpediente()),
							getTipoRadicacion());
		}
		return oficinaActual;
	}

	private OficinaAsignacionExp getRadicacionActual() {
		OficinaAsignacionExp radicacionActual = null;
		radicacionActual = ExpedienteManager.instance()
				.getOficinaAsignacionExp(getExpediente(), getTipoRadicacion());
		if (radicacionActual == null && getExpediente() != null
				&& !ExpedienteManager.isExpedienteBase(getExpediente())) {
			radicacionActual = ExpedienteManager.instance()
					.getOficinaAsignacionExp(
							ExpedienteManager.getExpedienteBase(
									getEntityManager(), getExpediente()),
							getTipoRadicacion());
		}
		return radicacionActual;
	}

	public static boolean isCompatible(Oficina oficina, Type tipoRadicacion) {
		if (CamaraManager.getCamaraActual().equals(oficina.getCamara())) {
			TipoOficina tipoOficina = oficina.getTipoOficina();
			return TipoRadicacionEnumeration.isCompatible(tipoOficina,
					tipoRadicacion);
		}
		return false;
	}

	public void inicializeRadicacionesFamiliaPreviaInfoList(
			boolean addSortearOption, boolean selectSortearOption) {
		setRadicacionFamiliaPreviaInfoSelected(null);
		radicacionesFamiliaPreviaInfoList = new ArrayList<RadicacionFamiliaPreviaInfo>();
		Oficina oficinaActual = null;
		OficinaAsignacionExp radicacionActual = getRadicacionActual();
		if (radicacionActual != null) {
			oficinaActual = radicacionActual.getOficinaConSecretaria();
		}
		List<Integer> oficinasExcluidas = getExcludeOficinaIds(getExpediente());
		List<OficinaAsignacionExp> ofiList = getRadicacionesFamiliaPreviaList();
		for (OficinaAsignacionExp oficinaAsignacionExp : ofiList) {
			if ((oficinaAsignacionExp.getOficina() != null)
					&& isCompatible(oficinaAsignacionExp.getOficina(),
							getTipoRadicacion())) {
				RadicacionFamiliaPreviaInfo radicacionFamiliaPreviaInfo = new RadicacionFamiliaPreviaInfo(
						oficinaAsignacionExp);
				radicacionesFamiliaPreviaInfoList
						.add(radicacionFamiliaPreviaInfo);
			}
		}
		// Selecciona la actual si tiene (solo asignacion) o la mas antigua
		if (!addSortearOption || !selectSortearOption) {
			Date fechaAsignacion = null;
			for (RadicacionFamiliaPreviaInfo radicacionFamiliaPreviaInfo : radicacionesFamiliaPreviaInfoList) {
				if (isExcluida(oficinasExcluidas,
						radicacionFamiliaPreviaInfo.getOficinaAsignacionExp())) {
					radicacionFamiliaPreviaInfo.setExcluida(true);
				} else {
					if ((oficinaActual != null)
							&& oficinaActual.equals(radicacionFamiliaPreviaInfo
									.getOficinaAsignacionExp()
									.getOficinaConSecretaria())) {
						if (!isCambioAsignacion()) {
							setRadicacionFamiliaPreviaInfoSelected(radicacionFamiliaPreviaInfo);
							break;
						} else {
							radicacionFamiliaPreviaInfo.setExcluida(true);
						}
					} else {
						if ((fechaAsignacion == null)
								|| fechaAsignacion
										.after(radicacionFamiliaPreviaInfo
												.getOficinaAsignacionExp()
												.getFechaAsignacion())) {
							setRadicacionFamiliaPreviaInfoSelected(radicacionFamiliaPreviaInfo);
							fechaAsignacion = radicacionFamiliaPreviaInfo
									.getOficinaAsignacionExp()
									.getFechaAsignacion();
						}
					}
				}
			}
		}
		if (addSortearOption) {
			RadicacionFamiliaPreviaInfo radicacionFamiliaPreviaInfoSorteo = new RadicacionFamiliaPreviaInfo(
					null);
			radicacionesFamiliaPreviaInfoList
					.add(radicacionFamiliaPreviaInfoSorteo);
			if (selectSortearOption
					|| getRadicacionFamiliaPreviaInfoSelected() == null) {
				setRadicacionFamiliaPreviaInfoSelected(radicacionFamiliaPreviaInfoSorteo);
			}
		}
	}

	protected boolean isCambioAsignacion() {
		return false;
	}

	private boolean isExcluida(List<Integer> oficinasExcluidas,
			OficinaAsignacionExp oficinaAsignacionExp) {
		boolean excluida = false;
		if (oficinasExcluidas != null) {
			if ((oficinaAsignacionExp.getOficina() != null)
					&& oficinasExcluidas.contains(oficinaAsignacionExp
							.getOficina().getId())) {
				excluida = true;
			} else if ((oficinaAsignacionExp.getSecretaria() != null)
					&& oficinasExcluidas.contains(oficinaAsignacionExp
							.getSecretaria().getId())) {
				excluida = true;
			}
		}
		return excluida;
	}

	private List<OficinaAsignacionExp> getRadicacionesFamiliaPreviaList() {
		if (radicacionesFamiliaPreviaList == null) {
			radicacionesFamiliaPreviaList = ConexidadManager
					.getRadicacionesFamiliaPrevia(getEntityManager(),
							getExpediente(), getTipoRadicacion(), SessionState
									.instance().getGlobalCamara());
		}
		return radicacionesFamiliaPreviaList;
	}

	public boolean hasRadicacionesFamilia() {
		return isPuedeSeleccionarRadicacionesFamiliaPrevia();
	}

	public OficinaAsignacionExp getRadicacionPorFamilia() {
		OficinaAsignacionExp oficinaAsignacionExp = null;
		if (getRadicacionFamiliaPreviaInfoSelected() == null) {
			boolean addSortearOption = isSeleccionarRadicacionFamilia();
			boolean selectSortearOption = false;
			inicializeRadicacionesFamiliaPreviaInfoList(addSortearOption,
					selectSortearOption);
		}
		if (getRadicacionFamiliaPreviaInfoSelected() != null) {
			if (getRadicacionFamiliaPreviaInfoSelected()
					.getOficinaAsignacionExp() != null) {
				oficinaAsignacionExp = getRadicacionFamiliaPreviaInfoSelected()
						.getOficinaAsignacionExp();
			}
		} else {
			// Para asegurarnos
			OficinaAsignacionExp radicacionActual =  getRadicacionActual();
			List<Integer> oficinasExcluidas = getExcludeOficinaIds(getExpediente());
			if ((radicacionActual != null) && !isExcluida(oficinasExcluidas, radicacionActual)) {
				oficinaAsignacionExp = radicacionActual;
			}
		}
		return oficinaAsignacionExp;
	}
	public Oficina getOficinaRadicacionPorFamilia() {
		Oficina oficina = null;
		if (getRadicacionFamiliaPreviaInfoSelected() == null) {
			boolean addSortearOption = isSeleccionarRadicacionFamilia();
			boolean selectSortearOption = false;
			inicializeRadicacionesFamiliaPreviaInfoList(addSortearOption,
					selectSortearOption);
		}
		if (getRadicacionFamiliaPreviaInfoSelected() != null) {
			if (getRadicacionFamiliaPreviaInfoSelected()
					.getOficinaAsignacionExp() != null) {
				oficina = getRadicacionFamiliaPreviaInfoSelected()
						.getOficinaAsignacionExp().getOficinaConSecretaria();
			}
		} else {
			// Para asegurarnos
			OficinaAsignacionExp radicacionActual =  getRadicacionActual();
			List<Integer> oficinasExcluidas = getExcludeOficinaIds(getExpediente());
			if ((radicacionActual != null) && !isExcluida(oficinasExcluidas, radicacionActual)) {
				oficina = getOficinaRadicacionActual();
			}
		}
		return oficina;
	}

	public boolean buscaConexidadPorFamilia(Expediente expediente,
			RecursoExp recursoExp,
			TipoRadicacionEnumeration.Type tipoRadicacion,
			String codigoTipoCambioAsignacion,
			String descripcionCambioAsignacion, Sorteo sorteo, String rubro,
			FojasEdit fojasPase)
			throws Lex100Exception {
		// Oficina oficina = getRadicacionFamiliaPrevia(expediente,
		// tipoRadicacion);
		Oficina oficina = getOficinaRadicacionPorFamilia();

		if (oficina != null) {
			try {
				Date FECHA_ASIGNACION_EN_CURSO = null; // null -> fecha en curso
				TipoAsignacionEnumeration.Type tipoAsignacionRadicacionAnterior = TipoAsignacionEnumeration
						.getTipoRadicacionAnterior(tipoRadicacion);
				new AccionCambioAsignacion().doAccion(SessionState.instance()
						.getUsername(), expediente, recursoExp, oficina,
						tipoRadicacion, tipoAsignacionRadicacionAnterior,
						TipoAsignacionLex100Enumeration.Type.conexidadFamilia,
						SessionState.instance().getGlobalOficina(),
						getTipoGiro(getExpediente(), getTipoRadicacion()),
						puedeCompensarPorConexidadFamilia(getTipoRadicacion()),
						sorteo, rubro, codigoTipoCambioAsignacion,
						descripcionCambioAsignacion, null,
						FECHA_ASIGNACION_EN_CURSO,
						fojasPase);

				// Copia la fiscalia de la radicacion de la familia si no la tiene asignada por turno
				ExpedienteManager.instance().copiaRadicacionFiscalia(tipoRadicacion, expediente, getRadicacionPorFamilia());
				if(!puedeCompensarPorConexidadFamilia(getTipoRadicacion())) {
					// Copia el orden de circulacion de la radicacion de la familia
					ExpedienteManager.instance().copiaRadicacionOrdenCirculacion(tipoRadicacion, expediente, getRadicacionPorFamilia());
				}
				//
				
				// Añadir el conexo seleccionado cuando hay
				// diversidad de juzgado o sala
				if (getRadicacionFamiliaPreviaInfoSelected() != null) {
					Expediente expedienteConexo = getRadicacionFamiliaPreviaInfoSelected()
							.getOficinaAsignacionExp().getExpediente();
					if (!getRadicacionFamiliaPreviaInfoSelected()
							.getOficinaAsignacionExp().equals(
									getRadicacionActual())
							&& ExpedienteManager.isPrincipal(expediente)
							&& !FamiliaManager.getFamiliaDirectaExpediente(
									getEntityManager(), expediente).contains(
									expedienteConexo)) {
						/**ATOS OVD */
						CreateExpedienteAction.addVinculadoConexo(
								getEntityManager(), getTipoRadicacion(),
								expediente, expedienteConexo,
								tipoAsignacionRadicacionAnterior, null, null, null);
						/**ATOS OVD */
					}
				}
				//
			} catch (AccionException e) {
				throw new Lex100Exception(e);
			}
			return true;
		}
		return false;
	}

	// private Oficina getRadicacionFamiliaPrevia(Expediente expediente,
	// TipoRadicacionEnumeration.Type tipoRadicacion) {
	// Oficina oficina =null;
	//
	// if (MateriaEnumeration.isAnyPenal(expediente.getMateria())) {
	// Expediente expedientePrincipal =
	// ExpedienteManager.getExpedientePrincipal(getEntityManager(), expediente);
	// oficina =
	// ExpedienteManager.instance().getOficinaByRadicacion(expedientePrincipal,
	// tipoRadicacion);
	// }
	// if (oficina == null) {
	// List<Expediente> familiaExpediente =
	// ConexidadManager.instance().getFamiliaExpediente(getEntityManager(),
	// expediente);
	//
	// Date fechaAsignacion = null;
	// for(Expediente expedienteConexo: familiaExpediente) { // elige la mas
	// antigua
	// OficinaAsignacionExp radicacion =
	// ExpedienteManager.instance().findRadicacionExpediente(expedienteConexo,
	// tipoRadicacion);
	// if (radicacion == null) {
	// continue;
	// }
	// if ((fechaAsignacion == null) ||
	// fechaAsignacion.after(radicacion.getFechaAsignacion())){
	// oficina = radicacion.getOficina();
	// fechaAsignacion = radicacion.getFechaAsignacion();
	// }
	// }
	// }
	// return oficina;
	// }

	protected Oficina getJuzgadoRadicacion(Expediente expediente) {
		OficinaAsignacionExp radicacion = ExpedienteManager.instance()
				.getRadicacionByTipoRadicacion(expediente,
						TipoRadicacionEnumeration.Type.juzgado);
		if (radicacion != null) {
			return radicacion.getOficinaConSecretaria();
		}
		return null;
	}

	protected Oficina getSalaRadicacion(Expediente expediente) {
		OficinaAsignacionExp radicacion = ExpedienteManager.instance()
				.getRadicacionByTipoRadicacion(expediente,
						TipoRadicacionEnumeration.Type.sala);
		if (radicacion != null) {
			return radicacion.getOficinaConSecretaria();
		}
		return null;
	}

	public boolean isSeleccionarRadicacionFamilia() {
		if (isCambioAsignacion()
				|| CamaraManager.isCamaraActualCivilCABA(SessionState
						.instance().getGlobalOficina().getCamara())) {
			boolean ret = ConfiguracionMateriaManager.instance()
					.isSeleccionarRadicacionFamilia(
							SessionState.instance().getGlobalOficina()
									.getCamara(), null);
			if(ret) {
				ret &= hasPermissionSeleccionaRadicacionFamilia();
			}
			return ret;
		} else {
			return getOficinaRadicacionActual() == null
					|| !isCompatible(getOficinaRadicacionActual(),
							getTipoRadicacion());
		}
	}

	private boolean hasPermissionSeleccionaRadicacionFamilia() {
		if (isCamaraSeguridadSocial()) {
			return org.jboss.seam.security.Identity.instance().hasPermission("MesaEntrada", "seleccionaRadicacionFamilia");
		}
		return true;
	}

	public boolean isTipoRadicacionJuzgado() {
		return TipoRadicacionEnumeration.Type.juzgado == getTipoRadicacion();
	}

	public boolean isTipoRadicacionSalaCasacion() {
		return TipoRadicacionEnumeration.Type.salaCasacion == getTipoRadicacion();
	}

	public boolean isTipoRadicacionCorteSuprema() {
		return TipoRadicacionEnumeration.Type.corteSuprema == getTipoRadicacion();
	}

	public boolean isTipoRadicacionAnySala() {
		return TipoRadicacionEnumeration.Type.sala == getTipoRadicacion()
				|| TipoRadicacionEnumeration.Type.salaEstudio == getTipoRadicacion();

	}

	public String labelDiversidadOficina() {
		String ret = "";
		if (isTipoRadicacionAnySala()) {
			ret = Messages.instance().get(
					"expediente.vinculadoExtendidoList.alert.diversidadSala");
		} else if (isTipoRadicacionJuzgado()) {
			ret = Messages
					.instance()
					.get("expediente.vinculadoExtendidoList.alert.diversidadJuzgado");
		}
		return ret;
	}

	public boolean hasDiversidadOficina() {
		boolean ret = false;
		if (isTipoRadicacionAnySala() || isTipoRadicacionJuzgado()) {
			return calculeDiversidad();
		}
		return ret;
	}

	public boolean calculeDiversidad() {
		int count = getRadicacionesFamiliaPreviaInfoList().size();
		// Con esto vemos si esta o no la opción de sorteo
		if (getRadicacionesFamiliaPreviaInfoList().size() == 2) {
			count = 0;
			for (RadicacionFamiliaPreviaInfo radicacionFamiliaPreviaInfo : getRadicacionesFamiliaPreviaInfoList()) {
				if (radicacionFamiliaPreviaInfo.getOficinaAsignacionExp() != null) {
					count++;
				}
			}
		}
		return count > 1;
	}

	public TipoSalaEstudio[] getTiposSalaEstudio() {
		return new TipoSalaEstudio[] { TipoSalaEstudio.salaEstudio,
				TipoSalaEstudio.salaPronunciamientoSentencia,
				TipoSalaEstudio.salaEstudioRecusacionConCausa };
	}

	public String getTitleByRecurso(RecursoExp recursoExp) {
		String ret = null;
		if (recursoExp.getFechaResolucionElevacion() != null) {
			ret = new SimpleDateFormat("dd/MM/yyyy").format(recursoExp
					.getFechaResolucionElevacion())
					+ " - "
					+ recursoExp.getTipoRecurso().getDescripcion();
		} else {
			if (!Strings.isEmpty(recursoExp.getClave())) {
				ret = recursoExp.getClave() + "-"
						+ recursoExp.getTipoRecurso().getDescripcion();
			} else {
				ret = recursoExp.getTipoRecurso().getDescripcion();
			}
		}
		return ret;

	}

	public boolean equals(TipoCausa t1, TipoCausa t2) {
		if (t1 == null || t2 == null) {
			return t1 == t2;
		} else {
			return t1.getId().equals(t2.getId());
		}
	}

	private boolean equals(List<String> l1, List<String> l2) {
		if (l1 == null || l2 == null) {
			return l1 == l2;
		} else {
			Set<String> s1 = new HashSet<String>(l1);
			Set<String> s2 = new HashSet<String>(l2);
			return s1.equals(s2);
		}
	}

	public ObjetoJuicio getObjetoJuicio() {
		return null;
	}

	public boolean showParametros() {
		return (getObjetoJuicio() != null) && (getParametroList() != null)
				&& (getParametroList().size() > 0);
	}

	public void actualizaParametros(List<IDataSheetProperty> parametrosAsPropertyList) {
		// Inicializa el valor del parametro domicilioDemandado con los datos
		// del domicilio del primer interviniente de tipo demandado
		// y el parametro Obra Social con la sigla del actor
		if (parametrosAsPropertyList != null) {
			for (IDataSheetProperty property : parametrosAsPropertyList) {
				if (ParametroEnumeration.DOMICILIO_DEMANDADO_NAME
						.equals(property.getName())) {
					String domicilioDemandado = null;
					for (IntervinienteExp item : getExpediente().getIntervinienteExpList()) {
						if (item.isDemandado()) {
							domicilioDemandado = item.getDomicilioCompleto();
							break;
						}
					}
					DataSheetEditorManager
							.instance()
							.getInstanceProperties()
							.put(property.getDataSheetPropertyId(),
									domicilioDemandado);
				} else if ("$siglaActor".equals(DataSheetPropertyUtils.getParameter(property, DataSheetPropertyUtils.INIT_DATA_PARAMETER))) {
					String siglaActor = null;
					if (isEditMode()) {
						siglaActor = getParametroValue(property);
					}
					Camara camara = SessionState.instance().getGlobalCamara();
					for (IntervinienteExp item : getExpediente().getIntervinienteExpList()) {
						if (item.isActor() && item.getInterviniente().isPersonaJuridica()) {
							Sigla sigla = SiglaSearch.findByDescripcion(getEntityManager(), item.getNombreCompleto(), camara);
							if (sigla != null) {
								siglaActor = sigla.getCodigo();
							}
							break;
						}
					}
					DataSheetEditorManager
					.instance()
					.getInstanceProperties()
					.put(property.getDataSheetPropertyId(),	siglaActor);
				}
			}
		}
	}

	public List<Parametro> getParametroList() {
		if (getObjetoJuicio() != null) {
			ObjetoJuicio objetoJuicio = (ObjetoJuicio) getEntityManager().find(
					ObjetoJuicio.class, getObjetoJuicio().getId());
			if (objetoJuicio != null) {
				return objetoJuicio.getParametroList();
			}
		}
		return null;
	}

	public List<IDataSheetProperty> getParametrosAsPropertyList() {
		if (showParametros()) {
			if ((getObjetoJuicio() != null)
					&& (parametrosAsPropertyList == null
							|| lastObjetoJuicioId == null || !lastObjetoJuicioId
							.equals(getObjetoJuicio().getId()))) {

				lastObjetoJuicioId = getObjetoJuicio().getId();
				dataSheetEditorManager.resetDataSheets();
				parametrosAsPropertyList = new ArrayList<IDataSheetProperty>();
				for (Parametro parametro : getParametroList()) {
					parametrosAsPropertyList
							.add(new ParametroToDataSheetPropertyAdapter(
									parametro, isEditMode()));
				}

				actualizaParametros(parametrosAsPropertyList);
			}
		} else {
			parametrosAsPropertyList = null;
		}
		return parametrosAsPropertyList;
	}

	public String getParametroValue(IDataSheetProperty property) {
		Object value = dataSheetEditorManager.getPropertyValue(property);
		return dataSheetEditorManager.getAsString(property, value, true);
	}

	public String getParametroValue(Parametro parametro) {
		return getParametroValue(new ParametroToDataSheetPropertyAdapter(
				parametro));
	}

	protected void checkParametros() throws EntityOperationException {
		// TODO. chequear validez de los parametros
		if (showParametros()) {
			for (IDataSheetProperty property : getParametrosAsPropertyList()) {
				Parametro parametro = ((ParametroToDataSheetPropertyAdapter) property)
						.getParametro();
				String stringValue = null;
				try {
					stringValue = getParametroValue(property);
				} catch (Exception e) {
					String errKey = "expedienteMe.error.parametro.erroneo";
					errorMsg(errKey, parametro.getLabel());
					throw new EntityOperationException();
				}
				if (Strings.isEmpty(stringValue)
						&& DataSheetPropertyUtils.isRequired(property)) {
					String errKey = "expedienteMe.error.parametro.requerido";
					errorMsg(errKey, parametro.getLabel());
					throw new EntityOperationException();
				}
				if (Boolean.TRUE.equals(parametro.getMultiple())) {
					checkParametroMultiple(parametro, stringValue);
				}
			}
		}
	}

	protected void checkParametroMultiple(Parametro parametro,
			String stringValue) throws EntityOperationException {
		List<String> valueList = Configuration
				.getCommaSeparatedAsNotNullList(stringValue);
		Map<String, Object> valueMap = new HashMap<String, Object>();
		for (String value : valueList) {
			if (valueMap.get(value) != null) {
				String errKey = "expedienteMe.error.parametroMultiple.duplicado";
				errorMsg(errKey, parametro.getLabel(), value);
				throw new EntityOperationException();
			}
			try {
				Object obj = PropertyValueManager.getAsObject((String) value,
						parametro.getType(), null);
				valueMap.put(value, obj);
			} catch (Exception e) {
				String errKey = "expedienteMe.error.parametroMultiple.erroneo";
				errorMsg(
						errKey,
						parametro.getLabel(),
						value,
						TipoDatoEnumeration.instance().getLabel(
								parametro.getType()));
				throw new EntityOperationException();
			}
		}
	}

	protected void createOrUpdateParametrosExpediente(
			ExpedienteIngreso expedienteIngreso) {
		setParametrosChanged(null);
		if (expedienteIngreso != null) {
			List<ParametroExpediente> otrosParametros = null;
			StringBuffer changes = new StringBuffer();
			if ((getParametroList() != null) && (getParametroList().size() > 0)) {
				for (Parametro parametro : getParametroList()) {
					String stringValue = getParametroValue(parametro);
					List<String> valueList = new ArrayList<String>();
					if (Boolean.TRUE.equals(parametro.getMultiple())) {
						valueList = Configuration
								.getCommaSeparatedAsNotNullList(stringValue);
					} else if (!Strings.isEmpty(stringValue)) {
						valueList.add(stringValue);
					}
					List<String> oldValueList = ParametroExpedienteSearch
							.getParametroValues(getEntityManager(), parametro,
									expedienteIngreso);
					String oldValue = Configuration
							.getCommaSeparatedString(oldValueList);
					boolean changed = !equals(valueList, oldValueList);
					if ((valueList.size() == 0) || changed) {
						ParametroExpedienteSearch.deleteParametro(
								getEntityManager(), parametro,
								expedienteIngreso);
					}
					if (changed) {
						for (String value : valueList) {
							ParametroExpediente parametroExpediente = new ParametroExpediente();
							parametroExpediente
									.setExpedienteIngreso(expedienteIngreso);
							parametroExpediente.setParametro(parametro);
							parametroExpediente.setIndice(0);
							parametroExpediente.setLogicalDeleted(false);
							parametroExpediente
									.setStatus(AbstractLogicalDeletion.NORMAL_STATUS_FLAG);
							parametroExpediente.setUuid(GUID.generate());
							getEntityManager().persist(parametroExpediente);
							if (!Strings.isEmpty(value)) {
								value = value.toUpperCase();
							}
							parametroExpediente.setValor(value);
							getEntityManager().flush();
						}
					}
					if (isEditMode()) {
						if (changed) {
							if (changes.length() > 0) {
								changes.append(", ");
							}
							if (!Strings.isEmpty(oldValue)) {
								if (!Strings.isEmpty(stringValue)) {
									changes.append(interpolate(getMessages()
											.get("parametro.updated"),
											parametro.getLabel(), oldValue,
											stringValue));
								} else {
									changes.append(interpolate(getMessages()
											.get("parametro.deleted"),
											parametro.getLabel(), oldValue));
								}
							} else {
								changes.append(interpolate(
										getMessages().get("parametro.added"),
										parametro.getLabel(), stringValue));
							}
						}
					}
				}
				otrosParametros = ParametroExpedienteSearch.getOtherParametros(
						getEntityManager(), getParametroList(),
						expedienteIngreso);
				getEntityManager().flush();
			} else {
				otrosParametros = ParametroExpedienteSearch
						.getParametrosExpediente(getEntityManager(),
								expedienteIngreso);
			}
			if (otrosParametros != null) {
				for (ParametroExpediente parametroExpediente : otrosParametros) {
					if (changes.length() > 0) {
						changes.append(", ");
					}
					changes.append(interpolate(
							getMessages().get("parametro.deleted"),
							parametroExpediente.getParametro().getLabel(),
							parametroExpediente.getValor()));
					getEntityManager().remove(parametroExpediente);
				}
			}
			getEntityManager().flush();
			getEntityManager().refresh(expedienteIngreso);
			setParametrosChanged((changes.length() > 0) ? changes.toString()
					: null);
		}
	}

	public String getParametrosChanged() {
		return parametrosChanged;
	}

	public void setParametrosChanged(String parametrosChanged) {
		this.parametrosChanged = parametrosChanged;
	}

	private boolean isEditMode() {
		return true;
	}

	public void onChangeObjetoJuicio(ValueChangeEvent event) {
	}

	// Conexidad Declarada

	private boolean equals(Object s1, Object s2) {
		return s1 == null ? s2 == null : s1.equals(s2);
	}

	public Camara getCamara() {
		return CamaraManager.getCamaraActual();
	}

	abstract public boolean showConexidadDeclarada();

	abstract public List<Oficina> getOficinasConexidadDeclarada();

	abstract public Materia getMateria();

	public ConexidadDeclarada getConexidadDeclarada() {
		return conexidadDeclarada;
	}

	public void setConexidadDeclarada(ConexidadDeclarada conexidadDeclarada) {
		this.conexidadDeclarada = conexidadDeclarada;
	}

	protected void checkConexidadDeclarada() throws EntityOperationException {
		if (showConexidadDeclarada()) {
			boolean updated = false;

			// Si ha habido cambios se actualiza
			if (((conexidadDeclarada.getExpedienteConexo() != null) && ((!conexidadDeclarada
					.getExpedienteConexo().getNumero()
					.equals(conexidadDeclarada.getNumeroExpedienteConexo()) || !conexidadDeclarada
					.getAnioExpedienteConexo().equals(
							conexidadDeclarada.getAnioExpedienteConexo())) || conexidadDeclarada
					.isAnteriorAlSistema()))
					|| (!conexidadDeclarada.isAnteriorAlSistema()
							&& (conexidadDeclarada.getExpedienteConexo() == null)
							&& (conexidadDeclarada.getAnioExpedienteConexo() != null) && (conexidadDeclarada
							.getNumeroExpedienteConexo() != null))) {
				updated = true;
				updateConexidadDeclarada();
			}

			if (!conexidadDeclarada.isEmpty()) {
				if (conexidadDeclarada.getExpedienteConexo() == null) {
					String errKey = "conexidad.conexidadDeclarada.expediente.notFound";
					errorMsg(
							errKey,
							String.valueOf(conexidadDeclarada
									.getNumeroExpedienteConexo())
									+ "/"
									+ String.valueOf(conexidadDeclarada
											.getAnioExpedienteConexo()));
					throw new EntityOperationException();
				} else {
					if (conexidadDeclarada.getOficina() != null) {
						if (!getCamara().equals(
								conexidadDeclarada.getOficina().getCamara())) {
							errorMsg(
									"expedienteMe.error.oficinaConexidadDeclarada",
									Messages.instance().get(
											getTipoRadicacion().getLabel()));
							throw new EntityOperationException();
						}
					} else {
						String errKey = "conexidad.conexidadDeclarada.oficina.notExists";
						errorMsg(
								errKey,
								Messages.instance().get(
										getTipoRadicacion().getLabel()));
						throw new EntityOperationException();
					}
				}
				checkFechaConexidadDeclarada(conexidadDeclarada);
				checkPartesConexidadDeclarada(conexidadDeclarada);
				checkObjetoJuicioConexidadDeclarada(conexidadDeclarada);

			}
			if (updated) {
				getStatusMessages().addFromResourceBundle(Severity.WARN,
						"conexidad.conexidadDeclarada.comprobarDatos");
				throw new EntityOperationException();
			}
		}
	}

	public void resetConexidadDeclarada() {
		if (conexidadDeclarada != null) {
			conexidadDeclarada.setNumeroExpedienteConexo(null);
			conexidadDeclarada.setAnioExpedienteConexo(null);
			conexidadDeclarada.setOficina(null);
			conexidadDeclarada.setExpedienteConexo(null);
			conexidadDeclarada.setObservaciones(null);
		}
	}

	public void updateConexidadDeclarada() {
		if (!conexidadDeclarada.isAnteriorAlSistema()) {
			Oficina oficina = null;
			ConexidadManager.instance().solveExpedienteConexidadDeclarada(
					conexidadDeclarada);
			if (conexidadDeclarada.getExpedienteConexo() != null) {
				OficinaAsignacionExp radicacion = ExpedienteManager.instance()
						.findRadicacionExpediente(
								conexidadDeclarada.getExpedienteConexo(),
								getTipoRadicacion());
				if (radicacion != null) {
					if (getOficinasConexidadDeclarada().contains(
							radicacion.getOficina())) {
						oficina = radicacion.getOficina();
					} else if (getOficinasConexidadDeclarada().contains(
							radicacion.getSecretaria())) {
						oficina = radicacion.getSecretaria();
					} else {
						oficina = radicacion.getOficina();
					}
				}
			}
			conexidadDeclarada.setOficina(oficina);
		} else {
			conexidadDeclarada.setExpedienteConexo(null);
		}
	}

	public boolean isUseConexidadSolicitadaMesa() {
		if (getObjetoJuicio() != null) {
			return getObjetoJuicio().isConexidadSolicitadaMesa();
		}
		return false;
	}

	public List<SelectItem> getTiposConexidadSolicitada() {
		return TipoConexidadSolicitadaEnumeration.instance()
				.getTiposConexidadSolicitada(isUseConexidadSolicitadaMesa());
	}

	public boolean hayTiposConexidadSolicitada() {
		return getTiposConexidadSolicitada().size() > 1;
	}

	public void updateAndCheckConexidadDeclarada() {
		updateConexidadDeclarada();
		try {
			checkConexidadDeclarada();
		} catch (EntityOperationException e) {
		}
	}

	private void checkObjetoJuicioConexidadDeclarada(
			ConexidadDeclarada conexidadDeclarada)
			throws EntityOperationException {
		if (conexidadDeclarada.isSolicitadaPorMesa()) {
			checkObjetoJuicioConexidadSolicitadaMesa(conexidadDeclarada);
		} else if (conexidadDeclarada.isPrevencion()
				|| ConfiguracionMateriaManager.instance()
						.isConexidadDeclaradaVerificaObjetoJuicio(getCamara(),
								getMateria())) {
			checkCoincidenciaOJConexidadDeclarada(conexidadDeclarada);
		}
	}

	private boolean objetoJuicioVerificaPartesConexidadDeclarada() {
		return !isCamaraLaboral() || 
			(!isObjetoJuicioExtensiónResponsabilidad() && 
			 !isObjetoJuicioEjecucionHonorarios() && 
			 !isObjetoJuicioEjecucionSentencia() && 
			 !isObjetoJuicioRevisionCosaJuzgada());
	}

	private void checkPartesConexidadDeclarada(
			ConexidadDeclarada conexidadDeclarada)
			throws EntityOperationException {
		if (!conexidadDeclarada.isSolicitadaPorMesa() && objetoJuicioVerificaPartesConexidadDeclarada()) {
			if (conexidadDeclarada.isPrevencion()
					|| ConfiguracionMateriaManager.instance()
							.isConexidadDeclaradaVerificaPartes(getCamara(),
									getMateria())) {
				checkCoincidenciaPartesConexidadDeclarada(conexidadDeclarada, (isIngresoASala() && conexidadDeclarada.isDeclarada()));
			}
		}
	}

	private void checkCoincidenciaPartesConexidadDeclarada(
			ConexidadDeclarada conexidadDeclarada, boolean admiteCruzada)
			throws EntityOperationException {
		boolean found = false;
		int numeroCoincidenciasActor = 0;
		int numeroCoincidenciasDemandado = 0;
		int numeroCoincidenciasActorX = 0;
		int numeroCoincidenciasDemandadoX = 0;
		for (IntervinienteExp intervinienteExp : getExpediente()
				.getIntervinienteExpList()) {
			if (!intervinienteExp.isLogicalDeleted()) {
				for (IntervinienteExp intervinienteRelacionadoExp : conexidadDeclarada
						.getExpedienteConexo().getIntervinienteExpList()) {
					if (!intervinienteRelacionadoExp.isLogicalDeleted()) {
						Interviniente intervinienteRelacionado = intervinienteRelacionadoExp
								.getInterviniente();
						if (intervinienteExp.getNombreCompleto() != null
								&& intervinienteExp.getNombreCompleto().equals(
										intervinienteRelacionado.getNombre())) {
							if (intervinienteExp.isActor()) {
								if (intervinienteRelacionadoExp.isActor()) {
									numeroCoincidenciasActor++;
								} else if (intervinienteRelacionadoExp
										.isDemandado()) {
									numeroCoincidenciasActorX++;
								}
							} else if (intervinienteExp.isDemandado()) {
								if (intervinienteRelacionadoExp.isDemandado()) {
									numeroCoincidenciasDemandado++;
								} else if (intervinienteRelacionadoExp
										.isActor()) {
									numeroCoincidenciasDemandadoX++;
								}
							}
						}
						if (!Strings.isEmpty(intervinienteExp
								.getInterviniente().getNumeroDocId())
								&& intervinienteExp
										.getInterviniente()
										.getNumeroDocId()
										.equals(intervinienteRelacionado
												.getNumeroDocId())) {
							if (intervinienteExp.isActor()) {
								if (intervinienteRelacionadoExp.isActor()) {
									numeroCoincidenciasActor++;
								} else if (intervinienteRelacionadoExp
										.isDemandado()) {
									numeroCoincidenciasActorX++;
								}
							} else if (intervinienteExp.isDemandado()) {
								if (intervinienteRelacionadoExp.isDemandado()) {
									numeroCoincidenciasDemandado++;
								} else if (intervinienteRelacionadoExp
										.isActor()) {
									numeroCoincidenciasDemandadoX++;
								}
							}
							// Añadir coincidencias por cuit de las empresas del
							// grupo
						} else if (!Strings.isEmpty(intervinienteRelacionado
								.getDni())
								&& intervinienteRelacionado.getDni().equals(
										intervinienteExp.getInterviniente()
												.getDni())) {
							if (intervinienteExp.isActor()) {
								if (intervinienteRelacionadoExp.isActor()) {
									numeroCoincidenciasActor++;
								} else if (intervinienteRelacionadoExp
										.isDemandado()) {
									numeroCoincidenciasActorX++;
								}
							} else if (intervinienteExp.isDemandado()) {
								if (intervinienteRelacionadoExp.isDemandado()) {
									numeroCoincidenciasDemandado++;
								} else if (intervinienteRelacionadoExp
										.isActor()) {
									numeroCoincidenciasDemandadoX++;
								}
							}
						}
					}
				}
				if (numeroCoincidenciasActor > 0
						&& numeroCoincidenciasDemandado > 0) {
					found = true;
				}
				if (!found && admiteCruzada && numeroCoincidenciasActorX > 0
						&& numeroCoincidenciasDemandadoX > 0) {
					found = true;
				}
				if (found) {
					break;
				}
			}
			if (!found) {
				getStatusMessages().addFromResourceBundle(Severity.ERROR,
						"conexidad.conexidadDeclarada.sinCoincidenciaPartes");
				throw new EntityOperationException();
			}
		}
	}

	private void checkObjetoJuicioConexidadSolicitadaMesa(
			ConexidadDeclarada conexidadDeclarada)
			throws EntityOperationException {
		String codigoObjetoJuicioConexo = ((conexidadDeclarada
				.getExpedienteConexo() != null) && (conexidadDeclarada
				.getExpedienteConexo().getObjetoJuicioPrincipal() != null)) ? conexidadDeclarada
				.getExpedienteConexo().getObjetoJuicioPrincipal().getCodigo()
				: null;
		ObjetoJuicio objetoJuicio = ObjetoJuicioSearch.findByCodigo(
				getEntityManager(), codigoObjetoJuicioConexo, getMateria());
		if ((objetoJuicio == null) || !objetoJuicio.isConexidadSolicitadaMesa()) {
			getStatusMessages().addFromResourceBundle(Severity.ERROR,
					"conexidad.conexidadDeclarada.objetoJuicio.noValido",
					codigoObjetoJuicioConexo);
			throw new EntityOperationException();
		}
	}

	private void checkCoincidenciaOJConexidadDeclarada(
			ConexidadDeclarada conexidadDeclarada)
			throws EntityOperationException {

		String codigoObjetoJuicio = (getObjetoJuicio() != null) ? getObjetoJuicio()
				.getCodigo() : null;
		String codigoObjetoJuicioConexo = ((conexidadDeclarada
				.getExpedienteConexo() != null) && (conexidadDeclarada
				.getExpedienteConexo().getObjetoJuicioPrincipal() != null)) ? conexidadDeclarada
				.getExpedienteConexo().getObjetoJuicioPrincipal().getCodigo()
				: null;
		if (!equals(codigoObjetoJuicio, codigoObjetoJuicioConexo)) {
			getStatusMessages().addFromResourceBundle(Severity.ERROR,
					"conexidad.conexidadDeclarada.sinCoincidenciaObjetoJuicio");
			throw new EntityOperationException();
		}
	}

	private boolean objetoJuicioVerificaFechaConexidadDeclarada() {
		return !isCamaraLaboral() || 
			(!isObjetoJuicioExtensiónResponsabilidad() && 
			 !isObjetoJuicioEjecucionHonorarios() && 
			 !isObjetoJuicioEjecucionSentencia() && 
			 !isObjetoJuicioRevisionCosaJuzgada());
	}

	private void checkFechaConexidadDeclarada(
			ConexidadDeclarada conexidadDeclarada)
			throws EntityOperationException {
		boolean ret = true;

		Date minimaFecha = null;

		if (objetoJuicioVerificaFechaConexidadDeclarada()) {
			String plazoConexidadDeclarada = null;
			if (conexidadDeclarada.isSolicitadaPorMesa()) {
				plazoConexidadDeclarada = ConfiguracionMateriaManager
						.instance().getConexidadSolicitadaMesaPlazo(
								getCamara(), getMateria());
			} else if (conexidadDeclarada.isPrevencion()) {
				plazoConexidadDeclarada = ConfiguracionMateriaManager
						.instance().getPrevencionDeclaradaPlazo(getCamara(),
								getMateria());
			} else {
				plazoConexidadDeclarada = ConfiguracionMateriaManager
						.instance().getConexidadDeclaradaPlazo(getCamara(),
								getMateria());
			}
			if (!Strings.isEmpty(plazoConexidadDeclarada)) {
				minimaFecha = ConfiguracionMateriaManager.getFechaAnterior(
						DateUtil.getToday(), plazoConexidadDeclarada);
			}

			if (minimaFecha != null) {
				Date fechaExpediente;
				if (TipoRadicacionEnumeration
						.isRadicacionSala(getTipoRadicacion())) {
					fechaExpediente = ExpedienteManager.instance()
							.getFechaAsignacionSala(
									conexidadDeclarada.getExpedienteConexo());
				} else {
					fechaExpediente = getFechaExpediente(conexidadDeclarada
							.getExpedienteConexo());
				}
				if ((fechaExpediente != null)
						&& minimaFecha.after(fechaExpediente)) {
					errorMsg("expedienteMe.error.fechaConexidadDeclarada",
							defaultParameterDateFormat.format(minimaFecha));
					throw new EntityOperationException();
				}
			}
		}
	}

	protected Date getFechaExpediente(Expediente expediente) {
		Date fecha = null;
		if (ExpedienteManager.instance().isIniciado(expediente)) {
			fecha = expediente.getFechaInicio();
		}
		if (fecha == null) {
			fecha = expediente.getFechaSorteoCarga();
		}
		if (fecha == null) {
			fecha = expediente.getFechaIngreso();
		}
		return fecha;
	}

	protected List<Oficina> getOficinas(Sorteo sorteo) {
		List<Oficina> list = new ArrayList<Oficina>();
		for (OficinaSorteo item : sorteo.getOficinaSorteoList()) {
			list.add(item.getOficina());
		}
		return list;
	}

	public boolean isCamaraLaboral() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCamaraLaboral(SessionState.instance()
						.getGlobalCamara());
	}

	public boolean isCamaraSeguridadSocial() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCamaraSeguridadSocial(SessionState.instance()
						.getGlobalCamara());
	}

	public boolean isObjetoJuicioExtensiónResponsabilidad() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.EXTENSION_RESPONSABILIDAD
				.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isObjetoJuicioEjecucionHonorarios() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.EJECUCION_HONORARIOS
				.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isObjetoJuicioEjecucionSentencia() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.EJECUCION_SENTENCIA
				.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isObjetoJuicioRevisionCosaJuzgada() {
		return (getObjetoJuicio() != null) && ObjetoJuicioEnumeration.REVISION_COSA_JUZGADA
				.equals(getObjetoJuicio().getCodigo());
	}

	public boolean isIngresoASala() {
		return TipoRadicacionEnumeration.isRadicacionSala(getTipoRadicacion());
	}

	public boolean isCorteSuprema() {
		return SessionState.instance().getGlobalCamara() != null
				&& CamaraManager.isCorteSuprema(SessionState.instance()
						.getGlobalCamara());
	}
}
