package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.SupportRequestDto;
import com.business.unknow.services.entities.SupportRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface SupportRequestMapper {
    SupportRequestDto getDtoFromEntity(SupportRequest entity);

    List<SupportRequestDto> getDtosFromEntities(List<SupportRequest> entities);

    SupportRequest getEntityFromDto(SupportRequestDto dto);
}
