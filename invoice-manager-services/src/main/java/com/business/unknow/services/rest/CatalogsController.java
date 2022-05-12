package com.business.unknow.services.rest;

import com.business.unknow.model.dto.catalogs.CatalogDto;
import com.business.unknow.model.dto.catalogs.ClaveProductoServicioDto;
import com.business.unknow.model.dto.catalogs.ClaveUnidadDto;
import com.business.unknow.model.dto.catalogs.CodigoPostalUiDto;
import com.business.unknow.model.dto.catalogs.RegimenFiscalDto;
import com.business.unknow.model.dto.catalogs.UsoCfdiDto;
import com.business.unknow.services.services.CatalogService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalogs")
public class CatalogsController {

  @Autowired private CatalogService catalogService;

  /**
   * Gets Postal code by code
   *
   * @param code
   * @return {@link CodigoPostalUiDto}
   */
  @GetMapping("/codigo-postal/{code}")
  public ResponseEntity<CodigoPostalUiDto> getPostalCodeByCode(@PathVariable String code) {
    return new ResponseEntity<>(catalogService.getPostalCodeByCode(code), HttpStatus.OK);
  }

  /**
   * Gets Product by description or code
   *
   * @param description
   * @param clave
   * @return {@link List<ClaveProductoServicioDto>}
   */
  @GetMapping("/producto-servicios")
  public ResponseEntity<List<ClaveProductoServicioDto>> getServiceProduct(
      @RequestParam(name = "descripcion") Optional<String> description,
      @RequestParam(name = "clave") Optional<String> clave) {
    return new ResponseEntity<>(
        catalogService.getServiceProduct(description, clave), HttpStatus.OK);
  }

  /**
   * Gets drive key by key
   *
   * @return {@link ClaveUnidadDto}
   */
  @GetMapping("/drive-keys")
  public ResponseEntity<List<ClaveUnidadDto>> getDriveKeys() {
    return new ResponseEntity<>(catalogService.getDriveKeys(), HttpStatus.OK);
  }

  /**
   * Gets all Cfdi uses in saved in cache
   *
   * @return {@link List<UsoCfdiDto>}
   */
  @GetMapping("/uso-cdfi")
  public ResponseEntity<List<UsoCfdiDto>> getCfdiUse() {
    return new ResponseEntity<>(catalogService.getCfdiUse(), HttpStatus.OK);
  }

  /**
   * Gets all Regime taxes saved in cache
   *
   * @return {@link List<RegimenFiscalDto>}
   */
  @GetMapping("/regimen-fiscal")
  public ResponseEntity<List<RegimenFiscalDto>> getRegimenFiscal() {
    return new ResponseEntity<>(catalogService.getTaxRegimes(), HttpStatus.OK);
  }

  /**
   * Gets all turns saved in cache
   *
   * @return {@link List<CatalogDto>}
   */
  @GetMapping("/turns")
  public ResponseEntity<List<CatalogDto>> getTurns() {
    return new ResponseEntity<>(catalogService.getTurns(), HttpStatus.OK);
  }

  /**
   * Gets Banks saved in cache
   *
   * @return {@link List<CatalogDto>}
   */
  @GetMapping("/banks")
  public ResponseEntity<List<CatalogDto>> getBanks() {
    return new ResponseEntity<>(catalogService.getBanks(), HttpStatus.OK);
  }

  /**
   * Gets Status events saved in cache
   *
   * @return {@link List<CatalogDto>}
   */
  @GetMapping("/status-events")
  public ResponseEntity<List<CatalogDto>> getStatusEvents() {
    return new ResponseEntity<>(catalogService.getStatusEvents(), HttpStatus.OK);
  }

  /**
   * Gets Status payments saved in cache
   *
   * @return {@link List<CatalogDto>}
   */
  @GetMapping("/status-payments")
  public ResponseEntity<List<CatalogDto>> getStatusPayments() {
    return new ResponseEntity<>(catalogService.getStatusPayments(), HttpStatus.OK);
  }
}
