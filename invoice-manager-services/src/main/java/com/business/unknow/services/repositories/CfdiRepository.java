package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.Cfdi;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CfdiRepository extends JpaRepository<Cfdi, Integer> {

  Optional<Cfdi> findByFolio(String folio);
}
