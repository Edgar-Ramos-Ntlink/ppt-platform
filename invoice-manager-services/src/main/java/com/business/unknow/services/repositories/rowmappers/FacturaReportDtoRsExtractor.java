/** */
package com.business.unknow.services.repositories.rowmappers;

import com.business.unknow.model.dto.FacturaReportDto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.springframework.jdbc.core.ResultSetExtractor;

/** @author ralfdemoledor */
public class FacturaReportDtoRsExtractor implements ResultSetExtractor<Optional<FacturaReportDto>> {

  @Override
  public Optional<FacturaReportDto> extractData(ResultSet rs) throws SQLException {

    if (rs.next()) {
      return Optional.of(
          FacturaReportDto.builder()
              .lineaEmisor(rs.getString("LINEA_EMISOR"))
              .correoPromotor(rs.getString("CORREO_PROMOTOR"))
              .porcentajeCliente(rs.getString("PORCENTAJE_CLIENTE"))
              .porcentajeConcatco(rs.getString("PORCENTAJE_CONTACTO"))
              .porcentajeDespacho(rs.getString("PORCENTAJE_DESPACHO"))
              .porcentajePromotor(rs.getString("PORCENTAJE_PROMOTOR"))
              .lineaEmisor("LINEA_EMISOR")
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
              .total(rs.getBigDecimal("SALDO_PENDIENTE"))
              .metodoPago(rs.getString("METODO_PAGO"))
              .formaPago(rs.getString("FORMA_PAGO"))
              .moneda(rs.getString("MONEDA"))
              .statusFactura(rs.getString("STATUS_FACTURA"))
              .fechaCancelacion(rs.getDate("FECHA_CANCELADO"))
              .cantidad(rs.getBigDecimal("CANTIDAD"))
              .claveUnidad(rs.getString("CLAVE_UNIDAD"))
              .unidad(rs.getString("UNIDAD"))
              .claveProdServ(rs.getInt("CLAVE_PROD_SERV"))
              .descripcion(rs.getString("DESCRIPCION"))
              .valorUnitario(rs.getBigDecimal("VALOR_UNITARIO"))
              .importe(rs.getBigDecimal("IMPORTE"))
              .build());
    } else {
      return Optional.empty();
    }
  }
}
