package com.vetSystem.vet_system.dto;

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

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 255, message = "El apellido no puede superar 255 caracteres")
    private String apellido;

    @NotBlank(message = "La matrícula es obligatoria", groups = Creacion.class)
    @Pattern(regexp = "MV-\\d+", message = "La matrícula debe tener el formato MV- seguido de números")
    @Size(max = 255, message = "La matrícula no puede superar 255 caracteres")
    private String matricula;

    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 255, message = "La especialidad no puede superar 255 caracteres")
    private String especialidad;
}
