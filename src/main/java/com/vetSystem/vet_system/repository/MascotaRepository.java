package com.vetSystem.vet_system.repository;

import com.vetSystem.vet_system.model.Mascota;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    @Override
    @EntityGraph(attributePaths = "dueno")
    List<Mascota> findAll();

    @Override
    @EntityGraph(attributePaths = "dueno")
    Optional<Mascota> findById(Long id);

    @EntityGraph(attributePaths = "dueno")
    List<Mascota> findByDuenoId(Long duenoId);

    boolean existsByNombreAndDuenoId(String nombre, Long duenoId);
}
