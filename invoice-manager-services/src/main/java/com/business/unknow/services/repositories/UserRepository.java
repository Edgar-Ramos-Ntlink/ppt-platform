package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

  List<User> findAll();

  Page<User> findAll(Pageable pageable);

  Optional<User> findByEmail(String email);

  @Query(
      "select u from User u where u.activo like upper(:status) and upper(u.email) like upper(:email) and upper(u.alias) like upper(:alias)")
  Page<User> findAllByParams(
      @Param("status") String status,
      @Param("email") String email,
      @Param("alias") String alias,
      Pageable pageable);

  Page<User> findAllByRoles_Role(String role, Pageable pageable);
}
