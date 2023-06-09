package com.base100.lex100.entity;
// Generated by Expand 4.0.0 

import static javax.persistence.GenerationType.SEQUENCE;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.base100.expand.annotation.BooleanType;
import com.base100.expand.annotation.ConditionType;
import com.base100.expand.annotation.ControlType;
import com.base100.expand.annotation.DataDefinition;
import com.base100.expand.annotation.EntityDefinition;
import com.base100.expand.annotation.EntityGroup;
import com.base100.expand.annotation.EntityIcon;
import com.base100.expand.annotation.GenerationProperty;
import com.base100.expand.annotation.InteractiveModuleProperties;
import com.base100.expand.annotation.RestrictedEntity;
import com.base100.expand.annotation.Search;
import com.base100.expand.annotation.SelectItems;

/**
 * SorteoLog generated by Expand 4.0.0
 */
@Entity
@EntityDefinition(label = "Conexidad Log", listLabel = "Conexidad Log", noSearchProperties=BooleanType.False)
@EntityGroup("MesaEntrada.Auditoria")
@EntityIcon
@RestrictedEntity
@InteractiveModuleProperties( {
		@GenerationProperty(propertyName = "gridColumns", propertyValue = "1") })
@Table(name = "CONEXIDAD_LOG")
public class ConexidadLog  implements java.io.Serializable {

     private Integer id;
     private String informacionConexidad;
     private String resultado;
     private Date fechaComienzo;
     private Date fechaFin;
     private Long intervalo;
     private Camara camara;
     private Expediente expediente;
     private String tipoRadicacion;

    public ConexidadLog() {
    }

    public ConexidadLog(String informacionConexidad, Date fechaComienzo, Date fechaFin, Camara camara, Expediente expediente) {
       this.informacionConexidad = informacionConexidad;
       this.fechaComienzo = fechaComienzo;
       this.fechaFin = fechaFin;
       this.camara = camara;
       this.expediente = expediente;
    }

    @SequenceGenerator(name="generator", sequenceName="S_CONEXIDAD_LOG", allocationSize=1)@Id @GeneratedValue(strategy=SEQUENCE, generator="generator")    
	@DataDefinition(label = "Id Conexidad Log")
    @Column(name="ID_CONEXIDAD_LOG", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
	@DataDefinition(label = "Informacion Conexidad", noList=BooleanType.True)
	@Lob
    @Column(name="INFORMACION_CONEXIDAD")
    public String getInformacionConexidad() {
        return this.informacionConexidad;
    }
    
    public void setInformacionConexidad(String informacionConexidad) {
        this.informacionConexidad = informacionConexidad;
    }
    
	@DataDefinition(label = "Resultado", noList=BooleanType.True)
	@Lob
    @Column(name="RESULTADO")
    public String getResultado() {
        return this.resultado;
    }
    
    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
	@DataDefinition(label = "Fecha Comienzo")
	@Search(condition=ConditionType.Range)
    @Column(name="FECHA_COMIENZO")
    public Date getFechaComienzo() {
        return this.fechaComienzo;
    }
    
   public void setFechaComienzo(Date fechaComienzo) {
        this.fechaComienzo = fechaComienzo;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
	@DataDefinition(label = "Fecha Fin")
	@Search(condition=ConditionType.Range)
    @Column(name="FECHA_FIN")
    public Date getFechaFin() {
        return this.fechaFin;
    }
    
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

	@DataDefinition(label = "Intervalo")
    @Column(name="INTERVALO")
	public Long getIntervalo() {
		return intervalo;
	}

	public void setIntervalo(Long intervalo) {
		this.intervalo = intervalo;
	}

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ID_CAMARA")
	@DataDefinition(label = "Camara", filteredByGlobalSelection = false)
	@SelectItems(enumeration = "camaraEnumeration",control=ControlType.Droplist)
    public Camara getCamara() {
        return this.camara;
    }
    
    public void setCamara(Camara camara) {
        this.camara = camara;
    }

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ID_EXPEDIENTE")
    public Expediente getExpediente() {
        return this.expediente;
    }
    
    public void setExpediente(Expediente expediente) {
        this.expediente = expediente;
    }
    
	@DataDefinition(label = "Tipo Radicación")
    @Column(name="TIPO_RADICACION", length=10)
	@SelectItems(enumeration = "tipoRadicacionEnumeration",control=ControlType.Droplist)
	public String getTipoRadicacion() {
		return tipoRadicacion;
	}

	public void setTipoRadicacion(String tipoRadicacion) {
		this.tipoRadicacion = tipoRadicacion;
	}

	@Transient
	public Long getIntervaloSeg() {
		if (intervalo != null) {
			return intervalo/1000L;
		}
		return null;
	}

}


