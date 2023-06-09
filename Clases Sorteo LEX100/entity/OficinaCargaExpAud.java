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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.base100.expand.annotation.BooleanType;
import com.base100.expand.annotation.ControlType;
import com.base100.expand.annotation.DataDefinition;
import com.base100.expand.annotation.EntityDefinition;
import com.base100.expand.annotation.EntityGroup;
import com.base100.expand.annotation.Hidden;
import com.base100.expand.annotation.PropertyList;
import com.base100.expand.annotation.PropertyLists;
import com.base100.expand.annotation.RestrictedEntity;
import com.base100.expand.annotation.SelectItem;
import com.base100.expand.annotation.SelectItems;
import com.base100.expand.annotation.index.IndexField;
import com.base100.lex100.manager.oficina.OficinaManager;

/**
 * OficinaCargaExp generated by Expand 4.0.0
 */
@Entity
@RestrictedEntity
@EntityDefinition(label = "Auditoria Bolilleros", listLabel = "Auditoria Gesti�n Bolilleros", inlineEdition=true, noListProperties=BooleanType.False, noSearchProperties=BooleanType.False,noEditProperties=BooleanType.False)
@EntityGroup("MesaEntrada.Auditoria")
@PropertyLists( {
	@PropertyList(name = "list", properties = {
			"usuario",
			"descripcionModificacion",
			"fechaModificacion",
			"tipoBolillero",
			"oficina",
			"rubro",
			"materia",
			"tipoOficina",
			"factorSuma",
			"inhibido"
	})
})
@Table(name = "OFICINA_CARGA_EXP_AUD")
public class OficinaCargaExpAud implements java.io.Serializable {

     private Integer id;
     private OficinaCargaExp oficinaCargaExp;
     private String rubro;
     private Integer cargaTrabajo;
     private Integer numeroExpedientes;
     private Integer factorSuma;
     private boolean inhibido;
     private String tipoOficina;
     private String uuid;
     private Materia materia;
     private Oficina oficina;
     private Boolean turno;
     private Sorteo sorteo;
     private TipoBolillero tipoBolillero;
     private int status;
     private String usuario;
     private Date fechaModificacion;
     private String descripcionModificacion;
     private Expediente expediente;

    public OficinaCargaExpAud() {
    }
	
    public OficinaCargaExpAud(OficinaCargaExp entity) {
    	setCargaTrabajo(entity.getCargaTrabajo());
    	setFactorSuma(entity.getFactorSuma());
    	setOficinaCargaExp(entity);
    	setInhibido(entity.isInhibido());
    	setMateria(entity.getMateria());
    	setNumeroExpedientes(entity.getNumeroExpedientes());
    	setOficina(entity.getOficina());
    	setRubro(entity.getRubro());
    	setUuid(entity.getUuid());
    	setStatus(entity.getStatus());
    	setTipoOficina(entity.getTipoOficina());
    	setTipoBolillero(entity.getTipoBolillero());
    }

