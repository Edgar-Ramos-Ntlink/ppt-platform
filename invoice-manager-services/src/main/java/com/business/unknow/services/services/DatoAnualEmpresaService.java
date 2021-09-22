package com.business.unknow.services.services;

import com.business.unknow.model.dto.services.DatoAnualEmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.DatoAnualEmpresa;
import com.business.unknow.services.mapper.DatoAnualEmpresaMapper;
import com.business.unknow.services.repositories.DatoAnualEmpresaRepository;
import com.business.unknow.services.util.validators.EmpresaValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DatoAnualEmpresaService {

  @Autowired private DatoAnualEmpresaRepository repository;

  @Autowired private DatoAnualEmpresaMapper mapper;

  @Autowired
  @Qualifier("EmpresaValidator")
  private EmpresaValidator empresaValidator;

  public List<DatoAnualEmpresaDto> findDatosEmpresaByRfc(String rfc) {
    return mapper.getDatosAnualesDtoFromEntities(repository.findByRfc(rfc));
  }

  public DatoAnualEmpresaDto createDatoAnual(DatoAnualEmpresaDto dato)
      throws InvoiceManagerException {
    empresaValidator.validateDatoAnual(dato);
    DatoAnualEmpresa dataToSave = repository.save(mapper.getDatoAnualEntityFromDto(dato));
    return mapper.getDatoAnualDtoFromEntity(dataToSave);
  }

  public void deleteDatoAnual(Integer id) {
    DatoAnualEmpresa toBeDeleted =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "EL dato a borrar no existe"));
    repository.delete(toBeDeleted);
  }
}
