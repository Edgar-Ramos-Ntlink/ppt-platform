package com.business.unknow.services.services;

import static com.business.unknow.Constants.CFDI_DATE_PATTERN;
import static com.business.unknow.Constants.ComplementoPpdDefaults.COMPROBANTE;
import static com.business.unknow.Constants.ComplementoPpdDefaults.IMPUESTO;
import static com.business.unknow.Constants.ComplementoPpdDefaults.PAGO_CLAVE;
import static com.business.unknow.Constants.ComplementoPpdDefaults.PAGO_DESC;
import static com.business.unknow.Constants.ComplementoPpdDefaults.PAGO_IMPUESTOS;
import static com.business.unknow.Constants.ComplementoPpdDefaults.PAGO_IMPUESTOS_GRAL;
import static com.business.unknow.Constants.ComplementoPpdDefaults.PAGO_UNIDAD;
import static com.business.unknow.Constants.ComplementoPpdDefaults.TASA_O_CUOTA;
import static com.business.unknow.Constants.ComplementoPpdDefaults.TIPO_FACTOR;
import static com.business.unknow.Constants.DATE_TIME_FORMAT;
import static com.business.unknow.Constants.IVA_BASE_16;
import static com.business.unknow.Constants.IVA_IMPUESTO_16;
import static com.business.unknow.enums.FacturaStatus.VALIDACION_TESORERIA;
import static com.mx.ntlink.models.generated.CTipoFactor.TASA;

