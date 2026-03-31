package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.PacientAsserts.*;
import static com.mycompany.myapp.domain.PacientTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PacientMapperTest {

    private PacientMapper pacientMapper;

    @BeforeEach
    void setUp() {
        pacientMapper = org.mapstruct.factory.Mappers.getMapper(PacientMapper.class);
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPacientSample1();
        var actual = pacientMapper.toEntity(pacientMapper.toDto(expected));
        assertPacientAllPropertiesEquals(expected, actual);
    }
}
