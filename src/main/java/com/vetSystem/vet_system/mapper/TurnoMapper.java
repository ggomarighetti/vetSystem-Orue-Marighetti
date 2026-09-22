package com.vetSystem.vet_system.mapper;

import com.vetSystem.vet_system.dto.TurnoRequestDTO;
import com.vetSystem.vet_system.dto.TurnoResponseDTO;
import com.vetSystem.vet_system.model.Turno;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TurnoMapper {

    @Mapping(source = "mascota.nombre", target = "mascotaNombre")
    @Mapping(source = "veterinario.nombre", target = "veterinarioNombre")
    TurnoResponseDTO toDTO(Turno turno);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "observaciones", ignore = true)
    @Mapping(target = "mascota", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    Turno toEntity(TurnoRequestDTO dto);
}
