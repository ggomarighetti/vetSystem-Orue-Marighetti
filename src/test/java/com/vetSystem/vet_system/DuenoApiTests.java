package com.vetSystem.vet_system;

import com.vetSystem.vet_system.model.Dueno;
import com.vetSystem.vet_system.support.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.vetSystem.vet_system.support.DuenoTestData.datosActualizacion;
import static com.vetSystem.vet_system.support.DuenoTestData.datosDueno;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DuenoApiTests extends IntegrationTest {

    @Test
    void listarSinDuenosDevuelveListaVacia() throws Exception {
        mvc.perform(get("/api/duenos"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void crearPersisteElDuenoYDevuelveSuUbicacion() throws Exception {
        mvc.perform(post("/api/duenos").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datosDueno())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.dni").value("28543210"))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.mascotas").doesNotExist())
                .andExpect(header().string("Location", "/api/duenos/" +
                        duenoRepository.findByEmail("carlos.gonzalez@example.com").orElseThrow().getId()));

        assertThat(duenoRepository.existsByDni("28543210")).isTrue();
        assertThat(duenoRepository.count()).isEqualTo(1);
        assertThat(duenoRepository.findByEmail("ausente@example.com")).isEmpty();
    }

    @Test
    @Sql("/fixtures/duenos/dueno.sql")
    void consultarYListarDevuelveLosDatosPersistidos() throws Exception {
        Dueno dueno = duenoExistente();
        mvc.perform(get("/api/duenos/{id}", dueno.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dueno.getId()))
                .andExpect(jsonPath("$.apellido").value("González"));
        mvc.perform(get("/api/duenos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].dni").value("28543210"));
    }

    @Test
    @Sql("/fixtures/duenos/dueno.sql")
    void dniDuplicadoDevuelveConflictoSinModificarElOriginal() throws Exception {
        Dueno datos = datosDueno();
        datos.setNombre("María");
        datos.setDni(" 28543210 ");
        mvc.perform(post("/api/duenos").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un dueño con DNI: 28543210"));
        assertThat(duenoRepository.count()).isEqualTo(1);
        assertThat(duenoRepository.findAll().getFirst().getNombre()).isEqualTo("Carlos");
    }

    @Test
    void altasConcurrentesConElMismoDniCreanUnSoloDueno() throws Exception {
        String cuerpo = objectMapper.writeValueAsString(datosDueno());
        var inicio = new CountDownLatch(1);
        var respuestas = new ArrayList<Future<Integer>>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 8; i++) {
                respuestas.add(executor.submit(() -> {
                    if (!inicio.await(10, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("No comenzó la prueba concurrente");
                    }
                    return mvc.perform(post("/api/duenos").contentType(MediaType.APPLICATION_JSON)
                                    .content(cuerpo)).andReturn().getResponse().getStatus();
                }));
            }
            inicio.countDown();
            var estados = new ArrayList<Integer>();
            for (Future<Integer> respuesta : respuestas) {
                estados.add(respuesta.get(20, TimeUnit.SECONDS));
            }
            assertThat(estados).containsExactlyInAnyOrder(201, 409, 409, 409, 409, 409, 409, 409);
        }
        assertThat(duenoRepository.count()).isEqualTo(1);
    }

    @Test
    @Sql("/fixtures/duenos/dueno.sql")
    void actualizarSinDniConservaElIdentificadorDeNegocio() throws Exception {
        Dueno dueno = duenoExistente();
        ObjectNode datos = objectMapper.valueToTree(datosActualizacion());
        datos.remove("dni");
        mvc.perform(put("/api/duenos/{id}", dueno.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos Alberto"))
                .andExpect(jsonPath("$.telefono").value("1199887766"))
                .andExpect(jsonPath("$.dni").value("28543210"));
        Dueno actualizado = duenoRepository.findById(dueno.getId()).orElseThrow();
        assertThat(actualizado.getNombre()).isEqualTo("Carlos Alberto");
        assertThat(actualizado.getEmail()).isEqualTo("carlos.nuevo@example.com");
    }

    @Test
    @Sql("/fixtures/duenos/dueno.sql")
    void actualizarIgnoraIdYDniDelCuerpoYPermiteQuitarTelefono() throws Exception {
        Dueno dueno = duenoExistente();
        Dueno actualizacion = datosActualizacion();
        actualizacion.setId(999L);
        actualizacion.setDni("11111111");
        ObjectNode datos = objectMapper.valueToTree(actualizacion);
        datos.remove("telefono");
        mvc.perform(put("/api/duenos/{id}", dueno.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dueno.getId()))
                .andExpect(jsonPath("$.dni").value("28543210"));
        assertThat(duenoRepository.findById(dueno.getId()).orElseThrow().getTelefono()).isNull();
        assertThat(duenoRepository.count()).isEqualTo(1);
    }

    @Test
    @Sql("/fixtures/duenos/dueno.sql")
    void crearConIdAjenoNoSobrescribeOtroDuenoNiCreaMascotas() throws Exception {
        Dueno original = duenoExistente();
        Dueno nuevo = datosDueno();
        nuevo.setId(original.getId());
        nuevo.setDni("30123456");
        nuevo.setNombre("María");
        nuevo.setApellido("López");
        nuevo.setEmail("maria@example.com");
        ObjectNode datos = objectMapper.valueToTree(nuevo);
        datos.putArray("mascotas").addObject()
                .put("nombre", "Luna")
                .put("especie", "Perro");
        mvc.perform(post("/api/duenos").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dni").value("30123456"));
        assertThat(duenoRepository.count()).isEqualTo(2);
        assertThat(duenoRepository.findById(original.getId()).orElseThrow().getNombre()).isEqualTo("Carlos");
        assertThat(mascotaRepository.count()).isZero();
    }

    @Test
    @Sql("/fixtures/duenos/dueno.sql")
    void eliminarDevuelve204SinCuerpoYLasSiguientesOperacionesDevuelven404() throws Exception {
        Dueno dueno = duenoExistente();
        mvc.perform(delete("/api/duenos/{id}", dueno.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        assertThat(duenoRepository.existsById(dueno.getId())).isFalse();
        mvc.perform(get("/api/duenos/{id}", dueno.getId())).andExpect(status().isNotFound());
        mvc.perform(delete("/api/duenos/{id}", dueno.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Dueño con id " + dueno.getId() + " no fue encontrado"));
    }

    @Test
    void idInexistenteDevuelve404EnConsultaActualizacionYBorrado() throws Exception {
        mvc.perform(get("/api/duenos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Dueño con id 999 no fue encontrado"));
        mvc.perform(put("/api/duenos/999").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datosActualizacion())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").isNotEmpty());
        mvc.perform(delete("/api/duenos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"nombre", "apellido", "dni", "email"})
    void crearRechazaCamposObligatoriosEnBlanco(String campo) throws Exception {
        ObjectNode datos = objectMapper.valueToTree(datosDueno());
        datos.put(campo, " ");
        mvc.perform(post("/api/duenos").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El campo " + campo + " es obligatorio"));
        assertThat(duenoRepository.count()).isZero();
    }

    @Test
    void crearRechazaDatosAusentesOLargosYJsonMalformado() throws Exception {
        mvc.perform(post("/api/duenos").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(objectMapper.createObjectNode())))
                .andExpect(status().isBadRequest());
        Dueno datos = datosDueno();
        datos.setNombre("C".repeat(256));
        mvc.perform(post("/api/duenos").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El campo nombre no puede superar 255 caracteres"));
        mvc.perform(post("/api/duenos").contentType(MediaType.APPLICATION_JSON).content("{malformado"))
                .andExpect(status().isBadRequest());
        assertThat(duenoRepository.count()).isZero();
    }

    @Test
    @Sql("/fixtures/duenos/dueno.sql")
    void actualizarConDatosInvalidosNoModificaLoGuardado() throws Exception {
        Dueno dueno = duenoExistente();
        mvc.perform(put("/api/duenos/{id}", dueno.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(objectMapper.createObjectNode())))
                .andExpect(status().isBadRequest());
        assertThat(duenoRepository.findById(dueno.getId()).orElseThrow().getNombre()).isEqualTo("Carlos");
    }

    private Dueno duenoExistente() {
        return duenoRepository.findByEmail("carlos.gonzalez@example.com").orElseThrow();
    }
}
