package com.business.unknow.rules.payments;

import com.business.unknow.enums.RevisionPagos;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.rules.Constants.PaymentsSuite;
import java.util.List;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(
    name = PaymentsSuite.DOUBLE_PAYMENT_VALIDATION_RULE,
    description = PaymentsSuite.DOUBLE_PAYMENT_VALIDATION_RULE_DESC)
public class DoubleValidatesPaymentRule {

  @Condition
  public boolean condition(
      @Fact("payment") PagoDto currentPayment, @Fact("dbPayment") PagoDto dbPayment) {

    if (dbPayment.getRevision1()
        && currentPayment.getRevision1()
        && currentPayment.getRevision2()
        && !RevisionPagos.RECHAZADO.name().equals(currentPayment.getStatusPago())) {
      currentPayment.setStatusPago(RevisionPagos.ACEPTADO.name());
      return false;
    } else {
      return !dbPayment.getRevision1() && currentPayment.getRevision2();
    }
  }

  @Action
  public void execute(@Fact("results") List<String> results) {
    results.add(PaymentsSuite.DOUBLE_PAYMENT_VALIDATION_RULE_DESC);
  }
}
