/**
 * 
 */
package com.business.unknow.services.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.business.unknow.model.StatusCatalogoDto;
import com.business.unknow.model.catalogs.ClaveProductoServicioDto;
import com.business.unknow.model.catalogs.ClaveUnidadDto;
import com.business.unknow.model.catalogs.GiroDto;
import com.business.unknow.model.catalogs.RegimenFiscalDto;
import com.business.unknow.model.catalogs.StatusFacturaDto;
import com.business.unknow.model.catalogs.UsoCfdiDto;
import com.business.unknow.services.entities.catalogs.ClaveProductoServicio;
import com.business.unknow.services.entities.catalogs.ClaveUnidad;
import com.business.unknow.services.mapper.CatalogsMapper;
import com.business.unknow.services.repositories.GiroRepository;
import com.business.unknow.services.repositories.StatusDevolucionRepository;
import com.business.unknow.services.repositories.StatusEventoRepository;
import com.business.unknow.services.repositories.StatusPagoRepository;
import com.business.unknow.services.repositories.catalogs.ClaveProductoServicioRepository;
import com.business.unknow.services.repositories.catalogs.ClaveUnidadRepository;
import com.business.unknow.services.repositories.catalogs.RegimanFiscalRepository;
import com.business.unknow.services.repositories.catalogs.StatusFacturaRepository;
import com.business.unknow.services.repositories.catalogs.StatusRevisionRepository;
import com.business.unknow.services.repositories.catalogs.UsoCfdiRepository;

/**
 * @author ralfdemoledor
 *
 */
@Service
public class CatalogsService {

	@Autowired
	private ClaveProductoServicioRepository productorServicioRepo;

	@Autowired
	private ClaveUnidadRepository unidadRepo;

	@Autowired
	private RegimanFiscalRepository regimenFiscalRepo;

	@Autowired
	private UsoCfdiRepository usoCfdiRepo;

	@Autowired
	private StatusFacturaRepository statusFacturaRepo;
	
	@Autowired
	private StatusEventoRepository statusEventoRepo;
	
	@Autowired
	private StatusPagoRepository statusPagoRepo;
	
	@Autowired
	private StatusDevolucionRepository statusDevoluicionRepo;
	
	@Autowired
	private StatusRevisionRepository statusRevisionRepo;

	@Autowired
	private GiroRepository giroRepo;

	@Autowired
	private CatalogsMapper mapper;

	public List<ClaveProductoServicioDto> getProductoServicioByDescription(String description) {
		return mapper.getClaveProdServDtosFromEntities(productorServicioRepo.findByDescripcionContainingIgnoreCase(description));
	}

	public Page<ClaveProductoServicioDto> getAllProductoServicioClaves(int page, int size) {
		Page<ClaveProductoServicio> result = productorServicioRepo.findAll(PageRequest.of(page, size));
		return new PageImpl<>(mapper.getClaveProdServDtosFromEntities(result.getContent()), result.getPageable(),
				result.getTotalElements());
	}

	public Page<ClaveUnidadDto> getAllClaveUnidad(int page, int size) {
		Page<ClaveUnidad> result = unidadRepo.findAll(PageRequest.of(page, size));
		return new PageImpl<>(mapper.getClaveUnidadDtosFromEntities(result.getContent()), result.getPageable(),
				result.getTotalElements());
	}
	
	public List<ClaveUnidadDto> getCalveUnidadByNombre(String nombre){
		return mapper.getClaveUnidadDtosFromEntities(unidadRepo.findByNombreContainingIgnoreCase(nombre));
	}

	public List<ClaveUnidadDto> getAllClaveUnidad() {
		return mapper.getClaveUnidadDtosFromEntities(unidadRepo.findAll());
	}

	public List<RegimenFiscalDto> getAllRegimenFiscal() {
		return mapper.getRegimenFiscalDtosFromEntities(regimenFiscalRepo.findAll());
	}

	public List<UsoCfdiDto> getAllUsoCfdi() {
		return mapper.getUsoCfdiDtosFromEntities(usoCfdiRepo.findAll());
	}

	public List<StatusFacturaDto> getAllStatusFactura() {
		return mapper.getStatusFacturaDtosFromEntities(statusFacturaRepo.findAll());
	}

	public List<GiroDto> getAllGiros() {
		return mapper.getGiroDtosFromEntities(giroRepo.findAll());
	}
	
	public List<StatusCatalogoDto> getAllStatusEvento() {
		return mapper.getStatusEventoDtosFromEntities(statusEventoRepo.findAll());
	}
	
	
	public List<StatusCatalogoDto> getAllStatusPago() {
		return mapper.getStatusPagoDtosFromEntities(statusPagoRepo.findAll());
	}
	
	public List<StatusCatalogoDto> getAllStatusDevoluicion() {
		return mapper.getStatusDevolucionDtosFromEntities(statusDevoluicionRepo.findAll());
	}
	
	public List<StatusCatalogoDto> getAllStatusRevision() {
		return mapper.getStatusRevisionDtosFromEntities(statusRevisionRepo.findAll());
	}

}
