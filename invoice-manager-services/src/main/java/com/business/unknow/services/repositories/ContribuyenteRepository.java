package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.Contribuyente;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContribuyenteRepository extends CrudRepository<Contribuyente, Integer> {

  public Optional<Contribuyente> findByRfc(String rfc);
}
