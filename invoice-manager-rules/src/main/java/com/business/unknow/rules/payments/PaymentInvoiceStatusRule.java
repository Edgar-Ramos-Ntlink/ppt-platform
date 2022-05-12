package com.business.unknow.rules.payments;

import com.business.unknow.enums.FacturaStatus;
import com.business.unknow.enums.MetodosPago;
import com.business.unknow.enums.RevisionPagos;
import com.business.unknow.model.dto.FacturaCustom;
import com.business.unknow.model.dto.pagos.PagoDto;
import com.business.unknow.rules.Constants.PaymentsSuite;
import java.util.List;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

@Rule(
    name = PaymentsSuite.INVOICE_STATUS_PAYMENT_UPADTE_VALIDATION_RULE,
    description = PaymentsSuite.INVOICE_STATUS_PAYMENT_UPADTE_VALIDATION_RULE_DESC)
public class PaymentInvoiceStatusRule {

  @Condition
  public boolean condition(
      @Fact("facturas") List<FacturaCustom> facturas,
      @Fact("results") List<String> results,
      @Fact("payment") PagoDto currentPayment) {
    for (FacturaCustom factura : facturas) {
      if (MetodosPago.PPD.getClave().equals(factura.getMetodoPago())) {
        if (!RevisionPagos.RECHAZADO.name().equals(currentPayment.getStatusPago())
            && (FacturaStatus.CANCELADA.getValor().equals(factura.getStatusFactura())
                || FacturaStatus.POR_TIMBRAR.getValor().equals(factura.getStatusFactura())
                || FacturaStatus.RECHAZO_OPERACIONES
                    .getValor()
                    .equals(factura.getStatusFactura()))) {
          results.add(
              String.format("La factura con pre folio %d no es valida", factura.getIdCfdi()));
          return true;
        }
      }

      if (MetodosPago.PUE.getClave().equals(factura.getMetodoPago())) {
        if (!RevisionPagos.RECHAZADO.name().equals(currentPayment.getStatusPago())
            && (FacturaStatus.CANCELADA.getValor().equals(factura.getStatusFactura())
                || FacturaStatus.RECHAZO_OPERACIONES
                    .getValor()
                    .equals(factura.getStatusFactura()))) {
          results.add(
              String.format("La factura con pre folio %d no es valida", factura.getIdCfdi()));
          return true;
        }
      }
    }
    return false;
  }

  @Action
  public void execute(@Fact("results") List<String> results) {
    results.add(PaymentsSuite.INVOICE_STATUS_PAYMENT_UPADTE_VALIDATION_RULE_DESC);
  }
}
