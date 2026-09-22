package com.vetSystem.vet_system.controller;

import com.vetSystem.vet_system.dto.VeterinarioDTO;
import com.vetSystem.vet_system.service.VeterinarioService;
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
@RequestMapping("/api/veterinarios")
@RequiredArgsConstructor
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    @GetMapping
    public ResponseEntity<List<VeterinarioDTO>> getAllVeterinarios() {
        return ResponseEntity.ok(veterinarioService.getAllVeterinarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioDTO> getVeterinarioById(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.getVeterinarioById(id));
    }

    @PostMapping
    public ResponseEntity<VeterinarioDTO> createVeterinario(@RequestBody VeterinarioDTO veterinario) {
        VeterinarioDTO nuevo = veterinarioService.createVeterinario(veterinario);
        return ResponseEntity.created(URI.create("/api/veterinarios/" + nuevo.getId())).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioDTO> updateVeterinario(
            @PathVariable Long id,
            @RequestBody VeterinarioDTO veterinario) {
        return ResponseEntity.ok(veterinarioService.updateVeterinario(id, veterinario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeterinario(@PathVariable Long id) {
        veterinarioService.deleteVeterinario(id);
        return ResponseEntity.noContent().build();
    }
}
