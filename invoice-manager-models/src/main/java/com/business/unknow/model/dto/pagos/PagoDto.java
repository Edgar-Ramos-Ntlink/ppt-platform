package com.business.unknow.model.dto.pagos;

import static com.business.unknow.Constants.JSON_DATETIME_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagoDto implements Serializable {

  private static final long serialVersionUID = -8495281362684756977L;

  private Integer id;
  private String moneda;
  private String banco;
  private String cuenta;
  private BigDecimal tipoDeCambio;
  private String formaPago;
  private BigDecimal monto;
  private String statusPago;
  private String acredor;
  private String deudor;
  private String comentarioPago;
  private String solicitante;
  private Boolean revision1;
  private Boolean revision2;
  private String revisor1;
  private String revisor2;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATETIME_FORMAT)
  private Date fechaPago;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATETIME_FORMAT)
  private Date fechaCreacion;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATETIME_FORMAT)
  private Date fechaActualizacion;

  private List<PagoFacturaDto> facturas;

  private String comprobante;
}
