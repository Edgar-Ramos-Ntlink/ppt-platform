package com.business.unknow.services.repositories.catalogs;

import com.business.unknow.services.entities.catalogs.CodigoPostal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodigoPostalRepository extends JpaRepository<CodigoPostal, String> {
  List<CodigoPostal> findByCodigoPostal(String codigoPostal);
}
