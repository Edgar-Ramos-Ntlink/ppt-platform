package com.business.unknow.services.services;

import com.business.unknow.model.dto.catalogs.CatalogDto;
import com.business.unknow.model.dto.catalogs.ClaveProductoServicioDto;
import com.business.unknow.model.dto.catalogs.ClaveUnidadDto;
import com.business.unknow.model.dto.catalogs.CodigoPostalUiDto;
import com.business.unknow.model.dto.catalogs.FormaPagoDto;
import com.business.unknow.model.dto.catalogs.RegimenFiscalDto;
import com.business.unknow.model.dto.catalogs.UsoCfdiDto;
import com.business.unknow.services.entities.catalogs.Banco;
import com.business.unknow.services.entities.catalogs.ClaveUnidad;
import com.business.unknow.services.entities.catalogs.CodigoPostal;
import com.business.unknow.services.entities.catalogs.FormaPago;
import com.business.unknow.services.entities.catalogs.Giro;
import com.business.unknow.services.entities.catalogs.UsoCfdi;
import com.business.unknow.services.mapper.CatalogsMapper;
import com.business.unknow.services.repositories.catalogs.BancoRepository;
import com.business.unknow.services.repositories.catalogs.ClaveProductoServicioRepository;
import com.business.unknow.services.repositories.catalogs.ClaveUnidadRepository;
import com.business.unknow.services.repositories.catalogs.CodigoPostalRepository;
import com.business.unknow.services.repositories.catalogs.FormaPagoRepository;
import com.business.unknow.services.repositories.catalogs.GiroRepository;
import com.business.unknow.services.repositories.catalogs.RegimenFiscalRepository;
import com.business.unknow.services.repositories.catalogs.StatusEventoRepository;
import com.business.unknow.services.repositories.catalogs.StatusPaymentRepository;
import com.business.unknow.services.repositories.catalogs.UsoCfdiRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@EnableScheduling
public class CatalogService {

  private Map<String, UsoCfdiDto> cfdiUseMappings;

  private Map<String, ClaveUnidadDto> driveKeyMappings;

  private Map<String, FormaPagoDto> paymentFormMappings;

  private Map<String, RegimenFiscalDto> taxRegimeMappings;

  private Map<String, CatalogDto> turnMappings;

  private Map<String, CatalogDto> bankMappings;

  private Map<String, CatalogDto> statusEventMappings;

  private Map<String, CatalogDto> statusPaymentsMappings;

  private Map<Integer, Giro> giroEmpresasMappings;

  @Autowired private UsoCfdiRepository usoCfdiRepository;

  @Autowired private FormaPagoRepository formaPagoRepository;

  @Autowired private RegimenFiscalRepository regimanFiscalRepository;

  @Autowired private ClaveUnidadRepository claveUnidadReppository;

  @Autowired private BancoRepository bancoRepository;

  @Autowired private CodigoPostalRepository codigoPostalRepository;

  @Autowired private GiroRepository giroRepository;

  @Autowired private ClaveProductoServicioRepository claveProductoServicioRepository;

  @Autowired private StatusEventoRepository statusEventoRepository;

  @Autowired private StatusPaymentRepository statusPaymentRepository;

  @Autowired private CatalogsMapper catalogsMapper;

