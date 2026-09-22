package com.vetSystem.vet_system.service;

import com.vetSystem.vet_system.dto.TurnoRequestDTO;
import com.vetSystem.vet_system.dto.TurnoResponseDTO;
import com.vetSystem.vet_system.exception.DuplicateResourceException;
import com.vetSystem.vet_system.mapper.TurnoMapper;
import com.vetSystem.vet_system.model.EstadoTurno;
import com.vetSystem.vet_system.model.Mascota;
import com.vetSystem.vet_system.model.Turno;
import com.vetSystem.vet_system.model.Veterinario;
import com.vetSystem.vet_system.repository.MascotaRepository;
import com.vetSystem.vet_system.repository.TurnoRepository;
import com.vetSystem.vet_system.repository.VeterinarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private TurnoMapper turnoMapper;

    @InjectMocks
    private TurnoService turnoService;

    @Test
    void createTurno_cuandoHorarioDisponible_guardaUnaVez() {
        TurnoRequestDTO request = requestValido();
        Mascota mascota = mascotaExistente();
        Veterinario veterinario = veterinarioExistente();
        Turno turno = nuevoTurno();
        TurnoResponseDTO respuesta = turnoCreado();
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(veterinarioRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(veterinario));
        when(turnoRepository.findFirstByVeterinarioIdAndFechaAndHora(2L, request.getFecha(), request.getHora()))
                .thenReturn(Optional.empty());
        when(turnoMapper.toEntity(request)).thenReturn(turno);
        when(turnoRepository.saveAndFlush(any(Turno.class))).thenReturn(turno);
        when(turnoMapper.toDTO(turno)).thenReturn(respuesta);

        TurnoResponseDTO resultado = turnoService.createTurno(request);

        assertEquals(1L, resultado.getId());
        ArgumentCaptor<Turno> captor = ArgumentCaptor.forClass(Turno.class);
        verify(turnoRepository, times(1)).saveAndFlush(captor.capture());
        assertSame(mascota, captor.getValue().getMascota());
        assertSame(veterinario, captor.getValue().getVeterinario());
        assertEquals(EstadoTurno.PENDIENTE, captor.getValue().getEstado());
        assertEquals("Consulta general", captor.getValue().getMotivo());
    }

    @Test
    void createTurno_cuandoHaySuperposicion_lanzaDuplicateResourceExceptionSinGuardar() {
        TurnoRequestDTO request = requestValido();
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascotaExistente()));
        when(veterinarioRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(veterinarioExistente()));
        Turno conflictivo = new Turno();
        conflictivo.setId(9L);
        conflictivo.setFecha(request.getFecha());
        conflictivo.setHora(request.getHora());
        when(turnoRepository.findFirstByVeterinarioIdAndFechaAndHora(2L, request.getFecha(), request.getHora()))
                .thenReturn(Optional.of(conflictivo));

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> turnoService.createTurno(request));

        assertTrue(exception.getMessage().contains("turno 9"));
        assertTrue(exception.getMessage().contains(request.getFecha().toString()));
        assertTrue(exception.getMessage().contains(request.getHora().toString()));
        verify(turnoRepository).findFirstByVeterinarioIdAndFechaAndHora(2L, request.getFecha(), request.getHora());
        verify(turnoRepository, never()).saveAndFlush(any());
        verifyNoInteractions(turnoMapper);
    }

    @Test
    void createTurno_noConfundeOtrosErroresDeIntegridadConTurnosDuplicados() {
        TurnoRequestDTO request = requestValido();
        Turno turno = nuevoTurno();
        DataIntegrityViolationException error = new DataIntegrityViolationException("Otro error de integridad");
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascotaExistente()));
        when(veterinarioRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(veterinarioExistente()));
        when(turnoRepository.findFirstByVeterinarioIdAndFechaAndHora(2L, request.getFecha(), request.getHora()))
                .thenReturn(Optional.empty());
        when(turnoMapper.toEntity(request)).thenReturn(turno);
        when(turnoRepository.saveAndFlush(turno)).thenThrow(error);

        DataIntegrityViolationException resultado = assertThrows(DataIntegrityViolationException.class,
                () -> turnoService.createTurno(request));

        assertSame(error, resultado);
    }

    private TurnoRequestDTO requestValido() {
        return new TurnoRequestDTO(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                " Consulta general ", 1L, 2L);
    }

    private Mascota mascotaExistente() {
        return new Mascota();
    }

    private Veterinario veterinarioExistente() {
        return new Veterinario();
    }

    private Turno nuevoTurno() {
        return new Turno();
    }

    private TurnoResponseDTO turnoCreado() {
        TurnoResponseDTO respuesta = new TurnoResponseDTO();
        respuesta.setId(1L);
        return respuesta;
    }
}
