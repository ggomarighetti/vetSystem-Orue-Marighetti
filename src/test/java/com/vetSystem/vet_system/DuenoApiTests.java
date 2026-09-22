package com.vetSystem.vet_system;

import com.vetSystem.vet_system.model.Dueno;
import com.vetSystem.vet_system.repository.DuenoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:dueno_api_test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.jpa.open-in-view=false"
})
@AutoConfigureMockMvc
class DuenoApiTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DuenoRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void limpiarDatos() {
        jdbc.update("delete from turnos");
        jdbc.update("delete from mascotas");
        repository.deleteAll();
        jdbc.update("delete from veterinarios");
    }

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
                        repository.findByEmail("carlos.gonzalez@example.com").orElseThrow().getId()));

        assertThat(repository.existsByDni("28543210")).isTrue();
        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.findByEmail("ausente@example.com")).isEmpty();
    }

    @Test
    void consultarYListarDevuelveLosDatosPersistidos() throws Exception {
        Dueno dueno = guardarDueno();
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
    void dniDuplicadoDevuelveConflictoSinModificarElOriginal() throws Exception {
        guardarDueno();
        Dueno datos = datosDueno();
        datos.setNombre("María");
        datos.setDni(" 28543210 ");
        mvc.perform(post("/api/duenos").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un dueño con DNI: 28543210"));
        assertThat(repository.count()).isEqualTo(1);
        assertThat(repository.findAll().getFirst().getNombre()).isEqualTo("Carlos");
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
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void actualizarSinDniConservaElIdentificadorDeNegocio() throws Exception {
        Dueno dueno = guardarDueno();
        ObjectNode datos = objectMapper.valueToTree(datosActualizacion());
        datos.remove("dni");
        mvc.perform(put("/api/duenos/{id}", dueno.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos Alberto"))
                .andExpect(jsonPath("$.telefono").value("1199887766"))
                .andExpect(jsonPath("$.dni").value("28543210"));
        Dueno actualizado = repository.findById(dueno.getId()).orElseThrow();
        assertThat(actualizado.getNombre()).isEqualTo("Carlos Alberto");
        assertThat(actualizado.getEmail()).isEqualTo("carlos.nuevo@example.com");
    }

    @Test
    void actualizarIgnoraIdYDniDelCuerpoYPermiteQuitarTelefono() throws Exception {
        Dueno dueno = guardarDueno();
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
        assertThat(repository.findById(dueno.getId()).orElseThrow().getTelefono()).isNull();
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void crearConIdAjenoNoSobrescribeOtroDuenoNiCreaMascotas() throws Exception {
        Dueno original = guardarDueno();
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
        assertThat(repository.count()).isEqualTo(2);
        assertThat(repository.findById(original.getId()).orElseThrow().getNombre()).isEqualTo("Carlos");
        assertThat(jdbc.queryForObject("select count(*) from mascotas", Long.class)).isZero();
    }

    @Test
    void consultarYActualizarUnDuenoConMascotasNoSerializaElGrafoNiCambiaLaRelacion() throws Exception {
        Dueno dueno = guardarDueno();
        jdbc.update("insert into mascotas (nombre, especie, dueno_id) values (?, ?, ?)",
                "Luna", "Perro", dueno.getId());
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
        assertThat(jdbc.queryForObject("select dueno_id from mascotas where nombre = 'Luna'", Long.class))
                .isEqualTo(dueno.getId());
    }

    @Test
    void eliminarDevuelve204SinCuerpoYLasSiguientesOperacionesDevuelven404() throws Exception {
        Dueno dueno = guardarDueno();
        mvc.perform(delete("/api/duenos/{id}", dueno.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        assertThat(repository.existsById(dueno.getId())).isFalse();
        mvc.perform(get("/api/duenos/{id}", dueno.getId())).andExpect(status().isNotFound());
        mvc.perform(delete("/api/duenos/{id}", dueno.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Dueño con id " + dueno.getId() + " no fue encontrado"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void eliminarDuenoConMascotasDevuelve409YConservaTodasLasDependencias(boolean conTurno) throws Exception {
        Dueno dueno = guardarDueno();
        jdbc.update("insert into mascotas (nombre, especie, dueno_id) values (?, ?, ?)",
                "Luna", "Perro", dueno.getId());
        jdbc.update("insert into mascotas (nombre, especie, dueno_id) values (?, ?, ?)",
                "Milo", "Gato", dueno.getId());
        if (conTurno) {
            Long mascotaId = jdbc.queryForObject("select id from mascotas where nombre = 'Milo'", Long.class);
            jdbc.update("insert into veterinarios (nombre, apellido, especialidad, matricula) values (?, ?, ?, ?)",
                    "Ana", "Pérez", "Clínica", "VET-123");
            Long veterinarioId = jdbc.queryForObject("select id from veterinarios where matricula = 'VET-123'", Long.class);
            jdbc.update("""
                    insert into turnos (fecha, hora, motivo, estado, mascota_id, veterinario_id)
                    values (DATE '2026-10-01', TIME '10:00:00', ?, ?, ?, ?)
                    """, "Control", "PENDIENTE", mascotaId, veterinarioId);
        }
        var duenosAntes = jdbc.queryForList("select * from duenos order by id");
        var mascotasAntes = jdbc.queryForList("select * from mascotas order by id");
        var turnosAntes = jdbc.queryForList("select * from turnos order by id");
        var veterinariosAntes = jdbc.queryForList("select * from veterinarios order by id");

        mvc.perform(delete("/api/duenos/{id}", dueno.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Dueño con id " + dueno.getId()
                        + " tiene registros asociados y no puede eliminarse"));

        assertThat(jdbc.queryForList("select * from duenos order by id")).isEqualTo(duenosAntes);
        assertThat(jdbc.queryForList("select * from mascotas order by id")).isEqualTo(mascotasAntes);
        assertThat(jdbc.queryForList("select * from turnos order by id")).isEqualTo(turnosAntes);
        assertThat(jdbc.queryForList("select * from veterinarios order by id")).isEqualTo(veterinariosAntes);
        mvc.perform(get("/api/duenos/{id}", dueno.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dueno.getId()));
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
        assertThat(repository.count()).isZero();
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
        assertThat(repository.count()).isZero();
    }

    @Test
    void actualizarConDatosInvalidosNoModificaLoGuardado() throws Exception {
        Dueno dueno = guardarDueno();
        mvc.perform(put("/api/duenos/{id}", dueno.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(objectMapper.createObjectNode())))
                .andExpect(status().isBadRequest());
        assertThat(repository.findById(dueno.getId()).orElseThrow().getNombre()).isEqualTo("Carlos");
    }

    private Dueno guardarDueno() {
        return repository.saveAndFlush(datosDueno());
    }

    private Dueno datosDueno() {
        Dueno dueno = new Dueno();
        dueno.setNombre("Carlos");
        dueno.setApellido("González");
        dueno.setDni("28543210");
        dueno.setTelefono("1145678901");
        dueno.setEmail("carlos.gonzalez@example.com");
        return dueno;
    }

    private Dueno datosActualizacion() {
        Dueno dueno = new Dueno();
        dueno.setNombre("Carlos Alberto");
        dueno.setApellido("González");
        dueno.setTelefono("1199887766");
        dueno.setEmail("carlos.nuevo@example.com");
        return dueno;
    }
}
