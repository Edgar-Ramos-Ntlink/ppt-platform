package com.business.unknow.services.config;

import com.business.unknow.rules.suites.facturas.CancelacionSuite;
import com.business.unknow.rules.suites.facturas.FacturaSuite;
import com.business.unknow.rules.suites.facturas.FacturaValidationSuite;
import com.business.unknow.rules.suites.payments.DeletePagoSuite;
import com.business.unknow.rules.suites.payments.PaymentCreationSuite;
import com.business.unknow.rules.suites.payments.PaymentUpdateSuite;
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

  @Bean
  public DeletePagoSuite getDeletePagoSuite() {
    return new DeletePagoSuite();
  }

  @Bean
  public FacturaSuite getFacturaSuite() {
    return new FacturaSuite();
  }

  @Bean
  public PaymentUpdateSuite getPagoPueSuite() {
    return new PaymentUpdateSuite();
  }

  @Bean
  public PaymentCreationSuite getPagoPpdSuite() {
    return new PaymentCreationSuite();
  }

  @Bean
  public FacturaValidationSuite getFacturaValidationSuite() {
    return new FacturaValidationSuite();
  }

  @Bean
  public CancelacionSuite getCancelacionSuite() {
    return new CancelacionSuite();
  }

  @Bean("stampSuite")
  public Rules getStampSuite() {
    Rules rules = new Rules();
    rules.register(new FacturaStatusRule());
    rules.register(new FacturaDatosValidationRule());
    rules.register(new FacturaPagoValidationRule());
    return rules;
  }

  @Bean
  public RulesEngine getRulesEngine() {
    return new DefaultRulesEngine();
  }
}
