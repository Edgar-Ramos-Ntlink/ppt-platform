package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.services.EmpresaIngresosDto;
import com.business.unknow.services.entities.EmpresaIngresos;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface EmpresaIngresosMapper {

  EmpresaIngresos getDatoAnualEntityFromDto(EmpresaIngresosDto datoAnual);

  EmpresaIngresosDto getDatoAnualDtoFromEntity(EmpresaIngresos entity);

  List<EmpresaIngresosDto> getDatosAnualesDtoFromEntities(List<EmpresaIngresos> entities);
}
