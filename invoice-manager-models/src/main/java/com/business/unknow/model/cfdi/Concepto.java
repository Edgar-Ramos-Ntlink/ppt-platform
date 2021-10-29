package com.business.unknow.model.cfdi;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Concepto", namespace = "http://www.sat.gob.mx/cfd/3")
@XmlAccessorType(XmlAccessType.FIELD)
public class Concepto {

  @XmlAttribute(name = "ClaveProdServ")
  private String claveProdServ;

  @XmlAttribute(name = "NoIdentificacion")
  private String noIdentificacion;

  @XmlAttribute(name = "Cantidad")
  private BigDecimal cantidad;

  @XmlAttribute(name = "ClaveUnidad")
  private String claveUnidad;

  @XmlAttribute(name = "Unidad")
  private String unidad;

  @XmlAttribute(name = "Descripcion")
  private String descripcion;

  @XmlAttribute(name = "ValorUnitario")
  private BigDecimal valorUnitario;

  @XmlAttribute(name = "Importe")
  private BigDecimal importe;

  @XmlAttribute(name = "Descuento")
  private BigDecimal descuento;

  @XmlElement(name = "Impuestos", namespace = "http://www.sat.gob.mx/cfd/3")
  private ConceptoImpuesto impuestos;

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

  public String getClaveUnidad() {
    return claveUnidad;
  }

  public void setClaveUnidad(String claveUnidad) {
    this.claveUnidad = claveUnidad;
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

  public ConceptoImpuesto getImpuestos() {
    return impuestos;
  }

  public void setImpuestos(ConceptoImpuesto impuestos) {
    this.impuestos = impuestos;
  }

  public BigDecimal getCantidad() {
    return cantidad;
  }

  public void setCantidad(BigDecimal cantidad) {
    this.cantidad = cantidad;
  }

  @Override
  public String toString() {
    return "Concepto [claveProdServ="
        + claveProdServ
        + ", noIdentificacion="
        + noIdentificacion
        + ", cantidad="
        + cantidad
        + ", claveUnidad="
        + claveUnidad
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
        + "]";
  }
}
