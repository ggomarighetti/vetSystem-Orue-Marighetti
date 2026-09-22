package com.vetSystem.vet_system.service;

import com.vetSystem.vet_system.dto.VeterinarioDTO;
import com.vetSystem.vet_system.exception.DuplicateResourceException;
import com.vetSystem.vet_system.exception.InvalidResourceException;
import com.vetSystem.vet_system.exception.ResourceInUseException;
import com.vetSystem.vet_system.exception.ResourceNotFoundException;
import com.vetSystem.vet_system.mapper.VeterinarioMapper;
import com.vetSystem.vet_system.model.Veterinario;
import com.vetSystem.vet_system.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final VeterinarioMapper veterinarioMapper;

    @Transactional(readOnly = true)
    public List<VeterinarioDTO> getAllVeterinarios() {
        return veterinarioRepository.findAll().stream()
                .map(veterinarioMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VeterinarioDTO getVeterinarioById(Long id) {
        return veterinarioMapper.toDTO(buscarVeterinario(id));
    }

    @Transactional
    public VeterinarioDTO createVeterinario(VeterinarioDTO datos) {
        validarDatos(datos, true);
        String matricula = datos.getMatricula().strip();
        if (veterinarioRepository.existsByMatricula(matricula)) {
            throw new DuplicateResourceException("Ya existe un veterinario con matrícula: " + matricula);
        }

        Veterinario veterinario = veterinarioMapper.toEntity(datos);
        veterinario.setId(null);
        veterinario.setMatricula(matricula);
        normalizarDatosEditables(veterinario);
        try {
            return veterinarioMapper.toDTO(veterinarioRepository.saveAndFlush(veterinario));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateResourceException("Ya existe un veterinario con matrícula: " + matricula, exception);
        }
    }

    @Transactional
    public VeterinarioDTO updateVeterinario(Long id, VeterinarioDTO datos) {
        Veterinario veterinario = buscarVeterinario(id);
        validarDatos(datos, false);
        veterinario.setNombre(datos.getNombre());
        veterinario.setApellido(datos.getApellido());
        veterinario.setEspecialidad(datos.getEspecialidad());
        normalizarDatosEditables(veterinario);
        return veterinarioMapper.toDTO(veterinarioRepository.save(veterinario));
    }

    @Transactional
    public void deleteVeterinario(Long id) {
        Veterinario veterinario = buscarVeterinario(id);
        try {
            veterinarioRepository.delete(veterinario);
            veterinarioRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResourceInUseException("Veterinario con id " + id
                    + " tiene registros asociados y no puede eliminarse", exception);
        }
    }

    private Veterinario buscarVeterinario(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario con id " + id + " no fue encontrado"));
    }

    private void normalizarDatosEditables(Veterinario veterinario) {
        veterinario.setNombre(veterinario.getNombre().strip());
        veterinario.setApellido(veterinario.getApellido().strip());
        veterinario.setEspecialidad(veterinario.getEspecialidad().strip());
    }

    private void validarDatos(VeterinarioDTO datos, boolean requiereMatricula) {
        if (datos == null) {
            throw new InvalidResourceException("Los datos del veterinario son obligatorios");
        }
        validarTexto("nombre", datos.getNombre(), true);
        validarTexto("apellido", datos.getApellido(), true);
        validarTexto("especialidad", datos.getEspecialidad(), true);
        validarTexto("matricula", datos.getMatricula(), requiereMatricula);
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
