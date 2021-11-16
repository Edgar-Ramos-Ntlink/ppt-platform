package com.business.unknow.enums;

import com.business.unknow.model.error.InvoiceManagerException;

public enum S3BucketsEnum {
  CFDIS,
  EMPRESAS,
  PAGOS,
  CLIENTES,
  CUENTAS_BANCARIAS,
  NOT_VALID;

  public static S3BucketsEnum findByValor(String valor) throws InvoiceManagerException {
    for (S3BucketsEnum v : values()) {
      if (v.name().equals(valor)) {
        return v;
      }
    }
    throw new InvoiceManagerException(
        String.format("El tipo de recurso [ %s ] no existe en el catalogo de la appliación", valor),
        400);
  }
}
