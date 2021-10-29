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

  public String getRfc() {
    return rfc;
  }

  public void setRfc(String rfc) {
    this.rfc = rfc;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getUsoCfdi() {
    return usoCfdi;
  }

  public void setUsoCfdi(String usoCfdi) {
    this.usoCfdi = usoCfdi;
  }

  public String getDireccion() {
    return direccion;
  }

  public void setDireccion(String direccion) {
    this.direccion = direccion;
  }

  public Cfdi getCfdi() {
    return cfdi;
  }

  public void setCfdi(Cfdi cfdi) {
    this.cfdi = cfdi;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Receptor [rfc=" + rfc + ", nombre=" + nombre + ", usoCfdi=" + usoCfdi + "]";
  }
}
