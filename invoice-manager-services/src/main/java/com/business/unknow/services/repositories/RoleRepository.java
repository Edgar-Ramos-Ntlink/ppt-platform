package com.business.unknow.services.repositories;

import com.business.unknow.services.entities.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {

  List<Role> findAll();

  Optional<Role> findByRole(String role);

  @Query("select c from Role c where c.user.id = :id")
  List<Role> findByUserId(@Param("id") Integer id);

  @Query("select c from Role c where c.user.id = :userId and c.id =:id")
  Optional<Role> findByUserIdAndId(@Param("userId") Integer userId, @Param("id") Integer id);

  @Query("select c from Role c where c.user.id = :userId and c.role =:role")
  Optional<Role> findByUserIdAndRole(@Param("userId") Integer userId, @Param("role") String role);
}
