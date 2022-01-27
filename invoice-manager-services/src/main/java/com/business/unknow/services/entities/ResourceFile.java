package com.business.unknow.services.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@ToString
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
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
  @Column(name = "NOMBRE")
  private String nombre;

  @NotEmpty
  @Column(name = "TIPO_ARCHIVO")
  private String tipoArchivo;

  @NotEmpty
  @Column(name = "TIPO_RECURSO")
  private String tipoRecurso;

  @NotEmpty
  @Column(name = "EXTENSION")
  private String extension;

  @NotEmpty
  @Column(name = "FORMATO")
  private String formato;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;
}
