package com.business.unknow.services.util.validators;

import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.pagos.PagoFacturaDto;
import com.business.unknow.model.error.InvoiceManagerException;

public class PagoValidator extends Validator {

  public static void validate(PagoDto dto) throws InvoiceManagerException {
    checkNotNull(dto.getBanco(), "Banco");
    checkNotNull(dto.getCuenta(), "Cuenta");
    checkNotNull(dto.getFechaPago(), "Fecha de pago");
    checkNotNull(dto.getFormaPago(), "Forma de pago");
    checkNotNull(dto.getMoneda(), "Moneda");
    checkNotNull(dto.getMonto(), "Monto");
    checkNotNull(dto.getAcredor(), "Razon social empresa");
    checkNotNull(dto.getDeudor(), "Razon social cliente");
    checkNotNull(dto.getSolicitante(), "Solicitante");
    checkNotNull(dto.getStatusPago(), "Estatus de pago");
    checkNotNull(dto.getTipoDeCambio(), "Tipo de cambio");
    checkNotNegative(dto.getTipoDeCambio(), "Tipo de cambio");
    checkNotNegative(dto.getMonto(), "Monto pago");
    if (!dto.getFacturas().isEmpty()) {
      for (PagoFacturaDto fact : dto.getFacturas()) {
        checkNotNull(fact.getFolio(), "Folio factura");
        checkNotEmpty(fact.getFolio(), "Folio factura");
        checkNotNull(fact.getMonto(), "Monto pago");
      }
    }
  }
}
