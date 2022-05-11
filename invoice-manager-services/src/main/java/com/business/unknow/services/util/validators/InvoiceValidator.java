package com.business.unknow.services.util.validators;

import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.error.InvoiceManagerException;
import org.apache.http.HttpStatus;

public class InvoiceValidator extends Validator {

  /**
   * Validates Invoice data
   *
   * @param {@link FacturaDto}
   * @param folio
   * @throws {@link InvoiceManagerException}
   */
  public static void validate(FacturaCustom facturaDto, String folio)
      throws InvoiceManagerException {
    checkNotNull(facturaDto.getFolio(), "folio ");
    if (!folio.equals(facturaDto.getFolio())) {
      throw new InvoiceManagerException(
          "Error en folio:Los folios son diferentes", HttpStatus.SC_BAD_REQUEST);
    }
    checkNotNull(facturaDto.getRfcEmisor(), "Rfc Emisor");
    checkNotNull(facturaDto.getRazonSocialEmisor(), "Razon Social Emisor");
    checkNotNull(facturaDto.getRfcRemitente(), "Rfc Remitente");
    checkNotNull(facturaDto.getRazonSocialRemitente(), "Razon Social Remitente");
    checkNotNull(facturaDto.getCfdi(), "cfdi");
    checkNotNull(facturaDto.getCfdi().getReceptor(), "Receptor Info");
    checkNotNull(facturaDto.getCfdi().getEmisor(), "Emisor Info");
    checkNotNull(facturaDto.getCfdi().getReceptor().getRfc(), "RFC receptor");
    checkNotNull(facturaDto.getCfdi().getEmisor().getRfc(), "RFC Emisor");
  }
}
