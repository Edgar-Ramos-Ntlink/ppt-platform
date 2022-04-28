package com.business.unknow.services.mapper.factura;

import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.services.entities.factura.Factura;
import com.business.unknow.services.mapper.IgnoreUnmappedMapperConfig;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface FacturaMapper {

  FacturaCustom getFacturaDtoFromEntity(Factura entity);

  List<FacturaCustom> getFacturaDtosFromEntities(List<Factura> entities);

  Factura getEntityFromFacturaCustom(FacturaCustom dto);
}
