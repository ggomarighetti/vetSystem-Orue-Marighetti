package com.vetSystem.vet_system;

import com.vetSystem.vet_system.support.IntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetSystemApplicationTests extends IntegrationTest {

    @Test
    void contextLoadsConBaseVacia() {
        assertThat(duenoRepository.count()).isZero();
        assertThat(mascotaRepository.count()).isZero();
        assertThat(turnoRepository.count()).isZero();
        assertThat(veterinarioRepository.count()).isZero();
    }
}
