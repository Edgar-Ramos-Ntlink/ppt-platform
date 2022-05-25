package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.services.ClientDto;
import com.business.unknow.services.entities.Client;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface ClientMapper {

  @Mappings({
    @Mapping(target = "rfc", source = "informacionFiscal.rfc"),
    @Mapping(target = "giro", source = "informacionFiscal.giro"),
    @Mapping(target = "nombre", source = "informacionFiscal.nombre"),
    @Mapping(target = "moral", source = "informacionFiscal.moral"),
    @Mapping(target = "curp", source = "informacionFiscal.curp"),
    @Mapping(target = "razonSocial", source = "informacionFiscal.razonSocial"),
    @Mapping(target = "regimenFiscal", source = "informacionFiscal.regimenFiscal"),
    @Mapping(target = "calle", source = "informacionFiscal.calle"),
    @Mapping(target = "noExterior", source = "informacionFiscal.noExterior"),
    @Mapping(target = "noInterior", source = "informacionFiscal.noInterior"),
    @Mapping(target = "municipio", source = "informacionFiscal.municipio"),
    @Mapping(target = "localidad", source = "informacionFiscal.localidad"),
    @Mapping(target = "estado", source = "informacionFiscal.estado"),
    @Mapping(target = "pais", source = "informacionFiscal.pais"),
    @Mapping(target = "coo", source = "informacionFiscal.coo"),
    @Mapping(target = "cp", source = "informacionFiscal.cp"),
    @Mapping(target = "correo", source = "informacionFiscal.correo"),
    @Mapping(target = "telefono", source = "informacionFiscal.telefono"),
  })
  ClientDto getClientDtoFromEntity(Client entity);

  List<ClientDto> getClientDtosFromEntities(List<Client> entities);

  @Mappings({
    @Mapping(
        expression = "java(clientDto.getRfc().toUpperCase())",
        target = "informacionFiscal.rfc"),
    @Mapping(source = "giro", target = "informacionFiscal.giro"),
    @Mapping(source = "moral", target = "informacionFiscal.moral"),
    @Mapping(source = "curp", target = "informacionFiscal.curp"),
    @Mapping(
        expression = "java(clientDto.getRazonSocial().toUpperCase())",
        target = "informacionFiscal.razonSocial"),
    @Mapping(source = "regimenFiscal", target = "informacionFiscal.regimenFiscal"),
    @Mapping(source = "calle", target = "informacionFiscal.calle"),
    @Mapping(source = "noExterior", target = "informacionFiscal.noExterior"),
    @Mapping(source = "noInterior", target = "informacionFiscal.noInterior"),
    @Mapping(source = "municipio", target = "informacionFiscal.municipio"),
    @Mapping(source = "localidad", target = "informacionFiscal.localidad"),
    @Mapping(source = "estado", target = "informacionFiscal.estado"),
    @Mapping(source = "pais", target = "informacionFiscal.pais"),
    @Mapping(source = "coo", target = "informacionFiscal.coo"),
    @Mapping(source = "cp", target = "informacionFiscal.cp"),
    @Mapping(source = "correo", target = "informacionFiscal.correo"),
    @Mapping(source = "telefono", target = "informacionFiscal.telefono"),
  })
  Client getEntityFromClientDto(ClientDto dto);

  List<Client> getEntitiesFromClientDtos(List<ClientDto> dto);
}
