package com.business.unknow.services.services;

import static com.business.unknow.enums.TipoRelacion.SUSTITUCION;

import com.business.unknow.Constants.FacturaSustitucionConstants;
import com.business.unknow.enums.FacturaStatus;
import com.business.unknow.enums.LineaEmpresa;
import com.business.unknow.enums.MetodosPago;
import com.business.unknow.enums.TipoDocumento;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.services.repositories.facturas.FacturaDao;
import com.business.unknow.services.util.FacturaUtils;
import com.google.common.collect.ImmutableList;
import com.mx.ntlink.cfdi.modelos.CfdiRelacionado;
import com.mx.ntlink.cfdi.modelos.CfdiRelacionados;
import com.mx.ntlink.cfdi.modelos.Concepto;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RelationBuilderService {

  @Autowired private FacturaDao facturaDao;

  public FacturaCustom sustitucionFactura(FacturaCustom facturaCustom, String folio) {
    CfdiRelacionado relacionado =
        CfdiRelacionado.builder()
            .tipoRelacion(SUSTITUCION.getId())
            .uuid(facturaCustom.getUuid())
            .build();
    facturaCustom
        .getCfdi()
        .setCfdiRelacionados(
            CfdiRelacionados.builder()
                .tipoRelacion(SUSTITUCION.getId())
                .cfdiRelacionado(ImmutableList.of(relacionado))
                .build());
    facturaCustom.setUuid(null);
    facturaCustom.getCfdi().setCertificado(null);
    facturaCustom.getCfdi().setNoCertificado(null);
    facturaCustom.getCfdi().setSello(null);
    facturaCustom.setFolioRelacionado(null);
    facturaCustom.setFolioRelacionadoPadre(facturaCustom.getFolio());
    facturaCustom.setCadenaOriginalTimbrado(null);
    facturaCustom.setFechaTimbrado(null);
    facturaCustom.setFolio(folio);
    facturaCustom.setValidacionOper(false);
    facturaCustom.setValidacionTeso(false);
    facturaCustom.setNotas("");
    facturaCustom.setPreFolio(
        FacturaUtils.generatePreFolio(
            facturaDao.getCantidadFacturasOfTheCurrentMonthByTipoDocumento()));
    facturaCustom.setSelloCfd(null);
    if (facturaCustom.getLineaEmisor().equals(LineaEmpresa.A.name())) {
      if (facturaCustom.getMetodoPago().equals(MetodosPago.PPD.name())) {
        facturaCustom.setStatusFactura(FacturaStatus.VALIDACION_OPERACIONES.getValor());
      } else {
        facturaCustom.setStatusFactura(FacturaStatus.VALIDACION_TESORERIA.getValor());
      }

    } else {
      facturaCustom.setStatusFactura(FacturaStatus.POR_TIMBRAR_CONTABILIDAD.getValor());
    }
    facturaCustom.setId(0);
    return facturaCustom;
  }

  public FacturaCustom notaCreditoFactura(FacturaCustom facturaDto) {
    if (facturaDto.getStatusFactura().equals(FacturaStatus.TIMBRADA.getValor())) {
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

  private void updateBaseInfoNotaCredito(FacturaCustom facturaDto) {
    facturaDto.setCadenaOriginalTimbrado(null);
    facturaDto.setFechaTimbrado(null);
    facturaDto.setFolio(null);
    facturaDto.setTipoDocumento(TipoDocumento.NOTA_CREDITO.getDescripcion());
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
