package com.business.unknow.commons.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import com.business.unknow.Constants;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.model.factura.FacturaDto;

public class FacturaCalculator {

	private DateHelper dateHelper = new DateHelper();

	public String folioEncrypt(FacturaDto dto) throws InvoiceManagerException {
		SimpleDateFormat dt1 = new SimpleDateFormat(Constants.DATE_STANDAR_FORMAT);
		String cadena = String.format("%s|%s|%s", dto.getRfcEmisor(), dto.getRfcRemitente(),
				dt1.format(dto.getFechaCreacion()));
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(cadena.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new InvoiceManagerException(e.getMessage(), e.getCause().toString(), Constants.INTERNAL_ERROR);
		}
	}

	public void assignFolioInFacturaDtoEncrypt(FacturaDto dto) throws InvoiceManagerException {
		String folio = folioEncrypt(dto);
		dto.setFolio(folio);
		if (dto.getCfdi() != null) {
			dto.getCfdi().setFolio(folio);
		}
	}

	public void assignFolioInFacturaDto(FacturaDto dto) throws InvoiceManagerException {
		String date = dateHelper.getStringFromFecha(dto.getFechaActualizacion(), Constants.DATE_FOLIO_FORMAT);
		String emisor;
		String receptor;
		if (dto.getRfcEmisor() != null && dto.getRfcEmisor().length() > 4) {
			emisor = dto.getRfcEmisor().substring(0, 4);
		} else {
			throw new InvoiceManagerException("Error generando Folio unico",
					"El emisor es nulo o no tiene el tamaño adecuadoa", Constants.INTERNAL_ERROR);
		}
		if (dto.getRfcRemitente() != null && dto.getRfcRemitente().length() > 4) {
			receptor = dto.getRfcRemitente().substring(0, 4);
		} else {
			throw new InvoiceManagerException("Error generando Folio unico",
					"El emisor es nulo o no tiene el tamaño adecuadoa", Constants.INTERNAL_ERROR);
		}
		String folio = emisor.concat(receptor).concat(date)
				.concat(dto.getCfdi().getMetodoPago());
		dto.setFolio(folio);
		if (dto.getCfdi() != null) {
			dto.getCfdi().setFolio(folio);
		}
	}

}
