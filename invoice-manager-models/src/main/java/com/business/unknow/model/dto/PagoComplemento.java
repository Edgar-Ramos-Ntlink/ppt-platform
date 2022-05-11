package com.business.unknow.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
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
  private String equivalenciaDR;
  private String monedaDr;
  private int numeroParcialidad;
  private BigDecimal importeSaldoAnterior;
  private BigDecimal importePagado;
  private BigDecimal importeSaldoInsoluto;
  private boolean valido;
  private BigDecimal tipoCambioDr;
  private BigDecimal tipoCambio;
}
