package com.business.unknow.rules.suites.facturas;

import com.business.unknow.rules.factura.EmisorValidationRule;
import com.business.unknow.rules.suites.InvoiceManagerSuite;
import org.jeasy.rules.api.Rules;

public class FacturaSuite implements InvoiceManagerSuite {

  private Rules rules = new Rules();

  public FacturaSuite() {
    rules.register(new EmisorValidationRule());
  }

  @Override
  public Rules getSuite() {
    return rules;
  }
}
