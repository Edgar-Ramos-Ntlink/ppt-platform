package com.business.unknow.model.cfdi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@XmlRootElement(name = "Emisor", namespace = "http://www.sat.gob.mx/cfd/3")
@XmlAccessorType(XmlAccessType.FIELD)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
public class Emisor {

  @XmlAttribute(name = "Rfc")
  private String rfc;

  @XmlAttribute(name = "Nombre")
  private String nombre;

  @XmlAttribute(name = "RegimenFiscal")
  private String regimenFiscal;
}
