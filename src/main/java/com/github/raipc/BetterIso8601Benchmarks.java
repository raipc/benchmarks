package com.github.raipc;

import com.github.raipc.impl.TimeUtils;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.CompilerProfiler;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 20)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class BetterIso8601Benchmarks {
    private static final DateTimeFormatter ISO_FORMATTER_JODA = ISODateTimeFormat.dateTime().withZoneUTC();

    long sourceTimestamp;
    String date;

    @Setup(value = Level.Trial)
    public void setup() {
        sourceTimestamp = RandomUtils.nextLong(0, System.currentTimeMillis());
        date = ISO_FORMATTER_JODA.print(sourceTimestamp);
    }

    @Benchmark
    public void printJodaIso8601() {
        final String result = ISO_FORMATTER_JODA.print(sourceTimestamp);
        if (!result.equals(date)) {
            fail(date, result);
        }
    }

    @Benchmark
    public void printWithCustomPrinterIsoOpt() {
        final String result = TimeUtils.printIso8601OptUtc(sourceTimestamp, true);
        if (!result.equals(date)) {
            fail(date, result);
        }
    }

    @Benchmark
    public void parseJoda() {
        final long result = ISODateTimeFormat.dateTime().parseMillis(date);
        if (result != sourceTimestamp) {
            fail(sourceTimestamp, result, date);
        }
    }

    @Benchmark
    public void parseCustomCurrentATSD() {
        final long result = TimeUtils.parseISO8601(date);
        if (result != sourceTimestamp) {
            fail(sourceTimestamp, result, date);
        }
    }

    @Benchmark
    public void parseOptimized() {
        final long result = TimeUtils.parseISO8601NanosOpt(date);
        if (result != sourceTimestamp) {
            fail(sourceTimestamp, result, date);
        }
    }

    private static void fail(String expected, String result) {
        throw new IllegalStateException("Expected: " + expected + " but was " + result);
    }

    private static void fail(long expected, long result, String input) {
        throw new IllegalStateException("Expected: " + expected + " but was " + result + " for input " + input);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include("BetterIso8601Benchmarks.*")
                .jvmArgs("-server", "-Xmx1024M")
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(3))
                .measurementIterations(20)
                .shouldDoGC(true)
                .addProfiler( StackProfiler.class )
                .addProfiler( GCProfiler.class )
                .addProfiler( CompilerProfiler.class )
                .resultFormat(ResultFormatType.JSON)
                .forks(1)
                .build();

        new Runner(opt).run();

    }

}
