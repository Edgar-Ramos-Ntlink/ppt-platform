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
@Table(name = "CFDI_RELACIONADO")
public class Relacionado implements Serializable {

  private static final long serialVersionUID = -3975983872415170489L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_CFDI_RELACIONADO")
  private int id;

  @Column(name = "TIPO_RELACION")
  private String tipoRelacion;

  @Column(name = "RELACION")
  private String relacion;

  @OneToOne
  @JoinColumn(name = "ID_CFDI")
  private Cfdi cfdi;
}
