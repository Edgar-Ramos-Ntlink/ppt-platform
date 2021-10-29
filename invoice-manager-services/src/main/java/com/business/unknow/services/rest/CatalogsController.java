/** */
package com.business.unknow.services.rest;

import com.business.unknow.model.dto.catalogs.CatalogDto;
import com.business.unknow.model.dto.catalogs.ClaveProductoServicioDto;
import com.business.unknow.model.dto.catalogs.ClaveUnidadDto;
import com.business.unknow.model.dto.catalogs.CodigoPostalUiDto;
import com.business.unknow.model.dto.catalogs.RegimenFiscalDto;
import com.business.unknow.model.dto.catalogs.UsoCfdiDto;
import com.business.unknow.services.services.CatalogsService;
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

/** @author ralfdemoledor */
@RestController
@RequestMapping("/api/catalogs")
public class CatalogsController {

  @Autowired private CatalogsService service;

  @GetMapping("/codigo-postal/{cp}")
  public ResponseEntity<CodigoPostalUiDto> getCodigoPostalesByCode(@PathVariable String cp) {
    return new ResponseEntity<>(service.getCodigosPostaleByCode(cp), HttpStatus.OK);
  }

  @GetMapping("/producto-servicios")
  public ResponseEntity<List<ClaveProductoServicioDto>> getClaveProductoServicios(
      @RequestParam(name = "descripcion") Optional<String> description,
      @RequestParam(name = "clave") Optional<String> clave) {
    return new ResponseEntity<>(service.getProductoServicio(description, clave), HttpStatus.OK);
  }

  @GetMapping("/clave-unidad")
  public ResponseEntity<List<ClaveUnidadDto>> getClaveUnidad(
      @RequestParam(name = "nombre", required = true) String nombre) {
    return new ResponseEntity<>(service.getCalveUnidadByNombre(nombre), HttpStatus.OK);
  }

  @GetMapping("/uso-cdfi")
  public ResponseEntity<List<UsoCfdiDto>> getAllUsoCdfi() {
    return new ResponseEntity<>(service.getAllUsoCfdi(), HttpStatus.OK);
  }

  @GetMapping("/regimen-fiscal")
  public ResponseEntity<List<RegimenFiscalDto>> getRegimenFiscal() {
    return new ResponseEntity<>(service.getAllRegimenFiscal(), HttpStatus.OK);
  }

  @GetMapping("/giros")
  public ResponseEntity<List<CatalogDto>> getGiros() {
    return new ResponseEntity<>(service.getAllGiros(), HttpStatus.OK);
  }

  @GetMapping("/bancos")
  public ResponseEntity<List<CatalogDto>> getBancos() {
    return new ResponseEntity<>(service.getAllBancos(), HttpStatus.OK);
  }

  @GetMapping("/bancos/{banco}")
  public ResponseEntity<CatalogDto> getBancoByName(@PathVariable String banco) {
    return new ResponseEntity<>(service.getAllBancoByName(banco), HttpStatus.OK);
  }

  @GetMapping("/status-evento")
  public ResponseEntity<List<CatalogDto>> getStatusEventos() {
    return new ResponseEntity<>(service.getAllStatusEvento(), HttpStatus.OK);
  }

  @GetMapping("/status-pago")
  public ResponseEntity<List<CatalogDto>> getStatusPago() {
    return new ResponseEntity<>(service.getAllStatusPago(), HttpStatus.OK);
  }

  @GetMapping("/status-devolucion")
  public ResponseEntity<List<CatalogDto>> getAllStatusDevoluicion() {
    return new ResponseEntity<>(service.getAllStatusDevoluicion(), HttpStatus.OK);
  }
}
