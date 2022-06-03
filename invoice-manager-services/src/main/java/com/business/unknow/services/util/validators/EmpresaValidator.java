package com.business.unknow.services.util.validators;

import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.dto.services.EmpresaIngresosDto;
import com.business.unknow.model.error.InvoiceManagerException;
import org.springframework.stereotype.Component;

@Component("EmpresaValidator")
public class EmpresaValidator extends Validator {

  /*
   *
   * TODO validate not null only next values
   *
     private Boolean activo;
  private String tipo;
  private Integer giro;
  private String regimenFiscal;


  private String rfc;
  private String nombre;
  private String razonSocial;

  private String calle;
  private String noExterior;
  private String noInterior;
  private String municipio;
  private String estado;
  private String pais;
  private String cp;
   * */
  public void validatePostEmpresa(EmpresaDto dto) throws InvoiceManagerException {
    checkNotNull(dto.getRfc(), "Rfc");
    checkNotNull(dto.getRazonSocial(), "Razon social");
    checkValidString(dto.getRazonSocial());
    checkNotNull(dto.getNombre(), "Nombre corto");
    checkNotEmpty(dto.getNombre(), "Nombre corto");
    checkValidString(dto.getNombre());
    checkNotEmpty(dto.getRazonSocial(), "Razon social");
    checkNotNull(dto.getRegimenFiscal(), "Regimen fiscal");
    checkNotNull(dto.getPais(), "Pais");
    checkNotEmpty(dto.getPais(), "Pais");
    checkNotNull(dto.getCp(), "Codigo postal");
    checkNotNull(dto.getMunicipio(), "Municipio");
    checkNotNull(dto.getEstado(), "Estado");
    checkNotNull(dto.getCalle(), "Calle");
    checkNotNull(dto.getGiro(), "Giro");
    checkNotNull(dto.getTipo(), "Tipo");
    checkNotEquals(dto.getTipo(), "*");
    checkNotNull(dto.isActivo(), "Activo");
    checkNotNull(dto.getTipo(), "Linea");
    checkNotEquals(dto.getTipo(), "*");
  }

  public void validateDatoAnual(EmpresaIngresosDto dto) throws InvoiceManagerException {
    checkNotNull(dto.getRfc(), "RFC");
    checkNotNull(dto.getAnio(), "Año");
    checkNotNull(dto.getCreador(), "Usuario creador");
    checkNotNull(dto.getDetalle(), "Detalle dato anual");
    checkNotNull(dto.getTipoDato(), "Tipo dato");
  }
}
