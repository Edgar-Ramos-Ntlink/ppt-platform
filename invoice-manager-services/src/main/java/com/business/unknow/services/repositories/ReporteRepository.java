package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {
  void deleteByFolio(String folio);
}
