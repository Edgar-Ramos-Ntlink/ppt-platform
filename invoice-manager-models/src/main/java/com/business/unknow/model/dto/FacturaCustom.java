package com.business.unknow.model.dto;

import static com.business.unknow.Constants.JSON_DAY_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FacturaCustom implements Serializable {

  private static final long serialVersionUID = -1019751668989298682L;
  private Integer id;
  private String rfcEmisor;
  private String rfcRemitente;
  private String razonSocialEmisor;
  private String lineaEmisor;
  private String razonSocialRemitente;
  private String lineaRemitente;
  private String tipoDocumento;
  private String solicitante;
  private String folio;
  private String preFolio;
  private String uuid;
  private Integer statusFactura;
  private String statusDetail;
  private String packFacturacion;
  private String metodoPago;
  private String notas;
  private String motivo;
  private String folioSustituto;
  private BigDecimal impuestosRetenidos;
  private BigDecimal impuestosTrasladados;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DAY_FORMAT)
  private Date fechaCreacion;

  private String statusCancelacion;
  private Date fechaCancelacion;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_DAY_FORMAT)
  private Date fechaActualizacion;

  private Date fechaTimbrado;
  private Integer statusCancelado;
  private String cadenaOriginalTimbrado;
  private String selloCfd;
  private Integer idCfdi;
  private Integer idCfdiRelacionado;
  private Integer idCfdiRelacionadoPadre;
  private BigDecimal total;
  private BigDecimal saldoPendiente;

  private Boolean validacionTeso;
  private Boolean validacionOper;

  // aditional factura Custom fields

  private String qr;
  private String cadenaOriginal;
  // TODO:VALIDAR SI ES NECESARIA
  private String folioPadre;
  private String totalDesc;
  private String subTotalDesc;
  private String usoCfdiDesc;
  private String regimenFiscalDesc;
  private String formaPagoDesc;
  private String metodoPagoDesc;
  private String direccionEmisor;
  private String direccionReceptor;
  private String tipoDeComprobanteDesc;
  private String logotipo;
  private String xml;
  private Cfdi cfdi;
  private String montoTotalDesc;
  private String tipoRelacion;
  private String relacion;
  private String selloSat;
}
