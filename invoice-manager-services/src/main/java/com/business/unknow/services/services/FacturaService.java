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
import static com.business.unknow.enums.TipoDocumento.NOTA_CREDITO;

import com.business.unknow.Constants;
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
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Factura33;
import com.business.unknow.services.entities.Factura40;
import com.business.unknow.services.mapper.FacturaMapper;
import com.business.unknow.services.repositories.facturas.Factura33Repository;
import com.business.unknow.services.repositories.facturas.FacturaDao;
import com.business.unknow.services.repositories.facturas.FacturaRepository;
import com.business.unknow.services.services.evaluations.FacturaEvaluatorService;
import com.business.unknow.services.services.evaluations.TimbradoEvaluatorService;
import com.business.unknow.services.util.FacturaUtils;
import com.business.unknow.services.util.validators.InvoiceValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mx.ntlink.NtlinkUtilException;
import com.mx.ntlink.cfdi.mappers.CfdiMapper;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import com.mx.ntlink.models.generated.Comprobante;
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

  @Autowired private Factura33Repository repository33;

  @Autowired private CfdiMapper cfdiMapper;

  @Autowired private FacturaMapper mapper;

  @Value("${invoce.environment}")
  private String environment;

  private static final SimpleDateFormat sdf = new SimpleDateFormat(JSON_DATETIME_FORMAT);

  private Specification<Factura40> buildSearchFilters(Map<String, String> parameters) {
    String linea = (parameters.get("lineaEmisor") == null) ? "A" : parameters.get("lineaEmisor");

    log.info("Finding facturas by {}", parameters);

    return new Specification<Factura40>() {

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
                  row.put("ESTATUS", inv.getStatusFactura());
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
            "FOLIO FISCAL PAGO",
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
                  return complement.getPagos().stream()
                      .map(
                          p -> {
                            Map<String, Object> row = new HashMap<>();
                            row.put("FOLIO", p.getFolioOrigen());
                            row.put("FOLIO FISCAL", p.getIdDocumento());
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
                            row.put("FORMA PAGO", p.getFormaDePagoP());
                            row.put("MONEDA", p.getMonedaDr());
                            row.put("ESTATUS", complement.getStatusFactura());
                            row.put(
                                "CANCELACION",
                                Objects.nonNull(complement.getFechaCancelacion())
                                    ? sdf.format(complement.getFechaCancelacion())
                                    : "");
                            row.put("FOLIO FISCAL PAGO", complement.getUuid());
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
      return result;
    } catch (IOException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Error recuperando detalles de la factura");
    }
  }

  public FacturaCustom getFacturaBaseByFolio(String folio) {
    Optional<Factura40> inv40 = repository.findByFolio(folio);
    Optional<Factura33> inv33 = repository33.findByFolio(folio);
    if (inv33.isPresent() && inv33.get().getLineaEmisor().equals("A")) {
      return mapper.getFacturaDtoFromEntity33(inv33.get());
    } else if (inv40.isPresent()) {
      return mapper.getFacturaDtoFromEntity(inv40.get());
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("La factura con el folio %S no existe", folio));
    }
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom createFacturaCustom(FacturaCustom facturaCustom)
      throws InvoiceManagerException, NtlinkUtilException {
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
    byte[] pdf =
        getPdfFromFactura(
            facturaCustom,
            FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())
                    || NOTA_CREDITO.getDescripcion().equals(facturaCustom.getTipoDocumento())
                ? PDF_FACTURA_SIN_TIMBRAR
                : PDF_COMPLEMENTO_TIMBRAR);
    filesService.sendFileToS3(facturaCustom.getFolio(), pdf, PDF.getFormat(), S3Buckets.CFDIS);
    reportDataService.upsertReportData(facturaCustom.getCfdi());
    return facturaCustom;
  }

  private void updateFacturaBase(Integer id, FacturaCustom facturaCustom)
      throws InvoiceManagerException {
    switch (facturaCustom.getVersion()) {
      case Constants.CFDI_40_VERSION:
        Factura40 entity40 = mapper.getEntityFromFacturaCustom(facturaCustom);
        entity40.setId(id);
        repository.save(entity40);
        break;
      case Constants.CFDI_33_VERSION:
        Factura33 entity33 = mapper.getEntity33FromFacturaCustom(facturaCustom);
        entity33.setId(id);
        repository33.save(entity33);
        break;

      default:
        throw new InvoiceManagerException(
            String.format(
                "La factura con folio %s no tiene una version definida", facturaCustom.getFolio()),
            HttpStatus.CONFLICT.value());
    }
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom updateFacturaCustom(String folio, FacturaCustom facturaCustom)
      throws InvoiceManagerException, NtlinkUtilException {
    FacturaCustom entity = getFacturaBaseByFolio(folio);
    facturaServiceEvaluator.facturaStatusValidation(
        facturaCustom); // TODO verify if this method can be moved outside of updateFacturaCustom
    // method, this is causing multiple status changed randomly
    updateFacturaBase(entity.getId(), facturaCustom);
    if (Objects.nonNull(facturaCustom.getCfdi())) { // TODO remove this logic when CFDI 33 is out
      facturaCustom = invoiceBuilderService.assignDescData(facturaCustom);
      InvoiceValidator.validate(facturaCustom, facturaCustom.getFolio());
      filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
    }
    if ((FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())
            || NOTA_CREDITO.getDescripcion().equals(facturaCustom.getTipoDocumento()))
        && !(entity.getStatusFactura().equals(FacturaStatus.TIMBRADA.getValor())
            || entity.getStatusFactura().equals(FacturaStatus.CANCELADA.getValor()))
        && Objects.nonNull(facturaCustom.getCfdi())) { // TODO remove this logic when CFDI 33 is out
      facturaCustom.setCfdi(cfdiService.updateCfdi(facturaCustom.getCfdi()));
      Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
      filesService.sendXmlToS3(facturaCustom.getFolio(), comprobante);
      byte[] pdf =
          getPdfFromFactura(
              facturaCustom,
              FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())
                      || NOTA_CREDITO.getDescripcion().equals(facturaCustom.getTipoDocumento())
                  ? PDF_FACTURA_SIN_TIMBRAR
                  : PDF_COMPLEMENTO_TIMBRAR);
      filesService.sendFileToS3(facturaCustom.getFolio(), pdf, PDF.getFormat(), S3Buckets.CFDIS);
      reportDataService.upsertReportData(facturaCustom.getCfdi());
    }
    return facturaCustom;
  }

  public FacturaCustom updateTotalAndSaldoFactura(
      String folioCfdi, Optional<BigDecimal> newTotal, Optional<BigDecimal> pago)
      throws InvoiceManagerException, NtlinkUtilException {
    FacturaCustom facturaCustom = getFacturaByFolio(folioCfdi);
    Cfdi cfdi = cfdiService.getCfdiByFolio(folioCfdi);
    BigDecimal total = newTotal.isPresent() ? newTotal.get() : facturaCustom.getTotal();
    BigDecimal montoPagado =
        pagoService.findPagosByFolio(facturaCustom.getFolio()).stream()
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
    facturaCustom.setTotal(total);
    facturaCustom.setSaldoPendiente(saldo);
    return updateFacturaCustom(facturaCustom.getFolio(), facturaCustom);
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public void deleteFactura(String folio) throws InvoiceManagerException, NtlinkUtilException {
    Optional<Factura40> inv40 = repository.findByFolio(folio);
    Optional<Factura33> inv33 = repository33.findByFolio(folio);
    if (inv33.isPresent() && inv33.get().getLineaEmisor().equals("A")) {
      repository33.delete(inv33.get());
    } else if (inv40.isPresent()) {
      repository.delete(inv40.get());
      reportDataService.deleteReportData(folio);
    }
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom stamp(String folio, FacturaCustom facturaCustom)
      throws InvoiceManagerException, NtlinkUtilException {
    FacturaCustom factura = getFacturaBaseByFolio(facturaCustom.getFolio());
    InvoiceValidator.validate(facturaCustom, folio);
    List<PagoDto> pagosFactura = new ArrayList<>();
    if (FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())) {
      pagosFactura = pagoService.findPagosByFolio(folio);
    }
    timbradoServiceEvaluator.facturaTimbradoValidation(facturaCustom, pagosFactura);
    String xml =
        new String(
            Base64.getDecoder()
                .decode(
                    filesService.getS3File(
                        S3Buckets.CFDIS, facturaCustom.getFolio().concat(XML.getFormat()))));
    facturaCustom = facturaExecutorService.stampInvoice(facturaCustom, xml);
    Factura40 entityFromDto = mapper.getEntityFromFacturaCustom(facturaCustom);
    entityFromDto.setId(factura.getId());
    entityFromDto.setFechaTimbrado(new Date());
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
                    || NOTA_CREDITO.getDescripcion().equals(facturaCustom.getTipoDocumento())
                ? PDF_FACTURA_TIMBRAR
                : PDF_COMPLEMENTO_TIMBRAR),
        PDF.getFormat(),
        S3Buckets.CFDIS);
    if (!"dev".equals(environment) && A.name().equalsIgnoreCase(facturaCustom.getLineaEmisor())) {
      sendMail(facturaCustom);
    }
    return facturaCustom;
  }

  public FacturaCustom reSendMail(String folio) {
    return sendMail(getFacturaBaseByFolio(folio));
  }

  public FacturaCustom sendMail(FacturaCustom facturaCustom) {
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
              .subject(MailConstants.STAMP_INVOICE_SUBJECT)
              .bodyText(
                  String.format(
                      MailConstants.STAMP_INVOICE_BODY_MESSAGE,
                      facturaCustom.getRazonSocialRemitente(),
                      facturaCustom.getFolio()))
              .attachments(files)
              .build();
      mailService.sendEmail(ImmutableList.of(facturaCustom.getSolicitante()), mailContent);
      return facturaCustom;
    } catch (Exception e) {
      log.info("Error mandando correo {}", e);
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          String.format(
              "Error mandando Correo para la factura con folio %s", facturaCustom.getFolio()));
    }
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom cancelInvoice(String folio, FacturaCustom facturaCustom)
      throws InvoiceManagerException, NtlinkUtilException {
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
      throws InvoiceManagerException, NtlinkUtilException {
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
      List<FacturaCustom> invoices, PagoDto pagoDto, FacturaStatus status)
      throws InvoiceManagerException, NtlinkUtilException {
    // TODO :MOVE THIS VALIDATION TO A RULE
    if (invoices.stream()
        .anyMatch(a -> !a.getStatusFactura().equals(FacturaStatus.TIMBRADA.getValor()))) {
      throw new InvoiceManagerException(
          "Una factura no esta timbrada", HttpStatus.BAD_REQUEST.value());
    }
    Optional<FacturaCustom> referenceInvoice = invoices.stream().findFirst();

    if (referenceInvoice.isPresent()) {
      FacturaCustom facturaCustom =
          invoiceBuilderService.assignComplementData(
              referenceInvoice.get(),
              invoices,
              pagoDto,
              facturaDao.getCantidadFacturasOfTheCurrentMonthByTipoDocumento());
      facturaCustom.setStatusFactura(status.getValor());
      if (FacturaStatus.POR_TIMBRAR.equals(status)) {
        facturaCustom.setValidacionTeso(Boolean.TRUE);
      }
      Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
      Factura40 save = repository.save(mapper.getEntityFromFacturaCustom(facturaCustom));
      facturaCustom.setFechaCreacion(save.getFechaCreacion());
      facturaCustom.setFechaActualizacion(save.getFechaActualizacion());
      for (FacturaCustom fc : invoices) {
        updateFacturaCustom(fc.getFolio(), fc);
      }
      filesService.sendXmlToS3(facturaCustom.getFolio(), comprobante);
      filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
      facturaCustom = invoiceBuilderService.assignDescData(facturaCustom);
      byte[] pdf = getPdfFromFactura(facturaCustom, PDF_COMPLEMENTO_SIN_TIMBRAR);
      filesService.sendFileToS3(facturaCustom.getFolio(), pdf, PDF.getFormat(), S3Buckets.CFDIS);
      return facturaCustom;
    } else {
      throw new InvoiceManagerException(
          "Debe tener por lo menos un pago", HttpStatus.BAD_REQUEST.value());
    }
  }

  public FacturaCustom createComplemento(String folio, PagoDto pagoDto)
      throws InvoiceManagerException, NtlinkUtilException {
    PagoFacturaDto pagoFactura =
        PagoFacturaDto.builder().folio(folio).monto(pagoDto.getMonto()).build();
    pagoDto.setFacturas(ImmutableList.of(pagoFactura));
    return generateComplemento(
        ImmutableList.of(getFacturaByFolio(folio)), pagoDto, FacturaStatus.POR_TIMBRAR);
  }

  public FacturaCustom postRelacion(FacturaCustom dto, TipoDocumento tipoDocumento)
      throws InvoiceManagerException, NtlinkUtilException {
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

  private byte[] getPdfFromFactura(FacturaCustom facturaCustom, String template)
      throws InvoiceManagerException, NtlinkUtilException {
    Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
    FacturaPdf facturaPdf = mapper.getFacturaPdfFromFacturaCustom(facturaCustom);
    facturaPdf.setCfdi(comprobante);
    facturaPdf.setLogotipo(
        filesService
            .getResourceFileByResourceReferenceAndType(
                S3Buckets.EMPRESAS, facturaCustom.getRfcEmisor(), "LOGO")
            // TODO REFACTOR CODE TO STOP USING  DEPRECATED METHOD
            .getData());
    return FacturaUtils.generateFacturaPdf(facturaPdf, template);
  }
}
