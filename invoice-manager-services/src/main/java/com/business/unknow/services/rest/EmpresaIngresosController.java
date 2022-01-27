package com.business.unknow.services.rest;

import com.business.unknow.model.dto.services.EmpresaIngresosDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.EmpresaIngresosService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmpresaIngresosController {

  @Autowired private EmpresaIngresosService service;

  @GetMapping("/empresas/{rfc}/datos")
  public ResponseEntity<List<EmpresaIngresosDto>> findDataBy(@PathVariable String rfc) {
    return new ResponseEntity<>(service.findDatosEmpresaByRfc(rfc), HttpStatus.OK);
  }

  @PostMapping("/empresas/{rfc}/datos")
  public ResponseEntity<EmpresaIngresosDto> createData(
      @PathVariable String rfc, @RequestBody @Valid EmpresaIngresosDto dato)
      throws InvoiceManagerException {
    return new ResponseEntity<>(service.createDatoAnual(dato), HttpStatus.CREATED);
  }

  @DeleteMapping("/empresas/{rfc}/datos/{id}")
  public ResponseEntity<Void> deleteData(@PathVariable Integer id, @PathVariable String rfc) {
    service.deleteDatoAnual(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
