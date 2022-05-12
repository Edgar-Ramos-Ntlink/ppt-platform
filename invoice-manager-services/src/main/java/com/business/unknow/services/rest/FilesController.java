package com.business.unknow.services.rest;

import com.business.unknow.enums.S3Buckets;
import com.business.unknow.enums.TipoArchivo;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.FilesService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
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
public class FilesController {

  @Autowired private FilesService service;

  @GetMapping("/facturas/{folio}/files/{fileType}")
  public ResponseEntity<FacturaFileDto> getFacturaFiles(
      @PathVariable(name = "folio") String folio, @PathVariable(name = "fileType") String fileType)
      throws InvoiceManagerException {
    return new ResponseEntity<>(
        service.getFacturaFileByFolioAndType(folio, fileType), HttpStatus.OK);
  }

  @GetMapping("/recursos/{recurso}/referencias/{referencia}/files")
  public ResponseEntity<List<ResourceFileDto>> getResourcesByResourceType(
      @PathVariable(name = "recurso") String recurso,
      @PathVariable(name = "referencia") String referencia) {
    return new ResponseEntity<>(
        service.findResourcesByResourceType(S3Buckets.valueOf(recurso), referencia), HttpStatus.OK);
  }

  @GetMapping("/recursos/{recurso}/referencias/{referencia}/files/{fileType}")
  public ResponseEntity<ResourceFileDto> getResourceFileByResourceTypeAndRefrence(
      @PathVariable(name = "recurso") String recurso,
      @PathVariable(name = "fileType") String fileType,
      @PathVariable(name = "referencia") String referencia)
      throws InvoiceManagerException {
    return new ResponseEntity<>(
        service.getResourceFileByResourceReferenceAndType(
            S3Buckets.findByValor(recurso), referencia, fileType),
        HttpStatus.OK);
  }

  @PostMapping("/facturas/{folio}/files")
  public ResponseEntity<Void> insertFacturaFile(@RequestBody @Valid FacturaFileDto facturaFile)
      throws InvoiceManagerException {
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      bos.write(Base64.getDecoder().decode(facturaFile.getData()));
      service.upsertFacturaFile(
          S3Buckets.CFDIS,
          TipoArchivo.valueOf(facturaFile.getTipoArchivo()).getFormat(),
          facturaFile.getFolio(),
          bos);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (IOException e) {
      throw new InvoiceManagerException(e.getMessage(), HttpStatus.CONFLICT.value());
    }
  }

  @PostMapping("/recursos/{recurso}/files")
  public ResponseEntity<Void> insertResourceFile(@RequestBody @Valid ResourceFileDto resourceFile)
      throws InvoiceManagerException {
    service.upsertResourceFile(resourceFile);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @DeleteMapping("/recursos/files/{id}")
  public ResponseEntity<Void> deleteRecursoFile(@PathVariable Integer id)
      throws InvoiceManagerException {
    service.deleteResourceFile(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
