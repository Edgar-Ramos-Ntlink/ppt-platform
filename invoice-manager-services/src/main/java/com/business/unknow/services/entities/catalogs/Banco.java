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
@Table(name = "CAT_BANCOS")
public class Banco implements Serializable {

  private static final long serialVersionUID = 5619170839532161430L;

  @Id
  @Column(name = "ID_BANCO")
  private String id;

  @Column(name = "NOMBRE")
  private String nombre;
}
