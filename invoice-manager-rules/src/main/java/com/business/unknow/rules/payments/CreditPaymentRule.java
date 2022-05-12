package com.business.unknow.rules.payments;

import com.business.unknow.enums.FormaPago;
import com.business.unknow.enums.MetodosPago;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.rules.Constants.PaymentsSuite;
import java.util.List;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(
    name = PaymentsSuite.CREDIT_PAYMENT_VALIDATION_RULE,
    description = PaymentsSuite.CREDIT_PAYMENT_VALIDATION_RULE_DESC)
public class CreditPaymentRule {

  @Condition
  public boolean condition(
      @Fact("payment") PagoDto currentPayment, @Fact("facturas") List<FacturaCustom> facturas) {

    for (FacturaCustom facturaDto : facturas) {
      if (MetodosPago.PPD.name().equals(facturaDto.getMetodoPago())
          && FormaPago.CREDITO.name().equals(currentPayment.getFormaPago())) {
        return true;
      }
    }
    return false;
  }

  @Action
  public void execute(@Fact("results") List<String> results) {
    results.add(PaymentsSuite.CREDIT_PAYMENT_VALIDATION_RULE_DESC);
  }
}
