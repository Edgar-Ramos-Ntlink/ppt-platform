package com.business.unknow.services.mapper;

import com.business.unknow.model.dto.catalogs.CatalogDto;
import com.business.unknow.model.dto.catalogs.ClaveProductoServicioDto;
import com.business.unknow.model.dto.catalogs.ClaveUnidadDto;
import com.business.unknow.model.dto.catalogs.FormaPagoDto;
import com.business.unknow.model.dto.catalogs.RegimenFiscalDto;
import com.business.unknow.model.dto.catalogs.UsoCfdiDto;
import com.business.unknow.services.entities.StatusPayment;
import com.business.unknow.services.entities.catalogs.Banco;
import com.business.unknow.services.entities.catalogs.ClaveProductoServicio;
import com.business.unknow.services.entities.catalogs.ClaveUnidad;
import com.business.unknow.services.entities.catalogs.FormaPago;
import com.business.unknow.services.entities.catalogs.Giro;
import com.business.unknow.services.entities.catalogs.RegimenFiscal;
import com.business.unknow.services.entities.catalogs.StatusEvento;
import com.business.unknow.services.entities.catalogs.UsoCfdi;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface CatalogsMapper {

  CatalogDto getDtoFromEntity(Banco banco);

  CatalogDto getDtoFromEntity(StatusPayment banco);

  CatalogDto getDtoFromEntity(StatusEvento statusEvento);

  CatalogDto getDtoFromEntity(Giro giro);

  FormaPagoDto getDtoFromEntity(FormaPago formaPago);

  ClaveProductoServicioDto getDtoFromEntity(ClaveProductoServicio prodServicio);

  UsoCfdiDto getDtoFromEntity(UsoCfdi usodCfdi);

  RegimenFiscalDto getDtoFromEntity(RegimenFiscal regimenFiscal);

  ClaveUnidadDto getDtoFromEntity(ClaveUnidad claveUnidad);

  List<ClaveProductoServicioDto> getDtosFromEntities(List<ClaveProductoServicio> prodServicio);
}
