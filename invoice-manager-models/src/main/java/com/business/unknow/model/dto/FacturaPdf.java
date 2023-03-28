package com.business.unknow.model.dto;

import com.unknown.models.generated.Comprobante;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "FacturaPdf")
@XmlAccessorType(XmlAccessType.NONE)
public class FacturaPdf {

  @XmlElement(name = "Qr")
  private String qr;

  @XmlElement(name = "CadenaOriginal")
  private String cadenaOriginal;

  @XmlElement(name = "folioPadre")
  private String folioPadre;

  @XmlElement(name = "TotalDesc")
  private String totalDesc;

  @XmlElement(name = "Total")
  private String total;

  @XmlElement(name = "SubTotalDesc")
  private String subTotalDesc;

  @XmlElement(name = "UsoCfdiDesc")
  private String usoCfdiDesc;

  @XmlElement(name = "RegimenFiscalDesc")
  private String regimenFiscalDesc;

  @XmlElement(name = "RegimenFiscalReceptorDesc")
  private String regimenFiscalReceptorDesc;

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
  private Comprobante cfdi;

  @XmlElement(name = "TipoRelacion")
  private String tipoRelacion;

  @XmlElement(name = "Relacion")
  private String relacion;

  @XmlElement(name = "UUID")
  private String uuid;

  @XmlElement(name = "SelloSat")
  private String selloSat;
}
