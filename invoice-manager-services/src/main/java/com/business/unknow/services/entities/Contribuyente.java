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
@Table(name = "CONTRIBUYENTES")
public class Contribuyente implements Serializable {

  private static final long serialVersionUID = -262866475157657093L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_CONTRIBUYENTE")
  private Integer id;

  @Basic(optional = false)
  @Column(name = "RFC")
  private String rfc;

  @Column(name = "GIRO")
  private String giro;

  @Column(name = "MORAL")
  private boolean moral;

  @Column(name = "NOMBRE")
  private String nombre;

  @Column(name = "CURP")
  private String curp;

  @Column(name = "RAZON_SOCIAL")
  private String razonSocial;

  @Column(name = "CALLE")
  private String calle;

  @Column(name = "NO_EXTERIOR")
  private String noExterior;

  @Column(name = "NO_INTERIOR")
  private String noInterior;

  @Column(name = "MUNICIPIO")
  private String municipio;

  @Column(name = "LOCALIDAD")
  private String localidad;

  @Column(name = "ESTADO")
  private String estado;

  @Column(name = "PAIS")
  private String pais;

  @Column(name = "COO")
  private String coo;

  @Column(name = "CODIGO_POSTAL")
  private String cp;

  @Basic(optional = false)
  @Column(name = "CORREO")
  private String correo;

  @Column(name = "TELEFONO")
  private String telefono;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date fechaActualizacion;
}
