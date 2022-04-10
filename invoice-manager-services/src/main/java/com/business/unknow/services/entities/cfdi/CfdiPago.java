package com.business.unknow.services.entities.cfdi;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "CFDI_PAGOS")
public class CfdiPago implements Serializable {

  private static final long serialVersionUID = -9003721389303480808L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_CFDI_PAGO")
  private int id;

  @Column(name = "VERSION")
  private String version;

  @Column(name = "FECHA_PAGO")
  private Date fechaPago;

  @Column(name = "FORMA_PAGO")
  private String formaPago;

  @Column(name = "MONEDA")
  private String moneda;

  @Column(name = "MONTO")
  private BigDecimal monto;

  @Column(name = "FOLIO")
  private String folio;

  @Column(name = "ID_DOCUMENTO")
  private String idDocumento;

  @Column(name = "IMPORTE_PAGADO")
  private BigDecimal importePagado;

  @Column(name = "IMPORTE_SALDO_ANTERIOR")
  private BigDecimal importeSaldoAnterior;

  @Column(name = "IMPORTE_SALDO_INSOLUTO")
  private BigDecimal importeSaldoInsoluto;

  @Column(name = "METODO_PAGO")
  private String metodoPago;

  @Column(name = "MONEDA_DR")
  private String monedaDr;

  @Column(name = "NUM_PARCIALIDAD", columnDefinition = "TINYINT")
  private int numeroParcialidad;

  @Column(name = "SERIE")
  private String serie;

  @Column(name = "TIPO_CAMBIO_DR")
  private BigDecimal tipoCambioDr;

  @Column(name = "TIPO_CAMBIO")
  private BigDecimal tipoCambio;

  @Column(name = "VALIDO", columnDefinition = "TINYINT")
  private Boolean valido;

  @ManyToOne
  @JoinColumn(name = "ID_CFDI", nullable = false)
  private Cfdi cfdi;

  public CfdiPago(BigDecimal importeSaldoInsoluto, int numeroParcialidad) {
    super();
    this.importeSaldoInsoluto = importeSaldoInsoluto;
    this.numeroParcialidad = numeroParcialidad;
  }
}
