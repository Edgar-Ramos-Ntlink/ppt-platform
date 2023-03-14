package com.business.unknow.services.services;

import static com.business.unknow.Constants.CANCEL_ACK;
import static com.business.unknow.Constants.JSON_DATETIME_FORMAT;
import static com.business.unknow.Constants.PDF_COMPLEMENTO_SIN_TIMBRAR;
import static com.business.unknow.Constants.PDF_COMPLEMENTO_TIMBRAR;
import static com.business.unknow.Constants.PDF_FACTURA_SIN_TIMBRAR;
import static com.business.unknow.Constants.PDF_FACTURA_TIMBRAR;
import static com.business.unknow.enums.LineaEmpresa.A;
import static com.business.unknow.enums.TipoArchivo.PDF;
import static com.business.unknow.enums.TipoArchivo.TXT;
import static com.business.unknow.enums.TipoArchivo.XML;
import static com.business.unknow.enums.TipoDocumento.COMPLEMENTO;
import static com.business.unknow.enums.TipoDocumento.FACTURA;

import com.business.unknow.MailConstants;
import com.business.unknow.enums.FacturaStatus;
import com.business.unknow.enums.MetodosPago;
import com.business.unknow.enums.S3Buckets;
import com.business.unknow.enums.TipoDocumento;
import com.business.unknow.model.config.MailContent;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.FacturaPdf;
import com.business.unknow.model.dto.PagoComplemento;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.pagos.PagoFacturaDto;
import com.business.unknow.model.dto.services.ClientDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Factura40;
import com.business.unknow.services.mapper.FacturaMapper;
import com.business.unknow.services.repositories.CfdiRepository;
import com.business.unknow.services.repositories.facturas.FacturaDao;
import com.business.unknow.services.repositories.facturas.FacturaRepository;
import com.business.unknow.services.services.evaluations.FacturaEvaluatorService;
import com.business.unknow.services.services.evaluations.TimbradoEvaluatorService;
import com.business.unknow.services.util.FacturaUtils;
import com.business.unknow.services.util.validators.InvoiceValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.unknown.cfdi.mappers.CfdiMapper;
import com.unknown.cfdi.mappers.pagos.PagosMapper;
import com.unknown.cfdi.modelos.complementos.pagos.Pagos;
import com.unknown.error.PptUtilException;
import com.unknown.models.generated.Comprobante;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

  @Autowired private InvoiceBuilderService invoiceBuilderService;

  @Autowired private RelationBuilderService sustitucionTranslator;

  @Autowired private TimbradoEvaluatorService timbradoServiceEvaluator;

  @Autowired private FacturaEvaluatorService facturaServiceEvaluator;

  @Autowired private FacturaDao facturaDao;

  @Autowired private FacturaRepository repository;

  @Autowired private CfdiRepository cfdiRepository;
  @Autowired private CfdiMapper cfdiMapper;

  @Autowired private PagosMapper pagosMapper;

  @Autowired private FacturaMapper mapper;

  @Value("${invoce.environment}")
  private String environment;

  private static final SimpleDateFormat sdf = new SimpleDateFormat(JSON_DATETIME_FORMAT);

  private Specification<Factura40> buildSearchFilters(Map<String, String> parameters) {
    String linea = (parameters.get("lineaEmisor") == null) ? "A" : parameters.get("lineaEmisor");

    log.info("Finding facturas by {}", parameters);

    return new Specification<>() {

      private static final long serialVersionUID = -7435096122716669730L;

      @Override
      public Predicate toPredicate(
          Root<Factura40> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("lineaEmisor"), linea)));
        // TODO move this logic into a enum class that handles all this logic
        if (parameters.get("solicitante") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("solicitante"), "%" + parameters.get("solicitante") + "%")));
        }
        if (parameters.containsKey("rfcEmisor")) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("rfcEmisor"), parameters.get("rfcEmisor"))));
        }
        if (parameters.containsKey("rfcRemitente")) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("rfcRemitente"), parameters.get("rfcRemitente"))));
        }
        if (parameters.containsKey("emisor")) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("razonSocialEmisor"), "%" + parameters.get("emisor") + "%")));
        }
        if (parameters.containsKey("remitente")) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("razonSocialRemitente"), "%" + parameters.get("remitente") + "%")));
        }

        if (parameters.containsKey("status")) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("statusFactura"), parameters.get("status"))));
        }

        if (parameters.containsKey("tipoDocumento")) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(
                      root.get("tipoDocumento"), parameters.get("tipoDocumento"))));
        }

        if (parameters.containsKey("metodoPago")) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("metodoPago"), parameters.get("metodoPago"))));
        }

        if (parameters.containsKey("saldoPendiente")) {
          BigDecimal saldo = new BigDecimal(parameters.get("saldoPendiente"));
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.greaterThanOrEqualTo(root.get("saldoPendiente"), saldo)));
        }

        if (parameters.containsKey("since") && parameters.containsKey("to")) {
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

  public Page<FacturaCustom> getFacturasByParametros(Map<String, String> parameters) {

    Page<Factura40> result;
    int page = (parameters.get("page") == null) ? 0 : Integer.valueOf(parameters.get("page"));
    int size = (parameters.get("size") == null) ? 10 : Integer.valueOf(parameters.get("size"));
    if (parameters.get("prefolio") != null) {
      result = repository.findByPreFolio(parameters.get("prefolio"), PageRequest.of(0, 10));
    } else {
      result =
          repository.findAll(
              buildSearchFilters(parameters),
              PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
    }
    return new PageImpl<>(
        mapper.getFacturaDtosFromEntities(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public ResourceFileDto getFacturaReportsByParams(Map<String, String> parameters)
      throws IOException {
    parameters.put("tipoDocumento", "Factura");

    List<String> folios =
        repository.findAll(buildSearchFilters(parameters)).stream()
            .map(Factura40::getFolio)
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
            "METODO PAGO",
            "FORMA PAGO",
            "MONEDA",
            "ESTATUS",
            "CANCELACION",
            "LINEA",
            "PROMOTOR",
            "CANTIDAD",
            "CLAVE UNIDAD",
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
                  row.put(
                      "FECHA EMISION",
                      Objects.nonNull(inv.getFechaEmision())
                          ? sdf.format(inv.getFechaEmision())
                          : "");
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
                  row.put(
                      "ESTATUS",
                      FacturaStatus.getStatusByValue(Integer.valueOf(inv.getStatusFactura())));
                  row.put(
                      "CANCELACION",
                      Objects.nonNull(inv.getFechaCancelacion())
                          ? sdf.format(inv.getFechaCancelacion())
                          : "");
                  row.put("LINEA", inv.getLineaEmisor());
                  row.put("PROMOTOR", inv.getCorreoPromotor());
                  row.put("CANTIDAD", inv.getCantidad());
                  row.put("CLAVE UNIDAD", inv.getClaveUnidad());
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
    parameters.put("tipoDocumento", "Complemento");

    List<String> folios =
        repository.findAll(buildSearchFilters(parameters)).stream()
            .map(Factura40::getFolio)
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
            "METODO PAGO",
            "FORMA PAGO",
            "MONEDA",
            "ESTATUS",
            "CANCELACION",
            "FOLIO FISCAL PPD",
            "IMPORTE",
            "SALDO ANTERIOR",
            "SALDO INSOLUTO",
            "PARCIALIDAD",
            "FECHA PAGO");

    List<Map<String, Object>> complements =
        folios.stream()
            .map(
                folio -> {
                  FacturaCustom complement = getFacturaByFolio(folio);
                  // TODO see if there is a better way to solve this temporary fix
                  String formaPago =
                      cfdiRepository
                          .findByFolio(folio)
                          .orElse(
                              com.business.unknow.services.entities.Cfdi.builder()
                                  .formaPago("99")
                                  .build())
                          .getFormaPago();

                  return complement.getPagos().stream()
                      .map(
                          p -> {
                            Map<String, Object> row = new HashMap<>();
                            row.put("FOLIO", folio);
                            row.put("FOLIO FISCAL", complement.getUuid());
                            row.put(
                                "FECHA EMISION",
                                Objects.nonNull(complement.getFechaTimbrado())
                                    ? sdf.format(complement.getFechaTimbrado())
                                    : "");
                            row.put("RFC EMISOR", complement.getRfcEmisor());
                            row.put("EMISOR", complement.getRazonSocialEmisor());
                            row.put("RFC RECEPTOR", complement.getRfcRemitente());
                            row.put("RECEPTOR", complement.getRazonSocialRemitente());
                            row.put("TIPO DOCUMENTO", complement.getTipoDocumento());
                            row.put("PACK", complement.getPackFacturacion());
                            row.put("TIPO", complement.getCfdi().getTipoDeComprobante());
                            row.put("IMPUESTOS TRASLADADOS", BigDecimal.ZERO);
                            row.put("IMPUESTOS RETENIDOS", BigDecimal.ZERO);
                            row.put("SUBTOTAL", complement.getCfdi().getSubtotal());
                            row.put("TOTAL", complement.getCfdi().getTotal());
                            row.put("METODO PAGO", MetodosPago.PPD.name());
                            row.put(
                                "FORMA PAGO",
                                Objects.nonNull(p.getFormaDePagoP())
                                    ? p.getFormaDePagoP()
                                    : formaPago);
                            row.put("MONEDA", p.getMonedaDr());
                            row.put("ESTATUS", complement.getStatusFactura());
                            row.put(
                                "CANCELACION",
                                Objects.nonNull(complement.getFechaCancelacion())
                                    ? sdf.format(complement.getFechaCancelacion())
                                    : "");
                            row.put("FOLIO FISCAL PPD", p.getIdDocumento());
                            row.put("IMPORTE", p.getImportePagado());
                            row.put("SALDO ANTERIOR", p.getImporteSaldoAnterior());
                            row.put("SALDO INSOLUTO", p.getImporteSaldoInsoluto());
                            row.put("PARCIALIDAD", p.getNumeroParcialidad());
                            row.put(
                                "FECHA PAGO",
                                Objects.nonNull(p.getFechaPago())
                                    ? sdf.format(p.getFechaPago())
                                    : "");
                            return row;
                          })
                      .collect(Collectors.toList());
                })
            .flatMap(p -> p.stream())
            .collect(Collectors.toList());

    return downloaderService.generateBase64Report("COMPLEMENTOS", complements, headersOrder);
  }

  public FacturaCustom getFacturaByFolio(String folio) {
    try {
      FacturaCustom base = getFacturaBaseByFolio(folio);
      InputStream is =
          filesService.getS3InputStream(S3Buckets.CFDIS, String.format("%s.json", folio));
      FacturaCustom result = new ObjectMapper().readValue(is.readAllBytes(), FacturaCustom.class);
      result.setVersion(base.getVersion());
      result.getCfdi().setVersion(base.getVersion());
      result.setStatusFactura(base.getStatusFactura());
      result.setValidacionTeso(base.getValidacionTeso());
      result.setValidacionOper(base.getValidacionOper());
      result.setTipoDocumento(base.getTipoDocumento());
      result.setFechaActualizacion(base.getFechaActualizacion());
      result.setFechaCreacion(base.getFechaCreacion());
      result.setId(base.getId());
      result.setFechaTimbrado(
          result.getFechaTimbrado() != null ? result.getFechaTimbrado() : base.getFechaTimbrado());
      return result;
    } catch (IOException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Error recuperando detalles de la factura");
    }
  }

  public FacturaCustom getFacturaBaseByFolio(String folio) {
    Factura40 inv40 =
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("La factura con el folio %S no existe", folio)));
    return mapper.getFacturaDtoFromEntity(inv40);
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom createFacturaCustom(FacturaCustom facturaCustom)
      throws InvoiceManagerException, PptUtilException {
    facturaCustom =
        invoiceBuilderService.assignFacturaData(
            facturaCustom, facturaDao.getCantidadFacturasOfTheCurrentMonthByTipoDocumento());
    InvoiceValidator.validate(facturaCustom, facturaCustom.getFolio());
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
    Factura40 save = repository.save(mapper.getEntityFromFacturaCustom(facturaCustom));
    facturaCustom.setFechaCreacion(save.getFechaCreacion());
    facturaCustom.setFechaActualizacion(save.getFechaActualizacion());
    filesService.sendXmlToS3(facturaCustom.getFolio(), comprobante);
    filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
    buildPdf(
        facturaCustom,
        (COMPLEMENTO.getDescripcion().equals(facturaCustom.getTipoDocumento()))
            ? PDF_COMPLEMENTO_SIN_TIMBRAR
            : PDF_FACTURA_SIN_TIMBRAR);
    reportDataService.upsertReportData(facturaCustom.getCfdi());
    log.info("New invoice has been created with folio: {}", facturaCustom.getFolio());
    return facturaCustom;
  }

  private void updateFacturaBase(Integer id, FacturaCustom facturaCustom) {
    Factura40 entity40 = mapper.getEntityFromFacturaCustom(facturaCustom);
    entity40.setId(id);
    repository.save(entity40);
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom updateFacturaCustom(String folio, FacturaCustom facturaCustom)
      throws InvoiceManagerException, PptUtilException {
    FacturaCustom entity = getFacturaBaseByFolio(folio);
    facturaServiceEvaluator.facturaStatusValidation(
        facturaCustom); // TODO verify if this method can be moved outside of updateFacturaCustom
    // method, this is causing multiple status changed randomly
    facturaCustom = invoiceBuilderService.assignDescData(facturaCustom);
    InvoiceValidator.validate(facturaCustom, facturaCustom.getFolio());
    filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
    if (!entity.getStatusFactura().equals(FacturaStatus.TIMBRADA.getValor())
        && !entity.getStatusFactura().equals(FacturaStatus.CANCELADA.getValor())) {
      switch (TipoDocumento.findByDesc(facturaCustom.getTipoDocumento())) {
        case NOTA_CREDITO:
        case FACTURA:
          facturaCustom.setCfdi(cfdiService.updateCfdi(facturaCustom.getCfdi()));
          reportDataService.upsertReportData(facturaCustom.getCfdi());
          buildPdf(facturaCustom, PDF_FACTURA_SIN_TIMBRAR);
          filesService.sendXmlToS3(
              facturaCustom.getFolio(), cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi()));
          break;
        case COMPLEMENTO:
          facturaCustom.setMetodoPago(MetodosPago.PPD.name());
          try {
            ObjectMapper objMapper = new ObjectMapper();
            String payments =
                objMapper.writeValueAsString(facturaCustom.getCfdi().getComplemento().get(0));
            Pagos pagos = objMapper.readValue(payments, Pagos.class);

            pagos
                .getTotales()
                .setMontoTotalPagos(
                    pagos.getTotales().getMontoTotalPagos().setScale(2, RoundingMode.HALF_UP));
            pagos
                .getTotales()
                .setTotalTrasladosBaseIVA16(
                    pagos
                        .getTotales()
                        .getTotalTrasladosBaseIVA16()
                        .setScale(2, RoundingMode.HALF_UP));
            pagos
                .getTotales()
                .setTotalTrasladosImpuestoIVA16(
                    pagos
                        .getTotales()
                        .getTotalTrasladosImpuestoIVA16()
                        .setScale(2, RoundingMode.HALF_UP));

            pagos
                .getPagos()
                .forEach(
                    p -> {
                      p.setMonto(p.getMonto().setScale(2, RoundingMode.HALF_UP));
                      p.getImpuestosP()
                          .getTrasladosP()
                          .getTrasladoP()
                          .forEach(
                              t -> {
                                t.setBaseP(t.getBaseP().setScale(2, RoundingMode.HALF_UP));
                                t.setImporteP(t.getImporteP().setScale(2, RoundingMode.HALF_UP));
                                t.setTasaOCuotaP(
                                    t.getTasaOCuotaP().setScale(2, RoundingMode.HALF_UP));
                              });
                      p.getRelacionados()
                          .forEach(
                              r -> {
                                r.setImpSaldoAnt(
                                    r.getImpSaldoAnt().setScale(2, RoundingMode.HALF_UP));
                                r.setImpSaldoInsoluto(
                                    r.getImpSaldoInsoluto().setScale(2, RoundingMode.HALF_UP));
                                r.setImpPagado(r.getImpPagado().setScale(2, RoundingMode.HALF_UP));
                                r.getImpuestosDR()
                                    .getTrasladosDR()
                                    .getTrasladoDR()
                                    .forEach(
                                        t -> {
                                          t.setBaseDR(
                                              t.getBaseDR().setScale(2, RoundingMode.HALF_UP));
                                          t.setImporteDR(
                                              t.getImporteDR().setScale(2, RoundingMode.HALF_UP));
                                          t.setTasaOCuotaDR(
                                              t.getTasaOCuotaDR()
                                                  .setScale(2, RoundingMode.HALF_UP));
                                        });
                              });
                    });

            facturaCustom.getCfdi().setComplemento(ImmutableList.of(pagos));
            buildPdf(facturaCustom, PDF_COMPLEMENTO_SIN_TIMBRAR);
            Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
            comprobante.setMetodoPago(null);
            comprobante.setFormaPago(null);
            filesService.sendXmlToS3(facturaCustom.getFolio(), comprobante);
          } catch (IOException e) {
            log.error("Error en la actualizacion del complemento", e);
            throw new InvoiceManagerException(
                "Error en la actualizacion del complemento",
                e.getMessage(),
                HttpStatus.CONFLICT.value());
          }
          break;
        default:
          log.warn(
              "XML and PDF not updated for  folio : {} and tipoDocumento : {}",
              facturaCustom.getFolio(),
              facturaCustom.getTipoDocumento());
      }
    }
    updateFacturaBase(entity.getId(), facturaCustom);
    return facturaCustom;
  }

  public FacturaCustom updateSaldoFactura(FacturaCustom facturaCustom, BigDecimal montoPagado)
      throws InvoiceManagerException {
    BigDecimal saldo = facturaCustom.getCfdi().getTotal().subtract(montoPagado);
    log.info(
        "Factura con folio {} total: {}, montoPagado : {} , saldo : {}",
        facturaCustom.getFolio(),
        facturaCustom.getCfdi().getTotal(),
        montoPagado,
        saldo);
    InvoiceValidator.checkNotNegative(saldo, "Saldo pendiente");
    facturaCustom.setSaldoPendiente(saldo);
    filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
    updateFacturaBase(facturaCustom.getId(), facturaCustom);
    return facturaCustom;
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public void deleteFactura(String folio) {
    Optional<Factura40> inv40 = repository.findByFolio(folio);
    if (inv40.isPresent()) {
      repository.delete(inv40.get());
      reportDataService.deleteReportData(folio);
    }
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom stamp(String folio) throws InvoiceManagerException, PptUtilException {
    FacturaCustom factura = getFacturaByFolio(folio);
    InvoiceValidator.validate(factura, folio);
    List<PagoDto> pagosFactura = new ArrayList<>();
    if (FACTURA.getDescripcion().equals(factura.getTipoDocumento())) {
      pagosFactura = pagoService.findPagosByFolio(folio);
    }
    timbradoServiceEvaluator.facturaTimbradoValidation(factura, pagosFactura);
    String xml =
        new String(
            Base64.getDecoder()
                .decode(filesService.getS3File(S3Buckets.CFDIS, folio.concat(XML.getFormat()))));
    FacturaCustom facturaCustom = facturaExecutorService.stampInvoice(factura, xml);
    filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
    filesService.sendFileToS3(
        facturaCustom.getFolio(),
        facturaCustom.getXml().getBytes(),
        XML.getFormat(),
        S3Buckets.CFDIS);
    buildPdf(
        facturaCustom,
        COMPLEMENTO.getDescripcion().equals(facturaCustom.getTipoDocumento())
            ? PDF_COMPLEMENTO_TIMBRAR
            : PDF_FACTURA_TIMBRAR);
    Factura40 entityFromDto = mapper.getEntityFromFacturaCustom(facturaCustom);
    entityFromDto.setId(factura.getId());
    entityFromDto.setFechaTimbrado(new Date());
    repository.save(entityFromDto);
    if (A.name().equalsIgnoreCase(facturaCustom.getLineaEmisor())) {
      sendMail(facturaCustom);
    }
    return facturaCustom;
  }

  public void reSendMail(String folio) {
    sendMail(getFacturaBaseByFolio(folio));
  }

  public void sendMail(FacturaCustom facturaCustom) {
    try {
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
              .subject(
                  String.format(
                      MailConstants.STAMP_INVOICE_SUBJECT,
                      facturaCustom.getTipoDocumento(),
                      facturaCustom.getFolio()))
              .bodyText(
                  String.format(
                      MailConstants.STAMP_INVOICE_BODY_MESSAGE,
                      facturaCustom.getRazonSocialRemitente(),
                      facturaCustom.getTipoDocumento(),
                      facturaCustom.getFolio()))
              .attachments(files)
              .build();

      List<String> recipients = new ArrayList<>();
      recipients.add(facturaCustom.getSolicitante());
      Optional<ClientDto> clientOpt =
          clientService.getClientsByPromotorAndClient(
              facturaCustom.getSolicitante(), facturaCustom.getRfcRemitente());
      if (clientOpt.isPresent()
          && Objects.nonNull(clientOpt.get().getCorreoContacto())
          && !clientOpt.get().getCorreoContacto().isEmpty()) {
        recipients.add(clientOpt.get().getCorreoContacto());
      }
      mailService.sendEmail(recipients, mailContent);
    } catch (Exception e) {
      log.error(
          "Error enviando la el correo de la factura con folio : {}", facturaCustom.getFolio());
      log.error("Error mandando correo {}", e);
    }
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom cancelInvoice(String folio, FacturaCustom facturaCustom)
      throws InvoiceManagerException, PptUtilException {
    FacturaCustom entity = getFacturaBaseByFolio(folio);
    InvoiceValidator.validate(facturaCustom, folio);
    timbradoServiceEvaluator.invoiceCancelValidation(facturaCustom);
    if (FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())
        && MetodosPago.PPD.name().equals(facturaCustom.getMetodoPago())) {
      if (Objects.nonNull(facturaCustom.getPagos())
          && facturaCustom.getPagos().stream().anyMatch(a -> a.isValido())) {
        throw new InvoiceManagerException(
            String.format(
                "La Factura %s no se puede cancelar un complemento no esta cancelado",
                facturaCustom.getFolio()),
            HttpStatus.NOT_IMPLEMENTED.value());
      }
    }
    facturaCustom = cancelInvoiceExecution(facturaCustom, entity);
    return facturaCustom;
  }

  private FacturaCustom cancelInvoiceExecution(FacturaCustom facturaCustom, FacturaCustom entity)
      throws InvoiceManagerException, PptUtilException {
    final String folio = facturaCustom.getFolio();
    facturaCustom = facturaExecutorService.cancelInvoice(facturaCustom);
    if (COMPLEMENTO.getDescripcion().equals(facturaCustom.getTipoDocumento())) {
      for (PagoComplemento pagoComplemento : facturaCustom.getPagos()) {
        pagoComplemento.setValido(false);
        FacturaCustom facturaPadre = getFacturaByFolio(pagoComplemento.getFolioOrigen());
        facturaPadre.getPagos().stream()
            .filter(a -> a.getFolio().equals(folio))
            .forEach(b -> b.setValido(false));
        facturaPadre.setSaldoPendiente(
            facturaPadre.getSaldoPendiente().add(pagoComplemento.getImportePagado()));
        updateFacturaCustom(facturaPadre.getFolio(), facturaPadre);
      }
    }
    Factura40 entityFromDto = mapper.getEntityFromFacturaCustom(facturaCustom);
    entityFromDto.setId(entity.getId());
    repository.save(entityFromDto);
    filesService.sendFileToS3(
        facturaCustom.getFolio().concat(CANCEL_ACK),
        facturaCustom.getAcuse().getBytes(),
        XML.getFormat(),
        S3Buckets.CFDIS);
    filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
    return facturaCustom;
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom generateComplemento(
      List<FacturaCustom> parentInvoices, PagoDto pagoDto, FacturaStatus status)
      throws InvoiceManagerException, PptUtilException {
    // TODO :MOVE THIS VALIDATION TO A RULE
    if (parentInvoices.stream()
            .allMatch(a -> !a.getStatusFactura().equals(FacturaStatus.TIMBRADA.getValor()))
        && !parentInvoices.isEmpty()) {
      throw new InvoiceManagerException(
          "Todas las facturas padre deben estar timbradas ", HttpStatus.BAD_REQUEST.value());
    }

    String prefolio =
        FacturaUtils.generatePreFolio(
            facturaDao.getCantidadFacturasOfTheCurrentMonthByTipoDocumento());

    FacturaCustom facturaCustom =
        invoiceBuilderService.assignComplementData(parentInvoices, pagoDto, prefolio);
    facturaCustom.setStatusFactura(status.getValor());
    if (FacturaStatus.POR_TIMBRAR.equals(status)) {
      facturaCustom.setValidacionTeso(Boolean.TRUE);
    }

    Factura40 save = repository.save(mapper.getEntityFromFacturaCustom(facturaCustom));
    facturaCustom.setFechaCreacion(save.getFechaCreacion());
    facturaCustom.setFechaActualizacion(save.getFechaActualizacion());
    for (FacturaCustom fc : parentInvoices) {
      // Updating missing data on parent invoice
      fc.getCfdi().setEmisor(facturaCustom.getCfdi().getEmisor());
      fc.getCfdi().setReceptor(facturaCustom.getCfdi().getReceptor());
      updateFacturaCustom(fc.getFolio(), fc);
    }
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
    comprobante.setMetodoPago(null);
    comprobante.setFormaPago(null);
    facturaCustom = invoiceBuilderService.assignDescData(facturaCustom);
    filesService.sendXmlToS3(facturaCustom.getFolio(), comprobante);
    filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
    buildPdf(facturaCustom, PDF_COMPLEMENTO_SIN_TIMBRAR);
    return facturaCustom;
  }

  public FacturaCustom createComplemento(String folio, PagoDto pagoDto)
      throws InvoiceManagerException, PptUtilException {
    PagoFacturaDto pagoFactura =
        PagoFacturaDto.builder().folio(folio).monto(pagoDto.getMonto()).build();
    pagoDto.setFacturas(ImmutableList.of(pagoFactura));
    return generateComplemento(
        ImmutableList.of(getFacturaByFolio(folio)), pagoDto, FacturaStatus.POR_TIMBRAR);
  }

  public FacturaCustom postRelacion(FacturaCustom dto, TipoDocumento tipoDocumento)
      throws InvoiceManagerException, PptUtilException {
    if (!dto.getStatusFactura().equals(FacturaStatus.TIMBRADA.getValor())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          String.format(
              "La factura con el pre-folio %s no esta timbrada y no puede tener nota de credito",
              dto.getPreFolio()));
    }
    FacturaCustom facturaCustom = getFacturaByFolio(dto.getFolio());
    String folio = FacturaUtils.generateFolio();
    dto.setFolioRelacionado(folio);

    if (FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())) {
      switch (tipoDocumento) {
        case FACTURA:
          facturaCustom = sustitucionTranslator.sustitucionFactura(facturaCustom, folio);
          break;
        case NOTA_CREDITO:
          facturaCustom = sustitucionTranslator.notaCreditoFactura(facturaCustom, folio);
          dto.setSaldoPendiente(BigDecimal.ZERO);
          break;
        default:
          throw new InvoiceManagerException(
              "The type of document not supported",
              String.format("The type of document %s not valid", facturaCustom.getTipoDocumento()),
              HttpStatus.BAD_REQUEST.value());
      }
      FacturaCustom replacedInvoice = createFacturaCustom(facturaCustom);
      updateFacturaCustom(dto.getFolio(), dto);
      return replacedInvoice;
    } else {
      throw new InvoiceManagerException(
          "El tipo de documento en la relacion no es de tipo factura",
          HttpStatus.BAD_REQUEST.value());
    }
  }

  public void rebuildPDF(String folio) throws InvoiceManagerException {
    FacturaCustom factCustom = getFacturaByFolio(folio);
    if (FacturaStatus.TIMBRADA.getValor().equals(factCustom.getStatusFactura())) {
      if (COMPLEMENTO.getDescripcion().equals(factCustom.getTipoDocumento())) {
        factCustom.setMetodoPago(MetodosPago.PPD.name());
        ObjectMapper objMapper = new ObjectMapper();
        Pagos pagos =
            factCustom.getCfdi().getComplemento().stream()
                .map(
                    c -> {
                      try {
                        return objMapper.writeValueAsString(c);
                      } catch (IOException e) {
                        return "";
                      }
                    })
                .filter(s -> s.contains("pagos"))
                .map(
                    p -> {
                      try {
                        return objMapper.readValue(p, Pagos.class);
                      } catch (IOException e) {
                        return Pagos.builder().build();
                      }
                    })
                .findFirst()
                .get();
        factCustom.getCfdi().setComplemento(ImmutableList.of(pagos));
        buildPdf(factCustom, PDF_COMPLEMENTO_TIMBRAR);
      } else {
        buildPdf(factCustom, PDF_FACTURA_TIMBRAR);
      }
      buildPdf(
          factCustom,
          COMPLEMENTO.getDescripcion().equals(factCustom.getTipoDocumento())
              ? PDF_COMPLEMENTO_TIMBRAR
              : PDF_FACTURA_TIMBRAR);
    } else {
      throw new InvoiceManagerException(
          "Solo las facturas timbradas regeneran PDF", HttpStatus.BAD_REQUEST.value());
    }
  }

  private void buildPdf(FacturaCustom facturaCustom, String template)
      throws InvoiceManagerException {

    Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
    FacturaPdf facturaPdf = mapper.getFacturaPdfFromFacturaCustom(facturaCustom);
    facturaPdf.setCfdi(comprobante);
    facturaPdf.setLogotipo(
        filesService
            .getResourceFileByResourceReferenceAndType(
                S3Buckets.EMPRESAS, facturaCustom.getRfcEmisor(), "LOGO")
            // TODO REFACTOR CODE TO STOP USING  DEPRECATED METHOD
            .getData());
    byte[] pdfBytes = FacturaUtils.generateFacturaPdf(facturaPdf, template);

    filesService.sendFileToS3(facturaCustom.getFolio(), pdfBytes, PDF.getFormat(), S3Buckets.CFDIS);
  }
}
