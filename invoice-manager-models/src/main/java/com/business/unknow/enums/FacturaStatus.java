package com.business.unknow.enums;

public enum FacturaStatus {
  VALIDACION_OPERACIONES(1, "Validacion operaciones"),
  VALIDACION_TESORERIA(2, "Validacion tesoreria"),
  TIMBRADA(3, "Timbrada"),
  POR_TIMBRAR(4, "Por Timbrar"),
  RECHAZO_TESORERIA(5, "Rechazo tesoreria"),
  RECHAZO_OPERACIONES(6, "Rechazo Operaciones"),
  CANCELADA(7, "Cancelada"),
  POR_TIMBRAR_CONTABILIDAD(8, "Por Timbrar Contabilidad");

  private Integer valor;
  private String descripcion;

  FacturaStatus(Integer valor, String descripcion) {
    this.valor = valor;
    this.descripcion = descripcion;
  }

  public Integer getValor() {
    return valor;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public static FacturaStatus getStatusByValue(Integer value) {
    for (FacturaStatus status : values()) {
      if (status.getValor().equals(value)) {
        return status;
      }
    }
    return FacturaStatus.VALIDACION_OPERACIONES;
  }
}
