package com.business.unknow.services.repositories.facturas;

import com.business.unknow.services.entities.cfdi.Concepto;
import com.business.unknow.services.entities.cfdi.Retencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetencionRepository extends JpaRepository<Retencion, Integer> {

  long deleteByConcepto(Concepto concepto);
}
