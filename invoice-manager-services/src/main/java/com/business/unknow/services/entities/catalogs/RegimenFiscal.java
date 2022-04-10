package com.business.unknow.services.entities.catalogs;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "CAT_REGIMEN_FISCAL")
public class RegimenFiscal {

  @Id
  @Column(name = "CLAVE")
  private Integer clave;

  @Column(name = "DESCRIPCION")
  private String descripcion;

  @Column(name = "P_FISICA")
  private boolean pFisica;

  @Column(name = "P_MORAL")
  private boolean pMoral;

  @Column(name = "INICIO_VIGENCIA")
  private Date inicioVigencia;
}
