package com.github.raipc;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class CharIsDigitBenchmark {
    @Param(value = {"+7(955)123-45-67", "79551234567"} )
    private String input;

    @Benchmark
    public String writeDigitsCharacter() {
        final int length = input.length();
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char ch = input.charAt(i);
            if (Character.isDigit(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    @Benchmark
    public String writeDigitsString() {
        final String digits = "0123456789";
        final int length = input.length();
        final StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            String ch = input.substring(i, i+1);
            if (digits.contains(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CharIsDigitBenchmark.class.getSimpleName())
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(5))
                .measurementIterations(20)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
