/** */
package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.pagos.PagoFacturaDto;
import com.business.unknow.services.entities.Pago;
import com.business.unknow.services.entities.PagoFactura;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** @author ralfdemoledor */
@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface PagoMapper {

  PagoDto getPagoDtoFromEntity(Pago pago);

  List<PagoDto> getPagosDtoFromEntities(List<Pago> pagos);

  @Mapping(target = "facturas", ignore = true)
  Pago getEntityFromPagoDto(PagoDto pago);

  public PagoFacturaDto getPagoFacturaDtoFromEntity(PagoFactura pago);

  public List<PagoFacturaDto> getPagosFacturaDtoFromEntities(List<PagoFactura> pagos);

  public PagoFactura getEntityFromPagoFacturaDto(PagoFacturaDto pago);

  public List<PagoFactura> getEntitiesFromPagosFacturaDtos(List<PagoFacturaDto> pagos);
}
