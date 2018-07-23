package com.github.raipc;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class StringConcatPuzzle {
    private String first;
    private String second;
    private String third;

    @Setup(Level.Trial)
    public void setup() {
        first = RandomStringUtils.randomAlphanumeric(10);
        second = RandomStringUtils.randomAlphanumeric(10);
        third = RandomStringUtils.randomAlphanumeric(10);
    }

    private String getSecondReversed() {
        return StringUtils.reverse(second);
    }

    @Benchmark
    public String concatOperator() {
        return first + second + third;
    }

    @Benchmark
    public String concatOperatorWithMethodCall() {
        return first + getSecondReversed() + third;
    }

    @Benchmark
    public String concatOperatorWithMethodCallLocalVar() {
        final String secondReversed = getSecondReversed();
        return first + secondReversed + third;
    }

    @Benchmark
    public String concatUsingStringBuilder() {
        final StringBuilder sb = new StringBuilder();
        sb.append(first);
        sb.append(second);
        sb.append(third);
        return sb.toString();
    }

    @Benchmark
    public String concatUsingStringBuilderChained() {
        return new StringBuilder()
                .append(first)
                .append(second)
                .append(third)
                .toString();
    }

    @Benchmark
    public String concatUsingStringBuilderChainedMethodCall() {
        return new StringBuilder()
                .append(first)
                .append(getSecondReversed())
                .append(third)
                .toString();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .jvmArgs("-XX:-OptimizeStringConcat")
                .include(".*StringConcatPuzzle.*")
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(5))
                .measurementTime(new TimeValue(3, TimeUnit.SECONDS))
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();

    }
}
