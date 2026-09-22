package com.vetSystem.vet_system.repository;

import com.vetSystem.vet_system.model.Dueno;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    @Query("select distinct d from Dueno d left join fetch d.mascotas where d.id = ?1")
    Optional<Dueno> findByIdWithMascotas(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Dueno d where d.id = :id")
    Optional<Dueno> findByIdForUpdate(@Param("id") Long id);

    boolean existsByDni(String dni);

    Optional<Dueno> findByEmail(String email);
}
