package com.base100.lex100.mesaEntrada.conexidad;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.Controller;
import org.jboss.seam.util.Strings;

import com.base100.lex100.component.enumeration.NaturalezaSubexpedienteEnumeration;
import com.base100.lex100.component.enumeration.TipoDocumentoIdentidadEnumeration;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.IntervinienteExp;
import com.base100.lex100.entity.Parametro;
import com.base100.lex100.entity.ParametroExpediente;
import com.base100.lex100.entity.TipoDocumentoIdentidad;
import com.base100.lex100.mesaEntrada.ingreso.ExpedienteMeAdd;
import com.base100.lex100.mesaEntrada.ingreso.IntervinienteEdit;
import com.base100.lex100.mesaEntrada.ingreso.IntervinienteEditList;

@Name("buscaConexidadTest")
public class BuscaConexidadTest extends Controller {

	private static final String FECHA_INICIO_PARAMETER = "fechaInicio";
	private static SimpleDateFormat stdDateFormat = new SimpleDateFormat("yyyy/MM/dd");

	public static BuscaConexidadTest instance() {
		return (BuscaConexidadTest) Component.getInstance(BuscaConexidadTest.class,true);
	}

	public void buscaConexidadPor1Interviniente(String nombre, ActorDemandado actorDemandado, Date iniciadoDespuesDeFecha, String codigoObjetoJuicio) {
		System.out.println("--------------------Busca conexidad por Nombre: "+nombre+" ----"+actorDemandado.getLabel()+" -----");		
		if(iniciadoDespuesDeFecha != null){
			System.out.println(" Iniciado despues de "+stdDateFormat.format(iniciadoDespuesDeFecha));
		}
		BuscadorConexidad buscadorConexidad = new BuscadorConexidad();
		List<String> objetosDeJuicio = BuscadorConexidad.parseObjetoJuicioExpression("$=,$=", codigoObjetoJuicio);
		List<IntervinienteExp> list = buscadorConexidad.buscaConexidadPor1Interviniente(nombre, actorDemandado, iniciadoDespuesDeFecha, objetosDeJuicio);
		System.out.println("CONEXIDADED ENCONTRADAS "+list.size());
		for(IntervinienteExp ie: list){
			System.out.println("Expediente: "+ ie.getExpediente().getNumero()+"/"+ ie.getExpediente().getAnio()+" Nombre: "+ie.getInterviniente().getNombre()+" orden "+ie.getTipoInterviniente().getNumeroOrden());
		}
		System.out.println("--------------------FIN-----------------");
	}
	

	public void buscaConexidadPorActorYDemandado(String nombreActor, String nombreDemandado, Date iniciadoDespuesDeFecha, String codigoObjetoJuicio ) {
		System.out.println("--------------------Busca conexidad por buscaConexidadPorActorYDemandado ------------");
		System.out.println("Actor: "+nombreActor);
		System.out.println("Demandado: "+nombreDemandado);
		if(iniciadoDespuesDeFecha != null){
			System.out.println(" Iniciado despues de "+stdDateFormat.format(iniciadoDespuesDeFecha));
		}
		
		BuscadorConexidad buscadorConexidad = new BuscadorConexidad();
		List<String> objetosDeJuicio = BuscadorConexidad.parseObjetoJuicioExpression("$=,$=", codigoObjetoJuicio);
		List<IntervinienteExp> list = buscadorConexidad.buscaConexidadPorActorYDemandado(nombreActor, nombreDemandado, null, iniciadoDespuesDeFecha, objetosDeJuicio);
		
		System.out.println("CONEXIDADED ENCONTRADAS "+list.size());
		for(IntervinienteExp ie: list){
			System.out.print("Expediente: "+ ie.getExpediente().getNumero()+"/"+ ie.getExpediente().getAnio()+" Actor: "+ie.getInterviniente().getNombre()+" orden "+ie.getTipoInterviniente().getNumeroOrden());
			System.out.println("buscando Actor: '"+nombreActor+"' y demandado '"+nombreDemandado+"'");
			
		}
		System.out.println("--------------------FIN-----------------");
	}

