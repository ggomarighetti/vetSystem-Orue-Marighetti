package com.vetSystem.vet_system.repository;

import com.vetSystem.vet_system.model.Dueno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    @Query("select distinct d from Dueno d left join fetch d.mascotas where d.id = ?1")
    Optional<Dueno> findByIdWithMascotas(Long id);

    boolean existsByDni(String dni);

    Optional<Dueno> findByEmail(String email);
}
