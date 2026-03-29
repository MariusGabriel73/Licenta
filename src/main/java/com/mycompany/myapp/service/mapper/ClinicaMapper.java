package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Clinica;
import com.mycompany.myapp.domain.Locatie;
import com.mycompany.myapp.domain.Medic;
import com.mycompany.myapp.service.dto.ClinicaDTO;
import com.mycompany.myapp.service.dto.LocatieDTO;
import com.mycompany.myapp.service.dto.MedicDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Clinica} and its DTO {@link ClinicaDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClinicaMapper extends EntityMapper<ClinicaDTO, Clinica> {
    @Mapping(target = "locatie", source = "locatie", qualifiedByName = "locatieId")
    @Mapping(target = "medicis", source = "medicis", qualifiedByName = "medicIdSet")
    ClinicaDTO toDto(Clinica s);

    @Mapping(target = "medicis", ignore = true)
    @Mapping(target = "removeMedici", ignore = true)
    Clinica toEntity(ClinicaDTO clinicaDTO);

    @Named("locatieId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LocatieDTO toDtoLocatieId(Locatie locatie);

    @Named("medicId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MedicDTO toDtoMedicId(Medic medic);

    @Named("medicIdSet")
    default Set<MedicDTO> toDtoMedicIdSet(Set<Medic> medic) {
        return medic.stream().map(this::toDtoMedicId).collect(Collectors.toSet());
    }
}
