package com.business.unknow.services.services;

import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.CuentaBancaria;
import com.business.unknow.services.entities.Empresa;
import com.business.unknow.services.entities.EmpresaDetalles;
import com.business.unknow.services.mapper.EmpresaMapper;
import com.business.unknow.services.repositories.EmpresaRepository;
import com.business.unknow.services.services.executor.EmpresaExecutorService;
import com.business.unknow.services.util.validators.EmpresaValidator;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
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

  public Page<Map<String, String>> getEmpresasByParametros(
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
    return new PageImpl<Map<String, String>>(
        getFlatCompanyDetails(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public List<Map<String, String>> getFlatCompanyDetails(List<Empresa> empresas) {
    List<Map<String, String>> result = new ArrayList<>();
    List<String> detailHeaders =
        empresas.stream()
            .flatMap(e -> e.getDetalles().stream())
            .map(d -> d.getTipo())
            .distinct()
            .collect(Collectors.toList());
    /*
    List<String> headers =
        Arrays.asList(
            "NOMBRE CORTO",
            "EMPRESA",
            "RFC",
            "DOMICILIO",
            "LINEA",
            "ACTIVA",
            "GIRO",
            "REGIMEN FISCAL",
            "PAGINA WEB",
            "CORREO ELECTRONICO",
            "ESTATUS JURIDICO",
            "ESTATUS JURIDICO FASE 2",
            "REPRESENTANTE LEGAL",
            "BANCO",
            "NO CUENTA",
            "DOMICILIO BANCOS",
            "SUCURSAL",
            "EXPEDIENTE ACTUALIZADO",
            "EXPIRACION CERTIFICADOS",
            "ACTIVIDAD SAT",
            "REGISTRO PATRONAL",
            "ENTIDAD REGISTRO PATRONAL",
            "IMPUESTO ESTATAL",
            "ENTIDAD IMPUESTO PATRONAL");

    headers.add("CREADOR");
    headers.add("CREACION");
    headers.add("ACTUALIZACION");*/

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
                      "%s EXT: %s INT : %s, %s, %s, %s, %s C.P. %s",
                      e.getCalle(),
                      e.getNoExterior(),
                      e.getNoInterior(),
                      e.getColonia(),
                      e.getMunicipio(),
                      e.getEstado(),
                      e.getPais(),
                      e.getCp()));
              row.put("LINEA", e.getTipo());
              row.put("ACTIVA", e.getActivo() ? "SI" : "NO");
              row.put("GIRO", e.getGiro().toString());
              row.put("REGIMEN_FISCAL", e.getRegimenFiscal());
              row.put("PAGINA_WEB", e.getWeb());
              row.put("CORREO_ELECTRONICO", e.getCorreo());
              row.put("ESTATUS_JURIDICO", e.getEstatusJuridico());
              row.put("ESTATUS_JURIDICO_FASE_2", e.getEstatusJuridico2());
              row.put("REPRESENTANTE_LEGAL", e.getRepresentanteLegal());
              row.put("BANCO", e.getRfc());
              row.put(
                  "NO_CUENTA",
                  e.getCuentas().stream()
                      .map(CuentaBancaria::getBanco)
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
                  "EXPIRACION_CERTIFICADOS",
                  String.format(
                      "%tF %tR", e.getExpiracionCertificado(), e.getExpiracionCertificado()));
              row.put("ACTIVIDAD_SAT", e.getActividadSAT());
              row.put("REGISTRO_PATRONAL", e.getRegistroPatronal());
              row.put("ENTIDAD_REGISTRO_PATRONAL", e.getEntidadRegistroPatronal());
              row.put("IMPUESTO_ESTATAL", e.getImpuestoEstatal());
              row.put("ENTIDAD_IMPUESTO_PATRONAL", e.getEntidadImpuestoPatronal());
              row.put("CREADOR", e.getCreador());
              row.put(
                  "CREACION", String.format("%tF %tR", e.getFechaCreacion(), e.getFechaCreacion()));
              row.put(
                  "ACTUALIZACION",
                  String.format("%tF %tR", e.getFechaActualizacion(), e.getFechaActualizacion()));

              for (String header : detailHeaders) {
                row.put(
                    header,
                    e.getDetalles().stream()
                        .filter(d -> header.equals(d.getTipo()))
                        .map(EmpresaDetalles::getResumen)
                        .findAny()
                        .orElse(""));
              }

              return row;
            })
        .collect(Collectors.toList());
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

    if (empresa.getActivo() && !empresaDto.getActivo()) {
      notificationHandlerService.sendNotification(
          "DESACTIVACION_EMPRESA",
          String.format("Se desactivo la empresa %s", empresaDto.getRazonSocial()));
    } else if (!empresa.getActivo() && empresaDto.getActivo()) {
      notificationHandlerService.sendNotification(
          "ACTIVACION_EMPRESA",
          String.format("Se activo la empresa %s", empresaDto.getRazonSocial()));
    }
    Empresa companyToSave = mapper.getEntityFromEmpresaDto(empresaDto);
    companyToSave.setId(empresa.getId());

    return mapper.getEmpresaDtoFromEntity(repository.save(companyToSave));
  }
}
