package com.github.raipc.impl;

import com.google.common.math.IntMath;

import java.time.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
    public static final int NANOS_IN_MILLIS = 1_000_000;
    public static final int MILLIS_IN_SECOND = 1_000;
    public static final int ISO_LENGTH = "1970-01-01T00:00:00.000+00:00".length();
    private static final String GMT_ID = "GMT";
    private static final ThreadLocal<Calendar> CALENDAR_CACHE = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(GMT_ID));
            calendar.setLenient(false);
            return calendar;
        }
    };

    private TimeUtils() {
    }

    public static String printIso8601UtcMillis(long timestamp) {
        return printIso8601(timestamp, true, false);
    }

    public static String printIso8601LocalSeconds(long timestamp) {
        return printIso8601(timestamp, false, true);
    }

    public static String printIso8601(long timestamp, boolean withMillis, boolean local) {
        final StringBuilder sb = new StringBuilder(ISO_LENGTH);
        ZoneId zoneId = local ? ZoneId.systemDefault() : UTC_ZONE_ID;
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);
        adjust(sb, zonedDateTime.getYear(), 4).append('-');
        adjust(sb, zonedDateTime.getMonthValue(), 2).append('-');
        adjust(sb, zonedDateTime.getDayOfMonth(), 2).append(local ? ' ' : 'T');
        adjust(sb, zonedDateTime.getHour(), 2).append(':');
        adjust(sb, zonedDateTime.getMinute(), 2).append(':');
        adjust(sb, zonedDateTime.getSecond(), 2);
        if (withMillis) {
            sb.append('.');
            adjust(sb, zonedDateTime.getNano() / NANOS_IN_MILLIS, 3);
        }
        if (!local) {
            sb.append('Z');
        }
        return sb.toString();
    }

    public static String printIso8601OptUtc(long timestamp, boolean withMillis) {
        final StringBuilder sb = new StringBuilder(ISO_LENGTH);
        final long secs = Math.floorDiv(timestamp, MILLIS_IN_SECOND);
        final int nanos = (int)Math.floorMod(timestamp, MILLIS_IN_SECOND) * NANOS_IN_MILLIS;
        final LocalDateTime dateTime = LocalDateTime.ofEpochSecond(secs, nanos, ZoneOffset.UTC);

        adjustOpt(sb, dateTime.getYear(), 4).append('-');
        adjustOpt(sb, dateTime.getMonthValue(), 2).append('-');
        adjustOpt(sb, dateTime.getDayOfMonth(), 2).append('T');
        adjustOpt(sb, dateTime.getHour(), 2).append(':');
        adjustOpt(sb, dateTime.getMinute(), 2).append(':');
        adjustOpt(sb, dateTime.getSecond(), 2);
        if (withMillis) {
            sb.append('.');
            adjustOpt(sb, dateTime.getNano() / NANOS_IN_MILLIS, 3);
        }
        return sb.append('Z').toString();
    }

    public static String printIso8601OptLocal(long timestamp, boolean withMillis) {
        final StringBuilder sb = new StringBuilder(ISO_LENGTH);
        final LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        adjustOpt(sb, dateTime.getYear(), 4).append('-');
        adjustOpt(sb, dateTime.getMonthValue(), 2).append('-');
        adjustOpt(sb, dateTime.getDayOfMonth(), 2).append(' ');
        adjustOpt(sb, dateTime.getHour(), 2).append(':');
        adjustOpt(sb, dateTime.getMinute(), 2).append(':');
        adjustOpt(sb, dateTime.getSecond(), 2);
        if (withMillis) {
            sb.append('.');
            adjustOpt(sb, dateTime.getNano() / NANOS_IN_MILLIS, 3);
        }
        return sb.toString();
    }

    private static StringBuilder adjust(StringBuilder sb, int num, int positions) {
        String numAsString = "" + num;
        final int length = numAsString.length();
        for (int i = positions - length; i > 0; --i) {
            sb.append('0');
        }
        return sb.append(numAsString);
    }

    private static StringBuilder adjustOpt(StringBuilder sb, int num, int positions) {
        for (int i = positions - sizeInDigits(num); i > 0; --i) {
            sb.append('0');
        }
        return sb.append(num);
    }

    /**
     * Return number of digits in base-10 string representation of number.
     * @param number Non-negative number
     * @return number of digits
     */
    public static int sizeInDigits(int number) {
        if (number < 100000) {
            if (number < 100) {
                return number < 10 ? 1 : 2;
            } else {
                if (number < 1000) {
                    return 3;
                } else {
                    return number < 10000 ? 4 : 5;
                }
            }
        } else {
            if (number < 10000000) {
                return number < 1000000 ? 6 : 7;
            } else {
                if (number < 100000000) {
                    return 8;
                } else {
                    return number < 1000000000 ? 9 : 10;
                }
            }
        }

    }

    public static long parseISO8601Java8(String date) {
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

            final int nanos = (int) TimeUnit.MILLISECONDS.toNanos(milliseconds);
            return ZonedDateTime.of(year, month, day, hour, minutes, seconds, nanos, timezone.toZoneId())
                    .toInstant().toEpochMilli();
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Failed to parse date " + date, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date " + date, e);
        }
    }

    public static long parseISO8601NanosOpt(String date) {
        try {
            final int length = date.length();
            int offset = 0;

            // extract year
            final int year = parseIntRaipcCodeNoMethodCallOptReverse(date, offset, offset += 4, length);
            checkOffset(date, offset, '-');

            // extract month
            final int month = parseIntRaipcCodeNoMethodCallOptReverse(date, offset += 1, offset += 2, length);
            checkOffset(date, offset, '-');

            // extract day
            final int day = parseIntRaipcCodeNoMethodCallOptReverse(date, offset += 1, offset += 2, length);
            checkOffset(date, offset, 'T');

            // extract hours, minutes, seconds and milliseconds
            final int hour = parseIntRaipcCodeNoMethodCallOptReverse(date, offset += 1, offset += 2, length);
            checkOffset(date, offset, ':');

            final int minutes = parseIntRaipcCodeNoMethodCallOptReverse(date, offset += 1, offset += 2, length);
            checkOffset(date, offset, ':');

            final int seconds = parseIntRaipcCodeNoMethodCallOptReverse(date, offset += 1, offset += 2, length);
            // milliseconds can be optional in the format
            final int nanos;
            if (date.charAt(offset) == '.') {
                final int digits = findFractionOfSecond(date, offset += 1, Math.min(offset + 10, length));
                nanos = parseNanosOpt(parseIntRaipcCodeNoMethodCallOptReverse(date, offset, offset += digits, length), digits);
            } else {
                nanos = 0;
            }

            // extract timezone
            final ZoneOffset zoneOffset;
            if (date.charAt(offset) == 'Z' && offset == length - 1) {
                zoneOffset = ZoneOffset.UTC;
            } else {
                zoneOffset = ZoneOffset.of(date.substring(offset));
            }
            return OffsetDateTime.of(year, month, day, hour, minutes, seconds, nanos, zoneOffset)
                    .toInstant().toEpochMilli();
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Failed to parse date " + date, e);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid date " + date, e);
        }
    }

    private static int findFractionOfSecond(String date, int startOffset, int length) {
        char c;
        for (int i = startOffset; i < length; ++i) {
            c = date.charAt(i);
            if (c < '0' || c > '9') {
                if (i == startOffset) {
                    break;
                }
                return i - startOffset;
            }
        }
        throw new IllegalStateException("Illegal fraction of second");
    }

    private static int parseNanosOpt(int value, int digits) {
        return value * powerOfTen(9 - digits);
    }

    private static int powerOfTen(int pow) {
        switch (pow) {
            case 0: return 1;
            case 1: return 10;
            case 2: return 100;
            case 3: return 1_000;
            case 4: return 10_000;
            case 5: return 100_000;
            case 6: return 1_000_000;
            case 7: return 10_000_000;
            case 8: return 100_000_000;
            case 9: return 1_000_000_000;
            default: return IntMath.pow(10, pow);
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

    public static int parseInt(String value, int beginIndex, int endIndex, int valueLength) throws NumberFormatException {
        if (beginIndex < 0 || endIndex > valueLength || beginIndex > endIndex) {
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

    public static int parseIntRaipc(String value, int beginIndex, int endIndex, int valueLength) {
        if (beginIndex < 0 || endIndex > valueLength || beginIndex > endIndex) {
            throw new NumberFormatException(value);
        }
        // use same logic as in Integer.parseInt() but less generic we're not supporting negative values
        int i = beginIndex;
        int result = 0;
        int digit;
        while (i < endIndex) {
            digit = resolveDigit(value.charAt(i++));
            if (digit < 0) {
                throw new NumberFormatException("Invalid number: " + value);
            }
            result *= 10;
            result -= digit;
        }
        return -result;
    }

    private static int resolveDigit(char c) {
        switch (c) {
            case '0': return 0;
            case '1': return 1;
            case '2': return 2;
            case '3': return 3;
            case '4': return 4;
            case '5': return 5;
            case '6': return 6;
            case '7': return 7;
            case '8': return 8;
            case '9': return 9;
            default: return -1;
        }
    }

    public static int parseIntRaipcCode(String value, int beginIndex, int endIndex, int valueLength) {
        if (beginIndex < 0 || endIndex > valueLength || beginIndex > endIndex) {
            throw new NumberFormatException(value);
        }
        // use same logic as in Integer.parseInt() but less generic we're not supporting negative values
        int result = 0;
        int digit;
        for (int i = beginIndex; i < endIndex; ++i) {
            digit = resolveDigitByCode(value.charAt(i));
            if (digit < 0) {
                throw new NumberFormatException("Invalid number: " + value);
            }
            result *= 10;
            result -= digit;
        }
        return -result;
    }

    public static int parseIntRaipcCode(char[] value, int beginIndex, int endIndex, int valueLength) {
        if (beginIndex < 0 || endIndex > valueLength || beginIndex > endIndex) {
            throw new NumberFormatException(new String(value));
        }
        // use same logic as in Integer.parseInt() but less generic we're not supporting negative values
        int result = 0;
        int digit;
        for (int i = beginIndex; i < endIndex; ++i) {
            digit = resolveDigitByCode(value[i]);
            if (digit < 0) {
                throw new NumberFormatException("Invalid number: " + new String(value));
            }
            result *= 10;
            result -= digit;
        }
        return -result;
    }

    public static int parseFractionOfSecond(String value, int beginIndex, int endIndex, int valueLength) {
        if (beginIndex < 0 || endIndex > valueLength || beginIndex > endIndex) {
            throw new NumberFormatException(value);
        }
        // use same logic as in Integer.parseInt() but less generic we're not supporting negative values
        char c = value.charAt(beginIndex);
        int digitPred = c - '0';
        if (digitPred < 0 || digitPred > 9) {
            throw new NumberFormatException("Invalid number: " + value);
        }
        int result = digitPred;
        for (int i = beginIndex + 1; i < endIndex; ++i) {
            c = value.charAt(i);
            digitPred = c - '0';
            if (digitPred < 0 || digitPred > 9) {
                throw new NumberFormatException("Invalid number: " + value);
            }
            result = result * 10 + digitPred;
        }
        return result;
    }

    public static int parseIntRaipcCodeNoMethodCallOptReverse(String value, int beginIndex, int endIndex, int valueLength) {
        if (beginIndex < 0 || endIndex > valueLength || beginIndex > endIndex) {
            throw new NumberFormatException(value);
        }
        // use same logic as in Integer.parseInt() but less generic we're not supporting negative values
        char c = value.charAt(beginIndex);
        int digitPrediction = c - '0';
        if (digitPrediction < 0 || digitPrediction > 9) {
            throw new NumberFormatException("Invalid number: " + value);
        }
        int result = c - '0';
        for (int i = beginIndex + 1; i < endIndex; ++i) {
            c = value.charAt(i);
            digitPrediction = c - '0';
            if (digitPrediction < 0 || digitPrediction > 9) {
                throw new NumberFormatException("Invalid number: " + value);
            }
            result = result * 10 + digitPrediction;
        }
        return result;
    }

    private static int resolveDigitByCode(char c) {
        if (c > '9') {
            return -1;
        }
        return c - '0';
    }

    private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
        if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
            throw new NumberFormatException(value);
        }
        // use same logic as in Integer.parseInt() but less generic we're not supporting negative values
        int i = beginIndex;
        int result = 0;
        int digit;
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

    public static void main(String[] args) {
        while (true) {
            final long l = parseISO8601NanosOpt("1980-01-01T00:00:09.000Z");
            if (l < 0) {
                break;
            }
        }
    }

    private static void checkOffset(String value, int offset, char expected) throws IndexOutOfBoundsException {
        if (value.charAt(offset) != expected) {
            throw new IndexOutOfBoundsException("Expected '" + expected + "' character but found '" + value.charAt(offset) + "'");
        }
    }
}
