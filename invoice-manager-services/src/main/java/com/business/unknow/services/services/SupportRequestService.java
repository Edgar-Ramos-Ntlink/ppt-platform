package com.business.unknow.services.services;

import com.business.unknow.MailConstants;
import com.business.unknow.model.config.MailContent;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.services.SupportRequestDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.SupportRequest;
import com.business.unknow.services.mapper.SupportRequestMapper;
import com.business.unknow.services.repositories.SupportRequestRepository;
import com.business.unknow.services.util.validators.SupportRequestValidator;
import java.io.IOException;
import java.time.LocalDate;
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
public class SupportRequestService {

  @Autowired private SupportRequestRepository repository;

  @Autowired private SupportRequestMapper mapper;

  @Autowired private DownloaderService downloaderService;

  @Autowired private MailService mailService;

  private Specification<SupportRequest> buildSearchFilters(Map<String, String> parameters) {

    log.info("Finding support requests by {}", parameters);

    return new Specification<>() {
      @Override
      public Predicate toPredicate(
          Root<SupportRequest> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (parameters.get("folio") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(
                      root.get("folio"), Integer.parseInt(parameters.get("folio")))));
        }
        if (parameters.get("contactEmail") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("contactEmail"),
                      String.format("%%%s%%", parameters.get("contactEmail")).toUpperCase())));
        }
        if (parameters.get("contactName") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("contactName"),
                      String.format("%%%s%%", parameters.get("contactName")).toUpperCase())));
        }

        if (parameters.get("module") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("module"), parameters.get("module"))));
        }
        if (parameters.get("status") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("status"), parameters.get("status"))));
        }
        if (parameters.get("agent") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.like(
                      root.get("agent"),
                      String.format("%%%s%%", parameters.get("agent")).toUpperCase())));
        }
        if (parameters.get("supportType") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("supportType"), parameters.get("supportType"))));
        }
        if (parameters.get("supportLevel") != null) {
          predicates.add(
              criteriaBuilder.and(
                  criteriaBuilder.equal(root.get("supportLevel"), parameters.get("supportLevel"))));
        }

        if (parameters.get("since") != null && parameters.get("to") != null) {
          java.sql.Date start = java.sql.Date.valueOf(LocalDate.parse(parameters.get("since")));
          java.sql.Date end =
              java.sql.Date.valueOf(LocalDate.parse(parameters.get("to")).plusDays(1));
          predicates.add(
              criteriaBuilder.and(criteriaBuilder.between(root.get("creation"), start, end)));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }

  public Page<SupportRequestDto> getSupportRequestByParams(Map<String, String> parameters) {

    int page =
        (parameters.get("page") == null) || parameters.get("page").equals("")
            ? 0
            : Integer.parseInt(parameters.get("page"));
    int size = (parameters.get("size") == null) ? 10 : Integer.parseInt(parameters.get("size"));
    Page<SupportRequest> result =
        repository.findAll(
            buildSearchFilters(parameters),
            PageRequest.of(page, size, Sort.by("update").descending()));
    return new PageImpl<>(
        mapper.getDtosFromEntities(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public ResourceFileDto getSupportReportByParams(Map<String, String> parameters)
      throws IOException {

    int page =
        (parameters.get("page") == null) || parameters.get("page").equals("")
            ? 0
            : Integer.parseInt(parameters.get("page"));
    int size = (parameters.get("size") == null) ? 10 : Integer.parseInt(parameters.get("size"));

    Page<SupportRequest> result =
        repository.findAll(
            buildSearchFilters(parameters),
            PageRequest.of(page, size, Sort.by("update").descending()));

    List<String> headers =
        Arrays.asList(
            "FOLIO",
            "EMAIL CONTACTO",
            "NOMBRE CONTACTO",
            "TELEFONO CONTACTO",
            "MODULO",
            "ESTATUS",
            "AGENTE",
            "TIPO SOPORTE",
            "NIVEL SOPORTE",
            "PROBLEMA",
            "MENSAJE ERROR",
            "SOLUCION",
            "NOTAS",
            "FECHA LIMITE",
            "CREACION",
            "ACTUALIZACION");
    List<Map<String, Object>> data =
        result.getContent().stream()
            .map(
                s -> {
                  Map<String, Object> row = new HashMap<>();
                  row.put("FOLIO", s.getFolio());
                  row.put("EMAIL CONTACTO", s.getContactEmail());
                  row.put("NOMBRE CONTACTO", s.getContactName());
                  row.put("TELEFONO CONTACTO", s.getContactPhone());
                  row.put("MODULO", s.getModule());
                  row.put("ESTATUS", s.getStatus());
                  row.put("AGENTE", s.getAgent());
                  row.put("TIPO SOPORTE", s.getSupportType());
                  row.put("NIVEL SOPORTE", s.getSupportLevel());
                  row.put("PROBLEMA", s.getProblem());
                  row.put("MENSAJE ERROR", s.getErrorMessage());
                  row.put("SOLUCION", s.getSolution());
                  row.put("NOTAS", s.getNotes());
                  row.put("FECHA LIMITE", s.getDueDate());
                  row.put("CREACION", s.getCreation());
                  row.put("ACTUALIZACION", s.getUpdate());
                  return row;
                })
            .collect(Collectors.toList());
    return downloaderService.generateBase64Report("REPORTE DE SOPORTE", data, headers);
  }

  public SupportRequestDto createSupportRequest(SupportRequestDto dto)
      throws InvoiceManagerException {
    SupportRequestValidator.validateSupportRequest(dto);
    SupportRequestValidator.assignDefaults(dto);
    SupportRequest clientSoporte = repository.save(mapper.getEntityFromDto(dto));
    SupportRequestDto result = mapper.getDtoFromEntity(clientSoporte);
    sendMailNotification(result);
    return result;
  }

  public SupportRequestDto updateSuppoprtRequest(SupportRequestDto requestDto, Integer folio) {
    SupportRequest requestDb =
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("No existe el folio de soporte : %d", folio)));

    SupportRequest request = mapper.getEntityFromDto(requestDto);
    request.setFolio(folio);
    sendMailNotification(requestDto);
    return mapper.getDtoFromEntity(repository.save(request));
  }

  public SupportRequestDto getSupportRequest(Integer folio) {
    SupportRequest request =
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("No existe el folio de soporte : %d", folio)));
    return mapper.getDtoFromEntity(request);
  }

  public void deleteSupportRequest(Integer folio) {
    SupportRequest request =
        repository
            .findByFolio(folio)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("No existe el folio de soporte : %d", folio)));
    repository.delete(request);
  }

  private void sendMailNotification(SupportRequestDto request) {
    List<String> recipients = Arrays.asList(request.getContactEmail(), request.getAgent());
    MailContent content =
        MailContent.builder()
            .subject(String.format(MailConstants.SUPPORT_REQUEST_SUBJECT, request.getFolio()))
            .bodyText(
                String.format(
                    MailConstants.SUPPORT_REQUEST_BODY_MESSAGE,
                    request.getFolio(),
                    request.getContactName(),
                    request.getContactEmail(),
                    request.getStatus(),
                    request.getSupportType(),
                    request.getProblem(),
                    request.getSolution(),
                    request.getNotes(),
                    request.getAgent()))
            .build();
    mailService.sendEmail(recipients, content);
  }
}
