package com.business.unknow.services.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "RESOURCE_FILES")
public class ResourceFile implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "FILE_ID")
  private Integer id;

  @NotEmpty
  @Column(name = "REFERENCIA")
  private String referencia;

  @NotEmpty
  @Column(name = "TIPO_ARCHIVO")
  private String tipoArchivo;

  @NotEmpty
  @Column(name = "TIPO_RECURSO")
  private String tipoRecurso;

  @NotEmpty
  @Column(name = "FORMATO")
  private String formato;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getReferencia() {
    return referencia;
  }

  public void setReferencia(String referencia) {
    this.referencia = referencia;
  }

  public String getTipoArchivo() {
    return tipoArchivo;
  }

  public void setTipoArchivo(String tipoArchivo) {
    this.tipoArchivo = tipoArchivo;
  }

  public String getTipoRecurso() {
    return tipoRecurso;
  }

  public void setTipoRecurso(String tipoRecurso) {
    this.tipoRecurso = tipoRecurso;
  }

  public String getFormato() {
    return formato;
  }

  public void setFormato(String formato) {
    this.formato = formato;
  }

  public Date getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(Date fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }
}
