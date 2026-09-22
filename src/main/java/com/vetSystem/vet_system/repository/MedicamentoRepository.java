package com.vetSystem.vet_system.repository;

import com.vetSystem.vet_system.model.Medicamento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Medicamento m where m.id = :id")
    Optional<Medicamento> findByIdForUpdate(@Param("id") Long id);

    @Query("select count(t) > 0 from Turno t join t.medicamentos m where m.id = :id")
    boolean estaRecetado(@Param("id") Long id);
}
