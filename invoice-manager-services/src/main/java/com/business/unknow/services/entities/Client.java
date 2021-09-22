package com.business.unknow.services.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "CLIENTES")
public class Client implements Serializable {

  private static final long serialVersionUID = -491025321146807933L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_CLIENTE")
  private int id;

  @NotNull
  @Column(name = "ACTIVO")
  private Boolean activo;

  @Column(name = "NOTAS")
  private String notas;

  @NotEmpty
  @Column(name = "CORREO_PROMOTOR")
  private String correoPromotor;

  @Column(name = "CORREO_CONTACTO")
  private String correoContacto;

  @DecimalMin("0.00")
  @DecimalMax("16.00")
  @Column(name = "PORCENTAJE_PROMOTOR")
  private BigDecimal porcentajePromotor;

  @DecimalMin("0.00")
  @DecimalMax("16.00")
  @Column(name = "PORCENTAJE_CLIENTE")
  private BigDecimal porcentajeCliente;

  @DecimalMin("0.00")
  @DecimalMax("16.00")
  @Column(name = "PORCENTAJE_DESPACHO")
  private BigDecimal porcentajeDespacho;

  @DecimalMin("0.00")
  @DecimalMax("16.00")
  @Column(name = "PORCENTAJE_CONTACTO")
  private BigDecimal porcentajeContacto;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date fechaActualizacion;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "RFC", referencedColumnName = "RFC")
  private Contribuyente informacionFiscal;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  public String getCorreoPromotor() {
    return correoPromotor;
  }

  public void setCorreoPromotor(String correoPromotor) {
    this.correoPromotor = correoPromotor;
  }

  public String getCorreoContacto() {
    return correoContacto;
  }

  public void setCorreoContacto(String correoContacto) {
    this.correoContacto = correoContacto;
  }

  public BigDecimal getPorcentajePromotor() {
    return porcentajePromotor;
  }

  public void setPorcentajePromotor(BigDecimal porcentajePromotor) {
    this.porcentajePromotor = porcentajePromotor;
  }

  public BigDecimal getPorcentajeCliente() {
    return porcentajeCliente;
  }

  public void setPorcentajeCliente(BigDecimal porcentajeCliente) {
    this.porcentajeCliente = porcentajeCliente;
  }

  public BigDecimal getPorcentajeDespacho() {
    return porcentajeDespacho;
  }

  public void setPorcentajeDespacho(BigDecimal porcentajeDespacho) {
    this.porcentajeDespacho = porcentajeDespacho;
  }

  public BigDecimal getPorcentajeContacto() {
    return porcentajeContacto;
  }

  public void setPorcentajeContacto(BigDecimal porcentajeContacto) {
    this.porcentajeContacto = porcentajeContacto;
  }

  public Date getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(Date fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public Date getFechaActualizacion() {
    return fechaActualizacion;
  }

  public void setFechaActualizacion(Date fechaActualizacion) {
    this.fechaActualizacion = fechaActualizacion;
  }

  public Contribuyente getInformacionFiscal() {
    return informacionFiscal;
  }

  public void setInformacionFiscal(Contribuyente informacionFiscal) {
    this.informacionFiscal = informacionFiscal;
  }

  public String getNotas() {
    return notas;
  }

  public void setNotas(String notas) {
    this.notas = notas;
  }

  @Override
  public String toString() {
    return "Client [id="
        + id
        + ", activo="
        + activo
        + ", correoPromotor="
        + correoPromotor
        + ", correoContacto="
        + correoContacto
        + ", porcentajePromotor="
        + porcentajePromotor
        + ", porcentajeCliente="
        + porcentajeCliente
        + ", porcentajeDespacho="
        + porcentajeDespacho
        + ", porcentajeContacto="
        + porcentajeContacto
        + ", fechaCreacion="
        + fechaCreacion
        + ", fechaActualizacion="
        + fechaActualizacion
        + ", informacionFiscal="
        + informacionFiscal
        + "]";
  }
}
