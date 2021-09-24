package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.Client;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

  public Page<Client> findAll(Pageable pageable);

  @Query(
      "select c from Client c where c.activo like upper(:status) and upper(c.informacionFiscal.rfc) like upper(:rfc) and upper(c.informacionFiscal.razonSocial) like upper(:razonSocial)")
  public Page<Client> findClientsByParms(
      @Param("status") String status,
      @Param("rfc") String rfc,
      @Param("razonSocial") String razonSocial,
      Pageable pageable);

  @Query(
      "select c from Client c where c.correoPromotor = :promotor and c.activo like upper(:status) and upper(c.informacionFiscal.rfc) like upper(:rfc) and upper(c.informacionFiscal.razonSocial) like upper(:razonSocial)")
  public Page<Client> findClientsFromPromotorByParms(
      @Param("promotor") String promotor,
      @Param("status") String status,
      @Param("rfc") String rfc,
      @Param("razonSocial") String razonSocial,
      Pageable pageable);

  @Query("select c from Client c where lower(c.informacionFiscal.rfc) = lower(:rfc)")
  public Optional<Client> findByRfc(@Param("rfc") String rfc);

  @Query(
      "select c from Client c where lower(c.informacionFiscal.razonSocial) = lower(:razonSocial)")
  public Optional<Client> findByRazonSocial(@Param("razonSocial") String razonSocial);

  public List<Client> findByCorreoPromotor(String promotor);

  @Query(
      "select c from Client c where lower(c.informacionFiscal.rfc) = lower(:rfc) and c.correoPromotor = :promotor")
  public Optional<Client> findByCorreoPromotorAndClient(
      @Param("promotor") String promotor, @Param("rfc") String rfc);
}
