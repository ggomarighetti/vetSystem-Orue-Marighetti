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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MedicamentoIntegrationTest {

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
    void medicamentoCrudValidaYNoExponeEntidad() throws Exception {
        mockMvc.perform(post("/api/medicamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datosMedicamento(-1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", containsString("stock")));

        long id = crearMedicamento(3);
        mockMvc.perform(get("/api/medicamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].principioActivo").value("Amoxicilina"));
        mockMvc.perform(get("/api/medicamentos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(3));
        mockMvc.perform(put("/api/medicamentos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datosMedicamento(5)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(5));
        mockMvc.perform(delete("/api/medicamentos/{id}", id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/medicamentos/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void recetarDescuentaUnaUnidadYRechazaSinStock() throws Exception {
        Dueno dueno = crearDueno();
        Mascota mascota = crearMascota(dueno);
        Veterinario veterinario = crearVeterinario();
        long primerTurno = crearTurno(mascota, veterinario, "10:00:00");
        long segundoTurno = crearTurno(mascota, veterinario, "11:00:00");
        long medicamento = crearMedicamento(1);

        mockMvc.perform(post("/api/turnos/{turnoId}/medicamentos/{medicamentoId}", primerTurno, medicamento))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(0));
        mockMvc.perform(get("/api/turnos/{id}/medicamentos", primerTurno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(medicamento));
        mockMvc.perform(post("/api/turnos/{turnoId}/medicamentos/{medicamentoId}", primerTurno, medicamento))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/turnos/{turnoId}/medicamentos/{medicamentoId}", segundoTurno, medicamento))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.mensaje", containsString("stock")));
        mockMvc.perform(get("/api/medicamentos/{id}", medicamento))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(0));
        mockMvc.perform(delete("/api/medicamentos/{id}", medicamento))
                .andExpect(status().isConflict());
    }

    private long crearMedicamento(int stock) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/medicamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datosMedicamento(stock)))
                .andExpect(status().isCreated())
                .andReturn();
        return idDe(resultado);
    }

    private long crearTurno(Mascota mascota, Veterinario veterinario, String hora) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datosTurno(mascota, veterinario, hora)))
                .andExpect(status().isCreated())
                .andReturn();
        return idDe(resultado);
    }

    private long idDe(MvcResult resultado) throws Exception {
        return objectMapper.readTree(resultado.getResponse().getContentAsString()).get("id").asLong();
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

    private String datosMedicamento(int stock) {
        return """
                {"nombre":"Amoxicilina 500","principioActivo":"Amoxicilina","stock":%d,"precioUnitario":1250.50}
                """.formatted(stock);
    }

    private String datosTurno(Mascota mascota, Veterinario veterinario, String hora) {
        return """
                {"fecha":"2026-10-15","hora":"%s","motivo":"Consulta general","mascotaId":%d,"veterinarioId":%d}
                """.formatted(hora, mascota.getId(), veterinario.getId());
    }
}
