package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.DatoAnualEmpresa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatoAnualEmpresaRepository extends JpaRepository<DatoAnualEmpresa, Integer> {

  List<DatoAnualEmpresa> findByRfc(String rfc);

}
