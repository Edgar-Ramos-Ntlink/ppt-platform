package com.business.unknow.rules.timbrado;

import static com.business.unknow.rules.Constants.Timbrado.TIMBRADO_DATOS_VALIDATION;
import static com.business.unknow.rules.Constants.Timbrado.TIMBRADO_DATOS_VALIDATION_RULE;
import static com.business.unknow.rules.Constants.Timbrado.TIMBRADO_DATOS_VALIDATION_RULE_DESC;
import static com.business.unknow.rules.Constants.Timbrado.TIMBRADO_SUITE;

import com.business.unknow.model.dto.FacturaCustom;
import java.util.List;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(name = TIMBRADO_DATOS_VALIDATION, description = TIMBRADO_DATOS_VALIDATION_RULE)
public class FacturaDatosValidationRule {

  @Condition
  public boolean condition(@Fact("facturaContext") FacturaCustom facturaCustom) {
    if (facturaCustom.getUuid() != null || facturaCustom.getFechaTimbrado() != null) {
      return true;
    } else {
      return false;
    }
  }

  @Action
  public void execute(@Fact("results") List<String> results) {
    results.add(
        String.format(
            String.format(
                "Error durante : %s con el error: %s",
                TIMBRADO_SUITE, TIMBRADO_DATOS_VALIDATION_RULE_DESC)));
  }
}
