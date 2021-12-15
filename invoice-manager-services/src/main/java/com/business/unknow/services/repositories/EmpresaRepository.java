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

  public Page<Empresa> findAll(Pageable pageable);

  @Query("select e from Empresa e where upper(e.tipo) like upper(:linea)")
  public Page<Empresa> findAllWithLinea(@Param("linea") String linea, Pageable pageable);

  @Query(
      "select e from Empresa e where upper(e.tipo) like upper(:linea) and upper(e.rfc) like upper(:rfc)")
  public Page<Empresa> findByRfcIgnoreCaseContaining(
      @Param("rfc") String rfc, @Param("linea") String linea, Pageable pageable);

  @Query(
      "select e from Empresa e where upper(e.tipo) like upper(:linea) and upper(e.razonSocial) like upper(:razonSocial)")
  public Page<Empresa> findByRazonSocialIgnoreCaseContaining(
      @Param("razonSocial") String razonSocial, @Param("linea") String linea, Pageable pageable);

  @Query("select e from Empresa e where e.rfc = :rfc")
  public Optional<Empresa> findByRfc(@Param("rfc") String rfc);

  public List<Empresa> findByTipoAndGiro(String tipo, Integer giroId);
}
