package com.business.unknow.services.repositories.catalogs;

import com.business.unknow.services.entities.catalogs.ClaveUnidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaveUnidadRepository extends JpaRepository<ClaveUnidad, String> {
  Page<ClaveUnidad> findAll(Pageable pageable);
}
