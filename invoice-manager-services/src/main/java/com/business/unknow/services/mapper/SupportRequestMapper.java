package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.services.SupportRequestDto;
import com.business.unknow.services.entities.SupportRequest;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface SupportRequestMapper {
  SupportRequestDto getDtoFromEntity(SupportRequest entity);

  List<SupportRequestDto> getDtosFromEntities(List<SupportRequest> entities);

  SupportRequest getEntityFromDto(SupportRequestDto dto);
}
