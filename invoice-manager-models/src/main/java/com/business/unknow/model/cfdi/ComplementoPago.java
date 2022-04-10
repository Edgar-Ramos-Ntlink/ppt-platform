package com.business.unknow.model.cfdi;

import java.math.BigDecimal;
import java.util.List;
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

@XmlRootElement(name = "Pago", namespace = "http://www.sat.gob.mx/Pagos")
@XmlAccessorType(XmlAccessType.FIELD)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ComplementoPago {

  @XmlAttribute(name = "FechaPago")
  private String fechaPago;

  @XmlAttribute(name = "FormaDePagoP")
  private String formaDePago;

  @XmlAttribute(name = "FormaDePagoDesc")
  private String formaDePagoDesc;

  @XmlAttribute(name = "MonedaP")
  private String moneda;

  @XmlAttribute(name = "Monto")
  private String monto;

  @XmlAttribute(name = "TipoCambioP")
  private BigDecimal tipoCambioP;

  public BigDecimal getTipoCambioP() {
    return tipoCambioP;
  }

  @XmlElement(name = "DoctoRelacionado", namespace = "http://www.sat.gob.mx/Pagos")
  private List<ComplementoDocRelacionado> complementoDocRelacionado;
}
