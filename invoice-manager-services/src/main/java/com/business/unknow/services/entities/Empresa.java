package com.business.unknow.services.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
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
@Table(name = "EMPRESAS")
public class Empresa implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_EMPRESA")
  private int id;

  @Column(name = "ACTIVO")
  private boolean activo;

  @Column(name = "OPERATIVA")
  private boolean operativa;

  @Column(name = "BLOCKED")
  private boolean bloqueada;

  @Column(name = "ESTATUS")
  private String estatus;

  @Column(name = "GIRO_ID")
  private Integer giro;

  @Column(name = "LINEA")
  private String tipo;

  @Column(name = "REGIMEN_FISCAL")
  private String regimenFiscal;

  @Column(name = "RFC")
  private String rfc;

  @Column(name = "NOMBRE")
  private String nombre;

  @Column(name = "RAZON_SOCIAL")
  private String razonSocial;

  @Column(name = "CALLE")
  private String calle;

  @Column(name = "NO_EXT")
  private String noExterior;

  @Column(name = "NO_INT")
  private String noInterior;

  @Column(name = "MUNICIPIO")
  private String municipio;

  @Column(name = "COLONIA")
  private String colonia;

  @Column(name = "ESTADO")
  private String estado;

  @Column(name = "PAIS")
  private String pais;

  @Column(name = "CODIGO_POSTAL")
  private String cp;

  @Column(name = "INICIO_ACTIVIDADES")
  private Date inicioActividades;

  @Column(name = "REGISTRO_PATRONAL")
  private String registroPatronal;

  @Column(name = "ESTATUS_JURIDICO1")
  private String estatusJuridico;

  @Column(name = "ESTATUS_JURIDICO2")
  private String estatusJuridico2;

  @Column(name = "REPRESENTANTE_LEGAL")
  private String representanteLegal;

  @Column(name = "CIEC")
  private String ciec;

  @Column(name = "FIEL")
  private String fiel;

  @Column(name = "ACTIVIDAD_SAT")
  private String actividadSAT;

  @Column(name = "WEB")
  private String web;

  @Email
  @Column(name = "CORREO")
  private String correo;

  @Column(name = "PW_CORREO")
  private String pwCorreo;

  @Column(name = "DOMINIO_CORREO")
  private String dominioCorreo;

  @Column(name = "PW_SAT")
  private String pwSat;

  @Column(name = "NO_CERTIFICADO")
  private String noCertificado;

  @Column(name = "EXPIRACION_CERTIFICADO")
  private Date expiracionCertificado;

  @Column(name = "EXPIRACION_FIEL")
  private Date expiracionFiel;

  @Column(name = "IMPUESTO_ESTATAL")
  private String impuestoEstatal;

  @Column(name = "ENT_REG_PATRONAL")
  private String entidadRegistroPatronal;

  @Column(name = "ENT_IMPUESTO_PATRONAL")
  private String entidadImpuestoPatronal;

  @Column(name = "CREADOR")
  private String creador;

  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @Column(name = "FECHA_CREACION")
  private Date fechaCreacion;

  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @Column(name = "FECHA_ACTUALIZACION")
  private Date fechaActualizacion;

  @OneToMany(mappedBy = "empresa")
  private List<CuentaBancaria> cuentas;

  @OneToMany(mappedBy = "empresa")
  private List<EmpresaDetalles> detalles;

  @OneToMany(mappedBy = "empresa")
  private List<EmpresaIngresos> ingresos;
}
