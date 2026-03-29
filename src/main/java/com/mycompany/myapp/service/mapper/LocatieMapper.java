package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Locatie;
import com.mycompany.myapp.service.dto.LocatieDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Locatie} and its DTO {@link LocatieDTO}.
 */
@Mapper(componentModel = "spring")
public interface LocatieMapper extends EntityMapper<LocatieDTO, Locatie> {}
