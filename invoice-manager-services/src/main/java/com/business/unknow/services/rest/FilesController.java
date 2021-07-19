/** */
package com.business.unknow.services.rest;

import com.business.unknow.enums.S3BucketsEnum;
import com.business.unknow.enums.TipoArchivoEnum;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.error.InvoiceCommonException;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.FilesService;
import com.business.unknow.services.util.helpers.StringHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

  @Autowired private StringHelper stringHelper;

  @GetMapping("/facturas/{folio}/files/{fileType}")
  public ResponseEntity<FacturaFileDto> getFacturaFiles(
      @PathVariable(name = "folio") String folio, @PathVariable(name = "fileType") String fileType)
      throws InvoiceManagerException {
    return new ResponseEntity<>(
        service.getFacturaFileByFolioAndType(folio, fileType), HttpStatus.OK);
  }

  @GetMapping("/recursos/{recurso}/files/{fileType}/referencias/{referencia}")
  public ResponseEntity<ResourceFileDto> getResourceFiles(
      @PathVariable(name = "recurso") String recurso,
      @PathVariable(name = "fileType") String fileType,
      @PathVariable(name = "referencia") String referencia)
      throws InvoiceManagerException {
    return new ResponseEntity<>(
        service.getResourceFileByResourceReferenceAndType(
            S3BucketsEnum.findByValor(recurso),
            referencia,
            fileType,
            TipoArchivoEnum.valueOf(recurso).getFormat()),
        HttpStatus.OK);
  }

  @PostMapping("/facturas/{folio}/files")
  public ResponseEntity<Void> insertFacturaFile(@RequestBody @Valid FacturaFileDto facturaFile)
      throws InvoiceManagerException {
    // TODO move this code inside the service
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      bos.write(Base64.getDecoder().decode(facturaFile.getData()));
      service.upsertFacturaFile(
          S3BucketsEnum.CFDIS,
          TipoArchivoEnum.valueOf(facturaFile.getTipoArchivo()).getFormat(),
          facturaFile.getFolio(),
          bos);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (IOException e) {
      // TODO for generic Exception is better use ResponseStatusException
      throw new InvoiceManagerException(e.getMessage(), HttpStatus.CONFLICT.value());
    }
  }

  // TODO refactor this controller to use S3 instead Mysql
  @PostMapping("/recursos/{recurso}/files")
  public ResponseEntity<Void> insertResourceFile(@RequestBody @Valid ResourceFileDto resourceFile)
      throws InvoiceManagerException, InvoiceCommonException {
    resourceFile.setFormato(stringHelper.getFileFormatFromBase64(resourceFile.getData()));
    service.upsertResourceFile(resourceFile);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @DeleteMapping("/recursos/files/{id}")
  public ResponseEntity<Void> deleteRecursoFile(@PathVariable Integer id)
      throws InvoiceManagerException {
    service.deleteResourceFile(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  // TODO code this implemntation, for now this is only a mock response
  @GetMapping("/empresas/{rfc}/documentos")
  public ResponseEntity<List<ResourceFileDto>> findAttachedDocumentsBy(@PathVariable String rfc) {
    List<ResourceFileDto> files = new ArrayList<>();

    return new ResponseEntity<>(files, HttpStatus.OK);
  }
}
