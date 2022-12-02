package com.business.unknow.services.rest;

import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.services.SupportRequestDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.SupportRequestService;
import java.io.IOException;
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
@RequestMapping("/api/support")
public class SupportController {

  @Autowired private SupportRequestService service;

  @GetMapping
  public ResponseEntity<Page<SupportRequestDto>> getSoportesByParametros(
      @RequestParam Map<String, String> parameters) {
    return new ResponseEntity<>(service.getSupportRequestByParams(parameters), HttpStatus.OK);
  }

  @GetMapping("/report")
  public ResponseEntity<ResourceFileDto> getSoporteReportByParametros(
      @RequestParam Map<String, String> parameters) throws IOException {
    return new ResponseEntity<>(service.getSupportReportByParams(parameters), HttpStatus.OK);
  }

  @GetMapping("/{folio}")
  public ResponseEntity<SupportRequestDto> getSupportRequestByFolio(@PathVariable Integer folio) {
    return new ResponseEntity<>(service.getSupportRequest(folio), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<SupportRequestDto> insertSupport(
      @RequestBody @Valid SupportRequestDto supportRequest) throws InvoiceManagerException {
    return new ResponseEntity<>(service.createSupportRequest(supportRequest), HttpStatus.CREATED);
  }

  @PutMapping("/{folio}")
  public ResponseEntity<SupportRequestDto> updateSupport(
      @PathVariable Integer folio, @RequestBody @Valid SupportRequestDto supportRequest) {
    return new ResponseEntity<>(
        service.updateSuppoprtRequest(supportRequest, folio), HttpStatus.OK);
  }
}
