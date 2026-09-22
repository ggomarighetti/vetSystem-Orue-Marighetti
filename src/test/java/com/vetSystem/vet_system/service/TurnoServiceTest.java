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
        Mascota mascota = new Mascota();
        Veterinario veterinario = new Veterinario();
        Turno turno = new Turno();
        TurnoResponseDTO respuesta = new TurnoResponseDTO();
        respuesta.setId(1L);
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(veterinarioRepository.findById(2L)).thenReturn(Optional.of(veterinario));
        when(turnoRepository.existsByVeterinarioIdAndFechaAndHora(2L, request.getFecha(), request.getHora()))
                .thenReturn(false);
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
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(new Mascota()));
        when(veterinarioRepository.findById(2L)).thenReturn(Optional.of(new Veterinario()));
        when(turnoRepository.existsByVeterinarioIdAndFechaAndHora(2L, request.getFecha(), request.getHora()))
                .thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> turnoService.createTurno(request));

        assertTrue(exception.getMessage().contains("turno"));
        verify(turnoRepository).existsByVeterinarioIdAndFechaAndHora(2L, request.getFecha(), request.getHora());
        verify(turnoRepository, never()).saveAndFlush(any());
        verifyNoInteractions(turnoMapper);
    }

    private TurnoRequestDTO requestValido() {
        return new TurnoRequestDTO(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                " Consulta general ", 1L, 2L);
    }
}
