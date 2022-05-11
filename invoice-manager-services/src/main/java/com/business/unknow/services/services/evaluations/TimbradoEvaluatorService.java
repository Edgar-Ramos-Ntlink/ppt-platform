package com.business.unknow.services.services.evaluations;

import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.rules.suites.facturas.CancelacionSuite;
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
public class TimbradoEvaluatorService extends AbstractEvaluatorService {

  @Autowired private CancelacionSuite cancelacionSuite;

  @Autowired
  @Qualifier("stampSuite")
  private Rules stampSuite;

  @Autowired private RulesEngine rulesEngine;

  public void facturaCancelacionValidation(FacturaContext facturaContext)
      throws InvoiceManagerException {
    Facts facts = new Facts();
    facts.put("facturaContext", facturaContext);
    rulesEngine.fire(cancelacionSuite.getSuite(), facts);
    validateFacturaContext(facturaContext);
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
