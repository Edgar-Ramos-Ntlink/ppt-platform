package com.business.unknow.services.services;

import com.business.unknow.model.dto.services.EmpresaIngresosDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.EmpresaIngresos;
import com.business.unknow.services.mapper.EmpresaIngresosMapper;
import com.business.unknow.services.repositories.EmpresaIngresosRepository;
import com.business.unknow.services.util.validators.EmpresaValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmpresaIngresosService {

  @Autowired private EmpresaIngresosRepository repository;

  @Autowired private EmpresaIngresosMapper mapper;

  @Autowired
  @Qualifier("EmpresaValidator")
  private EmpresaValidator empresaValidator;

  public List<EmpresaIngresosDto> findDatosEmpresaByRfc(String rfc) {
    return mapper.getDatosAnualesDtoFromEntities(repository.findByRfc(rfc));
  }

  public EmpresaIngresosDto createDatoAnual(EmpresaIngresosDto dato)
      throws InvoiceManagerException {
    empresaValidator.validateDatoAnual(dato);
    EmpresaIngresos dataToSave = repository.save(mapper.getDatoAnualEntityFromDto(dato));
    return mapper.getDatoAnualDtoFromEntity(dataToSave);
  }

  public void deleteDatoAnual(Integer id) {
    EmpresaIngresos toBeDeleted =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "EL dato a borrar no existe"));
    repository.delete(toBeDeleted);
  }
}
