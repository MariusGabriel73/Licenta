package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SpecializareDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SpecializareDTO.class);
        SpecializareDTO specializareDTO1 = new SpecializareDTO();
        specializareDTO1.setId(1L);
        SpecializareDTO specializareDTO2 = new SpecializareDTO();
        assertThat(specializareDTO1).isNotEqualTo(specializareDTO2);
        specializareDTO2.setId(specializareDTO1.getId());
        assertThat(specializareDTO1).isEqualTo(specializareDTO2);
        specializareDTO2.setId(2L);
        assertThat(specializareDTO1).isNotEqualTo(specializareDTO2);
        specializareDTO1.setId(null);
        assertThat(specializareDTO1).isNotEqualTo(specializareDTO2);
    }
}
