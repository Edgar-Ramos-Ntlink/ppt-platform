package com.business.unknow.services.services;

import com.business.unknow.services.entities.Reporte;
import com.business.unknow.services.repositories.ReporteRepository;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportDataService {

  @Autowired private ReporteRepository reporteRepository;

  public void upsertReportData(Cfdi cfdi) {
    reporteRepository.deleteByFolio(cfdi.getFolio());
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

  public void deleteReportData(String folio) {
    reporteRepository.deleteByFolio(folio);
  }
}
