package com.business.unknow.services.util.validators;

import com.business.unknow.Constants;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.error.InvoiceManagerException;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FacturaValidator extends AbstractValidator {

  public void validatePostFactura(FacturaCustom dto) throws InvoiceManagerException {
    checkNotNull(dto.getRfcEmisor(), "Rfc Emisor");
    checkNotNull(dto.getRazonSocialEmisor(), "Razon Social Emisor");
    checkNotNull(dto.getRfcRemitente(), "Rfc Remitente");
    checkNotNull(dto.getRazonSocialRemitente(), "Razon Social Remitente");
  }

  public void validatePostComplementoDto(FacturaCustom dto, String folio)
      throws InvoiceManagerException {
    checkNotNull(dto.getRfcEmisor(), "Rfc Emisor");
    checkNotNull(dto.getRazonSocialEmisor(), "Razon Social Emisor");
    checkNotNull(dto.getRfcRemitente(), "Rfc Remitente");
    checkNotNull(dto.getRazonSocialRemitente(), "Razon Social Remitente");
  }

  public void validateTimbrado(FacturaCustom dto, String folio) throws InvoiceManagerException {
    checkNotNull(dto.getFolio(), "folio ");
    if (!folio.equals(dto.getFolio())) {
      throw new InvoiceManagerException(
          "Error en folio", "Los folios son diferentes", Constants.BAD_REQUEST);
    }
  }

  public void validatePostFacturaWithDetail(FacturaCustom dto) throws InvoiceManagerException {
    checkNotNull(dto.getRfcEmisor(), "Rfc Emisor");
    checkNotNull(dto.getRazonSocialEmisor(), "Razon Social Emisor");
    checkNotNull(dto.getRfcRemitente(), "Rfc Remitente");
    checkNotNull(dto.getRazonSocialRemitente(), "Razon Social Remitente");
    checkNotNull(dto.getCfdi(), "cfdi");
    checkNotNull(dto.getCfdi().getReceptor(), "Receptor Info");
    checkNotNull(dto.getCfdi().getEmisor(), "Emisor Info");
    checkNotNull(dto.getCfdi().getReceptor().getRfc(), "RFC receptor");
    checkNotNull(dto.getCfdi().getEmisor().getRfc(), "RFC Emisor");
  }

  public void validate(FacturaCustom dto, String folio) throws InvoiceManagerException {
    checkNotNull(dto.getFolio(), "folio ");
    if (!folio.equals(dto.getFolio())) {
      throw new InvoiceManagerException(
          "Error en folio:Los folios son diferentes", HttpStatus.SC_BAD_REQUEST);
    }
    validatePostFacturaWithDetail(dto);
  }
}
