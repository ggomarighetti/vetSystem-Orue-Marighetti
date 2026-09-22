package com.vetSystem.vet_system.support;

import com.vetSystem.vet_system.dto.DuenoDTO;

public final class DuenoTestData {

    private DuenoTestData() {
    }

    public static DuenoDTO datosValidos() {
        return new DuenoDTO(null, "Carlos", "Pérez", "12345678", "1122334455", "carlos@example.com");
    }

    public static DuenoDTO duenoExistente() {
        DuenoDTO dueno = datosValidos();
        dueno.setId(1L);
        return dueno;
    }
}
