package com.business.unknow.services.entities.cfdi;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@ToString
@Entity
@Table(name = "CFDI_CONCEPTOS")
public class Concepto implements Serializable {

  private static final long serialVersionUID = -1917092984790590992L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_CONCEPTO")
  private Integer id;

  @Column(name = "CLAVE_PROD_SERV")
  private String claveProdServ;

  @Column(name = "DESCRIPCION_CLAVE_UNIDAD")
  private String descripcionCUPS;

  @Column(name = "NO_IDENTIFICACION")
  private String noIdentificacion;

  @Column(name = "CANTIDAD")
  private BigDecimal cantidad;

  @Column(name = "CLAVE_UNIDAD")
  private String claveUnidad;

  @Column(name = "UNIDAD")
  private String unidad;

  @Column(name = "DESCRIPCION")
  private String descripcion;

  @Column(name = "VALOR_UNITARIO")
  private BigDecimal valorUnitario;

  @Column(name = "IMPORTE")
  private BigDecimal importe;

  @Column(name = "DESCUENTO")
  private BigDecimal descuento;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "ID_CFDI", nullable = false)
  private Cfdi cfdi;

  @OneToMany(mappedBy = "concepto")
  private List<Impuesto> impuestos;

  @OneToMany(mappedBy = "concepto")
  private List<Retencion> retenciones;
}
