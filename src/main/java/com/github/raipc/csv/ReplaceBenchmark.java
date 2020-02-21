package com.github.raipc.csv;

import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ReplaceBenchmark {
    @Param({"1243412", "5674.1e9", "5674.1E-9", "5674.1E+9"})
    private String value;
    private final Matcher matcher = Pattern.compile("[eE][+]?").matcher("");

    @Benchmark
    public String replaceIgnoreCase() {
        return StringUtils.replace(StringUtils.replaceIgnoreCase(value, "E+", "E"), "e", "E");
    }

    @Benchmark
    public String replace() {
        return StringUtils.replace(StringUtils.replace(value, "e", "E"), "E+", "E");
    }

    @Benchmark
    public String regex() {
        return matcher.reset(value).replaceAll("E");
    }

    public static void main(String[] args) throws RunnerException {
        String[] values = new String[] {"1243412", "5674.1e9", "5674.1E-9", "5674.1E+9"};
        final ReplaceBenchmark replaceBenchmark = new ReplaceBenchmark();
        for (String value : values) {
            replaceBenchmark.value = value;
            System.out.println("---------");
            System.out.println(value);
            System.out.println(replaceBenchmark.replaceIgnoreCase());
            System.out.println(replaceBenchmark.replace());
            System.out.println(replaceBenchmark.regex());
        }

        Options opt = new OptionsBuilder()
                .include(".*ReplaceBenchmark.*.*")
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
