package com.vetSystem.vet_system.service;

import com.vetSystem.vet_system.dto.MedicamentoRequestDTO;
import com.vetSystem.vet_system.exception.InvalidResourceException;
import com.vetSystem.vet_system.mapper.MedicamentoMapper;
import com.vetSystem.vet_system.repository.MedicamentoRepository;
import com.vetSystem.vet_system.repository.TurnoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class MedicamentoServiceTest {

    @Mock
    private MedicamentoRepository medicamentoRepository;

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private MedicamentoMapper medicamentoMapper;

    @InjectMocks
    private MedicamentoService medicamentoService;

    @Test
    void createMedicamento_rechazaNombreLargoEnServicio() {
        MedicamentoRequestDTO datos = datosValidos();
        datos.setNombre(" " + "a".repeat(256) + " ");

        assertThrows(InvalidResourceException.class, () -> medicamentoService.createMedicamento(datos));

        verifyNoInteractions(medicamentoRepository, turnoRepository, medicamentoMapper);
    }

    @Test
    void updateMedicamento_rechazaPrincipioActivoLargoEnServicio() {
        MedicamentoRequestDTO datos = datosValidos();
        datos.setPrincipioActivo("a".repeat(256));

        assertThrows(InvalidResourceException.class, () -> medicamentoService.updateMedicamento(1L, datos));

        verifyNoInteractions(medicamentoRepository, turnoRepository, medicamentoMapper);
    }

    @Test
    void createMedicamento_rechazaPrecioConDemasiadosEnterosODecimales() {
        MedicamentoRequestDTO datos = datosValidos();
        datos.setPrecioUnitario(new BigDecimal("12345678901.00"));
        assertThrows(InvalidResourceException.class, () -> medicamentoService.createMedicamento(datos));

        datos.setPrecioUnitario(new BigDecimal("1.234"));
        assertThrows(InvalidResourceException.class, () -> medicamentoService.createMedicamento(datos));

        verifyNoInteractions(medicamentoRepository, turnoRepository, medicamentoMapper);
    }

    private MedicamentoRequestDTO datosValidos() {
        MedicamentoRequestDTO datos = new MedicamentoRequestDTO();
        datos.setNombre("Amoxicilina 500");
        datos.setPrincipioActivo("Amoxicilina");
        datos.setStock(10);
        datos.setPrecioUnitario(new BigDecimal("1250.50"));
        return datos;
    }
}
