package com.business.unknow.services.services;

import static com.business.unknow.Constants.CANCEL_ACK;
import static com.business.unknow.Constants.PDF_COMPLEMENTO_TIMBRAR;
import static com.business.unknow.Constants.PDF_FACTURA_SIN_TIMBRAR;
import static com.business.unknow.Constants.PDF_FACTURA_TIMBRAR;
import static com.business.unknow.enums.TipoArchivo.PDF;
import static com.business.unknow.enums.TipoArchivo.TXT;
import static com.business.unknow.enums.TipoArchivo.XML;
import static com.business.unknow.enums.TipoDocumento.FACTURA;

import com.business.unknow.MailConstants;
import com.business.unknow.enums.FacturaStatus;
import com.business.unknow.enums.MetodosPago;
import com.business.unknow.enums.S3Buckets;
import com.business.unknow.enums.TipoComprobante;
import com.business.unknow.enums.TipoDocumento;
import com.business.unknow.model.config.MailContent;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.FacturaPdf;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.pagos.PagoFacturaDto;
import com.business.unknow.model.dto.services.ClientDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.CfdiPago;
import com.business.unknow.services.entities.Factura;
import com.business.unknow.services.mapper.FacturaMapper;
import com.business.unknow.services.repositories.facturas.CfdiPagoRepository;
import com.business.unknow.services.repositories.facturas.FacturaDao;
import com.business.unknow.services.repositories.facturas.FacturaRepository;
import com.business.unknow.services.services.evaluations.FacturaEvaluatorService;
import com.business.unknow.services.services.evaluations.TimbradoEvaluatorService;
import com.business.unknow.services.services.translators.RelacionadosTranslator;
import com.business.unknow.services.util.FacturaUtils;
import com.business.unknow.services.util.validators.InvoiceValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mx.ntlink.NtlinkUtilException;
import com.mx.ntlink.cfdi.mappers.CfdiMapper;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import com.mx.ntlink.cfdi.modelos.Impuesto;
import com.mx.ntlink.models.generated.Comprobante;
import com.mx.ntlink.util.NumberTranslatorUtil;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class FacturaService {

  @Autowired private ClientService clientService;

  @Autowired private ReportDataService reportDataService;

  @Autowired private MailService mailService;

  @Autowired private FilesService filesService;

  @Autowired private DownloaderService downloaderService;

  @Autowired private CfdiService cfdiService;

  @Autowired private CatalogService catalogService;

  @Autowired private FacturaExecutorService facturaExecutorService;

  @Autowired private PagoService pagoService;

  @Autowired private RelacionadosTranslator sustitucionTranslator;

  @Autowired private TimbradoEvaluatorService timbradoServiceEvaluator;

  @Autowired private FacturaEvaluatorService facturaServiceEvaluator;

  @Autowired private FacturaDao facturaDao;

  @Autowired private FacturaRepository repository;

  @Autowired private CfdiPagoRepository cfdiPagoRepository;

  @Autowired private CfdiMapper cfdiMapper;

  @Autowired private FacturaMapper mapper;

  @Value("${invoce.environment}")
  private String environment;

  private Specification<Factura> buildSearchFilters(Map<String, String> parameters) {
    String linea = (parameters.get("lineaEmisor") == null) ? "A" : parameters.get("lineaEmisor");

    log.info("Finding facturas by {}", parameters);

    return new Specification<Factura>() {

      private static final long serialVersionUID = -7435096122716669730L;

      @Override
      public Predicate toPredicate(
          Root<Factura> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("lineaEmisor"), linea)));
        // TODO move this logic into a enum class that handles all this logic
        if (parameters.get("solicitante") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("solicitante"), "%" + parameters.get("solicitante") + "%")));
        }
        if (parameters.get("emisor") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("razonSocialEmisor"), "%" + parameters.get("emisor") + "%")));
        }
        if (parameters.get("remitente") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("razonSocialRemitente"), "%" + parameters.get("remitente") + "%")));
        }

        if (parameters.get("status") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("statusFactura"), parameters.get("status"))));
        }

        if (parameters.get("tipoDocumento") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(
                      root.get("tipoDocumento"), parameters.get("tipoDocumento"))));
        }

        if (parameters.get("metodoPago") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("metodoPago"), parameters.get("metodoPago"))));
        }

        if (parameters.get("saldoPendiente") != null) {
          BigDecimal saldo = new BigDecimal(parameters.get("saldoPendiente"));
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.greaterThanOrEqualTo(root.get("saldoPendiente"), saldo)));
        }

        if (parameters.get("since") != null && parameters.get("to") != null) {
          java.sql.Date start = java.sql.Date.valueOf(LocalDate.parse(parameters.get("since")));
          java.sql.Date end =
              java.sql.Date.valueOf(LocalDate.parse(parameters.get("to")).plusDays(1));
          predicates.add(
              criteriaBuilder.and(criteriaBuilder.between(root.get("fechaCreacion"), start, end)));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }

  // FACTURAS
  public Page<FacturaCustom> getFacturasByParametros(Map<String, String> parameters) {

    Page<Factura> result;
    int page = (parameters.get("page") == null) ? 0 : Integer.valueOf(parameters.get("page"));
    int size = (parameters.get("size") == null) ? 10 : Integer.valueOf(parameters.get("size"));
    if (parameters.get("prefolio") != null) {
      result = repository.findByPreFolio(parameters.get("prefolio"), PageRequest.of(0, 10));
    } else {
      result =
          repository.findAll(
              buildSearchFilters(parameters),
              PageRequest.of(page, size, Sort.by("fechaCreacion").descending()));
    }
    return new PageImpl<>(
        mapper.getFacturaDtosFromEntities(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public ResourceFileDto getFacturaReportsByParams(Map<String, String> parameters)
      throws IOException {
    int page = (parameters.get("page") == null) ? 0 : Integer.valueOf(parameters.get("page"));
    int size = (parameters.get("size") == null) ? 10 : Integer.valueOf(parameters.get("size"));
    // TODO CREATE REPORTS TABLE IN BD
    parameters.put("tipoDocumento", "Factura");

    List<String> folios =
        repository.findAll(buildSearchFilters(parameters)).stream()
            .map(Factura::getFolio)
            .collect(Collectors.toList());

    List<String> headersOrder =
        Arrays.asList(
            "FOLIO",
            "FOLIO FISCAL",
            "FECHA EMISION",
            "RFC EMISOR",
            "EMISOR",
            "RFC RECEPTOR",
            "RECEPTOR",
            "TIPO DOCUMENTO",
            "PACK",
            "TIPO",
            "IMPUESTOS TRASLADADOS",
            "IMPUESTOS RETENIDOS",
            "SUBTOTAL",
            "TOTAL",
            "METDOD PAGO",
            "FORMA PAGO",
            "MONEDA",
            "ESTATUS",
            "CANCELACION",
            "LINEA",
            "PROMOTOR",
            "CANTIDAD",
            "CLAVE UNIDAD",
            "UNIDAD",
            "CLAVE PROD SERV",
            "DESCRIPCION",
            "VALOR UNITARIO",
            "IMPORTE",
            "SALDO PENDIENTE");

    var invoices =
        facturaDao.getInvoiceDetailsByFolios(folios).stream()
            .map(
                inv -> {
                  Map<String, Object> row = new HashMap<>();
                  row.put("FOLIO", inv.getFolio());
                  row.put("FOLIO FISCAL", inv.getFolioFiscal());
                  row.put("FECHA EMISION", inv.getFechaEmision());
                  row.put("RFC EMISOR", inv.getRfcEmisor());
                  row.put("EMISOR", inv.getEmisor());
                  row.put("RFC RECEPTOR", inv.getRfcReceptor());
                  row.put("RECEPTOR", inv.getReceptor());
                  row.put("TIPO DOCUMENTO", inv.getTipoDocumento());
                  row.put("PACK", inv.getPackFacturacion());
                  row.put("TIPO", inv.getTipoComprobante());
                  row.put("IMPUESTOS TRASLADADOS", inv.getImpuestosTrasladados());
                  row.put("IMPUESTOS RETENIDOS", inv.getImpuestosRetenidos());
                  row.put("SUBTOTAL", inv.getSubtotal());
                  row.put("TOTAL", inv.getTotal());
                  row.put("METODO PAGO", inv.getMetodoPago());
                  row.put("FORMA PAGO", inv.getFormaPago());
                  row.put("MONEDA", inv.getMoneda());
                  row.put("ESTATUS", inv.getStatusFactura());
                  row.put("CANCELACION", inv.getFechaCancelacion());
                  row.put("LINEA", inv.getLineaEmisor());
                  row.put("PROMOTOR", inv.getCorreoPromotor());
                  row.put("CANTIDAD", inv.getCantidad());
                  row.put("CLAVE UNIDAD", inv.getClaveUnidad());
                  row.put("UNIDAD", inv.getUnidad());
                  row.put("CLAVE PROD SERV", inv.getClaveProdServ());
                  row.put("DESCRIPCION", inv.getDescripcion());
                  row.put("VALOR UNITARIO", inv.getValorUnitario());
                  row.put("IMPORTE", inv.getImporte());
                  row.put("SALDO PENDIENTE", inv.getSaldoPendiente());
                  return row;
                })
            .collect(Collectors.toList());

    return downloaderService.generateBase64Report("REPORTE DE FACTURAS", invoices, headersOrder);
  }

  public ResourceFileDto getComplementoReportsByParams(Map<String, String> parameters)
      throws IOException {
    int page = (parameters.get("page") == null) ? 0 : Integer.valueOf(parameters.get("page"));
    int size = (parameters.get("size") == null) ? 10 : Integer.valueOf(parameters.get("size"));
    // TODO CREATE REPORTS TABLE IN BD

    parameters.put("tipoDocumento", "Complemento");

    List<String> folios =
        repository.findAll(buildSearchFilters(parameters)).stream()
            .map(Factura::getFolio)
            .collect(Collectors.toList());

    List<String> headersOrder =
        Arrays.asList(
            "FOLIO",
            "FOLIO FISCAL",
            "FECHA EMISION",
            "RFC EMISOR",
            "EMISOR",
            "RFC RECEPTOR",
            "RECEPTOR",
            "TIPO DOCUMENTO",
            "PACK",
            "TIPO",
            "IMPUESTOS TRASLADADOS",
            "IMPUESTOS RETENIDOS",
            "SUBTOTAL",
            "TOTAL",
            "METDOD PAGO",
            "FORMA PAGO",
            "MONEDA",
            "ESTATUS",
            "CANCELACION",
            "FOLIO FISCAL PAGO",
            "IMPORTE",
            "SALDO ANTERIOR",
            "SALDO INSOLUTO",
            "PARCIALIDAD",
            "FECHA PAGO");

    var complements =
        facturaDao.getComplementsDetailsByFolios(folios).stream()
            .map(
                inv -> {
                  Map<String, Object> row = new HashMap<>();
                  row.put("FOLIO", inv.getFolioPago());
                  row.put("FOLIO FISCAL", inv.getFolioFiscal());
                  row.put("FECHA EMISION", inv.getFechaEmision());
                  row.put("RFC EMISOR", inv.getRfcEmisor());
                  row.put("EMISOR", inv.getEmisor());
                  row.put("RFC RECEPTOR", inv.getRfcReceptor());
                  row.put("RECEPTOR", inv.getReceptor());
                  row.put("TIPO DOCUMENTO", inv.getTipoDocumento());
                  row.put("PACK", inv.getPackFacturacion());
                  row.put("TIPO", inv.getTipoComprobante());
                  row.put("IMPUESTOS TRASLADADOS", inv.getImpuestosTrasladados());
                  row.put("IMPUESTOS RETENIDOS", inv.getImpuestosRetenidos());
                  row.put("SUBTOTAL", inv.getSubtotal());
                  row.put("TOTAL", inv.getTotal());
                  row.put("METODO PAGO", inv.getMetodoPago());
                  row.put("FORMA PAGO", inv.getFormaPago());
                  row.put("MONEDA", inv.getMoneda());
                  row.put("ESTATUS", inv.getStatusFactura());
                  row.put("CANCELACION", inv.getFechaCancelacion());
                  row.put("FOLIO FISCAL PAGO", inv.getFolioFiscalPago());
                  row.put("IMPORTE", inv.getImportePagado());
                  row.put("SALDO ANTERIOR", inv.getSaldoAnterior());
                  row.put("SALDO INSOLUTO", inv.getSaldoInsoluto());
                  row.put("PARCIALIDAD", inv.getNumeroParcialidad());
                  row.put("FECHA PAGO", inv.getFechaPago());
                  return row;
                })
            .collect(Collectors.toList());

    return downloaderService.generateBase64Report("COMPLEMENTOS", complements, headersOrder);
  }

  public FacturaCustom getComplementoByIdCfdiAnParcialidad(String folio, Integer parcialidad) {
    List<CfdiPago> pagos = cfdiPagoRepository.findByIdCfdiAndParcialidad(folio, parcialidad);
    Optional<CfdiPago> pago = pagos.stream().findFirst();
    if (pago.isPresent()) {
      return getFacturaBaseByFolio(pago.get().getFolio());
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("La factura con el pre-folio %s no existe", folio));
    }
  }

  public FacturaCustom getFacturaByFolio(String folio) {
    try {
      mapper.getFacturaDtoFromEntity(
          repository
              .findByFolio(folio)
              .orElseThrow(
                  () ->
                      new ResponseStatusException(
                          HttpStatus.NOT_FOUND,
                          String.format("La factura con el folio %s no existe", folio))));

      InputStream is =
          filesService.getS3InputStream(S3Buckets.CFDIS, String.format("%s.json", folio));

      return new ObjectMapper().readValue(is.readAllBytes(), FacturaCustom.class);
    } catch (IOException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Error recuperando detalles de la factura");
    }
  }

  public FacturaCustom getBaseFacturaByFolio(String folio) {
    return mapper.getFacturaDtoFromEntity(
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el folio %s no existe", folio))));
  }

  public FacturaCustom getFacturaBaseByFolio(String folio) {
    return mapper.getFacturaDtoFromEntity(
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el folio %S no existe", folio))));
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom createFacturaCustom(FacturaCustom facturaCustom)
      throws InvoiceManagerException, NtlinkUtilException {
    facturaCustom =
        assignFacturaData(
            facturaCustom, facturaDao.getCantidadFacturasOfTheCurrentMonthByTipoDocumento());
    InvoiceValidator.validate(facturaCustom, facturaCustom.getFolio());
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
    Factura save = repository.save(mapper.getEntityFromFacturaCustom(facturaCustom));
    facturaCustom.setFechaCreacion(save.getFechaCreacion());
    facturaCustom.setFechaActualizacion(save.getFechaActualizacion());
    filesService.sendXmlToS3(facturaCustom.getFolio(), comprobante);
    filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
    FacturaPdf facturaPdf = mapper.getFacturaPdfFromFacturaCustom(facturaCustom);
    facturaPdf.setCfdi(comprobante);
    byte[] pdf = FacturaUtils.generateFacturaPdf(facturaPdf, PDF_FACTURA_SIN_TIMBRAR);
    filesService.sendFileToS3(facturaCustom.getFolio(), pdf, ".pdf", S3Buckets.CFDIS);
    reportDataService.upsertReportData(facturaCustom.getCfdi());
    return facturaCustom;
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom updateFacturaCustom(String folio, FacturaCustom facturaCustom)
      throws InvoiceManagerException, NtlinkUtilException {
    Factura factura =
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el folio %d no existe", folio)));
    facturaServiceEvaluator.facturaStatusValidation(facturaCustom);
    facturaCustom.setCfdi(cfdiService.updateCfdi(facturaCustom.getCfdi()));
    InvoiceValidator.validate(facturaCustom, facturaCustom.getFolio());
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
    Factura entityFromDto = mapper.getEntityFromFacturaCustom(facturaCustom);
    entityFromDto.setId(factura.getId());
    repository.save(entityFromDto);
    filesService.sendXmlToS3(facturaCustom.getFolio(), comprobante);
    filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
    FacturaPdf facturaPdf = mapper.getFacturaPdfFromFacturaCustom(facturaCustom);
    facturaPdf.setCfdi(comprobante);
    byte[] pdf = FacturaUtils.generateFacturaPdf(facturaPdf, PDF_FACTURA_SIN_TIMBRAR);
    filesService.sendFileToS3(facturaCustom.getFolio(), pdf, ".pdf", S3Buckets.CFDIS);
    reportDataService.upsertReportData(facturaCustom.getCfdi());
    return facturaCustom;
  }

  public FacturaCustom updateTotalAndSaldoFactura(
      String folioCfdi, Optional<BigDecimal> newTotal, Optional<BigDecimal> pago)
      throws InvoiceManagerException, NtlinkUtilException {
    Factura factura =
        repository
            .findByFolio(folioCfdi)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el folio %dsno existe", folioCfdi)));
    Cfdi cfdi = cfdiService.getCfdiByFolio(folioCfdi);
    BigDecimal total = newTotal.isPresent() ? newTotal.get() : factura.getTotal();
    BigDecimal montoPagado =
        pagoService.findPagosByFolio(factura.getFolio()).stream()
            .filter(p -> !"CREDITO".equals(p.getFormaPago()))
            .map(
                p ->
                    cfdi.getMoneda().equals(p.getMoneda())
                        ? p.getMonto()
                        : p.getMonto().divide(p.getTipoDeCambio(), 2, RoundingMode.HALF_UP))
            .reduce(BigDecimal.ZERO, (p1, p2) -> p1.add(p2));

    if (pago.isPresent()) {
      montoPagado = montoPagado.add(pago.get());
    }
    BigDecimal saldo = total.subtract(montoPagado);
    InvoiceValidator.checkNotNegative(saldo, "Saldo pendiente");
    factura.setTotal(total);
    factura.setSaldoPendiente(saldo);
    return mapper.getFacturaDtoFromEntity(repository.save(factura));
  }

  public FacturaCustom updateTotalAndSaldoFacturaComplemento(
      String folio, Optional<BigDecimal> newTotal, Optional<BigDecimal> deuda)
      throws InvoiceManagerException {
    Factura factura =
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el folio %s no existe", folio)));
    if (newTotal.isPresent()) {
      factura.setTotal(newTotal.get());
    }
    if (deuda.isPresent()) {
      InvoiceValidator.checkNotNegative(deuda.get(), "Saldo pendiente");
      factura.setSaldoPendiente(deuda.get());
    }
    return mapper.getFacturaDtoFromEntity(repository.save(factura));
  }

  public FacturaCustom updateTotalAndSaldoComplemento(
      String folio, Optional<BigDecimal> newTotal, Optional<BigDecimal> pago)
      throws InvoiceManagerException {
    Factura factura =
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el folio %s no existe", folio)));
    BigDecimal total = newTotal.isPresent() ? newTotal.get() : factura.getTotal();
    BigDecimal montoPagado =
        pagoService.findPagosByFolio(factura.getFolio()).stream()
            .filter(p -> !"CREDITO".equals(p.getFormaPago()))
            .map(p -> p.getMonto())
            .reduce(BigDecimal.ZERO, (p1, p2) -> p1.add(p2));

    if (pago.isPresent()) {
      montoPagado = montoPagado.add(pago.get());
    }
    BigDecimal saldo = total.subtract(montoPagado);
    InvoiceValidator.checkNotNegative(saldo, "Saldo pendiente");
    factura.setTotal(total);
    factura.setSaldoPendiente(saldo);
    return mapper.getFacturaDtoFromEntity(repository.save(factura));
  }

  public FacturaCustom updateFacturaStatus(String folio, FacturaStatus status) {
    Factura factura =
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el pre-folio %s no existe", folio)));
    factura.setStatusFactura(status.getValor());
    return mapper.getFacturaDtoFromEntity(repository.save(factura));
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public void deleteFactura(String folio) throws InvoiceManagerException, NtlinkUtilException {
    Factura fact =
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el folio %s no existe", folio)));
    repository.delete(fact);
    reportDataService.deleteReportData(fact.getFolio());
  }

  public FacturaCustom createComplemento(String folio, PagoDto pagoDto)
      throws InvoiceManagerException, NtlinkUtilException {
    pagoDto.setMonto(pagoDto.getMonto().setScale(2));
    FacturaCustom facturaDto = getBaseFacturaByFolio(folio);
    List<FacturaCustom> facturas = new ArrayList<>();
    List<PagoFacturaDto> facturaPagos = new ArrayList<>();
    PagoFacturaDto facturaPagoDto = new PagoFacturaDto();
    facturaPagoDto.setMonto(pagoDto.getMonto());
    facturaPagoDto.setFolio(folio);
    facturaPagos.add(facturaPagoDto);
    facturas.add(facturaDto);
    pagoDto.setFacturas(facturaPagos);
    // return generateComplemento(facturas, pagoDto);
    return null;
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom stamp(String folio, FacturaCustom facturaCustom)
      throws InvoiceManagerException, NtlinkUtilException {
    FacturaCustom factura = getBaseFacturaByFolio(facturaCustom.getFolio());
    InvoiceValidator.validate(facturaCustom, folio);
    List<PagoDto> pagosFactura = new ArrayList<>();
    if (FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())) {
      pagosFactura = pagoService.findPagosByFolio(folio);
    }
    timbradoServiceEvaluator.facturaTimbradoValidation(facturaCustom, pagosFactura);
    facturaCustom = facturaExecutorService.stampInvoice(facturaCustom);
    Factura entityFromDto = mapper.getEntityFromFacturaCustom(facturaCustom);
    entityFromDto.setId(factura.getId());
    repository.save(entityFromDto);
    filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
    filesService.sendFileToS3(
        facturaCustom.getFolio(),
        facturaCustom.getXml().getBytes(),
        XML.getFormat(),
        S3Buckets.CFDIS);
    filesService.sendFileToS3(
        facturaCustom.getFolio(),
        getPdfFromFactura(
            facturaCustom,
            FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())
                ? PDF_FACTURA_TIMBRAR
                : PDF_COMPLEMENTO_TIMBRAR),
        PDF.getFormat(),
        S3Buckets.CFDIS);
    if (!"dev".equals(environment)) {
      sendMail(facturaCustom);
    }
    return facturaCustom;
  }

  public FacturaCustom sendMail(FacturaCustom facturaCustom) {
    try {
      ClientDto clientDto = clientService.getClientByRFC(facturaCustom.getRfcRemitente());
      String xml =
          filesService.getS3File(S3Buckets.CFDIS, facturaCustom.getFolio().concat(XML.getFormat()));
      String pdf =
          filesService.getS3File(S3Buckets.CFDIS, facturaCustom.getFolio().concat(PDF.getFormat()));
      MailContent.MailFile mailXml =
          MailContent.MailFile.builder().data(xml).type(TXT.getByteArrayData()).build();
      MailContent.MailFile mailPdf =
          MailContent.MailFile.builder().data(pdf).type(PDF.getByteArrayData()).build();
      Map<String, MailContent.MailFile> files =
          ImmutableMap.of(
              facturaCustom.getFolio().concat(XML.getFormat()), mailXml,
              facturaCustom.getFolio().concat(PDF.getFormat()), mailPdf);
      MailContent mailContent =
          MailContent.builder()
              .subject(MailConstants.STAMP_INVOICE_SUBJECT)
              .bodyText(
                  String.format(
                      MailConstants.STAMP_INVOICE_BODY_MESSAGE,
                      clientDto.getRazonSocial(),
                      facturaCustom.getFolio()))
              .attachments(files)
              .build();
      mailService.sendEmail(ImmutableList.of(clientDto.getCorreo()), mailContent);
      return facturaCustom;
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          String.format(
              "Error mandando Correo para la factura con folio %s", facturaCustom.getFolio()));
    }
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom cancelInvoice(String folio, FacturaCustom facturaCustom)
      throws InvoiceManagerException {
    Factura factura =
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el folio %d no existe", folio)));
    InvoiceValidator.validate(facturaCustom, folio);
    timbradoServiceEvaluator.invoiceCancelValidation(facturaCustom);
    if (FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())) {
      facturaCustom = facturaExecutorService.cancelInvoice(facturaCustom);
      Factura entityFromDto = mapper.getEntityFromFacturaCustom(facturaCustom);
      entityFromDto.setId(factura.getId());
      repository.save(entityFromDto);
      filesService.sendFileToS3(
          facturaCustom.getFolio().concat(CANCEL_ACK),
          facturaCustom.getAcuse().getBytes(),
          XML.getFormat(),
          S3Buckets.CFDIS);
      filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
    } else {
      // TODO: IMPLEMENT CANCEL COMPLEMENT  LOGIC
      throw new InvoiceManagerException(
          "Cancelacion de complemento no implementada", HttpStatus.NOT_IMPLEMENTED.value());
    }
    return facturaCustom;
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom generateComplemento(List<FacturaCustom> facturas, PagoDto pagoPpd) {
    return null;
  }
  /* throws InvoiceManagerException, NtlinkUtilException {
  if (facturas.stream()
      .anyMatch(a -> !a.getStatusFactura().equals(FacturaStatus.TIMBRADA.getValor()))) {
    throw new InvoiceManagerException(
        "Una factura no esta timbrada",
        "Una factura no esta timbrada",
        HttpStatus.BAD_REQUEST.value());
  }
  Optional<FacturaCustom> primerfactura = facturas.stream().findFirst();
  if (primerfactura.isPresent()) {
    FacturaCustom factura = getFacturaByFolio(primerfactura.get().getFolio());
    factura.setPackFacturacion(primerfactura.get().getPackFacturacion());
    FacturaContext factContext =
        facturaBuilderService.buildFacturaContextPagoPpdCreation(
            pagoPpd, factura, factura.getFolio());
    Cfdi cfdi = facturaBuilderService.buildFacturaComplementoCreation(factContext);
    FacturaCustom complemento =
        facturaBuilderService.buildFacturaDtoPagoPpdCreation(factura, pagoPpd);
    List<CfdiPagoDto> cfdiPagos =
        facturaBuilderService.buildFacturaComplementoPagos(factura, pagoPpd, facturas);
    cfdi.setComplemento(ImmutableList.of(new ComplementoDto()));
    // TODO validate Complementos
    // cfdi.getComplemento().setPagos(cfdiPagos);
    complemento.setCfdi(cfdi);
    /*facturaDefaultValues.assignaDefaultsComplemento(
        complemento, facturaDao.getCantidadFacturasOfTheCurrentMonthByTipoDocumento());
    Cfdi createdCfdi = cfdiService.insertNewCfdi(complemento.getCfdi());
    Factura fact = mapper.getEntityFromFacturaCustom(complemento);
    fact.setFolio(createdCfdi.getFolio());*/
  // TODO VALIDAR PAGOS
  /*for (FacturaDto dto : facturas) {
        Optional<CfdiPagoDto> cfdiPago =
            complemento.getCfdi().getComplemento().getPagos().stream()
                .filter(a -> a.getFolio().equals(dto.getFolio()))
                .findFirst();
        updateTotalAndSaldoFacturaComplemento(
            dto.getIdCfdi(),
            Optional.of(dto.getTotal()),
            Optional.of(cfdiPago.get().getImporteSaldoInsoluto()));
      }
      return null;
      // return mapper.getFacturaDtoFromEntity(repository.save(fact));
    } else {
      throw new InvoiceManagerException(
          "Debe tener por lo menos un pago",
          "No asigno el pago a una factura",
          HttpStatus.BAD_REQUEST.value());
    }
  }*/

  public FacturaCustom postRelacion(FacturaCustom dto, TipoDocumento tipoDocumento)
      throws InvoiceManagerException, NtlinkUtilException {
    FacturaCustom facturaDto = getFacturaByFolio(dto.getFolio());
    if (FACTURA.getDescripcion().equals(facturaDto.getTipoDocumento())) {
      switch (tipoDocumento) {
        case FACTURA:
          sustitucionTranslator.sustitucionFactura(facturaDto);
          break;
        case NOTA_CREDITO:
          sustitucionTranslator.notaCreditoFactura(facturaDto);
          break;
        default:
          throw new InvoiceManagerException(
              "The type of document not supported",
              String.format("The type of document %s not valid", facturaDto.getTipoDocumento()),
              HttpStatus.BAD_REQUEST.value());
      }
      facturaDto.setIdCfdiRelacionadoPadre(dto.getIdCfdi());
      facturaDto = createFacturaCustom(facturaDto);
      FacturaCustom facturaAnterior = getFacturaByFolio(dto.getFolio());
      facturaAnterior.setIdCfdiRelacionado(facturaDto.getIdCfdi());
      repository.save(mapper.getEntityFromFacturaCustom(facturaAnterior));
      return dto;
    } else {
      throw new InvoiceManagerException(
          "El tipo de documento en la relacion no es de tipo factura",
          HttpStatus.BAD_REQUEST.value());
    }
  }

  private FacturaCustom assignFacturaData(FacturaCustom facturaCustom, int amount)
      throws NtlinkUtilException, InvoiceManagerException {
    String folio =
        facturaCustom.getFolio() == null ? FacturaUtils.generateFolio() : facturaCustom.getFolio();
    Cfdi cfdi = cfdiService.recalculateCfdiAmmounts(facturaCustom.getCfdi());
    cfdi.setFolio(folio);
    return facturaCustom.toBuilder()
        .cfdi(cfdi)
        .total(facturaCustom.getCfdi().getTotal())
        .saldoPendiente(facturaCustom.getCfdi().getTotal())
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
        .formaPagoDesc(
            catalogService
                .getPaymentFormByKey(facturaCustom.getCfdi().getFormaPago())
                .getDescripcion())
        .metodoPagoDesc(
            MetodosPago.findByValor(facturaCustom.getCfdi().getMetodoPago()).getDescripcion())
        .tipoDeComprobanteDesc(
            TipoComprobante.findByValor(facturaCustom.getCfdi().getTipoDeComprobante())
                .getDescripcion())
        .logotipo(
            facturaCustom.getLogotipo() == null
                ? filesService
                    .getResourceFileByResourceReferenceAndType(
                        S3Buckets.EMPRESAS, facturaCustom.getRfcEmisor(), "LOGO")
                    .getData()
                : facturaCustom.getLogotipo())
        .impuestosTrasladados(
            facturaCustom.getCfdi().getImpuestos().stream()
                .collect(
                    Collectors.reducing(
                        BigDecimal.ZERO, Impuesto::getTotalImpuestosTrasladados, BigDecimal::add)))
        .impuestosRetenidos(
            facturaCustom.getCfdi().getImpuestos().stream()
                .collect(
                    Collectors.reducing(
                        BigDecimal.ZERO, Impuesto::getTotalImpuestosRetenidos, BigDecimal::add)))
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
  }

  private byte[] getPdfFromFactura(FacturaCustom facturaCustom, String template)
      throws InvoiceManagerException, NtlinkUtilException {
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
    FacturaPdf facturaPdf = mapper.getFacturaPdfFromFacturaCustom(facturaCustom);
    // TODO:REFACTOR ATTRIBUTE NAME TO COMPROBANTE
    facturaPdf.setCfdi(comprobante);
    facturaPdf.setLogotipo(
        filesService
            .getResourceFileByResourceReferenceAndType(
                S3Buckets.EMPRESAS, facturaCustom.getRfcEmisor(), "LOGO")
            // TODO REFACTOR CODE TO STOP USING  DEPRECATED METHOD
            .getData());
    byte[] pdf = FacturaUtils.generateFacturaPdf(facturaPdf, template);
    return pdf;
  }
}
