package com.business.unknow.services.services.evaluations;

import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.error.InvoiceManagerException;
import org.apache.http.HttpStatus;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractEvaluatorService {

  @Autowired protected RulesEngine rulesEngine;

  protected void validateFacturaContext(FacturaContext facturaContexrt)
      throws InvoiceManagerException {
    if (!facturaContexrt.isValid()) {
      throw new InvoiceManagerException(
          facturaContexrt.getRuleErrorDesc(),
          facturaContexrt.getSuiteError(),
          HttpStatus.SC_BAD_REQUEST);
    }
  }
}
