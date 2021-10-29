package com.business.unknow.services.util.helpers;

import com.business.unknow.model.error.InvoiceCommonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

public class StringHelper {

  private ObjectMapper objectMapper = new ObjectMapper();

  public String readStringFromFile(String path, String filename) throws InvoiceCommonException {
    try {
      InputStream is =
          Thread.currentThread()
              .getContextClassLoader()
              .getResourceAsStream(String.format("menus/%s.json", filename));
      if (is != null) {

        return objectMapper.readValue(is, String.class);
      } else {
        throw new InvoiceCommonException(String.format("Error reading the file %s", filename));
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new InvoiceCommonException(String.format("Error reading the file %s", e.getMessage()));
    }
  }

  public String getFileFormatFromBase64(String base64String) throws InvoiceCommonException {
    String[] a = base64String.split(",");
    if (a.length > 1) {
      String[] b = a[0].split("/");
      if (b.length > 1) {
        String[] c = b[1].split(";");
        if (c.length > 1) {
          return ".".concat(c[0]);
        }
      }
    }
    throw new InvoiceCommonException("El archivo no tiene un formato Asociado");
  }

  public String getFileDataFromBase64(String base64String) throws InvoiceCommonException {
    String[] a = base64String.split(",");
    if (a.length > 1) {
      return a[1];
    }
    throw new InvoiceCommonException("El archivo no tiene informacion asociada");
  }
}
