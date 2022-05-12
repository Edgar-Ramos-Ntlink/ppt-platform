package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.EmpresaIngresos;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaIngresosRepository extends JpaRepository<EmpresaIngresos, Integer> {
  List<EmpresaIngresos> findByRfc(String rfc);
}
