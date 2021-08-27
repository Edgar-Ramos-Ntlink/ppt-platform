package com.business.unknow.services.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import lombok.*;
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
}
