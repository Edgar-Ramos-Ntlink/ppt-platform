/** */
package com.business.unknow.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/** @author ralfdemoledor */
@SuperBuilder
@Getter
@Setter
@ToString
public class PagoReportDto extends ReportDto implements Serializable {

  private String folioPago;
  private String folioFiscalPago;
  private BigDecimal importePagado;
  private BigDecimal saldoAnterior;
  private BigDecimal saldoInsoluto;
  private Integer numeroParcialidad;
  private String fechaPago;
}
