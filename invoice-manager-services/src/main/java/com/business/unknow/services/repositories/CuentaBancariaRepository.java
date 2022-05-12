package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.CuentaBancaria;
import java.util.Date;
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
public interface CuentaBancariaRepository
    extends JpaRepository<CuentaBancaria, Integer>, JpaSpecificationExecutor<CuentaBancaria> {

  List<CuentaBancaria> findAll();

  List<CuentaBancaria> findByRfc(String rfc);

  Optional<CuentaBancaria> findByClabe(String clabe);

  Optional<CuentaBancaria> findByRfcAndCuenta(String rfc, String cuenta);

  List<CuentaBancaria> findById(String id);

  @Query(
      "select c from CuentaBancaria c where c.banco like upper(:banco) and c.empresa like upper(:empresa) and c.clabe like upper(:clabe) and c.cuenta like upper(:cuenta) and c.fechaCreacion between :since and :to")
  Page<CuentaBancaria> findCuentasByFilterParams(
      @Param("banco") String banco,
      @Param("empresa") String empresa,
      @Param("clabe") String clabe,
      @Param("cuenta") String cuenta,
      @Param("since") Date since,
      @Param("to") Date to,
      Pageable pageable);
}
