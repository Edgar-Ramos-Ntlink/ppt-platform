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
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

  public ResourceFileDto getResourceFileByResourceReferenceAndType(
      S3BucketsEnum resource, String reference, String type, String format)
      throws InvoiceManagerException {
    try {
      Optional<ResourceFile> entity =
          resourceFileRepository.findByTipoRecursoAndReferenciaAndTipoArchivo(
              resource.name(), reference, type);
      ResourceFileDto resourceFileDto = new ResourceFileDto();
      resourceFileDto.setReferencia(reference);
      resourceFileDto.setTipoArchivo(resource.name());
      if (entity.isPresent()) {
        resourceFileDto.setFormato(entity.get().getFormato());
      } else {
        resourceFileDto.setFormato(format);
      }
      String data = s3FileService.getS3File(resource, resourceFileDto.getFormato(), reference);
      resourceFileDto.setData(data);
      resourceFileDto.setTipoArchivo(type);
      if (type.equals(TipoArchivoEnum.IMAGEN.name())) {
        String dataRecalculated =
            String.format(
                "data:image/%s;base64,%s",
                resourceFileDto.getFormato().replace(".", ""), resourceFileDto.getData());
        resourceFileDto.setData(dataRecalculated);
      }
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
    byte[] decodedBytes = Base64.decode(resourceFile.getData());
    ByteArrayOutputStream baos = new ByteArrayOutputStream(decodedBytes.length);
    baos.write(decodedBytes, 0, decodedBytes.length);
    s3FileService.upsertS3File(
        S3BucketsEnum.findByValor(resourceFile.getTipoRecurso()),
        resourceFile.getFormato(),
        resourceFile.getReferencia(),
        baos);
    resourceFileRepository.save(resourceFileMapper.getEntityFromDto(resourceFile));
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
          file.getFormato(),
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
