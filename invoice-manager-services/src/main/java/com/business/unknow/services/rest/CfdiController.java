/** */
package com.business.unknow.services.rest;

import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.cfdi.CfdiPagoDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.CfdiService;
import com.business.unknow.services.services.FacturaService;
import com.business.unknow.services.util.validators.CfdiValidator;
import com.mx.ntlink.NtlinkUtilException;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** @author ralfdemoledor */
@RestController
@RequestMapping("/api/cfdis")
public class CfdiController {

  @Autowired private FacturaService facturaService;

  @Autowired private CfdiService cfdiService;

  @Autowired
  @Qualifier("CfdiValidator")
  private CfdiValidator validator;

  @PostMapping("/validacion")
  public ResponseEntity<String> validateCfdi(@RequestBody @Valid Cfdi cfdi)
      throws InvoiceManagerException {
    validator.validate(cfdi);
    return new ResponseEntity<>("VALIDA", HttpStatus.OK);
  }

  @PutMapping("/recalculate")
  public ResponseEntity<Cfdi> calculateMontosCfdi(@RequestBody @Valid Cfdi cfdi)
      throws InvoiceManagerException {
    validator.validate(cfdi);
    return new ResponseEntity<>(cfdiService.recalculateCfdiAmmounts(cfdi), HttpStatus.OK);
  }

  @GetMapping("/{folio}")
  public ResponseEntity<Cfdi> getfacturaCfdi(@PathVariable String folio)
      throws NtlinkUtilException {
    return new ResponseEntity<>(cfdiService.getCfdiByFolio(folio), HttpStatus.OK);
  }

  @GetMapping("/{folio}/facturaInfo")
  public ResponseEntity<FacturaCustom> getfacturabyFolioCfdi(@PathVariable String folio) {
    return new ResponseEntity<>(facturaService.getFacturaBaseByFolio(folio), HttpStatus.OK);
  }

  @PostMapping("")
  public ResponseEntity<Cfdi> insertFacturaCfdi(
      @PathVariable String folio, @RequestBody @Valid Cfdi cfdi) throws InvoiceManagerException {
    return new ResponseEntity<>(cfdiService.insertNewCfdi(cfdi), HttpStatus.OK);
  }

  @PutMapping("/{folio}")
  public ResponseEntity<Cfdi> updateFacturaCfdi(
      @PathVariable String folio, @RequestBody @Valid Cfdi cfdi) throws InvoiceManagerException {
    return new ResponseEntity<>(cfdiService.updateCfdiBody(folio, cfdi), HttpStatus.OK);
  }

  @DeleteMapping("/{folio}")
  public ResponseEntity<Void> deleteFacturaCfdi(@PathVariable String folio)
      throws NtlinkUtilException {
    cfdiService.deleteCfdi(folio);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  // PAGOS PPD CFDI
  @GetMapping("/{id}/pagos")
  public ResponseEntity<List<CfdiPagoDto>> getPagosPPD(@PathVariable Integer id) {
    return new ResponseEntity<>(cfdiService.getPagosPPD(id), HttpStatus.OK);
  }
}
