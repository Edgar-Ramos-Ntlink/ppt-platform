package com.business.unknow.services.services;

import com.amazonaws.util.Base64;
import com.business.unknow.enums.S3BucketsEnum;
import com.business.unknow.enums.TipoArchivoEnum;
import com.business.unknow.model.dto.files.FacturaFileDto;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.entities.ResourceFile;
import com.business.unknow.services.mapper.ResourceFileMapper;
import com.business.unknow.services.repositories.files.ResourceFileRepository;
import com.mx.ntlink.NtlinkUtilException;
import com.mx.ntlink.aws.S3Utils;
import com.mx.ntlink.cfdi.mappers.CfdiMapper;
import com.mx.ntlink.cfdi.modelos.Cfdi;
import com.mx.ntlink.models.generated.Comprobante;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class FilesService {

  @Autowired private ResourceFileRepository resourceFileRepository;

  @Autowired private ResourceFileMapper resourceFileMapper;

  // TODO VALIDAR ELIMINACION
  @Autowired private S3FileService s3FileService;

  @Autowired private S3Utils s3Utils;

  @Autowired private CfdiMapper cfdiMapper;

  @Value("${s3.bucket}")
  private String s3Bucket;

  @Value("${s3.path}")
  private String s3Path;

  @Value("classpath:/images/imagen-no-disponible.png")
  private Resource noAvailableImage;

  /**
   * Saves xml in S3 bucket
   *
   * @param name
   * @param {@link Comprobante}
   */
  public void sendXmlToS3(String name, Comprobante comprobante) {
    log.info("Saving {}.xml to S3", name);
    try {
      JAXBContext contextObj = JAXBContext.newInstance(Comprobante.class);
      Marshaller marshallerObj = contextObj.createMarshaller();
      marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      ByteArrayOutputStream xmlStream = new ByteArrayOutputStream();
      marshallerObj.marshal(comprobante, xmlStream);
      s3Utils.upsertFile(s3Bucket, s3Path, name.concat(".xml"), xmlStream.toByteArray());
    } catch (JAXBException | NtlinkUtilException e) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          String.format("Error saving Xml file in S3 with folio %s", comprobante.getFolio()));
    }
  }

  /**
   * Gets Cfdi from xml in S3 bucket
   *
   * @param folio
   * @return @link Cfdi}
   * @throws @link NtlinkUtilException}
   */
  public Cfdi getCfdiFromS3(String folio) throws NtlinkUtilException {
    try {
      JAXBContext contextObj = JAXBContext.newInstance(Comprobante.class);
      Unmarshaller unmarshaller = contextObj.createUnmarshaller();
      StringReader decodedString =
          new StringReader(
              new String(
                  org.apache.commons.ssl.Base64.decodeBase64(
                      s3Utils
                          .getFile(s3Bucket, s3Path, folio.concat(".xml"))
                          .getBytes(StandardCharsets.UTF_8))));
      Comprobante comprobante = (Comprobante) unmarshaller.unmarshal(decodedString);
      return cfdiMapper.comprobanteToCfdi(comprobante);
    } catch (JAXBException e) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, String.format("Error getting Xml file in S3 with folio %s", folio));
    }
  }

  public void deleteCfdiFromS3(String folio) throws NtlinkUtilException {
    s3Utils.deleteFile(s3Bucket, s3Path, folio.concat(".xml"));
  }

  public FacturaFileDto getFacturaFileByFolioAndType(String folio, String type)
      throws InvoiceManagerException {
    try {
      String data =
          s3FileService.getS3File(
              S3BucketsEnum.CFDIS, folio.concat(TipoArchivoEnum.valueOf(type).getFormat()));

      FacturaFileDto fileDto = new FacturaFileDto();
      fileDto.setFolio(folio);
      fileDto.setData(data);
      fileDto.setTipoArchivo(type);
      return fileDto;
    } catch (Exception e) {
      throw new InvoiceManagerException(e.getMessage(), HttpStatus.CONFLICT.value());
    }
  }

  public List<ResourceFileDto> findResourcesByResourceType(
      S3BucketsEnum resourceType, String referencia) {
    List<ResourceFile> recursos =
        resourceFileRepository.findByTipoRecursoAndReferencia(resourceType.name(), referencia);
    return resourceFileMapper.getDtosFromEntities(recursos);
  }

  public ResourceFileDto getResourceFileByResourceReferenceAndType(
      S3BucketsEnum resource, String reference, String type) throws InvoiceManagerException {
    try {
      ResourceFileDto resourceFileDto = null;
      Optional<ResourceFile> entity =
          resourceFileRepository.findByTipoRecursoAndReferenciaAndTipoArchivo(
              resource.name(), reference, type);
      if (entity.isPresent()) {
        resourceFileDto = resourceFileMapper.getDtoFromEntity(entity.get());
      } else {
        throw new InvoiceManagerException(
            String.format(
                "No se encuentra el %s en los archivos de %s con la referencia : %s ",
                type, resource, reference),
            HttpStatus.NOT_FOUND.value());
      }
      String data = s3FileService.getS3File(resource, resourceFileDto.getNombre());
      resourceFileDto.setData(data);
      return resourceFileDto;
    } catch (Exception e) {
      throw new InvoiceManagerException(
          String.format(
              "No se encuentra el %s en los archivos de %s con la referencia : %s ",
              type, resource, reference),
          HttpStatus.CONFLICT.value());
    }
  }

  public ResponseEntity<byte[]> getCompanyImage(String rfc) throws IOException {

    Optional<ResourceFile> resource =
        resourceFileRepository.findByTipoRecursoAndReferenciaAndTipoArchivo(
            S3BucketsEnum.EMPRESAS.name(), rfc, "LOGO");

    HttpHeaders headers = new HttpHeaders();
    headers.setCacheControl(CacheControl.noCache().getHeaderValue());
    byte[] bytes = null;
    if (resource.isPresent()) {
      headers.setContentType(MediaType.valueOf(resource.get().getFormato().replace(";", "")));
      bytes =
          s3FileService
              .getS3InputStream(S3BucketsEnum.EMPRESAS, resource.get().getNombre())
              .readAllBytes();
    } else {
      headers.setContentType(MediaType.IMAGE_PNG);
      bytes = noAvailableImage.getInputStream().readAllBytes();
      return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
    return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
  }

  public void upsertFacturaFile(
      S3BucketsEnum bucket, String fileFormat, String name, ByteArrayOutputStream file)
      throws InvoiceManagerException {
    s3FileService.upsertS3File(bucket, name.concat(fileFormat), file);
  }

  public void upsertResourceFile(ResourceFileDto resourceFile) throws InvoiceManagerException {

    Optional<ResourceFile> file =
        resourceFileRepository.findByTipoRecursoAndReferenciaAndTipoArchivo(
            resourceFile.getTipoRecurso(),
            resourceFile.getReferencia(),
            resourceFile.getTipoArchivo());
    if (file.isPresent()) {
      resourceFileRepository.delete(file.get());
    }

    if (resourceFile.getData().indexOf(",") < resourceFile.getData().length()) {
      String fileName =
          String.format(
              "%s-%s%s",
              resourceFile.getReferencia(),
              resourceFile.getTipoArchivo(),
              resourceFile.getExtension());
      resourceFile.setNombre(fileName);
      String[] fileInfo = resourceFile.getData().split(",");
      resourceFile.setFormato(fileInfo[0].replaceFirst("data:", "").replaceFirst("base64", ""));
      byte[] decodedBytes = Base64.decode(fileInfo[1]);
      ByteArrayOutputStream baos = new ByteArrayOutputStream(decodedBytes.length);
      baos.write(decodedBytes, 0, decodedBytes.length);
      s3FileService.upsertS3File(
          S3BucketsEnum.findByValor(resourceFile.getTipoRecurso()), fileName, baos);

      resourceFileRepository.save(resourceFileMapper.getEntityFromDto(resourceFile));
    } else {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Los datos en base64 no fueron enviados correctamente");
    }
  }

  public void deleteFacturaFile(String folio, String type) throws InvoiceManagerException {
    s3FileService.deleteS3File(
        S3BucketsEnum.CFDIS, folio.concat(TipoArchivoEnum.valueOf(type).getFormat()));
  }

  public void deleteResourceFileByResourceReferenceAndType(
      String resource, String referencia, String tipoArchivo) throws InvoiceManagerException {
    Optional<ResourceFile> resourceFile =
        resourceFileRepository.findByTipoRecursoAndReferenciaAndTipoArchivo(
            resource, referencia, tipoArchivo);
    if (resourceFile.isPresent()) {
      ResourceFile file = resourceFile.get();
      s3FileService.deleteS3File(
          S3BucketsEnum.findByValor(file.getTipoRecurso()), file.getNombre());
      resourceFileRepository.delete(file);
    }
  }

  public void deleteResourceFile(Integer id) throws InvoiceManagerException {
    Optional<ResourceFile> resourceFile = resourceFileRepository.findById(id);
    if (resourceFile.isPresent()) {
      ResourceFile file = resourceFile.get();
      s3FileService.deleteS3File(
          S3BucketsEnum.findByValor(file.getTipoRecurso()), file.getNombre());
      resourceFileRepository.delete(file);
    }
  }
}
