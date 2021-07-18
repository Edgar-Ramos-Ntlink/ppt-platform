package com.business.unknow.enums;

public enum TipoDocumentoEnum {

	FACTURA("Factura"),
	COMPLEMENTO("Complemento"),
	PREGUNTAR("PREGUNTAR"),
	NOTA_CREDITO("NotaDeCredito"),
	NOT_VALID("NOT_VALID");

	private String descripcion;

	private TipoDocumentoEnum( String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public static TipoDocumentoEnum findByDesc(String nombre) {
		for (TipoDocumentoEnum v : values()) {
			if (v.getDescripcion().equals(nombre)) {
				return v;
			}
		}
		return NOT_VALID;
	}
}
