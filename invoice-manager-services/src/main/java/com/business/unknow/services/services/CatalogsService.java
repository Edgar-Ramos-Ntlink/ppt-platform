/** */
package com.business.unknow.services.services;

import com.business.unknow.model.dto.catalogs.CatalogDto;
import com.business.unknow.model.dto.catalogs.ClaveProductoServicioDto;
import com.business.unknow.model.dto.catalogs.ClaveUnidadDto;
import com.business.unknow.model.dto.catalogs.CodigoPostalUiDto;
import com.business.unknow.model.dto.catalogs.FormaPagoDto;
import com.business.unknow.model.dto.catalogs.RegimenFiscalDto;
import com.business.unknow.model.dto.catalogs.UsoCfdiDto;
import com.business.unknow.services.entities.catalogs.ClaveProductoServicio;
import com.business.unknow.services.entities.catalogs.ClaveUnidad;
import com.business.unknow.services.entities.catalogs.CodigoPostal;
import com.business.unknow.services.entities.catalogs.FormaPago;
import com.business.unknow.services.entities.catalogs.UsoCfdi;
import com.business.unknow.services.mapper.CatalogsMapper;
import com.business.unknow.services.repositories.catalogs.BancoRepository;
import com.business.unknow.services.repositories.catalogs.ClaveProductoServicioRepository;
import com.business.unknow.services.repositories.catalogs.ClaveUnidadRepository;
import com.business.unknow.services.repositories.catalogs.CodigoPostalRepository;
import com.business.unknow.services.repositories.catalogs.FormaPagoRepository;
import com.business.unknow.services.repositories.catalogs.GiroRepository;
import com.business.unknow.services.repositories.catalogs.RegimenFiscalRepository;
import com.business.unknow.services.repositories.catalogs.StatusDevolucionRepository;
import com.business.unknow.services.repositories.catalogs.StatusEventoRepository;
import com.business.unknow.services.repositories.catalogs.StatusPagoRepository;
import com.business.unknow.services.repositories.catalogs.UsoCfdiRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** @author ralfdemoledor */
@Slf4j
@Service
@EnableScheduling
public class CatalogsService {

  @Autowired private ClaveProductoServicioRepository productorServicioRepo;

  @Autowired private ClaveUnidadRepository unidadRepo;

  @Autowired private RegimenFiscalRepository regimenFiscalRepo;

  @Autowired private UsoCfdiRepository usoCfdiRepo;

  @Autowired private StatusEventoRepository statusEventoRepo;

  @Autowired private StatusPagoRepository statusPagoRepo;

  @Autowired private StatusDevolucionRepository statusDevoluicionRepo;

  @Autowired private GiroRepository giroRepo;

  @Autowired private CodigoPostalRepository codigoPostalRepository;

  @Autowired private BancoRepository bancoRepository;

  @Autowired private CatalogsMapper mapper;

  @Autowired private FormaPagoRepository formaPagoRepository;

  private Map<String, UsoCfdiDto> cfdiUseMappings;

  private Map<String, RegimenFiscalDto> taxRegimeMappings;

  private Map<String, FormaPagoDto> paymentFormMappings;

  public CodigoPostalUiDto getCodigosPostaleByCode(String codigo) {
    List<CodigoPostal> codigos = codigoPostalRepository.findByCodigoPostal(codigo);
    CodigoPostal codigPostal =
        codigos.stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontraron resultados del codigo postal"));
    CodigoPostalUiDto dto =
        CodigoPostalUiDto.builder()
            .codigo_postal(codigo)
            .municipio(codigPostal.getMunicipio())
            .estado(codigPostal.getEstado())
            .build();
    for (CodigoPostal cod : codigos) {
      dto.getColonias().add(cod.getColonia());
    }
    return dto;
  }

  public List<ClaveProductoServicioDto> getProductoServicio(
      Optional<String> description, Optional<String> clave) {
    List<ClaveProductoServicioDto> mappings = new ArrayList<>();
    if (description.isPresent()) {
      mappings =
          mapper.getClaveProdServDtosFromEntities(
              productorServicioRepo.findByDescripcionContainingIgnoreCase(description.get()));
    }
    if (clave.isPresent()) {
      Integer codigo = Integer.valueOf(clave.get().trim());
      mappings = mapper.getClaveProdServDtosFromEntities(productorServicioRepo.findByClave(codigo));
    }
    if (mappings.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron resultados");
    } else {
      return mappings;
    }
  }

