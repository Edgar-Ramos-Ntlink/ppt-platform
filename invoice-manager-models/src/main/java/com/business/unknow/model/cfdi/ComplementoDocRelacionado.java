package com.business.unknow.model.cfdi;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "DoctoRelacionado", namespace = "http://www.sat.gob.mx/Pagos")
@XmlAccessorType(XmlAccessType.FIELD)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ComplementoDocRelacionado {

  @XmlAttribute(name = "Folio")
  private String folio;

  @XmlAttribute(name = "IdDocumento")
  private String idDocumento;

  @XmlAttribute(name = "ImpPagado")
  private String impPagado;

  @XmlAttribute(name = "ImpSaldoAnt")
  private String impSaldoAnt;

  @XmlAttribute(name = "ImpSaldoInsoluto")
  private String impSaldoInsoluto;

  @XmlAttribute(name = "MetodoDePagoDR")
  private String metodoDePagoDR;

  @XmlAttribute(name = "MonedaDR")
  private String monedaDR;

  @XmlAttribute(name = "NumParcialidad")
  private int numParcialidad;

  @XmlAttribute(name = "TipoCambioDR")
  private BigDecimal tipoCambioDR;

  @XmlAttribute(name = "Serie")
  private String serie;
}
