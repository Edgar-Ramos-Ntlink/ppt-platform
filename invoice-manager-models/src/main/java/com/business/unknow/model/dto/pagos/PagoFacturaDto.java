package com.business.unknow.model.dto.pagos;

import static com.business.unknow.Constants.JSON_DATETIME_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoFacturaDto implements Serializable {

  private static final long serialVersionUID = -3623786015284346953L;
  private Integer id;
  private Integer idCfdi;
  private String folio;
  private String folioReferencia;
  private String rfcEmisor;
  private String rfcReceptor;
  private String solicitante;
  private BigDecimal monto;
  private BigDecimal totalFactura;
  private String acredor;
  private String deudor;
  private String metodoPago;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATETIME_FORMAT)
  private Date fechaCreacion;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATETIME_FORMAT)
  private Date fechaActualizacion;
}
