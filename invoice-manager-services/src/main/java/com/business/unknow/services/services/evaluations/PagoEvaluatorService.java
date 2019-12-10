package com.business.unknow.services.services.evaluations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.business.unknow.Constants;
import com.business.unknow.commons.builder.FacturaBuilder;
import com.business.unknow.commons.builder.FacturaContextBuilder;
import com.business.unknow.enums.FormaPagoEnum;
import com.business.unknow.enums.MetodosPagoEnum;
import com.business.unknow.enums.TipoDocumentoEnum;
import com.business.unknow.model.PagoDto;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.rules.suites.facturas.ComplementoSuite;
import com.business.unknow.rules.suites.pagos.DeletePagoSuite;
import com.business.unknow.rules.suites.pagos.PagoPpdSuite;
import com.business.unknow.rules.suites.pagos.PagoPueSuite;
import com.business.unknow.services.entities.Pago;
import com.business.unknow.services.entities.factura.Factura;
import com.business.unknow.services.services.executor.PagoExecutorService;

@Service
public class PagoEvaluatorService extends AbstractEvaluatorService {

	@Autowired
	private PagoPpdSuite pagoPpdSuite;

	@Autowired
	private DeletePagoSuite deletePagoSuite;

	@Autowired
	private PagoPueSuite pagoPueSuite;

	@Autowired
	private ComplementoSuite complementoSuite;

	@Autowired
	private PagoExecutorService pagoExecutorService;

	public void validatePagoDeleting(Integer id) throws InvoiceManagerException {
		Pago pago = pagoRepository.findById(id)
				.orElseThrow(() -> new InvoiceManagerException("Metodo de pago no soportado",
						String.format("El pago con el id no existe %d", id), HttpStatus.BAD_REQUEST.value()));
		Factura factura = repository.findByFolio(pago.getFolio())
				.orElseThrow(() -> new InvoiceManagerException("No existe la factura del pago",
						String.format("Folio with the name %s not found", pago.getFolio()),
						HttpStatus.NOT_FOUND.value()));
		switch (TipoDocumentoEnum.findByDesc(factura.getTipoDocumento())) {
		case FACRTURA:
			deletePagoPue(factura, pago, id);
			break;
		case COMPLEMENTO:
			deletePagoPpd(factura, pago, id);
			break;
		default:
			new InvoiceManagerException("Tipo de documento not suported",
					String.format("Documento %s not suported", factura.getTipoDocumento()),
					HttpStatus.CONFLICT.value());
			break;
		}
	}

	public void deletePagoPpd(Factura factura, Pago pago, int id) throws InvoiceManagerException {
		Pago pagoPadre = pagoRepository.findByFolio(factura.getFolioPadre()).stream().findFirst()
				.orElseThrow(() -> new InvoiceManagerException("Pago a credito no encontrado",
						String.format("Verificar consitencia de pagos del folio %s", factura.getFolioPadre()),
						HttpStatus.NOT_FOUND.value()));
		PagoDto pagoDto=mapper.getPagoDtoFromEntity(pago);
		FacturaContext context = new FacturaContextBuilder()
				.setPagos(Arrays.asList(pagoDto))
				.setCurrentPago(pagoDto)
				.setFacturaDto(mapper.getFacturaDtoFromEntity(factura))
				.setPagoCredito(pagoMapper.getPagoDtoFromEntity(pagoPadre)).build();
		Facts facts = new Facts();
		facts.put("facturaContext", context);
		rulesEngine.fire(deletePagoSuite.getSuite(), facts);
		validateFacturaContext(context);
		deleteComplemento(context);
		pagoExecutorService.deletePagoPpdExecutor(context);
	}

