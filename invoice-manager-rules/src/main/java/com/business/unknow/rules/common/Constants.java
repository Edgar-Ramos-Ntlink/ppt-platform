package com.business.unknow.rules.common;

/**
 * @author eej000f
 *
 */
public class Constants {

	public class Prevalidations {
		public static final String PREVALIDATION_SUITE = "PrevalidationSuite";
		
		public static final String FACTURA_PADRE_COMPLEMENTO = "FacturaPadreComplemento";
		public static final String FACTURA_PADRE_COMPLEMENTO_RULE = "FacturaPadreComplementoRule";
		public static final String FACTURA_PADRE_COMPLEMENTO_RULE_DESC = "La factura padre no cumple los requerimientos minimos para poder tener complementos";

		public static final String FACTURA_PADRE_PAGOS = "FacturaPadrePagos";
		public static final String FACTURA_PADRE_PAGOS_RULE = "FacturaPadrePagosRule";
		public static final String FACTURA_PADRE_PAGOS_RULE_DESC = "Existe una inconsitencia con los pagos";

		public static final String FACTURA_PADRE_STATUS = "FacturaPadrePagos";
		public static final String FACTURA_PADRE_STATUS_RULE = "FacturaPadrePagosRule";
		public static final String FACTURA_PADRE_STATUS_RULE_DESC = "Los estatus de la factura padre no son correctos";
	}

	public class FacturaSuite {
		public static final String FACTURAR_SUITE = "FacturarSuite";
		
		public static final String FACTURA_STATUS = "FacturaStatus";
		public static final String FACTURA_STATUS_RULE = "FacturaStatusRule";
		public static final String FACTURA_STATUS_RULE_DESC = "La estatus de la factura no es correcta.";
		
		public static final String FACTURA_DATOS_VALIDATION = "FacturaDatosValidation";
		public static final String FACTURA_DATOS_VALIDATION_RULE = "FacturaDatosValidationRule";
		public static final String FACTURA_DATOS_VALIDATION_RULE_DESC = "Los datos de la factura tienen una inconsistencia.";
		
		public static final String FACTURA_PADRE_STATUS = "FacturaPadreStatusValidation";
		public static final String FACTURA_PADRE_STATUS_RULE = "FacturaPadreStatusValidationRule";
		public static final String FACTURA_PADRE_STATUS_RULE_DESC = "Los datos de la factura padre tienen una inconsistencia.";
		
		public static final String FACTURA_PAGO_VALIDATION = "FacturaPagoValidation";
		public static final String FACTURA_PAGO_VALIDATION_RULE = "FacturaPagoValidationRule";
		public static final String FACTURA_PAGO_VALIDATION_RULE_DES = "Los datos de la factura padre tienen una inconsistencia.";
	}
	
	public class CancelacionSuite {
		public static final String CANCELAR_SUITE = "CancelarSuite";
		
		public static final String CANCELAR_STATUS_VALIDATION = "StatusCancelarValidation";
		public static final String CANCELAR_STATUS_VALIDATION_RULE = "StatusCancelarValidationRule";
		public static final String CANCELAR_STATUS_VALIDATION_RULE_DESC = "Los status de la facura son incorrectos.";
		
		
	}
}
