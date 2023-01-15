package com.business.unknow.services.services;

import com.business.unknow.services.entities.Reporte;
import com.business.unknow.services.repositories.ReporteRepository;
import com.unknown.cfdi.modelos.Cfdi;
import com.unknown.cfdi.modelos.Impuesto;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportDataService {

  @Autowired private ReporteRepository reporteRepository;

  public void upsertReportData(Cfdi cfdi) {
    reporteRepository.deleteByFolio(cfdi.getFolio());

    BigDecimal impTrasladados =
        cfdi.getImpuestos().stream()
            .findFirst()
            .orElse(Impuesto.builder().totalImpuestosTrasladados(BigDecimal.ZERO).build())
            .getTotalImpuestosTrasladados();
    BigDecimal impRetenidos =
        cfdi.getImpuestos().stream()
            .findFirst()
            .orElse(Impuesto.builder().totalImpuestosRetenidos(BigDecimal.ZERO).build())
            .getTotalImpuestosRetenidos();

    List<Reporte> reports =
        cfdi.getConceptos().stream()
            .map(
                c -> {
                  return reporteRepository.save(
                      Reporte.builder()
                          .folio(cfdi.getFolio())
                          .tipoDeComprobante(cfdi.getTipoDeComprobante())
                          .impuestosTrasladados(impTrasladados)
                          .impuestosRetenidos(impRetenidos)
                          .subtotal(cfdi.getSubtotal())
                          .total(cfdi.getTotal())
                          .metodoPago(cfdi.getMetodoPago())
                          .formaPago(cfdi.getFormaPago())
                          .moneda(cfdi.getMoneda())
                          .cantidad(c.getCantidad())
                          .claveUnidad(c.getClaveUnidad())
                          .claveProdServ(c.getClaveProdServ())
                          .descripcion(c.getDescripcion())
                          .valorUnitario(c.getValorUnitario())
                          .importe(c.getImporte())
                          .build());
                })
            .collect(Collectors.toList());
    for (Reporte report : reports) {
      reporteRepository.save(report);
    }
  }

  public void deleteReportData(String folio) {
    reporteRepository.deleteByFolio(folio);
  }
}
