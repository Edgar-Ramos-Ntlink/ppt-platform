package com.business.unknow.services.mapper.decorator;

import com.business.unknow.Constants.FacturaConstants;
import com.business.unknow.model.cfdi.Cfdi;
import com.business.unknow.model.cfdi.Concepto;
import com.business.unknow.model.cfdi.ConceptoImpuesto;
import com.business.unknow.model.cfdi.Retencion;
import com.business.unknow.model.cfdi.Translado;
import com.business.unknow.model.dto.FacturaDto;
import com.business.unknow.model.dto.cfdi.CfdiDto;
import com.business.unknow.model.dto.cfdi.ConceptoDto;
import com.business.unknow.model.dto.cfdi.ImpuestoDto;
import com.business.unknow.model.dto.cfdi.RetencionDto;
import com.business.unknow.model.dto.services.EmpresaDto;
import com.business.unknow.services.mapper.factura.FacturaCfdiTranslatorMapper;
import com.business.unknow.services.util.helpers.DateHelper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class FacturaCfdiTranslatorDecorator implements FacturaCfdiTranslatorMapper {

  @Autowired private FacturaCfdiTranslatorMapper delegate;

  private DateHelper dateHelper = new DateHelper();

  @Override
  public Cfdi complementoRootInfo(CfdiDto cfdiDto, EmpresaDto empresaDto) {
    Cfdi cfdi = delegate.complementoRootInfo(cfdiDto, empresaDto);
    cfdi.setTotal(cfdi.getTotal().setScale(0, RoundingMode.DOWN));
    cfdi.setSubtotal(cfdi.getSubtotal().setScale(0, RoundingMode.DOWN));
    return cfdi;
  }

  @Override
  public Cfdi cdfiRootInfo(FacturaDto facturaDto, EmpresaDto empresaDto) {
    Cfdi cfdi = delegate.cdfiRootInfo(facturaDto, empresaDto);
    if (cfdi.getMoneda().equals("MXN")) {
      cfdi.setTipoCambio(BigDecimal.ONE);
    }
    cfdi.setFecha(dateHelper.getStringFromFecha(new Date(), FacturaConstants.FACTURA_DATE_FORMAT));
    return cfdi;
  }

  @Override
  public Concepto cfdiConcepto(ConceptoDto dto) {
    Concepto conpeto = delegate.cfdiConcepto(dto);
    ConceptoImpuesto impuesto = new ConceptoImpuesto();
    impuesto.setTranslados(new ArrayList<>());
    impuesto.setRetenciones(new ArrayList<>());
    if (!dto.getImpuestos().isEmpty()) {
      for (ImpuestoDto impuestoDto : dto.getImpuestos()) {
        Translado traslado = delegate.cfdiImpuesto(impuestoDto);
        traslado.setTasaOCuota(
            String.format(Locale.US, "%05f", Double.valueOf(traslado.getTasaOCuota())));
        impuesto.getTranslados().add(traslado);
      }
    } else {
      impuesto.setTranslados(null);
    }
    if (!dto.getRetenciones().isEmpty()) {
      for (RetencionDto retencionDto : dto.getRetenciones()) {
        Retencion reten = delegate.cfdiRetencion(retencionDto);
        reten.setTasaOCuota(
            String.format(Locale.US, "%05f", Double.valueOf(reten.getTasaOCuota())));
        impuesto.getRetenciones().add(reten);
      }
    } else {
      impuesto.setRetenciones(null);
    }
    if (!dto.getImpuestos().isEmpty() || !dto.getRetenciones().isEmpty()) {
      conpeto.setImpuestos(impuesto);
    }
    return conpeto;
  }

  @Override
  public Concepto complementoConcepto(ConceptoDto dto) {
    Concepto conpeto = delegate.complementoConcepto(dto);
    conpeto.setCantidad(conpeto.getCantidad().setScale(0, RoundingMode.DOWN));
    conpeto.setValorUnitario(conpeto.getValorUnitario().setScale(0, RoundingMode.DOWN));
    conpeto.setImporte(conpeto.getImporte().setScale(0, RoundingMode.DOWN));
    return conpeto;
  }
}
