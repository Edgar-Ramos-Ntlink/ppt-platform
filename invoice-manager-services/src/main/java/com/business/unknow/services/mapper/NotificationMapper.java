package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.services.NotificationDto;
import com.business.unknow.services.entities.Notification;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface NotificationMapper {
  NotificationDto getDtoFromEntity(Notification entity);

  List<NotificationDto> getDtosFromEntities(List<Notification> entity);

  Notification getEntityFromDto(NotificationDto dto);
}
