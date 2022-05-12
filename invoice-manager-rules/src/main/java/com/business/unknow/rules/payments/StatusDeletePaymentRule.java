package com.business.unknow.rules.payments;

import com.business.unknow.enums.FacturaStatus;
import com.business.unknow.enums.MetodosPago;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.rules.Constants.DeletePagoSuite;
import java.util.List;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(
    name = DeletePagoSuite.DELETE_STATUS_PAYMENT_RULE,
    description = DeletePagoSuite.DELETE_STATUS_PAYMENT_RULE_DESC)
public class StatusDeletePaymentRule {

  @Condition
  public boolean condition(
      @Fact("payment") PagoDto payment, @Fact("facturas") List<FacturaCustom> facturas) {

    for (FacturaCustom invoice : facturas) {
      if (MetodosPago.PUE.getClave().equals(invoice.getMetodoPago())
          && (FacturaStatus.TIMBRADA.getValor().equals(invoice.getStatusFactura())
              || FacturaStatus.CANCELADA.getValor().equals(invoice.getStatusFactura()))) {
        return true;
      }
    }
    return false;
  }

  @Action
  public void execute(@Fact("results") List<String> results) {
    results.add(DeletePagoSuite.DELETE_STATUS_PAYMENT_RULE_DESC);
  }
}
