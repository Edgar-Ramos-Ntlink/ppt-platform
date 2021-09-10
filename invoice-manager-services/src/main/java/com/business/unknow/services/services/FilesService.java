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
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FilesService {

  @Autowired private ResourceFileRepository resourceFileRepository;

  @Autowired private ResourceFileMapper resourceFileMapper;

  @Autowired private S3FileService s3FileService;

  public FacturaFileDto getFacturaFileByFolioAndType(String folio, String type)
      throws InvoiceManagerException {
    try {
      String data =
          s3FileService.getS3File(
              S3BucketsEnum.CFDIS, TipoArchivoEnum.valueOf(type).getFormat(), folio);
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
                "Error obteniendo el recurso %s de la referencia %s del tipo %s ",
                resource, reference, type),
            HttpStatus.CONFLICT.value());
      }
      String data =
          s3FileService.getS3File(
              resource, resourceFileDto.getExtension(), resourceFileDto.getReferencia());
      resourceFileDto.setData(data);
      return resourceFileDto;
    } catch (Exception e) {
      throw new InvoiceManagerException(
          String.format(
              "Error obteniendo el recurso %s de la referencia %s del tipo %s con el error:%s",
              resource, reference, type, e.getMessage()),
          HttpStatus.CONFLICT.value());
    }
  }

  public void upsertFacturaFile(
      S3BucketsEnum bucket, String fileFormat, String name, ByteArrayOutputStream file)
      throws InvoiceManagerException {
    s3FileService.upsertS3File(bucket, fileFormat, name, file);
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
      String[] fileInfo = resourceFile.getData().split(",");
      resourceFile.setFormato(fileInfo[0].replaceFirst("data:", "").replaceFirst("base64", ""));
      byte[] decodedBytes = Base64.decode(fileInfo[1]);
      ByteArrayOutputStream baos = new ByteArrayOutputStream(decodedBytes.length);
      baos.write(decodedBytes, 0, decodedBytes.length);
      s3FileService.upsertS3File(
          S3BucketsEnum.findByValor(resourceFile.getTipoRecurso()),
          resourceFile.getExtension(),
          resourceFile.getReferencia(),
          baos);
      resourceFileRepository.save(resourceFileMapper.getEntityFromDto(resourceFile));
    } else {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Los datos en base64 no fueron enviados correctamente");
    }
  }

  public void deleteFacturaFile(String folio, String type) throws InvoiceManagerException {
    s3FileService.deleteS3File(
        S3BucketsEnum.CFDIS, TipoArchivoEnum.valueOf(type).getFormat(), folio);
  }

  public void deleteResourceFileByResourceReferenceAndType(
      String resource, String referencia, String tipoArchivo) throws InvoiceManagerException {
    Optional<ResourceFile> resourceFile =
        resourceFileRepository.findByTipoRecursoAndReferenciaAndTipoArchivo(
            resource, referencia, tipoArchivo);
    if (resourceFile.isPresent()) {
      ResourceFile file = resourceFile.get();
      s3FileService.deleteS3File(
          S3BucketsEnum.findByValor(file.getTipoRecurso()),
          file.getExtension(),
          file.getReferencia());
      resourceFileRepository.delete(file);
    }
  }

  public void deleteResourceFile(Integer id) throws InvoiceManagerException {
    Optional<ResourceFile> resourceFile = resourceFileRepository.findById(id);
    if (resourceFile.isPresent()) {
      ResourceFile file = resourceFile.get();
      s3FileService.deleteS3File(
          S3BucketsEnum.findByValor(file.getTipoRecurso()),
          TipoArchivoEnum.valueOf(file.getTipoArchivo()).getFormat(),
          file.getReferencia());
      resourceFileRepository.delete(file);
    }
  }
}
