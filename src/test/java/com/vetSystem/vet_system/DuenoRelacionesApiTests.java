package com.vetSystem.vet_system;

import com.vetSystem.vet_system.model.Dueno;
import com.vetSystem.vet_system.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import tools.jackson.databind.node.ObjectNode;

import static com.vetSystem.vet_system.support.DuenoTestData.datosActualizacion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql({"/fixtures/duenos/dueno.sql", "/fixtures/duenos/mascotas.sql"})
class DuenoRelacionesApiTests extends IntegrationTest {

    @Test
    void consultarYActualizarUnDuenoConMascotasNoSerializaElGrafoNiCambiaLaRelacion() throws Exception {
        Dueno dueno = duenoExistente();
        mvc.perform(get("/api/duenos/{id}", dueno.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mascotas").doesNotExist());
        mvc.perform(get("/api/duenos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mascotas").doesNotExist());
        ObjectNode datos = objectMapper.valueToTree(datosActualizacion());
        datos.putArray("mascotas");
        mvc.perform(put("/api/duenos/{id}", dueno.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk());
        assertThat(mascotaRepository.findAll())
                .hasSize(2)
                .allSatisfy(mascota -> assertThat(mascota.getDueno().getId()).isEqualTo(dueno.getId()));
    }

    @Test
    void eliminarDuenoConMascotasSinTurnosDevuelve409YConservaTodasLasDependencias() throws Exception {
        assertThat(mascotaRepository.count()).isEqualTo(2);
        assertThat(turnoRepository.count()).isZero();
        assertThat(veterinarioRepository.count()).isZero();
        verificarBorradoRechazado();
    }

    @Test
    @Sql("/fixtures/duenos/turno.sql")
    void eliminarDuenoConMascotasYTurnoDevuelve409YConservaTodasLasDependencias() throws Exception {
        assertThat(mascotaRepository.count()).isEqualTo(2);
        assertThat(turnoRepository.count()).isEqualTo(1);
        assertThat(veterinarioRepository.count()).isEqualTo(1);
        verificarBorradoRechazado();
    }

    private void verificarBorradoRechazado() throws Exception {
        Dueno dueno = duenoExistente();
        Sort orden = Sort.by("id");
        var duenosAntes = duenoRepository.findAll(orden);
        var mascotasAntes = mascotaRepository.findAll(orden);
        var turnosAntes = turnoRepository.findAll(orden);
        var veterinariosAntes = veterinarioRepository.findAll(orden);

        mvc.perform(delete("/api/duenos/{id}", dueno.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Dueño con id " + dueno.getId()
                        + " tiene registros asociados y no puede eliminarse"));

        assertThat(duenoRepository.findAll(orden)).containsExactlyElementsOf(duenosAntes);
        assertThat(mascotaRepository.findAll(orden)).zipSatisfy(mascotasAntes, (actual, anterior) -> {
            assertThat(actual).isEqualTo(anterior);
            assertThat(actual.getDueno().getId()).isEqualTo(anterior.getDueno().getId());
        });
        assertThat(turnoRepository.findAll(orden)).zipSatisfy(turnosAntes, (actual, anterior) -> {
            assertThat(actual).isEqualTo(anterior);
            assertThat(actual.getMascota().getId()).isEqualTo(anterior.getMascota().getId());
            assertThat(actual.getVeterinario().getId()).isEqualTo(anterior.getVeterinario().getId());
        });
        assertThat(veterinarioRepository.findAll(orden)).containsExactlyElementsOf(veterinariosAntes);
        mvc.perform(get("/api/duenos/{id}", dueno.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dueno.getId()));
    }

    private Dueno duenoExistente() {
        return duenoRepository.findByEmail("carlos.gonzalez@example.com").orElseThrow();
    }
}
