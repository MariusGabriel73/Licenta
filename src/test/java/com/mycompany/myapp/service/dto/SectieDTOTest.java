package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SectieDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SectieDTO.class);
        SectieDTO sectieDTO1 = new SectieDTO();
        sectieDTO1.setId(1L);
        SectieDTO sectieDTO2 = new SectieDTO();
        assertThat(sectieDTO1).isNotEqualTo(sectieDTO2);
        sectieDTO2.setId(sectieDTO1.getId());
        assertThat(sectieDTO1).isEqualTo(sectieDTO2);
        sectieDTO2.setId(2L);
        assertThat(sectieDTO1).isNotEqualTo(sectieDTO2);
        sectieDTO1.setId(null);
        assertThat(sectieDTO1).isNotEqualTo(sectieDTO2);
    }
}
