package com.github.raipc.graalvm;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class NearestNumber {
    @Param({"", "7 -10 13 8 4 -7.2 -12 -3.7 3.5 -9.6 6.5 -1.7 -6.2 7"})
    private String numbers;

    @Benchmark
    public String findNearestNumber() {
        return NumberFormat.getInstance().format(
                Arrays.stream(numbers.split(" "))
                        .filter(str -> !str.isEmpty())
                        .map(Double::valueOf)
                        .min((a, b) -> Double.compare(Math.abs(a), Math.abs(b)) == 0 ?
                                Double.compare(b, a) :
                                Double.compare(Math.abs(a), Math.abs(b)))
                        .orElse(0.0));
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + NearestNumber.class.getSimpleName() + ".*")
                .warmupIterations(2)
                .warmupTime(TimeValue.seconds(2))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(3))
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .addProfiler(GCProfiler.class)
                .build();
        new Runner(opt).run();
    }
}
