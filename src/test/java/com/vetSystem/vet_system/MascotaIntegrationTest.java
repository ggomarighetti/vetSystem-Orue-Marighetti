package com.vetSystem.vet_system;

import com.vetSystem.vet_system.model.Dueno;
import com.vetSystem.vet_system.repository.DuenoRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MascotaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DuenoRepository duenoRepository;

    @Test
    void cupoCuentaMascotasExistentesYSeLiberaAlEliminar() throws Exception {
        Dueno dueno = crearDueno();
        long primera = 0;
        for (int indice = 1; indice <= 5; indice++) {
            MvcResult resultado = mockMvc.perform(post("/api/mascotas")
                            .param("duenoId", dueno.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(datosMascota("Mascota" + indice)))
                    .andExpect(status().isCreated())
                    .andReturn();
            if (indice == 1) {
                primera = objectMapper.readTree(resultado.getResponse().getContentAsString()).get("id").asLong();
            }
        }

        mockMvc.perform(post("/api/mascotas")
                        .param("duenoId", dueno.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datosMascota("Mascota6")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.mensaje", containsString("5 mascotas activas")));
        mockMvc.perform(delete("/api/mascotas/{id}", primera))
                .andExpect(status().isNoContent());
        mockMvc.perform(post("/api/mascotas")
                        .param("duenoId", dueno.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datosMascota("Mascota6")))
                .andExpect(status().isCreated());
    }

    private Dueno crearDueno() {
        Dueno dueno = new Dueno();
        dueno.setNombre("Ana");
        dueno.setApellido("Pérez");
        dueno.setDni("30123456");
        dueno.setEmail("ana@example.com");
        return duenoRepository.saveAndFlush(dueno);
    }

    private String datosMascota(String nombre) {
        return """
                {"nombre":"%s","especie":"Perro"}
                """.formatted(nombre);
    }
}
