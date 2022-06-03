package com.business.unknow.services.entities;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
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

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "CLIENTES")
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_CLIENTE")
  private int id;

  @Column(name = "ACTIVO")
  private boolean activo;

  @Column(name = "MORAL")
  private boolean moral;

  @Basic(optional = false)
  @Column(name = "RFC")
  private String rfc;

  @Column(name = "RAZON_SOCIAL")
  private String razonSocial;

  @Column(name = "REGIMEN_FISCAL")
  private String regimenFiscal;

  @Column(name = "CALLE")
  private String calle;

  @Column(name = "NO_EXTERIOR")
  private String noExterior;

  @Column(name = "NO_INTERIOR")
  private String noInterior;

  @Column(name = "COLONIA")
  private String colonia;

  @Column(name = "MUNICIPIO")
  private String municipio;

  @Column(name = "ESTADO")
  private String estado;

  @Column(name = "PAIS")
  private String pais;

  @Column(name = "CODIGO_POSTAL")
  private String cp;

  @Column(name = "TELEFONO")
  private String telefono;

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
}
