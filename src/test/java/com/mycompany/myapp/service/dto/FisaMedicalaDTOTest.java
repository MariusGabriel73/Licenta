package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FisaMedicalaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FisaMedicalaDTO.class);
        FisaMedicalaDTO fisaMedicalaDTO1 = new FisaMedicalaDTO();
        fisaMedicalaDTO1.setId(1L);
        FisaMedicalaDTO fisaMedicalaDTO2 = new FisaMedicalaDTO();
        assertThat(fisaMedicalaDTO1).isNotEqualTo(fisaMedicalaDTO2);
        fisaMedicalaDTO2.setId(fisaMedicalaDTO1.getId());
        assertThat(fisaMedicalaDTO1).isEqualTo(fisaMedicalaDTO2);
        fisaMedicalaDTO2.setId(2L);
        assertThat(fisaMedicalaDTO1).isNotEqualTo(fisaMedicalaDTO2);
        fisaMedicalaDTO1.setId(null);
        assertThat(fisaMedicalaDTO1).isNotEqualTo(fisaMedicalaDTO2);
    }
}
