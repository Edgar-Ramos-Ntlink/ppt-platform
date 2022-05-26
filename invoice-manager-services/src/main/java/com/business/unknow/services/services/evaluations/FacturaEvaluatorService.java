package com.business.unknow.services.services.evaluations;

import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.services.EmpresaDto;
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
public class FacturaEvaluatorService {

  @Autowired
  @Qualifier("invoiceSuite")
  public Rules invoiceSuite;

  @Autowired
  @Qualifier("invoiceValidationSuite")
  public Rules invoiceValidationSuite;

  @Autowired private RulesEngine rulesEngine;

  public void facturaEvaluation(EmpresaDto empresaDto) throws InvoiceManagerException {
    Facts facts = new Facts();
    List<String> results = new ArrayList<>();
    facts.put("empresaDto", empresaDto);
    facts.put("results", results);
    rulesEngine.fire(invoiceSuite, facts);
    ValidationRules.validateRulesResponse(results);
  }

  public void facturaStatusValidation(FacturaCustom facturaCustom) {
    Facts facts = new Facts();
    facts.put("facturaCustom", facturaCustom);
    rulesEngine.fire(invoiceValidationSuite, facts);
  }
}
