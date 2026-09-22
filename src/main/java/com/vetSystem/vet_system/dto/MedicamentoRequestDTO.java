package com.vetSystem.vet_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicamentoRequestDTO {

    @Schema(description = "Nombre comercial", example = "Amoxicilina 500")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
    private String nombre;

    @Schema(description = "Sustancia farmacológica", example = "Amoxicilina")
    @NotBlank(message = "El principio activo es obligatorio")
    @Size(max = 255, message = "El principio activo no puede superar 255 caracteres")
    private String principioActivo;

    @Schema(description = "Unidades disponibles", example = "10")
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Schema(description = "Precio por unidad", example = "1250.50")
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor que cero")
    @Digits(integer = 10, fraction = 2, message = "El precio unitario admite hasta 10 enteros y 2 decimales")
    private BigDecimal precioUnitario;
}
