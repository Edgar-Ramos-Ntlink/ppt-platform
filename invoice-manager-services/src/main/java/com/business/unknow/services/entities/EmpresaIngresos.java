package com.business.unknow.services.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@ToString
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EMPRESAS_INGRESOS")
public class EmpresaIngresos implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Integer id;

  @Column(name = "RFC")
  private String rfc;

  @Column(name = "TIPO_DATO")
  private String tipoDato;

  @Column(name = "ANIO")
  private Integer anio;

  @Column(name = "DETALLE")
  private BigDecimal detalle;

  @Column(name = "LINK")
  private String link;

  @Column(name = "CREADOR")
  private String creador;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date creacion;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date actualizacion;
}
