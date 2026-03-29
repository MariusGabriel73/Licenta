package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.SpecializareAsserts.*;
import static com.mycompany.myapp.domain.SpecializareTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpecializareMapperTest {

    private SpecializareMapper specializareMapper;

    @BeforeEach
    void setUp() {
        specializareMapper = new SpecializareMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSpecializareSample1();
        var actual = specializareMapper.toEntity(specializareMapper.toDto(expected));
        assertSpecializareAllPropertiesEquals(expected, actual);
    }
}
