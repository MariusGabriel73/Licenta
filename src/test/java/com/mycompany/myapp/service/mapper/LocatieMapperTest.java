package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.LocatieAsserts.*;
import static com.mycompany.myapp.domain.LocatieTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocatieMapperTest {

    private LocatieMapper locatieMapper;

    @BeforeEach
    void setUp() {
        locatieMapper = new LocatieMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLocatieSample1();
        var actual = locatieMapper.toEntity(locatieMapper.toDto(expected));
        assertLocatieAllPropertiesEquals(expected, actual);
    }
}
