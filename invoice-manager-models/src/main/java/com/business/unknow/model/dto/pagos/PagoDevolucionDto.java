package com.business.unknow.model.dto.pagos;

import static com.business.unknow.Constants.JSON_DAY_FORMAT;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PagoDevolucionDto implements Serializable {

  private static final long serialVersionUID = 5324209502166412941L;

  private Integer id;
  private String moneda;
  private BigDecimal tipoCambio;
  private BigDecimal monto;
  private String beneficiario;
  private String formaPago;
  private String banco;
  private String tipoReferencia;
  private String referencia;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DAY_FORMAT)
  private Date fechaPago;

  private String status;
  private String tipoReceptor;
  private String receptor;
  private String solicitante;
  private String cuentaPago;
  private String rfcEmpresa;
  private String autorizador;
  private String comentarios;
  private Integer idDevolucion;
  private String folioFactura;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DAY_FORMAT)
  private Date fechaCreacion;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DAY_FORMAT)
  private Date fechaActualizacion;
}
