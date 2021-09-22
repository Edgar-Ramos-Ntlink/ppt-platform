package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.services.EmpresaDetallesDto;
import com.business.unknow.services.entities.EmpresaDetalles;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface EmpresaDetallesMapper {

  EmpresaDetallesDto getDtoFromEntity(EmpresaDetalles entity);

  EmpresaDetalles getEntityFromDto(EmpresaDetallesDto dto);

  List<EmpresaDetallesDto> getDtosFromEntities(List<EmpresaDetalles> entities);
}
