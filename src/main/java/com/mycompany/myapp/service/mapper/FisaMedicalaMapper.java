package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.FisaMedicala;
import com.mycompany.myapp.domain.Programare;
import com.mycompany.myapp.service.dto.FisaMedicalaDTO;
import com.mycompany.myapp.service.dto.ProgramareDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FisaMedicala} and its DTO {@link FisaMedicalaDTO}.
 */
@Mapper(componentModel = "spring")
public interface FisaMedicalaMapper extends EntityMapper<FisaMedicalaDTO, FisaMedicala> {
    @Mapping(target = "programare", source = "programare", qualifiedByName = "programareId")
    FisaMedicalaDTO toDto(FisaMedicala s);

    @Named("programareId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProgramareDTO toDtoProgramareId(Programare programare);
}
