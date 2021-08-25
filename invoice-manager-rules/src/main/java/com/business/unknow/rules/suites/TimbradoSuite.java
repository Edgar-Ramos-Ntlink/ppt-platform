package com.business.unknow.rules.suites;

import com.business.unknow.rules.timbrado.FacturaDatosValidationRule;
import com.business.unknow.rules.timbrado.FacturaPagoValidationRule;
import com.business.unknow.rules.timbrado.FacturaStatusRule;
import org.jeasy.rules.api.Rules;

public class TimbradoSuite implements InvoiceManagerSuite {

  private Rules rules = new Rules();

  public TimbradoSuite() {
    rules.register(new FacturaStatusRule());
    rules.register(new FacturaDatosValidationRule());
    rules.register(new FacturaPagoValidationRule());
  }

  @Override
  public Rules getSuite() {
    return rules;
  }
}
