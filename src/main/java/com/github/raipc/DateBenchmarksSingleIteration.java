package com.github.raipc;

import com.github.raipc.impl.TimeUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 20)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public abstract class DateBenchmarksSingleIteration {
    protected abstract ZoneId getZoneId();
    protected abstract java.time.format.DateTimeFormatter getJsr310Formatter();
    protected abstract DateTimeFormatter getJodaFormatter();
    protected abstract FastDateFormat getApacheFDF();
    protected abstract SimpleDateFormat getSimpleDateFormat();

    long sourceTimestamp;
    String date;
    long parseTimestamp;

    @Setup(value = Level.Trial)
    public void setup() {
        final DateTimeFormatter format = getJodaFormatter();
        sourceTimestamp = RandomUtils.nextLong(0, System.currentTimeMillis());
        date = format.print(sourceTimestamp);
        parseTimestamp = format.parseMillis(date);
    }

    private static SimpleDateFormat prepareSdf(String format, TimeZone timeZone) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat;
    }

    @Benchmark
    public void printJoda() {
        final String result = getJodaFormatter().print(sourceTimestamp);
        if (!result.equals(date)) {
            fail(date, result);
        }
    }

    @Benchmark
    public void printJsr310() {
        final ZonedDateTime temporal = ZonedDateTime.ofInstant(Instant.ofEpochMilli(sourceTimestamp), getZoneId());
        final String result = getJsr310Formatter().format(temporal);
        if (!result.equals(date)) {
            fail(date, result);
        }
    }

    @Benchmark
    public void printSdf() {
        final String result = getSimpleDateFormat().format(sourceTimestamp);
        if (!result.equals(date)) {
            fail(date, result);
        }
        
    }

    @Benchmark
    public void printFdf() {
        final String result = getApacheFDF().format(sourceTimestamp);
        if (!result.equals(date)) {
            fail(date, result);
        }
        
    }

    @Benchmark
    public void parseWithSdf() throws ParseException {
        final long result = getSimpleDateFormat().parse(date).getTime();
        if (result != parseTimestamp) {
            fail(parseTimestamp, result, date);
        }
    }

    @Benchmark
    public void parseWithJoda() {
        final long result = getJodaFormatter().parseMillis(date);
        if (result != parseTimestamp) {
            fail(parseTimestamp, result, date);
        }
    }

    @Benchmark
    public void parseWithJsr310() {
        final long result = ZonedDateTime.parse(date, getJsr310Formatter()).toInstant().toEpochMilli();
        if (result != parseTimestamp) {
            fail(parseTimestamp, result, date);
        }
    }

    @Benchmark
    public void parseWithJsr310Offset() {
        final long result = OffsetDateTime.parse(date, getJsr310Formatter()).toInstant().toEpochMilli();
        if (result != parseTimestamp) {
            fail(parseTimestamp, result, date);
        }
    }

    @Benchmark
    public void parseWithFdf() throws ParseException {
        final long result = getApacheFDF().parse(date).getTime();
        if (result != parseTimestamp) {
            fail(parseTimestamp, result, date);
        }
    }


    private static void fail(String expected, String result) {
        throw new IllegalStateException("Expected: " + expected + " but was " + result);
    }

    private static void fail(long expected, long result, String input) {
        throw new IllegalStateException("Expected: " + expected + " but was " + result + " for input " + input);
    }

    public abstract static class NewInstancePrinterBenchmark extends DateBenchmarksSingleIteration {
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

    public abstract static class CachedPrinterBenchmark extends DateBenchmarksSingleIteration {
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
                    () -> FastDateFormat.getInstance(PATTERN, TimeZone.getTimeZone("UTC"), Locale.US),
                    () -> prepareSdf(PATTERN, TimeZone.getTimeZone("UTC")));
        }

        @Benchmark
        public void parseInstantWithJsr310() {
            
                final long result = Instant.parse(date).toEpochMilli();
                if (result != parseTimestamp) {
                    fail(parseTimestamp, result, date);
                }
        }

        @Benchmark
        public void printWithCustomPrinterIso() {
            final String result = TimeUtils.printIso8601(sourceTimestamp, true, false);
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
        public void parseWithCustomParserIso() {
            final long result = TimeUtils.parseISO8601(date);
            if (result != parseTimestamp) {
                fail(parseTimestamp, result, date);
            }
        }

        @Benchmark
        public void parseWithCustomParserJava8() {
            final long result = TimeUtils.parseISO8601Java8(date);
            if (result != parseTimestamp) {
                fail(parseTimestamp, result, date);
            }
        }

        @Benchmark
        public void parseWithCustomParserJava8Opt() {
            final long result = TimeUtils.parseISO8601NanosOpt(date);
            if (result != parseTimestamp) {
                fail(parseTimestamp, result, date);
            }
        }

        @Benchmark
        public void parseWithCustomParserCached() {
            final long result = TimeUtils.parseISO8601CachedCalendar(date);
            if (result != parseTimestamp) {
                fail(parseTimestamp, result, date);
            }
        }
    }

    public static class DatePrintParseBenchmarkLocal extends DateBenchmarksSingleIteration.CachedPrinterBenchmark {
        private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
        public DatePrintParseBenchmarkLocal() {
            super(() -> java.time.format.DateTimeFormatter.ofPattern(PATTERN, Locale.US).withZone(ZoneId.systemDefault()),
                    () -> DateTimeFormat.forPattern(PATTERN).withZone(DateTimeZone.getDefault()).withLocale(Locale.US),
                    () -> FastDateFormat.getInstance(PATTERN, TimeZone.getDefault(), Locale.US),
                    () -> prepareSdf(PATTERN, TimeZone.getDefault()));
        }

        @Benchmark
        public void printWithCustomPrinterLocal() {
            final String result = TimeUtils.printIso8601OptLocal(sourceTimestamp, false);
            if (!result.equals(date)) {
                fail(date, result);
            }
        }
    }

    public static class DatePrintParseBenchmarkCustomPattern extends DateBenchmarksSingleIteration.NewInstancePrinterBenchmark {
        private static final String PATTERN = "yyyyMMdd'T'HHmmss'Z'";

        private static final Map<String, java.time.format.DateTimeFormatter> cacheJavaTime = new ConcurrentHashMap<>();

        public DatePrintParseBenchmarkCustomPattern() {
            super(ZoneId.systemDefault(),
                    () -> cacheJavaTime.computeIfAbsent(PATTERN, key -> java.time.format.DateTimeFormatter.ofPattern(key, Locale.US))
                            .withZone(ZoneId.systemDefault()),
                    () -> DateTimeFormat.forPattern(PATTERN).withLocale(Locale.US),
                    () -> FastDateFormat.getInstance(PATTERN, TimeZone.getDefault(), Locale.US),
                    () -> prepareSdf(PATTERN, TimeZone.getDefault()));
        }
    }

    public static class DatePrintParseBenchmarkCustomPatternMillisFromDateGuavaCache extends DateBenchmarksSingleIteration.NewInstancePrinterBenchmark {
        private static final String PATTERN = "yyyy-MM-dd";

        private static final java.time.format.DateTimeFormatter simple = java.time.format.DateTimeFormatter.ofPattern(PATTERN, Locale.US);
        private static final java.time.format.DateTimeFormatter cacheJavaTime = new DateTimeFormatterBuilder()
                .append(simple)
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter(Locale.US).withZone(ZoneId.systemDefault());

        public DatePrintParseBenchmarkCustomPatternMillisFromDateGuavaCache() {
            super(ZoneId.systemDefault(),
                    () -> cacheJavaTime,
                    () -> DateTimeFormat.forPattern(PATTERN).withLocale(Locale.US),
                    () -> FastDateFormat.getInstance(PATTERN, TimeZone.getDefault(), Locale.US),
                    () -> prepareSdf(PATTERN, TimeZone.getDefault()));
        }

        private static LocalDate resolveDateFromTemporal(TemporalAccessor parsed) {
            final LocalDate query = parsed.query(TemporalQueries.localDate());
            if (query != null) {
                return query;
            }
            int year = 1970;
            int month = 1;
            int day = 1;
            if (parsed.isSupported(ChronoField.YEAR)) {
                year = parsed.get(ChronoField.YEAR);
            }
            if (parsed.isSupported(ChronoField.MONTH_OF_YEAR)) {
                month = parsed.get(ChronoField.MONTH_OF_YEAR);
            } else if (parsed.isSupported(IsoFields.QUARTER_OF_YEAR)) {
                int quarter = parsed.get(IsoFields.QUARTER_OF_YEAR);
                month = quarter * 3 - 2;
            }
            if (parsed.isSupported(ChronoField.DAY_OF_MONTH)) {
                day = parsed.get(ChronoField.DAY_OF_MONTH);
            }
            return LocalDate.of(year, month, day);
        }

        private static LocalTime resolveTimeFromTemporal(TemporalAccessor parsed) {
            final LocalTime query = parsed.query(TemporalQueries.localTime());
            if (query != null) {
                return query;
            }
            int hour = 0;
            int minute = 0;
            int second = 0;
            int nanos = 0;
            if (parsed.isSupported(ChronoField.HOUR_OF_DAY)) {
                hour = parsed.get(ChronoField.HOUR_OF_DAY);
            }
            if (parsed.isSupported(ChronoField.MINUTE_OF_HOUR)) {
                minute = parsed.get(ChronoField.MINUTE_OF_HOUR);
            }
            if (parsed.isSupported(ChronoField.SECOND_OF_MINUTE)) {
                second = parsed.get(ChronoField.SECOND_OF_MINUTE);
            }
            if (parsed.isSupported(ChronoField.NANO_OF_SECOND)) {
                nanos = parsed.get(ChronoField.NANO_OF_SECOND);
            }
            return LocalTime.of(hour, minute, second, nanos);

        }

        private static long manualParse(String value) {
            final TemporalAccessor parsed = simple.parse(value);
            final LocalDate localDate = resolveDateFromTemporal(parsed);
            final LocalTime localTime = resolveTimeFromTemporal(parsed);
            ZoneId zoneId = parsed.query(TemporalQueries.zone());
            if (zoneId == null) {
                zoneId = ZoneId.systemDefault();
            }
            return ZonedDateTime.of(localDate, localTime, zoneId).toInstant().toEpochMilli();

        }

        @Benchmark
        public void withManualParse() {
            final long result = manualParse(date);
            if (result != parseTimestamp) {
                fail(parseTimestamp, result, date);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .jvmArgs("-server", "-Xmx1024M")
//                .include(".+parseWithCustomParserJava8Nanos.*")
//                .include(".+parseWithCustomParser.*")
                .include(".*DateBenchmarksSingleIteration.*DatePrintParseBenchmarkISO8601UTC.*")
                .shouldDoGC(true)
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(5))
                .measurementTime(new TimeValue(3, TimeUnit.SECONDS))
                .mode(Mode.SampleTime)
                .measurementIterations(5)
                .addProfiler( StackProfiler.class )
                .addProfiler( GCProfiler.class )
                .forks(1)
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();

    }
}
