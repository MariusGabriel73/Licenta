package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProgramareTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Programare getProgramareSample1() {
        return new Programare().id(1L).observatii("observatii1");
    }

    public static Programare getProgramareSample2() {
        return new Programare().id(2L).observatii("observatii2");
    }

    public static Programare getProgramareRandomSampleGenerator() {
        return new Programare().id(longCount.incrementAndGet()).observatii(UUID.randomUUID().toString());
    }
}
