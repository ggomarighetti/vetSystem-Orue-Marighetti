package com.vetSystem.vet_system.service;

import com.vetSystem.vet_system.dto.TurnoRequestDTO;
import com.vetSystem.vet_system.dto.TurnoResponseDTO;
import com.vetSystem.vet_system.exception.DuplicateResourceException;
import com.vetSystem.vet_system.exception.InvalidResourceException;
import com.vetSystem.vet_system.exception.ResourceNotFoundException;
import com.vetSystem.vet_system.mapper.TurnoMapper;
import com.vetSystem.vet_system.model.EstadoTurno;
import com.vetSystem.vet_system.model.Mascota;
import com.vetSystem.vet_system.model.Turno;
import com.vetSystem.vet_system.model.Veterinario;
import com.vetSystem.vet_system.repository.MascotaRepository;
import com.vetSystem.vet_system.repository.TurnoRepository;
import com.vetSystem.vet_system.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final TurnoMapper turnoMapper;

    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> getAllTurnos() {
        return turnoRepository.findAll().stream()
                .map(turnoMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TurnoResponseDTO getTurnoById(Long id) {
        return turnoMapper.toDTO(buscarTurno(id));
    }

    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> getTurnosByVeterinarioYFecha(Long veterinarioId, LocalDate fecha) {
        if (veterinarioId == null) {
            throw new InvalidResourceException("El veterinarioId es obligatorio");
        }
        if (fecha == null) {
            throw new InvalidResourceException("La fecha es obligatoria");
        }
        if (!veterinarioRepository.existsById(veterinarioId)) {
            throw new ResourceNotFoundException("Veterinario con id " + veterinarioId + " no fue encontrado");
        }
        return turnoRepository.findByVeterinarioIdAndFechaOrderByHoraAsc(veterinarioId, fecha).stream()
                .map(turnoMapper::toDTO)
                .toList();
    }

    @Transactional
    public TurnoResponseDTO createTurno(TurnoRequestDTO request) {
        validarRequest(request);
        Mascota mascota = mascotaRepository.findById(request.getMascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota con id "
                        + request.getMascotaId() + " no fue encontrada"));
        Veterinario veterinario = veterinarioRepository.findById(request.getVeterinarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario con id "
                        + request.getVeterinarioId() + " no fue encontrado"));

        if (turnoRepository.existsByVeterinarioIdAndFechaAndHora(
                request.getVeterinarioId(), request.getFecha(), request.getHora())) {
            throw new DuplicateResourceException("El veterinario ya tiene un turno en ese horario");
        }

        Turno turno = turnoMapper.toEntity(request);
        turno.setEstado(EstadoTurno.PENDIENTE);
        turno.setMotivo(request.getMotivo().strip());
        turno.setMascota(mascota);
        turno.setVeterinario(veterinario);
        try {
            return turnoMapper.toDTO(turnoRepository.saveAndFlush(turno));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateResourceException("El veterinario ya tiene un turno en ese horario", exception);
        }
    }

    @Transactional
    public TurnoResponseDTO actualizarEstado(Long id, EstadoTurno nuevoEstado, String observaciones) {
        if (nuevoEstado == null) {
            throw new InvalidResourceException("El estado es obligatorio");
        }
        Turno turno = buscarTurno(id);
        turno.setEstado(nuevoEstado);
        if (observaciones != null) {
            turno.setObservaciones(observaciones.strip());
        }
        return turnoMapper.toDTO(turnoRepository.save(turno));
    }

    private Turno buscarTurno(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno con id " + id + " no fue encontrado"));
    }

    private void validarRequest(TurnoRequestDTO request) {
        if (request == null) {
            throw new InvalidResourceException("Los datos del turno son obligatorios");
        }
        if (request.getFecha() == null) {
            throw new InvalidResourceException("El campo fecha es obligatorio");
        }
        if (request.getHora() == null) {
            throw new InvalidResourceException("El campo hora es obligatorio");
        }
        if (request.getMotivo() == null || request.getMotivo().isBlank()) {
            throw new InvalidResourceException("El campo motivo es obligatorio");
        }
        if (request.getMotivo().strip().length() > 255) {
            throw new InvalidResourceException("El campo motivo no puede superar 255 caracteres");
        }
        if (request.getMascotaId() == null) {
            throw new InvalidResourceException("El campo mascotaId es obligatorio");
        }
        if (request.getVeterinarioId() == null) {
            throw new InvalidResourceException("El campo veterinarioId es obligatorio");
        }
    }
}