  public Page<ClaveProductoServicioDto> getAllProductoServicioClaves(int page, int size) {
    Page<ClaveProductoServicio> result = productorServicioRepo.findAll(PageRequest.of(page, size));
    return new PageImpl<>(
        mapper.getClaveProdServDtosFromEntities(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public Page<ClaveUnidadDto> getAllClaveUnidad(int page, int size) {
    Page<ClaveUnidad> result = unidadRepo.findAll(PageRequest.of(page, size));
    return new PageImpl<>(
        mapper.getClaveUnidadDtosFromEntities(result.getContent()),
        result.getPageable(),
        result.getTotalElements());
  }

  public List<ClaveUnidadDto> getClaveUnidadByNombre(String nombre) {
    List<ClaveUnidadDto> claveUnidadCat =
        mapper.getClaveUnidadDtosFromEntities(unidadRepo.findByNombreContainingIgnoreCase(nombre));
    claveUnidadCat.sort((a, b) -> a.getNombre().compareTo(b.getNombre()));
    return claveUnidadCat;
  }

  public List<ClaveUnidadDto> getAllClaveUnidad() {
    List<ClaveUnidadDto> claveUnidadCat =
        mapper.getClaveUnidadDtosFromEntities(unidadRepo.findAll());
    claveUnidadCat.sort((a, b) -> a.getNombre().compareTo(b.getNombre()));
    return claveUnidadCat;
  }

  public List<RegimenFiscalDto> getAllRegimenFiscal() {
    return mapper.getRegimenFiscalDtosFromEntities(regimenFiscalRepo.findAll());
  }

  public List<UsoCfdiDto> getAllUsoCfdi() {
    return mapper.getUsoCfdiDtosFromEntities(usoCfdiRepo.findAll());
  }

  //	public List<StatusFacturaDto> getAllStatusFactura() {
  //		return mapper.getStatusFacturaDtosFromEntities(statusFacturaRepo.findAll());
  //	}

  public List<CatalogDto> getAllGiros() {
    List<CatalogDto> giros = mapper.getGirosDtoFromEntities(giroRepo.findAll());
    giros.sort((a, b) -> a.getNombre().compareTo(b.getNombre()));
    return giros;
  }

  public List<CatalogDto> getAllStatusEvento() {
    return mapper.getStatusEventoDtosFromEntities(statusEventoRepo.findAll());
  }

  public List<CatalogDto> getAllStatusPago() {
    return mapper.getStatusPagoDtosFromEntities(statusPagoRepo.findAll());
  }

  public List<CatalogDto> getAllStatusDevoluicion() {
    return mapper.getStatusDevolucionDtosFromEntities(statusDevoluicionRepo.findAll());
  }

  //	public List<CatalogDto> getAllStatusRevision() {
  //		return mapper.getStatusRevisionDtosFromEntities(statusRevisionRepo.findAll());
  //	}

  public List<CatalogDto> getAllBancos() {
    List<CatalogDto> bancos = mapper.getBancoDtoFromEntities(bancoRepository.findAll());
    bancos.sort((a, b) -> a.getNombre().compareTo(b.getNombre()));
    return bancos;
  }

  public CatalogDto getAllBancoByName(String name) {
    return mapper.getBancoDtoFromEntity(
        bancoRepository
            .findByNombre(name)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontraron resultados")));
  }

  /** load caches every day at 00:10 A.M */
  @Scheduled(cron = "0 10 0 * * ?")
  @PostConstruct
  public void loadingCache() {
    log.info("Loading mappings");
    cfdiUseMappings =
        usoCfdiRepo.findAll().stream()
            .collect(Collectors.toMap(UsoCfdi::getClave, e -> mapper.getUsoCfdiDtoFromEntity(e)));
    log.info("Mappings cfdiUseMappings loaded {}", cfdiUseMappings.size());
    taxRegimeMappings =
        regimenFiscalRepo.findAll().stream()
            .collect(
                Collectors.toMap(
                    a -> a.getClave().toString(), e -> mapper.getRegimenFiscalDtoFromEntity(e)));
    log.info("Mappings taxRegimeMappings loaded {}", taxRegimeMappings.size());

    paymentFormMappings =
        formaPagoRepository.findAll().stream()
            .collect(Collectors.toMap(FormaPago::getId, e -> mapper.getFormaPagoDtoFromEntity(e)));
    log.info("Mappings paymentFormMappings loaded {}", paymentFormMappings.size());
  }

  public UsoCfdiDto getCfdiUseByKey(String key) {
    if (cfdiUseMappings.containsKey(key)) {
      return cfdiUseMappings.get(key);
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Uso Cfdi %s no existe", key));
    }
  }

  public RegimenFiscalDto getTaxRegimeByKey(String key) {
    if (taxRegimeMappings.containsKey(key)) {
      return taxRegimeMappings.get(key);
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Regimen Fiscal %s no existe", key));
    }
  }

  public FormaPagoDto getPaymentFormByKey(String key) {
    if (paymentFormMappings.containsKey(key)) {
      return paymentFormMappings.get(key);
    } else {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Forma de pago  %s no existe", key));
    }
  }
}
