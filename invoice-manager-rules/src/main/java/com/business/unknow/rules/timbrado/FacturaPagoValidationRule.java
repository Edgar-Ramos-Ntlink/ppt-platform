package com.business.unknow.rules.timbrado;

import com.business.unknow.enums.LineaEmpresaEnum;
import com.business.unknow.enums.MetodosPagoEnum;
import com.business.unknow.enums.RevisionPagosEnum;
import com.business.unknow.enums.TipoDocumentoEnum;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.rules.common.Constants.Timbrado;
import java.math.BigDecimal;
import java.util.List;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import static com.business.unknow.enums.MetodosPagoEnum.PPD;
import static com.business.unknow.enums.MetodosPagoEnum.PUE;
import static com.business.unknow.enums.RevisionPagosEnum.ACEPTADO;
import static com.business.unknow.enums.TipoDocumentoEnum.FACTURA;
import static com.business.unknow.rules.common.Constants.Timbrado.TIMBRADO_PAGO_VALIDATION;
import static com.business.unknow.rules.common.Constants.Timbrado.TIMBRADO_PAGO_VALIDATION_RULE;
import static com.business.unknow.rules.common.Constants.Timbrado.TIMBRADO_PAGO_VALIDATION_RULE_DES;
import static com.business.unknow.rules.common.Constants.Timbrado.TIMBRADO_SUITE;

@Rule(
    name = TIMBRADO_PAGO_VALIDATION,
    description = TIMBRADO_PAGO_VALIDATION_RULE)
public class FacturaPagoValidationRule {

  @Condition
  public boolean condition(@Fact("facturaCustom") FacturaCustom facturaCustom, List<PagoDto> pagos) {

    if (!facturaCustom.getLineaEmisor().equals(LineaEmpresaEnum.A.name())) {
      return false;
    } else if (FACTURA
        .getDescripcion()
        .equals(facturaCustom.getTipoDocumento())) {
      if (pagos == null && pagos.isEmpty()) {
        return !PPD.getClave().equals(facturaCustom.getMetodoPago());
        // PPD never has payments and PUE always should have payments
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
                            TIMBRADO_SUITE,
                            TIMBRADO_PAGO_VALIDATION_RULE_DES)));
  }
}
