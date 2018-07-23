package com.github.raipc;

import com.google.common.math.IntMath;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class CharBenchmarks {
    private List<DigitCount> list;

    @Setup(value = Level.Iteration)
    public void setup() {
        value = 10;
        pow = RandomUtils.nextInt(1, 10);
        list = IntStream.range(0, 100_000)
                .mapToObj(i -> RandomStringUtils.randomAlphanumeric(20))
                .map(DigitCount::new)
                .collect(Collectors.toList());
    }

    @Benchmark
    public void usingCharComparision() {
        for (DigitCount digitCount : list) {
            final int length = digitCount.pattern.length();
            int digits = 0;
            for (int i = 0; i < length; i++) {
                final char c = digitCount.pattern.charAt(i);
                if (c >= '0' && c <= '9') {
                    ++digits;
                }
            }
            if (digitCount.digitCount != digits) {
                throw new RuntimeException("Not matched. Expected: " + digitCount.digitCount + " . Received: " + digits);
            }
        }
    }

    @Benchmark
    public void usingCharIsDigit() {
        for (DigitCount digitCount : list) {
            final int length = digitCount.pattern.length();
            int digits = 0;
            for (int i = 0; i < length; i++) {
                if (Character.isDigit(digitCount.pattern.charAt(i))) {
                    ++digits;
                }
            }
            if (digitCount.digitCount != digits) {
                throw new RuntimeException("Not matched. Expected: " + digitCount.digitCount + " . Received: " + digits);
            }
        }
    }

    @Benchmark
    public void usingSwitch() {
        for (DigitCount digitCount : list) {
            final int length = digitCount.pattern.length();
            int digits = 0;
            for (int i = 0; i < length; i++) {
                switch (digitCount.pattern.charAt(i)) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        ++digits;
                }
            }
            if (digitCount.digitCount != digits) {
                throw new RuntimeException("Not matched. Expected: " + digitCount.digitCount + " . Received: " + digits);
            }
        }
    }

    private static final class DigitCount {
        private final String pattern;
        private int digitCount;

        private DigitCount(String pattern) {
            this.pattern = pattern;
            this.digitCount = 0;
            for (int i = 0; i < pattern.length(); i++) {
                if (Character.isDigit(pattern.charAt(i))) {
                    ++digitCount;
                }
            }
        }
    }

    int value;
    int pow;

    @Benchmark
    public int powJdk() {
        return (int) Math.pow(value, pow);
    }

    @Benchmark
    public int powGuava() {
        return IntMath.pow(value, pow);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include("CharBenchmarks.*pow.*")
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(5))
                .measurementIterations(5)
                .resultFormat(ResultFormatType.JSON)
                .forks(1)
                .build();

        new Runner(opt).run();

    }
}
