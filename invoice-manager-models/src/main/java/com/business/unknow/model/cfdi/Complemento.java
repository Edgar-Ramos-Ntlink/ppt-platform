package com.business.unknow.model.cfdi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "Complemento", namespace = "http://www.sat.gob.mx/cfd/3")
@XmlAccessorType(XmlAccessType.NONE)
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
public class Complemento {

  @XmlElement(name = "Pagos", namespace = "http://www.sat.gob.mx/Pagos")
  private ComplementoPagos complementoPago;

  @XmlElement(name = "TimbreFiscalDigital", namespace = "http://www.sat.gob.mx/TimbreFiscalDigital")
  private TimbreFiscalDigital timbreFiscalDigital;

  public Complemento() {
    complementoPago = new ComplementoPagos();
  }
}
