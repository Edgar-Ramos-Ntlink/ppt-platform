/**
 * 
 */
package com.business.unknow.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author hha0009
 *
 */
public class PagoReportDto extends ReportDto implements Serializable {

	private static final long serialVersionUID = -5168858340000054635L;
	
	//FOLIO_PAGO               |UUID_PAGO                           |IMPORTE_PAGADO|IMPORTE_SALDO_ANTERIOR|IMPORTE_SALDO_INSOLUTO|NUM_PARCIALIDAD
	
	private String folioPago;
	private String folioFiscalPago;
	private BigDecimal importePagado;
	private BigDecimal saldoAnterior;
	private BigDecimal saldoInsoluto;
	private Integer numeroParcialidad;
	
	public String getFolioPago() {
		return folioPago;
	}
	public void setFolioPago(String folioPago) {
		this.folioPago = folioPago;
	}
	public String getFolioFiscalPago() {
		return folioFiscalPago;
	}
	public void setFolioFiscalPago(String folioFiscalPago) {
		this.folioFiscalPago = folioFiscalPago;
	}
	public BigDecimal getImportePagado() {
		return importePagado;
	}
	public void setImportePagado(BigDecimal importePagado) {
		this.importePagado = importePagado;
	}
	public BigDecimal getSaldoAnterior() {
		return saldoAnterior;
	}
	public void setSaldoAnterior(BigDecimal saldoAnterior) {
		this.saldoAnterior = saldoAnterior;
	}
	public BigDecimal getSaldoInsoluto() {
		return saldoInsoluto;
	}
	public void setSaldoInsoluto(BigDecimal saldoInsoluto) {
		this.saldoInsoluto = saldoInsoluto;
	}
	public Integer getNumeroParcialidad() {
		return numeroParcialidad;
	}
	public void setNumeroParcialidad(Integer numeroParcialidad) {
		this.numeroParcialidad = numeroParcialidad;
	}
	
	
	

}