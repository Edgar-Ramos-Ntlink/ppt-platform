package com.business.unknow.services.services;

import com.business.unknow.model.dto.services.EmpresaDetallesDto;
import com.business.unknow.services.entities.EmpresaDetalles;
import com.business.unknow.services.mapper.EmpresaDetallesMapper;
import com.business.unknow.services.repositories.EmpresaDetallesRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmpresaDetallesService {
  @Autowired private EmpresaDetallesMapper mapper;
  @Autowired private EmpresaDetallesRepository repository;

  public List<EmpresaDetallesDto> findByRfcAndTipo(String rfc, String type) {
    List<EmpresaDetalles> EmpresaDetalles = repository.findByRfcAndTipo(rfc, type);
    return mapper.getDtosFromEntities(EmpresaDetalles);
  }

  public EmpresaDetallesDto createDetalle(EmpresaDetallesDto empresaDetallesDto) {
    EmpresaDetalles entity = mapper.getEntityFromDto(empresaDetallesDto);
    return mapper.getDtoFromEntity(repository.save(entity));
  }

  public void deleteDetalle(Integer id) {
    EmpresaDetalles entity =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El detalle a borrar no existe"));
    repository.delete(entity);
  }
}
