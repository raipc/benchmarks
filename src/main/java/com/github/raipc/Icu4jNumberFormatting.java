package com.github.raipc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.ibm.icu.number.LocalizedNumberFormatter;
import com.ibm.icu.number.NumberFormatter;
import com.ibm.icu.number.Precision;
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

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class Icu4jNumberFormatting {
    private static final DecimalFormat DECIMAL_FORMAT_JDK = createDecimalFormatJdk();
    private static final LocalizedNumberFormatter NUMBER_FORMATTER_ICU4J = createFormatterIcu4j();
    private static final com.ibm.icu.text.DecimalFormat DECIMAL_FORMAT_ICU4J = createDecimalFormatIcu4j();

    @Param({"0", "NaN", "123.6", "-6576235.2374526734"})
    private double number;

    private static DecimalFormat createDecimalFormatJdk() {
        final DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setNaN("NaN");
        return new DecimalFormat("#.##", decimalFormatSymbols);
    }

    private static com.ibm.icu.text.DecimalFormat createDecimalFormatIcu4j() {
        final com.ibm.icu.text.DecimalFormatSymbols decimalFormatSymbols =  com.ibm.icu.text.DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setNaN("NaN");
        return new com.ibm.icu.text.DecimalFormat("#.##", decimalFormatSymbols);
    }

    private static LocalizedNumberFormatter createFormatterIcu4j() {
        final com.ibm.icu.text.DecimalFormatSymbols decimalFormatSymbols = com.ibm.icu.text.DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setNaN("NaN");
        return NumberFormatter.withLocale(Locale.US)
                .precision(Precision.maxFraction(2))
                .grouping(NumberFormatter.GroupingStrategy.OFF)
                .symbols(decimalFormatSymbols);
    }

    @Benchmark
    public String formatStaticJdk() {
        return DECIMAL_FORMAT_JDK.format(number);
    }

    @Benchmark
    public String formatStaticIcu4j() {
        return NUMBER_FORMATTER_ICU4J.format(number).toString();
    }

    @Benchmark
    public String formatDecimalStaticIcu4j() {
        return DECIMAL_FORMAT_ICU4J.format(number).toString();
    }

    @Benchmark
    public String formatInstanceJdk() {
        return createDecimalFormatJdk().format(number);
    }

    @Benchmark
    public String formatInstanceIcu4j() {
        return createFormatterIcu4j().format(number).toString();
    }

    @Benchmark
    public String formatDecimalInstanceIcu4j() {
        return createDecimalFormatIcu4j().format(number);
    }

    public static void main(String[] args) throws RunnerException {
        double[] values = new double[] {0.0, Double.NaN, Double.NEGATIVE_INFINITY, 1653.27536, 123.1, -23754625346275.2384627546253};
        for (double value : values) {
            System.out.println("JDK: " + DECIMAL_FORMAT_JDK.format(value));
            System.out.println("ICU: " + NUMBER_FORMATTER_ICU4J.format(value));
        }

        Options opt = new OptionsBuilder()
                .include(".*Icu4jNumberFormatting.*.*")
                .warmupIterations(2)
                .warmupTime(TimeValue.seconds(3))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(4))
                .mode(Mode.AverageTime)
                .threads(16)
                .timeUnit(TimeUnit.NANOSECONDS)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
