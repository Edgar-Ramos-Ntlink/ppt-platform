package com.business.unknow.model.dto.services;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PagoDevolucionDto implements Serializable {

	private static final long serialVersionUID = 5324209502166412941L;

	private Integer id;
	private String moneda;
	private BigDecimal tipoCambio;
	private BigDecimal monto;
	private String beneficiario;
	private String formaPago;
	private String banco;
	private String tipoReferencia;
	private Date fechaPago;
	private String status;
	private String tipoReceptor;
	private String solicitante;
	private String tipoCuentaOrigen;
	private String cuentaOrigen;
	private String rfcEmpresa;
	private Date fechaPagoOrigen;
	private String autorizador;
	private Date fechaCreacion;
	private Date fechaActualizacion;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public BigDecimal getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(BigDecimal tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public BigDecimal getMonto() {
		return monto;
	}

	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	public String getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public String getTipoReferencia() {
		return tipoReferencia;
	}

	public void setTipoReferencia(String tipoReferencia) {
		this.tipoReferencia = tipoReferencia;
	}

	public Date getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTipoReceptor() {
		return tipoReceptor;
	}

	public void setTipoReceptor(String tipoReceptor) {
		this.tipoReceptor = tipoReceptor;
	}

	public String getSolicitante() {
		return solicitante;
	}

	public void setSolicitante(String solicitante) {
		this.solicitante = solicitante;
	}

	public String getTipoCuentaOrigen() {
		return tipoCuentaOrigen;
	}

	public void setTipoCuentaOrigen(String tipoCuentaOrigen) {
		this.tipoCuentaOrigen = tipoCuentaOrigen;
	}

	public String getCuentaOrigen() {
		return cuentaOrigen;
	}

	public void setCuentaOrigen(String cuentaOrigen) {
		this.cuentaOrigen = cuentaOrigen;
	}

	public String getRfcEmpresa() {
		return rfcEmpresa;
	}

	public void setRfcEmpresa(String rfcEmpresa) {
		this.rfcEmpresa = rfcEmpresa;
	}

	public Date getFechaPagoOrigen() {
		return fechaPagoOrigen;
	}

	public void setFechaPagoOrigen(Date fechaPagoOrigen) {
		this.fechaPagoOrigen = fechaPagoOrigen;
	}

	public String getAutorizador() {
		return autorizador;
	}

	public void setAutorizador(String autorizador) {
		this.autorizador = autorizador;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(Date fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	@Override
	public String toString() {
		return "PagoDevolucionDto [id=" + id + ", moneda=" + moneda + ", tipoCambio=" + tipoCambio + ", monto=" + monto
				+ ", beneficiario=" + beneficiario + ", formaPago=" + formaPago + ", banco=" + banco
				+ ", tipoReferencia=" + tipoReferencia + ", fechaPago=" + fechaPago + ", status=" + status
				+ ", tipoReceptor=" + tipoReceptor + ", solicitante=" + solicitante + ", tipoCuentaOrigen="
				+ tipoCuentaOrigen + ", cuentaOrigen=" + cuentaOrigen + ", rfcEmpresa=" + rfcEmpresa
				+ ", fechaPagoOrigen=" + fechaPagoOrigen + ", autorizador=" + autorizador + ", fechaCreacion="
				+ fechaCreacion + ", fechaActualizacion=" + fechaActualizacion + "]";
	}

}