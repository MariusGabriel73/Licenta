package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MedicDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MedicDTO.class);
        MedicDTO medicDTO1 = new MedicDTO();
        medicDTO1.setId(1L);
        MedicDTO medicDTO2 = new MedicDTO();
        assertThat(medicDTO1).isNotEqualTo(medicDTO2);
        medicDTO2.setId(medicDTO1.getId());
        assertThat(medicDTO1).isEqualTo(medicDTO2);
        medicDTO2.setId(2L);
        assertThat(medicDTO1).isNotEqualTo(medicDTO2);
        medicDTO1.setId(null);
        assertThat(medicDTO1).isNotEqualTo(medicDTO2);
    }
}
