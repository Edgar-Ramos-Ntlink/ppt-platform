package com.business.unknow.services.services.builder;

import com.business.unknow.enums.S3BucketsEnum;
import com.business.unknow.enums.TipoArchivoEnum;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.files.ResourceFileDto;
import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.model.error.InvoiceManagerException;
import com.business.unknow.services.services.FilesService;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractBuilderService {

  @Autowired private FilesService service;

  public void getEmpresaFiles(EmpresaDto empresaDto, FacturaDto facturaDto)
      throws InvoiceManagerException {
    ResourceFileDto certFile =
        service.getResourceFileByResourceReferenceAndType(
            S3BucketsEnum.EMPRESAS,
            facturaDto.getRfcEmisor(),
            TipoArchivoEnum.CERT.name(),
            TipoArchivoEnum.CERT.getFormat());
    ResourceFileDto keyFile =
        service.getResourceFileByResourceReferenceAndType(
            S3BucketsEnum.EMPRESAS,
            facturaDto.getRfcEmisor(),
            TipoArchivoEnum.KEY.name(),
            TipoArchivoEnum.KEY.getFormat());
    empresaDto.setCertificado(certFile.getData());
    empresaDto.setLlavePrivada(keyFile.getData());
  }
}
