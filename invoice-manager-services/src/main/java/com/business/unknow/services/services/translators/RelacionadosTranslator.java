package com.business.unknow.services.services.translators;

import com.business.unknow.Constants.FacturaSustitucionConstants;
import com.business.unknow.enums.FacturaStatusEnum;
import com.business.unknow.enums.LineaEmpresaEnum;
import com.business.unknow.enums.MetodosPagoEnum;
import com.business.unknow.enums.TipoDocumentoEnum;
import com.business.unknow.model.dto.FacturaDto;
import com.google.common.collect.ImmutableList;
import com.mx.ntlink.cfdi.modelos.CfdiRelacionado;
import com.mx.ntlink.cfdi.modelos.CfdiRelacionados;
import com.mx.ntlink.cfdi.modelos.Concepto;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RelacionadosTranslator {

  public FacturaDto sustitucionComplemento(FacturaDto facturaDto) {
    if (!facturaDto.getStatusFactura().equals(FacturaStatusEnum.TIMBRADA.getValor())) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format(
              "La factura con el pre-folio %s no esta TIMBRADA y no se puede sustituir",
              facturaDto.getPreFolio()));
    }
    updateBaseInfoSustitucion(facturaDto);
    facturaDto.setValidacionTeso(true);
    return facturaDto;
  }

  public FacturaDto sustitucionFactura(FacturaDto facturaDto) {
    if (!facturaDto.getStatusFactura().equals(FacturaStatusEnum.TIMBRADA.getValor())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          String.format(
              "La factura con el pre-folio %s no esta TIMBRADA y no se puede sustituir",
              facturaDto.getPreFolio()));
    }
    updateBaseInfoSustitucion(facturaDto);
    return facturaDto;
  }

  private void updateBaseInfoSustitucion(FacturaDto facturaDto) {
    facturaDto.setCadenaOriginalTimbrado(null);
    facturaDto.setFechaTimbrado(null);
    facturaDto.setFolio(null);
    facturaDto.setValidacionOper(false);
    facturaDto.setValidacionTeso(false);
    facturaDto.setNotas("");
    facturaDto.setPreFolio("");
    facturaDto.setSelloCfd(null);
    if (facturaDto.getLineaEmisor().equals(LineaEmpresaEnum.A.name())) {
      if (facturaDto.getMetodoPago().equals(MetodosPagoEnum.PPD.name())) {
        facturaDto.setStatusFactura(FacturaStatusEnum.VALIDACION_OPERACIONES.getValor());
      } else {
        facturaDto.setStatusFactura(FacturaStatusEnum.VALIDACION_TESORERIA.getValor());
      }

    } else {
      facturaDto.setStatusFactura(FacturaStatusEnum.POR_TIMBRAR_CONTABILIDAD.getValor());
    }
    facturaDto.setId(0);
    if (facturaDto.getCfdi() != null) {
      // TODO validate Complemento Pago
      /* if (facturaDto.getCfdi().getComplemento() != null
          && facturaDto.getCfdi().getComplemento().getPagos() != null) {
        for (CfdiPagoDto cfdiPagoDto : facturaDto.getCfdi().getComplemento().getPagos()) {
          cfdiPagoDto.setId(0);
        }
      }*/

      CfdiRelacionado relacionado =
          CfdiRelacionado.builder().tipoRelacion("004").uuid(facturaDto.getUuid()).build();
      facturaDto
          .getCfdi()
          .setCfdiRelacionados(
              CfdiRelacionados.builder().cfdiRelacionado(ImmutableList.of(relacionado)).build());
      facturaDto.setUuid(null);
    }
  }

  public FacturaDto notaCreditoFactura(FacturaDto facturaDto) {
    if (facturaDto.getStatusFactura().equals(FacturaStatusEnum.TIMBRADA.getValor())) {
      updateBaseInfoNotaCredito(facturaDto);
      return facturaDto;
    } else {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          String.format(
              "La factura con el pre-folio %s no esta timbrada y no puede tener nota de credito",
              facturaDto.getPreFolio()));
    }
  }

  private void updateBaseInfoNotaCredito(FacturaDto facturaDto) {
    facturaDto.setCadenaOriginalTimbrado(null);
    facturaDto.setFechaTimbrado(null);
    facturaDto.setFolio(null);
    facturaDto.setTipoDocumento(TipoDocumentoEnum.NOTA_CREDITO.getDescripcion());
    facturaDto.setTotal(BigDecimal.ZERO);
    facturaDto.setSaldoPendiente(BigDecimal.ZERO);
    facturaDto.setValidacionOper(false);
    facturaDto.setValidacionTeso(true);
    facturaDto.setNotas("");
    facturaDto.setPreFolio("");
    facturaDto.setSelloCfd(null);
    facturaDto.setStatusFactura(1);
    facturaDto.setId(0);
    if (facturaDto.getCfdi() != null) {
      facturaDto.getCfdi().getImpuestos().stream()
          .findFirst()
          .get()
          .setTotalImpuestosRetenidos(BigDecimal.ZERO);
      facturaDto.getCfdi().getImpuestos().stream()
          .findFirst()
          .get()
          .setTotalImpuestosTrasladados(BigDecimal.ZERO);
      facturaDto.getCfdi().setSubtotal(BigDecimal.ZERO);
      facturaDto.getCfdi().setTotal(BigDecimal.ZERO);
      facturaDto.getCfdi().setTipoDeComprobante("E");
      if (facturaDto.getCfdi().getReceptor() != null) {
        facturaDto
            .getCfdi()
            .getReceptor()
            .setUsoCfdi(FacturaSustitucionConstants.NOTA_CREDITO_USO_CFDI);
      }
      Concepto concepto =
          Concepto.builder()
              .cantidad(new BigDecimal(1))
              .claveProdServ(FacturaSustitucionConstants.NOTA_CREDITO_CLAVE_CONCEPTO)
              .descripcion(FacturaSustitucionConstants.NOTA_CREDITO_DESC_CONCEPTO)
              .claveUnidad(FacturaSustitucionConstants.NOTA_CREDITO_CLAVE_UNIDAD)
              .valorUnitario(BigDecimal.ZERO)
              .importe(BigDecimal.ZERO)
              .descuento(BigDecimal.ZERO)
              .build();
      facturaDto.getCfdi().setConceptos(new ArrayList<>());
      facturaDto.getCfdi().getConceptos().add(concepto);
      facturaDto.getCfdi().setComplemento(null);
    }
    CfdiRelacionado relacionado =
        CfdiRelacionado.builder().uuid(facturaDto.getUuid()).tipoRelacion("01").build();
    facturaDto
        .getCfdi()
        .setCfdiRelacionados(
            CfdiRelacionados.builder().cfdiRelacionado(ImmutableList.of(relacionado)).build());
    facturaDto.setUuid(null);
  }
}
