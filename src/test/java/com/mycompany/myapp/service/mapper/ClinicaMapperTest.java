package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ClinicaAsserts.*;
import static com.mycompany.myapp.domain.ClinicaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClinicaMapperTest {

    private ClinicaMapper clinicaMapper;

    @BeforeEach
    void setUp() {
        clinicaMapper = org.mapstruct.factory.Mappers.getMapper(ClinicaMapper.class);
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClinicaSample1();
        var actual = clinicaMapper.toEntity(clinicaMapper.toDto(expected));
        assertClinicaAllPropertiesEquals(expected, actual);
    }
}
