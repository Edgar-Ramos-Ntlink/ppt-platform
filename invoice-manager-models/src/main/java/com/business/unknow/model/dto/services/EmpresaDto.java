package com.business.unknow.model.dto.services;

import java.io.Serializable;
import java.util.Date;

import com.business.unknow.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * @author ralfdemoledor
 *
 */
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
	private String referencia;
	private String regimenFiscal;
	private String web;
	private String contactoAdmin;
	private String sucursal;
	private String lugarExpedicion;
	private String logotipo;
	private String llavePrivada;
	private String certificado;
	private String noCertificado;
	private String pwSat;
	private String pwCorreo;
	private String correo;
	private String encabezado;
	private String piePagina;
	private Boolean activo;
	private String dominioCorreo;
	private String tipo;

	private Integer giro;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
	private Date fechaCreacion;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
	private Date fechaActualizacion;
	private ContribuyenteDto informacionFiscal;
}
