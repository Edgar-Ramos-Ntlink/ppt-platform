package com.business.unknow.services.services;

import static com.business.unknow.Constants.CANCEL_ACK;
import static com.business.unknow.Constants.PDF_COMPLEMENTO_SIN_TIMBRAR;
import static com.business.unknow.Constants.PDF_COMPLEMENTO_TIMBRAR;
import static com.business.unknow.Constants.PDF_FACTURA_SIN_TIMBRAR;
import static com.business.unknow.Constants.PDF_FACTURA_TIMBRAR;
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
import com.business.unknow.model.dto.services.ClientDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Factura;
import com.business.unknow.services.mapper.FacturaMapper;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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

  @Autowired private InvoiceBuilderService invoiceBuilderService;

  @Autowired private RelationBuilderService sustitucionTranslator;

  @Autowired private TimbradoEvaluatorService timbradoServiceEvaluator;

  @Autowired private FacturaEvaluatorService facturaServiceEvaluator;

  @Autowired private FacturaDao facturaDao;

  @Autowired private FacturaRepository repository;

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
              PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
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

  public FacturaCustom getFacturaByFolio(String folio) {
    try {
      getFacturaBaseByFolio(folio);
      InputStream is =
          filesService.getS3InputStream(S3Buckets.CFDIS, String.format("%s.json", folio));

      return new ObjectMapper().readValue(is.readAllBytes(), FacturaCustom.class);
    } catch (IOException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Error recuperando detalles de la factura");
    }
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
        invoiceBuilderService.assignFacturaData(
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
    byte[] pdf =
        FacturaUtils.generateFacturaPdf(
            facturaPdf,
            FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())
                ? PDF_FACTURA_SIN_TIMBRAR
                : PDF_COMPLEMENTO_TIMBRAR);
    filesService.sendFileToS3(facturaCustom.getFolio(), pdf, PDF.getFormat(), S3Buckets.CFDIS);
    reportDataService.upsertReportData(facturaCustom.getCfdi());
    return facturaCustom;
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public FacturaCustom updateFacturaCustom(String folio, FacturaCustom facturaCustom)
      throws InvoiceManagerException, NtlinkUtilException {
    FacturaCustom entity = getFacturaBaseByFolio(folio);
    facturaServiceEvaluator.facturaStatusValidation(facturaCustom);
    InvoiceValidator.validate(facturaCustom, facturaCustom.getFolio());
    Factura entityFromDto = mapper.getEntityFromFacturaCustom(facturaCustom);
    entityFromDto.setId(entity.getId());
    repository.save(entityFromDto);
    filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
    if (FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())
        && !(entity.getStatusFactura().equals(FacturaStatus.TIMBRADA.getValor())
            || entity.getStatusFactura().equals(FacturaStatus.CANCELADA.getValor()))) {
      facturaCustom.setCfdi(cfdiService.updateCfdi(facturaCustom.getCfdi()));
      Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
      filesService.sendXmlToS3(facturaCustom.getFolio(), comprobante);
      FacturaPdf facturaPdf = mapper.getFacturaPdfFromFacturaCustom(facturaCustom);
      facturaPdf.setCfdi(comprobante);
      byte[] pdf =
          FacturaUtils.generateFacturaPdf(
              facturaPdf,
              FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())
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
      throws InvoiceManagerException, NtlinkUtilException {
    FacturaCustom entity = getFacturaBaseByFolio(folio);
    InvoiceValidator.validate(facturaCustom, folio);
    timbradoServiceEvaluator.invoiceCancelValidation(facturaCustom);
    if (FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())
        && MetodosPago.PPD.name().equals(facturaCustom.getMetodoPago())) {
      if (facturaCustom.getPagos().stream().anyMatch(a -> a.isValido())) {
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
    Factura entityFromDto = mapper.getEntityFromFacturaCustom(facturaCustom);
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
  public FacturaCustom generateComplemento(List<FacturaCustom> invoices, PagoDto pagoDto)
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
      Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
      Factura save = repository.save(mapper.getEntityFromFacturaCustom(facturaCustom));
      facturaCustom.setFechaCreacion(save.getFechaCreacion());
      facturaCustom.setFechaActualizacion(save.getFechaActualizacion());
      for (FacturaCustom fc : invoices) {
        updateFacturaCustom(fc.getFolio(), fc);
      }
      filesService.sendXmlToS3(facturaCustom.getFolio(), comprobante);
      filesService.sendFacturaCustomToS3(facturaCustom.getFolio(), facturaCustom);
      byte[] pdf = getPdfFromFactura(facturaCustom, PDF_COMPLEMENTO_SIN_TIMBRAR);
      filesService.sendFileToS3(facturaCustom.getFolio(), pdf, PDF.getFormat(), S3Buckets.CFDIS);
      return facturaCustom;
    } else {
      throw new InvoiceManagerException(
          "Debe tener por lo menos un pago", HttpStatus.BAD_REQUEST.value());
    }
  }

  public FacturaCustom postRelacion(FacturaCustom dto, TipoDocumento tipoDocumento)
      throws InvoiceManagerException, NtlinkUtilException {
    FacturaCustom facturaCustom = getFacturaByFolio(dto.getFolio());
    String folio = FacturaUtils.generateFolio();
    dto.setFolioRelacionado(folio);
    updateFacturaCustom(dto.getFolio(), dto);
    if (FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())) {
      switch (tipoDocumento) {
        case FACTURA:
          facturaCustom = sustitucionTranslator.sustitucionFactura(facturaCustom, folio);
          break;
        case NOTA_CREDITO:
          facturaCustom = sustitucionTranslator.notaCreditoFactura(facturaCustom);
          break;
        default:
          throw new InvoiceManagerException(
              "The type of document not supported",
              String.format("The type of document %s not valid", facturaCustom.getTipoDocumento()),
              HttpStatus.BAD_REQUEST.value());
      }
      createFacturaCustom(facturaCustom);
      return dto;
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
