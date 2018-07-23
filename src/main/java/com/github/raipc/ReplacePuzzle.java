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
public class ReplacePuzzle {

    String string;

    @Setup(Level.Trial)
    public void setup() {
        string = RandomStringUtils.randomAlphanumeric(30);
    }

    @Benchmark
    public String replaceUsingJdkMethod() {
        return string.replace(" ", "_");
    }

    @Benchmark
    public String replaceUsingStringUtils() {
        return StringUtils.replace(string, " ", "_");
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(".*ReplacePuzzle.*")
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
