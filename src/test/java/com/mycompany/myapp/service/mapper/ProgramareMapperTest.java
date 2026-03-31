package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ProgramareAsserts.*;
import static com.mycompany.myapp.domain.ProgramareTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProgramareMapperTest {

    private ProgramareMapper programareMapper;

    @BeforeEach
    void setUp() {
        programareMapper = org.mapstruct.factory.Mappers.getMapper(ProgramareMapper.class);
        org.springframework.test.util.ReflectionTestUtils.setField(
            programareMapper,
            "fisaMedicalaMapper",
            org.mapstruct.factory.Mappers.getMapper(FisaMedicalaMapper.class)
        );
        org.springframework.test.util.ReflectionTestUtils.setField(
            programareMapper,
            "raportProgramareMapper",
            org.mapstruct.factory.Mappers.getMapper(RaportProgramareMapper.class)
        );
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProgramareSample1();
        var actual = programareMapper.toEntity(programareMapper.toDto(expected));
        assertProgramareAllPropertiesEquals(expected, actual);
    }
}
