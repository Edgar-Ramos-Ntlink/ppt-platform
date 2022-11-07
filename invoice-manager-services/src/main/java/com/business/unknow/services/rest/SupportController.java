package com.business.unknow.services.rest;

import com.business.unknow.model.dto.SupportRequestDto;
import com.business.unknow.model.dto.files.ResourceFileDto;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/support")
public class SupportController {

  @Autowired private RestTemplate template;

  @Value("${ntlink.inv.manager.host}")
  private String ntlinkHost;

  @GetMapping("/{folio}")
  public ResponseEntity<SupportRequestDto> getSupportRequestByFolio(@PathVariable String folio) {
    SupportRequestDto result =
        template.getForObject(
            String.format("%s/api/support/%s", ntlinkHost, folio), SupportRequestDto.class);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<SupportRequestDto> insertSupport(
      @RequestBody @Valid SupportRequestDto supportRequest) {
    SupportRequestDto result =
        template.postForObject(
            String.format("%s/api/support", ntlinkHost), supportRequest, SupportRequestDto.class);
    return new ResponseEntity<>(result, HttpStatus.CREATED);
  }

  @PutMapping("/{folio}")
  public ResponseEntity<SupportRequestDto> updateSupport(
      @PathVariable String folio, @RequestBody @Valid SupportRequestDto supportRequest) {
    template.put(String.format("%s/api/support/%s", ntlinkHost, folio), supportRequest);
    return new ResponseEntity<>(supportRequest, HttpStatus.OK);
  }

  @GetMapping("/{folio}/file")
  public ResponseEntity<ResourceFileDto> getAttachedDocumentByFolio(@PathVariable String folio) {
    ResourceFileDto result =
        template.getForObject(
            String.format("%s/api/support/%s/file", ntlinkHost, folio), ResourceFileDto.class);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping("/{folio}/file")
  public ResponseEntity<Void> upsertAttachedDocument(
      @PathVariable String folio, @RequestBody @Valid ResourceFileDto resourceFile) {
    template.postForObject(
        String.format("%s/api/support/%s/file", ntlinkHost), resourceFile, ResourceFileDto.class);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
