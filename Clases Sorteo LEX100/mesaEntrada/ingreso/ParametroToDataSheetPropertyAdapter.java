package com.base100.lex100.mesaEntrada.ingreso;

import groovy.transform.Synchronized;

import java.util.List;

import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.seam.util.Strings;
import org.jfree.chart.plot.MultiplePiePlot;

import com.base100.lex100.component.datasheet.IDataSheetProperty;
import com.base100.lex100.component.datasheet.NameValue;
import com.base100.lex100.component.datasheet.controller.dataSheetProperty.DataSheetPropertyUtils;
import com.base100.lex100.component.datasheet.entity.DataSheet;
import com.base100.lex100.component.datasheet.entity.DataSheetPropertyValue;
import com.base100.lex100.component.enumeration.TipoDatoEnumeration;
import com.base100.lex100.entity.Parametro;

public class ParametroToDataSheetPropertyAdapter implements IDataSheetProperty {

	private static final Integer MULTIPLE_LENGHT = 100;
	private static final Integer MULTIPLE_MAXLENGHT = 1024;
	private Parametro parametro;
	
	private String parameters;
	
	private static DataSheet dataSheet;

	public ParametroToDataSheetPropertyAdapter(Parametro parametro) {
		this(parametro, false);
	}

	public ParametroToDataSheetPropertyAdapter(Parametro parametro, boolean editMode) {
		this.parametro = parametro;

		List<NameValue> nameValueList = DataSheetPropertyUtils.getParametersAsList(parametro.getParameters());

		if (editMode) {
			int disabledParameterIdx = indexOf(nameValueList, DataSheetPropertyUtils.DISABLED_PARAMETER);
			if (disabledParameterIdx >= 0) {
				nameValueList.remove(disabledParameterIdx);
			}
		}
		
		if(Boolean.TRUE.equals(parametro.getMultiple())) {
			if(TipoDatoEnumeration.Type.fecha.getValue().equals(parametro.getType())) {
				nameValueList.add(new NameValue(DataSheetPropertyUtils.COMMENT_PARAMETER, "parametro.multiple.input.date.comment"));
			} else {
				nameValueList.add(new NameValue(DataSheetPropertyUtils.COMMENT_PARAMETER, "parametro.multiple.input.comment"));
			}
			nameValueList.add(new NameValue(DataSheetPropertyUtils.LAYOUT_PARAMETER, "editComment"));
			nameValueList.add(new NameValue(DataSheetPropertyUtils.MAX_LENGTH_PARAMETER, "1024"));
		} else {
			if(TipoDatoEnumeration.Type.CUIL_CUIT_CDI.getValue().equals(parametro.getType())) {
				nameValueList.add(new NameValue(DataSheetPropertyUtils.LAYOUT_PARAMETER, "editComment"));
				nameValueList.add(new NameValue(DataSheetPropertyUtils.COMMENT_PARAMETER, "parametro.input.cuitCuil.comment"));
			} else if(TipoDatoEnumeration.Type.domicilio.getValue().equals(parametro.getType())) {
				nameValueList.add(new NameValue(DataSheetPropertyUtils.LAYOUT_PARAMETER, "editComment"));
				nameValueList.add(new NameValue(DataSheetPropertyUtils.COMMENT_PARAMETER, "parametro.input.domicilio.comment"));
			}
		}
		if(TipoDatoEnumeration.Type.alfanumericoMayusculas.getValue().equals(parametro.getType())) {
			nameValueList.add(new NameValue(DataSheetPropertyUtils.STYLE_PARAMETER, "text-transform:uppercase;"));
		}

		parameters = DataSheetPropertyUtils.getParametersAsString(nameValueList);
	}
	
	private int indexOf(List<NameValue> nameValueList, String name) {
		int idx = 0;

		if (!Strings.isEmpty(name)) {
			for (NameValue nameValue: nameValueList) {
				if (name.equals(nameValue.getName())) {
					return idx;
				}
				idx++;
			}
		}
		return -1;
	}

	public Integer getDataSheetPropertyId() {
		return parametro.getId();
	}

	public Integer getNum() {
		return parametro.getId();
	}

	public String getName() {
		return parametro.getName();
	}

	public String getLabel() {
		return parametro.getLabel();
	}

	public String getType() {		
		if(Boolean.TRUE.equals(parametro.getMultiple())) {
			return TipoDatoEnumeration.Type.alfanumerico.getValue().toString();
		}
		if (TipoDatoEnumeration.Type.CUIL_CUIT_CDI.getValue().equals(parametro.getType())) {
			return TipoDatoEnumeration.Type.alfanumerico.getValue().toString();
		}
		if (TipoDatoEnumeration.Type.domicilio.getValue().equals(parametro.getType())) {
			return TipoDatoEnumeration.Type.alfanumerico.getValue().toString();
		}
		return parametro.getType();
	}

	public Integer getLength() {
		if(Boolean.TRUE.equals(parametro.getMultiple())) {
			return MULTIPLE_LENGHT;
		}
		return parametro.getLength();
	}

	public Integer getPrecision() {
		return parametro.getPrecision();
	}

	public String getParameters() {
		return parameters;
	}

	public String getRendered() {
		return parametro.getRendered();
	}

	@Synchronized
	public DataSheet getDataSheet() {
		if(this.dataSheet == null){
			this.dataSheet = new DataSheet();
			this.dataSheet.setAllowRequired(true);
		}
		return this.dataSheet;
	}

	public List<DataSheetPropertyValue> getDataSheetPropertyValueList() {
		return null;
	}

	public Parametro getParametro() {
		return parametro;
	}

}
