package com.github.raipc.workshop2;

import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 20)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class PrimitiveMapsBenchmark {
    @Param({"1", "10", "1000", "1000000"})
    int size;

    private static final String value = "test";

    @Benchmark
    public Map<Long, String> withJavaMap() {
        Map<Long, String> res = new HashMap<>();
        for (long i = 0; i < size; i++) {
            res.put(i, value);
        }
        return res;
    }

    @Benchmark
    public MutableLongObjectMap withEclipseMap() {
        MutableLongObjectMap<String> res = new LongObjectHashMap<>();
        for (long i = 0; i < size; i++) {
            res.put(i, value);
        }
        return res;
    }

    @Benchmark
    public Long2ObjectMap withFastUtilMap() {
        Long2ObjectMap<String> res = new Long2ObjectOpenHashMap<>();
        for (int i = 0; i < size; i++) {
            res.put(i, value);
        }
        return res;
    }

    @Benchmark
    public Long2ObjectMap withFastUtilMap2() {
        Long2ObjectMap<String> res = new Long2ObjectAVLTreeMap<>();
        for (int i = 0; i < size; i++) {
            res.put(i, value);
        }
        return res;
    }

    public static void main(String[] args) throws RunnerException {
        String benchmark = ".*" + PrimitiveMapsBenchmark.class.getSimpleName() + ".*";
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
