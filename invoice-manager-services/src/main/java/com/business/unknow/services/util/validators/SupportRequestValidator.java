package com.business.unknow.services.util.validators;

import com.business.unknow.model.dto.services.SupportRequestDto;
import com.business.unknow.model.error.InvoiceManagerException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.http.HttpStatus;

public class SupportRequestValidator extends Validator {

  public static void validateSupportRequest(SupportRequestDto dto) throws InvoiceManagerException {

    checkNotNull(dto.getContactName(), "El nombre del contacto es requerido");
    checkNotEmpty(dto.getContactName(), "El nombre del contacto es requerido");
    checkValidString(dto.getContactName());
    checkNotNull(dto.getContactEmail(), "email");
    checkNotEmpty(dto.getContactEmail(), "email");
    checkValidEmail(dto.getContactEmail());
    checkNotNull(dto.getProblem(), "Descripción problema");
    checkNotEmpty(dto.getProblem(), "Descripción problema");
    checkValidString(dto.getProblem());
    checkNotNull(dto.getModule(), "Modulo");
    checkNotEmpty(dto.getModule(), "Modulo");

    checkNotNull(dto.getStatus(), "Estatus");
    checkNotEmpty(dto.getStatus(), "Estatus");

    checkNotNull(dto.getAgent(), "Agente");
    checkNotEmpty(dto.getAgent(), "Agente");
    checkValidEmail(dto.getAgent());
    if (dto.getSupportLevel().equals("*")) {
      throw new InvoiceManagerException(
          "El nivel de soporte es req uerido", HttpStatus.BAD_REQUEST.value());
    }
  }

  public static void assignDefaults(SupportRequestDto dto) {
    if (Objects.isNull(dto.getDueDate())) {
      dto.setDueDate(Date.valueOf(LocalDate.now().plusDays(1)));
    }
    if (Objects.isNull(dto.getSupportLevel()) || "*".equals(dto.getSupportLevel())) {
      dto.setSupportLevel("primer nivel");
    }
    if (Objects.isNull(dto.getSupportType()) || "*".equals(dto.getSupportType())) {
      dto.setSupportType("plataforma");
    }
    if (Objects.isNull(dto.getStatus()) || "*".equals(dto.getStatus())) {
      dto.setStatus("PENDIENTE");
    }
  }
}
