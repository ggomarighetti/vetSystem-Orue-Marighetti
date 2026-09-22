package com.vetSystem.vet_system.controller;

import com.vetSystem.vet_system.dto.MedicamentoRequestDTO;
import com.vetSystem.vet_system.dto.MedicamentoResponseDTO;
import com.vetSystem.vet_system.service.MedicamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
@RequiredArgsConstructor
@Tag(name = "Medicamentos", description = "Inventario de medicamentos")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    @GetMapping
    @Operation(summary = "Listar medicamentos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<MedicamentoResponseDTO>> getAllMedicamentos() {
        return ResponseEntity.ok(medicamentoService.getAllMedicamentos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar medicamento")
    @ApiResponse(responseCode = "200", description = "Medicamento encontrado")
    @ApiResponse(responseCode = "404", description = "Medicamento inexistente")
    public ResponseEntity<MedicamentoResponseDTO> getMedicamentoById(@PathVariable Long id) {
        return ResponseEntity.ok(medicamentoService.getMedicamentoById(id));
    }

    @PostMapping
    @Operation(summary = "Crear medicamento")
    @ApiResponse(responseCode = "201", description = "Medicamento creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<MedicamentoResponseDTO> createMedicamento(
            @Valid @RequestBody MedicamentoRequestDTO datos) {
        MedicamentoResponseDTO nuevo = medicamentoService.createMedicamento(datos);
        return ResponseEntity.created(URI.create("/api/medicamentos/" + nuevo.getId())).body(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar medicamento")
    @ApiResponse(responseCode = "200", description = "Medicamento actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Medicamento inexistente")
    public ResponseEntity<MedicamentoResponseDTO> updateMedicamento(
            @PathVariable Long id,
            @Valid @RequestBody MedicamentoRequestDTO datos) {
        return ResponseEntity.ok(medicamentoService.updateMedicamento(id, datos));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar medicamento")
    @ApiResponse(responseCode = "204", description = "Medicamento eliminado")
    @ApiResponse(responseCode = "404", description = "Medicamento inexistente")
    @ApiResponse(responseCode = "409", description = "Medicamento recetado en un turno")
    public ResponseEntity<Void> deleteMedicamento(@PathVariable Long id) {
        medicamentoService.deleteMedicamento(id);
        return ResponseEntity.noContent().build();
    }
}
