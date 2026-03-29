package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MedicTestSamples.*;
import static com.mycompany.myapp.domain.SectieTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SectieTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sectie.class);
        Sectie sectie1 = getSectieSample1();
        Sectie sectie2 = new Sectie();
        assertThat(sectie1).isNotEqualTo(sectie2);

        sectie2.setId(sectie1.getId());
        assertThat(sectie1).isEqualTo(sectie2);

        sectie2 = getSectieSample2();
        assertThat(sectie1).isNotEqualTo(sectie2);
    }

    @Test
    void mediciTest() {
        Sectie sectie = getSectieRandomSampleGenerator();
        Medic medicBack = getMedicRandomSampleGenerator();

        sectie.addMedici(medicBack);
        assertThat(sectie.getMedicis()).containsOnly(medicBack);
        assertThat(medicBack.getSectie()).isEqualTo(sectie);

        sectie.removeMedici(medicBack);
        assertThat(sectie.getMedicis()).doesNotContain(medicBack);
        assertThat(medicBack.getSectie()).isNull();

        sectie.medicis(new HashSet<>(Set.of(medicBack)));
        assertThat(sectie.getMedicis()).containsOnly(medicBack);
        assertThat(medicBack.getSectie()).isEqualTo(sectie);

        sectie.setMedicis(new HashSet<>());
        assertThat(sectie.getMedicis()).doesNotContain(medicBack);
        assertThat(medicBack.getSectie()).isNull();
    }
}
