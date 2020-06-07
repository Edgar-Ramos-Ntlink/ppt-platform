/**
 * 
 */
package com.business.unknow.services.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.business.unknow.enums.FormaPagoEnum;
import com.business.unknow.enums.MetodosPagoEnum;
import com.business.unknow.model.dto.cfdi.CfdiDto;
import com.business.unknow.model.dto.cfdi.CfdiPagoDto;
import com.business.unknow.model.dto.cfdi.ComplementoDto;
import com.business.unknow.model.dto.cfdi.ConceptoDto;
import com.business.unknow.model.dto.cfdi.ImpuestoDto;
import com.business.unknow.model.dto.cfdi.RetencionDto;
import com.business.unknow.model.dto.services.PagoDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.cfdi.Cfdi;
import com.business.unknow.services.entities.cfdi.CfdiPago;
import com.business.unknow.services.entities.cfdi.Concepto;
import com.business.unknow.services.entities.cfdi.Emisor;
import com.business.unknow.services.entities.cfdi.Impuesto;
import com.business.unknow.services.entities.cfdi.Receptor;
import com.business.unknow.services.entities.cfdi.Retencion;
import com.business.unknow.services.mapper.factura.CfdiMapper;
import com.business.unknow.services.repositories.facturas.CfdiPagoRepository;
import com.business.unknow.services.repositories.facturas.CfdiRepository;
import com.business.unknow.services.repositories.facturas.ConceptoRepository;
import com.business.unknow.services.repositories.facturas.EmisorRepository;
import com.business.unknow.services.repositories.facturas.ImpuestoRepository;
import com.business.unknow.services.repositories.facturas.ReceptorRepository;
import com.business.unknow.services.repositories.facturas.RetencionRepository;

/**
 * @author ralfdemoledor
 *
 */
@Service
public class CfdiService {

	@Autowired
	private CfdiRepository repository;

	@Autowired
	private ConceptoRepository conceptoRepository;

	@Autowired
	private EmisorRepository emisorRepository;

	@Autowired
	private CfdiPagoRepository cfdiPagoRepository;

	@Autowired
	private ReceptorRepository receptorReceptor;

	@Autowired
	private ImpuestoRepository impuestoRepository;

	@Autowired
	private RetencionRepository retencionRepository;

	@Autowired
	private PagoService pagoService;

	@Autowired
	private CfdiMapper mapper;

	@Autowired
	private CatalogCacheService cacheCatalogsService;

	private static final Logger log = LoggerFactory.getLogger(CfdiService.class);

