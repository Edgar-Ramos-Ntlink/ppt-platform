package com.business.unknow.services.repositories.catalogs;

import com.business.unknow.services.entities.catalogs.UsoCfdi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsoCfdiRepository extends JpaRepository<UsoCfdi, String> {}
