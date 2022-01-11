package com.business.unknow.services.entities;

import java.io.Serializable;
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
@Table(name = "CUENTAS_BANCARIAS")
public class CuentaBancaria implements Serializable {

  private static final long serialVersionUID = 5514259228800971253L;

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
