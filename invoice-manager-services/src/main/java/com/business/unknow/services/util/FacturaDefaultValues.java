package com.business.unknow.services.util;

import java.math.BigDecimal;
import java.util.Date;

import com.business.unknow.Constants.PagoPpdCreditoDefaults;
import com.business.unknow.commons.util.FacturaCalculator;
import com.business.unknow.enums.FacturaStatusEnum;
import com.business.unknow.enums.PackFacturarionEnum;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.cfdi.CfdiDto;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.error.InvoiceManagerException;

public class FacturaDefaultValues {

	private FacturaCalculator facturaCalculator = new FacturaCalculator();

	public void assignaDefaultsFactura(FacturaDto facturaDto) throws InvoiceManagerException {
		facturaDto.setSaldoPendiente(facturaDto.getTotal());
		facturaDto.setPackFacturacion(PackFacturarionEnum.SW_SAPIENS.name());
		facturaDto.getCfdi().setTipoCambio(BigDecimal.ONE);
		if (facturaDto.getStatusFactura() == null) {
			facturaDto.setStatusFactura(FacturaStatusEnum.VALIDACION_OPERACIONES.getValor());
		}
		facturaCalculator.assignFolioInFacturaDto(facturaDto);
	}

	public PagoDto assignaDefaultsPagoPPD(CfdiDto cfdi) {
		return new PagoBuilder().setBanco(PagoPpdCreditoDefaults.BANCO)
				.setSolicitante(PagoPpdCreditoDefaults.USER)
				.setCuenta(PagoPpdCreditoDefaults.CUENTA)
				.setComentarioPago(PagoPpdCreditoDefaults.COMENTARIO)
				.setFechaPago(new Date())
				.setFormaPago(PagoPpdCreditoDefaults.FORMA_PAGO).setMoneda(PagoPpdCreditoDefaults.MONEDA)
				.setMonto(cfdi.getTotal()).setRevision1(false).setRevision2(false)
				.setTipoDeCambio(new BigDecimal(PagoPpdCreditoDefaults.TIPO_CAMBIO))
				.setStatusPago(PagoPpdCreditoDefaults.STATUS_PAGO).build();
		
	}

	public void assignaDefaultsComplemento(FacturaDto facturaDto) throws InvoiceManagerException {
		facturaDto.setFechaCreacion(new Date());
		facturaDto.setFechaActualizacion(new Date());
		facturaDto.setStatusFactura(FacturaStatusEnum.VALIDACION_TESORERIA.getValor());
		facturaCalculator.assignFolioInFacturaDto(facturaDto);
	}
	
	
}
