package com.business.unknow.services.services.executor;

import com.business.unknow.enums.LineaEmpresaEnum;
import com.business.unknow.enums.TipoArchivoEnum;
import com.business.unknow.enums.TipoEmail;
import com.business.unknow.model.config.EmailConfig;
import com.business.unknow.model.config.FileConfig;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.error.InvoiceCommonException;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.Client;
import com.business.unknow.services.mapper.factura.FacturaMapper;
import com.business.unknow.services.repositories.ClientRepository;
import com.business.unknow.services.repositories.facturas.CfdiPagoRepository;
import com.business.unknow.services.repositories.facturas.FacturaRepository;
import com.business.unknow.services.services.FilesService;
import com.business.unknow.services.services.MailService;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimbradoExecutorService {

  @Autowired private FacturaRepository repository;

  @Autowired private CfdiPagoRepository cfdiPagoRepository;

  @Autowired private ClientRepository clientRepository;

  @Autowired private FacturaMapper mapper;

  @Autowired private MailService mailService;

  @Autowired private FilesService filesService;

  public void updateFacturaAndCfdiValues(FacturaContext context) throws InvoiceManagerException {
    // TODO use timbrado utils
    /*
       repository.save(mapper.getEntityFromFacturaDto(context.getFacturaDto()));
       cfdiRepository.save(cfdiMapper.getEntityFromCfdiDto(context.getFacturaDto().getCfdi()));
       for (FacturaFileDto facturaFileDto : context.getFacturaFilesDto()) {
         if (facturaFileDto != null) {
           s3FileService.upsertS3File(
               S3BucketsEnum.CFDIS,
               context.getFacturaDto().getFolio().concat(facturaFileDto.getFileFormat().getFormat()),
               facturaFileDto.getOutputStream());
         }
       }
    */
  }

  public void sendEmail(FacturaContext context, TipoEmail tipoEmail)
      throws InvoiceManagerException {
    if (context.getFacturaDto().getLineaEmisor().equals(LineaEmpresaEnum.A.name())) {
      Client client =
          clientRepository
              .findByCorreoPromotorAndClient(
                  context.getFacturaDto().getSolicitante(),
                  context.getFacturaDto().getRfcRemitente())
              .orElseThrow(
                  () ->
                      new InvoiceManagerException(
                          "Error sending the email",
                          String.format(
                              "The client %s does not exists",
                              context.getFacturaDto().getRfcEmisor()),
                          HttpStatus.SC_CONFLICT));
      FacturaFileDto xml =
          context.getFacturaFilesDto().stream()
              .filter(a -> a.getTipoArchivo().equals(TipoArchivoEnum.XML.name()))
              .findFirst()
              .orElseThrow(
                  () ->
                      new InvoiceManagerException(
                          "Error getting XML",
                          "No se guardo el XML correctamente",
                          HttpStatus.SC_CONFLICT));
      FacturaFileDto pdf =
          context.getFacturaFilesDto().stream()
              .filter(a -> a.getTipoArchivo().equals(TipoArchivoEnum.PDF.name()))
              .findFirst()
              .orElseThrow(
                  () ->
                      new InvoiceManagerException(
                          "Error getting PDF",
                          "No se guardo el PDF correctamente",
                          HttpStatus.SC_CONFLICT));
      EmailConfig email =
          EmailConfig.builder()
              .emisor(
                  tipoEmail.equals(TipoEmail.SEMEL_JACK)
                      ? context.getEmpresaDto().getCorreo()
                      : tipoEmail.getEmail())
              .pwEmisor(
                  tipoEmail.equals(TipoEmail.SEMEL_JACK)
                      ? context.getEmpresaDto().getPwCorreo()
                      : tipoEmail.getPw())
              .asunto(String.format("Factura %s", context.getFacturaDto().getFolio()))
              .receptor(
                  ImmutableList.of(
                      client.getCorreoPromotor(),
                      client.getInformacionFiscal().getCorreo(),
                      context.getEmpresaDto().getCorreo()))
              .port(tipoEmail.getPort())
              .dominio(
                  tipoEmail.equals(TipoEmail.SEMEL_JACK)
                      ? context.getEmpresaDto().getDominioCorreo()
                      : tipoEmail.getHost())
              .archivos(
                  ImmutableList.of(
                      FileConfig.builder()
                          .tipoArchivo(TipoArchivoEnum.XML)
                          .nombre(
                              context
                                  .getFacturaDto()
                                  .getFolio()
                                  .concat(TipoArchivoEnum.XML.getFormat()))
                          .base64Content(xml.getData())
                          .build(),
                      FileConfig.builder()
                          .tipoArchivo(TipoArchivoEnum.PDF)
                          .nombre(
                              context
                                  .getFacturaDto()
                                  .getFolio()
                                  .concat(TipoArchivoEnum.PDF.getFormat()))
                          .base64Content(pdf.getData())
                          .build()))
              .cuerpo("Su factura timbrada es:")
              .build();
      try {
        mailService.sendEmail(email);
      } catch (InvoiceCommonException e) {
        e.printStackTrace();
        throw new InvoiceManagerException(
            e.getMessage(), e.getErrorMessage().getDeveloperMessage(), HttpStatus.SC_CONFLICT);
      }
    }
  }

  public void updateCanceladoValues(FacturaContext context) {
    // TODO USE TIMPRADO UTIL
    /*   repository.save(mapper.getEntityFromFacturaDto(context.getFacturaDto()));
      if (context
          .getFacturaDto()
          .getTipoDocumento()
          .equals(TipoDocumentoEnum.COMPLEMENTO.getDescripcion())) {

        if (context.getFacturaDto().getCfdi() != null
            && context.getFacturaDto().getCfdi().getComplemento() != null
            && context.getFacturaDto().getCfdi().getComplemento().getPagos() != null) {
          for (CfdiPagoDto cfdiPagoDto :
              context.getFacturaDto().getCfdi().getComplemento().getPagos()) {
            CfdiPago cfdiPago = cfdiMapper.getEntityFromCdfiPagosDto(cfdiPagoDto);
            cfdiPago.setValido(false);
            cfdiPagoRepository.save(cfdiPago);
            if (cfdiPagoDto.getImporteSaldoAnterior().compareTo(BigDecimal.ZERO) > 0) {
              Optional<Factura> factura = repository.findByFolio(cfdiPagoDto.getFolio());
              if (factura.isPresent()) {
                factura.get().setSaldoPendiente(cfdiPagoDto.getImporteSaldoAnterior());
                repository.save(factura.get());
              }
            }
          }
        }
      }
    */
  }
}
