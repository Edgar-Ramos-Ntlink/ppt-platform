package com.business.unknow.model.dto.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.Date;

@Jacksonized
@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CuentaBancariaDto implements Serializable {

	private static final long serialVersionUID = 8643631228668299142L;

	private int id;
	private int total;
	private String empresa;
	private String linea;
	private String giro;
	private String razonSocial;
	private String banco;
	private String cuenta;
	private String clabe;
	private Date fechaCreacion;
	private Date fechaActualizacion;
}
