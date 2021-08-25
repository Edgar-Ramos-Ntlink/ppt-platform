package com.business.unknow.rules.suites.facturas;

import com.business.unknow.rules.factura.ValidacionFacturaComplementoRule;
import com.business.unknow.rules.factura.ValidacionFacturaPpdRule;
import com.business.unknow.rules.factura.ValidacionFacturaPueRule;
import com.business.unknow.rules.factura.ValidacionNotaCreditoRule;
import com.business.unknow.rules.suites.InvoiceManagerSuite;
import org.jeasy.rules.api.Rules;

public class FacturaValidationSuite implements InvoiceManagerSuite {

  private Rules rules = new Rules();

  public FacturaValidationSuite() {
    rules.register(new ValidacionFacturaComplementoRule());
    rules.register(new ValidacionFacturaPueRule());
    rules.register(new ValidacionFacturaPpdRule());
    rules.register(new ValidacionNotaCreditoRule());
  }

  @Override
  public Rules getSuite() {
    return rules;
  }
}
