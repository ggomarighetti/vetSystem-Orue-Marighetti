package com.vetSystem.vet_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioDTO {

    public interface Creacion extends Default {
    }

    @Schema(description = "Identificador asignado por el sistema", example = "2", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre del veterinario", example = "Lucía")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
    private String nombre;

    @Schema(description = "Apellido del veterinario", example = "Fernández")
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 255, message = "El apellido no puede superar 255 caracteres")
    private String apellido;

    @Schema(description = "Matrícula profesional, obligatoria al crear e inmutable al actualizar", example = "MV-12345")
    @NotBlank(message = "La matrícula es obligatoria", groups = Creacion.class)
    @Pattern(regexp = "MV-\\d+", message = "La matrícula debe tener el formato MV- seguido de números")
    @Size(max = 255, message = "La matrícula no puede superar 255 caracteres")
    private String matricula;

    @Schema(description = "Área de atención del veterinario", example = "Clínica general")
    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 255, message = "La especialidad no puede superar 255 caracteres")
    private String especialidad;
}
