package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.cfdi.CfdiPagoDto;
import com.business.unknow.services.entities.CfdiPago;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface CfdiPagoMapper {
  List<CfdiPagoDto> getDtosFromEntities(List<CfdiPago> cfdiPago);
}
