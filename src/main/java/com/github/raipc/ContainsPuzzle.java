package com.github.raipc;

import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class ContainsPuzzle {
    String string;

    @Setup
    public void setup() {
        string = RandomStringUtils.randomAscii(80);
    }

    @Benchmark
    public int countDotsInCycle() {
        if (string == null || string.isEmpty()) {
            return 0;
        }
        int count = 0;
        final int length = string.length();
        for (int i = 0; i < length; i++) {
            if ('.' == string.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    @Benchmark
    public int countDotsUsingIndexOf() {
        if (string == null || string.isEmpty()) {
            return 0;
        }
        int count = 0;
        int i = -1;
        while (true){
            i = string.indexOf('.', i + 1);
            if (i == -1) {
                break;
            }
            ++count;
        }
        return count;
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(".*ContainsPuzzle.*")
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
