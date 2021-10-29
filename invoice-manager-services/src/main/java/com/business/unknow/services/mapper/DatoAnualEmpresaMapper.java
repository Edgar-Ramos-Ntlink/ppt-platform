package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.services.DatoAnualEmpresaDto;
import com.business.unknow.services.entities.DatoAnualEmpresa;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface DatoAnualEmpresaMapper {

  DatoAnualEmpresa getDatoAnualEntityFromDto(DatoAnualEmpresaDto datoAnual);

  DatoAnualEmpresaDto getDatoAnualDtoFromEntity(DatoAnualEmpresa entity);

  List<DatoAnualEmpresaDto> getDatosAnualesDtoFromEntities(List<DatoAnualEmpresa> entities);
}
