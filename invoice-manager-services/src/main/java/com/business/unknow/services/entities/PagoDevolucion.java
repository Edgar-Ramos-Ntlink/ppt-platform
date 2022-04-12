package com.business.unknow.services.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.NotNull;
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
@Table(name = "PAGO_DEVOLUCION")
public class PagoDevolucion implements Serializable {

  private static final long serialVersionUID = -1572795797336952518L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_PAGO_DEVOLUCION")
  private Integer id;

  @NotNull
  @Column(name = "MONEDA")
  private String moneda;

  @NotNull
  @Column(name = "TIPO_CAMBIO")
  private BigDecimal tipoCambio;

  @NotNull
  @Column(name = "MONTO")
  private BigDecimal monto;

  @NotNull
  @Column(name = "BENEFICIARIO")
  private String beneficiario;

  @NotNull
  @Column(name = "FORMA_PAGO")
  private String formaPago;

  @Column(name = "BANCO")
  private String banco;

  @Column(name = "TIPO_REFERENCIA")
  private String tipoReferencia;

  @Column(name = "REFERENCIA")
  private String referencia;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "FECHA_PAGO")
  private Date fechaPago;

  @NotNull
  @Column(name = "STATUS")
  private String status;

  @NotNull
  @Column(name = "TIPO_RECEPTOR")
  private String tipoReceptor;

  @NotNull
  @Column(name = "RECEPTOR")
  private String receptor;

  @NotNull
  @Column(name = "SOLICITANTE")
  private String solicitante;

  @Column(name = "CUENTA_PAGO")
  private String cuentaPago;

  @Column(name = "RFC_EMPRESA")
  private String rfcEmpresa;

  @Column(name = "AUTORIZADOR")
  private String autorizador;

  @Column(name = "COMENTARIOS")
  private String comentarios;

  @Column(name = "ID_DEVOLUCION")
  private Integer idDevolucion;

  @Column(name = "FOLIO_FACT")
  private String folioFactura;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date fechaActualizacion;
}
