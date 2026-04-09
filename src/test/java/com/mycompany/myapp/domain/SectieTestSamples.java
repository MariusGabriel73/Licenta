package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SectieTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Sectie getSectieSample1() {
        return new Sectie().id(1L).nume("nume1");
    }

    public static Sectie getSectieSample2() {
        return new Sectie().id(2L).nume("nume2");
    }

    public static Sectie getSectieRandomSampleGenerator() {
        return new Sectie().id(longCount.incrementAndGet()).nume(UUID.randomUUID().toString());
    }
}
