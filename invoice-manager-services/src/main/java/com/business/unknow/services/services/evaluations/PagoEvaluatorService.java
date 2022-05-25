package com.business.unknow.services.services.evaluations;

import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.error.InvoiceManagerException;
import java.util.ArrayList;
import java.util.List;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PagoEvaluatorService {

  @Autowired
  @Qualifier("creationSuite")
  private Rules creationSuite;

  @Autowired
  @Qualifier("deletePagoSuite")
  private Rules deletePagoSuite;

  @Autowired
  @Qualifier("paymentUpdateSuite")
  private Rules paymentUpdateSuite;

  @Autowired protected RulesEngine rulesEngine;

  public void deletepaymentValidation(PagoDto payment, List<FacturaCustom> facturas)
      throws InvoiceManagerException {
    Facts facts = new Facts();
    List<String> results = new ArrayList<>();
    facts.put("payment", payment);
    facts.put("facturas", facturas);
    facts.put("results", results);

    rulesEngine.fire(deletePagoSuite, facts);
    if (!results.isEmpty()) {
      throw new InvoiceManagerException(
          results.toString(),
          "Some payment update rules was triggered.",
          HttpStatus.CONFLICT.value());
    }
  }

  public void validatePaymentCreation(PagoDto currentPayment, List<FacturaCustom> facturas)
      throws InvoiceManagerException {
    Facts facts = new Facts();
    List<String> results = new ArrayList<>();
    facts.put("payment", currentPayment);
    facts.put("facturas", facturas);
    facts.put("results", results);

    rulesEngine.fire(creationSuite, facts);
    if (!results.isEmpty()) {
      throw new InvoiceManagerException(
          results.toString(),
          "Una o varias reglas de creacion de pagos fue ejecutada.",
          HttpStatus.CONFLICT.value());
    }
  }

  public void validatePaymentUpdate(
      PagoDto currentPayment, PagoDto dbPayment, List<FacturaCustom> facturas)
      throws InvoiceManagerException {
    Facts facts = new Facts();
    List<String> results = new ArrayList<>();
    facts.put("payment", currentPayment);
    facts.put("dbPayment", dbPayment);
    facts.put("facturas", facturas);
    facts.put("results", results);

    rulesEngine.fire(paymentUpdateSuite, facts);
    if (!results.isEmpty()) {
      throw new InvoiceManagerException(
          results.toString(),
          "Alguna regla de actualizacion de pagos fue ejecutada.",
          HttpStatus.CONFLICT.value());
    }
  }
}
