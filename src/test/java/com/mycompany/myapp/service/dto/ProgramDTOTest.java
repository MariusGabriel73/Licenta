package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProgramDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProgramDTO.class);
        ProgramDTO programDTO1 = new ProgramDTO();
        programDTO1.setId(1L);
        ProgramDTO programDTO2 = new ProgramDTO();
        assertThat(programDTO1).isNotEqualTo(programDTO2);
        programDTO2.setId(programDTO1.getId());
        assertThat(programDTO1).isEqualTo(programDTO2);
        programDTO2.setId(2L);
        assertThat(programDTO1).isNotEqualTo(programDTO2);
        programDTO1.setId(null);
        assertThat(programDTO1).isNotEqualTo(programDTO2);
    }
}
