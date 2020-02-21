package com.github.raipc.workshop2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 20)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class LinkedListAddToHeadBenchmark {
    @Param({"1", "10", "1000", "1000000"})
    int size;

    @Benchmark
    public Collection<Integer> toArrayDeque() {
        final ArrayDeque<Integer> result = new ArrayDeque<>();
        for (int i = 0; i < size; i++) {
            result.addFirst(i);
        }
        return result;
    }

    @Benchmark
    public Collection<Integer> toArrayDequeOpt() {
        final ArrayDeque<Integer> result = new ArrayDeque<>(size);
        for (int i = 0; i < size; i++) {
            result.addFirst(i);
        }
        return result;
    }

    @Benchmark
    public Collection<Integer> toArrayList() {
        final ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(i);
        }
        Collections.reverse(result);
        return result;
    }

    @Benchmark
    public Collection<Integer> toArrayListOpt() {
        final ArrayList<Integer> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(i);
        }
        Collections.reverse(result);
        return result;
    }

    @Benchmark
    public Collection<Integer> toLinkedList() {
        final LinkedList<Integer> result = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            result.addFirst(i);
        }
        return result;
    }

    public static void main(String[] args) throws RunnerException {
        String benchmark = ".*" + LinkedListAddToHeadBenchmark.class.getSimpleName() + ".*";
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
