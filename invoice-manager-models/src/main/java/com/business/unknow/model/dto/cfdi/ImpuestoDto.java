package com.business.unknow.model.dto.cfdi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ImpuestoDto implements Serializable {

  private static final long serialVersionUID = 3241569278979852126L;
  private Integer id;
  private BigDecimal base;
  private String impuesto;
  private String tipoFactor;
  private BigDecimal tasaOCuota;
  private BigDecimal importe;

  public ImpuestoDto(BigDecimal base, BigDecimal importe, BigDecimal tasaOcuota) {
    this.base = base;
    this.importe = importe;
    this.tasaOCuota = tasaOcuota;
  }
}
