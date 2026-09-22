package com.vetSystem.vet_system.service;

import com.vetSystem.vet_system.exception.DuplicateResourceException;
import com.vetSystem.vet_system.exception.InvalidResourceException;
import com.vetSystem.vet_system.exception.ResourceInUseException;
import com.vetSystem.vet_system.exception.ResourceNotFoundException;
import com.vetSystem.vet_system.model.Dueno;
import com.vetSystem.vet_system.repository.DuenoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DuenoService {

    private final DuenoRepository duenoRepository;

    @Transactional(readOnly = true)
    public List<Dueno> getAllDuenos() {
        return duenoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Dueno getDuenoById(Long id) {
        return duenoRepository.findByIdWithMascotas(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dueño con id " + id + " no fue encontrado"));
    }

    public Dueno createDueno(Dueno datos) {
        validarDatos(datos);
        validarTexto("dni", datos.getDni(), true);
        String dni = datos.getDni().strip();
        if (duenoRepository.existsByDni(dni)) {
            throw new DuplicateResourceException("Ya existe un dueño con DNI: " + dni);
        }

        Dueno nuevo = new Dueno();
        nuevo.setDni(dni);
        copiarDatosEditables(datos, nuevo);
        try {
            return duenoRepository.saveAndFlush(nuevo);
        } catch (DataIntegrityViolationException exception) {
            if (duenoRepository.existsByDni(dni)) {
                throw new DuplicateResourceException("Ya existe un dueño con DNI: " + dni, exception);
            }
            throw exception;
        }
    }

    @Transactional
    public Dueno updateDueno(Long id, Dueno datos) {
        Dueno dueno = getDuenoById(id);
        validarDatos(datos);
        copiarDatosEditables(datos, dueno);
        return duenoRepository.save(dueno);
    }

    @Transactional
    public void deleteDueno(Long id) {
        Dueno dueno = getDuenoById(id);
        if (!dueno.getMascotas().isEmpty()) {
            throw new ResourceInUseException("Dueño con id " + id
                    + " tiene registros asociados y no puede eliminarse");
        }
        try {
            duenoRepository.delete(dueno);
            duenoRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResourceInUseException("Dueño con id " + id
                    + " tiene registros asociados y no puede eliminarse", exception);
        }
    }

    private void copiarDatosEditables(Dueno origen, Dueno destino) {
        destino.setNombre(origen.getNombre().strip());
        destino.setApellido(origen.getApellido().strip());
        destino.setTelefono(origen.getTelefono() == null ? null : origen.getTelefono().strip());
        destino.setEmail(origen.getEmail().strip());
    }

    private void validarDatos(Dueno datos) {
        if (datos == null) {
            throw new InvalidResourceException("Los datos del dueño son obligatorios");
        }
        validarTexto("nombre", datos.getNombre(), true);
        validarTexto("apellido", datos.getApellido(), true);
        validarTexto("email", datos.getEmail(), true);
        validarTexto("telefono", datos.getTelefono(), false);
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
