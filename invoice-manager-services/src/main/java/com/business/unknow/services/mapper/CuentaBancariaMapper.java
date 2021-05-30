package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.services.CuentaBancariaDto;
import com.business.unknow.services.entities.CuentaBancaria;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface CuentaBancariaMapper {

  CuentaBancariaDto getCuentaBancariaToFromEntity(CuentaBancaria entity);

  CuentaBancaria getEntityFromCuentaBancariaDto(CuentaBancariaDto dto);

  List<CuentaBancariaDto> getCuentaBancariaDtosFromEntities(List<CuentaBancaria> entities);
}
