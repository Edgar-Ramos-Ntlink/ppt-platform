package com.business.unknow.rules.factura;

import static com.business.unknow.enums.FacturaStatus.TIMBRADA;
import static com.business.unknow.rules.Constants.CancelacionSuite.CANCELAR_STATUS_VALIDATION;
import static com.business.unknow.rules.Constants.CancelacionSuite.CANCELAR_STATUS_VALIDATION_RULE;
import static com.business.unknow.rules.Constants.CancelacionSuite.CANCELAR_STATUS_VALIDATION_RULE_DESC;
import static com.business.unknow.rules.Constants.CancelacionSuite.CANCELAR_SUITE;

import com.business.unknow.model.dto.FacturaCustom;
import java.util.List;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(name = CANCELAR_STATUS_VALIDATION, description = CANCELAR_STATUS_VALIDATION_RULE)
public class CancelStatusValidationRule {

  @Condition
  public boolean condition(@Fact("facturaDto") FacturaCustom facturaCustom) {
    return !TIMBRADA.getValor().equals(facturaCustom.getStatusFactura());
  }

  @Action
  public void execute(@Fact("results") List<String> results) {
    results.add(
        String.format(
            String.format(
                "Error durante : %s con el error: %s",
                CANCELAR_SUITE, CANCELAR_STATUS_VALIDATION_RULE_DESC)));
  }
}
