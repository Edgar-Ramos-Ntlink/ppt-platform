package com.business.unknow.services.services;

import com.business.unknow.model.dto.cfdi.CfdiPagoDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Reporte;
import com.business.unknow.services.repositories.ReporteRepository;
import com.business.unknow.services.repositories.facturas.CfdiPagoRepository;
import com.business.unknow.services.util.validators.CfdiValidator;
import com.mx.ntlink.NtlinkUtilException;
import com.mx.ntlink.cfdi.mappers.CfdiMapper;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import com.mx.ntlink.models.generated.Comprobante;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CfdiService {

  @Autowired private CfdiPagoRepository cfdiPagoRepository;

  @Autowired private CfdiMapper cfdiMapper;

  @Autowired private FilesService filesService;

  @Autowired private FacturaService facturaService;

  @Autowired private CatalogCacheService cacheCatalogsService;

  @Autowired private ReporteRepository reporteRepository;

  @Autowired
  @Qualifier("CfdiValidator")
  private CfdiValidator validator;

  public Cfdi getCfdiByFolio(String folio) throws NtlinkUtilException {
    return filesService.getCfdiFromS3(folio);
  }

  public List<CfdiPagoDto> getCfdiPagosByFolio(String folio) {
    // TODO validate Complemento Pago
    return null;
  }

  public List<CfdiPagoDto> getPagosPPD(Integer id) {

    /*Cfdi cfdi =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("El cfdi con folio %d no fue encontrado", id)));
    return mapper.getCfdiPagosDtoFromEntities(cfdiPagoRepository.findByFolio(cfdi.getFolio()));
     */
    return null;
  }

  public Cfdi insertNewCfdi(Cfdi cfdi) throws InvoiceManagerException {
    validator.validate(cfdi);
    recalculateCfdiAmmounts(cfdi);
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(cfdi);
    filesService.sendXmlToS3(cfdi.getFolio(), comprobante);
    createReportData(cfdi);
    // TODO ADD CREATE PDF
    return cfdiMapper.comprobanteToCfdi(comprobante);
  }

  public Cfdi updateCfdiBody(String folio, Cfdi cfdi) throws InvoiceManagerException {
    deleteReportData(cfdi);
    // TODO ADD CREATE PDF
    return insertNewCfdi(cfdi);
  }

  public void deleteCfdi(String folio) throws NtlinkUtilException {
    filesService.deleteCfdiFromS3(folio);
  }

  public Cfdi recalculateCfdiAmmounts(Cfdi cfdi) {
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(cfdi);
    return cfdiMapper.comprobanteToCfdi(comprobante);
  }

  private void createReportData(Cfdi cfdi) {
    cfdi.getConceptos().stream()
        .forEach(
            concepto -> {
              reporteRepository.save(
                  Reporte.builder()
                      .folio(cfdi.getFolio())
                      .tipoDeComprobante(cfdi.getTipoDeComprobante())
                      .impuestosTrasladados(
                          cfdi.getImpuestos().stream()
                              .findFirst()
                              .get()
                              .getTotalImpuestosTrasladados())
                      .impuestosRetenidos(
                          cfdi.getImpuestos().stream()
                              .findFirst()
                              .get()
                              .getTotalImpuestosRetenidos())
                      .subtotal(cfdi.getSubtotal())
                      .total(cfdi.getTotal())
                      .metodoPago(cfdi.getMetodoPago())
                      .formaPago(cfdi.getFormaPago())
                      .moneda(cfdi.getMoneda())
                      .cantidad(concepto.getCantidad())
                      .claveUnidad(concepto.getClaveUnidad())
                      .descripcion(concepto.getDescripcion())
                      .valorUnitario(concepto.getValorUnitario())
                      .importe(concepto.getImporte())
                      .build());
            });
  }

  private void deleteReportData(Cfdi cfdi) {
    List<Reporte> reportes = reporteRepository.findByFolio(cfdi.getFolio());
    reportes.stream().forEach(reporte -> reporteRepository.delete(reporte));
  }
}
