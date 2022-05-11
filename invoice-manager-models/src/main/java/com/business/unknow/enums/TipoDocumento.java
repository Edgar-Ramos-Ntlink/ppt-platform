package com.business.unknow.enums;

public enum TipoDocumento {
  FACTURA("Factura"),
  COMPLEMENTO("Complemento"),
  PREGUNTAR("PREGUNTAR"),
  NOTA_CREDITO("NotaDeCredito"),
  NOT_VALID("NOT_VALID");

  private String descripcion;

  TipoDocumento(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public static TipoDocumento findByDesc(String nombre) {
    for (TipoDocumento v : values()) {
      if (v.getDescripcion().equals(nombre)) {
        return v;
      }
    }
    return NOT_VALID;
  }
}
