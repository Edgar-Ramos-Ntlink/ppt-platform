package com.business.unknow.model.dto.services;

import static com.business.unknow.Constants.JSON_DATETIME_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ClientDto implements Serializable {

  private static final long serialVersionUID = 4951260411762447946L;

  private int id;
  private Boolean activo;
  private String notas;
  private BigDecimal porcentajePromotor;
  private BigDecimal porcentajeCliente;
  private BigDecimal porcentajeDespacho;
  private BigDecimal porcentajeContacto;
  private String correoPromotor;
  private String correoContacto;

  private String rfc;
  private String giro;
  private String nombre;
  private boolean moral;
  private String curp;
  private String razonSocial;
  private String regimenFiscal;
  private String calle;
  private String noExterior;
  private String noInterior;
  private String municipio;
  private String localidad;
  private String estado;
  private String pais;
  private String coo;
  private String cp;
  private String telefono;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATETIME_FORMAT)
  private Date fechaCreacion;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATETIME_FORMAT)
  private Date fechaActualizacion;
}
