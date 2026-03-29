package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FisaMedicalaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FisaMedicala getFisaMedicalaSample1() {
        return new FisaMedicala().id(1L).diagnostic("diagnostic1").tratament("tratament1").recomandari("recomandari1");
    }

    public static FisaMedicala getFisaMedicalaSample2() {
        return new FisaMedicala().id(2L).diagnostic("diagnostic2").tratament("tratament2").recomandari("recomandari2");
    }

    public static FisaMedicala getFisaMedicalaRandomSampleGenerator() {
        return new FisaMedicala()
            .id(longCount.incrementAndGet())
            .diagnostic(UUID.randomUUID().toString())
            .tratament(UUID.randomUUID().toString())
            .recomandari(UUID.randomUUID().toString());
    }
}
