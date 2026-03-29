package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ClinicaTestSamples.*;
import static com.mycompany.myapp.domain.FisaMedicalaTestSamples.*;
import static com.mycompany.myapp.domain.MedicTestSamples.*;
import static com.mycompany.myapp.domain.PacientTestSamples.*;
import static com.mycompany.myapp.domain.ProgramareTestSamples.*;
import static com.mycompany.myapp.domain.RaportProgramareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProgramareTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Programare.class);
        Programare programare1 = getProgramareSample1();
        Programare programare2 = new Programare();
        assertThat(programare1).isNotEqualTo(programare2);

        programare2.setId(programare1.getId());
        assertThat(programare1).isEqualTo(programare2);

        programare2 = getProgramareSample2();
        assertThat(programare1).isNotEqualTo(programare2);
    }

    @Test
    void pacientTest() {
        Programare programare = getProgramareRandomSampleGenerator();
        Pacient pacientBack = getPacientRandomSampleGenerator();

        programare.setPacient(pacientBack);
        assertThat(programare.getPacient()).isEqualTo(pacientBack);

        programare.pacient(null);
        assertThat(programare.getPacient()).isNull();
    }

    @Test
    void medicTest() {
        Programare programare = getProgramareRandomSampleGenerator();
        Medic medicBack = getMedicRandomSampleGenerator();

        programare.setMedic(medicBack);
        assertThat(programare.getMedic()).isEqualTo(medicBack);

        programare.medic(null);
        assertThat(programare.getMedic()).isNull();
    }

    @Test
    void clinicaTest() {
        Programare programare = getProgramareRandomSampleGenerator();
        Clinica clinicaBack = getClinicaRandomSampleGenerator();

        programare.setClinica(clinicaBack);
        assertThat(programare.getClinica()).isEqualTo(clinicaBack);

        programare.clinica(null);
        assertThat(programare.getClinica()).isNull();
    }

    @Test
    void fisaMedicalaTest() {
        Programare programare = getProgramareRandomSampleGenerator();
        FisaMedicala fisaMedicalaBack = getFisaMedicalaRandomSampleGenerator();

        programare.setFisaMedicala(fisaMedicalaBack);
        assertThat(programare.getFisaMedicala()).isEqualTo(fisaMedicalaBack);
        assertThat(fisaMedicalaBack.getProgramare()).isEqualTo(programare);

        programare.fisaMedicala(null);
        assertThat(programare.getFisaMedicala()).isNull();
        assertThat(fisaMedicalaBack.getProgramare()).isNull();
    }

    @Test
    void raportProgramareTest() {
        Programare programare = getProgramareRandomSampleGenerator();
        RaportProgramare raportProgramareBack = getRaportProgramareRandomSampleGenerator();

        programare.setRaportProgramare(raportProgramareBack);
        assertThat(programare.getRaportProgramare()).isEqualTo(raportProgramareBack);
        assertThat(raportProgramareBack.getProgramare()).isEqualTo(programare);

        programare.raportProgramare(null);
        assertThat(programare.getRaportProgramare()).isNull();
        assertThat(raportProgramareBack.getProgramare()).isNull();
    }
}
