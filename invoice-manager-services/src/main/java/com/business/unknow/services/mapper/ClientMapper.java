package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.services.ClientDto;
import com.business.unknow.services.entities.Client;
import java.util.List;
import org.mapstruct.Mapper;

/** @author eej000f */
@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface ClientMapper {

  ClientDto getClientDtoFromEntity(Client entity);

  Client getEntityFromClientDto(ClientDto dto);

  List<ClientDto> getClientDtosFromEntities(List<Client> entities);

  List<Client> getEntitiesFromClientDtos(List<ClientDto> dto);
}
