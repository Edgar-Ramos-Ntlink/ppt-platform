package com.business.unknow.services.repositories.catalogs;

import com.business.unknow.services.entities.catalogs.FormaPago;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPago, String> {
  List<FormaPago> findAll();
}
