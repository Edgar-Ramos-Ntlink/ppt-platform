package com.business.unknow.model.dto.services;

import com.business.unknow.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
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
public class EmpresaDetallesDto implements Serializable {
  private int id;
  private String rfc;
  private String tipo;
  private String notificante;
  private String area;
  private String resumen;
  private String detalle;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATETIME_FORMAT)
  private Date fechaCreacion;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATETIME_FORMAT)
  private Date fechaActualizacion;
}
