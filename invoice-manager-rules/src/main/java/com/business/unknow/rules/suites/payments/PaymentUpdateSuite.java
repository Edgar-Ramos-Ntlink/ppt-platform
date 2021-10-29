package com.business.unknow.rules.suites.payments;

import com.business.unknow.rules.payments.ConflicPaymentRuleValidation;
import com.business.unknow.rules.payments.DoubleValidatesPaymentRule;
import com.business.unknow.rules.payments.PaymentInvoiceStatusRule;
import com.business.unknow.rules.payments.PaymentOrderValidationRule;
import com.business.unknow.rules.suites.InvoiceManagerSuite;
import org.jeasy.rules.api.Rules;

public class PaymentUpdateSuite implements InvoiceManagerSuite {

  private Rules rules = new Rules();

  public PaymentUpdateSuite() {
    rules.register(new ConflicPaymentRuleValidation());
    rules.register(new DoubleValidatesPaymentRule());
    rules.register(new PaymentOrderValidationRule());
    rules.register(new PaymentInvoiceStatusRule());
  }

  @Override
  public Rules getSuite() {
    return rules;
  }
}
