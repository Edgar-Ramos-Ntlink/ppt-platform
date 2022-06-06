package com.business.unknow.rules.factura;

import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.rules.Constants.FacturaSuite;
import java.util.List;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(name = FacturaSuite.EMISOR_VALIDATION_RULE, description = FacturaSuite.EMISOR_VALIDATION)
public class EmisorValidationRule {
  @Condition
  public boolean condition(@Fact("facturaCustom") EmpresaDto empresaDto) {
    return empresaDto != null && !empresaDto.isActivo();
  }

  @Action
  public void execute(@Fact("results") List<String> results) {
    results.add(
        String.format(
            String.format(
                "Error durante : %s con el error: %s",
                FacturaSuite.FACTURA_SUITE, FacturaSuite.EMISOR_VALIDATION_RULE_DESC)));
  }
}
