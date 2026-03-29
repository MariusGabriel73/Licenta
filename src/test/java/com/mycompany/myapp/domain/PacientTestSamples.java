package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PacientTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Pacient getPacientSample1() {
        return new Pacient().id(1L).cnp("cnp1").telefon("telefon1").adresa("adresa1");
    }

    public static Pacient getPacientSample2() {
        return new Pacient().id(2L).cnp("cnp2").telefon("telefon2").adresa("adresa2");
    }

    public static Pacient getPacientRandomSampleGenerator() {
        return new Pacient()
            .id(longCount.incrementAndGet())
            .cnp(UUID.randomUUID().toString())
            .telefon(UUID.randomUUID().toString())
            .adresa(UUID.randomUUID().toString());
    }
}
