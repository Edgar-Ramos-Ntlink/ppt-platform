package com.business.unknow.services.util;

import com.business.unknow.Constants.PagoPpdCreditoDefaults;
import com.business.unknow.enums.FacturaStatusEnum;
import com.business.unknow.enums.PackFacturarionEnum;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.cfdi.CfdiDto;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.util.helpers.FacturaCalculator;
import java.math.BigDecimal;
import java.util.Date;

public class FacturaDefaultValues {

  private FacturaCalculator facturaCalculator = new FacturaCalculator();

  public void assignaDefaultsFactura(FacturaDto facturaDto, int amount)
      throws InvoiceManagerException {
    facturaDto.setSaldoPendiente(facturaDto.getTotal());
    facturaDto.setPackFacturacion(PackFacturarionEnum.NTLINK.name());
    facturaDto
        .getCfdi()
        .setTipoCambio(
            facturaDto.getCfdi().getTipoCambio() == null
                ? BigDecimal.ONE
                : facturaDto.getCfdi().getTipoCambio());
    if (facturaDto.getStatusFactura() == null) {
      facturaDto.setStatusFactura(FacturaStatusEnum.VALIDACION_OPERACIONES.getValor());
    }
    facturaCalculator.assignFolioInFacturaDto(facturaDto);
    facturaCalculator.assignPreFolioInFacturaDto(facturaDto, amount);
  }

  public PagoDto assignaDefaultsPagoPPD(CfdiDto cfdi) {
    return PagoDto.builder()
        .banco(PagoPpdCreditoDefaults.BANCO)
        .solicitante(PagoPpdCreditoDefaults.USER)
        .cuenta(PagoPpdCreditoDefaults.CUENTA)
        .comentarioPago(PagoPpdCreditoDefaults.COMENTARIO)
        .fechaPago(new Date())
        .formaPago(PagoPpdCreditoDefaults.FORMA_PAGO)
        .moneda(PagoPpdCreditoDefaults.MONEDA)
        .monto(cfdi.getTotal())
        .revision1(false)
        .revision2(false)
        .tipoDeCambio(new BigDecimal(PagoPpdCreditoDefaults.TIPO_CAMBIO))
        .statusPago(PagoPpdCreditoDefaults.STATUS_PAGO)
        .build();
  }

  public void assignaDefaultsComplemento(FacturaDto facturaDto, int amount)
      throws InvoiceManagerException {
    facturaDto.setFechaCreacion(new Date());
    facturaDto.setFechaActualizacion(new Date());
    facturaDto.setStatusFactura(FacturaStatusEnum.VALIDACION_TESORERIA.getValor());
    facturaCalculator.assignFolioInFacturaDto(facturaDto);
    facturaCalculator.assignPreFolioInFacturaDto(facturaDto, amount);
  }
}
