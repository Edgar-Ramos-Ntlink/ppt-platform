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
@Table(name = "CAT_FORMA_PAGO")
public class FormaPago {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "CLAVE")
  private String id;

  @Column(name = "DESCRIPCION")
  private String descripcion;

  @Column(name = "SHORT_DESC")
  private String shortDescripcion;
}
