package com.business.unknow;

import java.util.regex.Pattern;

public class MailConstants {

  public static final String NTLINK_NOTIFICATION_EMAIL = "soporte@ntlink.com.mx";

  public static final String REGISTER_SUBJECT = "Regitro exitoso, bienvenido a NT link";
  public static final String CONTACT_SUBJECT = "NT Link - Solicitud de contacto";
  public static final String REGISTER_BODY_MESSAGE =
      "<p style=\"font-family: arial, sans-serif;\">"
          + "Hola %s, bienvenido a NT link su sistema de soluciones de facturación, tu contraseña es : </p>"
          + "<br> <strong>%s</strong> <br>"
          + "<p style=\"font-family: arial, sans-serif;\">"
          + "La puedes cambiar cuando gustes en el modulo de configuraciones de usuario.</p>";

  public static final String RECOVERY_SUBJECT = "Recuperación de contraseña exitoso";
  public static final String RECOVERY_BODY_MESSAGE =
      "<p style=\"font-family: arial, sans-serif;\">"
          + "Hola %s, su contraseña ha sido reseteada, la nueva contraseña es : </p>"
          + "<br> <strong>%s</strong> <br>"
          + "<p style=\"font-family: arial, sans-serif;\">"
          + "La puedes cambiar cuando gustes en el modulo de configuraciones de usuario.</p>";

  public static final String CONTACT_BODY_MESSAGE =
      "<p style=\"font-family: arial, sans-serif;\">"
          + "Una nueva solicitud de contacto se ha creado : </p>"
          + "<br> <strong> Nombre contacto : %s</strong> <br>"
          + "<br> <strong> Correo :  %s</strong> <br>"
          + "<br> <strong> Telefono :  %s</strong> <br>"
          + "<br> <strong> Empresa que representa :  %s</strong> <br>"
          + "<br> <strong>Comentarios</strong><br>"
          + "<p style=\"font-family: arial, sans-serif;\">%s</p>";

  public static final String STAMP_INVOICE_BODY_MESSAGE =
      "<p style=\"font-family: arial, sans-serif;\">"
          + "Hola %s, NT linkTe comparte tu factura con el folio:%s </p>"
          + "<p style=\"font-family: arial, sans-serif;</p>\">";

  public static final String STAMP_INVOICE_SUBJECT = "Factura generada por NT link";

  public static final Pattern EMAIL_PATTERN =
      Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$");
}