	public void buscaConexidadPorActorYDemandado(ListaIntervinientes actores, ListaIntervinientes demandados, Map<String, String> parametroMap, Date iniciadoDespuesDeFecha, String codigoObjetoJuicio) {
		System.out.println("--------------------Busca conexidad por buscaConexidadPorActorYDemandado ------------");
		System.out.println("Actores: "+actores.toString());
		System.out.println("Demandados: "+demandados.toString());
		if(iniciadoDespuesDeFecha != null){
			System.out.println(" Iniciado despues de "+stdDateFormat.format(iniciadoDespuesDeFecha));
		}
		
		BuscadorConexidad buscadorConexidad = new BuscadorConexidad();
		List<String> objetosDeJuicio = BuscadorConexidad.parseObjetoJuicioExpression("$=,$=", codigoObjetoJuicio);
		
//		List<IntervinienteExp> list = buscadorConexidad.buscaConexidadPorActorYDemandado(actores, demandados, parametros, iniciadoDespuesDeFecha, objetosDeJuicio);
//		
//		System.out.println("CONEXIDADED ENCONTRADAS "+list.size());
//		for(IntervinienteExp ie: list){
//			System.out.print("Expediente: "+ ie.getExpediente().getNumero()+"/"+ ie.getExpediente().getAnio()+" Actor/demandado: "+ie.getInterviniente().getNombre()+" orden "+ie.getTipoInterviniente().getNumeroOrden());
//			System.out.println("buscando Actors: '"+actores.toString()+"' y demandados '"+demandados.toString()+"'");
//			
//		}
//		System.out.println("--------------------FIN-----------------");

		List<String> naturalezas = new ArrayList<String>();
		naturalezas.add((String)NaturalezaSubexpedienteEnumeration.Type.principal.getValue());
		List<Expediente> list = buscadorConexidad.buscaConexidadGenerica(null, null, actores, demandados, parametroMap, naturalezas, iniciadoDespuesDeFecha, null, null, null, null, false, objetosDeJuicio, null, null, null, null, null, null, false, false);
		
		System.out.println("CONEXIDADED ENCONTRADAS "+list.size());
		for(Expediente expediente: list){
			System.out.print("Expediente: "+ expediente.getNumero()+"/"+ expediente.getAnio());
			System.out.println("buscando Actores: '"+actores.toString()+"' y demandados '"+demandados.toString()+"'");
			
		}
		System.out.println("--------------------FIN-----------------");

	}

	
	public void testIngreso(){
		ExpedienteMeAdd expedienteMeAdd = (ExpedienteMeAdd) Component.getInstance(ExpedienteMeAdd.class, true);
//		IntervinienteEditList actoresEdit = expedienteMeAdd.getActorEditList();
//		IntervinienteEditList demandadosEdit = expedienteMeAdd.getDemandadoEditList();
		IntervinienteEditList intervinientesEdit = expedienteMeAdd.getIntervinienteEditList();
		Map<String, String> parametroMap = new HashMap<String, String>();
		
		
		for (Parametro parametro : expedienteMeAdd.getParametroList()) {
			String stringValues = expedienteMeAdd.getParametroValue(parametro);
			if (!Strings.isEmpty(stringValues)) {
				parametroMap.put(parametro.getName(), stringValues);
			}
		}
		
		String objetoJuicio = null;
		
		ListaIntervinientes actores = new ListaIntervinientes(ActorDemandado.ACTOR);
		ListaIntervinientes demandados = new ListaIntervinientes(ActorDemandado.DEMANDADO);
		
		if(expedienteMeAdd.getObjetoJuicioEdit() != null){
			if(!Strings.isEmpty(expedienteMeAdd.getObjetoJuicioEdit().getCodigo())){
				objetoJuicio = expedienteMeAdd.getObjetoJuicioEdit().getCodigo();
			}			
		}
		
		Date iniciado = getFechaDesdeHace(100, 0, 0);
		
		String actor  = null;
		String demandado  = null;
		for(IntervinienteEdit i: intervinientesEdit.getList()){
			if(!i.isEmpty() && i.isActor()){
				buscaConexidadPor1Interviniente(i.getNombre(), ActorDemandado.ACTOR, iniciado, objetoJuicio);
				buscaConexidadPor1Interviniente(i.getNombre(), ActorDemandado.CUALQUIERA, iniciado, objetoJuicio);
				if(actor == null){
					actor = i.getNombre();
				}
				addIntervinienteEditToList(i, actores);
			}
		}
		for(IntervinienteEdit i: intervinientesEdit.getList()){
			if(!i.isEmpty() && i.isDemandado()){
				buscaConexidadPor1Interviniente(i.getNombre(), ActorDemandado.DEMANDADO, iniciado, objetoJuicio);
				buscaConexidadPor1Interviniente(i.getNombre(), ActorDemandado.CUALQUIERA, iniciado, objetoJuicio);
				if(demandado == null){
					demandado = i.getNombre();
				}
				addIntervinienteEditToList(i, demandados);
			}
		}
		if(actor != null && demandado != null){
			buscaConexidadPorActorYDemandado(actor, demandado, iniciado, objetoJuicio);
			//cruzado
			buscaConexidadPorActorYDemandado(demandado, actor, iniciado, objetoJuicio);
		}
		
		
		buscaConexidadPorActorYDemandado(actores, demandados, parametroMap, iniciado, objetoJuicio);
		//cruzado
		actores.setTipo(ActorDemandado.DEMANDADO);
		demandados.setTipo(ActorDemandado.ACTOR);
		buscaConexidadPorActorYDemandado(demandados, actores, parametroMap, iniciado, objetoJuicio);
	
	}

