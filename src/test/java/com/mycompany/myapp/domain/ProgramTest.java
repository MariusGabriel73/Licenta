package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.LocatieTestSamples.*;
import static com.mycompany.myapp.domain.MedicTestSamples.*;
import static com.mycompany.myapp.domain.ProgramTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProgramTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Program.class);
        Program program1 = getProgramSample1();
        Program program2 = new Program();
        assertThat(program1).isNotEqualTo(program2);

        program2.setId(program1.getId());
        assertThat(program1).isEqualTo(program2);

        program2 = getProgramSample2();
        assertThat(program1).isNotEqualTo(program2);
    }

    @Test
    void medicTest() {
        Program program = getProgramRandomSampleGenerator();
        Medic medicBack = getMedicRandomSampleGenerator();

        program.setMedic(medicBack);
        assertThat(program.getMedic()).isEqualTo(medicBack);

        program.medic(null);
        assertThat(program.getMedic()).isNull();
    }

    @Test
    void locatieTest() {
        Program program = getProgramRandomSampleGenerator();
        Locatie locatieBack = getLocatieRandomSampleGenerator();

        program.setLocatie(locatieBack);
        assertThat(program.getLocatie()).isEqualTo(locatieBack);

        program.locatie(null);
        assertThat(program.getLocatie()).isNull();
    }
}
