package com.business.unknow.services.repositories.facturas;

import com.business.unknow.services.entities.cfdi.Relacionado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CfdiRelacionadoRepository extends JpaRepository<Relacionado, Integer> {}
