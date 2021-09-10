package com.business.unknow.services.services;

import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Empresa;
import com.business.unknow.services.mapper.EmpresaMapper;
import com.business.unknow.services.repositories.EmpresaRepository;
import com.business.unknow.services.services.executor.EmpresaExecutorService;
import com.business.unknow.services.util.validators.EmpresaValidator;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmpresaService {

  @Autowired private EmpresaRepository repository;

  @Autowired private EmpresaMapper mapper;

  @Autowired private EmpresaExecutorService empresaEvaluatorService;

  @Autowired private NotificationHandlerService notificationHandlerService;

  @Autowired
  @Qualifier("EmpresaValidator")
  private EmpresaValidator empresaValidator;

  public Page<EmpresaDto> getEmpresasByParametros(
      Optional<String> rfc, Optional<String> razonSocial, String linea, int page, int size) {
    Page<Empresa> result;
    if (!razonSocial.isPresent() && !rfc.isPresent()) {
      result =
          repository.findAllWithLinea(String.format("%%%s%%", linea), PageRequest.of(page, size));
    } else if (rfc.isPresent()) {
      result =
          repository.findByRfcIgnoreCaseContaining(
              String.format("%%%s%%", rfc.get()),
              String.format("%%%s%%", linea),
              PageRequest.of(page, size));
    } else {
      result =
          repository.findByRazonSocialIgnoreCaseContaining(
              String.format("%%%s%%", razonSocial.get()),
              String.format("%%%s%%", linea),
              PageRequest.of(page, size));
    }
    return new PageImpl<>(
        mapper.getEmpresaDtosFromEntities(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public EmpresaDto getEmpresaByRfc(String rfc) {
    Empresa empresa =
        repository
            .findByRfc(rfc)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("No existe la empresa con rfc %s", rfc)));
    return mapper.getEmpresaDtoFromEntity(empresa);
  }

  public List<EmpresaDto> getEmpresasByGiroAndLinea(String tipo, Integer giro) {
    return mapper.getEmpresaDtosFromEntities(repository.findByTipoAndGiro(tipo, giro));
  }

  @Transactional(
      rollbackOn = {InvoiceManagerException.class, DataAccessException.class, SQLException.class})
  public EmpresaDto insertNewEmpresa(EmpresaDto empresaDto) throws InvoiceManagerException {
    empresaValidator.validatePostEmpresa(empresaDto);
    empresaDto.setActivo(false);

    if (repository.findByRfc(empresaDto.getRfc()).isPresent()) {
      throw new InvoiceManagerException(
          "Ya existe la empresa",
          String.format("La empresa %s ya existe", empresaDto.getRfc()),
          HttpStatus.CONFLICT.value());
    }
    notificationHandlerService.sendNotification(
        "NUEVA_EMPRESA", String.format("Se creo la empresa %s", empresaDto.getRazonSocial()));
    return empresaEvaluatorService.createEmpresa(empresaDto);
  }

  public EmpresaDto updateEmpresaInfo(EmpresaDto empresaDto, String rfc)
      throws InvoiceManagerException {

    empresaValidator.validatePostEmpresa(empresaDto);
    Empresa empresa =
        repository
            .findByRfc(rfc)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("El empresa con el rfc %s no existe", rfc)));

    if(empresa.getActivo()&&!empresaDto.getActivo()){
      notificationHandlerService.sendNotification(
              "DESACTIVACION_EMPRESA", String.format("Se desactivo la empresa %s", empresaDto.getRazonSocial()));
    }else  if(!empresa.getActivo()&&empresaDto.getActivo()){
      notificationHandlerService.sendNotification(
              "ACTIVACION_EMPRESA", String.format("Se activo la empresa %s", empresaDto.getRazonSocial()));
    }
    Empresa companyToSave = mapper.getEntityFromEmpresaDto(empresaDto);
    companyToSave.setId(empresa.getId());

    return mapper.getEmpresaDtoFromEntity(repository.save(companyToSave));
  }
}
