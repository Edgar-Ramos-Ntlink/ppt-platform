package com.business.unknow.services.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
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

@ToString
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EMPRESA_DETALLES")
public class EmpresaDetalles implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private int id;

  @Basic(optional = false)
  @Column(name = "RFC")
  private String rfc;

  @Basic(optional = false)
  @Column(name = "NOTIFICANTE")
  private String notificante;

  @Basic(optional = false)
  @Column(name = "AREA")
  private String area;

  @Basic(optional = false)
  @Column(name = "TIPO")
  private String tipo;

  @Basic(optional = false)
  @Column(name = "RESUMEN")
  private String resumen;

  @Column(name = "DETALLE")
  private String detalle;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date fechaActualizacion;

  @ManyToOne
  @JoinColumn(name = "RFC", referencedColumnName = "RFC", insertable = false, updatable = false)
  private Empresa empresa;
}
