package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.RaportProgramareAsserts.*;
import static com.mycompany.myapp.domain.RaportProgramareTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RaportProgramareMapperTest {

    private RaportProgramareMapper raportProgramareMapper;

    @BeforeEach
    void setUp() {
        raportProgramareMapper = new RaportProgramareMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRaportProgramareSample1();
        var actual = raportProgramareMapper.toEntity(raportProgramareMapper.toDto(expected));
        assertRaportProgramareAllPropertiesEquals(expected, actual);
    }
}
