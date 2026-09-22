package com.vetSystem.vet_system.service;

import com.vetSystem.vet_system.exception.DuplicateResourceException;
import com.vetSystem.vet_system.exception.InvalidResourceException;
import com.vetSystem.vet_system.exception.ResourceInUseException;
import com.vetSystem.vet_system.exception.ResourceNotFoundException;
import com.vetSystem.vet_system.model.Dueno;
import com.vetSystem.vet_system.model.Mascota;
import com.vetSystem.vet_system.repository.DuenoRepository;
import com.vetSystem.vet_system.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final DuenoRepository duenoRepository;

    @Transactional(readOnly = true)
    public List<Mascota> getAllMascotas() {
        return mascotaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Mascota getMascotaById(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota con id " + id + " no fue encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Mascota> getMascotasByDueno(Long duenoId) {
        if (!duenoRepository.existsById(duenoId)) {
            throw new ResourceNotFoundException("Dueño con id " + duenoId + " no fue encontrado");
        }
        return mascotaRepository.findByDuenoId(duenoId);
    }

    @Transactional
    public Mascota createMascota(Long duenoId, Mascota datos) {
        Dueno dueno = duenoRepository.findById(duenoId)
                .orElseThrow(() -> new ResourceNotFoundException("Dueño con id " + duenoId + " no fue encontrado"));
        validarDatos(datos);
        String nombre = datos.getNombre().strip();
        if (mascotaRepository.existsByNombreAndDuenoId(nombre, duenoId)) {
            throw new DuplicateResourceException("Ya existe una mascota con nombre " + nombre + " para el dueño con id " + duenoId);
        }

        Mascota mascota = new Mascota();
        copiarDatosEditables(datos, mascota);
        mascota.setDueno(dueno);
        return mascotaRepository.save(mascota);
    }

    @Transactional
    public Mascota updateMascota(Long id, Mascota datos) {
        Mascota mascota = getMascotaById(id);
        validarDatos(datos);
        String nombre = datos.getNombre().strip();
        Long duenoId = mascota.getDueno().getId();
        if (!nombre.equals(mascota.getNombre())
                && mascotaRepository.existsByNombreAndDuenoId(nombre, duenoId)) {
            throw new DuplicateResourceException("Ya existe una mascota con nombre " + nombre + " para el dueño con id " + duenoId);
        }
        copiarDatosEditables(datos, mascota);
        return mascotaRepository.save(mascota);
    }

    @Transactional
    public void deleteMascota(Long id) {
        Mascota mascota = getMascotaById(id);
        try {
            mascotaRepository.delete(mascota);
            mascotaRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResourceInUseException("Mascota con id " + id
                    + " tiene registros asociados y no puede eliminarse", exception);
        }
    }

    private void copiarDatosEditables(Mascota origen, Mascota destino) {
        destino.setNombre(origen.getNombre().strip());
        destino.setEspecie(origen.getEspecie().strip());
        destino.setRaza(origen.getRaza() == null ? null : origen.getRaza().strip());
        destino.setFechaNacimiento(origen.getFechaNacimiento());
    }

    private void validarDatos(Mascota datos) {
        if (datos == null) {
            throw new InvalidResourceException("Los datos de la mascota son obligatorios");
        }
        validarTexto("nombre", datos.getNombre(), true);
        validarTexto("especie", datos.getEspecie(), true);
        validarTexto("raza", datos.getRaza(), false);
    }

    private void validarTexto(String campo, String valor, boolean obligatorio) {
        if (obligatorio && (valor == null || valor.isBlank())) {
            throw new InvalidResourceException("El campo " + campo + " es obligatorio");
        }
        if (valor != null && valor.strip().length() > 255) {
            throw new InvalidResourceException("El campo " + campo + " no puede superar 255 caracteres");
        }
    }
}
