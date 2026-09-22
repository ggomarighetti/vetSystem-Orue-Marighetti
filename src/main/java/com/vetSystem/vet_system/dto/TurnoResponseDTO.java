package com.vetSystem.vet_system.dto;

import com.vetSystem.vet_system.model.EstadoTurno;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoResponseDTO {

    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private EstadoTurno estado;
    private String observaciones;
    private String mascotaNombre;
    private String veterinarioNombre;
}