	public void deletePagoPue(Factura factura, Pago pago, int id) throws InvoiceManagerException {
		List<Pago> pagos = pagoRepository.findByFolio(factura.getFolio());
		Optional<Pago> pagoCredito = pagos.stream()
				.filter(p -> p.getFormaPago().equals(FormaPagoEnum.CREDITO.getPagoValue())).findFirst();
		FacturaContext context = new FacturaContextBuilder().setCurrentPago(pagoMapper.getPagoDtoFromEntity(pago))
				.setPagos(pagoMapper.getPagosDtoFromEntities(pagos))
				.setFacturaDto(mapper.getFacturaDtoFromEntity(factura))
				.setPagoCredito(pagoCredito.isPresent() ? pagoMapper.getPagoDtoFromEntity(pagoCredito.get()) : null)
				.build();
		Facts facts = new Facts();
		facts.put("facturaContext", context);
		rulesEngine.fire(deletePagoSuite.getSuite(), facts);
		validateFacturaContext(context);
		pagoExecutorService.deletePagoPueExecutor(context);
	}

	public PagoDto validatePagoCreation(String folio, PagoDto pagoDto) throws InvoiceManagerException {
		FacturaContext facturaContext;
		Factura factura = repository.findByFolio(folio)
				.orElseThrow(() -> new InvoiceManagerException("No se encuentra la factura en el sistema",
						String.format("Folio with the name %s not found", folio), HttpStatus.NOT_FOUND.value()));
		pagoDto.setCreateUser(pagoDto.getUltimoUsuario());
		if (factura.getMetodoPago().equals(MetodosPagoEnum.PPD.getNombre())) {
			facturaContext = validatePagoPpdCreation(folio, pagoDto, factura);
		} else if (factura.getMetodoPago().equals(MetodosPagoEnum.PUE.getNombre())) {
			facturaContext = validatePagoPueCreation(folio, pagoDto);
		} else {
			throw new InvoiceManagerException("Metodo de pago no soportado",
					String.format("El metodo de pago %s no es valido", factura.getMetodoPago()),
					HttpStatus.BAD_REQUEST.value());
		}
		return pagoExecutorService.PagoCreation(facturaContext);
	}

	private FacturaContext validatePagoPpdCreation(String folio, PagoDto pagoDto, Factura factura)
			throws InvoiceManagerException {
		Factura facturaPadre = repository.findByFolio(folio)
				.orElseThrow(() -> new InvoiceManagerException("No se encuentra la factura en el sistema",
						String.format("Folio with the name %s not found", folio), HttpStatus.NOT_FOUND.value()));
		List<Pago> pagos = pagoRepository.findByFolioPadre(pagoDto.getFolioPadre());
		Pago pagoPadre = pagos.stream().filter(p -> p.getFolio().equals(folio)).findFirst()
				.orElseThrow(() -> new InvoiceManagerException("Pago a credito no encontrado",
						String.format("Verificar consitencia de pagos del folio %s", folio),
						HttpStatus.NOT_FOUND.value()));
		FacturaContext facturaContext = new FacturaContextBuilder().setPagos(pagoMapper.getPagosDtoFromEntities(pagos))
				.setFacturaPadreDto(mapper.getFacturaDtoFromEntity(facturaPadre))
				.setPagoCredito(pagoMapper.getPagoDtoFromEntity(pagoPadre)).setCurrentPago(pagoDto).build();
		Facts facts = new Facts();
		facts.put("facturaContext", facturaContext);
		rulesEngine.fire(pagoPpdSuite.getSuite(), facts);
		validateFacturaContext(facturaContext);
		return buildFacturaContextPagoPpdCreation(facturaContext);
	}

	private FacturaContext validatePagoPueCreation(String folio, PagoDto pagoDto) throws InvoiceManagerException {
		FacturaContext facturaContext = buildFacturaContextPagoPueCreation(folio, pagoDto);
		Facts facts = new Facts();
		facts.put("facturaContext", facturaContext);
		rulesEngine.fire(pagoPueSuite.getSuite(), facts);
		validateFacturaContext(facturaContext);
		if (facturaContext.getPagoCredito() != null) {
			facturaContext.getPagoCredito().setMonto(facturaContext.getPagoCredito().getMonto() - pagoDto.getMonto());
			pagoExecutorService.creaPapoPueExecutor(facturaContext);
		}
		return facturaContext;
	}

