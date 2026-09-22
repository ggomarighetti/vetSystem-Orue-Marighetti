package com.vetSystem.vet_system.mapper;

import com.vetSystem.vet_system.dto.DuenoDTO;
import com.vetSystem.vet_system.model.Dueno;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DuenoMapper {

    DuenoDTO toDTO(Dueno dueno);

    @Mapping(target = "mascotas", ignore = true)
    Dueno toEntity(DuenoDTO dto);
}
