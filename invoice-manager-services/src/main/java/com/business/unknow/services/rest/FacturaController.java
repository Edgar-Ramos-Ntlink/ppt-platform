package com.business.unknow.services.rest;

import com.business.unknow.enums.TipoDocumento;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.FacturaService;
import com.business.unknow.services.services.PagoService;
import com.unknown.error.PptUtilException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

  @Autowired private FacturaService service;

  @Autowired private PagoService pagoService;

  @GetMapping
  public ResponseEntity<Page<FacturaCustom>> getAllFacturasByParametros(
      @RequestParam Map<String, String> parameters) {
    return new ResponseEntity<>(service.getFacturasByParametros(parameters), HttpStatus.OK);
  }

  @GetMapping("/factura-reports")
  public ResponseEntity<ResourceFileDto> getAllFacturasReportsByParametros(
      @RequestParam Map<String, String> parameters) throws IOException {
    return new ResponseEntity<>(service.getFacturaReportsByParams(parameters), HttpStatus.OK);
  }

  @GetMapping("/complemento-reports")
  public ResponseEntity<ResourceFileDto> getAllComplementoReportsByParametros(
      @RequestParam Map<String, String> parameters) throws IOException {
    return new ResponseEntity<>(service.getComplementoReportsByParams(parameters), HttpStatus.OK);
  }

  @GetMapping("/{folio}")
  public ResponseEntity<FacturaCustom> getFactura(@PathVariable String folio) {
    return new ResponseEntity<>(service.getFacturaByFolio(folio), HttpStatus.OK);
  }

  @GetMapping("/{folio}/pagos")
  public ResponseEntity<List<PagoDto>> getPagosBy(@PathVariable String folio) {
    return new ResponseEntity<>(pagoService.findPagosByFolio(folio), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<FacturaCustom> createFacturaCustom(
      @RequestBody @Valid FacturaCustom factura) throws InvoiceManagerException, PptUtilException {
    return new ResponseEntity<>(service.createFacturaCustom(factura), HttpStatus.CREATED);
  }

  @PutMapping("/{folio}")
  public ResponseEntity<FacturaCustom> updateFactura(
      @PathVariable String folio, @RequestBody @Valid FacturaCustom facturaCustom)
      throws InvoiceManagerException, PptUtilException {
    return new ResponseEntity<>(service.updateFacturaCustom(folio, facturaCustom), HttpStatus.OK);
  }

  @PostMapping("/{folio}/timbrar")
  public ResponseEntity<FacturaCustom> timbrarFactura(
      @PathVariable String folio, @RequestBody @Valid FacturaCustom facturaCustom)
      throws InvoiceManagerException, PptUtilException {
    return new ResponseEntity<>(service.stamp(folio), HttpStatus.OK);
  }

  @PostMapping("/{folio}/cancelar")
  public ResponseEntity<FacturaCustom> cancelInvoice(
      @PathVariable String folio, @RequestBody @Valid FacturaCustom facturaCustom)
      throws InvoiceManagerException, PptUtilException {
    return new ResponseEntity<>(service.cancelInvoice(folio, facturaCustom), HttpStatus.OK);
  }

  @PostMapping("/{folio}/correos")
  public ResponseEntity<Void> renviarCorreos(@RequestBody @Valid String folio) {
    service.reSendMail(folio);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{folio}/reconstruccion-pdf")
  public ResponseEntity<Void> rebuildPDF(@RequestBody @Valid String folio)
      throws InvoiceManagerException {
    service.rebuildPDF(folio);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{folio}/sustitucion")
  public ResponseEntity<FacturaCustom> postSustitucion(
      @RequestBody @Valid FacturaCustom facturaCustom)
      throws InvoiceManagerException, PptUtilException {
    return new ResponseEntity<>(
        service.postRelacion(facturaCustom, TipoDocumento.FACTURA), HttpStatus.OK);
  }

  @PostMapping("/{folio}/nota-credito")
  public ResponseEntity<FacturaCustom> postNotaCredito(
      @RequestBody @Valid FacturaCustom facturaCustom)
      throws InvoiceManagerException, PptUtilException {
    return new ResponseEntity<>(
        service.postRelacion(facturaCustom, TipoDocumento.NOTA_CREDITO), HttpStatus.OK);
  }

  @PostMapping("/{folio}/complementos")
  public ResponseEntity<FacturaCustom> createComeplement(
      @PathVariable String folio, @RequestBody @Valid PagoDto pago)
      throws InvoiceManagerException, PptUtilException {
    FacturaCustom facturaCustom = service.createComplemento(folio, pago);

    return new ResponseEntity<>(facturaCustom, HttpStatus.OK);
  }
}
