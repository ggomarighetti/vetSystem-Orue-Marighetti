package com.vetSystem.vet_system.repository;

import com.vetSystem.vet_system.model.Veterinario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    boolean existsByMatricula(String matricula);

    Optional<Veterinario> findByMatricula(String matricula);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Veterinario v where v.id = :id")
    Optional<Veterinario> findByIdForUpdate(@Param("id") Long id);
}
