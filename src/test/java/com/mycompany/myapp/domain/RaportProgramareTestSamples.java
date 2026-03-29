package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RaportProgramareTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static RaportProgramare getRaportProgramareSample1() {
        return new RaportProgramare().id(1L).durataConsultatie(1);
    }

    public static RaportProgramare getRaportProgramareSample2() {
        return new RaportProgramare().id(2L).durataConsultatie(2);
    }

    public static RaportProgramare getRaportProgramareRandomSampleGenerator() {
        return new RaportProgramare().id(longCount.incrementAndGet()).durataConsultatie(intCount.incrementAndGet());
    }
}
