package com.business.unknow.model.dto.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/** @author ralfdemoledor */
@Jacksonized
@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmpresaDto implements Serializable {

  private static final long serialVersionUID = -5354660274346579595L;

  private int id;

  private Boolean activo;

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

  private String anioAlta;

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

  private Date expiracionCertificado;

  private Date expiracionFiel;

  private String creador;

  private Date fechaCreacion;

  private Date fechaActualizacion;
}
