package com.business.unknow.services.rest;

import com.business.unknow.model.dto.services.DatoAnualEmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.DatoAnualEmpresaService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DatoAnualEmpresaController {

  @Autowired private DatoAnualEmpresaService service;

  @GetMapping("/empresas/{rfc}/datos")
  public ResponseEntity<List<DatoAnualEmpresaDto>> findDataBy(@PathVariable String rfc) {
    return new ResponseEntity<>(service.findDatosEmpresaByRfc(rfc), HttpStatus.OK);
  }

  @PostMapping("/empresas/{rfc}/datos")
  public ResponseEntity<DatoAnualEmpresaDto> createData(
      @PathVariable String rfc, @RequestBody @Valid DatoAnualEmpresaDto dato)
      throws InvoiceManagerException {
    return new ResponseEntity<>(service.createDatoAnual(dato), HttpStatus.CREATED);
  }

  @DeleteMapping("/empresas/{rfc}/datos/{id}")
  public ResponseEntity<Void> deleteData(@PathVariable Integer id, @PathVariable String rfc) {
    service.deleteDatoAnual(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
