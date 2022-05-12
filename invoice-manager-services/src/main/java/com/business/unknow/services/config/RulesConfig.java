package com.business.unknow.services.config;

import com.business.unknow.rules.factura.CancelStatusValidationRule;
import com.business.unknow.rules.factura.EmisorValidationRule;
import com.business.unknow.rules.factura.ValidacionFacturaComplementoRule;
import com.business.unknow.rules.factura.ValidacionFacturaPpdRule;
import com.business.unknow.rules.factura.ValidacionFacturaPueRule;
import com.business.unknow.rules.factura.ValidacionNotaCreditoRule;
import com.business.unknow.rules.payments.ConflicPaymentRuleValidation;
import com.business.unknow.rules.payments.CreditPaymentRule;
import com.business.unknow.rules.payments.PaymentAmountValidationRule;
import com.business.unknow.rules.payments.PaymentInvoiceStatusRule;
import com.business.unknow.rules.payments.PaymentOrderValidationRule;
import com.business.unknow.rules.payments.ZeroAmmountValidationRule;
import com.business.unknow.rules.timbrado.FacturaDatosValidationRule;
import com.business.unknow.rules.timbrado.FacturaPagoValidationRule;
import com.business.unknow.rules.timbrado.FacturaStatusRule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RulesConfig {

  @Bean("deletePagoSuite")
  public Rules getDeletePagoSuite() {
    Rules rules = new Rules();
    rules.register(new CancelStatusValidationRule());
    return rules;
  }

  @Bean("invoiceSuite")
  public Rules getInvoiceSuite() {
    Rules rules = new Rules();
    rules.register(new EmisorValidationRule());
    return rules;
  }

  @Bean("paymentUpdateSuite")
  public Rules getPaymentUpdateSuite() {
    Rules rules = new Rules();
    rules.register(new ConflicPaymentRuleValidation());
    rules.register(new PaymentOrderValidationRule());
    rules.register(new PaymentInvoiceStatusRule());
    return rules;
  }

  @Bean("creationSuite")
  public Rules getCreationSuite() {
    Rules rules = new Rules();
    rules.register(new PaymentAmountValidationRule());
    rules.register(new ZeroAmmountValidationRule());
    rules.register(new PaymentInvoiceStatusRule());
    rules.register(new CreditPaymentRule());
    return rules;
  }

  @Bean("invoiceValidationSuite")
  public Rules getInvoiceValidationSuite() {
    Rules rules = new Rules();
    rules.register(new ValidacionFacturaComplementoRule());
    rules.register(new ValidacionFacturaPpdRule());
    rules.register(new ValidacionFacturaPueRule());
    rules.register(new ValidacionNotaCreditoRule());
    return rules;
  }

  @Bean("stampSuite")
  public Rules getStampSuite() {
    Rules rules = new Rules();
    rules.register(new FacturaStatusRule());
    rules.register(new FacturaDatosValidationRule());
    rules.register(new FacturaPagoValidationRule());
    return rules;
  }

  @Bean("cancelSuite")
  public Rules getCancelSuite() {
    Rules rules = new Rules();
    rules.register(new CancelStatusValidationRule());
    return rules;
  }

  @Bean
  public RulesEngine getRulesEngine() {
    return new DefaultRulesEngine();
  }
}
