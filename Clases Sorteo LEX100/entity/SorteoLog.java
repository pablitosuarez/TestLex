package com.base100.lex100.entity;
// Generated by Expand 4.0.0 

import com.base100.expand.annotation.*;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * SorteoLog generated by Expand 4.0.0
 */
@Entity
@EntityDefinition(label = "Sorteo Log", listLabel = "Sorteo Log", noSearchProperties=BooleanType.False)
@EntityGroup("MesaEntrada.Auditoria")
@EntityIcon
@RestrictedEntity
@InteractiveModuleProperties( {
		@GenerationProperty(propertyName = "gridColumns", propertyValue = "1") })
@Table(name = "SORTEO_LOG")
public class SorteoLog  implements java.io.Serializable {

     private Integer id;
     private String codigoSorteo;
     private String rubro;
     private String rubroAsignacion;
     private Integer rangoMinimos;
     private Integer cargaTrabajo;
     private String informacionSorteo;
     private Date fechaSorteo;
     private String usuario;
     private Expediente expediente;
     private Oficina oficina;
     private String codigoTipoCambioAsignacion;

    public SorteoLog() {
    }

    public SorteoLog(String codigoSorteo, String rubro, Integer rangoMinimos, String informacionSorteo, Date fechaSorteo, String usuario, Expediente expediente, Oficina oficina) {
       this.codigoSorteo = codigoSorteo;
       this.rubro = rubro;
       this.rangoMinimos = rangoMinimos;
       this.informacionSorteo = informacionSorteo;
       this.fechaSorteo = fechaSorteo;
       this.usuario = usuario;
       this.expediente = expediente;
       this.oficina = oficina;
    }

    @SequenceGenerator(name="generator", sequenceName="S_SORTEO_LOG", allocationSize=1)@Id @GeneratedValue(strategy=SEQUENCE, generator="generator")    
	@DataDefinition(label = "Id Sorteo Log")
    @Column(name="ID_SORTEO_LOG", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
	@DataDefinition(label = "Codigo Sorteo")
    @Column(name="CODIGO_SORTEO", length=40)
    public String getCodigoSorteo() {
        return this.codigoSorteo;
    }
    
    public void setCodigoSorteo(String codigoSorteo) {
        this.codigoSorteo = codigoSorteo;
    }
    
	@DataDefinition(label = "Rubro")
    @Column(name="RUBRO", length=10)
    public String getRubro() {
        return this.rubro;
    }
    
    public void setRubro(String rubro) {
        this.rubro = rubro;
    }

    @DataDefinition(label = "Rubro Asignacion")
    @Column(name="RUBRO_ASIGNACION", length=10)
    public String getRubroAsignacion() {
        return this.rubroAsignacion;
    }
    
    public void setRubroAsignacion(String rubroAsignacion) {
        this.rubroAsignacion = rubroAsignacion;
    }
    
	@DataDefinition(label = "Rango Minimos")
    @Column(name="RANGO_MINIMOS")
    public Integer getRangoMinimos() {
        return this.rangoMinimos;
    }
    
    public void setRangoMinimos(Integer rangoMinimos) {
        this.rangoMinimos = rangoMinimos;
    }
    
	@DataDefinition(label = "Informacion Sorteo", noList=BooleanType.True)
	@Lob
    @Column(name="INFORMACION_SORTEO")
    public String getInformacionSorteo() {
        return this.informacionSorteo;
    }
    
    public void setInformacionSorteo(String informacionSorteo) {
        this.informacionSorteo = informacionSorteo;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
	@DataDefinition(label = "Fecha Sorteo")
	@Search(condition=ConditionType.Range)
    @Column(name="FECHA_SORTEO")
    public Date getFechaSorteo() {
        return this.fechaSorteo;
    }
    
    public void setFechaSorteo(Date fechaSorteo) {
        this.fechaSorteo = fechaSorteo;
    }
    
	@DataDefinition(label = "Usuario")
    @Column(name="USUARIO", length=400)
    public String getUsuario() {
        return this.usuario;
    }
    
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ID_EXPEDIENTE")
    public Expediente getExpediente() {
        return this.expediente;
    }
    
    public void setExpediente(Expediente expediente) {
        this.expediente = expediente;
    }
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ID_OFICINA")
	@DataDefinition(label = "Oficina Resultado", filteredByGlobalSelection = false)
	@SelectItems(enumeration = "oficinaEnumeration",control=ControlType.Autocomplete)
    public Oficina getOficina() {
        return this.oficina;
    }
    
    public void setOficina(Oficina oficina) {
        this.oficina = oficina;
    }

	@DataDefinition(label = "Carga de Trabajo")
    @Column(name="CARGA_TRABAJO")
    public Integer getCargaTrabajo() {
        return this.cargaTrabajo;
    }
    
    public void setCargaTrabajo(Integer cargaTrabajo) {
        this.cargaTrabajo = cargaTrabajo;
    }

	@DataDefinition(label = "Tipo Cambio Asignación")
    @Column(name="TIPO_ASIGNACION", length=10)
	public String getCodigoTipoCambioAsignacion() {
		return codigoTipoCambioAsignacion;
	}

	public void setCodigoTipoCambioAsignacion(String codigoTipoCambioAsignacion) {
		this.codigoTipoCambioAsignacion = codigoTipoCambioAsignacion;
	}


}


