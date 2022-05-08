package com.business.unknow.services.services;

import com.business.unknow.enums.FacturaStatusEnum;
import com.business.unknow.enums.MetodosPagoEnum;
import com.business.unknow.enums.PackFacturarionEnum;
import com.business.unknow.enums.S3BucketsEnum;
import com.business.unknow.enums.TipoComprobanteEnum;
import com.business.unknow.enums.TipoDocumentoEnum;
import com.business.unknow.enums.TipoEmail;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.cfdi.CfdiPagoDto;
import com.business.unknow.model.dto.cfdi.ComplementoDto;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.pagos.PagoFacturaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.cfdi.CfdiPago;
import com.business.unknow.services.entities.factura.Factura;
import com.business.unknow.services.mapper.factura.FacturaMapper;
import com.business.unknow.services.repositories.facturas.CfdiPagoRepository;
import com.business.unknow.services.repositories.facturas.FacturaDao;
import com.business.unknow.services.repositories.facturas.FacturaRepository;
import com.business.unknow.services.services.builder.FacturaBuilderService;
import com.business.unknow.services.services.builder.TimbradoBuilderService;
import com.business.unknow.services.services.evaluations.FacturaEvaluatorService;
import com.business.unknow.services.services.evaluations.TimbradoEvaluatorService;
import com.business.unknow.services.services.executor.NtinkExecutorService;
import com.business.unknow.services.services.executor.TimbradoExecutorService;
import com.business.unknow.services.services.translators.FacturaTranslator;
import com.business.unknow.services.services.translators.RelacionadosTranslator;
import com.business.unknow.services.util.FacturaDefaultValues;
import com.business.unknow.services.util.FacturaUtils;
import com.business.unknow.services.util.validators.FacturaValidator;
import com.google.common.collect.ImmutableList;
import com.mx.ntlink.NtlinkUtilException;
import com.mx.ntlink.cfdi.mappers.CfdiMapper;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import com.mx.ntlink.cfdi.modelos.Impuesto;
import com.mx.ntlink.models.generated.Comprobante;
import com.mx.ntlink.util.NumberTranslatorUtil;
import java.io.IOException;
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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired private FacturaDao facturaDao;

  @Autowired private FacturaRepository repository;

  @Autowired private CfdiPagoRepository cfdiPagoRepository;

  @Autowired private CfdiService cfdiService;

  @Autowired private DownloaderService downloaderService;

  @Autowired private FacturaMapper mapper;

  @Autowired private TimbradoEvaluatorService timbradoServiceEvaluator;

  @Autowired private FacturaEvaluatorService facturaServiceEvaluator;

  @Autowired private FacturaBuilderService facturaBuilderService;

  @Autowired private TimbradoBuilderService timbradoBuilderService;

  @Autowired private FacturaTranslator facturaTranslator;

  @Autowired private NtinkExecutorService ntinkExecutorService;

  @Autowired private TimbradoExecutorService timbradoExecutorService;

  @Autowired private DevolucionService devolucionService;

  @Autowired private PDFService pdfService;

  @Autowired private FacturaDefaultValues facturaDefaultValues;

  @Autowired private PagoService pagoService;

  @Autowired private RelacionadosTranslator sustitucionTranslator;

  @Autowired private FilesService filesService;

  @Autowired private CfdiMapper cfdiMapper;

  @Autowired private FacturaValidator validator;

  @Autowired private CatalogsService catalogService;

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

  public FacturaCustom getFacturaByFolio(String folio) throws NtlinkUtilException {
    FacturaCustom factura =
        mapper.getFacturaDtoFromEntity(
            repository
                .findByFolio(folio)
                .orElseThrow(
                    () ->
                        new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            String.format("La factura con el folio %s no existe", folio))));
    // TODO recover factura metadata from S3 instead from DB table
    factura.setCfdi(cfdiService.getCfdiByFolio(folio));
    return factura;
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
    validator.validate(facturaCustom, facturaCustom.getFolio());
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
    Factura save = repository.save(mapper.getEntityFromFacturaCustom(facturaCustom));
    facturaCustom.setFechaCreacion(save.getFechaCreacion());
    facturaCustom.setFechaActualizacion(save.getFechaActualizacion());
    filesService.sendXmlToS3(facturaCustom.getFolio(), comprobante);
    filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
    // TODO enable pdf generation
    // FacturaPdf facturaPdf = mapper.getFacturaPdfFromFacturaCustom(facturaCustom);
    // facturaPdf.setCfdi(comprobante);
    // byte[] pdf = FacturaUtils.generateFacturaPdf(facturaPdf, PDF_FACTURA_SIN_TIMBRAR);
    // filesService.sendFileToS3(facturaCustom.getFolio(), pdf, ".pdf");
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
    facturaCustom.setCfdi(
        cfdiService.updateCfdiBody(facturaCustom.getFolio(), facturaCustom.getCfdi()));
    validator.validate(facturaCustom, facturaCustom.getFolio());
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
    Factura entityFromDto = mapper.getEntityFromFacturaCustom(facturaCustom);
    entityFromDto.setId(factura.getId());
    repository.save(entityFromDto);
    filesService.sendXmlToS3(facturaCustom.getFolio(), comprobante);
    filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);

    // TODO enable pdf generation
    // FacturaPdf facturaPdf = mapper.getFacturaPdfFromFacturaCustom(facturaCustom);
    // facturaPdf.setCfdi(comprobante);
    // byte[] pdf = FacturaUtils.generateFacturaPdf(facturaPdf, PDF_FACTURA_SIN_TIMBRAR);
    // filesService.sendFileToS3(facturaCustom.getFolio(), pdf, ".pdf");
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
    validator.checkNotNegative(saldo, "Saldo pendiente");
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
      validator.checkNotNegative(deuda.get(), "Saldo pendiente");
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
    validator.checkNotNegative(saldo, "Saldo pendiente");
    factura.setTotal(total);
    factura.setSaldoPendiente(saldo);
    return mapper.getFacturaDtoFromEntity(repository.save(factura));
  }

  public FacturaCustom updateFacturaStatus(String folio, FacturaStatusEnum status) {
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
    cfdiService.deleteCfdi(fact.getFolio());
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
    return generateComplemento(facturas, pagoDto);
  }

  // TIMBRADO
  public FacturaContext timbrarFactura(String folio, FacturaCustom facturaDto)
      throws InvoiceManagerException, NtlinkUtilException {
    validator.validateTimbrado(facturaDto, folio);
    FacturaContext facturaContext =
        timbradoBuilderService.buildFacturaContextTimbrado(facturaDto, folio);
    timbradoServiceEvaluator.facturaTimbradoValidation(facturaContext);
    switch (TipoDocumentoEnum.findByDesc(facturaContext.getTipoDocumento())) {
      case NOTA_CREDITO:
        if (facturaDto.getIdCfdiRelacionadoPadre() != null) {
          FacturaCustom facturaPadre = getFacturaBaseByFolio(facturaDto.getFolio());
          if (facturaDto.getTotal().compareTo(facturaPadre.getTotal()) > 0
              || facturaDto.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvoiceManagerException(
                "El monto total de la nota credito es invalido",
                "Nota de credito invalida",
                HttpStatus.CONFLICT.value());
          }
        } else {
          throw new InvoiceManagerException(
              "Nota de credito invalida",
              "No esta correctamente referenciada",
              HttpStatus.CONFLICT.value());
        }
      case FACTURA:
        facturaContext = facturaTranslator.translateFactura(facturaContext);
        break;
      case COMPLEMENTO:
        facturaContext = facturaTranslator.translateComplemento(facturaContext);
        break;
      default:
        throw new InvoiceManagerException(
            "The type of document not supported",
            String.format("The type of document %s not valid", facturaContext.getTipoDocumento()),
            HttpStatus.BAD_REQUEST.value());
    }
    switch (PackFacturarionEnum.findByNombre(facturaContext.getFacturaDto().getPackFacturacion())) {
      case NTLINK:
        ntinkExecutorService.stamp(facturaContext);
        break;
      default:
        throw new InvoiceManagerException(
            "Pack not supported yet", "Validate with programers", HttpStatus.BAD_REQUEST.value());
    }

    timbradoExecutorService.updateFacturaAndCfdiValues(facturaContext);
    // PDF GENERATION
    FacturaFileDto pdfFile = pdfService.generateInvoicePDF(facturaContext);
    facturaContext.getFacturaFilesDto().add(pdfFile);
    if ((facturaContext.getFacturaDto().getMetodoPago().equals(MetodosPagoEnum.PUE.name())
            || (facturaContext.getFacturaDto().getMetodoPago().equals(MetodosPagoEnum.PPD.name())
                && facturaContext
                    .getFacturaDto()
                    .getTipoDocumento()
                    .equals(TipoDocumentoEnum.COMPLEMENTO.getDescripcion())))
        && facturaContext.getFacturaDto().getLineaEmisor().equals("A")
        && facturaContext.getFacturaDto().getLineaRemitente().equals("CLIENTE")) {
      devolucionService.generarDevoluciones(facturaContext.getFacturaDto());
    }
    final FacturaContext fc = facturaContext;
    CompletableFuture.supplyAsync(() -> sendEmail(fc));
    return facturaContext;
  }

  private Boolean sendEmail(FacturaContext fc) {
    try {
      if (fc.getFacturaDto().getLineaRemitente().equals("CLIENTE")) {
        try {
          timbradoExecutorService.sendEmail(fc, TipoEmail.SEMEL_JACK);
        } catch (InvoiceManagerException e) {
          timbradoExecutorService.sendEmail(fc, TipoEmail.GMAIL);
        }
      }
      return true;
    } catch (InvoiceManagerException e) {
      return false;
    }
  }

  public FacturaContext cancelarFactura(String folio, FacturaCustom facturaDto)
      throws InvoiceManagerException, NtlinkUtilException {
    FacturaContext facturaContext =
        timbradoBuilderService.buildFacturaContextCancelado(facturaDto, folio);
    validator.validateTimbrado(facturaDto, folio);
    if (facturaDto.getTipoDocumento().equals("Factura")
        || facturaDto.getMetodoPago().equals("PPD")) {
      for (CfdiPagoDto cfdiPagoDto : cfdiService.getCfdiPagosByFolio(facturaDto.getFolio())) {
        FacturaCustom complemento = getFacturaByFolio(cfdiPagoDto.getCfdi().getFolio());
        if (complemento.getStatusFactura().equals(FacturaStatusEnum.TIMBRADA.getValor())) {
          cancelarFactura(complemento.getFolio(), complemento);
        } else {
          if (!complemento.getStatusFactura().equals(FacturaStatusEnum.CANCELADA.getValor())) {
            complemento.setStatusFactura(FacturaStatusEnum.CANCELADA.getValor());
            // updateFactura(complemento.getFolio(), complemento);
          }
        }
      }
    }

    timbradoServiceEvaluator.facturaCancelacionValidation(facturaContext);
    switch (PackFacturarionEnum.findByNombre(facturaContext.getFacturaDto().getPackFacturacion())) {
      case NTLINK:
        ntinkExecutorService.cancelarFactura(facturaContext);
        break;
      default:
        throw new InvoiceManagerException(
            "Pack not supported yet", "Validate with programers", HttpStatus.BAD_REQUEST.value());
    }
    timbradoExecutorService.updateCanceladoValues(facturaContext);
    return facturaContext;
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom generateComplemento(List<FacturaCustom> facturas, PagoDto pagoPpd)
      throws InvoiceManagerException, NtlinkUtilException {
    if (facturas.stream()
        .anyMatch(a -> !a.getStatusFactura().equals(FacturaStatusEnum.TIMBRADA.getValor()))) {
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
      facturaDefaultValues.assignaDefaultsComplemento(
          complemento, facturaDao.getCantidadFacturasOfTheCurrentMonthByTipoDocumento());
      Cfdi createdCfdi = cfdiService.insertNewCfdi(complemento.getCfdi());
      Factura fact = mapper.getEntityFromFacturaCustom(complemento);
      fact.setFolio(createdCfdi.getFolio());
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
      }*/
      return mapper.getFacturaDtoFromEntity(repository.save(fact));
    } else {
      throw new InvoiceManagerException(
          "Debe tener por lo menos un pago",
          "No asigno el pago a una factura",
          HttpStatus.BAD_REQUEST.value());
    }
  }

  public void recreatePdf(Cfdi dto) {
    FacturaCustom factura =
        mapper.getFacturaDtoFromEntity(
            repository
                .findByFolio(dto.getFolio())
                .orElseThrow(
                    () ->
                        new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            String.format(
                                "La factura con el folio %s no existe", dto.getFolio()))));
    factura.setCfdi(dto);
    pdfService.generateInvoicePDF(factura, null);
  }

  public FacturaContext resendEmail(String folio, FacturaCustom facturaDto)
      throws InvoiceManagerException, NtlinkUtilException {
    FacturaContext facturaContext =
        facturaBuilderService.buildEmailContext(folio, getFacturaByFolio(folio));
    timbradoExecutorService.sendEmail(facturaContext, TipoEmail.GMAIL);
    return facturaContext;
  }

  public FacturaCustom postRelacion(FacturaCustom dto, TipoDocumentoEnum tipoDocumento)
      throws InvoiceManagerException, NtlinkUtilException {
    FacturaCustom facturaDto = getFacturaByFolio(dto.getFolio());
    if (TipoDocumentoEnum.FACTURA.getDescripcion().equals(facturaDto.getTipoDocumento())) {
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
            MetodosPagoEnum.findByValor(facturaCustom.getCfdi().getMetodoPago()).getDescripcion())
        .tipoDeComprobanteDesc(
            TipoComprobanteEnum.findByValor(facturaCustom.getCfdi().getTipoDeComprobante())
                .getDescripcion())
        .logotipo(
            facturaCustom.getLogotipo() == null
                ? filesService
                    .getResourceFileByResourceReferenceAndType(
                        S3BucketsEnum.EMPRESAS, facturaCustom.getRfcEmisor(), "LOGO")
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
                ? FacturaStatusEnum.VALIDACION_OPERACIONES.getValor()
                : facturaCustom.getStatusFactura())
        .build();
  }
}
