package com.business.unknow.model.dto.services;

import static com.business.unknow.Constants.JSON_DATE_FORMAT;

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
public class CuentaBancariaDto implements Serializable {

  private int id;
  private String rfc;
  private String banco;
  private String cuenta;
  private String clabe;
  private String domicilioBanco;
  private String tipoContrato;
  private String sucursal;
  private String expedienteActualizado;

  private String linea;
  private String razonSocial;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATE_FORMAT)
  private Date fechaCreacion;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATE_FORMAT)
  private Date fechaActualizacion;
}
