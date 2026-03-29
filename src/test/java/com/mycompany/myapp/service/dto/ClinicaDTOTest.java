package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClinicaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClinicaDTO.class);
        ClinicaDTO clinicaDTO1 = new ClinicaDTO();
        clinicaDTO1.setId(1L);
        ClinicaDTO clinicaDTO2 = new ClinicaDTO();
        assertThat(clinicaDTO1).isNotEqualTo(clinicaDTO2);
        clinicaDTO2.setId(clinicaDTO1.getId());
        assertThat(clinicaDTO1).isEqualTo(clinicaDTO2);
        clinicaDTO2.setId(2L);
        assertThat(clinicaDTO1).isNotEqualTo(clinicaDTO2);
        clinicaDTO1.setId(null);
        assertThat(clinicaDTO1).isNotEqualTo(clinicaDTO2);
    }
}
