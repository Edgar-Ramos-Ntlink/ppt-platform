package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.services.entities.Empresa;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface EmpresaMapper {

  EmpresaDto getEmpresaDtoFromEntity(Empresa entity);

  List<EmpresaDto> getEmpresaDtosFromEntities(List<Empresa> entities);

  @Mapping(target = "cuentas", ignore = true)
  @Mapping(target = "detalles", ignore = true)
  Empresa getEntityFromEmpresaDto(EmpresaDto dto);

  List<Empresa> getEntitiesFromEmpresaDtos(List<EmpresaDto> dtos);
}
