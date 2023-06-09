package com.base100.lex100.entity;
// Generated by Expand 4.0.0 

import static javax.persistence.GenerationType.SEQUENCE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import com.base100.expand.annotation.ControlType;
import com.base100.expand.annotation.DataDefinition;
import com.base100.expand.annotation.EntityDefinition;
import com.base100.expand.annotation.EntityGroup;
import com.base100.expand.annotation.EntityIcon;
import com.base100.expand.annotation.GenerationProperty;
import com.base100.expand.annotation.InteractiveModuleProperties;
import com.base100.expand.annotation.RestrictedEntity;
import com.base100.expand.annotation.SelectItem;
import com.base100.expand.annotation.SelectItems;
import com.base100.expand.annotation.index.IndexField;
import com.base100.lex100.component.identity.IdentificableEntityListener;

/**
 * NumeradorExpediente generated by Expand 4.0.0
 */
@Entity
@EntityDefinition(label = "Numerador Expediente", listLabel = "Numerador Expediente")
@EntityGroup("Configuracion.Camaras")
@EntityIcon
@RestrictedEntity
@InteractiveModuleProperties( {
		@GenerationProperty(propertyName = "gridColumns", propertyValue = "1") })
@Table(name = "NUMERADOR_EXPEDIENTE")
@EntityListeners({IdentificableEntityListener.class})
public class NumeradorExpediente  implements java.io.Serializable, IIdentificable {

     private Integer id;
     private Integer anio;
     private Integer ultimoNumero;
     private Integer version;
     private String uuid;
     private Camara camara;
     private int tipoNumerador;

    public NumeradorExpediente() {
    }

    public NumeradorExpediente(Integer anio, Integer ultimoNumero, Camara camara) {
       this.anio = anio;
       this.ultimoNumero = ultimoNumero;
       this.camara = camara;
    }

    @SequenceGenerator(name="generator", sequenceName="S_NUMERADOR_EXPEDIENTE", allocationSize=1)@Id @GeneratedValue(strategy=SEQUENCE, generator="generator")    
	@DataDefinition(label = "Id Numerador Expediente")
    @Column(name="ID_NUMERADOR_EXPEDIENTE", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
	@DataDefinition(label = "Anio")
    @Column(name="ANIO", nullable=false)
    public Integer getAnio() {
        return this.anio;
    }
    
    public void setAnio(Integer anio) {
        this.anio = anio;
    }
    
	@DataDefinition(label = "Ultimo Numero")
    @Column(name="ULTIMO_NUMERO", nullable=false)
    public Integer getUltimoNumero() {
        return this.ultimoNumero;
    }
    
    public void setUltimoNumero(Integer ultimoNumero) {
        this.ultimoNumero = ultimoNumero;
    }
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ID_CAMARA", nullable=false)
    public Camara getCamara() {
        return this.camara;
    }
    
    public void setCamara(Camara camara) {
        this.camara = camara;
    }

    @DataDefinition(label = "UUID",displayLength = 10,disabled = true)
   	@IndexField
    @Column(name="UUID", unique=true, length=40)
    public String getUuid() {
        return this.uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

	@DataDefinition(label = "Tipo Numeradorl", defaultValue="0")
    @Column(name="TIPO_NUMERADOR", nullable=false)
	@SelectItems(enumeration="tipoNumeradorExpedienteEnumeration", control=ControlType.Radio,
			value={
			@SelectItem(value = "0",label = "Expediente Judicial"),
			@SelectItem(value = "1",label = "Expediente Mediacion")
		})
	public int getTipoNumerador() {
		return tipoNumerador;
	}

	public void setTipoNumerador(int tipoNumerador) {
		this.tipoNumerador = tipoNumerador;
	}

//    @Version
//	public Integer getVersion() {
//		return version;
//	}
//
//	public void setVersion(Integer version) {
//		this.version = version;
//	}

}


