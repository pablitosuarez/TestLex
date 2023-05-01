package com.base100.lex100.mesaEntrada.conexidad;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.util.Strings;

import com.base100.expand.seam.framework.entity.AbstractEntityHome;
import com.base100.expand.seam.framework.entity.AbstractEntitySearch;
import com.base100.lex100.component.audit.AbstractLogicalDeletionEntityList;
import com.base100.lex100.component.configuration.Configuration;
import com.base100.lex100.component.enumeration.EstadoIntervinienteEnumeration;
import com.base100.lex100.component.enumeration.TipoComparacionEnumeration;
import com.base100.lex100.component.enumeration.TipoFinalizacionEnumeration;
import com.base100.lex100.controller.entity.expediente.ExpedienteHome;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.Parametro;

/**
 * Class for ExpedienteIngreso List objects.
 * 
 * Generated by Expand.
 *
 */
@Name("antecedenteList")
public class AntecedenteList extends AbstractLogicalDeletionEntityList<Expediente>
{
	@In(create=true)
	private Configuration configuration;
    @In(create=true)
    private AntecedenteSearch antecedenteSearch;
    @In(create=true)
    private ExpedienteHome expedienteHome;
    
    private static final String[] FILTERS = {
		"expediente.camaraActual = #{antecedenteSearch.filter.camara}",
		"expediente.camara = #{antecedenteSearch.filter.camaraOriginal}",
		"expediente.materia.grupo = #{antecedenteSearch.filter.materiaGrupo}",
		"expediente.naturaleza = #{antecedenteSearch.filter.naturalezaSubexpediente}",
		"expediente.naturaleza in (#{antecedenteSearch.filter.naturalezaSubexpedienteList})",
		"expediente.anio >= #{antecedenteSearch.filter.fromAnioExpediente}",
		"expediente.fechaSorteoCarga >= #{antecedenteSearch.filter.fromFechaIngreso}",
		"expediente.fechaSorteoCarga <= #{antecedenteSearch.filter.toFechaIngreso}",
		"expediente != #{antecedenteSearch.filter.expedienteBase}",
		"expediente.expedienteEspecial.dependenciaOrigen = #{antecedenteSearch.filter.dependenciaOrigen}",
		"expediente.expedienteEspecial.codigoRelacionado = #{antecedenteSearch.filter.codigoRelacionado}",
		"expediente.tipoCausa = #{antecedenteSearch.filter.tipoCausa}",
//		"expediente.estadoExpediente.codigo = #{antecedenteSearch.filter.estadoValido}",
//		"expediente.estadoExpediente.codigo <> #{antecedenteSearch.filter.estadoNoValido}",
//		"(true = #{antecedenteSearch.filter.soloEnTramite} and (expediente.estadoExpediente.tipoFinalizacion <> '" + TipoFinalizacionEnumeration.Type.finalización_expediente.getValue() + "' or expediente.estadoExpediente.tipoFinalizacion is null) )",
		"expediente.estadoExpediente.id in (#{antecedenteSearch.filter.estadosValidosIds})",
		"expediente.estadoExpediente.id not in (#{antecedenteSearch.filter.estadosNoValidosIds})",

		"expediente.tipoCausa.codigo in (#{antecedenteSearch.filter.codigoTiposCausaIncluidos})",
		
//		"0 < (select count(*) from ObjetoJuicio o join o.expedienteList expedienteObjeto where expedienteObjeto = expediente and o.status <> -1 and o = (#{antecedenteSearch.filter.objetoJuicio}))",
//		"0 < (select count(*) from ObjetoJuicio o join o.expedienteList expedienteObjeto where expedienteObjeto = expediente and o.status <> -1 and o in (#{antecedenteSearch.filter.objetosJuicioIncluidos}))",
//		"0 < (select count(*) from ObjetoJuicio o join o.expedienteList expedienteObjeto where expedienteObjeto = expediente and o.status <> -1 and o not in (#{antecedenteSearch.filter.objetosJuicioExcluidos}))",
//		"0 < (select count(*) from ObjetoJuicio o join o.expedienteList expedienteObjeto where expedienteObjeto = expediente and o.status <> -1 and o.codigo in (#{antecedenteSearch.filter.codigoObjetosJuicioIncluidos}))",
//		"0 < (select count(*) from ObjetoJuicio o join o.expedienteList expedienteObjeto where expedienteObjeto = expediente and o.status <> -1 and o.codigo not in (#{antecedenteSearch.filter.codigoObjetosJuicioExcluidos}))",
		"expediente.objetoJuicio = (#{antecedenteSearch.filter.objetoJuicio})",
		"expediente.objetoJuicio in (#{antecedenteSearch.filter.objetosJuicioIncluidos})",
		"expediente.objetoJuicio not in (#{antecedenteSearch.filter.objetosJuicioExcluidos})",
		"expediente.objetoJuicio.codigo in (#{antecedenteSearch.filter.codigoObjetosJuicioIncluidos})",
		"expediente.objetoJuicio.codigo not in (#{antecedenteSearch.filter.codigoObjetosJuicioExcluidos})",

		"expediente.objetoJuicio.tema.codigo in (#{antecedenteSearch.filter.codigoTemasIncluidos})",
		"expediente.objetoJuicio.tema.id in (#{antecedenteSearch.filter.idTemasIncluidos})",

		"expediente.voluntario = (#{antecedenteSearch.filter.voluntario})",
		
    	"exists (select 1 from OficinaAsignacionExp oa where expediente = oa.expediente and oa.tipoRadicacion = #{antecedenteSearch.filter.conTipoRadicacion})",
    	"exists (select 1 from OficinaAsignacionExp oa where expediente = oa.expediente and oa.oficina.camara = #{antecedenteSearch.filter.camaraRadicacion})",
//
		// indistinta cuit
		"0 < (select count(*) from IntervinienteExp i where i.expediente = expediente and i.identidadReservada = 0 and i.status <> -1 and  (i.estadoInterviniente is null or i.estadoInterviniente in (select ei from EstadoInterviniente ei where ei.codigo <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "')) and (i.tipoInterviniente.numeroOrden = 1 or i.tipoInterviniente.numeroOrden = 2) and i.interviniente.numeroDocId in (#{antecedenteSearch.filter.cuitParte1List}))",
		"0 < (select count(*) from IntervinienteExp i where i.expediente = expediente and i.identidadReservada = 0 and i.status <> -1 and  (i.estadoInterviniente is null or i.estadoInterviniente in (select ei from EstadoInterviniente ei where ei.codigo <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "')) and (i.tipoInterviniente.numeroOrden = 1 or i.tipoInterviniente.numeroOrden = 2) and i.interviniente.numeroDocId in (#{antecedenteSearch.filter.cuitParte2List}))",
		// paralela y cruzada cuit
		"0 < (select count(*) from IntervinienteExp i where i.expediente = expediente and i.tipoInterviniente.numeroOrden = 1 and i.identidadReservada = 0 and i.status <> -1 and  (i.estadoInterviniente is null or i.estadoInterviniente in (select ei from EstadoInterviniente ei where ei.codigo <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "')) and i.interviniente.numeroDocId in (#{antecedenteSearch.filter.cuitActorList}))",
		"0 < (select count(*) from IntervinienteExp i where i.expediente = expediente and i.tipoInterviniente.numeroOrden = 2 and i.identidadReservada = 0 and i.status <> -1 and  (i.estadoInterviniente is null or i.estadoInterviniente in (select ei from EstadoInterviniente ei where ei.codigo <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "')) and i.interviniente.numeroDocId in (#{antecedenteSearch.filter.cuitDemandadoList}))",
		// indistinta dni
		"0 < (select count(*) from IntervinienteExp i where i.expediente = expediente and i.identidadReservada = 0 and i.status <> -1 and  (i.estadoInterviniente is null or i.estadoInterviniente in (select ei from EstadoInterviniente ei where ei.codigo <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "')) and (i.tipoInterviniente.numeroOrden = 1 or i.tipoInterviniente.numeroOrden = 2) and i.interviniente.dni in (#{antecedenteSearch.filter.dniParte1List}))",
		"0 < (select count(*) from IntervinienteExp i where i.expediente = expediente and i.identidadReservada = 0 and i.status <> -1 and  (i.estadoInterviniente is null or i.estadoInterviniente in (select ei from EstadoInterviniente ei where ei.codigo <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "')) and (i.tipoInterviniente.numeroOrden = 1 or i.tipoInterviniente.numeroOrden = 2) and i.interviniente.dni in (#{antecedenteSearch.filter.dniParte2List}))",
		// paralela y cruzada dni
		"0 < (select count(*) from IntervinienteExp i where i.expediente = expediente and i.tipoInterviniente.numeroOrden = 1 and i.identidadReservada = 0 and i.status <> -1 and  (i.estadoInterviniente is null or i.estadoInterviniente in (select ei from EstadoInterviniente ei where ei.codigo <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "')) and i.interviniente.dni in (#{antecedenteSearch.filter.dniActorList}))",
		"0 < (select count(*) from IntervinienteExp i where i.expediente = expediente and i.tipoInterviniente.numeroOrden = 2 and i.identidadReservada = 0 and i.status <> -1 and  (i.estadoInterviniente is null or i.estadoInterviniente in (select ei from EstadoInterviniente ei where ei.codigo <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "')) and i.interviniente.dni in (#{antecedenteSearch.filter.dniDemandadoList}))",
		// indistinta nombre
		"0 < (select count(*) from IntervinienteExp i where i.expediente = expediente and i.identidadReservada = 0 and i.status <> -1 and  (i.estadoInterviniente is null or i.estadoInterviniente in (select ei from EstadoInterviniente ei where ei.codigo <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "')) and (i.tipoInterviniente.numeroOrden = 1 or i.tipoInterviniente.numeroOrden = 2) and i.interviniente.nombre in (#{antecedenteSearch.filter.nombreParte1List}))",
		"0 < (select count(*) from IntervinienteExp i where i.expediente = expediente and i.identidadReservada = 0 and i.status <> -1 and  (i.estadoInterviniente is null or i.estadoInterviniente in (select ei from EstadoInterviniente ei where ei.codigo <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "')) and (i.tipoInterviniente.numeroOrden = 1 or i.tipoInterviniente.numeroOrden = 2) and i.interviniente.nombre in (#{antecedenteSearch.filter.nombreParte2List}))",
		// paralela y cruzada nombre
		"0 < (select count(*) from IntervinienteExp i where i.expediente = expediente and i.tipoInterviniente.numeroOrden = 1 and i.identidadReservada = 0 and i.status <> -1 and  (i.estadoInterviniente is null or i.estadoInterviniente in (select ei from EstadoInterviniente ei where ei.codigo <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "')) and i.interviniente.nombre in (#{antecedenteSearch.filter.nombreActorList}))",
		"0 < (select count(*) from IntervinienteExp i where i.expediente = expediente and i.tipoInterviniente.numeroOrden = 2 and i.identidadReservada = 0 and i.status <> -1 and  (i.estadoInterviniente is null or i.estadoInterviniente in (select ei from EstadoInterviniente ei where ei.codigo <> '" + EstadoIntervinienteEnumeration.SOBRESEIDO_CODIGO + "')) and i.interviniente.nombre in (#{antecedenteSearch.filter.nombreDemandadoList}))",
    };


