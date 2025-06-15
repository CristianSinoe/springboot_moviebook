package com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.repository;

import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.EReaction;
import com.sinoeruiz.spring.security.postgresql.SpringBootSecurityApplication.models.Reaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    
    // Buscar reacción por su nombre enum
    Optional<Reaction> findByName(EReaction name);

    // Verificar si ya existe una reacción por su nombre enum
    boolean existsByName(EReaction name);
}
