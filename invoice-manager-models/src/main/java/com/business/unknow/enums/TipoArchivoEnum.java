package com.business.unknow.enums;

public enum TipoArchivoEnum {

	XML(".xml", "text/plain"),
	QR(".png", "N/A"),
	PDF(".pdf", "application/pdf"),
	TXT(".txt", "text/plain"),
	CERT(".cer","N/A"),
	KEY(".key","N/A"),
	LOGO(".png","N/A"),
	IMAGEN("imagen","N/A"),
	NOT_VALID("NOT_VALID","NOT_VALID");

	private String format;
	private String byteArrayData;

	private TipoArchivoEnum( String format, String byteArrayData) {
		this.format = format;
		this.byteArrayData = byteArrayData;
	}


	public String getFormat() {
		return format;
	}

	public String getByteArrayData() {
		return byteArrayData;
	}
}
