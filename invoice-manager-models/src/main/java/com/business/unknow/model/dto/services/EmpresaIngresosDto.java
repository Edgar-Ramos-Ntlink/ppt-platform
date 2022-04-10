package com.business.unknow.model.dto.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmpresaIngresosDto implements Serializable {

  private Integer id;
  private String rfc;
  private String tipoDato;
  private Integer anio;
  private BigDecimal detalle;
  private String link;
  private String creador;
  private Date creacion;
  private Date actualizacion;
}
