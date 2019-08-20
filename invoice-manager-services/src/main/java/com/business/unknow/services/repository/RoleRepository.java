package com.business.unknow.services.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.business.unknow.services.entities.Role;

/**
 * @author eej000f
 *
 */
public interface RoleRepository extends CrudRepository<Role, Integer> {
 
	public List<Role> findAll();
	public Optional<Role> findByName(String name);
}
