package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ClinicaTestSamples.*;
import static com.mycompany.myapp.domain.LocatieTestSamples.*;
import static com.mycompany.myapp.domain.MedicTestSamples.*;
import static com.mycompany.myapp.domain.ProgramareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClinicaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Clinica.class);
        Clinica clinica1 = getClinicaSample1();
        Clinica clinica2 = new Clinica();
        assertThat(clinica1).isNotEqualTo(clinica2);

        clinica2.setId(clinica1.getId());
        assertThat(clinica1).isEqualTo(clinica2);

        clinica2 = getClinicaSample2();
        assertThat(clinica1).isNotEqualTo(clinica2);
    }

    @Test
    void locatieTest() {
        Clinica clinica = getClinicaRandomSampleGenerator();
        Locatie locatieBack = getLocatieRandomSampleGenerator();

        clinica.setLocatie(locatieBack);
        assertThat(clinica.getLocatie()).isEqualTo(locatieBack);

        clinica.locatie(null);
        assertThat(clinica.getLocatie()).isNull();
    }

    @Test
    void programareTest() {
        Clinica clinica = getClinicaRandomSampleGenerator();
        Programare programareBack = getProgramareRandomSampleGenerator();

        clinica.addProgramare(programareBack);
        assertThat(clinica.getProgramares()).containsOnly(programareBack);
        assertThat(programareBack.getClinica()).isEqualTo(clinica);

        clinica.removeProgramare(programareBack);
        assertThat(clinica.getProgramares()).doesNotContain(programareBack);
        assertThat(programareBack.getClinica()).isNull();

        clinica.programares(new HashSet<>(Set.of(programareBack)));
        assertThat(clinica.getProgramares()).containsOnly(programareBack);
        assertThat(programareBack.getClinica()).isEqualTo(clinica);

        clinica.setProgramares(new HashSet<>());
        assertThat(clinica.getProgramares()).doesNotContain(programareBack);
        assertThat(programareBack.getClinica()).isNull();
    }

    @Test
    void mediciTest() {
        Clinica clinica = getClinicaRandomSampleGenerator();
        Medic medicBack = getMedicRandomSampleGenerator();

        clinica.addMedici(medicBack);
        assertThat(clinica.getMedicis()).containsOnly(medicBack);
        assertThat(medicBack.getClinicis()).containsOnly(clinica);

        clinica.removeMedici(medicBack);
        assertThat(clinica.getMedicis()).doesNotContain(medicBack);
        assertThat(medicBack.getClinicis()).doesNotContain(clinica);

        clinica.medicis(new HashSet<>(Set.of(medicBack)));
        assertThat(clinica.getMedicis()).containsOnly(medicBack);
        assertThat(medicBack.getClinicis()).containsOnly(clinica);

        clinica.setMedicis(new HashSet<>());
        assertThat(clinica.getMedicis()).doesNotContain(medicBack);
        assertThat(medicBack.getClinicis()).doesNotContain(clinica);
    }
}
