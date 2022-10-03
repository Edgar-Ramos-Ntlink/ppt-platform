package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.FacturaPdf;
import com.business.unknow.services.entities.Factura40;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface FacturaMapper {

  @Mapping(target = "version", source = "version", defaultValue = "4.0")
  FacturaCustom getFacturaDtoFromEntity(Factura40 entity);

  List<FacturaCustom> getFacturaDtosFromEntities(List<Factura40> entities);

  Factura40 getEntityFromFacturaCustom(FacturaCustom dto);

  @Mapping(target = "cfdi", ignore = true)
  FacturaPdf getFacturaPdfFromFacturaCustom(FacturaCustom facturaCustom);
}
