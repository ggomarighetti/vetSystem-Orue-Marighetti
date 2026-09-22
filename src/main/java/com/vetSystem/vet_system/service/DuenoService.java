package com.vetSystem.vet_system.service;

import com.vetSystem.vet_system.dto.DuenoDTO;
import com.vetSystem.vet_system.exception.DuplicateResourceException;
import com.vetSystem.vet_system.exception.InvalidResourceException;
import com.vetSystem.vet_system.exception.ResourceInUseException;
import com.vetSystem.vet_system.exception.ResourceNotFoundException;
import com.vetSystem.vet_system.mapper.DuenoMapper;
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
    private final DuenoMapper duenoMapper;

    @Transactional(readOnly = true)
    public List<DuenoDTO> getAllDuenos() {
        return duenoRepository.findAll().stream()
                .map(duenoMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DuenoDTO getDuenoById(Long id) {
        return duenoMapper.toDTO(buscarDueno(id));
    }

    @Transactional
    public DuenoDTO createDueno(DuenoDTO datos) {
        validarDatos(datos);
        validarTexto("dni", datos.getDni(), true);
        String dni = datos.getDni().strip();
        if (duenoRepository.existsByDni(dni)) {
            throw new DuplicateResourceException("Ya existe un dueño con DNI: " + dni);
        }

        Dueno nuevo = duenoMapper.toEntity(datos);
        nuevo.setId(null);
        nuevo.setDni(dni);
        normalizarDatosEditables(nuevo);
        try {
            return duenoMapper.toDTO(duenoRepository.saveAndFlush(nuevo));
        } catch (DataIntegrityViolationException exception) {
            if (duenoRepository.existsByDni(dni)) {
                throw new DuplicateResourceException("Ya existe un dueño con DNI: " + dni, exception);
            }
            throw exception;
        }
    }

    @Transactional
    public DuenoDTO updateDueno(Long id, DuenoDTO datos) {
        Dueno dueno = buscarDueno(id);
        validarDatos(datos);
        dueno.setNombre(datos.getNombre());
        dueno.setApellido(datos.getApellido());
        dueno.setTelefono(datos.getTelefono());
        dueno.setEmail(datos.getEmail());
        normalizarDatosEditables(dueno);
        return duenoMapper.toDTO(duenoRepository.save(dueno));
    }

    @Transactional
    public void deleteDueno(Long id) {
        Dueno dueno = duenoRepository.findByIdWithMascotas(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dueño con id " + id + " no fue encontrado"));
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

    private Dueno buscarDueno(Long id) {
        return duenoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dueño con id " + id + " no fue encontrado"));
    }

    private void normalizarDatosEditables(Dueno dueno) {
        dueno.setNombre(dueno.getNombre().strip());
        dueno.setApellido(dueno.getApellido().strip());
        dueno.setTelefono(dueno.getTelefono() == null ? null : dueno.getTelefono().strip());
        dueno.setEmail(dueno.getEmail().strip());
    }

    private void validarDatos(DuenoDTO datos) {
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
