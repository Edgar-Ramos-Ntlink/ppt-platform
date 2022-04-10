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
@Table(name = "CFDI_EMISORES")
public class Emisor implements Serializable {

  private static final long serialVersionUID = 6696596495844299658L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_CFDI_EMISOR")
  private int id;

  @Column(name = "RFC")
  private String rfc;

  @Column(name = "NOMBRE")
  private String nombre;

  @Column(name = "REGIMEN_FISCAL")
  private String regimenFiscal;

  @Column(name = "DIRECCION")
  private String direccion;

  @OneToOne
  @JoinColumn(name = "ID_CFDI")
  private Cfdi cfdi;
}
