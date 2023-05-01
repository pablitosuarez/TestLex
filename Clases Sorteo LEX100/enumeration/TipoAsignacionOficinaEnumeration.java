package com.base100.lex100.component.enumeration;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.Messages;

import com.base100.expand.seam.framework.component.enumeration.AbstractEnumeration;
import com.base100.expand.seam.framework.component.enumeration.ILabeledEnum;


/**
 * Enumeration.
 * 
 * Generated by Expand.
 *
 */
@Name("tipoAsignacionOficinaEnumeration")
@Scope(ScopeType.APPLICATION)
public class TipoAsignacionOficinaEnumeration extends AbstractEnumeration {	
	public enum Type implements ILabeledEnum {
	
		asignacion("tipoAsignacionOficinaEnumeration.asignacion", "A", null),
		reasignacion("tipoAsignacionOficinaEnumeration.reasignacion", "R", null);

		private String label;
		private Object value;
		private String image;
		
		private Type(String label) {
			this.label = label;
			this.value = this;
		}

		private Type(String label, Object value) {
			this.label = label;
			this.value = value;
		}

		private Type(String label, Object value, String image) {
			this.label = label;
			this.value = value;
			this.image = image;
		}

		public String getLabel() {
			return label;
		}
	
		public Object getValue() {
			return value;
		}

		public String getImage() {
			return image;
		}
		
		public String getName() {
			return this.name();
		}
		
	}

	protected ILabeledEnum[] getEnumValues() {
		return Type.values();
	}

}

