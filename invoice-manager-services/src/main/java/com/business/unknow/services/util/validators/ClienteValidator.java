package com.business.unknow.services.util.validators;

import com.business.unknow.Constants;
import com.business.unknow.model.dto.services.ClientDto;
import com.business.unknow.model.error.InvoiceManagerException;
import java.math.BigDecimal;

public class ClienteValidator extends Validator {

  public static void validate(ClientDto dto) throws InvoiceManagerException {
    checkNotNull(dto.getRazonSocial(), "Razon Social");
    checkNotEmpty(dto.getRazonSocial(), "Razon Social");
    checkValidString(dto.getRazonSocial());
    checkNotNull(dto.getEstado(), "Estado");
    checkNotEmpty(dto.getEstado(), "Estado");
    checkNotNull(dto.getCalle(), "calle");
    checkNotEmpty(dto.getCalle(), "calle");
    checkNotNull(dto.getCp(), "Codigo postal");
    checkNotEmpty(dto.getCp(), "Codigo postal");
    checkNotNull(dto.getLocalidad(), "Localidad");
    checkNotEmpty(dto.getLocalidad(), "Localidad");
    checkNotNull(dto.getMunicipio(), "Municipio");
    checkNotEmpty(dto.getMunicipio(), "Municipio");
    checkNotNull(dto.getRfc(), "RFC");
    checkNotEmpty(dto.getRfc(), "RFC");
    checkNotNull(dto.getCorreoContacto(), "Correo cliente");
    checkNotEmpty(dto.getCorreoContacto(), "Correo cliente");

    if (dto.getPorcentajeCliente().compareTo(BigDecimal.ZERO) < 0) {
      throw new InvoiceManagerException(
          "El porcentaje debe ser positivo",
          String.format(
              "El porcentaje %s debe ser positivo", dto.getPorcentajeCliente().toString()),
          Constants.BAD_REQUEST);
    }
    if (dto.getPorcentajeContacto().compareTo(BigDecimal.ZERO) < 0) {
      throw new InvoiceManagerException(
          "El porcentaje debe ser positivo",
          String.format(
              "El porcentaje %s debe ser positivo", dto.getPorcentajeContacto().toString()),
          Constants.BAD_REQUEST);
    }
    if (dto.getPorcentajeDespacho().compareTo(BigDecimal.ZERO) < 0) {
      throw new InvoiceManagerException(
          "El porcentaje debe ser positivo",
          String.format(
              "El porcentaje %s debe ser positivo", dto.getPorcentajeDespacho().toString()),
          Constants.BAD_REQUEST);
    }
    if (dto.getPorcentajePromotor().compareTo(BigDecimal.ZERO) < 0) {
      throw new InvoiceManagerException(
          "El porcentaje debe ser positivo",
          String.format(
              "El porcentaje %s debe ser positivo", dto.getPorcentajePromotor().toString()),
          Constants.BAD_REQUEST);
    }
    if (!dto.getCp().matches("^[0-9]{5}(?:-[0-9]{4})?$")) {
      throw new InvoiceManagerException(
          "El codigo postal es incorrecto:No debe llevar letras", Constants.BAD_REQUEST);
    }
    if (!dto.getRfc()
        .matches(
            "^[A-Z&Ñ]{3,4}[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{2}[0-9A]?$")) {
      throw new InvoiceManagerException(
          "RFC invalido, verifique que no contenga espacios o caracteres inválidos",
          Constants.BAD_REQUEST);
    }
    if (dto.getCorreoContacto() != null && !dto.getCorreoContacto().isEmpty()) {
      checkValidEmail(dto.getCorreoContacto());
    }
  }
}
