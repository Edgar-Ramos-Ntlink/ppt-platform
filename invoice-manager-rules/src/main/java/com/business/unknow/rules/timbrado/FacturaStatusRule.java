package com.business.unknow.rules.timbrado;

import com.business.unknow.enums.FacturaStatusEnum;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.rules.common.Constants.Timbrado;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import java.util.List;

import static com.business.unknow.rules.common.Constants.Timbrado.TIMBRADO_STATUS;
import static com.business.unknow.rules.common.Constants.Timbrado.TIMBRADO_STATUS_RULE;
import static com.business.unknow.rules.common.Constants.Timbrado.TIMBRADO_STATUS_RULE_DESC;
import static com.business.unknow.rules.common.Constants.Timbrado.TIMBRADO_SUITE;

@Rule(name = TIMBRADO_STATUS, description = TIMBRADO_STATUS_RULE)
public class FacturaStatusRule {

  @Condition
  public boolean condition(@Fact("FacturaCustom") FacturaCustom facturaCustom) {
    return facturaCustom.getStatusFactura().equals(FacturaStatusEnum.TIMBRADA.getValor())
        || facturaCustom.getStatusFactura().equals(FacturaStatusEnum.CANCELADA.getValor())
        || facturaCustom            .getStatusFactura()
            .equals(FacturaStatusEnum.RECHAZO_OPERACIONES.getValor())
        || facturaCustom
            .getStatusFactura()
            .equals(FacturaStatusEnum.RECHAZO_TESORERIA.getValor());
  }

  @Action
  public void execute(@Fact("facturaContext") FacturaContext fc) {
    fc.setRuleErrorDesc(TIMBRADO_STATUS_RULE_DESC);
    fc.setSuiteError(String.format("Error durante : %s", TIMBRADO_SUITE));
    fc.setValid(false);
  }

  @Action
  public void execute(@Fact("results") List<String> results) {
    results.add(
            String.format(
                    String.format(
                            "Error durante : %s con el error: %s",
                            TIMBRADO_SUITE,
                            TIMBRADO_STATUS_RULE_DESC)));
  }
}