	private void addIntervinienteEditToList(IntervinienteEdit i, ListaIntervinientes actores) {
		actores.addNombre(i.getNombre());
		if(!Strings.isEmpty(i.getNumero())){
			if(isCuitCuil(i.getTipoDocumento())){
				actores.addCuitCuil(i.getNumero());
			}else if (isDni(i.getTipoDocumento())){
				actores.addDni(i.getNumero());
			}
		}
	}

	private boolean isCuitCuil(TipoDocumentoIdentidad tipoDocumento) {
		return TipoDocumentoIdentidadEnumeration.isCuitCuil(tipoDocumento);
	}

	private boolean isDni(TipoDocumentoIdentidad tipoDocumento) {
		return TipoDocumentoIdentidadEnumeration.isDni(tipoDocumento);
	}

	private static Date getFechaDesdeHace(int anios, int meses, int dias) {
		GregorianCalendar gc = new GregorianCalendar();
		
		gc.setTime(new Date());
		
		if(anios > 0){
			gc.add(GregorianCalendar.YEAR, -anios);
		}
		if(meses > 0){
			gc.add(GregorianCalendar.MONTH, -meses);
		}
		if(dias > 0){
			gc.add(GregorianCalendar.DAY_OF_YEAR, -dias);
		}
		return gc.getTime();
	}
		
	public static void main(String[] args) {
		for(int anios = 0; anios < 10;  anios++){
			for(int meses = 0; meses < 30; meses += 5){
				for(int dias = 0; dias < 500; dias += 40){
					testFechaDesdeHace(anios,meses,dias);					
				}				
			}			
		}
	}

	private static void testFechaDesdeHace(int anios, int meses, int dias) {
		Date date = getFechaDesdeHace(anios, meses, dias);
		System.out.println("fecha desde hace "+anios+" a "+meses+" m "+dias+" d ="+stdDateFormat.format(date));
		
	}
}


