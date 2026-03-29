package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ClinicaTestSamples.*;
import static com.mycompany.myapp.domain.MedicTestSamples.*;
import static com.mycompany.myapp.domain.ProgramTestSamples.*;
import static com.mycompany.myapp.domain.ProgramareTestSamples.*;
import static com.mycompany.myapp.domain.SectieTestSamples.*;
import static com.mycompany.myapp.domain.SpecializareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MedicTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Medic.class);
        Medic medic1 = getMedicSample1();
        Medic medic2 = new Medic();
        assertThat(medic1).isNotEqualTo(medic2);

        medic2.setId(medic1.getId());
        assertThat(medic1).isEqualTo(medic2);

        medic2 = getMedicSample2();
        assertThat(medic1).isNotEqualTo(medic2);
    }

    @Test
    void sectieTest() {
        Medic medic = getMedicRandomSampleGenerator();
        Sectie sectieBack = getSectieRandomSampleGenerator();

        medic.setSectie(sectieBack);
        assertThat(medic.getSectie()).isEqualTo(sectieBack);

        medic.sectie(null);
        assertThat(medic.getSectie()).isNull();
    }

    @Test
    void cliniciTest() {
        Medic medic = getMedicRandomSampleGenerator();
        Clinica clinicaBack = getClinicaRandomSampleGenerator();

        medic.addClinici(clinicaBack);
        assertThat(medic.getClinicis()).containsOnly(clinicaBack);

        medic.removeClinici(clinicaBack);
        assertThat(medic.getClinicis()).doesNotContain(clinicaBack);

        medic.clinicis(new HashSet<>(Set.of(clinicaBack)));
        assertThat(medic.getClinicis()).containsOnly(clinicaBack);

        medic.setClinicis(new HashSet<>());
        assertThat(medic.getClinicis()).doesNotContain(clinicaBack);
    }

    @Test
    void programTest() {
        Medic medic = getMedicRandomSampleGenerator();
        Program programBack = getProgramRandomSampleGenerator();

        medic.addProgram(programBack);
        assertThat(medic.getPrograms()).containsOnly(programBack);
        assertThat(programBack.getMedic()).isEqualTo(medic);

        medic.removeProgram(programBack);
        assertThat(medic.getPrograms()).doesNotContain(programBack);
        assertThat(programBack.getMedic()).isNull();

        medic.programs(new HashSet<>(Set.of(programBack)));
        assertThat(medic.getPrograms()).containsOnly(programBack);
        assertThat(programBack.getMedic()).isEqualTo(medic);

        medic.setPrograms(new HashSet<>());
        assertThat(medic.getPrograms()).doesNotContain(programBack);
        assertThat(programBack.getMedic()).isNull();
    }

    @Test
    void programareTest() {
        Medic medic = getMedicRandomSampleGenerator();
        Programare programareBack = getProgramareRandomSampleGenerator();

        medic.addProgramare(programareBack);
        assertThat(medic.getProgramares()).containsOnly(programareBack);
        assertThat(programareBack.getMedic()).isEqualTo(medic);

        medic.removeProgramare(programareBack);
        assertThat(medic.getProgramares()).doesNotContain(programareBack);
        assertThat(programareBack.getMedic()).isNull();

        medic.programares(new HashSet<>(Set.of(programareBack)));
        assertThat(medic.getProgramares()).containsOnly(programareBack);
        assertThat(programareBack.getMedic()).isEqualTo(medic);

        medic.setProgramares(new HashSet<>());
        assertThat(medic.getProgramares()).doesNotContain(programareBack);
        assertThat(programareBack.getMedic()).isNull();
    }

    @Test
    void specializariTest() {
        Medic medic = getMedicRandomSampleGenerator();
        Specializare specializareBack = getSpecializareRandomSampleGenerator();

        medic.addSpecializari(specializareBack);
        assertThat(medic.getSpecializaris()).containsOnly(specializareBack);

        medic.removeSpecializari(specializareBack);
        assertThat(medic.getSpecializaris()).doesNotContain(specializareBack);

        medic.specializaris(new HashSet<>(Set.of(specializareBack)));
        assertThat(medic.getSpecializaris()).containsOnly(specializareBack);

        medic.setSpecializaris(new HashSet<>());
        assertThat(medic.getSpecializaris()).doesNotContain(specializareBack);
    }
}
