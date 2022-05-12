package com.business.unknow.services.entities;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
public class EmpresaIngresos {

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

  @ManyToOne
  @JoinColumn(name = "RFC", referencedColumnName = "RFC", insertable = false, updatable = false)
  private Empresa empresa;
}
