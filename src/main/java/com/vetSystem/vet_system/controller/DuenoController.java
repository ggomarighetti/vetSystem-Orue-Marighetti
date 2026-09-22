package com.vetSystem.vet_system.controller;

import com.vetSystem.vet_system.dto.DuenoDTO;
import com.vetSystem.vet_system.dto.MascotaDTO;
import com.vetSystem.vet_system.service.DuenoService;
import com.vetSystem.vet_system.service.MascotaService;
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
public class DuenoController {

    private final DuenoService duenoService;
    private final MascotaService mascotaService;

    @GetMapping
    public ResponseEntity<List<DuenoDTO>> getAllDuenos() {
        return ResponseEntity.ok(duenoService.getAllDuenos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DuenoDTO> getDuenoById(@PathVariable Long id) {
        return ResponseEntity.ok(duenoService.getDuenoById(id));
    }

    @GetMapping("/{id}/mascotas")
    public ResponseEntity<List<MascotaDTO>> getMascotasByDueno(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.getMascotasByDueno(id));
    }

    @PostMapping
    public ResponseEntity<DuenoDTO> createDueno(@Valid @RequestBody DuenoDTO dueno) {
        DuenoDTO nuevo = duenoService.createDueno(dueno);
        return ResponseEntity.created(URI.create("/api/duenos/" + nuevo.getId())).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DuenoDTO> updateDueno(@PathVariable Long id, @Valid @RequestBody DuenoDTO dueno) {
        return ResponseEntity.ok(duenoService.updateDueno(id, dueno));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDueno(@PathVariable Long id) {
        duenoService.deleteDueno(id);
        return ResponseEntity.noContent().build();
    }
}
