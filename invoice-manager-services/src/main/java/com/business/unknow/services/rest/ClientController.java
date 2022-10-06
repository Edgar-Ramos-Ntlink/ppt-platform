package com.business.unknow.services.rest;

import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.services.ClientDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.ClientService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class ClientController {

  @Autowired private ClientService service;

  @GetMapping("/clientes")
  public ResponseEntity<Page<ClientDto>> getClientsByParameters(
      @RequestParam Map<String, String> parameters) {
    return new ResponseEntity<>(service.getClientsByParametros(parameters), HttpStatus.OK);
  }

  @GetMapping("/clientes/report")
  public ResponseEntity<ResourceFileDto> getClientsByParametersReport(
      @RequestParam Map<String, String> parameters) throws IOException {
    return new ResponseEntity<>(service.getClientsByParametrosReport(parameters), HttpStatus.OK);
  }

  @GetMapping("/clientes/{id}")
  public ResponseEntity<ClientDto> updateClient(@PathVariable Integer id) {
    return new ResponseEntity<>(service.getClientById(id), HttpStatus.OK);
  }

  @GetMapping("/promotores/{promotor}/clientes")
  public ResponseEntity<List<ClientDto>> clinetesPorPromotor(@PathVariable String promotor) {
    return new ResponseEntity<>(service.getClientsByPromotor(promotor), HttpStatus.OK);
  }

  @GetMapping("/promotores/{promotor}/clientes/{rfc}")
  public ResponseEntity<ClientDto> clintePorPromotorYRfc(
      @PathVariable String promotor, @PathVariable String rfc) {
    return new ResponseEntity<>(
        service
            .getClientsByPromotorAndClient(promotor, rfc)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El promotor no tiene el rfc selecionado")),
        HttpStatus.OK);
  }

  @PostMapping("/clientes")
  public ResponseEntity<ClientDto> insertClient(@RequestBody @Valid ClientDto client)
      throws InvoiceManagerException {
    return new ResponseEntity<>(service.insertNewClient(client), HttpStatus.CREATED);
  }

  @PutMapping("/clientes/{id}")
  public ResponseEntity<ClientDto> updateClient(
      @PathVariable Integer id, @RequestBody @Valid ClientDto client)
      throws InvoiceManagerException {
    return new ResponseEntity<>(service.updateClientInfo(client, id), HttpStatus.OK);
  }

  @DeleteMapping("/clientes/{id}")
  public ResponseEntity<Void> deleteClient(@PathVariable Integer id) {
    service.deleteClientInfo(id);
    return new ResponseEntity<Void>(HttpStatus.OK);
  }
}
