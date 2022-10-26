package com.business.unknow.services.util;

import static com.business.unknow.Constants.DATE_PRE_FOLIO_GENERIC_FORMAT;

import com.business.unknow.Constants;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.FacturaPdf;
import com.business.unknow.model.error.InvoiceManagerException;
import com.google.common.io.Resources;
import com.mx.ntlink.NtlinkUtilException;
import com.mx.ntlink.util.CfdiNamespaceMapper;
import com.mx.ntlink.util.PdfGeneratorUtil;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class FacturaUtils {

  private static final String EXP = "&amp;";

  public static String generatePreFolio(Integer amount) {
    return String.format(
        "%s-%s",
        LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_PRE_FOLIO_GENERIC_FORMAT)),
        String.format("%05d", amount + 1));
  }

  public static String generateFolio() {
    return LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern(Constants.DATE_FOLIO_GENERIC_FORMAT));
  }

  public static String getInvoiceExpression(FacturaCustom facturaCustom) {
    String expresion =
        String.format(
            "https://verificacfdi.facturaelectronica.sat.gob.mx/default.aspx?%sid=%s%sre=%s%srr=%s%stt=%s%sfe=%s",
            EXP,
            facturaCustom.getUuid(),
            EXP,
            facturaCustom.getRfcEmisor(),
            EXP,
            facturaCustom.getRfcRemitente(),
            EXP,
            facturaCustom.getTotal(),
            EXP,
            facturaCustom.getSelloCfd());
    return expresion;
  }

  public static byte[] generateFacturaPdf(FacturaPdf facturaPdf, String type)
      throws InvoiceManagerException {
    try {
      String template =
          Resources.toString(
              FacturaUtils.class.getClassLoader().getResource(type), StandardCharsets.UTF_8);
      StringWriter sw = new StringWriter();
      JAXBContext jaxbContext = JAXBContext.newInstance(FacturaPdf.class);
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      jaxbMarshaller.setProperty(
          "com.sun.xml.bind.namespacePrefixMapper", new CfdiNamespaceMapper());
      jaxbMarshaller.marshal(facturaPdf, sw);
      String xmlString =
          Base64.getEncoder().encodeToString(sw.toString().getBytes(StandardCharsets.UTF_8));
      String pdf =
          PdfGeneratorUtil.generatePdf(
              Base64.getEncoder().encodeToString(template.getBytes()), "XSD", xmlString);
      return Base64.getDecoder().decode(pdf);
    } catch (JAXBException | NtlinkUtilException | IOException e) {
      log.error("Error en la generacion del PDF", e);
      throw new InvoiceManagerException(
          "Error en la generación del documento PDF", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }
}
