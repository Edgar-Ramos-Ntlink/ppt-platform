package com.business.unknow.services.services;

import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.services.CuentaBancariaDto;
import com.business.unknow.services.entities.CuentaBancaria;
import com.business.unknow.services.entities.Empresa;
import com.business.unknow.services.mapper.CuentaBancariaMapper;
import com.business.unknow.services.repositories.CuentaBancariaRepository;
import com.business.unknow.services.repositories.EmpresaRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class CuentaBancariaService {

  @Autowired private CuentaBancariaRepository repository;

  @Autowired private EmpresaRepository empresaRepository;

  @Autowired private CuentaBancariaMapper mapper;

  @Autowired private DownloaderService downloaderService;

  private Specification<CuentaBancaria> buildSearchFilters(Map<String, String> parameters) {

    log.info("Finding facturas by {}", parameters);

    return new Specification<CuentaBancaria>() {

      private static final long serialVersionUID = -7435096122716669730L;

      @Override
      public Predicate toPredicate(
          Root<CuentaBancaria> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (parameters.get("empresa") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(root.get("rfc"), "%" + parameters.get("empresa") + "%")));
        }
        if (parameters.get("banco") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(root.get("banco"), "%" + parameters.get("banco") + "%")));
        }
        if (parameters.get("cuenta") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(root.get("cuenta"), "%" + parameters.get("cuenta") + "%")));
        }

        if (parameters.get("clabe") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("clabe"), parameters.get("clabe"))));
        }

        if (parameters.get("razonSocial") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("razonSocial"), "%" + parameters.get("razonSocial") + "%")));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }

  public Page<CuentaBancariaDto> getCuentasBancariasByfilters(Map<String, String> parameters) {
    Page<CuentaBancaria> result;
    int page =
        (parameters.get("page") == null) || parameters.get("page").equals("")
            ? 0
            : Integer.valueOf(parameters.get("page"));
    int size = (parameters.get("size") == null) ? 10 : Integer.valueOf(parameters.get("size"));
    result =
        repository.findAll(
            buildSearchFilters(parameters),
            PageRequest.of(page, size, Sort.by("fechaActualizacion").descending()));
    return new PageImpl<>(
        mapper.getCuentaBancariaDtosFromEntities(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public ResourceFileDto getCuentasBancariasReport(Map<String, String> parameters)
      throws IOException {

    Page<CuentaBancariaDto> result = getCuentasBancariasByfilters(parameters);

    List<String> headers =
        Arrays.asList(
            "BANCO",
            "RFC EMPRESA",
            "NUMERO DE CUENTA",
            "CLABE",
            "TIPO CONTRATO",
            "SUCURSAL",
            "DOMICILIO",
            "EXPEDIENTE ACTUALIZADO",
            "CREACION",
            "ACTUALIZACION");

    List<Map<String, Object>> data =
        result.getContent().stream()
            .map(
                cuenta -> {
                  Map<String, Object> row = new HashMap<>();
                  row.put("BANCO", cuenta.getBanco());
                  row.put("RFC EMPRESA", cuenta.getRfc());
                  row.put("NUMERO DE CUENTA", cuenta.getCuenta());
                  row.put("CLABE", cuenta.getClabe());
                  row.put("TIPO CONTRATO", cuenta.getTipoContrato());
                  row.put("SUCURSAL", cuenta.getSucursal());
                  row.put("DOMICILIO", cuenta.getDomicilioBanco());
                  row.put("EXPEDIENTE ACTUALIZADO", cuenta.getExpedienteActualizado());
                  row.put(
                      "CREACION",
                      String.format(
                          "%tF %tR", cuenta.getFechaCreacion(), cuenta.getFechaCreacion()));
                  row.put(
                      "ACTUALIZACION",
                      String.format(
                          "%tF %tR", cuenta.getFechaActualizacion(), cuenta.getFechaCreacion()));
                  return row;
                })
            .collect(Collectors.toList());

    return downloaderService.generateBase64Report("Reporte Cuentas Bancarias", data, headers);
  }

  public List<CuentaBancariaDto> getCuentasPorRfc(String rfc) {
    return mapper.getCuentaBancariaDtosFromEntities(repository.findByRfc(rfc));
  }

  public CuentaBancariaDto infoCuentaBancaria(String rfc, String cuenta) {
    return mapper.getCuentaBancariaToFromEntity(repository.findByRfcAndCuenta(rfc, cuenta).get());
  }

  public CuentaBancariaDto createCuentaBancaria(CuentaBancariaDto cuentaDto) {
    Optional<CuentaBancaria> entity =
        repository.findByRfcAndCuenta(cuentaDto.getRfc(), cuentaDto.getCuenta());
    if (entity.isPresent()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          String.format("Esta Empresa con esta cuenta ya existe %s", cuentaDto.getCuenta()));
    } else {
      Empresa empresa =
          empresaRepository
              .findByRfc(cuentaDto.getRfc())
              .orElseThrow(
                  () ->
                      new ResponseStatusException(
                          HttpStatus.BAD_REQUEST,
                          String.format("Esta Empresa no existe %s", cuentaDto.getRfc())));
      cuentaDto.setLinea(empresa.getTipo());
      cuentaDto.setRazonSocial(empresa.getRazonSocial());
      CuentaBancaria cuentaBancaria =
          repository.save(mapper.getEntityFromCuentaBancariaDto(cuentaDto));
      return mapper.getCuentaBancariaToFromEntity(cuentaBancaria);
    }
  }

  public CuentaBancariaDto updateCuentaBancaria(
      Integer accountId, CuentaBancariaDto cuentaBancariaDto) {
    CuentaBancaria entity =
        repository
            .findById(accountId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        String.format(
                            "Esta Empresa con esta cuenta ya existe %s",
                            cuentaBancariaDto.getCuenta())));

    CuentaBancaria account = mapper.getEntityFromCuentaBancariaDto(cuentaBancariaDto);
    account.setId(entity.getId());
    return mapper.getCuentaBancariaToFromEntity(repository.save(account));
  }

  public void deleteCuentaBancaria(Integer id) {
    CuentaBancaria entity =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("Esta cuenta no existe %d", id)));
    repository.delete(entity);
  }
}
