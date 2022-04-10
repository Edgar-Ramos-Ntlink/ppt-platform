package com.business.unknow.model.cfdi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "CfdiRelacionado", namespace = "http://www.sat.gob.mx/cfd/3")
@XmlAccessorType(XmlAccessType.FIELD)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CfdiRelacionado {
  @XmlAttribute(name = "UUID")
  private String uuid;
}
