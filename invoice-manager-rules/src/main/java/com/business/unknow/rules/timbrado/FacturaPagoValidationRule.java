package com.business.unknow.rules.timbrado;

import static com.business.unknow.enums.MetodosPago.PPD;
import static com.business.unknow.enums.MetodosPago.PUE;
import static com.business.unknow.enums.RevisionPagos.ACEPTADO;
import static com.business.unknow.enums.TipoDocumento.FACTURA;
import static com.business.unknow.rules.Constants.Timbrado.TIMBRADO_PAGO_VALIDATION;
import static com.business.unknow.rules.Constants.Timbrado.TIMBRADO_PAGO_VALIDATION_RULE;
import static com.business.unknow.rules.Constants.Timbrado.TIMBRADO_PAGO_VALIDATION_RULE_DES;
import static com.business.unknow.rules.Constants.Timbrado.TIMBRADO_SUITE;

import com.business.unknow.enums.LineaEmpresa;
import com.business.unknow.enums.TipoRelacion;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.pagos.PagoDto;
import java.math.BigDecimal;
import java.util.List;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(name = TIMBRADO_PAGO_VALIDATION, description = TIMBRADO_PAGO_VALIDATION_RULE)
public class FacturaPagoValidationRule {

  @Condition
  public boolean condition(
      @Fact("facturaCustom") FacturaCustom facturaCustom, @Fact("pagos") List<PagoDto> pagos) {

    if (!facturaCustom.getLineaEmisor().equals(LineaEmpresa.A.name())) {
      return false;
    } else if (TipoRelacion.SUSTITUCION.getId().equals(facturaCustom.getTipoRelacion())) {
      return false;
    }
    if (FACTURA.getDescripcion().equals(facturaCustom.getTipoDocumento())) {
      if (pagos == null && pagos.isEmpty()) {
        return !PPD.getClave().equals(facturaCustom.getMetodoPago());
      } else {
        if (PUE.getClave().equals(facturaCustom.getMetodoPago())) {
          BigDecimal paymentsAmmount =
              pagos.stream()
                  .filter(p -> ACEPTADO.name().equals(p.getStatusPago()))
                  .flatMap(p -> p.getFacturas().stream())
                  .filter(f -> f.getFolio().equals(facturaCustom.getFolio()))
                  .map(p -> p.getMonto())
                  .reduce(BigDecimal.ZERO, (p1, p2) -> p1.add(p2));
          BigDecimal totalfactura = facturaCustom.getCfdi().getTotal();
          return paymentsAmmount.compareTo(totalfactura) != 0;
        }
      }
    }
    return false;
  }

  @Action
  public void execute(@Fact("results") List<String> results) {
    results.add(
        String.format(
            String.format(
                "Error durante : %s con el error: %s",
                TIMBRADO_SUITE, TIMBRADO_PAGO_VALIDATION_RULE_DES)));
  }
}
