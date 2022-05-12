package com.business.unknow.rules.payments;

import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.pagos.PagoFacturaDto;
import com.business.unknow.rules.Constants.PaymentsSuite;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(
    name = PaymentsSuite.MONTO_PAGO_VALIDATION_RULE,
    description = PaymentsSuite.MONTO_PAGO_VALIDATION)
@Slf4j
public class PaymentAmountValidationRule {

  @Condition
  public boolean condition(
      @Fact("payment") PagoDto currentPayment, @Fact("facturas") List<FacturaCustom> facturas) {
    if (facturas != null && currentPayment != null) {
      BigDecimal total =
          currentPayment.getFacturas().stream()
              .map(PagoFacturaDto::getMonto)
              .reduce(BigDecimal.ZERO, (p1, p2) -> p1.add(p2));
      return total
              .subtract(currentPayment.getMonto())
              .setScale(1, RoundingMode.DOWN)
              .compareTo(BigDecimal.ZERO)
          != 0;
    } else {
      log.error(
          "One or more missing facts on {} rule", PaymentAmountValidationRule.class.getName());
      return true;
    }
  }

  @Action
  public void execute(@Fact("results") List<String> results) {
    results.add(PaymentsSuite.MONTO_PAGO_VALIDATION_RULE_DESC);
  }
}
