package com.github.raipc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ParseNumberBenchmark {
    @Param({"42", "1,000,000,000", "-18,2635.1235", "NaN"})
    private String rawNumber;
    @Param({".,", ", "})
    private String separators;
    private char groupingSeparator;
    private char decimalSeparator;
    private String number;
    private DecimalFormat decimalFormat;

    @Setup
    public void setup() {
        decimalSeparator = separators.charAt(0);
        groupingSeparator = separators.charAt(1);
        number = rawNumber.replace(',', groupingSeparator).replace('.', decimalSeparator);
        decimalFormat = new DecimalFormat();
        final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(groupingSeparator);
        symbols.setDecimalSeparator(decimalSeparator);
        decimalFormat.setDecimalFormatSymbols(symbols);
    }

    @Benchmark
    public boolean usingDecimalFormat() {
        return tryParseUsingDecimalFormat(number);
    }

    @Benchmark
    public boolean usingStringReplace() {
        return isNumberParsed(number, decimalSeparator, groupingSeparator);
    }

    private boolean tryParseUsingDecimalFormat(String number) {
        final ParsePosition pos = new ParsePosition(0);
        final Number parsedNumber = decimalFormat.parse(number, pos);
        return pos.getErrorIndex() == -1;
    }

    private static boolean isNumberParsed(String number, char decimalSeparator, char groupingSeparator) {
        if ("NaN".equalsIgnoreCase(number)) {
            return true;
        }
        final String normalizedInteger = StringUtils.remove(number, groupingSeparator);
        final String normalizedNumber = normalizedInteger.replace(decimalSeparator, '.');
        return NumberUtils.isParsable(normalizedNumber);
    }

    public static void main(String[] args) throws RunnerException {
        String[] numbers = {"42", "-182,635.1235", "NaN"};
        final ParseNumberBenchmark parseNumberBenchmark = new ParseNumberBenchmark();
        parseNumberBenchmark.separators = ".,";
        parseNumberBenchmark.setup();
        for (String number : numbers) {
            System.out.println(parseNumberBenchmark.tryParseUsingDecimalFormat(number));
            System.out.println(isNumberParsed(number, '.', ','));
        }
        Options opt = new OptionsBuilder()
                .include(".*" + ParseNumberBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(2)
                .warmupTime(TimeValue.seconds(2))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(3))
                .mode(Mode.Throughput)
                .timeUnit(TimeUnit.MILLISECONDS)
                .build();
        new Runner(opt).run();
    }
}
