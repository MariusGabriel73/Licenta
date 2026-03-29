package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SpecializareTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Specializare getSpecializareSample1() {
        return new Specializare().id(1L).nume("nume1");
    }

    public static Specializare getSpecializareSample2() {
        return new Specializare().id(2L).nume("nume2");
    }

    public static Specializare getSpecializareRandomSampleGenerator() {
        return new Specializare().id(longCount.incrementAndGet()).nume(UUID.randomUUID().toString());
    }
}
