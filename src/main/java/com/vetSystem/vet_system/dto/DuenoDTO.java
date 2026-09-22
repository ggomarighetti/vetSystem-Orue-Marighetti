package com.vetSystem.vet_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuenoDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String email;
}
