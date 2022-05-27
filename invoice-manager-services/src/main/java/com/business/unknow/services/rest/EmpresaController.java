package com.business.unknow.services.rest;

import com.business.unknow.enums.S3Buckets;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.EmpresaService;
import com.business.unknow.services.services.FilesService;
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
@RequestMapping("/api")
public class EmpresaController {

  @Autowired private EmpresaService service;
  @Autowired private FilesService fileService;

  @GetMapping("/empresas")
  public ResponseEntity<Page<Map<String, String>>> getEmpresasByParameter(
      @RequestParam Map<String, String> parameters) {
    return new ResponseEntity<>(service.getEmpresasByParametros(parameters), HttpStatus.OK);
  }

  @GetMapping("/empresas/report")
  public ResponseEntity<ResourceFileDto> getEmpresasByParametersReport(
      @RequestParam Map<String, String> parameters) throws IOException {
    return new ResponseEntity<>(service.getCompaniesReport(parameters), HttpStatus.OK);
  }

  @GetMapping(value = "/empresas/{rfc}/logo")
  public ResponseEntity<byte[]> getCompanyLogo(@PathVariable String rfc) throws IOException {
    return fileService.getS3File(S3Buckets.EMPRESAS, "LOGO", rfc);
  }

  @GetMapping("/empresas/{rfc}")
  public ResponseEntity<EmpresaDto> updateClient(@PathVariable String rfc) {
    return new ResponseEntity<>(service.getEmpresaByRfc(rfc), HttpStatus.OK);
  }

  @GetMapping("/lineas/{linea}/giros/{giro}/empresas")
  public ResponseEntity<List<EmpresaDto>> getEmpresasByLineaAndGiro(
      @PathVariable(name = "linea") String linea, @PathVariable(name = "giro") Integer giro) {
    return new ResponseEntity<>(service.getEmpresasByGiroAndLinea(linea, giro), HttpStatus.OK);
  }

  @PostMapping("/empresas")
  public ResponseEntity<EmpresaDto> insertClient(@RequestBody @Valid EmpresaDto empresa)
      throws InvoiceManagerException {
    return new ResponseEntity<>(service.insertNewEmpresa(empresa), HttpStatus.CREATED);
  }

  @PutMapping("/empresas/{rfc}")
  public ResponseEntity<EmpresaDto> updateEmpresa(
      @PathVariable String rfc, @RequestBody @Valid EmpresaDto empresa)
      throws InvoiceManagerException {
    return new ResponseEntity<>(service.updateEmpresaInfo(empresa, rfc), HttpStatus.OK);
  }
}
