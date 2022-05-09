package com.business.unknow.services.entities;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "REPORTES")
public class Reporte {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_REPORTE")
  private Integer id;

  @Column(name = "FOLIO")
  private String folio;

  @Column(name = "TIPO_COMPROBANTE")
  private String tipoDeComprobante;

  @Column(name = "IMP_TRASLADADOS")
  private BigDecimal impuestosTrasladados;

  @Column(name = "IMP_RETENIDOS")
  private BigDecimal impuestosRetenidos;

  @Column(name = "SUB_TOTAL")
  private BigDecimal subtotal;

  @Column(name = "TOTAL")
  private BigDecimal total;

  @Column(name = "METODO_PAGO")
  private String metodoPago;

  @Column(name = "FORMA_PAGO")
  private String formaPago;

  @Column(name = "MONEDA")
  private String moneda;

  @Column(name = "CANTIDAD")
  private BigDecimal cantidad;

  @Column(name = "CLAVE_UNIDAD")
  private String claveUnidad;

  @Column(name = "UNIDAD")
  private String unidad;

  @Column(name = "CLAVE_PROD_SERV")
  private String claveProdServ;

  @Column(name = "DESCRIPCION")
  private String descripcion;

  @Column(name = "VALOR_UNITARIO")
  private BigDecimal valorUnitario;

  @Column(name = "IMPORTE")
  private BigDecimal importe;
}
