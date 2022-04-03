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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
public class FilesService {

  @Autowired private ResourceFileRepository resourceFileRepository;

  @Autowired private ResourceFileMapper resourceFileMapper;

  @Autowired private S3FileService s3FileService;

  @Value("classpath:/images/imagen-no-disponible.png")
  private Resource noAvailableImage;

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
