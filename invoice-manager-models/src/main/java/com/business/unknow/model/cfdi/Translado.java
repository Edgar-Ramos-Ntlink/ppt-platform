package com.business.unknow.model.cfdi;

import java.math.BigDecimal;
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

@XmlRootElement(name = "Traslado", namespace = "http://www.sat.gob.mx/cfd/3")
@XmlAccessorType(XmlAccessType.FIELD)
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
public class Translado {

  @XmlAttribute(name = "Base")
  private BigDecimal base;

  @XmlAttribute(name = "Impuesto")
  private String impuesto;

  @XmlAttribute(name = "TipoFactor")
  private String tipoFactor;

  @XmlAttribute(name = "TasaOCuota")
  private String tasaOCuota;

  @XmlAttribute(name = "Importe")
  private BigDecimal importe;

  public Translado(String impuesto, String tipoFactor, String tasaOCuota, BigDecimal importe) {
    this.impuesto = impuesto;
    this.tipoFactor = tipoFactor;
    this.tasaOCuota = tasaOCuota;
    this.importe = importe;
  }
}
