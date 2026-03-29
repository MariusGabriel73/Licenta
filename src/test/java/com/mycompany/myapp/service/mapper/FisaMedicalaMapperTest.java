package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.FisaMedicalaAsserts.*;
import static com.mycompany.myapp.domain.FisaMedicalaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FisaMedicalaMapperTest {

    private FisaMedicalaMapper fisaMedicalaMapper;

    @BeforeEach
    void setUp() {
        fisaMedicalaMapper = new FisaMedicalaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFisaMedicalaSample1();
        var actual = fisaMedicalaMapper.toEntity(fisaMedicalaMapper.toDto(expected));
        assertFisaMedicalaAllPropertiesEquals(expected, actual);
    }
}
