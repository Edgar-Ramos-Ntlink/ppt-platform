package com.business.unknow.services.services;

import com.business.unknow.enums.FacturaStatusEnum;
import com.business.unknow.enums.FormaPagoEnum;
import com.business.unknow.enums.MetodosPagoEnum;
import com.business.unknow.enums.RevisionPagosEnum;
import com.business.unknow.enums.TipoArchivoEnum;
import com.business.unknow.enums.TipoDocumentoEnum;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.dto.pagos.PagoFacturaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Pago;
import com.business.unknow.services.entities.PagoFactura;
import com.business.unknow.services.mapper.PagoMapper;
import com.business.unknow.services.repositories.PagoFacturaRepository;
import com.business.unknow.services.repositories.PagoRepository;
import com.business.unknow.services.services.evaluations.PagoEvaluatorService;
import com.mx.ntlink.NtlinkUtilException;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import java.io.IOException;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** @author ralfdemoledor */
@Service
@Slf4j
public class PagoService {

  @Autowired private PagoRepository repository;

  @Autowired private PagoFacturaRepository facturaPagosRepository;

  @Autowired private PagoMapper mapper;

  @Autowired
  @Qualifier("PagoValidator")
  private PagoEvaluatorService pagoEvaluatorService;

  @Autowired private FilesService filesService;

  @Autowired private FacturaService facturaService;

  @Autowired private CfdiService cfdiService;

  @Autowired private DownloaderService downloaderService;

