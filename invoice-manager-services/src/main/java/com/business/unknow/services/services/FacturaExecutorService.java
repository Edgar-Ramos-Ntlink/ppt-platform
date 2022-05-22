package com.business.unknow.services.services;

import static com.business.unknow.Constants.LOCAL_DATE_TIME_FORMATTER;
import static com.business.unknow.Constants.SELLO_CFDI_SIZE;
import static com.business.unknow.enums.FacturaStatus.CANCELADA;
import static com.business.unknow.enums.FacturaStatus.TIMBRADA;

import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.services.util.FacturaUtils;
import com.mx.ntlink.NtlinkUtilException;
import com.mx.ntlink.cfdi.mappers.CfdiMapper;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import com.mx.ntlink.client.NtLinkClient;
import com.mx.ntlink.error.SoapClientException;
import com.mx.ntlink.helper.CfdiTransformer;
import com.mx.ntlink.models.generated.CancelaCfdi;
import com.mx.ntlink.models.generated.Comprobante;
import com.mx.ntlink.models.generated.TimbraCfdiQrSinSello;
import com.mx.ntlink.models.generated.TimbraCfdiQrSinSelloResult;
import com.mx.ntlink.models.generated.TimbreFiscalDigital;
import java.time.LocalDateTime;
import javax.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class FacturaExecutorService {

  @Autowired private ClientService clientService;

  @Autowired private NtLinkClient ntLinkClient;

  @Autowired private CfdiMapper cfdiMapper;

  @Value("${ntlink.username}")
  private String user;

  @Value("${ntlink.password}")
  private String password;

  @Value("${ntlink.host}")
  private String host;

  public FacturaCustom stampInvoice(FacturaCustom facturaCustom) throws NtlinkUtilException {
    try {
      // TODO: USE UTC
      facturaCustom
          .getCfdi()
          .setFecha(LOCAL_DATE_TIME_FORMATTER.format(LocalDateTime.now().minusHours(6)));
      Comprobante comprobante = cfdiMapper.cfdiToComprobante(facturaCustom.getCfdi());
      TimbraCfdiQrSinSello timbraCfdiQrSinSello = new TimbraCfdiQrSinSello();
      timbraCfdiQrSinSello.setUserName(user);
      timbraCfdiQrSinSello.setPassword(password);
      timbraCfdiQrSinSello.setComprobante(CfdiTransformer.cfdiMoldelToString(comprobante));
      log.info(timbraCfdiQrSinSello.getComprobante());
      TimbraCfdiQrSinSelloResult response =
          ntLinkClient.timbrarSinSelloConQr(timbraCfdiQrSinSello).getTimbraCfdiQrSinSelloResult();
      Comprobante comprobanteResponse = CfdiTransformer.xmlToCfdiModel(response.getCfdi());
      Cfdi cfdi = cfdiMapper.comprobanteToCfdi(comprobanteResponse);
      TimbreFiscalDigital timbreFiscalDigital =
          (TimbreFiscalDigital)
              comprobanteResponse.getComplemento().getAny().stream()
                  .filter(o -> o instanceof TimbreFiscalDigital)
                  .findFirst()
                  .get();
      String selloSat = timbreFiscalDigital.getSelloCFD();
      facturaCustom =
          facturaCustom.toBuilder()
              .uuid(timbreFiscalDigital.getUUID())
              .statusFactura(TIMBRADA.getValor())
              .cadenaOriginal(response.getCadenaTimbre())
              .selloSat(selloSat)
              .xml(response.getCfdi())
              .selloCfd(selloSat.substring(selloSat.length() - SELLO_CFDI_SIZE))
              .cfdi(cfdi)
              .qr(response.getQrCodeBase64())
              .build();
      if (!response.isValido()) {
        log.error(
            String.format(
                "La factura con el folio %s tiene problemas a timbrar, razon:%s",
                facturaCustom.getFolio(), response.getDescripcionError()));
        throw new ResponseStatusException(
            org.springframework.http.HttpStatus.CONFLICT,
            String.format(
                "La factura con el folio %s tiene problemas a timbrar, razon:%s",
                facturaCustom.getFolio(), response.getDescripcionError()));
      }
      return facturaCustom;
    } catch (SoapClientException | JAXBException e) {
      log.error(
          String.format(
              "La factura con el folio %s tiene problemas a timbrar, razon:%s",
              facturaCustom.getFolio(), e.getMessage()),
          e);
      throw new ResponseStatusException(
          org.springframework.http.HttpStatus.CONFLICT,
          String.format(
              "La factura con el folio %s tiene problemas a timbrar, razon:%s",
              facturaCustom.getFolio(), e.getMessage()));
    }
  }

  public FacturaCustom cancelInvoice(FacturaCustom facturaCustom) {
    try {
      CancelaCfdi cancelaCfdi = new CancelaCfdi();
      cancelaCfdi.setUserName(user);
      cancelaCfdi.setPassword(password);
      cancelaCfdi.setRfcReceptor(facturaCustom.getRfcRemitente());
      cancelaCfdi.setRfc(facturaCustom.getRfcEmisor());
      cancelaCfdi.setUuid(facturaCustom.getUuid());
      cancelaCfdi.setMotivo(facturaCustom.getMotivo());
      cancelaCfdi.setFolioSustituto(facturaCustom.getFolioSustituto());
      cancelaCfdi.setExpresion(FacturaUtils.getInvoiceExpression(facturaCustom));
      String response = ntLinkClient.cancelarCfdi(cancelaCfdi).getCancelaCfdiResult();
      log.info(response);
      facturaCustom.setStatusFactura(CANCELADA.getValor());
      facturaCustom.setAcuse(response);
      return facturaCustom;
    } catch (SoapClientException e) {
      throw new ResponseStatusException(
          org.springframework.http.HttpStatus.CONFLICT,
          String.format(
              "La factura con el folio %s tiene problemas para cancelar:%s",
              facturaCustom.getFolio(), e.getMessage()));
    }
  }
}
