package com.vetSystem.vet_system.repository;

import com.vetSystem.vet_system.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    List<Mascota> findByDuenoId(Long duenoId);

    boolean existsByNombreAndDuenoId(String nombre, Long duenoId);
}
