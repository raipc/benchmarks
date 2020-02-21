package com.github.raipc.workshop2;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 20)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class LinkedListIterationBenchmark {
    @Param({"1", "10", "1000", "1000000"})
    int size;
    @Param({"true", "false"})
    boolean arrayList;

    private List<Integer> list;

    @Setup(Level.Trial)
    public void prepareList() {
        list = arrayList ? new ArrayList<>(size) : new LinkedList<>();
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
    }

    @Benchmark
    public long sum() {
        long sum = 0;
        for (Integer integer : list) {
            sum += integer;
        }
        if (sum != size * (long)(size - 1) / 2) {
            throw new IllegalStateException();
        }
        return sum;
    }

    public static void main(String[] args) throws RunnerException {
        String benchmark = ".*" + LinkedListIterationBenchmark.class.getSimpleName() + ".*";
        Options opt = new OptionsBuilder()
                .include(benchmark)
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(3))
                .measurementIterations(2)
                .addProfiler( GCProfiler.class )
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
