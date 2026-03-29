package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PacientDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PacientDTO.class);
        PacientDTO pacientDTO1 = new PacientDTO();
        pacientDTO1.setId(1L);
        PacientDTO pacientDTO2 = new PacientDTO();
        assertThat(pacientDTO1).isNotEqualTo(pacientDTO2);
        pacientDTO2.setId(pacientDTO1.getId());
        assertThat(pacientDTO1).isEqualTo(pacientDTO2);
        pacientDTO2.setId(2L);
        assertThat(pacientDTO1).isNotEqualTo(pacientDTO2);
        pacientDTO1.setId(null);
        assertThat(pacientDTO1).isNotEqualTo(pacientDTO2);
    }
}
