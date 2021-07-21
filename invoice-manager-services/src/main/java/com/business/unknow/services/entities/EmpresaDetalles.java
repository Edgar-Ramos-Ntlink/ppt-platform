package com.business.unknow.services.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EMPRESA_DETALLES")
public class EmpresaDetalles implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private int id;

  @Basic(optional = false)
  @Column(name = "RFC")
  private String rfc;

  @Basic(optional = false)
  @Column(name = "USER")
  private String user;

  @Basic(optional = false)
  @Column(name = "AREA")
  private String area;

  @Basic(optional = false)
  @Column(name = "TIPO")
  private String tipo;

  @Basic(optional = false)
  @Column(name = "RESUMEN")
  private String resumen;

  @Column(name = "DETALLE")
  private String detalle;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date fechaActualizacion;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getRfc() {
    return rfc;
  }

  public void setRfc(String rfc) {
    this.rfc = rfc;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public String getResumen() {
    return resumen;
  }

  public void setResumen(String resumen) {
    this.resumen = resumen;
  }

  public String getDetalle() {
    return detalle;
  }

  public void setDetalle(String detalle) {
    this.detalle = detalle;
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

  @Override
  public String toString() {
    return "EmpresaDetalles{"
        + "id="
        + id
        + ", user='"
        + user
        + '\''
        + ", area='"
        + area
        + '\''
        + ", tipo='"
        + tipo
        + '\''
        + ", resumen='"
        + resumen
        + '\''
        + ", detalle='"
        + detalle
        + '\''
        + ", fechaCreacion="
        + fechaCreacion
        + ", fechaActualizacion="
        + fechaActualizacion
        + '}';
  }
}
