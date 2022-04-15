package com.business.unknow.services.services.evaluations;

import com.business.unknow.enums.MetodosPagoEnum;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.util.validators.AbstractValidator;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import com.mx.ntlink.cfdi.modelos.Concepto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service("CfdiValidator")
public class CfdiValidator extends AbstractValidator {

  public void validateCfdi(Cfdi cfdi) throws InvoiceManagerException {

    checkNotNull(cfdi.getEmisor(), "Emisor info");
    checkNotNull(cfdi.getEmisor().getRfc(), "RFC Emisor");
    checkNotNull(cfdi.getEmisor().getNombre(), "Razon social emisor");
    checkNotEmpty(cfdi.getEmisor().getNombre(), "Razon social emisor");
    checkNotNull(cfdi.getEmisor().getDireccion(), "Dirección emisor");
    checkNotEmpty(cfdi.getEmisor().getDireccion(), "Dirección emisor");
    checkNotNull(cfdi.getEmisor().getRegimenFiscal(), "Regimen fiscal emisor");

    checkNotNull(cfdi.getReceptor(), "Receptor info");
    checkNotNull(cfdi.getReceptor().getRfc(), "RFC receptor");
    checkNotNull(cfdi.getReceptor().getNombre(), "Razon social receptor");
    checkNotEmpty(cfdi.getReceptor().getNombre(), "Razon social receptor");
    checkNotNull(cfdi.getReceptor().getDireccion(), "Dirección receptor");
    checkNotEmpty(cfdi.getReceptor().getDireccion(), "Dirección receptor");
    checkNotNull(cfdi.getReceptor().getUsoCfdi(), "Uso CFDI receptor");

    checkNotEquals(cfdi.getReceptor().getUsoCfdi(), "*");
    checkNotEquals(cfdi.getFormaPago(), "*");

    if (!cfdi.getMetodoPago().equals(MetodosPagoEnum.PPD.name())
        && !cfdi.getMetodoPago().equals(MetodosPagoEnum.PUE.name())) {
      throw new InvoiceManagerException(
          "El metodo de pago de la factura solo puede ser PUE o PPD",
          "Metodo de pago invalido",
          HttpStatus.CONFLICT.value());
    }

    if (cfdi.getConceptos().isEmpty()) {
      throw new InvoiceManagerException(
          "El CFDI no puede tener 0 conceptos",
          "Numero de comceptos invalido",
          HttpStatus.CONFLICT.value());
    } else {
      for (Concepto conceptoDto : cfdi.getConceptos()) {
        checkNotNull(conceptoDto.getDescripcion(), "Descripción de concepto");
        checkNotEmpty(conceptoDto.getDescripcion(), "Descripción de concepto");
        checkNotNull(conceptoDto.getCantidad(), "cantidad concepto");
        checkNotNull(conceptoDto.getClaveProdServ(), "clave producto servicio ");
        checkNotNegative(conceptoDto.getImporte(), "Importe");
        checkNotNegative(conceptoDto.getCantidad(), "Cantidad");
        checkNotNegative(conceptoDto.getValorUnitario(), "valor unitario");
      }
    }
  }
}
