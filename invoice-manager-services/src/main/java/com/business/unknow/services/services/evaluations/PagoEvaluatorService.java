package com.business.unknow.services.services.evaluations;

import java.util.ArrayList;
import java.util.List;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.RulesEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.business.unknow.commons.validator.AbstractValidator;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.cfdi.CfdiDto;
import com.business.unknow.model.dto.services.PagoDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.rules.suites.payments.DeletePagoSuite;
import com.business.unknow.rules.suites.payments.PaymentCreationSuite;
import com.business.unknow.rules.suites.payments.PaymentUpdateSuite;

@Service
public class PagoEvaluatorService extends AbstractValidator {
	
	@Autowired
	protected RulesEngine rulesEngine;

	@Autowired
	private PaymentCreationSuite creationSuite;

	@Autowired
	private DeletePagoSuite deletePagoSuite;

	@Autowired
	private PaymentUpdateSuite updateSuite;


	public void deletepaymentValidation(PagoDto payment,FacturaDto factura) throws InvoiceManagerException {
		Facts facts = new Facts();
		List<String> results = new ArrayList<String>();
		facts.put("payment", payment);
		facts.put("factura", factura);
		facts.put("results", results);
		
		rulesEngine.fire(deletePagoSuite.getSuite(), facts);
		if(!results.isEmpty()) {
			throw new InvoiceManagerException(results.toString(), "Some payment update rules was triggered.", HttpStatus.CONFLICT.value());
		}
	}

	public void validatePaymentCreation(PagoDto currentPayment, List<PagoDto> payments,CfdiDto cfdi) throws InvoiceManagerException {
		Facts facts = new Facts();
		List<String> results = new ArrayList<String>();
		facts.put("currentPayment", currentPayment);
		facts.put("payments", payments);
		facts.put("cfdi", cfdi);
		facts.put("results", results);
		
		rulesEngine.fire(creationSuite.getSuite(), facts);
		if(!results.isEmpty()) {
			throw new InvoiceManagerException(results.toString(), "Some payment creation rules was triggered.", HttpStatus.CONFLICT.value());
		}
	}

	public void validatePaymentUpdate(PagoDto currentPayment,PagoDto dbPayment, List<PagoDto> payments,FacturaDto factura) throws InvoiceManagerException {
		Facts facts = new Facts();
		List<String> results = new ArrayList<String>();
		facts.put("currentPayment", currentPayment);
		facts.put("dbPayment", dbPayment);
		facts.put("factura", factura);
		facts.put("results", results);
		
		rulesEngine.fire(updateSuite.getSuite(), facts);
		if(!results.isEmpty()) {
			throw new InvoiceManagerException(results.toString(), "Some payment update rules was triggered.", HttpStatus.CONFLICT.value());
		}
	}
	
	
	public void validatePayment(PagoDto dto) throws InvoiceManagerException {
		checkNotNull(dto.getAcredor(), "Acredor");
		checkNotNull(dto.getBanco(), "Banco");
		checkNotNull(dto.getCuenta(), "Cuenta");
		checkNotNull(dto.getDeudor(), "Deudor");
		checkNotNull(dto.getFechaPago(), "Fecha de pago");
		checkNotNull(dto.getFolio(), "Folio factura");
		checkNotNull(dto.getFormaPago(), "Forma de pago");
		checkNotNull(dto.getMoneda(), "Moneda");
		checkNotNull(dto.getMonto(), "Monto");
		checkNotNull(dto.getSolicitante(), "Solicitante");
		checkNotNull(dto.getStatusPago(), "Estatus de pago");
		checkNotNull(dto.getTipoDeCambio(), "Tipo de cambio");
	}
	
	
}
