package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.EmpresaDetalles;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaDetallesRepository extends JpaRepository<EmpresaDetalles, Integer> {
  List<EmpresaDetalles> findByRfcAndTipo(String rfc, String tipo);
}
