package com.business.unknow.rules.factura;

import static com.business.unknow.enums.FacturaStatus.POR_TIMBRAR;
import static com.business.unknow.enums.FacturaStatus.VALIDACION_TESORERIA;

import com.business.unknow.enums.TipoDocumento;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.rules.Constants.FacturaValidationSuite;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(
    name = FacturaValidationSuite.FACTURA_VALIDATION_COMP_RULE,
    description = FacturaValidationSuite.FACTURA_VALIDATION_COMP_RULE_DESC)
public class ValidacionFacturaComplementoRule {

  @Condition
  public boolean condition(@Fact("facturaCustom") FacturaCustom facturaCustom) {
    if (facturaCustom.getTipoDocumento().equals(TipoDocumento.COMPLEMENTO.getDescripcion())) {
      return true;
    }
    return false;
  }

  @Action
  public void execute(@Fact("factura") FacturaCustom facturaDto) {
    if (facturaDto.getValidacionOper() && facturaDto.getValidacionTeso()) {
      facturaDto.setStatusFactura(POR_TIMBRAR.getValor());
    } else if (facturaDto.getValidacionOper() && !facturaDto.getValidacionTeso()) {
      facturaDto.setStatusFactura(VALIDACION_TESORERIA.getValor());
    } else if (!facturaDto.getValidacionOper() && facturaDto.getValidacionTeso()) {
      facturaDto.setStatusFactura(POR_TIMBRAR.getValor());
    } else {
      facturaDto.setStatusFactura(VALIDACION_TESORERIA.getValor());
    }
  }
}
