package com.github.raipc;

import org.apache.commons.lang3.time.FastDateFormat;
import org.openjdk.jmh.annotations.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class DateParseBenchmark {
    private static final String GMT_ID = "GMT";
    private static final ThreadLocal<Calendar> CALENDAR_CACHE = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(GMT_ID));
            calendar.setLenient(false);
            return calendar;
        }
    };

    private static final ThreadLocal<SimpleDateFormat> SDFTL = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return prepareSdf();
        }
    };

    private static final SimpleDateFormat SDF = prepareSdf();

    private static final FastDateFormat FDF = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("UTC"), Locale.US);

    private static SimpleDateFormat prepareSdf() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }

    long[] timestamps;
    List<String> dates;

    @Setup
    public void setup() {
        timestamps = new Random().longs(1000, 0L, System.currentTimeMillis()).toArray();
        dates = Arrays.stream(timestamps)
                .boxed()
                .map(ts -> FDF.format(ts))
                .collect(Collectors.toList());
    }

    @Benchmark
    public void parseWithSdfThreadLocal() throws ParseException {
        int i = 0;
        for (String date : dates) {
            if (SDFTL.get().parse(date).getTime() != timestamps[i++]) {
                throw new RuntimeException("Wrong parse");
            }
        }
    }

    @Benchmark
    public void parseWithSdf() throws ParseException {
        int i = 0;
        for (String date : dates) {
            if (SDF.parse(date).getTime() != timestamps[i++]) {
                throw new RuntimeException("Wrong parse");
            }
        }
    }

    @Benchmark
    public void parseWithFdf() throws ParseException {
        int i = 0;
        for (String date : dates) {
            if (FDF.parse(date).getTime() != timestamps[i++]) {
                throw new RuntimeException("Wrong parse");
            }
        }
    }

    @Benchmark
    public void parseWithCustomParser() throws ParseException {
        int i = 0;
        for (String date : dates) {
            if (parseISO8601(date) != timestamps[i++]) {
                throw new RuntimeException("Wrong parse");
            }
        }
    }

    @Benchmark
    public void parseWithCustomParserCached() throws ParseException {
        int i = 0;
        for (String date : dates) {
            if (parseISO8601CachedCalendar(date) != timestamps[i++]) {
                throw new RuntimeException("Wrong parse");
            }
        }
    }


    public static long parseISO8601(String date) {
        try {
            int offset = 0;

            // extract year
            int year = parseInt(date, offset, offset += 4);
            checkOffset(date, offset, '-');

            // extract month
            int month = parseInt(date, offset += 1, offset += 2);
            checkOffset(date, offset, '-');

            // extract day
            int day = parseInt(date, offset += 1, offset += 2);
            checkOffset(date, offset, 'T');

            // extract hours, minutes, seconds and milliseconds
            int hour = parseInt(date, offset += 1, offset += 2);
            checkOffset(date, offset, ':');

            int minutes = parseInt(date, offset += 1, offset += 2);
            checkOffset(date, offset, ':');

            int seconds = parseInt(date, offset += 1, offset += 2);
            // milliseconds can be optional in the format
            int milliseconds = 0; // always use 0 otherwise returned date will include millis of current time
            if (date.charAt(offset) == '.') {
                checkOffset(date, offset, '.');
                milliseconds = parseInt(date, offset += 1, offset += 3);
            }

            // extract timezone
            String timezoneId;
            char timezoneIndicator = date.charAt(offset);
            if (timezoneIndicator == '+' || timezoneIndicator == '-') {
                timezoneId = GMT_ID + date.substring(offset);
            } else if (timezoneIndicator == 'Z') {
                timezoneId = GMT_ID;
                if (date.length() > offset + 1) {
                    throw new IndexOutOfBoundsException("Invalid time zone indicator " + date.substring(offset));
                }
            } else {
                throw new IndexOutOfBoundsException("Invalid time zone indicator " + timezoneIndicator);
            }
            TimeZone timezone = TimeZone.getTimeZone(timezoneId);
            if (!timezone.getID().equals(timezoneId)) {
                throw new IndexOutOfBoundsException();
            }

            Calendar calendar = new GregorianCalendar(timezone);
            calendar.setLenient(false);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, seconds);
            calendar.set(Calendar.MILLISECOND, milliseconds);

            return calendar.getTime().getTime();
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Failed to parse date " + date, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date " + date, e);
        }
    }

    public static long parseISO8601CachedCalendar(String date) {
        try {
            int offset = 0;

            // extract year
            int year = parseInt(date, offset, offset += 4);
            checkOffset(date, offset, '-');

            // extract month
            int month = parseInt(date, offset += 1, offset += 2);
            checkOffset(date, offset, '-');

            // extract day
            int day = parseInt(date, offset += 1, offset += 2);
            checkOffset(date, offset, 'T');

            // extract hours, minutes, seconds and milliseconds
            int hour = parseInt(date, offset += 1, offset += 2);
            checkOffset(date, offset, ':');

            int minutes = parseInt(date, offset += 1, offset += 2);
            checkOffset(date, offset, ':');

            int seconds = parseInt(date, offset += 1, offset += 2);
            // milliseconds can be optional in the format
            final int milliseconds;
            if (date.charAt(offset) == '.') {
                checkOffset(date, offset, '.');
                milliseconds = parseInt(date, offset += 1, offset += 3);
            } else {
                milliseconds = 0; // always use 0 otherwise returned date will include millis of current time
            }

            // extract timezone
            char timezoneIndicator = date.charAt(offset);
            if (timezoneIndicator == 'Z') {
                if (date.length() > offset + 1) {
                    throw new IndexOutOfBoundsException("Invalid time zone indicator " + date.substring(offset));
                }
            } else {
                throw new IndexOutOfBoundsException("Invalid time zone indicator " + timezoneIndicator);
            }

            Calendar calendar = CALENDAR_CACHE.get();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, seconds);
            calendar.set(Calendar.MILLISECOND, milliseconds);

            return calendar.getTime().getTime();
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Failed to parse date " + date, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date " + date, e);
        }
    }

    private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
        if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
            throw new NumberFormatException(value);
        }
        // use same logic as in Integer.parseInt() but less generic we're not supporting negative values
        int i = beginIndex;
        int result = 0;
        int digit;
        if (i < endIndex) {
            digit = Character.digit(value.charAt(i++), 10);
            if (digit < 0) {
                throw new NumberFormatException("Invalid number: " + value);
            }
            result = -digit;
        }
        while (i < endIndex) {
            digit = Character.digit(value.charAt(i++), 10);
            if (digit < 0) {
                throw new NumberFormatException("Invalid number: " + value);
            }
            result *= 10;
            result -= digit;
        }
        return -result;
    }

    private static void checkOffset(String value, int offset, char expected) throws IndexOutOfBoundsException {
        char found = value.charAt(offset);
        if (found != expected) {
            throw new IndexOutOfBoundsException("Expected '" + expected + "' character but found '" + found + "'");
        }
    }

}
