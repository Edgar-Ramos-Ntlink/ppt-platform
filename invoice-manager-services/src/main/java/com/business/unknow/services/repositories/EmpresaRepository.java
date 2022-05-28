package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.Empresa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository
    extends JpaRepository<Empresa, Integer>, JpaSpecificationExecutor<Empresa> {
  Page<Empresa> findAll(Pageable pageable);

  @Query("select e from Empresa e where e.rfc = :rfc")
  Optional<Empresa> findByRfc(@Param("rfc") String rfc);

  @Query("select e from Empresa e where e.tipo = :tipo and e.giro = :giro and e.operativa =true")
  List<Empresa> findByTipoAndGiro(@Param("tipo") String tipo, @Param("giro") Integer giro);
}
