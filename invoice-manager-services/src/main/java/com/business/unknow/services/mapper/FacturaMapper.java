package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.FacturaPdf;
import com.business.unknow.services.entities.Factura;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface FacturaMapper {

  FacturaCustom getFacturaDtoFromEntity(Factura entity);

  List<FacturaCustom> getFacturaDtosFromEntities(List<Factura> entities);

  Factura getEntityFromFacturaCustom(FacturaCustom dto);

  @Mapping(target = "cfdi", ignore = true)
  FacturaPdf getFacturaPdfFromFacturaCustom(FacturaCustom facturaCustom);
}
