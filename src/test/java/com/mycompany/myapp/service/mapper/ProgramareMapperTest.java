package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ProgramareAsserts.*;
import static com.mycompany.myapp.domain.ProgramareTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProgramareMapperTest {

    private ProgramareMapper programareMapper;

    @BeforeEach
    void setUp() {
        programareMapper = new ProgramareMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProgramareSample1();
        var actual = programareMapper.toEntity(programareMapper.toDto(expected));
        assertProgramareAllPropertiesEquals(expected, actual);
    }
}
