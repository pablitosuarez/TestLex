package com.base100.lex100.manager.expediente;

import java.io.StringWriter;

import javax.persistence.EntityManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.base100.lex100.entity.CambioAsignacionExp;
import com.base100.lex100.entity.DelitoExpediente;
import com.base100.lex100.entity.Expediente;
import com.base100.lex100.entity.IntervinienteExp;
import com.base100.lex100.manager.AbstractManager;
 

@Name("infoSorteoManager")
@Scope(ScopeType.CONVERSATION)
public class InfoSorteoManager extends AbstractManager {
	

	public static String generaInfoEstadoExpedienteSorteado(Expediente expediente, CambioAsignacionExp cambioAsignacionExp, boolean flagPase) {
	
		try {
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("sorteo");
			doc.appendChild(rootElement);

			Attr attr = doc.createAttribute("version");
			attr.setValue("1.0");
			rootElement.setAttributeNode(attr);

			Element fechaSorteo = doc.createElement("fechaSorteo");
			fechaSorteo.appendChild(doc.createTextNode(expediente.getFechaSorteoCarga() == null ? "" : expediente.getFechaSorteoCarga().toString()));
			rootElement.appendChild(fechaSorteo);

			if (cambioAsignacionExp !=  null) {
				Element oficinaSorteada = doc.createElement("oficinaSorteada");
				Element codigoOficinaSorteada = doc.createElement("codigoOficinaSorteada");
				codigoOficinaSorteada.appendChild(doc.createTextNode(cambioAsignacionExp.getOficinaConSecretaria() == null ? "" : cambioAsignacionExp.getOficinaConSecretaria().getCodigo()));
				
				Element descripcionOficinaSorteada = doc.createElement("descripcionOficinaSorteada");
				descripcionOficinaSorteada.appendChild(doc.createTextNode(cambioAsignacionExp.getOficinaConSecretaria() == null ? "" : cambioAsignacionExp.getOficinaConSecretaria().getDescriptionExpression()));
				
				oficinaSorteada.appendChild(codigoOficinaSorteada);
				oficinaSorteada.appendChild(descripcionOficinaSorteada);

				rootElement.appendChild(oficinaSorteada);
			}

			Element delitos = doc.createElement("delitos");
			for (DelitoExpediente delitoExpediente : expediente.getDelitoExpedienteList()) {
				Element delito = doc.createElement("delito");
				
				Element codigoDelito = doc.createElement("codigoDelito");
				codigoDelito.appendChild(doc.createTextNode((delitoExpediente.getDelito() == null || delitoExpediente.getDelito().getCodigo() == null) ? "" : delitoExpediente.getDelito().getCodigo()));
				
				Element descripcionDelito = doc.createElement("descripcionDelito");
				descripcionDelito.appendChild(doc.createTextNode((delitoExpediente.getDelito() == null || delitoExpediente.getDelito().getDescripcion() == null) ? "" : delitoExpediente.getDelito().getDescripcion()));

				delito.appendChild(codigoDelito);
				delito.appendChild(descripcionDelito);
				
				delitos.appendChild(delito);
			}
			rootElement.appendChild(delitos);

			Element intervinientes = doc.createElement("intervinientes");
			for (IntervinienteExp intervinienteExp : expediente.getIntervinienteExpList()) {
				if (intervinienteExp.getStatus() != -1) {
					Element interviniente = doc.createElement("interviniente");
					
					Element nombreInterviniente = doc.createElement("nombreInterviniente");
					nombreInterviniente.appendChild(doc.createTextNode((intervinienteExp.getInterviniente() == null || intervinienteExp.getInterviniente().getNombre() == null) ? "" : intervinienteExp.getInterviniente().getNombre()));
					
					Element tipoInterviniente = doc.createElement("tipoInterviniente");
					tipoInterviniente.appendChild(doc.createTextNode((intervinienteExp.getTipoInterviniente() == null || intervinienteExp.getTipoInterviniente().getAbreviatura() == null) ? "" : intervinienteExp.getTipoInterviniente().getAbreviatura()));
					
					Element descripcionTipoInterviniente = doc.createElement("descripcionTipoInterviniente");
					descripcionTipoInterviniente.appendChild(doc.createTextNode((intervinienteExp.getTipoInterviniente() == null || intervinienteExp.getTipoInterviniente().getDescripcion() == null) ? "" : intervinienteExp.getTipoInterviniente().getDescripcion()));
					
					Element estadoInterviniente = doc.createElement("estadoInterviniente");
					estadoInterviniente.appendChild(doc.createTextNode((intervinienteExp.getEstadoInterviniente() == null || intervinienteExp.getEstadoInterviniente().getCodigo() == null) ? "" : intervinienteExp.getEstadoInterviniente().getCodigo()));
					
					Element numeroOrden = doc.createElement("numeroOrden");
					numeroOrden.appendChild(doc.createTextNode((intervinienteExp.getTipoInterviniente() == null || intervinienteExp.getTipoInterviniente().getNumeroOrden() == null) ? "" : intervinienteExp.getTipoInterviniente().getNumeroOrden().toString()));
					
					Element identidadReservada = doc.createElement("identidadReservada");
					identidadReservada.appendChild(doc.createTextNode(intervinienteExp.isIdentidadReservada() ? "1" : "0"));
					
					Element menor = doc.createElement("menor");
					menor.appendChild(doc.createTextNode(intervinienteExp.isMenor() ? "1" : "0"));
					
					interviniente.appendChild(nombreInterviniente);
					interviniente.appendChild(tipoInterviniente);
					interviniente.appendChild(descripcionTipoInterviniente);
					interviniente.appendChild(estadoInterviniente);
					interviniente.appendChild(numeroOrden);
					interviniente.appendChild(identidadReservada);
					interviniente.appendChild(menor);

					intervinientes.appendChild(interviniente);
				}
			}
			rootElement.appendChild(intervinientes);

			Element organismoOrigen = doc.createElement("organismoOrigen");
			organismoOrigen.appendChild(doc.createTextNode((expediente.getExpedienteEspecial() == null || expediente.getExpedienteEspecial().getDependenciaOrigen()==null) ? "" : expediente.getExpedienteEspecial().getDependenciaOrigen()));
			rootElement.appendChild(organismoOrigen);

			Element tipoCausa = doc.createElement("tipoCausa");
			tipoCausa.appendChild(doc.createTextNode((expediente.getTipoCausa() == null || expediente.getTipoCausa().getDescripcion() == null) ? "" : expediente.getTipoCausa().getDescripcion()));
			rootElement.appendChild(tipoCausa);

			Element pase = doc.createElement("pase");
			pase.appendChild(doc.createTextNode(flagPase ? "1" : "0"));
			rootElement.appendChild(pase);

			DOMSource source = new DOMSource(doc);

			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(source, result);
			
//			System.out.println(writer.toString());
			
			return writer.toString();

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}