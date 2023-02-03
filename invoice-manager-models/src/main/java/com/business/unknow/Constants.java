package com.business.unknow;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class Constants {

  public static final String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
  public static final String JSON_DAY_FORMAT = "yyyy-MM-dd";
  public static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern(JSON_DATE_FORMAT);
  public static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat(JSON_DATE_FORMAT);
  public static final String JSON_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String DATE_FOLIO_GENERIC_FORMAT = "yyyySSSMMhhssddmm";
  public static final String DATE_PRE_FOLIO_GENERIC_FORMAT = "MMyy";

  public static final String CANCEL_ACK = "CANCEL_ACK";

  public static final String MXN = "MXN";
  public static final String CFDI_40_VERSION = "4.0";
  public static final String CFDI_DATE_PATTERN = "%fecha-timbrado%";

  public static final Integer BAD_REQUEST = 400;
  public static final Integer SELLO_CFDI_SIZE = 8;
  public static final Integer IVA_IMPUESTO_16 = 16;
  public static final Integer IVA_BASE_16 = 116;

  public static final String PDF_FACTURA_SIN_TIMBRAR = "pdf-config/factura-sin-timbrar.xml";
  public static final String PDF_FACTURA_TIMBRAR = "pdf-config/factura-timbrada.xml";
  public static final String PDF_COMPLEMENTO_TIMBRAR = "pdf-config/complemento-timbrado.xml";
  public static final String PDF_COMPLEMENTO_SIN_TIMBRAR = "pdf-config/complemento-sin-timbrar.xml";

  public static class FacturaSustitucionConstants {

    public static final String NOTA_CREDITO_USO_CFDI = "G02";
    public static final String FACTURA_TASA = "Tasa";
    public static final String NOTA_CREDITO_CLAVE_CONCEPTO = "84111506";
    public static final String NOTA_CREDITO_CLAVE_UNIDAD = "ACT";
    public static final String NOTA_CREDITO_UNIDAD = "Actividad";
    public static final String NOTA_CREDITO_DESC_CONCEPTO = "Devolucion";
  }

  public static class PagoPpdCreditoDefaults {

    public static final String USER = "Sistema";
    public static final String FORMA_PAGO = "CREDITO";
    public static final String MONEDA = "MXN";
    public static final String STATUS_PAGO = "ACEPTADO";
  }

  public static class ComplementoPpdDefaults {

    public static final String MONEDA = "XXX";
    public static final String SERIE = "PFPC";
    public static final String METODO_PAGO = "PPD";
    public static final String FORMA_PAGO = "99";
    public static final String COMPROBANTE = "P";
    public static final String EXPORTACION = "01";
    public static final String PAGO_CLAVE = "84111506";
    public static final String PAGO_UNIDAD = "ACT";
    public static final String PAGO_IMPUESTOS = "01";
    public static final String PAGO_IMPUESTOS_GRAL = "02";
    public static final String PAGO_DESC = "Pago";
    public static final String TIPO_FACTOR = "TASA";
    public static final String IMPUESTO = "002";

    public static final BigDecimal TASA_O_CUOTA = BigDecimal.valueOf(0.16);
  }

  public static class FacturaComplemento {
    private FacturaComplemento() {}

    public static final String USO_CFDI = "CP01";
    public static final String FORMA_PAGO = "CREDITO";
    public static final String TOTAL = "Total=\"0.0\"";
  }
}
