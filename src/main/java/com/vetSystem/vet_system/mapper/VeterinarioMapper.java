package com.vetSystem.vet_system.mapper;

import com.vetSystem.vet_system.dto.VeterinarioDTO;
import com.vetSystem.vet_system.model.Veterinario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VeterinarioMapper {

    VeterinarioDTO toDTO(Veterinario veterinario);

    Veterinario toEntity(VeterinarioDTO dto);
}
