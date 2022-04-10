/** */
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
import javax.validation.constraints.NotNull;
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

/**
 * @author ralfdemoledor
 *     <p>Las devoluciones solo pueden ser creadas para facturas pagadas y timbradas, de otro modo
 *     no es posible crear una devolucion
 */
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "DEVOLUCIONES")
public class Devolucion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_DEVOLUCION")
  private Integer id;

  /*
   * Este ID es llenado al momento de hacer el pago de la devolucion, el diseño
   * permite que un pago pueda ligar multiples devoluciones.
   */
  @Column(name = "ID_PAGO")
  private Integer idPagoOrigen;

  /*
   * Folio de complemento si es PPD o folio factura si es PUE
   */
  @NotNull
  @Column(name = "TIPO")
  private String tipo;

  @Column(name = "FOLIO")
  private String folio;

  @Column(name = "PAGO_MONTO")
  private BigDecimal pagoMonto;

  @Column(name = "IMPUESTO")
  private BigDecimal impuesto;

  @Column(name = "PORCENTAJE")
  private BigDecimal porcentaje;

  @NotNull
  @Column(name = "MONTO")
  private BigDecimal monto;

  /*
   * Entidad que recibe pago,en este caso es e correo de promotor,RFC cliente,
   * correo contacto y CASA
   */
  @NotNull
  @Column(name = "ID_RECEPTOR")
  private String receptor;

  /*
   * CLIENTE,PROMOTOR,CONTACTO,CASA
   */
  @NotNull
  @Column(name = "TIPO_RECEPTOR")
  private String tipoReceptor;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date fechaActualizacion;
}
