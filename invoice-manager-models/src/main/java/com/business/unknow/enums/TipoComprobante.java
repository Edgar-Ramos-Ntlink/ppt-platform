package com.business.unknow.enums;

public enum TipoComprobante {
  INGRESO("I", "Ingreso"),
  EGRESO("E", "Egreso"),
  TRASLADO("T", "traslado"),
  PAGO("P", "Pago"),
  NOMINA("N", "Nomina");

  private String valor;
  private String descripcion;

  TipoComprobante(String valor, String descripcion) {
    this.valor = valor;
    this.descripcion = descripcion;
  }

  public String getValor() {
    return valor;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public static TipoComprobante findByValor(String valor) {
    for (TipoComprobante v : values()) {
      if (v.getValor().equals(valor)) {
        return v;
      }
    }
    return INGRESO;
  }
}
