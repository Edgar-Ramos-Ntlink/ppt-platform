package com.business.unknow.model.cfdi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlRootElement(
    name = "TimbreFiscalDigital",
    namespace = "http://www.sat.gob.mx/TimbreFiscalDigital")
@XmlAccessorType(XmlAccessType.NONE)
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
public class TimbreFiscalDigital {
  @XmlAttribute(name = "Version")
  private String version;

  @XmlAttribute(name = "UUID")
  private String uuid;

  @XmlAttribute(name = "FechaTimbrado")
  private String fechaTimbrado;

  @XmlAttribute(name = "RfcProvCertif")
  private String rfcProvCertif;

  @XmlAttribute(name = "SelloCFD")
  private String selloCFD;

  @XmlAttribute(name = "NoCertificadoSAT")
  private String noCertificadoSAT;

  @XmlAttribute(name = "SelloSAT")
  private String SelloSAT;
}
