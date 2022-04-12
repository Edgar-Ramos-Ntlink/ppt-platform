package com.business.unknow.services.entities.cfdi;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@ToString
@Entity
@Table(name = "CFDI")
public class Cfdi implements Serializable {

  private static final long serialVersionUID = 6362879952092338829L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_CFDI")
  private Integer id;

  @Column(name = "VERSION")
  private String version;

  @Column(name = "SERIE")
  private String serie;

  @Column(name = "FOLIO")
  private String folio;

  @Column(name = "SELLO")
  private String sello;

  @Column(name = "NO_CERTIFICADO")
  private String noCertificado;

  @Column(name = "CERTIFICADO")
  private String certificado;

  @Column(name = "MONEDA")
  private String moneda;

  @Column(name = "TIPO_CAMBIO")
  private BigDecimal tipoCambio;

  @Column(name = "IMP_TRASLADADOS")
  private BigDecimal impuestosTrasladados;

  @Column(name = "IMP_RETENIDOS")
  private BigDecimal impuestosRetenidos;

  @Column(name = "SUB_TOTAL")
  private BigDecimal subtotal;

  @Column(name = "DESCUENTO")
  private BigDecimal descuento;

  @Column(name = "TOTAL")
  private BigDecimal total;

  @Column(name = "TIPO_COMPROBANTE")
  private String tipoDeComprobante;

  @Column(name = "METODO_PAGO")
  private String metodoPago;

  @Column(name = "FORMA_PAGO")
  private String formaPago;

  @Column(name = "CONDICIONES_PAGO")
  private String condicionesDePago;

  @Column(name = "LUGAR_EXPEDICION")
  private String lugarExpedicion;

  @OneToMany(mappedBy = "cfdi")
  private List<Concepto> conceptos;

  @OneToOne(mappedBy = "cfdi")
  private Receptor receptor;

  @OneToOne(mappedBy = "cfdi")
  private Emisor emisor;

  @OneToOne(mappedBy = "cfdi")
  private Relacionado relacionado;

  @OneToMany(mappedBy = "cfdi")
  private List<CfdiPago> pagos;

  public Cfdi() {
    this.conceptos = new ArrayList<Concepto>();
  }
}
