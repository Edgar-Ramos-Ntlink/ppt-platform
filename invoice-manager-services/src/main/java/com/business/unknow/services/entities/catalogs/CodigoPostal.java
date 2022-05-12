package com.business.unknow.services.entities.catalogs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "CAT_CODIGO_POSTAL")
public class CodigoPostal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private int id;

  @Column(name = "CODIGO_POSTAL")
  private String codigoPostal;

  @Column(name = "ESTADO")
  private String estado;

  @Column(name = "MUNICIPIO")
  private String municipio;

  @Column(name = "CIUDAD")
  private String ciudad;

  @Column(name = "COLONIA")
  private String colonia;
}
