package com.business.unknow.rules.timbrado;

import static com.business.unknow.rules.Constants.Timbrado.TIMBRADO_STATUS;
import static com.business.unknow.rules.Constants.Timbrado.TIMBRADO_STATUS_RULE;
import static com.business.unknow.rules.Constants.Timbrado.TIMBRADO_STATUS_RULE_DESC;
import static com.business.unknow.rules.Constants.Timbrado.TIMBRADO_SUITE;

import com.business.unknow.enums.FacturaStatus;
import com.business.unknow.model.dto.FacturaCustom;
import java.util.List;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(name = TIMBRADO_STATUS, description = TIMBRADO_STATUS_RULE)
public class FacturaStatusRule {

  @Condition
  public boolean condition(@Fact("FacturaCustom") FacturaCustom facturaCustom) {
    return facturaCustom.getStatusFactura().equals(FacturaStatus.TIMBRADA.getValor())
        || facturaCustom.getStatusFactura().equals(FacturaStatus.CANCELADA.getValor())
        || facturaCustom.getStatusFactura().equals(FacturaStatus.RECHAZO_OPERACIONES.getValor())
        || facturaCustom.getStatusFactura().equals(FacturaStatus.RECHAZO_TESORERIA.getValor());
  }

  @Action
  public void execute(@Fact("results") List<String> results) {
    results.add(
        String.format(
            String.format(
                "Error durante : %s con el error: %s", TIMBRADO_SUITE, TIMBRADO_STATUS_RULE_DESC)));
  }
}
