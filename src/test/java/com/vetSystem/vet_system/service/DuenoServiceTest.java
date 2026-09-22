package com.vetSystem.vet_system.service;

import com.vetSystem.vet_system.dto.DuenoDTO;
import com.vetSystem.vet_system.exception.DuplicateResourceException;
import com.vetSystem.vet_system.exception.ResourceNotFoundException;
import com.vetSystem.vet_system.mapper.DuenoMapper;
import com.vetSystem.vet_system.model.Dueno;
import com.vetSystem.vet_system.repository.DuenoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DuenoServiceTest {

    @Mock
    private DuenoRepository duenoRepository;

    @Mock
    private DuenoMapper duenoMapper;

    @InjectMocks
    private DuenoService duenoService;

    @Test
    void getAllDuenos_cuandoListaVacia_retornaListaVacia() {
        when(duenoRepository.findAll()).thenReturn(List.of());

        List<DuenoDTO> resultado = duenoService.getAllDuenos();

        assertTrue(resultado.isEmpty());
        verify(duenoRepository).findAll();
        verifyNoInteractions(duenoMapper);
    }

    @Test
    void getAllDuenos_cuandoHayDatos_retornaDTOs() {
        Dueno dueno = duenoConId(1L);
        DuenoDTO dto = duenoExistente();
        when(duenoRepository.findAll()).thenReturn(List.of(dueno));
        when(duenoMapper.toDTO(dueno)).thenReturn(dto);

        List<DuenoDTO> resultado = duenoService.getAllDuenos();

        assertEquals(List.of(dto), resultado);
        verify(duenoRepository).findAll();
        verify(duenoMapper).toDTO(dueno);
    }

    @Test
    void getDuenoById_cuandoExiste_retornaDTO() {
        Dueno dueno = duenoConId(1L);
        DuenoDTO dto = duenoExistente();
        when(duenoRepository.findById(1L)).thenReturn(Optional.of(dueno));
        when(duenoMapper.toDTO(dueno)).thenReturn(dto);

        DuenoDTO resultado = duenoService.getDuenoById(1L);

        assertEquals("Carlos", resultado.getNombre());
        verify(duenoRepository).findById(1L);
        verify(duenoMapper).toDTO(dueno);
    }

    @Test
    void getDuenoById_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(duenoRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> duenoService.getDuenoById(99L));

        assertTrue(exception.getMessage().contains("99"));
        verify(duenoRepository).findById(99L);
        verify(duenoRepository, never()).save(any());
        verifyNoInteractions(duenoMapper);
    }

    @Test
    void createDueno_cuandoDniDisponible_guardaDatosNormalizados() {
        DuenoDTO datos = datosConEspacios();
        Dueno nuevo = nuevoDueno(datos);
        Dueno guardado = duenoConId(1L);
        DuenoDTO respuesta = duenoExistente();
        when(duenoRepository.existsByDni("12345678")).thenReturn(false);
        when(duenoMapper.toEntity(datos)).thenReturn(nuevo);
        when(duenoRepository.saveAndFlush(any(Dueno.class))).thenReturn(guardado);
        when(duenoMapper.toDTO(guardado)).thenReturn(respuesta);

        DuenoDTO resultado = duenoService.createDueno(datos);

        assertEquals(1L, resultado.getId());
        ArgumentCaptor<Dueno> captor = ArgumentCaptor.forClass(Dueno.class);
        verify(duenoRepository).saveAndFlush(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals("12345678", captor.getValue().getDni());
        assertEquals("Carlos", captor.getValue().getNombre());
        assertEquals("Pérez", captor.getValue().getApellido());
        assertEquals("carlos@example.com", captor.getValue().getEmail());
    }

    @Test
    void createDueno_cuandoDniDuplicado_lanzaDuplicateResourceException() {
        DuenoDTO datos = datosValidos();
        when(duenoRepository.existsByDni("12345678")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> duenoService.createDueno(datos));

        assertTrue(exception.getMessage().contains("12345678"));
        verify(duenoRepository).existsByDni("12345678");
        verify(duenoRepository, never()).saveAndFlush(any());
        verifyNoInteractions(duenoMapper);
    }

    private DuenoDTO datosValidos() {
        return new DuenoDTO(null, "Carlos", "Pérez", "12345678", "1122334455", "carlos@example.com");
    }

    private DuenoDTO duenoExistente() {
        DuenoDTO dueno = datosValidos();
        dueno.setId(1L);
        return dueno;
    }

    private DuenoDTO datosConEspacios() {
        DuenoDTO datos = datosValidos();
        datos.setNombre(" Carlos ");
        datos.setApellido(" Pérez ");
        datos.setDni(" 12345678 ");
        datos.setEmail(" carlos@example.com ");
        return datos;
    }

    private Dueno duenoConId(Long id) {
        Dueno dueno = new Dueno();
        dueno.setId(id);
        return dueno;
    }

    private Dueno nuevoDueno(DuenoDTO datos) {
        Dueno dueno = duenoConId(44L);
        dueno.setNombre(datos.getNombre());
        dueno.setApellido(datos.getApellido());
        dueno.setTelefono(datos.getTelefono());
        dueno.setEmail(datos.getEmail());
        return dueno;
    }
}
