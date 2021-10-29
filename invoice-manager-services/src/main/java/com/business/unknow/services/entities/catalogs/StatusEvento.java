package com.business.unknow.services.entities.catalogs;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CAT_STATUS_EVENTO")
public class StatusEvento implements Serializable {

  private static final long serialVersionUID = -3976447705621634661L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_STATUS_EVENTO")
  private Integer id;

  @Column(name = "VALUE")
  private String nombre;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  @Override
  public String toString() {
    return "StatusEvento [id=" + id + ", nombre=" + nombre + "]";
  }
}
