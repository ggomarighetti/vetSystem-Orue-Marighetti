package com.vetSystem.vet_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuenoDTO {

    @Schema(description = "Identificador asignado por el sistema", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre del dueño", example = "María")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
    private String nombre;

    @Schema(description = "Apellido del dueño", example = "González")
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 255, message = "El apellido no puede superar 255 caracteres")
    private String apellido;

    @Schema(description = "Documento nacional de identidad de 7 u 8 dígitos", example = "28543210")
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{7,8}", message = "El DNI debe contener entre 7 y 8 dígitos")
    private String dni;

    @Schema(description = "Teléfono de contacto", example = "11 4567-8901")
    @Size(max = 255, message = "El teléfono no puede superar 255 caracteres")
    private String telefono;

    @Schema(description = "Correo electrónico de contacto", example = "maria.gonzalez@example.com")
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 255, message = "El email no puede superar 255 caracteres")
    private String email;
}