import com.business.unknow.Constants;
import com.business.unknow.enums.FacturaStatus;
import com.business.unknow.enums.MetodosPago;
import com.business.unknow.enums.TipoComprobante;
import com.business.unknow.enums.TipoDocumento;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.PagoComplemento;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.pagos.PagoFacturaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.util.FacturaUtils;
import com.google.common.collect.ImmutableList;
import com.mx.ntlink.NtlinkUtilException;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import com.mx.ntlink.cfdi.modelos.Concepto;
import com.mx.ntlink.cfdi.modelos.Impuesto;
import com.mx.ntlink.cfdi.modelos.Receptor;
import com.mx.ntlink.cfdi.modelos.complementos.pagos.DocumentoRelacionado;
import com.mx.ntlink.cfdi.modelos.complementos.pagos.ImpuestosDR;
import com.mx.ntlink.cfdi.modelos.complementos.pagos.ImpuestosP;
import com.mx.ntlink.cfdi.modelos.complementos.pagos.Pago;
import com.mx.ntlink.cfdi.modelos.complementos.pagos.Pagos;
import com.mx.ntlink.cfdi.modelos.complementos.pagos.Totales;
import com.mx.ntlink.cfdi.modelos.complementos.pagos.TrasladoDR;
import com.mx.ntlink.cfdi.modelos.complementos.pagos.TrasladoP;
import com.mx.ntlink.cfdi.modelos.complementos.pagos.TrasladosDR;
import com.mx.ntlink.cfdi.modelos.complementos.pagos.TrasladosP;
import com.mx.ntlink.util.NumberTranslatorUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InvoiceBuilderService {

  @Autowired private CfdiService cfdiService;

  @Autowired private CatalogService catalogService;

  public FacturaCustom assignFacturaData(FacturaCustom facturaCustom, int amount)
      throws NtlinkUtilException {
    String folio =
        facturaCustom.getFolio() == null ? FacturaUtils.generateFolio() : facturaCustom.getFolio();
    Cfdi cfdi = cfdiService.recalculateCfdiAmmounts(facturaCustom.getCfdi());
    cfdi.setFecha(CFDI_DATE_PATTERN);
    cfdi.setFolio(folio);
    facturaCustom =
        facturaCustom.toBuilder()
            .cfdi(cfdi)
            .version(Constants.CFDI_40_VERSION)
            .total(facturaCustom.getCfdi().getTotal())
            .saldoPendiente(
                Objects.nonNull(facturaCustom.getSaldoPendiente())
                    ? facturaCustom.getSaldoPendiente()
                    : facturaCustom.getCfdi().getTotal())
            .impuestosTrasladados(
                facturaCustom.getCfdi().getImpuestos().stream()
                    .collect(
                        Collectors.reducing(
                            BigDecimal.ZERO,
                            Impuesto::getTotalImpuestosTrasladados,
                            BigDecimal::add)))
            .impuestosRetenidos(
                facturaCustom.getCfdi().getImpuestos().stream()
                    .collect(
                        Collectors.reducing(
                            BigDecimal.ZERO,
                            Impuesto::getTotalImpuestosRetenidos,
                            BigDecimal::add)))
            .folio(folio)
            .preFolio(
                facturaCustom.getPreFolio() == null
                    ? FacturaUtils.generatePreFolio(amount)
                    : facturaCustom.getPreFolio())
            .statusFactura(
                facturaCustom.getStatusFactura() == null
                    ? FacturaStatus.VALIDACION_OPERACIONES.getValor()
                    : facturaCustom.getStatusFactura())
            .build();
    return assignDescData(facturaCustom);
  }

  public FacturaCustom assignDescData(FacturaCustom facturaCustom) throws NtlinkUtilException {
    return facturaCustom.toBuilder()
        .totalDesc(
            NumberTranslatorUtil.getStringNumber(
                facturaCustom.getCfdi().getTotal(), facturaCustom.getCfdi().getMoneda()))
        .subTotalDesc(
            NumberTranslatorUtil.getStringNumber(
                facturaCustom.getCfdi().getSubtotal(), facturaCustom.getCfdi().getMoneda()))
        .usoCfdiDesc(
            catalogService
                .getCfdiUseByKey(facturaCustom.getCfdi().getReceptor().getUsoCfdi())
                .getDescripcion())
        .regimenFiscalDesc(
            catalogService
                .getTaxRegimeByKey(facturaCustom.getCfdi().getEmisor().getRegimenFiscal())
                .getDescripcion())
        .regimenFiscalReceptorDesc(
            catalogService
                .getTaxRegimeByKey(facturaCustom.getCfdi().getReceptor().getRegimenFiscalReceptor())
                .getDescripcion())
        .formaPagoDesc(
            Objects.isNull(facturaCustom.getCfdi().getFormaPago())
                ? null
                : catalogService
                    .getPaymentFormByKey(facturaCustom.getCfdi().getFormaPago())
                    .getDescripcion())
        .metodoPagoDesc(
            MetodosPago.findByValor(facturaCustom.getCfdi().getMetodoPago()).getDescripcion())
        .tipoDeComprobanteDesc(
            TipoComprobante.findByValor(facturaCustom.getCfdi().getTipoDeComprobante())
                .getDescripcion())
        .build();
  }

  public FacturaCustom assignComplementData(
      FacturaCustom facturaCustom, List<FacturaCustom> parentInvoices, PagoDto pagoDto, int amount)
      throws InvoiceManagerException {
    String folio = FacturaUtils.generateFolio();
    Cfdi cfdi = buildCfdiComplement(facturaCustom);
    cfdi.setFolio(folio);
    FacturaCustom complement =
        FacturaCustom.builder()
            .folio(folio)
            .version(Constants.CFDI_40_VERSION)
            .preFolio(FacturaUtils.generatePreFolio(amount))
            .total(BigDecimal.ZERO)
            .packFacturacion(facturaCustom.getPackFacturacion())
            .saldoPendiente(BigDecimal.ZERO)
            .lineaEmisor(facturaCustom.getLineaEmisor())
            .rfcEmisor(facturaCustom.getRfcEmisor())
            .metodoPago(Constants.ComplementoPpdDefaults.METODO_PAGO)
            .rfcRemitente(facturaCustom.getRfcRemitente())
            .direccionEmisor(facturaCustom.getDireccionEmisor())
            .direccionReceptor(facturaCustom.getDireccionReceptor())
            .lineaRemitente(facturaCustom.getLineaRemitente())
            .razonSocialEmisor(facturaCustom.getRazonSocialEmisor())
            .razonSocialRemitente(facturaCustom.getRazonSocialRemitente())
            .validacionTeso(false)
            .validacionOper(true)
            .statusFactura(VALIDACION_TESORERIA.getValor())
            .cfdi(cfdi)
            .solicitante(facturaCustom.getSolicitante())
            .tipoDocumento(TipoDocumento.COMPLEMENTO.getDescripcion())
            .build();
    Pagos pagos = assignComplementPaymentData(parentInvoices, pagoDto, complement);
    complement.getCfdi().setComplemento(ImmutableList.of(pagos));
    return complement;
  }

  public Pagos assignComplementPaymentData(
      List<FacturaCustom> facturaCustoms, PagoDto pagoDto, FacturaCustom complement)
      throws InvoiceManagerException {
    complement.setPagos(new ArrayList<>());

    String formaPago =
        catalogService
            .getPaymentFormByValue(pagoDto.getFormaPago())
            .orElseThrow(
                () ->
                    new InvoiceManagerException(
                        String.format(
                            "No hay una foma de pago asignable a %s", pagoDto.getFormaPago()),
                        HttpStatus.CONFLICT.value()));
    Pago pago =
        Pago.builder()
            .fechaPago(DATE_TIME_FORMAT.format(pagoDto.getFechaPago()))
            .formaDePagoP(formaPago)
            .monedaP(pagoDto.getMoneda())
            .monto(pagoDto.getMonto().setScale(2, RoundingMode.HALF_UP))
            .tipoCambioP(pagoDto.getTipoDeCambio().toString())
            .build();

    ImmutableList.Builder<DocumentoRelacionado> documentoRelacionados = ImmutableList.builder();

    BigDecimal importeP = BigDecimal.ZERO;
    BigDecimal baseP = BigDecimal.ZERO;
    for (FacturaCustom facturaCustomIterate : facturaCustoms) {

      PagoFacturaDto pagoFacturaDto =
          pagoDto.getFacturas().stream()
              .filter(e -> e.getFolio().equals(facturaCustomIterate.getFolio()))
              .findFirst()
              .orElseThrow(
                  () ->
                      new InvoiceManagerException(
                          "Debe tener por lo menos un pago", HttpStatus.BAD_REQUEST.value()));
      if (Objects.isNull(facturaCustomIterate.getPagos())) {
        facturaCustomIterate.setPagos(new ArrayList<>());
      }
      Integer numeroParcialidad =
          facturaCustomIterate.getPagos().stream()
                  .filter(e -> e.isValido())
                  .collect(Collectors.reducing(0, e -> 1, Integer::sum))
              + 1;
      PagoComplemento lastPayment =
          facturaCustomIterate.getPagos().stream()
              .filter(e -> e.isValido())
              .sorted(Comparator.comparing(PagoComplemento::getNumeroParcialidad).reversed())
              .findFirst()
              .orElse(PagoComplemento.builder().build());
      BigDecimal saldoAnterior =
          Objects.isNull(lastPayment.getImporteSaldoInsoluto())
              ? facturaCustomIterate.getTotal()
              : lastPayment.getImporteSaldoInsoluto();

      BigDecimal impuesto =
          pagoFacturaDto
              .getMonto()
              .multiply(BigDecimal.valueOf(IVA_IMPUESTO_16))
              .divide(BigDecimal.valueOf(IVA_BASE_16), 2, RoundingMode.HALF_UP);
      BigDecimal base =
          pagoFacturaDto.getMonto().subtract(impuesto).setScale(2, RoundingMode.HALF_UP);
      TrasladoDR trasladoDR =
          TrasladoDR.builder()
              .baseDR(base)
              .impuestoDR(IMPUESTO)
              .tipoFactorDR(TASA)
              .tasaOCuotaDR(TASA_O_CUOTA)
              .importeDR(impuesto)
              .build();

      importeP = importeP.add(impuesto);
      baseP = baseP.add(base);

      ImpuestosDR impuestosDR =
          ImpuestosDR.builder()
              .trasladosDR(TrasladosDR.builder().trasladoDR(ImmutableList.of(trasladoDR)).build())
              .build();
      DocumentoRelacionado documentoRelacionado =
          DocumentoRelacionado.builder()
              // TODO:VALIDATE DIFFERENT CURRENCIES
              .equivalenciaDR(BigDecimal.ONE)
              .idDocumento(facturaCustomIterate.getUuid())
              .folio(facturaCustomIterate.getFolio())
              .monedaDR(pagoDto.getMoneda())
              .numParcialidad(numeroParcialidad)
              .impSaldoAnt(saldoAnterior.setScale(2, RoundingMode.HALF_UP))
              .impPagado(pagoFacturaDto.getMonto().setScale(2, RoundingMode.HALF_UP))
              .impPagado(pagoFacturaDto.getMonto().setScale(2, RoundingMode.HALF_UP))
              .impSaldoInsoluto(
                  saldoAnterior
                      .subtract(pagoFacturaDto.getMonto())
                      .setScale(2, RoundingMode.HALF_UP))
              .objetoImpDR(PAGO_IMPUESTOS_GRAL)
              .impuestosDR(impuestosDR)
              .build();

      documentoRelacionados.add(documentoRelacionado);
      PagoComplemento pagoComplemento =
          PagoComplemento.builder()
              .folioOrigen(facturaCustomIterate.getFolio())
              .folio(complement.getFolio())
              .fechaPago(pagoDto.getFechaPago())
              .formaDePagoP(pago.getFormaDePagoP())
              .idDocumento(facturaCustomIterate.getUuid())
              .equivalenciaDR(documentoRelacionado.getEquivalenciaDR().toString())
              .monedaDr(documentoRelacionado.getMonedaDR())
              .numeroParcialidad(documentoRelacionado.getNumParcialidad())
              .importeSaldoAnterior(documentoRelacionado.getImpSaldoAnt())
              .importePagado(documentoRelacionado.getImpPagado())
              .importeSaldoInsoluto(documentoRelacionado.getImpSaldoInsoluto())
              .valido(true)
              .tipoCambio((pagoDto.getTipoDeCambio()))
              .build();
      facturaCustomIterate.getPagos().add(pagoComplemento);
      complement.getPagos().add(pagoComplemento);
      facturaCustomIterate.setSaldoPendiente(
          facturaCustomIterate.getSaldoPendiente().subtract(pagoFacturaDto.getMonto()));
    }

    Totales totales =
        Totales.builder()
            .montoTotalPagos(pagoDto.getMonto())
            .totalTrasladosBaseIVA16(baseP.setScale(2, RoundingMode.HALF_UP))
            .totalTrasladosImpuestoIVA16(importeP.setScale(2, RoundingMode.HALF_UP))
            .build();
    TrasladoP trasladoP =
        TrasladoP.builder()
            .importeP(importeP.setScale(2, RoundingMode.HALF_UP))
            .baseP(baseP.setScale(2, RoundingMode.HALF_UP))
            .tasaOCuotaP(TASA_O_CUOTA)
            .impuestoP(IMPUESTO)
            .tipoFactorP(TIPO_FACTOR)
            .build();
    Pagos pagos = Pagos.builder().totales(totales).build();
    pago.setImpuestosP(
        ImpuestosP.builder()
            .trasladosP(TrasladosP.builder().trasladoP(ImmutableList.of(trasladoP)).build())
            .build());
    pago.setRelacionados(documentoRelacionados.build());
    pagos.setPagos(ImmutableList.of(pago));
    return pagos;
  }

  private Cfdi buildCfdiComplement(FacturaCustom facturaCustom) {
    Cfdi cfdi =
        Cfdi.builder()
            .version(Constants.CFDI_40_VERSION)
            .fecha(CFDI_DATE_PATTERN)
            .serie(Constants.ComplementoPpdDefaults.SERIE)
            .folio(FacturaUtils.generateFolio())
            .subtotal(BigDecimal.ZERO)
            .moneda(Constants.ComplementoPpdDefaults.MONEDA)
            .total(BigDecimal.ZERO)
            .tipoDeComprobante(COMPROBANTE)
            .lugarExpedicion(facturaCustom.getCfdi().getLugarExpedicion())
            .exportacion(facturaCustom.getCfdi().getExportacion())
            .receptor(facturaCustom.getCfdi().getReceptor())
            .emisor(facturaCustom.getCfdi().getEmisor())
            .conceptos(ImmutableList.of(buildConceptoComplement()))
            .build();

    Receptor receptor = cfdi.getReceptor();
    if (Objects.isNull(receptor.getUsoCfdi()) || "*".equals(receptor.getUsoCfdi())) {
      receptor.setUsoCfdi("G03");
    }
    if (Objects.isNull(receptor.getRegimenFiscalReceptor())
        || "*".equals(receptor.getRegimenFiscalReceptor())) {
      receptor.setRegimenFiscalReceptor("601");
    }
    if (Objects.isNull(receptor.getDomicilioFiscalReceptor())
        || receptor.getDomicilioFiscalReceptor().isEmpty()) {
      receptor.setDomicilioFiscalReceptor("00000");
    }
    if (Objects.isNull(cfdi.getLugarExpedicion())
        || facturaCustom.getCfdi().getLugarExpedicion().isEmpty()) {
      facturaCustom.getCfdi().setLugarExpedicion("00000");
    }

    return cfdi;
  }

  private Concepto buildConceptoComplement() {
    return Concepto.builder()
        .claveProdServ(PAGO_CLAVE)
        .cantidad(BigDecimal.ONE)
        .claveUnidad(PAGO_UNIDAD)
        .descripcion(PAGO_DESC)
        .valorUnitario(BigDecimal.ZERO)
        .importe(BigDecimal.ZERO)
        .objetoImp(PAGO_IMPUESTOS)
        .build();
  }
}
