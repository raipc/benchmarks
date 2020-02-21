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
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class LinkedListCreationBenchmark {
    @Param({"1", "10", "1000", "1000000"})
    int size;

    @Benchmark
    public List<Integer> fillArrayList() {
        return addNumbers(new ArrayList<>());
    }

    @Benchmark
    public List<Integer> fillArrayListOpt() {
        return addNumbers(new ArrayList<>(size));
    }

    @Benchmark
    public List<Integer> fillLinkedList() {
        return addNumbers(new LinkedList<>());
    }

    private List<Integer> addNumbers(List<Integer> list) {
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        return list;
    }

    public static void main(String[] args) throws RunnerException {
        String benchmark = ".*" + LinkedListCreationBenchmark.class.getSimpleName() + ".*";
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
