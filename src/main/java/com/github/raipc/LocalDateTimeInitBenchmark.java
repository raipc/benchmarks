package com.github.raipc;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.time.*;
import java.util.concurrent.TimeUnit;

public class LocalDateTimeInitBenchmark {
    private static final Clock systemClockWithOffset = Clock.system(ZoneOffset.UTC);
    private static final Clock systemClockWithZoneId = Clock.system(ZoneId.of("UTC"));

    @Benchmark
    public LocalDateTime withZoneOffset() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    @Benchmark
    public LocalDateTime withCachedClockWithOffset() {
        return LocalDateTime.now(systemClockWithOffset);
    }

    @Benchmark
    public LocalDateTime withCachedClockWithZoneId() {
        return LocalDateTime.now(systemClockWithZoneId);
    }

    @Benchmark
    public LocalDateTime withInstant() {
        final Instant instant = systemClockWithZoneId.instant();
        return LocalDateTime.ofEpochSecond(instant.getEpochSecond(), instant.getNano(), ZoneOffset.UTC);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*LocalDateTimeInitBenchmark.*.*")
                .warmupIterations(2)
                .warmupTime(TimeValue.seconds(5))
                .measurementIterations(5)
                .measurementTime(TimeValue.seconds(10))
                .mode(Mode.SampleTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