  /** load caches every day at 00:10 A.M */
  @Scheduled(cron = "0 10 0 * * ?")
  @PostConstruct
  public void loadingCache() {
    log.info("Loading mappings");
    cfdiUseMappings =
        usoCfdiRepository.findAll().stream()
            .collect(Collectors.toMap(UsoCfdi::getClave, e -> catalogsMapper.getDtoFromEntity(e)));
    log.info("Mappings cfdiUseMappings loaded {}", cfdiUseMappings.size());
    paymentFormMappings =
        formaPagoRepository.findAll().stream()
            .collect(Collectors.toMap(FormaPago::getId, e -> catalogsMapper.getDtoFromEntity(e)));
    log.info("Mappings paymentFormMappings loaded {}", paymentFormMappings.size());
    taxRegimeMappings =
        regimanFiscalRepository.findAll().stream()
            .collect(
                Collectors.toMap(
                    a -> a.getClave().toString(), e -> catalogsMapper.getDtoFromEntity(e)));
    log.info("Mappings taxRegimeMappings loaded {}", taxRegimeMappings.size());
    driveKeyMappings =
        claveUnidadReppository.findAll().stream()
            .collect(
                Collectors.toMap(ClaveUnidad::getClave, e -> catalogsMapper.getDtoFromEntity(e)));
    log.info("Mappings driveKeyMappings loaded {}", driveKeyMappings.size());
    turnMappings =
        giroRepository.findAll().stream()
            .collect(
                Collectors.toMap(
                    a -> a.getId().toString(), e -> catalogsMapper.getDtoFromEntity(e)));
    log.info("Mappings giroMappings loaded {}", turnMappings.size());
    bankMappings =
        bancoRepository.findAll().stream()
            .collect(Collectors.toMap(Banco::getId, e -> catalogsMapper.getDtoFromEntity(e)));
    log.info("Mappings bankMappings loaded {}", bankMappings.size());
    statusEventMappings =
        statusEventoRepository.findAll().stream()
            .collect(
                Collectors.toMap(
                    a -> a.getId().toString(), e -> catalogsMapper.getDtoFromEntity(e)));
    log.info("Mappings statusEventMappings loaded {}", statusEventMappings.size());
    statusPaymentsMappings =
        statusPaymentRepository.findAll().stream()
            .collect(
                Collectors.toMap(
                    a -> a.getId().toString(), e -> catalogsMapper.getDtoFromEntity(e)));
    log.info("Mappings statusPaymentsMappings loaded {}", statusPaymentsMappings.size());
    giroEmpresasMappings =
        giroRepository.findAll().stream().collect(Collectors.toMap(a -> a.getId(), e -> e));
    log.info("Mappings giroEmpresasMappings loaded {}", giroEmpresasMappings.size());
  }

  /**
   * Gets Postal code by code
   *
   * @param code
   * @return {@link CodigoPostalUiDto}
   */
  public CodigoPostalUiDto getPostalCodeByCode(String code) {
    List<CodigoPostal> postalCodes = codigoPostalRepository.findByCodigoPostal(code);
    CodigoPostal codigoPostal =
        postalCodes.stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontraron resultados del codigo postal"));
    return CodigoPostalUiDto.builder()
        .codigo_postal(code)
        .municipio(codigoPostal.getMunicipio())
        .estado(codigoPostal.getEstado())
        .colonias(postalCodes.stream().map(a -> a.getColonia()).collect(Collectors.toList()))
        .build();
  }

  /**
   * Gets Product by description or code
   *
   * @param description
   * @param clave
   * @return {@link List<ClaveProductoServicioDto>}
   */
  public List<ClaveProductoServicioDto> getServiceProduct(
      Optional<String> description, Optional<String> clave) {
    // TODO: REFACTOR  ClaveProductoServicioDto TO CatalogDto
    List<ClaveProductoServicioDto> mappings = new ArrayList<>();
    if (description.isPresent()) {
      mappings =
          catalogsMapper.getDtosFromEntities(
              claveProductoServicioRepository.findByDescripcionContainingIgnoreCase(
                  description.get()));
    }
    if (clave.isPresent()) {
      Integer codigo = Integer.valueOf(clave.get().trim());
      mappings =
          catalogsMapper.getDtosFromEntities(claveProductoServicioRepository.findByClave(codigo));
    }
    if (mappings.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron resultados");
    } else {
      return mappings;
    }
  }

  /**
   * Gets drive key by key
   *
   * @param key
   * @return {@link ClaveUnidadDto}
   */
  public ClaveUnidadDto getDriveKeyByKey(String key) {
    // TODO: REFACTOR  ClaveUnidadDto TO CatalogDto
    if (driveKeyMappings.containsKey(key)) {
      return driveKeyMappings.get(key);
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Clave Unidad %s no existe", key));
    }
  }

