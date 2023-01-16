package com.business.unknow.services.rest;

import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.CfdiService;
import com.unknown.cfdi.modelos.Cfdi;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