	public CfdiDto getCfdiByFolio(String folio) {
		CfdiDto cfdiDto = mapper.getCfdiDtoFromEntity(
				repository.findByFolio(folio).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						String.format("No se encontro CFDI con folio %s", folio))));
		if (cfdiDto.getConceptos() == null || cfdiDto.getConceptos().isEmpty()) {
			cfdiDto.setConceptos(mapper.getDtosFromConceptoEntities(conceptoRepository.findByCfdi(cfdiDto.getId())));
		}
		for (ConceptoDto conceptoDto : cfdiDto.getConceptos()) {
			conceptoDto.setImpuestos(getImpuestosByConcepto(conceptoDto.getId()));
		}
		List<CfdiPagoDto> pagosCfdi = getCfdiPagosByCfdi(cfdiDto.getId());
		if (cfdiDto.getComplemento() == null) {
			cfdiDto.setComplemento(new ComplementoDto());
		}
		if (pagosCfdi != null && !pagosCfdi.isEmpty()) {
			cfdiDto.getComplemento().setPagos(pagosCfdi);
		}
		if (cfdiDto.getEmisor() == null) {
			Optional<Emisor> emisor = emisorRepository.findByIdCfdi(cfdiDto.getId());
			if (emisor.isPresent()) {
				cfdiDto.setEmisor(mapper.getEmisorDtoFromEntity(emisor.get()));
			}
		}
		if (cfdiDto.getReceptor() == null) {
			Optional<Receptor> receptor = receptorReceptor.findByIdCfdi(cfdiDto.getId());
			if (receptor.isPresent()) {
				cfdiDto.setReceptor(mapper.getRecetorDtoFromEntity(receptor.get()));
			}
		}
		return cfdiDto;
	}

	public CfdiDto getCfdibyId(Integer id) {
		CfdiDto cfdiDto = mapper.getCfdiDtoFromEntity(
				repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						String.format("El cfdi con folio %d no fue encontrado", id))));

		if (cfdiDto.getComplemento() == null) {
			cfdiDto.setComplemento(new ComplementoDto());
			List<CfdiPagoDto> pagosCfdi = getCfdiPagosByCfdi(cfdiDto.getId());
			if (pagosCfdi != null && !pagosCfdi.isEmpty()) {
				cfdiDto.getComplemento().setPagos(pagosCfdi);
			}
		}

		return cfdiDto;
	}

	private List<ImpuestoDto> getImpuestosByConcepto(int id) {
		return mapper.getImpuestosDtosFromEntities(impuestoRepository.findById(id));
	}

	private List<CfdiPagoDto> getCfdiPagosByCfdi(int id) {
		return mapper.getCfdiPagosDtoFromEntities(cfdiPagoRepository.findByCfdi(id));
	}

	public List<CfdiPagoDto> getPagosPPD(Integer id) {
		Cfdi cfdi = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
				String.format("El cfdi con folio %d no fue encontrado", id)));
		return mapper.getCfdiPagosDtoFromEntities(cfdiPagoRepository.findByFolio(cfdi.getFolio()));
	}

	public CfdiDto insertNewCfdi(CfdiDto cfdi) throws InvoiceManagerException {
		validateCfdi(cfdi);
		recalculateCfdiAmmounts(cfdi);
		Cfdi entity = repository.save(mapper.getEntityFromCfdiDto(cfdi));
		Emisor emisor = mapper.getEntityFromEmisorDto(cfdi.getEmisor());
		emisor.setCfdi(entity);
		emisorRepository.save(emisor);
		Receptor receptor = mapper.getEntityFromReceptorDto(cfdi.getReceptor());
		receptor.setCfdi(entity);
		receptorReceptor.save(receptor);
		for (ConceptoDto concepto : cfdi.getConceptos()) {
			Concepto conceptoEntity = mapper.getEntityFromConceptoDto(concepto);
			conceptoEntity.setCfdi(entity);
			conceptoEntity = conceptoRepository.save(conceptoEntity);
			for (ImpuestoDto impuesto : concepto.getImpuestos()) {
				Impuesto imp = mapper.getEntityFromImpuestoDto(impuesto);
				imp.setConcepto(conceptoEntity);
				impuestoRepository.save(imp);
			}
			for (RetencionDto retencion : concepto.getRetenciones()) {
				Retencion reten = mapper.getEntityFromRetencionDto(retencion);
				reten.setConcepto(conceptoEntity);
				retencionRepository.save(reten);
			}

		}
		if (cfdi.getComplemento() != null && cfdi.getComplemento().getPagos() != null
				&& !cfdi.getComplemento().getPagos().isEmpty()) {
			for (CfdiPagoDto cfdiPagoDto : cfdi.getComplemento().getPagos()) {
				CfdiPago cfdiPago = mapper.getEntityFromCdfiPagosDto(cfdiPagoDto);
				cfdiPago.setCfdi(entity);
				cfdiPagoRepository.save(cfdiPago);
			}
		}
		CfdiDto dto = mapper.getCfdiDtoFromEntity(repository.findById(entity.getId()).orElse(null));
		dto.setConceptos(cfdi.getConceptos());
		return dto;
	}

	@Transactional(rollbackFor = { InvoiceManagerException.class, DataAccessException.class, SQLException.class })
	public CfdiDto updateCfdiBody(Integer id, CfdiDto cfdi) throws InvoiceManagerException {
		validateCfdi(cfdi);
		recalculateCfdiAmmounts(cfdi);
		repository.findById(id).orElseThrow(() -> new InvoiceManagerException("Error al obtener el Cfdi",
				String.format("El cfdi con el prefolio %d no existe", id), HttpStatus.NOT_FOUND.value()));
		return mapper.getCfdiDtoFromEntity(repository.save(mapper.getEntityFromCfdiDto(cfdi)));
	}

	@Transactional(rollbackFor = { InvoiceManagerException.class, DataAccessException.class, SQLException.class })
	public void deleteCfdi(Integer id) throws InvoiceManagerException {
		Cfdi entity = repository.findById(id).orElseThrow(() -> new InvoiceManagerException("Error al obtener el Cfdi",
				String.format("El cfdi con el prefolio %d no existe", id), HttpStatus.NOT_FOUND.value()));

		for (Concepto concepto : entity.getConceptos()) {
			for (Impuesto impuesto : concepto.getImpuestos()) {
				impuestoRepository.delete(impuesto);
			}
			for (Retencion retencion : concepto.getRetenciones()) {
				retencionRepository.delete(retencion);
			}
			conceptoRepository.delete(concepto);
		}
		repository.delete(entity);
	}

	@Transactional(rollbackFor = { InvoiceManagerException.class, DataAccessException.class,
			SQLException.class }, isolation = Isolation.READ_UNCOMMITTED)
	public CfdiDto insertNewConceptoToCfdi(Integer idCfdi, ConceptoDto newConcept) throws InvoiceManagerException {
		CfdiDto cfdi = mapper.getCfdiDtoFromEntity(
				repository.findById(idCfdi).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						String.format("No se ecnontro CFDI con prefolio %d", idCfdi))));
		cfdi.getConceptos().add(newConcept);
		validateCfdi(cfdi);
		recalculateCfdiAmmounts(cfdi);
		Cfdi entity = repository.save(mapper.getEntityFromCfdiDto(cfdi));// Update CFDI ammounts
		Concepto conceptoEntity = mapper.getEntityFromConceptoDto(newConcept);
		conceptoEntity.setCfdi(entity);
		conceptoEntity = conceptoRepository.save(conceptoEntity);
		for (ImpuestoDto impuesto : newConcept.getImpuestos()) {
			Impuesto imp = mapper.getEntityFromImpuestoDto(impuesto);
			imp.setConcepto(conceptoEntity);
			imp.setBase(imp.getBase().setScale(6, RoundingMode.DOWN));
			imp.setImporte(imp.getImporte().setScale(6, RoundingMode.DOWN));
			impuestoRepository.save(imp);
		}
		for (RetencionDto retencion : newConcept.getRetenciones()) {
			Retencion ret = mapper.getEntityFromRetencionDto(retencion);
			ret.setConcepto(conceptoEntity);
			ret.setBase(ret.getBase().setScale(6, RoundingMode.DOWN));
			ret.setImporte(ret.getImporte().setScale(6, RoundingMode.DOWN));
			retencionRepository.save(ret);
		}
		return cfdi;
	}

	@Transactional(rollbackFor = { InvoiceManagerException.class, DataAccessException.class, SQLException.class })
	public CfdiDto removeConceptFromCfdi(Integer idCfdi, Integer conceptoId) throws InvoiceManagerException {
		CfdiDto cfdi = mapper.getCfdiDtoFromEntity(
				repository.findById(idCfdi).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						String.format("No se ecnontro CFDI con prefolio %d", idCfdi))));
		Concepto concepto = conceptoRepository.findById(conceptoId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						String.format("No se encontro concepto con id %d", conceptoId)));
		cfdi.getConceptos()
				.remove(cfdi.getConceptos().stream().filter(c -> c.getId().equals(conceptoId)).findFirst()
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
								String.format("No se encontro concepto con id %d", conceptoId))));
		validateCfdi(cfdi);
		recalculateCfdiAmmounts(cfdi);
		repository.save(mapper.getEntityFromCfdiDto(cfdi));// Update CFDI ammounts
		for (Impuesto impuesto : concepto.getImpuestos()) {
			impuestoRepository.delete(impuesto); // Deleting impuesto
		}
		for (Retencion retencion : concepto.getRetenciones()) {
			retencionRepository.delete(retencion); // Deleting retenciones
		}
		conceptoRepository.deleteById(concepto.getId());
		return cfdi;
	}

	@Transactional(rollbackFor = { InvoiceManagerException.class, DataAccessException.class, SQLException.class })
	public CfdiDto updateConceptoFromCfdi(Integer idCfdi, Integer conceptoId, ConceptoDto concepto)
			throws InvoiceManagerException {
		CfdiDto cfdi = mapper.getCfdiDtoFromEntity(
				repository.findById(idCfdi).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						String.format("No se ecnontro CFDI con prefolio %d", idCfdi))));
		int index = cfdi.getConceptos()
				.indexOf(cfdi.getConceptos().stream().filter(c -> c.getId().equals(conceptoId)).findFirst()
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
								String.format("No se encontro concepto con id %d", conceptoId))));
		cfdi.getConceptos().set(index, concepto);
		validateCfdi(cfdi);
		recalculateCfdiAmmounts(cfdi);
		Cfdi entity = repository.save(mapper.getEntityFromCfdiDto(cfdi));
		Concepto conceptoEntity = mapper.getEntityFromConceptoDto(concepto);
		conceptoEntity.setCfdi(entity);
		conceptoRepository.save(conceptoEntity);
		impuestoRepository.deleteByConcepto(conceptoEntity);
		retencionRepository.deleteByConcepto(conceptoEntity);
		// 2.- Update concepto and its references
		for (ImpuestoDto impuesto : concepto.getImpuestos()) {
			Impuesto imp = mapper.getEntityFromImpuestoDto(impuesto);
			imp.setConcepto(conceptoEntity);
			impuestoRepository.save(imp);
		}
		// 3.- Update concepto and its references
		for (RetencionDto retencionDto : concepto.getRetenciones()) {
			Retencion ret = mapper.getEntityFromRetencionDto(retencionDto);
			ret.setConcepto(conceptoEntity);
			retencionRepository.save(ret);
		}
		if (cfdi.getMetodoPago().equals(MetodosPagoEnum.PPD.name()) && cfdi.getTotal().compareTo(BigDecimal.ZERO) > 0) {
			List<PagoDto> pagos = pagoService.findPagosByFolioPadre(cfdi.getFolio());
			Optional<PagoDto> pagoCredito = pagos.stream().filter(a -> a.getFormaPago().equals("CREDITO")).findFirst();
			if (pagos.size() == 1 && pagoCredito.isPresent()) {
				PagoDto pago = pagoCredito.get();
				pago.setMonto(cfdi.getTotal());
				pagoService.updateMontoPago(pago.getId(), pago);
			}
		}
		// 4.- recalculate pdf
		return cfdi;
	}

	public void validateCfdi(CfdiDto cfdi) throws InvoiceManagerException {
		if (cfdi.getEmisor().getRfc() == null || cfdi.getEmisor().getNombre() == null) {
			throw new InvoiceManagerException("La informacion del emisor es requerida", "Informacion emisor faltante",
					HttpStatus.CONFLICT.value());
		}

		if (cfdi.getReceptor().getRfc() == null || cfdi.getReceptor().getNombre() == null) {
			throw new InvoiceManagerException("La informacion del receptor es requerida",
					"Informacion receptor faltante", HttpStatus.CONFLICT.value());
		}

		if (!cfdi.getMetodoPago().equals(MetodosPagoEnum.PPD.name())
				&& !cfdi.getMetodoPago().equals(MetodosPagoEnum.PUE.name())) {
			throw new InvoiceManagerException("El metodo de pago de la factura solo puede ser PUE o PPD",
					"Metodo de pago invalido", HttpStatus.CONFLICT.value());
		}

		if (!cacheCatalogsService.getUsoCfdi(cfdi.getReceptor().getUsoCfdi()).isPresent()) {
			throw new InvoiceManagerException("Uso de CFDI invalido", "Uso de CFDI invalido",
					HttpStatus.CONFLICT.value());
		}

		if (!cacheCatalogsService.getFormaPago(cfdi.getFormaPago()).isPresent()) {
			throw new InvoiceManagerException(String.format("La forma de pago %s es invalida", cfdi.getFormaPago()),
					"Forma de pago invalida", HttpStatus.CONFLICT.value());
		}

		if (cfdi.getConceptos().isEmpty()) {
			throw new InvoiceManagerException("El CFDI no puede tener 0 conceptos", "Numero de comceptos invalido",
					HttpStatus.CONFLICT.value());
		}
		if (FormaPagoEnum.EFECTIVO.getClave().equals(cfdi.getMetodoPago())) {
			throw new InvoiceManagerException(
					"En pagos en efectivo el monto a facturar no debe de ser superior a 2000 pesos",
					"Metodo de pago invalido", HttpStatus.CONFLICT.value());
		}

		// TODO add more validations here
	}

	/**
	 * This method recalculate CFDI amounts based on SAT rounding rules, do not move
	 * or update this method without carefully review.
	 * 
	 * @param cfdi
	 */
	private void recalculateCfdiAmmounts(CfdiDto cfdi) {

		// Importes, retenciones y traslados recalculo
		cfdi.getConceptos().forEach(a -> {
			a.setImporte(a.getValorUnitario().multiply(a.getCantidad()));
			a.getImpuestos().forEach(b -> {
				b.setImporte(b.getBase().multiply(b.getTasaOCuota()));
			});
			a.getRetenciones().forEach(b -> {
				b.setImporte(b.getBase().multiply(b.getTasaOCuota()));
			});
		});

		BigDecimal subtotal = cfdi.getConceptos().stream().map(c -> c.getValorUnitario().multiply(c.getCantidad()))
				.reduce(BigDecimal.ZERO, (i1, i2) -> i1.add(i2));
		BigDecimal retenciones = cfdi.getConceptos().stream()
				.map(i -> i.getRetenciones().stream().map(imp -> imp.getBase().multiply(imp.getTasaOCuota()))
						.reduce(BigDecimal.ZERO, (i1, i2) -> i1.add(i2)))// suma importe retencioness por concepto
				.reduce(BigDecimal.ZERO, (i1, i2) -> i1.add(i2)).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal impuestos = cfdi.getConceptos().stream()
				.map(i -> i.getImpuestos().stream().map(imp -> imp.getBase().multiply(imp.getTasaOCuota()))
						.reduce(BigDecimal.ZERO, (i1, i2) -> i1.add(i2)))// suma importe impuestos por concepto
				.reduce(BigDecimal.ZERO, (i1, i2) -> i1.add(i2)).setScale(2, BigDecimal.ROUND_HALF_UP);

		BigDecimal total = subtotal.add(impuestos).subtract(retenciones);
		log.info("Calculating cfdi values subtotal = {}, impuestos = {} , total = {}", subtotal, impuestos, total);

		cfdi.setImpuestosTrasladados(impuestos);
		cfdi.setImpuestosRetenidos(retenciones);
		cfdi.setSubtotal(subtotal.setScale(2, BigDecimal.ROUND_HALF_UP));
		cfdi.setTotal(total.setScale(2, BigDecimal.ROUND_HALF_UP));
		cfdi.setDescuento(BigDecimal.ZERO);// los descuentos no estan soportados
	}

}
