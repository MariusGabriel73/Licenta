package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProgramareDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProgramareDTO.class);
        ProgramareDTO programareDTO1 = new ProgramareDTO();
        programareDTO1.setId(1L);
        ProgramareDTO programareDTO2 = new ProgramareDTO();
        assertThat(programareDTO1).isNotEqualTo(programareDTO2);
        programareDTO2.setId(programareDTO1.getId());
        assertThat(programareDTO1).isEqualTo(programareDTO2);
        programareDTO2.setId(2L);
        assertThat(programareDTO1).isNotEqualTo(programareDTO2);
        programareDTO1.setId(null);
        assertThat(programareDTO1).isNotEqualTo(programareDTO2);
    }
}