    @Override
    public String getEjbql() 
    {
    	String stmt = "select expediente from Expediente expediente where (verificacionMesa = 'M' or verificacionMesa = 'N')";
    	if ((antecedenteSearch.getTipoComparacionPartes() != null) && (antecedenteSearch.getExpedienteOrigen() != null)){
    		stmt += getFiltroPartesCombinado();
    	}
    	return stmt;
    }

    private String getFiltroPartesCombinado() {
    	
    	StringBuffer partesFilter = new StringBuffer();

    	List<String> actorCuitList = null;
    	List<String> actorDniList = null;
    	List<String> actorNombreList = null;
		List<String> demandadoCuitList = null;
    	List<String> demandadoDniList = null;
    	List<String> demandadoNombreList = null;
		String actorCuits = null; 
		String actorDnis = null; 
		String actorNombres = null; 
		String demandadoCuits = null; 
		String demandadoDnis = null;
		String demandadoNombres = null;

		boolean excluirPersonasJuridicas = antecedenteSearch.isExcluirPersonasJuridicas();
		
    	if (!ConexidadManager.DEMANDADO.equals(antecedenteSearch.getTipoFiltroPartes())) {
			Integer tipoBusca = ConexidadManager.getTipoBusca(ConexidadManager.ACTOR, antecedenteSearch.getTipoComparacionPartes());
	    	actorCuitList = ConexidadManager.getCuitParteList(antecedenteSearch.getExpedienteOrigen(), ConexidadManager.ACTOR, tipoBusca, excluirPersonasJuridicas);
	    	actorDniList = ConexidadManager.getDniParteList(antecedenteSearch.getExpedienteOrigen(), ConexidadManager.ACTOR, tipoBusca, excluirPersonasJuridicas);
	    	actorNombreList = ConexidadManager.getNombreParteList(antecedenteSearch.getExpedienteOrigen(), ConexidadManager.ACTOR, tipoBusca, excluirPersonasJuridicas);
			actorCuits = listToString(actorCuitList); 
			actorDnis = listToString(actorDniList); 
			actorNombres = listToString(actorNombreList); 
    	}
    	if (!ConexidadManager.ACTOR.equals(antecedenteSearch.getTipoFiltroPartes())) {
			Integer tipoBusca = ConexidadManager.getTipoBusca(ConexidadManager.DEMANDADO, antecedenteSearch.getTipoComparacionPartes());
			demandadoCuitList = ConexidadManager.getCuitParteList(antecedenteSearch.getExpedienteOrigen(), ConexidadManager.DEMANDADO, tipoBusca, excluirPersonasJuridicas);
	    	demandadoDniList = ConexidadManager.getDniParteList(antecedenteSearch.getExpedienteOrigen(), ConexidadManager.DEMANDADO, tipoBusca, excluirPersonasJuridicas);
	    	demandadoNombreList = ConexidadManager.getNombreParteList(antecedenteSearch.getExpedienteOrigen(), ConexidadManager.DEMANDADO, tipoBusca, excluirPersonasJuridicas);
			demandadoCuits = listToString(demandadoCuitList); 
			demandadoDnis = listToString(demandadoDniList); 
			demandadoNombres = listToString(demandadoNombreList); 
    	}
    	StringBuffer actoresFilter = new StringBuffer();
    	StringBuffer demandadosFilter = new StringBuffer();
    	if(TipoComparacionEnumeration.Type.indistinta.getValue().equals(antecedenteSearch.getTipoComparacionPartes())) {
    		// actores
        	if (!ConexidadManager.DEMANDADO.equals(antecedenteSearch.getTipoFiltroPartes())) {
	    		addParteFilter(actoresFilter, actorCuits, "numeroDocId", null);
	   			addParteFilter(actoresFilter, actorDnis, "dni", null);
	   			addParteFilter(actoresFilter, actorNombres, "nombre", null);
        	}
    		// demandados
        	if (!ConexidadManager.ACTOR.equals(antecedenteSearch.getTipoFiltroPartes())) {
	   			addParteFilter(demandadosFilter, demandadoCuits, "numeroDocId", null);
	   			addParteFilter(demandadosFilter, demandadoDnis, "dni", null);
	   			addParteFilter(demandadosFilter, demandadoNombres, "nombre", null);
        	}
     	} else if(TipoComparacionEnumeration.Type.cruzada.getValue().equals(antecedenteSearch.getTipoComparacionPartes())) {
    		// actores
        	if (!ConexidadManager.DEMANDADO.equals(antecedenteSearch.getTipoFiltroPartes())) {
	   			addParteFilter(actoresFilter, actorCuits, "numeroDocId", ConexidadManager.DEMANDADO);
	   			addParteFilter(actoresFilter, actorDnis, "dni", ConexidadManager.DEMANDADO);
	   			addParteFilter(actoresFilter, actorNombres, "nombre", ConexidadManager.DEMANDADO);
        	}
    		// demandados
        	if (!ConexidadManager.ACTOR.equals(antecedenteSearch.getTipoFiltroPartes())) {
	   			addParteFilter(demandadosFilter, demandadoCuits, "numeroDocId", ConexidadManager.ACTOR);
	   			addParteFilter(demandadosFilter, demandadoDnis, "dni", ConexidadManager.ACTOR);
	   			addParteFilter(demandadosFilter, demandadoNombres, "nombre", ConexidadManager.ACTOR);
        	}
    	} else if(TipoComparacionEnumeration.Type.paralela.getValue().equals(antecedenteSearch.getTipoComparacionPartes())) {
    		// actores
        	if (!ConexidadManager.DEMANDADO.equals(antecedenteSearch.getTipoFiltroPartes())) {
	   			addParteFilter(actoresFilter, actorCuits, "numeroDocId", ConexidadManager.ACTOR);
	   			addParteFilter(actoresFilter, actorDnis, "dni", ConexidadManager.ACTOR);
	   			addParteFilter(actoresFilter, actorNombres, "nombre", ConexidadManager.ACTOR);
        	}
    		// demandados
        	if (!ConexidadManager.ACTOR.equals(antecedenteSearch.getTipoFiltroPartes())) {
	   			addParteFilter(demandadosFilter, demandadoCuits, "numeroDocId", ConexidadManager.DEMANDADO);
	   			addParteFilter(demandadosFilter, demandadoDnis, "dni", ConexidadManager.DEMANDADO);
	   			addParteFilter(demandadosFilter, demandadoNombres, "nombre", ConexidadManager.DEMANDADO);
        	}
    	}
    	if((actoresFilter.length() > 0) || (demandadosFilter.length() > 0)) {
    		partesFilter.append(" and (");
    		if(actoresFilter.length() > 0) { 
    			partesFilter.append("(").append(actoresFilter).append(")");
    		}
        	if((actoresFilter.length() > 0) && (demandadosFilter.length() > 0)) {
           		partesFilter.append(" and ");
        	}
    		if(demandadosFilter.length() > 0) { 
    			partesFilter.append("(").append(demandadosFilter).append(")");;
    		}
    		partesFilter.append(")");
    	}
    	return partesFilter.toString();
	}

