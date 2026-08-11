package com.vetSystem.vet_system;

import com.vetSystem.vet_system.model.Dueno;
import com.vetSystem.vet_system.model.EstadoTurno;
import com.vetSystem.vet_system.model.Mascota;
import com.vetSystem.vet_system.model.Turno;
import com.vetSystem.vet_system.model.Veterinario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class PersistenceVerificationTests {

    private static final Set<String> EXPECTED_TABLES = Set.of(
            "duenos",
            "mascotas",
            "veterinarios",
            "turnos"
    );

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void hibernateCreatesExpectedDomainTables() {
        var tables = jdbcTemplate.queryForList("""
                SELECT LOWER(TABLE_NAME)
                FROM INFORMATION_SCHEMA.TABLES
                WHERE TABLE_SCHEMA = 'PUBLIC'
                  AND TABLE_NAME IN ('DUENOS', 'MASCOTAS', 'VETERINARIOS', 'TURNOS')
                """, String.class);

        assertEquals(EXPECTED_TABLES, new HashSet<>(tables));
    }

    @Test
    void persistsCompleteAppointmentGraph() {
        var dueno = new Dueno();
        dueno.setNombre("Guillermo");
        dueno.setApellido("Orue Marighetti");
        dueno.setDni("30111222");
        dueno.setTelefono("+54 11 5555-0101");
        dueno.setEmail("guillermo@example.com");
        entityManager.persist(dueno);

        var mascota = new Mascota();
        mascota.setNombre("Milo");
        mascota.setEspecie("Perro");
        mascota.setRaza("Mestizo");
        mascota.setFechaNacimiento(LocalDate.of(2021, 5, 12));
        mascota.setDueno(dueno);
        entityManager.persist(mascota);

        var veterinario = new Veterinario();
        veterinario.setNombre("Ana");
        veterinario.setApellido("Pérez");
        veterinario.setMatricula("MAT-001");
        veterinario.setEspecialidad("Clínica general");
        entityManager.persist(veterinario);

        var turno = new Turno();
        turno.setFecha(LocalDate.of(2026, 8, 20));
        turno.setHora(LocalTime.of(10, 30));
        turno.setMotivo("Control anual");
        turno.setMascota(mascota);
        turno.setVeterinario(veterinario);
        entityManager.persist(turno);

        entityManager.flush();

        assertNotNull(dueno.getId());
        assertNotNull(mascota.getId());
        assertNotNull(veterinario.getId());
        assertNotNull(turno.getId());

        entityManager.clear();

        var persistedTurno = entityManager.find(Turno.class, turno.getId());
        assertNotNull(persistedTurno);
        assertEquals(EstadoTurno.PENDIENTE, persistedTurno.getEstado());
        assertEquals("Milo", persistedTurno.getMascota().getNombre());
        assertEquals("MAT-001", persistedTurno.getVeterinario().getMatricula());
    }
}
