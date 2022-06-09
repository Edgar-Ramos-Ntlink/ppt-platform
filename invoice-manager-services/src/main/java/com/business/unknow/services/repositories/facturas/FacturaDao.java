package com.business.unknow.services.repositories.facturas;

import com.business.unknow.model.dto.FacturaReportDto;
import com.business.unknow.model.dto.PagoReportDto;
import com.business.unknow.services.repositories.rowmappers.FacturaAmountDocument;
import com.business.unknow.services.repositories.rowmappers.FacturaReportDtoRowMapper;
import com.business.unknow.services.repositories.rowmappers.PagoReportDtoRowMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

@Repository
public class FacturaDao {

  @Autowired private JdbcTemplate invoiceManagerTemplate;

  private static final String INVOICES_BY_FOLIOS =
      "SELECT f.FOLIO,f.LINEA_EMISOR,f.UUID,f.FECHA_TIMBRADO,f.RAZON_SOCIAL_EMISOR,f.RFC_EMISOR,f.RAZON_SOCIAL_REMITENTE,f.RFC_REMITENTE,f.TIPO_DOCUMENTO,f.PACK_FACTURACION,f.SOLICITANTE,rep.TIPO_COMPROBANTE,rep.IMP_TRASLADADOS,rep.IMP_RETENIDOS,rep.SUB_TOTAL,rep.TOTAL,f.SALDO_PENDIENTE,rep.METODO_PAGO,rep.FORMA_PAGO,rep.MONEDA,f.STATUS_FACTURA,f.FECHA_CANCELADO,rep.CANTIDAD,rep.CLAVE_UNIDAD,rep.UNIDAD,rep.CLAVE_PROD_SERV,rep.DESCRIPCION,rep.VALOR_UNITARIO,rep.IMPORTE "
          + "FROM FACTURAS40 f INNER JOIN REPORTES rep ON f.FOLIO = rep.FOLIO "
          + "WHERE rep.FOLIO IN (%s)";

  private static final String COMPLEMENTS_BY_FOLIOS =
      "SELECT p.ID_DOCUMENTO AS UUID,rep.FECHA,f.RAZON_SOCIAL_EMISOR,f.RFC_EMISOR,f.RAZON_SOCIAL_REMITENTE,f.RFC_REMITENTE,f.TIPO_DOCUMENTO,f.PACK_FACTURACION,"
          + "	rep.TIPO_COMPROBANTE,rep.IMP_TRASLADADOS,rep.IMP_RETENIDOS,rep.SUB_TOTAL,rep.TOTAL,p.MONEDA,p.FORMA_PAGO,rep.METODO_PAGO,f.STATUS_FACTURA,f.FECHA_CANCELADO,p.FOLIO AS FOLIO_PAGO,f.UUID AS UUID_PAGO,"
          + "p.IMPORTE_PAGADO,p.IMPORTE_SALDO_ANTERIOR,p.IMPORTE_SALDO_INSOLUTO,p.NUM_PARCIALIDAD,p.FECHA_PAGO FROM FACTURAS40 f INNER JOIN REPORTES rep ON f.FOLIO = rep.FOLIO INNER JOIN CFDI_PAGOS p ON rep.FOLIO = p.FOLIO "
          + "WHERE rep.FOLIO IN (%s)";

  private static final String CANTIDAD_FACTURAS =
      "SELECT count(1) AS CANTIDAD "
          + "FROM FACTURAS "
          + "WHERE MONTH(fecha_creacion) = MONTH(CURRENT_DATE()) "
          + "AND YEAR(fecha_creacion) = YEAR(CURRENT_DATE())";

  public List<FacturaReportDto> getInvoiceDetailsByFolios(List<String> folios) {
    String questionMarks =
        folios.stream().reduce("", (a, b) -> a.concat(",?")).replaceFirst(",", "");
    return invoiceManagerTemplate.query(
        new PreparedStatementCreator() {
          @Override
          public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement ps =
                con.prepareStatement(String.format(INVOICES_BY_FOLIOS, questionMarks));
            int i = 1;
            for (String folio : folios) {
              ps.setString(i, folio);
              i++;
            }
            return ps;
          }
        },
        new FacturaReportDtoRowMapper());
  }

  public List<PagoReportDto> getComplementsDetailsByFolios(List<String> folios) {
    String questionMarks =
        folios.stream().reduce("", (a, b) -> a.concat(",?")).replaceFirst(",", "");
    return invoiceManagerTemplate.query(
        new PreparedStatementCreator() {
          @Override
          public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement ps =
                con.prepareStatement(String.format(COMPLEMENTS_BY_FOLIOS, questionMarks));
            int i = 1;
            for (String folio : folios) {
              ps.setString(i, folio);
              i++;
            }
            return ps;
          }
        },
        new PagoReportDtoRowMapper());
  }

  public Integer getCantidadFacturasOfTheCurrentMonthByTipoDocumento() {
    return invoiceManagerTemplate.query(
        new PreparedStatementCreator() {
          @Override
          public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement ps = con.prepareStatement(CANTIDAD_FACTURAS);
            return ps;
          }
        },
        new FacturaAmountDocument());
  }
}
