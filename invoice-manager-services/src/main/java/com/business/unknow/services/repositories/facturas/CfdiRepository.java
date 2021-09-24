package com.business.unknow.services.repositories.facturas;

import com.business.unknow.services.entities.cfdi.Cfdi;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CfdiRepository extends JpaRepository<Cfdi, Integer> {
  public Optional<Cfdi> findByFolio(String folio);
}
