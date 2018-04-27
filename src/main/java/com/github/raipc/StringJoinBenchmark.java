package com.github.raipc;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class StringJoinBenchmark {

    private List<String> iterable;

    @Setup(value = Level.Iteration)
    public void setup() {
        iterable = IntStream.range(0, 100_000)
                .mapToObj(i -> RandomStringUtils.randomAlphanumeric(20))
                .collect(Collectors.toList());
    }

    @Benchmark
    public String withStringUtils() {
        return StringUtils.join(iterable, ',');
    }

    @Benchmark
    public String withStringJoin() {
        return String.join(",", iterable);
    }

    @Benchmark
    public String withFastJoin() {
        return fastJoin(iterable, ',');
    }

    @Benchmark
    public String withStream() {
        return iterable.stream().collect(Collectors.joining(","));
    }

    @Benchmark
    public String withSbDelete() {
        return withStringBuilderDelete(iterable, ',');
    }

    @Benchmark
    public String withSbSetLength() {
        return withStringBuilderSetLength(iterable, ',');
    }

    @Benchmark
    public String withSbCheck() {
        return withStringBuilderCheck(iterable, ',');
    }

    private static String withStringBuilderCheck(Iterable<String> iterable, char separator) {
        StringBuilder builder = new StringBuilder();
        for (String s : iterable) {
            if (builder.length() > 0) {
                builder.append(separator);
            }
            builder.append(s);
        }
        return builder.toString();
    }

    private static String withStringBuilderDelete(Iterable<String> iterable, char separator) {
        StringBuilder builder = new StringBuilder();
        for (String s : iterable) {
            builder.append(s).append(separator);
        }
        final int length = builder.length();
        return builder.delete(length - 1, length).toString();
    }

    private static String withStringBuilderSetLength(Iterable<String> iterable, char separator) {
        StringBuilder builder = new StringBuilder();
        for (String s : iterable) {
            builder.append(s).append(separator);
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }


    public static String fastJoin(List<String> parts, char separator) {
        if (parts == null) {
            return null;
        }
        final int size = parts.size();
        if (size == 1) {
            return parts.get(0);
        }
        int length = size - 1;
        for (String part : parts) {
            length += part.length();
        }
        char[] base = new char[length];

        int k = 0;
        for (String part : parts) {
            if (k > 0) {
                base[k++] = separator;
            }
            final int cl = part.length();
            part.getChars(0, cl, base, k);
            k += cl;
        }
        return new String(base);
    }
}
