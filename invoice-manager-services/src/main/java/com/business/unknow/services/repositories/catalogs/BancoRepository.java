package com.business.unknow.services.repositories.catalogs;

import com.business.unknow.services.entities.catalogs.Banco;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BancoRepository extends JpaRepository<Banco, Integer> {
  List<Banco> findAll();
}
