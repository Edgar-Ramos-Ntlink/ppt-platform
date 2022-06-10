package com.business.unknow.model.dto;

import static com.business.unknow.Constants.JSON_DATETIME_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagoComplemento {

  private String idDocumento;
  private String folioOrigen;
  private String folio;
  private String equivalenciaDR;
  private String formaDePagoP;
  private String monedaDr;
  private int numeroParcialidad;
  private BigDecimal importeSaldoAnterior;
  private BigDecimal importePagado;
  private BigDecimal importeSaldoInsoluto;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATETIME_FORMAT)
  private Date fechaPago;

  private boolean valido;
  private BigDecimal tipoCambioDr;
  private BigDecimal tipoCambio;
}
