package com.business.unknow.enums;

public enum TipoRelacion {
  NOTA_CREDITO("01", "Nota de crédito de los documentos relacionados"),
  SUSTITUCION("04", "Sustitución de los CFDI previos"),
  NOT_VALID("99", "Not valid");

  private String id;
  private String valor;

  TipoRelacion(String id, String valor) {
    this.valor = valor;
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getValor() {
    return valor;
  }

  public void setValor(String valor) {
    this.valor = valor;
  }

  public static TipoRelacion findById(String id) {
    for (TipoRelacion v : values()) {
      if (v.getId().equals(id)) {
        return v;
      }
    }
    return NOT_VALID;
  }
}
