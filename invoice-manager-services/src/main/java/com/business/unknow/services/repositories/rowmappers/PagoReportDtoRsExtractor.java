/** */
package com.business.unknow.services.repositories.rowmappers;

import com.business.unknow.model.dto.PagoReportDto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/** @author ralfdemoledor */
public class PagoReportDtoRsExtractor implements ResultSetExtractor<Optional<PagoReportDto>> {

  @Override
  public Optional<PagoReportDto> extractData(ResultSet rs)
      throws SQLException, DataAccessException {
    if (rs.next()) {
      return Optional.of(
          PagoReportDto.builder()
              .folioFiscal(rs.getString("UUID"))
              .fechaEmision(rs.getDate("FECHA"))
              .rfcEmisor(rs.getString("RFC_EMISOR"))
              .emisor(rs.getString("RAZON_SOCIAL_EMISOR"))
              .rfcReceptor(rs.getString("RFC_REMITENTE"))
              .receptor(rs.getString("RAZON_SOCIAL_REMITENTE"))
              .tipoDocumento(rs.getString("TIPO_DOCUMENTO"))
              .packFacturacion(rs.getString("PACK_FACTURACION"))
              .tipoComprobante(rs.getString("TIPO_COMPROBANTE"))
              .impuestosTrasladados(rs.getBigDecimal("IMP_TRASLADADOS"))
              .impuestosRetenidos(rs.getBigDecimal("IMP_RETENIDOS"))
              .subtotal(rs.getBigDecimal("SUB_TOTAL"))
              .total(rs.getBigDecimal("TOTAL"))
              .metodoPago(rs.getString("METODO_PAGO"))
              .formaPago(rs.getString("FORMA_PAGO"))
              .moneda(rs.getString("MONEDA"))
              .statusFactura(rs.getString("STATUS_FACTURA"))
              .fechaCancelacion(rs.getDate("FECHA_CANCELADO"))
              .folioPago(rs.getString("FOLIO_PAGO"))
              .folioFiscalPago(rs.getString("UUID_PAGO"))
              .importePagado(rs.getBigDecimal("IMPORTE_PAGADO"))
              .saldoAnterior(rs.getBigDecimal("IMPORTE_SALDO_ANTERIOR"))
              .saldoInsoluto(rs.getBigDecimal("IMPORTE_SALDO_INSOLUTO"))
              .numeroParcialidad(rs.getInt("NUM_PARCIALIDAD"))
              .build());
    } else {
      return Optional.empty();
    }
  }
}
