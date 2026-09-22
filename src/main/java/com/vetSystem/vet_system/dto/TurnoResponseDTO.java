package com.vetSystem.vet_system.dto;

import com.vetSystem.vet_system.model.EstadoTurno;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoResponseDTO {

    @Schema(description = "Identificador del turno", example = "7")
    private Long id;
    @Schema(description = "Fecha del turno", example = "2026-10-15")
    private LocalDate fecha;
    @Schema(description = "Hora de inicio", example = "14:30:00")
    private LocalTime hora;
    @Schema(description = "Motivo de la consulta", example = "Control anual y vacunación")
    private String motivo;
    @Schema(description = "Estado actual del turno", example = "PENDIENTE")
    private EstadoTurno estado;
    @Schema(description = "Observaciones de la atención", example = "Paciente en buen estado")
    private String observaciones;
    @Schema(description = "Nombre de la mascota atendida", example = "Luna")
    private String mascotaNombre;
    @Schema(description = "Nombre del veterinario asignado", example = "Lucía Fernández")
    private String veterinarioNombre;
}
