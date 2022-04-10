package com.business.unknow.model.dto.cfdi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class RetencionDto implements Serializable {

  private static final long serialVersionUID = 4590109888394034653L;
  private Integer id;
  private BigDecimal base;
  private String impuesto;
  private String tipoFactor;
  private BigDecimal tasaOCuota;
  private BigDecimal importe;
}
