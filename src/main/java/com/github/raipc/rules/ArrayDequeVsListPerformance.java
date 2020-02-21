package com.github.raipc.rules;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.carrotsearch.hppc.ObjectArrayDeque;
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
@Measurement(iterations = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ArrayDequeVsListPerformance {
    private static final BigDecimal[] data = IntStream.range(0, 1_000_000).mapToObj(BigDecimal::valueOf).toArray(BigDecimal[]::new);
    private static final int[] ADD_INDEXES = IntStream.range(0, 1_000_000).map(i -> ThreadLocalRandom.current().nextInt(i + 1)).toArray();

//    @Param({"1000", "1000000"})
    @Param({"1", "10", "1000", "100000", "999900",  "1000000"})
    private int windowSize;

    @Benchmark
    public Collection<BigDecimal> addToArrayEnd() {
        final List<BigDecimal> decimalsArray = new ArrayList<>();
        for (BigDecimal datum : data) {
            if (decimalsArray.size() > windowSize) {
                decimalsArray.remove(0);
            }
            decimalsArray.add(datum);
        }
        return decimalsArray;
    }

    @Benchmark
    public Collection<BigDecimal> addToArrayByIndex() {
        final List<BigDecimal> decimalsArray = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            if (decimalsArray.size() > windowSize) {
                decimalsArray.remove(0);
                decimalsArray.add(data[i]);
            } else{
                decimalsArray.add(ADD_INDEXES[i], data[i]);
            }
        }
        return decimalsArray;
    }

    @Benchmark
    public Collection<BigDecimal> addToDequeEnd() {
        final Deque<BigDecimal> decimalsDeque = new ArrayDeque<>();
        for (BigDecimal datum : data) {
            if (decimalsDeque.size() > windowSize) {
                decimalsDeque.removeFirst();
            }
            decimalsDeque.add(datum);
        }
        return decimalsDeque;
    }

    @Benchmark
    public ObjectArrayDeque<BigDecimal> addToCarrotDequeEnd() {
        final ObjectArrayDeque<BigDecimal> decimalsDeque = new ObjectArrayDeque<>();
        for (BigDecimal datum : data) {
            if (decimalsDeque.size() > windowSize) {
                decimalsDeque.removeFirst();
            }
            decimalsDeque.addLast(datum);
        }
        return decimalsDeque;
    }

    private static void addToDequeByIndex(Deque<BigDecimal> deque, int index, BigDecimal element) {
        final int size = deque.size();
        if (index == 0) {
            deque.addFirst(element);
        } else if (index == size + 1) {
            deque.addLast(element);
        } else if (index == size) {
            BigDecimal tmpVal = deque.removeLast();
            deque.addLast(element);
            deque.addLast(tmpVal);
        } else if (index == 1) {
            BigDecimal tmpVal = deque.removeFirst();
            deque.addFirst(element);
            deque.addFirst(tmpVal);
        } else if (index < size / 2) {
            BigDecimal[] tmp = new BigDecimal[index];
            for (int i = 0; i < index; ++i) {
                tmp[i] = deque.removeFirst();
            }
            deque.addFirst(element);
            for (int i = index - 1; i >= 0; --i) {
                deque.addFirst(tmp[i]);
            }
        } else {
            BigDecimal[] tmp = new BigDecimal[size - index];
            for (int i = index; i < size; ++i) {
                tmp[size - i - 1] = deque.removeLast();
            }
            deque.addLast(element);
            Collections.addAll(deque, tmp);
        }
    }

//    @Benchmark
//    public Collection<BigDecimal> addToDequeByIndex() {
//        final Deque<BigDecimal> decimalsDeque = new ArrayDeque<>();
//        for (int i = 0; i < data.length; i++) {
//            if (decimalsDeque.size() > windowSize) {
//                decimalsDeque.removeFirst();
//                decimalsDeque.add(data[i]);
//            } else{
//                addToDequeByIndex(decimalsDeque, ADD_INDEXES[i], data[i]);
//            }
//        }
//        return decimalsDeque;
//    }

    public static void main(String[] args) throws RunnerException {
//        final ArrayDeque<BigDecimal> bigDecimals = new ArrayDeque<>();
//        bigDecimals.addLast(BigDecimal.ZERO);
//        bigDecimals.addLast(BigDecimal.ONE);
//        bigDecimals.addLast(BigDecimal.valueOf(2));
//        bigDecimals.addLast(BigDecimal.valueOf(3));
//        bigDecimals.addLast(BigDecimal.valueOf(4));
//        addToDequeByIndex(bigDecimals, 6, BigDecimal.valueOf(5));
//        System.out.println(bigDecimals);
//        addToDequeByIndex(bigDecimals, 0, BigDecimal.valueOf(-1));
//        System.out.println(bigDecimals);
//        addToDequeByIndex(bigDecimals, 3, BigDecimal.valueOf(1.9));
//        System.out.println(bigDecimals);
        String benchmark = ".*" + ArrayDequeVsListPerformance.class.getSimpleName() + ".*";
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
