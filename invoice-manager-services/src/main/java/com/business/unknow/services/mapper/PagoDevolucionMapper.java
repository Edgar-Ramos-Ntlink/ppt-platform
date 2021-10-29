package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.pagos.PagoDevolucionDto;
import com.business.unknow.services.entities.PagoDevolucion;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface PagoDevolucionMapper {

  PagoDevolucionDto getPagoDevolucionDtoFromEntity(PagoDevolucion pagoDevolucion);

  List<PagoDevolucionDto> getPagoDevolucionesDtoFromEntities(List<PagoDevolucion> pagoDevoluciones);

  PagoDevolucion getEntityFromPagoDevolucionDto(PagoDevolucionDto pagoDevolucion);
}
