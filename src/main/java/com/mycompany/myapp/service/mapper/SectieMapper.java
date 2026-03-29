package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Sectie;
import com.mycompany.myapp.service.dto.SectieDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Sectie} and its DTO {@link SectieDTO}.
 */
@Mapper(componentModel = "spring")
public interface SectieMapper extends EntityMapper<SectieDTO, Sectie> {}
