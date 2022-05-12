package com.business.unknow.services.entities;

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
@Table(name = "CUENTAS_BANCARIAS")
public class CuentaBancaria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_CUENTA_BANCARIA")
  private int id;

  @Column(name = "EMPRESA")
  private String rfc;

  @Column(name = "BANCO")
  private String banco;

  @Column(name = "NO_CUENTA")
  private String cuenta;

  @Column(name = "CLABE")
  private String clabe;

  @Column(name = "DOMICILIO_BANCO")
  private String domicilioBanco;

  @Column(name = "EXP_ACTUALIZADO")
  private String expedienteActualizado;

  @Column(name = "TIPO_CONTRATO")
  private String tipoContrato;

  @Column(name = "SUCURSAL")
  private String sucursal;

  @Column(name = "LINEA")
  private String linea;

  @Column(name = "RAZON_SOCIAL")
  private String razonSocial;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date fechaActualizacion;

  @ManyToOne
  @JoinColumn(name = "EMPRESA", referencedColumnName = "RFC", insertable = false, updatable = false)
  private Empresa empresa;
}
