package com.business.unknow.model.dto;

import com.unknown.cfdi.modelos.Cfdi;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "FacturaPdfModel")
@XmlAccessorType(XmlAccessType.NONE)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FacturaPdfModelDto implements Serializable {

  private static final long serialVersionUID = -5413904708136266306L;

  @XmlElement(name = "Qr")
  private String qr;

  @XmlElement(name = "CadenaOriginal")
  private String cadenaOriginal;

  @XmlElement(name = "folioPadre")
  private String folioPadre;

  @XmlElement(name = "TotalDesc")
  private String totalDesc;

  @XmlElement(name = "SubTotalDesc")
  private String subTotalDesc;

  @XmlElement(name = "UsoCfdiDesc")
  private String usoCfdiDesc;

  @XmlElement(name = "RegimenFiscalDesc")
  private String regimenFiscalDesc;

  @XmlElement(name = "FormaPagoDesc")
  private String formaPagoDesc;

  @XmlElement(name = "MetodoPagoDesc")
  private String metodoPagoDesc;

  @XmlElement(name = "DireccionEmisor")
  private String direccionEmisor;

  @XmlElement(name = "DireccionReceptor")
  private String direccionReceptor;

  @XmlElement(name = "TipoDeComprobanteDesc")
  private String tipoDeComprobanteDesc;

  @XmlElement(name = "Logotipo")
  private String logotipo;

  @XmlElement(name = "factura")
  private Cfdi factura;

  @XmlElement(name = "montoTotalDesc")
  private String montoTotalDesc;

  @XmlElement(name = "montoTotal")
  private BigDecimal montoTotal;

  @XmlElement(name = "TipoRelacion")
  private String tipoRelacion;

  @XmlElement(name = "Relacion")
  private String relacion;

  @XmlElement(name = "UUID")
  private String uuid;

  public FacturaPdfModelDto(String qr, String logotipo, Cfdi factura) {
    super();
    this.qr = qr;
    this.logotipo = logotipo;
    this.factura = factura;
  }
}
