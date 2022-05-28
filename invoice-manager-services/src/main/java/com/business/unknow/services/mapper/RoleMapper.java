package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.services.RoleDto;
import com.business.unknow.services.entities.Role;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface RoleMapper {

  RoleDto getRoleDtoFromentity(Role entity);

  Role getEntityFromRoleDto(RoleDto dto);

  List<RoleDto> getRoleDtosFromEntities(List<Role> entities);
}
