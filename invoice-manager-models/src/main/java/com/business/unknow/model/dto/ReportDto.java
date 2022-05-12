package com.business.unknow.model.dto;

import static com.business.unknow.Constants.JSON_DAY_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@ToString
public class ReportDto implements Serializable {

  private static final long serialVersionUID = 8443985089248503145L;

  private String folio;
  private String folioFiscal;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DAY_FORMAT)
  private Date fechaEmision;

  private String rfcEmisor;
  private String emisor;
  private String rfcReceptor;
  private String receptor;
  private String tipoDocumento;
  private String packFacturacion;
  private String tipoComprobante;
  private BigDecimal impuestosTrasladados;
  private BigDecimal impuestosRetenidos;
  private BigDecimal subtotal;
  private BigDecimal total;
  private String metodoPago;
  private String formaPago;
  private String moneda;
  private String statusFactura;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DAY_FORMAT)
  private Date fechaCancelacion;
}
