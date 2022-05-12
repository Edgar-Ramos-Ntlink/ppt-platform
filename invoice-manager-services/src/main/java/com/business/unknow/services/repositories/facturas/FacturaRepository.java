package com.business.unknow.services.repositories.facturas;

import com.business.unknow.services.entities.Factura;
import java.util.Date;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository
    extends JpaRepository<Factura, Integer>, JpaSpecificationExecutor<Factura> {

  Page<Factura> findAll(Pageable pageable);

  Optional<Factura> findByFolio(String folio);

  Page<Factura> findByIdCfdi(Integer prefolio, Pageable pageable);

  Page<Factura> findByPreFolio(String prefolio, Pageable pageable);

  @Query(
      "select f from Factura f where  f.tipoDocumento=:tipo and f.lineaEmisor=:lineaEmisor and upper(f.statusFactura) = upper(:status) and f.fechaCreacion between :since and :to and upper(f.razonSocialEmisor) like upper(:razonSocialEmisor) and upper(f.razonSocialRemitente) like upper(:razonSocialRemitente)")
  public Page<Factura> findReportsByLineaAndStatusEmisorWithParams(
      @Param("tipo") String tipoDocumento,
      @Param("status") String status,
      @Param("lineaEmisor") String lineaEmisor,
      @Param("since") Date since,
      @Param("to") Date to,
      @Param("razonSocialEmisor") String razonSocialEmisor,
      @Param("razonSocialRemitente") String razonSocialRemitente,
      Pageable pageable);
}
