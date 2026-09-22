package com.vetSystem.vet_system.repository;

import com.vetSystem.vet_system.model.Dueno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    boolean existsByDni(String dni);

    Optional<Dueno> findByEmail(String email);
}
