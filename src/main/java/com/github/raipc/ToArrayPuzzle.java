package com.github.raipc;

import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class ToArrayPuzzle {
    private Collection<Integer> collection;

    @Setup
    public void setup() {
        collection = new ArrayList<>(10000000);
        for (int i = 0; i < 10000000; i++) {
            collection.add(RandomUtils.nextInt());
        }
    }

    @Benchmark
    public Integer[] toSizedArray() {
        return collection.toArray(new Integer[collection.size()]);
    }

    @Benchmark
    public Integer[] toUnsizedArray() {
        return collection.toArray(new Integer[0]);
    }

    @Benchmark
    public Integer[] streamToArray() {
        return collection.stream().toArray(Integer[]::new);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(".*ToArrayPuzzle.*")
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(5))
                .measurementTime(new TimeValue(3, TimeUnit.SECONDS))
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();

    }
}
