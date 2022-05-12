package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.Contribuyente;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContribuyenteRepository extends CrudRepository<Contribuyente, Integer> {
  Optional<Contribuyente> findByRfc(String rfc);
}