	private void addParteFilter(StringBuffer filter, String lista, String field, Integer tipo) {
		if (!Strings.isEmpty(lista)) {
			if (filter.length() > 0) {
				filter.append(" or ");
			}
			filter.append(" ( 0 < (select count(*) from IntervinienteExp i where i.expediente = expediente and i.status <> -1 ");
			if(tipo == null) {
				filter.append(" and (i.tipoInterviniente.numeroOrden = 1 or i.tipoInterviniente.numeroOrden = 2)");
			} else {
				filter.append(" and i.tipoInterviniente.numeroOrden = ").append(tipo).append(" ");
			}
			filter.append(" and i.interviniente.").append(field).append(" in (").append(lista).append("))) ");
		}
	}

	private String listToString(List<String> list) {
		if (list != null) {
			StringBuffer result = new StringBuffer();
			for(String value:list) {
				if (result.length() > 0) {
					result.append(',');
				}
				result.append("'").append(value).append("'");
			}
			return result.toString();
		}
		return null;
	}

	@Override
    public String[] getFilters()
    {
       	return FILTERS;
    }
    
	@Override
	public String[] getDefaultRestrictions() {
		String[] defaultRestrictions = super.getDefaultRestrictions();
		
		ArrayList<String> parameterRestrictions =  getParameterRestrictions(antecedenteSearch.getObjetoJuicioPrincipal());
		if (defaultRestrictions != null) {
			for(String restriction: defaultRestrictions) {
				parameterRestrictions.add(restriction);
			}
		}
		return parameterRestrictions.toArray(new String[parameterRestrictions.size()]);
	};