	private FacturaContext buildFacturaContextPagoPpdCreation(FacturaContext facturaContext)
			throws InvoiceManagerException {
		FacturaBuilder facturaBuilder = new FacturaBuilder()
				.setFolioPadre(facturaContext.getFacturaPadreDto().getFolio())
				.setPackFacturacion(facturaContext.getFacturaPadreDto().getPackFacturacion())
				.setMetodoPago(facturaContext.getFacturaPadreDto().getMetodoPago())
				.setRfcEmisor(facturaContext.getFacturaPadreDto().getRfcEmisor())
				.setRfcRemitente(facturaContext.getFacturaPadreDto().getRfcRemitente())
				.setRazonSocialEmisor(facturaContext.getFacturaPadreDto().getRazonSocialEmisor())
				.setRazonSocialRemitente(facturaContext.getFacturaPadreDto().getRazonSocialRemitente())
				.setTotal(facturaContext.getCurrentPago().getMonto())
				.setTipoDocumento(TipoDocumentoEnum.COMPLEMENTO.getDescripcion())
				.setFormaPago(FormaPagoEnum.findByDesc(facturaContext.getCurrentPago().getFormaPago()).getClave());
		facturaContext.setFacturaDto(facturaBuilder.build());
		facturaContext = createNewComplemento(facturaContext, facturaContext.getFacturaPadreDto().getFolio());
		facturaContext.getCurrentPago().setFolio(facturaContext.getFacturaDto().getFolio());
		facturaContext.getCurrentPago().setFolioPadre(facturaContext.getFacturaDto().getFolioPadre());
		facturaContext.setPagos(Arrays.asList(facturaContext.getCurrentPago()));
		facturaContext.getPagoCredito()
				.setMonto(numberHelper.assignPrecision(
						facturaContext.getPagoCredito().getMonto() - facturaContext.getCurrentPago().getMonto(),
						Constants.DEFAULT_SCALE));
		return pagoExecutorService.creaPapoPpdExecutor(facturaContext);
	}

	private FacturaContext buildFacturaContextPagoPueCreation(String folio, PagoDto pagoDto)
			throws InvoiceManagerException {
		List<Pago> pagos = pagoRepository.findByFolio(folio);
		Optional<Factura> factura = repository.findByFolio(folio);
		Optional<Pago> pagoCredito = pagos.stream()
				.filter(p -> p.getFormaPago().equals(FormaPagoEnum.CREDITO.getPagoValue())).findFirst();
		return new FacturaContextBuilder().setPagos(Arrays.asList(pagoDto))
				.setPagos(pagoMapper.getPagosDtoFromEntities(pagos)).setCurrentPago(pagoDto)
				.setFacturaDto(factura.isPresent() ? mapper.getFacturaDtoFromEntity(factura.get()): null)
				.setPagoCredito(pagoCredito.isPresent() ? pagoMapper.getPagoDtoFromEntity(pagoCredito.get()) : null)
				.build();
	}

	public FacturaContext createNewComplemento(FacturaContext facturaContext, String folio)
			throws InvoiceManagerException {
		Facts facts = new Facts();
		facturaDefaultValues.assignaDefaultsComplemento(facturaContext.getFacturaDto());
		facts.put("facturaContext", facturaContext);
		rulesEngine.fire(complementoSuite.getSuite(), facts);
		validateFacturaContext(facturaContext);
		return facturaContext;
	}

	public FacturaContext deleteComplemento(FacturaContext context) throws InvoiceManagerException {
		Facts facts = new Facts();
		facts.put("facturaContext", context);
		rulesEngine.fire(deletePagoSuite.getSuite(), facts);
		validateFacturaContext(context);
		return context;
	}

}
