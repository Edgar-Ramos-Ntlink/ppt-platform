package com.business.unknow.services.entities.cfdi;

import java.io.Serializable;
import java.math.BigDecimal;
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

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@ToString
@Entity
@Table(name = "CFDI_IMPUESTOS")
public class Impuesto implements Serializable {

  private static final long serialVersionUID = 5623944906795628517L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_IMPUESTO")
  private Integer id;

  @Column(name = "BASE")
  private BigDecimal base;

  @Column(name = "IMPUESTO")
  private String impuesto;

  @Column(name = "TIPO_FACTOR")
  private String tipoFactor;

  @Column(name = "TASA_CUOTA")
  private BigDecimal tasaOCuota;

  @Column(name = "IMPORTE")
  private BigDecimal importe;

  @ManyToOne
  @JoinColumn(name = "ID_CONCEPTO", nullable = false)
  private Concepto concepto;
}
