package com.vetSystem.vet_system;

import com.vetSystem.vet_system.model.Dueno;
import com.vetSystem.vet_system.model.Mascota;
import com.vetSystem.vet_system.model.Veterinario;
import com.vetSystem.vet_system.repository.DuenoRepository;
import com.vetSystem.vet_system.repository.MascotaRepository;
import com.vetSystem.vet_system.repository.VeterinarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TurnoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Test
    void turnoConflictivoInformaIdYHorario() throws Exception {
        Dueno dueno = crearDueno();
        Mascota mascota = crearMascota(dueno);
        Veterinario veterinario = crearVeterinario();
        String datos = datosTurno(mascota, veterinario);
        MvcResult resultado = mockMvc.perform(post("/api/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datos))
                .andExpect(status().isCreated())
                .andReturn();
        long id = objectMapper.readTree(resultado.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datos))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje", containsString("turno " + id)))
                .andExpect(jsonPath("$.mensaje", containsString("2026-10-15")))
                .andExpect(jsonPath("$.mensaje", containsString("10:00")));
    }

    private Dueno crearDueno() {
        Dueno dueno = new Dueno();
        dueno.setNombre("Ana");
        dueno.setApellido("Pérez");
        dueno.setDni("30123456");
        dueno.setEmail("ana@example.com");
        return duenoRepository.saveAndFlush(dueno);
    }

    private Mascota crearMascota(Dueno dueno) {
        Mascota mascota = new Mascota();
        mascota.setNombre("Luna");
        mascota.setEspecie("Perro");
        mascota.setDueno(dueno);
        return mascotaRepository.saveAndFlush(mascota);
    }

    private Veterinario crearVeterinario() {
        Veterinario veterinario = new Veterinario();
        veterinario.setNombre("Lucía");
        veterinario.setApellido("Fernández");
        veterinario.setMatricula("MV-12345");
        veterinario.setEspecialidad("Clínica general");
        return veterinarioRepository.saveAndFlush(veterinario);
    }

    private String datosTurno(Mascota mascota, Veterinario veterinario) {
        return """
                {"fecha":"2026-10-15","hora":"10:00:00","motivo":"Consulta general","mascotaId":%d,"veterinarioId":%d}
                """.formatted(mascota.getId(), veterinario.getId());
    }
}
