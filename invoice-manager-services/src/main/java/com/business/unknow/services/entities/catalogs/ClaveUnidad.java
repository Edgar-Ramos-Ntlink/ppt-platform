package com.business.unknow.services.entities.catalogs;

import java.io.Serializable;
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
@Table(name = "CAT_CLAVE_UNIDAD")
public class ClaveUnidad implements Serializable {

  private static final long serialVersionUID = 1578386921742161974L;

  @Id
  @Column(name = "CLAVE")
  private String clave;

  @Column(name = "TIPO")
  private String tipo;

  @Column(name = "DESCRIPCION")
  private String descripcion;

  @Column(name = "NOMBRE")
  private String nombre;
}
