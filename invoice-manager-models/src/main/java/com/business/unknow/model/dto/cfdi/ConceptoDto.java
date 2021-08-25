package com.business.unknow.model.dto.cfdi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConceptoDto implements Serializable {

  private static final long serialVersionUID = 1690079459401358817L;
  private Integer id;
  private String claveProdServ;
  private String descripcionCUPS;
  private String noIdentificacion;
  private BigDecimal cantidad;
  private String claveUnidad;
  private String unidad;
  private String descripcion;
  private BigDecimal valorUnitario;
  private BigDecimal importe;
  private BigDecimal descuento;
  private List<ImpuestoDto> impuestos = new ArrayList<>();
  private List<RetencionDto> retenciones = new ArrayList<>();

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getClaveProdServ() {
    return claveProdServ;
  }

  public void setClaveProdServ(String claveProdServ) {
    this.claveProdServ = claveProdServ;
  }

  public String getNoIdentificacion() {
    return noIdentificacion;
  }

  public void setNoIdentificacion(String noIdentificacion) {
    this.noIdentificacion = noIdentificacion;
  }

  public BigDecimal getCantidad() {
    return cantidad;
  }

  public void setCantidad(BigDecimal cantidad) {
    this.cantidad = cantidad;
  }

  public String getClaveUnidad() {
    return claveUnidad;
  }

  public void setClaveUnidad(String claveUnidad) {
    this.claveUnidad = claveUnidad;
  }

  public String getDescripcionCUPS() {
    return descripcionCUPS;
  }

  public void setDescripcionCUPS(String descripcionCUPS) {
    this.descripcionCUPS = descripcionCUPS;
  }

  public String getUnidad() {
    return unidad;
  }

  public void setUnidad(String unidad) {
    this.unidad = unidad;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public BigDecimal getValorUnitario() {
    return valorUnitario;
  }

  public void setValorUnitario(BigDecimal valorUnitario) {
    this.valorUnitario = valorUnitario;
  }

  public BigDecimal getImporte() {
    return importe;
  }

  public void setImporte(BigDecimal importe) {
    this.importe = importe;
  }

  public BigDecimal getDescuento() {
    return descuento;
  }

  public void setDescuento(BigDecimal descuento) {
    this.descuento = descuento;
  }

  public List<ImpuestoDto> getImpuestos() {
    return impuestos;
  }

  public void setImpuestos(List<ImpuestoDto> impuestos) {
    this.impuestos = impuestos;
  }

  public List<RetencionDto> getRetenciones() {
    return retenciones;
  }

  public void setRetenciones(List<RetencionDto> retenciones) {
    this.retenciones = retenciones;
  }

  @Override
  public String toString() {
    return "ConceptoDto [id="
        + id
        + ", claveProdServ="
        + claveProdServ
        + ", noIdentificacion="
        + noIdentificacion
        + ", cantidad="
        + cantidad
        + ", claveUnidad="
        + claveUnidad
        + ", descripcionCUPS="
        + descripcionCUPS
        + ", unidad="
        + unidad
        + ", descripcion="
        + descripcion
        + ", valorUnitario="
        + valorUnitario
        + ", importe="
        + importe
        + ", descuento="
        + descuento
        + ", impuestos="
        + impuestos
        + ", retenciones="
        + retenciones
        + "]";
  }
}
