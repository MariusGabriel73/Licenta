package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Medic;
import com.mycompany.myapp.domain.Specializare;
import com.mycompany.myapp.service.dto.MedicDTO;
import com.mycompany.myapp.service.dto.SpecializareDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Specializare} and its DTO {@link SpecializareDTO}.
 */
@Mapper(componentModel = "spring")
public interface SpecializareMapper extends EntityMapper<SpecializareDTO, Specializare> {
    @Mapping(target = "medicis", source = "medicis", qualifiedByName = "medicIdSet")
    SpecializareDTO toDto(Specializare s);

    @Mapping(target = "medicis", ignore = true)
    @Mapping(target = "removeMedici", ignore = true)
    Specializare toEntity(SpecializareDTO specializareDTO);

    @Named("medicId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MedicDTO toDtoMedicId(Medic medic);

    @Named("medicIdSet")
    default Set<MedicDTO> toDtoMedicIdSet(Set<Medic> medic) {
        return medic.stream().map(this::toDtoMedicId).collect(Collectors.toSet());
    }
}
