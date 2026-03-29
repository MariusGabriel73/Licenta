package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.FisaMedicalaTestSamples.*;
import static com.mycompany.myapp.domain.ProgramareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FisaMedicalaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FisaMedicala.class);
        FisaMedicala fisaMedicala1 = getFisaMedicalaSample1();
        FisaMedicala fisaMedicala2 = new FisaMedicala();
        assertThat(fisaMedicala1).isNotEqualTo(fisaMedicala2);

        fisaMedicala2.setId(fisaMedicala1.getId());
        assertThat(fisaMedicala1).isEqualTo(fisaMedicala2);

        fisaMedicala2 = getFisaMedicalaSample2();
        assertThat(fisaMedicala1).isNotEqualTo(fisaMedicala2);
    }

    @Test
    void programareTest() {
        FisaMedicala fisaMedicala = getFisaMedicalaRandomSampleGenerator();
        Programare programareBack = getProgramareRandomSampleGenerator();

        fisaMedicala.setProgramare(programareBack);
        assertThat(fisaMedicala.getProgramare()).isEqualTo(programareBack);

        fisaMedicala.programare(null);
        assertThat(fisaMedicala.getProgramare()).isNull();
    }
}