    public ArrayList<String> getParameterRestrictions(ObjetoJuicio objetoJuicio) {
    	ArrayList<String> parameterRestrictions = new ArrayList<String>();
    	if (objetoJuicio != null){
	    	for(Parametro parametro: objetoJuicio.getParametroList()) {
	    		parameterRestrictions.add("0 < (select count(*) from ParametroExpediente pe where pe.expedienteIngreso.expediente = expediente and pe.status <> -1 and pe.parametro.name = '" + parametro.getName() + "' and pe.valor in (#{antecedenteSearch.filter.parametro." + parametro.getName() + "}))");
	    	}
    	}
    	return parameterRestrictions;
    }
    
    @Override
    public Integer getRowsPerPage()
    {
    	return configuration.getRowsPerPage();
    }
    
	public AbstractEntityHome<Expediente> getEntityHome() {
		return expedienteHome;
	}

	public AbstractEntitySearch<Expediente> getEntitySearch() {
		return antecedenteSearch;
	}
	
	/* (non-Javadoc)
	 * @see com.base100.expand.seam.framework.entity.AbstractEntityList#doSelection(java.lang.Object, java.lang.String)
	 */
	public String doSelection (Expediente expediente, String page) {
		return super.doSelection(expediente, page);
	}

	public String doSelection (Expediente expediente, String page, String returnPage) {
		page = doSelection(expediente, page);
		if (returnPage != null && !getEntitySearch().hasReturnPage()) {
			getEntitySearch().addReturnPage(returnPage, null);
		}
		return page;
	}

}