	@SequenceGenerator(name="generator", sequenceName="S_OFICINA_CARGA_EXP_AUD", allocationSize=1)@Id @GeneratedValue(strategy=SEQUENCE, generator="generator")    
	@DataDefinition(label = "Id OficinaCargaExp")
    @Column(name="ID_OFICINA_CARGA_EXP_AUD", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
	@DataDefinition(label = "Rubro")
    @Column(name="RUBRO", length=10)
    public String getRubro() {
        return this.rubro;
    }
    
    public void setRubro(String rubro) {
        this.rubro = rubro;
    }
    
	@DataDefinition(label = "Carga de Trabajo")
    @Column(name="CARGA_TRABAJO")
    public Integer getCargaTrabajo() {
        return this.cargaTrabajo;
    }
    
    public void setCargaTrabajo(Integer cargaTrabajo) {
        this.cargaTrabajo = cargaTrabajo;
    }
    
	@DataDefinition(label = "N�mero Expedientes")
    @Column(name="NUMERO_EXPEDIENTES")
    public Integer getNumeroExpedientes() {
        return this.numeroExpedientes;
    }
    
    public void setNumeroExpedientes(Integer numeroExpedientes) {
        this.numeroExpedientes = numeroExpedientes;
    }
    
	@DataDefinition(label = "Factor Suma")
    @Column(name="FACTOR_SUMA")
    public Integer getFactorSuma() {
        return this.factorSuma;
    }
    
    public void setFactorSuma(Integer factorSuma) {
        this.factorSuma = factorSuma;
    }
    
	@DataDefinition(label = "Inhibido", defaultValue = "false")
    @Column(name="INHIBIDO", nullable=false)
    public boolean isInhibido() {
        return this.inhibido;
    }
    
    public void setInhibido(boolean inhibido) {
        this.inhibido = inhibido;
    }
    
    @DataDefinition(label = "Tipo Oficina", defaultValue = "J")
	@SelectItems(enumeration = "tipoOficinaCargaEnumeration",control=ControlType.Droplist,
		value={
			@SelectItem(value = "J",label = "Juzgado"),
			@SelectItem(value = "S",label = "Sala")
		})
    @Column(name="TIPO_OFICINA", length=1)
    public String getTipoOficina() {
		return tipoOficina;
	}

	public void setTipoOficina(String tipoOficina) {
		this.tipoOficina = tipoOficina;
	}

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ID_TIPO_BOLILLERO", nullable=false)
	@SelectItems(enumeration = "tipoBolilleroEnumeration",control=ControlType.Droplist)
    public TipoBolillero getTipoBolillero() {
        return this.tipoBolillero;
    }
    
    public void setTipoBolillero(TipoBolillero tipoBolillero) {
        this.tipoBolillero = tipoBolillero;
    }

    @DataDefinition(label = "UUID",displayLength = 10,disabled = true)
	@IndexField
	@Hidden
    @Column(name="UUID", unique=true, length=40)
    public String getUuid() {
        return this.uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ID_MATERIA")
	@SelectItems(enumeration = "materiaEnumeration",control=ControlType.Droplist)
    public Materia getMateria() {
        return this.materia;
    }
    
    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ID_OFICINA")
	@DataDefinition(label = "Oficina", filteredByGlobalSelection=false)
    public Oficina getOficina() {
        return this.oficina;
    }
    
    public void setOficina(Oficina oficina) {
        this.oficina = oficina;
    }

    @Transient
	public Boolean getTurno() {
    	try {
    		if (turno == null) {
				turno = OficinaManager.instance().isTurnoSorteo(oficina, getSorteo());
			}
			return turno;
    	} catch (Exception e) {
    		return false;
    	}
	}

	public void setTurno(Boolean turno) {
		this.turno = turno;
	}
	

	@Transient
	public Sorteo getSorteo() {
    	try {
    		if (sorteo == null) {
    			return oficinaCargaExp.getSorteo();
	}
			return sorteo;
    	} catch (Exception e) {
    		return null;
    	}
	}

	public void setSorteo(Sorteo sorteo) {
		this.sorteo = sorteo;
	}

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ID_OFICINA_CARGA_EXP")
	public OficinaCargaExp getOficinaCargaExp() {
		return oficinaCargaExp;
	}

	public void setOficinaCargaExp(OficinaCargaExp oficinaCargaExp) {
		this.oficinaCargaExp = oficinaCargaExp;
	}

	@Column(name="STATUS")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name="USUARIO", length=100)
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	@Column(name="FECHA_MODIFICACION")
	@Temporal(TemporalType.TIMESTAMP)
	@DataDefinition(label="Fecha Modificaci�n", shortLabel="Fecha Mod.")
	public Date getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	@Column(name="DESCRIP_MODIFICACION", length=100)
	@DataDefinition(label="Operaci�n")
	public String getDescripcionModificacion() {
		return descripcionModificacion;
	}

	public void setDescripcionModificacion(String descripcionModificacion) {
		this.descripcionModificacion = descripcionModificacion;
	}

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ID_EXPEDIENTE")
	@DataDefinition(label="Expediente")
	public Expediente getExpediente() {
		return expediente;
	}

	public void setExpediente(Expediente expediente) {
		this.expediente = expediente;
	}
    
}


