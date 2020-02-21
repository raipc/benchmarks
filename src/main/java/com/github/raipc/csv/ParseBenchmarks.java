package com.github.raipc.csv;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ParseBenchmarks {
    @Param({"fast", "jdk", "icu"})
    private String type;
    @Param({"42", "1,000,000,000", "-18,2635.1235", "123.1E9", "123.1E+9", "123.1e+9"})
    private String rawNumber;
    @Param({".,", ", "})
    private String separators;
    private String number;
    private NumberParser numberParser;

    @Setup
    public void setup() {
        char decimalSeparator = separators.charAt(0);
        char groupingSeparator = separators.charAt(1);
        number = rawNumber.replace(',', groupingSeparator).replace('.', decimalSeparator);
        switch (type) {
            case "fast": numberParser = new FastNumberParser(decimalSeparator, groupingSeparator); break;
            case "jdk": numberParser = new FormatBasedNumberParser("#,#.#", decimalSeparator, groupingSeparator); break;
            case "icu": numberParser = new IcuNumberParser("#,#.#", decimalSeparator, groupingSeparator); break;
        }
    }

    @Benchmark
    public boolean canParseFormat() {
        return numberParser.canParse(number);
    }

    @Benchmark
    public BigDecimal parse() {
        return numberParser.parse(number);
    }

    public static void main(String[] args) throws RunnerException {
        final ParseBenchmarks parseBenchmarks = new ParseBenchmarks();

//        for (String n : new String[]{"asd","42", "1,000,000,000", "-18,2635.1235"}) {
//            parseBenchmarks.rawNumber = n;
//            for (String type : new String[]{"jdk", "icu"}) {
//                parseBenchmarks.type = type;
//                parseBenchmarks.separators = ".,";
//                parseBenchmarks.setup();
//
//                System.out.println(parseBenchmarks.parse());
//                final boolean canParse = parseBenchmarks.canParseFormat();
//
//                System.out.println(n +"," + type + "," + canParse + "," + (canParse ? parseBenchmarks.parse() : ""));
//            }
//        }

        Options opt = new OptionsBuilder()
                .include(".*ParseBenchmarks.*.*")
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
