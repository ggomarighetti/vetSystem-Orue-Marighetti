package com.vetSystem.vet_system.controller;

import com.vetSystem.vet_system.dto.MascotaDTO;
import com.vetSystem.vet_system.service.MascotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
@Tag(name = "Mascotas", description = "Gestión de mascotas de la clínica")
public class MascotaController {

    private final MascotaService mascotaService;

    @GetMapping
    @Operation(summary = "Listar mascotas", description = "Devuelve todas las mascotas registradas.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<MascotaDTO>> getAllMascotas() {
        return ResponseEntity.ok(mascotaService.getAllMascotas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar mascota", description = "Busca una mascota por su identificador.")
    @ApiResponse(responseCode = "200", description = "Mascota encontrada")
    @ApiResponse(responseCode = "400", description = "Identificador inválido")
    @ApiResponse(responseCode = "404", description = "Mascota inexistente")
    @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador de la mascota", example = "3")})
    public ResponseEntity<MascotaDTO> getMascotaById(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.getMascotaById(id));
    }

    @PostMapping
    @Operation(summary = "Crear mascota", description = "Registra una mascota asociada a un dueño existente.")
    @ApiResponse(responseCode = "201", description = "Mascota creada")
    @ApiResponse(responseCode = "400", description = "Datos o identificador del dueño inválidos")
    @ApiResponse(responseCode = "404", description = "Dueño inexistente")
    @ApiResponse(responseCode = "409", description = "Nombre de mascota repetido para el dueño")
    @Parameters({@Parameter(name = "duenoId", in = ParameterIn.QUERY, description = "Identificador del dueño", example = "1")})
    public ResponseEntity<MascotaDTO> createMascota(
            @RequestParam Long duenoId,
            @Valid @RequestBody MascotaDTO mascota) {
        MascotaDTO nueva = mascotaService.createMascota(duenoId, mascota);
        return ResponseEntity.created(URI.create("/api/mascotas/" + nueva.getId())).body(nueva);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mascota", description = "Actualiza los datos de una mascota registrada.")
    @ApiResponse(responseCode = "200", description = "Mascota actualizada")
    @ApiResponse(responseCode = "400", description = "Datos o identificador inválidos")
    @ApiResponse(responseCode = "404", description = "Mascota inexistente")
    @ApiResponse(responseCode = "409", description = "Nombre de mascota repetido para el dueño")
    @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador de la mascota", example = "3")})
    public ResponseEntity<MascotaDTO> updateMascota(
            @PathVariable Long id,
            @Valid @RequestBody MascotaDTO mascota) {
        return ResponseEntity.ok(mascotaService.updateMascota(id, mascota));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mascota", description = "Elimina una mascota sin turnos asociados.")
    @ApiResponse(responseCode = "204", description = "Mascota eliminada")
    @ApiResponse(responseCode = "400", description = "Identificador inválido")
    @ApiResponse(responseCode = "404", description = "Mascota inexistente")
    @ApiResponse(responseCode = "409", description = "Mascota con turnos asociados")
    @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador de la mascota", example = "3")})
    public ResponseEntity<Void> deleteMascota(@PathVariable Long id) {
        mascotaService.deleteMascota(id);
        return ResponseEntity.noContent().build();
    }
}
