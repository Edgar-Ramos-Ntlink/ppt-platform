package com.business.unknow;

public class MailConstants {

  public static final String STAMP_INVOICE_BODY_MESSAGE =
      "<p style=\"font-family: arial, sans-serif;\">"
          + "Buen dia %s, le compartimos su %s con el folio:%s </p>"
          + "<p style=\"font-family: arial, sans-serif;</p>\">";

  public static final String STAMP_INVOICE_SUBJECT = "Su %s con folio %s ha sido timbrado";

  public static final String SUPPORT_REQUEST_SUBJECT = "Solicitud de soporte no %d";

  public static final String SUPPORT_REQUEST_BODY_MESSAGE =
      "<p style=\"font-family: arial, sans-serif;\">"
          + "Su solicitud de soporte ha sido modificada : </p>"
          + "<br> <strong> Folio : %d</strong> <br>"
          + "<br> <strong> Nombre contacto : %s</strong> <br>"
          + "<br> <strong> Correo :  %s</strong> <br>"
          + "<br> <strong> Estatus :  %s</strong> <br>"
          + "<br> <strong> Producto :  %s</strong> <br>"
          + "<br> <strong> Descripción problema</strong><br>"
          + "<p style=\"font-family: arial, sans-serif;\">%s</p>"
          + "<br> <strong>Solución</strong><br>"
          + "<p style=\"font-family: arial, sans-serif;\">%s</p>"
          + "<br> <strong>Notas</strong><br>"
          + "<p style=\"font-family: arial, sans-serif;\">%s</p>"
          + "<br> <strong> Agente :  %s</strong> <br>";
}
