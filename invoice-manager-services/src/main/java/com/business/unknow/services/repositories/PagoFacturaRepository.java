package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.PagoFactura;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoFacturaRepository extends JpaRepository<PagoFactura, Integer> {
  @Query("select c from PagoFactura c where c.pago.id=:idPago")
  List<PagoFactura> findByPagoId(@Param("idPago") Integer idPago);
}
