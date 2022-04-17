package com.business.unknow.model.dto;

import com.business.unknow.Constants;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FacturaDto implements Serializable {

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

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATETIME_FORMAT)
  private Date fechaCreacion;

  private String statusCancelacion;
  private Date fechaCancelacion;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATETIME_FORMAT)
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
  private Cfdi cfdi;
  private Boolean validacionTeso;
  private Boolean validacionOper;
}
