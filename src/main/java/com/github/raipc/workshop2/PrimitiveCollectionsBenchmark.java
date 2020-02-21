package com.github.raipc.workshop2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.unimi.dsi.fastutil.ints.IntList;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
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
public class PrimitiveCollectionsBenchmark {
    @Param({"1", "10", "1000", "1000000"})
    int size;

    @Benchmark
    public List<Integer> withArrayList() {
        final ArrayList<Integer> res = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            res.add(i);
        }
        return res;
    }

    @Benchmark
    public int[] withArray() {
        int[] res = new int[size];
        for (int i = 0; i < size; i++) {
            res[i] = i;
        }
        return res;
    }

    @Benchmark
    public MutableIntCollection withEclipseCollection() {
        MutableIntCollection res = new IntArrayList();
        for (int i = 0; i < size; i++) {
            res.add(i);
        }
        return res;
    }

    @Benchmark
    public MutableIntCollection withEclipseCollectionOpt() {
        MutableIntCollection res = new IntArrayList(size);
        for (int i = 0; i < size; i++) {
            res.add(i);
        }
        return res;
    }

    @Benchmark
    public IntList withFastUtil() {
        IntList res = new it.unimi.dsi.fastutil.ints.IntArrayList();
        for (int i = 0; i < size; i++) {
            res.add(i);
        }
        return res;
    }

    public static void main(String[] args) throws RunnerException {
        String benchmark = ".*" + PrimitiveCollectionsBenchmark.class.getSimpleName() + ".*";
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
