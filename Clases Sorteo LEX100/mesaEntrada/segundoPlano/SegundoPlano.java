package com.base100.lex100.mesaEntrada.segundoPlano;

import com.base100.lex100.component.enumeration.TipoProcesoSegundoPlanoEnumeration;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.ProcesoSegundoPlanoExp;

/**
 * Interfaz a implementar en las tareas de segundo plano de un expediente
 * 
 * @author Sergio Forastieri
 */
public interface SegundoPlano {
	
	void finalizarProcesoSegundoPlano(ProcesoSegundoPlanoExp procesoSegundoPlanoExp);
	
	ProcesoSegundoPlanoExp createProcesoSegundoPlano(Expediente expediente, TipoProcesoSegundoPlanoEnumeration.Type tipo);
	
}
