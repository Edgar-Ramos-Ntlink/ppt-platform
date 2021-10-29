package com.business.unknow.services.util.helpers;

import com.business.unknow.model.cfdi.Cfdi;
import com.business.unknow.model.dto.FacturaPdfModelDto;
import com.business.unknow.model.error.InvoiceCommonException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class FacturaHelper {

  public String facturaCfdiToXml(Cfdi cfdi) throws InvoiceCommonException {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(Cfdi.class);
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
      jaxbMarshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new MyNamespaceMapper());
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      jaxbMarshaller.setProperty(
          Marshaller.JAXB_SCHEMA_LOCATION,
          "http://www.sat.gob.mx/cfd/3 http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd http://www.sat.gob.mx/Pagos http://www.sat.gob.mx/sitio_internet/cfd/Pagos/Pagos10.xsd");
      StringWriter sw = new StringWriter();
      jaxbMarshaller.marshal(cfdi, sw);
      return sw.toString();
    } catch (JAXBException e) {
      throw new InvoiceCommonException(e.getMessage());
    }
  }

  public String facturaPdfToXml(FacturaPdfModelDto modelDto) throws InvoiceCommonException {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(FacturaPdfModelDto.class);
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
      jaxbMarshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new MyNamespaceMapper());
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      jaxbMarshaller.setProperty(
          Marshaller.JAXB_SCHEMA_LOCATION,
          "http://www.sat.gob.mx/cfd/3 http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd");
      StringWriter sw = new StringWriter();
      jaxbMarshaller.marshal(modelDto, sw);
      return sw.toString();
    } catch (JAXBException e) {
      e.printStackTrace();
      throw new InvoiceCommonException(e.getMessage());
    }
  }

  public Cfdi getFacturaFromString(String xml) throws InvoiceCommonException {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(Cfdi.class);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      StringReader reader = new StringReader(xml);
      return (Cfdi) unmarshaller.unmarshal(reader);
    } catch (JAXBException e) {
      e.printStackTrace();
      throw new InvoiceCommonException(e.getMessage());
    }
  }
}
