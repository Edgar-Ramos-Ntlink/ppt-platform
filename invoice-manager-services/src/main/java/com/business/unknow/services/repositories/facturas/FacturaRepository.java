package com.business.unknow.services.repositories.facturas;

import com.business.unknow.services.entities.Factura40;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository
    extends JpaRepository<Factura40, Integer>, JpaSpecificationExecutor<Factura40> {

  Page<Factura40> findAll(Pageable pageable);

  Optional<Factura40> findByFolio(String folio);

  Page<Factura40> findByPreFolio(String prefolio, Pageable pageable);
}