  /**
   * Gets all drive keys
   *
   * @return {@link ClaveUnidadDto}
   */
  public List<ClaveUnidadDto> getDriveKeys() {
    return driveKeyMappings.values().stream().collect(Collectors.toList());
  }

  /**
   * Gets Cfdi use by key
   *
   * @param key
   * @return {@link UsoCfdiDto}
   */
  public UsoCfdiDto getCfdiUseByKey(String key) {
    // TODO: REFACTOR UsoCfdiDto  ClaveUnidadDto TO CatalogDto
    if (cfdiUseMappings.containsKey(key)) {
      return cfdiUseMappings.get(key);
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Uso Cfdi %s no existe", key));
    }
  }

  /**
   * Gets all Cfdi uses in saved in cache
   *
   * @return {@link List<UsoCfdiDto>}
   */
  public List<UsoCfdiDto> getCfdiUse() {
    return cfdiUseMappings.values().stream().collect(Collectors.toList());
  }

  /**
   * Get all payment form by key
   *
   * @param key
   * @return {@link FormaPagoDto}
   */
  public FormaPagoDto getPaymentFormByKey(String key) {
    if (paymentFormMappings.containsKey(key)) {
      return paymentFormMappings.get(key);
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Forma de pago  %s no existe", key));
    }
  }

  public Optional<String> getPaymentFormByValue(String value) {
    for (String key : paymentFormMappings.keySet()) {
      FormaPagoDto formaPagoDto = paymentFormMappings.get(key);
      if (formaPagoDto.getShortDescripcion().equalsIgnoreCase(value)) {
        return Optional.of(formaPagoDto.getId());
      }
    }
    if ("DEPOSITO".equals(value)) {
      // TODO REVIEW COMPLEMENTS LOGIC FOR BANK DEPOSITS
      return Optional.of("03");
    }
    return Optional.empty();
  }
  /**
   * Gets Tax Regime by key
   *
   * @param key
   * @return {@link RegimenFiscalDto}
   */
  public RegimenFiscalDto getTaxRegimeByKey(String key) {
    if (taxRegimeMappings.containsKey(key)) {
      return taxRegimeMappings.get(key);
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Regimen Fiscal %s no existe", key));
    }
  }

  /**
   * Gets all Regime taxes saved in cache
   *
   * @return {@link List<RegimenFiscalDto>}
   */
  public List<RegimenFiscalDto> getTaxRegimes() {
    return taxRegimeMappings.values().stream().collect(Collectors.toList());
  }

  /**
   * Gets all turns saved in cache
   *
   * @return {@link List<CatalogDto>}
   */
  public List<CatalogDto> getTurns() {
    return turnMappings.values().stream().collect(Collectors.toList());
  }

  public CatalogDto getTurnsById(String key) {
    if (turnMappings.containsKey(key)) {
      return turnMappings.get(key);
    } else {
      return CatalogDto.builder().nombre("SIN GIRO").build();
    }
  }

  /**
   * Gets all Banks saved in cache
   *
   * @return {@link List<CatalogDto>}
   */
  public List<CatalogDto> getBanks() {
    return bankMappings.values().stream().collect(Collectors.toList());
  }

  /**
   * Gets all Status events saved in cache
   *
   * @return {@link List<CatalogDto>}
   */
  public List<CatalogDto> getStatusEvents() {
    return statusEventMappings.values().stream().collect(Collectors.toList());
  }

  /**
   * Gets all Status payments saved in cache
   *
   * @return {@link List<CatalogDto>}
   */
  public List<CatalogDto> getStatusPayments() {
    return statusPaymentsMappings.values().stream().collect(Collectors.toList());
  }

  public Optional<String> getGiroEmpresa(Integer giroId) {
    return giroEmpresasMappings.containsKey(giroId)
        ? Optional.of(giroEmpresasMappings.get(giroId).getNombre())
        : Optional.empty();
  }
}
