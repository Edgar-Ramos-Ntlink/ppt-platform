package com.business.unknow.services.rest;

import com.business.unknow.model.dto.services.EmpresaDetallesDto;
import com.business.unknow.services.services.EmpresaDetallesService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public
@RequestMapping("/api/empresas") class EmpresaDetallesController {

  @Autowired private EmpresaDetallesService service;

  @GetMapping("/{rfc}/detalles/tipo/{tipo}")
  public ResponseEntity<List<EmpresaDetallesDto>> findByRfcAndTipo(
      @PathVariable String rfc, @PathVariable String tipo) {
    return new ResponseEntity<>(service.findByRfcAndTipo(rfc, tipo), HttpStatus.OK);
  }

  @PostMapping("/detalles")
  public ResponseEntity<EmpresaDetallesDto> createEmpresaDetalle(
      @RequestBody @Valid EmpresaDetallesDto empresaDetallesDto) {
    return new ResponseEntity<>(service.createDetalle(empresaDetallesDto), HttpStatus.OK);
  }

  @PutMapping("/detalles/{id}")
  public ResponseEntity<EmpresaDetallesDto> updateEmpresaDetalle(
      @RequestBody @Valid EmpresaDetallesDto empresaDetallesDto) {
    return new ResponseEntity<>(service.createDetalle(empresaDetallesDto), HttpStatus.OK);
  }

  @DeleteMapping("/detalles/{id}")
  public void deleteDetalleEmpresa(@PathVariable Integer id) {
    service.deleteDetalle(id);
  }
}
