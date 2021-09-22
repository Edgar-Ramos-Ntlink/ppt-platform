package com.business.unknow.rules.timbrado;

import com.business.unknow.enums.LineaEmpresaEnum;
import com.business.unknow.enums.MetodosPagoEnum;
import com.business.unknow.enums.RevisionPagosEnum;
import com.business.unknow.enums.TipoDocumentoEnum;
import com.business.unknow.model.context.FacturaContext;
import com.business.unknow.rules.common.Constants.Timbrado;
import java.math.BigDecimal;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(
    name = Timbrado.TIMBRADO_PAGO_VALIDATION,
    description = Timbrado.TIMBRADO_PAGO_VALIDATION_RULE)
public class FacturaPagoValidationRule {

  @Condition
  public boolean condition(@Fact("facturaContext") FacturaContext fc) {

    if (!fc.getFacturaDto().getLineaEmisor().equals(LineaEmpresaEnum.A.name())) {
      return false;
    } else if (TipoDocumentoEnum.FACTURA
        .getDescripcion()
        .equals(fc.getFacturaDto().getTipoDocumento())) {
      if (fc.getPagos() == null && fc.getPagos().isEmpty()) {
        return !MetodosPagoEnum.PPD.getClave().equals(fc.getFacturaDto().getMetodoPago());
        // PPD never has payments and PUE always should have payments
      } else {
        if (MetodosPagoEnum.PUE.getClave().equals(fc.getFacturaDto().getMetodoPago())) {
          BigDecimal paymentsAmmount =
              fc.getPagos().stream()
                  .filter(p -> RevisionPagosEnum.ACEPTADO.name().equals(p.getStatusPago()))
                  .flatMap(p -> p.getFacturas().stream())
                  .filter(f -> f.getFolio().equals(fc.getFacturaDto().getFolio()))
                  .map(p -> p.getMonto())
                  .reduce(BigDecimal.ZERO, (p1, p2) -> p1.add(p2));
          BigDecimal totalfactura = fc.getFacturaDto().getCfdi().getTotal();
          return paymentsAmmount.compareTo(totalfactura) != 0;
        }
      }
    }
    return false;
  }

  @Action
  public void execute(@Fact("facturaContext") FacturaContext fc) {
    fc.setRuleErrorDesc(Timbrado.TIMBRADO_PAGO_VALIDATION_RULE_DES);
    fc.setSuiteError(String.format("Error durante : %s", Timbrado.TIMBRADO_SUITE));
    fc.setValid(false);
  }
}
