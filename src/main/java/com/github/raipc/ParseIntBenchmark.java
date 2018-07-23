package com.github.raipc;

import com.github.raipc.impl.TimeUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 20)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class ParseIntBenchmark {
    private static final int SIZE = 100000;
    private static final int DIGITS_COUNT = 4;
    int[] numbers;
    int[] prefixes;
    String[] numbersAsString;

    @Setup
    public void setup() {
        numbers = new Random().ints(SIZE, 100000000, 999999999).toArray();
        numbersAsString = Arrays.stream(numbers).mapToObj(Integer::toString).toArray(String[]::new);
        prefixes = Arrays.stream(numbers).map(a -> a / 100_000).toArray();
    }

    @Benchmark
    public void jdkMethodFull() {
        for (int i = 0; i < SIZE; i++) {
            if (Integer.parseInt(numbersAsString[i]) != numbers[i]) {
                throw new IllegalStateException();
            }
        }
    }

    @Benchmark
    public void jdkMethodPrefix() {
        for (int i = 0; i < SIZE; i++) {
            if (Integer.parseInt(numbersAsString[i].substring(0, DIGITS_COUNT)) != prefixes[i]) {
                throw new IllegalStateException();
            }
        }
    }

    @Benchmark
    public void atsdMethodFull() {
        for (int i = 0; i < SIZE; i++) {
            final String value = numbersAsString[i];
            int len = value.length();
            if (TimeUtils.parseInt(value, 0, len, len) != numbers[i]) {
                throw new IllegalStateException();
            }
        }
    }

    @Benchmark
    public void atsdMethodPrefix() {
        for (int i = 0; i < SIZE; i++) {
            final String value = numbersAsString[i];
            int len = value.length();
            if (TimeUtils.parseInt(value, 0, DIGITS_COUNT, len) != prefixes[i]) {
                throw new IllegalStateException();
            }
        }
    }

    @Benchmark
    public void atsdMethodFullRaipc() {
        for (int i = 0; i < SIZE; i++) {
            final String value = numbersAsString[i];
            int len = value.length();
            if (TimeUtils.parseIntRaipc(value, 0, len, len) != numbers[i]) {
                throw new IllegalStateException();
            }
        }
    }

    @Benchmark
    public void atsdMethodPrefixRaipc() {
        for (int i = 0; i < SIZE; i++) {
            final String value = numbersAsString[i];
            int len = value.length();
            if (TimeUtils.parseIntRaipc(value, 0, DIGITS_COUNT, len) != prefixes[i]) {
                throw new IllegalStateException();
            }
        }
    }

    @Benchmark
    public void atsdMethodFullRaipcCode() {
        for (int i = 0; i < SIZE; i++) {
            final String value = numbersAsString[i];
            int len = value.length();
            if (TimeUtils.parseIntRaipcCode(value, 0, len, len) != numbers[i]) {
                throw new IllegalStateException();
            }
        }
    }

    @Benchmark
    public void atsdMethodPrefixRaipcCode() {
        for (int i = 0; i < SIZE; i++) {
            final String value = numbersAsString[i];
            int len = value.length();
            if (TimeUtils.parseIntRaipcCode(value, 0, DIGITS_COUNT, len) != prefixes[i]) {
                throw new IllegalStateException();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include("ParseIntBenchmark.+")
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(5))
                .measurementIterations(20)
                .forks(3)
                .build();
        new Runner(opt).run();

    }
}
