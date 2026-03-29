package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClinicaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Clinica getClinicaSample1() {
        return new Clinica().id(1L).nume("nume1").telefon("telefon1").email("email1");
    }

    public static Clinica getClinicaSample2() {
        return new Clinica().id(2L).nume("nume2").telefon("telefon2").email("email2");
    }

    public static Clinica getClinicaRandomSampleGenerator() {
        return new Clinica()
            .id(longCount.incrementAndGet())
            .nume(UUID.randomUUID().toString())
            .telefon(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString());
    }
}
