package com.business.unknow.rules.suites.payments;

import com.business.unknow.rules.payments.StatusDeletePaymentRule;
import com.business.unknow.rules.suites.InvoiceManagerSuite;
import org.jeasy.rules.api.Rules;

public class DeletePagoSuite implements InvoiceManagerSuite {

  private Rules rules = new Rules();

  public DeletePagoSuite() {
    rules.register(new StatusDeletePaymentRule());
  }

  @Override
  public Rules getSuite() {
    return rules;
  }
}
