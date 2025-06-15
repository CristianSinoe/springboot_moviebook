package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.ERole;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}