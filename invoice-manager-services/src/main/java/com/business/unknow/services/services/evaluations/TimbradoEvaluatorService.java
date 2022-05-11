package com.business.unknow.services.services.evaluations;

import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.util.ValidationRules;
import java.util.ArrayList;
import java.util.List;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TimbradoEvaluatorService {

  @Autowired
  @Qualifier("stampSuite")
  private Rules stampSuite;

  @Autowired
  @Qualifier("cancelSuite")
  private Rules cancelSuite;

  @Autowired private RulesEngine rulesEngine;

  public void invoiceCancelValidation(FacturaCustom facturaCustom) throws InvoiceManagerException {
    Facts facts = new Facts();
    List<String> results = new ArrayList<>();
    facts.put("facturaCustom", facturaCustom);
    facts.put("results", results);
    rulesEngine.fire(cancelSuite, facts);
    ValidationRules.validateRulesResponse(results);
  }

  public void facturaTimbradoValidation(FacturaCustom facturaCustom, List<PagoDto> pagos)
      throws InvoiceManagerException {
    Facts facts = new Facts();
    List<String> results = new ArrayList<>();
    facts.put("FacturaCustom", facturaCustom);
    facts.put("pagos", pagos);
    facts.put("results", results);
    rulesEngine.fire(stampSuite, facts);
    ValidationRules.validateRulesResponse(results);
  }
}
