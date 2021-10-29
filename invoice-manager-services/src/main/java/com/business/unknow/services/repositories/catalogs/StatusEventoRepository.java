package com.business.unknow.services.repositories.catalogs;

import com.business.unknow.services.entities.catalogs.StatusEvento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusEventoRepository extends JpaRepository<StatusEvento, Integer> {
  List<StatusEvento> findAll();
}
