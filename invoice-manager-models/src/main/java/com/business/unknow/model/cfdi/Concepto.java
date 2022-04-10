package com.business.unknow.model.cfdi;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "Concepto", namespace = "http://www.sat.gob.mx/cfd/3")
@XmlAccessorType(XmlAccessType.FIELD)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
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
}
