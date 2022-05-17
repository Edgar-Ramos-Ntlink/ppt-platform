package com.business.unknow.services.services;

import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.services.ClientDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Client;
import com.business.unknow.services.entities.Contribuyente;
import com.business.unknow.services.mapper.ClientMapper;
import com.business.unknow.services.repositories.ClientRepository;
import com.business.unknow.services.repositories.ContribuyenteRepository;
import com.business.unknow.services.util.ContactoHelper;
import com.business.unknow.services.util.validators.ClienteValidator;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
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

  @Autowired private ContribuyenteRepository contribuyenteRepository;

  @Autowired private ClientMapper mapper;

  @Autowired private DownloaderService downloaderService;
  private ContactoHelper contactoHelper = new ContactoHelper();

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
                  Contribuyente contribuyente = c.getInformacionFiscal();
                  row.put("RFC", contribuyente.getRfc());
                  row.put("RAZON_SOCIAL", contribuyente.getRazonSocial());
                  row.put("REGIMEN_FISCAL", contribuyente.getRegimenFiscal());
                  row.put("RESIDENCIA_FISCAL", contribuyente.getCp());
                  row.put("ACTIVO", c.getActivo() ? "ACTIVO" : "INACTIVO");
                  row.put("PROMOTOR", c.getCorreoPromotor());
                  row.put("EMAIL_CLIENTE", c.getCorreoContacto());

                  row.put(
                      "DOMICILIO",
                      String.format(
                          "%s EXT:%s INT:%s,%s,%s,%s,%s C.P. %s",
                          contribuyente.getCalle(),
                          contribuyente.getNoExterior(),
                          contribuyente.getNoInterior(),
                          contribuyente.getLocalidad(),
                          contribuyente.getMunicipio(),
                          contribuyente.getEstado(),
                          contribuyente.getPais(),
                          contribuyente.getCp()));

                  return row;
                })
            .collect(Collectors.toList());

    return downloaderService.generateBase64Report("Reporte Empresas", data, headers);
  }

  public ClientDto getClientByRFC(String rfc) {
    Client client =
        repository
            .findByRfc(rfc)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("No existe cliente con  rfc %s", rfc)));
    return mapper.getClientDtoFromEntity(client);
  }

  public List<ClientDto> getClientsByPromotor(String promotor) {
    return mapper.getClientDtosFromEntities(repository.findByCorreoPromotor(promotor));
  }

  public ClientDto getClientsByPromotorAndClient(String promotor, String rfc) {
    Optional<Client> entity = repository.findByCorreoPromotorAndClient(promotor, rfc);
    if (entity.isPresent()) {
      return mapper.getClientDtoFromEntity(entity.get());
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "El promotor no tiene el rfc selecionado");
    }
  }

  public ClientDto insertNewClient(ClientDto cliente) throws InvoiceManagerException {
    ClienteValidator.validate(cliente);
    cliente.setRfc(cliente.getRfc().trim());
    Optional<Contribuyente> entity = contribuyenteRepository.findByRfc(cliente.getRfc());
    Optional<Client> client =
        repository.findByCorreoPromotorAndClient(cliente.getCorreoPromotor(), cliente.getRfc());
    cliente.setCorreoContacto(
        contactoHelper.translateContacto(
            cliente.getRfc(), cliente.getCorreoPromotor(), cliente.getPorcentajeContacto()));
    Client clientEntity = mapper.getEntityFromClientDto(cliente);
    if (!entity.isPresent() && !client.isPresent()) {
      cliente.setRfc(cliente.getRfc().toUpperCase());
    } else if (entity.isPresent() && client.isPresent()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          String.format(
              "El RFC %s  para  el promotor %s ya existe en el sistema",
              cliente.getRfc(), cliente.getCorreoPromotor()));
    } else if (entity.isPresent() && !client.isPresent()) {
      clientEntity.setInformacionFiscal(entity.get());
    }
    clientEntity.setActivo(false);
    return mapper.getClientDtoFromEntity(repository.save(clientEntity));
  }

  public ClientDto updateClientInfo(ClientDto client, String rfc) throws InvoiceManagerException {
    ClienteValidator.validate(client);
    client.setCorreoContacto(
        contactoHelper.translateContacto(
            client.getRfc(), client.getCorreoPromotor(), client.getPorcentajeContacto()));
    Optional<Client> dbClient =
        repository.findByCorreoPromotorAndClient(client.getCorreoPromotor(), rfc);
    if (dbClient.isPresent()) {
      Client entity = mapper.getEntityFromClientDto(client);
      entity.getInformacionFiscal().setId(dbClient.get().getInformacionFiscal().getId());
      entity.getInformacionFiscal().setFechaCreacion(dbClient.get().getFechaCreacion());
      entity.getInformacionFiscal().setFechaActualizacion(new Date());
      return mapper.getClientDtoFromEntity(repository.save(entity));
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("El cliente con el rfc %s no existe", rfc));
    }
  }

  public void deleteClientInfo(String rfc) {
    Client dbClient =
        repository
            .findByRfc(rfc)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("El cliente con el rfc %s no existe", rfc)));
    repository.delete(dbClient);
  }
}
