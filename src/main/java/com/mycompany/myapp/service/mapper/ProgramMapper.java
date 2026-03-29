package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Locatie;
import com.mycompany.myapp.domain.Medic;
import com.mycompany.myapp.domain.Program;
import com.mycompany.myapp.service.dto.LocatieDTO;
import com.mycompany.myapp.service.dto.MedicDTO;
import com.mycompany.myapp.service.dto.ProgramDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Program} and its DTO {@link ProgramDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProgramMapper extends EntityMapper<ProgramDTO, Program> {
    @Mapping(target = "medic", source = "medic", qualifiedByName = "medicId")
    @Mapping(target = "locatie", source = "locatie", qualifiedByName = "locatieId")
    ProgramDTO toDto(Program s);

    @Named("medicId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MedicDTO toDtoMedicId(Medic medic);

    @Named("locatieId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LocatieDTO toDtoLocatieId(Locatie locatie);
}
