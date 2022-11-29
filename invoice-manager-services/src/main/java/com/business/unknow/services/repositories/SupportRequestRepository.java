package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.SupportRequest;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportRequestRepository
    extends JpaRepository<SupportRequest, Integer>, JpaSpecificationExecutor<SupportRequest> {

  Page<SupportRequest> findAll(Pageable pageable);

  Optional<SupportRequest> findByFolio(Integer folio);
}
