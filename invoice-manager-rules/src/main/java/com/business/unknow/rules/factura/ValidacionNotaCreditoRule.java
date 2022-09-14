package com.business.unknow.rules.factura;

import com.business.unknow.enums.FacturaStatus;
import com.business.unknow.enums.TipoDocumento;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.rules.Constants.FacturaValidationSuite;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(
    name = FacturaValidationSuite.FACTURA_VALIDATION_NOTA_CREDITO_RULE,
    description = FacturaValidationSuite.FACTURA_VALIDATION_NOTA_CREDITO_RULE_DESC)
public class ValidacionNotaCreditoRule {

  @Condition
  public boolean condition(@Fact("facturaCustom") FacturaCustom facturaCustom) {
    if ((FacturaStatus.VALIDACION_OPERACIONES.getValor().equals(facturaCustom.getStatusFactura())
            || FacturaStatus.VALIDACION_TESORERIA
                .getValor()
                .equals(facturaCustom.getStatusFactura())
            || FacturaStatus.RECHAZO_TESORERIA.getValor().equals(facturaCustom.getStatusFactura()))
        && facturaCustom.getTipoDocumento().equals(TipoDocumento.NOTA_CREDITO.getDescripcion()) && facturaCustom.getStatusFactura()<3) { // TODO verify if there is a better way to handle this
      return true;
    }
    return false;
  }

  @Action
  public void execute(@Fact("facturaCustom") FacturaCustom facturaCustom) {
    if (facturaCustom.getValidacionOper() && facturaCustom.getValidacionTeso()) {
      facturaCustom.setStatusFactura(FacturaStatus.POR_TIMBRAR.getValor());
    } else if (facturaCustom.getValidacionOper() && !facturaCustom.getValidacionTeso()) {
      facturaCustom.setStatusFactura(FacturaStatus.VALIDACION_TESORERIA.getValor());
    } else if (!facturaCustom.getValidacionOper() && facturaCustom.getValidacionTeso()) {
      facturaCustom.setStatusFactura(FacturaStatus.VALIDACION_OPERACIONES.getValor());
    } else {
      facturaCustom.setStatusFactura(FacturaStatus.VALIDACION_OPERACIONES.getValor());
    }
  }
}
