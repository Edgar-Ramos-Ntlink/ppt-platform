package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.services.ClientDto;
import com.business.unknow.services.entities.Client;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface ClientMapper {

  @Mapping(source = "colonia", target = "localidad")
  ClientDto getClientDtoFromEntity(Client entity);

  List<ClientDto> getClientDtosFromEntities(List<Client> entities);

  @Mappings({
    @Mapping(expression = "java(dto.getRfc().toUpperCase())", target = "rfc"),
    @Mapping(source = "localidad", target = "colonia"),
    @Mapping(
        expression = "java(dto.getRazonSocial().trim().toUpperCase())",
        target = "razonSocial"),
  })
  Client getEntityFromClientDto(ClientDto dto);
}
