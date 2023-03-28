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
import static com.business.unknow.Constants.FacturaComplemento.USO_CFDI;
import static com.business.unknow.Constants.IVA_BASE_16;
import static com.business.unknow.Constants.IVA_IMPUESTO_16;
import static com.business.unknow.enums.FacturaStatus.VALIDACION_TESORERIA;
import static com.business.unknow.enums.FormaPago.TRANSFERENCIA_ELECTRONICA;
import static com.unknown.models.generated.CTipoFactor.TASA;

import com.business.unknow.Constants;
import com.business.unknow.enums.FacturaStatus;
import com.business.unknow.enums.MetodosPago;
import com.business.unknow.enums.PackFacturarionEnum;
import com.business.unknow.enums.TipoComprobante;
import com.business.unknow.enums.TipoDocumento;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.PagoComplemento;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.pagos.PagoFacturaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Client;
import com.business.unknow.services.entities.Empresa;
import com.business.unknow.services.repositories.ClientRepository;
import com.business.unknow.services.repositories.EmpresaRepository;
import com.business.unknow.services.util.FacturaUtils;
import com.google.common.collect.ImmutableList;
import com.unknown.cfdi.modelos.Cfdi;
import com.unknown.cfdi.modelos.Concepto;
import com.unknown.cfdi.modelos.Emisor;
import com.unknown.cfdi.modelos.Impuesto;
import com.unknown.cfdi.modelos.Receptor;
import com.unknown.cfdi.modelos.complementos.pagos.DocumentoRelacionado;
import com.unknown.cfdi.modelos.complementos.pagos.ImpuestosDR;
import com.unknown.cfdi.modelos.complementos.pagos.ImpuestosP;
import com.unknown.cfdi.modelos.complementos.pagos.Pago;
import com.unknown.cfdi.modelos.complementos.pagos.Pagos;
import com.unknown.cfdi.modelos.complementos.pagos.Totales;
import com.unknown.cfdi.modelos.complementos.pagos.TrasladoDR;
import com.unknown.cfdi.modelos.complementos.pagos.TrasladoP;
import com.unknown.cfdi.modelos.complementos.pagos.TrasladosDR;
import com.unknown.cfdi.modelos.complementos.pagos.TrasladosP;
import com.unknown.error.PptUtilException;
import com.unknown.util.NumberTranslatorUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

  @Autowired private ClientRepository clientRepo;

  @Autowired private EmpresaRepository companyRepo;

  public FacturaCustom assignFacturaData(FacturaCustom facturaCustom, int amount)
      throws PptUtilException {
    String folio =
        facturaCustom.getFolio() == null ? FacturaUtils.generateFolio() : facturaCustom.getFolio();
    Cfdi cfdi = cfdiService.recalculateCfdiAmmounts(facturaCustom.getCfdi());
    cfdi.setFecha(CFDI_DATE_PATTERN);
    cfdi.setFolio(folio);
    facturaCustom =
        facturaCustom.toBuilder()
            .cfdi(cfdi)
            .version(Constants.CFDI_40_VERSION)
            .total(cfdi.getTotal())
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

  public FacturaCustom assignDescData(FacturaCustom facturaCustom) throws PptUtilException {
    String formaDePago =
        TipoDocumento.COMPLEMENTO.getDescripcion().equals(facturaCustom.getTipoDocumento())
            ? facturaCustom.getPagos().stream()
                .map(PagoComplemento::getFormaDePagoP)
                .findFirst()
                .orElse(TRANSFERENCIA_ELECTRONICA.getClave())
            : facturaCustom.getCfdi().getFormaPago();
    return facturaCustom.toBuilder()
        .totalDesc(
            NumberTranslatorUtil.getStringNumber(
                facturaCustom.getTotal(), facturaCustom.getCfdi().getMoneda()))
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
                : catalogService.getPaymentFormByKey(formaDePago).getDescripcion())
        .metodoPagoDesc(
            MetodosPago.findByValor(facturaCustom.getCfdi().getMetodoPago()).getDescripcion())
        .tipoDeComprobanteDesc(
            TipoComprobante.findByValor(facturaCustom.getCfdi().getTipoDeComprobante())
                .getDescripcion())
        .build();
  }

  public FacturaCustom assignComplementData(
      List<FacturaCustom> parentInvoices, PagoDto pagoDto, String prefolio)
      throws InvoiceManagerException {
    String folio = FacturaUtils.generateFolio();

    FacturaCustom facturaCustom =
        parentInvoices.stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new InvoiceManagerException(
                        "No hay facturas relacionadas al complemento de pago",
                        HttpStatus.CONFLICT.value()));

    Cfdi cfdi = buildCfdiComplement(facturaCustom);
    cfdi.setFolio(folio);
    FacturaCustom complement =
        FacturaCustom.builder()
            .folio(folio)
            .version(Constants.CFDI_40_VERSION)
            .preFolio(prefolio)
            .total(pagoDto.getMonto())
            .packFacturacion(PackFacturarionEnum.NTLINK.name())
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

  private Cfdi buildCfdiComplement(FacturaCustom fact) throws InvoiceManagerException {

    Empresa company =
        companyRepo
            .findByRfc(fact.getRfcEmisor())
            .orElseThrow(
                () ->
                    new InvoiceManagerException(
                        String.format(
                            "La empresa con RFC %s no existe en el sistema", fact.getRfcEmisor()),
                        HttpStatus.CONTINUE.value()));
    String companyAddress =
        String.format(
            "%s %s, %s, %s, %s CP : %s",
            company.getCalle(),
            company.getNoExterior(),
            company.getColonia(),
            company.getMunicipio(),
            company.getEstado(),
            company.getCp());
    fact.setDireccionEmisor(companyAddress.toUpperCase());
    Emisor emisor =
        Emisor.builder()
            .rfc(company.getRfc())
            .nombre(company.getNombre())
            .regimenFiscal(company.getRegimenFiscal())
            .direccion(companyAddress.toUpperCase())
            .build();

    Receptor receptor = fact.getCfdi().getReceptor();
    Optional<Client> client =
        clientRepo.findByCorreoPromotorAndClient(fact.getSolicitante(), fact.getRfcRemitente());
    if (client.isPresent()) { // LINE A
      String address =
          String.format(
                  "%s %s, %s, %s, %s CP : %s",
                  client.get().getCalle(),
                  client.get().getNoExterior(),
                  client.get().getColonia(),
                  client.get().getMunicipio(),
                  client.get().getEstado(),
                  client.get().getCp())
              .toUpperCase();
      receptor =
          Receptor.builder()
              .nombre(client.get().getRazonSocial())
              .rfc(client.get().getRfc())
              .domicilioFiscalReceptor(client.get().getCp())
              .regimenFiscalReceptor(client.get().getRegimenFiscal())
              .direccion(address)
              .usoCfdi(USO_CFDI)
              .build();
      fact.setDireccionReceptor(address);
    } else { // LINE B,C,D and E
      Optional<Empresa> clientCompany = companyRepo.findByRfc(fact.getRfcRemitente());
      if (clientCompany.isPresent()) {
        String address =
            String.format(
                    "%s %s, %s, %s, %s CP. %s",
                    clientCompany.get().getCalle(),
                    clientCompany.get().getNoExterior(),
                    clientCompany.get().getColonia(),
                    clientCompany.get().getMunicipio(),
                    clientCompany.get().getEstado(),
                    clientCompany.get().getCp())
                .toUpperCase();

        receptor =
            Receptor.builder()
                .nombre(clientCompany.get().getRazonSocial())
                .rfc(clientCompany.get().getRfc())
                .domicilioFiscalReceptor(clientCompany.get().getCp())
                .regimenFiscalReceptor(clientCompany.get().getRegimenFiscal())
                .direccion(address)
                .usoCfdi(USO_CFDI)
                .build();
        fact.setDireccionReceptor(address);
      }
    }

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
            .metodoPago(Constants.ComplementoPpdDefaults.METODO_PAGO)
            .formaPago(Constants.ComplementoPpdDefaults.FORMA_PAGO)
            .lugarExpedicion(company.getCp())
            .exportacion("01")
            .receptor(receptor)
            .emisor(emisor)
            .conceptos(ImmutableList.of(buildConceptoComplement()))
            .build();
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
