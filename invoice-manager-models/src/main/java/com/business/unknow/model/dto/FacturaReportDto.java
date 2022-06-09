package com.business.unknow.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@ToString
public class FacturaReportDto extends ReportDto implements Serializable {

  private static final long serialVersionUID = -1523422223111592963L;

  private String correoPromotor;
  private BigDecimal cantidad;
  private String lineaEmisor;
  private String claveUnidad;
  private String unidad;
  private Integer claveProdServ;
  private String descripcion;
  private BigDecimal valorUnitario;
  private BigDecimal importe;
  private BigDecimal saldoPendiente;
}
