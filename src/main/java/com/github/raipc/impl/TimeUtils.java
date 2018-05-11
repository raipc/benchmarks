package com.github.raipc.impl;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
    private static final int MILLIS_IN_NANO = 1_000_000;
    private static final int ISO_LENGTH = "1970-01-01T00:00:00.000+00:00".length();
    private static final String GMT_ID = "GMT";
    private static final ThreadLocal<Calendar> CALENDAR_CACHE = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(GMT_ID));
            calendar.setLenient(false);
            return calendar;
        }
    };

    public static String printIso8601UtcMillis(long timestamp) {
        return printIso860(timestamp, true, false);
    }

    public static String printIso8601LocalSeconds(long timestamp) {
        return printIso860(timestamp, false, true);
    }

    public static String printIso860(long timestamp, boolean withMillis, boolean local) {
        final StringBuilder sb = new StringBuilder(ISO_LENGTH);
        ZoneId zoneId = local ? ZoneId.systemDefault() : UTC_ZONE_ID;
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);
        adjust(sb, zonedDateTime.getYear(), 0, 9999, 4, "year").append('-');
        adjust(sb, zonedDateTime.getMonthValue(), 1, 12, 2, "month").append('-');
        adjust(sb, zonedDateTime.getDayOfMonth(), 1, 31, 2, "day").append(local ? ' ' : 'T');
        adjust(sb, zonedDateTime.getHour(), 0, 24, 2, "hour").append(':');
        adjust(sb, zonedDateTime.getMinute(), 0, 60, 2, "minute").append(':');
        adjust(sb, zonedDateTime.getSecond(), 0, 60, 2, "second");
        if (withMillis) {
            sb.append('.');
            adjust(sb, zonedDateTime.getNano() / MILLIS_IN_NANO, 0, 999, 3, "millis");
        }
        if (!local) {
            sb.append('Z');
        }
        return sb.toString();
    }

    private static StringBuilder adjust(StringBuilder sb, int num, int minValue, int maxValue, int positions, String descr) {
        if (num < minValue) {
            throw new IllegalArgumentException("Minimum " + descr + " is " + num + " but given " + num);
        } else if (num > maxValue) {
            throw new IllegalArgumentException("Maximum " + descr + " is " + num + " but given " + num);
        }
        String numAsString = "" + num;
        final int length = numAsString.length();
        if (length > positions) {
            throw new IllegalArgumentException("Maximum digits per " + descr + " is " + positions + " but " + num + " takes " + length);
        }
        for (int i = positions - length; i > 0; --i) {
            sb.append('0');
        }
        return sb.append(numAsString);
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
