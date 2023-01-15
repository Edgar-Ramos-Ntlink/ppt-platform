package com.business.unknow.services.services;

import com.business.unknow.model.dto.catalogs.ClaveProductoServicioDto;
import com.unknown.cfdi.modelos.Cfdi;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CfdiAdapter {

  @Autowired private CatalogService catalogService;

  public Cfdi mapCfdiNotInComprobante(Cfdi targetCfdi) {
    if (!targetCfdi.getConceptos().isEmpty()) {
      targetCfdi.getConceptos().stream()
          .filter(x -> x.getClaveProdServDesc() == null)
          .forEach(
              concepto -> {
                Optional<ClaveProductoServicioDto> prod =
                    catalogService
                        .getServiceProduct(
                            Optional.empty(), Optional.of(concepto.getClaveProdServ()))
                        .stream()
                        .findFirst();
                String claveProdDesc = prod.isEmpty() ? "" : prod.get().getDescripcion();
                concepto.setClaveProdServDesc(claveProdDesc);
              });
    }

    return targetCfdi;
  }
}
