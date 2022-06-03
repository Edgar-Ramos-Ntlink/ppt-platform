package com.business.unknow.model.dto.services;

import static com.business.unknow.Constants.JSON_DATETIME_FORMAT;

import com.business.unknow.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmpresaDto implements Serializable {

  private int id;

  private boolean activo;

  private boolean operativa;

  private boolean bloqueada;

  private String estatus;

  private Integer giro;

  private String tipo;

  private String regimenFiscal;

  private String rfc;

  private String nombre;

  private String razonSocial;

  private String calle;

  private String noExterior;

  private String noInterior;

  private String municipio;

  private String colonia;

  private String estado;

  private String pais;

  private String cp;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DAY_FORMAT)
  private Date inicioActividades;

  private String registroPatronal;

  private String estatusJuridico;

  private String estatusJuridico2;

  private String representanteLegal;

  private String ciec;

  private String fiel;

  private String actividadSAT;

  private String web;

  private String correo;

  private String pwCorreo;

  private String dominioCorreo;

  private String pwSat;

  private String noCertificado;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DAY_FORMAT)
  private Date expiracionCertificado;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DAY_FORMAT)
  private Date expiracionFiel;

  private String impuestoEstatal;

  private String entidadRegistroPatronal;

  private String entidadImpuestoPatronal;

  private String creador;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATETIME_FORMAT)
  private Date fechaCreacion;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DATETIME_FORMAT)
  private Date fechaActualizacion;

  private List<CuentaBancariaDto> cuentas;

  private List<EmpresaDetallesDto> detalles;

  private List<EmpresaIngresosDto> ingresos;
}
