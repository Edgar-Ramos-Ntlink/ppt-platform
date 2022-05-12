package com.business.unknow.services.repositories.facturas;

import com.business.unknow.services.entities.CfdiPago;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CfdiPagoRepository extends JpaRepository<CfdiPago, Integer> {

  List<CfdiPago> findByFolio(String folio);

  @Query("select p from CfdiPago p where p.folio=:folio AND p.numeroParcialidad=:parcialidad")
  List<CfdiPago> findByIdCfdiAndParcialidad(
      @Param("folio") String folio, @Param("parcialidad") int parcialidad);
}
