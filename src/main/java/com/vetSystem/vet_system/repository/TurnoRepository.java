package com.vetSystem.vet_system.repository;

import com.vetSystem.vet_system.model.Turno;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    @Override
    @EntityGraph(attributePaths = {"mascota", "veterinario"})
    List<Turno> findAll();

    @Override
    @EntityGraph(attributePaths = {"mascota", "veterinario"})
    Optional<Turno> findById(Long id);

    boolean existsByVeterinarioIdAndFechaAndHora(Long veterinarioId, LocalDate fecha, LocalTime hora);

    @EntityGraph(attributePaths = {"mascota", "veterinario"})
    List<Turno> findByVeterinarioIdAndFechaOrderByHoraAsc(Long veterinarioId, LocalDate fecha);

    @EntityGraph(attributePaths = {"mascota", "veterinario"})
    List<Turno> findByMascotaIdOrderByFechaDescHoraDesc(Long mascotaId);
}
