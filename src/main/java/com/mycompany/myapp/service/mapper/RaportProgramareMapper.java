package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Programare;
import com.mycompany.myapp.domain.RaportProgramare;
import com.mycompany.myapp.service.dto.ProgramareDTO;
import com.mycompany.myapp.service.dto.RaportProgramareDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RaportProgramare} and its DTO {@link RaportProgramareDTO}.
 */
@Mapper(componentModel = "spring")
public interface RaportProgramareMapper extends EntityMapper<RaportProgramareDTO, RaportProgramare> {
    @Mapping(target = "programare", source = "programare", qualifiedByName = "programareId")
    RaportProgramareDTO toDto(RaportProgramare s);

    @Named("programareId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProgramareDTO toDtoProgramareId(Programare programare);
}
