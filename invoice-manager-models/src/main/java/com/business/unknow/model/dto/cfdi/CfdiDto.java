package com.business.unknow.model.dto.cfdi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Getter
@Setter
@ToString
public class CfdiDto implements Serializable {

  private static final long serialVersionUID = -303198243726456894L;
  private Integer id;
  private String version;
  private String serie;
  private String folio;
  private String sello;
  private String noCertificado;
  private String certificado;
  private String moneda;
  private BigDecimal tipoCambio;
  private BigDecimal impuestosTrasladados;
  private BigDecimal impuestosRetenidos;
  private BigDecimal subtotal;
  private BigDecimal descuento;
  private BigDecimal total;
  private String tipoDeComprobante;
  private String metodoPago;
  private String formaPago;
  private String condicionesDePago;
  private String lugarExpedicion;
  private List<ConceptoDto> conceptos;
  private ComplementoDto complemento;
  private RelacionadoDto relacionado;
  private EmisorDto emisor;
  private ReceptorDto receptor;
}
