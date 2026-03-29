package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ProgramAsserts.*;
import static com.mycompany.myapp.domain.ProgramTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProgramMapperTest {

    private ProgramMapper programMapper;

    @BeforeEach
    void setUp() {
        programMapper = new ProgramMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProgramSample1();
        var actual = programMapper.toEntity(programMapper.toDto(expected));
        assertProgramAllPropertiesEquals(expected, actual);
    }
}
