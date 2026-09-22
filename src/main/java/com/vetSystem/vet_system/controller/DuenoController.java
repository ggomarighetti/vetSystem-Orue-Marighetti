package com.vetSystem.vet_system.controller;

import com.vetSystem.vet_system.dto.DuenoDTO;
import com.vetSystem.vet_system.dto.MascotaDTO;
import com.vetSystem.vet_system.service.DuenoService;
import com.vetSystem.vet_system.service.MascotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
@RequestMapping("/api/duenos")
@RequiredArgsConstructor
@Tag(name = "Dueños", description = "Gestión de dueños y consulta de sus mascotas")
public class DuenoController {

    private final DuenoService duenoService;
    private final MascotaService mascotaService;

    @GetMapping
    @Operation(summary = "Listar dueños", description = "Devuelve todos los dueños registrados, o una lista vacía.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<DuenoDTO>> getAllDuenos() {
        return ResponseEntity.ok(duenoService.getAllDuenos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar dueño", description = "Busca un dueño por su identificador.")
    @ApiResponse(responseCode = "200", description = "Dueño encontrado")
    @ApiResponse(responseCode = "400", description = "Identificador inválido")
    @ApiResponse(responseCode = "404", description = "Dueño inexistente")
    @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador del dueño", example = "1")})
    public ResponseEntity<DuenoDTO> getDuenoById(@PathVariable Long id) {
        return ResponseEntity.ok(duenoService.getDuenoById(id));
    }

    @GetMapping("/{id}/mascotas")
    @Operation(summary = "Listar mascotas de un dueño", description = "Devuelve las mascotas asociadas al dueño; falla si el dueño no existe.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    @ApiResponse(responseCode = "400", description = "Identificador inválido")
    @ApiResponse(responseCode = "404", description = "Dueño inexistente")
    @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador del dueño", example = "1")})
    public ResponseEntity<List<MascotaDTO>> getMascotasByDueno(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.getMascotasByDueno(id));
    }

    @PostMapping
    @Operation(summary = "Crear dueño", description = "Registra un dueño y devuelve su ubicación; rechaza datos inválidos o un DNI duplicado.")
    @ApiResponse(responseCode = "201", description = "Dueño creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "DNI ya registrado")
    public ResponseEntity<DuenoDTO> createDueno(@Valid @RequestBody DuenoDTO dueno) {
        DuenoDTO nuevo = duenoService.createDueno(dueno);
        return ResponseEntity.created(URI.create("/api/duenos/" + nuevo.getId())).body(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar dueño", description = "Actualiza sus datos de contacto; el DNI registrado permanece inmutable.")
    @ApiResponse(responseCode = "200", description = "Dueño actualizado")
    @ApiResponse(responseCode = "400", description = "Identificador o datos inválidos")
    @ApiResponse(responseCode = "404", description = "Dueño inexistente")
    @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador del dueño", example = "1")})
    public ResponseEntity<DuenoDTO> updateDueno(
            @PathVariable Long id,
            @Valid @RequestBody DuenoDTO dueno) {
        return ResponseEntity.ok(duenoService.updateDueno(id, dueno));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar dueño", description = "Elimina un dueño sin mascotas asociadas.")
    @ApiResponse(responseCode = "204", description = "Dueño eliminado")
    @ApiResponse(responseCode = "400", description = "Identificador inválido")
    @ApiResponse(responseCode = "404", description = "Dueño inexistente")
    @ApiResponse(responseCode = "409", description = "Dueño con mascotas asociadas")
    @Parameters({@Parameter(name = "id", in = ParameterIn.PATH, description = "Identificador del dueño", example = "1")})
    public ResponseEntity<Void> deleteDueno(@PathVariable Long id) {
        duenoService.deleteDueno(id);
        return ResponseEntity.noContent().build();
    }
}
