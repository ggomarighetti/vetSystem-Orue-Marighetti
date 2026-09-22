package com.vetSystem.vet_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaDTO {

    @Schema(description = "Identificador asignado por el sistema", example = "3", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre de la mascota", example = "Luna")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
    private String nombre;

    @Schema(description = "Especie de la mascota", example = "Perro")
    @NotBlank(message = "La especie es obligatoria")
    @Size(max = 255, message = "La especie no puede superar 255 caracteres")
    private String especie;

    @Schema(description = "Raza de la mascota", example = "Mestiza")
    @Size(max = 255, message = "La raza no puede superar 255 caracteres")
    private String raza;

    @Schema(description = "Fecha de nacimiento, actual o anterior", example = "2021-06-12")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    private LocalDate fechaNacimiento;

    @Schema(description = "Identificador del dueño", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long duenoId;
    @Schema(description = "Nombre completo del dueño", example = "María González", accessMode = Schema.AccessMode.READ_ONLY)
    private String duenoNombre;
}