  private Page<Pago> paymentsSearch(
      Optional<String> solicitante,
      Optional<String> acredor,
      Optional<String> deudor,
      String formaPago,
      String status,
      String banco,
      Date since,
      Date to,
      int page,
      int size) {

    Date start = (since == null) ? new DateTime().minusYears(1).toDate() : since;
    Date end = (to == null) ? new Date() : to;
    Page<Pago> result = null;
    if (solicitante.isPresent()) {
      result =
          repository.findBySolicitanteIgnoreCaseContaining(
              solicitante.get(),
              PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
    } else if (acredor.isPresent()) {
      result =
          repository.findPagosAcredorFilteredByParams(
              String.format("%%%s%%", acredor.get()),
              String.format("%%%s%%", status),
              String.format("%%%s%%", formaPago),
              String.format("%%%s%%", banco),
              start,
              end,
              PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
    } else if (deudor.isPresent()) {
      result =
          repository.findPagosDeudorFilteredByParams(
              String.format("%%%s%%", deudor.get()),
              String.format("%%%s%%", status),
              String.format("%%%s%%", formaPago),
              String.format("%%%s%%", banco),
              start,
              end,
              PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
    } else {
      result =
          repository.findPagosFilteredByParams(
              String.format("%%%s%%", status),
              String.format("%%%s%%", formaPago),
              String.format("%%%s%%", banco),
              start,
              end,
              PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
    }
    return result;
  }

  public Page<PagoDto> getPaginatedPayments(
      Optional<String> solicitante,
      Optional<String> acredor,
      Optional<String> deudor,
      String formaPago,
      String status,
      String banco,
      Date since,
      Date to,
      int page,
      int size) {

    Page<Pago> result =
        paymentsSearch(
            solicitante, acredor, deudor, formaPago, status, banco, since, to, page, size);

    return new PageImpl<>(
        mapper.getPagosDtoFromEntities(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public ResourceFileDto getPaymentsReport(
      Optional<String> solicitante,
      Optional<String> acredor,
      Optional<String> deudor,
      String formaPago,
      String status,
      String banco,
      Date since,
      Date to,
      int page,
      int size)
      throws IOException {

    Page<Pago> result =
        paymentsSearch(
            solicitante, acredor, deudor, formaPago, status, banco, since, to, page, size);

    List<String> headers =
        Arrays.asList(
            "ID",
            "ACREDOR",
            "DEUDOR",
            "MONEDA",
            "BANCO",
            "CUENTA",
            "TIPO CAMBIO",
            "FORMA PAGO",
            "MONTO",
            "FECHA PAGO",
            "ESTATUS",
            "COMENTARIO",
            "SOLICITANTE",
            "REVISOR 1",
            "REVISOR 2",
            "CREACION",
            "ACTUALIZACION");

    List<Map<String, Object>> data =
        result.getContent().stream()
            .map(
                pago -> {
                  Map<String, Object> row = new HashMap<>();
                  row.put("ID", pago.getId());
                  row.put("ACREDOR", pago.getAcredor());
                  row.put("DEUDOR", pago.getDeudor());
                  row.put("MONEDA", pago.getMoneda());
                  row.put("BANCO", pago.getBanco());
                  row.put("CUENTA", pago.getCuenta());
                  row.put("TIPO CAMBIO", pago.getTipoDeCambio());
                  row.put("FORMA PAGO", pago.getFormaPago());
                  row.put("MONTO", pago.getMonto());
                  row.put("FECHA PAGO", pago.getFechaPago());
                  row.put("ESTATUS", pago.getStatusPago());
                  row.put("COMENTARIO", pago.getComentarioPago());
                  row.put("SOLICITANTE", pago.getSolicitante());
                  row.put("REVISOR 1", pago.getRevisor1());
                  row.put("REVISOR 2", pago.getRevisor2());
                  row.put("CREACION", pago.getFechaCreacion());
                  row.put("ACTUALIZACION", pago.getFechaActualizacion());
                  return row;
                })
            .collect(Collectors.toList());

    return downloaderService.generateBase64Report("Pagos", data, headers);
  }

  public List<PagoDto> findPagosByFolio(String folio) {
    return mapper.getPagosDtoFromEntities(repository.findPagosByFolio(folio));
  }

  public List<PagoFacturaDto> findPagosFacturaByFolio(String folio) {
    return mapper.getPagosFacturaDtoFromEntities(facturaPagosRepository.findByFolio(folio));
  }

  public PagoDto getPaymentById(Integer id) throws InvoiceManagerException {
    Optional<Pago> payment = repository.findById(id);
    if (payment.isPresent()) {
      return mapper.getPagoDtoFromEntity(payment.get());
    } else {
      throw new InvoiceManagerException(
          "Pago no encontrado",
          String.format("El pago con id %d no fu encontrado.", id),
          HttpStatus.NOT_FOUND.value());
    }
  }

  public PagoDto insertNewPaymentWithoutValidation(PagoDto payment) {
    return mapper.getPagoDtoFromEntity(repository.save(mapper.getEntityFromPagoDto(payment)));
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public PagoDto insertNewPayment(PagoDto pagoDto)
      throws InvoiceManagerException, NtlinkUtilException {
    pagoEvaluatorService.validatePayment(pagoDto);
    List<FacturaDto> facturas = new ArrayList<>();
    for (PagoFacturaDto pagoFact : pagoDto.getFacturas()) {
      FacturaDto factura = facturaService.getBaseFacturaByFolio(pagoFact.getFolio());
      facturas.add(factura);
      // Populate missing information
      pagoFact.setAcredor(factura.getRazonSocialEmisor());
      pagoFact.setDeudor(factura.getRazonSocialRemitente());
      pagoFact.setTotalFactura(factura.getTotal());
      pagoFact.setMetodoPago(factura.getMetodoPago());
      if (MetodosPagoEnum.PUE.name().equals(factura.getMetodoPago())) {
        pagoFact.setIdCfdi(factura.getIdCfdi());
      }
    }
    pagoEvaluatorService.validatePaymentCreation(pagoDto, facturas);

    List<FacturaDto> factPpd =
        facturas.stream()
            .filter(f -> MetodosPagoEnum.PPD.name().equals(f.getMetodoPago()))
            .collect(Collectors.toList());

    List<FacturaDto> factPue =
        facturas.stream()
            .filter(f -> MetodosPagoEnum.PUE.name().equals(f.getMetodoPago()))
            .collect(Collectors.toList());

    if (!factPpd.isEmpty()) {
      log.info(
          "Generando complemento para : {}",
          factPpd.stream().map(f -> f.getFolio()).collect(Collectors.toList()));
      FacturaDto fact = facturaService.generateComplemento(facturas, pagoDto);
      factPpd.forEach(
          f -> {
            Optional<PagoFacturaDto> pagoFact =
                pagoDto.getFacturas().stream()
                    .filter(p -> p.getFolio().equals(f.getFolio()))
                    .findAny();
            if (pagoFact.isPresent()) {
              pagoFact.get().setIdCfdi(fact.getIdCfdi());
            }
          });
    }
    if (!factPue.isEmpty()) {
      factPue.forEach(
          f -> {
            Optional<PagoFacturaDto> pagoFact =
                pagoDto.getFacturas().stream()
                    .filter(p -> p.getFolio().equals(f.getFolio()))
                    .findAny();
            if (pagoFact.isPresent()) {
              pagoFact.get().setIdCfdi(f.getIdCfdi());
            }
          });
    }
    for (FacturaDto dto : facturas) {
      if (!FormaPagoEnum.CREDITO.getPagoValue().equals(pagoDto.getFormaPago())
          && dto.getTipoDocumento().equals(TipoDocumentoEnum.FACTURA.getDescripcion())
          && dto.getMetodoPago().equals(MetodosPagoEnum.PUE.name())) {
        log.info("Updating saldo pendiente factura {}", dto.getFolio());
        Optional<PagoFacturaDto> pagoFact =
            pagoDto.getFacturas().stream()
                .filter(p -> p.getFolio().equals(dto.getFolio()))
                .findAny();
        if (pagoFact.isPresent()) {
          facturaService.updateTotalAndSaldoFactura(
              dto.getFolio(), Optional.empty(), Optional.of(pagoFact.get().getMonto()));
        }
      }
    }
    if (FormaPagoEnum.CREDITO.getPagoValue().equals(pagoDto.getFormaPago())
        && facturas.size() == 1) {
      Optional<FacturaDto> currentFactura = facturas.stream().findFirst();
      if (currentFactura.isPresent()
          && currentFactura.get().getMetodoPago().equals(MetodosPagoEnum.PUE.name())) {
        currentFactura.get().setValidacionTeso(true);
        facturaService.updateFactura(currentFactura.get().getFolio(), currentFactura.get());
        pagoDto.setStatusPago("ACEPTADO");
        pagoDto.setRevision1(true);
        pagoDto.setRevision2(true);
      }
    }
    Pago payment = repository.save(mapper.getEntityFromPagoDto(pagoDto));
    for (PagoFacturaDto fact : pagoDto.getFacturas()) {
      PagoFactura pagoFact = mapper.getEntityFromPagoFacturaDto(fact);
      pagoFact.setPago(payment);
      payment.addFactura(facturaPagosRepository.save(pagoFact));
    }
    return mapper.getPagoDtoFromEntity(payment);
  }

  public List<PagoFacturaDto> getPAgoFacturaByIdCfdi(int idCfdi) {
    return mapper.getPagosFacturaDtoFromEntities(facturaPagosRepository.findByIdCfdi(idCfdi));
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public PagoDto updatePago(Integer idPago, PagoDto pago) throws InvoiceManagerException {
    Pago entity =
        repository
            .findById(idPago)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("El pago con el id %d no existe", idPago)));

    PagoDto pagoDto = mapper.getPagoDtoFromEntity(entity); // payment only update revision
    pagoDto.toBuilder()
        .revision1(pago.getRevision1())
        .revision2(pago.getRevision2())
        .revisor1(pago.getRevisor1())
        .revisor2(pago.getRevisor2())
        .build();
    pagoEvaluatorService.validatePayment(pago);

    List<FacturaDto> facturas = new ArrayList<>();
    for (PagoFacturaDto pagoFact : pago.getFacturas()) {
      FacturaDto factura = facturaService.getBaseFacturaByFolio(pagoFact.getFolio());
      facturas.add(factura);
    }
    pagoEvaluatorService.validatePaymentUpdate(pago, mapper.getPagoDtoFromEntity(entity), facturas);

    if (pago.getStatusPago().equals(RevisionPagosEnum.RECHAZADO.name())) {
      pagoDto.setStatusPago(RevisionPagosEnum.RECHAZADO.name());
      pagoDto.setComentarioPago(pago.getComentarioPago());
      for (FacturaDto factura : facturas) {
        if (MetodosPagoEnum.PUE.getClave().equals(factura.getMetodoPago())) {
          factura.setStatusFactura(FacturaStatusEnum.RECHAZO_TESORERIA.getValor());
          factura.setStatusDetail(pago.getComentarioPago());
          facturaService.updateFactura(factura.getFolio(), factura);
        }
      }
    } else if (entity.getRevision1() && pago.getRevision2()) {
      pagoDto.setStatusPago(RevisionPagosEnum.ACEPTADO.name());

      List<String> folioFacts =
          pago.getFacturas().stream()
              .map(f -> f.getFolio())
              .distinct()
              .collect(Collectors.toList());

      for (String folioCfdi : folioFacts) {
        FacturaDto fact = facturaService.getFacturaBaseByFolio(folioCfdi);
        fact.setValidacionTeso(true);
        facturaService.updateFactura(folioCfdi, fact);
      }
    }
    return mapper.getPagoDtoFromEntity(repository.save(mapper.getEntityFromPagoDto(pagoDto)));
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public void deletePago(Integer idPago) throws InvoiceManagerException, NtlinkUtilException {
    PagoDto payment =
        mapper.getPagoDtoFromEntity(
            repository
                .findById(idPago)
                .orElseThrow(
                    () ->
                        new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            String.format("El pago con id %d no existe", idPago))));
    List<FacturaDto> facturas = new ArrayList<>();
    for (PagoFacturaDto pagoFact : payment.getFacturas()) {
      FacturaDto factura = facturaService.getBaseFacturaByFolio(pagoFact.getFolio());
      facturas.add(factura);
    }
    pagoEvaluatorService.deletepaymentValidation(payment, facturas);

    for (FacturaDto facturaDto : facturas) {
      Optional<PagoFacturaDto> pagoFactOpt =
          payment.getFacturas().stream()
              .filter(p -> p.getFolio().equals(facturaDto.getFolio()))
              .findAny();
      if (pagoFactOpt.isPresent()) {
        Cfdi cfdi = cfdiService.getCfdiByFolio(facturaDto.getFolio());
        facturaService.updateTotalAndSaldoFactura(
            facturaDto.getFolio(),
            Optional.empty(),
            Optional.of(
                cfdi.getMoneda().equals(payment.getMoneda())
                    ? pagoFactOpt.get().getMonto().negate()
                    : pagoFactOpt
                        .get()
                        .getMonto()
                        .divide(payment.getTipoDeCambio(), 2, RoundingMode.HALF_UP)
                        .negate()));
      }
    }
    for (String folioCfdi :
        payment.getFacturas().stream()
            .map(f -> f.getFolio())
            .distinct()
            .collect(Collectors.toList())) {
      FacturaDto fact = facturaService.getFacturaBaseByFolio(folioCfdi);
      if (TipoDocumentoEnum.COMPLEMENTO.equals(
          TipoDocumentoEnum.findByDesc(fact.getTipoDocumento()))) {
        facturaService.deleteFactura(fact.getFolio());
        filesService.deleteFacturaFile(fact.getFolio(), "PDF");
      }
    }

    filesService.deleteResourceFileByResourceReferenceAndType(
        "PAGOS", idPago.toString(), TipoArchivoEnum.IMAGEN.name());
    repository.delete(mapper.getEntityFromPagoDto(payment));
  }

  public void delePagoFacturas(int id) {
    for (PagoFactura pagoFactura : facturaPagosRepository.findByPagoId(id)) {
      facturaPagosRepository.deleteById(pagoFactura.getId());
    }
  }
}
