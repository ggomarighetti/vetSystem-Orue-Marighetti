package com.vetSystem.vet_system.controller;

import com.vetSystem.vet_system.dto.VeterinarioDTO;
import com.vetSystem.vet_system.service.VeterinarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/veterinarios")
@RequiredArgsConstructor
@Tag(name = "Veterinarios", description = "Gestión de profesionales veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    @GetMapping
    @Operation(summary = "Listar veterinarios", description = "Devuelve todos los profesionales registrados.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<VeterinarioDTO>> getAllVeterinarios() {
        return ResponseEntity.ok(veterinarioService.getAllVeterinarios());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar veterinario", description = "Busca un profesional por su identificador.")
    @ApiResponse(responseCode = "200", description = "Veterinario encontrado")
    @ApiResponse(responseCode = "400", description = "Identificador inválido")
    @ApiResponse(responseCode = "404", description = "Veterinario inexistente")
    public ResponseEntity<VeterinarioDTO> getVeterinarioById(
            @Parameter(description = "Identificador del veterinario", example = "2") @PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.getVeterinarioById(id));
    }

    @PostMapping
    @Operation(summary = "Crear veterinario", description = "Registra un profesional con matrícula única.")
    @ApiResponse(responseCode = "201", description = "Veterinario creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Matrícula ya registrada")
    public ResponseEntity<VeterinarioDTO> createVeterinario(
            @Validated(VeterinarioDTO.Creacion.class) @Valid @RequestBody VeterinarioDTO veterinario) {
        VeterinarioDTO nuevo = veterinarioService.createVeterinario(veterinario);
        return ResponseEntity.created(URI.create("/api/veterinarios/" + nuevo.getId())).body(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar veterinario", description = "Actualiza los datos del profesional sin cambiar su matrícula.")
    @ApiResponse(responseCode = "200", description = "Veterinario actualizado")
    @ApiResponse(responseCode = "400", description = "Datos o identificador inválidos")
    @ApiResponse(responseCode = "404", description = "Veterinario inexistente")
    public ResponseEntity<VeterinarioDTO> updateVeterinario(
            @Parameter(description = "Identificador del veterinario", example = "2") @PathVariable Long id,
            @Valid @RequestBody VeterinarioDTO veterinario) {
        return ResponseEntity.ok(veterinarioService.updateVeterinario(id, veterinario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar veterinario", description = "Elimina un profesional sin turnos asociados.")
    @ApiResponse(responseCode = "204", description = "Veterinario eliminado")
    @ApiResponse(responseCode = "400", description = "Identificador inválido")
    @ApiResponse(responseCode = "404", description = "Veterinario inexistente")
    @ApiResponse(responseCode = "409", description = "Veterinario con turnos asociados")
    public ResponseEntity<Void> deleteVeterinario(
            @Parameter(description = "Identificador del veterinario", example = "2") @PathVariable Long id) {
        veterinarioService.deleteVeterinario(id);
        return ResponseEntity.noContent().build();
    }
}
