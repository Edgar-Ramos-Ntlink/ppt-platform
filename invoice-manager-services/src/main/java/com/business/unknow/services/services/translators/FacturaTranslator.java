package com.business.unknow.services.services.translators;

import com.business.unknow.Constants;
import com.business.unknow.Constants.FacturaComplemento;
import com.business.unknow.enums.S3BucketsEnum;
import com.business.unknow.enums.TipoArchivoEnum;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.cfdi.ComplementoDto;
import com.business.unknow.model.error.InvoiceCommonException;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.mapper.factura.FacturaCfdiTranslatorMapper;
import com.business.unknow.services.services.FilesService;
import com.business.unknow.services.util.helpers.CdfiHelper;
import com.business.unknow.services.util.helpers.DateHelper;
import com.business.unknow.services.util.helpers.FacturaHelper;
import com.business.unknow.services.util.helpers.SignHelper;
import com.google.common.collect.ImmutableList;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import com.mx.ntlink.cfdi.modelos.CfdiRelacionado;
import com.mx.ntlink.cfdi.modelos.CfdiRelacionados;
import com.mx.ntlink.cfdi.modelos.Concepto;
import com.mx.ntlink.cfdi.modelos.Retencion;
import com.mx.ntlink.cfdi.modelos.RetencionConcepto;
import com.mx.ntlink.cfdi.modelos.Traslado;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FacturaTranslator {

  @Autowired private CdfiHelper cdfiHelper;

  @Autowired private FacturaHelper facturaHelper;

  @Autowired private DateHelper dateHelper;

  @Autowired private FacturaCfdiTranslatorMapper facturaCfdiTranslatorMapper;

  @Autowired private SignHelper signHelper;

  @Autowired private FilesService fileService;

  public FacturaContext translateFactura(FacturaContext context) throws InvoiceManagerException {
    try {
      Cfdi cfdi =
          facturaCfdiTranslatorMapper.cdfiRootInfo(
              context.getFacturaDto(), context.getEmpresaDto());

      cfdi.setCertificado(
          fileService.getS3File(
              S3BucketsEnum.EMPRESAS,
              String.format(
                  "%s-%s%s",
                  cfdi.getEmisor().getRfc(),
                  Constants.CSD_CERT,
                  TipoArchivoEnum.CERT.getFormat())));
      BigDecimal totalImpuestos = new BigDecimal(0);
      BigDecimal totalRetenciones = new BigDecimal(0);
      List<Traslado> impuestos = new ArrayList<>();
      List<Retencion> retenciones = new ArrayList<>();
      if (context.getFacturaDto().getCfdi().getCfdiRelacionados() != null) {
        CfdiRelacionado relacionado =
            context.getFacturaDto().getCfdi().getCfdiRelacionados().getCfdiRelacionado().stream()
                .findFirst()
                .get();
        cfdi.setCfdiRelacionados(
            CfdiRelacionados.builder().tipoRelacion(relacionado.getTipoRelacion()).build());
        cfdi.getCfdiRelacionados()
            .setCfdiRelacionado(ImmutableList.of(CfdiRelacionado.builder().build()));
        cfdi.getCfdiRelacionados().getCfdiRelacionado().stream()
            .findFirst()
            .get()
            .setUuid(relacionado.getTipoRelacion());
      }
      for (Concepto concepto : context.getFacturaDto().getCfdi().getConceptos()) {
        cfdi.getConceptos().add(concepto);
        if (!concepto.getImpuestos().isEmpty()) {
          totalImpuestos = calculaImpuestos(impuestos, concepto, totalImpuestos);
        }
        if (!concepto.getImpuestos().stream().findFirst().get().getRetenciones().isEmpty()) {
          totalRetenciones = calculaRetenciones(retenciones, concepto, totalRetenciones);
        }
      }
      if (!impuestos.isEmpty()) {
        cfdi.getImpuestos().stream().findFirst().get().setTraslados(impuestos);
      }
      if (!retenciones.isEmpty()) {
        cfdi.getImpuestos().stream().findFirst().get().setRetenciones(retenciones);
      }

      if (!totalImpuestos.equals(BigDecimal.ZERO)) {
        cfdi.getImpuestos().stream()
            .findFirst()
            .get()
            .setTotalImpuestosTrasladados(totalImpuestos.setScale(2, RoundingMode.HALF_UP));
      } else {
        cfdi.getImpuestos().stream().findFirst().get().setTotalImpuestosTrasladados(null);
      }

      if (!totalRetenciones.equals(BigDecimal.ZERO)) {
        cfdi.getImpuestos().stream()
            .findFirst()
            .get()
            .setTotalImpuestosRetenidos(totalRetenciones.setScale(2, RoundingMode.HALF_UP));
      } else {
        cfdi.getImpuestos().stream().findFirst().get().setTotalImpuestosRetenidos(null);
      }
      context.setCfdi(cfdi);
      facturaToXmlSigned(context);
      log.debug(context.getXml());
      return context;
    } catch (InvoiceCommonException e) {
      e.printStackTrace();
      throw new InvoiceManagerException(
          "Error generating the xml", e.getMessage(), HttpStatus.SC_CONFLICT);
    }
  }

  public FacturaContext translateComplemento(FacturaContext context)
      throws InvoiceManagerException {
    // TODO validar generacion de Complemento
    /*  try {
       Cfdi cfdi =
           facturaCfdiTranslatorMapper.complementoRootInfo(
               context.getFacturaDto().getCfdi(), context.getEmpresaDto());
       cfdi.setCertificado(
           s3service.getS3File(
               S3BucketsEnum.EMPRESAS,
               String.format(
                   "%s-%s%s",
                   cfdi.getEmisor().getRfc(),
                   Constants.CSD_CERT,
                   TipoArchivoEnum.CERT.getFormat())));
       for (ConceptoDto concepto : context.getFacturaDto().getCfdi().getConceptos()) {
         cfdi.getConceptos().add(facturaCfdiTranslatorMapper.complementoConcepto(concepto));
       }
       if (context.getFacturaDto().getCfdi().getRelacionado() != null) {
         RelacionadoDto relacionadoDto = context.getFacturaDto().getCfdi().getRelacionado();
         cfdi.setCfdiRelacionados(
             CfdiRelacionados.builder().tipoRelacion(relacionadoDto.getTipoRelacion()).build());
         cfdi.getCfdiRelacionados().setCfdiRelacionado(new CfdiRelacionado());
         cfdi.getCfdiRelacionados().getCfdiRelacionado().setUuid(relacionadoDto.getRelacion());
       }
       Optional<CfdiPagoDto> primerPago =
           context.getFacturaDto().getCfdi().getComplemento().getPagos().stream().findFirst();
       Complemento complemento = new Complemento();
       ComplementoPagos complementoPagos = new ComplementoPagos();
       ComplementoPago complementoPago = new ComplementoPago();
       complementoPago.setFechaPago(
           dateHelper.getStringFromFecha(
               primerPago.get().getFechaPago(), FacturaConstants.FACTURA_DATE_FORMAT));
       if (!primerPago.get().getMoneda().equals("MXN")) {
         complementoPago.setTipoCambioP(primerPago.get().getTipoCambio());
       }
       complementoPago.setFormaDePago(primerPago.get().getFormaPago());
       complementoPago.setMoneda(primerPago.get().getMoneda());
       complemento.setComplementoPago(complementoPagos);
       BigDecimal montoTotal = new BigDecimal(0);
       List<ComplementoPago> complementosPago = new ArrayList<>();
       List<ComplementoDocRelacionado> complementosRelacionados = new ArrayList<>();
       complementosPago.add(complementoPago);
       complementoPagos.setComplementoPagos(complementosPago);
       complementoPago.setComplementoDocRelacionado(complementosRelacionados);
       for (CfdiPagoDto cfdiPago : context.getFacturaDto().getCfdi().getComplemento().getPagos()) {
         ComplementoDocRelacionado complementoRelacionado =
             facturaCfdiTranslatorMapper.complementoComponente(cfdiPago);
         if (!cfdiPago.getMoneda().equals(cfdiPago.getMonedaDr())) {
           complementoRelacionado.setTipoCambioDR(cfdiPago.getTipoCambioDr());
         }
         complementosRelacionados.add(complementoRelacionado);
         montoTotal =
             montoTotal.add(
                 cfdiPago
                     .getImportePagado()
                     .multiply(cfdiPago.getTipoCambioDr())
                     .setScale(2, RoundingMode.DOWN));
       }
       complementoPago.setMonto(montoTotal.toString());
       cfdi.setComplemento(complemento);
       cfdi.setImpuestos(null);
       context.setCfdi(cfdi);
       complementoToXmlSigned(context);
       log.debug(context.getXml());
       return context;
     } catch (InvoiceCommonException e) {
       throw new InvoiceManagerException(
           "Error generating the xml", e.getMessage(), HttpStatus.SC_CONFLICT);
     }
    */
    return null;
  }

  public void complementoToXmlSigned(FacturaContext context)
      throws InvoiceCommonException, InvoiceManagerException {
    String xml = facturaHelper.facturaCfdiToXml(context.getCfdi());
    log.debug(context.getXml());
    xml = xml.replace(FacturaComplemento.TOTAL, FacturaComplemento.TOTAL_FINAL);
    xml = xml.replace(FacturaComplemento.SUB_TOTAL, FacturaComplemento.SUB_TOTAL_FINAL);
    xml =
        cdfiHelper.changeDate(
            xml,
            dateHelper.isMyDateAfterDaysInPast(context.getFacturaDto().getFechaActualizacion(), 3)
                ? context.getFacturaDto().getFechaActualizacion()
                : new Date());
    String cadenaOriginal = signHelper.getCadena(xml);

    String llavePrivada =
        fileService.getS3File(
            S3BucketsEnum.EMPRESAS,
            String.format(
                "%s-%s%s",
                context.getEmpresaDto().getRfc(),
                Constants.CSD_KEY,
                TipoArchivoEnum.KEY.getFormat()));

    String sello =
        signHelper.getSign(cadenaOriginal, context.getEmpresaDto().getFiel(), llavePrivada);
    context.setXml(cdfiHelper.putsSign(xml, sello));
    if (context.getFacturaDto().getCfdi().getComplemento() == null) {
      context.getFacturaDto().getCfdi().setComplemento(ImmutableList.of(new ComplementoDto()));
    }
  }

  public void facturaToXmlSigned(FacturaContext context)
      throws InvoiceCommonException, InvoiceManagerException {
    String xml = facturaHelper.facturaCfdiToXml(context.getCfdi());
    xml =
        cdfiHelper.changeDate(
            xml,
            dateHelper.isMyDateAfterDaysInPast(context.getFacturaDto().getFechaActualizacion(), 3)
                ? context.getFacturaDto().getFechaActualizacion()
                : new Date());
    String cadenaOriginal = signHelper.getCadena(xml);
    String llavePrivada =
        fileService.getS3File(
            S3BucketsEnum.EMPRESAS,
            String.format(
                "%s-%s%s",
                context.getEmpresaDto().getRfc(),
                Constants.CSD_KEY,
                TipoArchivoEnum.KEY.getFormat()));
    String sello =
        signHelper.getSign(cadenaOriginal, context.getEmpresaDto().getFiel(), llavePrivada);
    context.setXml(cdfiHelper.putsSign(xml, sello).replace("standalone=\"no\"", ""));
    context.getFacturaDto().getCfdi().setComplemento(ImmutableList.of(new ComplementoDto()));
  }

  public BigDecimal calculaImpuestos(
      List<Traslado> impuestos, Concepto concepto, BigDecimal totalImpuestos) {
    // TODO REALIZAR CALCULO DE IMPUESTOS CON EL MAPPER DE UTILITIES
    return null;
  }

  public BigDecimal calculaRetenciones(FacturaContext context) {
    return calculaRetenciones(context.getFacturaDto());
  }

  public BigDecimal calculaRetenciones(FacturaDto facturaDto) {
    BigDecimal totalRetenciones = new BigDecimal(0);
    List<Retencion> retenciones = new ArrayList<>();
    for (Concepto concepto : facturaDto.getCfdi().getConceptos()) {
      if (!concepto.getImpuestos().stream().findFirst().get().getRetenciones().isEmpty()) {
        totalRetenciones = calculaRetenciones(retenciones, concepto, totalRetenciones);
      }
    }

    return totalRetenciones;
  }

  public BigDecimal calculaImpuestos(FacturaDto facturaDto) {
    BigDecimal totalImpuestos = new BigDecimal(0);
    List<Traslado> traslados = new ArrayList<>();
    for (Concepto concepto : facturaDto.getCfdi().getConceptos()) {
      if (!concepto.getImpuestos().isEmpty()) {
        totalImpuestos = calculaImpuestos(traslados, concepto, totalImpuestos);
      }
    }

    return totalImpuestos;
  }

  public BigDecimal calculaRetenciones(
      List<Retencion> retenciones, Concepto concepto, BigDecimal totalRetenciones) {
    for (RetencionConcepto translado :
        concepto.getImpuestos().stream().findFirst().get().getRetenciones()) {
      Optional<Retencion> tempTranslado =
          retenciones.stream()
              .filter(a -> a.getImpuesto().equals(translado.getImpuesto()))
              .findFirst();
      if (tempTranslado.isPresent()) {
        tempTranslado
            .get()
            .setImporte(tempTranslado.get().getImporte().add(translado.getImporte()));
      } else {
        retenciones.add(new Retencion(translado.getImpuesto(), translado.getImporte()));
      }
      totalRetenciones = totalRetenciones.add(translado.getImporte());
    }
    return totalRetenciones;
  }
}
