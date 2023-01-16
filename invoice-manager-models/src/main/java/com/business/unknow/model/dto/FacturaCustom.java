package com.business.unknow.model.dto;

import com.business.unknow.serializers.CustomDateTimeDeserializer;
import com.business.unknow.serializers.CustomDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.unknown.cfdi.modelos.Cfdi;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
  private String version;
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

  @JsonSerialize(using = CustomDateTimeSerializer.class)
  @JsonDeserialize(using = CustomDateTimeDeserializer.class)
  private Date fechaCreacion;

  @JsonSerialize(using = CustomDateTimeSerializer.class)
  @JsonDeserialize(using = CustomDateTimeDeserializer.class)
  private Date fechaActualizacion;

  private String statusCancelacion;

  @JsonSerialize(using = CustomDateTimeSerializer.class)
  @JsonDeserialize(using = CustomDateTimeDeserializer.class)
  private Date fechaCancelacion;

  @JsonSerialize(using = CustomDateTimeSerializer.class)
  @JsonDeserialize(using = CustomDateTimeDeserializer.class)
  private Date fechaTimbrado;

  private Integer statusCancelado;
  private String cadenaOriginalTimbrado;
  private String selloCfd;
  private Integer idCfdi;
  private String folioRelacionado;
  private String folioRelacionadoPadre;

  private BigDecimal total;
  private BigDecimal saldoPendiente;

  private Boolean validacionTeso;
  private Boolean validacionOper;

  // aditional factura Custom fields

  private String qr;
  private String cadenaOriginal;
  private String folioPadre;
  private String totalDesc;
  private String subTotalDesc;
  private String usoCfdiDesc;
  private String regimenFiscalDesc;
  private String regimenFiscalReceptorDesc;
  private String formaPagoDesc;
  private String metodoPagoDesc;
  private String direccionEmisor;
  private String direccionReceptor;
  private String tipoDeComprobanteDesc;
  private String logotipo;
  @JsonIgnore private String xml;
  @JsonIgnore private String acuse;
  private Cfdi cfdi;
  private String montoTotalDesc;
  private String tipoRelacion;
  private String relacion;
  private String selloSat;
  private @Builder.Default List<PagoComplemento> pagos = new ArrayList<>();
}
