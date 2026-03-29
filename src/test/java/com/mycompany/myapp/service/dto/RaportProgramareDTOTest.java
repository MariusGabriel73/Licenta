package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RaportProgramareDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RaportProgramareDTO.class);
        RaportProgramareDTO raportProgramareDTO1 = new RaportProgramareDTO();
        raportProgramareDTO1.setId(1L);
        RaportProgramareDTO raportProgramareDTO2 = new RaportProgramareDTO();
        assertThat(raportProgramareDTO1).isNotEqualTo(raportProgramareDTO2);
        raportProgramareDTO2.setId(raportProgramareDTO1.getId());
        assertThat(raportProgramareDTO1).isEqualTo(raportProgramareDTO2);
        raportProgramareDTO2.setId(2L);
        assertThat(raportProgramareDTO1).isNotEqualTo(raportProgramareDTO2);
        raportProgramareDTO1.setId(null);
        assertThat(raportProgramareDTO1).isNotEqualTo(raportProgramareDTO2);
    }
}
