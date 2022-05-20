package com.business.unknow.services.services;

import com.business.unknow.model.dto.cfdi.CfdiPagoDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.mapper.CfdiPagoMapper;
import com.business.unknow.services.repositories.facturas.CfdiPagoRepository;
import com.business.unknow.services.util.validators.CfdiValidator;
import com.mx.ntlink.NtlinkUtilException;
import com.mx.ntlink.cfdi.mappers.CfdiMapper;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import com.mx.ntlink.models.generated.Comprobante;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CfdiService {

  @Autowired private FilesService filesService;

  @Autowired private CfdiMapper cfdiMapper;

  @Autowired private CfdiPagoMapper cfdiPagoMapper;

  @Autowired private CfdiPagoRepository cfdiPagoRepository;

  public Cfdi getCfdiByFolio(String folio) throws NtlinkUtilException {
    return filesService.getCfdiFromS3(folio);
  }

  /**
   * Recalculates CFDI amounts based on SAT rounding rules, do not move or update this method
   * without carefully review.
   *
   * @param {@link Cfdi}
   */
  public Cfdi updateCfdi(Cfdi cfdi) throws InvoiceManagerException, NtlinkUtilException {
    CfdiValidator.validate(cfdi);
    Cfdi newCfdi = recalculateCfdi(cfdi);
    return newCfdi;
  }

  /**
   * Recalculate invoice amounts
   *
   * @param {@link Cfdi}
   * @return {@link Cfdi}
   * @throws {@link InvoiceManagerException}
   */
  public Cfdi recalculateCfdi(Cfdi cfdi) throws InvoiceManagerException, NtlinkUtilException {
    CfdiValidator.validate(cfdi);
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(cfdi);
    return cfdiMapper.comprobanteToCfdi(comprobante);
  }

  /**
   * Gets payments by folio
   *
   * @param {@link List<CfdiPagoDto>}
   * @return
   */
  public List<CfdiPagoDto> getPaymentsByFolio(String folio) {
    return cfdiPagoMapper.getDtosFromEntities(cfdiPagoRepository.findByFolio(folio));
  }

  public Cfdi recalculateCfdiAmmounts(Cfdi cfdi) throws NtlinkUtilException {
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(cfdi);
    return cfdiMapper.comprobanteToCfdi(comprobante);
  }
}
