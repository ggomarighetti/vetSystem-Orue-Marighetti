package com.vetSystem.vet_system.controller;

import com.vetSystem.vet_system.dto.TurnoRequestDTO;
import com.vetSystem.vet_system.dto.TurnoResponseDTO;
import com.vetSystem.vet_system.model.EstadoTurno;
import com.vetSystem.vet_system.service.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
@Tag(name = "Turnos", description = "Agenda y gestión de turnos veterinarios")
public class TurnoController {

    private final TurnoService turnoService;

    @GetMapping
    @Operation(summary = "Listar turnos", description = "Lista todos los turnos o filtra por veterinario y fecha cuando se indican los parámetros.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    @ApiResponse(responseCode = "400", description = "Filtros inválidos")
    @ApiResponse(responseCode = "404", description = "Veterinario del filtro inexistente")
    public ResponseEntity<List<TurnoResponseDTO>> getAllTurnos(
            @Parameter(description = "Identificador del veterinario", example = "2") @RequestParam(required = false) Long veterinarioId,
            @Parameter(description = "Fecha de la agenda en formato ISO", example = "2026-10-15") @RequestParam(required = false) LocalDate fecha) {
        if (veterinarioId != null || fecha != null) {
            return ResponseEntity.ok(turnoService.getTurnosByVeterinarioYFecha(veterinarioId, fecha));
        }
        return ResponseEntity.ok(turnoService.getAllTurnos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar turno", description = "Busca un turno por su identificador.")
    @ApiResponse(responseCode = "200", description = "Turno encontrado")
    @ApiResponse(responseCode = "400", description = "Identificador inválido")
    @ApiResponse(responseCode = "404", description = "Turno inexistente")
    public ResponseEntity<TurnoResponseDTO> getTurnoById(
            @Parameter(description = "Identificador del turno", example = "7") @PathVariable Long id) {
        return ResponseEntity.ok(turnoService.getTurnoById(id));
    }

    @GetMapping("/agenda")
    @Operation(summary = "Consultar agenda", description = "Devuelve los turnos de un veterinario en una fecha concreta.")
    @ApiResponse(responseCode = "200", description = "Agenda obtenida")
    @ApiResponse(responseCode = "400", description = "Parámetros ausentes o inválidos")
    @ApiResponse(responseCode = "404", description = "Veterinario inexistente")
    public ResponseEntity<List<TurnoResponseDTO>> getAgenda(
            @Parameter(description = "Identificador del veterinario", example = "2") @RequestParam Long veterinarioId,
            @Parameter(description = "Fecha de la agenda en formato ISO", example = "2026-10-15") @RequestParam LocalDate fecha) {
        return ResponseEntity.ok(turnoService.getTurnosByVeterinarioYFecha(veterinarioId, fecha));
    }

    @PostMapping
    @Operation(summary = "Crear turno", description = "Agenda una mascota con un veterinario; rechaza referencias inexistentes y horarios ocupados.")
    @ApiResponse(responseCode = "201", description = "Turno creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Mascota o veterinario inexistente")
    @ApiResponse(responseCode = "409", description = "Horario ya ocupado")
    public ResponseEntity<TurnoResponseDTO> createTurno(@Valid @RequestBody TurnoRequestDTO request) {
        TurnoResponseDTO nuevo = turnoService.createTurno(request);
        return ResponseEntity.created(URI.create("/api/turnos/" + nuevo.getId())).body(nuevo);
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del turno", description = "Actualiza el estado y, opcionalmente, las observaciones de un turno.")
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    @ApiResponse(responseCode = "400", description = "Estado u observaciones inválidos")
    @ApiResponse(responseCode = "404", description = "Turno inexistente")
    public ResponseEntity<TurnoResponseDTO> actualizarEstado(
            @Parameter(description = "Identificador del turno", example = "7") @PathVariable Long id,
            @Parameter(description = "Nuevo estado del turno", example = "EN_CURSO") @RequestParam EstadoTurno estado,
            @Parameter(description = "Observaciones de hasta 255 caracteres", example = "Paciente confirmado") @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(turnoService.actualizarEstado(id, estado, observaciones));
    }
}
