package com.vetSystem.vet_system.controller;

import com.vetSystem.vet_system.dto.DuenoDTO;
import com.vetSystem.vet_system.exception.ResourceNotFoundException;
import com.vetSystem.vet_system.service.DuenoService;
import com.vetSystem.vet_system.service.MascotaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DuenoController.class)
class DuenoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DuenoService duenoService;

    @MockitoBean
    private MascotaService mascotaService;

    @Test
    void getAllDuenos_cuandoListaVacia_retorna200() throws Exception {
        when(duenoService.getAllDuenos()).thenReturn(List.of());

        mockMvc.perform(get("/api/duenos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(duenoService).getAllDuenos();
    }

    @Test
    void getDuenoById_cuandoExiste_retorna200YNombre() throws Exception {
        when(duenoService.getDuenoById(1L)).thenReturn(duenoExistente());

        mockMvc.perform(get("/api/duenos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos"));

        verify(duenoService).getDuenoById(1L);
    }

    @Test
    void getDuenoById_cuandoNoExiste_retorna404() throws Exception {
        when(duenoService.getDuenoById(99L))
                .thenThrow(new ResourceNotFoundException("Dueño con id 99 no fue encontrado"));

        mockMvc.perform(get("/api/duenos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Dueño con id 99 no fue encontrado"))
                .andExpect(jsonPath("$.path").value("/api/duenos/99"));

        verify(duenoService).getDuenoById(99L);
    }

    @Test
    void createDueno_cuandoDatosValidos_retorna201() throws Exception {
        when(duenoService.createDueno(any(DuenoDTO.class))).thenReturn(duenoExistente());

        mockMvc.perform(post("/api/duenos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/duenos/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos"));

        verify(duenoService).createDueno(any(DuenoDTO.class));
    }

    @Test
    void createDueno_cuandoEmailVacio_retorna400SinInvocarServicio() throws Exception {
        String jsonInvalido = jsonValido().replace("carlos@example.com", "");

        mockMvc.perform(post("/api/duenos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("email: El email es obligatorio"))
                .andExpect(jsonPath("$.path").value("/api/duenos"));

        verifyNoInteractions(duenoService);
    }

    private DuenoDTO duenoExistente() {
        return new DuenoDTO(1L, "Carlos", "Pérez", "12345678", "1122334455", "carlos@example.com");
    }

    private String jsonValido() {
        return """
                {
                  "nombre": "Carlos",
                  "apellido": "Pérez",
                  "dni": "12345678",
                  "telefono": "1122334455",
                  "email": "carlos@example.com"
                }
                """;
    }
}
