package com.business.unknow.services.repositories.catalogs;

import com.business.unknow.services.entities.StatusPayment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusPaymentRepository extends JpaRepository<StatusPayment, Integer> {
  List<StatusPayment> findAll();
}
