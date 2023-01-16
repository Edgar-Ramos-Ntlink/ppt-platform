package com.business.unknow.services.services;

import static com.business.unknow.Constants.ComplementoPpdDefaults.IMPUESTO;
import static com.business.unknow.Constants.ComplementoPpdDefaults.PAGO_IMPUESTOS_GRAL;
import static com.business.unknow.Constants.ComplementoPpdDefaults.TASA_O_CUOTA;
import static com.business.unknow.Constants.FacturaSustitucionConstants.FACTURA_TASA;
import static com.business.unknow.Constants.FacturaSustitucionConstants.NOTA_CREDITO_UNIDAD;
import static com.business.unknow.Constants.IVA_BASE_16;
import static com.business.unknow.Constants.IVA_IMPUESTO_16;
import static com.business.unknow.enums.TipoRelacion.NOTA_CREDITO;
import static com.business.unknow.enums.TipoRelacion.SUSTITUCION;

import com.business.unknow.Constants.FacturaSustitucionConstants;
import com.business.unknow.enums.FacturaStatus;
import com.business.unknow.enums.LineaEmpresa;
import com.business.unknow.enums.TipoDocumento;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.services.repositories.facturas.FacturaDao;
import com.business.unknow.services.util.FacturaUtils;
import com.google.common.collect.ImmutableList;
import com.unknown.cfdi.modelos.CfdiRelacionado;
import com.unknown.cfdi.modelos.CfdiRelacionados;
import com.unknown.cfdi.modelos.Concepto;
import com.unknown.cfdi.modelos.Impuesto;
import com.unknown.cfdi.modelos.ImpuestoConcepto;
import com.unknown.cfdi.modelos.Traslado;
import com.unknown.cfdi.modelos.TrasladoConcepto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    facturaCustom.setTipoRelacion(SUSTITUCION.getId());
    facturaCustom.setRelacion(facturaCustom.getUuid());
    assignRelationBaseData(facturaCustom, folio);
    if (facturaCustom.getLineaEmisor().equals(LineaEmpresa.A.name())) {
      facturaCustom.setStatusFactura(FacturaStatus.POR_TIMBRAR.getValor());
    } else {
      facturaCustom.setStatusFactura(FacturaStatus.POR_TIMBRAR_CONTABILIDAD.getValor());
    }
    facturaCustom.setId(0);
    return facturaCustom;
  }

  public FacturaCustom notaCreditoFactura(FacturaCustom facturaCustom, String folio) {
    CfdiRelacionado relacionado =
        CfdiRelacionado.builder()
            .tipoRelacion(NOTA_CREDITO.getId())
            .uuid(facturaCustom.getUuid())
            .build();
    facturaCustom
        .getCfdi()
        .setCfdiRelacionados(
            CfdiRelacionados.builder()
                .tipoRelacion(NOTA_CREDITO.getId())
                .cfdiRelacionado(ImmutableList.of(relacionado))
                .build());
    facturaCustom.setTipoRelacion(NOTA_CREDITO.getId());
    facturaCustom.setRelacion(facturaCustom.getUuid());
    assignRelationBaseData(facturaCustom, folio);
    BigDecimal impuesto =
        facturaCustom
            .getSaldoPendiente()
            .multiply(BigDecimal.valueOf(IVA_IMPUESTO_16))
            .divide(BigDecimal.valueOf(IVA_BASE_16), 2, RoundingMode.HALF_UP);
    BigDecimal base =
        facturaCustom.getSaldoPendiente().subtract(impuesto).setScale(2, RoundingMode.HALF_UP);

    facturaCustom.setTipoDocumento(TipoDocumento.NOTA_CREDITO.getDescripcion());
    facturaCustom.setTotal(facturaCustom.getSaldoPendiente());
    facturaCustom.getCfdi().setTotal(facturaCustom.getSaldoPendiente());
    facturaCustom.getCfdi().setSubtotal(base);
    facturaCustom.setSaldoPendiente(BigDecimal.ZERO);
    facturaCustom.getCfdi().setTipoDeComprobante("E");
    facturaCustom.setStatusFactura(FacturaStatus.VALIDACION_OPERACIONES.getValor());
    facturaCustom
        .getCfdi()
        .getReceptor()
        .setUsoCfdi(FacturaSustitucionConstants.NOTA_CREDITO_USO_CFDI);
    facturaCustom.setValidacionTeso(true);
    TrasladoConcepto trasladoConcepto =
        TrasladoConcepto.builder()
            .base(base)
            .impuesto(IMPUESTO)
            .tipoFactor(FACTURA_TASA)
            .tasaOCuota(TASA_O_CUOTA)
            .importe(impuesto)
            .build();
    ImpuestoConcepto impuestoConcepto =
        ImpuestoConcepto.builder().traslados(ImmutableList.of(trasladoConcepto)).build();
    Concepto concepto =
        Concepto.builder()
            .cantidad(new BigDecimal(1))
            .claveProdServ(FacturaSustitucionConstants.NOTA_CREDITO_CLAVE_CONCEPTO)
            .descripcion(FacturaSustitucionConstants.NOTA_CREDITO_DESC_CONCEPTO)
            .claveUnidad(FacturaSustitucionConstants.NOTA_CREDITO_CLAVE_UNIDAD)
            .unidad(NOTA_CREDITO_UNIDAD)
            .objetoImp(PAGO_IMPUESTOS_GRAL)
            .valorUnitario(base)
            .importe(base)
            .descuento(BigDecimal.ZERO)
            .impuestos(ImmutableList.of(impuestoConcepto))
            .build();
    facturaCustom.getCfdi().setConceptos(ImmutableList.of(concepto));
    Traslado traslado =
        Traslado.builder()
            .base(base)
            .impuesto(IMPUESTO)
            .tasaOCuota(TASA_O_CUOTA)
            .importe(impuesto)
            .tipoFactor(FACTURA_TASA)
            .build();
    Impuesto impuestoSub =
        Impuesto.builder()
            .totalImpuestosTrasladados(impuesto)
            .totalImpuestosRetenidos(BigDecimal.ZERO)
            .traslados(ImmutableList.of(traslado))
            .build();
    facturaCustom.getCfdi().setImpuestos(ImmutableList.of(impuestoSub));
    facturaCustom.setId(0);
    return facturaCustom;
  }

  private void assignRelationBaseData(FacturaCustom facturaCustom, String folio) {
    facturaCustom.setUuid(null);
    facturaCustom.getCfdi().setCertificado(null);
    facturaCustom.getCfdi().setNoCertificado(null);
    facturaCustom.getCfdi().setSello(null);
    facturaCustom.setFolioRelacionado(null);
    facturaCustom.setFolioRelacionadoPadre(facturaCustom.getFolio());
    facturaCustom.setCadenaOriginalTimbrado(null);
    facturaCustom.setFechaTimbrado(null);
    facturaCustom.setFolio(folio);
    facturaCustom.setPagos(null);
    facturaCustom.setValidacionOper(false);
    facturaCustom.setValidacionTeso(false);
    facturaCustom.setNotas("");
    facturaCustom.setPreFolio(
        FacturaUtils.generatePreFolio(
            facturaDao.getCantidadFacturasOfTheCurrentMonthByTipoDocumento()));
    facturaCustom.setSelloCfd(null);
  }
}
