package com.business.unknow.services.repositories.facturas;

import com.business.unknow.services.entities.Factura33;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Factura33Repository extends JpaRepository<Factura33, Integer> {

  Optional<Factura33> findByFolio(String folio);

  Page<Factura33> findByPreFolio(String prefolio, Pageable pageable);
}
