package com.vetSystem.vet_system.mapper;

import com.vetSystem.vet_system.dto.MascotaDTO;
import com.vetSystem.vet_system.model.Mascota;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MascotaMapper {

    @Mapping(source = "dueno.id", target = "duenoId")
    @Mapping(source = "dueno.nombre", target = "duenoNombre")
    MascotaDTO toDTO(Mascota mascota);
}
