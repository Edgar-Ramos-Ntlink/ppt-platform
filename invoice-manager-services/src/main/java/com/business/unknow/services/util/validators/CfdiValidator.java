package com.business.unknow.services.util.validators;

import com.business.unknow.enums.MetodosPago;
import com.business.unknow.enums.TipoComprobante;
import com.business.unknow.model.error.InvoiceManagerException;
import com.unknown.cfdi.modelos.Cfdi;
import com.unknown.cfdi.modelos.Concepto;
import org.springframework.http.HttpStatus;

public class CfdiValidator extends Validator {

  public static void validate(Cfdi cfdi) throws InvoiceManagerException {

    checkNotNull(cfdi.getEmisor(), "Emisor info");
    checkNotNull(cfdi.getEmisor().getRfc(), "RFC Emisor");
    checkNotNull(cfdi.getEmisor().getNombre(), "Razon social emisor");
    checkNotEmpty(cfdi.getEmisor().getNombre(), "Razon social emisor");
    checkNotNull(cfdi.getEmisor().getRegimenFiscal(), "Regimen fiscal emisor");

    checkNotNull(cfdi.getReceptor(), "Receptor info");
    checkNotNull(cfdi.getReceptor().getRfc(), "RFC receptor");
    checkNotNull(cfdi.getReceptor().getNombre(), "Razon social receptor");
    checkNotEmpty(cfdi.getReceptor().getNombre(), "Razon social receptor");
    checkNotNull(cfdi.getReceptor().getUsoCfdi(), "Uso CFDI receptor");

    checkNotEquals(cfdi.getReceptor().getUsoCfdi(), "*");

    if (TipoComprobante.INGRESO.getValor().equals(cfdi.getTipoDeComprobante())) {
      checkNotEquals(cfdi.getFormaPago(), "*");
      if (!cfdi.getMetodoPago().equals(MetodosPago.PPD.name())
          && !cfdi.getMetodoPago().equals(MetodosPago.PUE.name())) {
        throw new InvoiceManagerException(
            "El metodo de pago de la factura solo puede ser PUE o PPD",
            "Metodo de pago invalido",
            HttpStatus.CONFLICT.value());
      }
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
