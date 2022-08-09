package com.business.unknow.services.entities;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "FACTURAS40")
public class Factura40 {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_FACTURA")
  private Integer id;

  @Column(name = "VERSION")
  private String version;

  @Column(name = "RFC_EMISOR")
  private String rfcEmisor;

  @Column(name = "RFC_REMITENTE")
  private String rfcRemitente;

  @Column(name = "RAZON_SOCIAL_EMISOR")
  private String razonSocialEmisor;

  @Column(name = "LINEA_EMISOR")
  private String lineaEmisor;

  @Column(name = "RAZON_SOCIAL_REMITENTE")
  private String razonSocialRemitente;

  @Column(name = "LINEA_REMITENTE")
  private String lineaRemitente;

  @Column(name = "TIPO_DOCUMENTO")
  private String tipoDocumento;

  @Column(name = "SOLICITANTE")
  private String solicitante;

  @Column(name = "FOLIO")
  private String folio;

  @Column(name = "PRE_FOLIO")
  private String preFolio;

  @Column(name = "METODO_PAGO")
  private String metodoPago;

  @Column(name = "STATUS_FACTURA")
  private Integer statusFactura;

  @Column(name = "STATUS_DETAIL")
  private String statusDetail;

  @Column(name = "UUID")
  private String uuid;

  @Column(name = "MOVITO_CANCELACION")
  private String motivo;

  @Column(name = "FOLIO_SUSTITUTO")
  private String folioSustituto;

  @Column(name = "TOTAL")
  private BigDecimal total;

  @Column(name = "SALDO_PENDIENTE")
  private BigDecimal saldoPendiente;

  @Column(name = "PACK_FACTURACION")
  private String packFacturacion;

  @Column(name = "NOTAS")
  private String notas;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date fechaActualizacion;

  @Column(name = "FECHA_TIMBRADO")
  private Date fechaTimbrado;

  @Column(name = "FECHA_CANCELADO")
  private Date fechaCancelacion;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  @Column(name = "CADENA_ORIGINAL_TIMBRADO")
  private String cadenaOriginalTimbrado;

  @Column(name = "SELLO_CFD")
  private String selloCfd;

  @Column(name = "ID_CFDI")
  private Integer idCfdi;

  @Column(name = "FOLIO_RELACIONADO")
  private String folioRelacionado;

  @Column(name = "FOLIO_RELACIONADO_PADRE")
  private String folioRelacionadoPadre;

  @Column(name = "VALIDACION_TESO", columnDefinition = "TINYINT")
  private Boolean validacionTeso;

  @Column(name = "VALIDACION_OPER", columnDefinition = "TINYINT")
  private Boolean validacionOper;
}
