package com.business.unknow.builder;

import com.business.unknow.model.dto.cfdi.ConceptoDto;
import java.math.BigDecimal;

public class ConceptoDtoBuilder extends AbstractBuilder<ConceptoDto> {

  public ConceptoDtoBuilder() {
    super(new ConceptoDto());
  }

  public ConceptoDtoBuilder setCantidad(BigDecimal cantidad) {
    instance.setCantidad(cantidad);
    return this;
  }

  public ConceptoDtoBuilder setClaveProdServ(String claveProdServ) {
    instance.setClaveProdServ(claveProdServ);
    return this;
  }

  public ConceptoDtoBuilder setClaveUnidad(String claveUnidad) {
    instance.setClaveUnidad(claveUnidad);
    return this;
  }

  public ConceptoDtoBuilder setDescripcionCUPS(String descripcionCUPS) {
    instance.setDescripcionCUPS(descripcionCUPS);
    return this;
  }

  public ConceptoDtoBuilder setDescripcion(String descripcion) {
    instance.setDescripcion(descripcion);
    return this;
  }

  public ConceptoDtoBuilder setImporte(BigDecimal importe) {
    instance.setImporte(importe);
    return this;
  }

  public ConceptoDtoBuilder setValorUnitario(BigDecimal valorUnitario) {
    instance.setValorUnitario(valorUnitario);
    return this;
  }

  public ConceptoDtoBuilder setDescuento(BigDecimal descuento) {
    instance.setDescuento(descuento);
    return this;
  }
}
