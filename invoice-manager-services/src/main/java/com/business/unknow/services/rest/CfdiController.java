package com.business.unknow.services.rest;

import com.business.unknow.model.dto.cfdi.CfdiPagoDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.CfdiService;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cfdis")
public class CfdiController {

  @Autowired private CfdiService cfdiService;

  /**
   * Recalculate invoice amounts
   *
   * @param {@link Cfdi}
   * @return {@link Cfdi}
   * @throws {@link InvoiceManagerException}
   */
  @PutMapping("/recalculate")
  public ResponseEntity<Cfdi> recalculateCfdi(@RequestBody @Valid Cfdi cfdi)
      throws InvoiceManagerException {
    return new ResponseEntity<>(cfdiService.recalculateCfdi(cfdi), HttpStatus.OK);
  }

  /**
   * Gets payments by folio
   *
   * @param {@link List<CfdiPagoDto>}
   * @return
   */
  @GetMapping("/{folio}/payments")
  public ResponseEntity<List<CfdiPagoDto>> getPaymentsByFolio(String folio) {
    return new ResponseEntity<>(cfdiService.getPaymentsByFolio(folio), HttpStatus.OK);
  }
}
