package com.business.unknow.services.entities.catalogs;

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
@Table(name = "CAT_GIROS")
public class Giro {

  @Id
  @Column(name = "ID_GIRO")
  private Integer id;

  @Column(name = "NOMBRE")
  private String nombre;
}
