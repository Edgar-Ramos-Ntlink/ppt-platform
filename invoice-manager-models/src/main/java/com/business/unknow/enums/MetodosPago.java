package com.business.unknow.enums;

public enum MetodosPago {
  PUE("PUE", "PAGO EN UNA SOLA EXHIBICION"),
  PPD("PDD", "PAGO POR DEFINIR");

  private String clave;

  private String descripcion;

  MetodosPago(String clave, String descripcion) {
    this.clave = clave;
    this.descripcion = descripcion;
  }

  public String getClave() {
    return clave;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public static MetodosPago findByValor(String clave) {
    for (MetodosPago v : values()) {
      if (v.getClave().equals(clave)) {
        return v;
      }
    }
    return PUE;
  }
}
