package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MedicTestSamples.*;
import static com.mycompany.myapp.domain.SpecializareTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SpecializareTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Specializare.class);
        Specializare specializare1 = getSpecializareSample1();
        Specializare specializare2 = new Specializare();
        assertThat(specializare1).isNotEqualTo(specializare2);

        specializare2.setId(specializare1.getId());
        assertThat(specializare1).isEqualTo(specializare2);

        specializare2 = getSpecializareSample2();
        assertThat(specializare1).isNotEqualTo(specializare2);
    }

    @Test
    void mediciTest() {
        Specializare specializare = getSpecializareRandomSampleGenerator();
        Medic medicBack = getMedicRandomSampleGenerator();

        specializare.addMedici(medicBack);
        assertThat(specializare.getMedicis()).containsOnly(medicBack);
        assertThat(medicBack.getSpecializaris()).containsOnly(specializare);

        specializare.removeMedici(medicBack);
        assertThat(specializare.getMedicis()).doesNotContain(medicBack);
        assertThat(medicBack.getSpecializaris()).doesNotContain(specializare);

        specializare.medicis(new HashSet<>(Set.of(medicBack)));
        assertThat(specializare.getMedicis()).containsOnly(medicBack);
        assertThat(medicBack.getSpecializaris()).containsOnly(specializare);

        specializare.setMedicis(new HashSet<>());
        assertThat(specializare.getMedicis()).doesNotContain(medicBack);
        assertThat(medicBack.getSpecializaris()).doesNotContain(specializare);
    }
}
