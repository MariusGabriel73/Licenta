package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.SectieAsserts.*;
import static com.mycompany.myapp.domain.SectieTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SectieMapperTest {

    private SectieMapper sectieMapper;

    @BeforeEach
    void setUp() {
        sectieMapper = new SectieMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSectieSample1();
        var actual = sectieMapper.toEntity(sectieMapper.toDto(expected));
        assertSectieAllPropertiesEquals(expected, actual);
    }
}
