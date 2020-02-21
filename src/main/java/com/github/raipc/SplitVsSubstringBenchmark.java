package com.github.raipc;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SplitVsSubstringBenchmark {
    @Param({"a a", 
            "b sfdgasfdfgafsdghafsdghfasghdfaghsfdhgasfdghasfdhgafsdghasfdgahsdgfahsdgafhsdgfahsdgfashdgfashgdfashgdf",
            "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc c",
            "dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd " +
                    "sfdgasfdfgafsdghafsdghfasghdfaghsfdhgasfdghasfdhgafsdghasfdgahsdgfahsdgafhsdgfahsdgfashdgfashgdfashgdf",
    
    })
    private String value;
    
    @Benchmark
    public String splitStringUtils() {
        return StringUtils.split(value, " ", 2)[1];
    }

    @Benchmark
    public String splitStringJava() {
        return value.split(" ", 2)[1];
    }

    @Benchmark
    public String substringAfter() {
        return StringUtils.substringAfter(value, " ");
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*SplitVsSubstringBenchmark.*.*")
                .warmupIterations(2)
                .warmupTime(TimeValue.seconds(3))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(4))
                .mode(Mode.AverageTime)
                .threads(1)
                .timeUnit(TimeUnit.NANOSECONDS)
                .forks(2)
                .build();

        new Runner(opt).run();
    }
}
