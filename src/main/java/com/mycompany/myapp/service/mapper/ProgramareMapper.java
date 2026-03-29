package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Clinica;
import com.mycompany.myapp.domain.Medic;
import com.mycompany.myapp.domain.Pacient;
import com.mycompany.myapp.domain.Programare;
import com.mycompany.myapp.service.dto.ClinicaDTO;
import com.mycompany.myapp.service.dto.MedicDTO;
import com.mycompany.myapp.service.dto.PacientDTO;
import com.mycompany.myapp.service.dto.ProgramareDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Programare} and its DTO {@link ProgramareDTO}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProgramareMapper extends EntityMapper<ProgramareDTO, Programare> {
    @Override
    @Mapping(target = "pacient", source = "pacient", qualifiedByName = "pacientId")
    @Mapping(target = "medic", source = "medic", qualifiedByName = "medicId")
    @Mapping(target = "clinica", source = "clinica", qualifiedByName = "clinicaId")
    ProgramareDTO toDto(Programare s);

    @Override
    @Mapping(target = "pacientId", source = "pacient.id")
    @Mapping(target = "medicId", source = "medic.id")
    @Mapping(target = "clinicaId", source = "clinica.id")
    @Mapping(target = "fisaMedicala", ignore = true)
    @Mapping(target = "raportProgramare", ignore = true)
    Programare toEntity(ProgramareDTO dto);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @InheritConfiguration(name = "toEntity")
    void partialUpdate(@MappingTarget Programare entity, ProgramareDTO dto);

    @Named("pacientId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    PacientDTO toDtoPacientId(Pacient pacient);

    @Named("medicId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "gradProfesional", source = "gradProfesional")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    MedicDTO toDtoMedicId(Medic medic);

    @Named("clinicaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nume", source = "nume")
    ClinicaDTO toDtoClinicaId(Clinica clinica);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    UserDTO toDtoUserId(com.mycompany.myapp.domain.User user);
}
