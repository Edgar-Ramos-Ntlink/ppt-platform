package com.business.unknow.services.services.evaluations;

import org.apache.http.HttpStatus;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.unknow.enums.PackFacturarionEnum;
import com.business.unknow.enums.TipoDocumentoEnum;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.model.factura.FacturaDto;
import com.business.unknow.rules.suites.TimbradoSuite;
import com.business.unknow.rules.suites.facturas.CancelacionSuite;
import com.business.unknow.services.services.executor.FacturacionModernaExecutor;
import com.business.unknow.services.services.executor.SwSapinsExecutorService;
import com.business.unknow.services.services.translators.FacturaTranslator;

@Service
public class TimbradoEvaluatorService extends AbstractEvaluatorService {

	@Autowired
	private CancelacionSuite cancelacionSuite;

	@Autowired
	private TimbradoSuite FacturarSuite;

	@Autowired
	private FacturaTranslator facturaTranslator;

	@Autowired
	private SwSapinsExecutorService swSapinsExecutorService;

	@Autowired
	private FacturacionModernaExecutor facturacionModernaExecutor;

	@Autowired
	private RulesEngine rulesEngine;

	public FacturaContext facturaCancelacionValidation(FacturaDto facturaDto, String folio)
			throws InvoiceManagerException {
		FacturaContext facturaContext = buildFacturaContextCancelado(facturaDto, folio);
		Facts facts = new Facts();
		facts.put("facturaContext", facturaContext);
		rulesEngine.fire(cancelacionSuite.getSuite(), facts);
		validateFacturaContext(facturaContext);
		switch (PackFacturarionEnum.findByNombre(facturaContext.getFacturaDto().getPackFacturacion())) {
		case SW_SAPIENS:
			swSapinsExecutorService.cancelarFactura(facturaContext);
			break;
		case FACTURACION_MODERNA:
			facturacionModernaExecutor.cancelarFactura(facturaContext);
			break;
		default:
			throw new InvoiceManagerException("Pack not supported yet", "Validate with programers",
					HttpStatus.SC_BAD_REQUEST);
		}
		updateCanceladoValues(facturaContext);
		return facturaContext;
	}

	public FacturaContext facturaTimbradoValidation(FacturaDto facturaDto, String folio)
			throws InvoiceManagerException {
		FacturaContext facturaContext = buildFacturaContextTimbrado(facturaDto, folio);
		Facts facts = new Facts();
		facts.put("facturaContext", facturaContext);
		rulesEngine.fire(FacturarSuite.getSuite(), facts);
		validateFacturaContext(facturaContext);
		switch (TipoDocumentoEnum.findByDesc(facturaContext.getTipoDocumento())) {
		case FACRTURA:
			facturaContext = facturaTranslator.translateFactura(facturaContext);
			break;
		case COMPLEMENTO:
			facturaContext = facturaTranslator.translateComplemento(facturaContext);
			break;
		default:
			throw new InvoiceManagerException("The type of document not supported",
					String.format("The type of document %s not valid", facturaContext.getTipoDocumento()),
					HttpStatus.SC_BAD_REQUEST);
		}
		switch (PackFacturarionEnum.findByNombre(facturaContext.getFacturaDto().getPackFacturacion())) {
		case SW_SAPIENS:
			swSapinsExecutorService.stamp(facturaContext);
			break;
		case FACTURACION_MODERNA:
			facturacionModernaExecutor.stamp(facturaContext);
			break;
		default:
			throw new InvoiceManagerException("Pack not supported yet", "Validate with programers",
					HttpStatus.SC_BAD_REQUEST);
		}
		updateFacturaAndCfdiValues(facturaContext);
		return facturaContext;
	}

}
