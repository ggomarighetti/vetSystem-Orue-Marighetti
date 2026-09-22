package com.vetSystem.vet_system.repository;

import com.vetSystem.vet_system.model.Dueno;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    @Override
    @EntityGraph(attributePaths = "mascotas")
    List<Dueno> findAll();

    @Override
    @EntityGraph(attributePaths = "mascotas")
    Optional<Dueno> findById(Long id);

    boolean existsByDni(String dni);

    Optional<Dueno> findByEmail(String email);
}
