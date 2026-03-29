package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ProgramareTestSamples.*;
import static com.mycompany.myapp.domain.RaportProgramareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RaportProgramareTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RaportProgramare.class);
        RaportProgramare raportProgramare1 = getRaportProgramareSample1();
        RaportProgramare raportProgramare2 = new RaportProgramare();
        assertThat(raportProgramare1).isNotEqualTo(raportProgramare2);

        raportProgramare2.setId(raportProgramare1.getId());
        assertThat(raportProgramare1).isEqualTo(raportProgramare2);

        raportProgramare2 = getRaportProgramareSample2();
        assertThat(raportProgramare1).isNotEqualTo(raportProgramare2);
    }

    @Test
    void programareTest() {
        RaportProgramare raportProgramare = getRaportProgramareRandomSampleGenerator();
        Programare programareBack = getProgramareRandomSampleGenerator();

        raportProgramare.setProgramare(programareBack);
        assertThat(raportProgramare.getProgramare()).isEqualTo(programareBack);

        raportProgramare.programare(null);
        assertThat(raportProgramare.getProgramare()).isNull();
    }
}
