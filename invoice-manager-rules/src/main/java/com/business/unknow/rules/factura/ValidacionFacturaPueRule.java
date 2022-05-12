package com.business.unknow.rules.factura;

import com.business.unknow.enums.FacturaStatus;
import com.business.unknow.enums.MetodosPago;
import com.business.unknow.enums.TipoDocumento;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.rules.Constants.FacturaValidationSuite;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(
    name = FacturaValidationSuite.FACTURA_VALIDATION_PUE_RULE,
    description = FacturaValidationSuite.FACTURA_VALIDATION_PUE_RULE_DESC)
public class ValidacionFacturaPueRule {

  @Condition
  public boolean condition(@Fact("factura") FacturaCustom facturaDto) {
    if ((FacturaStatus.VALIDACION_OPERACIONES.getValor().equals(facturaDto.getStatusFactura())
            || FacturaStatus.VALIDACION_TESORERIA.getValor().equals(facturaDto.getStatusFactura())
            || FacturaStatus.RECHAZO_TESORERIA.getValor().equals(facturaDto.getStatusFactura()))
        && facturaDto.getTipoDocumento().equals(TipoDocumento.FACTURA.getDescripcion())
        && facturaDto.getMetodoPago().equals(MetodosPago.PUE.name())) {
      return true;
    }
    return false;
  }

  @Action
  public void execute(@Fact("factura") FacturaCustom facturaDto) {
    if (facturaDto.getValidacionOper() && facturaDto.getValidacionTeso()) {
      facturaDto.setStatusFactura(FacturaStatus.POR_TIMBRAR.getValor());
    } else if (facturaDto.getValidacionOper() && !facturaDto.getValidacionTeso()) {
      facturaDto.setStatusFactura(FacturaStatus.VALIDACION_TESORERIA.getValor());
    } else if (!facturaDto.getValidacionOper() && facturaDto.getValidacionTeso()) {
      facturaDto.setStatusFactura(FacturaStatus.VALIDACION_OPERACIONES.getValor());
    } else {
      facturaDto.setStatusFactura(FacturaStatus.VALIDACION_OPERACIONES.getValor());
    }
  }
}
