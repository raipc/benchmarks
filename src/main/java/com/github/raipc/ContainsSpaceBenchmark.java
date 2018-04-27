package com.github.raipc;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.regex.Pattern;

@State(Scope.Benchmark)
public class ContainsSpaceBenchmark {
    private static final Pattern NO_SPACE_PATTERN = Pattern.compile("[^\\s]+");
    private static final Pattern HAS_SPACE_PATTERN = Pattern.compile("\\s");

    @Param({"metric", "longer_metric", "s pace_in_the_beginning", "space_in the_middle", "space_in_the_en d"})
    private String value;

    @Benchmark
    public boolean noSpacePatternCompiled() {
        return NO_SPACE_PATTERN.matcher(value).matches();
    }

    @Benchmark
    public boolean noSpacePatternInline() {
        return value.matches("[^\\s]+");
    }

    @Benchmark
    public boolean hasSpacePatternNegated() {
        return !HAS_SPACE_PATTERN.matcher(value).find();
    }

    @Benchmark
    public boolean manualCycleOverCharacters() {
        final int length = value.length();
        for (int i = 0; i < length; i++) {
            if (Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
