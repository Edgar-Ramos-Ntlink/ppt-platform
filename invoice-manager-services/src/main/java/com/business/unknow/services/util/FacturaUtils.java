package com.business.unknow.services.util;

import com.business.unknow.Constants;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FacturaUtils {

  private static final String EXP = "&";

  public static String generatePreFolio(Integer amount) {
    return String.format(
        "%s-%s",
        LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern(Constants.DATE_PRE_FOLIO_GENERIC_FORMAT)),
        String.format("%05d", amount + 1));
  }

  public static String generateFolio() {
    return LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern(Constants.DATE_FOLIO_GENERIC_FORMAT));
  }
}
