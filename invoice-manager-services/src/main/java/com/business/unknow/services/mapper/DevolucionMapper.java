/** */
package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.services.DevolucionDto;
import com.business.unknow.services.entities.Devolucion;
import java.util.List;
import org.mapstruct.Mapper;

/** @author ralfdemoledor */
@Mapper
public interface DevolucionMapper {

  DevolucionDto getDevolucionDtoFromEntity(Devolucion devolucion);

  List<DevolucionDto> getDevolucionesDtoFromEntities(List<Devolucion> devoluciones);

  Devolucion getEntityFromDevolucionDto(DevolucionDto devolucion);
}
