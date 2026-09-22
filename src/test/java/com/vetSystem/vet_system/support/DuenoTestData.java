package com.vetSystem.vet_system.support;

import com.vetSystem.vet_system.model.Dueno;

public final class DuenoTestData {

    private DuenoTestData() {
    }

    public static Dueno datosDueno() {
        Dueno dueno = new Dueno();
        dueno.setNombre("Carlos");
        dueno.setApellido("González");
        dueno.setDni("28543210");
        dueno.setTelefono("1145678901");
        dueno.setEmail("carlos.gonzalez@example.com");
        return dueno;
    }

    public static Dueno datosActualizacion() {
        Dueno dueno = new Dueno();
        dueno.setNombre("Carlos Alberto");
        dueno.setApellido("González");
        dueno.setTelefono("1199887766");
        dueno.setEmail("carlos.nuevo@example.com");
        return dueno;
    }
}
