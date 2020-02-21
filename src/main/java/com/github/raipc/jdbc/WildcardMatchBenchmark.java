package com.github.raipc.jdbc;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class WildcardMatchBenchmark {
    private final String table = "jvm_memory_used";
    @Param({"%", "jvm%", "jvm_me_ory_used", "jvm\\_memory\\_used", "atsd"})
    private String pattern;

    @Benchmark
    public boolean usingWildcardMatch() {
        return WildcardUtil.wildcardMatch(table, pattern);
    }

    @Benchmark
    public boolean usingRegex() {
        return likeToRegex(pattern).matcher(table).matches();
    }

    /** Converts a LIKE-style pattern (where '%' represents a wildcard, escaped
     * using '\') to a Java regex. */
    private static Pattern likeToRegex(String pattern) {
        final StringBuilder buf = new StringBuilder("^");
        int slash = -2;
        final int length = pattern.length();
        for (int i = 0; i < length; i++) {
            char c = pattern.charAt(i);
            if (slash == i - 1) {
                buf.append('[').append(c).append(']');
            } else {
                switch (c) {
                    case '\\':
                        slash = i;
                        break;
                    case '%':
                        buf.append(".*");
                        break;
                    case '_':
                        buf.append(".");
                        break;
                    case '[':
                        buf.append("\\[");
                        break;
                    case ']':
                        buf.append("\\]");
                        break;
                    default:
                        buf.append('[').append(c).append(']');
                }
            }
        }
        buf.append("$");
        return Pattern.compile(buf.toString());
    }

    public static void main(String[] args) throws RunnerException {
//        final String[] patterns = {"jvm_me_ory_used"};
        final String[] patterns = {"%", "jvm%", "jvm_me_ory_used", "jvm\\_memory\\_used", "atsd"};
        final String table = "jvm_memory_used";
        for (String pattern : patterns) {
            System.out.println("with Java regex for pattern '" + pattern + "': " + likeToRegex(pattern).matcher(table).matches());
            System.out.println("with special method for pattern '" + pattern + "': " + WildcardUtil.wildcardMatch(table, pattern));
        }

        String benchmark = ".*" + WildcardMatchBenchmark.class.getSimpleName() + ".*";
        Options opt = new OptionsBuilder()
                .include(benchmark)
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(3))
                .measurementIterations(2)
                .forks(1)
                .build();
        new Runner(opt).run();
    }

}
