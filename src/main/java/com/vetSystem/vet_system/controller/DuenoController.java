package com.vetSystem.vet_system.controller;

import com.vetSystem.vet_system.model.Dueno;
import com.vetSystem.vet_system.service.DuenoService;
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

    @GetMapping
    public ResponseEntity<List<Dueno>> getAllDuenos() {
        return ResponseEntity.ok(duenoService.getAllDuenos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dueno> getDuenoById(@PathVariable Long id) {
        return ResponseEntity.ok(duenoService.getDuenoById(id));
    }

    @PostMapping
    public ResponseEntity<Dueno> createDueno(@RequestBody Dueno dueno) {
        Dueno nuevo = duenoService.createDueno(dueno);
        return ResponseEntity.created(URI.create("/api/duenos/" + nuevo.getId())).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dueno> updateDueno(@PathVariable Long id, @RequestBody Dueno dueno) {
        return ResponseEntity.ok(duenoService.updateDueno(id, dueno));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDueno(@PathVariable Long id) {
        duenoService.deleteDueno(id);
        return ResponseEntity.noContent().build();
    }
}
