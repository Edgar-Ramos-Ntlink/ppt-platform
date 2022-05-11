package com.business.unknow.services.util;

import com.business.unknow.model.error.InvoiceManagerException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.http.HttpStatus;

public class ValidationRules {
  public static void validateRulesResponse(List<String> results) throws InvoiceManagerException {
    if (!results.isEmpty()) {
      String errorMessage = results.stream().collect(Collectors.joining("-", "{", "}"));
      throw new InvoiceManagerException(errorMessage, HttpStatus.SC_BAD_REQUEST);
    }
  }
}
