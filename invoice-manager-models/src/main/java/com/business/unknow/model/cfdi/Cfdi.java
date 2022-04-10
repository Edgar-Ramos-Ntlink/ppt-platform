package com.business.unknow.model.cfdi;

import com.business.unknow.Constants.CfdiConstants;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@XmlRootElement(name = "Comprobante", namespace = "http://www.sat.gob.mx/cfd/3")
@XmlAccessorType(XmlAccessType.NONE)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor()
@Setter
@Getter
public class Cfdi {

  @XmlAttribute(name = "Version")
  private String version = CfdiConstants.FACTURA_VERSION;

  @XmlAttribute(name = "Serie")
  private String serie;

  @XmlAttribute(name = "Folio")
  private String folio;

  @XmlAttribute(name = "Fecha")
  private String fecha;

  @XmlAttribute(name = "Sello")
  private String sello;

  @XmlAttribute(name = "FormaPago")
  private String formaPago;

  @XmlAttribute(name = "NoCertificado")
  private String noCertificado;

  @XmlAttribute(name = "Certificado")
  private String certificado;

  @XmlAttribute(name = "SubTotal")
  private BigDecimal subtotal;

  @XmlAttribute(name = "Descuento")
  private BigDecimal descuento;

  @XmlAttribute(name = "TipoCambio")
  private BigDecimal tipoCambio;

  @XmlAttribute(name = "Moneda")
  private String moneda;

  @XmlAttribute(name = "Total")
  private BigDecimal total;

  @XmlAttribute(name = "TipoDeComprobante")
  private String tipoDeComprobante;

  @XmlAttribute(name = "MetodoPago")
  private String metodoPago;

  @XmlAttribute(name = "LugarExpedicion")
  private String lugarExpedicion;

  @XmlElement(name = "CfdiRelacionados", namespace = "http://www.sat.gob.mx/cfd/3")
  private CfdiRelacionados cfdiRelacionados;

  @XmlElement(name = "Emisor", namespace = "http://www.sat.gob.mx/cfd/3")
  private Emisor emisor;

  @XmlElement(name = "Receptor", namespace = "http://www.sat.gob.mx/cfd/3")
  private Receptor receptor;

  @XmlElementWrapper(name = "Conceptos", namespace = "http://www.sat.gob.mx/cfd/3")
  @XmlElement(name = "Concepto", namespace = "http://www.sat.gob.mx/cfd/3")
  private List<Concepto> conceptos;

  @XmlElement(name = "Impuestos", namespace = "http://www.sat.gob.mx/cfd/3")
  private Impuesto impuestos;

  @XmlElement(name = "Complemento", namespace = "http://www.sat.gob.mx/cfd/3")
  private Complemento complemento;

  public Cfdi() {
    this.conceptos = new ArrayList<>();
    this.impuestos = new Impuesto();
  }
}
