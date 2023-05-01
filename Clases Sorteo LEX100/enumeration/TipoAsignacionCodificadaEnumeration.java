package com.base100.lex100.component.enumeration;

import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.Strings;

import com.base100.lex100.entity.TipoAsignacion;
import com.base100.lex100.homelistener.ResolucionRecursoExpHomeListener;
import com.base100.expand.seam.framework.Util;
import com.base100.expand.seam.framework.component.enumeration.IEntityEnumeration;
import com.base100.expand.seam.framework.component.enumeration.AbstractEntityEnumeration;



@Name("tipoAsignacionCodificadaEnumeration")
@Scope(ScopeType.EVENT)
public class TipoAsignacionCodificadaEnumeration extends AbstractEntityEnumeration<TipoAsignacion> implements IEntityEnumeration<TipoAsignacion> {

	@In(create=true)
	private ResolucionRecursoExpHomeListener resolucionRecursoExpHomeListener;
	private String categoria;
	
   private static final String[] FILTERS = {
	"lower(descriptionExpression) like #{likePattern[tipoAsignacionCodificadaEnumeration.selectionPattern]}",
	"tipoAsignacion.categoria = #{tipoAsignacionCodificadaEnumeration.categoria}",
   };

   @Override
	public String getOrder() {
		return "tipoAsignacion.codigo";
	}
   
   @Override
    public Integer getMaxResults()
    {
    	return null;
    }
    
    @Override
    public String getEjbql() 
    { 
        return "select tipoAsignacion from TipoAsignacion tipoAsignacion where tipoAsignacion.status <> -1";
    }
    
    @Override
    public String[] getFilters()
    {
    	return FILTERS;
    }
    
	@SuppressWarnings("unchecked")
	public TipoAsignacion getItem(String pattern){
		if(!Strings.isEmpty(pattern)){
			Query query = getEntityManager().createQuery("from TipoAsignacion where descriptionExpression = '"+pattern+"'");
			query.setMaxResults(1);
			List<TipoAsignacion> list = query.getResultList();
			if(list.size() > 0){
				return (TipoAsignacion)(list.get(0));
			}
		}
		return null;
	}
	
	public List<TipoAsignacion> getItemsByCategoria(String categoria) {
		setCategoria(categoria);
		refresh();
		setEjbql(null);
		return getResultList();
	}

	public TipoAsignacion getItemByCodigoYCategoria(String codigo, String categoria){
		String stmt = "from TipoAsignacion where codigo = :codigo";
		if(categoria != null){
			stmt += " and categoria = :categoria";
		}
		Query query = getEntityManager().createQuery(stmt);
		query.setParameter("codigo", codigo);
		if(categoria != null){
			query.setParameter("categoria", categoria);
		}		
		query.setMaxResults(1);
		List<TipoAsignacion> list = query.getResultList();
		if(list.size() > 0){
			return (TipoAsignacion)(list.get(0));
		}
		return null;		
	}

	public String labelFor(TipoAsignacion item) {
		if(item == null){
			return "";
		}else{
			return Util.rtrim(item.getDescriptionExpression());
		}		
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public static TipoAsignacionCodificadaEnumeration instance(){
		return (TipoAsignacionCodificadaEnumeration) Component.getInstance(TipoAsignacionCodificadaEnumeration.class, true);
	}
}

