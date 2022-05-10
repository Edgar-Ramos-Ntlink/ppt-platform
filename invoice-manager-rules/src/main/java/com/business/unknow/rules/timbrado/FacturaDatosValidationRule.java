package com.business.unknow.rules.timbrado;

import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.rules.common.Constants.Timbrado;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import java.util.List;

import static com.business.unknow.rules.common.Constants.Timbrado.TIMBRADO_DATOS_VALIDATION;
import static com.business.unknow.rules.common.Constants.Timbrado.TIMBRADO_DATOS_VALIDATION_RULE;
import static com.business.unknow.rules.common.Constants.Timbrado.TIMBRADO_DATOS_VALIDATION_RULE_DESC;
import static com.business.unknow.rules.common.Constants.Timbrado.TIMBRADO_SUITE;

@Rule(
    name = TIMBRADO_DATOS_VALIDATION,
    description = TIMBRADO_DATOS_VALIDATION_RULE)
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
                            TIMBRADO_SUITE,
                            TIMBRADO_DATOS_VALIDATION_RULE_DESC)));
  }
}
