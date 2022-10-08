package com.business.unknow.services.services;

import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.services.ClientDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Client;
import com.business.unknow.services.mapper.ClientMapper;
import com.business.unknow.services.repositories.ClientRepository;
import com.business.unknow.services.util.ContactoHelper;
import com.business.unknow.services.util.validators.ClienteValidator;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class ClientService {

  @Autowired private ClientRepository repository;

  @Autowired private ClientMapper mapper;

  @Autowired private DownloaderService downloaderService;
  private final ContactoHelper contactoHelper = new ContactoHelper();

  private Page<Client> findClientByParams(Map<String, String> parameters) {

    int page =
        (parameters.get("page") == null) || parameters.get("page").equals("")
            ? 0
            : Integer.valueOf(parameters.get("page"));
    int size = (parameters.get("size") == null) ? 10 : Integer.valueOf(parameters.get("size"));

    if (parameters.containsKey("promotor")) {
      return repository.findClientsFromPromotorByParms(
          parameters.get("promotor"),
          String.format("%%%s%%", parameters.containsKey("status") ? parameters.get("status") : ""),
          String.format("%%%s%%", parameters.containsKey("rfc") ? parameters.get("rfc") : ""),
          String.format(
              "%%%s%%", parameters.containsKey("razonSocial") ? parameters.get("razonSocial") : ""),
          PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
    } else {
      return repository.findClientsByParms(
          String.format("%%%s%%", parameters.containsKey("status") ? parameters.get("status") : ""),
          String.format("%%%s%%", parameters.containsKey("rfc") ? parameters.get("rfc") : ""),
          String.format(
              "%%%s%%", parameters.containsKey("razonSocial") ? parameters.get("razonSocial") : ""),
          PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
    }
  }

  public Page<ClientDto> getClientsByParametros(Map<String, String> parameters) {
    Page<Client> result = findClientByParams(parameters);
    return new PageImpl<>(
        mapper.getClientDtosFromEntities(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public ResourceFileDto getClientsByParametrosReport(Map<String, String> parameters)
      throws IOException {
    parameters.put("page", "0");
    parameters.put("size", "10000");

    List<String> headers =
        Arrays.asList(
            "RFC",
            "RAZON_SOCIAL",
            "REGIMEN_FISCAL",
            "RESIDENCIA_FISCAL",
            "ACTIVO",
            "PROMOTOR",
            "EMAIL_CLIENTE",
            "DOMICILIO");

    List<Map<String, Object>> data =
        findClientByParams(parameters).stream()
            .map(
                c -> {
                  Map<String, Object> row = new HashMap<>();
                  row.put("RFC", c.getRfc());
                  row.put("RAZON_SOCIAL", c.getRazonSocial());
                  row.put("REGIMEN_FISCAL", c.getRegimenFiscal());
                  row.put("RESIDENCIA_FISCAL", c.getCp());
                  row.put("ACTIVO", c.isActivo() ? "ACTIVO" : "INACTIVO");
                  row.put("PROMOTOR", c.getCorreoPromotor());
                  row.put("EMAIL_CLIENTE", c.getCorreoContacto());

                  row.put(
                      "DOMICILIO",
                      String.format(
                          "%s EXT:%s INT:%s,%s,%s,%s,%s C.P. %s",
                          c.getCalle(),
                          c.getNoExterior(),
                          c.getNoInterior(),
                          c.getColonia(),
                          c.getMunicipio(),
                          c.getEstado(),
                          c.getPais(),
                          c.getCp()));

                  return row;
                })
            .collect(Collectors.toList());

    return downloaderService.generateBase64Report("Reporte Empresas", data, headers);
  }

  public ClientDto getClientById(Integer id) {
    Client client =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El cliente solicitado no existe"));
    return mapper.getClientDtoFromEntity(client);
  }

  public List<ClientDto> getClientsByPromotor(String promotor) {
    return mapper.getClientDtosFromEntities(repository.findByCorreoPromotor(promotor));
  }

  public Optional<ClientDto> getClientsByPromotorAndClient(String promotor, String rfc) {
    Optional<Client> entity = repository.findByCorreoPromotorAndClient(promotor, rfc);
    if (entity.isPresent()) {
      return Optional.of(mapper.getClientDtoFromEntity(entity.get()));
    } else {
      return Optional.empty();
    }
  }

  public ClientDto insertNewClient(ClientDto cliente) throws InvoiceManagerException {
    ClienteValidator.validate(cliente);
    cliente.setRfc(cliente.getRfc().trim());
    Optional<Client> client =
        repository.findByCorreoPromotorAndClient(cliente.getCorreoPromotor(), cliente.getRfc());
    if (!client.isPresent()) {
      Client clientEntity = mapper.getEntityFromClientDto(cliente);
      return mapper.getClientDtoFromEntity(repository.save(clientEntity));
    } else {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          String.format(
              "El RFC %s  para  el promotor %s ya existe en el sistema",
              cliente.getRfc(), cliente.getCorreoPromotor()));
    }
  }

  public ClientDto updateClientInfo(ClientDto client, Integer id) throws InvoiceManagerException {
    ClienteValidator.validate(client);
    Optional<Client> dbClient = repository.findById(id);
    if (dbClient.isPresent()) {
      Client entity = mapper.getEntityFromClientDto(client);
      return mapper.getClientDtoFromEntity(repository.save(entity));
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format("El cliente con el rfc %s no existe", client.getRfc()));
    }
  }

  public void deleteClientInfo(Integer id) {
    Client dbClient =
        repository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El cliente no existe"));
    repository.delete(dbClient);
  }
}
