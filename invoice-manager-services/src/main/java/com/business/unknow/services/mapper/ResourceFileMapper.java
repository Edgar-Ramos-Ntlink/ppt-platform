package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.services.entities.ResourceFile;
import org.mapstruct.Mapper;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface ResourceFileMapper {
  ResourceFile getEntityFromDto(ResourceFileDto dto);
}
