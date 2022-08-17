package com.business.unknow.services.entities;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "PAGO_FACTURAS")
public class PagoFactura {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Integer id;

  @Column(name = "ID_CFDI")
  private Integer idCfdi;

  @Column(name = "FOLIO")
  private String folio;

  @Column(name = "FOLIO_REFERENCIA")
  private String folioReferencia;

  @Column(name = "RFC_EMISOR")
  private String rfcEmisor;

  @Column(name = "RFC_RECEPTOR")
  private String rfcReceptor;

  @Column(name = "SOLICITANTE")
  private String solicitante;

  @Column(name = "MONTO")
  private BigDecimal monto;

  @Column(name = "TOTAL_FACTURA")
  private BigDecimal totalFactura;

  @Column(name = "ACREDOR")
  private String acredor;

  @Column(name = "DEUDOR")
  private String deudor;

  @Column(name = "METODO_PAGO")
  private String metodoPago;

  @Column(name = "FOLIO_DEVOLUCION")
  private Integer folioDevolucion;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date fechaActualizacion;

  @ManyToOne
  @JoinColumn(name = "ID_PAGO", nullable = false)
  private Pago pago;
}
