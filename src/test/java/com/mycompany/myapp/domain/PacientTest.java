package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.PacientTestSamples.*;
import static com.mycompany.myapp.domain.ProgramareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PacientTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pacient.class);
        Pacient pacient1 = getPacientSample1();
        Pacient pacient2 = new Pacient();
        assertThat(pacient1).isNotEqualTo(pacient2);

        pacient2.setId(pacient1.getId());
        assertThat(pacient1).isEqualTo(pacient2);

        pacient2 = getPacientSample2();
        assertThat(pacient1).isNotEqualTo(pacient2);
    }

    @Test
    void programareTest() {
        Pacient pacient = getPacientRandomSampleGenerator();
        Programare programareBack = getProgramareRandomSampleGenerator();

        pacient.addProgramare(programareBack);
        assertThat(pacient.getProgramares()).containsOnly(programareBack);
        assertThat(programareBack.getPacient()).isEqualTo(pacient);

        pacient.removeProgramare(programareBack);
        assertThat(pacient.getProgramares()).doesNotContain(programareBack);
        assertThat(programareBack.getPacient()).isNull();

        pacient.programares(new HashSet<>(Set.of(programareBack)));
        assertThat(pacient.getProgramares()).containsOnly(programareBack);
        assertThat(programareBack.getPacient()).isEqualTo(pacient);

        pacient.setProgramares(new HashSet<>());
        assertThat(pacient.getProgramares()).doesNotContain(programareBack);
        assertThat(programareBack.getPacient()).isNull();
    }
}
