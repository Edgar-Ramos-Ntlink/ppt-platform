package com.business.unknow.services.repositories.catalogs;

import com.business.unknow.services.entities.catalogs.Giro;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiroRepository extends CrudRepository<Giro, Integer> {
  List<Giro> findAll();
}
