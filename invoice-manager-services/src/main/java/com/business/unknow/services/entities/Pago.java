package com.business.unknow.services.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PAGOS")
public class Pago {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_PAGO")
  private Integer id;

  @NotEmpty
  @Column(name = "MONEDA")
  private String moneda;

  @NotEmpty
  @Column(name = "BANCO")
  private String banco;

  @NotEmpty
  @Column(name = "CUENTA")
  private String cuenta;

  @NotNull
  @Column(name = "TIPO_CAMBIO")
  private BigDecimal tipoDeCambio;

  /* DEPOSITO,TRANSFERENCIA, CHEQUE, EFECTIVO */
  @NotEmpty
  @Column(name = "FORMA_PAGO")
  private String formaPago;

  @Column(name = "MONTO")
  private BigDecimal monto;

  @Column(name = "STATUS_PAGO")
  private String statusPago;

  @Column(name = "COMENTARIO_PAGO")
  private String comentarioPago;

  @Column(name = "ACREDOR")
  private String acredor;

  @Column(name = "DEUDOR")
  private String deudor;

  @Column(name = "SOLICITANTE")
  private String solicitante;

  @Column(name = "REVISION_1", columnDefinition = "TINYINT")
  private Boolean revision1;

  @Column(name = "REVISION_2", columnDefinition = "TINYINT")
  private Boolean revision2;

  @Column(name = "REVISOR_1")
  private String revisor1;

  @Column(name = "REVISOR_2")
  private String revisor2;

  @Column(name = "FECHA_PAGO")
  private Date fechaPago;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date fechaActualizacion;

  @OneToMany(mappedBy = "pago", cascade = CascadeType.PERSIST)
  @Builder.Default
  private List<PagoFactura> facturas = new ArrayList<>();

  public Pago() {
    this.facturas = new ArrayList<>();
    this.tipoDeCambio = BigDecimal.ONE;
  }

  public void addFactura(PagoFactura factura) {
    this.facturas.add(factura);
    factura.setPago(this);
  }
}
