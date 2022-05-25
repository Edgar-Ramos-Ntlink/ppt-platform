package com.business.unknow.services.services;

import com.business.unknow.enums.FacturaStatus;
import com.business.unknow.enums.FormaPago;
import com.business.unknow.enums.MetodosPago;
import com.business.unknow.enums.RevisionPagos;
import com.business.unknow.enums.TipoArchivo;
import com.business.unknow.enums.TipoDocumento;
import com.business.unknow.model.dto.FacturaCustom;
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
import com.business.unknow.services.util.validators.PagoValidator;
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
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class PagoService {

  @Autowired private PagoEvaluatorService pagoEvaluatorService;

  @Autowired private FilesService filesService;

  @Autowired private FacturaService facturaService;

  @Autowired private CfdiService cfdiService;

  @Autowired private DownloaderService downloaderService;

  @Autowired private PagoRepository repository;

  @Autowired private PagoFacturaRepository facturaPagosRepository;

  @Autowired private PagoMapper mapper;

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
    PagoValidator.validatePayment(pagoDto);
    List<FacturaCustom> facturas = new ArrayList<>();
    for (PagoFacturaDto pagoFact : pagoDto.getFacturas()) {
      FacturaCustom factura = facturaService.getFacturaByFolio(pagoFact.getFolio());
      facturas.add(factura);
      pagoFact.setAcredor(factura.getRazonSocialEmisor());
      pagoFact.setDeudor(factura.getRazonSocialRemitente());
      pagoFact.setTotalFactura(factura.getTotal());
      pagoFact.setMetodoPago(factura.getMetodoPago());
      if (MetodosPago.PUE.name().equals(factura.getMetodoPago())) {
        pagoFact.setIdCfdi(factura.getIdCfdi());
      }
    }
    pagoEvaluatorService.validatePaymentCreation(pagoDto, facturas);

    List<FacturaCustom> factPpd =
        facturas.stream()
            .filter(f -> MetodosPago.PPD.name().equals(f.getMetodoPago()))
            .collect(Collectors.toList());

    List<FacturaCustom> factPue =
        facturas.stream()
            .filter(f -> MetodosPago.PUE.name().equals(f.getMetodoPago()))
            .collect(Collectors.toList());

    if (!factPpd.isEmpty()) {
      log.info(
          "Generando complemento para : {}",
          factPpd.stream().map(f -> f.getFolio()).collect(Collectors.toList()));
      FacturaCustom facturaCustomComeplemento =
          facturaService.generateComplemento(facturas, pagoDto);
      pagoDto
          .getFacturas()
          .forEach(a -> a.setFolioReferencia(facturaCustomComeplemento.getFolio()));
    }
    if (!factPue.isEmpty()) {
      pagoDto.getFacturas().forEach(a -> a.setFolioReferencia(a.getFolio()));
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
    for (FacturaCustom dto : facturas) {
      if (!FormaPago.CREDITO.getPagoValue().equals(pagoDto.getFormaPago())
          && dto.getTipoDocumento().equals(TipoDocumento.FACTURA.getDescripcion())
          && dto.getMetodoPago().equals(MetodosPago.PUE.name())) {
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
    if (FormaPago.CREDITO.getPagoValue().equals(pagoDto.getFormaPago()) && facturas.size() == 1) {
      Optional<FacturaCustom> currentFactura = facturas.stream().findFirst();
      if (currentFactura.isPresent()
          && currentFactura.get().getMetodoPago().equals(MetodosPago.PUE.name())) {
        FacturaCustom fact = facturaService.getFacturaByFolio(currentFactura.get().getFolio());
        fact.setValidacionTeso(Boolean.TRUE);
        facturaService.updateFacturaCustom(currentFactura.get().getFolio(), fact);
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

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public PagoDto updatePago(Integer idPago, PagoDto pago)
      throws InvoiceManagerException, NtlinkUtilException {
    Pago entity =
        repository
            .findById(idPago)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("El pago con el id %d no existe", idPago)));

    PagoDto pagoDto =
        mapper.getPagoDtoFromEntity(entity).toBuilder()
            .revision1(pago.getRevision1())
            .revision2(pago.getRevision2())
            .revisor1(pago.getRevisor1())
            .revisor2(pago.getRevisor2())
            .build(); // payment only update revision
    PagoValidator.validatePayment(pago);

    List<FacturaCustom> facturas = new ArrayList<>();
    for (PagoFacturaDto pagoFact : pago.getFacturas()) {
      FacturaCustom factura = facturaService.getFacturaByFolio(pagoFact.getFolio());
      facturas.add(factura);
    }
    pagoEvaluatorService.validatePaymentUpdate(pago, mapper.getPagoDtoFromEntity(entity), facturas);

    if (pago.getStatusPago().equals(RevisionPagos.RECHAZADO.name())) {
      pagoDto.setStatusPago(RevisionPagos.RECHAZADO.name());
      pagoDto.setComentarioPago(pago.getComentarioPago());
      for (FacturaCustom factura : facturas) {
        if (MetodosPago.PUE.getClave().equals(factura.getMetodoPago())) {
          factura.setStatusFactura(FacturaStatus.RECHAZO_TESORERIA.getValor());
          factura.setStatusDetail(pago.getComentarioPago());
          facturaService.updateFacturaCustom(factura.getFolio(), factura);
        }
      }
    } else if (entity.getRevision1() && pago.getRevision2()) {
      pagoDto.setStatusPago(RevisionPagos.ACEPTADO.name());

      List<String> folioFacts =
          pago.getFacturas().stream()
              .map(f -> f.getFolioReferencia())
              .distinct()
              .collect(Collectors.toList());

      for (String folioCfdi : folioFacts) {
        FacturaCustom fact = facturaService.getFacturaByFolio(folioCfdi);
        fact.setValidacionTeso(true);
        facturaService.updateFacturaCustom(folioCfdi, fact);
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
    List<FacturaCustom> facturas = new ArrayList<>();
    for (PagoFacturaDto pagoFact : payment.getFacturas()) {
      FacturaCustom factura = facturaService.getFacturaBaseByFolio(pagoFact.getFolio());
      facturas.add(factura);
    }
    pagoEvaluatorService.deletepaymentValidation(payment, facturas);

    for (FacturaCustom facturaDto : facturas) {
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
      FacturaCustom fact = facturaService.getFacturaBaseByFolio(folioCfdi);
      if (TipoDocumento.COMPLEMENTO.equals(TipoDocumento.findByDesc(fact.getTipoDocumento()))) {
        facturaService.deleteFactura(fact.getFolio());
        filesService.deleteFacturaFile(fact.getFolio(), "PDF");
      }
    }

    filesService.deleteResourceFileByResourceReferenceAndType(
        "PAGOS", idPago.toString(), TipoArchivo.IMAGEN.name());
    repository.delete(mapper.getEntityFromPagoDto(payment));
  }

  public void delePagoFacturas(int id) {
    for (PagoFactura pagoFactura : facturaPagosRepository.findByPagoId(id)) {
      facturaPagosRepository.deleteById(pagoFactura.getId());
    }
  }
}
