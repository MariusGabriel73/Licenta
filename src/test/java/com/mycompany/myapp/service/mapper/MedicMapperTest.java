package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.MedicAsserts.*;
import static com.mycompany.myapp.domain.MedicTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MedicMapperTest {

    private MedicMapper medicMapper;

    @BeforeEach
    void setUp() {
        medicMapper = new MedicMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMedicSample1();
        var actual = medicMapper.toEntity(medicMapper.toDto(expected));
        assertMedicAllPropertiesEquals(expected, actual);
    }
}
