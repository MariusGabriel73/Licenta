package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MedicTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Medic getMedicSample1() {
        return new Medic().id(1L).gradProfesional("gradProfesional1").telefon("telefon1");
    }

    public static Medic getMedicSample2() {
        return new Medic().id(2L).gradProfesional("gradProfesional2").telefon("telefon2");
    }

    public static Medic getMedicRandomSampleGenerator() {
        return new Medic()
            .id(longCount.incrementAndGet())
            .gradProfesional(UUID.randomUUID().toString())
            .telefon(UUID.randomUUID().toString());
    }
}
