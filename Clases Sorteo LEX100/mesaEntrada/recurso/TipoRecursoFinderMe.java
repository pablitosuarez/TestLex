package com.base100.lex100.mesaEntrada.recurso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.Strings;

import com.base100.lex100.component.configuration.SessionState;
import com.base100.lex100.component.enumeration.TipoSalaResolucionRecursoEnumeration;
import com.base100.lex100.entity.Materia;
import com.base100.lex100.entity.ObjetoJuicio;
import com.base100.lex100.entity.TipoCausa;
import com.base100.lex100.entity.TipoRecurso;
import com.base100.lex100.mesaEntrada.ingreso.ScreenController;


@Name("tipoRecursoFinderMe")
@Scope(ScopeType.CONVERSATION)
public class TipoRecursoFinderMe extends ScreenController
{
	private static final String TIPOS_RECURSO_MATERIA_STMT = "from TipoRecurso where materia = :materia and status = 0";
	private static final String WHERE_RECURSO_TIPO_INGRESO_SALA= "and tipoSala = :tipoSala";
	private static final String WHERE_RECURSO_CODIGO= "and codigo = :codigo";

	@In(create=true)
	private EntityManager entityManager;

	private List<TipoRecurso> tiposRecurso;
	private List<TipoRecurso> tiposRecursoCodigo;
	private List<TipoRecurso> tiposRecursoSala;
	private List<TipoRecurso> tiposRecursoMateria;
	private Map<String, String> lastKeysMap = new HashMap<String, String>();

	
	@SuppressWarnings("unchecked")
	private List<TipoRecurso> getTiposRecursoByTipoIngresoSala(Materia materia, String tipo) {
		if (tiposRecursoSala == null || keyChanged("MATERIA", materia.getAbreviatura()) || keyChanged("SALA", tipo)) {
			Query query = entityManager.createQuery(TIPOS_RECURSO_MATERIA_STMT+" "+WHERE_RECURSO_TIPO_INGRESO_SALA).
				setParameter("materia", materia).
				setParameter("tipoSala", tipo)
				;
			tiposRecurso = query.getResultList();
		}
		return tiposRecurso;
	}

	private boolean keyChanged(String key, String value) {
		String prev = lastKeysMap.get(key);
		boolean change = false;
		if(prev == null || !prev.equals(value)){
			change = true;
		}
		if(change){
			lastKeysMap.put(key, value);
		}
		return change;
	}

	@SuppressWarnings("unchecked")
	public List<TipoRecurso> getTiposRecursoQueja(Materia materia) {
		return getTiposRecursoByTipoIngresoSala(materia, TipoSalaResolucionRecursoEnumeration.Type.recurso_queja.getValue().toString());
	}
	
	public List<TipoRecurso> getTiposRecursoPredefinidos(TipoCausa tipoCausa, Materia materia, ObjetoJuicio objetoJuicio) {
		List<TipoRecurso> ret = null;
		if(materia!= null) {
			if(tipoCausa != null && !Strings.isEmpty(tipoCausa.getCodigoTipoRecurso())){
				ret = getTiposRecursoByCodigo(materia, tipoCausa.getCodigoTipoRecurso());
			}else if(objetoJuicio != null && !Strings.isEmpty(objetoJuicio.getCodigoTipoRecurso())){
				ret = getTiposRecursoByCodigo(materia, objetoJuicio.getCodigoTipoRecurso());
			}else if(tipoCausa != null && !Strings.isEmpty(tipoCausa.getTipoSala())){
				ret = getTiposRecursoByTipoIngresoSala(materia, tipoCausa.getTipoSala());
			}
		}
		return ret;
		
	}
	public List<TipoRecurso> getTiposRecurso(TipoCausa tipoCausa, Materia materia, ObjetoJuicio objetoJuicio) {
		List<TipoRecurso> ret = null;
		ret = getTiposRecursoPredefinidos(tipoCausa, materia, objetoJuicio);
		if((ret == null) || (ret.size() == 0)) {
			if(materia != null){
				ret = getTiposRecursoByMateria(materia);
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private List<TipoRecurso> getTiposRecursoByCodigo(Materia materia, String codigoTipoRecurso) {
		if (tiposRecursoCodigo == null || keyChanged("CODIGO", codigoTipoRecurso)) {
			Query query = entityManager.createQuery(TIPOS_RECURSO_MATERIA_STMT+" "+WHERE_RECURSO_CODIGO)
			.setParameter("materia", materia)
			.setParameter("codigo", codigoTipoRecurso);
			tiposRecursoCodigo = query.getResultList();
		}
		return tiposRecursoCodigo;
	}

	@SuppressWarnings("unchecked")
	public List<TipoRecurso> getTiposRecursoByMateria(Materia materia) {
		if (tiposRecursoMateria == null || keyChanged("MATERIA", materia.getAbreviatura())) {
				Query query = entityManager.createQuery(TIPOS_RECURSO_MATERIA_STMT).
				setParameter("materia", materia);
				tiposRecursoMateria = query.getResultList();
		}
		return tiposRecursoMateria;
	}

	public static TipoRecursoFinderMe instance(){
		return (TipoRecursoFinderMe) Component.getInstance(TipoRecursoFinderMe.class, true);
	}
}

