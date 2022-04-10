package com.business.unknow.services.entities.cfdi;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@ToString
@Entity
@Table(name = "CFDI_RECEPTORES")
public class Receptor implements Serializable {

  private static final long serialVersionUID = 4815637463468040210L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_CFDI_RECEPTOR")
  private int id;

  @Column(name = "RFC")
  private String rfc;

  @Column(name = "NOMBRE")
  private String nombre;

  @Column(name = "USO_CFDI")
  private String usoCfdi;

  @Column(name = "DIRECCION")
  private String direccion;

  @OneToOne
  @JoinColumn(name = "ID_CFDI", referencedColumnName = "ID_CFDI")
  private Cfdi cfdi;
}
