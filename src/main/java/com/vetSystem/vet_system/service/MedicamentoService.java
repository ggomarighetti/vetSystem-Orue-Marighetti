package com.vetSystem.vet_system.service;

import com.vetSystem.vet_system.dto.MedicamentoRequestDTO;
import com.vetSystem.vet_system.dto.MedicamentoResponseDTO;
import com.vetSystem.vet_system.exception.BusinessRuleException;
import com.vetSystem.vet_system.exception.DuplicateResourceException;
import com.vetSystem.vet_system.exception.InvalidResourceException;
import com.vetSystem.vet_system.exception.ResourceInUseException;
import com.vetSystem.vet_system.exception.ResourceNotFoundException;
import com.vetSystem.vet_system.mapper.MedicamentoMapper;
import com.vetSystem.vet_system.model.Medicamento;
import com.vetSystem.vet_system.model.Turno;
import com.vetSystem.vet_system.repository.MedicamentoRepository;
import com.vetSystem.vet_system.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final TurnoRepository turnoRepository;
    private final MedicamentoMapper medicamentoMapper;

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> getAllMedicamentos() {
        return medicamentoRepository.findAll().stream()
                .map(medicamentoMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MedicamentoResponseDTO getMedicamentoById(Long id) {
        return medicamentoMapper.toDTO(buscarMedicamento(id));
    }

    @Transactional
    public MedicamentoResponseDTO createMedicamento(MedicamentoRequestDTO datos) {
        validarDatos(datos);
        Medicamento medicamento = medicamentoMapper.toEntity(datos);
        medicamento.setNombre(datos.getNombre().strip());
        medicamento.setPrincipioActivo(datos.getPrincipioActivo().strip());
        return medicamentoMapper.toDTO(medicamentoRepository.save(medicamento));
    }

    @Transactional
    public MedicamentoResponseDTO updateMedicamento(Long id, MedicamentoRequestDTO datos) {
        validarDatos(datos);
        Medicamento medicamento = medicamentoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento con id " + id + " no fue encontrado"));
        medicamento.setNombre(datos.getNombre().strip());
        medicamento.setPrincipioActivo(datos.getPrincipioActivo().strip());
        medicamento.setStock(datos.getStock());
        medicamento.setPrecioUnitario(datos.getPrecioUnitario());
        return medicamentoMapper.toDTO(medicamentoRepository.save(medicamento));
    }

    @Transactional
    public void deleteMedicamento(Long id) {
        Medicamento medicamento = buscarMedicamento(id);
        if (medicamentoRepository.estaRecetado(id)) {
            throw new ResourceInUseException("Medicamento con id " + id
                    + " está recetado en un turno y no puede eliminarse");
        }
        try {
            medicamentoRepository.delete(medicamento);
            medicamentoRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResourceInUseException("Medicamento con id " + id
                    + " está recetado en un turno y no puede eliminarse", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<MedicamentoResponseDTO> getMedicamentosByTurno(Long turnoId) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Turno con id " + turnoId + " no fue encontrado"));
        return turno.getMedicamentos().stream()
                .map(medicamentoMapper::toDTO)
                .toList();
    }

    @Transactional
    public MedicamentoResponseDTO asociarATurno(Long turnoId, Long medicamentoId) {
        Turno turno = turnoRepository.findByIdForUpdate(turnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Turno con id " + turnoId + " no fue encontrado"));
        if (turno.getMedicamentos().stream().anyMatch(medicamento -> medicamentoId.equals(medicamento.getId()))) {
            throw new DuplicateResourceException("El medicamento con id " + medicamentoId
                    + " ya está recetado en el turno con id " + turnoId);
        }
        Medicamento medicamento = medicamentoRepository.findByIdForUpdate(medicamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento con id " + medicamentoId + " no fue encontrado"));
        if (medicamento.getStock() == 0) {
            throw new BusinessRuleException("El medicamento con id " + medicamentoId
                    + " no tiene stock disponible para el turno con id " + turnoId);
        }
        medicamento.setStock(medicamento.getStock() - 1);
        turno.getMedicamentos().add(medicamento);
        turnoRepository.saveAndFlush(turno);
        return medicamentoMapper.toDTO(medicamento);
    }

    private Medicamento buscarMedicamento(Long id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicamento con id " + id + " no fue encontrado"));
    }

    private void validarDatos(MedicamentoRequestDTO datos) {
        if (datos == null) {
            throw new InvalidResourceException("Los datos del medicamento son obligatorios");
        }
        if (datos.getNombre() == null || datos.getNombre().isBlank()
                || datos.getPrincipioActivo() == null || datos.getPrincipioActivo().isBlank()
                || datos.getStock() == null || datos.getStock() < 0
                || datos.getPrecioUnitario() == null || datos.getPrecioUnitario().signum() <= 0) {
            throw new InvalidResourceException("Los datos del medicamento son inválidos");
        }
        if (datos.getNombre().strip().length() > 255
                || datos.getPrincipioActivo().strip().length() > 255
                || datos.getPrecioUnitario().scale() > 2
                || datos.getPrecioUnitario().precision() - datos.getPrecioUnitario().scale() > 10) {
            throw new InvalidResourceException("Los datos del medicamento son inválidos");
        }
    }
}
