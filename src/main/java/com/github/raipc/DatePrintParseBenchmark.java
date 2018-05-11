package com.github.raipc;

import com.github.raipc.impl.TimeUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 20)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public abstract class DatePrintParseBenchmark {
    private static final int SIZE = 10000;

    protected abstract ZoneId getZoneId();
    protected abstract java.time.format.DateTimeFormatter getJsr310Formatter();
    protected abstract DateTimeFormatter getJodaFormatter();
    protected abstract FastDateFormat getApacheFDF();
    protected abstract SimpleDateFormat getSimpleDateFormat();

    long[] sourceTimestamps;
    String[] dates;
    long[] parseTimestamps;

    @Setup
    public void setup() {
        sourceTimestamps = new Random().longs(SIZE, 0L, System.currentTimeMillis()).toArray();
        final DateTimeFormatter format = getJodaFormatter();
        dates = Arrays.stream(sourceTimestamps)
                .boxed()
                .map(format::print)
                .toArray(String[]::new);
        parseTimestamps = Arrays.stream(dates).mapToLong(format::parseMillis).toArray();
    }

    private static SimpleDateFormat prepareSdf(String format, TimeZone timeZone) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat;
    }

    @Benchmark
    public void printJoda() {
        for (int i = 0; i < SIZE; i++) {
            final String result = getJodaFormatter().print(sourceTimestamps[i]);
            if (!result.equals(dates[i])) {
                fail(dates[i], result);
            }
        }
    }

    @Benchmark
    public void printJsr310() {
        for (int i = 0; i < SIZE; i++) {
            final ZonedDateTime temporal = ZonedDateTime.ofInstant(Instant.ofEpochMilli(sourceTimestamps[i]), getZoneId());
            final String result = getJsr310Formatter().format(temporal);
            if (!result.equals(dates[i])) {
                fail(dates[i], result);
            }
        }
    }

    @Benchmark
    public void printSdf() {
        for (int i = 0; i < SIZE; i++) {
            final String result = getSimpleDateFormat().format(sourceTimestamps[i]);
            if (!result.equals(dates[i])) {
                fail(dates[i], result);
            }
        }
    }

    @Benchmark
    public void printFdf() {
        for (int i = 0; i < SIZE; i++) {
            final String result = getApacheFDF().format(sourceTimestamps[i]);
            if (!result.equals(dates[i])) {
                fail(dates[i], result);
            }
        }
    }

    @Benchmark
    public void parseWithSdf() throws ParseException {
        for (int i = 0; i < SIZE; i++) {
            final long result = getSimpleDateFormat().parse(dates[i]).getTime();
            if (result != parseTimestamps[i]) {
                fail(parseTimestamps[i], result, dates[i]);
            }
        }
    }

    @Benchmark
    public void parseWithJoda() {
        for (int i = 0; i < SIZE; i++) {
            final long result = getJodaFormatter().parseMillis(dates[i]);
            if (result != parseTimestamps[i]) {
                fail(parseTimestamps[i], result, dates[i]);
            }
        }
    }

    @Benchmark
    public void parseWithJsr310() {
        for (int i = 0; i < SIZE; i++) {
            final long result = ZonedDateTime.parse(dates[i], getJsr310Formatter()).toInstant().toEpochMilli();
            if (result != parseTimestamps[i]) {
                fail(parseTimestamps[i], result, dates[i]);
            }
        }
    }

    @Benchmark
    public void parseWithFdf() throws ParseException {
        for (int i = 0; i < SIZE; i++) {
            final long result = getApacheFDF().parse(dates[i]).getTime();
            if (result != parseTimestamps[i]) {
                fail(parseTimestamps[i], result, dates[i]);
            }
        }
    }


    private static void fail(String expected, String result) {
        throw new IllegalStateException("Expected: " + expected + " but was " + result);
    }

    private static void fail(long expected, long result, String input) {
        throw new IllegalStateException("Expected: " + expected + " but was " + result + " for input " + input);
    }

    public abstract static class NewInstancePrinterBenchmark extends DatePrintParseBenchmark {
        private final ZoneId zoneId;
        private final Supplier<java.time.format.DateTimeFormatter> jsr310Formatter;
        private final Supplier<DateTimeFormatter> jodaFormatter;

        private final Supplier<FastDateFormat> apacheFDF;
        private final Supplier<SimpleDateFormat> simpleDateFormat;

        protected NewInstancePrinterBenchmark(ZoneId zoneId,
                                              Supplier<java.time.format.DateTimeFormatter> jsr310Formatter,
                                              Supplier<DateTimeFormatter> jodaFormatter,
                                              Supplier<FastDateFormat> apacheFDF,
                                              Supplier<SimpleDateFormat> simpleDateFormat) {
            this.zoneId = zoneId;
            this.jsr310Formatter = jsr310Formatter;
            this.jodaFormatter = jodaFormatter;
            this.apacheFDF = apacheFDF;
            this.simpleDateFormat = simpleDateFormat;
        }

        @Override
        protected ZoneId getZoneId() {
            return zoneId;
        }

        @Override
        protected java.time.format.DateTimeFormatter getJsr310Formatter() {
            return jsr310Formatter.get();
        }

        @Override
        protected DateTimeFormatter getJodaFormatter() {
            return jodaFormatter.get();
        }

        @Override
        protected FastDateFormat getApacheFDF() {
            return apacheFDF.get();
        }

        @Override
        protected SimpleDateFormat getSimpleDateFormat() {
            return simpleDateFormat.get();
        }
    }

    public abstract static class CachedPrinterBenchmark extends DatePrintParseBenchmark {
        private final ZoneId zoneId;
        private final java.time.format.DateTimeFormatter jsr310Formatter;
        private final DateTimeFormatter jodaFormatter;

        private final FastDateFormat apacheFDF;
        private final ThreadLocal<SimpleDateFormat> simpleDateFormatTL;

        public CachedPrinterBenchmark(Supplier<java.time.format.DateTimeFormatter> jsr310Supplier,
                                      Supplier<DateTimeFormatter> jodaFormatterSupplier,
                                      Supplier<FastDateFormat> fdfSupplier,
                                      Supplier<SimpleDateFormat> simpleDateFormatSupplier) {
            super();
            this.jsr310Formatter = jsr310Supplier.get();
            this.jodaFormatter = jodaFormatterSupplier.get();
            this.apacheFDF = fdfSupplier.get();
            this.simpleDateFormatTL = new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return simpleDateFormatSupplier.get();
                }
            };
            this.zoneId = this.simpleDateFormatTL.get().getTimeZone().toZoneId();
        }

        @Override
        protected ZoneId getZoneId() {
            return zoneId;
        }

        @Override
        protected java.time.format.DateTimeFormatter getJsr310Formatter() {
            return jsr310Formatter;
        }

        @Override
        protected DateTimeFormatter getJodaFormatter() {
            return jodaFormatter;
        }

        @Override
        protected FastDateFormat getApacheFDF() {
            return apacheFDF;
        }

        @Override
        protected SimpleDateFormat getSimpleDateFormat() {
            return simpleDateFormatTL.get();
        }
    }

    public static class DatePrintParseBenchmarkISO8601UTC extends CachedPrinterBenchmark {
        private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        public DatePrintParseBenchmarkISO8601UTC() {
            super(() -> java.time.format.DateTimeFormatter.ofPattern(PATTERN, Locale.US).withZone(ZoneId.of("UTC")),
                  () -> ISODateTimeFormat.dateTime().withLocale(Locale.US).withZoneUTC(),
                  () ->  FastDateFormat.getInstance(PATTERN, TimeZone.getTimeZone("UTC"), Locale.US),
                  () ->  prepareSdf(PATTERN, TimeZone.getTimeZone("UTC")));
        }

        @Benchmark
        public void parseInstantWithJsr310() {
            for (int i = 0; i < SIZE; i++) {
                final long result = Instant.parse(dates[i]).toEpochMilli();
                if (result != parseTimestamps[i]) {
                    fail(parseTimestamps[i], result, dates[i]);
                }
            }
        }

        @Benchmark
        public void printWithCustomPrinterIso() {
            for (int i = 0; i < SIZE; i++) {
                final String result = TimeUtils.printIso8601UtcMillis(sourceTimestamps[i]);
                if (!result.equals(dates[i])) {
                    fail(dates[i], result);
                }
            }
        }

        @Benchmark
        public void parseWithCustomParserIso() {
            for (int i = 0; i < SIZE; i++) {
                final long result = TimeUtils.parseISO8601(dates[i]);
                if (result != parseTimestamps[i]) {
                    fail(parseTimestamps[i], result, dates[i]);
                }
            }
        }

        @Benchmark
        public void parseWithCustomParserJava8() {
            for (int i = 0; i < SIZE; i++) {
                final long result = TimeUtils.parseISO8601Java8(dates[i]);
                if (result != parseTimestamps[i]) {
                    fail(parseTimestamps[i], result, dates[i]);
                }
            }
        }

        @Benchmark
        public void parseWithCustomParserCached() {
            for (int i = 0; i < SIZE; i++) {
                final long result = TimeUtils.parseISO8601CachedCalendar(dates[i]);
                if (result != parseTimestamps[i]) {
                    fail(parseTimestamps[i], result, dates[i]);
                }
            }
        }
    }

    public static class DatePrintParseBenchmarkLocal extends CachedPrinterBenchmark {
        private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
        public DatePrintParseBenchmarkLocal() {
            super(() -> java.time.format.DateTimeFormatter.ofPattern(PATTERN, Locale.US).withZone(ZoneId.systemDefault()),
                  () -> DateTimeFormat.forPattern(PATTERN).withZone(DateTimeZone.getDefault()).withLocale(Locale.US),
                  () -> FastDateFormat.getInstance(PATTERN, TimeZone.getDefault(), Locale.US),
                  () -> prepareSdf(PATTERN, TimeZone.getDefault()));
        }

        @Benchmark
        public void printWithCustomPrinterLocal() {
            for (int i = 0; i < SIZE; i++) {
                final String result = TimeUtils.printIso8601LocalSeconds(sourceTimestamps[i]);
                if (!result.equals(dates[i])) {
                    fail(dates[i], result);
                }
            }
        }
    }

    public static class DatePrintParseBenchmarkCustomPattern extends NewInstancePrinterBenchmark {
        private static final String PATTERN = "dd/MM/yyyy (HH:mm:ss)";
        public DatePrintParseBenchmarkCustomPattern() {
            super(ZoneId.systemDefault(),
                    () -> java.time.format.DateTimeFormatter.ofPattern(PATTERN, Locale.US).withZone(ZoneId.systemDefault()),
                    () -> DateTimeFormat.forPattern(PATTERN).withLocale(Locale.US),
                    () -> FastDateFormat.getInstance(PATTERN, TimeZone.getDefault(), Locale.US),
                    () -> prepareSdf(PATTERN, TimeZone.getDefault()));
        }
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include("DatePrintParseBenchmark.+")
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(5))
                .measurementIterations(20)
                .forks(3)
                .build();

        new Runner(opt).run();

    }
}
