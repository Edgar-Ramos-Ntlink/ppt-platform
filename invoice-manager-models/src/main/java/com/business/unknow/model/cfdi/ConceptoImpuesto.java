package com.business.unknow.model.cfdi;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "Impuestos", namespace = "http://www.sat.gob.mx/cfd/3")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"translados", "retenciones"})
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ConceptoImpuesto {

  @XmlElementWrapper(name = "Retenciones", namespace = "http://www.sat.gob.mx/cfd/3")
  @XmlElement(name = "Retencion", namespace = "http://www.sat.gob.mx/cfd/3")
  private List<Retencion> retenciones;

  @XmlElementWrapper(name = "Traslados", namespace = "http://www.sat.gob.mx/cfd/3")
  @XmlElement(name = "Traslado", namespace = "http://www.sat.gob.mx/cfd/3")
  private List<Translado> translados;
}
