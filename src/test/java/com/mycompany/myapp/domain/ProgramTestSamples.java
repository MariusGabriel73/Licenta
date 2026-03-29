package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProgramTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Program getProgramSample1() {
        return new Program().id(1L).ziuaSaptamanii("ziuaSaptamanii1");
    }

    public static Program getProgramSample2() {
        return new Program().id(2L).ziuaSaptamanii("ziuaSaptamanii2");
    }

    public static Program getProgramRandomSampleGenerator() {
        return new Program().id(longCount.incrementAndGet()).ziuaSaptamanii(UUID.randomUUID().toString());
    }
}
