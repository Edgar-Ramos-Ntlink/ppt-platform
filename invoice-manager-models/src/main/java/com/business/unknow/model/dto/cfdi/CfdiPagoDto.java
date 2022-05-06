package com.business.unknow.model.dto.cfdi;

import com.mx.ntlink.cfdi.modelos.Cfdi;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@XmlRootElement(name = "CfdiPagoDto")
@Builder
@Getter
@Setter
@ToString
public class CfdiPagoDto implements Serializable {

  private static final long serialVersionUID = -7042373656454531924L;
  private int id;
  private String version;

  @XmlAttribute(name = "ComplentoFechaPago")
  private Date fechaPago;

  @XmlAttribute(name = "ComplentoFormaPago")
  private String formaPago;

  @XmlAttribute(name = "ComplentoMoneda")
  private String moneda;

  @XmlAttribute(name = "ComplentoMonto")
  private BigDecimal monto;

  @XmlAttribute(name = "ComplentoFolio")
  private String folio;

  @XmlAttribute(name = "ComplentoIdDocumento")
  private String idDocumento;

  @XmlAttribute(name = "ComplentoImportePagado")
  private BigDecimal importePagado;

  @XmlAttribute(name = "ComplentoImporteSaldoAnterior")
  private BigDecimal importeSaldoAnterior;

  @XmlAttribute(name = "ComplentoImporteSaldoInsoluto")
  private BigDecimal importeSaldoInsoluto;

  @XmlAttribute(name = "ComplentoMetodoPago")
  private String metodoPago;

  private String monedaDr;

  @XmlAttribute(name = "ComplentoNumeroParcialidad")
  private int numeroParcialidad;

  @XmlAttribute(name = "Serie")
  private String serie;

  private String montoDesc;
  private boolean valido;
  private Cfdi cfdi;
  private BigDecimal tipoCambioDr;
  private BigDecimal tipoCambio;
}
