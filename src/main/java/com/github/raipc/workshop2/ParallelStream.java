package com.github.raipc.workshop2;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 20)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ParallelStream {
    @Benchmark
    public int sequentialRandomSum() {
        return new Random().ints(1, 21)
                .distinct()
                .limit(5)
                .sum();
    }

    @Benchmark
    public int parallelRandomSum() {
        return new Random().ints(1, 21)
                .parallel()
                .distinct()
                .limit(5)
                .sum();
    }

    public static void main(String[] args) throws RunnerException {
        String benchmark = ".*" + ParallelStream.class.getSimpleName() + ".*";
        Options opt = new OptionsBuilder()
                .include(benchmark)
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(3))
                .measurementIterations(2)
                .forks(1)
                .build();
        new Runner(opt).run();
    }


}
