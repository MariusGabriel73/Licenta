package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LocatieDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LocatieDTO.class);
        LocatieDTO locatieDTO1 = new LocatieDTO();
        locatieDTO1.setId(1L);
        LocatieDTO locatieDTO2 = new LocatieDTO();
        assertThat(locatieDTO1).isNotEqualTo(locatieDTO2);
        locatieDTO2.setId(locatieDTO1.getId());
        assertThat(locatieDTO1).isEqualTo(locatieDTO2);
        locatieDTO2.setId(2L);
        assertThat(locatieDTO1).isNotEqualTo(locatieDTO2);
        locatieDTO1.setId(null);
        assertThat(locatieDTO1).isNotEqualTo(locatieDTO2);
    }
}
