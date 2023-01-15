package com.business.unknow.services.services;

import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.util.validators.CfdiValidator;
import com.unknown.cfdi.mappers.CfdiMapper;
import com.unknown.cfdi.modelos.Cfdi;
import com.unknown.error.PptUtilException;
import com.unknown.models.generated.Comprobante;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CfdiService {

  @Autowired private FilesService filesService;

  @Autowired private CfdiMapper cfdiMapper;

  @Autowired private CfdiAdapter cfdiAdapter;

  public Cfdi getCfdiByFolio(String folio) throws PptUtilException {
    return filesService.getCfdiFromS3(folio);
  }

  /**
   * Recalculates CFDI amounts based on SAT rounding rules, do not move or update this method
   * without carefully review.
   *
   * @param {@link Cfdi}
   */
  public Cfdi updateCfdi(Cfdi cfdi) throws InvoiceManagerException {
    CfdiValidator.validate(cfdi);
    Cfdi newCfdi = recalculateCfdi(cfdi);
    return cfdiAdapter.mapCfdiNotInComprobante(newCfdi);
  }

  /**
   * Recalculate invoice amounts
   *
   * @param {@link Cfdi}
   * @return {@link Cfdi}
   * @throws {@link InvoiceManagerException}
   */
  public Cfdi recalculateCfdi(Cfdi cfdi) throws InvoiceManagerException {
    CfdiValidator.validate(cfdi);
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(cfdi);
    return cfdiAdapter.mapCfdiNotInComprobante(cfdiMapper.comprobanteToCfdi(comprobante));
  }

  public Cfdi recalculateCfdiAmmounts(Cfdi cfdi) {
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(cfdi);
    return cfdiAdapter.mapCfdiNotInComprobante(cfdiMapper.comprobanteToCfdi(comprobante));
  }
}
