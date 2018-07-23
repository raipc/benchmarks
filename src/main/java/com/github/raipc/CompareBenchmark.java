package com.github.raipc;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class CompareBenchmark {
    private char ac;
    private char bc;
    private int ai;
    private int bi;

    @Setup
    public void setup() {
        ac = RandomStringUtils.randomAlphanumeric(1).charAt(0);
        bc = RandomStringUtils.randomAlphanumeric(1).charAt(0);
        ai = RandomUtils.nextInt();
        bi = RandomUtils.nextInt();
    }

    @Benchmark
    public boolean compareInt() {
        return ai > bi;
    }

    @Benchmark
    public boolean compareChar() {
        return ac > bc;
    }

    public static void main(String[] args) throws RunnerException {

        final Options build = new OptionsBuilder()
                .include("CompareBenchmark.*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .forks(1)
                .measurementIterations(10)
                .measurementTime(new TimeValue(3, TimeUnit.SECONDS))
                .warmupTime(new TimeValue(3, TimeUnit.SECONDS))
                .mode(Mode.AverageTime)
                .forks(3)
                .build();
        new Runner(build).run();
    }

}
