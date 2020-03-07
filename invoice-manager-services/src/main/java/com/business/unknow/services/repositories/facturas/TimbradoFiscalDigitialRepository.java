package com.business.unknow.services.repositories.facturas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.business.unknow.services.entities.cfdi.TimbradoFiscalDigitial;

@Repository
public interface TimbradoFiscalDigitialRepository extends JpaRepository<TimbradoFiscalDigitial, Integer> {

	@Query("select f from TimbradoFiscalDigitial f where f.cfdi.id=:id")
	public Optional<TimbradoFiscalDigitial> findByIdCfdi(@Param("id") Integer idCfdi);
	
}
