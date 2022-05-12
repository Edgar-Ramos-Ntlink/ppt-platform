package com.business.unknow.services.repositories.catalogs;

import com.business.unknow.services.entities.catalogs.RegimenFiscal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegimenFiscalRepository extends JpaRepository<RegimenFiscal, Integer> {

  List<RegimenFiscal> findAll();
}
