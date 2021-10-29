package com.business.unknow.services.repositories.catalogs;

import com.business.unknow.services.entities.catalogs.StatusDevolucion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusDevolucionRepository extends JpaRepository<StatusDevolucion, Integer> {
  List<StatusDevolucion> findAll();
}
