package com.business.unknow.services.mapper.factura;

import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.services.entities.factura.Factura;
import com.business.unknow.services.mapper.IgnoreUnmappedMapperConfig;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface FacturaMapper {

  FacturaDto getFacturaDtoFromEntity(Factura entity);

  List<FacturaDto> getFacturaDtosFromEntities(List<Factura> entities);

  Factura getEntityFromFacturaDto(FacturaDto dto);
}
