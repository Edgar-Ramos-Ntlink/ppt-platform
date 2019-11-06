package com.business.unknow.services.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PAGOS")
public class Pago implements Serializable {

	private static final long serialVersionUID = 8371622895161409889L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_PAGO")
	private Integer id;

	@NotNull
	@Column(name = "FOLIO")
	private String folio;

	@NotEmpty
	@Column(name = "MONEDA")
	private String moneda;

	@Column(name = "DOCUMENTO")
	private String documento;

	@NotEmpty
	@Column(name = "BANCO")
	private String banco;

	@NotNull
	@Column(name = "TIPO_CAMBIO")
	private Double tipoDeCambio;
	
	@DecimalMin(value = "1.00")
	@Column(name = "MONTO")
	private Double monto;
	
	@NotNull
	@Column(name = "REVISION_1")
	private Boolean revision1;
	
	@NotNull
	@Column(name = "REVISION_2")
	private Boolean revision2;

	@Column(name = "STATUS_PAGO")
	private String statusPago;

	@Column(name = "COMENTARIO_PAGO")
	private String comentarioPago;
	/*INGRESO(pagos facturas) -- EGRESO(devoluciones)*/
	@NotEmpty
	@Column(name = "TIPO_PAGO")
	private String tipoPago;
	
	/* DEPOSITO,TRANSFERENCIA, CHEQUE, EFECTIVO*/
	@NotEmpty
	@Column(name = "FORMA_PAGO")
	private String formaPago;

	@Temporal(TemporalType.DATE)
	@Column(name = "FECHA_PAGO")
	private Date fechaPago;

	@Temporal(TemporalType.DATE)
	@CreatedDate
	@Column(name = "FECHA_CREACION")
	private Date fechaCreacion;

	@Temporal(TemporalType.DATE)
	@LastModifiedDate
	@Column(name = "FECHA_ACTUALIZACION")
	private Date fechaActualizacion;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFolio() {
		return folio;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public Double getTipoDeCambio() {
		return tipoDeCambio;
	}

	public void setTipoDeCambio(Double tipoDeCambio) {
		this.tipoDeCambio = tipoDeCambio;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public Boolean getRevision1() {
		return revision1;
	}

	public void setRevision1(Boolean revision1) {
		this.revision1 = revision1;
	}

	public Boolean getRevision2() {
		return revision2;
	}

	public void setRevision2(Boolean revision2) {
		this.revision2 = revision2;
	}

	public String getStatusPago() {
		return statusPago;
	}

	public void setStatusPago(String statusPago) {
		this.statusPago = statusPago;
	}

	public String getComentarioPago() {
		return comentarioPago;
	}

	public void setComentarioPago(String comentarioPago) {
		this.comentarioPago = comentarioPago;
	}

	public String getTipoPago() {
		return tipoPago;
	}

	public void setTipoPago(String tipoPago) {
		this.tipoPago = tipoPago;
	}

	public String getFormaPago() {
		return formaPago;
	}

	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}

	public Date getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
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
		return "Pago [id=" + id + ", folio=" + folio + ", moneda=" + moneda + ", documento=" + documento + ", banco="
				+ banco + ", tipoDeCambio=" + tipoDeCambio + ", monto=" + monto + ", revision1=" + revision1
				+ ", revision2=" + revision2 + ", statusPago=" + statusPago + ", comentarioPago=" + comentarioPago
				+ ", tipoPago=" + tipoPago + ", formaPago=" + formaPago + ", fechaPago=" + fechaPago
				+ ", fechaCreacion=" + fechaCreacion + ", fechaActualizacion=" + fechaActualizacion + "]";
	}

}