package com.business.unknow.services.repositories.catalogs;

import com.business.unknow.services.entities.catalogs.StatusPago;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusPagoRepository extends JpaRepository<StatusPago, Integer> {
  List<StatusPago> findAll();
}
