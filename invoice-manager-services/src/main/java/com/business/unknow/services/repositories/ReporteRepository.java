package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.Reporte;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {

  public List<Reporte> findByFolio(String folio);
}
