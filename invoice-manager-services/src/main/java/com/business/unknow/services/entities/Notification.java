package com.business.unknow.services.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.*;
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
@Table(name = "NOTIFICACIONES")
public class Notification implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private int id;

  @Column(name = "EVENT")
  private String event;

  @Column(name = "EMAILS")
  private String emails;

  @Column(name = "RESUME")
  private String resume;

  @Column(name = "NOTIFICATION")
  private String notification;

  @Column(name = "ACTIVE")
  private Boolean active;

  @Column(name = "UPDATED_BY")
  private String updatedBy;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date fechaActualizacion;
}
