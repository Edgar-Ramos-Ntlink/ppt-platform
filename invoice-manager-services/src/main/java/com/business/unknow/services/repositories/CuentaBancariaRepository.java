package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.CuentaBancaria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaBancariaRepository
    extends JpaRepository<CuentaBancaria, Integer>, JpaSpecificationExecutor<CuentaBancaria> {

  List<CuentaBancaria> findAll();

  List<CuentaBancaria> findByRfc(String rfc);

  Optional<CuentaBancaria> findByRfcAndCuenta(String rfc, String cuenta);
}
