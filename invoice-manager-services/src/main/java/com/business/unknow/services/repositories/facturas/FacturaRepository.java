package com.business.unknow.services.repositories.facturas;

import com.business.unknow.services.entities.Factura;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository
    extends JpaRepository<Factura, Integer>, JpaSpecificationExecutor<Factura> {

  Page<Factura> findAll(Pageable pageable);

  Optional<Factura> findByFolio(String folio);

  Page<Factura> findByPreFolio(String prefolio, Pageable pageable);
}
