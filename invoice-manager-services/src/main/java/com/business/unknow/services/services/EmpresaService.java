package com.business.unknow.services.services;

import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.CuentaBancaria;
import com.business.unknow.services.entities.Empresa;
import com.business.unknow.services.entities.EmpresaDetalles;
import com.business.unknow.services.mapper.EmpresaMapper;
import com.business.unknow.services.repositories.EmpresaRepository;
import com.business.unknow.services.util.validators.EmpresaValidator;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
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
public class EmpresaService {

  @Autowired private EmpresaRepository repository;

  @Autowired private EmpresaMapper mapper;

  @Autowired private CatalogService catalogService;

  @Autowired private NotificationHandlerService notificationHandlerService;

  @Autowired private DownloaderService downloaderService;

  @Autowired
  @Qualifier("EmpresaValidator")
  private EmpresaValidator empresaValidator;

  private Specification<Empresa> buildSearchFilters(Map<String, String> parameters) {

    log.info("Finding empresas by {}", parameters);

    return new Specification<Empresa>() {

      private static final long serialVersionUID = -7435096122716669730L;

      @Override
      public Predicate toPredicate(
          Root<Empresa> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (parameters.get("activo") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(
                      root.get("activo"), Boolean.valueOf(parameters.get("activo")))));
        }
        if (parameters.get("giro") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(
                      root.get("giro"), Integer.valueOf(parameters.get("giro")))));
        }
        if (parameters.get("linea") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("tipo"), parameters.get("linea"))));
        }

        if (parameters.get("rfc") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(root.get("rfc"), "%" + parameters.get("rfc") + "%")));
        }

        if (parameters.get("razonSocial") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("razonSocial"), "%" + parameters.get("razonSocial") + "%")));
        }

        if (parameters.get("actividadSAT") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("actividadSAT"), "%" + parameters.get("actividadSAT") + "%")));
        }

        if (parameters.get("registroPatronal") != null) {
          if (Boolean.valueOf(parameters.get("registroPatronal"))) {
            predicates.add(
                criteriaBuilder.and(criteriaBuilder.isNotNull(root.get("registroPatronal"))));
          } else {
            predicates.add(
                criteriaBuilder.and(criteriaBuilder.isNull(root.get("registroPatronal"))));
          }
        }

        if (parameters.get("representanteLegal") != null) {
          if (Boolean.valueOf(parameters.get("representanteLegal"))) {
            predicates.add(
                criteriaBuilder.and(criteriaBuilder.isNotNull(root.get("representanteLegal"))));
          } else {
            predicates.add(
                criteriaBuilder.and(criteriaBuilder.isNull(root.get("representanteLegal"))));
          }
        }

        if (parameters.get("impuestoEstatal") != null) {
          if (Boolean.valueOf(parameters.get("impuestoEstatal"))) {
            predicates.add(
                criteriaBuilder.and(criteriaBuilder.isNotNull(root.get("impuestoEstatal"))));
          } else {
            predicates.add(
                criteriaBuilder.and(criteriaBuilder.isNull(root.get("impuestoEstatal"))));
          }
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }

  public Page<Map<String, String>> getEmpresasByParametros(Map<String, String> parameters) {
    Page<Empresa> result;
    int page =
        (parameters.get("page") == null) || parameters.get("page").equals("")
            ? 0
            : Integer.valueOf(parameters.get("page"));
    int size = (parameters.get("size") == null) ? 10 : Integer.valueOf(parameters.get("size"));
    result =
        repository.findAll(
            buildSearchFilters(parameters),
            PageRequest.of(page, size, Sort.by("razonSocial").ascending()));
    return new PageImpl<Map<String, String>>(
        getFlatCompanyDetails(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public List<Map<String, String>> getFlatCompanyDetails(List<Empresa> empresas) {
    return empresas.stream()
        .map(
            e -> {
              Map<String, String> row = new HashMap<>();

              row.put("NOMBRE_CORTO", e.getNombre());
              row.put("EMPRESA", e.getRazonSocial());
              row.put("RFC", e.getRfc());

              row.put(
                  "DOMICILIO",
                  String.format(
                      "%s EXT:%s INT:%s,%s,%s,%s,%s C.P. %s",
                      e.getCalle(),
                      e.getNoExterior(),
                      e.getNoInterior(),
                      e.getColonia(),
                      e.getMunicipio(),
                      e.getEstado(),
                      e.getPais(),
                      e.getCp()));
              row.put("LINEA", e.getTipo());
              row.put("ACTIVA", e.isActivo() ? "SI" : "NO");
              row.put("OPERATIVA", e.isOperativa() ? "SI" : "NO");
              row.put("BLOQUEADA", e.isBloqueada() ? "SI" : "NO");
              row.put("GIRO", catalogService.getGiroEmpresa(e.getGiro()).orElse("SIN GIRO"));
              row.put("REGIMEN_FISCAL", e.getRegimenFiscal());
              row.put("PAGINA_WEB", e.getWeb());
              row.put("CORREO_ELECTRONICO", e.getCorreo());
              row.put("ESTATUS_JURIDICO", e.getEstatusJuridico());
              row.put("ESTATUS_JURIDICO_FASE_2", e.getEstatusJuridico2());
              row.put("REPRESENTANTE_LEGAL", e.getRepresentanteLegal());
              row.put(
                  "BANCOS",
                  e.getCuentas().stream()
                      .map(CuentaBancaria::getBanco)
                      .collect(Collectors.toList())
                      .toString());
              row.put(
                  "NO_CUENTA",
                  e.getCuentas().stream()
                      .map(CuentaBancaria::getCuenta)
                      .collect(Collectors.toList())
                      .toString());
              row.put(
                  "DOMICILIO_BANCOS",
                  e.getCuentas().stream()
                      .map(CuentaBancaria::getDomicilioBanco)
                      .collect(Collectors.toList())
                      .toString());
              row.put(
                  "SUCURSAL",
                  e.getCuentas().stream()
                      .map(CuentaBancaria::getSucursal)
                      .collect(Collectors.toList())
                      .toString());
              row.put(
                  "EXPEDIENTE_ACTUALIZADO",
                  e.getCuentas().stream()
                      .map(CuentaBancaria::getExpedienteActualizado)
                      .collect(Collectors.toList())
                      .toString());
              row.put(
                  "EXPIRACION_CERTIFICADOS", String.format("%tF", e.getExpiracionCertificado()));
              row.put("ACTIVIDAD_SAT", e.getActividadSAT());
              row.put("REGISTRO_PATRONAL", e.getRegistroPatronal());
              row.put("ENTIDAD_REGISTRO_PATRONAL", e.getEntidadRegistroPatronal());
              row.put("IMPUESTO_ESTATAL", e.getImpuestoEstatal());
              row.put("ENTIDAD_IMPUESTO_PATRONAL", e.getEntidadImpuestoPatronal());

              List<EmpresaDetalles> accionistas =
                  e.getDetalles().stream()
                      .filter(d -> "ACCIONISTA".equals(d.getTipo()))
                      .collect(Collectors.toList());
              List<EmpresaDetalles> apoderados =
                  e.getDetalles().stream()
                      .filter(d -> "APODERADO".equals(d.getTipo()))
                      .collect(Collectors.toList());
              List<EmpresaDetalles> pendientes =
                  e.getDetalles().stream()
                      .filter(d -> "PENDIENTE".equals(d.getTipo()))
                      .collect(Collectors.toList());
              List<EmpresaDetalles> observaciones =
                  e.getDetalles().stream()
                      .filter(d -> "OBSERVACION".equals(d.getTipo()))
                      .collect(Collectors.toList());

              row.put(
                  "ACCIONISTAS",
                  accionistas.stream()
                      .map(EmpresaDetalles::getDetalle)
                      .collect(Collectors.toList())
                      .toString());
              row.put(
                  "APODERADOS",
                  apoderados.stream()
                      .map(EmpresaDetalles::getDetalle)
                      .collect(Collectors.toList())
                      .toString());
              row.put(
                  "PENDIENTES",
                  pendientes.stream()
                      .map(EmpresaDetalles::getResumen)
                      .collect(Collectors.toList())
                      .toString());
              row.put(
                  "OBSERVACIONES",
                  observaciones.stream()
                      .map(EmpresaDetalles::getResumen)
                      .collect(Collectors.toList())
                      .toString());

              row.put("CREADOR", e.getCreador());
              row.put(
                  "CREACION", String.format("%tF %tR", e.getFechaCreacion(), e.getFechaCreacion()));
              row.put(
                  "ACTUALIZACION",
                  String.format("%tF %tR", e.getFechaActualizacion(), e.getFechaActualizacion()));

              return row;
            })
        .collect(Collectors.toList());
  }

  public ResourceFileDto getCompaniesReport(Map<String, String> parameters) throws IOException {

    Page<Map<String, String>> result = getEmpresasByParametros(parameters);

    List<String> headers =
        Arrays.asList(
            "NOMBRE_CORTO",
            "EMPRESA",
            "RFC",
            "DOMICILIO",
            "LINEA",
            "ACTIVA",
            "OPERATIVA",
            "BLOQUEADA",
            "GIRO",
            "REGIMEN_FISCAL",
            "PAGINA_WEB",
            "CORREO_ELECTRONICO",
            "ESTATUS_JURIDICO",
            "ESTATUS_JURIDICO_FASE_2",
            "REPRESENTANTE_LEGAL",
            "BANCOS",
            "NO_CUENTA",
            "DOMICILIO_BANCOS",
            "SUCURSAL",
            "EXPEDIENTE_ACTUALIZADO",
            "EXPIRACION_CERTIFICADOS",
            "ACTIVIDAD_SAT",
            "REGISTRO_PATRONAL",
            "ENTIDAD_REGISTRO_PATRONAL",
            "IMPUESTO_ESTATAL",
            "ACCIONISTAS",
            "APODERADOS",
            "PENDIENTES",
            "OBSERVACIONES",
            "CREADOR",
            "CREACION",
            "ACTUALIZACION");

    List<Map<String, Object>> data =
        result.getContent().stream()
            .map(
                empresa -> {
                  Map<String, Object> row = new HashMap<>();
                  for (String header : headers) {
                    row.put(header, empresa.get(header));
                  }
                  return row;
                })
            .collect(Collectors.toList());

    return downloaderService.generateBase64Report("Reporte Empresas", data, headers);
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
    Empresa empresa = mapper.getEntityFromEmpresaDto(empresaDto);
    return mapper.getEmpresaDtoFromEntity(repository.save(empresa));
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

    if (empresa.isActivo() && !empresaDto.isActivo()) {
      notificationHandlerService.sendNotification(
          "DESACTIVACION_EMPRESA",
          String.format("Se desactivo la empresa %s", empresaDto.getRazonSocial()));
    } else if (!empresa.isActivo() && empresaDto.isActivo()) {
      notificationHandlerService.sendNotification(
          "ACTIVACION_EMPRESA",
          String.format("Se activo la empresa %s", empresaDto.getRazonSocial()));
    }
    Empresa companyToSave = mapper.getEntityFromEmpresaDto(empresaDto);
    companyToSave.setId(empresa.getId());

    return mapper.getEmpresaDtoFromEntity(repository.save(companyToSave));
  }
}
