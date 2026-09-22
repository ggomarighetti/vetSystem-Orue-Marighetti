package com.vetSystem.vet_system;

import com.vetSystem.vet_system.model.Dueno;
import com.vetSystem.vet_system.repository.DuenoRepository;
import com.vetSystem.vet_system.support.ApiIntegrationTest;
import com.vetSystem.vet_system.support.DatabaseAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import static com.vetSystem.vet_system.support.DuenoTestData.datosActualizacion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiIntegrationTest
@Sql({"/fixtures/duenos/dueno.sql", "/fixtures/duenos/mascotas.sql"})
class DuenoRelacionesApiTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DuenoRepository repository;

    @Autowired
    private DatabaseAssertions database;

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
        database.assertDuenoDeMascota("Luna", dueno.getId());
        database.assertDuenoDeMascota("Milo", dueno.getId());
    }

    @Test
    void eliminarDuenoConMascotasSinTurnosDevuelve409YConservaTodasLasDependencias() throws Exception {
        var anterior = database.snapshot();
        assertThat(anterior.mascotas()).hasSize(2);
        assertThat(anterior.turnos()).isEmpty();
        verificarBorradoRechazado(anterior);
    }

    @Test
    @Sql("/fixtures/duenos/turno.sql")
    void eliminarDuenoConMascotasYTurnoDevuelve409YConservaTodasLasDependencias() throws Exception {
        var anterior = database.snapshot();
        assertThat(anterior.mascotas()).hasSize(2);
        assertThat(anterior.turnos()).hasSize(1);
        assertThat(anterior.veterinarios()).hasSize(1);
        verificarBorradoRechazado(anterior);
    }

    private void verificarBorradoRechazado(DatabaseAssertions.Snapshot anterior) throws Exception {
        Dueno dueno = duenoExistente();
        mvc.perform(delete("/api/duenos/{id}", dueno.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Dueño con id " + dueno.getId()
                        + " tiene registros asociados y no puede eliminarse"));

        database.assertSinCambios(anterior);
        mvc.perform(get("/api/duenos/{id}", dueno.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dueno.getId()));
    }

    private Dueno duenoExistente() {
        return repository.findByEmail("carlos.gonzalez@example.com").orElseThrow();
    }
}
