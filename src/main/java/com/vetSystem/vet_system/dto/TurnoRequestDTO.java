package com.vetSystem.vet_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoRequestDTO {

    @Schema(description = "Fecha del turno, actual o futura", example = "2026-10-15")
    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser actual o futura")
    private LocalDate fecha;

    @Schema(description = "Hora de inicio del turno", example = "14:30:00")
    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @Schema(description = "Motivo de la consulta", example = "Control anual y vacunación")
    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivo;

    @Schema(description = "Identificador de la mascota atendida", example = "3")
    @NotNull(message = "El identificador de la mascota es obligatorio")
    @Positive(message = "El identificador de la mascota debe ser positivo")
    private Long mascotaId;

    @Schema(description = "Identificador del veterinario asignado", example = "2")
    @NotNull(message = "El identificador del veterinario es obligatorio")
    @Positive(message = "El identificador del veterinario debe ser positivo")
    private Long veterinarioId;
}
