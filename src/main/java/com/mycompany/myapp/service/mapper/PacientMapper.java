package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Pacient;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.PacientDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pacient} and its DTO {@link PacientDTO}.
 */
@Mapper(componentModel = "spring")
public interface PacientMapper extends EntityMapper<PacientDTO, Pacient> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    PacientDTO toDto(Pacient s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    UserDTO toDtoUserId(User user);
}
