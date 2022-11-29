package com.business.unknow.services.entities;

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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "SOLICITUD_SOPORTE")
public class SupportRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "FOLIO")
  private Integer folio;

  @Column(name = "EMAIL_CONTACTO")
  private String contactEmail;

  @Column(name = "NOMBRE_CONTACTO")
  private String contactName;

  @Column(name = "TELEFONO_CONTACTO")
  private String contactPhone;

  @Column(name = "MODULO")
  private String module;

  @Column(name = "ESTATUS")
  private String status;

  @Column(name = "AGENTE")
  private String agent;

  @Column(name = "TIPO_SOPORTE")
  private String supportType;

  @Column(name = "NIVEL_SOPORTE")
  private String supportLevel;

  @Column(name = "PROBLEMA")
  private String problem;

  @Column(name = "MENSAJE_ERROR")
  private String errorMessage;

  @Column(name = "SOLUCION")
  private String solution;

  @Column(name = "NOTAS")
  private String notes;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "FECHA_LIMITE")
  private Date dueDate;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date creation;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date update;
}
