package com.vetSystem.vet_system.support;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseAssertions {

    private final JdbcTemplate jdbc;

    public DatabaseAssertions(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void assertSinMascotas() {
        assertThat(jdbc.queryForObject("select count(*) from mascotas", Long.class)).isZero();
    }

    public void assertDuenoDeMascota(String nombre, Long duenoId) {
        assertThat(jdbc.queryForObject("select dueno_id from mascotas where nombre = ?", Long.class, nombre))
                .isEqualTo(duenoId);
    }

    public Snapshot snapshot() {
        return new Snapshot(
                jdbc.queryForList("select * from duenos order by id"),
                jdbc.queryForList("select * from mascotas order by id"),
                jdbc.queryForList("select * from turnos order by id"),
                jdbc.queryForList("select * from veterinarios order by id"));
    }

    public void assertSinCambios(Snapshot anterior) {
        assertThat(snapshot()).isEqualTo(anterior);
    }

    public record Snapshot(List<Map<String, Object>> duenos, List<Map<String, Object>> mascotas,
                           List<Map<String, Object>> turnos, List<Map<String, Object>> veterinarios) {
    }
}
