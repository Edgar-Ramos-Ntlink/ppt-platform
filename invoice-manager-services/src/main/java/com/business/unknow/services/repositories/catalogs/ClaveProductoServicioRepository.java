package com.business.unknow.services.repositories.catalogs;

import com.business.unknow.services.entities.catalogs.ClaveProductoServicio;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaveProductoServicioRepository
    extends JpaRepository<ClaveProductoServicio, Integer> {
  Page<ClaveProductoServicio> findAll(Pageable pageable);

  List<ClaveProductoServicio> findByDescripcionContainingIgnoreCase(String description);

  List<ClaveProductoServicio> findByClave(Integer clave);
}
