package com.business.unknow.model.cfdi;

import com.business.unknow.Constants.CfdiConstants;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "Pagos", namespace = "http://www.sat.gob.mx/Pagos")
@XmlAccessorType(XmlAccessType.NONE)
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
public class ComplementoPagos {

  @XmlAttribute(name = "Version")
  private String version = CfdiConstants.FACTURA_COMPLEMENTO_VERSION;

  @XmlElement(name = "Pago", namespace = "http://www.sat.gob.mx/Pagos")
  private List<ComplementoPago> complementoPagos;

  public ComplementoPagos() {
    this.complementoPagos = new ArrayList<>();
  }
}
